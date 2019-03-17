class GreedySolutionTrimmed(trimThreshold: Int, sampleSize: Float) extends GreedySolution {
  override def execute = {
    if (n > trimThreshold) {
      hPics = hPics.par.toList.sortBy(_.tags.size).reverse.take(math.round(hPics.size * sampleSize)).toSet
      vPics = vPics.par.toList.sortBy(_.tags.size).reverse.take(roundEven(vPics.size * sampleSize)).toSet
      updateN
    }
    super.execute
    // TODO: Append remaining pictures in random order to the end
  }

  def roundEven(d: Double): Int = math.round(d / 2) * 2 toInt
}
