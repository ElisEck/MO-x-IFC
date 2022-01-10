import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RIOT;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.tdb.sys.TDBInternal;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.elisabetheckstaedt.moxifc.rdf.helper.ModelHelper;

import java.io.*;
import java.util.Iterator;

/**
 * Sammelsurium an Test des Jena-Frameworks
 */
public class JenaTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(JenaTest.class);

    static String PREFIXSTRING =
            "PREFIX ifc: <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#>" + System.lineSeparator() +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + System.lineSeparator() +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + System.lineSeparator() +
            "PREFIX list: <https://w3id.org/list#> " + System.lineSeparator() +
            "PREFIX cc: <http://creativecommons.org/ns#> " + System.lineSeparator() +
            "PREFIX vann: <http://purl.org/vocab/vann/> " + System.lineSeparator() +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> " + System.lineSeparator() +
            "PREFIX express: <https://w3id.org/express#> " + System.lineSeparator() +
            "PREFIX owl: <http://www.w3.org/2002/07/owl#> " + System.lineSeparator() +
            "PREFIX inst: <http://linkedbuildingdata.net/ifc/resources20210823_160347/> " + System.lineSeparator();

    @Test
    /**
     * zeigt Reasoning anhand des Alignments
     * aus C:\_DATEN\WORKSPACES\IntelliJ\jena-examples\src\main\java\org\apache\jena\examples\ExampleONT_01.java
     */
    public void Reasoning() throws FileNotFoundException {
        OntModel base = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
//        base.read( sourceURL, "RDF/XML" );

//        model6 ist merge aus IFC-Instanz + IFC-Standard (=model4) + Alignment
        ModelHelper.merge("model4.ttl", "c:\\_DATEN\\_FMI4BIM\\BIM\\Ontologien und Alignments\\6_AlignmentIFCModelica\\AlignmentModelicaIFC_220106.ttl", "model6.ttl");
        InputStream in = new FileInputStream("model6.ttl");
        base.read(in, "", "ttl");

        String namespace = "https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#";

        OntModel inf = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF, base );

//        OntClass pipeFittingClass = base.getOntClass( namespace + "IfcPipeFitting" );
//        Individual pipeFitting = base.createIndividual( namespace + "pf1", pipeFittingClass );

        OntClass spaceClass = base.getOntClass( namespace + "IfcSpace" );
        Individual space = base.createIndividual( namespace + "sp1", spaceClass );

        System.out.println("---- Assertions in the data ----");
        for (Iterator<Resource> i = space.listRDFTypes(false); i.hasNext(); ) {
            System.out.println( space.getURI() + " is a " + i.next() );
        }

        Property property = inf.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");

