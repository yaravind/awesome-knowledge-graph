package com.aravind

import org.apache.jena.query.{Query, QueryExecutionFactory, QueryFactory}
import org.apache.jena.rdf.model.Model

object Queries {

  def getResultSet(m: Model, q: Query) = {
    QueryExecutionFactory
      .create(q, m)
      .execSelect()
  }

  def getResultSet(m: Model, qStr: String) = {
    val q = QueryFactory.create(qStr.stripMargin)
    QueryExecutionFactory
      .create(q, m)
      .execSelect()
  }
}
