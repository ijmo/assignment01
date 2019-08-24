package ijmo.kakaopay.financialassistance.administrativedistrict

import ijmo.kakaopay.financialassistance.base.GeoLocation

import scala.collection.Set
import scala.util.matching.Regex

trait AdministrativeDistrict

object District extends AdministrativeDistrict {
  private def getFullname(d: District): String = {
    if (d.parent == null) return d.name
    getFullname(d.parent) + " " + d.name
  }

  private def asList(d: District): List[String] = {
    if (d == null) return Nil
    asList(d.parent) ++ List(d.code, d.name)
  }

  val postfixPattern: Regex = "(특별시|광역시|특별자치시|도|특별자치도|시|군|구|읍|면|동)$".r

  val areaUnitLevel: Map[String, Int] = Map(
    "특별시" -> 0,
    "특별자치시" -> 0,
    "광역시" -> 0,
    "도" -> 0,
    "특별자치도" -> 0,
    "시" -> 1,
    "군" -> 1,
    "구" -> 2,
    "동" -> 3,
    "읍" -> 3,
    "면" -> 3
  )

  def shortenAreaUnit(areaUnit: String): String = areaUnit match {
    case "특별시" => "시"
    case "광역시" => "시"
    case "특별자치시" => "시"
    case "특별자치도" => "도"
    case _ => areaUnit
  }

  /**
    * (지명, 지역단위)로 나누기
    *
    * @param name 행정구역명
    * @return Returns (지명, 지역단위). If 지역단위 does not exist, (name, null)
    */
  def getPlaceNameAndAreaUnit(name: String): (String, String) = {
    val areaUnit = postfixPattern.findFirstIn(name).orNull
    if (areaUnit == null) {
      return (name, null)
    }
    val placeName = name.dropRight(areaUnit.length).replaceAll("[\\d·]+가?$", "")

    if (placeName.length < 2) return (name, areaUnit)

    if (areaUnit == "도" && placeName.matches("[가-힣]{2}(북|남)$")) {
      return (placeName.take(1) + placeName.takeRight(1), areaUnit)
    }
    (placeName, areaUnit)
  }
}

case class District(code: String, name: String, placeName: String, areaUnit: String, location: GeoLocation,
                    parent: District = null) extends AdministrativeDistrict with Ordered[District] {
  val various: List[String] = getVarious
  lazy val shortenName: String = placeName + District.shortenAreaUnit(areaUnit)
  lazy val fullName: String = District.getFullname(this)

  def getVarious: List[String] = Set(name, placeName, shortenName).toList

  def isChildOf(that: District): Boolean = {
    if (code.startsWith(that.code)) return true
    if (parent != null && code.length > that.code.length) return parent.isChildOf(that)
    false
  }

  def toList: List[String] = District.asList(this)

  def equals(that: District): Boolean = {
    if (code == that.code) return true
    false
  }

  def hasSamePlaceNameWith(that: District): Boolean = {
    if (this.placeName != that.placeName) return false
    if (this.parent != null && that.parent != null) return this.parent.hasSamePlaceNameWith(that.parent)
    if (this.parent != that.parent) return false
    true
  }

  override def compare(that: District): Int = code.padTo(7, '0') compare that.code.padTo(7, '0')
}
