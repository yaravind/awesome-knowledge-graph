package com.aravind.linkeddata

import org.apache.jena.rdf.model._
import org.apache.jena.sparql.vocabulary.FOAF
import org.apache.jena.vocabulary._

import java.io.FileOutputStream
import scala.collection.JavaConverters._ // For .asScala on Java collections

object JenaToDotConverter {

  def main(args: Array[String]): Unit = {
    // 1. Create a sample Jena Model
    val model = ModelFactory.createDefaultModel()

    // Set prefixes for better readability in DOT
    val ex = "http://example.org/data#"
    val ont = "http://example.org/ontology#"
    val foaf = "http://xmlns.com/foaf/0.1/"
    val dc = "http://purl.org/dc/elements/1.1/" // Dublin Core for title

    model.setNsPrefix("ex", ex)
    model.setNsPrefix("ont", ont)
    model.setNsPrefix("foaf", foaf)
    model.setNsPrefix("dc", dc) // Set prefix for Dublin Core

    // Add some data relevant to the "Linked Research Publication Explorer" project
    val alice = model.createResource(ex + "Alice")
    val bob = model.createResource(ex + "Bob")
    val univA = model.createResource(ex + "UniversityA")
    val paper1 = model.createResource(ex + "Paper1")
    val paper2 = model.createResource(ex + "Paper2")

    // Define custom properties for the project
    val hasAuthor = model.createProperty(ont + "hasAuthor")
    val affiliatedWith = model.createProperty(ont + "affiliatedWith")
    val hasTitle = model.createProperty(ont + "hasTitle")
    val publicationYear = model.createProperty(ont + "publicationYear")
    val cites = model.createProperty(ont + "cites")

    // Define custom classes for the project
    val authorClass = model.createResource(ont + "Author")
    authorClass.addProperty(RDF.`type`, RDFS.Class)
    val publicationClass = model.createResource(ont + "Publication")
    publicationClass.addProperty(RDF.`type`, RDFS.Class)
    val journalArticleClass = model.createResource(ont + "JournalArticle")
    journalArticleClass.addProperty(RDF.`type`, RDFS.Class)
    journalArticleClass.addProperty(RDFS.subClassOf, publicationClass)

    // Add data to the model
    alice.addProperty(RDF.`type`, authorClass)
    alice.addProperty(FOAF.name, "Alice Smith")
    alice.addProperty(affiliatedWith, univA)

    bob.addProperty(RDF.`type`, authorClass)
    bob.addProperty(FOAF.name, "Bob Johnson")

    univA.addProperty(FOAF.name, "University of Academia")

    paper1.addProperty(RDF.`type`, journalArticleClass)
    paper1.addProperty(hasTitle, "A Novel Approach to Semantic Web")
    paper1.addProperty(hasAuthor, alice)
    paper1.addProperty(publicationYear, model.createTypedLiteral(Integer.valueOf(2023)))

    paper2.addProperty(RDF.`type`, publicationClass) // Generic publication
    paper2.addProperty(hasTitle, "Scalable Knowledge Graphs")
    paper2.addProperty(hasAuthor, bob)
    paper2.addProperty(publicationYear, model.createTypedLiteral(Integer.valueOf(2022)))
    paper2.addProperty(cites, paper1) // Paper 2 cites Paper 1

    // Add a blank node for a complex affiliation detail
    val deptBnode = model.createResource()
    deptBnode.addProperty(RDF.`type`, model.createResource(ont + "Department"))
    deptBnode.addProperty(FOAF.name, "Computer Science Department")
    univA.addProperty(model.createProperty(ont + "hasDepartment"), deptBnode)


    // 2. Generate DOT content from the Model
    val dotContent = convertModelToDot(model)

    // 3. Save the DOT content to a file
    val outputPath = "publication_explorer_model.dot"
    try {
      val fos = new FileOutputStream(outputPath)
      fos.write(dotContent.getBytes("UTF-8"))
      fos.close()
      println(s"Model successfully saved to $outputPath")
    } catch {
      case e: Exception => println(s"Error saving DOT file: ${e.getMessage}")
    }
  }

