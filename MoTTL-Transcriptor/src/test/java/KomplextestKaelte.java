import de.elisabetheckstaedt.moxifc.rdf.helper.ModelHelper;
import de.elisabetheckstaedt.moxifc.rdf.helper.ResultSet;
import openllet.jena.PelletReasonerFactory;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.Predicate;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import static de.elisabetheckstaedt.moxifc.rdf.helper.ModelHelper.*;

/**
 * komplexer Test des Jena Frameworks anhand der Beispiels Kälteerzeugung
 */
public class KomplextestKaelte {

    private static final Logger LOGGER = LoggerFactory.getLogger(KomplextestKaelte.class);

    static String PREFIXSTRING =
            "PREFIX ifc: <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#>" + System.lineSeparator() +
                    "PREFIX elli:  <http://linkedbuildingdata.net/ifc/resources_Elli/>" + System.lineSeparator() +
                    "PREFIX moont:  <http://www.eas.iis.fraunhofer.de/moont#>" + System.lineSeparator() +
                    "PREFIX aix:  <http://www.eas.iis.fraunhofer.de/aix#>" + System.lineSeparator() +
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
    /**
     * einlesen von IFC-Modell (Kälte Erzeugung Matthias) und IFC-Standard als ttl
     * mergen
     * gemergten Graph reduzieren um alles was Geometrie ist
     */
    public void Kaelte() {
        LOGGER.info("Start");
        String filepath1 = "C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\main\\resources\\ontologies\\2_IFC\\IFC4_ADD2_TC1.ttl";
//        String filepath2 = "C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\resources\\B_COOplant\\KälteErzeugung_MU.ttl";
//        String filepath2 = "C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\resources\\B_COOplant\\KälteErzeugung_MU_mitPumpenparametern.ttl";
//        String filepath2 = "C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\resources\\B_COOplant\\220122_KälteErzeugung_MU_fixed2.ttl";
        String filepath2 = "C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\resources\\B_COOplant\\220122_KälteErzeugung_MU_fixed3.ttl";
        String filename3 = "C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\model3.ttl";
        String filename4 = "C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\model4.ttl";
        String filename5 = "C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\model5.ttl";

        Model model = RDFDataMgr.loadModel(filepath1);
        LOGGER.info("read "+filepath1+" with "+ ModelHelper.countTriples(model) + " Triples");

        Model model2 = RDFDataMgr.loadModel(filepath2);
        LOGGER.info("read "+filepath2+" with "+ ModelHelper.countTriples(model2) + " Triples");

        Model model3 = model.add(model2);
        LOGGER.info("merged to "+ ModelHelper.countTriples(model3) + " Triples");
        ModelHelper.serialize(model3, filename3);

        Model model4 = deleteIFCGeometry(model3);
        LOGGER.info("reduced to "+ ModelHelper.countTriples(model4) + " Triples");
        ModelHelper.serialize(model4, filename4);
        LOGGER.info("serialized as "+filename4);

        Model model5 = deleteOWL(model4);
        LOGGER.info("reduced to "+ ModelHelper.countTriples(model5) + " Triples");
        ModelHelper.serialize(model5, filename5);
        LOGGER.info("serialized as "+filename5);

        //        Model model5 = RDFDataMgr.loadModel("c:\\_DATEN\\_FMI4BIM\\BIM\\Ontologien und Alignments\\6_AlignmentIFCModelica\\AlignmentModelicaIFC_220106.ttl");
        //        Model model6 = model4.add(model5);
        //        serialize(model6, "model6.ttl");


        LOGGER.info(ModelHelper.countDistinctPredicates(model4));
        LOGGER.info(ModelHelper.countDistinctNodes(model4));
        //        LOGGER.info(model4ee.countNodesByClass(150));

        ModelHelper.stringToFile(ModelHelper.countPredicatesByName(model4,150), "C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\STAT-Files\\Model4_countPredicatesByName.txt");
        LOGGER.info("printed countPredicatesByName to model4_countPredicatesByName.txt");

        ResultSet rse = ModelHelper.countPredicatesByName2(model4,150);
        LOGGER.info("Predicates - NamespaceCountSorted:");
        rse.printNamespaceCountSorted();

        LOGGER.info("calculateNodedegrees");
        Map<String, Integer> node2DegreeMap = ModelHelper.calculateNodeDegrees(model4); //ausfüllen der Node2DegreeMap am model4ee
        ModelHelper.stringToFile(ModelHelper.printNode2DegreeMap(node2DegreeMap), "C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\STAT-Files\\model4_nodedegrees.txt");
        LOGGER.info("printed Node2DegreeMap to model4_nodedegrees.txt");
    }


