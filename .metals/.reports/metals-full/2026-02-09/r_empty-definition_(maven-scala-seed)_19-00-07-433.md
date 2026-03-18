file:///C:/Users/npott/Documents/musicsort/src/main/scala/musicsort/ProblemStore.scala
empty definition using pc, found symbol in pc: 
semanticdb not found
empty definition using fallback
non-local guesses:
	 -io/circe/syntax/Configuration.
	 -io/circe/syntax/Configuration#
	 -io/circe/syntax/Configuration().
	 -io/circe/generic/extras/Configuration.
	 -io/circe/generic/extras/Configuration#
	 -io/circe/generic/extras/Configuration().
	 -io/circe/generic/semiauto/Configuration.
	 -io/circe/generic/semiauto/Configuration#
	 -io/circe/generic/semiauto/Configuration().
	 -Configuration.
	 -Configuration#
	 -Configuration().
	 -scala/Predef.Configuration.
	 -scala/Predef.Configuration#
	 -scala/Predef.Configuration().
offset: 357
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
import scala.io.Source

import io.circe.{Encoder, Decoder}
import io.circe.syntax.*
import io.circe.parser.decode
import io.circe.generic.extras.Confi@@guration
import io.circe.generic.semiauto.*

// different from in-memory Album class
case class ProblemAlbumRecord(
  dir: String,
  metadata: AlbumMetadata,
  tracks: List[TrackMetadata],
  problems: List[Problem],
  scannedAt: Instant
)

given Encoder[Problem] = Encoder.encodeString.contramap {
  case MissingAlbumTitle      => "MissingAlbumTitle"
  case MissingAlbumArtist     => "MissingAlbumArtist"
  case MissingGenre           => "MissingGenre"
  case MissingYear            => "MissingYear"
  case MissingAlbumArt        => "MissingAlbumArt"
  case MissingTrackTitle(p)   => s"MissingTrackTitle(${p.toString})"
  case MissingTrackNumber(p)  => s"MissingTrackNumber(${p.toString})"
  case BadTimestamp(p)        => s"BadTimestamp(${p.toString})"
  /* 
  case MissingGenre    => "MissingGenre"
  case MissingAlbumArt      => "MissingArt"
  case BadTimestamp    => "BadTimestamp"
  case MissingTitle    => "MissingTitle"
  case MissingTrackNumber  => "MissingTrackNum"
   */
}

given Encoder[TrackMetadata] = deriveEncoder
given Encoder[AlbumMetadata] = deriveEncoder
given Encoder[ProblemAlbumRecord] = deriveEncoder



object ProblemAlbumWriter {
    def append(record: ProblemAlbumRecord, file: Path): Unit = {
        val jsonLine = record.asJson.noSpaces + "\n"
        Files.writeString(file, jsonLine,
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE,
            StandardOpenOption.APPEND)
    }
}

object ProblemAlbumReader {
  def readAll(file: Path): Iterator[ProblemAlbumRecord] =
    Source.fromFile(file.toFile).getLines()
      .flatMap { line => decode[ProblemAlbumRecord](line).toOption }
}

/* 
case class ProblemResolvedRecord(
  dir: String,
  resolvedProblems: List[Problem],
  resolvedAt: Instant
)
 */
// TODO event sourcing model of JSON problems
```


#### Short summary: 

empty definition using pc, found symbol in pc: 