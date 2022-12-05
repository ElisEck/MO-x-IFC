package de.elisabetheckstaedt.moxifc.modelicatranscriptor.model;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ModelicaObject {
    String owlPrefix;
    String typePrefix;
    String typeSpecifier;
    MClass mClass;
    String name = "";
    String modification;
    String stringComment;
    String annotation;

    private static final Logger LOGGER = LoggerFactory.getLogger(MClass.class);

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
    String[] splitModifications() {
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
            LOGGER.warn("splitModififications went wrong "); //TODO perform splitModifications earlier (when parsing the file), should be easier than handling all brackets/special cases here
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
