package ijmo.kakaopay.financialassistance.organization

import java.io
import java.sql.{Connection, ResultSet, Statement}

import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator

object OrganizationIdGenerator {
  val PREFIX = "reg"
}

class OrganizationIdGenerator extends IdentifierGenerator {
  override def generate(session: SharedSessionContractImplementor, `object`: Any): io.Serializable = {
    val connection: Connection = session.connection()
    val statement: Statement = connection.createStatement()
    val resultSet: ResultSet = statement.executeQuery("SELECT next_val FROM organization_sequence")

    if (resultSet.next) {
      val id = resultSet.getInt(1)
      statement.executeUpdate(s"UPDATE organization_sequence SET next_val = ${id + 1} WHERE next_val = $id")
      val generatedId = OrganizationIdGenerator.PREFIX + id.toString.reverse.padTo(4, '0').reverse
      return generatedId
    }
    null
  }
}
