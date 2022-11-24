package de.elisabetheckstaedt.moxifc.modelicatranscriptor.model;

import de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser.ModelicaFileAntlrParser;
import de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.Files.readString;

public class ModelicaLibrary {

    private static final Logger LOGGER = LoggerFactory.getLogger(MClass.class);

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

    public ModelicaLibrary(String rootpath, String name, String prefix) {
        this.rootpath = rootpath;
        this.name = name;
        this.prefix = prefix;
    }

    /**
     * constrc
     * @param name
     * @param prefix
     * @param dir
     */
    public ModelicaLibrary(String name, String prefix, Path dir) {
//        if (dir == null) return null;
        new ModelicaLibrary(dir.toString(), name, prefix);
        try {
            Files.walk(dir)
                    .filter(file -> file.toFile().isFile())
                    .filter(file -> file.toFile().getAbsolutePath().toLowerCase().endsWith(".mo"))
                    .forEach(file -> {
                                ModelicaFile mf1 = new ModelicaFile(file);
                                LOGGER.info("Parsing " + mf1.path + "...");
                                ModelicaFileAntlrParser parser = new ModelicaFileAntlrParser(prefix);
                                String content = null;
                                try {
                                    content = readString(file);
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



    void writeExtendsToTTL() {
        try {
            FileWriter myWriter = new FileWriter(name+".ttl");
            myWriter.write("@prefix aix:    <http://www.buildingsmart-tech.org/ifcOWL/IFC4_ADD1#> .\r\n");
            myWriter.write("@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\r\n");
            for (ModelicaFile mf : mfs) {
                for (MClass mk : mf.mks) {
                        myWriter.write(mk.writeParentToTTL());
                    }
                }
                myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public void serializeAsMo(String rootpath) {
        for (ModelicaFile mf : mfs) {
            mf.serializeAsMo(rootpath);
        }
    }



    public void serializeAsTTL(String filename, String prefix, String libraryRootName, String serializingOption) {
        this.prefix = prefix;
        try {
            FileWriter myWriter = new FileWriter(filename);

            writePrefixes(myWriter);

            replaceRelativePaths(libraryRootName);

            for (ModelicaFile mf : mfs) {
                for (MClass mk : mf.mks) {
                    if (serializingOption.equals("full")) {
                        myWriter.write(mk.serializeAsTTL());
                    } else if (serializingOption.equals("fullclean")) {
                            myWriter.write(cleanttl(mk.serializeAsTTL()));
                    } else {
                        myWriter.write(mk.serializeAsTTLHeaderAndParents());
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
     * @param libraryRootName
     */
    private void replaceRelativePaths(String libraryRootName) {
        generateContainerHierarchyTree(libraryRootName);
        for (ModelicaFile mf : mfs) {
            for (MClass mk : mf.mks) {
//                mk.replaceRelativePaths("AixLib.", packageHierarchyRoot);
                mk.replaceRelativePaths(libraryRootName, packageHierarchyRoot);
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
                    currentNode= currentNode.addChild(pathPart);}
                }
                currentNode = currentNode.addChild(mk.name); //also add models, not only package hierarchy
            }
        }
    }

    void generatePackageHierarchyFromPackageList() {
        List<String> mps_distinct = mps.stream()
                .distinct()
                .collect(Collectors.toList());
        Iterator<String> it = mps_distinct.iterator();
        while(it.hasNext()) {
            String cur = it.next();
            String[] teile = cur.split(".");
            for (String teil : teile           ) {
                //TODO
            }
        }
    }

    /**
     * erzeugt den Header des Turtle-Files
     * definiert Prefixes und importiert msl, moont
     * @param myWriter
     * @throws IOException
     */
    private void writePrefixes(FileWriter myWriter) throws IOException {myWriter.write("@prefix "+prefix+":    <http://www.eas.iis.fraunhofer.de/"+ prefix+"#> ." + NEWLINE);
        myWriter.write("@prefix moont:    <http://www.eas.iis.fraunhofer.de/moont#> ." + NEWLINE);
        //TODO Header entschlacken
        myWriter.write("@prefix msl:    <http://www.eas.iis.fraunhofer.de/msl#> ." + NEWLINE);
        myWriter.write("@prefix aix:    <http://www.eas.iis.fraunhofer.de/aix#> ." + NEWLINE);
        myWriter.write("@prefix mbl:    <http://www.eas.iis.fraunhofer.de/mbl#> ." + NEWLINE);
        myWriter.write("@prefix ibpsa:    <http://www.eas.iis.fraunhofer.de/ibpsa#> ." + NEWLINE);
        myWriter.write("@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> ." + NEWLINE);
        myWriter.write("@prefix rdf:  <http://www.w3.org/2000/01/rdf#> ." + NEWLINE);
        myWriter.write("@prefix owl:  <http://www.w3.org/2002/07/owl#> ." + NEWLINE);
        myWriter.write("@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> ." + NEWLINE);
        myWriter.write(prefix + ": rdf:type owl:Ontology ;" + NEWLINE);
        myWriter.write("\t owl:imports msl: ;" + NEWLINE); //TODO Import weglassen, wenn es sich um MSL handelt
        myWriter.write("\t owl:imports moont: ." + NEWLINE); //TODO nur importieren bei MSL, nicht bei den davon ableitenden
    }

    public String getName() {
        return name;
    }
}
