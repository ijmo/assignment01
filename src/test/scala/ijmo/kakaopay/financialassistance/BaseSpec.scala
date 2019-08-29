package ijmo.kakaopay.financialassistance

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FeatureSpec, GivenWhenThen, Matchers}
import org.springframework.test.context.TestContextManager

trait BaseSpec extends FeatureSpec with GivenWhenThen with Matchers with BeforeAndAfter with BeforeAndAfterAll {
  new TestContextManager(this.getClass).prepareTestInstance(this)
}
