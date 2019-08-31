package ijmo.kakaopay.financialassistance.nlp

import org.bitbucket.eunjeon.seunjeon

import scala.collection.{Iterable, mutable}

object Analyzer {
  private val analyzer = seunjeon.Analyzer
  private val userDictionary: mutable.HashSet[String] = mutable.HashSet()

  def parse(s: String): Iterable[Morpheme] = analyzer.parse(s).map(_.morpheme).map(m => Morpheme(m.getSurface, m.getFeatureHead))

  def parseNounsOnly(s: String): List[String] = parse(s).filter(_.feature startsWith "N").map(_.surface).toList

  def addUserDictionary(s: String): Unit = {
    if (userDictionary.contains(s)) return
    userDictionary += s
    analyzer.setUserDict(userDictionary.toIterator)
  }
}
