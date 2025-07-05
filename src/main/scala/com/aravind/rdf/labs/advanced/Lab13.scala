package com.aravind.rdf.labs.advanced

import com.aravind.rdf.JenaModels
import com.aravind.rdf.labs.Constants.{BaseDataURI, BaseOntologyURI}
import org.apache.jena.rdf.model.ModelFactory

/**
 * Objective: Learn how to represent ordered and unordered collections of resources or literals using RDF containers.
 *
 * Concepts Covered:
 *  - rdf:Bag (unordered, allows duplicates)
 *  - rdf:Seq (ordered, allows duplicates)
 *  - rdf:Alt (ordered, for alternatives)
 *  - rdf:_1, rdf:_2, etc. (member properties)
 *  - model.createBag(), model.createSeq(), model.createAlt()
 *  - Adding members (container.add(resourceOrLiteral))
 *
 * <p>Hint:</p>
 *
 * -nrdf:List (using rdf:first and rdf:rest) is another way to represent ordered lists, often preferred
 * for more complex list operations, but containers are simpler for basic collections.
 */
object Lab13 {
  def main(args: Array[String]): Unit = {
    val Lab13DataURI = BaseDataURI + "labs/advanced/lab13#"

    val m = ModelFactory.createDefaultModel()

    //Person interests
    val personA = m.createResource(Lab13DataURI + "PersonA")
    val interestsProp = m.createProperty(BaseOntologyURI + "hasInterest")

    val interestContainer = m.createBag()
    interestContainer.add(m.createLiteral("Reading"))
    interestContainer.add(m.createLiteral("Reading")) //Bag allows duplicate
    interestContainer.add(m.createLiteral("Hiking"))

    personA.addProperty(interestsProp, interestContainer)
    println("Person A's interests (Bag):")
    JenaModels.printAsTurtle(m)

    //Recipe has sequence of steps - ordered collection: Seq
    //Seq allows duplicates, but is ordered
    val recipeA = m.createResource(Lab13DataURI + "RecipeA")
    val stepsProp = m.createProperty(BaseOntologyURI + "hasStep")
    val stepsContainer = m.createSeq()
    stepsContainer.add(m.createLiteral("Step 1"))
    stepsContainer.add(m.createLiteral("Step 2"))
    stepsContainer.add(m.createLiteral("Step 2"))
    recipeA.addProperty(stepsProp, stepsContainer)

    println("Recipe A's steps (Seq):")
    JenaModels.printAsTurtle(m)

    //Ordered - Alternatives
    val personB = m.createResource(Lab13DataURI + "PersonB")
    val preferredContactProp = m.createProperty(BaseOntologyURI + "preferredContactMethod")
    val contactContainer = m.createAlt()
    contactContainer.add(m.createLiteral("Email"))
    contactContainer.add(m.createLiteral("Phone"))
    personB.addProperty(preferredContactProp, contactContainer)

    println("Person B's preferred contact methods (Alt):")
    JenaModels.printAsTurtle(m)
  }
}
