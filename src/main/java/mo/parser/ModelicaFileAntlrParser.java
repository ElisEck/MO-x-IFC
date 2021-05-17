package mo.parser;

import mo.parser.antlr.modelicaLexer;
import mo.parser.antlr.modelicaParser;
import model.ModelicaClass;
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

    public Set<ModelicaClass> parseFile(String content) {
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
