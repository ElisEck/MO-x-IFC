package de.elisabetheckstaedt.moxifc.modelicatranscriptor;

import de.elisabetheckstaedt.moxifc.modelicatranscriptor.model.ModelicaLibrary;
import org.apache.commons.cli.*;

import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ModelicaTranscriptor {

    public static final String PARAM_VERSION = "ontologyVersion";
    public static final String PARAM_NAME = "ontologyLongName";
    public static final String PARAM_PREFIX = "prefix";
    public static final String PARAM_OUTPUT = "outputPath";
    public static final String PARAM_INPUT = "inputPath";
    public static final String PARAM_TITLE = "ontologyTitle";

    public static void main(String[] args) {
        Options options = new Options();

        Option inputPath = new Option("i", PARAM_INPUT, true, "path to directory containing the input package (package.mo, package.order) e.g. c:\\LibEAS (without trailing slash), use quotes if whitespaces contained");
        inputPath.setRequired(true);
        options.addOption(inputPath);

        Option outputPath = new Option("o", PARAM_OUTPUT, true, "path to directory where the turtle file should be placed e.g. c:\\TMP (without trailing slash), use quotes if whitespaces contained");
        outputPath.setRequired(true);
        options.addOption(outputPath);

        Option prefix = new Option("p", PARAM_PREFIX, true, "ontology prefix e.g. \"mbl\" (without colon and quotes)");
        prefix.setRequired(true);
        options.addOption(prefix);

        Option ontologyLongName = new Option("n", PARAM_NAME, true, "e.g. \"Buildings\" (no quotes necessary)");
        ontologyLongName.setRequired(true);
        options.addOption(ontologyLongName);

        Option ontologyTitle = new Option("t", PARAM_TITLE, true, "e.g. Modelica Buildings Library (v8.0.0) ontology");
        ontologyTitle.setRequired(true);
        options.addOption(ontologyTitle);

        Option ontologyVersion = new Option("v", PARAM_VERSION, true, "combined version number of Modelica package and the ontology v8.0.0-1.0.0");
        ontologyVersion.setRequired(true);
        options.addOption(ontologyVersion);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;//not a good practice, it serves it purpose

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("ModelicaTranscriptor", options);

            System.exit(1);
        }

        convertModelicaLibraryToGraph(cmd.getOptionValue(PARAM_PREFIX), cmd.getOptionValue(PARAM_NAME), cmd.getOptionValue(PARAM_INPUT), cmd.getOptionValue(PARAM_TITLE), cmd.getOptionValue(PARAM_VERSION), cmd.getOptionValue(PARAM_OUTPUT));
    }

    /**
     * @param prefix     z.B. aix
     * @param longName   z.B. AixLib
     * @param sourcePath Ordner der das Wurzel package.mo enthält z.B. C:\Program Files\Dymola 2021\Modelica\Library\Modelica 3.2.3\
     */
    private static void convertModelicaLibraryToGraph(String prefix, String longName, String sourcePath, String ontologyTitle, String ontologyVersion, String outputBasePath) {
        ModelicaLibrary ml = new ModelicaLibrary(prefix, prefix, Path.of(sourcePath));
        String filename = String.format("%s\\%s_%s_fullclean.ttl", outputBasePath, prefix, getCurrentDateFormatted());
        ml.serializeAsTTL(filename, prefix, longName, "fullclean", ontologyTitle, ontologyVersion);
    }

    private static String getCurrentDateFormatted() {
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd_HHmm");
        return sdf3.format(new Timestamp(System.currentTimeMillis()));
    }
}
