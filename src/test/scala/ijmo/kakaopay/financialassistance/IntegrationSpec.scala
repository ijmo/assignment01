package ijmo.kakaopay.financialassistance

import ijmo.kakaopay.financialassistance.security.SecurityConfig
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FeatureSpec, GivenWhenThen, Matchers}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.{HttpEntity, HttpHeaders, HttpMethod, HttpStatus, MediaType, ResponseEntity}
import org.springframework.test.context.TestContextManager

trait IntegrationSpec extends FeatureSpec with GivenWhenThen with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  new TestContextManager(this.getClass).prepareTestInstance(this)

  @Autowired
  var testRestTemplate: TestRestTemplate = _

  def basicAuthTemplate: TestRestTemplate =
    testRestTemplate.withBasicAuth(SecurityConfig.DEFAULT_CLIENT_ID, SecurityConfig.DEFAULT_CLIENT_PW)

  def createHttpEntity(body: Any): HttpEntity[Any] = {
    val headers: HttpHeaders = new HttpHeaders()
    headers.setContentType(MediaType.APPLICATION_JSON)
    new HttpEntity(body, headers)
  }

  def createHttpEntityJWT(body: Any, jwt: String): HttpEntity[Any] = {
    val headers: HttpHeaders = new HttpHeaders()
    headers.setContentType(MediaType.APPLICATION_JSON)
    headers.add("Authorization", "Bearer " + jwt)
    new HttpEntity(body, headers)
  }

  def httpPost(path: String, payload: Any): ResponseEntity[Any] = {
    testRestTemplate.postForEntity(path, payload, classOf[Any])
  }

  def httpPostJWT(path: String, payload: Any, token: String): ResponseEntity[Any] = {
    testRestTemplate.postForEntity(path, createHttpEntityJWT(payload, token), classOf[Any])
  }

  def httpGet(path: String): ResponseEntity[Any] = {
    testRestTemplate.getForEntity(path, classOf[Any])
  }

  def httpGetJWT(path: String, token: String): ResponseEntity[Any] = {
    testRestTemplate.exchange(path, HttpMethod.GET, createHttpEntityJWT("", token), classOf[Any])
  }

  def httpPut(path: String, payload: Any): ResponseEntity[Any] = {
    testRestTemplate.exchange(path, HttpMethod.PUT, createHttpEntity(payload), classOf[Any])
  }

  def httpPutJWT(path: String, payload: Any, token: String): ResponseEntity[Any] = {
    testRestTemplate.exchange(path, HttpMethod.PUT, createHttpEntityJWT(payload, token), classOf[Any])
  }
}
