package ch.hsr.isf.serepo.rest.resources.commits;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.tika.mime.MimeTypeException;
import org.eclipse.jgit.api.Git;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;

import ch.hsr.isf.serepo.commons.FileUtils;
import ch.hsr.isf.serepo.commons.MimeTypes;
import ch.hsr.isf.serepo.data.restinterface.commit.CommitMode;
import ch.hsr.isf.serepo.data.restinterface.commit.CreateCommit;
import ch.hsr.isf.serepo.data.restinterface.common.User;
import ch.hsr.isf.serepo.data.restinterface.seitem.CreateSeItem;
import ch.hsr.isf.serepo.data.restinterface.seitem.Relation;
import ch.hsr.isf.serepo.git.error.GitCommandException;
import ch.hsr.isf.serepo.git.repository.GitAuthor;
import ch.hsr.isf.serepo.git.repository.GitRepository;
import ch.hsr.isf.serepo.git.repository.GitRepositoryBuilder;
import ch.hsr.isf.serepo.markdown.Link;
import ch.hsr.isf.serepo.markdown.MarkdownWriter;
import ch.hsr.isf.serepo.markdown.yamlfrontmatter.Metadata;
import ch.hsr.isf.serepo.search.index.Indexer;

public class NewCommit {

  private static Logger logger = LoggerFactory.getLogger(NewCommit.class);
  
  private File repositoriesDir;
  private File repositoriesWorkingDir;

  private GitRepository git;
  private File repositoryDir;
  private String solrUrl;

  public NewCommit(File repositoriesDir, File repositoriesWorkingDir, String solrUrl) {
    this.repositoriesDir = repositoriesDir;
    this.repositoriesWorkingDir = repositoriesWorkingDir;
    this.solrUrl = solrUrl;
  }

  private void cloneRepository(String repositoryName) throws IOException, GitCommandException {
    File repositorySourceDir = Paths.get(repositoriesDir.getAbsolutePath(), repositoryName + ".git")
                                    .toFile();
    File repositoryCloneDir =
        Files.createTempDirectory(repositoriesWorkingDir.toPath(), repositoryName + "_")
             .toFile();

    Git git = GitRepositoryBuilder.clone(repositorySourceDir.getAbsolutePath(), repositoryCloneDir);
    this.repositoryDir = repositoryCloneDir;
    this.git = new GitRepository(git);
  }

  public Response create(String repository, MultipartFormDataInput multipart)
      throws IOException, GitCommandException, URISyntaxException {

    try {

      cloneRepository(repository);

      CreateCommit createCommit = getCreateCommit(multipart);
      if (createCommit == null) {
        return Response.status(Status.BAD_REQUEST)
                       .entity("Your JSON input is incomplete. Missing commit entity.")
                       .build();
      }

      String workingBranch = createCommit.getUser()
                                         .getName();
      checkoutBranch(workingBranch);

      if (createCommit.getMode() == CommitMode.ADD_UPDATE_DELETE) {
        deleteRepositoryContent();
      }

      List<MetadataContentTuple> metadataContentTuples = getMetadataContent(multipart);
      createSeItems(metadataContentTuples);

      if (git.hasUncomittedChanges()) {
        String commitId = commit(createCommit.getMessage(), createCommit.getUser());
        merge(workingBranch);
        push(workingBranch);
        indexSeItems(repository, commitId, metadataContentTuples);
        return Response.status(Status.CREATED)
                       .entity(commitId)
                       .type(MediaType.TEXT_PLAIN_TYPE)
                       .build();
      } else {
        return Response.status(Status.OK)
                       .entity("Nothing to commit.")
                       .type(MediaType.TEXT_PLAIN_TYPE)
                       .build();
      }

    } finally {
      if (git != null) {
        git.close();
      }
      if (repositoryDir != null) {
        FileUtils.delete(repositoryDir);
      }
    }

  }

  private void indexSeItems(String repository, String commitId, List<MetadataContentTuple> metadataContentTuples) {
    try (Indexer indexer = new Indexer(solrUrl, repository, commitId)) {
      for (MetadataContentTuple tuple : metadataContentTuples) {
        String seItemId = Paths.get(tuple.seItem.getFolder(), tuple.seItem.getName()).toString();
        try (InputStream contentInputStream = tuple.content.getBody(new GenericType<InputStream>(InputStream.class))) {
          indexer.index(seItemId, tuple.seItem.getName(), tuple.seItem.getMetadata(), ByteStreams.toByteArray(contentInputStream));
        } catch (Exception e) {
          logger.error("An error occured while indexing SE-Item '" + seItemId + "'", e);
        }
      }
    }
  }

  private String commit(String message, User user) throws GitCommandException {
    GitAuthor author = new GitAuthor(user.getName(), user.getEmail());
    String commitId = git.commit(message, author);
    return commitId;
  }

  private void merge(String workingBranch) throws GitCommandException {
    git.checkout("master");
    git.merge(workingBranch);
  }

  private void push(String workingBranch) throws GitCommandException {
    git.push("master"); // TODO what if in the mean time master already outdated?
    git.push(workingBranch);
  }

