package de.elisabetheckstaedt.moxifc.modelicatranscriptor.model;

import de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser.ModelicaFileAntlrParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.Files.readString;

public class ModelicaLibrary {
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



    public void serializeAsTTL(String filename) {

        try {
            FileWriter myWriter = new FileWriter(filename);

            myWriter.write("@prefix "+prefix+":    <http://www.eas.iis.fraunhofer.de/"+ prefix+"#> ." + NEWLINE);
            myWriter.write("@prefix moont:    <https://www.eas.iis.fraunhofer.de/MoOnt#> ." + NEWLINE);
            myWriter.write("@prefix msl:    <http://www.eas.iis.fraunhofer.de/msl#> ." + NEWLINE);
            myWriter.write("@prefix aix:    <http://www.eas.iis.fraunhofer.de/aix#> ." + NEWLINE);
            myWriter.write("@prefix mbl:    <http://www.eas.iis.fraunhofer.de/mbl#> ." + NEWLINE);
            myWriter.write("@prefix ibpsa:    <http://www.eas.iis.fraunhofer.de/ibpsa#> ." + NEWLINE);
            myWriter.write("@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> ." + NEWLINE);
            myWriter.write("@prefix rdf:  <http://www.w3.org/2000/01/rdf#> ." + NEWLINE);
            myWriter.write("@prefix owl:  <http://www.w3.org/2002/07/owl#> ." + NEWLINE);
            myWriter.write("@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> ." + NEWLINE);
            myWriter.write(prefix + ": rdf:type owl:Ontology ;" + NEWLINE);
            myWriter.write("\t owl:imports <https://www.eas.iis.fraunhofer.de/MoOnt> ." + NEWLINE);

            for (ModelicaFile mf : mfs) {
                for (MClass mk : mf.mks) {
                    myWriter.write(mk.serializeAsTTL());
                }
            }
            myWriter.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }
}
