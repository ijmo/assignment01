package ijmo.kakaopay.financialassistance.security

import java.time.Duration

import ijmo.kakaopay.financialassistance.user.UserService
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.configuration.{EnableWebSecurity, WebSecurityConfigurerAdapter}
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder

object SecurityConfig {
  val TOKEN_EXPIRE_TIME: Long = Duration.ofMinutes(30).toMillis
  val MY_SECRET_KEY: String = "mySecretKey"
  val DEFAULT_CLIENT_ID: String = "financialClientId"
  val DEFAULT_CLIENT_PW: String = "1111"
}

@Configuration
@EnableWebSecurity
class SecurityConfig (val userService: UserService) extends WebSecurityConfigurerAdapter {

  @Bean
  override def authenticationManagerBean(): AuthenticationManager = super.authenticationManagerBean()

  override protected def configure(auth: AuthenticationManagerBuilder): Unit =
    auth.authenticationProvider(authenticationProvider())

  @Bean
  def passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

  @Bean
  def authenticationProvider(): DaoAuthenticationProvider = {
    val authProvider: DaoAuthenticationProvider = new DaoAuthenticationProvider()
    authProvider.setUserDetailsService(userService)
    authProvider.setPasswordEncoder(passwordEncoder())
    authProvider
  }
}
