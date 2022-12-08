package de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser;

import de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser.antlr.modelicaBaseListener;
import de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser.antlr.modelicaParser;
import de.elisabetheckstaedt.moxifc.modelicatranscriptor.model.MClass;
import de.elisabetheckstaedt.moxifc.modelicatranscriptor.model.MConnection;
import de.elisabetheckstaedt.moxifc.modelicatranscriptor.model.ModelicaObject;
import de.elisabetheckstaedt.moxifc.modelicatranscriptor.model.MParameterComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class ModelicaComponentListener extends modelicaBaseListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MClass.class);

    Set<MClass> mks = new HashSet<>();
    Stack<MClass> mkstack = new Stack<>();
    /**
     * gecleart für jede neue ModelicaClass
     */
    ModelicaObject curObject;
    private MConnection curConnection;
    String container = "";
    /**
     * flow/stream input/output parameter
     */
    String owlPrefix = "";
    private boolean insideComment = false;
    private boolean insideAnnotation = false;
    private boolean insideExtendsClause = false;
    private boolean insideConstrainingClause = false;
    private boolean insideClassModification = false;
    private boolean insideComponentDeclaration = false;
    private boolean insideElementRedeclaration = false;
    private int modificationLevel = 0;


    public  ModelicaComponentListener(String owlPrefix) {
        this.owlPrefix = owlPrefix;
    }

    @Override
    public void enterComponent_clause(modelicaParser.Component_clauseContext ctx) {
        curObject = new ModelicaObject(owlPrefix);
        curObject.setTypePrefix(ctx.type_prefix().getText());
        curObject.setTypeSpecifier(ctx.type_specifier().getText());
    }

    @Override
    public void exitComponent_clause(modelicaParser.Component_clauseContext ctx) {

        curObject = null;
    }

    @Override
    public void enterComponent_declaration(modelicaParser.Component_declarationContext ctx) {
        insideComponentDeclaration = true;
    }

    @Override
    public void exitComponent_declaration(modelicaParser.Component_declarationContext ctx) {
        if (curObject.getTypePrefix().equals("parameter")) {
            mkstack.peek().appendParameter(new MParameterComponent(curObject.getTypeSpecifier(), curObject.getName(), curObject.getModification(), curObject.getStringComment(), curObject.getAnnotation()));
        } else {
            curObject.setName(ctx.declaration().IDENT().getText());
            mkstack.peek().appendComponent(curObject);
        }
        insideComponentDeclaration = false;
    }

    @Override
    public void exitComponent_declaration1(modelicaParser.Component_declaration1Context ctx) {
        insideComponentDeclaration = false;
    }


    @Override
    public void enterString_comment(modelicaParser.String_commentContext ctx) {
        if (curObject != null && !ctx.getText().equals("")) {
            curObject.setStringComment(ctx.getText());
        }
    }

    @Override
    public void enterAnnotation(modelicaParser.AnnotationContext ctx) {
        insideAnnotation = true;
        if (curObject != null) {
            curObject.setAnnotation(ctx.getText());
        } else {
            mkstack.peek().setAnnotation(ctx.getText());
        }

    }

    @Override
    public void enterElement(modelicaParser.ElementContext ctx) {
        ctx.getText();
    }

    @Override
    public void enterEquation_section(modelicaParser.Equation_sectionContext ctx) {
        ctx.getText();
    }

    @Override
    public void enterDeclaration(modelicaParser.DeclarationContext ctx) {
        if (!insideElementRedeclaration) { //TODO element redaclaration ausprogrammieren
            curObject.setName(ctx.IDENT().getText());
        }
    }

    @Override
    public void enterElement_redeclaration(modelicaParser.Element_redeclarationContext ctx) {
        insideElementRedeclaration=true;
    }
    @Override
    public void exitElement_redeclaration(modelicaParser.Element_redeclarationContext ctx) {
        insideElementRedeclaration=false;
    }


    @Override
    public void enterModification(modelicaParser.ModificationContext ctx) {
        if (!insideComment) {
            modificationLevel++;
        }
        String mod;
        if (ctx.getText().contains("redeclarepackage")) {
            mod = ctx.getText().replace("redeclarepackage", "redeclare package ");
        } else {
            mod = ctx.getText();
        }
        if (!insideComment && curObject != null && modificationLevel==1) {
            curObject.setModification(mod); //FIXME if keyword "final" is present, sometimes the whitespace seems to be missing
            // modification besteht aus optionaler class_modification, welche in Klammern geschrieben wird und einer Expression, welche nach dem = steht
            // die modification an sich ist auch optional d.h. es kann auch keins von beiden da sein
        }
        mod = "";
    }

    @Override
    public void exitModification(modelicaParser.ModificationContext ctx) {
        if (!insideComment) {
            modificationLevel--;
        }
    }

    @Override
    public void enterComment(modelicaParser.CommentContext ctx) {
        insideComment = true;
        if (curConnection != null && !ctx.getText().equals("")) {
            curConnection.setAnnotation(ctx.getText());
        }
    }

    @Override
    public void exitComment(modelicaParser.CommentContext ctx) {
        insideComment = false;
    }

    @Override
    public void enterClass_modification(modelicaParser.Class_modificationContext ctx) {
        insideClassModification = true;
    }

    @Override
    public void exitClass_modification(modelicaParser.Class_modificationContext ctx) {
        insideClassModification = false;
    }

    @Override
    public void exitAnnotation(modelicaParser.AnnotationContext ctx) {
        insideAnnotation = false;
    }

    @Override
    public void enterConstraining_clause(modelicaParser.Constraining_clauseContext ctx) {
        insideConstrainingClause = true;
    }

    @Override
    public void exitConstraining_clause(modelicaParser.Constraining_clauseContext ctx) {
        insideConstrainingClause = false;
    }



    @Override
    public void enterStored_definition(modelicaParser.Stored_definitionContext ctx) {
        try {
            container = ctx.name(0).getText();
        } catch (NullPointerException nullPointerException) {
            container = "";
        }
    }

    @Override
    public void enterClass_definition(modelicaParser.Class_definitionContext ctx) {
//        String class_prefixes = ctx.class_prefixes().toString();
//        ctx.class_specifier(
        mkstack.push(new MClass(owlPrefix));
        mkstack.peek().setType(ctx.class_prefixes().getText());
        mkstack.peek().setContainer(container);
    }

    @Override
    public void exitClass_definition(modelicaParser.Class_definitionContext ctx) {
        MClass mkAkt = mkstack.pop();
        mkAkt.sortParent();
        try {
            if (!mkstack.empty()) {//wenn noch etwas drunter liegt
                StringBuilder sb = new StringBuilder();
                mkstack.stream().forEach(cont -> sb.append(cont.getName() + "."));
//                mkAkt.setContainer(mkstack.peek().getContainer() + "." + mkstack.peek().getName());

                mkAkt.setContainer(mkstack.peek().getContainer() + "." + removeTrailingPoint(sb.toString()));
            }
            mks.add(mkAkt); //neu 15.2.22 um auch Klassen zu behandeln, die kein eigenes File haben
            mkstack.peek().appendChildClass(mkAkt);
        } catch (EmptyStackException ee) {
            mks.add(mkAkt); //notwendig für die Root-Klasse eines Files
        }
    }
    String removeTrailingPoint(String input) {
        if (input.endsWith(".")) {
            return input.substring(0, input.length()-1);
        } else
            return input;
    }
    @Override
    public void enterConnect_clause(modelicaParser.Connect_clauseContext ctx) {
        curConnection = new MConnection();
        curConnection.setConnectees(ctx.component_reference(0).getText(), ctx.component_reference(1).getText());
        mkstack.peek().addConnection(curConnection);
    }

