package kz.astanait.visitor.visitor;

import kz.astanait.visitor.element.Directory;
import kz.astanait.visitor.element.File;
import kz.astanait.visitor.element.FileSystemElement;
import kz.astanait.visitor.util.SizeFormatter;

/**
 * Concrete visitor #3 - added AFTER File and Directory were finished, without
 * touching either of them. This is the proof of the Open/Closed Principle:
 * a brand-new operation is just a new class.
 *
 * <p>Single responsibility: render the visited structure as an indented tree.
 */
public final class PrintTreeVisitor implements FileSystemVisitor {

    private static final String INDENT = "    ";
    private static final String NEW_LINE = System.lineSeparator();

    private final StringBuilder tree = new StringBuilder();
    private int depth;

    @Override
    public void visitFile(File file) {
        appendLine("- " + file.getName() + " (" + SizeFormatter.format(file.getSizeInBytes()) + ")");
    }

    @Override
    public void visitDirectory(Directory directory) {
        appendLine("+ " + directory.getName() + "/");
        depth++;
        for (FileSystemElement child : directory.getChildren()) {
            child.accept(this);
        }
        depth--;
    }

    /** @return the rendered tree, one element per line. */
    public String getTree() {
        return tree.toString();
    }

    private void appendLine(String text) {
        tree.append(INDENT.repeat(depth)).append(text).append(NEW_LINE);
    }
}
