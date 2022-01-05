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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rdf.helper.ModelEE;
import rdf.helper.ResultLine;
import rdf.helper.ResultSetEE;

import java.io.*;
import java.util.Iterator;
import java.util.List;

import static rdf.helper.serialize.serialize;
import static rdf.helper.serialize.stringToFile;

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
    public void Kaelte() {
        LOGGER.info("Start");
        String filepath1 = "c:\\_DATEN\\_FMI4BIM\\BIM\\Ontologien und Alignments\\2_IFC\\IFC4_ADD2_TC1__korrigiert2.ttl";
        String filepath2 = "c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl";
        String filename3 = "model3.ttl";
        String filename4 = "model4.ttl";

        Model model = RDFDataMgr.loadModel(filepath1);
        LOGGER.info("read "+filepath1+" with "+new ModelEE(model).countTriples() + " Triples");

        Model model2 = RDFDataMgr.loadModel(filepath2);
        LOGGER.info("read "+filepath2+" with "+new ModelEE(model2).countTriples() + " Triples");

        Model model3 = model.add(model2);
        LOGGER.info("merged to "+new ModelEE(model3).countTriples() + " Triples");


        Model model4 = deleteSubClassNodes(model3, "<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcRepresentationItem>");
//        model4 = deleteSubClassNodes(model4, "<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcBuildingElement>"); //macht keine Änderung
//        model4 = deleteSubClassNodes(model4, "<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#NOTDEFINED>"); //macht keine Änderung
        model4 = deleteSubClassNodes(model4, "<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcLengthMeasure_List>");
        model4 = deleteSubClassNodes(model4, "<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcLengthMeasure>");
//        model4 = deleteByPredicate(model4, "owl:onProperty");
        model4 = deleteByPredicateNamesspace(model4, "http://www.w3.org/2002/07/owl#", "owl"); //TODO umbauen, so dass man nur eins von beiden angeben muss
        ModelEE model4ee = new ModelEE(model4);
        LOGGER.info("reduced to "+model4ee.countTriples() + " Triples");
        serialize(model4, filename4);
        LOGGER.info("serialized as "+filename4);

        LOGGER.info(model4ee.countDistinctPredicates());
        LOGGER.info(model4ee.countDistinctNodes());
//        LOGGER.info(model4ee.countNodesByClass(150));

        stringToFile(model4ee.countPredicatesByName(150), "model4_countPredicatesByName.txt");
        LOGGER.info("printed countPredicatesByName to model4_countPredicatesByName.txt");

        ResultSetEE rse = model4ee.countPredicatesByName2(150);
        LOGGER.info("Predicates - NamespaceCountSorted:");
        rse.printNamespaceCountSorted();

        LOGGER.info("calculateNodedegrees");
        model4ee.calculateNodeDegrees(); //ausfüllen der Node2DegreeMap am model4ee
        stringToFile(model4ee.printNode2DegreeMap(), "model4_nodedegrees.txt");
        LOGGER.info("printed Node2DegreeMap to model4_nodedegrees.txt");

    }
    @Test
    public void Kaelte2() {
        LOGGER.info("Start");
        String filepath1 = "c:\\_DATEN\\_FMI4BIM\\BIM\\Ontologien und Alignments\\2_IFC\\IFC4_ADD2_TC1.ttl";
        String filepath2 = "c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl";
        String filename3 = "model3.ttl";

        Model model = RDFDataMgr.loadModel(filepath1);
        LOGGER.info("read " + filepath1 + " with " + new ModelEE(model).countTriples());

        System.out.println(model.listSubjects().next());
        System.out.println(model.listObjects().next());

        System.out.println(model.listStatements().next().getSubject());
        System.out.println(model.listStatements().next().getPredicate());
        System.out.println(model.listStatements().next().getObject());
//
//        model4.listResourcesWithProperty(new ObjectPropertyImpl());
//        Property
//
//
//        private void acquireClasses() {
//            ResIterator it = null;
//
//            if (m_inferred) {
//                it = m_inferred_model.listResourcesWithProperty(RDF.type, OWL.Class);
//            } else {
//                it = m_raw_model.listResourcesWithProperty(RDF.type, OWL.Class);
//            }
//
//            m_classes = acquireResources(it);
//        }
////
//        Property property = model.createProperty(RDF_TYPE);
////
//        ResIterator iterator = model.listSubjectsWithProperty(getPropertyFromUri("<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcRepresentationItem>"));
//        ResourceFactory.createResource("http://www.w3.org/2011/http-statusCodes#BadRequest"));
    }

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
    public void stats() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());
