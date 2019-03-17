case class Slide(val pictures: Set[Picture]) {
  def tags: Set[String] = pictures.flatMap(_.tags)
  def score(other: Slide): Int = Set(tags & other.tags, tags -- other.tags, other.tags -- tags).map(_.size).min
  def orientation: Char = pictures.head.orientation
}
