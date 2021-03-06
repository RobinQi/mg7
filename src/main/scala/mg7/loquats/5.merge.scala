package ohnosequences.mg7.loquats

import ohnosequences.mg7._
import ohnosequences.loquat._
import ohnosequences.statika._
import ohnosequences.cosas._, types._, klists._
import ohnosequences.datasets._
import better.files._

case class mergeDataProcessing() extends DataProcessingBundle()(
  input  = data.mergeInput,
  output = data.mergeOutput
) {
  def instructions: AnyInstructions = say("Merging, joining, amalgamating!")

  // TODO: use streams, file-writers, etc. stuff
  def mergeChunks(dir: File, out: File): Unit = {
    // only one level in depth:
    dir.list.foreach { chunkFile =>
      out.append( chunkFile.contentAsString )
      chunkFile.delete()
    }
  }

  def process(context: ProcessingContext[Input]): AnyInstructions { type Out <: OutputFiles } = {

    val blastMerged  = (context / "blast.csv").createIfNotExists()
    val noHitsMerged = (context / "blast.no-hits").createIfNotExists()
    val lcaMerged    = (context / "lca.csv").createIfNotExists()
    val bbhMerged    = (context / "bbh.csv").createIfNotExists()

    // TODO: write header for Blast output
    LazyTry { mergeChunks( context.inputFile(data.blastChunksFolder), blastMerged)  } -&-
    LazyTry { mergeChunks( context.inputFile(data.blastNoHitsFolder), noHitsMerged) } -&-
    LazyTry {
      // header row:
      lcaMerged.appendLine( csv.assignment.columns.labels.mkString(",") )
      mergeChunks( context.inputFile(data.lcaChunksFolder), lcaMerged  )
    } -&-
    LazyTry {
      // header row:
      bbhMerged.appendLine( csv.assignment.columns.labels.mkString(",") )
      mergeChunks( context.inputFile(data.bbhChunksFolder), bbhMerged )
    } -&-
    success(s"Everything is merged",
      data.blastResult(blastMerged)   ::
      data.blastNoHits(noHitsMerged)  ::
      data.lcaCSV(lcaMerged)          ::
      data.bbhCSV(bbhMerged)          ::
      *[AnyDenotation { type Value <: FileResource }]
    )
  }
}
