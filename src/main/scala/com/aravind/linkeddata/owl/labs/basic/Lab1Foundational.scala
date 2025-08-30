package com.aravind.linkeddata.owl.labs.basic

import com.aravind.linkeddata.Constants
import com.aravind.linkeddata.rdf.JenaModels
import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.ontology.{OntModel, OntModelSpec}
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.vocabulary.XSD

/**
 * Objective: Learn how to create a foundational OWL ontology with classes, properties, and individuals using Apache Jena.
 *
 * Learning Objectives:
 *
 *  - Understand the distinction between classes, individuals, and properties.
 *  - Learn to define basic classes and create individuals.
 *  - Define ObjectProperty and DatatypeProperty.
 *  - Assign properties to individuals.
 *
 * Enterprise Context: Imagine a small company managing its employees, departments, and projects.
 *
 */
object Lab1Foundational {
  val NS = Constants.BaseOntologyURI + "#company-ontology"

  def main(args: Array[String]): Unit = {
    val m: OntModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM)
    m.setNsPrefix("comp", NS)
    m.setNsPrefix("xsd", XSD.getURI)

    //1. Define core OWL classes
    val empClass = m.createClass(NS + "Employee")
    val deptClass = m.createClass(NS + "Department")
    val projClass = m.createClass(NS + "Project")
    println(s"Defined classes: ${empClass.getLocalName}, ${deptClass.getLocalName}, ${projClass.getLocalName}")

    //2. Create individuals (instances)
    val john = m.createIndividual(NS + "JohnDoe", empClass)
    val jane = m.createIndividual(NS + "JaneSmith", empClass)
    val peter = m.createIndividual(NS + "PeterJones", empClass)

    val itDept = m.createIndividual(NS + "Information Tech", deptClass)
    val hrDept = m.createIndividual(NS + "HumanResources", deptClass)
    val marketing = m.createIndividual(NS + "Marketing", deptClass)

    val webProj = m.createIndividual(NS + "WebRedesign", projClass)
    val newProdProj = m.createIndividual(NS + "NewProductLaunch", projClass)
    val erpProj = m.createIndividual(NS + "ERPImplementation", projClass)

    println(s"Created individuals: ${john.getLocalName}, ${jane.getLocalName}, ${peter.getLocalName}")

    //3.1 Create object properties
    val worksInProp = m.createObjectProperty(NS + "worksIn")
    worksInProp.addDomain(empClass)
    worksInProp.addRange(deptClass)

    val managesProp = m.createObjectProperty(NS + "manages")
    managesProp.addDomain(empClass)
    managesProp.addRange(projClass)
    println(s"Defined object properties: ${worksInProp.getLocalName}, ${managesProp.getLocalName}")

    //3.2 Create DataType properties
    val hasNameProp = m.createDatatypeProperty(NS + "hasName")
    hasNameProp.addDomain(empClass)
    hasNameProp.addRange(XSD.xstring)

    val hasBudgetProp = m.createDatatypeProperty(NS + "hasBudget")
    hasBudgetProp.addDomain(projClass)
    hasBudgetProp.addRange(XSD.decimal)
    println(s"Defined datatype properties: ${hasNameProp.getLocalName}, ${hasBudgetProp.getLocalName}")

    //4. Populate data
    john.addProperty(hasNameProp, "John Doe")
    jane.addProperty(hasNameProp, "Jane Smith")
    peter.addProperty(hasNameProp, "Peter Jones")

    john.addProperty(worksInProp, itDept)
    jane.addProperty(worksInProp, marketing)
    peter.addProperty(worksInProp, itDept)

    jane.addProperty(managesProp, webProj)
    john.addProperty(managesProp, erpProj)

    webProj.addProperty(hasBudgetProp, m.createTypedLiteral(150000.0, XSDDatatype.XSDdecimal))
    newProdProj.addProperty(hasBudgetProp, m.createTypedLiteral(250000.0, XSDDatatype.XSDdecimal))
    erpProj.addProperty(hasBudgetProp, m.createTypedLiteral(120000.0, XSDDatatype.XSDdecimal))

    // --- Verification (Optional) ---
    println("\n--- Verification ---")
    println(s"John Doe's name: ${john.getProperty(hasNameProp).getString}")
    println(s"Jane Smith works in: ${jane.getProperty(worksInProp).getResource.getLocalName}")
    println(s"ERP Implementation budget: ${erpProj.getProperty(hasBudgetProp).getDouble}")

    // Write the model to console in Turtle format for inspection
    println("\n--- RDF Graph (Turtle format) ---")
    JenaModels.printAsTurtle(m)
  }
}
