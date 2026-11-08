package kz.astanait.visitor.visitor;

import kz.astanait.visitor.element.Directory;
import kz.astanait.visitor.element.File;
import kz.astanait.visitor.element.FileSystemElement;

public final class SizeCalculatorVisitor implements FileSystemVisitor {
    private long totalSizeInBytes;

    @Override
    public void visitFile(File file) {
        totalSizeInBytes += file.getSizeInBytes();
    }

    @Override
    public void visitDirectory(Directory directory) {
        for (FileSystemElement child : directory.getChildren()) {
            child.accept(this);
        }
    }

    public long getTotalSizeInBytes() {
        return totalSizeInBytes;
    }
}