    /**
     * hier wird versucht mit dem Kürzel für die PropertyChain eine Query zu starten
     * für jedes Objekt Pset, Property-Name und Property-Wert rausgeben
     */
    @Test
    public void propertyChainForProperty(){
        Model model = RDFDataMgr.loadModel("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\model4.ttl");
        model = model.add(RDFDataMgr.loadModel("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\main\\resources\\alignments\\alignment2.ttl"));
        System.out.println("Triples model: " +      countTriples(model));
        OntModel inf = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, model );
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "?object ?pset ?psetname ?propname ?propvalue "+
                        " WHERE {"+
                        "?object elli:ps ?pset . " +
                        "?pset ifc:name_IfcRoot / express:hasString ?psetname . " +
                        "?pset ifc:hasProperties_IfcPropertySet ?prop . " +
                        "?prop ifc:name_IfcProperty / express:hasString ?propname . " +
                        "?prop ifc:nominalValue_IfcPropertySingleValue / express:hasString ?propvalue . " +
//                        "?prop ifc:nominalValue_IfcPropertySingleValue / express:hasDouble ?propvalue . " + //keine Treffer
                        "}";
        System.out.println(printSelects(inf, queryString));
    }

    /**
     * Modelica
     */
    @Test
    public void Modelica(){
        Model model = RDFDataMgr.loadModel("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\main\\resources\\ontologies\\7_MoOnt\\MoOnt.ttl");
        model = model.add(RDFDataMgr.loadModel("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\main\\resources\\ontologies\\8_IBPLib incl MSL\\IBPLib.ttl"));
        System.out.println("Triples model: " +      countTriples(model));
        OntModel inf = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, model );
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "?object ?pset ?psetname ?propname ?propvalue "+
                        " WHERE {"+
                        "?object elli:ps ?pset . " +
                        "?pset ifc:name_IfcRoot / express:hasString ?psetname . " +
                        "?pset ifc:hasProperties_IfcPropertySet ?prop . " +
                        "?prop ifc:name_IfcProperty / express:hasString ?propname . " +
                        "?prop ifc:nominalValue_IfcPropertySingleValue / express:hasString ?propvalue . " +
                        //                        "?prop ifc:nominalValue_IfcPropertySingleValue / express:hasDouble ?propvalue . " + //keine Treffer
                        "}";
        System.out.println(printSelects(inf, queryString));
    }

    @Test
    /**
     * Versuch eine PropertyChain bis zum Literal zu definieren
     */
    public void propertyChainWithData(){
        Model model = RDFDataMgr.loadModel("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\model4.ttl");
        model = model.add(RDFDataMgr.loadModel("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\main\\resources\\alignments\\alignment2.ttl"));
        System.out.println("Triples model: " +      countTriples(model));
        OntModel inf = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, model );
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                            "?object ?rel "+
                        " WHERE {"+
//                        "?object express:hasString ?rel . " +
//                        "?object elli:ps / ifc:name_IfcRoot / express:hasString ?rel . " + //elli:psn owl:propertyChainAxiom ( ifc:name_IfcRoot express:hasString ) .
//                        "?object elli:ps / ifc:hasProperties_IfcPropertySet / ?rel . " +
                        "?object elli:psvv ?rel . " +
                        "}";
        System.out.println(printSelects(inf, queryString));
    }



    @Test
    /**
     * zeigt Reasoning anhand des Alignments
     * aus C:\_DATEN\WORKSPACES\IntelliJ\jena-examples\src\main\java\org\apache\jena\examples\ExampleONT_01.java
     */
    public void Reasoning() throws FileNotFoundException {
        OntModel base = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
        //        base.read( sourceURL, "RDF/XML" );

        //        model6 ist merge aus IFC-Instanz + IFC-Standard (=model4) + Alignment
        ModelHelper.merge(  "C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\model4.ttl",
                            "C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\main\\resources\\alignments\\Scenario6_Alignment_Modelica_IFC.ttl",
                            "C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\model6.ttl");
        InputStream in = new FileInputStream("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\model6.ttl");
        base.read(in, "", "ttl");

        String namespace = "https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#";

        printAssertionsInData(base, namespace);

        //create OntModel mit Inference
        OntModel inf = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF, base );

        //        Individual individual2 = base.getIndividual("http://linkedbuildingdata.net/ifc/resources20210823_160347/IfcPipeFitting_763");

        LOGGER.info("Inferred assertions:");
        Resource sp = base.getResource("https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcPipeFitting");
