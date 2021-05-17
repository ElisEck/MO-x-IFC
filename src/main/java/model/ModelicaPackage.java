package model;

import java.util.LinkedList;
import java.util.List;

public class ModelicaPackage {
    String fullName;
    String container;
    String name;
    List<String> subpackages = new LinkedList<>();
    List<String> containedModels = new LinkedList<>();

    public ModelicaPackage(String name, String container) {
        this.name = name;
        this.container = container;
        this.fullName = container + "." + name;
    }

    public ModelicaPackage(String fullName) {
        this.fullName = fullName;
    }
}
