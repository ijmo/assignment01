package ijmo.kakaopay.financialassistance.auth

import ijmo.kakaopay.financialassistance.system.AppConfiguration
import ijmo.kakaopay.financialassistance.user.{User, UserService}
import io.jsonwebtoken.Jwts
import javax.validation.Valid
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._

@RestController
@RequestMapping(Array("/api"))
class LoginController (val loginService: LoginService,
                       val userService: UserService) {

  @PostMapping(Array("/login"))
  def login(@Valid @RequestBody u: User): ResponseEntity[Object] = {
    val user = userService.findByUsernameAndPassword(u.username, u.password).orNull
    if (user == null) return new ResponseEntity(HttpStatus.UNAUTHORIZED)
    val token = loginService.createToken(user.username)
    new ResponseEntity(token, HttpStatus.OK)
  }

  @PostMapping(Array("/signup"))
  def signup(@Valid @RequestBody u: User): ResponseEntity[Object] = {
    if (userService.findByUsernameAndPassword(u.username, u.password).isDefined) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST)
    }
    val user = userService.addUser(User(u.username, u.password))
    if (user == null) return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
    new ResponseEntity(HttpStatus.OK)
  }

  @PostMapping(Array("/refresh"))
  def refresh(@RequestHeader("Authorization") authorization: String): ResponseEntity[Object] = {
    val jwt = authorization.drop(7)
    val jws = Jwts.parser().setSigningKey(AppConfiguration.MY_SECRET_KEY).parseClaimsJws(jwt).getBody
    val username = jws.getSubject
    val user = userService.findByUsername(username)
    val token = loginService.createToken(user.username)
    new ResponseEntity(token, HttpStatus.OK)
  }
}
