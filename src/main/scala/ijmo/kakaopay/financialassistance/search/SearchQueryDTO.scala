package ijmo.kakaopay.financialassistance.search

import javax.validation.constraints.NotEmpty

import scala.beans.BeanProperty

object SearchQueryDTO {
  def apply(input: String): SearchQueryDTO =
    new SearchQueryDTO(input)
}

class SearchQueryDTO (aInput: String) {
  def this() {
    this(null)
  }
  @BeanProperty
  @NotEmpty
  var input: String = aInput
}
