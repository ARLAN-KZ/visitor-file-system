package kz.astanait.visitor.element;

import kz.astanait.visitor.visitor.FileSystemVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Directory implements FileSystemElement {
    private final String name;
    private final List<FileSystemElement> children = new ArrayList<>();

    public Directory(String name) {
        this.name = File.requireValidName(name);
    }

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

    public List<FileSystemElement> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public void accept(FileSystemVisitor visitor) {
        visitor.visitDirectory(this);
    }

    @Override
    public String toString() {
        return "Directory{" + name + ", " + children.size() + " children}";
    }
}
