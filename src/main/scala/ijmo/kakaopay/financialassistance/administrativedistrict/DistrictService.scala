package ijmo.kakaopay.financialassistance.administrativedistrict

import ijmo.kakaopay.financialassistance.base.GeoLocation
import org.springframework.stereotype.Service

object DistrictService { }

@Service
class DistrictService {

  def findAllIterable(): Iterable[District] = DistrictRepository.findAllDistrictsIterable()
  def findAllDistrictsNames(): List[String] = findAllIterable().map(_.name).toList
  def findAllDistrictsShortenNames(): List[String] = findAllIterable().map(_.shortenName).toList
  def findAllDistrictsPlaceNames(): List[String] = DistrictRepository.getPlaceNameDistrictMap.keySet.toList
  def findAllDistrictsAllNames(): List[String] = findAllIterable().flatMap(_.various).toList

  def findDistrictsByAnyName(name: String): List[District] = {
    val districts = findDistrictsByPlaceName(name)
    val (placeName, areaUnit) = District.getPlaceNameAndAreaUnit(name)
    if (areaUnit != null) {
      return districts ++ findDistrictsByPlaceName(placeName)
    }
    districts
  }

//  def findDistrictsByName(name: String): List[District] = {
//    val (placeName, _) = District.getPlaceNameAndAreaUnit(name)
//    val districts = findDistrictsByPlaceName(placeName)
//    districts.find(_.name == name).orNull
//  }

  def findDistrictsByPlaceName(placeName: String): List[District] = DistrictRepository.getPlaceNameDistrictMap.getOrElse(placeName, Nil)

  def findDistrictsOrderByDistanceTo(location: GeoLocation): List[District] = {
    findAllIterable().toList.sortBy(_.location.distanceTo(location))
  }

  def findAllDistrictsIn(text: String): List[District] = {
    findAllIterable().filter(_.various.exists(text.contains)).toList
  }
}
