package rdf.helper;

public class ResultLine {
    public String namespace;
    public String name;
    public Integer count;

    public ResultLine(String namespace, String name, Integer count) {
        this.namespace = namespace;
        this.name = name;
        this.count = count;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    public Integer getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "ResultLine{" +
                "namespace='" + namespace + '\'' +
                ", name='" + name + '\'' +
                ", count=" + count +
                '}';
    }
}
