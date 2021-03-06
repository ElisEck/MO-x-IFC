package rdf.parser;

import de.elisabetheckstaedt.moxifc.rdf.parser.RdfParser;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;

public class RdfParserTest {

    private RdfParser rdfParser = new RdfParser();

    @Test
    /**
     * liest ein rdf-Modell
     * wandelt es intern um
     * schreibt es anschließend in verschiedenen Formen (mit und ohne Inferenzstatements) wieder raus (als ttl)
     */
    public void readRDF() {
//        rdfParser.parseFile("c:\\_DATEN\\WORKSPACES\\Notebooks\\owlready\\pizzaElli"); //RDF Pizza Ontologie
//        rdfParser.parseFile("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\BRICK\\EASNeubau\\RLT\\neubau_EE.ttl"); //ttl BRICK Neubau RLT -- geht nicht
//        rdfParser.parseFile("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\BRICK\\EASNeubau\\RLT\\neubau_EE.rdf"); //ttl BRICK Neubau RLT von Protege umgewandelt in RDF
//        rdfParser.parseFile("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\IFC\\NeubauEAS\\RLT4_5red.ttl"); //ttl IFC Neubau RLT
//        rdfParser.parseFile("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\IFC\\NeubauEAS\\RLT4_5red.rdf"); //ttl IFC Neubau RLT von Protege umgewandelt in RDF
        Model modelAix = rdfParser.parseFile("c:\\_DATEN\\_FMI4BIM\\BIM\\Ontologien und Alignments\\ModelicaOWL\\aix_2103082215_short.rdf"); //774KB
        Model modelKLT = rdfParser.parseFile("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\BRICK\\EASNeubau\\KLT\\PrimaerBetaFix3.rdf"); //10.3KB
        Model alignment = rdfParser.parseFile("c:\\_DATEN\\_FMI4BIM\\BIM\\Ontologien und Alignments\\AlignmentBRICKModelica\\AlignmentBRICKModelica210317.rdf"); //1.9KB
        Model IFCOWL = rdfParser.parseFile("c:\\_DATEN\\_FMI4BIM\\BIM\\Ontologien und Alignments\\IFC\\IFC4.rdf"); //3.5MB
        Model BRICKOWL = rdfParser.parseFile("c:\\_DATEN\\_FMI4BIM\\BIM\\Ontologien und Alignments\\BRICK\\BRICK.rdf"); //2.7MB
        Model min = rdfParser.parseFile("c:\\_DATEN\\_FMI4BIM\\BIM\\RDF Modelle\\Minimalbeispiel.rdf"); //2.7MB

        Model modelm = modelKLT.union(alignment);

        System.out.println("KLT:");
        printAllStatements(modelKLT);
        System.out.println("alignemnt:");
        printAllStatements(alignment);
        System.out.println("merge:");
        printAllStatements(modelm);
        min=modelm;
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
            printAllStatements(inf);
            inf.write(infWriter, "TTL" );
        } catch (IOException e) {
            e.printStackTrace();
        }
//        return new ModelicaClass("bsp");
    }


    /**
     * iteriert über alle Statesments
     * printet alle Subjekte, Prädikate und Objekte auf die Console
     * @param m
     */
    public void printAllStatements(Model m) {
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
}