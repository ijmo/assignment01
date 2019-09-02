package ijmo.kakaopay.financialassistance.administrativedistrict

import ijmo.kakaopay.financialassistance.nlp.Analyzer
import org.springframework.stereotype.Service

object DistrictService { }

@Service
class DistrictService {

  def findAllIterable(): Iterable[District] = DistrictRepository.findAllDistrictsIterable()
  def findAllDistrictsNames(): List[String] = findAllIterable().map(_.name).toList
  def findAllDistrictsShortenNames(): List[String] = findAllIterable().map(_.shortenName).toList
  def findAllDistrictsPlaceNames(): List[String] = DistrictRepository.getPlaceNameDistrictMap.keySet.toList
  def findAllDistrictsAllNames(): List[String] = findAllIterable().flatMap(_.various).toList

  def findDistrictsByPlaceName(placeName: String): List[District] = DistrictRepository.getPlaceNameDistrictMap.getOrElse(placeName, Nil)

  def findDistrictsByAnyName(name: String): List[District] = {
    val districts = findDistrictsByPlaceName(name)
    val (placeName, areaUnit) = District.getPlaceNameAndAreaUnit(name)
    if (areaUnit != null) {
      return districts ++ findDistrictsByPlaceName(placeName)
    }
    districts
  }

  def findDistrictsIn(text: String): List[District] = {
    val words = Analyzer.parseNounsOnly(text)
    val districts = words.flatMap(findDistrictsByAnyName).distinct
    districts.size match {
      case 0 => return Nil
      case 1 => if (District.areaUnitLevel(districts.head.areaUnit) < 3) return List(districts.head) else return Nil
      case _ =>
    }
    districts.combinations(2).filter(dd => {
      val sorted = dd.sorted
      sorted.last.isChildOf(sorted.head)
    }).flatten match {
      case l if l.nonEmpty => List(l.max)
      case _ => districts
    }
  }

  def findDistrictNameCodeLocation(text: String): (String, String, Option[Double], Option[Double]) = {
    val districts = findDistrictsIn(text)
    val district = if (districts.nonEmpty) districts.min else return (null, null, None, None)
    (district.name, district.code, Option(district.location.x), Option(district.location.y))
  }
}
