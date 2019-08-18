package ijmo.kakaopay.financialassistance.user

import org.springframework.data.jpa.repository.JpaRepository


trait UserRepository extends JpaRepository[User, Long] {
  def findByUsername(username: String): Option[User]
}
