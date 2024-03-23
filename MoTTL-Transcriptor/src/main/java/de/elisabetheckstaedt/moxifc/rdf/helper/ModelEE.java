package de.elisabetheckstaedt.moxifc.rdf.helper;

import org.apache.jena.graph.Node;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelEE {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelEE.class);

    Model model;
    Map<String, Integer> Node2DegreeMap= new HashMap<>();
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
    public ModelEE(Model model) {
        this.model = model;
    }

    Map<String, String> reverseMap(Map<String, String> map) {
        Map<String, String> nSURI2PrefixMap = new HashMap<>();
//        model.getNsPrefixMap()
        map
                .forEach((key, value) -> nSURI2PrefixMap.put(value, key));
        return nSURI2PrefixMap;
    }
    public void calculateNodeDegrees() {
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
            Node2DegreeMap.put(st, countTriplesBySubject(st));
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
                Node2DegreeMap.put(st, vorher + countTriplesByObject(st));
            } else {
                Node2DegreeMap.put(st, countTriplesByObject(st));
            }
        }
        Node2DegreeMap = Node2DegreeMap.entrySet().stream()
                //                .sorted(Map.Entry.comparingByKey())
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        LOGGER.info("Degree für alle Knoten ermittelt. (Literale: " + literals + " Blanks: " + blanks +  " distinct URI nodes: " + Node2DegreeMap.size() + " Subjects: " + subjIterator.toList().size() + " Objects: " + objIterator.toList().size() + ")");
    }

    public String getTriplesBySubject(String subject){
        String queryString =
                PREFIXSTRING +
                "SELECT "+
                        "?p "+
                        "?o "+
                        "WHERE {"+
                            subject + " ?p ?o ."+
                        "}";
        return printSelects(queryString);
    }

    public String getTriplesByPredicate(String predicate){
        String queryString =
                "SELECT "+
                        "?s "+
                        "?o "+
                        "WHERE {"+
                        "?s " + predicate + " ?o ."+
                        "}";
        return printSelects(queryString);
    }

    public String getTriplesByNodeClass(String nodeClass){
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
        return printSelects(queryString);
    }

    public String printSelects(String queryString){
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        String ergebnis = "";
        try {
            org.apache.jena.query.ResultSet resultSet = qexec.execSelect();
            while (resultSet.hasNext()) {
                QuerySolution soln = resultSet.next();
                for(String colName: resultSet.getResultVars()) {
                    try {
                        ergebnis = ergebnis.concat(soln.get(colName).asLiteral().getString() + "\t");
                    } catch (LiteralRequiredException e) {
                        ergebnis = ergebnis.concat(soln.get(colName).toString() + "\t");
                    }
                }
                ergebnis = ergebnis.concat(System.lineSeparator());
            }
        } finally {
               qexec.close();
        }
        return ergebnis;
    }

    public String countTriplesBySubjectAndPredicate(String subject, String predicate) {
        String queryString =
                "SELECT "+
                        "(COUNT(?o) AS ?count) "+
                        "WHERE {"+
                        subject + " " + predicate + " ?o ."+
                        "}";
        return printCount(queryString, "\t triples \t "+ subject + " \t" + predicate);
    }

    public String countTriplesBySubjectPrint(String subject) {
        String queryString =
                "SELECT "+
                        "(COUNT(?o) AS ?count) "+
                        "WHERE {"+
                        subject + " ?p ?o ."+
                        "}";
        return printCount(queryString, "\t triples \t "+ subject);
    }

    public Integer countTriplesBySubject(String subject) {
        String queryString =
                PREFIXSTRING +
                "SELECT "+
                        "(COUNT(?p) AS ?count) "+
                        "WHERE {"+
                        subject + " ?p ?o ."+
                        "}";
        return returnCount(queryString);
    }

    public Integer countTriplesByObject( String object) {
        String queryString =
                PREFIXSTRING +
                "SELECT "+
                        "(COUNT(?p) AS ?count) "+
                        "WHERE {"+
                        "?s ?p "+ object +"."+
                        "}";
        return returnCount(queryString);
    }

    public Integer countTriples(){
        String queryString =
                "SELECT "+
                        "(COUNT(?s) AS ?count) "+
                        "WHERE {"+
                        "?s ?p ?o ."+
                        "}";
        return returnCount(queryString);
    }

    public  String countDistinctSubjects(){
        String queryString =
                "SELECT "+
                        "(COUNT(DISTINCT(?s)) AS ?count) "+
                        "WHERE {"+
                        "?s ?p ?o ."+
                        "}";
        return printCount(queryString, " distinct subjects");
    }

    public  String countDistinctObjects(){
        String queryString =
                "SELECT "+
                        "(COUNT(DISTINCT(?o)) AS ?count) "+
                        "WHERE {"+
                        "?s ?p ?o ."+
                        "}";

        return printCount( queryString, " distinct objects");
    }
    public String countDistinctNodes(){
        String queryString =
                "SELECT "+
                        "(COUNT(DISTINCT(?node)) AS ?count) "+
                        "WHERE {" +
                        "{ ?s ?p ?node.} " +
                        "UNION " +
                        "{?node ?p2 ?o2.}" +
                        "}";
        return printCount(queryString, " distinct nodes");
    }

    public  String countSubjectClasses(){
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "(COUNT(DISTINCT(?klasse)) AS ?count) "+
                        "WHERE {" +
                        "?node ?p2 ?o2." +
                        "?node rdf:type ?klasse." +
                        "} " ;
        return printCount(queryString, " Subject-Classes");
    }

    public String countObjectClasses(){
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "(COUNT(DISTINCT(?klasse)) AS ?count) "+
                        "WHERE {" +
                        "?s ?p ?node." +
                        "?node rdf:type ?klasse." +
                        "} " ;
        return printCount(queryString, " Object-Classes");
    }

    public  String countNodeClasses(){
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
        return printCount(queryString, " Node-Classes");
    }

    /**
     *
     * @return String
     */
    public String countDistinctPredicates(){
        String queryString =
                "SELECT "+
                        "(COUNT(DISTINCT(?p)) AS ?count) "+
                        "WHERE {"+
                        "?s ?p ?o ."+
                        "}";

        return printCount( queryString, " distinct predicates");
    }


    public String countSubjectsByClass(Integer limit){
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

        return printCountBy(queryString, "Subjects");
    }


    public String countObjectsByClass(Integer limit){
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

        return printCountBy(queryString, "Objects");
    }
    public String countNodesByClass(Integer limit){
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
        return printCountBy(queryString, "Nodes");
    }


    /**
     *
     * @param limit
     * @return String
     */
    public  String countPredicatesByName(Integer limit){
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

        return printCountBy(queryString, "Predicates");
    }

    /**
     *
     * @param limit (-1, wenn kein Limit
     * @return ResultSetEE
     */
    public ResultSetEE countPredicatesByName2(Integer limit){
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

        return countBy(queryString);
    }

    public  String printCount(String queryString, String header){
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

    public  Integer returnCount(String queryString){
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

    public  String printCountBy(String queryString, String header){
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



    public ResultSetEE countBy(String queryString){
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        ResultSetEE rsc;
        try {
            rsc = new ResultSetEE(qexec.execSelect());
        } finally {
            qexec.close();
        }
        return rsc;
    }

    public void stats(String outputfilename) {
        try {
            FileWriter myWriter = new FileWriter(outputfilename);
            myWriter.write(countTriples()+System.lineSeparator());
            myWriter.write(countDistinctSubjects()+System.lineSeparator());
            myWriter.write(countDistinctObjects()+System.lineSeparator());
            myWriter.write(countDistinctNodes()+System.lineSeparator());
            myWriter.write(countDistinctPredicates()+System.lineSeparator());
            myWriter.write(countNodeClasses()+System.lineSeparator());
            myWriter.write(countObjectClasses()+System.lineSeparator());
            myWriter.write(countSubjectClasses()+System.lineSeparator());
            myWriter.write(countPredicatesByName(120)+System.lineSeparator());
            myWriter.write(countNodesByClass(80)+System.lineSeparator());
            myWriter.write(countObjectsByClass(80)+System.lineSeparator());
            myWriter.write(countSubjectsByClass( 80)+System.lineSeparator());
            myWriter.close();
            LOGGER.info("printed stats to " + outputfilename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String printNode2DegreeMap() {
        String string = "";
        for (Map.Entry<String, Integer> entry : Node2DegreeMap.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            string = string.concat(key + ": " + value + System.lineSeparator());
        }
        return string;
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

}
