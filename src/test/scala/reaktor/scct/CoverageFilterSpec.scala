package reaktor.scct

import org.specs2.mutable._
import reaktor.scct.ClassTypes.ClassType

class CoverageFilterSpec extends Specification {

  "empty filter does pass thru" in {
    val data = List(block( "packageName" ))

    new CoverageFilter(Array()).filter(data) mustEqual data
  }

  "non matching filter does pass thru" in {
    val data = List(block( "packageName" ))
    new CoverageFilter(Array("nonMatching".r)).filter(data) mustEqual data
  }

  "filter excludes matches" in {
    val blockMatching = block( "MATCHINGPART" )
    val blockNonMatching = block( "nonmatching" )

    new CoverageFilter(Array("MATCHINGPART".r)).filter(List(blockMatching, blockNonMatching)) mustEqual List(blockNonMatching)
  }

  "multiple filters" in {
    val blockMatching1 = block( "MATCH1.suffix" )
    val blockMatching2 = block( "MATCH2.suffix" )
    val blockNonMatching = block( "nonmatching" )

    new CoverageFilter(Array("^MATCH1".r, "^MATCH2".r)).filter(List(blockMatching1, blockMatching2, blockNonMatching)) mustEqual List(blockNonMatching)
  }

  def block(packageName: String) = new CoveredBlock("c1", 1, Name("file1", ClassTypes.Class, packageName, "className", "projectName"), 1, false)
}