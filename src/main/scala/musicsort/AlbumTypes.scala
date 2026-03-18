package musicsort

import java.nio.file._
import java.nio.file.{Path, Files}
import java.nio.file.attribute.FileTime
import java.time.Instant

// Targeted issues in MP3 metadata
sealed trait Problem {
  def level: ProblemLevel
}
sealed trait ProblemLevel
case object AlbumLevel extends ProblemLevel
case object TrackLevel extends ProblemLevel

// Album-level (all files in directory will share)
case object MissingAlbumTitle extends Problem {
  val level = AlbumLevel
}
case object MissingAlbumArtist extends Problem {
  val level = AlbumLevel
}
case object MissingGenre extends Problem {
  val level = AlbumLevel
}
case object MissingYear extends Problem {
  val level = AlbumLevel
}
case object MissingAlbumArt extends Problem {
  val level = AlbumLevel
}

// Track-level
case class MissingTrackTitle(track: Path) extends Problem {
  val level = TrackLevel
}
case class MissingTrackNumber(track: Path) extends Problem {
  val level = TrackLevel
}
case class BadTimestamp(track: Path) extends Problem {
  val level = TrackLevel
}



// Metadata (re: album) per file/track
case class TrackMetadata(
  title: Option[String],
  trackNumber: Option[Int]
)

// Audio file-specific data
case class AudioFile(
  path: Path,
  created: Instant,
  modified: Instant,
  metadata: TrackMetadata
)

// Album metadata
case class AlbumMetadata(
  albumTitle: Option[String],
  albumArtist: Option[String],
  genre: Option[String],
  year: Option[Int],
  hasAlbumArt: Boolean
)

// Album is a folder with tracks
case class Album(
  dir: Path,
  metadata: AlbumMetadata,
  tracks: List[AudioFile],
  problems: List[Problem]
)
