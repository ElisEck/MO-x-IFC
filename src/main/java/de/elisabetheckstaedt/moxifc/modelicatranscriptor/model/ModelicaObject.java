package de.elisabetheckstaedt.moxifc.modelicatranscriptor.model;

public class ModelicaObject {
    String owlPrefix;
    String typePrefix;
    String typeSpecifier;
    MClass mClass;
    String name = "";
    String modification;
    String stringComment;
    String annotation;


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
