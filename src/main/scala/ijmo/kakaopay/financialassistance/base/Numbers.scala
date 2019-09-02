package ijmo.kakaopay.financialassistance.base

import scala.util.matching.Regex

object Numbers {
  val largeNumberPattern: Regex = "\\d+(,\\d+)?\\s*(십|백|천|만|억)+".r
  val largeNumberUnits: Map[String, String] = Map(
    "십" -> "0",
    "백" -> "00",
    "천" -> "000",
    "만" -> "0000",
    "억" -> "00000000")

  def from(s: String): Long = largeNumberUnits.foldLeft(s)((acc, kv) => acc.replace(kv._1, kv._2)).trim.toLong

  def findFirst(s: String): Option[Long] = largeNumberPattern findFirstIn s match {
    case Some(found) => Some(from(found.replaceAll("[,\\s]+", "")))
    case None => None
  }

  def rates(d1: Double, d2: Double): String = {
    if (d1 == 100) return "대출이자 전액"
    if (d1 == d2) return s"$d1%"
    s"$d1% ~ $d2%"
  }
}
