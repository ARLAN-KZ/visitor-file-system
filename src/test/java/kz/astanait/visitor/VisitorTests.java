package kz.astanait.visitor;

import kz.astanait.visitor.element.Directory;
import kz.astanait.visitor.element.File;
import kz.astanait.visitor.visitor.FileSystemVisitor;
import kz.astanait.visitor.visitor.PrintTreeVisitor;
import kz.astanait.visitor.visitor.SearchVisitor;
import kz.astanait.visitor.visitor.SizeCalculatorVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Dependency-free test runner (no JUnit needed, so the project compiles with
 * plain javac). Run: java -cp out kz.astanait.visitor.VisitorTests
 * Exit code is 0 when all tests pass and 1 otherwise.
 */
public final class VisitorTests {

    private static int passed;
    private static int failed;

    public static void main(String[] args) {
        run("File.accept dispatches to visitFile", VisitorTests::fileDispatchesToVisitFile);
        run("Directory.accept dispatches to visitDirectory", VisitorTests::directoryDispatchesToVisitDirectory);
        run("Size: single file", VisitorTests::sizeOfSingleFile);
        run("Size: nested directories", VisitorTests::sizeOfNestedDirectories);
        run("Size: empty directory is zero", VisitorTests::sizeOfEmptyDirectory);
        run("Search: by extension returns full paths", VisitorTests::searchByExtension);
        run("Search: case-insensitive extension", VisitorTests::searchExtensionIsCaseInsensitive);
        run("Search: larger than", VisitorTests::searchLargerThan);
        run("Search: no matches", VisitorTests::searchWithoutMatches);
        run("Search: custom criterion", VisitorTests::searchCustomCriterion);
        run("Two visitors on the same structure", VisitorTests::twoVisitorsOnSameStructure);
        run("PrintTree: indents by depth", VisitorTests::printTreeIndentsByDepth);
        run("File: extension parsing", VisitorTests::fileExtension);
        run("Validation of inputs", VisitorTests::validation);
        run("Children view is read-only", VisitorTests::childrenAreReadOnly);

        System.out.println();
        System.out.println("Passed: " + passed + ", failed: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ---------- double dispatch ----------

    private static void fileDispatchesToVisitFile() {
        RecordingVisitor visitor = new RecordingVisitor();
        new File("a.txt", 1).accept(visitor);
        assertEquals(List.of("file:a.txt"), visitor.calls);
    }

    private static void directoryDispatchesToVisitDirectory() {
        RecordingVisitor visitor = new RecordingVisitor();
        new Directory("d").accept(visitor);
        assertEquals(List.of("dir:d"), visitor.calls);
    }

    // ---------- SizeCalculatorVisitor ----------

    private static void sizeOfSingleFile() {
        SizeCalculatorVisitor visitor = new SizeCalculatorVisitor();
        new File("a.bin", 42).accept(visitor);
        assertEquals(42L, visitor.getTotalSizeInBytes());
    }

    private static void sizeOfNestedDirectories() {
        SizeCalculatorVisitor visitor = new SizeCalculatorVisitor();
        sampleTree().accept(visitor);
        assertEquals(1_000L + 200L + 30L + 4L, visitor.getTotalSizeInBytes());
    }

    private static void sizeOfEmptyDirectory() {
        SizeCalculatorVisitor visitor = new SizeCalculatorVisitor();
        new Directory("empty").accept(visitor);
        assertEquals(0L, visitor.getTotalSizeInBytes());
    }

    // ---------- SearchVisitor ----------

    private static void searchByExtension() {
        SearchVisitor visitor = SearchVisitor.byExtension("java");
        sampleTree().accept(visitor);
        assertEquals(List.of("root/src/A.java", "root/src/inner/B.java"), visitor.getMatches());
    }

    private static void searchExtensionIsCaseInsensitive() {
        SearchVisitor visitor = SearchVisitor.byExtension("JAVA");
        sampleTree().accept(visitor);
        assertEquals(2, visitor.getMatches().size());
    }

    private static void searchLargerThan() {
        SearchVisitor visitor = SearchVisitor.largerThan(100);
        sampleTree().accept(visitor);
        assertEquals(List.of("root/data.bin", "root/src/A.java"), visitor.getMatches());
    }

    private static void searchWithoutMatches() {
        SearchVisitor visitor = SearchVisitor.byExtension("exe");
        sampleTree().accept(visitor);
        assertTrue(visitor.getMatches().isEmpty(), "expected no matches");
    }

    private static void searchCustomCriterion() {
        SearchVisitor visitor = new SearchVisitor("empty files", file -> file.getSizeInBytes() == 0);
        new Directory("d").add(new File("zero", 0)).add(new File("one", 1)).accept(visitor);
        assertEquals(List.of("d/zero"), visitor.getMatches());
    }

    // ---------- several visitors, one structure ----------

    private static void twoVisitorsOnSameStructure() {
        Directory tree = sampleTree();
        SizeCalculatorVisitor size = new SizeCalculatorVisitor();
        SearchVisitor search = SearchVisitor.byExtension("java");
        tree.accept(size);
        tree.accept(search);
        assertEquals(1_234L, size.getTotalSizeInBytes());
        assertEquals(2, search.getMatches().size());
    }

    private static void printTreeIndentsByDepth() {
        PrintTreeVisitor visitor = new PrintTreeVisitor();
        new Directory("root").add(new Directory("sub").add(new File("f.txt", 10))).accept(visitor);
        String nl = System.lineSeparator();
        assertEquals("+ root/" + nl + "    + sub/" + nl + "        - f.txt (10 B)" + nl, visitor.getTree());
    }

    // ---------- elements ----------

    private static void fileExtension() {
        assertEquals("pdf", new File("report.pdf", 1).getExtension());
        assertEquals("gz", new File("archive.tar.gz", 1).getExtension());
        assertEquals("", new File("Makefile", 1).getExtension());
        assertEquals("", new File("trailingdot.", 1).getExtension());
    }

    private static void validation() {
        assertThrows(() -> new File("a", -1));
        assertThrows(() -> new File(" ", 1));
        assertThrows(() -> new File(null, 1));
        assertThrows(() -> new Directory(""));
        assertThrows(() -> new Directory("d").add(null));
        Directory self = new Directory("self");
        assertThrows(() -> self.add(self));
    }

    private static void childrenAreReadOnly() {
        Directory directory = new Directory("d").add(new File("a", 1));
        assertThrows(() -> directory.getChildren().add(new File("hack", 1)));
    }

    // ---------- helpers ----------

    /**
     * root
     * +-- data.bin (1000)
     * +-- src
     *     +-- A.java (200)
     *     +-- inner
     *         +-- B.java (30)
     * +-- notes.txt (4)
     */
    private static Directory sampleTree() {
        Directory inner = new Directory("inner").add(new File("B.java", 30));
        Directory src = new Directory("src").add(new File("A.java", 200)).add(inner);
        return new Directory("root")
                .add(new File("data.bin", 1_000))
                .add(src)
                .add(new File("notes.txt", 4));
    }

    /** Records which visitX() method was called - used to verify double dispatch. */
    private static final class RecordingVisitor implements FileSystemVisitor {
        private final List<String> calls = new ArrayList<>();

        @Override
        public void visitFile(File file) {
            calls.add("file:" + file.getName());
        }

        @Override
        public void visitDirectory(Directory directory) {
            calls.add("dir:" + directory.getName());
        }
    }

    private static void run(String name, Runnable test) {
        try {
            test.run();
            passed++;
            System.out.println("[PASS] " + name);
        } catch (RuntimeException | AssertionError e) {
            failed++;
            System.out.println("[FAIL] " + name + " -> " + e);
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("expected <" + expected + "> but was <" + actual + ">");
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertThrows(Runnable action) {
        try {
            action.run();
        } catch (RuntimeException expected) {
            return;
        }
        throw new AssertionError("expected an exception, but none was thrown");
    }
}
