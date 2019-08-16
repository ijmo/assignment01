package ijmo.kakaopay.financialassistance.organization

import javax.persistence._
import org.hibernate.annotations.GenericGenerator

import scala.beans.BeanProperty

@Entity
@Table(name = "organization")
case class Organization (aName: String) {

  def this() {
    this(null)
  }

  @Id
  @GenericGenerator(name = "organization_code", strategy = "ijmo.kakaopay.financialassistance.organization.OrganizationIdGenerator")
  @GeneratedValue(generator = "organization_code")
  @BeanProperty
  val code: String = null

  @Column(name = "", unique = true, nullable = false, updatable = false)
  @BeanProperty
  var name: String = aName


//  @ManyToOne()
//  var assistanceInfos: java.util.List[AssistanceInfo] = _

  override def toString: String = s"Org($code, $name)"
}
