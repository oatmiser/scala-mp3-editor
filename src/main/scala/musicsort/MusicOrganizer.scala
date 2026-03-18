//> using scala "3.3.3"
//> using jvm "17"
package musicsort

import java.nio.file.{Path, Paths}
import scala.io.StdIn

object MusicOrganizer {

  def main(args: Array[String]): Unit = {
    /*args.headOption match {
      case Some("fix") =>
        Editor.promptFixFromNDJSON(Paths.get("problems.ndjson"))
        return

      case Some("scan") =>
        val root = Paths.get(args.lift(1).getOrElse("C:/Users/npott/Music"))
        runScan(root)
        return

      case _ => // fallthrough to interactive prompt
    }
    */

    println("Choose action\n 1) Scan folder\n 2) Fix problems")//\n q) Quit")
    print("Selection: ")
    StdIn.readLine().trim match {
      case "1" =>
        print("Enter path to scan (leave blank for default): ")
        val p = StdIn.readLine().trim
        val root = if (p.isEmpty) Paths.get("C:/Users/npott/Music") else Paths.get(p)
        runScan(root)

      case "2" =>
        Editor.promptFixFromNDJSON(Paths.get("problems.ndjson"))

      case _ => println("Goodbye")
    }
  }

  private def runScan(root: Path): Unit = {
    println(s"Scanning $root")
    val albums = Scanner.scanMusicRoot(root)
    Reporter.printProblemAlbums(albums)
    // interactive per-album quick genre update
    //val updated = albums.map(Editor.promptGenreUpdate)
    println("\nDone.\n")
  }
}
