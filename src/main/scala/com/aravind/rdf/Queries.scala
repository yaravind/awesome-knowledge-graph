package com.aravind.rdf

import org.apache.jena.query._
import org.apache.jena.rdf.model.Model

import scala.util.Try

object Queries {

  def getResultSet(m: Model, q: Query): Try[ResultSet] = {
    Try {
      QueryExecutionFactory
        .create(q, m)
        .execSelect()
    }.recover { case e: Exception =>
      println(s"Exception occurred: ${e.getClass.getSimpleName} - ${e.getMessage}")
      throw e
    }
  }

  def getResultSet(m: Model, qStr: String): Try[ResultSet] = {
    Try {
      val q = QueryFactory.create(qStr.stripMargin)
      QueryExecutionFactory
        .create(q, m)
        .execSelect()
    }.recover { case e: Exception =>
      println(s"Exception occurred: ${e.getClass.getSimpleName} - ${e.getMessage}")
      throw e
    }
  }

  def getResultSet(d: Dataset, qStr: String): Try[ResultSet] = {
    Try {
      val q = QueryFactory.create(qStr.stripMargin)
      QueryExecutionFactory
        .create(q, d)
        .execSelect()
    }.recover { case e: Exception =>
      println(s"Exception occurred: ${e.getClass.getSimpleName} - ${e.getMessage}")
      throw e
    }
  }
}
