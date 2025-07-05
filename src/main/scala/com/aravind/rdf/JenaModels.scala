package com.aravind.rdf

import org.apache.jena.datatypes.DatatypeFormatException
import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.query.{Dataset, ResultSet}
import org.apache.jena.rdf.model._
import org.apache.jena.riot.{Lang, RDFDataMgr, RDFFormat}
import org.apache.jena.vocabulary.RDFS

import java.io.FileOutputStream
import scala.collection.JavaConverters.asScalaIteratorConverter
import scala.util.{Failure, Try}

object JenaModels {
  /**
   * Adds a validated literal to the model. If the literal is invalid for the specified datatype, it will not be added.
   *
   * @param model       The Jena model to which the literal will be added.
   * @param subjectURI  The URI of the subject resource.
   * @param propertyURI The URI of the property.
   * @param value       The value of the literal to be added.
   * @param datatype    The XSD datatype of the literal.
   * @return A Try[Unit] indicating success or failure.
   */
  def addValidatedLiteral(model: Model, subjectURI: String, propertyURI: String, value: String, datatype: XSDDatatype): Try[Unit] = {
    Try {
      // Validate the literal by attempting to parse it
      datatype.parse(value)
      model.add(model.createResource(subjectURI),
        model.createProperty(propertyURI),
        model.createTypedLiteral(value, datatype))
      //The issue is that model.add(...) returns a Model, but your function should return Try[Unit].
      //To fix this, add an explicit () after the add call so the block returns Unit.
      ()
    } recoverWith {
      case e: DatatypeFormatException =>
        println(s"Invalid literal: $value for datatype: ${datatype.getURI}")
        Failure(e)
    }
  }

  def readXml(path: String = ".", fileName: String): Option[Model] = {
    try {
      val m = RDFDataMgr.loadModel(s"$path/$fileName", Lang.RDFXML)
      if (m.isEmpty)
        None
      else
        Some(m)
    } catch {
      case e: Exception =>
        println(s"Error reading XML file: ${e.getMessage}")
        None
    }
  }

  def readTriple(path: String = ".", fileName: String): Option[Model] = {
    try {
      val m = RDFDataMgr.loadModel(s"$path/$fileName", Lang.NTRIPLES)
      if (m.isEmpty)
        None
      else
        Some(m)
    } catch {
      case e: Exception =>
        println(s"Error reading XML file: ${e.getMessage}")
        None
    }
  }

  def readTurtle(path: String = ".", fileName: String): Option[Model] = {
    try {
      val m = RDFDataMgr.loadModel(s"$path/$fileName", Lang.TURTLE)
      if (m.isEmpty)
        None
      else
        Some(m)
    } catch {
      case e: Exception =>
        println(s"Error reading XML file: ${e.getMessage}")
        None
    }
  }

  def saveAsXML(model: Model, fileName: String, path: String = "."): Unit = {
    val out = new FileOutputStream(s"$path/$fileName.rdf")
    try {
      RDFDataMgr.write(out, model, RDFFormat.RDFXML)
    } finally {
      out.close()
    }
  }

  def saveAsTriple(model: Model, fileName: String, path: String = "."): Unit = {
    val out = new FileOutputStream(s"$path/$fileName.nt")
    try {
      RDFDataMgr.write(out, model, RDFFormat.NTRIPLES)
    } finally {
      out.close()
    }
  }

  def saveAsTurtle(model: Model, fileName: String, path: String = "."): Unit = {
    val out = new FileOutputStream(s"$path/$fileName.ttl")
    try {
      RDFDataMgr.write(out, model, RDFFormat.TURTLE)
    } finally {
      out.close()
    }
  }

  def printAsXML(model: Model): Unit = {
    println("\nXML:")
    RDFDataMgr.write(System.out, model, Lang.RDFXML)
    //model.write(System.out) //, "RDF/XML"
  }

  /*
      * Prints the model in N-TRIPLE format. NTriples doesn't have any short way of writing URIs (i.e. no prefixes),
      * it takes no notice of prefixes on output and doesn't provide any on input.
     */
  def printAsTriple(model: Model): Unit = {
    println("\nN-TRIPLE:")
    RDFDataMgr.write(System.out, model, Lang.NTRIPLES)
    // model.write(System.out, "N-TRIPLE")
  }

  /*
   * Prints the model in N3 format. N3 is a more human-readable format than N-TRIPLE, it allows for prefixes and
   * has a more compact syntax.
  */
  def printAsN3(model: Model): Unit = {
    println("\nN3:")
    RDFDataMgr.write(System.out, model, Lang.N3)
    // model.write(System.out, "N-TRIPLE")
  }

  def printAsTurtle(model: Model): Unit = {
    println("\nTURTLE:")
    RDFDataMgr.write(System.out, model, Lang.TURTLE)
    //model.write(System.out, "TURTLE")
  }

  def printDatasetAsTrix(dataset: Dataset): Unit = {
    println("\nTRIX:")
    RDFDataMgr.write(System.out, dataset, Lang.TRIX)
  }

