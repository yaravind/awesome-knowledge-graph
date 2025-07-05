package com.aravind

import com.aravind.rdf.JenaModels
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.reasoner.{Reasoner, ReasonerRegistry}
import org.apache.jena.vocabulary.{RDF, RDFS, VCARD}

/**
 * @author ${user.name}
 */
object App {

  def main(args: Array[String]): Unit = {

    //1. CREATE
    val peopleModel = ModelFactory.createDefaultModel()
    val socialModel = ModelFactory.createDefaultModel()
    socialModel.createResource("https://github.com/yaravind")
      .addProperty(VCARD.EMAIL, socialModel.createResource()
        .addProperty(RDF.`type`, "Work")
        .addProperty(RDF.value, "yaravind@gmail.com"))
    /*val telephone = "+1-248-890-7565"
    val telResource = peopleModel.createResource()
      .addProperty(RDF.value, telephone)
      .addProperty(RDF.`type`, "Cell")*/

    /*val blankNodeName = peopleModel.createResource()
      .addProperty(VCARD.Given, givenName)
      .addProperty(VCARD.Family, familyName)*/

    peopleModel
      .createResource("https://github.com/yaravind")
      .addProperty(VCARD.FN, "Aravind Yarram", "te-IN") //Telugu
      .addProperty(VCARD.N, peopleModel.createResource()
        .addProperty(VCARD.Given, "Aravind")
        .addProperty(VCARD.Family, "Yarram"))
    //.addProperty(VCARD.EMAIL, "yaravind@gmail.com", "en")
    //.addProperty(VCARD.TEL, telResource)

    peopleModel.createResource("https://x.com/sama")
      .addProperty(VCARD.FN, "Sam Altman")
      .addProperty(VCARD.N, peopleModel.createResource()
        .addProperty(VCARD.Given, "Sam")
        .addProperty(VCARD.Family, "Altman"))

    //2. PRINT

    JenaModels.printAsXML(peopleModel)
    //printAsTriple(peopleModel)
    //printAsN3(peopleModel)
    //printAsTurtle(peopleModel)

    //3. SEARCH
    peopleModel.listStatements(null, VCARD.FN, null)
      .forEachRemaining(statement => {
        println(s"Found: ${statement.getSubject.getURI} - ${statement.getObject.toString}")
      })

    //4. MERGE
    val fullModel = peopleModel.union(socialModel)
    JenaModels.printAsXML(fullModel)
    JenaModels.saveAsXML(fullModel, "target", "mergedModel")
    JenaModels.saveAsTriple(fullModel, "target", "mergedModel")
    JenaModels.saveAsTurtle(fullModel, "target", "mergedModel")

    //5. Inference - Basic Reasoning
    val teacherModel = ModelFactory.createDefaultModel()
    val teachesURI = "http://aravind.com/rdf/teaches"
    val teachesAtProp = teacherModel.createProperty(teachesURI, "teachesAt")
    val worksAtProp = teacherModel.createProperty(teachesURI, "worksAt")
    teacherModel.add(teachesAtProp, RDFS.subPropertyOf, worksAtProp)
    teacherModel.createResource(teachesURI + "/Jane")
      .addProperty(teachesAtProp, "Georgia Tech")

    val r: Reasoner = ReasonerRegistry.getRDFSReasoner
    /*
    We didn't specify the triplet: Jane worksAt "Georgia Tech" but the resoner can infer it based on the subProperty relationship
     */
    val infereModel = ModelFactory.createInfModel(r, teacherModel)
    infereModel.listStatements(null, worksAtProp, null)
      .forEachRemaining(statement => {
        println(s"Inferred: ${statement.getSubject.getURI} - ${statement.getPredicate.toString} - ${statement.getObject.toString}")
      })
  }
}
