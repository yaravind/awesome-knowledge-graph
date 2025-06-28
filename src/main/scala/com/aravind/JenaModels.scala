package com.aravind

import org.apache.jena.query.ResultSet
import org.apache.jena.rdf.model.Model
import org.apache.jena.riot.{Lang, RDFDataMgr, RDFFormat}

import java.io.FileOutputStream
import scala.collection.JavaConverters.asScalaIteratorConverter

object JenaModels {

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

  def printDimensions(m: Model): Unit = {
    println("Total Statements: " + m.size())
    println("Total Prefixes: " + m.numPrefixes())
  }

  def printSOResultSet(rs: ResultSet, subjBindVar: String = "s", objBindVar: String = "o"): Unit = {
    rs.asScala.foreach {
      r => println(s"Subj: ${r.get(subjBindVar)}, Obj: ${r.get(objBindVar)}")
    }
  }
}
