# File System Visitor (Visitor Design Pattern in Java)

Assignment #6 - ShP-2216 Software Design Patterns, Astana IT University.
Author: Arlan Matzhan, group SE-2505. Java 17, no external dependencies.

A small file system (`File`, `Directory`) on which several unrelated operations
are performed by **visitors** - without any operation code inside the elements.

## Pattern roles

| Role | Class |
|------|-------|
| Element interface | `FileSystemElement` - `accept(FileSystemVisitor)` |
| Concrete elements | `File`, `Directory` |
| Visitor interface | `FileSystemVisitor` - `visitFile(File)`, `visitDirectory(Directory)` |
| Concrete visitors | `SizeCalculatorVisitor`, `SearchVisitor`, `PrintTreeVisitor` |
| Object structure | `Directory` tree built in `Main` |
| Client | `Main` |

### Double dispatch in two lines

```java
// File.java
public void accept(FileSystemVisitor visitor) { visitor.visitFile(this); }
// Directory.java
public void accept(FileSystemVisitor visitor) { visitor.visitDirectory(this); }
```

1. `element.accept(visitor)` - Java picks `File.accept` or `Directory.accept`
   by the **runtime type of the element**.
2. Inside, `visitor.visitFile(this)` - the compiler already knows `this` is a `File`,
   so the matching `visitX()` of the **runtime type of the visitor** runs.

Two virtual calls = the operation depends on both types. No `instanceof` anywhere.

## Project structure

```
src/main/java/kz/astanait/visitor/
  element/   FileSystemElement, File, Directory
  visitor/   FileSystemVisitor, SizeCalculatorVisitor, SearchVisitor, PrintTreeVisitor
  util/      SizeFormatter
  Main.java                      demo: all visitors on the same structure
src/test/java/kz/astanait/visitor/
  VisitorTests.java              15 dependency-free tests
docs/
  uml-class-diagram.png/.dot     UML class diagram
  sample-output.txt              real output of Main
```

## Build and run

```bash
# from the project root
mkdir out
javac --release 17 -d out $(find src -name "*.java")      # Linux / macOS / Git Bash
java -cp out kz.astanait.visitor.Main                      # demo
java -cp out kz.astanait.visitor.VisitorTests              # tests (exit code 0 = OK)
```

In IntelliJ IDEA: open the folder, mark `src/main/java` as *Sources Root* and
`src/test/java` as *Test Sources Root*, run `Main` or `VisitorTests`.

## Sample output

```
=== 1. SizeCalculatorVisitor ===
Total size of 'project': 2915197 B (2.8 MB)

=== 2. SearchVisitor ===
Search by extension .java:
  project/src/Main.java
  project/src/Utils.java
  project/src/test/AppTest.java
```

Full output: [docs/sample-output.txt](docs/sample-output.txt).

## Adding a new operation

Create a class implementing `FileSystemVisitor` (see `PrintTreeVisitor`, which was
added in its own commit and changed neither `File` nor `Directory`).

## Encapsulation trade-offs

* Visitors need the data of the elements, so `File` exposes `getSizeInBytes()` /
  `getExtension()` and `Directory` exposes `getChildren()`.
* Mitigation: elements are immutable, and `getChildren()` returns an
  **unmodifiable** view, so a visitor can read but never corrupt the structure.
* Known limitation of the pattern: adding a new *element* type means adding a method
  to `FileSystemVisitor` and to every visitor. It fits stable structures like this one.
