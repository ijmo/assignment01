package ijmo.kakaopay.financialassistance.auth

import ijmo.kakaopay.financialassistance.user.{User, UserService}
import javax.validation.Valid
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._

@RestController
@RequestMapping(Array("/api"))
class LoginController (val userService: UserService) {
  @PostMapping(Array("/signup"))
  def signup(@Valid @RequestBody u: User): ResponseEntity[Object] = {
    if (userService.findByUsername(u.username).isDefined) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST)
    }
    val user = userService.addUser(User(u.username, u.password))
    if (user == null) return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
    new ResponseEntity(HttpStatus.CREATED)
  }
}
