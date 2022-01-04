package rdf.helper;

import org.apache.jena.graph.Node;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.LiteralRequiredException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
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

    static String PREFIXSTRING =
            "PREFIX ifc: <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#>" + System.lineSeparator() +
                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + System.lineSeparator() +
                    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + System.lineSeparator() +
                    "PREFIX list: <https://w3id.org/list#> " + System.lineSeparator();

    public ModelEE(Model model) {
        this.model = model;
    }

    public void calculateNodeDegrees() {
        while (model.listSubjects().hasNext()) {
            Resource node = model.listSubjects().next();
            Node2DegreeMap.put(node.toString(), countTriplesBySubject(node.toString()));
        }
        while (model.listObjects().hasNext()) {
            Node node = model.listObjects().next().asNode(); //TODO Unterschied zu Resource (siehe oben)
            if (null!=Node2DegreeMap.get(node.toString())) {
                Integer vorher = Node2DegreeMap.get(node.toString());
                Node2DegreeMap.put(node.toString(), vorher + countTriplesByObject(node.toString()));
            } else {
                Node2DegreeMap.put(node.toString(), countTriplesByObject(node.toString()));
            }
        }
        Node2DegreeMap = Node2DegreeMap.entrySet().stream()
                //                .sorted(Map.Entry.comparingByKey())
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
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
        String ergebnis = new String();
        try {
            ResultSet resultSet = qexec.execSelect();
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
                "SELECT "+
                        "(COUNT(?p) AS ?count) "+
                        "WHERE {"+
                        subject + " ?p ?o ."+
                        "}";
        return returnCount(queryString);
    }

    public Integer countTriplesByObject( String object) {
        String queryString =
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

    public ResultSetEE countPredicatesByName2(Integer limit){
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

        return countBy(queryString);
    }

    public  String printCount(String queryString, String header){
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        String ergebnis = new String();
        try {
            ResultSet results = qexec.execSelect();
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
            ResultSet results = qexec.execSelect();
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
        String ergebnis = new String();
        ergebnis = ergebnis.concat(System.lineSeparator() + header + System.lineSeparator());
        try {
            ResultSet results = qexec.execSelect();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printNode2DegreeMap() {
        Node2DegreeMap.forEach((key, value) -> {
            System.out.println(key + ": " + value);
        });
    }

}
