package kz.astanait.visitor.element;

import kz.astanait.visitor.visitor.FileSystemVisitor;

public interface FileSystemElement {
    String getName();

    void accept(FileSystemVisitor visitor);
}
