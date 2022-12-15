package de.elisabetheckstaedt.moxifc.modelicatranscriptor.model;

import de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser.ModelicaFileAntlrParser;
import de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser.TreeNode;
import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.*;

public class ModelicaLibrary {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelicaLibrary.class);

    public static final String NEWLINE = System.lineSeparator();
    /**
     * Liste von ModelicaFiles, die zur Library gehören
     */
    List<ModelicaFile> mfs = new LinkedList<>();
    /**
     * ungeordnete Liste aller Packages
     */
    List<String> mps = new LinkedList<>();
    String rootpath;
    String name;
    String prefix;
    TreeNode<String> packageHierarchyRoot;

    /**
     * constrc
     *
     * @param name
     * @param prefix
     * @param dir
     */
    public ModelicaLibrary(String name, String prefix, Path dir) {
        //        if (dir == null) return null;
        //        new ModelicaLibrary(, name, prefix, title);
        this.rootpath = dir.toString();
        this.name = name;
        this.prefix = prefix;

        try {
            Files.walk(dir)
                    .filter(file -> file.toFile().isFile())
                    .filter(file -> file.toFile().getAbsolutePath().toLowerCase().endsWith(".mo"))
                    .forEach(file -> {
                                ModelicaFile mf1 = new ModelicaFile(file);
                                LOGGER.trace("Parsing " + mf1.path + "...");
                                ModelicaFileAntlrParser parser = new ModelicaFileAntlrParser(prefix);
                                String content = null;
                                try {
                                    content = new BufferedReader(new InputStreamReader(new BOMInputStream(new FileInputStream(file.toFile()))))
                                            .lines()
                                            .collect(Collectors.joining("\r\n")); //TODO handle line breaks (only working for windows yet)
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //                                Set<ModelicaKlasse> result = parser.parseFile(content);
                                mf1.mks = parser.parseFile(content);
                                this.addFile(mf1);
                            }
                    );
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addFile(ModelicaFile mf1) {
        mfs.add(mf1);
    }

    void addPackage(String mp1) {
        if (!mps.contains(mp1)) {
            mps.add(mp1);
        }
    }


    void writeExtendsToTTL(String backupPrefix) {
        try {
            FileWriter myWriter = new FileWriter(name + ".ttl");
            myWriter.write("@prefix aix:    <http://www.buildingsmart-tech.org/ifcOWL/IFC4_ADD1#> .\r\n");
            myWriter.write("@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\r\n");
            for (ModelicaFile mf : mfs) {
                for (MClass mk : mf.mks) {
                    myWriter.write(mk.writeParentToTTL(backupPrefix));
                }
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void serializeAsMo(String rootpath) {
        for (ModelicaFile mf : mfs) {
            mf.serializeAsMo(rootpath);
        }
    }


    public void serializeAsTTL(String filename, String prefix, String libraryRootName, String serializingOption, String ontologyTitle, String ontologyVersion) {
        this.prefix = prefix;
        try {
            FileWriter myWriter = new FileWriter(filename, UTF_8);

            writeHeader(myWriter, ontologyTitle, ontologyVersion);

            replaceRelativePaths(libraryRootName);

            for (ModelicaFile mf : mfs) {
                for (MClass mk : mf.mks) {
                    if (serializingOption.equals("full")) {
                        myWriter.write(mk.serializeAsTTL(prefix));
                    } else if (serializingOption.equals("fullclean")) {
                        myWriter.write(cleanttl(mk.serializeAsTTL(prefix)));
                    } else {
                        myWriter.write(mk.serializeAsTTLHeaderAndParents(prefix));
                    }
                }
            }
            myWriter.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    String cleanttl(String input) {
        input = input.replace("=", "_gleich_");
        input = input.replace("[", "_");
        input = input.replace("]", "_");
        input = input.replace("'", "_");
        input = input.replace("_^_", "_hoch_");
        input = input.replace("_*_", "_mal_");
        input = input.replace("_+_", "_plus_");
        input = input.replace("_-_", "_minus_");
        input = input.replace("_/_", "_durch_");
        input = input.replace("___", "XXX");
        input = input.replace("msl:.", "msl:");
        input = input.replace("aix:.", "aix:");
        input = input.replace("mbl:.", "mbl:");
        input = input.replace("aix:Modelica.", "msl:Modelica.");
        input = input.replace("mbl:Modelica.", "msl:Modelica.");
        input = input.replace("\\\\", "\\"); //doppelte Backslash durch einfachen ersetzen
        input = input.replace("\"\\\"", "\"");
        input = input.replace("\\\"\"", "\"");
        return input;
    }

    /**
     * für alle Files und Klassen
     *
     * @param libraryRootName
     */
    private void replaceRelativePaths(String libraryRootName) {
        generateContainerHierarchyTree(libraryRootName);
        for (ModelicaFile mf : mfs) {
            for (MClass mk : mf.mks) {
                //                mk.replaceRelativePaths("AixLib.", packageHierarchyRoot);
                mk.replaceRelativeModelicaPathsWithAbsoluteModelicaPathsForAllParametersComponetsParents(libraryRootName, packageHierarchyRoot);
            }
        }
    }

    private void generateContainerHierarchyTree(String rootName) {
        packageHierarchyRoot = new TreeNode<>(rootName, new HashMap<>());
        for (ModelicaFile mf : mfs) {
            for (MClass mk : mf.mks) {
                String[] pathParts = mk.container.split("\\.");
                TreeNode<String> currentNode = packageHierarchyRoot;
                for (String pathPart : pathParts) {
                    if (!rootName.equals(pathPart)) {
                        currentNode = currentNode.getOrCreateChildWithData(pathPart);
                    }
                }
                currentNode = currentNode.getOrCreateChildWithData(mk.name); //also add models, not only package hierarchy
            }
        }
    }

    void generatePackageHierarchyFromPackageList() {
        List<String> mps_distinct = mps.stream()
                .distinct()
                .collect(Collectors.toList());
        Iterator<String> it = mps_distinct.iterator();
        while (it.hasNext()) {
            String cur = it.next();
            String[] teile = cur.split(".");
            for (String teil : teile) {
                //TODO
            }
        }
    }

    /**
     * erzeugt den Header des Turtle-Files
     * definiert Prefixes und importiert msl, moont
     *
     * @param myWriter
     * @throws IOException
     */
    private void writeHeader(FileWriter myWriter, String ontologyTitle, String ontologyVersion) throws IOException {
        myWriter.write("@prefix " + prefix + ":    <http://www.eas.iis.fraunhofer.de/" + prefix + "#> ." + NEWLINE);
        myWriter.write("@prefix moont:    <http://www.eas.iis.fraunhofer.de/moont#> ." + NEWLINE);
        //TODO Header entschlacken
        myWriter.write("@prefix msl:    <http://www.eas.iis.fraunhofer.de/msl#> ." + NEWLINE);
        myWriter.write("@prefix bs:    <http://www.eas.iis.fraunhofer.de/bs#> ." + NEWLINE);
        myWriter.write("@prefix aix:    <http://www.eas.iis.fraunhofer.de/aix#> ." + NEWLINE);
        myWriter.write("@prefix mbl:    <http://www.eas.iis.fraunhofer.de/mbl#> ." + NEWLINE);
        myWriter.write("@prefix libeas:    <http://www.eas.iis.fraunhofer.de/libeas#> ." + NEWLINE);
        //        myWriter.write("@prefix ibpsa:    <http://www.eas.iis.fraunhofer.de/ibpsa#> ." + NEWLINE);
        myWriter.write("@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> ." + NEWLINE);
        myWriter.write("@prefix rdf:  <http://www.w3.org/2000/01/rdf#> ." + NEWLINE);
        myWriter.write("@prefix owl:  <http://www.w3.org/2002/07/owl#> ." + NEWLINE);
        myWriter.write("@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> ." + NEWLINE);
        myWriter.write("@prefix dcterms:  <http://purl.org/dc/terms#> ." + NEWLINE);
        myWriter.write(prefix + ": rdf:type owl:Ontology ;" + NEWLINE);
        myWriter.write("\t dcterms:title \"" + ontologyTitle + "\"@en ;" + NEWLINE);
        myWriter.write("\t dcterms:creator \"Elisabeth Eckstädt using MoTTL transcriptor\" ;" + NEWLINE);
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");
        myWriter.write("\t dcterms:issued \"" + sdf3.format(new Timestamp(System.currentTimeMillis())) + "\";" + NEWLINE);
        myWriter.write("\t owl:versionInfo \"" + ontologyVersion + "\";" + NEWLINE);
        myWriter.write("\t owl:imports msl: ;" + NEWLINE);
        myWriter.write("\t owl:imports mbl: ;" + NEWLINE);
        myWriter.write("\t owl:imports aix: ;" + NEWLINE);
        myWriter.write("\t owl:imports bs: ;" + NEWLINE);
        myWriter.write("\t owl:imports libeas: ;" + NEWLINE);
        myWriter.write("\t owl:imports moont: ." + NEWLINE);
    }

    public String getName() {
        return name;
    }
}
