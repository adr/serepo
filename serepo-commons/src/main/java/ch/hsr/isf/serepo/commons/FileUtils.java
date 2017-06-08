package ch.hsr.isf.serepo.commons;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

public class FileUtils {

	private FileUtils() {
	}
	
	public static String cleanFilename(String filename) {
		return filename.replaceAll("[^a-zA-Z0-9\\@\\!\\#\\%\\&\\(\\)\\_\\-\\=\\+\\[\\]\\{\\}\\,\\;\\.äÄöÖüÜ ]+", "_");
	}
	
	public static boolean isValidRepositoryName(String name) {
	  boolean empty = Strings.isNullOrEmpty(name);
	  boolean validChars = Strings.nullToEmpty(name).matches("[a-zA-Z0-9-]+");
	  return !empty && validChars;
	}
	
	public static String relativize(File rootDir, File file) {
		return relativize(rootDir.getAbsolutePath(), file.getAbsolutePath());
	}
	
	public static String relativize(String root, String absoluteFilePath) {
		return absoluteFilePath.substring(root.length() + 1);
	}
	
	public static List<File> listDirs(File root) {
		
		List<File> files = null;
		if (root == null || !root.isDirectory()) {
			files = ImmutableList.of();
		} else {
			files = ImmutableList.copyOf(root.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			}));
		}
		
		return files;
		
	}
	
	/**
	 * Deletes a directory with all its sub-directories.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void delete(File file) throws IOException {
	  org.apache.commons.io.FileUtils.forceDelete(file);
	  /* This code leads to file access problems under MS Windows!
		Path directory = file.toPath();
		java.nio.file.Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				java.nio.file.Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				java.nio.file.Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}

		});
		*/
	}
	
	/**
	 * Returns a list with all files according to the filer options. If
	 * <code>root</code> is null or not a directory, an empty list will be
	 * returned.
	 * 
	 * @param root
	 * @param recursive
	 * @param acceptedExtensions
	 *            may be empty but not null. Accepted extensions must be
	 *            provided without a dot (.) e.g. for Markdownfiles: md
	 * @return
	 */
	public static List<File> listFiles(File root, boolean recursive, final String... acceptedExtensions) {

		if (root == null || !root.isDirectory()) {
			return new ArrayList<>();
		}

		FluentIterable<File> fluentIterable;

		if (recursive) {
			fluentIterable = Files.fileTreeTraverser().preOrderTraversal(root);
		} else {
			int length = root.listFiles().length;
			fluentIterable = Files.fileTreeTraverser().breadthFirstTraversal(root).limit(length);
		}

		return listFiles(fluentIterable, recursive, acceptedExtensions);

	}

	private static List<File> listFiles(FluentIterable<File> fluentIterable, boolean recursive,
			String... acceptedExtensions) {
		fluentIterable = fluentIterable.filter(new Predicate<File>() {
			@Override
			public boolean apply(File input) {
				return input.isFile();
			}
		});
		for (final String extension : acceptedExtensions) {
			fluentIterable = fluentIterable.filter(new Predicate<File>() {
				@Override
				public boolean apply(File input) {
					return Files.getFileExtension(input.getName()).equals(extension);
				}
			});
		}
		return fluentIterable.toList();
	}

}
