import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TruffulaPrinterTest {

    /**
     * Checks if the current operating system is Windows.
     *
     * This method reads the "os.name" system property and checks whether it
     * contains the substring "win", which indicates a Windows-based OS.
     * 
     * You do not need to modify this method.
     *
     * @return true if the OS is Windows, false otherwise
     */
    private static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }

    /**
     * Creates a hidden file in the specified parent folder.
     * 
     * The filename MUST start with a dot (.).
     *
     * On Unix-like systems, files prefixed with a dot (.) are treated as hidden.
     * On Windows, this method also sets the DOS "hidden" file attribute.
     * 
     * You do not need to modify this method, but you SHOULD use it when creating hidden files
     * for your tests. This will make sure that your tests work on both Windows and UNIX-like systems.
     *
     * @param parentFolder the directory in which to create the hidden file
     * @param filename the name of the hidden file; must start with a dot (.)
     * @return a File object representing the created hidden file
     * @throws IOException if an I/O error occurs during file creation or attribute setting
     * @throws IllegalArgumentException if the filename does not start with a dot (.)
     */
    private static File createHiddenFile(File parentFolder, String filename) throws IOException {
        if(!filename.startsWith(".")) {
            throw new IllegalArgumentException("Hidden files/folders must start with a '.'");
        }
        File hidden = new File(parentFolder, filename);
        hidden.createNewFile();
        if(isWindows()) {
            Path path = Paths.get(hidden.toURI());
            Files.setAttribute(path, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
        }
        return hidden;
    }

    @Test
    public void testPrintTree_ExactOutput_WithCustomPrintStream(@TempDir File tempDir) throws IOException {
        // Build the example directory structure:
        // myFolder/
        //    .hidden.txt
        //    Apple.txt
        //    banana.txt
        //    Documents/
        //       images/
        //          Cat.png
        //          cat.png
        //          Dog.png
        //       notes.txt
        //       README.md
        //    zebra.txt

        // Create "myFolder"
        File myFolder = new File(tempDir, "myFolder");
        assertTrue(myFolder.mkdir(), "myFolder should be created");

        // Create visible files in myFolder
        File apple = new File(myFolder, "Apple.txt");
        File banana = new File(myFolder, "banana.txt");
        File zebra = new File(myFolder, "zebra.txt");
        apple.createNewFile();
        banana.createNewFile();
        zebra.createNewFile();

        // Create a hidden file in myFolder
        createHiddenFile(myFolder, ".hidden.txt");

        // Create subdirectory "Documents" in myFolder
        File documents = new File(myFolder, "Documents");
        assertTrue(documents.mkdir(), "Documents directory should be created");

        // Create files in Documents
        File readme = new File(documents, "README.md");
        File notes = new File(documents, "notes.txt");
        readme.createNewFile();
        notes.createNewFile();

        // Create subdirectory "images" in Documents
        File images = new File(documents, "images");
        assertTrue(images.mkdir(), "images directory should be created");

        // Create files in images
        File cat = new File(images, "cat.png");
        File dog = new File(images, "Dog.png");
        cat.createNewFile();
        dog.createNewFile();

        // Set up TruffulaOptions with showHidden = false and useColor = true
        TruffulaOptions options = new TruffulaOptions(myFolder, false, true);

        // Capture output using a custom PrintStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);

        // Instantiate TruffulaPrinter with custom PrintStream
        TruffulaPrinter printer = new TruffulaPrinter(options, printStream);

        // Call printTree (output goes to printStream)
        printer.printTree();

        // Retrieve printed output
        String output = baos.toString();
        String nl = System.lineSeparator();

        // Build expected output with exact colors and indentation
        ConsoleColor reset = ConsoleColor.RESET;
        ConsoleColor white = ConsoleColor.WHITE;
        ConsoleColor purple = ConsoleColor.PURPLE;
        ConsoleColor yellow = ConsoleColor.YELLOW;

        StringBuilder expected = new StringBuilder();
        expected.append(white).append("myFolder/").append(nl).append(reset);
        expected.append(purple).append("   Apple.txt").append(nl).append(reset);
        expected.append(purple).append("   banana.txt").append(nl).append(reset);
        expected.append(purple).append("   Documents/").append(nl).append(reset);
        expected.append(yellow).append("      images/").append(nl).append(reset);
        expected.append(white).append("         cat.png").append(nl).append(reset);
        expected.append(white).append("         Dog.png").append(nl).append(reset);
        expected.append(yellow).append("      notes.txt").append(nl).append(reset);
        expected.append(yellow).append("      README.md").append(nl).append(reset);
        expected.append(purple).append("   zebra.txt").append(nl).append(reset);

        // Assert that the output matches the expected output exactly
        assertEquals(expected.toString(), output);
    }


    @Test
    public void testPrintTreeSimpleTest(@TempDir File tempDir) throws IOException {
        // Creates root folder
        File root = new File(tempDir, "rootFolder");
        assertTrue(root.mkdir(), "rootFolder should be created");

        // Creates files and directories under rootFolder
        File fileA = new File(root, "fileA.txt");
        File fileB = new File(root, "fileB.txt");

        File subDir = new File(root, "subDir");
        assertTrue(subDir.mkdir(), "subDir should be created");

        File subFile = new File(subDir, "subFile.txt");
        fileA.createNewFile();
        fileB.createNewFile();
        subFile.createNewFile();

        // color enabled now
        TruffulaOptions options = new TruffulaOptions(root, false, false); // <--- enable color

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);

        TruffulaPrinter printer = new TruffulaPrinter(options, printStream);
        printer.printTree();

        String nl = System.lineSeparator();
        String WHITE = "\u001B[0;37m";
        String RESET = "\u001B[0m";

        StringBuilder expected = new StringBuilder();
        expected.append(WHITE).append("rootFolder/").append(nl).append(RESET);
        expected.append(WHITE).append("   fileA.txt").append(nl).append(RESET);
        expected.append(WHITE).append("   fileB.txt").append(nl).append(RESET);
        expected.append(WHITE).append("   subDir/").append(nl).append(RESET);
        expected.append(WHITE).append("      subFile.txt").append(nl).append(RESET);

        assertEquals(expected.toString(), baos.toString());
    }//end simpleTest

    @Test
    public void testPrintTreeWithHiddenFiles(@TempDir File tempDir) throws IOException {
        // Create root folder
        File root = new File(tempDir, "rootFolder");
        assertTrue(root.mkdir(), "rootFolder should be created");

        // Create visible files and directories
        File fileA = new File(root, "fileA.txt");
        File fileB = new File(root, "fileB.txt");
        File subDir = new File(root, "subDir");
        assertTrue(subDir.mkdir(), "subDir should be created");
        File subFile = new File(subDir, "subFile.txt");

        // Create hidden files and directories
        File hiddenFile = new File(root, ".hiddenFile.txt");
        File hiddenDir = new File(root, ".hiddenDir");
        assertTrue(hiddenDir.mkdir(), "hiddenDir should be created");
        File hiddenSubFile = new File(hiddenDir, "hiddenSubFile.txt");

        // Create all files
        fileA.createNewFile();
        fileB.createNewFile();
        subFile.createNewFile();
        hiddenFile.createNewFile();
        hiddenSubFile.createNewFile();

        // Show hidden = true, color enabled = true
        TruffulaOptions options = new TruffulaOptions(root, true, false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);

        TruffulaPrinter printer = new TruffulaPrinter(options, printStream);
        printer.printTree();

        String nl = System.lineSeparator();
        String WHITE = "\u001B[0;37m";
        String RESET = "\u001B[0m";

        StringBuilder expected = new StringBuilder();
        expected.append(WHITE).append("rootFolder/").append(nl).append(RESET);
        expected.append(WHITE).append("   .hiddenDir/").append(nl).append(RESET);
        expected.append(WHITE).append("      hiddenSubFile.txt").append(nl).append(RESET);
        expected.append(WHITE).append("   .hiddenFile.txt").append(nl).append(RESET);
        expected.append(WHITE).append("   fileA.txt").append(nl).append(RESET);
        expected.append(WHITE).append("   fileB.txt").append(nl).append(RESET);
        expected.append(WHITE).append("   subDir/").append(nl).append(RESET);
        expected.append(WHITE).append("      subFile.txt").append(nl).append(RESET);

        assertEquals(expected.toString(), baos.toString());
    }//end withHiddenFiles

    @Test
    public void testPrintTreeWithColors(@TempDir File tempDir) throws IOException {
        // Create root folder
        File root = new File(tempDir, "rootFolder");
        assertTrue(root.mkdir(), "rootFolder should be created");

        // Create visible files and directories
        File fileA = new File(root, "fileA.txt");
        File fileB = new File(root, "fileB.txt");
        File subDir = new File(root, "subDir");
        assertTrue(subDir.mkdir(), "subDir should be created");
        File subFile = new File(subDir, "subFile.txt");

        // Create hidden files and directories
        File hiddenFile = new File(root, ".hiddenFile.txt");
        File hiddenDir = new File(root, ".hiddenDir");
        assertTrue(hiddenDir.mkdir(), "hiddenDir should be created");
        File hiddenSubFile = new File(hiddenDir, "hiddenSubFile.txt");

        // Create all files
        fileA.createNewFile();
        fileB.createNewFile();
        subFile.createNewFile();
        hiddenFile.createNewFile();
        hiddenSubFile.createNewFile();

        // Show hidden = true, color enabled = true
        TruffulaOptions options = new TruffulaOptions(root, true, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);

        TruffulaPrinter printer = new TruffulaPrinter(options, printStream);
        printer.printTree();

        String nl = System.lineSeparator();
        String WHITE = "\u001B[0;37m";
        String PURPLE = "\033[0;35m";
        String YELLOW = "\033[0;33m";
        String RESET = "\u001B[0m";

        StringBuilder expected = new StringBuilder();
        expected.append(WHITE).append("rootFolder/").append(nl).append(RESET);
        expected.append(PURPLE).append("   .hiddenDir/").append(nl).append(RESET);
        expected.append(YELLOW).append("      hiddenSubFile.txt").append(nl).append(RESET);
        expected.append(PURPLE).append("   .hiddenFile.txt").append(nl).append(RESET);
        expected.append(PURPLE).append("   fileA.txt").append(nl).append(RESET);
        expected.append(PURPLE).append("   fileB.txt").append(nl).append(RESET);
        expected.append(PURPLE).append("   subDir/").append(nl).append(RESET);
        expected.append(YELLOW).append("      subFile.txt").append(nl).append(RESET);

        assertEquals(expected.toString(), baos.toString());
    }//end withHiddenFiles

    @Test
    public void testPrintTreeWithNoColors(@TempDir File tempDir) throws IOException {
        // Create root folder
        File root = new File(tempDir, "rootFolder");
        assertTrue(root.mkdir(), "rootFolder should be created");

        // Create visible files and directories
        File fileA = new File(root, "fileA.txt");
        File fileB = new File(root, "fileB.txt");
        File subDir = new File(root, "subDir");
        assertTrue(subDir.mkdir(), "subDir should be created");
        File subFile = new File(subDir, "subFile.txt");

        // Create hidden files and directories
        File hiddenFile = new File(root, ".hiddenFile.txt");
        File hiddenDir = new File(root, ".hiddenDir");
        assertTrue(hiddenDir.mkdir(), "hiddenDir should be created");
        File hiddenSubFile = new File(hiddenDir, "hiddenSubFile.txt");

        // Create all files
        fileA.createNewFile();
        fileB.createNewFile();
        subFile.createNewFile();
        hiddenFile.createNewFile();
        hiddenSubFile.createNewFile();

        // Show hidden = true, color enabled = true
        TruffulaOptions options = new TruffulaOptions(root, true, false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);

        TruffulaPrinter printer = new TruffulaPrinter(options, printStream);
        printer.printTree();

        String nl = System.lineSeparator();
        String WHITE = "\u001B[0;37m";
        String RESET = "\u001B[0m";

        StringBuilder expected = new StringBuilder();
        expected.append(WHITE).append("rootFolder/").append(nl).append(RESET);
        expected.append(WHITE).append("   .hiddenDir/").append(nl).append(RESET);
        expected.append(WHITE).append("      hiddenSubFile.txt").append(nl).append(RESET);
        expected.append(WHITE).append("   .hiddenFile.txt").append(nl).append(RESET);
        expected.append(WHITE).append("   fileA.txt").append(nl).append(RESET);
        expected.append(WHITE).append("   fileB.txt").append(nl).append(RESET);
        expected.append(WHITE).append("   subDir/").append(nl).append(RESET);
        expected.append(WHITE).append("      subFile.txt").append(nl).append(RESET);

        assertEquals(expected.toString(), baos.toString());
    }//end withHiddenFiles

}//end file
