package com.aravind.rdf.labs

import org.apache.jena.query.{Query, QueryExecutionFactory, QueryFactory}
import org.apache.jena.rdf.model.Model
import org.apache.jena.vocabulary.RDF

object Reasoners {
  /**
   * Query to select all subjects and objects that have rdf:type predicate.
   * This query is used to demonstrate the inference capabilities of RDFS reasoners.
   */
  val SelectAllRDFType: Query = QueryFactory.create(
    //We are specifying the full RDF URI instead of using namespace prefix
    s"""
       |PREFIX rdf: <${RDF.getURI}>
       |SELECT ?s ?o
       |WHERE {
       | ?s rdf:type ?o .
       |}
       |""".stripMargin)

  val SelectAllRDFType2: Query = QueryFactory.create(
    //We are specifying the full RDF URI instead of using namespace prefix
    s"""
       |PREFIX rdf: <${RDF.getURI}>
       |SELECT ?s ?o
       |WHERE {
       | ?s rdf:type ?o .
       | FILTER(
       |  ?o != rdf:Property &&
       |  STRSTARTS(STR(?s), "https://github.com/yaravind/data") &&
       |  STRSTARTS(STR(?o), "https://github.com/yaravind/data")
       | )
       |}
       |""".stripMargin)

  val SelectAllRDFType3: Query = QueryFactory.create(
    //We are specifying the full RDF URI instead of using namespace prefix
    s"""
       |SELECT ?s ?o
       |WHERE {
       | FILTER(
       |  STRSTARTS(STR(?s), "https://github.com/yaravind/data") &&
       |  STRSTARTS(STR(?o), "https://github.com/yaravind/data")
       | )
       |}
       |""".stripMargin)
}