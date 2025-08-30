package com.aravind.linkeddata.rdf.labs.basic

import com.aravind.linkeddata.rdf.JenaModels
import com.aravind.linkeddata.Constants.{BaseDataURI, BaseOntologyURI}
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.vocabulary.{RDF, RDFS, VCARD}

/**
 * Objective: Learn how to use blank nodes (anonymous resources) to model complex relationships without assigning explicit URIs.
 *
 * Concepts Covered:
 *
 * <ul>
 * <li>Blank Nodes (Bnodes)</li>
 * <li>When to use blank nodes (e.g., describing components, nested structures)</li>
 * <li>Creating blank nodes in Jena (model.createResource())</li>
 * <li>How blank nodes appear in different serialization formats.</li>
 * </ul>
 *
 * Expected output:
 *
 * The Turtle output will show the blank nodes either as _: identifiers or as nested structures.
 * The N-Triples output will show explicit _: identifiers.
 */
object Lab7 {
  def main(args: Array[String]): Unit = {
    val Lab7DataURI = BaseDataURI + "labs/basic/lab6#"
    val m = ModelFactory.createDefaultModel()

    //Create Ontology - Properties
    val hasAddressProp = m.createProperty(BaseOntologyURI + "#hasAddress")
    hasAddressProp.addProperty(RDF.`type`, RDF.Property)

    val hasAuthorRoleProp = m.createProperty(BaseOntologyURI + "#hasAuthorRole")
    hasAuthorRoleProp.addProperty(RDF.`type`, RDF.Property)

    //Create data instances
    val personA = m.createResource(Lab7DataURI + "PersonA")
    val bNodeAddress = m.createResource()
    bNodeAddress.addProperty(VCARD.Street, "123 Main St.")
    bNodeAddress.addProperty(VCARD.Locality, "Any Town")
    bNodeAddress.addProperty(VCARD.Region, "Any State")
    bNodeAddress.addProperty(VCARD.Pcode, "12345")
    m.add(personA, hasAddressProp, bNodeAddress)

    val bookX = m.createResource(Lab7DataURI + "BookX")

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
  }
}
