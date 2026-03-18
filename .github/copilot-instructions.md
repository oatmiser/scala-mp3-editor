# Copilot Instructions for MusicSort

## Project Overview
MusicSort is a Scala 3 application that scans a music library, identifies metadata issues (missing genre, title, album art, etc.), and provides an interactive workflow to fix them. It uses the jaudiotagger library to read MP3 metadata and circe for JSON serialization.

## Architecture

### Core Data Model
- **Album**: Represents a folder with tracks; contains consensus metadata (if all tracks agree) plus a list of detected problems
- **AudioFile**: Individual track with path, timestamps, and metadata (title, track number)
- **Problem**: Sealed trait representing issues at album or track level (MissingGenre, MissingAlbumArt, BadTimestamp, etc.)
- **ProblemAlbumRecord**: NDJSON format for persistence (includes scanned timestamp for event sourcing)

### Key Modules
1. **Scanner** ([src/main/scala/musicsort/Scanner.scala](src/main/scala/musicsort/Scanner.scala)): Walks file tree, reads MP3 metadata via jaudiotagger, computes consensus for album fields (returns None if tracks disagree or all missing)
2. **ProblemStore** ([src/main/scala/musicsort/ProblemStore.scala](src/main/scala/musicsort/ProblemStore.scala)): NDJSON reader/writer using circe; append-only design for event sourcing (see TODO comment for future direction)
3. **Reporter** ([src/main/scala/musicsort/Reporter.scala](src/main/scala/musicsort/Reporter.scala)): Pretty-prints problem albums to stdout
4. **Editor** ([src/main/scala/musicsort/Editor.scala](src/main/scala/musicsort/Editor.scala)): Interactive CLI prompts for fixing metadata; currently only handles genre updates
5. **MusicOrganizer** ([src/main/scala/musicsort/MusicOrganizer.scala](src/main/scala/musicsort/MusicOrganizer.scala)): Main entry point; orchestrates scan → report → edit workflow

### Data Flow
```
Music directory → Scanner.scanMusicRoot()
                     ↓
              List[Album] with problems detected
                     ↓
         Reporter.printProblemAlbums() + ProblemStore.append()
                     ↓
         Editor.promptGenreUpdate() (currently genre only)
                     ↓
                  Updated Album
```

## Development Workflow

### Build & Run
```bash
# Compile
mvn clean compile

# Run (scans C:/Users/npott/Music by default, or use args)
mvn scala:run -Dexec.args="C:/path/to/music"

# Tests
mvn test
```

### Key Dependencies
- **Scala 3.3.7** (Java 8+ target)
- **jaudiotagger 2.3.14**: MP3 metadata reading
- **circe**: JSON serialization for NDJSON persistence
- **munit 1.2.2**: Test framework

## Project Conventions & Patterns

### Consensus Logic
Scanner uses a `consensus[A]` function: if all tracks have the same value, return Some; if any disagree or all missing, return None. This is intentionally strict—no majority voting. See [src/main/scala/musicsort/Scanner.scala#L49-L55](src/main/scala/musicsort/Scanner.scala#L49-L55).

### Problem Encoding
Problems are encoded to simple strings in JSON (e.g., "MissingGenre" → `MissingGenre` case object). The sealed trait hierarchy enforces type safety; custom encoders in [ProblemStore.scala](src/main/scala/musicsort/ProblemStore.scala#L27-L34) handle the mapping.

### Problem Filtering
When updating metadata, use `.filterNot(_ == MissingGenre)` to remove resolved problems from the Album. See [Editor.scala#L15](src/main/scala/musicsort/Editor.scala#L15).

### Immutable Records
Albums and metadata use case classes with `.copy()` for immutable updates (Scala 3 style). New Album instances preserve original data while updating specific fields.

## Critical TODOs & Extension Points

1. **Event Sourcing** ([ProblemStore.scala](src/main/scala/musicsort/ProblemStore.scala#L54-L57)): Add `ProblemResolvedRecord` to track fixes over time, not just problems detected.
2. **Multi-Problem Fixing** ([Editor.scala#L26-L35](src/main/scala/musicsort/Editor.scala#L26-L35)): `promptAllUpdates()` is stubbed; implement to fix year, artist, title, album art in one session.
3. **Timestamp Validation**: BadTimestamp problems are defined but never detected (see commented TODO in Scanner).
4. **File Writing**: Once metadata is fixed via Editor, need to write changes back to MP3 files (likely via jaudiotagger's tag writing API).

## Common Pitfalls & Tips

- **Path handling**: Use `java.nio.file.Path` throughout; `toString()` for directory comparison in NDJSON records
- **Option chaining**: Album metadata fields are `Option[String]`; use `.flatten` on Lists of Options before checking consensus
- **NDJSON format**: Each line is a separate JSON object; `decode[ProblemAlbumRecord](line)` will fail silently if corrupted (wrapped in `.toOption`)
- **jaudiotagger API**: Tag reading returns `Optional` (Java); call `.isPresent()` before `.get()`

## Testing
Current test ([src/test/scala/example/HelloSuiteTest.scala](src/test/scala/example/HelloSuiteTest.scala)) is minimal. Add munit tests in `src/test/scala/musicsort/` following the pattern: test Scanner consensus logic, ProblemStore serialization, and Editor prompts (mock StdIn).
