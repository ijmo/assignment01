package ijmo.kakaopay.financialassistance.auth

import ijmo.kakaopay.financialassistance.user.User
import ijmo.kakaopay.financialassistance.{BaseSpec, IntegrationSpec}
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
class LoginIntegrationTest extends BaseSpec with IntegrationSpec {
  feature("Sign-up and Login") {
    val user: User = User("loginuser", "1234")

    scenario("New user signs up") {
      val response = testRestTemplate.exchange("/api/signup", HttpMethod.POST, createHttpEntity(user), classOf[Any])
      response.getStatusCode shouldBe HttpStatus.CREATED
    }

    scenario("Get token for new user(login)") {
      val params = new LinkedMultiValueMap[String, String]()
      params.add("grant_type", "password")
      params.add("username", user.getUsername)
      params.add("password", user.getPassword)
      val response = basicAuthTemplate.postForObject("/oauth/token", params, classOf[java.util.Map[String, String]])
    }
  }
}
