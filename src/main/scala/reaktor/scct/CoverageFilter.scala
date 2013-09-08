package reaktor.scct

import scala.util.matching.Regex

class CoverageFilter(excludePackages : Array[Regex]) {

  private var debug = System.getProperty("scct.debug") == "true"

  private def isIncluded(block: CoveredBlock) = {
    val isMatched = excludePackages.filter( _.findFirstIn(block.name.sourceFile).isDefined).size > 0

    if (debug && isMatched) println("scct : excluding " + block.name.sourceFile)

    !isMatched
  }

  def filter(data: List[CoveredBlock]): List[CoveredBlock] = data.filter( isIncluded )
}