/*    @Override
    public void exitEquation(modelicaParser.EquationContext ctx) {
        mkstack.peek().addConnection(curConnection);
        curConnection = null;
    }*/

    @Override
    public void enterLong_class_specifier(modelicaParser.Long_class_specifierContext ctx) {
        mkstack.peek().setName(ctx.IDENT().get(0).getText());
        if (ctx.string_comment() != null) {
            mkstack.peek().setDescription(ctx.string_comment().getText());
        }
        LOGGER.trace("Using long_class_specifier for " + ctx.IDENT().get(0).getText());
    }



    @Override
    public void enterShort_class_specifier(modelicaParser.Short_class_specifierContext ctx) {
        if (insideElementRedeclaration) {
           // curObject.setName(ctx.IDENT().getText());
        } else {
            mkstack.peek().setName(ctx.IDENT().getText());
            LOGGER.warn("Using short_class_specifier for " + ctx.IDENT().getText());
        }

    }

    @Override
    public void enterDer_class_specifier(modelicaParser.Der_class_specifierContext ctx) {
        mkstack.peek().setName(ctx.IDENT().get(0).getText());
        System.out.println(ctx.IDENT().get(0).getText());
        LOGGER.warn("Using der_class_specifier for " + ctx.IDENT().get(0).getText());
    }

    @Override
    public void enterExtends_clause(modelicaParser.Extends_clauseContext ctx) {
        insideExtendsClause = true;
        mkstack.peek().appendParent(ctx.name().getText());
    }

    @Override
    public void exitExtends_clause(modelicaParser.Extends_clauseContext ctx) {
        insideExtendsClause = false;
    }
}

