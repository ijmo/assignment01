package ijmo.kakaopay.financialassistance.administrativedistrict

import java.io.File

import com.github.tototoshi.csv.{CSVReader, CSVWriter}
import ijmo.kakaopay.financialassistance.GeoLocation
import play.api.libs.json.Json
import scalaj.http.{Http, HttpResponse}

import scala.collection.{immutable, parallel}

object DistrictRepository {
  private val ADMINISTRATIVE_DISTRICTS_WITH_LOCATION = "./src/main/resources/dictionary/with_locations_2019_07.csv"
  private val ADMINISTRATIVE_DISTRICTS = "./src/main/resources/dictionary/administrative_district_2019_07.csv"
  private lazy val districts: immutable.HashMap[String, List[District]] = loadDistricts(readCSV())

  private def getLocationFromKakao(d: District): District = {
    def locationFromKakao(q: String): GeoLocation = {
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
    println("Getting location from kakao... " + d.fullName)
    val location = locationFromKakao(d.fullName)
    d.copy(location = location)
  }

  private def pmapDistricts(f: District => District, ds: List[District]): List[District] = {
    val numOfThreads = 8
    val par = ds.par
    par.tasksupport = new parallel.ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(numOfThreads))
    par.map(f).toList
  }

  private def districtToStrings(district: District): List[String] = {
    val x = if (district.location != null) district.location.x.toString else ""
    val y = if (district.location != null) district.location.y.toString else ""
    val l = district.toList
    val NUM_ADDRESS_COLUMNS = 6
    l ++ List.fill(NUM_ADDRESS_COLUMNS - l.size)("") ++ List(x, y)
  }

  private def readCSV(): List[List[String]] = {
    val locFile = new File(ADMINISTRATIVE_DISTRICTS_WITH_LOCATION)
    lazy val baseFile = new File(ADMINISTRATIVE_DISTRICTS)
    val reader = CSVReader.open(if (locFile.isFile) locFile else baseFile)
    reader.all()
  }

  private def writeCSV(rows: List[List[String]]): Unit = {
    val outFile = new File(ADMINISTRATIVE_DISTRICTS_WITH_LOCATION)
    val writer = CSVWriter.open(outFile)
    writer.writeAll(rows)
    writer.close()
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

  private def loadDistricts(csv: List[List[String]]): immutable.HashMap[String, List[District]] = {
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
    })._1.filterNot(_.areaUnit.matches("읍|면"))

    val districtsWithLocation = if (districts.exists(_.location == null)) pmapDistricts(getLocationFromKakao, districts) else districts
    writeCSV(districtsWithLocation.map(districtToStrings))
    mapByName(districtsWithLocation)
  }

  def getPlaceNameDistrictMap: immutable.HashMap[String, List[District]] = districts
  def findAllDistrictsIterable(): Iterable[District] = getPlaceNameDistrictMap.values.flatten
  def findAllDistricts(): List[District] = findAllDistrictsIterable().toList
}