//        String filename = "210823_KälteErzeugung_MU";
//        Model model = FileManager.get().loadModel("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl");
        String filename = "deletedSubClassOfGeometricRepresentation";
//        Model model = FileManager.get().loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\deletedPath2.ttl");
        Model model = FileManager.get().loadModel("deletedSubClassOfGeometricRepresentation.ttl");
        System.out.println(new ModelEE(model).countNodesByClass(20));
//        stats(model, filename);
    }

    @Test
    public void queryCartesianPointList2() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());
        Model model = FileManager.get().loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\model3.ttl");
        try {
            FileWriter myWriter = null;
            myWriter = new FileWriter("output.ttl");
            myWriter.write(new ModelEE(model).getTriplesByNodeClass("<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcCartesianPoint_List>"));
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
            myWriter = new FileWriter("outputPolyLoop.ttl");
            myWriter.write(new ModelEE(model).getTriplesByNodeClass("<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcPolyLoop>"));
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
            myWriter.write(new ModelEE(model).getTriplesByNodeClass("<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcFaceOuterBound>"));
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void queryCartesianPointList() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());
        Model model = FileManager.get().loadModel("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl");
        //        Model model = FileManager.get().loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\deletedPath.ttl");
        new ModelEE(model).countTriples();

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
    public void queryPropertyPath() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());
        Model model = FileManager.get().loadModel("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl");
