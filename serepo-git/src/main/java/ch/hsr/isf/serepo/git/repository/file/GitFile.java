package ch.hsr.isf.serepo.git.repository.file;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteStreams;

public class GitFile {

  private String name;
  private String path;
  private InputStream inputStream;

  public GitFile(String name, String path, InputStream inputStream) {
    this.name = name;
    this.path = path;
    this.inputStream = inputStream;
  }

  public String getName() {
    return name;
  }

  public String getPath() {
    return path;
  }
  
  public String getFullPath() {
    return path + name;
  }

  /**
   * The caller needs to close the stream.
   * 
   * @return
   */
  public InputStream getInputStream() {
    return inputStream;
  }

  /**
   * Closes the underlying {@link InputStream}. Do not call this method twice!
   * 
   * @return
   * @throws IOException
   */
  public byte[] getBytes() throws IOException {
    try (InputStream is = inputStream) {
      return ByteStreams.toByteArray(inputStream);
    }
  }

}
