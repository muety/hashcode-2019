case class Picture(id: Int, orientation: Char, tags: Set[String]) {
  override def toString: String = s"[$id] $orientation: ${tags.toString}"
}
