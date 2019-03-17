import scala.io.Source
import scala.reflect.io.File

abstract class Solution {
  val H = "H".charAt(0)
  val V = "V".charAt(0)

  var hPics: Set[Picture] = null
  var vPics: Set[Picture] = null
  var n: Int = 0
  var datasetId: Char = "x".charAt(0)

  val datasets = Map(
    "a" -> "a_example.txt",
    "b" -> "b_lovely_landscapes.txt",
    "c" -> "c_memorable_moments.txt",
    "d" -> "d_pet_pictures.txt",
    "e" -> "e_shiny_selfies.txt"
  )

  def run(datasetId: String) = {
    init(datasetId)
    val slides = execute
    finish(slides)
  }

  protected def execute: Array[Slide]

  private def finish(slides: Array[Slide]): Unit = {
    val score = totalScore(slides)
    println(s"Total Score: $score")
    File(s"submission_${datasetId}_${score}.txt").writeAll(output(slides))
    validate(slides)
  }

  private def init(datasetId: String) = {
    this.datasetId = datasetId.charAt(0)
    val lines = Source.fromFile(s"/home/ferdinand/Desktop/hashcode19/${datasets(datasetId)}").mkString.split(System.lineSeparator())
    val pics = lines.drop(1).zipWithIndex.map(t => {
      Picture(id = t._2, orientation = t._1.charAt(0), tags = t._1.split(" ").drop(2).toSet)
    })
    hPics = pics.filter(_.orientation == H).toSet
    vPics = pics.filter(_.orientation == V).toSet
    n = hPics.size + vPics.size / 2
  }

  private def totalScore(slides: Array[Slide]) = slides.sliding(2).foldLeft(0)((score, pair) => score + pair.head.score(pair(1)))

  private def output(slides: Array[Slide]) = {
    val serialized = slides.map(s => s.pictures match {
      case p if p.size == 1 => s"${p.head.id}"
      case p => s"${p.toList.head.id} ${p.toList(1).id}"
    }).mkString(System.lineSeparator())
    s"${slides.length}\n$serialized"
  }

  private def validate(slides: Array[Slide]) = {
    assert(slides.forall(_.pictures.map(_.orientation).size == 1))
    assert(slides.flatMap(_.pictures.map(_.id).toList).size == slides.flatMap(_.pictures.map(_.id).toList).distinct.size)
    assert(slides.filter(_.pictures.size == 1).flatMap(_.pictures).forall(_.orientation == H))
    assert(slides.filter(_.pictures.size == 2).flatMap(_.pictures).forall(_.orientation == V))
  }

  def time[R](block: => R): R = {
    val t0 = System.nanoTime().toDouble
    val result = block // call-by-name
    val t1 = System.nanoTime().toDouble
    println("Elapsed time: " + (t1 - t0) / 1000 / 1000 / 1000 + " sec")
    result
  }
}
