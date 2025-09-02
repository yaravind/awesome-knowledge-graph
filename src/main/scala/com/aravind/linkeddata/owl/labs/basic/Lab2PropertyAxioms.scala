package com.aravind.linkeddata.owl.labs.basic

import com.aravind.linkeddata.owl.labs.basic.CompanyOntology._
import com.aravind.linkeddata.rdf.JenaModels
import org.apache.jena.vocabulary.{OWL2, RDF}

object Lab2PropertyAxioms {
  def main(args: Array[String]): Unit = {
    //Define properties
    val supervisorProp = CompanyModel.createObjectProperty(CompanyOntologyNS + "isSupervisorOf")
    supervisorProp.addDomain(CompanyOntology.empClass)
    supervisorProp.addRange(CompanyOntology.empClass)
    //If A supervises B, and B supervises C, then A supervises C
    supervisorProp.addProperty(RDF.`type`, OWL2.TransitiveProperty)
    //An employee cannot supervise themselves
    supervisorProp.addProperty(RDF.`type`, OWL2.IrreflexiveProperty)
    println(s"Defined property with axioms: ${supervisorProp.getLocalName}: Transitive: ${supervisorProp.isTransitiveProperty}, Irreflexive: ${JenaModels.isIrreflexiveProperty(supervisorProp)}")

    val hasDirectSupervisorProp = CompanyModel.createObjectProperty(CompanyOntologyNS + "hasDirectSupervisor")
    hasDirectSupervisorProp.addDomain(empClass)
    hasDirectSupervisorProp.addRange(empClass)
    //An employee has at most one direct supervisor
    hasDirectSupervisorProp.addProperty(RDF.`type`, OWL2.FunctionalProperty)
    println(s"Defined ${hasDirectSupervisorProp.getLocalName} as Functional.")

    val reportsToProp = CompanyModel.createObjectProperty(CompanyOntologyNS + "reportsTo")
    reportsToProp.addDomain(empClass)
    reportsToProp.addRange(empClass)
    reportsToProp.addInverseOf(hasDirectSupervisorProp)
    println(s"Defined ${reportsToProp.getLocalName} as inverse of ${hasDirectSupervisorProp.getLocalName}.")

    //Add a restriction to DepartmentHead stating that every DepartmentHead must have a direct supervisor (use owl:someValuesFrom with hasDirectSupervisor).
    val hasDirectSupervisorRest = CompanyModel.createRestriction(hasDirectSupervisorProp)
    //OWL2.someValuesFrom means "must have at least one value of a certain type."
    hasDirectSupervisorRest.addProperty(OWL2.someValuesFrom, empClass)
    deptHeadClass.addSuperClass(hasDirectSupervisorRest)


    //Add a restriction to DepartmentHead stating that a DepartmentHead supervises at least one Employee (use owl:minCardinality with isSupervisorOf).

    //Create individuals
    val johnDoe = CompanyModel.createIndividual(CompanyOntologyNS + "JohnDoe", empClass)
    val janeSmith = CompanyModel.createIndividual(CompanyOntologyNS + "JaneSmith", empClass)
    val peterJones = CompanyModel.createIndividual(CompanyOntologyNS + "PeterJones", empClass)
    val aliceBrown = CompanyModel.createIndividual(CompanyOntologyNS + "AliceBrown", empClass) // New for supervision chain
    val bobWhite = CompanyModel.createIndividual(CompanyOntologyNS + "BobWhite", empClass) // New for supervision chain

    johnDoe.addProperty(hasNameProp, "John Doe")
    janeSmith.addProperty(hasNameProp, "Jane Smith")
    peterJones.addProperty(hasNameProp, "Peter Jones")
    aliceBrown.addProperty(hasNameProp, "Alice Brown")
    bobWhite.addProperty(hasNameProp, "Bob White")

    val engineering = CompanyModel.createIndividual(CompanyOntologyNS + "Engineering", deptClass)
    val websiteRedesign = CompanyModel.createIndividual(CompanyOntologyNS + "WebsiteRedesign", projClass)

    johnDoe.addProperty(worksInProp, engineering)
    janeSmith.addProperty(managesProp, websiteRedesign)

  }
}
