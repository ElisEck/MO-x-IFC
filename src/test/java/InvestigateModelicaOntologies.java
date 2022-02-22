import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.elisabetheckstaedt.moxifc.rdf.helper.ModelHelper.countTriples;
import static de.elisabetheckstaedt.moxifc.rdf.helper.ModelHelper.printSelects;

public class InvestigateModelicaOntologies {

    private static final Logger LOGGER = LoggerFactory.getLogger(JenaTest.class);

    static String PREFIXSTRING =
            "PREFIX ifc: <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#>" + System.lineSeparator() +
                    "PREFIX elli:  <http://linkedbuildingdata.net/ifc/resources_Elli/>" + System.lineSeparator() +
                    "PREFIX moont:  <http://www.eas.iis.fraunhofer.de/moont#>" + System.lineSeparator() +
                    "PREFIX aix:  <http://www.eas.iis.fraunhofer.de/aix#>" + System.lineSeparator() +
                    "PREFIX mbl:  <http://www.eas.iis.fraunhofer.de/mbl#>" + System.lineSeparator() +
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

    @Test
    /**
     *Buildings.Fluid.Interfaces.PartialFourPortInterface
     */
    public void findAllExecutables(){
        Model model = RDFDataMgr.loadModel("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\Aix_20220208_1441_full.ttl");
//        Model model = RDFDataMgr.loadModel("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\MBL_20220209_1741_full.ttl");
        //        OntModel inf = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, model );
        OntModel inf = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF, model );
        String queryString =
            PREFIXSTRING +
                "SELECT "+
                    "?model"+
                " WHERE {"+
                    "?model rdfs:subClassOf moont:MModel . " +
                    "FILTER NOT EXISTS { ?model moont:isPartial \"true\"^^xsd:boolean } " +
                    "?model moont:extends aix:Modelica.Icons.Example . " +
//                    "?model moont:extends mbl:Modelica.Icons.Example . " +
                "}" +
                "ORDER BY ASC(?model)";
        System.out.println(printSelects(inf, queryString));
    }

    @Test
    public void findAllExecutablesWithPorts(){
        Model model = RDFDataMgr.loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\aix_20220217_1619_fullclean.ttl");
        model = model.add(RDFDataMgr.loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\msl_20220217_1615_fullclean.ttl"));
        model = model.add(RDFDataMgr.loadModel("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\main\\resources\\ontologies\\7_MoOnt\\MoOnt.ttl"));
        //        Model model = RDFDataMgr.loadModel("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\MBL_20220209_1741_full.ttl");
        OntModel inf = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF, model );
        String queryString =
            PREFIXSTRING +
            "SELECT "+
                "?model ?component ?klasse"+
            " WHERE {"+
                "?model rdfs:subClassOf moont:MModel . " +
                "?model moont:hasPart ?component . " +
                "?component a moont:MComponent . " +
                "?component a ?klasse . " +
                "?klasse rdfs:subClassOf moont:MConnector . " +

//                "?component a moont:MConnectorComponent . " +
//                "FILTER NOT EXISTS { ?model moont:isPartial \"true\"^^xsd:boolean } " +
                "?model moont:extends aix:Modelica.Icons.Example . " +
                //                    "?model moont:extends mbl:Modelica.Icons.Example . " +
            "}" +
            "ORDER BY ASC(?model)";
        System.out.println(printSelects(inf, queryString));
    }

    @Test
    /**
     * MComponent
     * MConnectorComponent
     */
    public void findAllComponents(){
        Model model = RDFDataMgr.loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\aix_20220217_1619_fullclean.ttl");
        model = model.add(RDFDataMgr.loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\msl_20220217_1615_fullclean.ttl"));
        model = model.add(RDFDataMgr.loadModel("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\main\\resources\\ontologies\\7_MoOnt\\MoOnt.ttl"));
        //        Model model = RDFDataMgr.loadModel("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\MBL_20220209_1741_full.ttl");
        OntModel inf = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF, model );
        String queryString =
                PREFIXSTRING +
//                        "SELECT "+
                        "SELECT DISTINCT"+
                        "?model ?component"+
                        " WHERE {"+
                        "?model rdfs:subClassOf moont:MModel . " +
                        "?model moont:hasPart ?component . " +
                        //TODO Herve fragen, wie man das in ein alignment packt
                        "{?component a moont:MConnectorComponent . }" +
                " UNION " +
                        "{" +
                        "?component a moont:MComponent ." +
                        "?component a ?klasse . " +
                        "?klasse rdfs:subClassOf moont:MConnector . " +
                        "}" +
                        "}" +
                        "ORDER BY ASC(?model)";
        System.out.println(printSelects(inf, queryString));
    }

    @Test
    /**
     * finde alle ausführbaren Modelle (erben von Modelica.Icons.Example bzw. sind NICHT partial)
     * die Komponenten einer bestimmten Klasse enthalten
     * kann also genutzt werden um Beispiele für die Verwendung zu finden
     */
    public void testFindModelsUsingDescendantsOfClass() {
        findModelsUsingDescendantsOfClass("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\Aix_20220208_1441_full.ttl", "aix:AixLib.Fluid.Interfaces.PartialFourPortInterface");
        findModelsUsingDescendantsOfClass2("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\Aix_20220208_1441_full.ttl", "aix:AixLib.Fluid.Interfaces.PartialFourPortInterface");
        findModelsUsingDescendantsOfClass3("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\Aix_20220208_1441_full.ttl", "aix:AixLib.Fluid.Interfaces.PartialFourPortInterface");
//        findModelsUsingDescendantsOfClass("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\MBL_20220209_1741_full.ttl", "mbl:Buildings.Fluid.Interfaces.PartialFourPortInterface");
//        findModelsUsingDescendantsOfClass("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\Aix_20220208_1441_full.ttl", "aix:AixLib.Fluid.Interfaces.PartialTwoPort");
//        findModelsUsingDescendantsOfClass("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\Aix_20220208_1441_full.ttl", "aix:AixLib.Systems.HydraulicModules.BaseClasses.PartialHydraulicModule");
    }

    /**
     * alle Modelle die eine Komponente enthalten, die direkt oder indirekt von ancestor erbt
     * (ohne weitere Bedingungen an das Modell)
     * @param filename
     * @param ancestor
     * return: ?model ?instance ?descendant
     */
    public void findModelsUsingDescendantsOfClass(String filename, String ancestor){
        Model model = RDFDataMgr.loadModel(filename);
        OntModel inf = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF, model );
        String queryString =
                PREFIXSTRING +
                "SELECT "+
                    "?model ?instance ?descendant"+
                " WHERE {"+
                      "?descendant moont:extends* " + ancestor + ". " +
                      "?instance a ?descendant . " +
                      "?model moont:hasPart ?instance . " +
                      "?model rdfs:subClassOf moont:MModel . " +
                "}" +
                "ORDER BY DESC(?model)";
        System.out.println(printSelects(inf, queryString));
    }

    /**
     * MModel
     * extends from Modelica.Icons.Example
     * not Partial
     * @param filename
     * @param ancestor
     * return model instance(=model component) und konkrete Elternklasse
     */
    public void findModelsUsingDescendantsOfClass2(String filename, String ancestor){
        Model model = RDFDataMgr.loadModel(filename);
        OntModel inf = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF, model );
        String queryString =
                PREFIXSTRING +
                    "SELECT "+
                        "?model ?instance ?descendant"+
                    " WHERE {"+
                        "?descendant moont:extends* " + ancestor + ". " +
                        "?instance a ?descendant . " +
                        "?model moont:hasPart ?instance . " +
                        "?model rdfs:subClassOf moont:MModel . " +
                        "FILTER NOT EXISTS { ?model moont:isPartial \"true\"^^xsd:boolean } " +
                        "?model moont:extends aix:Modelica.Icons.Example . " +
                    "}" +
                    "ORDER BY DESC(?model)";
        System.out.println(printSelects(inf, queryString));
    }

    /**
     * MModel
     * extends from Modelica.Icons.Example
     * not Partial
     * @param filename
     * @param ancestor
     * return DISTINCT model und konkrete Elternklasse
     */
    public void findModelsUsingDescendantsOfClass3(String filename, String ancestor){
        Model model = RDFDataMgr.loadModel(filename);
        OntModel inf = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF, model );
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "DISTINCT ?model ?descendant"+
                        " WHERE {"+
                        "?descendant moont:extends* " + ancestor + ". " +
                        "?instance a ?descendant . " +
                        "?model moont:hasPart ?instance . " +
                        "?model rdfs:subClassOf moont:MModel . " +
                        "FILTER NOT EXISTS { ?model moont:isPartial \"true\"^^xsd:boolean } " +
                        "?model moont:extends aix:Modelica.Icons.Example . " +
                        "}" +
                        "ORDER BY DESC(?model)";
        System.out.println(printSelects(inf, queryString));
    }

    @Test
    /**
     * finde alle Modelica-Klassen, die MClass sind (kein MModel, kein MPackage, ...)
     */
    public void findMClass(){
        Model model = RDFDataMgr.loadModel("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\Aix_20220208_1441_full.ttl");
//        Model model = RDFDataMgr.loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\MBL_20220209_1741_full.ttl");
        OntModel inf = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF, model );
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "?model"+
                        " WHERE {"+
                        "?model rdfs:subClassOf moont:MClass . " +
                        "}" +
                        "ORDER BY DESC(?model)";
        System.out.println(printSelects(inf, queryString));
    }
}
