package ijmo.kakaopay.financialassistance

import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.{HttpEntity, HttpHeaders, MediaType}
import org.springframework.test.context.TestContextManager

trait AcceptanceSpec extends FeatureSpec with GivenWhenThen with Matchers {

  new TestContextManager(this.getClass).prepareTestInstance(this)

  @Autowired
  var testRestTemplate: TestRestTemplate = _

  def createHttpEntity(body: Any): HttpEntity[Any] = {
    val headers: HttpHeaders = new HttpHeaders()
    headers.setContentType(MediaType.APPLICATION_JSON)
    new HttpEntity(body, headers)
  }
}
