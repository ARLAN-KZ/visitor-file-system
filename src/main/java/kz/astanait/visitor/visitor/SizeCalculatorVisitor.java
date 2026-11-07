package kz.astanait.visitor.visitor;

import kz.astanait.visitor.element.Directory;
import kz.astanait.visitor.element.File;
import kz.astanait.visitor.element.FileSystemElement;

/**
 * Concrete visitor #1. Single responsibility: compute the total size of
 * everything it visits.
 *
 * <p>The visitor is stateful (it accumulates a total), so create a new
 * instance for every calculation.
 */
public final class SizeCalculatorVisitor implements FileSystemVisitor {

    private long totalSizeInBytes;

    @Override
    public void visitFile(File file) {
        totalSizeInBytes += file.getSizeInBytes();
    }

    @Override
    public void visitDirectory(Directory directory) {
        for (FileSystemElement child : directory.getChildren()) {
            child.accept(this); // double dispatch, step 2 happens inside accept()
        }
    }

    public long getTotalSizeInBytes() {
        return totalSizeInBytes;
    }
}
