package com.aravind.rdf.labs.advanced

import com.aravind.rdf.JenaModels
import com.aravind.rdf.labs.Constants.{BaseDataURI, BaseOntologyURI}
import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.vocabulary.RDF

/**
 * Objective: Understand how to model statements about other statements using RDF reification.
 *
 * Hints/Tips:
 *
 *  - Reification creates four new triples to describe one existing triple. It does not replace the original triple.
 *  - While powerful, reification can make models verbose. Consider alternative modeling patterns
 *    (e.g., n-ary relations) if reification becomes too complex for your use case.
 */
object Lab12 {
  def main(args: Array[String]): Unit = {
    val Lab11DataURI = BaseDataURI + "labs/advanced/lab12#"

    val m = ModelFactory.createDefaultModel()

    val stmt = m.createStatement(
      m.createResource(BaseOntologyURI + "Boston"),
      m.createProperty(Lab11DataURI + "hasPopulation"),
      m.createTypedLiteral(6000000, XSDDatatype.XSDint) // Boston has a population of 6 million
    )

    m.add(stmt)

    println("Before reification:")
    JenaModels.printAsTurtle(m)

    //Reify the statement
    val reifiedStmt = m.createResource() //Shoule be a blank resource
    reifiedStmt.addProperty(RDF.`type`, RDF.Statement)
    reifiedStmt.addProperty(RDF.subject, stmt.getSubject)
    reifiedStmt.addProperty(RDF.predicate, stmt.getPredicate)
    reifiedStmt.addProperty(RDF.`object`, stmt.getObject)

    //Add properties about the reified statement:
    val yearProp = m.createProperty(BaseDataURI + "year")
    yearProp.addProperty(RDF.`type`, RDF.Property)

    val sourceProp = m.createProperty(BaseDataURI + "source")
    sourceProp.addProperty(RDF.`type`, RDF.Property)

    reifiedStmt.addProperty(sourceProp, "Wikipedia")
    reifiedStmt.addProperty(yearProp, m.createTypedLiteral(2004, XSDDatatype.XSDint))

    println("After reification:")
    JenaModels.printAsTurtle(m)
  }
}
