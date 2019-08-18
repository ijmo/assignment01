package ijmo.kakaopay.financialassistance.auth

import ijmo.kakaopay.financialassistance.system.SecurityConfig
import io.jsonwebtoken.Jwts
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import javax.servlet.{Filter, FilterChain, ServletRequest, ServletResponse}

object SecurityFilter {
  val excludedUris = "/api/signup,/api/login"
}

class SecurityFilter extends Filter {

  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain): Unit = {
    val httpServletRequest: HttpServletRequest = request.asInstanceOf[HttpServletRequest]
    val httpServletResponse: HttpServletResponse = response.asInstanceOf[HttpServletResponse]

    val uri = httpServletRequest.getServletPath
    if (SecurityFilter.excludedUris.contains(uri)) {
      chain.doFilter(request, response)
      return
    }

    try {
      val token: String = httpServletRequest.getHeader("Authorization")
      val jwt = if (token != null && token.toLowerCase.startsWith("bearer ")) token.drop(7) else throw new Exception("Unauthorized")
      val jws = Jwts.parser().setSigningKey(SecurityConfig.MY_SECRET_KEY).parseClaimsJws(jwt)
    } catch {
      case e: Throwable =>
        println(e.getMessage)
        httpServletResponse.reset()
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
        return
    }
    chain.doFilter(request, response)
  }
}