  private void createSeItems(List<MetadataContentTuple> metadataContentTuples)
      throws IOException, GitCommandException, URISyntaxException {

    for (MetadataContentTuple tuple : metadataContentTuples) {
      CreateSeItem createSeItem = tuple.seItem;
      MarkdownWriter markdownWriter = new MarkdownWriter();

      Metadata metadata = new Metadata(createSeItem.getMetadata());
      markdownWriter.setYamlFrontmatter(metadata);
      writeContent(tuple, markdownWriter);
      writeRelations(tuple.seItem.getRelations(), markdownWriter);

      File mdFile = getMdFile(tuple.seItem.getFolder(), tuple.seItem.getName());
      try (OutputStreamWriter outputStreamWriter =
          new OutputStreamWriter(new FileOutputStream(mdFile), StandardCharsets.UTF_8);) {
        markdownWriter.write(outputStreamWriter);
      }
      String relativeFilepath = FileUtils.relativize(repositoryDir, mdFile);
      git.add(relativeFilepath);
    }

  }

  private void writeRelations(List<Relation> relations, MarkdownWriter markdownWriter)
      throws URISyntaxException {
    for (Relation relation : relations) {
      String target = null;
      if (new URI(relation.getTarget()).isAbsolute()) {
        target = relation.getTarget();
      } else {
        target = relation.getTarget() + ".md";
      }
      markdownWriter.addLink(relation.getType(), new Link(target, null, target));
    }
  }

  private void writeContent(MetadataContentTuple tuple, MarkdownWriter markdownWriter)
      throws IOException, GitCommandException {
    try (InputStream contentInputStream =
        tuple.content.getBody(new GenericType<InputStream>(InputStream.class))) {
      if (tuple.content.getMediaType()
                       .getSubtype()
                       .toLowerCase()
                       .contains("markdown")) { // text/markdown (since March 2016) or
                                                // text/x-markdown (before March 2016)
        try (InputStreamReader isr =
            new InputStreamReader(contentInputStream, StandardCharsets.UTF_8)) {
          markdownWriter.setContent(CharStreams.toString(isr));
        }
      } else {
        // write content to separate file
        writeSeparateFile(tuple.seItem.getFolder(), tuple.seItem.getName(), tuple.content,
            contentInputStream);
      }
    }
  }

  private void writeSeparateFile(String folder, String name, InputPart content,
      InputStream contenInputStream) throws GitCommandException, IOException {
    String relativePath = Paths.get(folder, name + getFileExtension(content.getMediaType()))
                               .toString();
    File separateFile = Paths.get(repositoryDir.getAbsolutePath(), relativePath)
                             .toFile();
    separateFile.getParentFile()
                .mkdirs();
    separateFile.createNewFile();

    try (FileOutputStream fos = new FileOutputStream(separateFile, false)) {
      ByteStreams.copy(contenInputStream, fos);
      fos.flush();
    }
    git.add(relativePath);
  }

  private String getFileExtension(MediaType mediaType) {
    try {
      return MimeTypes.getFileExtension(
          String.format("%s/%s", mediaType.getType(), mediaType.getSubtype()));
    } catch (MimeTypeException e) {
      return ""; // no file extension
    }
  }

  private File getMdFile(String folder, String name) throws IOException {
    File mdFile = Paths.get(repositoryDir.getAbsolutePath(), folder, name + ".md")
                       .toFile(); // TODO clean/sanitize path & filename. check iff path contains
                                  // only / and no \ !!! Check: filename must not contain /
    mdFile.getParentFile()
          .mkdirs();
    mdFile.createNewFile();
    return mdFile;
  }

  private void deleteRepositoryContent() throws IOException {
    File[] filesInRootDir = repositoryDir.listFiles(new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        boolean accept = true;
        if (pathname.getName()
                    .equalsIgnoreCase(".git")) {
          accept = false;
        } else if (pathname.getName()
                           .equalsIgnoreCase("readme.md")) {
          accept = false;
        }
        return accept;
      }
    });
    for (File file : filesInRootDir) {
      FileUtils.delete(file);
    }
  }

  private void checkoutBranch(String name) throws GitCommandException {
    git.checkoutNew(name);
  }

  private static class MetadataContentTuple {
    public CreateSeItem seItem;
    public InputPart content;
  }

  private CreateCommit getCreateCommit(MultipartFormDataInput multipart) throws IOException {
    if (multipart.getFormDataMap()
                 .containsKey("commit")) {
      return multipart.getFormDataMap()
                      .get("commit")
                      .get(0)
                      .getBody(new GenericType<CreateCommit>(CreateCommit.class));
    } else {
      return null;
    }
  }

  private List<MetadataContentTuple> getMetadataContent(MultipartFormDataInput multipart)
      throws IOException {

    Map<String, MetadataContentTuple> mapIdentifierToInputPartTuble = new HashMap<>();
    for (Map.Entry<String, List<InputPart>> entry : multipart.getFormDataMap()
                                                             .entrySet()) {
      String key = null;
      if (entry.getKey()
               .startsWith("metadata_")) {
        key = entry.getKey()
                   .substring("metadata_".length());
        if (!mapIdentifierToInputPartTuble.containsKey(key)) {
          mapIdentifierToInputPartTuble.put(key, new MetadataContentTuple());
        }
        mapIdentifierToInputPartTuble.get(key).seItem = entry.getValue()
                                                             .get(0)
                                                             .getBody(new GenericType<CreateSeItem>(
                                                                 CreateSeItem.class));
      } else if (entry.getKey()
                      .startsWith("content_")) {
        key = entry.getKey()
                   .substring("content_".length());
        if (!mapIdentifierToInputPartTuble.containsKey(key)) {
          mapIdentifierToInputPartTuble.put(key, new MetadataContentTuple());
        }
        mapIdentifierToInputPartTuble.get(key).content = entry.getValue()
                                                              .get(0);
      }
    }

    return new ArrayList<>(mapIdentifierToInputPartTuble.values());

  }

}
