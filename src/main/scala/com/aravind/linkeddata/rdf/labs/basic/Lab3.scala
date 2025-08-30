package com.aravind.linkeddata.rdf.labs.basic

import com.aravind.linkeddata.rdf.JenaModels.{printAsTurtle, printAsXML}
import com.aravind.linkeddata.Constants.{BaseDataURI, BaseOntologyURI}
import org.apache.jena.rdf.model._
import org.apache.jena.vocabulary.{RDF, RDFS, VCARD, VCARD4}

/**
 * Objective: Understand and implement rdfs:subClassOf and rdfs:subPropertyOf relationships to build more structured ontologies.
 * <p>
 * Think about real-world "is-a" and "is-part-of" relationships when designing hierarchies.
 *
 *  - rdfs:subClassOf is for "is-a" (e.g., "A Smartphone IS A CellPhone").
 *  - rdfs:subPropertyOf is for "is a more specific way of" (e.g., "hasAuthor IS A MORE SPECIFIC WAY OF contributesTo").
 *
 * </p>
 */
object Lab3 {
  def main(args: Array[String]): Unit = {
    val Lab3DataURI = BaseDataURI + "labs/basic/lab3#"

    //Ontology: Classes
    val CreativeWorkURI = BaseOntologyURI + "#CreativeWork"
    val BookURI = BaseOntologyURI + "#Book"
    val ArticleURI = BaseOntologyURI + "#Article"

    //Ontology: Properties
    val ContributionURI = BaseOntologyURI + "#contributesTo"
    val AuthorshipURI = BaseOntologyURI + "#hasAuthor"

    val m = ModelFactory.createDefaultModel()

    //1. Refine class hierarchy
    val creativeWorkClass = m.createResource(CreativeWorkURI)
    creativeWorkClass.addProperty(RDF.`type`, RDFS.Class)
    creativeWorkClass.addProperty(RDFS.label, m.createTypedLiteral("Creative work@en", RDF.dtLangString))
    creativeWorkClass.addProperty(RDFS.comment, "Class representing creative work.")

    val bookClass = m.createResource(BookURI)
    bookClass.addProperty(RDF.`type`, RDFS.Class)
    //Make Book an rdfs:subClassOf CreativeWork
    bookClass.addProperty(RDFS.subClassOf, creativeWorkClass)

    val articleClass = m.createResource(ArticleURI)
    articleClass.addProperty(RDF.`type`, RDFS.Class)
    articleClass.addProperty(RDFS.subClassOf, creativeWorkClass)

    println("1. Refine class hierarchy:")
    printAsXML(m)
    printAsTurtle(m)

    //2. Refine property hierarchy
    val contributesToProp = m.createProperty(ContributionURI)
    contributesToProp.addProperty(RDF.`type`, RDF.Property)

    val hasAuthorProp = m.createProperty(AuthorshipURI)
    hasAuthorProp.addProperty(RDF.`type`, RDF.Property)
    //Make hasAuthor an rdfs:subPropertyOf contributesTo
    hasAuthorProp.addProperty(RDFS.subPropertyOf, contributesToProp)

    println("2. Refine property hierarchy:")
    printAsXML(m)
    printAsTurtle(m)

    //3. Add domain and range
    //Define Class of Subject
    hasAuthorProp.addProperty(RDFS.domain, VCARD4.Individual)
    //Define Class of Object
    hasAuthorProp.addProperty(RDFS.range, bookClass)

    println("3. Add domain and range:")
    printAsXML(m)
    printAsTurtle(m)

    //4. Create data instances
    val book = m.createResource(Lab3DataURI + "TheHobbit")
    book.addProperty(RDF.`type`, bookClass)

    val author = m.createResource(Lab3DataURI + "J.R.R.Tolkien")
    author.addProperty(RDF.`type`, VCARD4.Individual)
    author.addProperty(VCARD.FN, "J.R.R. Tolkien")

    val article = m.createResource(Lab3DataURI + "HowToLearnAProgrammingLanguage")
    article.addProperty(RDF.`type`, articleClass)

    val authorAravind = m.createResource(Lab3DataURI + "Aravind")
    authorAravind.addProperty(RDF.`type`, VCARD4.Individual)

    //5. Add data to model
    m.add(book, hasAuthorProp, author)
    m.add(article, hasAuthorProp, authorAravind)

    println("4. Data instance:")
    printAsXML(m)
    printAsTurtle(m)
  }
}
