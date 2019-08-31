package ijmo.kakaopay.financialassistance.assistanceinfo

import java.time.ZonedDateTime

import ijmo.kakaopay.financialassistance.base.Numbers
import ijmo.kakaopay.financialassistance.nlp.Analyzer
import ijmo.kakaopay.financialassistance.organization.{Organization, OrganizationService}
import ijmo.kakaopay.financialassistance.search.SearchService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
import scala.compat.java8.OptionConverters._

@Service
class AssistanceInfoService (val assistanceInfoRepository: AssistanceInfoRepository,
                             val organizationService: OrganizationService,
                             val searchService: SearchService) {

  def findAll: Iterable[AssistanceInfo] = assistanceInfoRepository.findAll().asScala

  def findById(id: Long): Option[AssistanceInfo] = assistanceInfoRepository.findById(id).asScala

  def findByOrganizationName: String => AssistanceInfo = assistanceInfoRepository.findByOrganizationName

  def findByOrganizationCode: String => AssistanceInfo = assistanceInfoRepository.findByOrganizationCode

  def findOrganizationNames(pageable: Pageable): Iterable[String] =
    assistanceInfoRepository.findOrganizationNamesOrderByMaxAmountNumDescAvgRateAsc(pageable).getContent.asScala

  def findOrganizationNamesWithMinimumRate: Iterable[String] =
    assistanceInfoRepository.findOrganizationNamesWithMinimumRate.asScala

  def search: (Double, Double, String, Long, Double) => Array[Object] =
    assistanceInfoRepository.findByXAndYAndUsagesAndMaxAmountAndRateLimit

  def addAssistanceInfo: AssistanceInfo => AssistanceInfo = assistanceInfoRepository.save

  def addAssistanceInfo(assistanceInfoDTO: AssistanceInfoDTO): AssistanceInfo = addAssistanceInfo(
    assistanceInfoDTO.getRegion,
    assistanceInfoDTO.getTarget,
    assistanceInfoDTO.getUsage,
    assistanceInfoDTO.getLimit,
    assistanceInfoDTO.getRate,
    assistanceInfoDTO.getInstitute,
    assistanceInfoDTO.getMgmt,
    assistanceInfoDTO.getReception)

  def addAssistanceInfo(organizationName: String, target: String, usages: String, maxAmount: String, rates: String,
                        recommenderNames: String, management: String, reception: String): AssistanceInfo = {
    Analyzer.addUserDictionary(organizationName)
    val organization: Organization = organizationService.findOrAddOrganization(organizationName)
    val recommenders = recommenderNames.split(",").toList.map(_.trim).map(s => organizationService.findOrAddOrganization(s))
    val district = searchService.findDistricts(searchService.parse(target))
    val districtName = if (district.isEmpty) null else district.min.name
    val districtCode = if (district.isEmpty) null else district.min.code
    val (longitude, latitude) = if (district.isEmpty) (null, null) else
      (district.min.location.x.toString, district.min.location.y.toString)

    addAssistanceInfo(
      AssistanceInfo(
        organization,
        target.trim,
        districtName,
        districtCode,
        longitude,
        latitude,
        AssistanceInfo.parseUsages(usages),
        maxAmount,
        rates,
        recommenders,
        management,
        reception))
  }

  def updateAssistanceInfo(assistanceInfo: AssistanceInfo, assistanceInfoDTO: AssistanceInfoDTO): AssistanceInfo =
    updateAssistanceInfo(
      assistanceInfo,
      assistanceInfoDTO.getRegion,
      assistanceInfoDTO.getTarget,
      assistanceInfoDTO.getUsage,
      assistanceInfoDTO.getLimit,
      assistanceInfoDTO.getRate,
      assistanceInfoDTO.getInstitute,
      assistanceInfoDTO.getMgmt,
      assistanceInfoDTO.getReception)

  def updateAssistanceInfo(assistanceInfo: AssistanceInfo, organizationName: String, target: String,
                           usage: String, maxAmount: String, rate: String, recommenderNames: String,
                           management: String, reception: String): AssistanceInfo = {
    val organization: Organization = organizationService.findOrAddOrganization(organizationName)
    val recommenders = recommenderNames.split(",").toList.map(_.trim).map(s => organizationService.findOrAddOrganization(s))
    val district = searchService.findDistricts(searchService.parse(target))

    val districtName = if (district.isEmpty) null else district.min.name
    val districtCode = if (district.isEmpty) null else district.min.code
    val (longitude, latitude) = if (district.isEmpty) (null, null) else
      (district.min.location.x.toString, district.min.location.y.toString)
    val maxAmountNum = Numbers.findFirst(maxAmount)
    val rates = AssistanceInfo.parseRates(rate)

    assistanceInfo.setOrganization(organization)
    assistanceInfo.setTarget(target)
    assistanceInfo.setTargetDistrictName(districtName)
    assistanceInfo.setTargetDistrictCode(districtCode)
    assistanceInfo.setLongitude(longitude)
    assistanceInfo.setLatitude(latitude)
    assistanceInfo.setUsages(AssistanceInfo.parseUsages(usage))
    assistanceInfo.setMaxAmount(maxAmount)
    assistanceInfo.setMaxAmountNum(if (maxAmountNum.isDefined) maxAmountNum.get else Long.MaxValue)
    assistanceInfo.setRate1(rates._1)
    assistanceInfo.setRate2(rates._2)
    assistanceInfo.getRecommenders.clear()
    assistanceInfo.getRecommenders.addAll(recommenders.asJava)
    assistanceInfo.setManagement(management)
    assistanceInfo.setReception(reception)
    assistanceInfo.setModifiedOn(ZonedDateTime.now())

    assistanceInfoRepository.save(assistanceInfo)
  }
}
