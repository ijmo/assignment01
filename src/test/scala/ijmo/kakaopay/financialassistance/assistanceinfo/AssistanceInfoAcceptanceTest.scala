package ijmo.kakaopay.financialassistance.assistanceinfo

import ijmo.kakaopay.financialassistance.AcceptanceSpec
import ijmo.kakaopay.financialassistance.search.SearchQueryDTO
import org.junit.runner.RunWith
import org.scalatest.Assertion
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.http.{HttpMethod, HttpStatus}
import org.springframework.test.context.junit4.SpringRunner

@RunWith(classOf[SpringRunner])
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AssistanceInfoAcceptanceTest extends AcceptanceSpec {
  // 지자체명, 지원대상, 용도, 지원한도, 이차보전, 추천기관, 관리점, 취급점
  val dto1: AssistanceInfoDTO = new AssistanceInfoDTO(
    "강릉시",
    "강릉시 소재 중소기업으로서 강릉시장이 추천한 자",
    "운전",
    "추천금액 이내",
    "3%",
    "강릉시",
    "강릉지점",
    "강릉시 소재 영업점")
  val dto2: AssistanceInfoDTO = new AssistanceInfoDTO(
    "서울시",
    "서울시 소재 중소기업으로서 강릉시장이 추천한 자",
    "시설",
    "2백만",
    "3%",
    "강릉시",
    "강릉지점",
    "강릉시 소재 영업점")
  val dto3: AssistanceInfoDTO = new AssistanceInfoDTO(
    "부산시",
    "부산시 소재 중소기업으로서 강릉시장이 추천한 자",
    "시설",
    "2백만",
    "2%",
    "강릉시",
    "강릉지점",
    "강릉시 소재 영업점")
  val dto4: AssistanceInfoDTO = new AssistanceInfoDTO(
    "광주광역시",
    "광주광역시 소재 중소기업으로서 강릉시장이 추천한 자",
    "시설",
    "1백만",
    "3%",
    "광주광역시",
    "광주광역시지점",
    "광주광역시 소재 영업점")

  def path(p: String = ""): String = "/api/assistanceinfo" + p

  def createResource(dto: AssistanceInfoDTO): Assertion = {
    val response = testRestTemplate.postForEntity(path(), dto, classOf[AssistanceInfoDTO])
    response.getStatusCode shouldBe HttpStatus.CREATED
  }

  feature("AssistanceInfoController") {
    scenario("Create an AssistanceInfo") {
      createResource(dto1)
    }

    scenario("List AssistanceInfo") {
      val response = testRestTemplate.getForEntity(path(), classOf[Array[AssistanceInfoDTO]])
      response.getBody.length shouldBe 1
      response.getStatusCode shouldBe HttpStatus.OK
    }

    scenario("Modify and find by updated field data") {
      val newRegion = "공룡시"
      dto1.setRegion(newRegion)

      val response1 = testRestTemplate.exchange(path("/1"), HttpMethod.PUT, createHttpEntity(dto1), classOf[AssistanceInfoDTO])
      response1.getStatusCode shouldBe HttpStatus.ACCEPTED

      val param = new AssistanceInfoDTO(newRegion, null, null, null, null, null, null, null)
      val response2 = testRestTemplate.postForEntity(path("/match"), param, classOf[AssistanceInfoDTO])
      response2.getBody.region shouldBe newRegion
    }

    scenario("Sort by support amount limit") {
      Given("3 more rows")
      createResource(dto2)
      createResource(dto3)
      createResource(dto4)

      When("Find data sorting by support amount descending and then rates descending")
      val response = testRestTemplate.getForEntity(path("/find?limit=3"), classOf[Array[String]])

      Then("Sorted data with limited counts lists.")
      response.getBody.length shouldBe 3
      val l = response.getBody.toList
      l.head shouldBe "공룡시"
      l(1) shouldBe "서울시"
      l(2) shouldBe "부산시"
    }

    scenario("Get a organization name which has minimum rate") {
      val response = testRestTemplate.getForEntity(path("/minimumRate"), classOf[Array[String]])
      response.getBody.head shouldBe "부산시"
    }

    scenario("Search a proper AssistanceInfo by given text") {
      val text = "철수는 충남 대천에 살고 있는데 시설 관리 비즈니스를 하기를 원한다. 대체로 2백만은 필요하고, 이차보전은 2%이내가 좋다는"
      val response = testRestTemplate.postForEntity(path("/search"), new SearchQueryDTO(text), classOf[AssistanceInfoDTO])
      response.getBody.region shouldBe "reg0003"
    }
  }
}
