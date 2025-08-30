package com.aravind.linkeddata.rdf.labs.advanced

import com.aravind.linkeddata.rdf.{JenaModels, Queries}
import com.aravind.linkeddata.Constants.BaseDataURI
import org.apache.jena.query.DatasetFactory
import org.apache.jena.rdf.model.ModelFactory

/**
 * Objective: Learn how to manage multiple RDF graphs within a single Jena Dataset and query them using SPARQL GRAPH clauses.
 *
 * Concepts Covered:
 *
 *  - org.apache.jena.query.Dataset
 *  - Default Graph vs. Named Graphs
 *  - Adding models to a dataset (dataset.addNamedModel)
 *  - SPARQL GRAPH clause
 *  - SPARQL FROM and FROM NAMED
 *
 * <Hint:</p>
 *  - Named graphs are crucial for managing provenance, context, or different versions of data.
 *  - FROM and FROM NAMED can also be used in SPARQL queries to specify the graphs to query.
 *  - To select from both the DEFAULT graph and all named graphs, use UNION to combine results from the default
 * graph and the GRAPH ?g clause for named graphs
 */
object Lab17 {
  def main(args: Array[String]): Unit = {
    val Lab17DataURI = BaseDataURI + "labs/advanced/lab17#"

    val defaultGraph = ModelFactory.createDefaultModel()
    val graphA = ModelFactory.createDefaultModel()
    val graphB = ModelFactory.createDefaultModel()

    val dataset = DatasetFactory.create()

    //Add fact to default graph
    defaultGraph.add(defaultGraph.createResource(Lab17DataURI + "Vehicle"),
      defaultGraph.createProperty(Lab17DataURI + "hasColor"),
      defaultGraph.createLiteral("Black"))
    println("Default Graph:")
    JenaModels.printAsTurtle(defaultGraph)

    //ex:PersonA ex:status "Active"
    graphA.add(graphA.createResource(Lab17DataURI + "PersonA"),
      graphA.createProperty(Lab17DataURI + "status"),
      graphA.createLiteral("Active"))
    println("Graph A:")
    JenaModels.printAsTurtle(graphA)

    //ex:PersonB ex:status "Inactive" - demonstrate conflicting info across graphs
    graphB.add(graphB.createResource(Lab17DataURI + "PersonA"),
      graphB.createProperty(Lab17DataURI + "status"),
      graphB.createLiteral("Inactive"))
    println("Graph B:")
    JenaModels.printAsTurtle(graphB)

    //Add graphs to dataset
    dataset.setDefaultModel(defaultGraph)
    val GraphAName = Lab17DataURI + "GraphA"
    val GraphBName = Lab17DataURI + "GraphB"
    dataset.addNamedModel(GraphAName, graphA)
    dataset.addNamedModel(GraphBName, graphB)

    println("Dataset with Default Graph and Named Graphs:")
    //write dataset to Turtle format
    JenaModels.printDatasetAsTrix(dataset)
    JenaModels.printDatasetAsTrig(dataset)
    JenaModels.printDatasetAsJSONLD11(dataset)

    // Print the default model in Turtle format
    JenaModels.printAsTurtle(dataset.getDefaultModel)

    //Query default graph
    val queryDefaultGraph =
      """
        |SELECT ?s ?o WHERE {
        |    ?s ?p ?o .
        |}
    """.stripMargin
    println(s"Results from Default Graph:\n$defaultGraph")
    val rs1 = Queries.getResultSet(dataset, queryDefaultGraph)
    JenaModels.printSPOResultSet(rs1, "s", "p", "o")

    //Query named graph A
    val queryGraphA =
      s"""
         |SELECT ?s ?p ?o WHERE {
         |  GRAPH <$GraphAName> {
         |    ?s ?p ?o .
         |  }
         |}
      """.stripMargin
    println(s"Results from Query named graph A:\n$queryGraphA")
    val rs2 = Queries.getResultSet(dataset, queryGraphA)
    JenaModels.printSPOResultSet(rs2, "s", "p", "o")

    //Query named graph B
    val queryGraphB =
      s"""
         |SELECT ?s ?p ?o WHERE {
         |  GRAPH <$GraphBName> {
         |    ?s ?p ?o .
         |    }
         |}
      """.stripMargin
    println(s"Results from Query named graph B:\n$queryGraphB")
    val rs3 = Queries.getResultSet(dataset, queryGraphB)
    JenaModels.printSPOResultSet(rs3, "s", "p", "o")

    //Query named graph B
    val queryGraphBUsingFROM_NAMED =
      s"""
         |SELECT ?s ?p ?o
         |FROM NAMED <$GraphBName>
         |WHERE {
         |  GRAPH ?g {
         |    ?s ?p ?o .
         |    }
         |}
      """.stripMargin
    println(s"Results from Query named GraphB using FROM NAMED:\n$queryGraphBUsingFROM_NAMED")
    val rs33 = Queries.getResultSet(dataset, queryGraphBUsingFROM_NAMED)
    JenaModels.printSPOResultSet(rs33, "s", "p", "o")

    //Query Across All Named Graphs - DOESN'T include default graph:
    val queryAllNamedGraphs =
      """
        |SELECT ?g ?s ?p WHERE {
        |  GRAPH ?g {
        |    ?s ?p ?o .
        |  }
        |}
      """.stripMargin
    println(s"Results Across all Named Graph:\n$queryAllNamedGraphs")
    val rs4 = Queries.getResultSet(dataset, queryAllNamedGraphs)
    JenaModels.printGSPOResultSet(rs4, "g", "s", "p", "o")

    val queryAllGraphs =
      """
        |SELECT ?g ?s ?p ?o WHERE {
        |  {
        |    ?s ?p ?o .
        |    BIND("default" AS ?g)
        |  }
        |  UNION
        |  {
        |    GRAPH ?g {
        |      ?s ?p ?o .
        |    }
        |  }
        |}
  """.stripMargin
    println(s"Results Across all Graphs including DEFAULT graph:\n$queryAllGraphs")
    val rs5 = Queries.getResultSet(dataset, queryAllGraphs)
    JenaModels.printGSPOResultSet(rs5, "g", "s", "p", "o")
  }
}
