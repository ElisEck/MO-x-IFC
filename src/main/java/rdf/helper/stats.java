package rdf.helper;

import io.micrometer.core.instrument.internal.Mergeable;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class stats {
    public static String countTriples(Dataset dataset){
        String queryString =
                "SELECT "+
                        "(COUNT(?s) AS ?triplecount) "+
                        "WHERE {"+
                        "?s ?p ?o ."+
                        "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
        String ergebnis = new String();
        try {
            ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                ergebnis = ergebnis.concat(soln.get("triplecount").toString() + " Triple" + System.lineSeparator());
            }
        } finally {
            qexec.close();
        }
        return ergebnis;
    }

    public static String countTriples(Model model){
        String queryString =
            "SELECT "+
                "(COUNT(?s) AS ?count) "+
            "WHERE {"+
                "?s ?p ?o ."+
            "}";
        return printCount(model, queryString, " triples");
    }

    public static String countDistinctSubjects(Model model){
        String queryString =
            "SELECT "+
                "(COUNT(DISTINCT(?s)) AS ?count) "+
            "WHERE {"+
                "?s ?p ?o ."+
            "}";
        return printCount(model, queryString, " distinct subjects");
    }

    public static String countDistinctObjects(Model model){
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


    public static String countSubjectClasses(Model model){
        String queryString =
            "PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#> " +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
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
            "PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#> " +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "SELECT "+
                "(COUNT(DISTINCT(?klasse)) AS ?count) "+
            "WHERE {" +
                "?s ?p ?node." +
                "?node rdf:type ?klasse." +
            "} " ;
        return printCount(model, queryString, " Object-Classes");
    }
    public static String countNodeClasses(Model model){
        String queryString =
            "PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#> " +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
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

    public static String countSubjectsByClass(Model model, Integer limit){
        String queryString =
                "PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#> " +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
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

        return printCountKlasse(model, queryString, "Subjects");
    }


    public static String countObjectsByClass(Model model, Integer limit){
        String queryString =
                "PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#> " +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
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

        return printCountKlasse(model, queryString, "Objects");
    }
    public static String countNodesByClass(Model model, Integer limit){
        String queryString =
            "PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#> " +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
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
        return printCountKlasse(model, queryString, "Nodes");
    }

    public static String countDistinctPredicates(Model model){
        String queryString =
                "SELECT "+
                        "(COUNT(DISTINCT(?p)) AS ?count) "+
                        "WHERE {"+
                        "?s ?p ?o ."+
                        "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        String ergebnis = new String();
        try {
            ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                ergebnis = ergebnis.concat(soln.get("count").asLiteral().getString() + " verschiedene Prädikate");
            }
        } finally {
            qexec.close();
        }
        return ergebnis;
    }

    public static String countPredicatesByName(Model model, Integer limit){
        String queryString =
            "PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC4/ADD1/OWL#> " +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "SELECT "+
                "(COUNT(?klasse) AS ?count) " +
                "?klasse "+
            "WHERE {" +
                "?s ?klasse ?node. " +
            "} " +
            "GROUP BY ?klasse " +
            "ORDER BY desc(?count) " +
            "LIMIT " + limit;

        return printCountKlasse(model, queryString, "Predicates");
    }




    public static String printCount(Model model, String queryString, String header){
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


    public static String printCountKlasse(Model model, String queryString, String header){
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


}
