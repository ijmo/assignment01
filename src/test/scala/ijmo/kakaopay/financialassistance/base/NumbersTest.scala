package ijmo.kakaopay.financialassistance.base

import org.scalatest.{FeatureSpec, Matchers}

class NumbersTest extends FeatureSpec with Matchers {
  feature("Numbers Utilities") {
    scenario("Find korean large number") {
      Numbers.largeNumberPattern.findFirstIn("1십").get shouldBe "1십"
      Numbers.largeNumberPattern.findFirstIn("1백").get shouldBe "1백"
      Numbers.largeNumberPattern.findFirstIn("1천").get shouldBe "1천"
      Numbers.largeNumberPattern.findFirstIn("1만").get shouldBe "1만"
      Numbers.largeNumberPattern.findFirstIn("1십만").get shouldBe "1십만"
      Numbers.largeNumberPattern.findFirstIn("1백만").get shouldBe "1백만"
      Numbers.largeNumberPattern.findFirstIn("1천만").get shouldBe "1천만"
    }

    scenario("Parse korean large number") {
      Numbers.findFirst("1십").get shouldBe 10
      Numbers.findFirst("1백").get shouldBe 100
      Numbers.findFirst("1천").get shouldBe 1000
      Numbers.findFirst("1만").get shouldBe 10000
      Numbers.findFirst("1십만").get shouldBe 100000
      Numbers.findFirst("1백만").get shouldBe 1000000
      Numbers.findFirst("1천만").get shouldBe 10000000
      Numbers.findFirst("1억").get shouldBe 100000000
    }
  }
}
