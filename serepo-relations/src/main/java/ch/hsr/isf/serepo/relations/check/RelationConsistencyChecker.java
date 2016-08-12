package ch.hsr.isf.serepo.relations.check;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import ch.hsr.isf.serepo.commons.Uri;
import ch.hsr.isf.serepo.git.error.GitCommandException;
import ch.hsr.isf.serepo.git.repository.GitRepository;
import ch.hsr.isf.serepo.git.repository.GitRepository.FileReader;
import ch.hsr.isf.serepo.git.repository.GitRepositoryBuilder;
import ch.hsr.isf.serepo.git.repository.file.GitFile;
import ch.hsr.isf.serepo.git.repository.log.GitCommitLog;
import ch.hsr.isf.serepo.markdown.MarkdownReader;
import ch.hsr.isf.serepo.markdown.SectionLink;
import ch.hsr.isf.serepo.relations.RelationDefinition;
import ch.hsr.isf.serepo.relations.RelationsFile;
import ch.hsr.isf.serepo.relations.check.CheckResult.Inconsistency;

public class RelationConsistencyChecker {

	public RelationConsistencyChecker() {
	}

	public CheckResult check(final File repository, final String revstr, final RelationsFile relationsFile) throws GitCommandException, IOException {
		
		final CheckResult result = new CheckResult();
		
		try (final GitRepository git = GitRepositoryBuilder.open(repository)) {
			
			final List<String> relationIdentifiers = new ArrayList<>(relationsFile.getDefinitions().size());
			for (RelationDefinition relDef : relationsFile.getDefinitions()) {
				relationIdentifiers.add(relDef.getIdentifier());
			}
			
			try {
			  git.log(revstr);
			} catch (GitCommandException e) {
			  String msg = String.format("CommitId '%s' does not exist.", revstr);
			  throw new GitCommandException(msg, revstr);
			}
			
			git.readFilesByExtension(revstr, ".md", new FileReader() {
              
              @Override
              public boolean read(GitFile gitFile) {
                if ("readme.md".equals(gitFile.getFullPath())) {
                  return true;
                }
                
                MarkdownReader mdReader = null;
                try {
                  mdReader = new MarkdownReader(gitFile.getBytes());
                } catch (IOException e) {
                  e.printStackTrace(); // TODO
                  return true;
                }
                for (SectionLink link : mdReader.getLinks(relationIdentifiers)) {
                  try {
                    URI linkAsUri = new URI(link.getUrl());
                    if (linkAsUri.isAbsolute()) {
                        // TODO currently we dont check absolute uris. We should probaly provide some warnings iff they don't exist.
                    } else {
                        URI target = Uri.of(gitFile.getFullPath()).resolve(linkAsUri.toString());
                        try {
                          final boolean[] found = { false };
                          git.readFilesByPath(revstr, target.toString().replace("%20", " "), new FileReader() { // TODO Hack!
                            @Override
                            public boolean read(GitFile gitFile) {
                              found[0] = true;
                              try (InputStream is = gitFile.getInputStream()) {
                              } catch (IOException e) {
                              }
                              return false;
                            }
                          });
                          if (!found[0]) {
                            GitCommitLog commitLog = git.logLatest(revstr, gitFile.getFullPath());
                            final String file = gitFile.getPath() + gitFile.getName().replace(".md", "");
                            final String type = link.getSection();
                            final String url = link.getUrl();
                            final String inconsistencyReason = "The target could not be found.";
                            result.addInconsistency(new Inconsistency(file, type, url, inconsistencyReason, commitLog));
                          }
                        } catch (GitCommandException e) {
                          e.printStackTrace(); // TODO
                        }
                    }
                  } catch (URISyntaxException e) {
                    try {
                      GitCommitLog commitLog = git.logLatest(revstr, gitFile.getFullPath());
                      final String file = gitFile.getPath() + gitFile.getName().replace(".md", "");
                      final String type = link.getSection();
                      final String url = link.getUrl();
                      final String inconsistencyReason = "The url is not valid. Reason: " + e.getReason();
                      result.addInconsistency(new Inconsistency(file, type, url, inconsistencyReason, commitLog));
                    } catch (GitCommandException gce) {
                      gce.printStackTrace(); // TODO
                    }
                  }
                }
                return true;
              }
            });
		}
		
		return result;
		
	}
	
}
