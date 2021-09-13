import model.MClass;
import model.ModelicaFile;
import model.ModelicaLibrary;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RIOT;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.tdb.sys.TDBInternal;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;
import mo.parser.ModelicaFileAntlrParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import java.util.UUID;

import static java.nio.file.Files.readString;

public class ModelicaFileAntlrParserTest {

    @Test
    public void testAntlrParser() throws IOException {
        ModelicaFileAntlrParser parser = new ModelicaFileAntlrParser("test");
//        String content = Files.readString(Paths.get("c:\\TMP\\AixLib\\Fluid\\Chillers\\Examples\\Carnot_TEva.mo"));
        String content = Files.readString(Paths.get("c:\\_DATEN\\Modelica\\_Modelle\\FMI4BIM\\Demonstratoren\\NeubauEAS\\Anlagen\\KLT\\F.mo"));
        Set<MClass> result = parser.parseFile(content);
        result.size();
    }

    @Test
    public void ReadMoAndSerialize(){
//        ModelicaLibrary ml = ReadMo("ibpsa", "ibpsa", Path.of("c:\\_DATEN\\Modelica\\_Libraries_extern\\modelica-ibpsa\\IBPSA\\"));
//        ModelicaLibrary ml = ReadMo("msl", "msl", Path.of("C:\\Program Files\\Dymola 2021\\Modelica\\Library\\Modelica 3.2.3\\"));
//        ModelicaLibrary ml = ReadMo("mbl", "mbl", Path.of("c:\\_DATEN\\Modelica\\_Libraries_extern\\modelica-buildings\\Buildings\\"));
//        ModelicaLibrary ml = ReadMo("aix", "aix", Path.of("c:\\_DATEN\\Modelica\\_Libraries_extern\\AixLib\\AixLib\\"));
//        ModelicaLibrary ml = ReadMo("bs", "bs", Path.of("c:\\_DATEN\\Modelica\\_Libraries_extern\\BuildingSystems\\BuildingSystems\\"));
//        ModelicaLibrary ml = ReadMo("ideas", "ideas", Path.of("c:\\_DATEN\\Modelica\\_Libraries_extern\\IDEAS\\IDEAS\\"));
//        ModelicaLibrary ml = ReadMo("lvbmin", "lvbmin", Path.of(":\\TMP\\LVBmin\\"));
//        ModelicaLibrary ml = ReadMo("RLT4", "eas", Path.of("c:\\_DATEN\\Prototyp\\2103\\RDFModelle\\minimal\\"));
//        ModelicaLibrary ml = ReadMo("coo", "eas", Path.of("c:\\_DATEN\\2021-04-22_FullPaper_LDAC\\"));
//        ModelicaLibrary ml = ReadMo("rlt4", "rlt4", Path.of("c:\\_DATEN\\Modelica\\_Modelle\\EASNeubau\\"));
        ModelicaLibrary ml = ReadMo("klterz", "eas", Path.of("c:\\_DATEN\\Modelica\\_Modelle\\FMI4BIM\\Demonstratoren\\NeubauEAS\\Anlagen\\KLT\\F.mo"));
//        ml.generatePackageHierarchyFromPackageList();

        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd_HHmm");
        ml.serializeAsTTL(ml.getName()+"_"+sdf3.format(new Timestamp(System.currentTimeMillis()))+ ".ttl");
//        ml.serializeAsMo("C:/TMP/ModelicaTestSerialisation/");
    }

    public ModelicaLibrary ReadMo(String name, String prefix, Path dir) {
        if (dir == null) return null;
        ModelicaLibrary ml = new ModelicaLibrary(dir.toString(), name, prefix);
        try {
            Files.walk(dir)
                    .filter(file -> file.toFile().isFile())
                    .filter(file -> file.toFile().getAbsolutePath().toLowerCase().endsWith(".mo"))
                    .forEach(file -> {
                                System.out.print(file + "\t");
                                ModelicaFile mf1 = new ModelicaFile(file);
                                ModelicaFileAntlrParser parser = new ModelicaFileAntlrParser(prefix);
                                String content = null;
                                try {
                                    content = readString(file);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
//                                Set<ModelicaKlasse> result = parser.parseFile(content);
                                mf1.mks = parser.parseFile(content);
                                ml.addFile(mf1);
                            }
                    );
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return ml;
    }






}
