import model.MClass;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RIOT;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.tdb.sys.TDBInternal;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;
import mo.parser.ModelicaFileAntlrParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

import java.util.UUID;

public class ModelicaFileAntlrParserTest {
    private ModelicaFileAntlrParser parser = new ModelicaFileAntlrParser("test");

    @Test
    public void testAntlrParser() throws IOException {
        String content = Files.readString(Paths.get("c:\\TMP\\AixLib\\Fluid\\Chillers\\Examples\\Carnot_TEva.mo"));
        Set<MClass> result = parser.parseFile(content);
//        assertEquals(3, result.size());
    }

    /**
     * Example connection to Fuseki. For this to work, you need to start a local
     * Fuseki server like this: ./fuseki-server --update --mem /ds
     */

    /** A template for creating a nice SPARUL query */
    private static final String UPDATE_TEMPLATE =
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
                    + "INSERT DATA"
                    + "{ <http://example/%s>    dc:title    \"A new book\" ;"
                    + "                         dc:creator  \"A.N.Other\" ." + "}   ";



    @Test
    public void testJenaFileReadingAndChanging () {
        InputStream in = null;
        try {
            in = new FileInputStream("c:\\TMP\\MoOnt.ttl");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        RIOT.init();

        Model model = ModelFactory.createDefaultModel(); // creates an in-memory Jena Model

        model.read(in, null, "TURTLE"); // parses an InputStream assuming RDF in Turtle format

        // Write the Jena Model in Turtle, RDF/XML and N-Triples format
        System.out.println("\n---- Turtle ----");
        System.out.println("\n---- Turtle ----");

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

        model.write(System.out, "TURTLE");
    }
    @Test
    public void testJenaGenerateTDB() {
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
    public void testFusekiSelect() {
        String queryString = "SELECT * { ?s ?p ?o }";
        Query query = QueryFactory.create(queryString);
        QueryEngineHTTP qexec = (QueryEngineHTTP)QueryExecutionFactory.createServiceRequest("http://localhost:8080/fuseki/FusekiTest4", query);
        try {
            ResultSetRewindable results = ResultSetFactory.makeRewindable(qexec.execSelect());
            System.out.println("---- Text ----");
            ResultSetFormatter.out(System.out, results);
            results.reset();

        } finally {
            qexec.close();
        }
    }

    @Test
    public void testFusekiUpdate () {
        String queryString = "prefix aix: <http://fraunhofer.w3.org/2002/07/aix#> INSERT DATA  { aix:testel2 aix:predi aix:blablablaIntelli3. }";
        UpdateRequest update = UpdateFactory.create(queryString);
        UpdateProcessor qexec = UpdateExecutionFactory.createRemote(update, "http://localhost:8080/fuseki/FusekiTest4/update");
        qexec.execute();

    }
}
