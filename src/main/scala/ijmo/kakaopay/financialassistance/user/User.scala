package ijmo.kakaopay.financialassistance.user

import java.time.ZonedDateTime

import javax.persistence._
import javax.validation.constraints.NotEmpty

import scala.beans.BeanProperty

object User {
  def apply(aUsername: String, aPassword: String): User = new User(aUsername, aPassword)
}

@Entity
class User private (aUsername: String,
                    aPassword: String) {
  def this() {
    this(null, null)
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  val id: Long = 0L

  @Column(name = "username", nullable = false, unique = true)
  @BeanProperty
  @NotEmpty
  var username: String = aUsername

  @Column(name = "password", nullable = false)
  @BeanProperty
  @NotEmpty
  var password: String = aPassword

  @Column(name = "created_on")
  @BeanProperty
  var createdOn = ZonedDateTime.now

  override def toString: String = s"User($username)"
}
