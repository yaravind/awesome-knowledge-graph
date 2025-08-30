package com.aravind.linkeddata.rdf.labs.basic

import com.aravind.linkeddata.rdf.JenaModels
import com.aravind.linkeddata.Constants.{BaseDataURI, BaseOntologyURI}
import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.vocabulary.{RDF, RDFS}

import scala.collection.JavaConverters.asScalaIteratorConverter

/**
 * Objective: Understand how to use different XML Schema (XSD) datatypes and language tags with RDF literals.
 *
 * Concepts covered:
 *
 * <ul>
 * <li>org.apache.jena.datatypes.xsd.XSDDatatype</li>
 * <li>Creating typed literals (e.g., xsd:integer, xsd:dateTime, xsd:boolean)</li>
 * <li>Creating literals with language tags (e.g., @en, @fr)</li>
 * <li>Retrieving literal values and their datatypes/language tags.</li>
 * </ul>
 */
object Lab6 {
  def main(args: Array[String]): Unit = {
    val Lab6DataURI = BaseDataURI + "labs/basic/lab6#"
    val m = ModelFactory.createDefaultModel()

    //Create Ontology - Classes & Properties
    val productClass = m.createResource(BaseOntologyURI + "#Product")
    productClass.addProperty(RDF.`type`, RDFS.Class)

    val hasPriceProp = m.createProperty(BaseOntologyURI + "#hasPrice")
    hasPriceProp.addProperty(RDF.`type`, RDF.Property)

    val releaseDateProp = m.createProperty(BaseOntologyURI + "#releaseDate")
    releaseDateProp.addProperty(RDF.`type`, RDF.Property)

    val isInStockProp = m.createProperty(BaseOntologyURI + "#isInStock")
    isInStockProp.addProperty(RDF.`type`, RDF.Property)

    val quantityProp = m.createProperty(BaseOntologyURI + "#quantity")
    quantityProp.addProperty(RDF.`type`, RDF.Property)

    //Create data instances
    val productX = m.createResource(Lab6DataURI + "ProductX")
    productX.addProperty(RDF.`type`, productClass)

    m.add(productX, hasPriceProp, m.createTypedLiteral(99.99, XSDDatatype.XSDdecimal))
    m.add(productX, releaseDateProp, m.createTypedLiteral("2023-10-26T14:30:00Z", XSDDatatype.XSDdateTime))
    m.add(productX, isInStockProp, m.createTypedLiteral(true, XSDDatatype.XSDboolean))
    m.add(productX, quantityProp, m.createTypedLiteral(150, XSDDatatype.XSDint))

    //Add literals with language tags
    productX.addProperty(RDFS.label, "Super Gadget", "en")
    productX.addProperty(RDFS.label, "Super Gadget in French", "fr")
    JenaModels.printAsTurtle(m)

    //Iterate and print all literals
    m.listStatements().asScala.foreach(stmt => {
      val o = stmt.getObject
      if (o.isLiteral) {
        val l = o.asLiteral()
        println()
        println("Lexical form: " + l.getLexicalForm)
        println("Datatype URI: " + l.getDatatypeURI)
        println("Lang: " + l.getLanguage)
        println("Val: " + l.getValue)

      }
    })
  }
}
