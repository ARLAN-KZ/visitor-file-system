package kz.astanait.visitor.visitor;

import kz.astanait.visitor.element.Directory;
import kz.astanait.visitor.element.File;

public interface FileSystemVisitor {
    void visitFile(File file);

    void visitDirectory(Directory directory);
}
