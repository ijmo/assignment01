package ijmo.kakaopay.financialassistance.assistanceinfo

import java.time.LocalDateTime

import ijmo.kakaopay.financialassistance.base.Numbers
import ijmo.kakaopay.financialassistance.organization.Organization
import javax.persistence._
import org.springframework.data.annotation.{CreatedDate, LastModifiedDate}

import scala.collection.JavaConverters._
import scala.util.matching.Regex

object AssistanceInfo {
  def apply(organization: Organization, target: String, targetDistrictName: String, targetDistrictCode: String,
            longitude: Option[Double], latitude: Option[Double], usages: String, maxAmount: String, rate: String,
            recommenders: List[Organization], management: String, reception: String): AssistanceInfo = {
    val maxAmountNum = Numbers.findFirst(maxAmount)
    val rates = parseRates(rate)

    val assistanceInfo = new AssistanceInfo(
      organization,
      target.trim,
      targetDistrictName,
      targetDistrictCode,
      longitude,
      latitude,
      usages,
      maxAmount,
      maxAmountNum,
      rates._1,
      rates._2,
      recommenders.asJava,
      management,
      reception,
      LocalDateTime.now())
    assistanceInfo
  }

  val usagePattern: Regex = "(시설|운전)".r
  val interestCoverageRatePattern: Regex = "\\d+(.\\d+)?%".r

  def parseUsages(s: String): String = {
    if (s == null || s.trim.isEmpty) return null
    usagePattern.findAllIn(s).toList.distinct.sorted.mkString(",")
  }

  private def convertRateToDouble(s: String): Option[Double] = try {
    s match {
      case x if x == null => None
      case x if x.endsWith("%") => Some(x.dropRight(1).toDouble)
      case _ => Some(s.toDouble)
    }
  } catch {
    case _: NumberFormatException => None
  }

  def parseRates(s: String): (Option[Double], Option[Double]) = {
    def removeWhitespaceAndTilde(x: String): String = x.replaceAll("[~%\\s]+", "")
    def convert(x: Option[String]): Double = x.map(removeWhitespaceAndTilde).flatMap(convertRateToDouble).get

    val from = "\\d+(.\\d+)?\\s*%?\\s*~".r findFirstIn s

    val toPattern = if (from.isDefined) "~\\s*\\d+(.\\d+)?\\s*%".r else "\\d+(.\\d+)?\\s*%".r
    val rest = if (from.isDefined) s.drop(from.get.length - 1) else s // drop tilde mark
    val to = toPattern findFirstIn rest

    if (from.isEmpty && to.isEmpty) return (None, None)
    if (from.isEmpty && to.isDefined || from.isDefined && to.isEmpty) {
      val either = if (from.isDefined) from else to
      val value = convert(either)
      return (Option(value), Option(value))
    }
    (Option(convert(from)), Option(convert(to)))
  }
}

