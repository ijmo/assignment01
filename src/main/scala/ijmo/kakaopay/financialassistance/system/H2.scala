package ijmo.kakaopay.financialassistance.system

import org.h2.tools.Server
import org.springframework.context.event.{ContextClosedEvent, ContextRefreshedEvent, EventListener}
import org.springframework.stereotype.Component

@Component
class H2 {
//  @Profile(Array("test")) // <-- up to you class H2 { private var webServer: Server = null
  private var server: Server = _
  private var webServer: Server = _

  @EventListener(Array(classOf[ContextRefreshedEvent]))
  @throws[java.sql.SQLException]
  def start(): Unit = {
    this.webServer = org.h2.tools.Server.createWebServer("-webPort", "8082", "-tcpAllowOthers").start
    this.server = org.h2.tools.Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start
  }

  @EventListener(Array(classOf[ContextClosedEvent]))
  def stop(): Unit = {
    this.webServer.stop()
    this.server.stop()
  }
}
