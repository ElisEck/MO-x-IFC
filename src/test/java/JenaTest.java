import org.apache.commons.lang3.ObjectUtils;
import org.apache.jena.graph.Graph;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RIOT;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.tdb.sys.TDBInternal;
import org.apache.jena.update.*;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

import java.io.*;
import java.util.Iterator;

import static rdf.helper.serialize.serialize;
import static rdf.helper.stats.*;

public class JenaTest {

    @Test //funktioniert 24.8.21 10:30
    public void FileReadingAndChanging () {
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
                "PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#> " +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
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
    public void stats() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());
//        String filename = "210823_KälteErzeugung_MU";
//        Model model = FileManager.get().loadModel("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl");
        String filename = "deletedSubClassOfGeometricRepresentation";
//        Model model = FileManager.get().loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\deletedPath2.ttl");
        Model model = FileManager.get().loadModel("deletedSubClassOfGeometricRepresentation.ttl");
        System.out.println(countNodesByClass(model, 20));
//        stats(model, filename);
    }

    @Test
    public void queryCartesianPointList() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());
        Model model = FileManager.get().loadModel("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl");
        //        Model model = FileManager.get().loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\deletedPath.ttl");
        countTriples(model);

        String queryString =
                "PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#> " +
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                        "PREFIX list: <https://w3id.org/list#> " +
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
    public void queryPropertyPath() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());
        Model model = FileManager.get().loadModel("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl");
//        Model model = FileManager.get().loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\deletedPath.ttl");
        countTriples(model);

        String queryString =
                "PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#> " +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX list: <https://w3id.org/list#> " +
                "SELECT " +
                    " ?start " +
                    " ?ende " +
                "WHERE {" +
                "    ?start ifc:representations_IfcProductRepresentation    ?b." +
                "    ?b     list:hasContents                                ?c." +
                "    ?c     ifc:items_IfcRepresentation                     ?d." +
                "    ?d     ifc:mappingSource_IfcMappedItem                 ?e." +
                "    ?e     ifc:mappedRepresentation_IfcRepresentationMap   ?f." +
                "    ?f     ifc:items_IfcRepresentation                     ?g." +
                "    ?g     ifc:outer_IfcManifoldSolidBrep                  ?h." +
                "    ?h     ifc:cfsFaces_IfcConnectedFaceSet                ?i." +
                "    ?i     ifc:bounds_IfcFace                              ?k." +
                "    ?k     ifc:bound_IfcFaceBound                          ?l." +
                "    ?l     ifc:polygon_IfcPolyLoop                         ?ende." +
                "    }";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);

        try {
            ResultSet results = qexec.execSelect();
            int i=0;
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                System.out.println(
                        soln.get("start").toString() +
                        soln.get("ende").toString());
                i++;
            }
            System.out.println("Anzahl Ergebnisse:" + i);
        } finally {
            qexec.close();
        }
    }

    @Test
    public void querySubClasses() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());
        Model model = FileManager.get().loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\model3.ttl");
        String queryString =
                "PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#> " +
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                        "PREFIX list: <https://w3id.org/list#> " +
                        "SELECT " +
                        " ?a " +
//                        " ?s " +
//                        " ?p " +
//                        " ?o " +
                        "WHERE {" +
//                        "{    ?a    ?p ?o.}" +
//                        "UNION"+
                        " {   ?s    ?p ?a.}" +
                        "    ?a    rdf:type ?sc." +
//                        "    ?a rdfs:subClassOf ifc:IfcGeometricRepresentationItem." +
//                        "    ?sc rdfs:subClassOf* <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcGeometricRepresentationItem>." +
//                        "    ?sc rdfs:subClassOf <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcGeometricRepresentationItem>." +
//                        "    ?sc rdfs:subClassOf* <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcTopologicalRepresentationItem>." +
                        "    ?sc rdfs:subClassOf* <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcRepresentationItem>." +
                        "    }";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);

        try {
            ResultSet results = qexec.execSelect();
            int i=0;
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
//                try{System.out.print(soln.get("a").toString() + "\t");} catch (NullPointerException np) {System.out.print("\t"); };
//                try{System.out.print(soln.get("p").toString() + "\t");} catch (NullPointerException np) {System.out.print("\t"); };
//                try{System.out.print(soln.get("p1").toString() + "\t");} catch (NullPointerException np) {System.out.print("\t"); };
//                try{System.out.print(soln.get("o").toString() + "\t");} catch (NullPointerException np) {System.out.print("\t"); };
//                try{System.out.print(soln.get("s1").toString() + "\t");} catch (NullPointerException np) {System.out.print("\t"); };
                i++;
            }
            System.out.println("Anzahl Ergebnisse:" + i);
        } finally {
            qexec.close();
        }
    }

    @Test
    public void queryPropertyPath2() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());
        Model model = FileManager.get().loadModel("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl");
