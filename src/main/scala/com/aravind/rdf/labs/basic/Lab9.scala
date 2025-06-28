package com.aravind.rdf.labs.basic

import com.aravind.JenaModels
import com.aravind.rdf.labs.Constants.BaseDataURI
import org.apache.jena.rdf.model.ModelFactory

/**
 * Objective: Learn how to dynamically modify an RDF model by adding, removing, and clearing statements.
 *
 * Concepts Covered:
 *
 * <li>model.add(statement)</li>
 * <li>model.remove(statement)</li>
 * <li>model.removeAll(subject, predicate, object) (pattern-based removal)</li>
 * <li>model.removeAll() (clearing the entire model)</li>
 * <li>model.contains(statement)</li>
 */
object Lab9 {
  def main(args: Array[String]): Unit = {
    val Lab9DataURI = BaseDataURI + "labs/basic/lab6#"
    val m = ModelFactory.createDefaultModel()

    //Add data instances
    val hasNameProp = m.createProperty(Lab9DataURI + "hasName")
    val hasAgeProp = m.createProperty(Lab9DataURI + "hasAge")
    val hasEmailProp = m.createProperty(Lab9DataURI + "hasEmail")
    val knowsProp = m.createProperty(Lab9DataURI + "knows")

    val personA = m.createResource(Lab9DataURI + "PersonA")
    m.add(personA, hasNameProp, "Alice")
    m.add(personA, hasAgeProp, m.createTypedLiteral(Integer.valueOf(30)))

    val personB = m.createResource(Lab9DataURI + "PersonB")
    m.add(personB, hasNameProp, "Bob")
    m.add(personB, hasAgeProp, m.createTypedLiteral(Integer.valueOf(25)))
    m.add(personB, hasEmailProp, "bob@abc.com")

    m.add(personA, knowsProp, personB)

    //Remove
    val statementToRemove = m.createStatement(personB, hasAgeProp, m.createTypedLiteral(Integer.valueOf(25)))

    JenaModels.printAsTurtle(m)

    //After removing bobs age
    m.remove(statementToRemove)
    JenaModels.printAsTurtle(m)

    //remove all statements where personA is subject
    m.removeAll(personA, null, null)
    JenaModels.printAsTurtle(m)

    println("Model contains PersonA with age? " + m.contains(personA, hasAgeProp, m.createTypedLiteral(Integer.valueOf(30))))
  }
}
