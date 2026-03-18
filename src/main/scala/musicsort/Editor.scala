package musicsort

import java.nio.file._
import java.time.Instant
import java.io.File

import scala.io.StdIn
import scala.jdk.CollectionConverters._
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import org.jaudiotagger.tag.id3.ID3v24Tag
import org.jaudiotagger.tag.images.ArtworkFactory

object Editor {

  /*def promptGenreUpdate(album: Album): Album = {
    if (!album.problems.contains(MissingGenre)) return album

    println(s"\nAlbum '${album.metadata.albumTitle.get}' has no genre.")
    println(s"${album.dir}")
    print("Enter genre (or leave blank to skip): ")
    val input = StdIn.readLine().trim

    if (input.isEmpty) album
    else {
      album.copy(
        metadata = album.metadata.copy(genre = Some(input)),
        problems = album.problems.filterNot(_ == MissingGenre)
      )
    }
  }

  def promptAllUpdates(record: ProblemAlbumRecord): Unit = {
    println(s"\nAlbum: ${record.metadata.albumTitle} (${record.metadata.albumArtist.getOrElse("Unknown")})")
    println(s"Directory: ${record.dir}")
    println(s"Problems:")
    record.problems.foreach(p => println(s" - $p"))

    println("\nFix now? [y/n]")
    if (scala.io.StdIn.readLine().trim.toLowerCase == "y") {
      // later:
      // ask for genre, year, etc
      // write metadata
      // append RESOLVED record
      println("L bozo")
    }
  }
  */

