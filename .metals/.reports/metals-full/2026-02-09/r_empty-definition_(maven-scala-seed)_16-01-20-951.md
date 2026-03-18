error id: file:///C:/Users/npott/Documents/musicsort/src/main/scala/musicsort/ProblemStore.scala:
file:///C:/Users/npott/Documents/musicsort/src/main/scala/musicsort/ProblemStore.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -deriveEncoder.
	 -deriveEncoder#
	 -deriveEncoder().
	 -scala/Predef.deriveEncoder.
	 -scala/Predef.deriveEncoder#
	 -scala/Predef.deriveEncoder().
offset: 816
uri: file:///C:/Users/npott/Documents/musicsort/src/main/scala/musicsort/ProblemStore.scala
text:
```scala
package musicsort

/* 
Serialize Album → NDJSON
Deserialize NDJSON → Album
Append records
Read all records
*/

import java.nio.file.{Files, Path, StandardOpenOption}
import java.time.Instant

import io.circe.*
import io.circe.generic.semiauto.*

// different from in-memory Album class
case class ProblemAlbum(
  dir: String,
  metadata: AlbumMetadata,
  tracks: List[TrackMetadata],
  problems: List[Problem],
  scannedAt: Instant
)

given Encoder[Problem] = Encoder.encodeString.contramap {
  case MissingGenre    => "MissingGenre"
  case MissingArt      => "MissingArt"
  case BadTimestamp    => "BadTimestamp"
  case MissingTitle    => "MissingTitle"
  case MissingTrackNo  => "MissingTrackNum"
}

given Encoder[TrackMetadata] = deriveEncoder
given Encoder[AlbumMetadata] = deriv@@eEncoder
given Encoder[ProblemAlbumRecord] = deriveEncoder

object ProblemAlbumWriter {
    def append(
    record: ProblemAlbumRecord,
    file: Path
  ): Unit = {
    val jsonLine = record.asJson.noSpaces + "\n"

    Files.writeString(
      file,
      jsonLine,
      StandardOpenOption.CREATE,
      StandardOpenOption.WRITE,
      StandardOpenOption.APPEND
    )
  }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 