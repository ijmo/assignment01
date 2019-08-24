package ijmo.kakaopay.financialassistance.base

import ijmo.kakaopay.financialassistance.base

object GeoLocation {
  def apply(x: Double, y: Double): GeoLocation = new GeoLocation(x, y)

  def apply(sx: String, sy: String): GeoLocation = {
    val x = base.Double.from(sx)
    val y = base.Double.from(sy)

    if (x.isDefined && y.isDefined) new GeoLocation(x.get, y.get)
    else null
  }

  def distanceBetween(a: GeoLocation)(b: GeoLocation): Double = math.hypot(a.x - b.x, a.y - b.y)
}

class GeoLocation private(val x: Double, val y: Double) {
  override def toString: String = "<" + x + ", " + y + ">"

  def distanceTo(g: GeoLocation): Double = GeoLocation.distanceBetween(this)(g)
}
