error id: file:///C:/Users/npott/Documents/musicsort/src/main/scala/musicsort/Scanner.scala:
file:///C:/Users/npott/Documents/musicsort/src/main/scala/musicsort/Scanner.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -scala/jdk/CollectionConverters.TrackAlbumFields#
	 -TrackAlbumFields#
	 -scala/Predef.TrackAlbumFields#
offset: 2842
uri: file:///C:/Users/npott/Documents/musicsort/src/main/scala/musicsort/Scanner.scala
text:
```scala
package musicsort

import java.nio.file.{Files, Path}
import scala.jdk.CollectionConverters._
import java.nio.file.attribute.BasicFileAttributes
import java.time.Instant

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey


object Scanner {
  /*
  Tracks store facts; Albums store consensus
  A Problem stores any disagreement or absence of metadata
  */

  def scanMusicRoot(root: Path): List[Album] = {
    /* Java stream ->
      Scala iterator ->
      if isDirectory then scanAlbumDir else skip
    */
    Files.walk(root)
      .iterator().asScala
      .filter(Files.isDirectory(_))
      .flatMap(scanAlbumDir)
      .toList
  }

  private def scanAlbumDir(dir: Path): Option[Album] = {
    // get list of files with path/name ending in mp3
    val mp3s = Files.list(dir)
      .iterator().asScala
      // lambda. path to bool
      .filter(p => p.toString.toLowerCase.endsWith(".mp3"))
      .toList

    if (mp3s.isEmpty) return None

    // readAudioFile on every mp3 under this directory
    val tracks = mp3s.map(readAudioFile)

    // TODO jaudiotagger metadata
    // Placeholder detection: jgp exists under this directory
    val hasArt = Files.exists(dir.resolve("cover.jpg")) ||
                 Files.exists(dir.resolve("folder.jpg"))
    
    // missing for now
    val genre: Option[String] = None

    val problems = List(
      if (genre.isEmpty) Some(MissingGenre) else None,
      if (!hasArt)       Some(MissingAlbumArt) else None
    ).flatten

    // TODO from metadata
    Some(Album(
        dir = dir,
        metadata = AlbumMetadata(
          albumTitle = Some(dir.getFileName.toString),
          albumArtist = None,
          genre = genre,
          year = None,
          hasAlbumArt = hasArt
        ),
        tracks = tracks,
        problems = problems
      ))
  }

  private def readAudioFile(path: Path): AudioFile = {
    // basic attributes for timestamp
    val attrs: BasicFileAttributes = Files.readAttributes(path, classOf[BasicFileAttributes])

    // jaudiotagger mp3 tag
    val audioFile = AudioFileIO.read(path.toFile)
    val tag = Option(audioFile.getTag)

    // get key from a tag := String of the first instance of the given field
    def get(key: FieldKey): Option[String] = {
      tag.flatMap(t => Option(t.getFirst(key)).filter(_.nonEmpty))
    }
    val trackTitle  = get(FieldKey.TITLE)
    val trackNumber = get(FieldKey.TRACK).flatMap(_.toIntOption)

    AudioFile(
      path = path,
      created = attrs.creationTime().toInstant,
      modified = attrs.lastModifiedTime().toInstant,
      metadata = TrackMetadata(
        title = trackTitle,
        trackNumber = trackNumber
      )
    )
  }

  private def readAlbumFields(path: Path): TrackA@@lbumFields = {
    val audioFile = AudioFileIO.read(path.toFile)
    val tag       = Option(audioFile.getTag)

    def get(key: FieldKey): Option[String] =
      tag.flatMap(t => Option(t.getFirst(key)).filter(_.nonEmpty))

    val year =
      get(FieldKey.YEAR)
        .orElse(get(FieldKey.DATE))
        .flatMap(_.take(4).toIntOption)

    val hasArt =
      tag.exists(_.getArtworkList != null) &&
      tag.exists(_.getArtworkList.size() > 0)

    TrackAlbumFields(
      albumTitle  = get(FieldKey.ALBUM),
      albumArtist = get(FieldKey.ALBUM_ARTIST).orElse(get(FieldKey.ARTIST)),
      genre       = get(FieldKey.GENRE),
      year        = year,
      hasArt      = hasArt
    )
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 