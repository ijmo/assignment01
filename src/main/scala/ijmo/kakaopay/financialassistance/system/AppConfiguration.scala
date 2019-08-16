package ijmo.kakaopay.financialassistance.system

import java.time.Duration

import ijmo.kakaopay.financialassistance.auth.SecurityFilter
import javax.servlet.Filter
import org.springframework.context.annotation.{Bean, Configuration}

object AppConfiguration {
  val TOKEN_EXPIRE_TIME: Long = Duration.ofMinutes(30).toMillis
  val MY_SECRET_KEY: String = "mySecretKey"
}

@Configuration
class AppConfiguration {
  @Bean
  def jwtFilter: Filter = new SecurityFilter()
}
