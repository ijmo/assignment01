package ijmo.kakaopay.financialassistance.search

import javax.validation.constraints.NotEmpty

class SearchQueryDTO(aInput: String) {
  def this() {
    this(null)
  }

  @NotEmpty(message = "'input' is blank")
  private var input: String = aInput

  def getInput: String = input
  def setInput(input: String): Unit = this.input = input
}
