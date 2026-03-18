package musicsort

import java.nio.file.{Files, Path, Paths, StandardOpenOption}
import scala.io.Source
import io.circe.parser.decode
import io.circe.syntax.*
import io.circe.generic.semiauto.*
import io.circe.*

object GenreStore {
  private var counts: Map[String, Int] = Map.empty
  private var canonical: Map[String, String] = Map.empty // lowercase -> original
  private def defaultPath: Path = Paths.get("data/genres.json")
  private def normalizeKey(g: String): String = g.trim.replaceAll("\\s+", " ").toLowerCase

  def load(file: Path = defaultPath): Unit = {
    counts = Map.empty
    canonical = Map.empty
    if (Files.exists(file)) {
      try {
        val source = Source.fromFile(file.toFile).mkString
        decode[Map[String, Int]](source).toOption.foreach { m =>
          counts = m
          canonical = m.keys.map(k => k.toLowerCase -> k).toMap
        }
      } catch { case _: Throwable => () }
    }
  }

  def save(file: Path = defaultPath): Unit = {
    try {
      val parent = file.getParent
      if (parent != null && !Files.exists(parent)) {
        Files.createDirectories(parent)
      }
      // write one key per line JSON object
      val entries = counts.toList
      def escape(s: String): String = s.replace("\\", "\\\\").replace("\"", "\\\"")
      val json = new StringBuilder
      json.append("{\n")
      for (((genre, count), i) <- entries.zipWithIndex) {
        json.append(s"  \"${escape(genre)}\": $count")
        if (i != entries.size - 1)
            json.append(",\n") else json.append("\n")
      }
      json.append("}\n")
      Files.writeString(file, json.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)
    } catch { case e: Throwable => println(s"Failed to save genres file: ${e.getMessage}") }
  }

  def increment(genre: String, delta: Int = 1): Unit = {
    if (genre == null)
      return
    val key = normalizeKey(genre)
    val display = canonical.getOrElse(key, genre.trim)
    canonical = canonical + (key -> display)
    counts = counts.updated(display, counts.getOrElse(display, 0) + delta)
  }

  def mergeAll(found: Seq[String]): Unit = found.foreach(g => if (g != null) increment(g))

  def listTop(n: Int): List[(String, Int)] = counts.toList.sortBy(-_._2).take(n)

  def allSorted: List[(String, Int)] = counts.toList.sortBy(-_._2)
}
/*
Load from genres.json
Increment frequency and add new genres

{
  "Gothic Rock": 12,
  "Post-Punk": 7,
  "Jazz": 42,
  "Darkwave": 3
}
*/

  /* When fixing a MissingGenre problem, display all by frequency descending
    Select genre:
        1) Gothic Rock (12)
        2) Post-Punk (7)
        3) Jazz (42)
        n) New genre
    Choice:
    New genre will prompt again for a string and check if it is new.
    Add to json with freq=1 else update freq+1
    Must modify Genre prompt in Editor.scala. What is the Separation of concerns?
  */
