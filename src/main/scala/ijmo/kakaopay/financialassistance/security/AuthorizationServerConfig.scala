package ijmo.kakaopay.financialassistance.security

import java.time.Duration

import ijmo.kakaopay.financialassistance.user.UserService
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.{AuthorizationServerConfigurerAdapter, EnableAuthorizationServer}
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.provider.token.store.{JwtAccessTokenConverter, JwtTokenStore}
import org.springframework.security.oauth2.provider.token.TokenStore

@Configuration
@EnableAuthorizationServer
class AuthorizationServerConfig (val authenticationManager: AuthenticationManager,
                                 val userService: UserService,
                                 val passwordEncoder: PasswordEncoder)
  extends AuthorizationServerConfigurerAdapter {

  override def configure(clients: ClientDetailsServiceConfigurer): Unit = {
    clients.inMemory()
      .withClient(SecurityConfig.DEFAULT_CLIENT_ID)
      .secret(passwordEncoder.encode(SecurityConfig.DEFAULT_CLIENT_PW))
      .authorizedGrantTypes("password", "refresh_token")
      .scopes("read", "write", "assistanceInfo")
      .accessTokenValiditySeconds((Duration.ofMinutes(30).toMinutes * 60).asInstanceOf[Int])
      .refreshTokenValiditySeconds((Duration.ofDays(1).toMinutes * 60).asInstanceOf[Int])
  }

  override def configure(endpoints: AuthorizationServerEndpointsConfigurer): Unit = {
    endpoints
      .tokenStore(tokenStore())
      .accessTokenConverter(accessTokenConverter())
      .authenticationManager(authenticationManager)
      .userDetailsService(userService)
  }

  @Bean
  def accessTokenConverter(): JwtAccessTokenConverter = {
    val converter: JwtAccessTokenConverter = new JwtAccessTokenConverter()
    converter.setSigningKey(SecurityConfig.MY_SECRET_KEY)
    converter
  }

  @Bean
  def tokenStore(): TokenStore = new JwtTokenStore(accessTokenConverter())
}
