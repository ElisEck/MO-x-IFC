package de.elisabetheckstaedt.moxifc.rdf.parser;

import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;

import java.io.InputStream;

/**
 * enthält eine Methode um aus einem String eine org.apache.jena.rdf.model zu machen
 */
public class RdfParser {
    public Model parseFile(String inputFileName) {
        // create an empty model
        Model model = ModelFactory.createDefaultModel();

        // use the RDFDataMgr to find the input file
        InputStream in = RDFDataMgr.open( inputFileName );
        if (in == null) {
            throw new IllegalArgumentException(
                    "File: " + inputFileName + " not found");
        }

// read the RDF/XML file
        model.read(in, null);
        return model;
    }
}
