package ijmo.kakaopay.financialassistance.assistanceinfo

import java.time.ZonedDateTime

import ijmo.kakaopay.financialassistance.base.Numbers
import ijmo.kakaopay.financialassistance.nlp.Analyzer
import ijmo.kakaopay.financialassistance.organization.{Organization, OrganizationService}
import ijmo.kakaopay.financialassistance.search.SearchService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._

@Service
class AssistanceInfoService (val assistanceInfoRepository: AssistanceInfoRepository,
                             val organizationService: OrganizationService,
                             val searchService: SearchService) {

  def findAll(): Iterable[AssistanceInfo] = assistanceInfoRepository.findAll().asScala
  def findById(id: Long): Option[AssistanceInfo] = Some(assistanceInfoRepository.findById(id))
  def findOrganizationNames(pageable: Pageable): Iterable[String] = assistanceInfoRepository.findOrganizationNames(pageable).getContent.asScala
  def findOrganizationNamesWithMinimumRate(): Iterable[String] = assistanceInfoRepository.findOrganizationNamesWithMinimumRate().asScala

  def findByOrganizationName(organizationName: String): AssistanceInfo =
    assistanceInfoRepository.findByOrganizationName(organizationName)

  def addAssistanceInfo(assistanceInfo: AssistanceInfo): AssistanceInfo = {
    assistanceInfoRepository.save(assistanceInfo)
  }

  def addAssistanceInfo(assistanceInfoDTO: AssistanceInfoDTO): AssistanceInfo = addAssistanceInfo(
    assistanceInfoDTO.region,
    assistanceInfoDTO.target,
    assistanceInfoDTO.usage,
    assistanceInfoDTO.limit,
    assistanceInfoDTO.rate,
    assistanceInfoDTO.institute,
    assistanceInfoDTO.mgmt,
    assistanceInfoDTO.reception)

  def addAssistanceInfo(organizationName: String, target: String, usage: String, limit: String, rates: String,
                        recommenderNames: String, management: String, reception: String): AssistanceInfo = {
    Analyzer.setUserDictionary(Iterator.single(organizationName))
    val organization: Organization = organizationService.findOrAddOrganization(organizationName)
    val recommenders = recommenderNames.split(",").toList.map(_.trim).map(s => organizationService.findOrAddOrganization(s))
    val district = searchService.findDistricts(searchService.parse(target))
    val districtName = if (district.isEmpty) null else district.min.name
    val districtCode = if (district.isEmpty) null else district.min.code
    val (longitude, latitude) = if (district.isEmpty) (null, null) else
      (district.min.location.x.toString, district.min.location.y.toString)

    println(organizationName)
    assistanceInfoRepository.save(
      AssistanceInfo(
        organization,
        target.trim,
        districtName,
        districtCode,
        longitude,
        latitude,
        AssistanceInfo.parseUsages(usage),
        limit,
        rates,
        recommenders,
        management,
        reception))
  }

  def updateAssistanceInfo(assistanceInfo: AssistanceInfo, assistanceInfoDTO: AssistanceInfoDTO): AssistanceInfo =
    updateAssistanceInfo(
      assistanceInfo,
      assistanceInfoDTO.region,
      assistanceInfoDTO.target,
      assistanceInfoDTO.usage,
      assistanceInfoDTO.limit,
      assistanceInfoDTO.rate,
      assistanceInfoDTO.institute,
      assistanceInfoDTO.mgmt,
      assistanceInfoDTO.reception)

  def updateAssistanceInfo(assistanceInfo: AssistanceInfo, organizationName: String, target: String, usage: String, limit: String, rate: String,
                           recommenderNames: String, management: String, reception: String): AssistanceInfo = {
    Analyzer.setUserDictionary(Iterator.single(organizationName))
    val organization: Organization = organizationService.findOrAddOrganization(organizationName)
    val recommenders = recommenderNames.split(",").toList.map(_.trim).map(s => organizationService.findOrAddOrganization(s))
    val district = searchService.findDistricts(searchService.parse(target))

    val districtName = if (district.isEmpty) null else district.min.name
    val districtCode = if (district.isEmpty) null else district.min.code
    val (longitude, latitude) = if (district.isEmpty) (null, null) else
      (district.min.location.x.toString, district.min.location.y.toString)
    val maxAmountNum = Numbers.findFirst(limit)
    val rates = AssistanceInfo.parseRates(rate)

    assistanceInfo.setOrganization(organization)
    assistanceInfo.setTarget(target)
    assistanceInfo.setTargetDistrictName(districtName)
    assistanceInfo.setTargetDistrictCode(districtCode)
    assistanceInfo.setLongitude(longitude)
    assistanceInfo.setLatitude(latitude)
    assistanceInfo.setUsages(AssistanceInfo.parseUsages(usage))
    assistanceInfo.setMaxAmount(limit)
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
