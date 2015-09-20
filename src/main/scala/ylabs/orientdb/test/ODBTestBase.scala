package ylabs.orientdb.test

import org.scalatest.Tag
import ylabs.orientdb.pool.{ ODBConnectionPool, ODBConnectConfig }
import ylabs.orientdb.session.ODBSession
import ylabs.orientdb.ODBScala
import ylabs.util.Logging
import ylabs.util.Pimpers._

import scala.util.{ Success, Try }

trait ODBTestBase extends ODBScala with Logging {

  implicit lazy val pool = new ODBConnectionPool {
    override def dbConfig: Try[ODBConnectConfig] = {
      log.info(s"Using $dbConnectConfig")
      Success(dbConnectConfig)
    }
  }

  def dbConnectConfig: ODBConnectConfig

  def dbTestTag: Tag

  def dbName: String

  def classNames: Seq[String]

  def dropClasses(): Unit = ODBSession { implicit db ⇒
    classNames.foreach(className ⇒ Try { dropClass(className) })
  }.run().withErrorLog("failed to drop classes")

  def deleteClassRecords(): Unit = ODBSession { implicit db ⇒
    classNames.foreach { className ⇒
      Try { sqlCommand(s"delete from $className").execute().asInstanceOf[java.lang.Integer] }
    }
  }.run().withErrorLog("failed to delete class records")
}
