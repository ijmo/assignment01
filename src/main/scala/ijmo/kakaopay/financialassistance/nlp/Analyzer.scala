package ijmo.kakaopay.financialassistance.nlp

import org.bitbucket.eunjeon.seunjeon

import scala.collection.Iterable

object Analyzer {
  def parse(s: String): Iterable[Morpheme] = seunjeon.Analyzer.parse(s).map(_.morpheme).map(m => Morpheme(m.getSurface, m.getFeatureHead))
  def setUserDictionary(s: Iterator[String]): Unit = seunjeon.Analyzer.setUserDict(s)
  def analyzer = seunjeon.Analyzer
}
