file:///C:/Users/npott/Documents/musicsort/src/main/scala/musicsort/MusicOrganizer.scala
empty definition using pc, found symbol in pc: 
semanticdb not found
empty definition using fallback
non-local guesses:
	 -java/nio/file/Path.of.
	 -java/nio/file/Path.of#
	 -java/nio/file/Path.of().
	 -Path.of.
	 -Path.of#
	 -Path.of().
	 -scala/Predef.Path.of.
	 -scala/Predef.Path.of#
	 -scala/Predef.Path.of().
offset: 217
uri: file:///C:/Users/npott/Documents/musicsort/src/main/scala/musicsort/MusicOrganizer.scala
text:
```scala
//> using scala "3.3.3"
//> using jvm "17"
package musicsort

import java.nio.file.{Path, Paths}

object MusicOrganizer {

  def main(args: Array[String]): Unit = {
    println(args(0))
    val root = Path.o@@f(args.headOption.getOrElse("."))
    println(root)

    val albums = Scanner.scanMusicRoot(root)
    
    Reporter.printProblemAlbums(albums)
    
    // test interaction to update genre of problem albums
    val updated = albums.map(Editor.promptGenreUpdate)

    println("\nDone.")
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 