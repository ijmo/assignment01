package ijmo.kakaopay.financialassistance.organization

import java.io
import java.sql.{Connection, ResultSet, Statement}

import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator

object OrganizationIdGenerator {
  val prefix = "reg"
}

class OrganizationIdGenerator extends IdentifierGenerator {
  override def generate(session: SharedSessionContractImplementor, `object`: Any): io.Serializable = {
    val connection: Connection = session.connection()
    val statement: Statement = connection.createStatement()
    val resultSet: ResultSet = statement.executeQuery("SELECT COUNT(*) AS id FROM organization")

    if (resultSet.next) {
      val id = resultSet.getInt(1)
      val generatedId = OrganizationIdGenerator.prefix + id.toString.reverse.padTo(4, '0').reverse
      return generatedId
    }
    null
  }
}
