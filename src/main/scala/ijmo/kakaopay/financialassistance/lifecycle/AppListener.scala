package ijmo.kakaopay.financialassistance.lifecycle

import ijmo.kakaopay.financialassistance.administrativedistrict.DistrictService
import ijmo.kakaopay.financialassistance.assistanceinfo.AssistanceInfoService
import ijmo.kakaopay.financialassistance.search.SearchService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class AppListener (val assistanceInfoService: AssistanceInfoService,
                   val districtService: DistrictService,
                   val searchService: SearchService) {

  @EventListener
  def insertSampleData(event: ApplicationReadyEvent): Unit = { // for test
//    val reader = CSVReader.open(new File("./src/test/resources/sample/local_gov_20xx.csv"), "euc-kr")
//    val csv = reader.all().drop(1).map(_.slice(1, 9)) // 지자체명, 지원대상, 용도, 지원한도, 이차보전, 추천기관, 관리점, 취급점
//    csv.foreach(row => assistanceInfoService.addAssistanceInfo(row.head.trim, row(1), row(2), row(3), row(4), row(5), row(6), row(7)))
//    assistanceInfoService.findAll().foreach(println)
////    districtService.findAllIterable().foreach(println)
  }
}
