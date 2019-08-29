package ijmo.kakaopay.financialassistance.user

import org.springframework.validation.{Errors, Validator}

class UserValidator extends Validator {
  override def supports(clazz: Class[_]): Boolean = clazz.getClass.isInstance(User.getClass)

  override def validate(target: Any, errors: Errors): Unit = {
    val user: User = target.asInstanceOf[User]
    val username = user.getUsername
    val password = user.getPassword

    if (Option(username).forall(_.isEmpty)) {
      errors.rejectValue("username", "REQUIRED", "Username is required")
    }

    if (username.length < 2 || username.length > 12) {
      errors.rejectValue("username", "INVALID_LENGTH", "Username should be between 2 to 12")
    }

    if (Option(password).forall(_.isEmpty)) {
      errors.rejectValue("password", "REQUIRED", "Password is required")
    }
  }
}
