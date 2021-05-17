package mo.parser;

import model.ModelicaFile;
import model.ModelicaLibrary;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.Files.readString;

public class ModelicaFilesRegExer {

    public static void main(String[] args) {
//        String name = "aix";
//        String prefix = "aix";
//        Path dir = Path.of("c:\\_DATEN\\Modelica\\_Libraries_extern\\AixLib\\AixLib\\");
//                String name = "mbl";
//        String prefix = "mbl";
//        Path dir = Path.of("c:\\_DATEN\\Modelica\\_Libraries_extern\\modelica-buildings\\Buildings\\");
//                String name = "bs";
//        String prefix = "bs";
//        Path dir = Path.of("c:\\_DATEN\\Modelica\\_Libraries_extern\\BuildingSystems\\BuildingSystems\\");
        String name = "ibpsa";
        String prefix = "ibpsa";
        Path dir = Path.of("c:\\_DATEN\\Modelica\\_Libraries_extern\\modelica-ibpsa\\IBPSA\\");
//                        String name = "ideas";
//        String prefix = "ideas";
//        Path dir = Path.of("c:\\_DATEN\\Modelica\\_Libraries_extern\\IDEAS\\IDEAS\\");


//                String name = "RLT4";
//        String prefix = "eas";
//        Path dir = Path.of("c:\\_DATEN\\Prototyp\\2103\\RDFModelle\\minimal\\");
//        String name = "coo";
//        String prefix = "eas";
//        Path dir = Path.of("c:\\_DATEN\\2021-04-22_FullPaper_LDAC\\");
//        String name = "rlt4";
//        String prefix = "rlt4";
//        Path dir = Path.of("c:\\_DATEN\\Modelica\\_Modelle\\EASNeubau\\");
//        String name = "msl";
//        String prefix = "msl";
//        Path dir = Path.of("C:\\Program Files\\Dymola 2021\\Modelica\\Library\\Modelica 3.2.3\\");

//        Path dir = checkArgs(args);

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
        ml.writeAllToTTL("c:\\_DATEN\\Prototyp\\2103\\RDFModelle\\"+name+"_210419_2144.ttl");
        ml.serializeAsMo("C:/TMP/ModelicaTestSerialisation/");
    }

//    public static void mainalt(String[] args) {
//        Path path = checkArgs(args);
//        if (path == null) return;
//        ModelicaLibrary ml = new ModelicaLibrary(path.toString());
//        try {
//            Files.walk(path)
//                    .filter(file -> file.toFile().isFile())
//                    .filter(file -> file.toFile().getAbsolutePath().toLowerCase().endsWith(".mo"))
//                    .forEach(file -> {
//                                System.out.print(file + "\t");
//                                ModelicaFile mf1 = new ModelicaFile(file);
//                                mf1.erzeugeKlassenAusString();
//                                ml.addFile(mf1);
//                                ml.addPackage(mf1.container);
//                                for (ModelicaKlasse mk1 : mf1.mks) {
//                                    mk1.BeschreibungAusInhalt();
//                                    mk1.ParentAusInhalt(mk1.klasseninhalt);
//                                    mk1.sort_parent();
////                                    mk1.ParameterAusInhalt();
//                                    System.out.println(mk1.container + "." + mk1.name + "\t" + mk1.printParent());
//                                }
//                            }
//                    );
//        } catch (IOException e) {
//            System.err.println(e.getMessage());
//        }
////        ml.generatePackageHierarchyFromPackageList();
//        ml.writeParametersToTTL();
//        ml.writeExtendsToTTL();
//    }
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
