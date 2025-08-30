package com.aravind.linkeddata.rdf.labs.basic

import com.aravind.linkeddata.rdf.JenaModels
import com.aravind.linkeddata.rdf.JenaModels.{printAsTriple, printAsTurtle, printAsXML}
import com.aravind.linkeddata.Constants.BaseDataURI
import org.apache.jena.rdf.model.ModelFactory

/**
 * Objective: Learn how to persist models to files and load them back, supporting various serialization formats..
 */
object Lab10 {
  def main(args: Array[String]): Unit = {
    val NamespaceURI = BaseDataURI + "labs/basic/lab10#"
    val m = ModelFactory.createDefaultModel()

    //1. Create resources and properties
    val hasName = m.createProperty(NamespaceURI, "hasName")
    val hasAge = m.createProperty(NamespaceURI, "hasAge")
    val hasEmail = m.createProperty(NamespaceURI, "hasEmail")
    val knows = m.createProperty(NamespaceURI, "knows")

    val r1 = m.createResource(NamespaceURI + "Alice")
    val r2 = m.createResource(NamespaceURI + "Bob")

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

    //3. Provide meaningful prefix (instead of j.0) to the NS
    m.setNsPrefix("lab1", NamespaceURI)

    val path = "target"
    val fileName = "lab10"
    val filePath = s"$path/$fileName"

    println(s"Saving to files in different formats to $filePath.")
    JenaModels.saveAsXML(m, filePath)
    JenaModels.saveAsTriple(m, filePath)
    JenaModels.saveAsTurtle(m, filePath)

    println("Reading from INVALID path should return none.")
    JenaModels.readXml("non-existent", fileName + ".rdf") match {
      case Some(model) =>
        println("Loaded model from XML file successfully.")
        printAsXML(model)
      case None => println("Failed to load model from XML file.")
    }

    println("Reading RDFXML should return a model.")
    JenaModels.readXml(path, fileName + ".rdf") match {
      case Some(model) =>
        println("Loaded model from XML file successfully.")
        printAsXML(model)
      case None => println("Failed to load model from XML file.")
    }

    println("Reading Turtle should return a model.")
    JenaModels.readTurtle(path, fileName + ".ttl") match {
      case Some(model) =>
        println("Loaded model from TTL file successfully.")
        printAsTurtle(model)
      case None => println("Failed to load model from XML file.")
    }

    println("Reading Triple should return a model.")
    JenaModels.readTriple(path, fileName + ".nt") match {
      case Some(model) =>
        println("Loaded model from nt file successfully.")
        printAsTriple(model)
      case None => println("Failed to load model from XML file.")
    }

  }
}
