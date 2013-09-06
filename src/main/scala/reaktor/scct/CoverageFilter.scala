package reaktor.scct

import scala.util.matching.Regex

class CoverageFilter(excludePackages : Array[Regex]) {

  private def isIncluded(block: CoveredBlock) = {
    val isMatched = excludePackages.filter( _.findFirstIn(block.name.packageName).isDefined).size > 0
    !isMatched
  }

  def filter(data: List[CoveredBlock]): List[CoveredBlock] = data.filter( isIncluded )
}