package ijmo.kakaopay.financialassistance.auth

import ijmo.kakaopay.financialassistance.user.{User, UserService, UserValidator}
import javax.validation.Valid
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation._

import scala.collection.JavaConverters._

@RestController
@RequestMapping(Array("/api"))
class LoginController (val userService: UserService) {

  @InitBinder(Array("user"))
  def initUserBinder(dataBinder: WebDataBinder): Unit = dataBinder.setValidator(new UserValidator)

  @PostMapping(Array("/signup"))
  def signup(@Valid @RequestBody u: User, result: BindingResult): ResponseEntity[Object] = {
    if (result.hasErrors) {
      return new ResponseEntity(result.getAllErrors.asScala.map(_.getDefaultMessage).mkString(","), HttpStatus.BAD_REQUEST)
    }
    if (userService.findByUsername(u.getUsername).isDefined) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST)
    }
    val user = userService.addUser(User(u.getUsername, u.getPassword))
    if (user == null) return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
    new ResponseEntity(HttpStatus.CREATED)
  }
}
