package reaktor.scct.report

import reaktor.scct._

object HtmlReporter {


  def report(blocks: List[CoveredBlock], env: Env)  = {

    def fixRelativePathForMaven(sourceFile: String) : String = {
      val prefix: String = env.projectId + "/"

      if (sourceFile.startsWith(prefix)) sourceFile.substring(prefix.length, sourceFile.length)
      else sourceFile
    }

    def fixRelativePathInCoveredBlockForMaven(block:CoveredBlock): CoveredBlock = {
      val name: Name = block.name
      val nameWithFixedRelativePath: Name = Name(fixRelativePathForMaven(name.sourceFile), name.classType, name.packageName, name.className)
      val blockWithFixedRelativePath: CoveredBlock =  new CoveredBlock(block.id, nameWithFixedRelativePath, block.offset, block.placeHolder)
      blockWithFixedRelativePath.count = block.count
      blockWithFixedRelativePath
    }

    new HtmlReporter(new CoverageData(blocks.map(fixRelativePathInCoveredBlockForMaven)), new HtmlReportWriter(env.reportDir), env).report
  }
}

class HtmlReporter(data: CoverageData, writer: HtmlReportWriter, env: Env) extends HtmlHelper {

  object files {
    val packages = "packages.html"
    val summary = "summary.html"
  }

  def report = {
    summaryReport
    packageListReport
    packageReports
    sourceFileReports
    resources
  }

  def summaryReport {
    val header = headerRow("Total", data.percentage)
    val items = for ((name, packageData) <- data.forPackages) yield
      itemRow(name, packageData.percentage, packageReportFileName(name))
    writer.write(files.summary, table(header, items.toList))
  }

  def packageListReport {
    val html =
      <div class="content">
        <div class="pkgRow header">
          <a href={files.summary}>Summary
            {format(data.percentage)}
          </a>
        </div>{for ((pkg, packageData) <- data.forPackages) yield {
        <div class="pkgRow pkgLink">
          <a href={packageReportFileName(pkg)}>
            {pkg}&nbsp;{format(packageData.percentage)}
          </a>
        </div> ++
          <div class="pkgRow pkgContent">
            {for ((clazz, classData) <- packageData.forClasses) yield
            <div class="pkgRow">
              <a href={classHref(clazz)}>
                <span class="className">
                  {classNameHeader(clazz)}
                </span> &nbsp;{format(classData.percentage)}
              </a>
            </div>}
          </div>
      }}
      </div>
    writer.write(files.packages, html)
  }

  def packageReports {
    for ((pkg, packageData) <- data.forPackages) {
      val header = headerRow(pkg, packageData.percentage)
      val items = classItemRows(packageData)
      writer.write(packageReportFileName(pkg), table(header, items))
    }
  }

  def resources {
    val rs = List("class.png", "object.png", "package.png", "trait.png", "filter_box_left.png", "filter_box_right.png",
      "jquery-1.6.1.min.js", "jquery-ui-1.8.4.custom.min.js", "style.css", "main.js", "index.html")
    rs.foreach {
      name =>
        writer.write(name, IO.readResourceBytes("/html-reporting/" + name))
    }
  }

  def sourceFileReports {
    for ((sourceFile, sourceData) <- data.forSourceFiles) {

      val report = SourceFileHtmlReporter.report(sourceFile, sourceData, env)
      writer.write(sourceReportFileName(sourceFile), report)
    }
  }


}