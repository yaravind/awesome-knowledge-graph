package com.aravind.rdf.labs.advanced

import com.aravind.rdf.labs.Constants.{BaseDataURI, BaseOntologyURI}
import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.rdf.model.{Model, ModelFactory, Property}

/**
 * Objective: Explore advanced model operations like union, intersection, difference, and chaining.
 *
 * Concepts Covered:
 *  - Model.union() for combining models
 *  - Model.intersection() for finding common triples
 *  - Model.difference() for finding unique triples in one model compared to another
 *  - Chaining operations to create complex queries
 *
 * <p>Tips:</p>
 *
 *  - These operations create new models; they do not modify the original models.
 *  - union is often used for merging data from different sources. difference can be used for change detection.
 */
object Lab19 {
  def printModel(model: Model, title: String): Unit = {
    println(s"\n--- $title (Size: ${model.size()}) ---")
    if (model.isEmpty) println("Model is empty.")
    else model.write(System.out, "TURTLE")
    println("-" * (title.length + 10))
  }

  def main(args: Array[String]): Unit = {
    println("--- Lab 19: Comparing and Merging Models ---")
    val Lab19DataURI = BaseDataURI + "labs/advanced/lab19#"

    // 1. Create Three Models
    val hasName: Property = ModelFactory.createDefaultModel().createProperty(BaseOntologyURI + "hasName")
    val hasAge: Property = ModelFactory.createDefaultModel().createProperty(BaseOntologyURI + "hasAge")
    val livesIn: Property = ModelFactory.createDefaultModel().createProperty(BaseOntologyURI + "livesIn")
    val hasEmail: Property = ModelFactory.createDefaultModel().createProperty(BaseOntologyURI + "hasEmail")

    val personA = ModelFactory.createDefaultModel().createResource(Lab19DataURI + "PersonA")
    val personB = ModelFactory.createDefaultModel().createResource(Lab19DataURI + "PersonB")
    val personC = ModelFactory.createDefaultModel().createResource(Lab19DataURI + "PersonC")

    val modelA: Model = ModelFactory.createDefaultModel()
    modelA.add(personA, hasName, "Alice")
    modelA.add(personA, hasAge, modelA.createTypedLiteral("30", XSDDatatype.XSDint))
    modelA.add(personA, livesIn, "New York")

    val modelB: Model = ModelFactory.createDefaultModel()
    modelB.add(personA, hasName, "Alice") // Common statement
    modelB.add(personA, hasEmail, "alice@example.com")
    modelB.add(personB, hasName, "Bob")

    val modelC: Model = ModelFactory.createDefaultModel()
    modelC.add(personA, hasAge, modelC.createTypedLiteral("30", XSDDatatype.XSDint)) // Common statement
    modelC.add(personC, hasName, "Charlie")

    printModel(modelA, "Model A")
    printModel(modelB, "Model B")
    printModel(modelC, "Model C")

    // 2. Perform Union
    val unionAB: Model = modelA.union(modelB)
    printModel(unionAB, "Union of A and B")

    // 3. Perform Intersection
    val intersectionAB: Model = modelA.intersection(modelB)
    printModel(intersectionAB, "Intersection of A and B") // Should contain only (PersonA hasName "Alice")

    val intersectionAC: Model = modelA.intersection(modelC)
    printModel(intersectionAC, "Intersection of A and C") // Should contain only (PersonA hasAge 30)

    // 4. Perform Difference
    val differenceAB: Model = modelA.difference(modelB)
    printModel(differenceAB, "Difference A - B (triples in A but not in B)")

    val differenceBA: Model = modelB.difference(modelA)
    printModel(differenceBA, "Difference B - A (triples in B but not in A)")

    // 5. Chaining Operations
    val chainedResult: Model = (modelA.union(modelB)).intersection(modelC)
    printModel(chainedResult, "Result of (A union B) intersection C")
  }
}