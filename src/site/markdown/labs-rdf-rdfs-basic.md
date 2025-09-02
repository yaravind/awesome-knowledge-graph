# Basic Labs for RDF and RDFS Modeling with Apache Jena (Scala)

These labs will guide you through the fundamentals of RDF modeling, RDFS semantics, and how to implement them using the
Apache Jena library in Scala.

[*Basic Labs*](#basic-labs)

[Lab 1: Introduction to RDF and Jena Basics	2](#lab-1:-introduction-to-rdf-and-jena-basics)

[Lab 2: Working with Vocabularies and Basic RDFS	3](#lab-2:-working-with-vocabularies-and-basic-rdfs)

[Lab 3: RDFS Modeling \- Subclass and Subproperty Hierarchies	5](#lab-3:-rdfs-modeling---subclass-and-subproperty-hierarchies)

[Lab 4: Introduction to RDFS Reasoning	6](#lab-4:-introduction-to-rdfs-reasoning)

[Lab 5: Basic SPARQL Querying	7](#lab-5:-basic-sparql-querying)

[Lab 6: Datatypes and Language Tags	9](#lab-6:-datatypes-and-language-tags)

[Lab 7: RDF Modeling with Blank Nodes	10](#lab-7:-rdf-modeling-with-blank-nodes)

[Lab 8: Navigating the Model (Iterators)    12](#lab-8:-navigating-the-model-\(iterators\))

[Lab 9: Modifying Models (Add, Remove, Clear)    13](#lab-9:-modifying-models-\(add,-remove,-clear\))

[Lab 10: Reading and Writing Models to Files	14](#lab-10:-reading-and-writing-models-to-files)


## **Basic Labs** {#basic-labs}

### **Lab 1: Introduction to RDF and Jena Basics** {#lab-1:-introduction-to-rdf-and-jena-basics}

**Objective:** Understand the core concepts of RDF triples and learn how to create, populate, and serialize a basic Jena
Model.

**Concepts Covered:**

* RDF Triples (Subject-Predicate-Object)
* Resources (URIs)
* Literals (plain and typed)
* org.apache.jena.rdf.model.Model
* org.apache.jena.rdf.model.Resource
* org.apache.jena.rdf.model.Property
* org.apache.jena.rdf.model.Literal
* Adding statements (addProperty)
* Setting namespace prefixes (setNsPrefix)
* Writing models to different formats (write)

**Tasks:**

1. **Project Setup:**
    * Create a new Scala project (e.g., using sbt or IntelliJ IDEA).
    * Add the necessary Apache Jena dependencies to your build.sbt. You'll typically need jena-arq and jena-core.  
      // build.sbt  
      name := "JenaRdfLabs"  
      version := "0.1"  
      scalaVersion := "2.13.12" // Or 2.12.x if preferred, but 2.13.x is common  
      // Note: Scala 2.17 is not a real version. Use 2.13.x or 3.x  
      // For these labs, Scala 2.13.x is fine.

      libraryDependencies \++= Seq(  
      "org.apache.jena" % "jena-arq" % "5.0.0", // Use a recent stable version  
      "org.apache.jena" % "jena-core" % "5.0.0"  
      )

2. **Create a Basic Model:**
    * Create a new Scala object (e.g., Lab1).
    * Inside its main method, create an empty Model using ModelFactory.createDefaultModel().
    * Define a base URI for your custom resources (e.g., http://example.org/data\#).
3. **Add Triples:**
    * Create a Resource for "Alice" (e.g., http://example.org/data\#Alice).
    * Create a Property for "hasName" (e.g., http://example.org/data\#hasName).
    * Create a Literal for "Alice Smith".
    * Add the triple: Alice hasName "Alice Smith".
    * Add another triple: Alice hasAge 30 (use an integer literal).
    * Add a triple: Alice hasEmail "alice@example.com" (use a string literal).
4. **Add a Second Individual:**
    * Create a Resource for "Bob" (http://example.org/data\#Bob).
    * Add triples for Bob's name and age.
    * Add a triple: Alice knows Bob (create a knows property and use Bob's Resource as the object).
5. **Set Prefixes:**
    * Set a prefix for your custom base URI (e.g., data).
    * Set prefixes for RDF and RDFS vocabularies.
6. **Write the Model:**
    * Write the Model to System.out in RDF/XML-ABBREV format.
    * Write the Model to System.out again, but this time in TURTLE format.

Expected Output:  
You should see two blocks of RDF output on your console, one in RDF/XML (abbreviated) and one in Turtle, representing
the triples you added.  
**Hints/Tips:**

* Remember to import necessary Jena classes: org.apache.jena.rdf.model.\_, org.apache.jena.vocabulary.\_.
* Use model.createResource(uri) to create resources.
* Use model.createProperty(uri) to create properties.
* Use resource.addProperty(property, literal) or resource.addProperty(property, otherResource) to add triples.

### **Lab 2: Working with Vocabularies and Basic RDFS** {#lab-2:-working-with-vocabularies-and-basic-rdfs}

**Objective:** Learn how to use standard RDF/RDFS vocabularies and define your own custom classes and properties with
basic RDFS metadata.

**Concepts Covered:**

* org.apache.jena.vocabulary.RDF (e.g., RDF.type)
* org.apache.jena.vocabulary.RDFS (e.g., RDFS.Class, RDFS.label, RDFS.comment)
* org.apache.jena.vocabulary.VCARD (for person information)
* Defining custom classes using rdf:type rdfs:Class
* Defining custom properties using rdf:type rdf:Property
* Adding human-readable labels and comments to classes/properties.

**Tasks:**

1. **Start with Lab 1's code.**
2. **Use VCARD for Person:**
    * Instead of creating a custom Person class, use VCARD.Individual as the type for Alice and Bob.
    * Use VCARD.FN (Formatted Name) property for their names instead of your custom hasName.
3. **Define Custom Classes:**
    * Create a Resource for a new custom class, e.g., http://example.org/ontology\#Book.
    * Assert its type as RDFS.Class: bookClass.addProperty(RDF.type, RDFS.Class).
    * Add an rdfs:label for Book (e.g., "Book").
    * Add an rdfs:comment for Book (e.g., "A written or printed work consisting of pages bound together.").
4. **Define Custom Properties:**
    * Create a Property for http://example.org/ontology\#hasAuthor.
    * Assert its type as RDF.Property: hasAuthorProperty.addProperty(RDF.type, RDF.Property).
    * Add an rdfs:label and rdfs:comment for hasAuthor.
5. **Create an Instance of Your Custom Class:**
    * Create a Resource for a specific book, e.g., http://example.org/data\#TheHobbit.
    * Assert its type as your Book class: theHobbit.addProperty(RDF.type, bookClass).
    * Add a title (plain literal) and use your hasAuthor property to link it to "J.R.R. Tolkien" (as a literal or a new
      VCARD.Individual if you want to model authors as individuals).
6. **Print the Model:**
    * Write the updated Model to System.out in TURTLE format. Observe how the rdf:type, rdfs:Class, rdfs:label, and
      rdfs:comment statements appear.

Expected Output:  
The Turtle output should clearly show your custom classes and properties with their labels and comments, and the
individuals typed using VCARD.Individual and your custom Book class.  
**Hints/Tips:**

* Remember to use RDF.type and RDFS.Class (or RDF.Property) to define your schema elements.
* RDFS.label and RDFS.comment are useful for human-readable documentation within the RDF graph itself.

### **Lab 3: RDFS Modeling \- Subclass and Subproperty Hierarchies

** {#lab-3:-rdfs-modeling---subclass-and-subproperty-hierarchies}

**Objective:** Understand and implement rdfs:subClassOf and rdfs:subPropertyOf relationships to build more structured
ontologies.

**Concepts Covered:**

* rdfs:subClassOf
* rdfs:subPropertyOf
* rdfs:domain (optional, for explicit documentation in base model)
* rdfs:range (optional, for explicit documentation in base model)

**Tasks:**

1. **Start with Lab 2's code.**
2. **Refine Class Hierarchy:**
    * Introduce a more general class, e.g., http://example.org/ontology\#CreativeWork.
    * Make Book an rdfs:subClassOf CreativeWork.
        * bookClass.addProperty(RDFS.subClassOf, creativeWorkClass)
    * Add labels and comments for CreativeWork.
3. **Refine Property Hierarchy:**
    * Introduce a more general property, e.g., http://example.org/ontology\#contributesTo.
    * Make hasAuthor an rdfs:subPropertyOf contributesTo.
        * hasAuthorProperty.addProperty(RDFS.subPropertyOf, contributesToProperty)
    * Add labels and comments for contributesTo.
4. **Add Domain and Range (Optional but good practice for documentation):**
    * For hasAuthorProperty, add rdfs:domain to VCARD.Individual (or your custom Person if you reverted).
    * For hasAuthorProperty, add rdfs:range to Book.
    * Note: In a basic Model, rdfs:domain and rdfs:range are just statements; they don't enforce constraints like in
      OWL.
5. **Add More Data:**
    * Create another Book individual.
    * Create an individual for an Article (as a new class, rdfs:subClassOf CreativeWork).
    * Use hasAuthor for the Article.
6. **Print the Model:**
    * Write the updated Model to System.out in TURTLE format. Observe the rdfs:subClassOf and rdfs:subPropertyOf
      statements.

Expected Output:  
The Turtle output should clearly show the hierarchical relationships between your classes and properties.  
**Hints/Tips:**

* Think about real-world "is-a" and "is-part-of" relationships when designing hierarchies. rdfs:subClassOf is for "
  is-a" (e.g., "A Smartphone IS A CellPhone"). rdfs:subPropertyOf is for "is a more specific way of" (e.g., "hasAuthor
  IS A MORE SPECIFIC WAY OF contributesTo").

### **Lab 4: Introduction to RDFS Reasoning** {#lab-4:-introduction-to-rdfs-reasoning}

**Objective:** Understand how RDFS inference rules can be applied to a model to derive new, implicit facts.

**Concepts Covered:**

* RDFS Inference Rules (specifically rdfs:subClassOf and rdfs:subPropertyOf rules)
* org.apache.jena.reasoner.Reasoner
* org.apache.jena.reasoner.ReasonerRegistry.getRDFSReasoner()
* org.apache.jena.rdf.model.InfModel
* Distinguishing between explicit and inferred triples.

**Tasks:**

1. **Use the provided rdfs-reasoner-example-scala code as your starting point.**
2. **Review the Code:**
    * Carefully read through the RDFSReasonerExample.scala code.
    * Identify where the baseModel is created with explicit facts and RDFS schema.
    * Locate where the Reasoner is obtained and the InfModel is created.
    * Understand how the asScala.foreach is used to iterate over statements.
3. **Experiment with the baseModel:**
    * Comment out the line where johnsPhone is explicitly typed as smartPhoneClass (i.e., johnsPhone.addProperty(
      RDF.type, smartPhoneClass)).
    * Instead, explicitly type johnsPhone as cellPhoneClass.
    * Run the code. What changes do you observe in the "Inferred Model" and the "Demonstrating Inferred Triples"
      section? (Hint: johnsPhone will still be inferred as a Device, but not as a Smartphone unless you explicitly state
      it or have other rules.)
4. **Experiment with rdfs:subPropertyOf:**
    * Comment out the line hasCellPhoneProperty.addProperty(RDFS.subPropertyOf, hasDeviceProperty).
    * Run the code. What happens to the hasDevice inferred triples? Why?
5. **Add a new rdfs:subClassOf relationship:**
    * Add a new class, e.g., http://example.org/vocabulary/device\#WearableDevice.
    * Make Smartphone an rdfs:subClassOf WearableDevice (if you want to imply that smartphones can be worn).
    * Add an individual that is explicitly a Smartphone.
    * Observe if it's now inferred as a WearableDevice.

Expected Output:  
By modifying and running the code, you should observe how removing or adding RDFS schema statements directly impacts the
triples that the RDFS reasoner infers. This will solidify your understanding of how rdfs:subClassOf and rdfs:
subPropertyOf drive inference.  
**Hints/Tips:**

* The InfModel *does not* change the baseModel. It provides a *view* that includes both explicit and inferred triples.
* The if (\!baseModel.contains(stmt)) check is crucial for identifying which triples were *newly inferred*.

### **Lab 5: Basic SPARQL Querying** {#lab-5:-basic-sparql-querying}

**Objective:** Learn to retrieve data from your RDF models using SPARQL, the RDF query language.

**Concepts Covered:**

* SPARQL SELECT queries
* WHERE clause for triple patterns
* Variables (starting with ?)
* org.apache.jena.query.QueryFactory
* org.apache.jena.query.QueryExecutionFactory
* org.apache.jena.query.ResultSet
* Iterating over query results

**Tasks:**

1. **Start with the RDFSReasonerExample.scala code from Lab 4\.** You will query the inferredModel.
2. **Perform a Simple SELECT Query:**
    * Write a SPARQL query to select all subjects and objects that have rdf:type as their predicate.  
      SELECT ?s ?o WHERE {  
      ?s rdf:type ?o .  
      }

    * Use QueryFactory.create(queryString) to parse the query.
    * Use QueryExecutionFactory.create(query, inferredModel) to create a query execution object.
    * Execute the query and get a ResultSet.
    * Iterate through the ResultSet and print out the subject and object of each result.
3. **Query for Specific Relationships:**
    * Write a SPARQL query to find all individuals who hasDevice something, and what that device is.  
      SELECT ?person ?device WHERE {  
      ?person \<http://example.org/vocabulary/device\#hasDevice\> ?device .  
      }

    * Execute this query on the inferredModel and print the results. Notice that JohnDoe hasDevice JohnsPhone will
      appear, even though it was only inferred.
4. **Filter by Type:**
    * Write a SPARQL query to find all resources that are of type device:Smartphone.  
      SELECT ?s WHERE {  
      ?s rdf:type \<http://example.org/vocabulary/device\#Smartphone\> .  
      }

    * Execute this query on the inferredModel.
5. **Combine Patterns:**
    * Write a SPARQL query to .  
      SELECT ?personName ?phoneType WHERE {  
      ?person vcard:FN ?personName .  
      ?person \<http://example.org/vocabulary/device\#hasCellPhone\> ?phone .  
      ?phone rdf:type ?phoneType .  
      }

    * Execute this query on the inferredModel.

Expected Output:  
For each query, you should see a list of results corresponding to the triples found in the inferredModel, including
those that were inferred by the RDFS reasoner.  
**Hints/Tips:**

* Remember to import org.apache.jena.query.\_.
* Always close the QueryExecution object using qexec.close() in a finally block or try-with-resources.
* SPARQL queries can be written directly in multiline Scala strings.

### **Lab 6: Datatypes and Language Tags** {#lab-6:-datatypes-and-language-tags}

**Objective:** Understand how to use different XML Schema (XSD) datatypes and language tags with RDF literals.

**Concepts Covered:**

* org.apache.jena.datatypes.xsd.XSDDatatype
* Creating typed literals (e.g., xsd:integer, xsd:dateTime, xsd:boolean)
* Creating literals with language tags (e.g., @en, @fr)
* Retrieving literal values and their datatypes/language tags.

**Tasks:**

1. **Start with a clean Model (similar to Lab 1).**
2. **Add Typed Literals:**
    * Create a resource for a "Product" (e.g., http://example.org/data\#ProductX).
    * Add a price: ProductX hasPrice "99.99"^^xsd:decimal.
    * Add a release date: ProductX releaseDate "2023-10-26T14:30:00Z"^^xsd:dateTime.
    * Add a boolean property: ProductX isInStock "true"^^xsd:boolean.
    * Add a quantity: ProductX quantity "150"^^xsd:integer.
3. **Add Literals with Language Tags:**
    * Add a label for ProductX in English: ProductX rdfs:label "Super Gadget"@en.
    * Add a label for ProductX in French: ProductX rdfs:label "Super Gadget"@fr.
4. **Retrieve and Inspect Literals:**
    * Iterate through all statements in your model.
    * For each statement where the object is a Literal, print:
        * Its lexical form (literal.getLexicalForm()).
        * Its datatype URI if it has one (literal.getDatatypeURI()).
        * Its language tag if it has one (literal.getLanguage()).
        * Its Java value (literal.getValue()).
5. **Print the Model:**
    * Write the Model to System.out in TURTLE format.

Expected Output:  
The Turtle output will show the typed literals and language-tagged literals. Your program's output will demonstrate how
to extract this information programmatically.  
**Hints/Tips:**

* Use model.createTypedLiteral(value, XSDDatatype.XSD\_TYPE) for typed literals.
* Use model.createLiteral(value, languageTag) for language-tagged literals.

### **Lab 7: RDF Modeling with Blank Nodes** {#lab-7:-rdf-modeling-with-blank-nodes}

**Objective:** Learn how to use blank nodes (anonymous resources) to model complex relationships without assigning
explicit URIs.

**Concepts Covered:**

* Blank Nodes (Bnodes)
* When to use blank nodes (e.g., describing components, nested structures)
* Creating blank nodes in Jena (model.createResource())
* How blank nodes appear in different serialization formats.

**Tasks:**

1. **Start with a clean Model.**
2. **Model an Address with a Blank Node:**
    * Create a Resource for "PersonA".
    * Create a Property for hasAddress.
    * Create a blank node for the address: val addressBnode \= model.createResource().
    * Link PersonA to this blank node: personA.addProperty(hasAddress, addressBnode).
    * Add properties to the blank node (the address details):
        * addressBnode addProperty VCARD.Street "123 Main St"
        * addressBnode addProperty VCARD.Locality "Anytown"
        * addressBnode addProperty VCARD.Region "State"
        * addressBnode addProperty VCARD.Pcode "12345"
3. **Model a Book with Multiple Authors (using blank nodes for author roles):**
    * Create a Resource for a "BookX".
    * Create a Property for hasAuthorRole.
    * Create a blank node for "AuthorRole1": val authorRole1 \= model.createResource().
    * Link BookX to authorRole1: bookX.addProperty(hasAuthorRole, authorRole1).
    * Add properties to authorRole1:
        * authorRole1 addProperty VCARD.FN "Author One"
        * authorRole1 addProperty RDFS.label "Primary Author"
    * Create another blank node for "AuthorRole2" and link it similarly.
4. **Print the Model:**
    * Write the Model to System.out in TURTLE format. Observe how blank nodes are represented (usually with \_: or
      nested structures).
    * Write the Model to System.out in N-TRIPLES format. Observe how blank nodes are assigned internal identifiers.

Expected Output:  
The Turtle output will show the blank nodes either as \_: identifiers or as nested structures. The N-Triples output will
show explicit \_: identifiers.  
**Hints/Tips:**

* When model.createResource() is called without a URI argument, it creates a blank node.
* Blank nodes are useful for describing things that don't have a globally unique identifier but are part of a larger
  structure.

### **Lab 8: Navigating the Model (Iterators)** {#lab-8:-navigating-the-model-(iterators)}

**Objective:** Learn various ways to traverse and inspect the triples within a Jena Model using different iterators.

**Concepts Covered:**

* StmtIterator (iterating over all statements)
* listStatements(subject, predicate, object) (pattern matching)
* listSubjects()
* listPredicates()
* listObjects()
* listProperties(subject, predicate)

**Tasks:**

1. **Load a Model:**
    * Create a Model and add a few individuals with various properties (e.g., Alice, Bob, a Book, a Phone, some
      properties like hasAge, knows, hasTitle, hasColor). Make sure to use different subjects, predicates, and objects.
2. **Iterate All Statements:**
    * Use model.listStatements() to get an iterator over all statements.
    * Print each statement in the format (Subject, Predicate, Object).
3. **Find Specific Statements (Pattern Matching):**
    * Use model.listStatements(alice, null, null) to find all statements about Alice. Print them.
    * Use model.listStatements(null, knowsProperty, null) to find all "knows" relationships. Print them..
4. **List Subjects, Predicates, Objects:**
    * Use model.listSubjects() to get an iterator over all unique subjects. Print them.
    * Use model.listPredicates() to get an iterator over all unique predicates. Print them.
    * Use model.listObjects() to get an iterator over all unique objects (resources and literals). Print them.
5. **List Properties of a Resource:**
    * Use alice.listProperties() to get all properties of Alice. Print them.
    * Use alice.listProperties(hasAgeProperty) to get a specific property of Alice. Print it.

Expected Output:  
Your console output will show various ways to extract and view data from your RDF model, demonstrating the flexibility
of Jena's iterators.  
**Hints/Tips:**

* Remember to use asScala.foreach or while (iterator.hasNext()) for iterating over Jena's Java iterators.
* null can be used as a wildcard in listStatements.

### **Lab 9: Modifying Models (Add, Remove, Clear)** {#lab-9:-modifying-models-(add,-remove,-clear)}

**Objective:** Learn how to dynamically modify an RDF model by adding, removing, and clearing statements.

**Concepts Covered:**

* model.add(statement)
* model.remove(statement)
* model.removeAll(subject, predicate, object) (pattern-based removal)
* model.removeAll() (clearing the entire model)
* model.contains(statement)

**Tasks:**

1. **Create an Initial Model:**
    * Create a Model and add a few explicit facts:
        * PersonA hasName "Alice"
        * PersonB hasName "Bob"
        * PersonA hasAge 30
        * PersonB hasAge 25
        * PersonA knows PersonB
2. **Add a New Statement:**
    * Add a new triple: PersonB hasEmail "bob@example.com".
    * Print the model to verify.
3. **Remove a Specific Statement:**
    * Create the exact Statement object for PersonA hasAge 30\.
    * Use model.remove(statement) to remove it.
    * Print the model to verify.
4. **Remove Statements by Pattern:**
    * Remove all hasAge properties from all subjects: model.removeAll(null, hasAgeProperty, null).
    * Print the model to verify.
    * Remove all statements where PersonA is the subject: model.removeAll(personA, null, null).
    * Print the model to verify.
5. **Check for Existence:**
    * After removals, use model.contains(statement) to check if PersonA hasName "Alice" still exists (it shouldn't if
      you removed all of PersonA's statements).
    * Check if PersonB hasEmail "bob@example.com" still exists.
6. **Clear the Entire Model:**
    * Use model.removeAll() to clear all statements from the model.
    * Print the model size (model.size()) to confirm it's empty.

Expected Output:  
You will see the model changing after each modification operation, demonstrating the dynamic nature of Jena models.  
**Hints/Tips:**

* removeAll(null, null, null) is equivalent to removeAll().
* Be careful with removeAll as it can remove a lot of data quickly.

### **Lab 10: Reading and Writing Models to Files** {#lab-10:-reading-and-writing-models-to-files}

**Objective:** Learn how to persist RDF models to files and load them back, supporting various RDF serialization
formats.

**Concepts Covered:**

* Model.write(OutputStream, format)
* Model.read(InputStream, baseURI, format)
* File I/O in Scala (java.io.FileOutputStream, java.io.FileInputStream)
* Common RDF formats: TURTLE, RDF/XML, N-TRIPLES, JSON-LD.

**Tasks:**

1. **Create a Model with Data:**
    * Create a Model and populate it with a few individuals, properties, and literals (e.g., from Lab 3 or 4).
2. **Write to a Turtle File:**
    * Define a file path (e.g., "data.ttl").
    * Use new FileOutputStream(filePath) to create an output stream.
    * Call model.write(outputStream, "TURTLE").
    * Close the output stream.
    * Verify the file content manually.
3. **Write to an RDF/XML File:**
    * Define another file path (e.g., "data.rdf").
    * Write the same model to this file in RDF/XML format.
    * Close the output stream.
    * Verify the file content.
4. **Read from a File:**
    * Create a *new, empty* Model (e.g., loadedModel).
    * Define the input file path (e.g., "data.ttl").
    * Use new FileInputStream(filePath) to create an input stream.
    * Call loadedModel.read(inputStream, baseURI, "TURTLE").
    * Close the input stream.
    * Print the loadedModel to System.out to confirm it loaded correctly.
5. **Handle Non-Existent File (Error Handling):**
    * Try to read from a non-existent file and observe the FileNotFoundException. Implement basic try-catch to handle it
      gracefully.

Expected Output:  
You will have data.ttl and data.rdf files created in your project directory. The console output will show the loaded
model, confirming successful read operations.  
**Hints/Tips:**

* Always close file streams to prevent resource leaks.
* The baseURI argument in read is important for resolving relative URIs in the input file. For absolute URIs, it can be
  null or an empty string, but it's good practice to provide it.