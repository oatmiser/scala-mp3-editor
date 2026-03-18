error id: file:///C:/Users/npott/Documents/musicsort/src/main/scala/musicsort/Editor.scala:
file:///C:/Users/npott/Documents/musicsort/src/main/scala/musicsort/Editor.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -java/nio/file/record/album/artist.
	 -record/album/artist.
	 -scala/Predef.record.album.artist.
offset: 680
uri: file:///C:/Users/npott/Documents/musicsort/src/main/scala/musicsort/Editor.scala
text:
```scala
package musicsort

import java.nio.file._
import scala.io.StdIn

object Editor {

  def promptGenreUpdate(album: Album): Album = {
    if (!album.problems.contains(MissingGenre)) return album

    println(s"\nAlbum '${album.metadata.albumTitle}' has no genre.")
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
    println(s"\nAlbum: ${record.album.art@@ist.getOrElse("Unknown")} - ${record.metadata.albumName}")
    println(s"Directory: ${record.dir}")
    println(s"Problems:")

    record.problems.foreach(p => println(s" - $p"))

    println("\nFix now? [y/N]")
    if (scala.io.StdIn.readLine().trim.toLowerCase == "y") {
      // later:
      // ask for genre, year, etc
      // write metadata
      // append RESOLVED record
    }
  }

}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 