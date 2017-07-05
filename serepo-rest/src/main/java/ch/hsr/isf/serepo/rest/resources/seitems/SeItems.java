package ch.hsr.isf.serepo.rest.resources.seitems;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.common.io.Files;

import ch.hsr.isf.serepo.commons.MimeTypes;
import ch.hsr.isf.serepo.commons.Uri;
import ch.hsr.isf.serepo.data.restinterface.common.Link;
import ch.hsr.isf.serepo.data.restinterface.common.User;
import ch.hsr.isf.serepo.data.restinterface.metadata.MetadataContainer;
import ch.hsr.isf.serepo.data.restinterface.metadata.MetadataEntry;
import ch.hsr.isf.serepo.data.restinterface.seitem.RelationContainer;
import ch.hsr.isf.serepo.data.restinterface.seitem.RelationEntry;
import ch.hsr.isf.serepo.data.restinterface.seitem.SeItem;
import ch.hsr.isf.serepo.data.restinterface.seitem.SeItemContainer;
import ch.hsr.isf.serepo.git.error.GitCommandException;
import ch.hsr.isf.serepo.git.repository.GitRepository;
import ch.hsr.isf.serepo.git.repository.GitRepository.FileReader;
import ch.hsr.isf.serepo.git.repository.GitRepositoryBuilder;
import ch.hsr.isf.serepo.git.repository.file.GitFile;
import ch.hsr.isf.serepo.git.repository.log.GitCommitLog;
import ch.hsr.isf.serepo.markdown.MarkdownReader;
import ch.hsr.isf.serepo.markdown.SectionLink;
import ch.hsr.isf.serepo.markdown.yamlfrontmatter.Metadata;
import ch.hsr.isf.serepo.markdown.yamlfrontmatter.Metadata.InputType;
import ch.hsr.isf.serepo.relations.RelationDefinition;
import ch.hsr.isf.serepo.relations.RelationsFile;
import ch.hsr.isf.serepo.relations.RelationsFileReader;
import ch.hsr.isf.serepo.rest.resources.Resource;

public class SeItems {

  private SeItems() {
  }

  public static Response createResponse(File repositoriesDir, String repositoryName, String commitId, URI baseUri) throws Throwable {
    File repositoryDir = Paths.get(repositoriesDir.getAbsolutePath(), repositoryName + ".git").toFile();
    if (!repositoryDir.isDirectory()) {
      String msg = String.format("Repository '%s' could not be found.", repositoryName);
      return Response.status(Status.NOT_FOUND)
                     .entity(msg)
                     .type(MediaType.TEXT_PLAIN_TYPE)
                     .build();
    }
    try (GitRepository git = GitRepositoryBuilder.open(repositoryDir)) {
      try {
        git.log(commitId);
      } catch (GitCommandException e) {
        String msg = String.format("CommitId '%s' could not be found.", commitId);
        return Response.status(Status.NOT_FOUND)
                       .entity(msg)
                       .type(MediaType.TEXT_PLAIN_TYPE)
                       .build();
      }
      return Response.ok(container(git, repositoryName, commitId, baseUri)).build();
    }
  }
  
  public static SeItemContainer container(GitRepository git, String repositoryName, String commitId, URI baseUri) throws Throwable {
    
    SeItemContainer seItemContainer = new SeItemContainer();
    seItemContainer.setId(Uri.of(baseUri, "repos", repositoryName, "commits", commitId, "seitems"));
    seItemContainer.setCommitId(commitId);
    seItemContainer.setUpdated(new Date()); // TODO
    
    seItemContainer.getLinks().add(new Link("self", seItemContainer.getId().toString()));
    
    seItemContainer.getSeItems().addAll(loadAllSeItems(git, commitId, seItemContainer.getId()));
    
    return seItemContainer;
    
  }
  
