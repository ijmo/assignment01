package ijmo.kakaopay.financialassistance.assistanceinfo

import java.time.LocalDateTime

import ijmo.kakaopay.financialassistance.administrativedistrict.DistrictService
import ijmo.kakaopay.financialassistance.base.Numbers
import ijmo.kakaopay.financialassistance.nlp.Analyzer
import ijmo.kakaopay.financialassistance.organization.{Organization, OrganizationService}
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
import scala.compat.java8.OptionConverters._

@Service
class AssistanceInfoService (val assistanceInfoRepository: AssistanceInfoRepository,
                             val organizationService: OrganizationService,
                             val districtService: DistrictService) {

  def findAll: Iterable[AssistanceInfo] = assistanceInfoRepository.findAll().asScala

  def findById(id: Long): Option[AssistanceInfo] = assistanceInfoRepository.findById(id).asScala

  def findByOrganizationName: String => AssistanceInfo = assistanceInfoRepository.findByOrganizationName

  def findByOrganizationCode: String => AssistanceInfo = assistanceInfoRepository.findByOrganizationCode

  def findOrganizationNames(pageable: Pageable): Iterable[String] =
    assistanceInfoRepository.findOrganizationNamesOrderByMaxAmountNumDescAvgRateAsc(pageable).getContent.asScala

  def findOrganizationNamesWithMinimumRate: Iterable[String] =
    assistanceInfoRepository.findOrganizationNamesWithMinimumRate.asScala

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
    val recommenders = recommenderNames.split(",").toList.map(_.trim).map(s =>
      organizationService.findOrAddOrganization(s))
    val (districtName, districtCode, longitude, latitude) = districtService.findDistrictNameCodeLocation(target)

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
    Analyzer.addUserDictionary(organizationName)
    val organization: Organization = organizationService.findOrAddOrganization(organizationName)
    val recommenders = recommenderNames.split(",").toList.map(_.trim).map(s => organizationService.findOrAddOrganization(s))
    val (districtName, districtCode, longitude, latitude) = districtService.findDistrictNameCodeLocation(target)
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
    assistanceInfo.setMaxAmountNum(maxAmountNum)
    assistanceInfo.setRate1(rates._1)
    assistanceInfo.setRate2(rates._2)
    assistanceInfo.getRecommenders.clear()
    assistanceInfo.getRecommenders.addAll(recommenders.asJava)
    assistanceInfo.setManagement(management)
    assistanceInfo.setReception(reception)
    assistanceInfo.setModifiedOn(LocalDateTime.now)

    assistanceInfoRepository.save(assistanceInfo)
  }

  def searchByText(text: String): Option[Map[String, Any]] = {
    val maxAmount = Numbers.findFirst(text)
    val maxAmountNum = maxAmount.getOrElse(0L)
    val usages = AssistanceInfo.parseUsages(text)
    val rates = AssistanceInfo.parseRates(text)
    val districts = districtService.findDistrictsIn(text)

    if (districts.isEmpty) return None

    val district = districts.min
    val result = assistanceInfoRepository.searchByLocationAndUsagesAndMaxAmountAndRate(district.location.x, district.location.y, usages, maxAmountNum, rates._2.get)
    if (result.isEmpty) return None
    val row = result.get(0).asInstanceOf[Array[Object]]

    Some(Map(
      "region" -> row(0),
      "usage" -> row(1).toString.split(",").mkString(" 및 "),
      "limit" -> row(2),
      "rate" -> Numbers.rates(row(3).asInstanceOf[Double], row(4).asInstanceOf[Double])
    ))
  }
}
