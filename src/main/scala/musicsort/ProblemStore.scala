package musicsort

/* 
Serialize Album to NDJSON
Deserialize NDJSON = Album with some missing field(s)
Append records
Read all records
*/

import java.nio.file.{Files, Path, Paths, StandardOpenOption}
import java.time.Instant
import scala.io.Source

import io.circe.{Encoder, Decoder}
import io.circe.syntax.*
import io.circe.parser.decode
import io.circe.generic.semiauto.*

// use different from in-memory Album class
case class ProblemAlbumRecord(
  dir: String,
  metadata: AlbumMetadata,
  tracks: List[TrackMetadata],
  problems: List[Problem],
  scannedAt: Instant
)

/* 
given Encoder[Path] =
  Encoder.encodeString.contramap(_.toString)
*/
given Encoder[Problem] = Encoder.encodeString.contramap {
  case MissingAlbumTitle      => "MissingAlbumTitle"
  case MissingAlbumArtist     => "MissingAlbumArtist"
  case MissingGenre           => "MissingGenre"
  case MissingYear            => "MissingYear"
  case MissingAlbumArt        => "MissingAlbumArt"
  case MissingTrackTitle(p)   => s"MissingTrackTitle(${p.toString})"
  case MissingTrackNumber(p)  => s"MissingTrackNumber(${p.toString})"
  case BadTimestamp(p)        => s"BadTimestamp(${p.toString})"
}

given Encoder[TrackMetadata] = deriveEncoder
given Encoder[AlbumMetadata] = deriveEncoder
given Encoder[ProblemAlbumRecord] = deriveEncoder


/* 
given Decoder[Path] =
  Decoder.decodeString.map(Paths.get)
*/
given Decoder[Problem] = Decoder.decodeString.emapTry { s =>
  scala.util.Try {
    s match {
      case "MissingAlbumTitle"   => MissingAlbumTitle
      case "MissingAlbumArtist"  => MissingAlbumArtist
      case "MissingGenre"        => MissingGenre
      case "MissingYear"         => MissingYear
      case "MissingAlbumArt"     => MissingAlbumArt
      case s if s.startsWith("MissingTrackTitle(") => 
        val path = s.stripPrefix("MissingTrackTitle(").stripSuffix(")")
        MissingTrackTitle(Paths.get(path))
      case s if s.startsWith("MissingTrackNumber(") =>
        val path = s.stripPrefix("MissingTrackNumber(").stripSuffix(")")
        MissingTrackNumber(Paths.get(path))
      case s if s.startsWith("BadTimestamp(") =>
        val path = s.stripPrefix("BadTimestamp(").stripSuffix(")")
        BadTimestamp(Paths.get(path))
      case _ => throw new Exception(s"Unknown problem type: $s")
    }
  }
}

given Decoder[TrackMetadata] = deriveDecoder
given Decoder[AlbumMetadata] = deriveDecoder
given Decoder[ProblemAlbumRecord] = deriveDecoder



object ProblemAlbumWriter {
    def append(record: ProblemAlbumRecord, file: Path): Unit = {
        val jsonLine = record.asJson.noSpaces + "\n"
        Files.writeString(file, jsonLine,
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE,
            StandardOpenOption.APPEND)
    }
}

// event-sourcing model of the JSON problems
// read from problems.ndjson and write a valid resolution to resolved.ndjson

// Structure for a resolved Album and how it was fixed (string replaced)
case class ProblemSolution(problem: Problem, solution: String)

case class ProblemResolvedRecord(
  dir: String,
  resolved: List[ProblemSolution],
  resolvedAt: Instant
)

given Encoder[ProblemSolution] = deriveEncoder
given Encoder[ProblemResolvedRecord] = deriveEncoder

object ProblemResolvedWriter {
  def append(record: ProblemResolvedRecord, file: Path): Unit = {
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