  public static List<SeItem> loadAllSeItems(final GitRepository git, final String commitId, final URI baseUri) throws Throwable {
    final List<SeItem> seItems = new ArrayList<>();
    try {
      git.readFilesByExtension(commitId, ".md", new FileReader() {
        
        @Override
        public boolean read(GitFile gitFile) {
          try (InputStream inputStream = gitFile.getInputStream()) {

            if ("readme.md".equalsIgnoreCase(gitFile.getName())) {
              return true;
            }
            
            GitCommitLog log = git.logLatest(commitId, gitFile.getPath() + gitFile.getName());
            String filenameWithoutExtension = Files.getNameWithoutExtension(gitFile.getName());
            
            SeItem seItem = new SeItem();
            seItems.add(seItem);

            User user = new User(log.getAuthor().getName(), log.getAuthor().getEmail());
            seItem.setAuthor(user);
            seItem.setUpdated(log.getWhen());
            seItem.setName(filenameWithoutExtension);
            seItem.setFolder(gitFile.getPath());
            URI id = Uri.of(baseUri, gitFile.getPath(), filenameWithoutExtension);
            seItem.setId(id);
            
            SeItem.Content content = new SeItem.Content();
            seItem.setContent(content);
            content.setMimetype(MimeTypes.get(inputStream));
            content.setUrl(id.toString());
            
            seItem.getLinks().add(new Link("self", id.toString()));
            seItem.getLinks().add(new Link(Resource.HATEOAS_PREFIX + "serepo_content", id.toString()));
            seItem.getLinks().add(new Link(Resource.HATEOAS_PREFIX + "serepo_metadata", id.toString()+"?metadata"));
            seItem.getLinks().add(new Link(Resource.HATEOAS_PREFIX + "serepo_relations", id.toString()+"?relations"));
            
          } catch (GitCommandException | URISyntaxException | IOException e) {
            throw new RuntimeException(e);
          }
          return true;
        }
      });
    } catch (RuntimeException e) {
      throw e.getCause();
    }
    Collections.sort(seItems, new Comparator<SeItem>() {

      @Override
      public int compare(SeItem s1, SeItem s2) {
        int compare = 0;
        int numberOfFoldersS1 = s1.getFolder().split("/").length;
        int numberOfFoldersS2 = s2.getFolder().split("/").length;
        if (numberOfFoldersS1 == numberOfFoldersS2) {
          compare = s1.getFolder().compareTo(s2.getFolder());
        } else {
          compare = Integer.compare(numberOfFoldersS1, numberOfFoldersS2);
        }
        return compare;
      }
      
    });
    return seItems;
  }
  
