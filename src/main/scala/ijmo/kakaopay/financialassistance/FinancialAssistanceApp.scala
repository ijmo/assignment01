package ijmo.kakaopay.financialassistance

import org.bitbucket.eunjeon.seunjeon.Analyzer
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

import scala.concurrent.{ExecutionContext, Future}

object FinancialAssistanceApp {
  def main(args: Array[String]) : Unit = {
    Future { Analyzer.parse("") } (ExecutionContext.global)
    SpringApplication.run(classOf[FinancialAssistanceApp], args :_ *)
  }
}

@SpringBootApplication
class FinancialAssistanceApp {}