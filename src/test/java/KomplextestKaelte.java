import de.elisabetheckstaedt.moxifc.rdf.helper.ModelHelper;
import de.elisabetheckstaedt.moxifc.rdf.helper.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static de.elisabetheckstaedt.moxifc.rdf.helper.ModelHelper.deleteByPredicateNamesspace;
import static de.elisabetheckstaedt.moxifc.rdf.helper.ModelHelper.deleteSubClassNodes;

/**
 * komplexer Test des Jena Frameworks anhand der Beispiels Kälteerzeugung
 */
public class KomplextestKaelte {

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
    /**
     * einlesen von IFC-Modell (Kälte Erzeugung Matthias) und IFC-Standard als ttl
     * mergen
     * gemergten Graph reduzieren um alles was Geometrie ist
     */
    public void Kaelte() {
        LOGGER.info("Start");
        String filepath1 = "c:\\_DATEN\\_FMI4BIM\\BIM\\Ontologien und Alignments\\2_IFC\\IFC4_ADD2_TC1__korrigiert2.ttl";
        String filepath2 = "c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\2_IFC\\NeubauEAS\\210823_KälteErzeugung_MU.ttl";
        String filename3 = "model3.ttl";
        String filename4 = "model4.ttl";

        Model model = RDFDataMgr.loadModel(filepath1);
        LOGGER.info("read "+filepath1+" with "+ ModelHelper.countTriples(model) + " Triples");

        Model model2 = RDFDataMgr.loadModel(filepath2);
        LOGGER.info("read "+filepath2+" with "+ ModelHelper.countTriples(model2) + " Triples");

        Model model3 = model.add(model2);
        LOGGER.info("merged to "+ ModelHelper.countTriples(model3) + " Triples");


        Model model4 = deleteSubClassNodes(model3, "<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcRepresentationItem>");
        //        model4 = deleteSubClassNodes(model4, "<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcBuildingElement>"); //macht keine Änderung
        //        model4 = deleteSubClassNodes(model4, "<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#NOTDEFINED>"); //macht keine Änderung
        model4 = deleteSubClassNodes(model4, "<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcLengthMeasure_List>");
        model4 = deleteSubClassNodes(model4, "<https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#IfcLengthMeasure>");
        //        model4 = deleteByPredicate(model4, "owl:onProperty");
        model4 = deleteByPredicateNamesspace(model4, "http://www.w3.org/2002/07/owl#", "owl"); //TODO umbauen, so dass man nur eins von beiden angeben muss
        LOGGER.info("reduced to "+ ModelHelper.countTriples(model4) + " Triples");
        ModelHelper.serialize(model4, filename4);
        LOGGER.info("serialized as "+filename4);

        //        Model model5 = RDFDataMgr.loadModel("c:\\_DATEN\\_FMI4BIM\\BIM\\Ontologien und Alignments\\6_AlignmentIFCModelica\\AlignmentModelicaIFC_220106.ttl");
        //        Model model6 = model4.add(model5);
        //        serialize(model6, "model6.ttl");


        LOGGER.info(ModelHelper.countDistinctPredicates(model4));
        LOGGER.info(ModelHelper.countDistinctNodes(model4));
        //        LOGGER.info(model4ee.countNodesByClass(150));

        ModelHelper.stringToFile(ModelHelper.countPredicatesByName(model4,150), "model4_countPredicatesByName.txt");
        LOGGER.info("printed countPredicatesByName to model4_countPredicatesByName.txt");

        ResultSet rse = ModelHelper.countPredicatesByName2(model4,150);
        LOGGER.info("Predicates - NamespaceCountSorted:");
        rse.printNamespaceCountSorted();

        LOGGER.info("calculateNodedegrees");
        Map<String, Integer> node2DegreeMap = ModelHelper.calculateNodeDegrees(model4); //ausfüllen der Node2DegreeMap am model4ee
        ModelHelper.stringToFile(ModelHelper.printNode2DegreeMap(node2DegreeMap), "model4_nodedegrees.txt");
        LOGGER.info("printed Node2DegreeMap to model4_nodedegrees.txt");

    }

}
