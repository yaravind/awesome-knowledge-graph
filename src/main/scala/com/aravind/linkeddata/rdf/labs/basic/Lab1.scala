package com.aravind.linkeddata.rdf.labs.basic

import com.aravind.linkeddata.rdf.JenaModels._
import com.aravind.linkeddata.Constants.BaseDataURI
import org.apache.jena.rdf.model._

/**
 * Objective: Understand the core concepts of RDF triples and learn how to create, populate, and serialize a basic Jena Model.
 *
 * <p>
 * Expected Output:
 * <pre>
 * Alice hasName "Alice Smith"
 * Alice hasAge 30
 * Alice hasEmail alice@example.com
 *
 * Bob hasName "Bob Martin"
 * Bob hasAge 50
 * Alice knows Bob
 * </pre>
 * </p>
 */
object Lab1 {
  def main(args: Array[String]): Unit = {

    val NamespaceURI = BaseDataURI + "labs/basic/lab1#"
    val m = ModelFactory.createDefaultModel()

    //1. Create resources and properties
    val hasName = m.createProperty(NamespaceURI, "hasName")
    val hasAge = m.createProperty(NamespaceURI, "hasAge")
    val hasEmail = m.createProperty(NamespaceURI, "hasEmail")
    val knows = m.createProperty(NamespaceURI, "knows")

    val r1 = m.createResource(NamespaceURI + "Alice")
    val r2 = m.createResource(NamespaceURI + "Bob")

    println("Create statements doesn't populate the graph until add() is called. That is why the followig prints empty model.")
    printAsXML(m)
    printAsTurtle(m)

    //2. Add data instances
    val a1 = m.createStatement(r1, hasName, m.createLiteral("Alice Smith"))
    val a2 = m.createStatement(r1, hasAge, m.createTypedLiteral(Integer.valueOf(30)))
    val a3 = m.createStatement(r1, hasEmail, m.createLiteral("alice@example.com"))
    m.add(a1).add(a2).add(a3)

    val s1 = m.createStatement(r2, hasName, m.createLiteral("Bob Martin"))
    val s2 = m.createStatement(r2, hasAge, m.createTypedLiteral(Integer.valueOf(50)))
    val s3 = m.createStatement(r2, hasEmail, m.createLiteral("bob@example.com"))
    m.add(s1).add(s2).add(s3)

    val friend = m.createStatement(r1, knows, r2)
    m.add(friend)

    println("After adding statements to the model.")
    printAsXML(m)
    printAsTurtle(m)

    //3. Provide meaningful prefix (instead of j.0) to the NS
    m.setNsPrefix("lab1", NamespaceURI)

    println("After specifying custom prefix.")
    printAsXML(m)
    printAsTurtle(m)

    printDimensions(m)
  }
}
