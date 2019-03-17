class GreedySolutionN3 extends Solution {
  override def execute = {
    // Find good starting slide (for simplicity only consider horizontal pictures for now)
    val startSlide = Slide(pictures = Set(hPics.maxBy(_.tags.size)))
    val slides = Array.ofDim[Slide](n)
    slides(0) = startSlide
    hPics = hPics -- startSlide.pictures

    time {
      for (i <- 0 until n - 1) {
        if ((i + 1) % 10 == 1) println(s"${i + 1} / ${n - 1}")
        val slide = findBestMatch(slides(i))
        slides(i+1) = slide
        hPics = hPics -- slide.pictures.filter(_.orientation == H)
        vPics = vPics -- slide.pictures.filter(_.orientation == V)
      }
    }

    slides
  }

  def findBestMatch(slide: Slide): Slide = {
    val hSlides = hPics.par.map(p => Slide(pictures = Set(p)))
    val vSlides = vPics.par.map(p1 => {
      vPics.map(p2 => Slide(pictures = Set(p1, p2))).maxBy(_.score(slide))
    })
    val hMax = if (hSlides.nonEmpty) hSlides.par.maxBy(_.score(slide)) else null
    val vMax = if (vSlides.nonEmpty) vSlides.par.maxBy(_.score(slide)) else null

    Set(hMax, vMax).filter(_ != null).maxBy(_.score(slide))
  }
}
