package ijmo.kakaopay.financialassistance.search

import ijmo.kakaopay.financialassistance.administrativedistrict.{District, DistrictService}
import ijmo.kakaopay.financialassistance.assistanceinfo.{AssistanceInfo, AssistanceInfoRepository}
import ijmo.kakaopay.financialassistance.base.Numbers
import ijmo.kakaopay.financialassistance.nlp.Analyzer
import org.springframework.stereotype.Service

@Service
class SearchService (val districtService: DistrictService,
                     val assistanceInfoRepository: AssistanceInfoRepository) {
  def parse(text: String): List[String] = Analyzer.parse(text).filter(_.feature startsWith "N").map(_.surface).toList

  def findDistricts(words: List[String]): List[District] = {
    val districts = words.flatMap(districtService.findDistrictsByAnyName).distinct
    districts.size match {
      case 0 => return Nil
      case 1 => if (District.areaUnitLevel(districts.head.areaUnit) < 3) return List(districts.head) else return Nil
      case _ =>
    }
    districts.combinations(2).filter(dd => {
        val sorted = dd.sorted
        sorted.last.isChildOf(sorted.head)
      }).flatten match {
      case l if l.nonEmpty => List(l.max)
      case _ => districts
    }
  }

  def searchByText(text: String): AssistanceInfo = {
    val maxAmount = Numbers.findFirst(text)
    val maxAmountNum = maxAmount.getOrElse(Long.MaxValue)
    val usages = AssistanceInfo.parseUsages(text)
    val rates = AssistanceInfo.parseRates(text)
    val districts = findDistricts(parse(text))
    val district = if (districts.isEmpty) null else districts.min

    val result = assistanceInfoRepository.findByXAndYAndUsagesAndMaxAmountAndRateLimit(district.location.x, district.location.y, usages, maxAmountNum, rates._1)
    if (result == null || result.length == 0) return null
    val organizationCode = result(0).asInstanceOf[Array[Object]](0)
    assistanceInfoRepository.findByOrganizationCode(organizationCode.toString)
  }
}