//        //1. Versuch listResourcesWithProperty
//        Property property = inf.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
//        ResIterator iterator = inf.listResourcesWithProperty(property, sp) ;
        //2.Versuch listIndividuals
        for (Iterator<Individual> pipeFittingIterator = inf.listIndividuals(sp); pipeFittingIterator.hasNext(); ) {//für jedes pipeFitting
            Individual pipeFittingIndividual = pipeFittingIterator.next();
            LOGGER.info(pipeFittingIndividual.toString());
            for (Iterator<Resource> i = pipeFittingIndividual.listRDFTypes(false); i.hasNext(); ) {//jede zugehörige Klasse
                System.out.println(pipeFittingIndividual.getURI() + " is a " + i.next());
            }
        }
        //für das erste Element alle Eigenschaften plotten
        Property property = inf.getProperty("https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#name_IfcRoot");
        for (Iterator<Individual> pipeFittingIterator = inf.listIndividuals(sp); pipeFittingIterator.hasNext(); ) {//für jedes pipeFitting
            Individual pipeFittingIndividual = pipeFittingIterator.next();
            LOGGER.info(pipeFittingIndividual.toString());
            for (StmtIterator i = pipeFittingIndividual.listProperties(); i.hasNext(); ) {//alle Statements zu diesem pipeFitting
                Statement curStmt = i.next();
                Property pred = curStmt.getPredicate(); //--> alle pred sind vom Typ isURIResource()
                System.out.println(curStmt.getPredicate().toString() + " --- " + curStmt.getObject().toString());
                //nun alle Statements des Objekts durchgehen
                RDFNode obj = curStmt.getObject();
                for (StmtIterator j = obj.asResource().listProperties(); j.hasNext(); ) {
                    Statement cs = j.next();
                    //für alle Statement die in zweiter Ordnung Literale enthalten werden diese nun ausgegeben
                    if (cs.getObject().isLiteral()) {
                        System.out.println(cs.getSubject().toString() + " " + cs.getPredicate().toString() + " " + cs.getObject().toString());
                    }
                }
            }
            break;
        }

    }

    @Test
    /**
     */
    public void ReasoningModelica() throws FileNotFoundException {
        OntModel base = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
        ModelHelper.merge(  "C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\main\\resources\\ontologies\\7_MoOnt\\MoOnt.ttl",
                "C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\null_20220128_0810.ttl",
                "C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\aix_mo.ttl");
        InputStream in = new FileInputStream("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\aix_mo.ttl");
        base.read(in, "", "ttl");

        String namespace = "http://www.eas.iis.fraunhofer.de/aix#";

        //printAssertionsInData(base, namespace);

        //create OntModel mit Inference
        OntModel inf = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF, base );

        //        Individual individual2 = base.getIndividual("http://linkedbuildingdata.net/ifc/resources20210823_160347/IfcPipeFitting_763");

        LOGGER.info("Inferred assertions:");
        Resource sp = base.getResource("http://www.eas.iis.fraunhofer.de/aix#AixLib.Airflow.AirCurtain.AirCurtainSimplified");
        for (Iterator<Individual> pipeFittingIterator = inf.listIndividuals(sp); pipeFittingIterator.hasNext(); ) {//für jedes pipeFitting
            Individual pipeFittingIndividual = pipeFittingIterator.next();
            LOGGER.info(pipeFittingIndividual.toString());
            for (Iterator<Resource> i = pipeFittingIndividual.listRDFTypes(false); i.hasNext(); ) {//jede zugehörige Klasse
                System.out.println(pipeFittingIndividual.getURI() + " is a " + i.next());
            }
        }
        //für das erste Element alle Eigenschaften plotten
        Property property = inf.getProperty("https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#name_IfcRoot");
        for (Iterator<Individual> pipeFittingIterator = inf.listIndividuals(sp); pipeFittingIterator.hasNext(); ) {//für jedes pipeFitting
            Individual pipeFittingIndividual = pipeFittingIterator.next();
            LOGGER.info(pipeFittingIndividual.toString());
            for (StmtIterator i = pipeFittingIndividual.listProperties(); i.hasNext(); ) {//alle Statements zu diesem pipeFitting
                Statement curStmt = i.next();
                Property pred = curStmt.getPredicate(); //--> alle pred sind vom Typ isURIResource()
                System.out.println(curStmt.getPredicate().toString() + " --- " + curStmt.getObject().toString());
                //nun alle Statements des Objekts durchgehen
                RDFNode obj = curStmt.getObject();
                for (StmtIterator j = obj.asResource().listProperties(); j.hasNext(); ) {
                    Statement cs = j.next();
                    //für alle Statement die in zweiter Ordnung Literale enthalten werden diese nun ausgegeben
                    if (cs.getObject().isLiteral()) {
                        System.out.println(cs.getSubject().toString() + " " + cs.getPredicate().toString() + " " + cs.getObject().toString());
                    }
                }
            }
            break;
        }

    }

    @Test
    /**
     * hiermit wird gezeigt, dass die eigene Definition einer Property funktioniert
     * außerdem wird die PropertyChain für PropertyWerte herausgearbeitet
     */
    public void queryByPropertyChain(){
        Model model = RDFDataMgr.loadModel("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\model4.ttl");
        model = model.add(RDFDataMgr.loadModel("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\main\\resources\\alignments\\alignment1.ttl"));
        OntModel inf = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF, model );
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
//                        "?object ?rel "+
                        "?rel ?object ?Pset_Name ?Prop_Name ?value "+
                        "WHERE {"+
                        "?object elli:relDefinesByProperty_IfcObjectDefinition ?rel. " + //Das funktioniert nur mit dem inf-Model!
