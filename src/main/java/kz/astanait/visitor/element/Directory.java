package kz.astanait.visitor.element;

import kz.astanait.visitor.visitor.FileSystemVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Concrete element and the Object Structure of the pattern: a directory holds
 * files and other directories (Composite-like structure).
 *
 * <p>Like {@link File}, it has no operation logic. It only stores children and
 * exposes them READ-ONLY, so a visitor can traverse them but cannot break the
 * structure (this is the one deliberate encapsulation trade-off, see README).
 */
public final class Directory implements FileSystemElement {

    private final String name;
    private final List<FileSystemElement> children = new ArrayList<>();

    public Directory(String name) {
        this.name = File.requireValidName(name);
    }

    /** Adds a child and returns this directory, so calls can be chained. */
    public Directory add(FileSystemElement child) {
        Objects.requireNonNull(child, "child must not be null");
        if (child == this) {
            throw new IllegalArgumentException("A directory cannot contain itself: " + name);
        }
        children.add(child);
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    /** @return an unmodifiable view of the children (needed by visitors to traverse). */
    public List<FileSystemElement> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /** Double dispatch, step 1: the element picks visitDirectory(...) and passes itself. */
    @Override
    public void accept(FileSystemVisitor visitor) {
        visitor.visitDirectory(this);
    }

    @Override
    public String toString() {
        return "Directory{" + name + ", " + children.size() + " children}";
    }
}
