package musicsort

import java.nio.file._

object Reporter {

  def printProblemAlbums(albums: List[Album]): Unit = {
    val problemAlbums = albums.filter(_.problems.nonEmpty)

    if (problemAlbums.isEmpty) {
      println("No problems found.")
      return
    }

    println("Updates in " + System.getProperty("user.dir"))

    for (album <- problemAlbums) {
      println("=" * 80)
      println(s"Album:  ${album.metadata.albumTitle.getOrElse("None")}")
      println(s"Artist: ${album.metadata.albumArtist.getOrElse("None")}")
      println(s"Path:   ${album.dir}")
      println(s"Problems:")
      album.problems.foreach(p => println(s"  - $p"))
      //println("Tracks:")
      //album.tracks.foreach(t => println(s"  - ${t.path.getFileName}"))
      println("=" * 80)
      println()
    }
  }
}
