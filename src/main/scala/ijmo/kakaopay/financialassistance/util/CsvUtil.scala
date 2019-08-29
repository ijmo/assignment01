package ijmo.kakaopay.financialassistance.util

import com.github.tototoshi.csv.{CSVReader, CSVWriter}
import org.slf4j.LoggerFactory

object CsvUtil {
  val log = LoggerFactory.getLogger(CsvUtil.getClass)

  def readAll(file: java.io.File, encoding: String): List[List[String]] = {
    var reader: CSVReader = null
    try {
      reader = CSVReader.open(file, encoding)
      reader.all()
    } catch {
      case e: Exception => log.error(e.getMessage); null
    } finally {
      if (reader != null) {
        reader.close()
      }
    }
  }

  def write(rows: List[List[String]], f: java.io.File): Unit = {
    var writer: CSVWriter = null
    try {
      writer = CSVWriter.open(f)
      writer.writeAll(rows)
    } catch {
      case e: Exception => log.error(e.getMessage)
    } finally {
      if (writer != null) {
        writer.close()
      }
    }
  }
}
