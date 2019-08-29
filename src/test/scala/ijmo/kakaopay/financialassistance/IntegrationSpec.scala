package ijmo.kakaopay.financialassistance

import ijmo.kakaopay.financialassistance.security.SecurityConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.{HttpEntity, HttpHeaders, MediaType}

trait IntegrationSpec {
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
}