//        Individual individual2 = base.getIndividual("http://linkedbuildingdata.net/ifc/resources20210823_160347/IfcPipeFitting_763");

        System.out.println("\n---- Inferred assertions ----");
        Resource sp = base.getResource("https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcPipeFitting");
        ResIterator iterator = inf.listResourcesWithProperty(property, sp) ; //1. Versuch
        for (Iterator<Individual> pipeFittingIterator = inf.listIndividuals(sp); pipeFittingIterator.hasNext(); ) {
            Individual pipeFittingIndividual = pipeFittingIterator.next();
            LOGGER.info(pipeFittingIndividual.toString());
            for (Iterator<Resource> i = pipeFittingIndividual.listRDFTypes(false); i.hasNext(); ) {
                System.out.println(pipeFittingIndividual.getURI() + " is a " + i.next());
            }
        }
    }

    @Test
    /**
     * aus C:\_DATEN\WORKSPACES\IntelliJ\jena-examples\src\main\java\org\apache\jena\examples\ExampleONT_02.java
     * läuft in seiner Originalumgebung! (6.1.22)
     */
    public void validity() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());

        Model tbox = FileManager.get().loadModel("data/inference/tbox.owl", null, "RDF/XML"); // http://en.wikipedia.org/wiki/Tbox
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner().bindSchema(tbox.getGraph());
        Model abox = FileManager.get().loadModel("data/inference/abox.owl", null, "RDF/XML"); // http://en.wikipedia.org/wiki/Abox

        InfModel inf = ModelFactory.createInfModel(reasoner, abox);

        ValidityReport validityReport = inf.validate();

        if ( !validityReport.isValid() ) {
            System.out.println("Inconsistent");
            Iterator<ValidityReport.Report> iter = validityReport.getReports();
            while ( iter.hasNext() ) {
                ValidityReport.Report report = iter.next();
                System.out.println(report);
            }
        } else {
            System.out.println("Valid");
        }
    }

    @Test
    /**
     * fügt "Alice" und "Bob"-Triple zu einem beliebigen eingelesenen TTL-File hinzu und schreibt es anschließend wieder raus
     * funktioniert 24.8.21 10:30
     */
    public void addTriplesToFile() {
        InputStream in = null;
        try {
            in = new FileInputStream("c:\\TMP\\MoOnt.ttl");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        RIOT.init();

        Model model = ModelFactory.createDefaultModel(); // creates an in-memory Jena Model

        model.read(in, null, "TURTLE"); // parses an InputStream assuming RDF in Turtle format


        model.createResource("http://example.org/alice", FOAF.Person)
                .addProperty(FOAF.name, "Alice")
                .addProperty(FOAF.mbox, model.createResource("mailto:alice@example.org"))
                .addProperty(FOAF.knows, model.createResource("http://example.org/bob"));

        Resource alice2 = ResourceFactory.createResource("http://example.org/alice2");
        Resource bob2 = ResourceFactory.createResource("http://example.org/bob2");

        model.add(alice2, RDF.type, FOAF.Person);
        model.add(alice2, FOAF.name, "Alice");
        model.add(alice2, FOAF.mbox, ResourceFactory.createResource("mailto:alice2@example.org"));
        model.add(alice2, FOAF.knows, bob2);

        // Write the Jena Model in Turtle, RDF/XML and N-Triples format
        System.out.println("\n---- Turtle ----");
        model.write(System.out, "TURTLE");
    }

    @Test //funktioniert 24.8.21 10:34
    public void GenerateTDBFromNTFile() {
        InputStream in = null;
        try {
            in = new FileInputStream("c:\\TMP\\data.nt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Location location = Location.create("target/TDB");
        // Load some initial data (URL into dataset, Input=NQUADS)
        TDBLoader.load(TDBInternal.getBaseDatasetGraphTDB(TDBFactory.createDatasetGraph(location)), in, Lang.NQUADS, true, true);

        Dataset dataset = TDBFactory.createDataset(location);
        dataset.begin(ReadWrite.READ);
        //über das dataset iterieren
        try {
            Iterator<Quad> iter = dataset.asDatasetGraph().find();
            while (iter.hasNext()) {
                Quad quad = iter.next();
                System.out.println("Hallo");
                System.out.println(quad);
            }
        } finally {
            dataset.end();
        }
    }

    @Test
    public void querySimple() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());
        Model model = FileManager.get().loadModel("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl");

        String queryString =
                PREFIXSTRING +
                "SELECT "+
                    "(COUNT(?p) AS ?pcount) "+
                    "?p "+
                "WHERE {"+
                    "?s2 ?p ?s1 ."+
                    "?s1 rdf:type ifc:IfcCartesianPoint_List ."+
                "} GROUP BY ?p "+
                "ORDER BY desc(?pcount)";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        try {
            ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
//                Literal pcount = soln.getLiteral("pcount");
                RDFNode pcount = soln.get("pcount");
                RDFNode p = soln.get("p");
                System.out.println(pcount.toString() + p.toString());
            }
        } finally {
            qexec.close();
        }
    }


    @Test
    public void queryCartesianPointList() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());
        Model model = FileManager.get().loadModel("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl");
        //        Model model = FileManager.get().loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\deletedPath.ttl");
        ModelHelper.countTriples(model);

        String queryString =
                PREFIXSTRING +
                        "SELECT " +
                        //                        " ?p1 " +
                        //                        " ?p2 " +
                        //                        " ?at " +
                        //                        " ?ct " +
                        " ?b " +
                        "WHERE {" +
                        //                        "    ?a ?p1    ?b." +
                        //                        "    ?b ?p2    ?c." +
                        "    ?b     rdf:type      <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcCartesianPoint_List>." +
                        //                        "    ?a     rdf:type      ?at." +
                        //                        "    ?c     rdf:type      ?ct." +
                        "    }";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);

        int i=0;
        try {
            ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                System.out.println(
                        //                        soln.get("p1").toString() + "\t"+
                        //                        soln.get("p2").toString() + "\t"
                        //                        soln.get("at").toString() + "\t"
                        //                        soln.get("ct").toString() + "\t"
                );
                i++;
            }
        } finally {
            qexec.close();
        }
        System.out.println("Anzahl Ergebnisse:" + i);
    }


    @Test
    public void queryCartesianPointList2() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());
        Model model = FileManager.get().loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\model3.ttl");
        try {
            FileWriter myWriter = null;
            myWriter = new FileWriter("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\output_CartesianPointList.ttl");
            myWriter.write(ModelHelper.getTriplesByNodeClass(model, "<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcCartesianPoint_List>"));
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void queryPolyloop() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());
        Model model = FileManager.get().loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\model3.ttl");
        try {
            FileWriter myWriter = null;
            myWriter = new FileWriter("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\outputPolyLoop.ttl");
            myWriter.write(ModelHelper.getTriplesByNodeClass(model, "<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcPolyLoop>"));
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void queryIfcFaceOuterBound() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());
        Model model = FileManager.get().loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\model3.ttl");
        try {
            FileWriter myWriter = null;
            myWriter = new FileWriter("outputIfcFaceOuterBound.ttl");
            myWriter.write(ModelHelper.getTriplesByNodeClass(model, "<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcFaceOuterBound>"));
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
