import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;

import static de.elisabetheckstaedt.moxifc.rdf.helper.ModelHelper.printSelects;

public class InvestigateModelicaOntologies {

    public static final String NEWLINE = System.lineSeparator();
    private static final Logger LOGGER = LoggerFactory.getLogger(InvestigateModelicaOntologies.class);

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

    @Test
    public void findAllExecutablesWithPorts(){
        Model model = RDFDataMgr.loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\aix_20220222_1739_fullclean.ttl");
//        Model model = RDFDataMgr.loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\aix_20220217_1619_fullclean.ttl");
//        model = model.add(RDFDataMgr.loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\msl_20220217_1615_fullclean.ttl"));
        model = model.add(RDFDataMgr.loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\msl_20220222_1739_fullclean.ttl"));
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
                "FILTER NOT EXISTS { ?model moont:isPartial \"true\"^^xsd:boolean } " +
                "?model moont:extends aix:Modelica.Icons.Example . " +
                //                    "?model moont:extends mbl:Modelica.Icons.Example . " +
            "}" +
            "ORDER BY ASC(?model)";
        System.out.println(printSelects(inf, queryString));
    }


@Test
public void prepareDataForGephiGraph() {
    Model model = RDFDataMgr.loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\aix_20220228_1305_fullclean.ttl");
    model = model.add(RDFDataMgr.loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\msl_20220228_1306_fullclean.ttl"));
    model = model.add(RDFDataMgr.loadModel("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\main\\resources\\ontologies\\7_MoOnt\\MoOnt.ttl"));
    OntModel inf = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF, model );

//    System.out.println(ModelicaOntologiesHelper.findHasPartsOfType(inf));
//    System.out.println(ModelicaOntologiesHelper.findPartials(inf));
//    System.out.println(ModelicaOntologiesHelper.findConnectorClasses(inf));
//    System.out.println(ModelicaOntologiesHelper.findAllExecutables(inf));
    System.out.println(ModelicaOntologiesHelper.findContainer(inf));
    System.out.println(ModelicaOntologiesHelper.findParent(inf));
    }



    @Test
    /**
     * findet alle Parameter der Klasse
     */
    public void findSignatures() {

        String[] modelicaKlassenliste = {
                "mbl:Buildings.Fluid.HeatExchangers.RadiantSlabs.SingleCircuitSlab",
                "mbl:Buildings.Fluid.HeatExchangers.RadiantSlabs.ParallelCurcuitsSlabs",
                "aix:AixLib.Fluid.HeatExchangers.ActiveWalls.PanelHeating",
                "aix:AixLib.Fluid.HeatExchangers.Radiators.RadiatorEN442_2",
                "aix:AixLib.Fluid.HeatExchangers.Radiators.Radiator",
                "aix:AixLib.Fluid.HeatExchangers.ActiveBeams.CoolingAndHeating",
                "aix:AixLib.Fluid.HeatExchangers.ActiveBeams.Cooling",
                "aix:AixLib.Fluid.Movers.FlowControlled_dp",
                "aix:AixLib.Fluid.Movers.FlowControlled_m_flow",
                "aix:AixLib.Fluid.Movers.SpeedControlled_Nrpm",
                "aix:AixLib.Fluid.Movers.SpeedControlled_y",
                "aix:AixLib.Fluid.Movers.PumpsPolynomialBased.PumpHeadControlled",
                "aix:AixLib.Fluid.Movers.PumpsPolynomialBased.PumpSpeedControlled",
                "aix:AixLib.Fluid.Sources.Boundary_pT",
                "mbl:Buildings.Fluid.Storage.ExpansionVessel",
                "aix:AixLib.Fluid.FixedResistances.HydraulicDiameter",
                "aix:AixLib.Fluid.FixedResistances.LosslessPipe",
                "aix:AixLib.Fluid.FixedResistances.Pipe",
                "aix:AixLib.Fluid.FixedResistances.PlugFlowPipe",
                "aix:AixLib.Fluid.FixedResistances.PressureDrop",
                "aix:AixLib.Fluid.FixedResistances.GenericPipe",
                "aix:AixLib.Fluid.FixedResistances.HydraulicResistance",
                "aix:AixLib.Fluid.FixedResistances.Pipe",
                "aix:AixLib.Fluid.FixedResistances.SimplePipe",
                "mbl:Buildings.Fluid.FixedResistances.Pipe",
                "aix:AixLib.Fluid.FixedResistances.Junction",
                "aix:AixLib.Fluid.FixedResistances.CheckValve",
                "aix:AixLib.Fluid.Actuators.Valves.TwoWayButterfly",
                "aix:AixLib.Fluid.Actuators.Valves.TwoWayEqualPercentage",
                "aix:AixLib.Fluid.Actuators.Valves.TwoWayLinear",
                "aix:AixLib.Fluid.Actuators.Valves.TwoWayPolynomial",
                "aix:AixLib.Fluid.Actuators.Valves.TwoWayPressureIndependent",
                "aix:AixLib.Fluid.Actuators.Valves.TwoWayQuickOpening",
                "aix:AixLib.Fluid.Actuators.Valves.TwoWayTable",
                "aix:AixLib.Fluid.Actuators.Valves.ThreeWayEqualPercentageLinear",
                "aix:AixLib.Fluid.Actuators.Valves.ThreeWayLinear",
                "aix:AixLib.Fluid.Actuators.Valves.ThreeWayTable",
                "aix:AixLib.Systems.HydraulicModules.Admix",
                "aix:AixLib.Systems.HydraulicModules.Injection",
                "aix:AixLib.Systems.HydraulicModules.Injection2WayValve",
                "aix:AixLib.Systems.HydraulicModules.Pump",
                "aix:AixLib.Systems.HydraulicModules.Throttle",
                "aix:AixLib.Systems.HydraulicModules.ThrottlePump",
                "aix:AixLib.Fluid.Storage.Stratified",
                "aix:AixLib.Fluid.Storage.StratifiedEnhanced",
                "aix:AixLib.Fluid.Storage.StratifiedEnhancedInternalHex",
                "aix:AixLib.Fluid.Storage.Storage",
                "aix:AixLib.Fluid.Storage.BufferStorage",
                "aix:AixLib.Fluid.HeatExchangers.Heater_T",
                "aix:AixLib.Fluid.HeatExchangers.HeaterCooler_u",
                "aix:AixLib.Fluid.HeatExchangers.HeatingRod",
                "aix:AixLib.Fluid.BoilerCHP.Boiler",
                "aix:AixLib.Fluid.BoilerCHP.BoilerNoControl",
                "aix:AixLib.Fluid.BoilerCHP.HeatGenereatorNoControl",
                "mbl:Buildings.Fluid.Boilers.BoilerPolynomial",
                "aix:AixLib.Fluid.HeatExchangers.ConstantEffectiveness",
                "aix:AixLib.Fluid.HeatExchangers.DryCoilCounterFlow",
                "aix:AixLib.Fluid.HeatExchangers.DynamicHX",
                "mbl:Buildings.Fluid.HeatExchangers.DryCoilDiscretized",
                "mbl:Buildings.Fluid.HeatExchangers.DryCoilEffectivenessNTU",
                "mbl:Buildings.Fluid.HeatExchangers.PlateHeatExchangerEffectivenessNTU",
                "mbl:Buildings.Fluid.HeatExchangers.WetCoilCounterFlow",
                "mbl:Buildings.Fluid.HeatExchangers.WetCoilDiscretized",
                "mbl:Buildings.Fluid.HeatExchangers.WetCoilEffectivenessNTU",
                "aix:AixLib.Fluid.HeatPumps.Carnot_Tcon",
                "aix:AixLib.Fluid.HeatPumps.Carnot_y",
                "aix:AixLib.Fluid.HeatPumps.ReciprocatingWaterToWater",
                "aix:AixLib.Fluid.HeatPumps.ScrollWaterToWater",
                "aix:AixLib.Fluid.HeatPumps.HeatPump",
                "mbl:Buildings.Fluid.HeatPumps.EquationFitReversible",
                "aix:AixLib.Fluid.HeatExchangers.SensibleCooler_T",
                "aix:AixLib.Fluid.Chillers.Carnot_Teva",
                "aix:AixLib.Fluid.Chillers.Carnot_y",
                "aix:AixLib.Fluid.Chillers.Chiller",
                "mbl:Buildings.Fluid.Chillers.ElectricEIR",
                "mbl:Buildings.Fluid.Chillers.ElectricReformulatedEIR",
                "mbl:Buildings.AbsorptionIndirectSteam",
                "aix:AixLib.Fluid.BoilerCHP.CHP",
                "aix:AixLib.Fluid.BoilerCHP.ModularCHP",
                "mbl:Buildings.Fluid.CHPs.ThermalElectricalFollowing",
                "mbl:Buildings.Fluid.HeatExchangers.CoolingTowers.FixedApproach",
                "mbl:Buildings.Fluid.HeatExchangers.CoolingTowers.Merkel",
                "mbl:Buildings.Fluid.HeatExchangers.CoolingTowers.YorkCalc",
                "aix:AixLib.Fluid.Geothermal.Borefields.OneUTube",
                "aix:AixLib.Fluid.Geothermal.Borefields.TwoUTubes",
                "mbl:Buildings.Fluid.Geothermal.Boreholes.UTube",
                "aix:AixLib.Fluid.Solar.Thermal.SolarThermal",
                "mbl:Buildings.Fluid.SolarCollectors.ASHRAE93",
                "mbl:Buildings.Fluid.SolarCollectors.EN12975"};

        Model model = RDFDataMgr.loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\aix_20220228_1305_fullclean.ttl");
        model = model.add(RDFDataMgr.loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\msl_20220228_1306_fullclean.ttl"));
        model = model.add(RDFDataMgr.loadModel("c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\mbl_20220228_1306_fullclean.ttl"));
        model = model.add(RDFDataMgr.loadModel("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\main\\resources\\ontologies\\7_MoOnt\\MoOnt.ttl"));
        OntModel inf = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF, model );

        for (String modelicaClassName : modelicaKlassenliste){
            //        String modelicaClassName = "aix:AixLib.Fluid.HeatExchangers.ActiveWalls.PanelHeating";
            String filename = modelicaClassName.replace("aix:", "").
                    replace("mbl:", "").
                    replace(".", "_"); //"AixLib_Fluid_HeatExchangers_ActiveWalls_PanelHeating";
            String content = "";
            content = content.concat(modelicaClassName + NEWLINE + NEWLINE);
            content = content.concat(ModelicaOntologiesHelper.findDescription(modelicaClassName, inf) + NEWLINE);
            content = content.concat(ModelicaOntologiesHelper.findAncestors(modelicaClassName, inf) + NEWLINE);
            content = content.concat(ModelicaOntologiesHelper.findAllParameters(modelicaClassName, inf) + NEWLINE);
            content = content.concat(ModelicaOntologiesHelper.findAllPorts(modelicaClassName, inf) + NEWLINE);
            try {
                FileWriter myWriter = new FileWriter("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\Doku\\" + filename + "_DOKU.txt");
                System.out.println(modelicaClassName);
                myWriter.write(ModelicaOntologiesHelper.shortify(content));
                myWriter.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
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
