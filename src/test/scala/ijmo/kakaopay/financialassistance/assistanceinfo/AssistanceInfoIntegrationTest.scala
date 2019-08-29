package ijmo.kakaopay.financialassistance.assistanceinfo

import ijmo.kakaopay.financialassistance.{BaseSpec, IntegrationSpec}
import ijmo.kakaopay.financialassistance.search.SearchQueryDTO
import ijmo.kakaopay.financialassistance.user.User
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.http.{HttpMethod, HttpStatus}
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.TestExecutionListeners.MergeMode
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.LinkedMultiValueMap

@RunWith(classOf[SpringRunner])
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestExecutionListeners(mergeMode = MergeMode.MERGE_WITH_DEFAULTS, listeners = {
  Array(classOf[WithSecurityContextTestExecutionListener])
})
class AssistanceInfoIntegrationTest extends BaseSpec with IntegrationSpec {
  def path(p: String = ""): String = "/api/assistanceinfo" + p

  var user: User = _
  var token: String = _

  def signUp(user: User): Unit = {
    testRestTemplate.exchange("/api/signup", HttpMethod.POST, createHttpEntity(user), classOf[Any]).getStatusCode shouldBe HttpStatus.CREATED
  }

  def getAccessToken(user: User): String = {
    val params = new LinkedMultiValueMap[String, String]()
    params.add("grant_type", "password")
    params.add("username", user.getUsername)
    params.add("password", user.getPassword)
    val response = basicAuthTemplate.postForObject("/oauth/token", params, classOf[java.util.Map[String, String]])
    import scala.collection.JavaConverters._
    response.asScala("access_token")
  }

  override def beforeAll(): Unit = {
    val testUser: User = User("testuser", "1234")
    signUp(testUser)
    token = getAccessToken(testUser)
  }

  feature("AssistanceInfoController") {

    scenario("Create AssistanceInfo") {
      val dto1: AssistanceInfoDTO = new AssistanceInfoDTO( // reg0001
        "강릉시",
        "강릉시 소재 중소기업으로서 강릉시장이 추천한 자",
        "운전",
        "1백만",
        "1%~2%",
        "강릉시",
        "강릉지점",
        "강릉시 소재 영업점")
      val dto2: AssistanceInfoDTO = new AssistanceInfoDTO( // reg0002
        "경주시",
        "경주시 소재 중소기업으로서 경주시장이 추천한 자",
        "운전",
        "추천금액 이내",
        "3%",
        "경주시",
        "경주지점",
        "경주시 소재 영업점")
      val dto3: AssistanceInfoDTO = new AssistanceInfoDTO( // reg0003
        "서울시",
        "서울시 소재 중소기업으로서 강릉시장이 추천한 자",
        "운전 및 시설",
        "3백만",
        "4%~5%",
        "서울시",
        "강릉지점",
        "강릉시 소재 영업점")
      val dto4: AssistanceInfoDTO = new AssistanceInfoDTO( // reg0004
        "부산시",
        "부산시 소재 중소기업으로서 강릉시장이 추천한 자",
        "운전 및 시설",
        "4백만",
        "6%",
        "부산시",
        "강릉지점",
        "강릉시 소재 영업점")
      val dto5: AssistanceInfoDTO = new AssistanceInfoDTO( // reg0005
        "제주시",
        "제주시 소재 중소기업으로서 강릉시장이 추천한 자",
        "운전 및 시설",
        "5백만",
        "7~8%",
        "제주시",
        "제주시지점",
        "제주시 소재 영업점")
      testRestTemplate.postForEntity(path(), createHttpEntityJWT(dto1, token), classOf[Any]).getStatusCode shouldBe HttpStatus.CREATED
      testRestTemplate.postForEntity(path(), createHttpEntityJWT(dto2, token), classOf[Any]).getStatusCode shouldBe HttpStatus.CREATED
      testRestTemplate.postForEntity(path(), createHttpEntityJWT(dto3, token), classOf[Any]).getStatusCode shouldBe HttpStatus.CREATED
      testRestTemplate.postForEntity(path(), createHttpEntityJWT(dto4, token), classOf[Any]).getStatusCode shouldBe HttpStatus.CREATED
      testRestTemplate.postForEntity(path(), createHttpEntityJWT(dto5, token), classOf[Any]).getStatusCode shouldBe HttpStatus.CREATED
    }


    scenario("List AssistanceInfo") {
      val response = testRestTemplate.exchange(path(), HttpMethod.GET, createHttpEntityJWT("", token), classOf[Array[AssistanceInfoDTO]])
      response.getBody.length shouldBe 5
      response.getStatusCode shouldBe HttpStatus.OK
    }


    scenario("Modify field data") {
      Given("Modified data")
      val newRegion = "공룡시"
      val dto1: AssistanceInfoDTO = new AssistanceInfoDTO(
        newRegion, // 변경: 강릉시 -> 공룡시
        "공룡시 소재 중소기업으로서 공룡시장이 추천한 자",
        "운전",
        "추천금액 이내", // 변경: 1백만 -> 추천금액 이내
        "1%~2%",
        "강릉시",
        "강릉지점",
        "강릉시 소재 영업점")

      When(s"Http put request to ${path("/1")} is sent")
      testRestTemplate.exchange(path("/1"), HttpMethod.PUT, createHttpEntityJWT(dto1, token), classOf[Any]).getStatusCode shouldBe HttpStatus.ACCEPTED

      Then("Get response with updated data")
      val param = new AssistanceInfoDTO(newRegion, null, null, null, null, null, null, null)
      val response = testRestTemplate.postForEntity(path("/match"), createHttpEntityJWT(param, token), classOf[AssistanceInfoDTO])
      response.getStatusCode shouldBe HttpStatus.OK
      response.getBody.getRegion shouldBe newRegion
    }


    scenario("Sort by support amount limit") { // 지원금액 내림차순, 이차보전비율의 평균 오름차순
      Given("The number of result")
      val limit = 3

      When(s"Request to /find?limit=$limit is sent")
      val response = testRestTemplate.exchange(path(s"/find?limit=$limit"), HttpMethod.GET, createHttpEntityJWT("", token), classOf[Array[String]])

      Then("Get response with sorted data")
      response.getBody.length shouldBe limit
      val l = response.getBody.toList
      l.head should (be ("경주시") or be ("공룡시"))
      l(1) should (be ("경주시") or be ("공룡시"))
      l(2) shouldBe "제주시"
    }


    scenario("Minimum rate") {
      val response = testRestTemplate.exchange(path("/minimumRate"), HttpMethod.GET, createHttpEntityJWT("", token), classOf[Array[String]])
      response.getBody.head shouldBe "공룡시"
    }


    scenario("Search a proper AssistanceInfo by given text 1") {
      Given("Query text")
      val text = "철수는 충남 대천에 살고 있는데 운전 비즈니스를 하기를 원한다. 대체로 2백만은 필요하고, 이차보전은 4%이내가 좋다는"

      When("Request to /search is sent")
      val response = testRestTemplate.postForEntity(path("/search"), createHttpEntityJWT(new SearchQueryDTO(text), token), classOf[AssistanceInfoDTO])

      Then("Get response with recommended region code")
      response.getBody.getRegion shouldBe "reg0003"
    }



    // TODO: Authorization test
    // TODO: Bad argument test
  }
}
