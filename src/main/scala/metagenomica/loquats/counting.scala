package ohnosequences.metagenomica.loquats

import ohnosequences.metagenomica.configuration._
import ohnosequences.metagenomica.bundles

import ohnosequences.metagenomica.bio4j._, taxonomyTree._, titanTaxonomyTree._

import ohnosequences.loquat.dataProcessing._
import ohnosequences.loquat.utils._

import ohnosequences.statika.instructions._
import ohnosequences.cosas._, typeSets._
import ohnosequences.datasets._, dataSets._, fileLocations._


case object countingDataProcessing extends DataProcessingBundle(
  bundles.bio4jNCBITaxonomy
)(input = lcaCSV :^: bbhCSV :^: DNil,
  output = lcaCountsCSV :^: bbhCountsCSV :^: DNil
) {

  def instructions: AnyInstructions = say("I'm counting you!")

  // returns count of the given id and a filtered list (without that id)
  def count(id: ID, list: List[ID]): (Int, List[ID]) =
    list.foldLeft( (0, List[ID]()) ) { case ((count, rest), next) =>
      if (next == id) (count + 1, rest)
      else (count, next :: rest)
    }

  def directCounts(taxIds: List[TaxID]): Map[TaxID, Int] = {

    @scala.annotation.tailrec
    def rec(list: List[TaxID], acc: Map[TaxID, Int]): Map[TaxID, Int] =
      list match{
        case Nil => acc
        case h :: t => {
          val (n, rest) = count(h, t)
          rec(rest, acc.updated(h, n + 1))
        }
      }

    rec(taxIds, Map[TaxID, Int]())
  }

  // TODO: figure out some more effective algorithm
  // Caution: it uses bio4j bundle!
  def accumulatedCounts(counts: Map[TaxID, Int]): Map[TaxID, (Int, Int)] = {
    counts.foldLeft(
      Map[TaxID, (Int, Int)]()
    ) { case (acc, (id, count)) =>
      val node: Option[TitanTaxonNode] = titanTaxonNode(bundles.bio4jNCBITaxonomy.graph, id)
      val ancestors: Seq[AnyTaxonNode] = node.map{ n => pathToTheRoot(n, Seq()) }.getOrElse(Seq())

      ancestors.foldLeft(
        acc.updated(id, (count, 0))
      ) { (acc, node) =>
        val (direct, accumulated) = acc.get(node.id).getOrElse((0, 0))
        acc.updated(node.id, (direct, accumulated + count))
      }
    }
  }

  def processData(
    dataMappingId: String,
    context: Context
  ): Instructions[OutputFiles] = {

    import com.github.tototoshi.csv._

    // same thing that we do for lca and bbh
    def processFile(f: file): file = {
      val csvReader: CSVReader = CSVReader.open( f.javaFile )
      val counts: Map[TaxID, (Int, Int)] = accumulatedCounts(
        // FIXME: use some csv api instead of row(1)
        directCounts( csvReader.iterator.map{ row => row(1) }.toList )
      )
      csvReader.close

      val outFile = context / s"${f.name}.counts"
      val csvWriter = CSVWriter.open(outFile.javaFile, append = true)
      counts foreach { case (taxId, (dir, acc)) => csvWriter.writeRow( List(taxId, dir, acc) ) }
      csvWriter.close

      outFile
    }

    val lcaOut: file = processFile( context.file(lcaCSV) )
    val bbhOut: file = processFile( context.file(bbhCSV) )

    success(
      s"Results are written to ...",
      lcaCountsCSV.inFile(lcaOut) :~:
      bbhCountsCSV.inFile(bbhOut) :~:
      ∅
    )
  }
}
