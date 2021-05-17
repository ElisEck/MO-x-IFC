package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class ModelicaFile {
    public Set<ModelicaClass> mks = new HashSet<>();
    String path;
    String fileRemainder;
    String containerName;
    ModelicaPackage containerPackage;

    public ModelicaFile(Path file)
    {
        String dateiinhalt;
        //file einlesen
        try {
            dateiinhalt = Files.readString(file);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
        this.fileRemainder = dateiinhalt;
        this.containerName = "";
        this.path = file.toString();
    }

    void serializeAsMo(String rootpath) {
        for (ModelicaClass mk : mks) {
            mk.serializeAsMo(rootpath);
        }
    }

    public Set<ModelicaClass> getMks() {
        return mks;
    }
/*    public void generateClassesFromString() {
        String classType = "";
        String rest = "";
        String className ="";
        String classContent = "";

        //Container finden
        String regex = "within\\s*?(.*?);.*";
        Matcher matcher = matcher = Pattern.compile(regex, Pattern.DOTALL | Pattern.MULTILINE).matcher(this.fileRemainder);
        if(matcher.matches()) {
            this.containerName = matcher.group(1);
        }
        //Klassenname und -inhalt finden
        String[] klassen = {"partial model", "partial package", "partial record", "partial block","partial function","partial class", "partial connector","partial type", "model", "package", "record", "block","function","class", "connector","type"};
        String regex2;
        for (String klasse : klassen) {
            regex2 = ".*?\\s" + klasse + "\\s(.*?)\\s(.*?)\\send\\s\\1\\s*;(.*)";
            matcher = Pattern.compile(regex2, Pattern.DOTALL | Pattern.MULTILINE).matcher(this.fileRemainder);
            classType = klasse;
            if (matcher.matches()) {
                className = matcher.group(1);
                classContent = matcher.group(2);
                this.fileRemainder = matcher.group(3);
                break;
            }
        }
        mks.add(new ModelicaClass(containerName, classType, className, classContent, path, prefix));
        containerPackage = new ModelicaPackage(containerName);
    }*/


}