//        Model model = FileManager.get().loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\deletedPath.ttl");
        new ModelEE(model).countTriples();

        // language=sparql
        String queryString =
                PREFIXSTRING +
                "SELECT " +
                    " ?start " +
                    " ?ende " +
                "WHERE {" +
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
                    "}";

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
                PREFIXSTRING +
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
        new ModelEE(model).countTriples();

        String queryString =
                PREFIXSTRING +
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
        new ModelEE(model).countTriples();

        String queryString =
                PREFIXSTRING +
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
        new ModelEE(model).countTriples();
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
        new ModelEE(model).countTriples();
        qexec.execute();
    }

    @Test
    public void deletePropertyPath() {
        Dataset dataset = RDFDataMgr.loadDataset("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl") ;
        new ModelEE(dataset.getDefaultModel()).countTriples();
        serialize(dataset, "original.ttl" );
        UpdateRequest request = UpdateFactory.create() ;
        request.add(
                PREFIXSTRING +
                "DELETE WHERE {" +
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
        new ModelEE(dataset.getDefaultModel()). countTriples();
        serialize(dataset, "deletedPath.ttl" );
    }

    @Test
    public void deletePropertyPath2() {
        Dataset dataset = RDFDataMgr.loadDataset("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl") ;
        new ModelEE(dataset.getDefaultModel()).countTriples();
        serialize(dataset, "original.ttl" );
        UpdateRequest request = UpdateFactory.create() ;
        request.add(
                PREFIXSTRING +
                "DELETE WHERE {" +
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
        new ModelEE(dataset.getDefaultModel()).countTriples();
        serialize(dataset, "deletedPath2.ttl" );
    }

    @Test
    public void deleteSubClassesShort() {
        Dataset dataset = RDFDataMgr.loadDataset("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\model3.ttl") ;
        serialize(dataset, "original.ttl");
        System.out.println(new ModelEE(dataset.getDefaultModel()).countTriples());
        UpdateRequest request = UpdateFactory.create() ;
        request.add(
                PREFIXSTRING +
                "DELETE  WHERE {" +
                "    ?s    ?p ?a." +
                "    ?a    rdf:type ?sc1." +
//                "    ?sc rdfs:subClassOf ?sc1." +
                "    ?sc1 rdfs:subClassOf <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcGeometricRepresentationItem>." +
//                "    ?sc rdfs:subClassOf/rdfs:subClassOf <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcGeometricRepresentationItem>." + --> funktioniert nicht
                "} ");
        // And perform the operations.
        UpdateAction.execute(request, dataset) ;
        System.out.println(new ModelEE(dataset.getDefaultModel()).countTriples());
        serialize(dataset, "deletedSubClassOfGeometricRepresentationShort.ttl" );
    }

    @Test
    public void deleteSubClasses() {
        Dataset dataset = RDFDataMgr.loadDataset("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\model3.ttl") ;
        serialize(dataset, "original.ttl");
        System.out.println(new ModelEE(dataset.getDefaultModel()).countTriples());
        UpdateRequest request = UpdateFactory.create() ;
        request.add(
                PREFIXSTRING +
                "DELETE {" +
                "?s ?p ?a." +
                "} WHERE {" +
                " {?s ?p ?a.}" +
                "?a rdf:type ?sc." +
                //                    "?sc rdfs:subClassOf* <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcGeometricRepresentationItem>." +
                "?sc rdfs:subClassOf* <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcRepresentationItem>." +
                "} ");
        // And perform the operations.
        UpdateAction.execute(request, dataset) ;
        System.out.println(new ModelEE(dataset.getDefaultModel()).countTriples());
        request.add(
                PREFIXSTRING +
                "DELETE {" +
                "?a ?p ?o." +
                "} WHERE {" +
                        "{?a ?p ?o.}" +
                    "?a rdf:type ?sc." +
//                    "?sc rdfs:subClassOf* <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcGeometricRepresentationItem>." +
                    "?sc rdfs:subClassOf* <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcRepresentationItem>." +
                "} ");
        // And perform the operations.
        UpdateAction.execute(request, dataset) ;
        System.out.println(new ModelEE(dataset.getDefaultModel()).countTriples());
        serialize(dataset, "deletedSubClassOfGeometricRepresentation.ttl" );
        System.out.println(new ModelEE(dataset.getDefaultModel()).countNodesByClass(20));
    }

    /**
     * löscht alle Triple deren Subjekt oder Objekt vom Typ fatherClass ist (auch indirekt)
     * @param model
     * @param fatherClass
     * @return
     */
    public Model deleteSubClassNodes(Model model, String fatherClass) {
        UpdateRequest request = UpdateFactory.create();
        request.add(
                PREFIXSTRING +
                "DELETE {" +
                        "?a ?p ?o." +
                        "?s ?p ?a." +
                "} WHERE {" +
                        "{?a ?p ?o.}" +
                    "UNION" +
                        " {?s ?p ?a.}" +
                    "?a rdf:type ?sc." +
                    "?sc rdfs:subClassOf* " + fatherClass + "." +
                "}");
        // And perform the operations.
        UpdateAction.execute(request, model);
        return model;
    }

    /**
     * löscht alle Triple, die ein bestimmtes Prädikat haben
     * @param model
     * @param predicate
     * @return
     */
    public Model deleteByPredicate(Model model, String predicate) {
        UpdateRequest request = UpdateFactory.create();
        request.add(
                PREFIXSTRING +
                    "DELETE {" +
                        "?s ?p ?o." +
                    "} WHERE {" +
                        "?s ?p ?o." +
                        "?s " + predicate + " ?o." +
                    "}");
        // And perform the operations.
        UpdateAction.execute(request, model);
        return model;
    }

    /**
     * löscht alle Triple mit Prädikaten eines bestimmten Namespace
     * indem es mehrfahr die Funktion deletByPredicate aufruft
     * //TODO als große SPARQL query implementieren
     * @param model
     * @param namespace
     * @return
     */
    public Model deleteByPredicateNamesspace(Model model, String namespace, String namespacekuerzel) {
        ModelEE modelee = new ModelEE(model);
        ResultSetEE rse = modelee.countPredicatesByName2(-1);
        List<ResultLine> predicateList = rse.getResultLinesGroupedByNamespace().get(namespace);
        for (ResultLine predicate:predicateList) {
                model = deleteByPredicate(model, namespacekuerzel + ":" + predicate.name);
        }
        return model;
    }

    @Test
    public void deleteSimple() {
//        Dataset dataset = RDFDataMgr.loadDataset("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl") ;
        Dataset dataset = RDFDataMgr.loadDataset("model4.ttl") ;
        System.out.println(new ModelEE(dataset.getDefaultModel()).countTriples());
//        serialize(dataset, "original.ttl" );
        UpdateRequest request = UpdateFactory.create() ;
        request.add(PREFIXSTRING);
//        request.add("DELETE WHERE {?s rdf:type ?o.} ");
        request.add("DELETE WHERE {?s owl:onProperty ?o.} ");
        // And perform the operations.
        UpdateAction.execute(request, dataset) ;
        System.out.println(new ModelEE(dataset.getDefaultModel()).countTriples());
        serialize(dataset, "deletedSimple.ttl" );
    }

    @Test
    public void merge() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());
        Model model = FileManager.get().loadModel("c:\\_DATEN\\_FMI4BIM\\BIM\\Ontologien und Alignments\\2_IFC\\IFC4_ADD2_TC1.ttl");
        System.out.println(new ModelEE(model).countTriples());
        Model model2 = FileManager.get().loadModel("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl");
        System.out.println(new ModelEE(model2).countTriples());
        Model model3 = model.add(model2);
        System.out.println(new ModelEE(model3).countTriples());
        serialize(model3, "model3.ttl");
    }




}
