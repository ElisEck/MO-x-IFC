package model;

import java.util.LinkedList;
import java.util.List;

public class MPackage {
    String fullName;
    String container;
    String name;
    List<String> subpackages = new LinkedList<>();
    List<String> containedModels = new LinkedList<>();

    public MPackage(String name, String container) {
        this.name = name;
        this.container = container;
        this.fullName = container + "." + name;
    }

    public MPackage(String fullName) {
        this.fullName = fullName;
    }
}
