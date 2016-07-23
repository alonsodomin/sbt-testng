package org.testng.annotations

import java.lang.annotation._

/* Note that `expectedExceptions` currently doesn't work, because the compiler doesn't
 * seem to like the shape when transforming it and crashes in the backend:
 *
 * scala.MatchError: scala.Array.apply[Class[_]](classOf[java.lang.NullPointerException])((ClassTag.apply[Class[_]](classOf[java.lang.Class]): scala.reflect.ClassTag[Class[_]])) (of class scala.reflect.internal.Trees$ApplyToImplicitArgs)
 *	 at org.scalajs.core.compiler.GenJSCode$JSCodePhase.genNormalApply(GenJSCode.scala:2273)
 *   at org.scalajs.core.compiler.GenJSCode$JSCodePhase.genApply(GenJSCode.scala:2032)
 *   at org.scalajs.core.compiler.GenJSCode$JSCodePhase.genStatOrExpr(GenJSCode.scala:1620)
 *   at org.scalajs.core.compiler.GenJSCode$JSCodePhase.genExpr(GenJSCode.scala:1548)
 *
 * Skipping the jscode phase (``-Yskip:jscode`) we see that this is in fact an issue of scalac and not of Scala.js:
 *
 * scala.reflect.internal.FatalError: Unknown qualifier scala.Array.apply[Class[_]](classOf[java.lang.NullPointerException])
 *   at scala.reflect.internal.Reporting$class.abort(Reporting.scala:59)
 *   at scala.reflect.internal.SymbolTable.abort(SymbolTable.scala:16)
 *   at scala.tools.nsc.backend.icode.GenICode$ICodePhase.genLoadQualifier(GenICode.scala:1101)
 *   at scala.tools.nsc.backend.icode.GenICode$ICodePhase.genLoadApply6$1(GenICode.scala:777)
 * 	 at scala.tools.nsc.backend.icode.GenICode$ICodePhase.scala$tools$nsc$backend$icode$GenICode$ICodePhase$$genLoad(GenICode.scala:809)
 * 	 at scala.tools.nsc.backend.icode.GenICode$ICodePhase$$anonfun$genLoadArguments$1.apply(GenICode.scala:1140)
 * 	 at scala.tools.nsc.backend.icode.GenICode$ICodePhase$$anonfun$genLoadArguments$1.apply(GenICode.scala:1138)
 * 	 at scala.collection.LinearSeqOptimized$class.foldLeft(LinearSeqOptimized.scala:124)
 * 	 at scala.collection.immutable.List.foldLeft(List.scala:84)
 * 	 at scala.tools.nsc.backend.icode.GenICode$ICodePhase.genLoadArguments(GenICode.scala:1138)
 *
 * Replacing it with something which is implicitly convertible from Array, like WrappedArray, also doesn't work:
 *
 *  scala.reflect.internal.Types$TypeError: type mismatch;
 *     found   : scala.collection.mutable.WrappedArray[Class[NullPointerException]]
 *     required: scala.collection.mutable.WrappedArray
 *	 at scala.tools.nsc.typechecker.Contexts$ThrowingReporter.handleError(Contexts.scala:1402)
 *	 at scala.tools.nsc.typechecker.Contexts$ContextReporter.issue(Contexts.scala:1254)
 *	 at scala.tools.nsc.typechecker.Contexts$Context.issue(Contexts.scala:573)
 *	 at scala.tools.nsc.typechecker.ContextErrors$ErrorUtils$.issueTypeError(ContextErrors.scala:106)
 *	 at scala.tools.nsc.typechecker.ContextErrors$ErrorUtils$.issueNormalTypeError(ContextErrors.scala:99)
 *	 at scala.tools.nsc.typechecker.ContextErrors$TyperContextErrors$TyperErrorGen$.AdaptTypeError(ContextErrors.scala:218)
 *	 at scala.tools.nsc.typechecker.Typers$Typer.adaptMismatchedSkolems$1(Typers.scala:1017)
 *	 at scala.tools.nsc.typechecker.Typers$Typer.fallbackAfterVanillaAdapt$1(Typers.scala:1086)
 *	 at scala.tools.nsc.typechecker.Typers$Typer.vanillaAdapt$1(Typers.scala:1123)
 *	 at scala.tools.nsc.typechecker.Typers$Typer.adapt(Typers.scala:1166)
 *
 * Defining an implicit conversion from Array to another collection type doesn't work either:
 *
 * scala.reflect.internal.FatalError: Unexpected type application bp.this.`package`.arrayToList[sym: org.threeten.bp.arrayToList] in: bp.this.`package`.arrayToList[Class[NullPointerException]](scala.Array.apply[Class[NullPointerException]](classOf[java.lang.NullPointerException])((ClassTag.apply[Class[NullPointerException]](classOf[java.lang.Class]): scala.reflect.ClassTag[Class[NullPointerException]])))
 *   at scala.reflect.internal.Reporting$class.abort(Reporting.scala:59)
 *   at scala.reflect.internal.SymbolTable.abort(SymbolTable.scala:16)
 *   at scala.tools.nsc.backend.icode.GenICode$ICodePhase.genLoadApply1$1(GenICode.scala:607)
 *   at scala.tools.nsc.backend.icode.GenICode$ICodePhase.scala$tools$nsc$backend$icode$GenICode$ICodePhase$$genLoad(GenICode.scala:638)
 *   at scala.tools.nsc.backend.icode.GenICode$ICodePhase$$anonfun$genLoadArguments$1.apply(GenICode.scala:1140)
 *   at scala.tools.nsc.backend.icode.GenICode$ICodePhase$$anonfun$genLoadArguments$1.apply(GenICode.scala:1138)
 *   at scala.collection.LinearSeqOptimized$class.foldLeft(LinearSeqOptimized.scala:124)
 *   at scala.collection.immutable.List.foldLeft(List.scala:84)
 *
 * The only work-around I see is the following: We create a package object in the JS-specific test directory and define
 *   type Array[T] = List[T]
 *   def Array[T](xs: T*) = List.apply(xs: _*)
 * and deal with all the fallout this creates by adding `scala.` to all the "true" arrays.
 * (We can't do it the other way around, e. g. using _Array() for annotations and forwarding that to the right platform-specific method/type as
 * "annotation argument needs to be a constant" – EDIT: Wait? It compiles? Maybe because I added @inline? ... oh no, it doesn't. Weird.)
 *
 * Ok, after trying the work-around I can report that it fails with the same error message as with WrappedArray above.
 *
 * Additionally, default arguments also don't seem to work.
 */

class Test(
    val dataProvider: String,
    val enabled: Boolean,
    val timeOut: Long,
    val expectedExceptions: Array[Class[_ <: Throwable]] // Currently not supported, see above
  ) extends scala.annotation.StaticAnnotation with Annotation {

  def this() = {
    this(null, true, 0L, Array())
  }

  def this(dataProvider: String) = {
    this(dataProvider, true, 0L, Array())
  }

  def this(enabled: Boolean) = {
    this(null, enabled, 0L, Array())
  }

  def this(expectedExceptions: Array[Class[_ <: Throwable]]) = {
    this(null, true, 0L, expectedExceptions)
  }

  def annotationType(): Class[_ <: Annotation] = classOf[Test]
}
