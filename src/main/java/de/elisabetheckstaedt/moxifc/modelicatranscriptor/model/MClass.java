package de.elisabetheckstaedt.moxifc.modelicatranscriptor.model;

import de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class MClass {

    private static final Logger LOGGER = LoggerFactory.getLogger(MClass.class);

    public static final String NEWLINE = System.lineSeparator();
    String type_prefix;
    String type;
    String name;
    String description = "";
    String container;
    String owlPrefix;
    String classPrefix;
    Set<String> parents = new HashSet<>();
    Set<String> parentsIcons = new HashSet<>();
    Set<MParameterComponent> parameters = new HashSet<>();
    Set<ModelicaObject> components = new HashSet<>();
    Set<MClass> childClasses = new HashSet<>();
    String annotation = "";
    String classContent;
    String file = "";
    int parentCount = 0;
    private final List<MConnection> connections = new LinkedList();

    /**
     * alles Strings
     * @param container
     * @param type
     * @param name
     * @param classContent
     * @param file
     * @param owlPrefix
     */
    public MClass(String container, String type, String name, String classContent, String file, String owlPrefix) {
        container = container.stripLeading();
        container = container.stripTrailing();
        this.container = container;
        this.type = type;
        this.name = name;
        this.owlPrefix = owlPrefix;
        this.classContent = classContent;
    }

    public MClass(String owlPrefix) {
        this.owlPrefix = owlPrefix;
    }

    public void addConnection(MConnection connection) {
        connections.add(connection);
    }

    public void sortParent() {
        if (parents.size()==0) {
            parentCount = 0;
            return;
        }

        Iterator<String> it = parents.iterator();
        while(it.hasNext()) {
            String cur = it.next();
            if(cur.contains(".Icons")) {
                parentsIcons.add(cur);
                it.remove();
            }
        }
        parentCount = parents.size();
    }

//    void BeschreibungAusInhalt() {
//        String klassenbeschreibung = "";
//        String klassenrest ="";
//        int Anzahl_Klassen_mit_mehreren_extends=0;
//
//        String regex_beschreibung = "\\s*?\\\"(.*?)\\\"(.*?)\\Z";
//        Matcher matcher = Pattern.compile(regex_beschreibung, Pattern.DOTALL | Pattern.MULTILINE).matcher(klasseninhalt);
//        if (matcher.matches()) {
//            klassenbeschreibung = matcher.group(1);
//        }
//        String Beschreibung_clean = klassenbeschreibung.replaceAll("\n", " ");
//        beschreibung = Beschreibung_clean.replaceAll("\r", " ");
//    }
//    void ParameterAusInhalt() {
//        String regex = "parameter\\s*(.*?)\\s(.*?)\\((.*?)\\)\\s*?=\\s*(.*?)\\s*\\\"(.*?)\\\"\\s*?;";
//        Matcher matcher = Pattern.compile(regex, Pattern.DOTALL | Pattern.MULTILINE).matcher(klasseninhalt);
//        while (matcher.find()) {
//            String typ = matcher.group(1);
//            String name = matcher.group(2);
//            String ueberschreibung = matcher.group(3);
//            String standardbelegung = matcher.group(4);
//            String beschreibung = matcher.group(5);
//            parameter.add(new ModelicaParameter(typ, name, standardbelegung, beschreibung));
//        }
//    }

//    void ParentAusInhalt(String suchtext){
//        String klassenvater ="";
//        String suchrest ="";
//        //extends
//        // extends mit Klammer direkt nach der Klasse, die extended wird
////        String regex_extends_mitKlammer = ".*?extends\\s(.*?)\\s*?\\((.*?)\\);(.*)\\Z";
//
//        //extends ohne Klammer?
////      String regex_extends_ohneKlammer = ".*?extends\\s*?(.*?)\\s*?;(.*)\\Z";
//        String regex_extends_ohneKlammer = "extends\\s(.*?);(.*)\\Z";
//        Matcher matcher = Pattern.compile(regex_extends_ohneKlammer, Pattern.DOTALL | Pattern.MULTILINE).matcher(suchtext);
//        if (matcher.find()) {
//            klassenvater = matcher.group(1);
//            suchrest = matcher.group(2);
//            //evtl. zu lang (bis zum ersten Semikolon, ggf. nach öffnender Klammer --> alles bis zur öffnenden Klammer
//            String regex_extends_mitKlammer = "(.*?)\\s*?\\((.*)\\Z";
//            matcher = Pattern.compile(regex_extends_mitKlammer, Pattern.DOTALL | Pattern.MULTILINE).matcher(klassenvater);
//            if (matcher.find()) {
//                klassenvater = matcher.group(1);
//                suchrest = suchrest + "(" + matcher.group(2);
//            }
//        } else {
//            String regex_extends_mitKlammer = "extends\\s(.*?)\\s*?\\((.*)\\Z";
//            matcher = Pattern.compile(regex_extends_mitKlammer, Pattern.DOTALL | Pattern.MULTILINE).matcher(suchtext);
//            if (matcher.find()) {
//                klassenvater= matcher.group(1);
//                suchrest = matcher.group(2);
//            } else {  //gar kein extends
//                klassenvater="";
//                suchrest="";
//            }
//        }
//        if (!(klassenvater.equals(""))) {
//            String klassenvater_clean = klassenvater.replaceAll("\n", " ");
//            klassenvater_clean = klassenvater_clean.replaceAll("\r", " ");
//            klassenvater_clean = klassenvater_clean.stripLeading();
//            klassenvater_clean = klassenvater_clean.stripTrailing();
//            parent.add(klassenvater_clean);
//        }
//        if (!(suchrest.equals(""))) {
//            ParentAusInhalt(suchrest);
//        }
//    }

    public String printHeader() {
        return("Container: " + container + "\tType:" + type + "\tName: " + name);
    }

    public String printHeaderEnhanced() {
//        return(print_header() +"\tBeschreibung: "+beschreibung + "\tParent: "+print_parent());
        return(printHeader()  + "\tParent: " + printParent());
    }

    public String printParent() {
        String alle_parent = "(";
        for(String el : parents) {
            alle_parent = alle_parent + el +",";
        }
        return alle_parent +")";
    }

    String writeParametersToTTL() {
        String zf = "";
        for (MParameterComponent mp : parameters)
        {
            zf = zf.concat(owlPrefix +":"+ container + "." + name + " moont:hasPart " + owlPrefix +":"+ container + "." + name + "." + mp.getName() + "." + NEWLINE);
            zf = zf.concat(owlPrefix +":"+ container + "." + name + "." + mp.getName()   + " a moont:MParameterComponent;" + NEWLINE);
            zf = zf.concat("\t moont:identifier \"" + mp.getName() + "\";" + NEWLINE);

            if ((mp.getTypeSpecifier().equals("Real"))) {//für Komponenten aus der MSL wird die Klasse nicht angegeben
                zf = zf.concat("\t" + " moont:type " + "xsd:Real" );
            } else if ((mp.getTypeSpecifier().equals("Integer"))) {//für Komponenten aus der MSL wird die Klasse nicht angegeben
                zf = zf.concat("\t" + " moont:type " + "xsd:Integer");
            } else if ((mp.getTypeSpecifier().equals("Boolean"))) {//für Komponenten aus der MSL wird die Klasse nicht angegeben
                zf = zf.concat("\t" + " moont:type " + "xsd:Boolean");
            } else {//für Komponenten aus der MSL wird die Klasse nicht angegeben
                zf = zf.concat("\t" + " moont:type " + owlPrefix +":" + mp.getTypeSpecifier()) ;
            }
            zf += ";" + NEWLINE ;
            if (!mp.getStringComment().equals("")) {
                zf += "\t" + " moont:stringComment \"" + maskSpecialCharacter(cleanStringFromLineBreaks(mp.getStringComment())) + "\"^^xsd:string;" + NEWLINE;
            }

            if (mp.getModification().equals("")) {
                zf = zf.concat("." + NEWLINE);
            } else {
                try {
                    String mod = mp.getModification().split("=")[1];
                    Double modDoub = Double.parseDouble(mod);
                    zf += "\t" + " moont:modification \"" + modDoub + "\"^^xsd:Real." + NEWLINE;
                } catch (NumberFormatException e) {
                    zf += "\t" + " moont:modification \"" + maskSpecialCharacter(cleanStringFromLineBreaks(mp.getModification())) + "\"^^xsd:string." + NEWLINE;
                }
            }
//Achtung: wenn Zeile wieder rein, Semikolon in Vorzeile oder Subjekt ergänzen
            //            parameterString = parameterString.concat("\t rdfs:range " +"aix:" + mp.klasse + "." + NEWLINE);
        }
        return zf;
    }

    String maskSpecialCharacter(String oldString) {
        String newString = oldString;
        if (newString.contains("\"")) {
            newString = newString.replace("\"", "\\\"");
        }
        return newString;
    }

    String writeComponentsToTTL() {
        String zf = "";
        for (ModelicaObject mo : components)
        {
            zf += owlPrefix + ":"+ container + "." + name +" moont:hasPart " + owlPrefix +":" + container + "." + name + "." + mo.name+ "." + NEWLINE;
            zf += owlPrefix +":"+ container + "." + name + "." + mo.name   + " a moont:MComponent ;"+ NEWLINE;
            zf = zf.concat("\t moont:identifier \"" + mo.getName() + "\";" + NEWLINE);
//            zf = zf.concat("\t moont:identifier \"" + mo.getName() + "\";" + NEWLINE);
//            zf += "\t"                                             + " a owl:NamedIndividual;" + NEWLINE;
            //transcribe String Comment
            if (mo.getStringComment().equals("")) {
                String a = "";
            } else {
                zf += "\t moont:stringComment \"" + maskSpecialCharacter(cleanStringFromLineBreaks(mo.getStringComment())) + "\"^^xsd:string;" + NEWLINE;
            }
            //transcribe parent class
            if (mo.mClass.name.equals("")) {
                continue; //temporarily: skip redeclare replaceable //TODO
            }
            else  if ((mo.mClass.name.startsWith("Modelica"))) {
//                zf += "\t" + " moont:type " + "msl:" + mo.mClass.name + "." + NEWLINE;
                zf += "\t" + " a " + "msl:" + mo.mClass.name + "." + NEWLINE;
            }
            else  if ((mo.mClass.name.startsWith("Buildings"))) {
                zf += "\t" + " a " + "mbl:" + mo.mClass.name + "." + NEWLINE;
            }
            else  if ((mo.mClass.name.startsWith("AixLib"))) {
                zf += "\t" + " a " + "aix:" + mo.mClass.name + "." + NEWLINE;
            }
            else  {
//                zf = zf.concat("aix:"+ container + "." + name + "." + mo.name + " rdfs:range " + "aix:" + mo.klasse.name + "." + NEWLINE);
                zf += "\t" + " a " + owlPrefix +":" + mo.mClass.name + "." + NEWLINE;
            }
            //handle modifications
            if (mo.getModification().equals("")) {
                //no modification present
            } else if (mo.getModification().stripLeading().stripTrailing().startsWith("=")) {
                //the component is a variable, its definition is not transcribed to KG
//            } else if (
//                //complex modification with inner bracket or square bracket - temporary: leave as is
//                    (mo.getModification().substring(1, mo.getModification().length()-1).contains("(")) ||
//                    (mo.getModification().substring(1, mo.getModification().length()-1).contains("{")) ||
//                    (mo.getModification().substring(1, mo.getModification().length()-1).contains("["))) {
//                zf += owlPrefix +":"+ container + "." + name + "." + mo.name   + " moont:modification \"" + maskSpecialCharacter(cleanStringFromLineBreaks(mo.getModification())) + "\"^^xsd:string." + NEWLINE;
            } else {
                //simple modification - just some values changed
//                String[] mods = mo.getModification().substring(1, mo.getModification().length()-1).split(",");
                String[] mods = mo.splitModifications();
                ScriptEngine engine = new ScriptEngineManager().getEngineByName("graal.js");
                for (String mod:mods) {
                    if (mod.split("=").length!=2) {//complex expression is copied to KG //TODO check whether this should be omitted in the KG
                        zf += owlPrefix +":"+ container + "." + name + "." + mo.name   + " moont:modification \"" + maskSpecialCharacter(cleanStringFromLineBreaks(mod)) + "\"^^xsd:string." + NEWLINE;
                    } else {
                        String comp = mod.split("=")[0];
                        String value = mod.split("=")[1];
                        if (comp.contains("(") || value.contains("}")|| value.contains("\"")) {
                            LOGGER.warn("modification not transcribed to KG"); //TODO handle complex modifications - should be solved if modifications are separated when parsing the *.mo-file
                            continue;
                        }
                        try { //simple case: modification with a number assigned to a variable
                            double doubleValue = Double.parseDouble(value);
                            zf += owlPrefix + ":" + container + "." + name + "." + mo.name + "." + comp + " moont:modification \"" + doubleValue + "\"^^xsd:Real;" + NEWLINE;
                            zf +=  "\t moont:identifier \"" + comp + "\"." + NEWLINE;
                            zf += owlPrefix + ":" + container + "." + name + "." + mo.name + " moont:hasPart " + owlPrefix + ":" + container + "." + name + "." + mo.name + "." + comp + "." + NEWLINE;
                        } catch(NumberFormatException e) {
                            if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("true")) {
                                zf += owlPrefix + ":" + container + "." + name + "." + mo.name + "." + comp + " moont:modification \"" + value + "\"^^xsd:boolean;" + NEWLINE;
                                zf +=  "\t moont:identifier \"" + comp + "\"." + NEWLINE;
                                zf += owlPrefix + ":" + container + "." + name + "." + mo.name + " moont:hasPart " + owlPrefix + ":" + container + "." + name + "." + mo.name + "." + comp +  "." + NEWLINE;
                            } else if (comp.startsWith("redeclare")) {
                                zf += owlPrefix +":"+ container + "." + name + "." + mo.name   + " moont:modification \"" + maskSpecialCharacter(cleanStringFromLineBreaks(mod)) + "\"^^xsd:string;" + NEWLINE;
                                zf +=  "\t moont:identifier \"" + comp + "\"." + NEWLINE;
//                                zf += owlPrefix + ":" + container + "." + name + "." + mo.name + " moont:hasPart " + owlPrefix + ":" + container + "." + name + "." + mo.name + "." + comp +  "." + NEWLINE;
                            } else {//equation assigned to a variable
                                try {//if possible: evaluate
                                    Object result = engine.eval(value);
                                    zf += owlPrefix + ":" + container + "." + name + "." + mo.name + "." + comp + " moont:modification \"" + result.toString() + "\"^^xsd:Real;" + NEWLINE;
                                    zf +=  "\t moont:identifier \"" + comp + "\"." + NEWLINE;
                                    zf += owlPrefix + ":" + container + "." + name + "." + mo.name + " moont:hasPart " + owlPrefix + ":" + container + "." + name + "." + mo.name + "." + comp +  "." + NEWLINE;
                                } catch (ScriptException ex) {
                                    // simple expression (equation assigned to parameter) is handled in knowledge graph //TODO check whether this should be omitted in the KG
                                        zf += owlPrefix + ":" + container + "." + name + "." + mo.name + "." + comp + " moont:modification \"" + value + "\"^^xsd:String;" + NEWLINE;
                                        zf +=  "\t moont:identifier \"" + comp + "\"." + NEWLINE;
                                        zf += owlPrefix + ":" + container + "." + name + "." + mo.name + " moont:hasPart " + owlPrefix + ":" + container + "." + name + "." + mo.name + "." + comp +  "." + NEWLINE;
                                }
                            }
                        }
                    }
                }
            }
        }
        return zf;
    }


    String cleanName(String nameOld) { //TODO replace cleanName
        String nameNew = nameOld;
        if (nameOld.contains("[")) {
            nameNew = nameNew.replace("[", "_");
            nameNew = nameNew.replace("]", "");
        } if (nameOld.contains("+")) {
            nameNew = nameNew.replace("+", "plus");
        } if (nameOld.contains(",")) {
            nameNew = nameNew.replace(",", "comma");
        } if (nameOld.contains(":")) {
            nameNew = nameNew.replace(":", "TO");
        } if (nameOld.contains("*")) {
            nameNew = nameNew.replace("*", "TIMES");
        } if (nameOld.contains("(")) {
            nameNew = nameNew.replace("(", "_");
            nameNew = nameNew.replace(")", "");
        } if (nameOld.contains(">")) {
            nameNew = nameNew.replace(">", "GREATER");
        } if (nameOld.contains("<")) {
            nameNew = nameNew.replace("<", "SMALLER");
        } if (nameOld.contains("-")) {
            nameNew = nameNew.replace("*", "MINUS");
        }
        return nameNew;
    }

    String writeConnectionsToTTL() {
        String zf = "";
        for (MConnection mc : connections)
        {
            String leftPortName = cleanName(mc.getLeftPort());
            //left component
            if (mc.getLeftPort().contains("[")) {
                zf += writeClassNamespace() + leftPortName + " a moont:Vector." + NEWLINE;
            }
            if (mc.getLeftComponent() == null) { //wenn  Verbindung von einem Konnektor des Modells ausgeht
                zf += writeClassNamespace() + leftPortName;
            } else { //wenn Verbindung vom Konnektor einer Komponente des Modells ausgeht
                String leftComponentName = cleanName(mc.getLeftComponent());
                if (mc.getLeftComponent().contains("[")) {
                    zf += writeClassNamespace() + leftComponentName + " a moont:Vector." + NEWLINE;
                }
                zf +=  writeClassNamespace() + leftComponentName + " moont:hasPart " + writeClassNamespace() + leftComponentName + "." + leftPortName + "." + NEWLINE;
                zf +=  writeClassNamespace() + leftComponentName +  "." + leftPortName + " a moont:MConnectorComponent." + NEWLINE;
                zf +=  writeClassNamespace() + leftComponentName +  "." + leftPortName + " moont:identifier \"" + leftPortName + "\"." + NEWLINE;
                zf +=  writeClassNamespace() + leftComponentName + "." + leftPortName;
            }

            zf += " moont:connectedTo ";

            //right component
            String rightPortName = cleanName(mc.getRightPort());
            if (mc.getRightComponent() == null) {
                zf += writeClassNamespace() + rightPortName + "." + NEWLINE;
                if (mc.getRightPort().contains("[")) {
                    zf += writeClassNamespace() + rightPortName + " a moont:Vector." + NEWLINE;
                }
            } else {
                String rightComponentName = cleanName(mc.getRightComponent());
                zf +=  writeClassNamespace() + rightComponentName + "." + rightPortName + "." + NEWLINE;
                zf +=  writeClassNamespace() + rightComponentName + " moont:hasPart " + writeClassNamespace() + rightComponentName + "." + rightPortName + "." + NEWLINE;
                zf +=  writeClassNamespace() + rightComponentName + "." + rightPortName + " a moont:MConnectorComponent." + NEWLINE;
                zf +=  writeClassNamespace() + rightComponentName +  "." + rightPortName + " moont:identifier \"" + rightPortName + "\"." + NEWLINE;
                if (mc.getRightComponent().contains("[")) {
                    zf += writeClassNamespace() + rightComponentName + " a moont:Vector." + NEWLINE;
                }
            }
        }
        return zf;
    }
    String writeClassNamespace() {
        return owlPrefix + ":"+ container + "." + name + ".";
    }
    /*
    String writeKomponentenToTTL() {
        String zf = "";
        for (ModelicaObjekt mo : komponenten)
        {
            zf = zf.concat("aix:"+ container + "." + name +" owl:hasPart " +"aix:" + container + "." + name + "." + mo.name+ "." + NEWLINE);
            if (mo.klasse.name.equals("")) { //wenn es keine
                continue; //TODO kommt bei redeclare replaceable
            }
            else  if (!(mo.klasse.name.startsWith("Modelica"))) {//für Komponenten aus der MSL wird die Klasse nicht angegeben
                zf = zf.concat("aix:"+ container + "." + name + "." + mo.name + " a owl:ObjectProperty ;" + NEWLINE);
                zf = zf.concat("\t" + " rdfs:range " + "msl:" + mo.klasse.name + ";" + NEWLINE);
                zf = zf.concat("\t" + " rdfs:domain " + "aix:"+ container + "." + name + "." + NEWLINE);
            }
            else  {
//                zf = zf.concat("aix:"+ container + "." + name + "." + mo.name + " rdfs:range " + "aix:" + mo.klasse.name + "." + NEWLINE);
                zf = zf.concat("aix:"+ container + "." + name + "." + mo.name + " a owl:ObjectProperty ;" + NEWLINE);
                zf = zf.concat("\t" + " rdfs:range " + "aix:" + mo.klasse.name + ";" + NEWLINE);
                zf = zf.concat("\t" + " rdfs:domain " + "aix:"+ container + "." + name + "." + NEWLINE);
            }
        }
        return zf;
    }*/

    /**
     * vollständig
     * @return
     */
    String serializeAsTTL() {
        String zf = "";
        zf = zf.concat(writeHeaderToTTL());
        zf = zf.concat(writeParentToTTL());
        zf = zf.concat(writeParentIconsToTTL());
        zf = zf.concat(writeParametersToTTL());
        zf = zf.concat(writeComponentsToTTL());
        zf = zf.concat(writeConnectionsToTTL());
        return zf;
    }

    /**
     * nur Header und Parents
     * @return
     */
    String serializeAsTTLHeaderAndParents() {
        String zf = "";
        zf = zf.concat(writeHeaderToTTL());
        zf = zf.concat(writeParentToTTL());
        zf = zf.concat(writeParentIconsToTTL());
        return zf;
    }

    /**
     * Modelicaklasse: Model, Class, Package, ...
     * Container (Packagestruktur)
     * ggf. is Partial
     * @return
     */
    private String writeHeaderToTTL() {
        String zf = "";
    /*        //ausführbare Modelle und Packages werden als Instanzen modelliert
            if (parentsIcons.contains("Modelica.Icons.Example") | getTypeAsMoont().equals("MPackage") ) {*/
        if (container.isBlank()) {
            zf = zf.concat(owlPrefix +":" + name + " rdfs:subClassOf moont:" + getTypeAsMoont());
        } else {
            zf = zf.concat(owlPrefix +":" + container + "." + name + " rdfs:subClassOf moont:" + getTypeAsMoont());
        }
        if (!description.equals("")) {
            zf = zf.concat(";" + NEWLINE);
            zf = zf.concat("\t moont:stringComment " + cleanStringFromLineBreaks(description) + "^^xsd:string");
        }
/*        // alles andere wird als Subclass modelliert
        } else {
            if (container.isBlank()) {
                zf = zf.concat(owlPrefix +":" + name + " rdfs:subclassOf moont:" + getTypeAsMoont() );
            } else {
                zf = zf.concat(owlPrefix +":" + container + "." + name + " rdfs:subclassOf moont:" + getTypeAsMoont() + ";" + NEWLINE );
            }
            zf = zf.concat("\t" + " rdf:type owl:Class;"+ NEWLINE);
        }*/
        if (!container.isBlank()) {
            zf = zf.concat(";" + NEWLINE);
            zf = zf.concat("\t" + " moont:containedIn " + owlPrefix + ":" + container);
        }
        if (type_prefix.isEmpty()) {
            zf = zf.concat("." + NEWLINE);
        } else {
            zf = zf.concat(";" + NEWLINE);
            if (type_prefix.contains("partial")) {
                zf = zf.concat("\t moont:isPartial \"true\"^^xsd:boolean." + NEWLINE);
            } else {
                zf = zf.concat("." + NEWLINE);

            }
        }

        return zf;
    }

    String cleanStringFromLineBreaks(String input) {
        input = input.replace("\r\n", "_");
        input = input.replace("\n", "_");
        input = input.replace("\r", "_");
        return input;

    }

    /**
     * extends
     * @return
     */
    String writeParentToTTL() {
        String zf = "";
        for (String el : parents) {
            if (container.isBlank()) { //18.4.21: es passiert beides mal das Gleiche?!
                zf = zf.concat(owlPrefix +":" + container + "." + name + " moont:extends " + owlPrefix +":" + el + "." + NEWLINE);
            } else {
                zf = zf.concat(owlPrefix +":" + container + "." + name + " moont:extends " + owlPrefix +":" + el + "." + NEWLINE);
            }
            if (!(parents.contains(el))) { //18.4.21: Wann soll das denn eintreffen?
                parents.add(el);
            }
        }

        return zf;
    }

    /**
     * extends (nur die Klassen die Icons vererben)
     * @return
     */
    String writeParentIconsToTTL() {
        String zf = "";
        for (String el : parentsIcons) {
            if (container.isBlank()) { //18.4.21: es passiert beides mal das Gleiche?!
                zf = zf.concat(owlPrefix +":" + container + "." + name + " moont:extends " + owlPrefix +":" + el + "." + NEWLINE);
            } else {
                zf = zf.concat(owlPrefix +":" + container + "." + name + " moont:extends " + owlPrefix +":" + el + "." + NEWLINE);
            }
            if (!(parents.contains(el))) { //18.4.21: Wann soll das denn eintreffen?
                parents.add(el);
            }
        }

        return zf;
    }

    void serializeAsMo(String rootpath) {
        System.out.println(name);
        String path = container.replace(".","/");
        if (type.equals("package")) {
            serializePackageAsMo();
        } else {
            try {
                Path root = Files.createDirectories(Path.of(rootpath + "/" + path + "/"));
                FileWriter myWriter = new FileWriter(root.resolve(name + ".mo").toFile());
                StringBuilder str = new StringBuilder();
                
                str.append(type + " " + name +NEWLINE);

                parents.stream()
                        .forEach(p -> str.append("\textends "  + p + ";" + NEWLINE));

                str.append(NEWLINE);

                parameters.stream()
                        .sorted(Comparator
                                .comparing((MParameterComponent p) -> p.getTypeSpecifier())
                                .thenComparing(p -> p.getName()))
                        .forEach(p -> str.append("\t parameter " + p.getTypeSpecifier() + " " + p.getName() +  p.getModification() + "\t" + p.getStringComment() + " " + p.getAnnotation() +";" + NEWLINE))                ;

                str.append(NEWLINE);

                components.stream()
                        .sorted(Comparator
                                .comparing((ModelicaObject c) -> c.typePrefix)
                                .thenComparing(c -> c.mClass.name)
                                .thenComparing(c -> c.name))
                        .forEach(c -> str.append("\t" + c.getTypePrefix() + " " + c.getTypeSpecifier() + " " + c.getName() +  c.getModification() + "\t" + c.getStringComment() + " " + c.getAnnotation() +";" + NEWLINE));

                str.append(NEWLINE + "equation" + NEWLINE);
                try { //TODO ordentlich abprüfen, ob connections vorhanden sind
                    connections.stream()
                            .forEach(cn -> str.append("\tconnect(" + cn.getLeftComponent() + "," + cn.getRightComponent() + ")\t" + cn.getAnnotation() + ";" + NEWLINE));
                } catch (NullPointerException ne) {ne.printStackTrace();}
                str.append("end " + name + ";" + NEWLINE);
                myWriter.write(str.toString());
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String huppifluppi(MParameterComponent p) {
        return p.getName();
    }


    void serializePackageAsMo() {
        //TODO package.mo & package.order
    }
    public void setContainer(String container) {
        this.container = container;
    }

    public void setType(String type) {
        this.type_prefix = "";
        if (type.contains("partial")) {
            type = type.replace("partial","");
            this.type_prefix = this.type_prefix + "partial";
        }
        if (type.contains("operator")) {
            type = type.replace("operator","");
            this.type_prefix = this.type_prefix + " operator";
        }
        if (type.contains("expandable")) {
            type = type.replace("expandable","");
            this.type_prefix = this.type_prefix + " expandable";
        }
        if (type.contains("pure")) {
            type = type.replace("pure","");
            this.type_prefix = this.type_prefix + " pure";
        }
        if (type.contains("impure")) {
            type = type.replace("impure","");
            this.type_prefix = this.type_prefix + " impure";
        }
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setClassContent(String classContent) {
        this.classContent = classContent;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Set<MClass> getChildClasses() {
        return childClasses;
    }

    public void appendChildClass(MClass child) {
        childClasses.add(child);
    }

    public void appendParent(String newParent) {
        parents.add(newParent);
    }

    public void appendParameter(MParameterComponent mp) {
        parameters.add(mp);
    }

    public void appendComponent(ModelicaObject komp) {
        components.add(komp);
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getTypeAsMoont() {
        switch(type) {
            case "class": return "MClass";
            case "model": return "MModel";
            case "record": return "MRecord";
            case "block": return "MBlock";
            case "connector": return "MConnector";
            case "type": return "MType";
            case "package": return "MPackage";
            case "function": return "MFunction";
            default:
                System.out.println(container + "." + name + " Type not supported");
        }
        return type;
    }

    String findParentNodeOfSearchString(TreeNode<String> root, String startPath, String searchString) {
        try {
            return findParentNodeOfSearchString(root.findTreeNode(startPath), searchString);
        } catch (Exception e) {
            LOGGER.info("Reached root node while searching for " + searchString + " starting from " + startPath);
            return "";
        }

    }

    private String findParentNodeOfSearchString(TreeNode<String> startNode, String searchString) throws NullPointerException {
//        Optional<TreeNode<String>> optionalHit = startNode.getSiblings()
            Optional<TreeNode<String>> optionalHit = startNode.getParent().getChildren()
                .stream()
                .filter(c -> c.getData().equals(searchString))
                .findAny();
            if(optionalHit.isPresent()) {
                return startNode.getParent().getFullPath();
            } else {
                if(startNode.isRoot()) {
    //                throw new RuntimeException("Reached root node while searching for " + searchString);
                    System.out.println("Reached root node while searching for " + searchString); //kommt nie zum Tragen
                }
                return findParentNodeOfSearchString(startNode.getParent(), searchString);
            }
    }

    /**
     *
     * @param node Vater
     * @param name Name des Kindes
     * @return true, wenn der Knoten ein Kind dieses Namens hat
     * @throws NullPointerException
     */
    private boolean hasChildWithName(TreeNode<String> node, String name) throws NullPointerException{
        return node.getChildren().stream().anyMatch((TreeNode<String> t) -> t.getData().equals(name));
    }

    /**
     * replace(libraryRootName, packageTree) wenn es sich nicht um einen wohlbekannten Start handelt
     * für alle Parameter, Komponenten, Parents
     * @param libraryRootName
     * @param packageTree
     */
    public void replaceRelativePaths(String libraryRootName, TreeNode<String> packageTree) {
        for (MParameterComponent mp :  parameters) {
            mp.setTypeSpecifier(replace(libraryRootName, packageTree, mp.getTypeSpecifier()));
        }
        for (ModelicaObject mp :  components) {
            mp.setTypeSpecifier(replace(libraryRootName, packageTree, mp.getTypeSpecifier()));
        }
        Set<String> newParents = new HashSet<>();
        for (String mp :  parents) {
            String newParentName = replace(libraryRootName, packageTree, mp);
            newParents.add(newParentName);
        }
        parents = newParents;
        Set<ModelicaObject> components = new HashSet<>();
    }

    /**
     * wenn "typeSpecifier" nicht mit wohlbekannten Kürzeln startet, wird der Vorpfad davor gesetzt
     * @param libraryRootName
     * @param packageTree
     * @param typeSpecifier
     * @return
     */
    private String replace(String libraryRootName, TreeNode<String> packageTree, String typeSpecifier) {
        if (typeSpecifier.startsWith(libraryRootName) || //TODO: eigentlich müssten hier die Imports behandelt werden, statt dieser pauschalen Lösung
                typeSpecifier.startsWith("Medium") ||
                typeSpecifier.startsWith("SI") || //weiterhin gesehen SIunits
                typeSpecifier.startsWith("NonSI") ||
                typeSpecifier.startsWith("SDF") ||
                typeSpecifier.startsWith("LibEAS") ||
                typeSpecifier.startsWith("AixLib") ||
                typeSpecifier.startsWith("Buildings") ||
                typeSpecifier.startsWith("Modelica")) {
            return typeSpecifier;
        } else if (typeSpecifier.equalsIgnoreCase("Real") ||
                typeSpecifier.equalsIgnoreCase("Integer") ||
                typeSpecifier.equalsIgnoreCase("String") ||
                typeSpecifier.equalsIgnoreCase("Boolean")) {
            return typeSpecifier;
        } else {
            String newTypeSpecifier = "";
            String vorpfad = "";
            String[] nameparts = typeSpecifier.split("\\.", 0);
            // wenn es bereits ein absoluter Pfad ist - vorher abfangen, da ansonsten die folgende if-Bedingung einen null-pointer bringt
            if (container.equals(libraryRootName)) {
                newTypeSpecifier = container + "." + typeSpecifier;
            // wenn es ein Geschwister der aktuellen Klasse ist
            } else if (hasChildWithName(packageTree.getIndex().get(container), nameparts[0])) {
                newTypeSpecifier = container + "." + typeSpecifier;
            //wenn es ein Kinder aktuellen Klasse ist (lokal definierte Klasse)
            } else if (hasChildWithName(packageTree.getIndex().get(container+"."+name), nameparts[0])) {
                newTypeSpecifier = container + "." + name + "." + typeSpecifier;
            // Vorfahren suchen
            } else {
                try {
                    vorpfad = findParentNodeOfSearchString(packageTree, container, nameparts[0]);
                    //wenn während der Suche nach oben die Wurzel erreicht wird, kommt dieser leere String zurück
                    //FIXME das ist dann ein nicht behandelter Import --> erstmal kennzeichnen, später fixen
                    if (vorpfad.equals("")) {
                        newTypeSpecifier = "IMPORT." + typeSpecifier;
                    //Normalfall: wenn es beim Suchen nach oben gefunden wird
                    } else {
                        newTypeSpecifier = vorpfad + "." + typeSpecifier;
                    }
                }
                //wenn Pfad nicht gefunden wird (dann ist es wahrscheinlich eine Komponente, die das Modell erbt)
                catch (Exception e) {
                    newTypeSpecifier = typeSpecifier;
                }
            }
            return newTypeSpecifier;
        }

    }



    public String getContainer() {
        return container;
    }

    public String getName() {
        return name;
    }
}
