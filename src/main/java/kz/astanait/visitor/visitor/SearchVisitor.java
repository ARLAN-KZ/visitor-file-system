package kz.astanait.visitor.visitor;

import kz.astanait.visitor.element.Directory;
import kz.astanait.visitor.element.File;
import kz.astanait.visitor.element.FileSystemElement;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Concrete visitor #2. Single responsibility: collect the paths of all files
 * that satisfy a search criterion.
 *
 * <p>The criterion is injected as a {@link Predicate}, so the visitor itself
 * never changes when a new kind of search is needed. The visitor also keeps
 * track of the directory path it is currently in - state that belongs to the
 * OPERATION, not to the elements.
 */
public final class SearchVisitor implements FileSystemVisitor {

    private static final String SEPARATOR = "/";

    private final String description;
    private final Predicate<File> criterion;
    private final Deque<String> currentPath = new ArrayDeque<>();
    private final List<String> matches = new ArrayList<>();

    public SearchVisitor(String description, Predicate<File> criterion) {
        this.description = Objects.requireNonNull(description, "description must not be null");
        this.criterion = Objects.requireNonNull(criterion, "criterion must not be null");
    }

    /** Criterion: files with the given extension (case-insensitive, without dot). */
    public static SearchVisitor byExtension(String extension) {
        String wanted = extension.toLowerCase(Locale.ROOT);
        return new SearchVisitor("extension ." + wanted,
                file -> file.getExtension().toLowerCase(Locale.ROOT).equals(wanted));
    }

    /** Criterion: files strictly larger than the given number of bytes. */
    public static SearchVisitor largerThan(long sizeInBytes) {
        return new SearchVisitor("size > " + sizeInBytes + " B",
                file -> file.getSizeInBytes() > sizeInBytes);
    }

    /** Criterion: files whose name contains the text (case-insensitive). */
    public static SearchVisitor nameContains(String text) {
        String wanted = text.toLowerCase(Locale.ROOT);
        return new SearchVisitor("name contains \"" + text + "\"",
                file -> file.getName().toLowerCase(Locale.ROOT).contains(wanted));
    }

    @Override
    public void visitFile(File file) {
        if (criterion.test(file)) {
            matches.add(pathTo(file));
        }
    }

    @Override
    public void visitDirectory(Directory directory) {
        currentPath.addLast(directory.getName());
        for (FileSystemElement child : directory.getChildren()) {
            child.accept(this);
        }
        currentPath.removeLast();
    }

    public String getDescription() {
        return description;
    }

    /** @return full paths (e.g. "project/src/Main.java") of all matching files, in visit order. */
    public List<String> getMatches() {
        return Collections.unmodifiableList(matches);
    }

    private String pathTo(File file) {
        if (currentPath.isEmpty()) {
            return file.getName();
        }
        return String.join(SEPARATOR, currentPath) + SEPARATOR + file.getName();
    }
}
