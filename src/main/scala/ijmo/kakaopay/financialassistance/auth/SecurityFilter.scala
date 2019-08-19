package ijmo.kakaopay.financialassistance.auth

import ijmo.kakaopay.financialassistance.system.SecurityConfig
import io.jsonwebtoken.Jwts
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import javax.servlet.{Filter, FilterChain, ServletRequest, ServletResponse}
import org.slf4j.{Logger, LoggerFactory}

object SecurityFilter {
  val excludedUris = "/api/signup,/api/signin"
  val logger: Logger = LoggerFactory.getLogger(SecurityFilter.getClass)
}

class SecurityFilter extends Filter {

  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain): Unit = {
    val httpServletRequest: HttpServletRequest = request.asInstanceOf[HttpServletRequest]
    val httpServletResponse: HttpServletResponse = response.asInstanceOf[HttpServletResponse]

    val uri = httpServletRequest.getServletPath
    if (SecurityFilter.excludedUris.contains(uri) || uri.toLowerCase.startsWith("/actuator")) {
      chain.doFilter(request, response)
      return
    }

    try {
      val token: String = httpServletRequest.getHeader("Authorization")
      val jwt = if (token != null && token.startsWith("Bearer ")) token.drop(7) else throw new Exception("Unauthorized: Failed to get bearer token")
      val jws = Jwts.parser().setSigningKey(SecurityConfig.MY_SECRET_KEY).parseClaimsJws(jwt)
    } catch {
      case e: Throwable =>
        SecurityFilter.logger.warn(e.getMessage)
        httpServletResponse.reset()
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
        return
    }
    chain.doFilter(request, response)
  }
}
