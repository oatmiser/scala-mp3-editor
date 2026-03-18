package musicsort

import java.nio.file.{Files, Path, Paths}
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
    // Java stream -> Scala iterator -> if isDirectory then scanAlbum
    // Load existing genre counts, scan folders, then save updated genres
    GenreStore.load(Paths.get("data/genres.json"))
    val albums = Files.walk(root)
      .iterator().asScala
      .filter(Files.isDirectory(_))
      .flatMap(scanAlbumDir)
      .toList
    GenreStore.save(Paths.get("data/genres.json"))
    albums
  }

  private def scanAlbumDir(dir: Path): Option[Album] = {

    if (dir.getFileName.toString.endsWith("unfinished")) {
      println(s"Skip $dir")
      return None
    }

    // get list of files with path/name ending in mp3
    val mp3s = Files.list(dir)
      .iterator().asScala
      // lambda. path to bool
      .filter(p => p.toString.toLowerCase.endsWith(".mp3"))
      .toList

    if (mp3s.isEmpty) return None

    // readAudioFile on every mp3 under this directory
    val tracks = mp3s.map(readAudioFile)
    // make separate Album-level metadata from each track, but they should match
    val albumFields = mp3s.map(readAlbumFields)
    // Record any Genre names on the in-memory GenreStore
    albumFields.flatMap(_.genre).foreach(g => GenreStore.increment(g))
    // Metadata from Muzio Player set genres to (<int>)
    // Clear metadata upon track 1 with such a genre (consensus is broken?)
    val hasEnumGenre = albumFields.zip(tracks).exists { case (field, track) =>
      field.genre.exists(_.startsWith("(")) && track.metadata.trackNumber.contains(1)
    }
    val cleanedAlbumFields = if (hasEnumGenre) albumFields.map(_.copy(genre = None)) else albumFields
    
    // Record any found genre strings into the GenreStore (in-memory)
    cleanedAlbumFields.flatMap(_.genre).foreach(g => GenreStore.increment(g))

    // Helper functions will aggregate over the metadatas
    def consensus[A](values: List[Option[A]]): Option[A] = {
      val present = values.flatten
      if (present.isEmpty) None
      else if (present.distinct.size == 1) Some(present.head)
      else None
    }

    /*def majority(values: List[Option[Int]]): Option[Int] = {
      values.flatten
        .groupBy(identity)
        .view
        .mapValues(_.size)
        .toList
        .sortBy(-_._2)
        .headOption
        .map(_._1)
    }
    */
    val albumArtist = consensus(albumFields.map(_.albumArtist))
    // Skip if directory name is same as the artist in metadata, but
    // allow the case of the parent also having the same name (/Artist/Artist)
    val dirName = dir.getFileName.toString
    val parentName = Option(dir.getParent).map(_.getFileName.toString)

    if (albumArtist.exists { artist =>
      val matches = dirName.endsWith(artist)
      val parentMatches = parentName.contains(artist)
      // bool test for If statement
      matches && !parentMatches
    }) {
      println(s"Skip $dir")
      return None
    }

    val albumTitle = consensus(albumFields.map(_.albumTitle))
    val genre = consensus(albumFields.map(_.genre))
    //val year = majority(albumFields.map(_.year))
    val year = consensus(albumFields.map(_.year))
    val hasArt = albumFields.forall(_.hasAlbumArt)

    val metadata = AlbumMetadata(
      albumArtist = albumArtist,
      albumTitle  = albumTitle,
      genre       = genre,
      year        = year,
      hasAlbumArt = hasArt
    )

    // Album Problem consists of shared metadata and/or individual tracks
    val problems = List(
      if (albumTitle.isEmpty)  Some(MissingAlbumTitle)  else None,
      if (albumArtist.isEmpty) Some(MissingAlbumArtist) else None,
      if (genre.isEmpty)       Some(MissingGenre)       else None,
      if (year.isEmpty)        Some(MissingYear)        else None,
      if (!hasArt)             Some(MissingAlbumArt)    else None
    ).flatten ++
    tracks.flatMap { t => List(
        if (t.metadata.title.isEmpty) Some(MissingTrackTitle(t.path))
        else None,
        if (t.metadata.trackNumber.isEmpty) Some(MissingTrackNumber(t.path))
        else None
        // TODO created/modified time
      ).flatten
    }

    // write to NDJSON for albums that will be fix later
    if (problems.nonEmpty) {
      val record = ProblemAlbumRecord(
        dir = dir.toString,
        metadata = metadata,
        tracks = tracks.map(_.metadata),
        problems = problems,
        scannedAt = Instant.now()
      )
      ProblemAlbumWriter.append(record, Paths.get("problems.ndjson"))
    }


    // Return the Album object for this directory
    Option(Album(
      dir      = dir,
      metadata = metadata,
      tracks   = tracks,
      problems = problems
    ))

    /*
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
      */
  }

  private def readAudioFile(path: Path): AudioFile = {
    // basic attributes for timestamp
    val attrs: BasicFileAttributes = Files.readAttributes(path, classOf[BasicFileAttributes])

    // jaudiotagger mp3 tag
    val audioFile = AudioFileIO.read(path.toFile)
    val tag = Option(audioFile.getTag)

    // get key from a tag := String of the first instance of the given field
    def get(key: FieldKey): Option[String] =
      tag.flatMap(t => Option(t.getFirst(key)).filter(_.nonEmpty))
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

  /* other metadata which must be identical between all songs
   * for their Album's metadata to be valid/exist (checked by caller)
   */
  private def readAlbumFields(path: Path): AlbumMetadata = {
    val audioFile = AudioFileIO.read(path.toFile)
    val tag = Option(audioFile.getTag)

    def get(key: FieldKey): Option[String] =
      tag.flatMap(t => Option(t.getFirst(key)).filter(_.nonEmpty))

    // int(String[:4]) from get(year)
    val year = get(FieldKey.YEAR).flatMap(_.take(4).toIntOption)

    // APIC tag on the id3 metadata for mp3
    val hasArt = tag.exists(_.getArtworkList != null) &&
      tag.exists(_.getArtworkList.size() > 0)

    AlbumMetadata(
      albumTitle  = get(FieldKey.ALBUM),
      albumArtist = get(FieldKey.ALBUM_ARTIST).orElse(get(FieldKey.ARTIST)),
      genre       = get(FieldKey.GENRE),
      year        = year,
      hasAlbumArt = hasArt
    )
  }
}
