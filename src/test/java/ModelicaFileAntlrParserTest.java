import model.ModelicaClass;
import org.junit.Test;
import mo.parser.ModelicaFileAntlrParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ModelicaFileAntlrParserTest {
    private ModelicaFileAntlrParser parser = new ModelicaFileAntlrParser("test");

    @Test
    public void testAntlrParser() throws IOException {
        String content = Files.readString(Paths.get("c:\\TMP\\AixLib\\Fluid\\Chillers\\Examples\\Carnot_TEva.mo"));
        Set<ModelicaClass> result = parser.parseFile(content);
//        assertEquals(3, result.size());
    }
}
