package com.aravind.rdf.labs.advanced

import com.aravind.JenaModels
import com.aravind.rdf.labs.Constants.BaseDataURI
import com.aravind.rdf.labs.Reasoners
import com.aravind.rdf.labs.Reasoners.{SelectAllRDFType2, SelectAllRDFType3}
import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.query.{QueryExecutionFactory, ResultSet}
import org.apache.jena.rdf.model.{InfModel, ModelFactory}
import org.apache.jena.reasoner.ReasonerRegistry
import org.apache.jena.vocabulary.{RDF, RDFS, XSD}

import scala.util.Using

/**
 * Objective: Deeper dive into how `rdfs:domain`` and `rdfs:range`` statements can lead to type inference when used
 * with an RDFS reasoner.
 *
 * Hints/Tips:
 *
 *  - RDFS reasoners use `rdfs:domain` and rdfs:range` axioms to infer the `rdf:type` of subjects and objects of statements.
 *  - If (S P O) is a triple, and `P rdfs:domain C` is an axiom, then S rdf:type C` is inferred.
 *  - If (S P O) is a triple, and `P rdfs:range C` is an axiom, then O `rdf:type C` is inferred.
 *
 * Expected Output:
 *
 * The inferred model will contain `rdf:type` statements for individuals that were not explicitly typed in the base model,
 * demonstrating the power of domain/range inference.
 */
object Lab11 {
  def main(args: Array[String]): Unit = {
    val Lab11DataURI = BaseDataURI + "labs/advanced/lab11#"

    val m = ModelFactory.createDefaultModel()

    //1. Create Ontology - Classes
    val employeeClass = m.createResource(BaseDataURI + "Employee")
    employeeClass.addProperty(RDF.`type`, RDFS.Class)
    val deptClass = m.createResource(BaseDataURI + "Department")
    deptClass.addProperty(RDF.`type`, RDFS.Class)
    val projectClass = m.createResource(BaseDataURI + "Project")
    projectClass.addProperty(RDF.`type`, RDFS.Class)

    //2. Create Ontology - Properties

    //ex:worksFor (ObjectProperty)
    val worksForProp = m.createProperty(BaseDataURI + "worksFor")
    worksForProp.addProperty(RDF.`type`, RDF.Property)
    worksForProp.addProperty(RDFS.domain, employeeClass)
    worksForProp.addProperty(RDFS.range, deptClass) //ObjectProperty
    //ex:manages (ObjectProperty)
    val managesProp = m.createProperty(BaseDataURI + "manages")
    managesProp.addProperty(RDF.`type`, RDF.Property)
    managesProp.addProperty(RDFS.domain, employeeClass)
    managesProp.addProperty(RDFS.range, projectClass) //Defines that an employee manages a project
    //ex:hasBudget (DatatypeProperty)
    val hasBudgetProp = m.createProperty(BaseDataURI + "hasBudget")
    hasBudgetProp.addProperty(RDF.`type`, RDF.Property)
    hasBudgetProp.addProperty(RDFS.domain, projectClass)
    hasBudgetProp.addProperty(RDFS.range, XSD.decimal) //DatatypeProperty

    println("RDFS Schema:")
    //JenaModels.printAsTurtle(m)

    //3. Create data instances
    val alice = m.createResource(Lab11DataURI + "Alice")
    val hrDept = m.createResource(Lab11DataURI + "HR_Dept")
    val projectX = m.createResource(Lab11DataURI + "ProjectX")

    m.add(alice, worksForProp, hrDept) // Alice works for HR Department
    m.add(alice, managesProp, projectX) // Alice manages ProjectX
    m.add(projectX, hasBudgetProp, m.createTypedLiteral(50000.0, XSDDatatype.XSDdecimal)) // ProjectX has a budget of 50000.0

    println("Data Instances:")
    JenaModels.printAsTurtle(m)

    //4. Infer types using RDFS reasoner
    val reasoner = ReasonerRegistry.getRDFSReasoner
    val inferredModel: InfModel = ModelFactory.createInfModel(reasoner, m)

    val rs1: ResultSet = Reasoners.getResultSet(inferredModel, SelectAllRDFType2)
    print(
      """Inferred Types (after applying rdfs:domain and rdfs:range):

         You should see that ex:Alice is inferred as ex:Employee, ex:HR_Dept as ex:Department,
         and ex:ProjectX as ex:Project, solely due to the rdfs:domain and
         rdfs:range axioms and the explicit property usage.""")
    println()
    JenaModels.printSOResultSet(rs1)

    println("All data")

    val rs2: ResultSet = Reasoners.getResultSet(m, SelectAllRDFType3)
    println()
    JenaModels.printSOResultSet(rs2)

    println("\n--- Querying Inferred Model for rdf:type statements ---")
    val queryString =
      s"""PREFIX rdf: <${RDF.getURI}>
         |SELECT ?subject ?type WHERE { ?subject rdf:type ?type }""".stripMargin
    val rs3: ResultSet = Reasoners.getResultSet(m, queryString)
    println()

    Using(QueryExecutionFactory.create(queryString, inferredModel)) { qexec =>
      println("execSelect on inferred model")
      val results = qexec.execSelect()

      while (rs3.hasNext) {
        val soln = results.nextSolution()
        val subject = soln.getResource("subject")
        val typeRes = soln.getResource("type")

        // Filter to show only types of our specific individuals of interest
        if (subject.equals(alice) || subject.equals(hrDept) || subject.equals(projectX)) {
          val statement = inferredModel.createStatement(subject, RDF.`type`, typeRes)
          // Check if this statement was inferred
          val isInferred = if (m.contains(statement)) "(explicit)" else "(inferred)"
          println(f"Found type: $subject rdf:type $typeRes $isInferred%-12s")
        }
      }
    }.failed.foreach { ex =>
      println(s"Exception during query execution: ${ex.getMessage}")
    }

    println("\n--- Explanation of Inferences ---")
    println("1. (data:Alice rdf:type ex:Employee) was inferred because:")
    println("   - 'data:Alice' is the subject of 'ex:worksFor', which has 'rdfs:domain ex:Employee'.")
    println("   - 'data:Alice' is the subject of 'ex:manages', which also has 'rdfs:domain ex:Employee'.")
    println("2. (data:HR_Dept rdf:type ex:Department) was inferred because:")
    println("   - 'data:HR_Dept' is the object of 'ex:worksFor', which has 'rdfs:range ex:Department'.")
    println("3. (data:ProjectX rdf:type ex:Project) was inferred because:")
    println("   - 'data:ProjectX' is the object of 'ex:manages', which has 'rdfs:range ex:Project'.")
    println("   - 'data:ProjectX' is the subject of 'ex:hasBudget', which has 'rdfs:domain ex:Project'.")
  }
}
