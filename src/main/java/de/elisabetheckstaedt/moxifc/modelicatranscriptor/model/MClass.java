package de.elisabetheckstaedt.moxifc.modelicatranscriptor.model;

import de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser.Helper.*;
import static de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser.TreeNode.findParentNodeOfSearchString;
import static de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser.TreeNode.hasChildWithName;

public class MClass {


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
     * Constructor
     * all parameters are given as Strings
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

    /**
     * constructor
     * @param owlPrefix as String
     */
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


    String writeParametersToTTL(String backupPrefix) {
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
                zf = zf.concat("\t" + " moont:type " + attachPrefixToModelicaName(mp.getTypeSpecifier(), backupPrefix)) ;
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



    /**
     * transcribe components (variables, submodels) to triples, parameters are handled separately
     * Y moont:hasPart X
     * X a moont:MComponent
     * X moont:stringComment
     * X a parentClassName
     * X moont:modification Z
     * @return String
     */
    String writeComponentsToTTL(String backupPrefix) {
        String zf = "";
        for (ModelicaObject mo : components)
        {
            zf += owlPrefix + ":"+ container + "." + name +" moont:hasPart " + owlPrefix +":" + container + "." + name + "." + mo.name+ "." + NEWLINE;
            zf += owlPrefix +":"+ container + "." + name + "." + mo.name   + " a moont:MComponent ;"+ NEWLINE;
            zf = zf.concat("\t moont:identifier \"" + mo.getName() + "\";" + NEWLINE);
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
            else  {
                zf += "\t" + " a " + attachPrefixToModelicaName(mo.mClass.name, backupPrefix) + "." + NEWLINE;
            }
            //transcribe modifications
            zf = mo.writeObjectModificationsToTTL(zf, owlPrefix +":"+ container + "." + name + "." + mo.name, container + "." + name);
        }
        return zf;
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

    /**
     * calls separate functions for header, parents, icons, parameters, components, connections
     * @return String
     */
    String serializeAsTTL(String backupPrefix) {
        String zf = "";
        zf = zf.concat(writeHeaderToTTL());
        zf = zf.concat(writeParentToTTL(backupPrefix));
        zf = zf.concat(writeParentIconsToTTL(backupPrefix));
        zf = zf.concat(writeParametersToTTL(backupPrefix));
        zf = zf.concat(writeComponentsToTTL(backupPrefix));
        zf = zf.concat(writeConnectionsToTTL());
        return zf;
    }

    /**
     * only used if not "Full" or "fullclean" option is used
     * nur Header und Parents
     * @return
     */
    String serializeAsTTLHeaderAndParents(String backupPrefix) {
        String zf = "";
        zf = zf.concat(writeHeaderToTTL());
        zf = zf.concat(writeParentToTTL(backupPrefix));
        zf = zf.concat(writeParentIconsToTTL(backupPrefix));
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



    /**
     * serialized "extends" to ttl
     * @return
     */
    String writeParentToTTL(String backupPrefix) {
        String zf = "";
        for (String el : parents) {
            if (container.isBlank()) {
                zf = zf.concat(owlPrefix +":" + name + " moont:extends " + attachPrefixToModelicaName(el, backupPrefix) + "." + NEWLINE);
            } else {
                zf = zf.concat(owlPrefix +":" + container + "." + name + " moont:extends " + attachPrefixToModelicaName(el, backupPrefix) + "." + NEWLINE);
            }
        }

        return zf;
    }

    /**
     * extends (nur die Klassen die Icons vererben)
     * @return
     */
    String writeParentIconsToTTL(String backupPrefix) {
        String zf = "";
        for (String el : parentsIcons) {
            if (container.isBlank()) {
                zf = zf.concat(owlPrefix +":" + name + " moont:extends " + attachPrefixToModelicaName(el, backupPrefix) + "." + NEWLINE);
            } else {
                zf = zf.concat(owlPrefix +":" + container + "." + name + " moont:extends " + attachPrefixToModelicaName(el, backupPrefix) + "." + NEWLINE);
            }
            if (!(parents.contains(el))) { //kommt vor
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



    /**
     * replace(libraryRootName, packageTree) wenn es sich nicht um einen wohlbekannten Start handelt
     * für alle Parameter, Komponenten, Parents
     * @param libraryRootName
     * @param packageTree
     */
    public void replaceRelativeModelicaPathsWithAbsoluteModelicaPathsForAllParametersComponetsParents(String libraryRootName, TreeNode<String> packageTree) {
        for (MParameterComponent mp :  parameters) {
            mp.setTypeSpecifier(replaceRelativeModelicaPathWithAbsoluteModelicaPath(libraryRootName, packageTree, mp.getTypeSpecifier()));
        }
        for (ModelicaObject mp :  components) {
            mp.setTypeSpecifier(replaceRelativeModelicaPathWithAbsoluteModelicaPath(libraryRootName, packageTree, mp.getTypeSpecifier()));
        }
        Set<String> newParents = new HashSet<>();
        for (String mp :  parents) {
            String newParentName = replaceRelativeModelicaPathWithAbsoluteModelicaPath(libraryRootName, packageTree, mp);
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
    private String replaceRelativeModelicaPathWithAbsoluteModelicaPath(String libraryRootName, TreeNode<String> packageTree, String typeSpecifier) {
        if (typeSpecifier.startsWith(libraryRootName) || //TODO: eigentlich müssten hier die Imports behandelt werden, statt dieser pauschalen Lösung
                typeSpecifier.startsWith("Medium") ||
                typeSpecifier.startsWith("NonSI") ||
                typeSpecifier.startsWith("SDF") ||
                typeSpecifier.startsWith("LibEAS") ||
                typeSpecifier.startsWith("AixLib") ||
                typeSpecifier.startsWith("Buildings") ||
                typeSpecifier.startsWith("Modelica")) {
            return typeSpecifier;
        } else if (typeSpecifier.startsWith("SI")) {
            return typeSpecifier.replaceFirst("SI.", "Modelica.SIunits.");
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
                    if (vorpfad.equals("")) {
                        //wenn während der Suche nach oben die Wurzel erreicht wird, kommt dieser leere String zurück
                        //FIXME unhandled import --> interim only marked with keyword "IMPORT"
                        newTypeSpecifier = "IMPORT." + typeSpecifier;
                    } else {
                        //Normalfall: wenn es beim Suchen nach oben gefunden wird
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

    public String printHeader() {
        return("Container: " + container + "\tType:" + type + "\tName: " + name);
    }

    public String printHeaderEnhanced() {
        return(printHeader()  + "\tParent: " + printParent());
    }

    public String printParent() {
        String alle_parent = "(";
        for(String el : parents) {
            alle_parent = alle_parent + el +",";
        }
        return alle_parent +")";
    }

    public String getContainer() {
        return container;
    }

    public String getName() {
        return name;
    }
}