  def promptFixFromNDJSON(file: Path): Unit = {
    // Load existing genres so we can offer suggestions
    GenreStore.load(Paths.get("data/genres.json"))

    var records = ProblemAlbumReader.readAll(file).toList
    if (records.isEmpty) {
      println(s"No problem records found in $file")
      return
    }

    // Outer loop: allow selecting and updating multiple albums
    var continue = true
    while (continue) {
      println("\nAlbums to update:")
      records.zipWithIndex.foreach { (rec, i) =>
        println(f"${i+1}%3s) ${rec.metadata.albumTitle.getOrElse("(unknown)")} - ${rec.metadata.albumArtist.getOrElse("(unknown)")}")
        println(s"     ${rec.dir}")
      }

      print("Select an album number (q to Quit): ")
      val sel = StdIn.readLine().trim
      if (sel.toLowerCase == "q") return

      val idx = try sel.toInt - 1 catch case _: Throwable => -1
      if (idx < 0 || idx >= records.size) {
        println("Invalid selection")
        // (scala cannot continue a loop)
      } else {
        val rec = records(idx)
        println(s"\nSelected: ${rec.metadata.albumTitle.getOrElse("(unknown)")} @ ${rec.dir}")

        // Build dynamic actions
        // always allow Genre update and offer other actions based on actual Problems
        var actionIndex = 1
        val actions = scala.collection.mutable.ListBuffer.empty[(String, () => Unit, List[Problem])]

        // Helper: write operation helpers using jaudiotagger
        def writeToAll(dirPath: Path)(op: Tag => Unit): Int = {
          val mp3s = try Files.list(dirPath).iterator().asScala.filter(_.toString.toLowerCase.endsWith(".mp3")).toList catch { case _: Throwable => Nil }
          var written = 0
          for (path <- mp3s) {
            try {
              val file = AudioFileIO.read(path.toFile)
              var tag = file.getTag
              if (tag == null) {
                tag = new ID3v24Tag()
                file.setTag(tag)
              }
              op(tag)
              AudioFileIO.write(file)
              written += 1
            } catch { case e: Throwable => println(s"Failed writing tag for $path: ${e.getMessage}") }
          }
          written
        }

        def writeArtToAll(dirPath: Path, imageFile: File): Int = {
          val mp3s = try Files.list(dirPath).iterator().asScala.filter(_.toString.toLowerCase.endsWith(".mp3")).toList catch { case _: Throwable => Nil }
          var written = 0
          for (path <- mp3s) {
            try {
              val file = AudioFileIO.read(path.toFile)
              var tag = file.getTag
              if (tag == null) {
                tag = new ID3v24Tag()
                file.setTag(tag)
              }
              val artwork = ArtworkFactory.createArtworkFromFile(imageFile)
              // remove existing artwork and set new
              try tag.deleteArtworkField() catch { case _: Throwable => () }
              tag.setField(artwork)
              AudioFileIO.write(file)
              written += 1
            } catch { case e: Throwable => println(s"Failed writing artwork for $path: ${e.getMessage}") }
          }
          written
        }

        // Always show Update Genre (mark as not required if not missing)
        val genreLabel = if (rec.problems.contains(MissingGenre)) "Update Genre" else "Update Genre (not required)"
        actions += ((genreLabel, () => {
          val top20 = GenreStore.listTop(20)
          if (top20.nonEmpty) {
            println("\nTop genres:")
            for (((g, count), i) <- top20.zipWithIndex) {
              println(s" ${i + 1}) $g ($count)")
            }
            println(" n) New genre")
            print("Select by number or enter new genre: ")
          } else {
            print("Enter genre (blank to cancel): ")
          }

          val input = StdIn.readLine().trim
          val newGenre = if (input.isEmpty) {
            ""
          } else if (input.forall(_.isDigit)) {
            val idx = input.toInt - 1
            if (idx >= 0 && idx < top20.size) top20(idx)._1 else ""
          } else {
            input
          }

          if (newGenre.nonEmpty) {
            val dirPath = Paths.get(rec.dir)
            val written = writeToAll(dirPath) { tag =>
              try tag.setField(FieldKey.GENRE, newGenre) catch { case _: Throwable => () }
            }
            if (written > 0) {
              GenreStore.increment(newGenre)
              GenreStore.save(Paths.get("data/genres.json"))
              val sols = List(ProblemSolution(MissingGenre, newGenre))
              ProblemResolvedWriter.append(ProblemResolvedRecord(rec.dir, sols, Instant.now()), Paths.get("resolved.ndjson"))
              println(s"Logged genre update and wrote '$newGenre' to $written files.")
            } else println("No files updated (genre not written).")
          }
        }, List(MissingGenre)))

        // Album-level problems
        if (rec.problems.exists {
          case MissingAlbumTitle | MissingAlbumArtist | MissingYear => true
          case _ => false
        }) {
          if (rec.problems.contains(MissingAlbumTitle)) actions += (("Set Album Title", () => {
            print("Enter album title: ")
            val newTitle = StdIn.readLine().trim
            if (newTitle.nonEmpty) {
              val dirPath = Paths.get(rec.dir)
              val written = writeToAll(dirPath) { tag =>
                try tag.setField(FieldKey.ALBUM, newTitle) catch { case _: Throwable => () }
              }
              if (written > 0) {
                val sols = List(ProblemSolution(MissingAlbumTitle, newTitle))
                ProblemResolvedWriter.append(ProblemResolvedRecord(rec.dir, sols, Instant.now()), Paths.get("resolved.ndjson"))
                println(s"Logged title update and wrote '$newTitle' to $written files.")
              } else println("No files updated (album title not written).")
            }
          }, List(MissingAlbumTitle)))

          if (rec.problems.contains(MissingAlbumArtist)) actions += (("Set Album Artist", () => {
            print("Enter album artist: ")
            val newArtist = StdIn.readLine().trim
            if (newArtist.nonEmpty) {
              val dirPath = Paths.get(rec.dir)
              val written = writeToAll(dirPath) { tag =>
                try tag.setField(FieldKey.ALBUM_ARTIST, newArtist) catch { case _: Throwable => () }
              }
              if (written > 0) {
                val sols = List(ProblemSolution(MissingAlbumArtist, newArtist))
                ProblemResolvedWriter.append(ProblemResolvedRecord(rec.dir, sols, Instant.now()), Paths.get("resolved.ndjson"))
                println(s"Logged artist update and wrote '$newArtist' to $written files.")
              } else println("No files updated (album artist not written).")
            }
          }, List(MissingAlbumArtist)))

          if (rec.problems.contains(MissingYear)) actions += (("Set Year", () => {
            print("Enter year (YYYY): ")
            val year = StdIn.readLine().trim
            if (year.nonEmpty) {
              val dirPath = Paths.get(rec.dir)
              val written = writeToAll(dirPath) { tag =>
                try tag.setField(FieldKey.YEAR, year) catch { case _: Throwable => () }
              }
              if (written > 0) {
                val sols = List(ProblemSolution(MissingYear, year))
                ProblemResolvedWriter.append(ProblemResolvedRecord(rec.dir, sols, Instant.now()), Paths.get("resolved.ndjson"))
                println(s"Logged update and saved $year for $written files.")
              } else println("No files updated (year not written).")
            }
          }, List(MissingYear)))
        }

        // Track-level problems
        if (rec.problems.exists {
          case MissingTrackTitle(_) | MissingTrackNumber(_) | BadTimestamp(_) => true
          case _ => false
        }) {
          if (rec.problems.exists {
            case MissingTrackTitle(_) => true
            case _ => false
          }) actions += (("Copy filename to missing track titles", () => {
            val dirPath = Paths.get(rec.dir)
            val mp3s = try Files.list(dirPath).iterator().asScala.filter(_.toString.toLowerCase.endsWith(".mp3")).toList catch { case _: Throwable => Nil }
            val missing = mp3s.flatMap { p =>
              try {
                val af = AudioFileIO.read(p.toFile)
                val tag = Option(af.getTag)
                val title = tag.flatMap(t => Option(t.getFirst(FieldKey.TITLE)).filter(_.nonEmpty))
                if (title.isEmpty) Some(MissingTrackTitle(p)) else None
              } catch { case _: Throwable => None }
            }
            if (missing.isEmpty) println("No track titles are missing.")
            else {
              println(s"Found ${missing.size} files to update.")
              missing.foreach(m => println(s" - $m"))
              // perform per-file title write
              var written = 0
              val sols = missing.flatMap {
                case mt @ MissingTrackTitle(p) =>
                  val name = p.getFileName.toString
                  val title = name.lastIndexOf('.') match { case i if i > 0 => name.substring(0, i) case _ => name }
                  try {
                    val af = AudioFileIO.read(p.toFile)
                    var tag = af.getTag
                    if (tag == null) { tag = new ID3v24Tag(); af.setTag(tag) }
                    try tag.setField(FieldKey.TITLE, title) catch { case _: Throwable => () }
                    AudioFileIO.write(af)
                    written += 1
                    Some(ProblemSolution(mt, title))
                  } catch { case e: Throwable => println(s"Failed writing title for $p: ${e.getMessage}"); None }
                case _ => None
              }
              if (written > 0) {
                ProblemResolvedWriter.append(ProblemResolvedRecord(rec.dir, sols, Instant.now()), Paths.get("resolved.ndjson"))
                println(s"Wrote $written track titles and logged to resolved.ndjson")
              } else println("No titles were written.")
            }
          }, rec.problems.collect { case p @ MissingTrackTitle(_) => p }))
        }

        // Album art: look in hardcode folder for "artist_album.jpg|png"
        if (rec.problems.contains(MissingAlbumArt)) {
          val artistOpt = rec.metadata.albumArtist
          val titleOpt = rec.metadata.albumTitle
          if (artistOpt.isEmpty || titleOpt.isEmpty) {
            // still expose an action but it will be a no-op that explains why
            actions += (("Find cover image (requires Artist and Title)", () => {
              println("Cannot search Album Covers: artist or album title is not set on this record.")
              print("Press Enter to continue...")
              StdIn.readLine()
            }, List()))
          } else {
            actions += (("Find cover image", () => {
              val coverDir = Paths.get("C:/Users/npott/Music/Album Covers")
              if (!Files.exists(coverDir) || !Files.isDirectory(coverDir)) {
                println(s"Image folder not found: $coverDir")
                print("Press Enter to continue...")
                StdIn.readLine()
              } else {
                val base = s"${artistOpt.get}_${titleOpt.get}".toLowerCase.replaceAll("\\s+","")
                val found = try {
                  Files.list(coverDir).iterator().asScala.find { p =>
                    val n = p.getFileName.toString.toLowerCase
                    (n.startsWith(base) || n == base) && (n.endsWith(".jpg") || n.endsWith(".png"))
                  }
                } catch { case _: Throwable => None }

                found match {
                  case Some(p) =>
                    println(s"Found cover image: $p")
                    // write artwork into each mp3 file in the album
                    val written = writeArtToAll(Paths.get(rec.dir), p.toFile)
                    if (written > 0) {
                      val sols = List(ProblemSolution(MissingAlbumArt, p.toString))
                      ProblemResolvedWriter.append(ProblemResolvedRecord(rec.dir, sols, Instant.now()), Paths.get("resolved.ndjson"))
                      println(s"Logged artwork update and wrote APIC to $written files.")
                    } else println("Failed to write artwork.")
                  case None =>
                    println(s"No filename matches '$base' in the Album Covers folder.")
                }
                print("Press Enter to continue...")
                StdIn.readLine()
              }
            }, List(MissingAlbumArt)))
          }
        }

        // Show actions and loop until user goes back
        var inAlbum = true
        while (inAlbum) {
          println("\nActions:")
          for (((label, _, _), i) <- actions.zipWithIndex) println(s" ${i + 1}) $label")
          println(" q) return to albums")
          print("Choose action: ")
          StdIn.readLine().trim match {
            case "q" => inAlbum = false
            case s =>
              val ai = try s.toInt - 1 catch case _: Throwable => -1
              if (ai >= 0 && ai < actions.size) {
                val (_, fn, resolvedProblems) = actions(ai)
                fn()
                // Optionally remove resolved problems from our in-memory record list so they don't reappear
                if (resolvedProblems.nonEmpty) {
                  val updatedProblems = rec.problems.filterNot(p => resolvedProblems.contains(p))
                  val updatedRec = rec.copy(problems = updatedProblems)
                  records = records.updated(idx, updatedRec)
                  if (updatedProblems.isEmpty) {
                    println(s"All problems resolved for ${rec.dir} (in-memory)")
                    inAlbum = false
                  }
                }
              } else println("Invalid choice")
          }
        }
      }
    }
  }

}
