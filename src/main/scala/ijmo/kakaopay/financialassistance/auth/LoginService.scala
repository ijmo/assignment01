package ijmo.kakaopay.financialassistance.auth

import java.util.Date

import ijmo.kakaopay.financialassistance.system.AppConfiguration
import io.jsonwebtoken.{Jwts, SignatureAlgorithm}
import org.springframework.stereotype.Service


@Service
class LoginService {
  def createToken(username: String): String = {
    Jwts.builder()
      .setHeaderParam("typ", "JWT")
      .setHeaderParam("issueDate", System.currentTimeMillis())
      .setSubject(username)
      .setExpiration(new Date(System.currentTimeMillis() + AppConfiguration.TOKEN_EXPIRE_TIME))
      .signWith(SignatureAlgorithm.HS512, AppConfiguration.MY_SECRET_KEY)
      .compact()
  }
}
