package com.aravind.linkeddata.owl.labs.basic

import com.aravind.linkeddata.owl.labs.basic.CompanyOntology._
import com.aravind.linkeddata.rdf.JenaModels
import org.apache.jena.datatypes.xsd.XSDDatatype

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

  def main(args: Array[String]): Unit = {

    println(s"Defined classes: ${empClass.getLocalName}, ${deptClass.getLocalName}, ${projClass.getLocalName}")
    println(s"Defined object properties: ${worksInProp.getLocalName}, ${managesProp.getLocalName}")
    println(s"Defined datatype properties: ${hasNameProp.getLocalName}, ${hasBudgetProp.getLocalName}")

    //2. Create individuals (instances)
    val john = CompanyModel.createIndividual(CompanyOntologyNS + "JohnDoe", empClass)
    val jane = CompanyModel.createIndividual(CompanyOntologyNS + "JaneSmith", empClass)
    val peter = CompanyModel.createIndividual(CompanyOntologyNS + "PeterJones", empClass)

    val itDept = CompanyModel.createIndividual(CompanyOntologyNS + "Information Tech", deptClass)
    val hrDept = CompanyModel.createIndividual(CompanyOntologyNS + "HumanResources", deptClass)
    val marketing = CompanyModel.createIndividual(CompanyOntologyNS + "Marketing", deptClass)

    val webProj = CompanyModel.createIndividual(CompanyOntologyNS + "WebRedesign", projClass)
    val newProdProj = CompanyModel.createIndividual(CompanyOntologyNS + "NewProductLaunch", projClass)
    val erpProj = CompanyModel.createIndividual(CompanyOntologyNS + "ERPImplementation", projClass)

    println(s"Created individuals: ${john.getLocalName}, ${jane.getLocalName}, ${peter.getLocalName}")

    //4. Populate data
    john.addProperty(hasNameProp, "John Doe")
    jane.addProperty(hasNameProp, "Jane Smith")
    peter.addProperty(hasNameProp, "Peter Jones")

    john.addProperty(worksInProp, itDept)
    jane.addProperty(worksInProp, marketing)
    peter.addProperty(worksInProp, itDept)

    jane.addProperty(managesProp, webProj)
    john.addProperty(managesProp, erpProj)

    webProj.addProperty(hasBudgetProp, CompanyModel.createTypedLiteral(150000.0, XSDDatatype.XSDdecimal))
    newProdProj.addProperty(hasBudgetProp, CompanyModel.createTypedLiteral(250000.0, XSDDatatype.XSDdecimal))
    erpProj.addProperty(hasBudgetProp, CompanyModel.createTypedLiteral(120000.0, XSDDatatype.XSDdecimal))

    // --- Verification (Optional) ---
    println("\n--- Verification ---")
    println(s"John Doe's name: ${john.getProperty(hasNameProp).getString}")
    println(s"Jane Smith works in: ${jane.getProperty(worksInProp).getResource.getLocalName}")
    println(s"ERP Implementation budget: ${erpProj.getProperty(hasBudgetProp).getDouble}")

    // Write the model to console in Turtle format for inspection
    println("\n--- RDF Graph (Turtle format) ---")
    JenaModels.printAsTurtle(CompanyModel)
  }

}
