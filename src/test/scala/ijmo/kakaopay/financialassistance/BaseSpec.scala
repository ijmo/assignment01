package ijmo.kakaopay.financialassistance

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FeatureSpec, GivenWhenThen, Matchers}
import org.springframework.test.context.TestContextManager

trait BaseSpec extends FeatureSpec with GivenWhenThen with Matchers with BeforeAndAfterEach with BeforeAndAfterAll {
  new TestContextManager(this.getClass).prepareTestInstance(this)
}
