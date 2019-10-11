package ijmo.kakaopay.financialassistance.assistanceinfo

import ijmo.kakaopay.financialassistance.organization.Organization
import javax.persistence.{Column, Entity, FetchType, GeneratedValue, GenerationType, Id, JoinColumn, ManyToOne, Table}

object AssistanceInfoOrganization {
  def apply(assistanceInfo: AssistanceInfo, organization: Organization): AssistanceInfoOrganization = {
    new AssistanceInfoOrganization(assistanceInfo, organization)
  }
}

@Entity
@Table(name = "assistance_info_organization_recommender")
class AssistanceInfoOrganization private (aAssistanceInfo: AssistanceInfo,
                                          aOrganization: Organization) {
  def this() {
    this(null, null)
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private val id: Long = 0L

  @ManyToOne(fetch = FetchType.LAZY)
  private var assistanceInfo: AssistanceInfo = aAssistanceInfo

  @ManyToOne(fetch = FetchType.LAZY)
  private var organization: Organization = aOrganization

  def getAssistanceInfo: AssistanceInfo = assistanceInfo
  def setAssistanceInfo(assistanceInfo: AssistanceInfo): Unit = this.assistanceInfo = assistanceInfo
  def getOrganization: Organization = organization
  def setOrganization(organization: Organization): Unit = this.organization = organization
}