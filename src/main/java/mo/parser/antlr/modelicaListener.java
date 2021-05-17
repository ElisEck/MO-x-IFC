// Generated from modelica.g4 by ANTLR 4.8
package mo.parser.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link modelicaParser}.
 */
public interface modelicaListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link modelicaParser#stored_definition}.
	 * @param ctx the parse tree
	 */
	void enterStored_definition(modelicaParser.Stored_definitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#stored_definition}.
	 * @param ctx the parse tree
	 */
	void exitStored_definition(modelicaParser.Stored_definitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#class_definition}.
	 * @param ctx the parse tree
	 */
	void enterClass_definition(modelicaParser.Class_definitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#class_definition}.
	 * @param ctx the parse tree
	 */
	void exitClass_definition(modelicaParser.Class_definitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#class_specifier}.
	 * @param ctx the parse tree
	 */
	void enterClass_specifier(modelicaParser.Class_specifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#class_specifier}.
	 * @param ctx the parse tree
	 */
	void exitClass_specifier(modelicaParser.Class_specifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#class_prefixes}.
	 * @param ctx the parse tree
	 */
	void enterClass_prefixes(modelicaParser.Class_prefixesContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#class_prefixes}.
	 * @param ctx the parse tree
	 */
	void exitClass_prefixes(modelicaParser.Class_prefixesContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#long_class_specifier}.
	 * @param ctx the parse tree
	 */
	void enterLong_class_specifier(modelicaParser.Long_class_specifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#long_class_specifier}.
	 * @param ctx the parse tree
	 */
	void exitLong_class_specifier(modelicaParser.Long_class_specifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#short_class_specifier}.
	 * @param ctx the parse tree
	 */
	void enterShort_class_specifier(modelicaParser.Short_class_specifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#short_class_specifier}.
	 * @param ctx the parse tree
	 */
	void exitShort_class_specifier(modelicaParser.Short_class_specifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#der_class_specifier}.
	 * @param ctx the parse tree
	 */
	void enterDer_class_specifier(modelicaParser.Der_class_specifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#der_class_specifier}.
	 * @param ctx the parse tree
	 */
	void exitDer_class_specifier(modelicaParser.Der_class_specifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#base_prefix}.
	 * @param ctx the parse tree
	 */
	void enterBase_prefix(modelicaParser.Base_prefixContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#base_prefix}.
	 * @param ctx the parse tree
	 */
	void exitBase_prefix(modelicaParser.Base_prefixContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#enum_list}.
	 * @param ctx the parse tree
	 */
	void enterEnum_list(modelicaParser.Enum_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#enum_list}.
	 * @param ctx the parse tree
	 */
	void exitEnum_list(modelicaParser.Enum_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#enumeration_literal}.
	 * @param ctx the parse tree
	 */
	void enterEnumeration_literal(modelicaParser.Enumeration_literalContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#enumeration_literal}.
	 * @param ctx the parse tree
	 */
	void exitEnumeration_literal(modelicaParser.Enumeration_literalContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#composition}.
	 * @param ctx the parse tree
	 */
	void enterComposition(modelicaParser.CompositionContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#composition}.
	 * @param ctx the parse tree
	 */
	void exitComposition(modelicaParser.CompositionContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#language_specification}.
	 * @param ctx the parse tree
	 */
	void enterLanguage_specification(modelicaParser.Language_specificationContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#language_specification}.
	 * @param ctx the parse tree
	 */
	void exitLanguage_specification(modelicaParser.Language_specificationContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#external_function_call}.
	 * @param ctx the parse tree
	 */
	void enterExternal_function_call(modelicaParser.External_function_callContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#external_function_call}.
	 * @param ctx the parse tree
	 */
	void exitExternal_function_call(modelicaParser.External_function_callContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#element_list}.
	 * @param ctx the parse tree
	 */
	void enterElement_list(modelicaParser.Element_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#element_list}.
	 * @param ctx the parse tree
	 */
	void exitElement_list(modelicaParser.Element_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#element}.
	 * @param ctx the parse tree
	 */
	void enterElement(modelicaParser.ElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#element}.
	 * @param ctx the parse tree
	 */
	void exitElement(modelicaParser.ElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#import_clause}.
	 * @param ctx the parse tree
	 */
	void enterImport_clause(modelicaParser.Import_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#import_clause}.
	 * @param ctx the parse tree
	 */
	void exitImport_clause(modelicaParser.Import_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#import_list}.
	 * @param ctx the parse tree
	 */
	void enterImport_list(modelicaParser.Import_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#import_list}.
	 * @param ctx the parse tree
	 */
	void exitImport_list(modelicaParser.Import_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#extends_clause}.
	 * @param ctx the parse tree
	 */
	void enterExtends_clause(modelicaParser.Extends_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#extends_clause}.
	 * @param ctx the parse tree
	 */
	void exitExtends_clause(modelicaParser.Extends_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#constraining_clause}.
	 * @param ctx the parse tree
	 */
	void enterConstraining_clause(modelicaParser.Constraining_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#constraining_clause}.
	 * @param ctx the parse tree
	 */
	void exitConstraining_clause(modelicaParser.Constraining_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#component_clause}.
	 * @param ctx the parse tree
	 */
	void enterComponent_clause(modelicaParser.Component_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#component_clause}.
	 * @param ctx the parse tree
	 */
	void exitComponent_clause(modelicaParser.Component_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#type_prefix}.
	 * @param ctx the parse tree
	 */
	void enterType_prefix(modelicaParser.Type_prefixContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#type_prefix}.
	 * @param ctx the parse tree
	 */
	void exitType_prefix(modelicaParser.Type_prefixContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#type_specifier}.
	 * @param ctx the parse tree
	 */
	void enterType_specifier(modelicaParser.Type_specifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#type_specifier}.
	 * @param ctx the parse tree
	 */
	void exitType_specifier(modelicaParser.Type_specifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#component_list}.
	 * @param ctx the parse tree
	 */
	void enterComponent_list(modelicaParser.Component_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#component_list}.
	 * @param ctx the parse tree
	 */
	void exitComponent_list(modelicaParser.Component_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#component_declaration}.
	 * @param ctx the parse tree
	 */
	void enterComponent_declaration(modelicaParser.Component_declarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#component_declaration}.
	 * @param ctx the parse tree
	 */
	void exitComponent_declaration(modelicaParser.Component_declarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#condition_attribute}.
	 * @param ctx the parse tree
	 */
	void enterCondition_attribute(modelicaParser.Condition_attributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#condition_attribute}.
	 * @param ctx the parse tree
	 */
	void exitCondition_attribute(modelicaParser.Condition_attributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(modelicaParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(modelicaParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#modification}.
	 * @param ctx the parse tree
	 */
	void enterModification(modelicaParser.ModificationContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#modification}.
	 * @param ctx the parse tree
	 */
	void exitModification(modelicaParser.ModificationContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#class_modification}.
	 * @param ctx the parse tree
	 */
	void enterClass_modification(modelicaParser.Class_modificationContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#class_modification}.
	 * @param ctx the parse tree
	 */
	void exitClass_modification(modelicaParser.Class_modificationContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#argument_list}.
	 * @param ctx the parse tree
	 */
	void enterArgument_list(modelicaParser.Argument_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#argument_list}.
	 * @param ctx the parse tree
	 */
	void exitArgument_list(modelicaParser.Argument_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#argument}.
	 * @param ctx the parse tree
	 */
	void enterArgument(modelicaParser.ArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#argument}.
	 * @param ctx the parse tree
	 */
	void exitArgument(modelicaParser.ArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#element_modification_or_replaceable}.
	 * @param ctx the parse tree
	 */
	void enterElement_modification_or_replaceable(modelicaParser.Element_modification_or_replaceableContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#element_modification_or_replaceable}.
	 * @param ctx the parse tree
	 */
	void exitElement_modification_or_replaceable(modelicaParser.Element_modification_or_replaceableContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#element_modification}.
	 * @param ctx the parse tree
	 */
	void enterElement_modification(modelicaParser.Element_modificationContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#element_modification}.
	 * @param ctx the parse tree
	 */
	void exitElement_modification(modelicaParser.Element_modificationContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#element_redeclaration}.
	 * @param ctx the parse tree
	 */
	void enterElement_redeclaration(modelicaParser.Element_redeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#element_redeclaration}.
	 * @param ctx the parse tree
	 */
	void exitElement_redeclaration(modelicaParser.Element_redeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#element_replaceable}.
	 * @param ctx the parse tree
	 */
	void enterElement_replaceable(modelicaParser.Element_replaceableContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#element_replaceable}.
	 * @param ctx the parse tree
	 */
	void exitElement_replaceable(modelicaParser.Element_replaceableContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#component_clause1}.
	 * @param ctx the parse tree
	 */
	void enterComponent_clause1(modelicaParser.Component_clause1Context ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#component_clause1}.
	 * @param ctx the parse tree
	 */
	void exitComponent_clause1(modelicaParser.Component_clause1Context ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#component_declaration1}.
	 * @param ctx the parse tree
	 */
	void enterComponent_declaration1(modelicaParser.Component_declaration1Context ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#component_declaration1}.
	 * @param ctx the parse tree
	 */
	void exitComponent_declaration1(modelicaParser.Component_declaration1Context ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#short_class_definition}.
	 * @param ctx the parse tree
	 */
	void enterShort_class_definition(modelicaParser.Short_class_definitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#short_class_definition}.
	 * @param ctx the parse tree
	 */
	void exitShort_class_definition(modelicaParser.Short_class_definitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#equation_section}.
	 * @param ctx the parse tree
	 */
	void enterEquation_section(modelicaParser.Equation_sectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#equation_section}.
	 * @param ctx the parse tree
	 */
	void exitEquation_section(modelicaParser.Equation_sectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#algorithm_section}.
	 * @param ctx the parse tree
	 */
	void enterAlgorithm_section(modelicaParser.Algorithm_sectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#algorithm_section}.
	 * @param ctx the parse tree
	 */
	void exitAlgorithm_section(modelicaParser.Algorithm_sectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#equation}.
	 * @param ctx the parse tree
	 */
	void enterEquation(modelicaParser.EquationContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#equation}.
	 * @param ctx the parse tree
	 */
	void exitEquation(modelicaParser.EquationContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(modelicaParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(modelicaParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#if_equation}.
	 * @param ctx the parse tree
	 */
	void enterIf_equation(modelicaParser.If_equationContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#if_equation}.
	 * @param ctx the parse tree
	 */
	void exitIf_equation(modelicaParser.If_equationContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#if_statement}.
	 * @param ctx the parse tree
	 */
	void enterIf_statement(modelicaParser.If_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#if_statement}.
	 * @param ctx the parse tree
	 */
	void exitIf_statement(modelicaParser.If_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#for_equation}.
	 * @param ctx the parse tree
	 */
	void enterFor_equation(modelicaParser.For_equationContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#for_equation}.
	 * @param ctx the parse tree
	 */
	void exitFor_equation(modelicaParser.For_equationContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#for_statement}.
	 * @param ctx the parse tree
	 */
	void enterFor_statement(modelicaParser.For_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#for_statement}.
	 * @param ctx the parse tree
	 */
	void exitFor_statement(modelicaParser.For_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#for_indices}.
	 * @param ctx the parse tree
	 */
	void enterFor_indices(modelicaParser.For_indicesContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#for_indices}.
	 * @param ctx the parse tree
	 */
	void exitFor_indices(modelicaParser.For_indicesContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#for_index}.
	 * @param ctx the parse tree
	 */
	void enterFor_index(modelicaParser.For_indexContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#for_index}.
	 * @param ctx the parse tree
	 */
	void exitFor_index(modelicaParser.For_indexContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#while_statement}.
	 * @param ctx the parse tree
	 */
	void enterWhile_statement(modelicaParser.While_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#while_statement}.
	 * @param ctx the parse tree
	 */
	void exitWhile_statement(modelicaParser.While_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#when_equation}.
	 * @param ctx the parse tree
	 */
	void enterWhen_equation(modelicaParser.When_equationContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#when_equation}.
	 * @param ctx the parse tree
	 */
	void exitWhen_equation(modelicaParser.When_equationContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#when_statement}.
	 * @param ctx the parse tree
	 */
	void enterWhen_statement(modelicaParser.When_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#when_statement}.
	 * @param ctx the parse tree
	 */
	void exitWhen_statement(modelicaParser.When_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#connect_clause}.
	 * @param ctx the parse tree
	 */
	void enterConnect_clause(modelicaParser.Connect_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#connect_clause}.
	 * @param ctx the parse tree
	 */
	void exitConnect_clause(modelicaParser.Connect_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(modelicaParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(modelicaParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#simple_expression}.
	 * @param ctx the parse tree
	 */
	void enterSimple_expression(modelicaParser.Simple_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#simple_expression}.
	 * @param ctx the parse tree
	 */
	void exitSimple_expression(modelicaParser.Simple_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#logical_expression}.
	 * @param ctx the parse tree
	 */
	void enterLogical_expression(modelicaParser.Logical_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#logical_expression}.
	 * @param ctx the parse tree
	 */
	void exitLogical_expression(modelicaParser.Logical_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#logical_term}.
	 * @param ctx the parse tree
	 */
	void enterLogical_term(modelicaParser.Logical_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#logical_term}.
	 * @param ctx the parse tree
	 */
	void exitLogical_term(modelicaParser.Logical_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#logical_factor}.
	 * @param ctx the parse tree
	 */
	void enterLogical_factor(modelicaParser.Logical_factorContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#logical_factor}.
	 * @param ctx the parse tree
	 */
	void exitLogical_factor(modelicaParser.Logical_factorContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#relation}.
	 * @param ctx the parse tree
	 */
	void enterRelation(modelicaParser.RelationContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#relation}.
	 * @param ctx the parse tree
	 */
	void exitRelation(modelicaParser.RelationContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#rel_op}.
	 * @param ctx the parse tree
	 */
	void enterRel_op(modelicaParser.Rel_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#rel_op}.
	 * @param ctx the parse tree
	 */
	void exitRel_op(modelicaParser.Rel_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#arithmetic_expression}.
	 * @param ctx the parse tree
	 */
	void enterArithmetic_expression(modelicaParser.Arithmetic_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#arithmetic_expression}.
	 * @param ctx the parse tree
	 */
	void exitArithmetic_expression(modelicaParser.Arithmetic_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#add_op}.
	 * @param ctx the parse tree
	 */
	void enterAdd_op(modelicaParser.Add_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#add_op}.
	 * @param ctx the parse tree
	 */
	void exitAdd_op(modelicaParser.Add_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(modelicaParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(modelicaParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#mul_op}.
	 * @param ctx the parse tree
	 */
	void enterMul_op(modelicaParser.Mul_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#mul_op}.
	 * @param ctx the parse tree
	 */
	void exitMul_op(modelicaParser.Mul_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterFactor(modelicaParser.FactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitFactor(modelicaParser.FactorContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(modelicaParser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(modelicaParser.PrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(modelicaParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(modelicaParser.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#component_reference}.
	 * @param ctx the parse tree
	 */
	void enterComponent_reference(modelicaParser.Component_referenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#component_reference}.
	 * @param ctx the parse tree
	 */
	void exitComponent_reference(modelicaParser.Component_referenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#function_call_args}.
	 * @param ctx the parse tree
	 */
	void enterFunction_call_args(modelicaParser.Function_call_argsContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#function_call_args}.
	 * @param ctx the parse tree
	 */
	void exitFunction_call_args(modelicaParser.Function_call_argsContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#function_arguments}.
	 * @param ctx the parse tree
	 */
	void enterFunction_arguments(modelicaParser.Function_argumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#function_arguments}.
	 * @param ctx the parse tree
	 */
	void exitFunction_arguments(modelicaParser.Function_argumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#named_arguments}.
	 * @param ctx the parse tree
	 */
	void enterNamed_arguments(modelicaParser.Named_argumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#named_arguments}.
	 * @param ctx the parse tree
	 */
	void exitNamed_arguments(modelicaParser.Named_argumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#named_argument}.
	 * @param ctx the parse tree
	 */
	void enterNamed_argument(modelicaParser.Named_argumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#named_argument}.
	 * @param ctx the parse tree
	 */
	void exitNamed_argument(modelicaParser.Named_argumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#function_argument}.
	 * @param ctx the parse tree
	 */
	void enterFunction_argument(modelicaParser.Function_argumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#function_argument}.
	 * @param ctx the parse tree
	 */
	void exitFunction_argument(modelicaParser.Function_argumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#output_expression_list}.
	 * @param ctx the parse tree
	 */
	void enterOutput_expression_list(modelicaParser.Output_expression_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#output_expression_list}.
	 * @param ctx the parse tree
	 */
	void exitOutput_expression_list(modelicaParser.Output_expression_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#expression_list}.
	 * @param ctx the parse tree
	 */
	void enterExpression_list(modelicaParser.Expression_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#expression_list}.
	 * @param ctx the parse tree
	 */
	void exitExpression_list(modelicaParser.Expression_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#array_subscripts}.
	 * @param ctx the parse tree
	 */
	void enterArray_subscripts(modelicaParser.Array_subscriptsContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#array_subscripts}.
	 * @param ctx the parse tree
	 */
	void exitArray_subscripts(modelicaParser.Array_subscriptsContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#subscript}.
	 * @param ctx the parse tree
	 */
	void enterSubscript(modelicaParser.SubscriptContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#subscript}.
	 * @param ctx the parse tree
	 */
	void exitSubscript(modelicaParser.SubscriptContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#comment}.
	 * @param ctx the parse tree
	 */
	void enterComment(modelicaParser.CommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#comment}.
	 * @param ctx the parse tree
	 */
	void exitComment(modelicaParser.CommentContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#string_comment}.
	 * @param ctx the parse tree
	 */
	void enterString_comment(modelicaParser.String_commentContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#string_comment}.
	 * @param ctx the parse tree
	 */
	void exitString_comment(modelicaParser.String_commentContext ctx);
	/**
	 * Enter a parse tree produced by {@link modelicaParser#annotation}.
	 * @param ctx the parse tree
	 */
	void enterAnnotation(modelicaParser.AnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link modelicaParser#annotation}.
	 * @param ctx the parse tree
	 */
	void exitAnnotation(modelicaParser.AnnotationContext ctx);
}