  public static Response getContent(final File repositoriesDir, final String repositoryName, final String commitId, final String seitem, final File globalRelationsDefinitionFile) throws IOException, GitCommandException {
    
    File repositoryDir = Paths.get(repositoriesDir.getAbsolutePath(), repositoryName + ".git").toFile();
    if (!repositoryDir.isDirectory()) {
      String msg = String.format("Repository '%s' could not be found.", repositoryName);
      return Response.status(Status.NOT_FOUND)
                     .entity(msg)
                     .type(MediaType.TEXT_PLAIN_TYPE)
                     .build();
    }
    try (GitRepository git = GitRepositoryBuilder.open(repositoryDir)) {
      try {
        git.log(commitId);
      } catch (GitCommandException e) {
        String msg = String.format("CommitId '%s' could not be found.", commitId);
        return Response.status(Status.NOT_FOUND)
                       .entity(msg)
                       .type(MediaType.TEXT_PLAIN_TYPE)
                       .build();
      }
      
      class GitFileContentStream {
        InputStream stream;
      }
      final GitFileContentStream gitFileContent = new GitFileContentStream();
      
      String path = seitem.substring(0, seitem.lastIndexOf("/"));
      if (path.isEmpty()) {
        path= ".";
      }
      git.readFilesByPath(commitId, path, new FileReader() {
        
        @Override
        public boolean read(GitFile gitFile) {
          String filename = Files.getNameWithoutExtension(gitFile.getName());
          if (!gitFile.getName().endsWith(".md") && seitem.equalsIgnoreCase(gitFile.getPath() + filename)) {
            gitFileContent.stream = gitFile.getInputStream(); // TODO who closes the stream?
            return false;
          }
          return true;
        }
      });

      if (gitFileContent.stream != null) {
        String mimetype = MimeTypes.get(gitFileContent.stream);
        // TODO not all streams support reset(). Workaround? Test with large files, because
        // JGit uses another concrete InputStream for large files!
        gitFileContent.stream.reset();
        return Response.ok(gitFileContent.stream, mimetype)
                       .build();
      } else {
        git.readFilesByPath(commitId, path, new FileReader() {
          
          @Override
          public boolean read(GitFile gitFile) {
            if (gitFile.getFullPath().equalsIgnoreCase(seitem+".md")) {
              gitFileContent.stream = gitFile.getInputStream();
              return false;
            }
            return true;
          }
        });
        if (gitFileContent.stream != null) {
          String yamlFrontmatter = MarkdownReader.readYamlFrontmatter(gitFileContent.stream).or("---\n");
          Metadata metadata = new Metadata(yamlFrontmatter, InputType.YAML);
          if (metadata.containsKey(Metadata.ReservedKeys.CONTENT_URL)) {
            return Response.temporaryRedirect(URI.create((String) metadata.get(Metadata.ReservedKeys.CONTENT_URL))).build();
          } else {
            RelationsFileReader relationsFileReader = new RelationsFileReader();
            RelationsFile relationsFile = relationsFileReader.read(globalRelationsDefinitionFile);
            List<String> listOfHeadersToStop = new ArrayList<>();
            for (RelationDefinition relDef : relationsFile.getDefinitions()) {
              listOfHeadersToStop.add(relDef.getIdentifier());
            }
            gitFileContent.stream.reset();
            String markdownContent = MarkdownReader.readContent(gitFileContent.stream, listOfHeadersToStop);
            return Response.ok(markdownContent, "text/markdown; charset=UTF-8").build();
          }
        }
      }

      return Response.status(Status.NOT_FOUND).build();
      
    }
    
  }

  public static Response getMetadata(URI baseUri, File repositoriesDir, String repositoryName, String commitId,
      String seitem) throws IOException, GitCommandException, URISyntaxException {

    File repositoryDir = Paths.get(repositoriesDir.getAbsolutePath(), repositoryName + ".git").toFile();
    if (!repositoryDir.isDirectory()) {
      String msg = String.format("Repository '%s' could not be found.", repositoryName);
      return Response.status(Status.NOT_FOUND)
                     .entity(msg)
                     .type(MediaType.TEXT_PLAIN_TYPE)
                     .build();
    }
    try (GitRepository git = GitRepositoryBuilder.open(repositoryDir)) {
      GitCommitLog log = null;
      try {
        log = git.log(commitId);
      } catch (GitCommandException e) {
        String msg = String.format("CommitId '%s' could not be found.", commitId);
        return Response.status(Status.NOT_FOUND)
                       .entity(msg)
                       .type(MediaType.TEXT_PLAIN_TYPE)
                       .build();
      }
      
      final Metadata[] metadataTransfer = new Metadata[1];
      git.readFilesByPath(commitId, seitem + ".md", new FileReader() {
        
        @Override
        public boolean read(GitFile gitFile) {
          try {
            MarkdownReader markdownReader = new MarkdownReader(gitFile.getBytes());
            metadataTransfer[0] = new Metadata(markdownReader.getYamlFrontmatter().or("---\n"), InputType.YAML);
          } catch (IOException e) {
            e.printStackTrace(); // TODO what can we do here?
          }
          return false;
        }
      });
      
      if (metadataTransfer[0] != null) {

        MetadataContainer metadataContainer = new MetadataContainer();
        URI id = new URI(null, null, baseUri.toString(), "metadata", null);
        metadataContainer.setId(id);
        metadataContainer.setUpdated(log.getWhen());
        metadataContainer.getLinks().add(new Link("self", id.toString()));

        User user = new User(log.getAuthor().getName(), log.getAuthor().getEmail());
        MetadataEntry metadataEntry = new MetadataEntry();
        metadataContainer.setMetadata(metadataEntry);
        metadataEntry.setId(id);
        metadataEntry.setAuthor(user);
        metadataEntry.setUpdated(log.getWhen());
        metadataEntry.setMap(metadataTransfer[0]);
        metadataEntry.getLinks().add(new Link("self", id.toString()));
        metadataEntry.getLinks().add(new Link(Resource.HATEOAS_PREFIX + "serepo_content", baseUri.toString()));
        metadataEntry.getLinks().add(new Link(Resource.HATEOAS_PREFIX + "serepo_relations", new URI(null, null, baseUri.toString(), "relations", null).toString()));
        
        return Response.ok(metadataContainer).build();
        
      } else {
        return Response.status(Status.NOT_FOUND).build();
      }
      
    }
  }
  
