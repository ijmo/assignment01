package ijmo.kakaopay.financialassistance.user

import org.springframework.stereotype.Service

@Service
class UserService (val userRepository: UserRepository) {
  def findByUsernameAndPassword(username: String, password: String): Option[User] =
    userRepository.findByUsernameAndPassword(username, password)

  def findByUsername(username: String): User = userRepository.findByUsername(username)

  def addUser(user: User): User = {
    userRepository.save(user)
  }
}
