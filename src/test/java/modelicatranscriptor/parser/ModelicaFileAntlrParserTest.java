package modelicatranscriptor.parser;

import de.elisabetheckstaedt.moxifc.modelicatranscriptor.model.MClass;
import de.elisabetheckstaedt.moxifc.modelicatranscriptor.model.ModelicaLibrary;
import org.junit.Test;
import de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser.ModelicaFileAntlrParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Set;

import static java.nio.file.Files.readString;

public class ModelicaFileAntlrParserTest {

    @Test
    /**
     * liest ein Modelica-File (*.mo) ein und erzeugt daraus ein Set<MClass>
     *     läuft: 10.1.22 18:33
     */
    public void readModelicaClassesFromMo() throws IOException {
        ModelicaFileAntlrParser parser = new ModelicaFileAntlrParser("test");
        String content = Files.readString(Paths.get("c:\\_DATEN\\Modelica\\_Modelle\\FMI4BIM\\Demonstratoren\\NeubauEAS\\Erzeugungsanlagen\\KLT\\F.mo"));
        Set<MClass> result = parser.parseFile(content);
        result.size();
    }

    @Test
    /**
     * liest die Inhalte eines Ordners in eine interne ModelicaLibrary und schreibt diese anschließend als ttl wieder raus
     */
    public void convertModelicaLibraryToGraph(){
        convertModelicaLibraryToGraph("ex", "LBDCG_example", "c:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\resources\\C_HeatPumpPlant\\LBDCG_example\\", "knowledge graph representing the Modelica LBDCG_example package","v1.0.1-1.0.0");
        convertModelicaLibraryToGraph("libeas", "LibEAS", "c:\\_DATEN\\Modelica\\_Libraries_intern\\LibEAS\\", "Modelica LibEAS (v3.3.0) ontology", "v3.3.0-1.0.1");
//        convertModelicaLibraryToGraph("aix", "AixLib", "c:\\_DATEN\\Modelica\\_Libraries_extern\\AixLib 1.0.0\\", "Modelica AixLib (v1.0.0) ontology", "v1.0.0-1.0.0");
//        convertModelicaLibraryToGraph("mbl", "Buildings", "c:\\_DATEN\\Modelica\\_Libraries_extern\\Buildings 8.0.0\\", "Modelica Buildings Library (v8.0.0) ontology","v8.0.0-1.0.0");
//        convertModelicaLibraryToGraph("bs", "BuildingSystems", "c:\\_DATEN\\Modelica\\_Libraries_extern\\BuildingSystems 2.0.0-beta\\", "Modelica BuildingSystems Library (v2.0.0) ontology","v2.0.0-1.0.0");
//        convertModelicaLibraryToGraph("msl", "Modelica", "C:\\Program Files\\Dymola 2021\\Modelica\\Library\\Modelica 3.2.3\\", "Modelica Standard Library (v3.2.3) ontology", "v3.2.3-1.0.0");
    }

    /**
     *
     * @param prefix z.B. aix
     * @param longName z.B. AixLib
     * @param sourcePath Ordner der das Wurzel package.mo enthält z.B. C:\Program Files\Dymola 2021\Modelica\Library\Modelica 3.2.3\
     */
    public void convertModelicaLibraryToGraph(String prefix, String longName, String sourcePath, String ontologyTitle, String ontologyVersion) {
        ModelicaLibrary ml = new ModelicaLibrary(prefix, prefix, Path.of(sourcePath));
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd_HHmm");
//        ml.serializeAsTTL("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\" + prefix+"_"+sdf3.format(new Timestamp(System.currentTimeMillis()))+ "short.ttl", prefix, longName,"short");
        ml.serializeAsTTL("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\" + prefix+"_"+sdf3.format(new Timestamp(System.currentTimeMillis()))+ "_fullclean.ttl", prefix, longName,"fullclean", ontologyTitle, ontologyVersion);
//        ml.serializeAsTTL("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\" + prefix+"_"+sdf3.format(new Timestamp(System.currentTimeMillis()))+ "_full.ttl", longName,"full");//lässt sich in Protege nicht öffnen wegen Syntaxproblemen
    }








}
