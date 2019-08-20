package ijmo.kakaopay.financialassistance.administrativedistrict

import org.scalatest.{FeatureSpec, Matchers}
class DistrictServiceTest extends FeatureSpec with Matchers {

  var districtService: DistrictService = new DistrictService() // DistrictService does not need Spring

  feature("Support hierarchy of administrative district") {
    scenario("Check hierarchy for 상도동") {
      val districts = districtService.findDistrictsByAnyName("상도동")
      districts.find(d => d.parent != null && d.parent.name == "동작구").get should not be null
    }

    scenario("Check hierarchy for 안양동") {
      val districts = districtService.findDistrictsByAnyName("안양동")
      districts.find(d => d.parent != null && d.parent.name == "만안구" &&
        d.parent.parent != null && d.parent.parent.name == "안양시" &&
        d.parent.parent.parent != null && d.parent.parent.parent.name == "경기도").get should not be null
    }
  }
}
