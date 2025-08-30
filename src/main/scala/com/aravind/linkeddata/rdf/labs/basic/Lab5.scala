package com.aravind.linkeddata.rdf.labs.basic

import com.aravind.linkeddata.rdf.{JenaModels, Queries}
import com.aravind.linkeddata.rdf.Reasoners.SelectAllRDFType
import com.aravind.linkeddata.Constants.BaseOntologyURI
import org.apache.jena.query.ResultSet
import org.apache.jena.rdf.model.{InfModel, ModelFactory}
import org.apache.jena.reasoner.{Reasoner, ReasonerRegistry}
import org.apache.jena.vocabulary.{RDF, VCARD}

import scala.collection.JavaConverters.asScalaIteratorConverter
import scala.util.Try

/**
 * Objective: Understand how RDFS inference rules can be applied to a model to derive new, implicit facts.
 *
 * Concepts Covered:
 *
 *  - RDFS Inference Rules (specifically rdfs:subClassOf and rdfs:subPropertyOf rules)
 *  - org.apache.jena.reasoner.Reasoner
 *  - org.apache.jena.reasoner.ReasonerRegistry.getRDFSReasoner()
 *  - org.apache.jena.rdf.model.InfModel
 *  - Distinguishing between explicit and inferred triples.
 *
 * Hints/Tips:
 *
 *  - The InfModel does not change the baseModel. It provides a view that includes both explicit and inferred triples.
 *  - The if (!baseModel.contains(stmt)) check is crucial for identifying which triples were newly inferred.
 */
object Lab5 {
  Lab4.main(Array(""))

  val model = Lab4.baseModel

  def main(args: Array[String]): Unit = {
    // Get the standard RDFS reasoner from Jena's ReasonerRegistry
    val reasoner: Reasoner = ReasonerRegistry.getRDFSReasoner

    //Create an InfModel (Inferred Model). This model combines the base model with the reasoner's inferred triples
    val inferredModel: InfModel = ModelFactory.createInfModel(reasoner, model)

    //Q1: Select all subjects and objects that have rdf:type predicate
    println(s"1. Resultset: $SelectAllRDFType")
    val rs1: Try[ResultSet] = Queries.getResultSet(inferredModel, SelectAllRDFType)
    JenaModels.printSOResultSet(rs1)
    rs1.get.close()

    //Q2: find all individuals who hasDevice something, and what that device is
    val q2 =
      s"""
         |SELECT ?person ?device
         |WHERE {
         |?person <$BaseOntologyURI#hasDevice> ?device .
         |}
         |""".stripMargin

    println(s"2. Resultset: ${q2}")
    val rs2: Try[ResultSet] = Queries.getResultSet(inferredModel, q2)
    JenaModels.printSOResultSet(rs2, "person", "device")
    rs2.get.close()

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
    val rs3: Try[ResultSet] = Queries.getResultSet(inferredModel, q3)
    rs3.get.asScala.foreach {
      r => println(s"${r.get("s")}")
    }
    rs3.get.close()

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
    val rs4: Try[ResultSet] = Queries.getResultSet(inferredModel, q4)

    rs4.get.asScala.foreach {
      r => println(s"Person Name: ${r.get("personName")}, Phone Type: ${r.get("phoneType")}")
    }
    rs4.get.close()
  }
}
