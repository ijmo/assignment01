package ijmo.kakaopay.financialassistance.user

import java.time.LocalDateTime

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence._
import javax.validation.constraints.{NotBlank, Size}
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils

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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private val id: Long = 0L

  @Column(name = "username", nullable = false, unique = true)
  @Size(min = 4, max = 12)
  private var username: String = aUsername

  @Column(name = "password", nullable = false)
  @NotBlank
  private var password: String = aPassword

  @Column(name = "authorities", nullable = false)
  @JsonIgnore
  private var authorities: String = "ROLE_USER"

  @Column(name = "created_on", nullable = false)
  private var createdOn: LocalDateTime = LocalDateTime.now

  override def toString: String = s"User($username)"

  def getId: Long = id
  def getUsername: String = username
  def setUsername(username: String): Unit = this.username = username
  def getPassword: String = password
  def setPassword(password: String): Unit = this.password = password
  def getCreatedOn: LocalDateTime = createdOn
  def setCreatedOn(createdOn: LocalDateTime): Unit = this.createdOn = createdOn

  def getAuthorities: java.util.List[GrantedAuthority] = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities)

  def setAuthorities(commaSeparatedAuthorities: String): Unit = authorities = commaSeparatedAuthorities

  def setAuthorities(authorities: List[String]): Unit = this.authorities = authorities.mkString(",")
}
