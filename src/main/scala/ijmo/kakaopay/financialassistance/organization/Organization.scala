package ijmo.kakaopay.financialassistance.organization

import javax.persistence._
import org.hibernate.annotations.GenericGenerator

object Organization {
  def apply(name: String): Organization = {
    new Organization(name)
  }
}

@Entity
@Table(name = "organization")
class Organization (aName: String) {
  def this() {
    this(null)
  }

  @Id
  @GenericGenerator(name = "organization_code", strategy = "ijmo.kakaopay.financialassistance.organization.OrganizationIdGenerator")
  @GeneratedValue(generator = "organization_code")
  private val code: String = null

  @Column(name = "", unique = true, nullable = false, updatable = false)
  private var name: String = aName

  override def toString: String = s"Org($code, $name)"

  def getCode: String = code
  def getName: String = name
  def setName(name: String): Unit = this.name = name
}
