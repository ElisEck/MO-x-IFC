package rdf.helper;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class serialize {

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
}
