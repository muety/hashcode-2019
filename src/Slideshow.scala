import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.reflect.io.File

object Slideshow extends App {
  val H = "H".charAt(0)
  val V = "V".charAt(0)

  val datasets = Map(
    "a" -> "a_example.txt",
    "b" -> "b_lovely_landscapes.txt",
    "c" -> "c_memorable_moments.txt",
    "d" -> "d_pet_pictures.txt",
    "e" -> "e_shiny_selfies.txt"
  )

  val lines = Source.fromFile(s"/home/ferdinand/Desktop/hashcode19/${datasets("d")}").mkString.split(System.lineSeparator())
  val pics = lines.drop(1).zipWithIndex.map(t => {
    Picture(id = t._2, orientation = t._1.charAt(0), tags = t._1.split(" ").drop(2).toSet)
  })

  var hPics = pics.filter(_.orientation == H).toSet
  var vPics = pics.filter(_.orientation == V).toSet
  val n = hPics.size + vPics.size / 2

  // Find good starting slide (for simplicity only consider horizontal pictures for now)
  val startSlide = Slide(pictures = Set(hPics.minBy(_.tags.size)))
  hPics = hPics -- startSlide.pictures

  time {
    var slides = ListBuffer(startSlide)
    for (i <- 0 until n - 1) {
      if ((i+1) % 10 == 1) println(s"${i+1} / ${n-1}")
      val slide = findBestMatch(slides(i))
      slides += slide
      hPics = hPics -- slide.pictures.filter(_.orientation == H)
      vPics = vPics -- slide.pictures.filter(_.orientation == V)
    }

    println(s"Total Score: ${totalScore(slides)}")
    validate(slides)
    File("submission.txt").writeAll(output(slides))
  }

  def findBestMatch(slide: Slide): Slide = {
    val hSorted = hPics.par.map(p => Slide(pictures = Set(p)))
    val vSorted = vPics.par.map(p1 => {
      vPics.map(p2 => Slide(pictures = Set(p1, p2))).maxBy(_.score(slide))
    })
    val hMax = if (hSorted.nonEmpty) hSorted.par.maxBy(_.score(slide)) else null
    val vMax = if (vSorted.nonEmpty) vSorted.par.maxBy(_.score(slide)) else null

    Set(hMax, vMax).filter(_ != null).maxBy(_.score(slide))
  }

  def totalScore(slides: ListBuffer[Slide]) = slides.sliding(2).foldLeft(0)((score, pair) => score + pair.head.score(pair(1)))

  def output(slides: ListBuffer[Slide]) = {
    val serialized = slides.map(s => s.pictures match {
      case p if p.size == 1 => s"${p.head.id}"
      case p => s"${p.toList.head.id} ${p.toList(1).id}"
    }).mkString(System.lineSeparator())
    s"${slides.length}\n$serialized"
  }

  def validate(slides: ListBuffer[Slide]) = {
    assert(slides.forall(_.pictures.map(_.orientation).size == 1))
    assert(slides.flatMap(_.pictures.map(_.id).toList).size == slides.flatMap(_.pictures.map(_.id).toList).distinct.size)
  }

  def time[R](block: => R): R = {
    val t0 = System.nanoTime().toDouble
    val result = block    // call-by-name
    val t1 = System.nanoTime().toDouble
    println("Elapsed time: " + (t1 - t0) / 1000 / 1000 / 1000 + " sec")
    result
  }
}
