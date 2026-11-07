package kz.astanait.visitor.element;

import kz.astanait.visitor.visitor.FileSystemVisitor;

/**
 * Element interface of the Visitor pattern.
 *
 * <p>Every node of the file system (a file or a directory) can be "visited".
 * The element does not know WHAT the visitor will do - it only knows how to
 * hand itself over to the visitor ({@link #accept(FileSystemVisitor)}).
 */
public interface FileSystemElement {

    /** @return the simple name of this element (not the full path). */
    String getName();

    /**
     * First half of double dispatch: the concrete element chooses the matching
     * {@code visitX()} method of the visitor and passes {@code this} to it.
     */
    void accept(FileSystemVisitor visitor);
}