  /**
   * Converts a Jena Model to a DOT language string.
   *
   * @param model The Jena Model to convert.
   * @return A String containing the DOT graph definition.
   */
  def convertModelToDot(model: Model): String = {
    val sb = new StringBuilder()
    sb.append("digraph RDFModel {\n")
    sb.append("  rankdir=LR;\n") // Left to right layout
    sb.append("  node [shape=box, style=filled, fillcolor=\"#ADD8E6\", fontname=\"Helvetica\"]; // Resources\n")
    sb.append("  literal [shape=plaintext, fontname=\"Courier New\", fontsize=10, fillcolor=\"#FFFF99\"]; // Literals\n\n")

    // Map to store unique short IDs for nodes (resources and literals)
    val nodeMap = scala.collection.mutable.Map.empty[RDFNode, String]
    var nodeIdCounter = 0

    // Helper to get or create a unique ID for a node (Resource or Literal)
    def getNodeDotIdentifier(node: RDFNode): String = {
      nodeMap.getOrElseUpdate(node, {
        nodeIdCounter += 1
        val prefix = if (node.isLiteral) "lit" else if (node.isAnon) "bnode" else "res"
        s"${prefix}_$nodeIdCounter"
      })
    }

    // Collect all nodes and define them in DOT
    val allNodes = scala.collection.mutable.Set.empty[RDFNode]
    model.listStatements().asScala.foreach { stmt =>
      allNodes += stmt.getSubject
      allNodes += stmt.getObject
    }

    // Define all unique nodes with their labels
    allNodes.foreach { node =>
      val dotId = getNodeDotIdentifier(node)
      if (node.isURIResource) {
        val uri = node.asResource().getURI
        // Attempt to get a prefixed name, fallback to local name or full URI if no prefix
        val prefixedUri = model.qnameFor(uri) match {
          case qname: String => qname
          case _ =>
            val localName = uri.substring(uri.lastIndexOf('/') + 1).substring(uri.lastIndexOf('#') + 1)
            if (localName.nonEmpty) localName else uri // Use local name if available, else full URI
        }
        sb.append(s"""  "$dotId" [label="$prefixedUri"];\n""")
      } else if (node.isLiteral) {
        val literal = node.asLiteral()
        val lexicalForm = literal.getLexicalForm.replace("\"", "\\\"") // Escape quotes
        val label = if (literal.getLanguage != null && !literal.getLanguage.isEmpty) {
          "\"" + lexicalForm + "\"@" + literal.getLanguage
        } else if (literal.getDatatypeURI != null) {
          val datatypeUri = literal.getDatatypeURI
          val prefixedDatatype = model.qnameFor(datatypeUri) match {
            case qname: String => qname
            case _ => datatypeUri.substring(datatypeUri.lastIndexOf('#') + 1) // Simple local name
          }
          "\"" + lexicalForm + " \"^^" + prefixedDatatype
        } else {
          "\"" + lexicalForm + "\""
        }
        sb.append(s"""  "$dotId" [label="$label", style=filled, fillcolor="#FFFF99"];\n""") // Apply literal style
      } else if (node.isAnon) {
        // Blank nodes are represented directly by their DOT ID, with a label indicating it's a blank node
        sb.append(s"""  "$dotId" [label="_:${dotId.substring(dotId.indexOf("_") + 1)}", style=filled, fillcolor="#D3D3D3"];\n""") // Grey for blank nodes
      }
    }
    sb.append("\n")

    // Add edges (triples)
    model.listStatements().asScala.foreach { stmt =>
      val subjectDotId = getNodeDotIdentifier(stmt.getSubject)
      val objectDotId = getNodeDotIdentifier(stmt.getObject)
      val predicateUri = stmt.getPredicate.getURI
      val prefixedPredicate = model.qnameFor(predicateUri) match {
        case qname: String => qname
        case _ => predicateUri.substring(predicateUri.lastIndexOf('/') + 1).substring(predicateUri.lastIndexOf('#') + 1) // Simple local name
      }
      sb.append(s"""  "$subjectDotId" -> "$objectDotId" [label="$prefixedPredicate", fontname="Helvetica-Oblique", fontsize=10];\n""")
    }

    sb.append("}\n")
    sb.toString()
  }
}