@Entity
@Table(name = "assistance_info")
class AssistanceInfo private (aOrganization: Organization,
                              aTarget: String,
                              aTargetDistrictName: String,
                              aTargetDistrictCode: String,
                              aLongitude: Option[Double],
                              aLatitude: Option[Double],
                              aUsages: String,
                              aMaxAmount: String,
                              aMaxAmountNum: Option[Long],
                              aRate1: Option[Double],
                              aRate2: Option[Double],
                              aRecommenders: java.util.List[Organization],
                              aManagement: String,
                              aReception: String,
                              aModifiedOn: LocalDateTime) {
  def this() {
    this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private val id: Long = 0L

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organization_code")
  private var organization: Organization = aOrganization

  @Column(name = "target", nullable = false)
  private var target: String = aTarget

  @Column(name = "target_district_name", nullable = true)
  private var targetDistrictName: String = aTargetDistrictName

  @Column(name = "target_district_code", nullable = true)
  private var targetDistrictCode: String = aTargetDistrictCode

  @Column(name = "longitude", nullable = true)
  private var longitude: java.lang.Double = locationAsJava(aLongitude)

  @Column(name = "latitude", nullable = true)
  private var latitude: java.lang.Double = locationAsJava(aLatitude)

  @Column(name = "usages", nullable = false)
  private var usages: String = aUsages

  @Column(name = "max_amount", nullable = false)
  private var maxAmount: String = aMaxAmount

  @Column(name = "max_amount_num", nullable = false)
  private var maxAmountNum: java.lang.Long = maxAmountNumAsJava(aMaxAmountNum)

  @Column(name = "rate1", nullable = false)
  private var rate1: java.lang.Double = rateAsJava(aRate1)

  @Column(name = "rate2", nullable = false)
  private var rate2: java.lang.Double = rateAsJava(aRate2)

  @OneToMany(mappedBy = "assistanceInfo", fetch = FetchType.LAZY, cascade = Array(CascadeType.PERSIST))
  private var recommenders: java.util.List[AssistanceInfoOrganization] = getRecommendersInternal(aRecommenders)

  @Column(name = "management", nullable = false)
  private var management: String = aManagement

  @Column(name = "reception", nullable = false)
  private var reception: String = aReception

  @Column(name = "created_on")
  @CreatedDate
  private var createdOn: LocalDateTime = LocalDateTime.now

  @Column(name = "modified_on")
  @LastModifiedDate
  private var modifiedOn: LocalDateTime = aModifiedOn

  override def toString: String =
    s"AssistanceInfo($organization, $target, $targetDistrictName, $targetDistrictCode, $longitude, $latitude, $usages, $maxAmount, $rate1, $rate2, ${recommenders.asScala.toList.toString}, $management, $reception)"

  def getId: Long = id

  def getOrganization: Organization = organization
  def setOrganization(organization: Organization): Unit = this.organization = organization

  def getTarget: String = target
  def setTarget(target: String): Unit = this.target = target

  def getTargetDistrictName: String = targetDistrictName
  def setTargetDistrictName(targetDistrictName: String): Unit = this.targetDistrictName = targetDistrictName

  def getTargetDistrictCode: String = targetDistrictCode
  def setTargetDistrictCode(targetDistrictCode: String): Unit = this.targetDistrictCode = targetDistrictCode

  def getLongitude: Option[Double] = Option(longitude)
  def setLongitude(longitude: Option[Double]): Unit = this.longitude = locationAsJava(longitude)

  def getLatitude: Option[Double] = Option(latitude)
  def setLatitude(latitude: Option[Double]): Unit = this.latitude = locationAsJava(latitude)

  def getUsages: String = usages
  def setUsages(usages: String): Unit = this.usages = usages

  def getMaxAmount: String = maxAmount
  def setMaxAmount(maxAmount: String): Unit = this.maxAmount = maxAmount

  def getMaxAmountNum: java.lang.Long = maxAmountNum
  def setMaxAmountNum(maxAmountNum: Option[Long]): Unit = maxAmountNumAsJava(maxAmountNum)

  def getRate1: Option[Double] = Option(rate1)
  def setRate1(rate: Option[Double]): Unit = this.rate1 = rateAsJava(rate)
  def getRate2: Option[Double] = Option(rate2)
  def setRate2(rate: Option[Double]): Unit = this.rate2 = rateAsJava(rate)

  def getRecommenders: java.util.List[Organization] = recommenders.asScala.map(_.getOrganization).asJava
  private def getRecommendersInternal(recommenders: java.util.List[Organization]): java.util.List[AssistanceInfoOrganization] = {
    if (recommenders == null) {
      return new java.util.ArrayList()
    }
    recommenders.asScala.map(r => AssistanceInfoOrganization(this, r)).asJava
  }
  def setRecommenders(recommenders: java.util.List[Organization]): Unit = {
    this.recommenders = getRecommendersInternal(recommenders)
  }

  def getManagement: String = management
  def setManagement(management: String): Unit = this.management = management

  def getReception: String = reception
  def setReception(reception: String): Unit = this.reception = reception

  def getCreatedOn: LocalDateTime = createdOn
  def setCreatedOn(createdOn: LocalDateTime): Unit = this.createdOn = createdOn

  def getModifiedOn: LocalDateTime = modifiedOn
  def setModifiedOn(modifiedOn: LocalDateTime): Unit = this.modifiedOn = modifiedOn

  private def locationAsJava(o: Option[Double]): java.lang.Double = if (o != null && o.isDefined) o.get else null
  private def maxAmountNumAsJava(maxAmountNum: Option[Long]): java.lang.Long =
    if (maxAmountNum != null && maxAmountNum.isDefined) maxAmountNum.get else Long.MaxValue
  private def rateAsJava(rate: Option[Double]): java.lang.Double = if (rate != null && rate.isDefined) rate.get else 100.0
}
