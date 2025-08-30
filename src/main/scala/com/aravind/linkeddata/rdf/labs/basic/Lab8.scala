package com.aravind.linkeddata.rdf.labs.basic

import com.aravind.linkeddata.rdf.JenaModels
import com.aravind.linkeddata.Constants.{BaseDataURI, BaseOntologyURI}
import org.apache.jena.rdf.model._
import org.apache.jena.vocabulary.{RDF, RDFS, VCARD}

import scala.collection.JavaConverters.asScalaIteratorConverter

/**
 * Objective: Learn various ways to traverse and inspect the triples within a Jena Model using different iterators.
 *
 * Concepts Covered:
 *
 * <ul>
 * <li>StmtIterator (iterating over all statements)</li>
 * <li>listStatements(subject, predicate, object) (pattern matching)</li>
 * <li>listSubjects()</li>
 * <li>listPredicates()</li>
 * <li>listObjects()</li>
 * <li>listProperties(subject, predicate)</li>
 * </ul>
 */
object Lab8 {
  def main(args: Array[String]): Unit = {
    val Lab8DataURI = BaseDataURI + "labs/basic/lab6#"

    val m = ModelFactory.createDefaultModel()

    //Create Ontology - Properties
    val hasAddressProp = m.createProperty(BaseOntologyURI + "#hasAddress")
    hasAddressProp.addProperty(RDF.`type`, RDF.Property)

    val hasAuthorRoleProp = m.createProperty(BaseOntologyURI + "#hasAuthorRole")
    hasAuthorRoleProp.addProperty(RDF.`type`, RDF.Property)

    //Create data instances
    val personA = m.createResource(Lab8DataURI + "PersonA")
    personA.addProperty(VCARD.FN, "Person A")
    personA.addProperty(RDFS.label, "Person A")
    val bNodeAddress = m.createResource()
    bNodeAddress.addProperty(VCARD.Street, "123 Main St.")
    bNodeAddress.addProperty(VCARD.Locality, "Any Town")
    bNodeAddress.addProperty(VCARD.Region, "Any State")
    bNodeAddress.addProperty(VCARD.Pcode, "12345")
    m.add(personA, hasAddressProp, bNodeAddress)

    val bookX = m.createResource(Lab8DataURI + "BookX")

    val bNodePrimaryAuthor = m.createResource()
    bNodePrimaryAuthor.addProperty(VCARD.FN, "Author One")
    bNodePrimaryAuthor.addProperty(RDFS.label, "Primary Author")
    m.add(bookX, hasAuthorRoleProp, bNodePrimaryAuthor)

    val bNodeSecondaryAuthor = m.createResource()
    bNodeSecondaryAuthor.addProperty(VCARD.FN, "Author Two")
    bNodeSecondaryAuthor.addProperty(RDFS.label, "Secondary Author")
    m.add(bookX, hasAuthorRoleProp, bNodeSecondaryAuthor)

    println("Turtle prints bnodes usually with _: or nested structures")
    JenaModels.printAsTurtle(m)

    println("N-Triples usually assigns internal identifiers for bnodes")
    JenaModels.printAsTriple(m)

    println("\n1. Iterate all statements:")
    JenaModels.printStatements(m, filterBNodes = false)

    println("\n2. Pattern matching:")
    println("2.1. Find all statements with subject 'PersonA':")
    JenaModels.printStatements(m.listStatements(personA, null, null), filterBNodes = false)
    println("2.2. find all hasAuthorRole relationships")
    JenaModels.printStatements(m.listStatements(null, hasAuthorRoleProp, null), filterBNodes = false)

    println("2.3. List all unique subjects:")
    m.listSubjects().asScala.foreach { subject =>
      val label = if (subject.hasProperty(RDFS.label)) {
        println("label exists")
        JenaModels.getLabel(subject)
      }
      println(s"Subject: ${subject.toString} $label (label)")
    }
  }
}
