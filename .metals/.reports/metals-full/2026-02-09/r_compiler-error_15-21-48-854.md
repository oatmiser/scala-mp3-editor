error id: 83AFD821B1B0D317D2D0752DE5920841
file:///c:/Users/npott/Documents/musicsort/Main.scala
### java.lang.NullPointerException: Cannot read the array length because "a" is null

occurred in the presentation compiler.



action parameters:
offset: 9
uri: file:///c:/Users/npott/Documents/musicsort/Main.scala
text:
```scala
object Ma@@

```


presentation compiler configuration:
Scala version: 2.13.18
Classpath:
<WORKSPACE>\target\bloop-bsp-clients-classes\classes-Metals-rN2naJU9SFKA_KuIFGygjA== [missing ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\sourcegraph\semanticdb-javac\0.11.2\semanticdb-javac-0.11.2.jar [exists ], <WORKSPACE>\target\classes [exists ], <HOME>\.m2\repository\com\github\Adonai\jaudiotagger\2.3.14\jaudiotagger-2.3.14.jar [exists ], <HOME>\.m2\repository\com\squareup\okio\okio\1.17.3\okio-1.17.3.jar [exists ], <HOME>\.m2\repository\org\scala-lang\scala-library\2.13.18\scala-library-2.13.18.jar [exists ]
Options:
-release 8 -Yrangepos -Xplugin-require:semanticdb




#### Error stacktrace:

```
java.base/java.util.Arrays.sort(Arrays.java:1233)
	scala.tools.nsc.classpath.JFileDirectoryLookup.listChildren(DirectoryClassPath.scala:126)
	scala.tools.nsc.classpath.JFileDirectoryLookup.listChildren$(DirectoryClassPath.scala:110)
	scala.tools.nsc.classpath.DirectoryClassPath.listChildren(DirectoryClassPath.scala:331)
	scala.tools.nsc.classpath.DirectoryClassPath.listChildren(DirectoryClassPath.scala:331)
	scala.tools.nsc.classpath.DirectoryLookup.list(DirectoryClassPath.scala:91)
	scala.tools.nsc.classpath.DirectoryLookup.list$(DirectoryClassPath.scala:85)
	scala.tools.nsc.classpath.DirectoryClassPath.list(DirectoryClassPath.scala:331)
	scala.tools.nsc.classpath.AggregateClassPath.$anonfun$list$3(AggregateClassPath.scala:106)
	scala.collection.immutable.Vector.foreach(Vector.scala:2125)
	scala.tools.nsc.classpath.AggregateClassPath.list(AggregateClassPath.scala:102)
	scala.tools.nsc.util.ClassPath.list(ClassPath.scala:34)
	scala.tools.nsc.util.ClassPath.list$(ClassPath.scala:34)
	scala.tools.nsc.classpath.AggregateClassPath.list(AggregateClassPath.scala:31)
	scala.tools.nsc.symtab.SymbolLoaders$PackageLoader.doComplete(SymbolLoaders.scala:298)
	scala.tools.nsc.symtab.SymbolLoaders$SymbolLoader.$anonfun$complete$2(SymbolLoaders.scala:250)
	scala.tools.nsc.symtab.SymbolLoaders$SymbolLoader.complete(SymbolLoaders.scala:248)
	scala.reflect.internal.Symbols$Symbol.completeInfo(Symbols.scala:1584)
	scala.reflect.internal.Symbols$Symbol.info(Symbols.scala:1549)
	scala.reflect.internal.Types$TypeRef.decls(Types.scala:2602)
	scala.tools.nsc.typechecker.Namers$Namer.enterPackage(Namers.scala:727)
	scala.tools.nsc.typechecker.Namers$Namer.dispatch$1(Namers.scala:275)
	scala.tools.nsc.typechecker.Namers$Namer.standardEnterSym(Namers.scala:288)
	scala.tools.nsc.typechecker.AnalyzerPlugins.pluginsEnterSym(AnalyzerPlugins.scala:500)
	scala.tools.nsc.typechecker.AnalyzerPlugins.pluginsEnterSym$(AnalyzerPlugins.scala:499)
	scala.meta.internal.pc.MetalsGlobal$MetalsInteractiveAnalyzer.pluginsEnterSym(MetalsGlobal.scala:85)
	scala.tools.nsc.typechecker.Namers$Namer.enterSym(Namers.scala:266)
	scala.tools.nsc.typechecker.Analyzer$namerFactory$$anon$1.apply(Analyzer.scala:53)
	scala.tools.nsc.Global$GlobalPhase.applyPhase(Global.scala:485)
	scala.tools.nsc.Global$Run.$anonfun$compileLate$2(Global.scala:1701)
	scala.tools.nsc.Global$Run.$anonfun$compileLate$2$adapted(Global.scala:1700)
	scala.collection.IterableOnceOps.foreach(IterableOnce.scala:630)
	scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:628)
	scala.collection.AbstractIterator.foreach(Iterator.scala:1313)
	scala.tools.nsc.Global$Run.compileLate(Global.scala:1700)
	scala.tools.nsc.interactive.Global.parseAndEnter(Global.scala:667)
	scala.tools.nsc.interactive.Global.typeCheck(Global.scala:677)
	scala.meta.internal.pc.HoverProvider.typedHoverTreeAt(HoverProvider.scala:330)
	scala.meta.internal.pc.HoverProvider.hoverOffset(HoverProvider.scala:51)
	scala.meta.internal.pc.HoverProvider.hover(HoverProvider.scala:30)
	scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$hover$1(ScalaPresentationCompiler.scala:474)
	scala.meta.internal.pc.CompilerAccess.withSharedCompiler(CompilerAccess.scala:148)
	scala.meta.internal.pc.CompilerAccess.$anonfun$withNonInterruptableCompiler$1(CompilerAccess.scala:132)
	scala.meta.internal.pc.CompilerAccess.$anonfun$onCompilerJobQueue$1(CompilerAccess.scala:209)
	scala.meta.internal.pc.CompilerJobQueue$Job.run(CompilerJobQueue.scala:152)
	java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
	java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
	java.base/java.lang.Thread.run(Thread.java:840)
```
#### Short summary: 

java.lang.NullPointerException: Cannot read the array length because "a" is null