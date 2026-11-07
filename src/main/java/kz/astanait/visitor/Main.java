package kz.astanait.visitor;

import kz.astanait.visitor.element.Directory;
import kz.astanait.visitor.element.File;
import kz.astanait.visitor.util.SizeFormatter;
import kz.astanait.visitor.visitor.PrintTreeVisitor;
import kz.astanait.visitor.visitor.SearchVisitor;
import kz.astanait.visitor.visitor.SizeCalculatorVisitor;

/**
 * Client code: builds one file system tree and runs several different
 * visitors over the SAME structure.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        Directory root = buildSampleFileSystem();

        printHeader("0. PrintTreeVisitor (third visitor, added without changing File/Directory)");
        PrintTreeVisitor printer = new PrintTreeVisitor();
        root.accept(printer);
        System.out.print(printer.getTree());

        printHeader("1. SizeCalculatorVisitor");
        SizeCalculatorVisitor totalSize = new SizeCalculatorVisitor();
        root.accept(totalSize);
        System.out.println("Total size of '" + root.getName() + "': "
                + totalSize.getTotalSizeInBytes() + " B ("
                + SizeFormatter.format(totalSize.getTotalSizeInBytes()) + ")");

        printHeader("2. SearchVisitor");
        runSearch(root, SearchVisitor.byExtension("java"));
        runSearch(root, SearchVisitor.largerThan(1_000_000));
        runSearch(root, SearchVisitor.nameContains("report"));
        runSearch(root, SearchVisitor.byExtension("exe"));
    }

    private static Directory buildSampleFileSystem() {
        Directory docs = new Directory("docs")
                .add(new File("report.pdf", 1_258_291))
                .add(new File("notes.txt", 4_096))
                .add(new File("thesis.docx", 350_000));

        Directory test = new Directory("test")
                .add(new File("AppTest.java", 2_400));

        Directory src = new Directory("src")
                .add(new File("Main.java", 3_200))
                .add(new File("Utils.java", 1_850))
                .add(test);

        Directory images = new Directory("images")
                .add(new File("logo.png", 245_760))
                .add(new File("banner.png", 1_048_576));

        return new Directory("project")
                .add(docs)
                .add(src)
                .add(images)
                .add(new Directory("tmp"))
                .add(new File("README.md", 1_024));
    }

    private static void runSearch(Directory root, SearchVisitor search) {
        root.accept(search);
        System.out.println("Search by " + search.getDescription() + ":");
        if (search.getMatches().isEmpty()) {
            System.out.println("  (nothing found)");
        }
        for (String path : search.getMatches()) {
            System.out.println("  " + path);
        }
    }

    private static void printHeader(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
