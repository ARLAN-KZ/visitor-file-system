package kz.astanait.visitor.element;

import kz.astanait.visitor.visitor.FileSystemVisitor;

import java.util.Objects;

public final class File implements FileSystemElement {
    private final String name;
    private final long sizeInBytes;

    public File(String name, long sizeInBytes) {
        this.name = requireValidName(name);
        if (sizeInBytes < 0) {
            throw new IllegalArgumentException("File size must not be negative: " + sizeInBytes);
        }
        this.sizeInBytes = sizeInBytes;
    }

    @Override
    public String getName() {
        return name;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public String getExtension() {
        int dotIndex = name.lastIndexOf('.');
        return (dotIndex < 0 || dotIndex == name.length() - 1) ? "" : name.substring(dotIndex + 1);
    }

    @Override
    public void accept(FileSystemVisitor visitor) {
        visitor.visitFile(this);
    }

    @Override
    public String toString() {
        return "File{" + name + ", " + sizeInBytes + " B}";
    }

    static String requireValidName(String name) {
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        return name;
    }
}