  def printDatasetAsTrig(dataset: Dataset): Unit = {
    println("\nTRIG:")
    RDFDataMgr.write(System.out, dataset, Lang.TRIG)
  }

  def printDatasetAsJSONLD11(dataset: Dataset): Unit = {
    println("\nJSONLD11:")
    RDFDataMgr.write(System.out, dataset, Lang.JSONLD11)
  }

  def printDimensions(m: Model): Unit = {
    println("Total Statements: " + m.size())
    println("Total Prefixes: " + m.numPrefixes())
  }

  /**
   * Prints the result set in a simple format with subject and object variables.
   *
   * @param rs          ResultSet to print
   * @param subjBindVar Subject bind variable name used in SPARQL query (default is "s")
   * @param objBindVar  Object variable name used in SPARQL query (default is "o")
   */
  def printSOResultSet(rs: Try[ResultSet], subjBindVar: String = "s", objBindVar: String = "o"): Unit = {
    rs match {
      case scala.util.Success(resultSet) =>
        resultSet.asScala.foreach {
          r => println(s"Subj: ${r.get(subjBindVar)}, Obj: ${r.get(objBindVar)}")
        }
      case scala.util.Failure(exception) =>
        println(s"Failed to execute query: ${exception.getMessage}")
    }
  }

  /**
   * Prints the result set in a simple format with subject, predicate and object variables.
   *
   * @param rs          ResultSet to print
   * @param subjBindVar Subject bind variable name used in SPARQL query (default is "s")
   * @param predBindVar Predicate bind variable name used in SPARQL query (default is "p")
   * @param objBindVar  Object variable name used in SPARQL query (default is "o")
   */
  def printSPOResultSet(rs: Try[ResultSet], subjBindVar: String = "s", predBindVar: String = "p", objBindVar: String = "o"): Unit = {
    rs match {
      case scala.util.Success(resultSet) =>
        resultSet.asScala.foreach {
          r => println(s"Subj: ${r.get(subjBindVar)}, Pred: ${r.get(predBindVar)}, Obj: ${r.get(objBindVar)}")
        }
      case scala.util.Failure(exception) =>
        println(s"Failed to execute query: ${exception.getMessage}")
    }
  }

  /**
   * Prints the result set in a simple format across all graphs of a Dataset with graph, subject, predicate and object variables.
   *
   * @param rs           ResultSet to print
   * @param graphBindVar Graph bind variable name used in SPARQL query (default is "g")
   * @param subjBindVar  Subject bind variable name used in SPARQL query (default is "s")
   * @param predBindVar  Predicate bind variable name used in SPARQL query (default is "p")
   * @param objBindVar   Object variable name used in SPARQL query (default is "o")
   */
  def printGSPOResultSet(rs: Try[ResultSet], graphBindVar: String = "g", subjBindVar: String = "s", predBindVar: String = "p", objBindVar: String = "o"): Unit = {
    rs match {
      case scala.util.Success(resultSet) =>
        resultSet.asScala.foreach {
          r => println(s"Subj: ${r.get(subjBindVar)}, Pred: ${r.get(predBindVar)}, Obj: ${r.get(objBindVar)}")
        }
      case scala.util.Failure(exception) =>
        println(s"Failed to execute query: ${exception.getMessage}")
    }
  }

  def printStatements(m: Model, filterBNodes: Boolean = false): Unit = {
    val stIter = m.listStatements()

    printStatements(stIter, filterBNodes)
  }

  def printStatements(stIter: StmtIterator, filterBNodes: Boolean): Unit = {
    stIter.asScala.toSeq
      .filterNot(st => filterBNodes && st.getSubject.isAnon)
      .foreach { st =>
        val s = st.getSubject
        val p = st.getPredicate
        val o = st.getObject

        //println((s.isURIResource, s.hasProperty(RDFS.label), s.isAnon))
        println(JenaModels.mkPrettyStmt(s, p, o))
      }
  }

  def mkPrettyStmt(s: Resource, p: Property, o: RDFNode, addLabel: Boolean = true): String = {

    def labelOrValue(node: RDFNode): String = {
      val labelOpt =
        Option(node)
          .filter(_ => addLabel)
          .flatMap(n =>
            if (n.isResource && n.asResource().hasProperty(RDFS.label)) {
              val label = getLabel(n)
              if (n.isAnon) Option(label + " (label) " + n.toString) else Option(label + " (label)")
            }
            else None
          )
      val base = labelOpt.getOrElse(node.toString)
      if (node.isAnon) s"$base (bnode)" else base
    }

    val subjStr = labelOrValue(s)
    val predStr = labelOrValue(p)
    val objStr = labelOrValue(o)

    s"\u001b[31m $subjStr \u001b[0m \u001b[34m--\u001b[0m \u001b[33m $predStr \u001b[0m \u001b[34m->\u001b[0m \u001b[36m $objStr \u001b[0m"
  }

  def getLabel(n: RDFNode) = {
    n.asResource().getProperty(RDFS.label).getObject.toString
  }
}
