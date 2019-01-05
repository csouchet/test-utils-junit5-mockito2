package org.csouchet.test.extension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import org.csouchet.test.exception.CreateException;
import org.csouchet.test.exception.WalkException;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import lombok.Cleanup;

/**
 * Add <code>@RegisterExtension static TemporaryFolderExtension
 * temporaryFolderExtension = new TemporaryFolderExtension();</code> in your
 * test class. <br>
 * In your test method, use <code>temporaryFolderExtension.newFile();</code>
 * <br>
 * <br>
 * <code>@RegisterExtension</code> fields must not be private or null (at
 * evaluation time) to be executed.
 *
 * @author SOUCHET Céline
 */
public class TemporaryFolderExtension implements BeforeEachCallback, AfterEachCallback {

	private final File parentFolder;
	private File folder;

	/**
	 * To create a temporary folder in the default temporary directory specified
	 * by the system property java.io.tmpdir
	 */
	public TemporaryFolderExtension() {
		this(null);
	}

	/**
	 * To create a temporary folder in a specific folder
	 *
	 * @param parentFolder
	 *            The folder where the temporary folder must be created
	 */
	public TemporaryFolderExtension(final File parentFolder) {
		this.parentFolder = parentFolder;
	}

	@Override
	public void beforeEach(final ExtensionContext extensionContext) throws CreateException {
		folder = createTemporaryFolderIn(parentFolder);
	}

	@Override
	public void afterEach(final ExtensionContext extensionContext) throws WalkException {
		if (folder != null) {
			@Cleanup
			final Stream<Path> walk = walk();
			walk.sorted(Comparator.reverseOrder())
					.map(Path::toFile)
					.forEach(File::delete);
		}
	}

	/**
	 * @return the location of this temporary folder.
	 */
	public File getRoot() {
		if (folder == null) {
			throw new IllegalStateException("The temporary folder has not yet been created");
		}
		return folder;
	}

	/**
	 * Create a new file with the given name under the temporary folder.
	 *
	 * @param fileName
	 *            The name of the file to create
	 * @return The new file
	 * @throws CreateException
	 *             Throws when an error occurs while creating file
	 */
	public File newFile(final String fileName) throws CreateException {
		final File file = new File(getRoot(), fileName);
		if (!createNewFile(file)) {
			throw new CreateException("a file with the name \'" + fileName + "\' already exists in the test folder");
		}
		return file;
	}

	/**
	 * Create a new file with a random name under the temporary folder.
	 *
	 * @return The new file
	 * @throws CreateException
	 *             Throws when an error occurs while creating file
	 */
	public File newFile() throws CreateException {
		return createTempFile(getRoot());
	}

	/**
	 * Create a new folder with a random name under the temporary folder.
	 *
	 * @return The new folder
	 * @throws CreateException
	 *             Throws when an error occurs while creating folder
	 */
	public File newFolder() throws CreateException {
		return createTemporaryFolderIn(getRoot());
	}

	/**
	 * Create a new folder with the given name under the temporary folder.
	 *
	 * @param folderName
	 *            The name of the folder to create
	 * @return The new folder
	 * @throws CreateException
	 *             Throws when an error occurs while creating folder
	 */
	public File newFolder(final String folderName) throws CreateException {
		return newFolder(new String[]{folderName});
	}

	/**
	 * Create new folders with the given name(s) under the temporary folder.
	 *
	 * @param folderNames
	 *            The name of the folders to create
	 * @return The new folders
	 * @throws CreateException
	 *             Throws when an error occurs while creating folder
	 */
	public File newFolder(final String... folderNames) throws CreateException {
		File file = getRoot();
		for (int i = 0; i < folderNames.length; i++) {
			final String folderName = folderNames[i];
			validateFolderName(folderName);
			file = new File(file, folderName);
			if (!file.mkdir() && isLastElementInArray(i, folderNames)) {
				throw new CreateException("A folder with the name \'" + folderName + "\' already exists");
			}
		}
		return file;
	}

	/**
	 * Validates if multiple path components were used while creating a folder.
	 *
	 * @param folderName
	 *            Name of the folder being created
	 */
	private static void validateFolderName(final String folderName) {
		final File tempFile = new File(folderName);
		if (tempFile.getParent() != null) {
			final String errorMsg = "Folder name cannot consist of multiple path components separated by a file separator."
					+ " Please use newFolder('MyParentFolder','MyFolder') to create hierarchies of folders";
			throw new IllegalArgumentException(errorMsg);
		}
	}

	private static boolean isLastElementInArray(final int index, final String[] array) {
		return index == array.length - 1;
	}

	private static File createTemporaryFolderIn(final File parentFolder) throws CreateException {
		final File createdFolder = createTempFile(parentFolder);
		createdFolder.delete();
		createdFolder.mkdir();
		return createdFolder;
	}

	private static boolean createNewFile(final File file) throws CreateException {
		try {
			return file.createNewFile();
		} catch (final IOException e) {
			throw new CreateException(e);
		}
	}
	private static File createTempFile(final File parent) throws CreateException {
		try {
			return File.createTempFile("junit", null, parent);
		} catch (final IOException e) {
			throw new CreateException(e);
		}
	}

	private Stream<Path> walk() throws WalkException {
		try {
			return Files.walk(folder.toPath());
		} catch (final IOException e) {
			throw new WalkException(e);
		}
	}

}
