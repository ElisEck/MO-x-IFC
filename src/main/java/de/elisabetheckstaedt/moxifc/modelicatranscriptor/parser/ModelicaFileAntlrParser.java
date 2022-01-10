package de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser;

import de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser.antlr.modelicaLexer;
import de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser.antlr.modelicaParser;
import de.elisabetheckstaedt.moxifc.modelicatranscriptor.model.MClass;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.Set;

public class ModelicaFileAntlrParser {
    String prefix;

    public ModelicaFileAntlrParser(String prefix) {
        this.prefix = prefix;
    }

    /**
     * wandelt einen String in ein Set von Modelica-Klassen um
     * @param content (String)
     * @return Set<MClass>
     */
    public Set<MClass> parseFile(String content) {
        modelicaLexer lexer = new modelicaLexer(CharStreams.fromString(content));

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        modelicaParser parser = new modelicaParser(tokens);
        ParseTree tree = parser.stored_definition();

        ParseTreeWalker walker = new ParseTreeWalker();
        ModelicaComponentListener listener = new ModelicaComponentListener(prefix);
        walker.walk(listener, tree);

        return listener.mks;
    }
}
