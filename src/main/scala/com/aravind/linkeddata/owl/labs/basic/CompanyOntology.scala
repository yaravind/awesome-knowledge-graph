package com.aravind.linkeddata.owl.labs.basic

import com.aravind.linkeddata.Constants
import org.apache.jena.ontology.{OntModel, OntModelSpec}
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.vocabulary.XSD

object CompanyOntology {

  val CompanyOntologyNS = Constants.BaseOntologyURI + "#company-ontology"

  val CompanyModel: OntModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM)
  CompanyModel.setNsPrefix("comp", CompanyOntologyNS)
  CompanyModel.setNsPrefix("xsd", XSD.getURI)

  //1. Define core OWL classes
  val empClass = CompanyModel.createClass(CompanyOntologyNS + "Employee")
  val deptClass = CompanyModel.createClass(CompanyOntologyNS + "Department")
  val projClass = CompanyModel.createClass(CompanyOntologyNS + "Project")

  //3.1 Create object properties
  val worksInProp = CompanyModel.createObjectProperty(CompanyOntologyNS + "worksIn")
  worksInProp.addDomain(empClass)
  worksInProp.addRange(deptClass)

  val managesProp = CompanyModel.createObjectProperty(CompanyOntologyNS + "manages")
  managesProp.addDomain(empClass)
  managesProp.addRange(projClass)

  //3.2 Create DataType properties
  val hasNameProp = CompanyModel.createDatatypeProperty(CompanyOntologyNS + "hasName")
  hasNameProp.addDomain(empClass)
  hasNameProp.addRange(XSD.xstring)

  val hasBudgetProp = CompanyModel.createDatatypeProperty(CompanyOntologyNS + "hasBudget")
  hasBudgetProp.addDomain(projClass)
  hasBudgetProp.addRange(XSD.decimal)

}
