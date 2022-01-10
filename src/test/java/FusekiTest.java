import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.fuseki.server.DataService;
import org.apache.jena.query.*;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.jena.system.Txn;
import org.apache.jena.update.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;

/**
 * Klasse enthält diverse Tests zur Arbeit mit Fuseki Server, basierend auf den Examples vom Jena-Projekt: c:\_DATEN\WORKSPACES\IntelliJ\jena-examples\
 */
public class FusekiTest {

    /**
     * Example connection to Fuseki. For this to work, you need to start a local
     * Fuseki server like this: ./fuseki-server --update --mem /ds
     */


    @Test
    /**
     * funktioniert am 24.8.21 10:15 mit Matthias KLT-IFC-Modell, auch mit diesem "Riesenmodell", dauert 3s
     */
    public void Select() {
//        String queryString = "SELECT * { ?s ?p ?o }";
        String queryString = "SELECT DISTINCT ?p WHERE { ?s ?p ?o }";
        Query query = QueryFactory.create(queryString);
//        QueryEngineHTTP qexec = (QueryEngineHTTP) QueryExecutionFactory.createServiceRequest("http://localhost:8080/fuseki/NeubauKLT1", query);
        QueryEngineHTTP qexec = (QueryEngineHTTP) QueryExecutionFactory.createServiceRequest("http://localhost:8080/fuseki/test2", query);
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
    /**
     * er zeigt "Process finished with exit code 0" - Abfrage über WebGUI des Fuseki und das nächste SelectStatement zeigt, dass es funktioniert hat
     */
    public void UpdateInsert() {
        String queryString = "prefix aix: <http://fraunhofer.w3.org/2002/07/aix#> INSERT DATA  { aix:testel2 <http://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#location_IfcPlacement> aix:blablablaIntelli3. }";
        UpdateRequest update = UpdateFactory.create(queryString);
//        UpdateProcessor qexec = UpdateExecutionFactory.createRemote(update, "http://localhost:8080/fuseki/FusekiTest4/update");
        UpdateProcessor qexec = UpdateExecutionFactory.createRemote(update, "http://localhost:8080/fuseki/NeubauKLT1/update");
        qexec.execute();
    }


    @Test
    /**
     * vorher: test2 hat Triples
     * Ergebnis: "Process finished with exit code 0", lt Tomcat Log ist etwas passiert, Abfrage mit Weboberfläche im Anschluss zeigt, dass keine Triples mehr da sind
     */
    public void UpdateDrop() {
        String queryString = "DROP ALL";
        UpdateRequest update = UpdateFactory.create(queryString);
        //        UpdateProcessor qexec = UpdateExecutionFactory.createRemote(update, "http://localhost:8080/fuseki/FusekiTest4/update");
        UpdateProcessor qexec = UpdateExecutionFactory.createRemote(update, "http://localhost:8080/fuseki/test2/update");
        qexec.execute();
    }
    @Test
    /**
     * beim ersten Versuche http://example dauert es ein paar Sekunden, bevor es endet, man kann aber kein Ergebnis sehen
     * beim zweiten VErsuche "graph2" kommt ein Fehler
     */
    public void UpdateCreate() {
//        String queryString = "CREATE GRAPH <http://example/g2>";
        String queryString = "CREATE GRAPH graph2";
        UpdateRequest update = UpdateFactory.create(queryString);
        //        UpdateProcessor qexec = UpdateExecutionFactory.createRemote(update, "http://localhost:8080/fuseki/FusekiTest4/update");
        UpdateProcessor qexec = UpdateExecutionFactory.createRemote(update, "http://localhost:8080/fuseki/test2/update");
        qexec.execute();
    }

    @Test
    /**
     * funktioniert 24.8.21 18:16
     */
    public void UpdateLoad() {
//        String queryString = "LOAD <file:klterz_20210824_114856.ttl>";
        String queryString = "LOAD <file:klterz_minload.ttl>";
        UpdateRequest update = UpdateFactory.create(queryString);
        UpdateProcessor qexec = UpdateExecutionFactory.createRemote(update, "http://localhost:8080/fuseki/test2/update");
        qexec.execute();
    }

    @Test
    /**
     * Fehler: org.apache.jena.query.QueryParseException: Encountered " <IRIref> "<http://example/g2> "" at line 1, column 45. Was expecting:  "graph" ...
     */
    public void UpdateLoadInto() {
        String queryString = "LOAD <file:klterz_20210824_114856.ttl> INTO <http://example/g2>";
        UpdateRequest update = UpdateFactory.create(queryString);
        UpdateProcessor qexec = UpdateExecutionFactory.createRemote(update, "http://localhost:8080/fuseki/test2/update");
        qexec.execute();
    }


    @Test
    /**
     * funktioniert am 24.8.21 10:26 mit Matthias KLT-IFC-Modell
     */
    public void SelectComplex() {
        String queryString = "SELECT ?s ?o { ?s <http://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#location_IfcPlacement> ?o }";
        Query query = QueryFactory.create(queryString);
        QueryEngineHTTP qexec = (QueryEngineHTTP) QueryExecutionFactory.createServiceRequest("http://localhost:8080/fuseki/NeubauKLT1", query);
        try {
            ResultSetRewindable results = ResultSetFactory.makeRewindable(qexec.execSelect());
            System.out.println("---- Text ----");
            ResultSetFormatter.out(System.out, results);
            results.reset();

        } finally {
            qexec.close();
        }
    }



}
