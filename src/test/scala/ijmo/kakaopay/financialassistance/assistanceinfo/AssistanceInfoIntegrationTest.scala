package ijmo.kakaopay.financialassistance.assistanceinfo

import ijmo.kakaopay.financialassistance.IntegrationSpec
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
class AssistanceInfoIntegrationTest extends IntegrationSpec {
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

    // TODO: Search how to test endpoints with @PreAuthorize
//    scenario("Do unauthorized access") {
//      When("Create an AssistanceInfo")
//      testRestTemplate.postForEntity(path(), createHttpEntity(dto1), classOf[Any]).getStatusCode shouldBe HttpStatus.UNAUTHORIZED
//
//      When("List AssistanceInfo")
//      testRestTemplate.getForEntity(path(), classOf[Any]).getStatusCode shouldBe HttpStatus.UNAUTHORIZED
//
//      When("Update AssistanceInfo")
//      testRestTemplate.exchange(path("/1"), HttpMethod.PUT, createHttpEntity(dto1), classOf[Any]).getStatusCode shouldBe HttpStatus.UNAUTHORIZED
//
//      When("List AssistanceInfo with limited size")
//      testRestTemplate.getForEntity(path("/find?limit=3"), classOf[Any]).getStatusCode shouldBe HttpStatus.UNAUTHORIZED
//
//      When("Get AssistanceInfo which has minimum rate")
//      testRestTemplate.getForEntity(path("/minimumRate"), classOf[Any]).getStatusCode shouldBe HttpStatus.UNAUTHORIZED
//
//      When("Get AssistanceInfo which has specific region name")
//      testRestTemplate.postForEntity(path("/match"), createHttpEntity(dto1), classOf[Any]).getStatusCode shouldBe HttpStatus.UNAUTHORIZED
//
//      When("Search AssistanceInfo for given text")
//      testRestTemplate.postForEntity(path("/search"), createHttpEntity(""), classOf[Any]).getStatusCode shouldBe HttpStatus.UNAUTHORIZED
//    }

    scenario("Create AssistanceInfo") {
      testRestTemplate.postForEntity(path(), createHttpEntityJWT(dto1, token), classOf[Any]).getStatusCode shouldBe HttpStatus.CREATED
    }

    scenario("List AssistanceInfo") {
      val response = testRestTemplate.exchange(path(), HttpMethod.GET, createHttpEntityJWT("", token), classOf[Array[AssistanceInfoDTO]])
      response.getBody.length shouldBe 1
      response.getStatusCode shouldBe HttpStatus.OK
    }

    scenario("Modify and find by updated field data") {
      val newRegion = "공룡시"
      dto1.setRegion(newRegion)

      testRestTemplate.exchange(path("/1"), HttpMethod.PUT, createHttpEntityJWT(dto1, token), classOf[Any]).getStatusCode shouldBe HttpStatus.ACCEPTED

      val param = new AssistanceInfoDTO(newRegion, null, null, null, null, null, null, null)
      val response = testRestTemplate.postForEntity(path("/match"), createHttpEntityJWT(param, token), classOf[AssistanceInfoDTO])
      response.getStatusCode shouldBe HttpStatus.OK
      response.getBody.region shouldBe newRegion
    }

    scenario("Sort by support amount limit") {
      Given("3 more rows")
      testRestTemplate.postForEntity(path(), createHttpEntityJWT(dto2, token), classOf[Any]).getStatusCode shouldBe HttpStatus.CREATED
      testRestTemplate.postForEntity(path(), createHttpEntityJWT(dto3, token), classOf[Any]).getStatusCode shouldBe HttpStatus.CREATED
      testRestTemplate.postForEntity(path(), createHttpEntityJWT(dto4, token), classOf[Any]).getStatusCode shouldBe HttpStatus.CREATED

      When("Find data sorting by support amount descending and then rates descending")
      val response = testRestTemplate.exchange(path("/find?limit=3"), HttpMethod.GET, createHttpEntityJWT("", token), classOf[Array[String]])

      Then("Sorted data with limited counts lists.")
      response.getBody.length shouldBe 3
      val l = response.getBody.toList
      l.head shouldBe "공룡시"
      l(1) shouldBe "서울시"
      l(2) shouldBe "부산시"
    }

    scenario("Get a organization name which has minimum rate") {
      val response = testRestTemplate.exchange(path("/minimumRate"), HttpMethod.GET, createHttpEntityJWT("", token), classOf[Array[String]])
      response.getBody.head shouldBe "부산시"
    }

    scenario("Search a proper AssistanceInfo by given text") {
      val text = "철수는 충남 대천에 살고 있는데 시설 관리 비즈니스를 하기를 원한다. 대체로 2백만은 필요하고, 이차보전은 2%이내가 좋다는"
      val response = testRestTemplate.postForEntity(path("/search"), createHttpEntityJWT(new SearchQueryDTO(text), token), classOf[AssistanceInfoDTO])
      response.getBody.region shouldBe "reg0003"
    }
  }
}
