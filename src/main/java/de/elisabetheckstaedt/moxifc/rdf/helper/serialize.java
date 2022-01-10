package de.elisabetheckstaedt.moxifc.rdf.helper;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class serialize {

    /**
     *
     * @param dataset org.apache.jena.query.Dataset
     * @param filename
     */
    public static void serialize(Dataset dataset, String filename) {
        try {
            RDFWriter.create()
                    .source(dataset.getDefaultModel())
                    .lang(Lang.TTL)
                    .base("http://base/")
                    .output(new FileOutputStream(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * schreibt ein Modell in ein File
     * @param model org.apache.jena.rdf.model.Model
     * @param filename
     */
    public static void serialize(Model model, String filename) {
        try {
            RDFWriter.create()
                    .source(model)
                    .lang(Lang.TTL)
                    .base("http://base/")
                    .output(new FileOutputStream(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param inhalt String
     * @param filename String
     */
    public static void stringToFile(String inhalt, String filename) {
        try {
            FileWriter myWriter = new FileWriter(filename);
            myWriter.write(inhalt);
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
