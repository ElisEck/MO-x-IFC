package model;

public class ModelicaConnection {
    private String leftComponent;
    private String leftPort;
    private String rightComponent;
    private String rightPort;
    private String annotation;

    public ModelicaConnection() {
    }

    public void setConnectees(String left, String right) {
        if (left.split("\\.").length == 1) {
            this.leftPort = left;
        } else {
            this.leftComponent=left.split("\\.")[0];
            this.leftPort=left.split("\\.")[1];
        }
        if (right.split("\\.").length == 1) {
            this.rightPort = right;
        } else {
            this.rightComponent=right.split("\\.")[0];
            this.rightPort=right.split("\\.")[1];
        }
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getLeftComponent() {
        return leftComponent;
    }

    public String getRightComponent() {
        return rightComponent;
    }

    public String getLeftPort() {
        return leftPort;
    }

    public String getRightPort() {
        return rightPort;
    }

    public String getAnnotation() {
        if (annotation == null) {
            return "";
        } else {
            return annotation;
        }
    }
}
