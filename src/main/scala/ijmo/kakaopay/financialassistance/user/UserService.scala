package ijmo.kakaopay.financialassistance.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.{UserDetails, UserDetailsService}
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService (val userRepository: UserRepository) extends UserDetailsService {

  @Autowired
  var passwordEncoder: PasswordEncoder = _

  override def loadUserByUsername(username: String): UserDetails = {
    findByUsername(username).map(u => new UserPrincipal(u)).orNull
  }

  def findByUsername(username: String): Option[User] = userRepository.findByUsername(username.trim.toLowerCase)

  def findByUsernameAndPassword(username: String, password: String): Option[User] = {
    val user = findByUsername(username)
    if (user.isEmpty) return None
    if (passwordEncoder.matches(password, user.get.getPassword)) user
    else None
  }

  def addUser(user: User): User = {
    userRepository.save(User(user.username.trim.toLowerCase, passwordEncoder.encode(user.password)))
  }
}
