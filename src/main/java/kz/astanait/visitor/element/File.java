package kz.astanait.visitor.element;

import kz.astanait.visitor.visitor.FileSystemVisitor;

import java.util.Objects;

/**
 * Concrete element: a leaf of the file system tree.
 *
 * <p>The class is immutable and contains NO operation logic (no size
 * calculation, no searching, no printing). All of that lives in visitors.
 */
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

    /**
     * @return the extension without the dot (for "report.pdf" -> "pdf"),
     *         or an empty string when the name has no extension.
     */
    public String getExtension() {
        int dotIndex = name.lastIndexOf('.');
        return (dotIndex < 0 || dotIndex == name.length() - 1) ? "" : name.substring(dotIndex + 1);
    }

    /** Double dispatch, step 1: the element picks visitFile(...) and passes itself. */
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
