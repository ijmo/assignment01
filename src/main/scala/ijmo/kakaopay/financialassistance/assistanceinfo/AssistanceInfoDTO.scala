package ijmo.kakaopay.financialassistance.assistanceinfo

import ijmo.kakaopay.financialassistance.base.Numbers
import javax.validation.constraints.NotBlank

import scala.collection.JavaConverters._

object AssistanceInfoDTO {
  def apply(info: AssistanceInfo): AssistanceInfoDTO =
    new AssistanceInfoDTO(
      info.getOrganization.getName,
      info.getTarget,
      info.getUsages.split(",").mkString(" 및 "),
      info.getMaxAmount,
      Numbers.rates(info.getRate1, info.getRate2),
      info.getRecommenders.asScala.map(_.getName).mkString(", "),
      info.getManagement,
      info.getReception)
}

class AssistanceInfoDTO (aRegion: String,
                         aTarget: String,
                         aUsage: String,
                         aLimit: String,
                         aRate: String,
                         aInstitute: String,
                         aMgmt: String,
                         aReception: String) {
  def this() {
    this(null, null, null, null, null, null, null, null)
  }

  @NotBlank(message = "'region' is blank")
  private var region: String = aRegion
  @NotBlank(message = "'target' is blank")
  private var target: String = aTarget
  @NotBlank(message = "'usage' is blank")
  private var usage: String = aUsage
  @NotBlank(message = "'limit' is blank")
  private var limit: String = aLimit
  @NotBlank(message = "'rate' is blank")
  private var rate: String = aRate
  @NotBlank(message = "'institute' is blank")
  private var institute: String = aInstitute
  @NotBlank(message = "'mgmt' is blank")
  private var mgmt: String = aMgmt
  @NotBlank(message = "'reception' is blank")
  private var reception: String = aReception

  def getRegion: String = region
  def setRegion(region: String): Unit = this.region = region
  def getTarget: String = target
  def setTarget(target: String): Unit = this.target = target
  def getUsage: String = usage
  def setUsage(usage: String): Unit = this.usage = usage
  def getLimit: String = limit
  def setLimit(limit: String): Unit = this.limit = limit
  def getRate: String = rate
  def setRate(rate: String): Unit = this.rate = rate
  def getInstitute: String = institute
  def setInstitute(institute: String): Unit = this.institute = institute
  def getMgmt: String = mgmt
  def setMgmt(mgmt: String): Unit = this.mgmt = mgmt
  def getReception: String = reception
  def setReception(reception: String): Unit = this.reception = reception
}
