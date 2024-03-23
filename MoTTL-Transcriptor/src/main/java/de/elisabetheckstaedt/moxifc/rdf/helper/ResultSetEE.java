package de.elisabetheckstaedt.moxifc.rdf.helper;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * enthält die Ergebnisse einer Query in roh und in verschiedenen aufbereiten Formen
 */
public class ResultSetEE {

    public List<ResultLine> resultLines;
    Map<String, List<ResultLine>> resultLinesGroupedByNamespace = new HashMap<>();

    Map<String, Integer> namespace2CountMap = new HashMap<>();
    Map<String, Long> namespace2DistinctCountMap = new HashMap<>();
    Map<String, Integer> namespace2CountMapSorted = new LinkedHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetEE.class);

    public ResultSetEE(ResultSet results) {
        resultLines = new ArrayList<>();
        while ( results.hasNext() ) {
            QuerySolution soln = results.nextSolution();
            ResultLine qsc = new ResultLine(soln.get("klasse").asResource().getNameSpace(), soln.get("klasse").asResource().getLocalName(), soln.get("count").asLiteral().getInt());
            resultLines.add(qsc);
        }
        generateNamespaceCount();
        groupResultLinesByNamespace();
    }

    void groupResultLinesByNamespace() {
        resultLinesGroupedByNamespace = resultLines
                .stream()
                .collect(Collectors.groupingBy(ResultLine::getNamespace));
    }

    void generateNamespaceCount() {
        namespace2CountMap = resultLines
                .stream()
                .collect(Collectors.groupingBy(ResultLine::getNamespace, Collectors.summingInt(ResultLine::getCount)));

        namespace2DistinctCountMap = resultLines
                .stream()
                .collect(Collectors.groupingBy(ResultLine::getNamespace, Collectors.counting()));

        //        Map sorted = countBy.entrySet().stream()
        namespace2CountMapSorted = namespace2CountMap.entrySet().stream()
                //                .sorted(Map.Entry.comparingByKey())
                .sorted(Map.Entry.comparingByValue()) //TODO Reihenfolge umkehren
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        ;
//        namespaceCountSorted.get("https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#");
    }

    public void printNamespaceCountSorted() {
        namespace2CountMapSorted.forEach((key, value) -> System.out.println(value + " \t" + key));
    }

    public void printResultLinesGroupedByNamespace () {
        resultLinesGroupedByNamespace
                .forEach((key, value) -> {
                    print(value);
                });
//        resultLinesGroupedByNamespace.get((ke)
    }

    private void print(List<ResultLine> value) {
        value.forEach((rl) -> System.out.println(rl));
    }

    Integer sumTotal() {
        Integer totalcount = resultLines.stream().mapToInt(ResultLine::getCount).sum();
        LOGGER.info(String.valueOf(totalcount));
        return totalcount;
    }



    public List<ResultLine> getResultLines() {
        return resultLines;
    }

    public Map<String, List<ResultLine>> getResultLinesGroupedByNamespace() {
        return resultLinesGroupedByNamespace;
    }

    public Map<String, Integer> getNamespace2CountMap() {
        return namespace2CountMap;
    }

    public Map<String, Integer> getNamespace2CountMapSorted() {
        return namespace2CountMapSorted;
    }
}
