package ijmo.kakaopay.financialassistance.administrativedistrict

import java.io.File

import com.github.tototoshi.csv.{CSVReader, CSVWriter}
import ijmo.kakaopay.financialassistance.GeoLocation
import play.api.libs.json.Json
import scalaj.http.{Http, HttpResponse}

import scala.collection.{immutable, parallel}

object DistrictRepository {
  private val LOC_FILE = "./src/main/resources/dictionary/with_locations_2019_07.csv"
  private val ADMINISTRATIVE_DISTRICT_FILE = "./src/main/resources/dictionary/administrative_district_2019_07.csv"
  private lazy val districts: immutable.HashMap[String, List[District]] = mapByName(loadDistricts)

  private def getLocation(d: District): District = {
    def getLocationFromKakao(q: String): GeoLocation = {
      val response: HttpResponse[String] = Http("https://dapi.kakao.com/v2/local/search/address.json")
        .param("query", q)
        .header("Accept", "application/json")
        .header("Authorization", "KakaoAK c95295ed963a98c89e51c8ff0fa6fa95")
        .asString
      val json = Json.parse(response.body)
      val elem = (json \ "documents" \ 0).getOrElse(return null)
      val x = (elem \ "x").get.as[String]
      val y = (elem \ "y").get.as[String]
      GeoLocation(x.toDouble, y.toDouble)
    }
    val location = getLocationFromKakao(d.fullName)
    d.copy(location = location)
  }

  private def pmapDistricts(ds: List[District], f: District => District): List[District] = {
    val numOfThreads = 8
    val par = ds.par
    par.tasksupport = new parallel.ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(numOfThreads))
    par.map(f).toList
  }

  private def saveInFile(ds: List[District]): Unit = {
    val districtRows = ds.map(d => {
      val x = if (d.location != null) d.location.x else ""
      val y = if (d.location != null) d.location.y else ""
      val l = d.toList
      l ++ List.fill(6 - l.size)("") ++ List(x, y)
    })
    val outFile = new File(LOC_FILE)
    val writer = CSVWriter.open(outFile)
    writer.writeAll(districtRows)
    writer.close()
  }

  private def getDistrictFrom(row: List[String], stack: List[District]): (District, List[District]) = {
    def getDistrict(row: List[String]): District = {
      if (row.isEmpty) return null
      val codeName = row.takeRight(2)
      val code = codeName.head
      val name = codeName(1)
      val (placeName, areaUnit) = District.getPlaceNameAndAreaUnit(name)
      District(code, name, placeName, areaUnit, null, getDistrict(row.dropRight(2)))
    }
    (getDistrict(row.dropRight(2).filter(_ != "")).copy(location = GeoLocation(row.takeRight(2).head, row.last)),
      stack)
  }

  private def loadDistricts: List[District] = {
    val locFile = new File(LOC_FILE)
    lazy val baseFile = new File(ADMINISTRATIVE_DISTRICT_FILE)
    val reader = CSVReader.open(if (locFile.isFile) locFile else baseFile)
    val csv = reader.all()

    val districts = csv.foldLeft((List[District](), List[District]())) ((acc, row) => {
      val location = GeoLocation(row.takeRight(2).head, row.last)
      val codeName = row.dropRight(2).filter(_ != "").takeRight(2)
      val code = codeName.head
      val name = codeName(1)
      val (placeName, areaUnit) = District.getPlaceNameAndAreaUnit(name)
      val areaUnitLevel = District.areaUnitLevel(areaUnit)
      val stack = acc._2.filter(d => District.areaUnitLevel(d.areaUnit) < areaUnitLevel)
      val parent = if (stack.isEmpty) null else stack.last
      val district = District(code, name, placeName, areaUnit, location, parent)
      (acc._1 :+ district, stack :+ district)
    })._1 //.filterNot(_.areaUnit.matches("읍|면"))

    val districtsWithLocation = if (districts.exists(_.location == null)) pmapDistricts(districts, getLocation) else districts
    saveInFile(districtsWithLocation)
    districtsWithLocation
  }

  private def mapByName(districts: List[District]): immutable.HashMap[String, List[District]] = {
    districts.foldLeft(immutable.HashMap[String, immutable.List[District]]()) ((m, district) => {
      if (m.contains(district.placeName)) {
        if (m(district.placeName).exists(_.hasSamePlaceNameWith(district))) m
        else m + (district.placeName -> (m(district.placeName) :+ district))
      } else {
        m + (district.placeName -> immutable.List(district))
      }
    })
  }

  def getPlaceNameDistrictMap: immutable.HashMap[String, List[District]] = districts
  def findAllDistrictsIterable(): Iterable[District] = getPlaceNameDistrictMap.values.flatten
  def findAllDistricts(): List[District] = findAllDistrictsIterable().toList
}
