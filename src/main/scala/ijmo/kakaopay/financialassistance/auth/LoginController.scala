package ijmo.kakaopay.financialassistance.auth

import ijmo.kakaopay.financialassistance.system.SecurityConfig
import ijmo.kakaopay.financialassistance.user.{User, UserPrincipal, UserService}
import io.jsonwebtoken.{Claims, Jws, Jwts}
import javax.validation.Valid
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation._

@RestController
@RequestMapping(Array("/api"))
class LoginController (val loginService: LoginService,
                       val userService: UserService) {

  @PostMapping(Array("/signin"))
  def signin(@AuthenticationPrincipal u: UserPrincipal): ResponseEntity[Object] = {
    val token = loginService.createToken(u.getUsername)
    new ResponseEntity(token, HttpStatus.OK)
  }

  @PostMapping(Array("/signup"))
  def signup(@Valid @RequestBody u: User): ResponseEntity[Object] = {
    if (userService.findByUsernameAndPassword(u.username, u.password).isDefined) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST)
    }
    val user = userService.addUser(User(u.username, u.password))
    if (user == null) return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
    new ResponseEntity(HttpStatus.CREATED)
  }

  @PostMapping(Array("/refresh"))
  def refresh(@RequestHeader("Authorization") authorization: String): ResponseEntity[Object] = {
    val jwt = if (authorization.toLowerCase.startsWith("bearer ")) authorization.drop(7) else
      return new ResponseEntity(HttpStatus.UNAUTHORIZED)
    var jws: Jws[Claims] = null
    try {
      jws = Jwts.parser().setSigningKey(SecurityConfig.MY_SECRET_KEY).parseClaimsJws(jwt)
    } catch {
      case _: Throwable =>
        return new ResponseEntity(HttpStatus.UNAUTHORIZED)
    }
    val username = jws.getBody.getSubject
    val user = userService.findByUsername(username)
    if (user.isEmpty) new ResponseEntity(HttpStatus.UNAUTHORIZED)
    val token = loginService.createToken(user.get.username)
    new ResponseEntity(token, HttpStatus.OK)
  }
}
