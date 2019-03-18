class GreedyFiltered(trimThreshold: Int = 10000, tagSizeTolerance: Int = 2, fallbackConsiderRatio: Float = .2f) extends Solution {
  var hSlides: List[Slide] = List()
  var vSlides: List[Slide] = List()
  var hPicList: List[Picture] = List()
  var vPicList: List[Picture] = List()
  var usedSlides: Set[Slide] = Set()

  override def execute = {
    val gcInterval = 1000
    val slides = Array.ofDim[Slide](n)

    time {
      hPicList ++= hPics.par.toList.sortBy(_.tags.size).reverse
      vPicList ++= vPics.par.toList.sortBy(_.tags.size).reverse

      hSlides ++= hPicList.map(p => Slide(pictures = Set(p)))
      vSlides ++= vPicList.sliding(2, 2).map(pl => Slide(pictures = pl.toSet))

      slides(0) = hSlides.headOption.getOrElse(vSlides.head)
      usedSlides += slides.head

      for (i <- 0 until n - 1) {
        if ((i + 1) % 10 == 1) println(s"${i + 1} / ${n - 1}")
        val slide = findBestMatch(slides(i))
        slides(i + 1) = slide
        usedSlides += slide

        if (i % gcInterval == 0) gc
      }
    }
    slides
  }

  def findBestMatch(slide: Slide): Slide = {
    val range = slide.tags.size - tagSizeTolerance to slide.tags.size + tagSizeTolerance toSet
    val hConsider = hSlides.par.filter(!usedSlides.contains(_)) match {
      case s if s.isEmpty => List()
      case s if s.size < trimThreshold => s
      case s if s.exists(range contains _.tags.size) => s.filter(range contains _.tags.size)
      case s => s.toList.sortBy(_.tags.size - slide.tags.size).take(math.ceil(s.size * fallbackConsiderRatio).toInt)
    }
    val vConsider = vSlides.par.filter(!usedSlides.contains(_)) match {
      case s if s.isEmpty => List()
      case s if s.size < trimThreshold => s
      case s if s.exists(range contains _.tags.size) => s.filter(range contains _.tags.size)
      case s => s.toList.sortBy(_.tags.size - slide.tags.size).take(math.ceil(s.size * fallbackConsiderRatio).toInt)
    }
    val hMax = if (hConsider.nonEmpty) hConsider.par.maxBy(_.score(slide)) else null
    val vMax = if (vConsider.nonEmpty) vConsider.par.maxBy(_.score(slide)) else null
    Set(hMax, vMax).filter(_ != null).maxBy(_.score(slide))
  }

  def gc = {
    hSlides = hSlides.par.filter(!usedSlides.contains(_)).toList
    vSlides = vSlides.par.filter(!usedSlides.contains(_)).toList
  }
}