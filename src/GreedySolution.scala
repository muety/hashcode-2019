import scala.collection.parallel.ParSet

class GreedySolution extends Solution {
  var hSlides: ParSet[Slide] = null
  var vSlides: ParSet[Slide] = null

  override def execute = {
    // Find good starting slide using very simple heuristic
    // If horizontal pics are presents: Take the one with most tags
    // If only vertical pics are present: Take the one with most tags and find good match

    val startSlide = hPics match {
      case p if p.nonEmpty => Slide(pictures = Set(hPics.maxBy(_.tags.size)))
      case _ =>
        val p1 = vPics.par.maxBy(_.tags.size)
        vPics.par.map(p2 => Slide(pictures = Set(p1, p2))).filter(_.pictures.size == 2).maxBy(_.tags.size)
    }
    val slides = Array.ofDim[Slide](n)
    slides(0) = startSlide
    hPics --= startSlide.pictures.filter(_.orientation == H)
    vPics --= startSlide.pictures.filter(_.orientation == V)

    time {
      hSlides = hPics.par.map(p => Slide(pictures = Set(p)))
      // Pre-compute horizontal slides by combining pictures with very distinct tags
      // TODO: Use stream for this!
      vSlides = ParSet()
      while (vPics.nonEmpty) {
        val s = vPics.par.map(p2 => Slide(pictures = Set(vPics.head, p2))).filter(_.pictures.size == 2).maxBy(_.tags.size)
        vSlides += s
        vPics --= s.pictures
        if (vPics.size % 10 == 0) println(s"Pre-processing: ${vPics.size}")
      }

      for (i <- 0 until n - 1) {
        if ((i + 1) % 10 == 1) println(s"${i + 1} / ${n - 1}")
        val slide = findBestMatch(slides(i))
        slides(i+1) = slide
        if (slide.orientation == H) hSlides -= slide
        else if (slide.orientation == V) vSlides -= slide
      }
    }

    slides
  }

  def findBestMatch(slide: Slide): Slide = {
    val hMax = if (hSlides.nonEmpty) hSlides.par.maxBy(_.score(slide)) else null
    val vMax = if (vSlides.nonEmpty) vSlides.par.maxBy(_.score(slide)) else null
    Set(hMax, vMax).filter(_ != null).maxBy(_.score(slide))
  }
}
