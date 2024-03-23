package de.elisabetheckstaedt.moxifc.rdf.helper;

import org.apache.jena.graph.Node;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * contains several methods to enhance functionality of jena.model
 * all methods are static and need "model" as a first parameter
 */
public class ModelHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelHelper.class);

//    Model model;

//
//    static String PREFIXSTRING =
//            "PREFIX ifc: <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#>" + System.lineSeparator() +
//                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + System.lineSeparator() +
//                    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + System.lineSeparator() +
//                    "PREFIX list: <https://w3id.org/list#> " + System.lineSeparator() +
//    "PREFIX inst: <http://linkedbuildingdata.net/ifc/resources20210823_160347/>" + System.lineSeparator();

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


    static Map<String, String> reverseMap(Map<String, String> map) {
        Map<String, String> nSURI2PrefixMap = new HashMap<>();
//        model.getNsPrefixMap()
        map
                .forEach((key, value) -> nSURI2PrefixMap.put(value, key));
        return nSURI2PrefixMap;
    }
    public static Map<String, Integer> calculateNodeDegrees(Model model) {
        Map<String, Integer> Node2DegreeMap= new HashMap<>();
        Map<String, String> nSURI2PrefixMap = reverseMap(model.getNsPrefixMap());
//        int i = 0;
        int literals = 0;
        int blanks = 0;
        ResIterator subjIterator = model.listSubjects();
        while (subjIterator.hasNext()) {
            Resource node = subjIterator.next();
            if (node.isLiteral()) {
                literals++;
                continue;
            }
            if (node.isAnon()) {
                blanks++;
                continue;
            }
            String nskurz = nSURI2PrefixMap.get(node.getNameSpace())+":";
            if (nskurz == null) {
                nskurz=node.getNameSpace();
            }
            String st = nskurz + node.getLocalName();
//            i++;
//            System.out.println(i + " " + st);
            Node2DegreeMap.put(st, countTriplesBySubject(model, st));
//            Node2DegreeMap.put(node.toString(), countTriplesBySubject(node.toString()));
        }
        NodeIterator objIterator = model.listObjects();
        while (objIterator.hasNext()) {
            Node node = objIterator.next().asNode();
//            Node node = model.listObjects().next().asNode(); //TODO Unterschied zu Resource (siehe oben)
            if (node.isLiteral()) {
                literals++;
                continue;
            }
            if (node.isBlank()) {
                blanks++;
                continue;
            }
            String st = nSURI2PrefixMap.get(node.getNameSpace()) + ":" + node.getLocalName();
//            i++;
//            System.out.println(i + " " + st);
            if (null!=Node2DegreeMap.get(st)) {
                Integer vorher = Node2DegreeMap.get(st);
                Node2DegreeMap.put(st, vorher + countTriplesByObject(model, st));
            } else {
                Node2DegreeMap.put(st, countTriplesByObject(model, st));
            }
        }
        Node2DegreeMap = Node2DegreeMap.entrySet().stream()
                //                .sorted(Map.Entry.comparingByKey())
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        LOGGER.info("Degree für alle Knoten ermittelt. (Literale: " + literals + " Blanks: " + blanks +  " distinct URI nodes: " + Node2DegreeMap.size() + " Subjects: " + subjIterator.toList().size() + " Objects: " + objIterator.toList().size() + ")");
        return Node2DegreeMap;
    }

    public static String getTriplesBySubject(Model model, String subject){
        String queryString =
                PREFIXSTRING +
                "SELECT "+
                        "?p "+
                        "?o "+
                        "WHERE {"+
                            subject + " ?p ?o ."+
                        "}";
        return printSelects(model, queryString);
    }

    public static String getTriplesByPredicate(Model model, String predicate){
        String queryString =
                "SELECT "+
                        "?s "+
                        "?o "+
                        "WHERE {"+
                        "?s " + predicate + " ?o ."+
                        "}";
        return printSelects(model, queryString);
    }

    public static String getTriplesByNodeClass(Model model, String nodeClass){
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "?s ?p ?o "+
                        "WHERE {"+
                        "?s ?p ?o ."+
                        "{?s rdf:type "+ nodeClass + " .}"+
                        "UNION"+
                        "{?o rdf:type "+ nodeClass + " .}"+
                        "}";
        return printSelects(model, queryString);
    }

    public static String printSelects(Model model, String queryString){
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        final StringBuilder ergebnis = new StringBuilder();
        final StringBuilder zeilen = new StringBuilder();
        try {
            org.apache.jena.query.ResultSet resultSet = qexec.execSelect();
            resultSet.getResultVars().forEach(var -> zeilen.append(var).append("\t"));
            zeilen.append(System.lineSeparator());
            while (resultSet.hasNext()) {
                QuerySolution soln = resultSet.next();
                for(String colName: resultSet.getResultVars()) {
                    try {
                        zeilen.append(soln.get(colName).asLiteral().getString() + "\t");
                    } catch (LiteralRequiredException e) {
                        zeilen.append(soln.get(colName).toString() + "\t");
                    } catch (NullPointerException f) { //wenn optionales Variable, die in dem Fall nicht da ist
                        zeilen.append("\t");
                    }
                }
                zeilen.append(System.lineSeparator());
            }
            ergebnis.append("Ergebniszeilen: " + resultSet.getRowNumber() + System.lineSeparator());
            ergebnis.append(zeilen);
            ergebnis.append("Ergebniszeilen: " + resultSet.getRowNumber() + System.lineSeparator());
        } finally {
               qexec.close();
        }
        return ergebnis.toString();
    }

    public static String countTriplesBySubjectAndPredicate(Model model, String subject, String predicate) {
        String queryString =
                "SELECT "+
                        "(COUNT(?o) AS ?count) "+
                        "WHERE {"+
                        subject + " " + predicate + " ?o ."+
                        "}";
        return printCount(model, queryString, "\t triples \t "+ subject + " \t" + predicate);
    }

    public static String countTriplesBySubjectPrint(Model model, String subject) {
        String queryString =
                "SELECT "+
                        "(COUNT(?o) AS ?count) "+
                        "WHERE {"+
                        subject + " ?p ?o ."+
                        "}";
        return printCount(model, queryString, "\t triples \t "+ subject);
    }

    public static Integer countTriplesBySubject(Model model, String subject) {
        String queryString =
                PREFIXSTRING +
                "SELECT "+
                        "(COUNT(?p) AS ?count) "+
                        "WHERE {"+
                        subject + " ?p ?o ."+
                        "}";
        return returnCount(model, queryString);
    }

    public static Integer countTriplesByObject(Model model, String object) {
        String queryString =
                PREFIXSTRING +
                "SELECT "+
                        "(COUNT(?p) AS ?count) "+
                        "WHERE {"+
                        "?s ?p "+ object +"."+
                        "}";
        return returnCount(model, queryString);
    }

    public static Integer countTriples(Model model){
        String queryString =
                "SELECT "+
                        "(COUNT(?s) AS ?count) "+
                        "WHERE {"+
                        "?s ?p ?o ."+
                        "}";
        return returnCount(model, queryString);
    }

    public static  String countDistinctSubjects(Model model){
        String queryString =
                "SELECT "+
                        "(COUNT(DISTINCT(?s)) AS ?count) "+
                        "WHERE {"+
                        "?s ?p ?o ."+
                        "}";
        return printCount(model, queryString, " distinct subjects");
    }

    public static  String countDistinctObjects(Model model){
        String queryString =
                "SELECT "+
                        "(COUNT(DISTINCT(?o)) AS ?count) "+
                        "WHERE {"+
                        "?s ?p ?o ."+
                        "}";

        return printCount(model, queryString, " distinct objects");
    }
    public static String countDistinctNodes(Model model){
        String queryString =
                "SELECT "+
                        "(COUNT(DISTINCT(?node)) AS ?count) "+
                        "WHERE {" +
                        "{ ?s ?p ?node.} " +
                        "UNION " +
                        "{?node ?p2 ?o2.}" +
                        "}";
        return printCount(model, queryString, " distinct nodes");
    }

    public static  String countSubjectClasses(Model model){
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "(COUNT(DISTINCT(?klasse)) AS ?count) "+
                        "WHERE {" +
                        "?node ?p2 ?o2." +
                        "?node rdf:type ?klasse." +
                        "} " ;
        return printCount(model, queryString, " Subject-Classes");
    }

    public static String countObjectClasses(Model model){
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "(COUNT(DISTINCT(?klasse)) AS ?count) "+
                        "WHERE {" +
                        "?s ?p ?node." +
                        "?node rdf:type ?klasse." +
                        "} " ;
        return printCount(model, queryString, " Object-Classes");
    }

    public static  String countNodeClasses(Model model){
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "(COUNT(DISTINCT(?klasse)) AS ?count) "+
                        "WHERE {" +
                        "{{?s ?p ?node.}" +
                        "UNION" +
                        "{?node ?p2 ?o2.}}" +
                        "?node rdf:type ?klasse." +
                        "} " ;
        return printCount(model, queryString, " Node-Classes");
    }

    /**
     *
     * @return String
     */
    public static String countDistinctPredicates(Model model){
        String queryString =
                "SELECT "+
                        "(COUNT(DISTINCT(?p)) AS ?count) "+
                        "WHERE {"+
                        "?s ?p ?o ."+
                        "}";

        return printCount(model, queryString, " distinct predicates");
    }


    public static String countSubjectsByClass(Model model, Integer limit){
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "(COUNT(?node) AS ?count) "+
                        "?klasse " +
                        "WHERE {" +
                        "?node ?p ?o." +
                        "?node rdf:type ?klasse." +
                        "} " +
                        "GROUP BY ?klasse " +
                        "ORDER BY desc(?count) " +
                        "LIMIT " + limit;

        return printCountBy(model, queryString, "Subjects");
    }


    public static String countObjectsByClass(Model model, Integer limit){
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "(COUNT(?node) AS ?count) "+
                        "?klasse " +
                        "WHERE {" +
                        "?s ?p ?node." +
                        "?node rdf:type ?klasse." +
                        "} " +
                        "GROUP BY ?klasse " +
                        "ORDER BY desc(?count) " +
                        "LIMIT " + limit;

        return printCountBy(model, queryString, "Objects");
    }
    public static  String countNodesByClass(Model model, Integer limit){
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "(COUNT(?node) AS ?count) "+
                        "?klasse " +
                        "WHERE {" +
                        "{{?s ?p ?node.}" +
                        "UNION" +
                        "{?node ?p2 ?o2.}}" +
                        "?node rdf:type ?klasse." +
                        "} " +
                        "GROUP BY ?klasse " +
                        "ORDER BY desc(?count) " +
                        "LIMIT " + limit;
        return printCountBy(model, queryString, "Nodes");
    }


    /**
     *
     * @param limit
     * @return String
     */
    public static  String countPredicatesByName(Model model, Integer limit){
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "(COUNT(?klasse) AS ?count) " +
                        "?klasse "+
                        "WHERE {" +
                        "?s ?klasse ?node. " +
                        "} " +
                        "GROUP BY ?klasse " +
                        "ORDER BY desc(?count) " +
                        "LIMIT " + limit;

        return printCountBy(model, queryString, "Predicates");
    }

    /**
     *
     * @param limit (-1, wenn kein Limit
     * @return ResultSetEE
     */
    public static ResultSet countPredicatesByName2(Model model, Integer limit){
        String queryString = "";
        if (limit>0) {
            queryString =
                    PREFIXSTRING +
                            "SELECT " +
                            "(COUNT(?klasse) AS ?count) " +
                            "?klasse " +
                            "WHERE {" +
                            "?s ?klasse ?node. " +
                            "} " +
                            "GROUP BY ?klasse " +
                            "ORDER BY desc(?count) " +
                            "LIMIT " + limit;
        } else {
            queryString =
                    PREFIXSTRING +
                            "SELECT " +
                            "(COUNT(?klasse) AS ?count) " +
                            "?klasse " +
                            "WHERE {" +
                            "?s ?klasse ?node. " +
                            "} " +
                            "GROUP BY ?klasse " +
                            "ORDER BY desc(?count) " ;
        }

        return countBy(model, queryString);
    }

    public static  String printCount(Model model, String queryString, String header){
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        String ergebnis = "";
        try {
            org.apache.jena.query.ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                ergebnis = ergebnis.concat(soln.get("count").asLiteral().getString() + header);
            }
        } finally {
            qexec.close();
        }
        return ergebnis;
    }

    public static  Integer returnCount(Model model, String queryString){
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        Integer ergebnis = 0;
        try {
            org.apache.jena.query.ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                ergebnis = soln.get("count").asLiteral().getInt();
            }
        } finally {
            qexec.close();
        }
        return ergebnis;
    }

    public static  String printCountBy(Model model, String queryString, String header){
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        String ergebnis = "";
        ergebnis = ergebnis.concat(System.lineSeparator() + header + System.lineSeparator());
        try {
            org.apache.jena.query.ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                ergebnis = ergebnis.concat(soln.get("count").asLiteral().getString() + "\t" + soln.get("klasse").toString() + System.lineSeparator());
            }
        } finally {
            qexec.close();
        }
        return ergebnis;
    }



    public static ResultSet countBy(Model model, String queryString){
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        ResultSet rsc;
        try {
            rsc = new ResultSet(qexec.execSelect());
        } finally {
            qexec.close();
        }
        return rsc;
    }

    public static void stats(Model model, String outputfilename) {
        try {
            FileWriter myWriter = new FileWriter(outputfilename);
            myWriter.write(countTriples(model)+System.lineSeparator());
            myWriter.write(countDistinctSubjects(model)+System.lineSeparator());
            myWriter.write(countDistinctObjects(model)+System.lineSeparator());
            myWriter.write(countDistinctNodes(model)+System.lineSeparator());
            myWriter.write(countDistinctPredicates(model)+System.lineSeparator());
            myWriter.write(countNodeClasses(model)+System.lineSeparator());
            myWriter.write(countObjectClasses(model)+System.lineSeparator());
            myWriter.write(countSubjectClasses(model)+System.lineSeparator());
            myWriter.write(countPredicatesByName(model,120)+System.lineSeparator());
            myWriter.write(countNodesByClass(model,80)+System.lineSeparator());
            myWriter.write(countObjectsByClass(model,80)+System.lineSeparator());
            myWriter.write(countSubjectsByClass(model, 80)+System.lineSeparator());
            myWriter.close();
            LOGGER.info("printed stats to " + outputfilename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String printNode2DegreeMap(Map<String,Integer> node2DegreeMap) {
        String string = "";
        for (Map.Entry<String, Integer> entry : node2DegreeMap.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            string = string.concat(key + ": " + value + System.lineSeparator());
        }
        return string;
    }

    /**
     * iteriert über alle Statesments
     * printet alle Subjekte, Prädikate und Objekte auf die Console
     */
    public static void printAllStatements(Model m) {
        StmtIterator iter = m.listStatements();
        int i = 0;
        if (iter.hasNext()) {
            System.out.println("Alle Statements:");
            while (iter.hasNext()) {
                Statement stm = iter.nextStatement();
                System.out.println(
                        i +"  " + stm.getSubject().toString() +
                                "  " + stm.getPredicate().toString() +
                                "  " + stm.getObject().toString()
                );
                i = i+1;

            }
        } else {
            System.out.println("keine weiteren Statements");
        }
    }

//    public void printFileNode2DegreeMap(String outputfilename) {
//        try {
//            FileWriter myWriter = new FileWriter(outputfilename);
//            for (Map.Entry<String, Integer> entry : Node2DegreeMap.entrySet()) {
//                myWriter.write(entry.getKey() + ": " + entry.getValue() + System.lineSeparator());
//            }
//            myWriter.close();
//            LOGGER.info("printed node degrees to " + outputfilename);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }


    /**
     *
     * @param dataset org.apache.jena.query.Dataset
     * @param filename
     */
    public static void serialize(Dataset dataset, String filename) {
        try {
            RDFWriter.create()
                    .source(dataset.getDefaultModel())
                    .lang(Lang.TTL)
                    .base("http://base/")
                    .output(new FileOutputStream(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * schreibt ein Modell in ein File
     * @param model org.apache.jena.rdf.model.Model
     * @param filename
     */
    public static void serialize(Model model, String filename) {
        try {
            RDFWriter.create()
                    .source(model)
                    .lang(Lang.TTL)
                    .base("http://base/")
                    .output(new FileOutputStream(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param inhalt String
     * @param filename String
     */
    public static void stringToFile(String inhalt, String filename) {
        try {
            FileWriter myWriter = new FileWriter(filename);
            myWriter.write(inhalt);
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * liest zwei turtle-Files, fügt sie zusammen und serialisiert das Ergebnis
     * @param file1 input filename
     * @param file2 input filename
     * @param file3 output filename
     */
    public static void merge(String file1, String file2, String file3) {
        Model model1 = RDFDataMgr.loadModel(file1);
        Model model2 = RDFDataMgr.loadModel(file2);
        Model model3 = model1.add(model2);
        ModelHelper.serialize(model3, file3);
    }


    /**
     * löscht alle Triple, die ein bestimmtes Prädikat haben
     * @param model
     * @param predicate
     * @return
     */
    public static Model deleteByPredicate(Model model, String predicate) {
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
    public static Model deleteByPredicateNamesspace(Model model, String namespace, String namespacekuerzel) {
        ResultSet rse = ModelHelper.countPredicatesByName2(model,-1);
        List<ResultLine> predicateList = rse.getResultLinesGroupedByNamespace().get(namespace);
        for (ResultLine predicate:predicateList) {
            model = deleteByPredicate(model, namespacekuerzel + ":" + predicate.name);
        }
        return model;
    }


    /**
     * löscht alle Triple deren Subjekt oder Objekt vom Typ fatherClass ist (auch indirekt)
     * @param model
     * @param fatherClass
     * @return
     */
    public static Model deleteSubClassNodes(Model model, String fatherClass) {
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



}
