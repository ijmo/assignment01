package ijmo.kakaopay.financialassistance.base

object Double {
  def unapply(s : String) : Option[Double] = try {
    Some(s.toDouble)
  } catch {
    case _ : java.lang.NumberFormatException => None
  }

  def from(s: String): Option[Double] = s match {
    case Double(n) => Some(n.toDouble)
    case _ => None
  }
}
