error id: C62CDAA942FC70F91C970340C2FA1055
file:///C:/Users/npott/Documents/musicsort/src/main/scala/musicsort/Editor.scala
### java.lang.AssertionError: assertion failed

occurred in the presentation compiler.



action parameters:
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
    println(s"\nAlbum: ${record.metadata.albumTitName} (${record.metadata.artist.getOrElse("Unknown")})")
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

}

```


presentation compiler configuration:
Scala version: 3.3.7-bin-nonbootstrapped
Classpath:
<WORKSPACE>\target\bloop-bsp-clients-classes\classes-Metals-vuUDzGuFQxStzaNXvLCeLg== [exists ], <HOME>\AppData\Local\bloop\cache\semanticdb\com.sourcegraph.semanticdb-javac.0.11.2\semanticdb-javac-0.11.2.jar [exists ], <WORKSPACE>\target\classes [exists ], <HOME>\.m2\repository\org\scala-lang\scala-library\2.13.18\scala-library-2.13.18.jar [exists ], <HOME>\.m2\repository\org\scala-lang\scala3-library_3\3.3.1\scala3-library_3-3.3.1.jar [exists ], <HOME>\.m2\repository\com\github\Adonai\jaudiotagger\2.3.14\jaudiotagger-2.3.14.jar [exists ], <HOME>\.m2\repository\com\squareup\okio\okio\1.17.3\okio-1.17.3.jar [exists ], <HOME>\.m2\repository\io\circe\circe-core_3\0.14.6\circe-core_3-0.14.6.jar [exists ], <HOME>\.m2\repository\io\circe\circe-numbers_3\0.14.6\circe-numbers_3-0.14.6.jar [exists ], <HOME>\.m2\repository\org\typelevel\cats-core_3\2.9.0\cats-core_3-2.9.0.jar [exists ], <HOME>\.m2\repository\org\typelevel\cats-kernel_3\2.9.0\cats-kernel_3-2.9.0.jar [exists ], <HOME>\.m2\repository\io\circe\circe-generic_3\0.14.6\circe-generic_3-0.14.6.jar [exists ], <HOME>\.m2\repository\io\circe\circe-parser_3\0.14.6\circe-parser_3-0.14.6.jar [exists ], <HOME>\.m2\repository\io\circe\circe-jawn_3\0.14.6\circe-jawn_3-0.14.6.jar [exists ], <HOME>\.m2\repository\org\typelevel\jawn-parser_3\1.4.0\jawn-parser_3-1.4.0.jar [exists ], <HOME>\.m2\repository\org\scala-lang\scala-library\2.13.16\scala-library-2.13.16.jar [exists ], <HOME>\.m2\repository\org\scala-lang\scala3-library_3\3.3.7\scala3-library_3-3.3.7.jar [exists ]
Options:
-release 8 -Xsemanticdb -sourceroot <WORKSPACE>




#### Error stacktrace:

```
scala.runtime.Scala3RunTime$.assertFailed(Scala3RunTime.scala:11)
	dotty.tools.dotc.core.TypeOps$.dominators$1(TypeOps.scala:253)
	dotty.tools.dotc.core.TypeOps$.approximateOr$1(TypeOps.scala:397)
	dotty.tools.dotc.core.TypeOps$.orDominator(TypeOps.scala:410)
	dotty.tools.dotc.core.Types$OrType.join(Types.scala:3538)
	dotty.tools.dotc.core.Types$OrType.widenUnionWithoutNull(Types.scala:3554)
	dotty.tools.dotc.core.Types$Type.widenUnion(Types.scala:1382)
	dotty.tools.dotc.core.ConstraintHandling.widenOr$1(ConstraintHandling.scala:662)
	dotty.tools.dotc.core.ConstraintHandling.widenInferred(ConstraintHandling.scala:687)
	dotty.tools.dotc.core.ConstraintHandling.widenInferred$(ConstraintHandling.scala:29)
	dotty.tools.dotc.core.TypeComparer.widenInferred(TypeComparer.scala:28)
	dotty.tools.dotc.core.ConstraintHandling.instanceType(ConstraintHandling.scala:726)
	dotty.tools.dotc.core.ConstraintHandling.instanceType$(ConstraintHandling.scala:29)
	dotty.tools.dotc.core.TypeComparer.instanceType(TypeComparer.scala:28)
	dotty.tools.dotc.core.TypeComparer$.instanceType(TypeComparer.scala:3087)
	dotty.tools.dotc.core.Types$TypeVar.instantiate(Types.scala:4974)
	dotty.tools.dotc.typer.Inferencing.tryInstantiate$1(Inferencing.scala:760)
	dotty.tools.dotc.typer.Inferencing.doInstantiate$1(Inferencing.scala:763)
	dotty.tools.dotc.typer.Inferencing.instantiateTypeVars(Inferencing.scala:766)
	dotty.tools.dotc.typer.Inferencing.instantiateTypeVars$(Inferencing.scala:569)
	dotty.tools.dotc.typer.Typer.instantiateTypeVars(Typer.scala:122)
	dotty.tools.dotc.typer.Inferencing.interpolateTypeVars(Inferencing.scala:637)
	dotty.tools.dotc.typer.Inferencing.interpolateTypeVars$(Inferencing.scala:569)
	dotty.tools.dotc.typer.Typer.interpolateTypeVars(Typer.scala:122)
	dotty.tools.dotc.typer.Typer.simplify(Typer.scala:3243)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3231)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3299)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3303)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3414)
	dotty.tools.dotc.typer.Typer.typeSelectOnTerm$1(Typer.scala:817)
	dotty.tools.dotc.typer.Typer.typedSelect(Typer.scala:855)
	dotty.tools.dotc.typer.Typer.typedNamed$1(Typer.scala:3117)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3228)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3196)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3229)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3196)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3229)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3299)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3303)
	dotty.tools.dotc.typer.Typer.typedIf(Typer.scala:1309)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3157)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3229)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3299)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3303)
	dotty.tools.dotc.typer.Typer.traverse$1(Typer.scala:3352)
	dotty.tools.dotc.typer.Typer.typedStats(Typer.scala:3371)
	dotty.tools.dotc.typer.Typer.typedBlockStats(Typer.scala:1235)
	dotty.tools.dotc.typer.Typer.typedBlock(Typer.scala:1239)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3156)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3229)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3299)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3303)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3414)
	dotty.tools.dotc.typer.Typer.$anonfun$58(Typer.scala:2581)
	dotty.tools.dotc.inlines.PrepareInlineable$.dropInlineIfError(PrepareInlineable.scala:242)
	dotty.tools.dotc.typer.Typer.typedDefDef(Typer.scala:2581)
	dotty.tools.dotc.typer.Typer.typedNamed$1(Typer.scala:3124)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3228)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3299)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3303)
	dotty.tools.dotc.typer.Typer.traverse$1(Typer.scala:3325)
	dotty.tools.dotc.typer.Typer.typedStats(Typer.scala:3371)
	dotty.tools.dotc.typer.Typer.typedClassDef(Typer.scala:2768)
	dotty.tools.dotc.typer.Typer.typedTypeOrClassDef$1(Typer.scala:3136)
	dotty.tools.dotc.typer.Typer.typedNamed$1(Typer.scala:3140)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3228)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3299)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3303)
	dotty.tools.dotc.typer.Typer.traverse$1(Typer.scala:3325)
	dotty.tools.dotc.typer.Typer.typedStats(Typer.scala:3371)
	dotty.tools.dotc.typer.Typer.typedPackageDef(Typer.scala:2911)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3181)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3229)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3299)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3303)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3414)
	dotty.tools.dotc.typer.TyperPhase.typeCheck$$anonfun$1(TyperPhase.scala:45)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	dotty.tools.dotc.core.Phases$Phase.monitor(Phases.scala:467)
	dotty.tools.dotc.typer.TyperPhase.typeCheck(TyperPhase.scala:51)
	dotty.tools.dotc.typer.TyperPhase.$anonfun$4(TyperPhase.scala:97)
	scala.collection.Iterator$$anon$6.hasNext(Iterator.scala:479)
	scala.collection.Iterator$$anon$9.hasNext(Iterator.scala:583)
	scala.collection.immutable.List.prependedAll(List.scala:152)
	scala.collection.immutable.List$.from(List.scala:685)
	scala.collection.immutable.List$.from(List.scala:682)
	scala.collection.IterableOps$WithFilter.map(Iterable.scala:900)
	dotty.tools.dotc.typer.TyperPhase.runOn(TyperPhase.scala:96)
	dotty.tools.dotc.Run.runPhases$1$$anonfun$1(Run.scala:351)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	scala.collection.ArrayOps$.foreach$extension(ArrayOps.scala:1324)
	dotty.tools.dotc.Run.runPhases$1(Run.scala:344)
	dotty.tools.dotc.Run.compileUnits$$anonfun$1(Run.scala:384)
	dotty.tools.dotc.Run.compileUnits$$anonfun$adapted$1(Run.scala:393)
	dotty.tools.dotc.util.Stats$.maybeMonitored(Stats.scala:69)
	dotty.tools.dotc.Run.compileUnits(Run.scala:393)
	dotty.tools.dotc.Run.compileSources(Run.scala:297)
	dotty.tools.dotc.interactive.InteractiveDriver.run(InteractiveDriver.scala:161)
	dotty.tools.pc.CachingDriver.run(CachingDriver.scala:45)
	dotty.tools.pc.WithCompilationUnit.<init>(WithCompilationUnit.scala:31)
	dotty.tools.pc.SimpleCollector.<init>(PcCollector.scala:357)
	dotty.tools.pc.PcSemanticTokensProvider$Collector$.<init>(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.Collector$lzyINIT1(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.Collector(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.provide(PcSemanticTokensProvider.scala:88)
	dotty.tools.pc.ScalaPresentationCompiler.semanticTokens$$anonfun$1(ScalaPresentationCompiler.scala:158)
	scala.meta.internal.pc.CompilerAccess.withSharedCompiler(CompilerAccess.scala:149)
	scala.meta.internal.pc.CompilerAccess.$anonfun$1(CompilerAccess.scala:93)
	scala.meta.internal.pc.CompilerAccess.onCompilerJobQueue$$anonfun$1(CompilerAccess.scala:210)
	scala.meta.internal.pc.CompilerJobQueue$Job.run(CompilerJobQueue.scala:153)
	java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
	java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
	java.base/java.lang.Thread.run(Thread.java:840)
```
#### Short summary: 

java.lang.AssertionError: assertion failed