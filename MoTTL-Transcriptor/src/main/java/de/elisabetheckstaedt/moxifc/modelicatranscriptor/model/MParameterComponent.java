package de.elisabetheckstaedt.moxifc.modelicatranscriptor.model;

public class MParameterComponent {
    private String typeSpecifier ;
    private String name;
    private String modification;
    private String stringComment;
    private String annotation;

    public MParameterComponent(String typeSpecifier, String name, String modification, String stringComment, String annotation) {
        this.typeSpecifier = typeSpecifier;
        this.name = name;
        this.modification = modification;
        this.stringComment = stringComment;
        this.annotation = annotation;
    }

    public String getTypeSpecifier() {
        return typeSpecifier;
    }

    public void setTypeSpecifier(String typeSpecifier) {
        this.typeSpecifier = typeSpecifier;
    }

    public String getName() {
        return name;
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
