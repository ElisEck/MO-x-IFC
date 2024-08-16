package de.elisabetheckstaedt.moxifc.modelicatranscriptor.model;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser.Helper.cleanStringFromLineBreaks;
import static de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser.Helper.maskSpecialCharacter;

public class ModelicaObject {
    public static final String NEWLINE = System.lineSeparator();
    String owlPrefix;
    String typePrefix;
    String typeSpecifier;
    MClass mClass;
    String name = "";
    String modification;
    String stringComment;
    String annotation;

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelicaObject.class);

    public ModelicaObject(String owlPrefix) {
        this.owlPrefix = owlPrefix;
    }


    public void setStringComment(String stringComment) {
        this.stringComment = stringComment;
    }


    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public void setModelicaClass(MClass mClass) {
        this.mClass = mClass;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTypePrefix(String typePrefix) {
        this.typePrefix = typePrefix;
    }

    public void setModification(String modification) {
        this.modification = modification;
    }

    public void setTypeSpecifier(String typeSpecifier) {
        this.typeSpecifier = typeSpecifier;
        this.mClass = new MClass(owlPrefix);
        this.mClass.setName(typeSpecifier);
    }

    public MClass getModelicaClass() {
        return mClass;
    }

    public String getName() {
        return name;
    }

    public String getTypePrefix() {
        return typePrefix;
    }


    public String getTypeSpecifier() {
        return typeSpecifier;
    }


    public String getModification() {
        if (modification == null) {
            return "";
        } else {
            return modification;
        }
    }


    /**
     * split modification of an object by comma (but handling brackets)
     *
     * @return String[]
     */
    String[] splitModifications(String classContainingTheObject) {
        String modifications = this.getModification().substring(1, this.getModification().length() - 1);
        String modificationsSimplified = modifications;
        String replacement = "_beforeBEFORE_";
        String[] mods;
        try {
            if (modifications.contains("(")) {
                replacement = modifications.substring(modifications.indexOf("("), modifications.lastIndexOf(")") + 1);
            } else if (modifications.contains("{")) {
                replacement = modifications.substring(modifications.indexOf("{"), modifications.lastIndexOf("}") + 1);
            } else if (modifications.contains("[")) {
                replacement = modifications.substring(modifications.indexOf("["), modifications.lastIndexOf("]") + 1);
            }
        } catch (Exception e) {
            LOGGER.warn("splitModififications went wrong at " + classContainingTheObject + "." + this.name + ": " + modifications); //TODO perform splitModifications earlier (when parsing the file), should be easier than handling all brackets/special cases here
        }
        //if no brackets are present the String _beforeBEFORE_ will be replaced, and this will - most likely - never be present
        modificationsSimplified = modifications.replace(replacement, "_REP_");
        String finalReplacement = replacement;
        mods = Arrays.stream(modificationsSimplified.split(","))
                .map(s -> s.replace("_REP_", finalReplacement))
                .collect(Collectors.toList())
                .toArray(new String[]{});
        return mods;
    }

    String writeObjectModificationsToTTL(String zf, String objectName, String classContainingTheObject) {
        if (this.getModification().equals("")) {
            //no modification present
        } else if (this.getModification().stripLeading().stripTrailing().startsWith("=")) {
            //the component is a variable, its definition is not transcribed to KG  //complex modification with inner bracket or square bracket - temporary: leave as is
            //                    (mo.getModification().substring(1, mo.getModification().length()-1).contains("(")) ||
            //                    (mo.getModification().substring(1, mo.getModification().length()-1).contains("{")) ||
            //                    (mo.getModification().substring(1, mo.getModification().length()-1).contains("["))) {
            //                zf += owlPrefix +":"+ container + "." + name + "." + mo.name   + " moont:modification \"" + maskSpecialCharacter(cleanStringFromLineBreaks(mo.getModification())) + "\"^^xsd:string." + NEWLINE;
        } else {
            //simple modification - just some values changed
            //                String[] mods = mo.getModification().substring(1, mo.getModification().length()-1).split(",");
            String[] mods = this.splitModifications(classContainingTheObject);
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("graal.js");
            for (String mod:mods) {
                if (mod.split("=").length!=2) {//complex expression is copied to KG //TODO check whether this should be omitted in the KG
                    zf += objectName   + " moont:modification \"" + maskSpecialCharacter(cleanStringFromLineBreaks(mod)) + "\"^^xsd:string." + NEWLINE;
                } else {
                    String comp = mod.split("=")[0];
                    String value = mod.split("=")[1];
                    if (comp.contains("(") || value.contains("}")|| value.contains("\"")) {
                        LOGGER.warn("modification not transcribed to KG at " + classContainingTheObject + "." + name + ": " + mod); //TODO handle complex modifications - should be solved if modifications are separated when parsing the *.mo-file
                        continue;
                    }
                    try { //simple case: modification with a number assigned to a variable
                        double doubleValue = Double.parseDouble(value);
                        zf += objectName + "." + comp + " moont:modification \"" + doubleValue + "\"^^xsd:Real;" + NEWLINE;
                        zf +=  "\t moont:identifier \"" + comp + "\"." + NEWLINE;
                        zf += objectName + " moont:hasPart " + objectName + "." + comp + "." + NEWLINE;
                    } catch(NumberFormatException e) {
                        if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("true")) {
                            zf += objectName + "." + comp + " moont:modification \"" + value + "\"^^xsd:boolean;" + NEWLINE;
                            zf +=  "\t moont:identifier \"" + comp + "\"." + NEWLINE;
                            zf += objectName + " moont:hasPart " + objectName + "." + comp +  "." + NEWLINE;
                        } else if (comp.startsWith("redeclare")) {
                            zf += objectName   + " moont:modification \"" + maskSpecialCharacter(cleanStringFromLineBreaks(mod)) + "\"^^xsd:string." + NEWLINE;
                            //zf +=  "\t moont:identifier \"" + comp + "\"." + NEWLINE; //auskommentiert 1.11.23
                            //                                zf += objectName + " moont:hasPart " + objectName + "." + comp +  "." + NEWLINE;
                        } else {//equation assigned to a variable
                            try {//if possible: evaluate
                                Object result = engine.eval(value);
                                zf += objectName + "." + comp + " moont:modification \"" + result.toString() + "\"^^xsd:Real;" + NEWLINE;
                                zf +=  "\t moont:identifier \"" + comp + "\"." + NEWLINE;
                                zf += objectName + " moont:hasPart " + objectName + "." + comp +  "." + NEWLINE;
                            } catch (ScriptException ex) {
                                // simple expression (equation assigned to parameter) is handled in knowledge graph //TODO check whether this should be omitted in the KG
                                zf += objectName + "." + comp + " moont:modification \"" + value + "\"^^xsd:String;" + NEWLINE;
                                zf +=  "\t moont:identifier \"" + comp + "\"." + NEWLINE;
                                zf += objectName + " moont:hasPart " + objectName + "." + comp +  "." + NEWLINE;
                            }
                        }
                    }
                }
            }
        }
        return zf;
    }

    Optional<String> extractPlacementFromAnnotation() {
        //annotation(Placement(transformation(extent={{-10,-10},{10,10}},rotation=0,origin={670,-270})))
        int start = this.annotation.indexOf("origin=");
        if (start != -1) {
            int end = this.annotation.indexOf("}", start);
            String zf = "\"" + this.annotation.substring(start+7, end+1) + "\"^^xsd:string";
            return Optional.of(zf);
            //coord_string = "\"{" + str(res[3]) + ", " + str(res[4]) + "}\""
        }
        start = this.annotation.indexOf("extent={{");
        if (start != -1) {
            int end = this.annotation.indexOf("}}", start);
            String zf = this.annotation.substring(start+9, end);
            int komma_1 = zf.indexOf(",", 0);
            int trennung_1 = zf.indexOf("},{", komma_1);
            int komma_2 = zf.indexOf(",", trennung_1+3);
            String x1 = zf.substring(0, komma_1);
            String y1 = zf.substring(komma_1+1, trennung_1);
            String x2 = zf.substring(trennung_1+3, komma_2);
            String y2 = zf.substring(komma_2+1, zf.length());
            Double x = (double) Math.round((Double.parseDouble(x1) + Double.parseDouble(x2)) / 2);
            Double y = (double) Math.round((Double.parseDouble(y1) + Double.parseDouble(y2)) / 2);
            String res = "\"{" + String.format("%.0f",x) + ","+ String.format("%.0f",y) + "}\"^^xsd:string";
            return Optional.of(res);
            }
        return Optional.empty();
    }

    public String getStringComment() {
        if (stringComment == null) {
            return "";
        } else {
            return stringComment;
        }
    }

    public String getAnnotation() {
        if (annotation == null) {
            return "";
        } else {
            return annotation;
        }
    }

}
