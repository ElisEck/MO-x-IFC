package modelicatranscriptor.parser;

import de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser.ModelicaFileAntlrParser;
import de.elisabetheckstaedt.moxifc.modelicatranscriptor.model.ModelicaFile;
import de.elisabetheckstaedt.moxifc.modelicatranscriptor.model.ModelicaLibrary;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.Files.readString;

/**
 * main()-Methode
 * parst ein vorgegebenes ttl-File
 * schreibt es als ttl wieder raus
 */
public class ReadMoAndSerializeTtlAndMo {

    public static void main(String[] args) {
        String name = "aix";
        String prefix = "aix";

        Path dir = checkArgs(args);

        if (dir == null) return;
        ModelicaLibrary ml = new ModelicaLibrary(dir.toString(), name, prefix);
        try {
            Files.walk(dir)
                    .filter(file -> file.toFile().isFile())
                    .filter(file -> file.toFile().getAbsolutePath().toLowerCase().endsWith(".mo"))
                    .forEach(file -> {
                        System.out.print(file + "\t");
                        ModelicaFile mf1 = new ModelicaFile(file);
                        ModelicaFileAntlrParser parser = new ModelicaFileAntlrParser(prefix);
                                String content = null;
                                try {
                                    content = readString(file);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
//                                Set<ModelicaKlasse> result = parser.parseFile(content);
                                mf1.mks = parser.parseFile(content);
                                ml.addFile(mf1);
                    }
                    );
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
//        ml.generatePackageHierarchyFromPackageList();
        ml.serializeAsTTL(name+"_210629_1404.ttl");
//        ml.serializeAsMo("C:/TMP/ModelicaTestSerialisation/");
    }

       private static Path checkArgs(String[] args) {
        if(args.length != 1) {
            System.err.println("Usage: ModelicaRegex <StartDirectory>");
            return null;
        }
        Path path = Path.of(args[0]);
        File startDir = path.toFile();
        if(!startDir.exists() || !startDir.isDirectory()) {
            System.err.println("Not a valid directory");
            return null;
        }
        return path;
    }
}
