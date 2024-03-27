import logging
import time

def insert_triples_header(default_world, sparql_header):
    logging.info("generating triples for header")
    result = list(
        default_world.sparql(
            sparql_header + """
               SELECT ?ifcinstance { 
                   ?ifcinstance rdf:type moont:MModel. 
               }""", error_on_undefined_entities=False))
    default_world.sparql(sparql_header + """
        INSERT {
             moinst: moont:stringComment \"""" + result[0][0].name + """\".
             moinst: a moont:MModel.
             moinst: moont:extends msl:Modelica.Icons.Examples.
            } WHERE{} """
                         , error_on_undefined_entities=False)

def insert_triples_components(default_world, sparql_header):
    logging.info("generating triples for components of model")
    test = default_world.sparql(sparql_header + """ 
        INSERT {
            moinst: moont:hasPart ?obj.
            ?obj a moont:MComponent.
            ?obj moont:identifier ?namev.
            ?obj moont:comment ?guidv.
            } WHERE { 
                ?obj ifc:globalId_IfcRoot / express:hasString ?guidv.
                ?obj ifc:name_IfcRoot / express:hasString ?namev.
                ?obj rdf:type / rdfs:subClassOf* moon:MComponent.
            } """)

    #result = list(default_world.sparql(sparql_header + """SELECT DISTINCT ?obj WHERE {
    #?obj rdf:type / rdfs:subClassOf* moon:MComponent.
    #?obj ifc:globalId_IfcRoot / express:hasString ?guidv.
    #?obj ifc:name_IfcRoot / express:hasString ?namev.} """))
    #print(test)
    #moont:identifier = ifc:globalId_IfcRoot / express:hasString ins Alignment? - geht nicht, da datatype Property
    #moont:comment ifc:globalId_IfcRoot / express:hasString - dito
def insert_triples_components_placement(default_world, sparql_header):
    # find name, guid and position of objects #TODO (bisher für alle, not limited to MComponents - sollte es aber sein)
    logging.info("generating triples for position of components")
    result = list(
        default_world.sparql(
            sparql_header + """
               SELECT ?obj ?namev ?guidv ?x_coord ?y_coord { 
                   ?irdbp ifc:relatedObjects_IfcRelDefinesByProperties ?obj.
                   ?irdbp ifc:relatingPropertyDefinition_IfcRelDefinesByProperties ?ps.
                   ?obj ifc:globalId_IfcRoot ?guid.
                   ?guid express:hasString ?guidv.
                    ?obj ifc:name_IfcRoot ?name.
                   ?name express:hasString ?namev.
                   ?ps ifc:hasProperties_IfcPropertySet ?psvx.
                   ?psvx a aimaix:IfcPropertySingleValue_x_coord .
                   ?psvx ifc:nominalValue_IfcPropertySingleValue ?r.
                   ?r express:hasDouble ?x_coord. 
                   ?ps ifc:hasProperties_IfcPropertySet ?psvy.
                   ?psvy a aimaix:IfcPropertySingleValue_y_coord .
                   ?psvy ifc:nominalValue_IfcPropertySingleValue ?r2.
                   ?r2 express:hasDouble ?y_coord. 
               }"""))



    for res in result:
        coord_string = "\"{" + str(res[3]) + ", " + str(res[4]) + "}\""
        new_axiom = "<" + res[0].namespace.base_iri + res[0].get_name() + "> moont:placement_origin " + coord_string + "."
        default_world.sparql(
            sparql_header + """
            INSERT {"""
            + new_axiom +
            """} WHERE{} """
            , error_on_undefined_entities=False)

def insert_triples_components_class_assignment(default_world, sparql_header):
    logging.info("generating triples stating the Modelica class (and some additional classes) of all components")
    result11 = list(
        default_world.sparql(
            sparql_header + """
               SELECT ?obj ?class{ 
                   ?obj rdf:type / rdfs:subClassOf* moon:MComponent.
                   ?obj rdf:type / rdfs:subClassOf* ?class.
                    ?class rdfs:subClassOf* moon:MComponent.
               }"""))  # ergibt 206 Zuordnungen von Objekt zu Klasse
    new_axiom_count = 0
    set_of_new_axioms = set()
    for res in result11:
        if res[1].namespace.base_iri == 'https://w3id.org/aix/': #TODO propagate Namespace of classes that will be translated as a parameter
            # new_axiom = "<" + res[0].namespace.base_iri + res[0].get_name() + "> rdf:type " + "<" + res[1].namespace.base_iri + res[1].get_name() + ">."
            # new_axiom = "<" + res[0].namespace.base_iri + res[0].get_name() + "> rdf:type " + "<" + res[1].namespace.base_iri + res[1].name + ">."
            new_axiom = "<" + res[0].namespace.base_iri + res[0].name + "> rdf:type " + "<" + res[1].namespace.base_iri + res[1].name + ">."
            set_of_new_axioms.add(new_axiom)
            new_axiom_count += 1
    print(new_axiom_count)
    insertion_count = 1
    for na in set_of_new_axioms:
        default_world.sparql(
            sparql_header + """
                INSERT {"""
            + na +
            """} WHERE{} """
            #, error_on_undefined_entities=False)
            , error_on_undefined_entities=True)
        insertion_count += 1
    logging.info("insertion count: " + str(insertion_count))

