package ijmo.kakaopay.financialassistance.security

import org.springframework.context.annotation.{Bean, Configuration, Primary}
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.config.annotation.web.configuration.{EnableResourceServer, ResourceServerConfigurerAdapter}
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.{DefaultTokenServices, TokenStore}

@Configuration
@EnableResourceServer
class ResourceServerConfig (val tokenStore: TokenStore,
                            val env: Environment) extends ResourceServerConfigurerAdapter {

  override def configure(http: HttpSecurity): Unit = {
    if (env.getActiveProfiles.contains("dev")) {
      http.headers().frameOptions().disable()
    }
    http
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and().authorizeRequests().anyRequest().permitAll()
  }

  override def configure(config: ResourceServerSecurityConfigurer) {
    config.tokenServices(tokenServices())
  }

  @Bean
  @Primary
  def tokenServices(): DefaultTokenServices = {
    val defaultTokenServices: DefaultTokenServices = new DefaultTokenServices()
    defaultTokenServices.setTokenStore(tokenStore)
    defaultTokenServices.setSupportRefreshToken(true)
    defaultTokenServices
  }
}