  public static Response getRelations(URI baseUri, File repositoriesDir, String repositoryName,
      String commitId, String seItem, RelationsFile relationsFile) throws IOException, GitCommandException {
    File repositoryDir = Paths.get(repositoriesDir.getAbsolutePath(), repositoryName + ".git").toFile();
    if (!repositoryDir.isDirectory()) {
      String msg = String.format("Repository '%s' could not be found.", repositoryName);
      return Response.status(Status.NOT_FOUND)
                     .entity(msg)
                     .type(MediaType.TEXT_PLAIN_TYPE)
                     .build();
    }
    
    final List<String> relationIdentifiers = new ArrayList<>(relationsFile.getDefinitions().size());
    for (RelationDefinition relDef : relationsFile.getDefinitions()) {
        relationIdentifiers.add(relDef.getIdentifier());
    }
    
    try (GitRepository git = GitRepositoryBuilder.open(repositoryDir)) {
      GitCommitLog log = null;
      try {
        log = git.log(commitId);
      } catch (GitCommandException e) {
        String msg = String.format("CommitId '%s' could not be found.", commitId);
        return Response.status(Status.NOT_FOUND)
                       .entity(msg)
                       .type(MediaType.TEXT_PLAIN_TYPE)
                       .build();
      }

      final List<SectionLink> sectionLinks = new ArrayList<>();
      final boolean[] found = { false };
      git.readFilesByPath(commitId, seItem + ".md", new FileReader() {

        @Override
        public boolean read(GitFile gitFile) {
          try {
            found[0] = true;
            MarkdownReader markdownReader = new MarkdownReader(gitFile.getBytes());
            sectionLinks.addAll(markdownReader.getLinks(relationIdentifiers));
          } catch (IOException e) {
            e.printStackTrace(); // TODO what can we do here?
          }
          return false;
        }
      });
      
      if (found[0]) {
  
        URI id = null;
        try {
          id = new URI(null, null, baseUri.toString(), "relations", null);
        } catch (URISyntaxException e) {
          // THIS should never happen
        }
        RelationContainer relationContainer = new RelationContainer();
        relationContainer.setId(id);
        relationContainer.setUpdated(log.getWhen());
        relationContainer.getLinks().add(new Link("self", id.toString()));
        
        RelationEntry relationEntry = new RelationEntry();
        relationContainer.setEntry(relationEntry);
        relationEntry.setId(baseUri);
        relationEntry.setAuthor(new User(log.getAuthor().getName(), log.getAuthor().getEmail()));
        relationEntry.setUpdated(log.getWhen());
        
        // map relation identifier to relation
        Map<String, RelationDefinition> mapRelDef = new HashMap<>();
        for (RelationDefinition relDef : relationsFile.getDefinitions()) {
          mapRelDef.put(relDef.getIdentifier(), relDef);
        }
        for (SectionLink sectionLink : sectionLinks) {
          String target = null;
          try {
            target = baseUri.resolve(sectionLink.getUrl().replace(".md", "")).toString();
          } catch (IllegalArgumentException e) {
            target = sectionLink.getUrl();
          }
          RelationDefinition relDef = mapRelDef.get(sectionLink.getSection());
          relationEntry.getLinks().add(new Link(relDef.getIdentifier(), relDef.getUri(), target));
        }
  
        return Response.ok(relationContainer).build();
      } else {
        return Response.status(Status.NOT_FOUND).build();
      }
      
    }
  }
  
}
