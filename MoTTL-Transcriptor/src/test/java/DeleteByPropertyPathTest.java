import de.elisabetheckstaedt.moxifc.rdf.helper.ModelHelper;
import org.apache.jena.graph.Graph;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.update.*;
import org.apache.jena.util.FileManager;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;

import static de.elisabetheckstaedt.moxifc.rdf.helper.ModelHelper.serialize;

/**
 * verschiedene Tests zum löschen per PropertyPath
 */
public class DeleteByPropertyPathTest {

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
    public void queryPropertyPath() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());
        Model model = FileManager.get().loadModel("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl");
        //        Model model = FileManager.get().loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\deletedPath.ttl");
        ModelHelper.countTriples(model);

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
    public void queryPropertyPath2() {
        FileManager.get().addLocatorClassLoader(JenaTest.class.getClassLoader());
        Model model = FileManager.get().loadModel("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl");
        //                Model model = FileManager.get().loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\deletedPath.ttl");
        ModelHelper.countTriples(model);

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
        ModelHelper.countTriples(model);

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
        ModelHelper.countTriples(model);
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
        ModelHelper.countTriples(model);
        qexec.execute();
    }

    @Test
    public void deletePropertyPath() {
        Dataset dataset = RDFDataMgr.loadDataset("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl") ;
        ModelHelper.countTriples(dataset.getDefaultModel());
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
        ModelHelper.countTriples(dataset.getDefaultModel());
        serialize(dataset, "deletedPath.ttl" );
    }

    @Test
    public void deletePropertyPath2() {
        Dataset dataset = RDFDataMgr.loadDataset("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl") ;
        ModelHelper.countTriples(dataset.getDefaultModel());
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
        ModelHelper.countTriples(dataset.getDefaultModel());
        serialize(dataset, "deletedPath2.ttl" );
    }


}
