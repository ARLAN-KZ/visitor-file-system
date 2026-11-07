package kz.astanait.visitor.visitor;

import kz.astanait.visitor.element.Directory;
import kz.astanait.visitor.element.File;

/**
 * Visitor interface: one {@code visitX()} method per concrete element type.
 *
 * <p>Because the methods are overloaded by the PARAMETER type, the compiler
 * binds {@code visitFile(File)} and {@code visitDirectory(Directory)} - no
 * {@code instanceof} checks are ever required.
 *
 * <p>Adding a new operation = adding a new implementation of this interface.
 * (Adding a new element type would require a new method here - the known
 * trade-off of the Visitor pattern.)
 */
public interface FileSystemVisitor {

    void visitFile(File file);

    void visitDirectory(Directory directory);
}
