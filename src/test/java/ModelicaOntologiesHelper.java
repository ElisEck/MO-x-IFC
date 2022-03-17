import org.apache.jena.ontology.OntModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.elisabetheckstaedt.moxifc.rdf.helper.ModelHelper.printSelects;

public class ModelicaOntologiesHelper {
    public static final String NEWLINE = System.lineSeparator();
    private static final Logger LOGGER = LoggerFactory.getLogger(JenaTest.class);

    static String PREFIXSTRING =
            "PREFIX ifc: <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#>" + System.lineSeparator() +
                    "PREFIX elli:  <http://linkedbuildingdata.net/ifc/resources_Elli/>" + System.lineSeparator() +
                    "PREFIX moont:  <http://www.eas.iis.fraunhofer.de/moont#>" + System.lineSeparator() +
                    "PREFIX aix:  <http://www.eas.iis.fraunhofer.de/aix#>" + System.lineSeparator() +
                    "PREFIX mbl:  <http://www.eas.iis.fraunhofer.de/mbl#>" + System.lineSeparator() +
                    "PREFIX msl:  <http://www.eas.iis.fraunhofer.de/msl#>" + System.lineSeparator() +
                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + System.lineSeparator() +
                    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + System.lineSeparator() +
                    "PREFIX list: <https://w3id.org/list#> " + System.lineSeparator() +
                    "PREFIX cc: <http://creativecommons.org/ns#> " + System.lineSeparator() +
                    "PREFIX vann: <http://purl.org/vocab/vann/> " + System.lineSeparator() +
                    "PREFIX dc: <http://purl.org/dc/elements/1.1/> " + System.lineSeparator() +
                    "PREFIX express: <https://w3id.org/express#> " + System.lineSeparator() +
                    "PREFIX owl: <http://www.w3.org/2002/07/owl#> " + System.lineSeparator() +
                    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  " + System.lineSeparator() +
                    "PREFIX inst: <http://linkedbuildingdata.net/ifc/resources20210823_160347/> " + System.lineSeparator();

    public static String shortify(String content) {
        content = content.replace("http://www.eas.iis.fraunhofer.de/aix#", "");
        content = content.replace("http://www.eas.iis.fraunhofer.de/msl#", "");
        content = content.replace("http://www.eas.iis.fraunhofer.de/mbl#", "");
        content = content.replace("http://www.w3.org/2001/XMLSchema#", "");
        return content;
    }


    /**
     * findet alle MConnector im Graph
     * ?klasse rdfs:subClassOf moont:MConnector
     * @param inf
     * @return String printSelects ?klasse
     */
    public static String findConnectorClasses(OntModel inf){
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "DISTINCT ?klasse"+
                        " WHERE {"+
                        "?klasse rdfs:subClassOf moont:MConnector . " +
                        "}" +
                        "ORDER BY ASC(?ancestor)";
        return printSelects(inf, queryString);
    }

    /**
     * gibt alle (wahrscheinlich) ausführbaren Modelle zurück
     * ?model moont:extends aix:Modelica.Icons.Example
     * NOT Partial
     * @param inf
     * @return String printSelects ?model
     */
    public static String findAllExecutables(OntModel inf){
        String queryString =
            PREFIXSTRING +
                "SELECT "+
                    "?model"+
                " WHERE {"+
                    "?model rdfs:subClassOf moont:MModel . " +
                    "FILTER NOT EXISTS { ?model moont:isPartial \"true\"^^xsd:boolean } " +
                    "?model moont:extends msl:Modelica.Icons.Example . " +
                    "}" +
                    "ORDER BY ASC(?model)";
        return printSelects(inf, queryString);
    }


    public static String findHasPartsOfType(OntModel inf){
        String queryString =
            PREFIXSTRING +
                "SELECT "+
                    "DISTINCT ?model ?parttype"+
                " WHERE {"+
                    "?model moont:extends* ?ancestor ."+
                    "?ancestor moont:hasPart ?part . " +
                    "?part moont:type ?parttype . " +
                "}" +
                "ORDER BY ASC(?model)";
        return printSelects(inf, queryString);
    }