//                Model model = FileManager.get().loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\deletedPath.ttl");
        countTriples(model);

        String queryString =
                "PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#> " +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX list: <https://w3id.org/list#> " +
                "SELECT " +
                    " ?a " +
                    " ?l " +
                    " ?ktype " +
                "WHERE {" +
                    "?a ifc:representations_IfcProductRepresentation ?b. " +
                    "?b list:hasContents ?c. " +
                    "?c ifc:items_IfcRepresentation ?d ." +
                    "?d ifc:mappingSource_IfcMappedItem ?e. " +
                    "?e ifc:mappedRepresentation_IfcRepresentationMap ?f. " +
                    "?f ifc:items_IfcRepresentation ?g ." +
                    "?g ifc:sbsmBoundary_IfcShellBasedSurfaceModel ?h. " +
                    "?h ifc:cfsFaces_IfcConnectedFaceSet ?i. " +
                    "?i ifc:bounds_IfcFace ?k ." +
                    "?k rdf:type ?ktype. " +
                    "?k ifc:orientation_IfcFaceBound ?l. " +

//                    "?l ifc:bound_IfcFaceBound ?m ." +
//                    "?m ifc:polygon_IfcPolyLoop ?n." +
                "    }";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);

        try {
            ResultSet results = qexec.execSelect();
            int i=0;
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                System.out.println(
                        soln.get("a").toString() +
                        soln.get("ktype").toString()
//                                soln.get("n").toString()
                                                                );
                i++;
            }
            System.out.println("Anzahl Ergebnisse:" + i);
        } finally {
            qexec.close();
        }
    }

    @Test
    public void probeLoeschergebnis() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());
        Model model = FileManager.get().loadModel("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl");
        //        Model model = FileManager.get().loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\deletedPath.ttl");
        countTriples(model);

        String queryString =
                "PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#> " +
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                        "PREFIX list: <https://w3id.org/list#> " +
                        "SELECT " +
                        " ?start " +
                        " ?b " +
                        " ?c " +
                        " ?d " +
                        " ?e " +
                        " ?f " +
                        " ?g " +
                        " ?h " +
                        " ?i " +
                        " ?k " +
                        " ?l " +
                        " ?ende " +
                        "WHERE {" +
                        "    ?start ifc:representations_IfcProductRepresentation    ?b." +
                        "    ?b     list:hasContents                                ?c." +
                        "    ?c     ifc:items_IfcRepresentation                     ?d." +
                        "    ?d     ifc:mappingSource_IfcMappedItem                 ?e." +
                        "    ?e     ifc:mappedRepresentation_IfcRepresentationMap   ?f." +
                        "    ?f     ifc:items_IfcRepresentation                     ?g." +
                        "    ?g     ifc:outer_IfcManifoldSolidBrep                  ?h." +
                        "    ?h     ifc:cfsFaces_IfcConnectedFaceSet                ?i." +
                        "    ?i     ifc:bounds_IfcFace                              ?k." +
                        "    ?k     ifc:bound_IfcFaceBound                          ?l." +
                        "    ?l     ifc:polygon_IfcPolyLoop                         ?ende." +
                        "    }";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);

        try {
            FileWriter myWriter = new FileWriter("löschergebnis.txt");
            ResultSet results = qexec.execSelect();
            int i=0;
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                myWriter.write(
                        soln.get("start").toString() +
                                soln.get("b").toString() +
                                soln.get("c").toString() +
                                soln.get("d").toString() +
                                soln.get("e").toString() +
                                soln.get("f").toString() +
                                soln.get("g").toString() +
                                soln.get("h").toString() +
                                soln.get("i").toString() +
                                soln.get("k").toString() +
                                soln.get("l").toString() +
                                soln.get("ende").toString() + System.lineSeparator());
                i++;
            }
            System.out.println("Anzahl Ergebnisse:" + i);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            qexec.close();
        }
    }

    @Test
    public void deletePropertyPathALT() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());
        Model model = FileManager.get().loadModel("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl");
        countTriples(model);
        String queryString =
                        "DELETE " +
                        "WHERE {" +
                        "    ?start ifc:representations_IfcProductRepresentation    ?b." +
                        "    ?b     list:hasContents                                ?c." +
                        "    ?c     ifc:items_IfcRepresentation                     ?d." +
                        "    ?d     ifc:mappingSource_IfcMappedItem                 ?e." +
                        "    ?e     ifc:mappedRepresentation_IfcRepresentationMap   ?f." +
                        "    ?f     ifc:items_IfcRepresentation                     ?g." +
                        "    ?g     ifc:outer_IfcManifoldSolidBrep                  ?h." +
                        "    ?h     ifc:cfsFaces_IfcConnectedFaceSet                ?i." +
                        "    ?i     ifc:bounds_IfcFace                              ?k." +
                        "    ?k     ifc:bound_IfcFaceBound                          ?l." +
                        "    ?l     ifc:polygon_IfcPolyLoop                         ?ende." +
                        "    }";

        Query query = QueryFactory.create(queryString);
        UpdateRequest update = UpdateFactory.create(queryString);
        Graph graph = model.getGraph();
        UpdateProcessor qexec = UpdateExecutionFactory.create(update, (DatasetGraph) graph);
        countTriples(model);
        qexec.execute();
    }

    @Test
    public void deletePropertyPath() {
        Dataset dataset = RDFDataMgr.loadDataset("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl") ;
        countTriples(dataset);
        serialize(dataset, "original.ttl" );
        UpdateRequest request = UpdateFactory.create() ;
        request.add("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
        request.add("PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#>");
        request.add("PREFIX list: <https://w3id.org/list#>");
        request.add("DELETE WHERE {" +
                "?start ifc:representations_IfcProductRepresentation    ?b." +
                "?b     list:hasContents                                ?c." +
                "?c     ifc:items_IfcRepresentation                     ?d." +
                "?d     ifc:mappingSource_IfcMappedItem                 ?e." +
                "?e     ifc:mappedRepresentation_IfcRepresentationMap   ?f." +
                "?f     ifc:items_IfcRepresentation                     ?g." +
                "?g     ifc:outer_IfcManifoldSolidBrep                  ?h." +
                "?h     ifc:cfsFaces_IfcConnectedFaceSet                ?i." +
                "?i     ifc:bounds_IfcFace                              ?k." +
                "?k     ifc:bound_IfcFaceBound                          ?l." +
                "?l     ifc:polygon_IfcPolyLoop                         ?ende." +
                "} ");
        // And perform the operations.
        UpdateAction.execute(request, dataset) ;
        countTriples(dataset);
        serialize(dataset, "deletedPath.ttl" );
    }

    @Test
    public void deletePropertyPath2() {
        Dataset dataset = RDFDataMgr.loadDataset("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl") ;
        countTriples(dataset);
        serialize(dataset, "original.ttl" );
        UpdateRequest request = UpdateFactory.create() ;
        request.add("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
        request.add("PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#>");
        request.add("PREFIX list: <https://w3id.org/list#>");
        request.add("DELETE WHERE {" +
                "?a ifc:representations_IfcProductRepresentation ?b. " +
                "?b list:hasContents ?c. " +
                "?c ifc:items_IfcRepresentation ?d ." +
                "?d ifc:mappingSource_IfcMappedItem ?e. " +
                "?e ifc:mappedRepresentation_IfcRepresentationMap ?f. " +
                "?f ifc:items_IfcRepresentation ?g ." +
                "?g ifc:sbsmBoundary_IfcShellBasedSurfaceModel ?h. " +
                "?h ifc:cfsFaces_IfcConnectedFaceSet ?i. " +
                "?i ifc:bounds_IfcFace ?k ." +
                "?k ifc:orientation_IfcFaceBound ?l. " +
                "} ");
        // And perform the operations.
        UpdateAction.execute(request, dataset) ;
        countTriples(dataset);
        serialize(dataset, "deletedPath2.ttl" );
    }

    @Test
    public void deleteSubClassesShort() {
        Dataset dataset = RDFDataMgr.loadDataset("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\model3.ttl") ;
        serialize(dataset, "original.ttl");
        System.out.println(countTriples(dataset));
        UpdateRequest request = UpdateFactory.create() ;
        request.add("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
        request.add("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");
        request.add("PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#>");
        request.add("PREFIX list: <https://w3id.org/list#>");
        request.add("DELETE  WHERE {" +
                "    ?s    ?p ?a." +
                "    ?a    rdf:type ?sc1." +
//                "    ?sc rdfs:subClassOf ?sc1." +
                "    ?sc1 rdfs:subClassOf <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcGeometricRepresentationItem>." +
//                "    ?sc rdfs:subClassOf/rdfs:subClassOf <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcGeometricRepresentationItem>." + --> funktioniert nicht
                "} ");
        // And perform the operations.
        UpdateAction.execute(request, dataset) ;
        System.out.println(countTriples(dataset));
        serialize(dataset, "deletedSubClassOfGeometricRepresentationShort.ttl" );
    }

    @Test
    public void deleteSubClasses() {
        Dataset dataset = RDFDataMgr.loadDataset("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\model3.ttl") ;
        serialize(dataset, "original.ttl");
        System.out.println(countTriples(dataset));
        UpdateRequest request = UpdateFactory.create() ;
        request.add("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
        request.add("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");
        request.add("PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#>");
        request.add("PREFIX list: <https://w3id.org/list#>");
        request.add("DELETE {" +
                "?s ?p ?a." +
                "} WHERE {" +
                " {?s ?p ?a.}" +
                "?a rdf:type ?sc." +
                //                    "?sc rdfs:subClassOf* <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcGeometricRepresentationItem>." +
                "?sc rdfs:subClassOf* <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcRepresentationItem>." +
                "} ");
        // And perform the operations.
        UpdateAction.execute(request, dataset) ;
        System.out.println(countTriples(dataset));
        request.add("DELETE {" +
                "?a ?p ?o." +
                "} WHERE {" +
                        "{?a ?p ?o.}" +
                    "?a rdf:type ?sc." +
//                    "?sc rdfs:subClassOf* <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcGeometricRepresentationItem>." +
                    "?sc rdfs:subClassOf* <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcRepresentationItem>." +
                "} ");
        // And perform the operations.
        UpdateAction.execute(request, dataset) ;
        System.out.println(countTriples(dataset));
        serialize(dataset, "deletedSubClassOfGeometricRepresentation.ttl" );
        System.out.println(countNodesByClass(dataset.getDefaultModel(), 20));
    }


    @Test
    public void deleteSimple() {
        Dataset dataset = RDFDataMgr.loadDataset("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl") ;
        countTriples(dataset);
        serialize(dataset, "original.ttl" );
        UpdateRequest request = UpdateFactory.create() ;
        request.add("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
        request.add("DELETE WHERE {?s rdf:type ?o.} ");
        // And perform the operations.
        UpdateAction.execute(request, dataset) ;
        countTriples(dataset);
        serialize(dataset, "deletedSimple.ttl" );
    }

    @Test
    public void merge() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());
        Model model = FileManager.get().loadModel("c:\\_DATEN\\_FMI4BIM\\BIM\\Ontologien und Alignments\\2_IFC\\IFC4_ADD2_TC1.ttl");
        System.out.println(countTriples(model));
        Model model2 = FileManager.get().loadModel("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl");
        System.out.println(countTriples(model2));
        Model model3 = model.add(model2);
        System.out.println(countTriples(model3));
        serialize(model3, "model3.ttl");
    }

    public void stats(Model model, String filename) {
        try {
            FileWriter myWriter = new FileWriter(filename+"_STATS.txt");
            myWriter.write(countTriples(model)+System.lineSeparator());
            myWriter.write(countDistinctSubjects(model)+System.lineSeparator());
            myWriter.write(countDistinctObjects(model)+System.lineSeparator());
            myWriter.write(countDistinctNodes(model)+System.lineSeparator());
            myWriter.write(countDistinctPredicates(model)+System.lineSeparator());
            myWriter.write(countNodeClasses(model)+System.lineSeparator());
            myWriter.write(countObjectClasses(model)+System.lineSeparator());
            myWriter.write(countSubjectClasses(model)+System.lineSeparator());
            myWriter.write(countPredicatesByName(model, 120)+System.lineSeparator());
            myWriter.write(countNodesByClass(model, 80)+System.lineSeparator());
            myWriter.write(countObjectsByClass(model, 80)+System.lineSeparator());
            myWriter.write(countSubjectsByClass(model, 80)+System.lineSeparator());
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
