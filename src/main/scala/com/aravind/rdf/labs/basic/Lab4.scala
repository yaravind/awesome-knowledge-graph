package com.aravind.rdf.labs.basic

import com.aravind.JenaModels
import com.aravind.rdf.labs.Constants.{BaseDataURI, BaseOntologyURI}
import org.apache.jena.rdf.model._
import org.apache.jena.reasoner.{Reasoner, ReasonerRegistry}
import org.apache.jena.vocabulary.{RDF, RDFS, VCARD, VCARD4}

import scala.collection.JavaConverters._ // For converting Java iterators to Scala collections

/**
 * Objective: Understand how RDFS inference rules can be applied to a model to derive new, implicit facts.
 *
 * Concepts covered:
 *
 * <ul>
 * <li>RDFS Inference Rules (specifically `rdfs:subClassOf` and `rdfs:subPropertyOf` rules)</li>
 * <li>`org.apache.jena.reasoner.Reasoner`</li>
 * <li>`org.apache.jena.rdf.model.InfModel`</li>
 * <li>Distinguishing between explicit and inferred triples.</li>
 * </ul>
 *
 * Notes:
 *
 * <ul>
 * <li>The InfModel does not change the baseModel. It provides a view that includes both explicit and inferred triples.</li>
 * <li>The `if(!baseModel.contains(stmt))` check is crucial for identifying which triples were newly inferred.</li>
 * </ul>
 */
object Lab4 {
  val Lab4DataURI = BaseDataURI + "labs/basic/lab4#"

  // 1. Create a Base RDF Model (the explicit facts)
  val baseModel: Model = ModelFactory.createDefaultModel()

  def main(args: Array[String]): Unit = {
    // Set prefixes for better readability
    baseModel.setNsPrefix("device", BaseOntologyURI)
    baseModel.setNsPrefix("rdf", RDF.getURI)
    baseModel.setNsPrefix("rdfs", RDFS.getURI)
    baseModel.setNsPrefix("vcard", VCARD4.getURI)

    // Ontology Classes
    val DeviceURI = BaseOntologyURI + "#Device"
    val CellPhoneURI = BaseOntologyURI + "#CellPhone"
    val SmartPhoneURI = BaseOntologyURI + "#SmartPhone"

    val personClass: Resource = VCARD4.Individual // Using VCARD for person
    val deviceClass: Resource = baseModel.createResource(DeviceURI)
    deviceClass.addProperty(RDF.`type`, RDFS.Class)

    val cellPhoneClass: Resource = baseModel.createResource(CellPhoneURI)
    cellPhoneClass.addProperty(RDF.`type`, RDFS.Class)
    // rdfs:subClassOf inference rule: If X subClassOf Y, and A type X, then A type Y
    cellPhoneClass.addProperty(RDFS.subClassOf, deviceClass)

    val smartPhoneClass: Resource = baseModel.createResource(SmartPhoneURI)
    smartPhoneClass.addProperty(RDF.`type`, RDFS.Class)
    // rdfs:subClassOf inference rule: If X subClassOf Y, and A type X, then A type Y
    smartPhoneClass.addProperty(RDFS.subClassOf, cellPhoneClass)

    val hasDevicePropURI = BaseOntologyURI + "#hasDevice"
    //  //Ontology: Properties and their hierarchy
    val hasDeviceProp: Property = baseModel.createProperty(hasDevicePropURI)
    hasDeviceProp.addProperty(RDF.`type`, RDF.Property)

    val hasCellPhonePropURI = BaseOntologyURI + "#hasCellPhone"
    val hasCellPhoneProp: Property = baseModel.createProperty(hasCellPhonePropURI)
    hasCellPhoneProp.addProperty(RDF.`type`, RDF.Property)
    // rdfs:subPropertyOf inference rule: If P subPropertyOf Q, and S P O, then S Q O
    hasCellPhoneProp.addProperty(RDFS.subPropertyOf, hasDeviceProp)

    // Create Individuals and Explicit Assertions
    val johnDoe: Resource = baseModel.createResource(Lab4DataURI + "JohnDoe")
    johnDoe.addProperty(RDF.`type`, personClass)
    johnDoe.addProperty(VCARD.FN, "John Doe")

    val johnsPhone: Resource = baseModel.createResource(Lab4DataURI + "JohnsPixelPhone")
    // Explicitly state that John's Phone is a Smartphone
    johnsPhone.addProperty(RDF.`type`, smartPhoneClass)

    // Explicitly state that John Doe has John's Phone
    johnDoe.addProperty(hasCellPhoneProp, johnsPhone)

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
    println("\nExample 1: rdfs:subClassOf inference - Querying for resources of type 'CellPhone' in inferred model:")
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
    println("\nExample 2: rdfs:subPropertyOf inference - Querying for resources that 'hasDevice' in inferred model:")
    val hasDeviceStatements: StmtIterator = inferredModel.listStatements(null, hasDeviceProp, null.asInstanceOf[RDFNode])
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

