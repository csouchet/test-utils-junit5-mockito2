package org.csouchet.test.extension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/***
 * Add "@RegisterExtension static TemporaryFolderExtension temporaryFolderExtension = new TemporaryFolderExtension();"
 * in your test class. </br>
 * In your test method, use "temporaryFolderExtension.newFile();" </br>
 * </br>
 * "@RegisterExtension" fields must not be private or null (at evaluation time) to be executed.
 * 
 * @author Céline Souchet
 */
public class TemporaryFolderExtension implements BeforeEachCallback, AfterEachCallback {

    private final File parentFolder;
    private File folder;

    /**
     * Default constructor
     */
    public TemporaryFolderExtension() {
        this(null);
    }

    /**
     * @param parentFolder
     */
    public TemporaryFolderExtension(final File parentFolder) {
        this.parentFolder = parentFolder;
    }

    @Override
    public void beforeEach(final ExtensionContext extensionContext) throws IOException {
        folder = createTemporaryFolderIn(parentFolder);
    }

    @Override
    public void afterEach(final ExtensionContext extensionContext) throws IOException {
        if (folder != null) {
            final Stream<Path> walk = Files.walk(folder.toPath());
            try {
                walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            } finally {
                walk.close();
            }
        }
    }

    /**
     * @return the location of this temporary folder.
     */
    public File getRoot() {
        if (folder == null) {
            throw new IllegalStateException("the temporary folder has not yet been created");
        }
        return folder;
    }

    /**
     * Returns a new fresh file with the given name under the temporary folder.
     * 
     * @param fileName
     * @return
     * @throws IOException
     */
    public File newFile(final String fileName) throws IOException {
        final File file = new File(getRoot(), fileName);
        if (!file.createNewFile()) {
            throw new IOException("a file with the name \'" + fileName + "\' already exists in the test folder");
        }
        return file;
    }

    /**
     * Returns a new fresh file with a random name under the temporary folder.
     * 
     * @return
     * @throws IOException
     */
    public File newFile() throws IOException {
        return File.createTempFile("junit", null, getRoot());
    }

    /**
     * Returns a new fresh folder with a random name under the temporary folder.
     * 
     * @return
     * @throws IOException
     */
    public File newFolder() throws IOException {
        return createTemporaryFolderIn(getRoot());
    }

    /**
     * Returns a new fresh folder with the given name under the temporary folder.
     * 
     * @param folder
     * @return
     * @throws IOException
     */
    public File newFolder(final String folder) throws IOException {
        return newFolder(new String[] { folder });
    }

    /**
     * Returns a new fresh folder with the given name(s) under the temporary folder.
     * 
     * @param folderNames
     * @return
     * @throws IOException
     */
    public File newFolder(final String... folderNames) throws IOException {
        File file = getRoot();
        for (int i = 0; i < folderNames.length; i++) {
            final String folderName = folderNames[i];
            validateFolderName(folderName);
            file = new File(file, folderName);
            if (!file.mkdir() && isLastElementInArray(i, folderNames)) {
                throw new IOException("a folder with the name \'" + folderName + "\' already exists");
            }
        }
        return file;
    }

    /**
     * Validates if multiple path components were used while creating a folder.
     * 
     * @param folderName Name of the folder being created
     */
    private static void validateFolderName(final String folderName) throws IOException {
        final File tempFile = new File(folderName);
        if (tempFile.getParent() != null) {
            final String errorMsg = "Folder name cannot consist of multiple path components separated by a file separator."
                    + " Please use newFolder('MyParentFolder','MyFolder') to create hierarchies of folders";
            throw new IOException(errorMsg);
        }
    }

    private static boolean isLastElementInArray(final int index, final String[] array) {
        return index == array.length - 1;
    }

    private static File createTemporaryFolderIn(final File parentFolder) throws IOException {
        final File createdFolder = File.createTempFile("junit", "", parentFolder);
        createdFolder.delete();
        createdFolder.mkdir();
        return createdFolder;
    }

}
