package ijmo.kakaopay.financialassistance.assistanceinfo

import java.time.ZonedDateTime

import ijmo.kakaopay.financialassistance.base.Numbers
import ijmo.kakaopay.financialassistance.organization.Organization
import javax.persistence._

import scala.beans.BeanProperty
import scala.collection.JavaConverters._
import scala.util.matching.Regex


object AssistanceInfo {
  def apply(organization: Organization, target: String, targetDistrictName: String, targetDistrictCode: String,
            longitude: String, latitude: String, usage: String, limit: String, rate: String,
            recommenders: List[Organization], management: String, reception: String): AssistanceInfo = {
    val maxAmount = Numbers.findFirst(limit)
    val rates = parseRates(rate)
    val assistanceInfo = new AssistanceInfo(
      organization,
      target.trim,
      targetDistrictName,
      targetDistrictCode,
      longitude,
      latitude,
      usage,
      limit,
      if (maxAmount.isDefined) maxAmount.get else Long.MaxValue,
      rates._1,
      rates._2,
      recommenders.asJava,
      management,
      reception,
      ZonedDateTime.now())
    assistanceInfo.setOrganization(organization)
    assistanceInfo.setRecommenders(recommenders.asJava)
    assistanceInfo
  }

  val usagePattern: Regex = "(시설|운전)".r
  val interestCoverageRatePattern: Regex = "\\d+(.\\d+)?%".r

  def parseUsages(s: String): String = {
    if (s == null) return null
    val usages = usagePattern.findAllIn(s)
    if (usages == null) return null
    usages.toSet.toList.sorted.mkString(",")
  }

  private def convertRateToDouble(s: Any): java.lang.Double = s match {
    case x: Int => x.toDouble
    case x: String if x.endsWith("%") => x.dropRight(1).toDouble
    case null => null
    case _ => s.toString.toDouble
  }

  def parseRates(s: String): (java.lang.Double, java.lang.Double) = {
    val d1 = "\\d+(.\\d+)?\\s*%?\\s*~".r findFirstIn s match {
      case Some(ss) => ss.replaceAll("[~%\\s]+", "")
      case _ => null
    }
    val d2Pattern = if (d1 == null) "\\d+(.\\d+)?%".r else "~\\s*\\d+(.\\d+)?%".r
    val ss = if (d1 == null) s else s.drop(d1.length)
    val d2 = d2Pattern findFirstIn ss match {
      case Some(ss) => ss.replaceAll("[~%\\s]+", "")
      case _ => null
    }

    if (d1 == null && d2 == null) return (100.0, 100.0)
    if (d1 == null && d2 != null) return (convertRateToDouble(d2), convertRateToDouble(d2))
    (convertRateToDouble(d1), convertRateToDouble(d2))
  }
}

@Entity
@Table(name = "assistance_info")
class AssistanceInfo private (aOrganization: Organization,
                              aTarget: String,
                              aTargetDistrictName: String,
                              aTargetDistrictCode: String,
                              aLongitude: String,
                              aLatitude: String,
                              aUsages: String,
                              aMaxAmount: String,
                              aMaxAmountNum: java.lang.Long,
                              aRate1: java.lang.Double,
                              aRate2: java.lang.Double,
                              aRecommenders: java.util.List[Organization],
                              aManagement: String,
                              aReception: String,
                              aModifiedOn: ZonedDateTime) {
  def this() {
    this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
  }

  // show columns from assistance_info;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  val id: Long = 0L

  @OneToOne(fetch = FetchType.EAGER)
  @BeanProperty
  var organization: Organization = aOrganization

  @Column(name = "target", nullable = false)
  @BeanProperty
  var target: String = aTarget

  @Column(name = "target_district_name", nullable = true)
  @BeanProperty
  var targetDistrictName: String = aTargetDistrictName

  @Column(name = "target_district_code", nullable = true)
  @BeanProperty
  var targetDistrictCode: String = aTargetDistrictCode

  @Column(name = "longitude", nullable = true)
  @BeanProperty
  var longitude: String = aLongitude

  @Column(name = "latitude", nullable = true)
  @BeanProperty
  var latitude: String = aLatitude

  @Column(name = "usages", nullable = false)
  @BeanProperty
  var usages: String = aUsages

  @Column(name = "max_amount", nullable = true)
  @BeanProperty
  var maxAmount: String = aMaxAmount

  @Column(name = "max_amount_num", nullable = true)
  @BeanProperty
  var maxAmountNum: java.lang.Long = aMaxAmountNum

  @Column(name = "rate1", nullable = true)
  @BeanProperty
  var rate1: java.lang.Double = aRate1

  @Column(name = "rate2", nullable = true)
  @BeanProperty
  var rate2: java.lang.Double = aRate2

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "assistance_info_organization_recommend"
    , joinColumns = Array(new JoinColumn(name = "assistance_info_id"))
    , inverseJoinColumns = Array(new JoinColumn(name = "organization_code")))
  @BeanProperty
  var recommenders: java.util.List[Organization] = aRecommenders

  @Column(name = "management", nullable = false)
  @BeanProperty
  var management: String = aManagement

  @Column(name = "reception", nullable = false)
  @BeanProperty
  var reception: String = aReception

  @Column(name = "created_on")
  @BeanProperty
  var createdOn: ZonedDateTime = ZonedDateTime.now

  @Column(name = "modified_on")
  @BeanProperty
  var modifiedOn: ZonedDateTime = aModifiedOn

  override def toString: String =
    s"AssistanceInfo($organization, $target, $targetDistrictName, $targetDistrictCode, $longitude, $latitude, $usages, $maxAmount, $rate1, $rate2, ${recommenders.asScala.toList.toString}, $management, $reception)"

}
