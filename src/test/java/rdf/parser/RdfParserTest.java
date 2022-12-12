package rdf.parser;

import de.elisabetheckstaedt.moxifc.modelicatranscriptor.model.MClass;
import de.elisabetheckstaedt.moxifc.rdf.helper.ModelHelper;
import de.elisabetheckstaedt.moxifc.rdf.parser.RdfParser;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;

public class RdfParserTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MClass.class);
    private RdfParser rdfParser = new RdfParser();

    @Test
    /**
     * liest ein rdf-Modell
     * wandelt es intern um
     * schreibt es anschließend in verschiedenen Formen (mit und ohne Inferenzstatements) wieder raus (als ttl)
     * läuft 10.1.22 18:51
     */
    public void readRDF() {
//        rdfParser.parseFile("c:\\_DATEN\\WORKSPACES\\Notebooks\\owlready\\pizzaElli"); //RDF Pizza Ontologie
//        rdfParser.parseFile("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\BRICK\\EASNeubau\\RLT\\neubau_EE.ttl"); //ttl BRICK Neubau RLT -- geht nicht
//        rdfParser.parseFile("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\BRICK\\EASNeubau\\RLT\\neubau_EE.rdf"); //ttl BRICK Neubau RLT von Protege umgewandelt in RDF
//        rdfParser.parseFile("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\IFC\\NeubauEAS\\RLT4_5red.ttl"); //ttl IFC Neubau RLT
//        rdfParser.parseFile("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\IFC\\NeubauEAS\\RLT4_5red.rdf"); //ttl IFC Neubau RLT von Protege umgewandelt in RDF
//        Model modelAix = rdfParser.parseFile("c:\\_DATEN\\_FMI4BIM\\BIM\\Ontologien und Alignments\\ModelicaOWL\\aix_2103082215_short.rdf"); //774KB
        Model modelKLT = rdfParser.parseFile("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\5_BRICK\\EASNeubau\\KLT\\PrimaerBetaFix3.rdf"); //10.3KB
        Model alignment = rdfParser.parseFile("c:\\_DATEN\\_FMI4BIM\\BIM\\Ontologien und Alignments\\4_AlignmentBRICKModelica\\AlignmentBRICKModelica210317.rdf"); //1.9KB
//        Model IFCOWL = rdfParser.parseFile("c:\\_DATEN\\_FMI4BIM\\BIM\\Ontologien und Alignments\\IFC\\IFC4.rdf"); //3.5MB
//        Model BRICKOWL = rdfParser.parseFile("c:\\_DATEN\\_FMI4BIM\\BIM\\Ontologien und Alignments\\BRICK\\BRICK.rdf"); //2.7MB
//        Model min = rdfParser.parseFile("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\Minimalbeispiel.rdf"); //2.7MB

        Model modelm = modelKLT.union(alignment);

        LOGGER.trace("KLT:");
        ModelHelper.printAllStatements(modelKLT);
        LOGGER.trace("alignment:");
        ModelHelper.printAllStatements(alignment);
        LOGGER.trace("merge:");
        ModelHelper.printAllStatements(modelm);
        Model min=modelm;
        try {
            FileWriter origWriter = new FileWriter("Originalmodell.ttl");
            min.write(origWriter, "TTL" );
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter infWriter = new FileWriter("Inferenzmodell_RDFS.ttl");
            InfModel inf = ModelFactory.createRDFSModel(min);
            inf.write(infWriter, "TTL" );
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter infWriter = new FileWriter("Inferenzmodell_OWL.ttl");
            Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
            InfModel inf = ModelFactory.createInfModel(reasoner, min);
            ModelHelper.printAllStatements(inf);
            inf.write(infWriter, "TTL" );
        } catch (IOException e) {
            e.printStackTrace();
        }
//        return new ModelicaClass("bsp");
    }



}