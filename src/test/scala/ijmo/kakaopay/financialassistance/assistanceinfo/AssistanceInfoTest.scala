package ijmo.kakaopay.financialassistance.assistanceinfo

import ijmo.kakaopay.financialassistance.BaseSpec

class AssistanceInfoTest extends BaseSpec {
  feature("AssistanceInfo utils in companion class") {
    scenario("Parse rates from string - case 1") {
      val (d1, d2) = AssistanceInfo.parseRates("a 1.0 ~ 2.0% b")
      d1.get shouldBe 1.0
      d2.get shouldBe 2.0
    }
    scenario("Parse rates from string - case 2") {
      val (d1, d2) = AssistanceInfo.parseRates("a ~ 1.0% b")
      d1.get shouldBe 1.0
      d2.get shouldBe 1.0
    }
    scenario("Parse rates from string - case 3") {
      val (d1, d2) = AssistanceInfo.parseRates("a 1.0% b")
      d1.get shouldBe 1.0
      d2.get shouldBe 1.0
    }
    scenario("Parse rates from string - case 4") {
      val (d1, d2) = AssistanceInfo.parseRates("a1.0zz")
      d1 shouldBe None
      d2 shouldBe None
    }
  }
}
