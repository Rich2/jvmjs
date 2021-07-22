# jvmjs
So this repository is created from the [openstrat](https://github.com/Rich2/openstrat) for the purposes of illustrating a Scala 3 issue. The code
compiles for Jvm but is failing for Scala.js. There are 2 modules Util and Graphics. Util compiles under all 4 combinations of Js and jvm targets and
Scala 2.13.6 and Scala 3.0.2-RC1. The graphics module fails for Scala.js on Scala 3. In Sbt:<br>
<br>
graphicsJvm2/compile //compiles<br>
graphicsJvm3/compile //compiles<br>
graphicsJs2/compile //compiles<br>
graphicsJs3/compile //gives the following errors<br>


sbt:jvmjs> graphicsJs3/compile
[info] compiling 177 Scala sources to /Common/jvmjs/graphicsJs3/target/scala-3.0.2-RC1/classes ...
[error] -- Error: /Common/jvmjs/srcGraphics/Trans/ProlignMatrix.scala:48:50 ------------
[error] 48 |    (arrA : Arr[A], prolignMatrix: ProlignMatrix) => arrA.map(a => ev.prolignObj(a, prolignMatrix))
[error]    |                                                  ^
[error]    |bridge generated for member method prolignObj(arrA: ostrat.Arr[A], prolignMatrix: ostrat.geom.ProlignMatrix): ostrat.Arr[A] in anonymous class Object with ostrat.geom.Prolign {...}
[error]    |which overrides method prolignObj(obj: A, prolignMatrix: ostrat.geom.ProlignMatrix): A in trait Prolign
[error]    |clashes with definition of the member itself; both have erased type (arrA: Object, prolignMatrix: ostrat.geom.ProlignMatrix): Object."
[error] -- Error: /Common/jvmjs/srcGraphics/Trans/Slate.scala:28:84 --------------------
[error] 28 |  implicit def arrImplicit[A](implicit ev: Slate[A]): Slate[Arr[A]] = (obj, dx, dy) => obj.smap(ev.SlateXYT(_, dx, dy))
[error]    |                                                                                    ^
[error]    |bridge generated for member method SlateXYT(obj: ostrat.Arr[A], dx: Double, dy: Double): ostrat.Arr[A] in anonymous class Object with ostrat.geom.Slate {...}
[error]    |which overrides method SlateXYT(obj: T, xDelta: Double, yDelta: Double): T in trait Slate
[error]    |clashes with definition of the member itself; both have erased type (obj: Object, dx: Double, dy: Double): Object."
[error] two errors found
[error] two errors found
[error] (graphicsJs3 / Compile / compileIncremental) Compilation failed
[error] Total time: 19 s, completed 22 Jul 2021, 09:13:01
