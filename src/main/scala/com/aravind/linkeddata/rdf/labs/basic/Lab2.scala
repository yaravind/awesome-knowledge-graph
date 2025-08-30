package com.aravind.linkeddata.rdf.labs.basic

import com.aravind.linkeddata.rdf.JenaModels
import com.aravind.linkeddata.Constants.{BaseDataURI, BaseOntologyURI}
import org.apache.jena.rdf.model._
import org.apache.jena.vocabulary.{RDF, RDFS, VCARD, VCARD4}

/**
 * Objective: Learn how to use standard RDF/RDFS vocabularies and define your own custom classes and properties with basic RDFS metadata.
 */
object Lab2 {
  def main(args: Array[String]): Unit = {
    val NamespaceURI = BaseDataURI + "labs/basic/lab2#"
    val m = ModelFactory.createDefaultModel()

    //1. Define custom classes or reuse existing vocabularies
    val personClass = VCARD4.Individual
    val BookURI = BaseOntologyURI + "#Book"
    val bookClass = m.createResource(BookURI)
    bookClass.addProperty(RDF.`type`, RDFS.Class)
    bookClass.addProperty(RDFS.label, "Book")
    bookClass.addProperty(RDFS.comment, "A written or printed work consisting of pages bound together.")

    val hasAuthor = m.createProperty(NamespaceURI, "hasAuthor")
    hasAuthor.addProperty(RDF.`type`, RDF.Property)
    hasAuthor.addProperty(RDFS.label, "hasAuthor")
    hasAuthor.addProperty(RDFS.comment, "Authored by")

    println("Custom classes and properties: ")
    JenaModels.printAsXML(m)
    JenaModels.printAsTurtle(m)

    //2. Create data instances
    val alice = m.createResource(NamespaceURI + "Alice")
    alice.addProperty(RDF.`type`, personClass)
    alice.addProperty(VCARD.FN, "Alice Smith")

    val bob = m.createResource(NamespaceURI + "Bob")
    bob.addProperty(RDF.`type`, personClass)
    bob.addProperty(VCARD.FN, "Bob Martin")

    val book = m.createResource(NamespaceURI + "TheHobbit")
    book.addProperty(RDF.`type`, bookClass)

    val author = m.createResource(NamespaceURI + "J.R.R.Tolkien")
    author.addProperty(RDF.`type`, personClass)
    author.addProperty(VCARD.FN, "J.R.R. Tolkien")

    m.add(book, hasAuthor, author)

    println("Alice and Bob with VCARD4.INDIVIDUAL type")
    JenaModels.printAsXML(m)
    JenaModels.printAsTurtle(m)
  }
}
