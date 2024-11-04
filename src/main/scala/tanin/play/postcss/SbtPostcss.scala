package tanin.play.postcss

import sbt._
import com.typesafe.sbt.web.{PathMapping, SbtWeb}
import com.typesafe.sbt.web.js.JS
import com.typesafe.sbt.web.pipeline.Pipeline
import sbt.Keys._
import org.apache.ivy.util.ChecksumHelper
import sbt.Task
import sbt.internal.util.ManagedLogger

class Shell {
  def execute(logger: ManagedLogger, cmd: String, cwd: File, envs: (String, String)*): Int = {
    import scala.sys.process._

    val envString = if (envs.nonEmpty) {
      envs.toList.map { case (k, v) => s"$k=$v"}.mkString(" ") + " "
    } else {
      ""
    }

    logger.info(s"Current working dir: ${cwd.getCanonicalPath}")
    logger.info(s"Executing: ${envString}${cmd}")

    val exitCode = Process(cmd, cwd, envs: _*).!
    logger.info(s"Exited with $exitCode")

    exitCode
  }

  def fileExists(file: File): Boolean = file.exists()
}

object Import {

  val postcss = TaskKey[Pipeline.Stage]("postcss", "Run postcss-cli")

  object PostcssKeys {
    val binaryFile = TaskKey[String]("postcss-cli-binary", "The postcss CLI binary.")
    val assetPath = TaskKey[String]("postcss-asset-path", "The input asset path for postcss-cli. We support only 1 input file for now.")
    val nodeModulesPath = TaskKey[String]("postcss-node-modules-path", "The location of the node_modules. Default: ./node_modules")
  }

}

object SbtPostcss extends AutoPlugin {

  override def requires = SbtWeb

  override def trigger = AllRequirements

  val autoImport = Import

  import SbtWeb.autoImport._
  import WebKeys._
  import autoImport._
  import PostcssKeys._

  override def projectSettings: Seq[Setting[_]] = Seq(
    (postcss / includeFilter) := AllPassFilter,
    (postcss / excludeFilter) := HiddenFileFilter,
    (postcss / nodeModulesPath) := "./node_modules",
    postcss := postcssStage.value
  )

  def postcssStage: Def.Initialize[Task[Pipeline.Stage]] = Def.task {
    val include = (postcss / includeFilter).value
    val exclude = (postcss / excludeFilter).value
    val binaryFilePath = (postcss / binaryFile).value
    val inputAssetPath = (postcss / assetPath).value
    val target = webTarget.value / postcss.key.label
    val logger = (Assets / streams).value.log
    val nodeModulesLocation = (postcss / nodeModulesPath).value
    val shell = new Shell

    { mappings =>
      PostcssStage.run(
        mappings = mappings.filter { case (file, output) => include.accept(file) && !exclude.accept(file) },
        binaryFilePath = binaryFilePath,
        inputAssetPath = inputAssetPath,
        targetDir = target,
        nodeModulesPath = nodeModulesLocation,
        logger = logger,
        shell = shell
      )
    }
  }

  object PostcssStage {

    def run(
      mappings: Seq[PathMapping],
      binaryFilePath: String,
      inputAssetPath: String,
      targetDir: File,
      nodeModulesPath: String,
      logger: ManagedLogger,
      shell: Shell
    ): Seq[PathMapping] = {
      val (inputEntries, ignoreds) = mappings
        .partition { case (file, path) =>
          path == inputAssetPath
        }

      val processeds = inputEntries
        .map { case (file, path) =>
          val outputFile = targetDir / path
          val cmd = Seq(
            binaryFilePath,
            file.getCanonicalPath,
            "--config",
            ".",
            "--output",
            outputFile.getCanonicalPath
          ).mkString(" ")

          shell.execute(logger, cmd, new File("."), "NODE_PATH" -> nodeModulesPath)

          if (!shell.fileExists(outputFile)) {
            throw new Exception(s"${outputFile.getCanonicalPath} should have been generated, but it wasn't.")
          }

          logger.info(s"Generated: ${outputFile.getCanonicalPath}")
          outputFile -> path
        }

      ignoreds ++ processeds
    }
  }
}
