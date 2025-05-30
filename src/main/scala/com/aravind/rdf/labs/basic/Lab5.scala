package com.aravind.rdf.labs.basic

import com.aravind.JenaModels
import com.aravind.rdf.labs.Constants.BaseOntologyURI
import org.apache.jena.query.{QueryExecutionFactory, QueryFactory, ResultSet}
import org.apache.jena.rdf.model.{InfModel, ModelFactory}
import org.apache.jena.reasoner.{Reasoner, ReasonerRegistry}
import org.apache.jena.vocabulary.{RDF, VCARD}

import scala.collection.JavaConverters.asScalaIteratorConverter

object Lab5 {
  Lab4.main(Array(""))

  val model = Lab4.baseModel

  def main(args: Array[String]): Unit = {
    // Get the standard RDFS reasoner from Jena's ReasonerRegistry
    val reasoner: Reasoner = ReasonerRegistry.getRDFSReasoner

    //Create an InfModel (Inferred Model). This model combines the base model with the reasoner's inferred triples
    val inferredModel: InfModel = ModelFactory.createInfModel(reasoner, model)

    //Q1: Select all subjects and objects that have rdf:type predicate
    //We are specifying the full RDF URI instead of using namespace prefix
    val q1 = QueryFactory.create(
      s"""
         |PREFIX rdf: <${RDF.getURI}>
         |SELECT ?s ?o
         |WHERE {
         | ?s rdf:type ?o .
         |}
         |""".stripMargin)

    println(s"1. Resultset: $q1")
    val rs1: ResultSet = QueryExecutionFactory
      .create(q1, inferredModel)
      .execSelect()
    JenaModels.printSOResultSet(rs1)
    rs1.close()

    //Q2: find all individuals who hasDevice something, and what that device is
    val q2 =
      s"""
         |SELECT ?person ?device
         |WHERE {
         |?person <$BaseOntologyURI#hasDevice> ?device .
         |}
         |""".stripMargin

    println(s"2. Resultset: ${q2}")
    val rs2: ResultSet = QueryExecutionFactory
      .create(q2, inferredModel)
      .execSelect()

    JenaModels.printSOResultSet(rs2, "person", "device")
    rs2.close()

    //Q3: find all resources that are of type device:Smartphone
    val q3 =
      s"""
         |PREFIX rdf: <${RDF.getURI}>
         |SELECT ?s
         |WHERE {
         |?s rdf:type <$BaseOntologyURI#SmartPhone> .
         |}
         |""".stripMargin

    println(s"3. Resultset: ${q3}")
    val rs3: ResultSet = QueryExecutionFactory
      .create(q3, inferredModel)
      .execSelect()

    rs3.asScala.foreach {
      r => println(s"${r.get("s")}")
    }
    rs3.close()

    //4. find the name of a person and the type of cell phone they own
    val q4 =
      s"""
         |PREFIX rdf: <${RDF.getURI}>
         |SELECT ?personName ?phoneType
         |WHERE {
         |?person <${VCARD.FN.getURI}> ?personName .
         |?person <$BaseOntologyURI#hasCellPhone> ?phone .
         |?phone rdf:type ?phoneType .
         |}
         |""".stripMargin

    println(s"4. Resultset: ${q4}")
    val rs4: ResultSet = QueryExecutionFactory
      .create(q4, inferredModel)
      .execSelect()

    rs4.asScala.foreach {
      r => println(s"Person Name: ${r.get("personName")}, Phone Type: ${r.get("phoneType")}")
    }
    rs4.close()
  }
}
