package ijmo.kakaopay.financialassistance.system

import ijmo.kakaopay.financialassistance.auth.SecurityFilter
import javax.servlet.Filter
import org.springframework.context.annotation.{Bean, Configuration}

@Configuration
class AppConfig {
  @Bean
  def jwtFilter: Filter = new SecurityFilter()
}
