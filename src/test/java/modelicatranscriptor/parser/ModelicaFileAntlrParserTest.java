package modelicatranscriptor.parser;

import de.elisabetheckstaedt.moxifc.modelicatranscriptor.model.MClass;
import de.elisabetheckstaedt.moxifc.modelicatranscriptor.model.ModelicaFile;
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
     * liest ein Modelica-File (*.mo) ein in eine interne ModelicaLibrary und schreibt diese anschließend als ttl wieder raus
     * läuft: 10.1.22 18:33
     */
    public void readModelicaLibraryFromMoAndSerialize(){
//        ModelicaLibrary ml = ReadMo("ibpsa", "ibpsa", Path.of("c:\\_DATEN\\Modelica\\_Libraries_extern\\modelica-ibpsa\\IBPSA\\"));
//        ModelicaLibrary ml = ReadMo("msl", "msl", Path.of("C:\\Program Files\\Dymola 2021\\Modelica\\Library\\Modelica 3.2.3\\"));
//        ModelicaLibrary ml = ReadMo("bs", "bs", Path.of("c:\\_DATEN\\Modelica\\_Libraries_extern\\BuildingSystems\\BuildingSystems\\"));
//        ModelicaLibrary ml = ReadMo("ideas", "ideas", Path.of("c:\\_DATEN\\Modelica\\_Libraries_extern\\IDEAS\\IDEAS\\"));
//        ModelicaLibrary ml = ReadMo("lvbmin", "lvbmin", Path.of(":\\TMP\\LVBmin\\"));
//        ModelicaLibrary ml = ReadMo("RLT4", "eas", Path.of("c:\\_DATEN\\Prototyp\\2103\\RDFModelle\\minimal\\"));
//        ModelicaLibrary ml = ReadMo("coo", "eas", Path.of("c:\\_DATEN\\2021-04-22_FullPaper_LDAC\\"));
//        ModelicaLibrary ml = ReadMo("rlt4", "rlt4", Path.of("c:\\_DATEN\\Modelica\\_Modelle\\EASNeubau\\"));
        ModelicaLibrary ml = new ModelicaLibrary("mbl", "mbl", Path.of("c:\\_DATEN\\Modelica\\_Libraries_extern\\Buildings 8.0.0\\"));
//        ModelicaLibrary ml = new ModelicaLibrary("aix", "aix", Path.of("c:\\_DATEN\\Modelica\\_Libraries_extern\\AixLib 1.0.0\\"));
//        ModelicaLibrary ml = new ModelicaLibrary("klterz", "eas", Path.of("c:\\_DATEN\\Modelica\\_Modelle\\FMI4BIM\\Demonstratoren\\NeubauEAS\\Erzeugungsanlagen\\KLT\\F.mo"));
//        ml.generatePackageHierarchyFromPackageList();

        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd_HHmm");
//        ml.serializeAsTTL("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\" + ml.getName()+"_"+sdf3.format(new Timestamp(System.currentTimeMillis()))+ "_full.ttl", "AixLib","full");
        ml.serializeAsTTL("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\" + ml.getName()+"_"+sdf3.format(new Timestamp(System.currentTimeMillis()))+ "_full.ttl", "Buildings","full");
//        ml.serializeAsTTL("C:\\_DATEN\\WORKSPACES\\IntelliJ\\mo-x-ifc\\src\\test\\java\\output\\" + ml.getName()+"_"+sdf3.format(new Timestamp(System.currentTimeMillis()))+ "_short.ttl", "short");
//        ml.serializeAsMo("C:/TMP/ModelicaTestSerialisation/");
    }








}
