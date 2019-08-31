package ijmo.kakaopay.financialassistance.search

import javax.validation.constraints.NotBlank

class SearchQueryDTO(aInput: String) {
  def this() {
    this(null)
  }

  @NotBlank(message = "'input' is blank")
  private var input: String = aInput

  def getInput: String = input
  def setInput(input: String): Unit = this.input = input
}
