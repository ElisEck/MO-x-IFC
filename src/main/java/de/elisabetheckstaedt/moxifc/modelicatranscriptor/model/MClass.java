package de.elisabetheckstaedt.moxifc.modelicatranscriptor.model;

import de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class MClass {

    private static final Logger LOGGER = LoggerFactory.getLogger(MClass.class);

    public static final String NEWLINE = System.lineSeparator();
    String container;
    String owlPrefix;
    String type;
    String type_prefix;
    String name;
    String classPrefix;
    String description = "";
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

            if ((mp.getTypeSpecifier().equals("Real"))) {//für Komponenten aus der MSL wird die Klasse nicht angegeben
                zf = zf.concat("\t" + " moont:type " + "xsd:Real" );
            } else if ((mp.getTypeSpecifier().equals("Integer"))) {//für Komponenten aus der MSL wird die Klasse nicht angegeben
                zf = zf.concat("\t" + " moont:type " + "xsd:Integer");
            } else if ((mp.getTypeSpecifier().equals("Boolean"))) {//für Komponenten aus der MSL wird die Klasse nicht angegeben
                zf = zf.concat("\t" + " moont:type " + "xsd:Boolean");
            } else {//für Komponenten aus der MSL wird die Klasse nicht angegeben
                zf = zf.concat("\t" + " moont:type " + owlPrefix +":" + mp.getTypeSpecifier()) ;
            }

            if (mp.getModification().equals("")) {
                zf = zf.concat("." + NEWLINE);
            } else {
                zf += ";" + NEWLINE + "\t" + " moont:modification \"" + maskSpecialCharacter(mp.getModification()) + "\"^^xsd:string." + NEWLINE;
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
//            zf += "\t"                                             + " a owl:NamedIndividual;" + NEWLINE;
            if (mo.getModification().equals("")) {
                String a = "";
            } else {
                zf += "\t" + " moont:modification \"" + maskSpecialCharacter(mo.getModification()) + "\"^^xsd:string;" + NEWLINE;
            }
            if (mo.mClass.name.equals("")) { //wenn es keine
                continue; //TODO kommt bei redeclare replaceable
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
        }
        return zf;
    }

    String cleanName(String nameOld) { //TODO cleanName ersetzen
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
            zf = zf.concat(owlPrefix +":" + name + " rdfs:subClassOf moont:" + getTypeAsMoont() );
        } else {
            zf = zf.concat(owlPrefix +":" + container + "." + name + " rdfs:subClassOf moont:" + getTypeAsMoont() + ";" + NEWLINE );
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

    private String findParentNodeOfSearchString(TreeNode<String> startNode, String searchString) {
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
                    System.out.println("Reached root node while searching for " + searchString);
                }
                return findParentNodeOfSearchString(startNode.getParent(), searchString);
            }
    }

    private boolean hasChildWithName(TreeNode<String> node, String name) {
        return node.getChildren().stream().anyMatch((TreeNode<String> t) -> t.getData().equals(name));
    }

    public void replaceRelativePaths(String sollstart, TreeNode<String> packageTree) {
        for (MParameterComponent mp :  parameters) {
            mp.setTypeSpecifier(replace(sollstart, packageTree, mp.getTypeSpecifier()));
        }
        for (ModelicaObject mp :  components) {
            mp.setTypeSpecifier(replace(sollstart, packageTree, mp.getTypeSpecifier()));
        }
        Set<String> newParents = new HashSet<>();
        for (String mp :  parents) {
            String newParentName = replace(sollstart, packageTree, mp);
            newParents.add(newParentName);
        }
        parents = newParents;
        Set<ModelicaObject> components = new HashSet<>();
    }

    private String replace(String sollstart, TreeNode<String> packageTree, String typeSpecifier) {
        if (typeSpecifier.startsWith(sollstart) ||
                typeSpecifier.startsWith("Medium") ||
                typeSpecifier.startsWith("SI") ||
                typeSpecifier.startsWith("SDF") ||
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
            String[] nameparts = typeSpecifier.split("\\.",0);
           if (typeSpecifier.contains(".")) {
               if (hasChildWithName(packageTree.getIndex().get(container), nameparts[0])) { //erstmal "nach unten" suchen
                   newTypeSpecifier = container + "." + typeSpecifier;
               } else {
                   vorpfad = findParentNodeOfSearchString(packageTree, container, nameparts[0]);
                   if (vorpfad.equals("")) {
                       newTypeSpecifier = typeSpecifier;
                   } else {
                       newTypeSpecifier = vorpfad + "." + typeSpecifier;
                   }
               }
           } else {
               newTypeSpecifier = container + "." + typeSpecifier;
           }
           return newTypeSpecifier;

        }
    }


}
