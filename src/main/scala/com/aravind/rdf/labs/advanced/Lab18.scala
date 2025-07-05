package com.aravind.rdf.labs.advanced

import com.aravind.rdf.JenaModels
import com.aravind.rdf.labs.Constants.BaseDataURI
import org.apache.jena.datatypes.DatatypeFormatException
import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.rdf.model.ModelFactory

/**
 * Objective: Explore more advanced aspects of working with XSD datatypes, including parsing, validation, and custom datatypes.
 *
 * Concepts Covered:
 *  - Literal.getDatatype() and Literal.getDatatypeURI()
 *  - Literal.getValue() for accessing Java objects
 *  - XSDDatatype.get and XSDDatatype.getURI
 *  - Handling LiteralRequiredException for invalid literals
 *  - (Optional) Brief mention of custom datatypes (though defining them fully is more advanced OWL/SHACL).
 *
 * <p>Tips:</p>
 * Jena does not throw an exception when you add an invalid typed literal (like "not-a-num" for XSDdecimal) to a model. \
 * The reason is:
 *
 *  - Jena stores RDF triples as-is: It does not validate the lexical form of literals when adding them to the model. It simply stores the value and datatype URI.
 *    Validation happens on access: An exception (like DatatypeFormatException) is only thrown when you try to access the literal's value as a Java object using getValue. This is why your code only sees errors during the iteration and not at the time of add.
 *    So, Jena defers datatype validation until the value is actually used, not when the triple is created.
 *
 */
object Lab18 {
  def main(args: Array[String]): Unit = {
    val Lab18DataURI = BaseDataURI + "labs/advanced/lab18#"

    val m = ModelFactory.createDefaultModel()

    //Valid XSD literal
    m.add(m.createResource(Lab18DataURI + "item1"),
      m.createProperty(Lab18DataURI + "price"),
      m.createTypedLiteral("100.50", XSDDatatype.XSDdecimal))

    //Invalid XSD literal
    m.add(m.createResource(Lab18DataURI + "item2"),
      m.createProperty(Lab18DataURI + "price"),
      m.createTypedLiteral("not-a-num", XSDDatatype.XSDdecimal))

    //Valid date
    m.add(m.createResource(Lab18DataURI + "event1"),
      m.createProperty(Lab18DataURI + "scheduledOn"),
      m.createTypedLiteral("2024-01-01T10:00:00Z", XSDDatatype.XSDdateTime))

    //Invalid date
    m.add(m.createResource(Lab18DataURI + "event2"),
      m.createProperty(Lab18DataURI + "scheduledOn"),
      m.createTypedLiteral("invalid-date", XSDDatatype.XSDdateTime))

    //Valid XSD literal
    m.add(m.createResource(Lab18DataURI + "personA"),
      m.createProperty(Lab18DataURI + "age"),
      m.createTypedLiteral("10", XSDDatatype.XSDinteger))

    println("Model contents in Turtle format:")
    JenaModels.printAsTurtle(m)

    println("\nIterating over statements in the model and trying to get the actual Java value:\n")
    m.listStatements().forEachRemaining { stmt =>
      println(s"Subject: ${stmt.getSubject}, Predicate: ${stmt.getPredicate}, Object: ${stmt.getObject}")
      if (stmt.getObject.isLiteral) {
        val literal = stmt.getObject.asLiteral()

        try {
          // Attempt to access the value as a Java object
          println(s"  Literal Value: ${literal.getValue}, Datatype: ${literal.getDatatypeURI}")
        } catch {
          case e: DatatypeFormatException => println(s"  Error accessing value: ${e.getMessage}")
        }
      }
    }
  }
}