    /**
     * alles was moont:isPartial-Relation hat (auch wenn sie false wäre)
     * @param inf
     * @return String: printSelects ?klasse
     */
    public static String findPartials(OntModel inf){
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "?klasse"+
                        " WHERE {"+
                        "?klasse moont:isPartial ?xx . " +
                        //                    "?model moont:extends mbl:Modelica.Icons.Example . " +
                        "}" +
                        "ORDER BY ASC(?model)";
        return printSelects(inf, queryString);
    }

    /**
     * findet alle Parameter der Klasse (eigene und geerbte)
     * @param classname
     * @param inf
     */
    public static String findAllParameters(String classname, OntModel inf){
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "DISTINCT ?ancestor ?parameter ?type ?comment ?modification"+
                        " WHERE {"+
                        classname + " moont:extends* ?ancestor ."+ //extends* findet auch 0x extends und damit sich selbst --> UNION nicht nötig
                        "?ancestor moont:hasPart ?parameter ." +
                        "?parameter a moont:MParameterComponent ." +
                        "?parameter moont:type ?type ." +
                        //                        " ?parameter moont:stringComment ?comment ." +
                        " OPTIONAL { ?parameter moont:stringComment ?comment .}" +
                        " OPTIONAL { ?parameter moont:modification ?modification .}" +
                        "}" +
                        "ORDER BY ASC(?ancestor)";
        return printSelects(inf, queryString);
    }



    /**
     * Vorfahren, Container, Klassen der Komponenten
     * @param classname
     * @param inf
     * @return
     */
    public static String findClassesOfComponents(String classname, OntModel inf){
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "DISTINCT ?ancestor ?type"+
                        " WHERE {"+
                        classname + " moont:extends* ?ancestor ."+
                        "?ancestor moont:hasPart ?parameter ." +
                        "?parameter moont:type ?type ." +
                        "}" +
                        "ORDER BY ASC(?ancestor)";
        return printSelects(inf, queryString);
    }

    public static String findContainer(OntModel inf){
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "?class ?container"+
                        " WHERE {"+
                        "?class moont:containedIn ?container ."+
                        "}" +
                        "ORDER BY ASC(?ancestor)";
        return printSelects(inf, queryString);
    }

    public static String findParent(OntModel inf){
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "?class ?parent"+
                        " WHERE {"+
                        "?class moont:extends ?parent ."+
                        "}" +
                        "ORDER BY ASC(?ancestor)";
        return printSelects(inf, queryString);
    }



    public static String findDescription(String classname, OntModel inf){
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "?comment"+
                        " WHERE {"+
                        classname + " moont:stringComment ?comment ." +
                        "}" ;
        return printSelects(inf, queryString);
    }

    public static String findAncestors(String classname, OntModel inf){
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "DISTINCT ?ancestor"+
                        " WHERE {"+
                        classname + " moont:extends* ?ancestor ."+ //extends* findet auch 0x extends und damit sich selbst --> UNION nicht nötig
                        "}" +
                        "ORDER BY ASC(?ancestor)";
        return printSelects(inf, queryString);
    }

    /**
     * findet alle Konnektoren der übergebenen Klasse (eigene und geerbte)
     * @param classname
     * @param inf
     * @return String printSelects ?ancestor ?port ?klasse
     */
    public static String findAllPorts(String classname, OntModel inf){
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "DISTINCT ?ancestor ?port ?klasse"+
                        " WHERE {"+
                        classname + " moont:extends* ?ancestor ."+ //extends* findet auch 0x extends und damit sich selbst --> UNION nicht nötig
                        "?ancestor moont:hasPart ?port . " +
                        "?port a moont:MComponent . " +
                        "?port a ?klasse . " +
                        "?klasse rdfs:subClassOf moont:MConnector . " +
                        "}" +
                        "ORDER BY ASC(?ancestor)";
        return printSelects(inf, queryString);
    }

}
