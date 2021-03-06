/*
  # Default MG7 test configuration and parameters
*/
package ohnosequences.test.mg7

import ohnosequences.mg7._, loquats._
import ohnosequences.datasets._, illumina._
import ohnosequences.cosas._, types._, klists._
import ohnosequences.loquat._
import ohnosequences.statika._, aws._
import ohnosequences.blast.api._
import ohnosequences.db.rna16s
import ohnosequences.awstools.ec2._, InstanceType._
import ohnosequences.awstools.s3._
import ohnosequences.awstools.autoscaling._
import ohnosequences.awstools.regions.Region._
import com.amazonaws.auth._, profile._
import ohnosequences.datasets.illumina._

case object testDefaults {

  lazy val mg7 = ohnosequences.generated.metadata.mg7

  /* Output test data *is* scoped by version */
  lazy val commonS3Prefix = S3Folder("resources.ohnosequences.com", mg7.organization) /
    mg7.artifact /
    mg7.version /
    "test" /

  def outputS3FolderFor(pipeline: String): (SampleID, StepName) => S3Folder = { (sampleID, step) =>
    commonS3Prefix / pipeline / sampleID / step /
  }


  trait MG7PipelineDefaults extends AnyMG7Pipeline {

    val metadata = ohnosequences.generated.metadata.mg7
    val iamRoleName = "loquat.testing"
    val logsBucketName = "loquat.testing"

    val splitConfig  = SplitConfig(1)
    val blastConfig  = BlastConfig(100)
    val assignConfig = AssignConfig(6)
    val mergeConfig  = MergeConfig(1)
    val countConfig  = CountConfig(1)
  }

  lazy val loquatUser = LoquatUser(
    email = "aalekhin@ohnosequences.com",
    localCredentials = new ProfileCredentialsProvider("default"),
    keypairName = "aalekhin"
  )

  val IlluminaParameters = defaults.Illumina.parameters(rna16sRefDB)
  val PacBioParameters = defaults.PacBio.parameters(rna16sRefDB)
}