def insert_triples_components_class_assignment_2(default_world, sparql_header, moinst_python):
    logging.info("generating triples stating the Modelica class (and some additional classes) of all components")
    result11 = list(
        default_world.sparql(
            sparql_header + """
               SELECT ?obj ?class{ 
                   ?obj rdf:type / rdfs:subClassOf* moon:MComponent.
                   ?obj rdf:type / rdfs:subClassOf* ?class.
                    ?class rdfs:subClassOf* moon:MComponent.
               }"""))  # ergibt 206 Zuordnungen von Objekt zu Klasse
    new_axiom_count = 0
    set_of_new_axioms = set()
    for res in result11:
        if res[1].namespace.base_iri == 'https://w3id.org/aix/': #TODO propagate Namespace of classes that will be translated as a parameter
            # new_axiom = "<" + res[0].namespace.base_iri + res[0].get_name() + "> rdf:type " + "<" + res[1].namespace.base_iri + res[1].get_name() + ">."
            # new_axiom = "<" + res[0].namespace.base_iri + res[0].get_name() + "> rdf:type " + "<" + res[1].namespace.base_iri + res[1].name + ">."
            new_axiom = "<" + res[0].namespace.base_iri + res[0].name + "> rdf:type " + "<" + res[1].namespace.base_iri + res[1].name + ">."
            set_of_new_axioms.add(new_axiom)
            new_axiom_count += 1
    print(new_axiom_count)
    insertion_count = 1
    for na in set_of_new_axioms:
        default_world.sparql(
            sparql_header + """
            WITH <http://eas.iis.fraunhofer.de/testmodelicapython/>
                INSERT {"""
            + na +
            """} WHERE{} """
            , error_on_undefined_entities=True)
        insertion_count += 1
    logging.info("insertion count: " + str(insertion_count))
    return moinst_python


def insert_triples_port_description(default_world, sparql_header):
    logging.info("generating triples for all ports: description")
    default_world.sparql( sparql_header + """
        INSERT { 
            ?port moont:identifier ?descr. 
            } WHERE { 
                ?port a ifc:IfcDistributionPort.
                ?port ifc:description_IfcRoot / express:hasString ?descr . 
            } """)

def insert_triples_port_connections(default_world, sparql_header):
    logging.info("generating triples for connected ports")
    test = default_world.sparql( sparql_header + """
        INSERT {
            ?port1 moont:connectedTo ?port2.
            ?obj1 moont:hasPart ?port1.
            ?obj2 moont:hasPart ?port2.
            ?port1 a moont:MConnectorComponent.
            ?port2 a moont:MConnectorComponent.
            } WHERE { 
                ?port1 moont:connectedTo ?port2.
                ?obj1 moont:hasPart ?port1.
                ?obj2 moont:hasPart ?port2.
                ?port1 a ifc:IfcDistributionPort.
                ?port1 ifc:description_IfcRoot / express:hasString ?descr1.
                ?port2 a ifc:IfcDistributionPort.
                ?port2 ifc:description_IfcRoot / express:hasString ?descr2.
            } """)
    #print(test)

def get_sparql_header():
    timestring = time.strftime('%Y%m%d_%H%M%S')

    sparql_header = """
     PREFIX ifc: <https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#>
     PREFIX aimaix: <https://w3id.org/aimaix/>
     PREFIX aima: <https://w3id.org/aimaix/>
     PREFIX moont: <https://w3id.org/moont/>
     PREFIX moon: <https://w3id.org/moont/>
     PREFIX msl: <https://w3id.org/msl/>
     PREFIX express: <https://w3id.org/express#>
     PREFIX moinst: <http://eas.iis.fraunhofer.de/resources20230725_090232_""" + timestring + """/>
     """
    return sparql_header