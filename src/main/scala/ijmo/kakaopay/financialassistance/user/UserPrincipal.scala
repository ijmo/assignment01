package ijmo.kakaopay.financialassistance.user

import java.util

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserPrincipal (aUser: User) extends UserDetails {

  val user: User = aUser

  override def getAuthorities: util.Collection[_ <: GrantedAuthority] = user.getAuthorities

  override def getPassword: String = user.getPassword

  override def getUsername: String = user.getUsername

  override def isAccountNonExpired: Boolean = true

  override def isAccountNonLocked: Boolean = true

  override def isCredentialsNonExpired: Boolean = true

  override def isEnabled: Boolean = true
}
