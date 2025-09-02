# Advanced Labs for RDF and RDFS Modeling with Apache Jena (Scala)

[Lab 11: Advanced RDFS Reasoning (Domain/Range Inference)    15](#lab-11:-advanced-rdfs-reasoning-\(domain/range-inference\))

[Lab 12: Reification (rdf:Statement)    17](#lab-12:-reification-\(rdf:statement\))

[Lab 13: RDF Collections (rdf:Bag, rdf:Seq, rdf:Alt)    18](#lab-13:-rdf-collections-\(rdf:bag,-rdf:seq,-rdf:alt\))

[Lab 14: Property Functions and Filters in SPARQL	19](#lab-14:-property-functions-and-filters-in-sparql)

[Lab 15: SPARQL CONSTRUCT and DESCRIBE Queries	21](#lab-15:-sparql-construct-and-describe-queries)

[Lab 16: SPARQL UPDATE Operations	23](#lab-16:-sparql-update-operations)

[Lab 17: Working with Named Graphs (Datasets)    25](#lab-17:-working-with-named-graphs-\(datasets\))

[Lab 18: Advanced Datatype Handling	26](#lab-18:-advanced-datatype-handling)

[Lab 19: Comparing and Merging Models	28](#lab-19:-comparing-and-merging-models)

[Lab 20: Error Handling and Best Practices	29](#lab-20:-error-handling-and-best-practices)

[*Suggested Medium-Complexity Linked Data Project 30*](#suggested-medium-complexity-linked-data-project)

### **Lab 11: Advanced RDFS Reasoning (Domain/Range Inference)

** {#lab-11:-advanced-rdfs-reasoning-(domain/range-inference)}

**Objective:** Deeper dive into how rdfs:domain and rdfs:range statements can lead to type inference when used with an
RDFS reasoner.

**Concepts Covered:**

* rdfs:domain and rdfs:range in detail
* How RDFS reasoner infers rdf:type based on property usage.
* Combining rdfs:subClassOf, rdfs:subPropertyOf, rdfs:domain, and rdfs:range for richer inference.

**Tasks:**

1. **Create a Base Model:**
    * Define a custom ontology prefix (e.g., ex: \<http://example.org/ontology\#\>).
    * Define classes: ex:Employee, ex:Department, ex:Project.
    * Define properties:
        * ex:worksFor (ObjectProperty)
        * ex:manages (ObjectProperty)
        * ex:hasBudget (DatatypeProperty)
    * Assert rdfs:domain and rdfs:range for properties:
        * ex:worksFor rdfs:domain ex:Employee
        * ex:worksFor rdfs:range ex:Department
        * ex:manages rdfs:domain ex:Employee
        * ex:manages rdfs:range ex:Project
        * ex:hasBudget rdfs:domain ex:Project
        * ex:hasBudget rdfs:range xsd:decimal
2. **Add Explicit Data (without explicit rdf:type for some individuals):**
    * Create an individual ex:Alice.
    * Create an individual ex:HR\_Dept.
    * Create an individual ex:ProjectX.
    * Add a statement: ex:Alice ex:worksFor ex:HR\_Dept.
    * Add a statement: ex:Alice ex:manages ex:ProjectX.
    * Add a statement: ex:ProjectX ex:hasBudget "100000.0"^^xsd:decimal.
    * **Crucially, do NOT explicitly state ex:Alice rdf:type ex:Employee, ex:HR\_Dept rdf:type ex:Department, or ex:
      ProjectX rdf:type ex:Project in the baseModel.**
3. **Apply RDFS Reasoner:**
    * Create an InfModel using ReasonerRegistry.getRDFSReasoner() and your baseModel.
4. **Query and Observe Inferred Types:**
    * Query the inferredModel for all rdf:type statements.
    * Print all inferred types. You should see that ex:Alice is inferred as ex:Employee, ex:HR\_Dept as ex:Department,
      and ex:ProjectX as ex:Project, solely due to the rdfs:domain and rdfs:range axioms and the explicit property
      usage.
    * Explain in comments why these types were inferred.

Expected Output:  
The inferred model will contain rdf:type statements for individuals that were not explicitly typed in the base model,
demonstrating the power of domain/range inference.  
**Hints/Tips:**

* RDFS reasoners use rdfs:domain and rdfs:range axioms to infer the rdf:type of subjects and objects of statements.
* If (S P O) is a triple, and P rdfs:domain C is an axiom, then S rdf:type C is inferred.
* If (S P O) is a triple, and P rdfs:range C is an axiom, then O rdf:type C is inferred.

### **Lab 12: Reification (rdf:Statement)** {#lab-12:-reification-(rdf:statement)}

**Objective:** Understand how to model statements about other statements using RDF reification.

**Concepts Covered:**

* rdf:Statement
* rdf:subject, rdf:predicate, rdf:object
* When to use reification (e.g., source of information, confidence level, time of assertion).

**Tasks:**

1. **Create a Base Model:**
    * Create resources for Boston, hasPopulation, 600000\.
    * Add a simple statement: Boston hasPopulation 600000\.
2. **Reify a Statement:**
    * Choose a statement you want to reify (e.g., Boston hasPopulation 600000).
    * Create a new Resource to represent the reified statement (e.g., val reifiedStmt \= model.createResource()).
    * Assert that this resource is an rdf:Statement: reifiedStmt.addProperty(RDF.type, RDF.Statement).
    * Link the reified statement to its components:
        * reifiedStmt.addProperty(RDF.subject, Boston)
        * reifiedStmt.addProperty(RDF.predicate, hasPopulation)
        * reifiedStmt.addProperty(RDF.object, 600000\)
    * Add properties *about* the reified statement:
        * reifiedStmt.addProperty(model.createProperty("http://example.org/data\#year"), "2004"^^xsd:integer)
        * reifiedStmt.addProperty(model.createProperty("http://example.org/data\#source"), "Wikipedia")
3. **Print the Model:**
    * Write the Model to System.out in TURTLE format.

Expected Output:  
The Turtle output will show the original statement and then a set of triples describing the reified statement, including
its subject, predicate, object, and any additional metadata.  
**Hints/Tips:**

* Reification creates *four* new triples to describe one existing triple. It does not replace the original triple.
* While powerful, reification can make models verbose. Consider alternative modeling patterns (e.g., n-ary relations) if
  reification becomes too complex for your use case.

### **Lab 13: RDF Collections (rdf:Bag, rdf:Seq, rdf:Alt)** {#lab-13:-rdf-collections-(rdf:bag,-rdf:seq,-rdf:alt)}

**Objective:** Learn how to represent ordered and unordered collections of resources or literals using RDF containers.

**Concepts Covered:**

* rdf:Bag (unordered, allows duplicates)
* rdf:Seq (ordered, allows duplicates)
* rdf:Alt (ordered, for alternatives)
* rdf:\_1, rdf:\_2, etc. (member properties)
* model.createBag(), model.createSeq(), model.createAlt()
* Adding members (container.add(resourceOrLiteral))

**Tasks:**

1. **Create a Model.**
2. **Model a Bag of Interests:**
    * Create a Resource for "PersonA".
    * Create a Property for hasInterest.
    * Create an rdf:Bag: val interestsBag \= model.createBag().
    * Add interests to the bag: interestsBag.add("Reading"), interestsBag.add("Hiking"), interestsBag.add("Reading") (
      demonstrate duplicates).
    * Link PersonA to the bag: personA.addProperty(hasInterest, interestsBag).
3. **Model a Sequence of Steps:**
    * Create a Resource for "RecipeX".
    * Create a Property for hasStep.
    * Create an rdf:Seq: val stepsSeq \= model.createSeq().
    * Add steps in order: stepsSeq.add("Mix ingredients"), stepsSeq.add("Bake at 350"), stepsSeq.add("Serve hot").
    * Link RecipeX to the sequence: recipeX.addProperty(hasStep, stepsSeq).
4. **Model Alternatives (e.g., preferred contact methods):**
    * Create a Resource for "PersonB".
    * Create a Property for preferredContact.
    * Create an rdf:Alt: val contactAlt \= model.createAlt().
    * Add alternatives: contactAlt.add("Email"), contactAlt.add("Phone"), contactAlt.add("SMS").
    * Link PersonB to the alternatives: personB.addProperty(preferredContact, contactAlt).
5. **Print the Model:**
    * Write the Model to System.out in TURTLE format. Observe how Jena represents these containers using rdf:\_1, rdf:
      \_2, etc.

Expected Output:  
The Turtle output will show the rdf:Bag, rdf:Seq, and rdf:Alt structures with their respective members, demonstrating
the different ways to represent collections.  
**Hints/Tips:**

* rdf:List (using rdf:first and rdf:rest) is another way to represent ordered lists, often preferred for more complex
  list operations, but containers are simpler for basic collections.

### **Lab 14: Property Functions and Filters in SPARQL** {#lab-14:-property-functions-and-filters-in-sparql}

**Objective:** Enhance SPARQL queries using property functions and FILTER clauses for more precise data retrieval.

**Concepts Covered:**

* FILTER clause for conditional selection
* Comparison operators (=, \!=, \<, \>, \<=, \>=)
* Logical operators (&&, ||, \!)
* String functions (STR, LANG, REGEX, LCASE, UCASE)
* Datatype functions (DATATYPE, isLiteral, isURI, isBLANK)
* BOUND (checking if a variable is bound)

**Tasks:**

1. **Create a Model with Diverse Data:**
    * Include individuals with:
        * Names (plain literals, language-tagged literals)
        * Ages (typed xsd:integer)
        * Prices (typed xsd:decimal)
        * Descriptions (plain literals)
        * Blank nodes
    * Example:
        * PersonA vcard:FN "Alice"@en
        * PersonB vcard:FN "Bob"
        * PersonA hasAge 30
        * PersonB hasAge 25
        * ProductX hasPrice "150.75"^^xsd:decimal
        * ProductY hasPrice "99.00"^^xsd:decimal
        * ProductX rdfs:comment "A great product."@en
        * ProductY rdfs:comment "Un bon produit."@fr
2. **Query with FILTER (Numeric):**
    * Find all resources with a price greater than 100\.  
      SELECT ?product ?price WHERE {  
      ?product ex:hasPrice ?price .  
      FILTER (?price \> 100.0)  
      }

3. **Query with FILTER (String and Language):**
    * Find all rdfs:label values that are in English.  
      SELECT ?s ?label WHERE {  
      ?s rdfs:label ?label .  
      FILTER (LANG(?label) \= "en")  
      }

    * Find all names that start with "A" (case-insensitive).  
      SELECT ?personName WHERE {  
      ?person vcard:FN ?personName .  
      FILTER (REGEX(?personName, "^A", "i"))  
      }

4. **Query with FILTER (Datatype and BOUND):**
    * Find all subjects that have an rdfs:comment and whose rdfs:comment is a literal.  
      SELECT ?s ?comment WHERE {  
      ?s rdfs:comment ?comment .  
      FILTER (isLiteral(?comment) && BOUND(?comment))  
      }

5. **Query with FILTER (Blank Nodes):**
    * Find all subjects that are blank nodes.  
      SELECT ?s WHERE {  
      ?s ?p ?o .  
      FILTER (isBlank(?s))  
      }

Expected Output:  
The query results will demonstrate how FILTER clauses and SPARQL functions allow for precise selection of data based on
various conditions.  
**Hints/Tips:**

* FILTER conditions are applied to the results of the WHERE clause.
* Pay attention to case sensitivity in string functions and regular expressions.

### **Lab 15: SPARQL CONSTRUCT and DESCRIBE Queries** {#lab-15:-sparql-construct-and-describe-queries}

**Objective:** Learn to use CONSTRUCT to build new RDF graphs from existing ones, and DESCRIBE to get a summary of a
resource.

**Concepts Covered:**

* SPARQL CONSTRUCT query (building new triples)
* SPARQL DESCRIBE query (summarizing a resource)
* Use cases for CONSTRUCT (data transformation, creating views)
* Use cases for DESCRIBE (exploring unknown data)

**Tasks:**

1. **Create a Source Model:**
    * Populate a model with data about people, their ages, and their cities.
        * PersonA hasName "Alice"
        * PersonA hasAge 30
        * PersonA livesIn CityX
        * CityX hasName "New York"
        * PersonB hasName "Bob"
        * PersonB hasAge 25
        * PersonB livesIn CityY
        * CityY hasName "London"
2. **CONSTRUCT Query:**
    * Write a CONSTRUCT query to create a new graph where each person is linked to their city's name via a new property,
      e.g., ex:livesInCityName.  
      PREFIX ex: \<http://example.org/data\#\>  
      PREFIX vcard: \<http://www.w3.org/2001/vcard-rdf/3.0\#\>

      CONSTRUCT {  
      ?person ex:livesInCityName ?cityName .  
      }  
      WHERE {  
      ?person ex:livesIn ?city .  
      ?city ex:hasName ?cityName .  
      }

    * Execute the CONSTRUCT query on your source model.
    * Print the resulting constructed model in TURTLE format.
3. **DESCRIBE Query:**
    * Write a DESCRIBE query for PersonA.  
      PREFIX ex: \<http://example.org/data\#\>  
      DESCRIBE ex:PersonA

    * Execute the DESCRIBE query on your source model.
    * Print the resulting model (which will contain all triples related to PersonA that Jena deems "relevant").
    * Try DESCRIBE on a blank node if you have one in your model.

Expected Output:  
The CONSTRUCT query will output a new, smaller RDF graph with transformed relationships. The DESCRIBE query will output
a subgraph centered around the described resource.  
**Hints/Tips:**

* CONSTRUCT is powerful for data integration and transformation.
* The exact triples returned by DESCRIBE can vary slightly between Jena versions and configurations, as it's an
  implementation-dependent summary.

### **Lab 16: SPARQL UPDATE Operations** {#lab-16:-sparql-update-operations}

**Objective:** Learn how to modify RDF graphs directly using SPARQL UPDATE statements.

**Concepts Covered:**

* SPARQL INSERT DATA (adding specific triples)
* SPARQL DELETE DATA (removing specific triples)
* SPARQL DELETE WHERE (removing triples matching a pattern)
* SPARQL INSERT WHERE (conditionally adding triples)
* org.apache.jena.update.UpdateFactory
* org.apache.jena.update.UpdateRequest
* org.apache.jena.update.UpdateExecutionFactory
* org.apache.jena.update.GraphStoreFactory (for in-memory updates)

**Tasks:**

1. **Create an Initial Model:**
    * Populate a model with PersonA hasName "Alice", PersonA hasAge 30\.
    * Print the initial model.
2. **INSERT DATA:**
    * Create an UpdateRequest for INSERT DATA.  
      PREFIX ex: \<http://example.org/data\#\>  
      INSERT DATA {  
      ex:PersonA ex:hasEmail "alice@example.com" .  
      ex:PersonB ex:hasName "Bob" .  
      }

    * Execute the update.
    * Print the model to verify new triples.
3. **DELETE DATA:**
    * Create an UpdateRequest for DELETE DATA.  
      PREFIX ex: \<http://example.org/data\#\>  
      DELETE DATA {  
      ex:PersonA ex:hasAge 30 .  
      }

    * Execute the update.
    * Print the model to verify deletion.
4. **INSERT WHERE (Conditional Add):**
    * Add a new property ex:isAdult to anyone with hasAge greater than or equal to 18\.  
      PREFIX ex: \<http://example.org/data\#\>  
      INSERT {  
      ?person ex:isAdult true .  
      }  
      WHERE {  
      ?person ex:hasAge ?age .  
      FILTER (?age \>= 18\)  
      }

    * Execute the update.
    * Print the model to verify.
5. **DELETE WHERE (Conditional Delete):**
    * Delete all ex:hasName triples for subjects that also have ex:isAdult true.  
      PREFIX ex: \<http://example.org/data\#\>  
      DELETE {  
      ?person ex:hasName ?name .  
      }  
      WHERE {  
      ?person ex:hasName ?name ;  
      ex:isAdult true .  
      }

    * Execute the update.
    * Print the model to verify.

Expected Output:  
You will see the model's content change after each SPARQL UPDATE operation, demonstrating how to programmatically modify
RDF data.  
**Hints/Tips:**

* For in-memory models, you can use UpdateExecutionFactory.create(updateRequest, model). For persistent stores, you'd
  use a GraphStore.
* UPDATE operations are powerful and should be used with caution, especially on live data.

### **Lab 17: Working with Named Graphs (Datasets)** {#lab-17:-working-with-named-graphs-(datasets)}

**Objective:** Learn how to manage multiple RDF graphs within a single Jena Dataset and query them using SPARQL GRAPH
clauses.

**Concepts Covered:**

* org.apache.jena.query.Dataset
* Default Graph vs. Named Graphs
* Adding models to a dataset (dataset.addNamedModel)
* SPARQL GRAPH clause
* SPARQL FROM and FROM NAMED

**Tasks:**

1. **Create a Dataset:**
    * Use DatasetFactory.create() to create an empty in-memory dataset.
2. **Create Multiple Models (Graphs):**
    * Create Model defaultGraphModel and add some general facts (e.g., ex:GlobalFactA).
    * Create Model graphA (named "[http://example.org/graphA](http://example.org/graphA)") and add facts specific to
      it (e.g., ex:PersonA ex:status "Active").
    * Create Model graphB (named "[http://example.org/graphB](http://example.org/graphB)") and add facts specific to
      it (e.g., ex:PersonA ex:status "OnLeave" \- demonstrate conflicting info across graphs).
3. **Add Models to the Dataset:**
    * Add defaultGraphModel to the dataset's default graph.
    * Add graphA to the dataset as a named graph: dataset.addNamedModel("http://example.org/graphA", graphA).
    * Add graphB similarly.
4. **Query the Default Graph:**
    * Write a SPARQL query to select all triples from the default graph.  
      SELECT ?s ?p ?o WHERE { ?s ?p ?o . }

    * Execute against the dataset.
5. **Query a Specific Named Graph:**
    * Write a SPARQL query to select all triples from http://example.org/graphA.  
      SELECT ?s ?p ?o WHERE { GRAPH \<http://example.org/graphA\> { ?s ?p ?o . } }

    * Execute against the dataset.
6. **Query Across All Named Graphs:**
    * Write a SPARQL query to find all subjects and predicates that appear in *any* named graph.  
      SELECT ?g ?s ?p WHERE { GRAPH ?g { ?s ?p ?o . } }

    * Execute against the dataset.
7. **Query for PersonA's status across graphs:**
    * Write a query to find PersonA's status from both named graphs.  
      SELECT ?g ?status WHERE {  
      GRAPH ?g {  
      \<http://example.org/data\#PersonA\> \<http://example.org/data\#status\> ?status .  
      }  
      }

    * Execute against the dataset.

Expected Output:  
The query results will demonstrate how to target specific graphs within a dataset using GRAPH and how to retrieve
triples from the default graph or all named graphs.  
**Hints/Tips:**

* Named graphs are crucial for managing provenance, context, or different versions of data.
* FROM and FROM NAMED can also be used in SPARQL queries to specify the graphs to query.

### **Lab 18: Advanced Datatype Handling** {#lab-18:-advanced-datatype-handling}

**Objective:** Explore more advanced aspects of working with XSD datatypes, including parsing, validation, and custom
datatypes.

**Concepts Covered:**

* Literal.getDatatype() and Literal.getDatatypeURI()
* Literal.getValue() for accessing Java objects
* XSDDatatype.get and XSDDatatype.getURI
* Handling LiteralRequiredException for invalid literals
* (Optional) Brief mention of custom datatypes (though defining them fully is more advanced OWL/SHACL).

**Tasks:**

1. **Create a Model with Various Typed Literals:**
    * Add literals with correct and incorrect XSD datatypes:
        * ex:item1 ex:price "100.50"^^xsd:decimal
        * ex:item2 ex:price "not-a-number"^^xsd:decimal (intentional error)
        * ex:event1 ex:date "2024-01-01T10:00:00Z"^^xsd:dateTime
        * ex:event2 ex:date "invalid-date"^^xsd:dateTime (intentional error)
        * ex:person1 ex:age "30"^^xsd:integer
2. **Iterate and Validate Literals:**
    * Iterate through all statements in the model.
    * If the object is a Literal:
        * Print its lexical form and datatype URI.
        * Try to get its Java value using literal.getValue().
        * Use a try-catch block around literal.getValue() to catch DatatypeFormatException for invalid literals. Print
          an error message for invalid ones.
3. **Programmatic Datatype Creation/Lookup:**
    * Get the XSDDatatype object for xsd:integer using XSDDatatype.XSDinteger.
    * Get the URI for xsd:dateTime using XSDDatatype.XSDdateTime.getURI().
    * Create a literal using model.createTypedLiteral("42", XSDDatatype.XSDinteger).
4. **Print the Model:**
    * Write the Model to System.out in TURTLE format.

Expected Output:  
The program will successfully parse valid typed literals and gracefully handle DatatypeFormatException for invalid ones,
demonstrating robust datatype handling.  
**Hints/Tips:**

* Jena performs basic syntactic validation for XSD datatypes when creating typed literals or reading models.
* literal.getValue() returns a Java object (e.g., Integer, Double, Calendar) corresponding to the datatype.

### **Lab 19: Comparing and Merging Models** {#lab-19:-comparing-and-merging-models}

**Objective:** Learn how to perform set operations (union, intersection, difference) on Jena Model objects.

**Concepts Covered:**

* model.union(otherModel)
* model.intersection(otherModel)
* model.difference(otherModel)
* Use cases for model comparison and merging (e.g., change tracking, data integration).

**Tasks:**

1. **Create Three Models:**
    * Model modelA: Add PersonA hasName "Alice", PersonA hasAge 30, PersonA livesIn "New York".
    * Model modelB: Add PersonA hasName "Alice", PersonA hasEmail "alice@example.com", PersonB hasName "Bob".
    * Model modelC: Add PersonA hasAge 30, PersonC hasName "Charlie".
2. **Perform Union:**
    * Create Model unionAB \= modelA.union(modelB).
    * Print unionAB size and content.
3. **Perform Intersection:**
    * Create Model intersectionAB \= modelA.intersection(modelB).
    * Print intersectionAB size and content.
    * Create Model intersectionAC \= modelA.intersection(modelC).
    * Print intersectionAC size and content.
4. **Perform Difference:**
    * Create Model differenceAB \= modelA.difference(modelB) (triples in A but not in B).
    * Print differenceAB size and content.
    * Create Model differenceBA \= modelB.difference(modelA) (triples in B but not in A).
    * Print differenceBA size and content.
5. **Chaining Operations:**
    * Calculate (modelA union modelB) intersection modelC.
    * Print the result.

Expected Output:  
The console output will show the results of each set operation, demonstrating how triples are combined or filtered based
on the operation.  
**Hints/Tips:**

* These operations create *new* models; they do not modify the original models.
* union is often used for merging data from different sources. difference can be used for change detection.

### **Lab 20: Error Handling and Best Practices** {#lab-20:-error-handling-and-best-practices}

**Objective:** Learn about common pitfalls and best practices for robust Jena application development.

**Concepts Covered:**

* Resource management (closing iterators, query executions, streams)
* Handling JenaException and specific subclasses
* Using try-finally or Scala's resource management patterns
* Best practices for URIs and prefixes.

**Tasks:**

1. **Resource Management (Crucial\!):**
    * **Incorrect:** Write code that opens a FileInputStream or QueryExecution but *doesn't* close it. Explain why this
      is bad (resource leaks).
    * **Correct:** Rewrite the code using try-finally or Scala's using pattern (if available via a library like
      scala-arm or scala.util.Using in Scala 2.13+) to ensure streams and query executions are always closed.  
      // Example using scala.util.Using (Scala 2.13+)  
      import scala.util.Using  
      import java.io.{FileInputStream, FileOutputStream}

      // ... model creation ...

      val filePath \= "data.ttl"  
      Using(new FileOutputStream(filePath)) { os \=\>  
      model.write(os, "TURTLE")  
      } match {  
      case scala.util.Success(\_) \=\> println(s"Model written to $filePath")  
      case scala.util.Failure(e) \=\> println(s"Error writing model: ${e.getMessage}")  
      }

      Using(new FileInputStream(filePath)) { is \=\>  
      val loadedModel \= ModelFactory.createDefaultModel()  
      loadedModel.read(is, baseURI, "TURTLE")  
      println(s"Loaded model size: ${loadedModel.size()}")  
      } match {  
      case scala.util.Success(\_) \=\> // Handled inside Using block  
      case scala.util.Failure(e) \=\> println(s"Error reading model: ${e.getMessage}")  
      }

2. **Handling Invalid RDF/SPARQL:**
    * Try to read a malformed RDF file (e.g., intentionally corrupt a Turtle file). Catch the
      org.apache.jena.riot.RiotException or org.apache.jena.shared.BadURIException.
    * Try to execute a syntactically incorrect SPARQL query. Catch the org.apache.jena.query.QueryParseException.
3. **URI Best Practices:**
    * Discuss in comments:
        * Why stable, dereferenceable URIs are important for linked data.
        * The difference between http://example.org/resource\#fragment and http://example.org/resource/path.
        * The importance of consistent prefix usage.
4. **Model Size and Performance (Discussion):**
    * Briefly discuss in comments that for very large models, in-memory models might not be sufficient, and persistent
      stores (TDB, Fuseki) would be necessary.

Expected Output:  
The program will demonstrate robust error handling for file operations and SPARQL parsing, and your comments will
outline key best practices.  
**Hints/Tips:**

* Jena's StmtIterator and NodeIterator also implement AutoCloseable (or Closeable in older Java versions), so they
  should also be closed, though for small in-memory models, the impact is less severe.

## **Suggested Medium-Complexity Linked Data Project** {#suggested-medium-complexity-linked-data-project}

**Project Title:** Linked Research Publication Explorer

**Objective:** Model and explore a small dataset of academic publications, authors, and their affiliations,
demonstrating linked data principles and basic reasoning.

**Key Features to Implement:**

1. **Data Modeling (RDF/RDFS):**
    * **Classes:**
        * ex:Publication (subclasses: ex:JournalArticle, ex:ConferencePaper, ex:BookChapter)
        * ex:Author (use VCARD.Individual as a superclass or directly)
        * ex:Affiliation (e.g., ex:University, ex:ResearchLab)
        * ex:Topic
    * **Properties:**
        * ex:hasTitle (DatatypeProperty, xsd:string)
        * ex:hasAbstract (DatatypeProperty, xsd:string, with language tags)
        * ex:publishedIn (ObjectProperty, links Publication to Journal/Conference)
        * ex:hasAuthor (ObjectProperty, links Publication to Author)
        * ex:affiliatedWith (ObjectProperty, links Author to Affiliation)
        * ex:hasKeyword (ObjectProperty, links Publication to Topic)
        * ex:publicationYear (DatatypeProperty, xsd:gYear or xsd:integer)
        * ex:cites (ObjectProperty, links Publication to Publication)
    * **RDFS Axioms:**
        * Define rdfs:subClassOf relationships (e.g., JournalArticle rdfs:subClassOf Publication).
        * Define rdfs:subPropertyOf relationships (e.g., hasAuthor rdfs:subPropertyOf ex:contributesTo).
        * Define rdfs:domain and rdfs:range for all custom properties.
2. **Populate the Model:**
    * Create a small dataset (5-10 publications, 5-10 authors, 2-3 affiliations, a few topics).
    * Include at least one blank node (e.g., for an anonymous reviewer, or a complex author affiliation).
    * Include various datatypes and language tags.
    * Include some cites relationships.
3. **Reasoning:**
    * Apply an RDFS reasoner to infer new facts (e.g., if a JournalArticle is a Publication, then an instance of
      JournalArticle is also a Publication).
    * Demonstrate domain/range inference (e.g., if Alice hasAuthor ThePaper, and hasAuthor rdfs:domain Author, then
      Alice is inferred as an Author).
4. **SPARQL Querying:**
    * **Basic Queries:**
        * List all publications and their titles.
        * Find all authors affiliated with a specific university.
        * Find all publications on a given topic.
    * **Inference-Aware Queries:**
        * Find all Publication instances (including inferred ones).
        * Find all individuals who contributesTo any CreativeWork (using rdfs:subPropertyOf inference).
    * **Complex Queries:**
        * Find authors who have published more than 3 papers.
        * Find publications that cite papers from a specific year.
        * Find co-authors of a given author.
        * Find publications by authors from a specific country (requires extending Affiliation with VCARD.Country).
    * **CONSTRUCT Query:**
        * Construct a new graph linking authors directly to the titles of their publications.
5. **Serialization:**
    * Save the initial explicit model to a Turtle file.
    * Save the inferred model to a separate Turtle file.

**Challenges/Extensions:**

* **Data Loading:** Instead of hardcoding, try reading data from a CSV or JSON file and converting it to RDF.
* **External Vocabularies:** Integrate more external vocabularies like DC (Dublin Core for publication metadata) or
  FOAF (Friend of a Friend for social network aspects).
* **Simple UI:** (Beyond Jena scope, but for a real project) Build a simple web UI (e.g., using Scala Play or a simple
  HTTP server) that allows users to run predefined SPARQL queries and display results.
* **OWL (Future Step):** Once comfortable with RDFS, extend the model to OWL and use an OWL reasoner for more complex
  inferences (e.g., disjoint classes, inverse properties, cardinality restrictions).

This project will provide hands-on experience with the entire lifecycle of a small linked data application, from
modeling to querying and reasoning.