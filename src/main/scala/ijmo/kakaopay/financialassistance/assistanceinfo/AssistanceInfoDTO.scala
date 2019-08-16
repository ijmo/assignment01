package ijmo.kakaopay.financialassistance.assistanceinfo

import ijmo.kakaopay.financialassistance.base.Numbers
import javax.validation.constraints.NotEmpty

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

object AssistanceInfoDTO {
  def apply(info: AssistanceInfo): AssistanceInfoDTO =
    new AssistanceInfoDTO(
      info.organization.name,
      info.target,
      info.usages.split(",").mkString(" 및 "),
      info.maxAmount,
      Numbers.rates(info.rate1, info.rate2),
      info.recommenders.asScala.map(_.name).mkString(", "),
      info.management,
      info.reception)
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
  @BeanProperty @NotEmpty(message = "'region' is blank") var region: String = aRegion
  @BeanProperty @NotEmpty(message = "'target' is blank") var target: String = aTarget
  @BeanProperty @NotEmpty(message = "'usage' is blank") var usage: String = aUsage
  @BeanProperty @NotEmpty(message = "'limit' is blank") var limit: String = aLimit
  @BeanProperty @NotEmpty(message = "'rate' is blank") var rate: String = aRate
  @BeanProperty @NotEmpty(message = "'institute' is blank") var institute: String = aInstitute
  @BeanProperty @NotEmpty(message = "'mgmt' is blank") var mgmt: String = aMgmt
  @BeanProperty @NotEmpty(message = "'reception' is blank") var reception: String = aReception
}