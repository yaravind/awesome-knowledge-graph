package com.aravind

import org.apache.jena.rdf.model._
import org.apache.jena.reasoner.{Reasoner, ReasonerRegistry}
import org.apache.jena.vocabulary.{RDF, RDFS, VCARD, VCARD4}

import scala.collection.JavaConverters._ // For converting Java iterators to Scala collections

object RDFSReasonerExample {

  def main(args: Array[String]): Unit = {
    // Define a base URI for our custom vocabulary
    val baseURI = "http://example.org/vocabulary/device#"

    // 1. Create a Base RDF Model (the explicit facts)
    val baseModel: Model = ModelFactory.createDefaultModel()

    // Set prefixes for better readability
    baseModel.setNsPrefix("device", baseURI)
    baseModel.setNsPrefix("rdf", RDF.getURI)
    baseModel.setNsPrefix("rdfs", RDFS.getURI)
    baseModel.setNsPrefix("vcard", VCARD4.getURI)

    // Define RDFS Classes and their hierarchy
    val personClass: Resource = VCARD4.Individual // Using VCARD for person
    val deviceClass: Resource = baseModel.createResource(baseURI + "Device")
    deviceClass.addProperty(RDF.`type`, RDFS.Class)

    val cellPhoneClass: Resource = baseModel.createResource(baseURI + "CellPhone")
    cellPhoneClass.addProperty(RDF.`type`, RDFS.Class)
    // rdfs:subClassOf inference rule: If X subClassOf Y, and A type X, then A type Y
    cellPhoneClass.addProperty(RDFS.subClassOf, deviceClass)

    val smartPhoneClass: Resource = baseModel.createResource(baseURI + "Smartphone")
    smartPhoneClass.addProperty(RDF.`type`, RDFS.Class)
    // rdfs:subClassOf inference rule: If X subClassOf Y, and A type X, then A type Y
    smartPhoneClass.addProperty(RDFS.subClassOf, cellPhoneClass)

    // Define RDFS Properties and their hierarchy
    val hasDeviceProperty: Property = baseModel.createProperty(baseURI + "hasDevice")
    hasDeviceProperty.addProperty(RDF.`type`, RDF.Property)

    val hasCellPhoneProperty: Property = baseModel.createProperty(baseURI + "hasCellPhone")
    hasCellPhoneProperty.addProperty(RDF.`type`, RDF.Property)
    // rdfs:subPropertyOf inference rule: If P subPropertyOf Q, and S P O, then S Q O
    hasCellPhoneProperty.addProperty(RDFS.subPropertyOf, hasDeviceProperty)

    // Create Individuals and Explicit Assertions
    val johnDoe: Resource = baseModel.createResource(baseURI + "JohnDoe")
    johnDoe.addProperty(RDF.`type`, personClass)
    johnDoe.addProperty(VCARD.FN, "John Doe")

    val johnsPhone: Resource = baseModel.createResource(baseURI + "JohnsPhone")
    // Explicitly state that John's Phone is a Smartphone
    johnsPhone.addProperty(RDF.`type`, smartPhoneClass)

    // Explicitly state that John Doe has John's Phone
    johnDoe.addProperty(hasCellPhoneProperty, johnsPhone)

    // Print the Base Model
    println("--- Base Model (Explicit Facts) ---")
    JenaModels.printAsTriple(baseModel)
    //baseModel.write(System.out, "TURTLE") // Turtle is often more readable for RDFS
    println(s"\nNumber of triples in Base Model: ${baseModel.size()}")
    println("-----------------------------------\n")


    // 2. Create an RDFS Reasoner
    // Get the standard RDFS reasoner from Jena's ReasonerRegistry
    val reasoner: Reasoner = ReasonerRegistry.getRDFSReasoner

    // 3. Create an InfModel (Inferred Model)
    // This model combines the base model with the reasoner's inferred triples
    val inferredModel: InfModel = ModelFactory.createInfModel(reasoner, baseModel)

    // Print the Inferred Model
    println("--- Inferred Model (Explicit + Inferred Facts) ---")
    JenaModels.printAsTriple(inferredModel)
    //inferredModel.write(System.out, "TURTLE")
    println(s"\nNumber of triples in Inferred Model: ${inferredModel.size()}")
    println("--------------------------------------------------\n")

    // 4. Demonstrate Inference: Find new triples
    println("--- Demonstrating Inferred Triples ---")

    // Example 1: rdfs:subClassOf inference
    // Query for resources of type CellPhone
    println("\nQuerying for resources of type 'CellPhone' in inferred model:")
    val cellPhoneTypes: StmtIterator = inferredModel.listStatements(null, RDF.`type`, cellPhoneClass)
    // Using JavaConverters for more idiomatic Scala iteration
    cellPhoneTypes.asScala.foreach { stmt =>
      println(s"  Inferred: ${stmt.getSubject.getLocalName} is a ${stmt.getObject.asResource.getLocalName}")
      // Check if this specific triple was NOT in the base model
      if (!baseModel.contains(stmt)) {
        println("    (This triple was inferred, not explicit in base model)")
      }
    }
    // In this case, "JohnsPhone" was explicitly a Smartphone,
    // but inferred to be a CellPhone (because Smartphone subClassOf CellPhone)

    // Example 2: rdfs:subPropertyOf inference
    // Query for resources that 'hasDevice'
    println("\nQuerying for resources that 'hasDevice' in inferred model:")
    val hasDeviceStatements: StmtIterator = inferredModel.listStatements(null, hasDeviceProperty, null.asInstanceOf[RDFNode])
    hasDeviceStatements.asScala.foreach { stmt =>
      println(s"  Inferred: ${stmt.getSubject.getLocalName} ${stmt.getPredicate.getLocalName} ${stmt.getObject.asResource.getLocalName}")
      // Check if this specific triple was NOT in the base model
      if (!baseModel.contains(stmt)) {
        println("    (This triple was inferred, not explicit in base model)")
      }
    }
    // In this case, "JohnDoe" explicitly 'hasCellPhone' "JohnsPhone",
    // but inferred to 'hasDevice' "JohnsPhone" (because hasCellPhone subPropertyOf hasDevice)

    println("--------------------------------------")
  }
}