//                        "?rel ifc:relatedObjects_IfcRelDefinesByProperties ?object."+

                        "?rel ifc:relatingPropertyDefinition_IfcRelDefinesByProperties / " +
                        "ifc:hasProperties_IfcPropertySet / " +
                        "ifc:nominalValue_IfcPropertySingleValue / " +
                        "express:hasString ?value ."+
//                        "express:hasDouble ?value ."+ //keine Treffer!

                        "?rel ifc:relatingPropertyDefinition_IfcRelDefinesByProperties / " +
                        "ifc:name_IfcRoot / " +
                        "express:hasString ?Pset_Name ."+

                        "?rel ifc:relatingPropertyDefinition_IfcRelDefinesByProperties / " +
                        "ifc:hasProperties_IfcPropertySet / " +
                        "ifc:name_IfcProperty / " +
                        "express:hasString ?Prop_Name ."+

                        "}";
//        System.out.println(printSelects(model, queryString));
        System.out.println(printSelects(inf, queryString));
    }

    @Test
    /**
     * hiermit wird gezeigt, wie es möglich ist im Quelltext hier ein Statement zum Modell hinzuzufügen --> keine Lösung gefunden!
     */
    public void addStatementManually(){
        Model model = RDFDataMgr.loadModel("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\model4.ttl");
//        model = model.add(RDFDataMgr.loadModel("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\main\\resources\\alignments\\alignment1.ttl"));
        model = model.add(
                model.createStatement(
//                        model.createProperty("ifc", "relDefinesByProperty_IfcObjectDefinition"),//-->erzeugt spitze Klammern
                        model.createResource("ifc:relDefinesByProperty_IfcObjectDefinition"), //erzeugt spitze Klammern
                        model.createProperty("owl:inverseOf"),
                        model.createResource("ifc:relatedObjects_IfcRelDefinesByProperties")));
        ModelHelper.serialize(model, "C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\model4_TestAddStatement.ttl");
    }

    @Test
    /**
     * proof of concept for propertyChainAxiom (incl. Herleitung) - working 18.1.22
     */
    public void propertyChainPOC(){
                Model model = RDFDataMgr.loadModel("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\main\\resources\\alignments\\Minimalbeispiel_PropertyChainAxiom.ttl");
        //        OntModel inf = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF, model );
        //        OntModel inf = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MINI_RULE_INF, model ); //lief 45min ohne Ergebnis, danach abgebrochen
        OntModel inf = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, model );
        String queryString =
                PREFIXSTRING +
                        "SELECT "+
                        "?object ?rel"+
                        " WHERE {"+
                        "?object elli:stringNominalValue ?rel . " + //only working with PelletReasoner, otherwise no results
                        //                        "?object ifc:relatingPropertyDefinition_IfcRelDefinesByProperties / ifc:hasProperties_IfcPropertySet ?rel . " + //~20 Statements returned
                        //                        "?object owl:propertyChainAxiom ?rel . " + //just to make sure it has been recognized --> yep, it is!
                        "}";
        System.out.println(printSelects(inf, queryString));
    }



    /**
     * temporär beseite geräumt, ist eigentlich nichts besonderes
     * @param base
     * @param namespace
     */
    void printAssertionsInData(OntModel base, String namespace) {
        OntClass spaceClass = base.getOntClass( namespace + "IfcSpace" );
        Individual space = base.createIndividual( namespace + "sp1", spaceClass );

        System.out.println("---- Assertions in the data ----");
        for (Iterator<Resource> i = space.listRDFTypes(false); i.hasNext(); ) {
            System.out.println( space.getURI() + " is a " + i.next() );
        }
    }

    Model deleteIFCGeometry(Model model) {
        Model model1 = deleteSubClassNodes(model, "<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcRepresentationItem>");
        //        model4 = deleteSubClassNodes(model4, "<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcBuildingElement>"); //macht keine Änderung
        //        model4 = deleteSubClassNodes(model4, "<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#NOTDEFINED>"); //macht keine Änderung
        model = deleteSubClassNodes(model, "<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcLengthMeasure_List>");
        model = deleteSubClassNodes(model, "<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcLengthMeasure>");
        return model1;
    }

    Model deleteOWL(Model model) {
        Model model1 = deleteByPredicateNamesspace(model, "http://www.w3.org/2002/07/owl#", "owl"); //TODO umbauen, so dass man nur eins von beiden angeben muss
        //        model4 = deleteByPredicate(model4, "owl:onProperty");
        return model1;
    }

}
