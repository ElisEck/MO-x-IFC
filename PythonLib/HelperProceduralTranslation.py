import logging
from rdflib import Namespace, Graph, URIRef, Literal
import ifcopenshell
from rdflib.namespace import DCTERMS
from datetime import datetime

def add_metadata_to_graph(graph, sourcepath):
    graph.add((URIRef("http://www.eas.iis.fraunhofer.de/hil#"), DCTERMS.issued, Literal(datetime.now().strftime("%Y-%m-%d %H:%M"))))
    graph.add((URIRef("http://www.eas.iis.fraunhofer.de/hil#"), DCTERMS.source, Literal(sourcepath)))
    graph.add((URIRef("http://www.eas.iis.fraunhofer.de/hil#"), DCTERMS.creator, Literal("C:/_DATEN/WORKSPACES/PyCharm/SemanticScripting/2023-12-20 Modelica to IFC procedural with ports/BivalentPlant.py executed by eckstaedt on troll 918")))
    graph.add((URIRef("http://www.eas.iis.fraunhofer.de/hil#"), DCTERMS.title, Literal("HIL")))
    return graph
def bind_ifc_prefixes(graph):
    """
    ifc, list, express, dcterms
    :param graph:
    :return:
    """
    graph.bind("ifc", Namespace("https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#"))
    graph.bind("list", Namespace("https://w3id.org/list#"))
    graph.bind("express", Namespace("https://w3id.org/express#"))
    graph.bind("dcterms", Namespace("http://purl.org/dc/terms/"))
    return graph
def generate_IfcRelNests_for_ports(instancegraph, lineno, prefix = 'hil'):
    """
    Erzeugung von IfcDistributionPorts
    Zuordnung aller verbundenen Ports einer Komponente zu dieser Komponente
    :param instancegraph: with triples from moont namespace
    :return: graph with new triples from ifc namesspace
    """
    logging.info("generating Nests Relations...")
    query_select = """
    SELECT DISTINCT ?port ?comp
    WHERE {
        {?port moont:connectedTo ?port2.}
        UNION
        {?port2 moont:connectedTo ?port.}
        ?comp moont:hasPart ?port.
    } 
    """
    ports_and_components = instancegraph.query(query_select)

    result = Graph()
    dict_comp_port = dict()
    for line in ports_and_components:
        port = line.get('port')
        comp = line.get('comp')
        if comp in dict_comp_port:
            dict_comp_port[comp].append(port)
        else:
            dict_comp_port[comp] = [port]
    for comp in dict_comp_port:
        ports = dict_comp_port[comp]
        no_of_ports = len(dict_comp_port[comp])
        irn = prefix + ':IfcRelNests_{num:0{width}}'.format(num=lineno, width=3)
        lineno += 1
        iodl = prefix + ':IfcObjectDefinition_List_{num:0{width}}'.format(num=lineno, width=3)
        lineno += 1
        iguid = prefix + ':IfcGloballyUniqueId_{num:0{width}}'.format(num=lineno, width=3)
        lineno += 1
        guid = ifcopenshell.guid.new()
        query_construct_connections = """
        construct {
            """ + irn + """   rdf:type  ifc:IfcRelNests .
            """ + irn + """ ifc:globalId_IfcRoot """ + iguid + """.
            """ + iguid + """ express:hasString  \""""+ guid +"""\" .
            """ + irn + """   ifc:relatingObject_IfcRelNests  <""" + str(comp) + """> .
            """ + irn + """   ifc:relatedObjects_IfcRelNests  """ + iodl + """.
            """ + iodl + """ rdf:type ifc:IfcObjectDefinition_List .
            """ + iodl + """ list:hasContents  <""" + str(ports[0]) + """> .
            }
        WHERE {    }    """
        compresult = instancegraph.query(query_construct_connections).graph
        # für jeden weiteren Port
        if no_of_ports>1:
            for i in range(1,no_of_ports):
                iodl2 = prefix + ':IfcObjectDefinition_List_{num:0{width}}'.format(num=lineno, width=3)
                lineno += 1
                query_construct_connections = """
                construct {
                    """ + iodl + """ list:hasNext      """ + iodl2 + """ .
                    """ + iodl2 + """ list:hasContents  <""" + str(ports[i]) + """> .
                    }
                WHERE {    }    """
                lineresult = instancegraph.query(query_construct_connections)
                compresult = compresult + lineresult.graph
                iodl = iodl2
        for port in ports:
            lineno += 1
            iguid = prefix + ':IfcGloballyUniqueId_{num:0{width}}'.format(num=lineno, width=3)
            guid = ifcopenshell.guid.new()
            query_construct_connections = """
            construct
            {
                <""" + str(port) + """> rdf:type ifc:IfcDistributionPort.
                <""" + str(port) + """> ifc:globalId_IfcRoot """ + iguid + """.
                """ + iguid + """ express:hasString  \""""+ guid +"""\" .
            } WHERE {}
            """
            lineresult = instancegraph.query(query_construct_connections)
            compresult = compresult + lineresult.graph

        result = result + compresult
    return result, lineno

def generate_IfcRelConnectsPorts(instancegraph, lineno, prefix='hil'):
    """
    ein Triple für jede Verbindung
    :param instancegraph: with triples from moont namespace
    :return: graph with new triples from ifc namesspace
    """
    logging.info("generating port connections...")
    query_select = """
    SELECT ?port1 ?port2 ?comp1 ?comp2
    WHERE {
        ?port1 moont:connectedTo ?port2.
        ?comp1 moont:hasPart ?port1.
        ?comp2 moont:hasPart ?port2.
    } 
    """
    ports_and_components = instancegraph.query(query_select)
    result = Graph()
    for line in ports_and_components:
        port1 = line.get('port1')
        port2 = line.get('port2')
        ircp = prefix + ':IfcRelConnectsPorts_{num:0{width}}'.format(num=lineno, width=3)
        #TODO eigentlich: GUID zumindest versuchen auszulesen
        lineno += 1
        iguid = prefix + ':IfcGloballyUniqueId_{num:0{width}}'.format(num=lineno, width=3)
        guid = ifcopenshell.guid.new()
        lineno += 1
        query_construct_connections = """
        construct {
            """ + ircp + """ rdf:type  ifc:IfcRelConnectsPorts .
            """ + ircp + """ ifc:globalId_IfcRoot """ + iguid + """.
            """ + iguid + """ express:hasString  \""""+ guid +"""\" .
            """ + ircp + """ ifc:relatingPort_IfcRelConnectsPorts <""" + str(port1) + """> .
            """ + ircp + """ ifc:relatedPort_IfcRelConnectsPorts  <""" + str(port2) + """> .
            }
        WHERE { }  ORDER BY ASC(?comp)
        """
        lineresult = instancegraph.query(query_construct_connections)
        result = result + lineresult.graph
    return result, lineno

def generate_instance_objects(moont_graph, lineno, model_resource_string="hil:HIL.HIL_flat", prefix='hil'):
    dict_mapping = {
        'IfcPipeSegment_USERDEFINED': ['libeas:LibEAS.Sensors.PressureDrop_Display','aix:AixLib.Fluid.BaseClasses.PartialResistance', 'aix:AixLib.Fluid.FixedResistances.LosslessPipe'],
        'IfcPipeFitting_JUNCTION': ['aix:AixLib.Fluid.FixedResistances.Junction', 'aix:AixLib.Fluid.BaseClasses.PartialThreeWayResistance', 'libeas:LibEAS.Sensors.Junction_Display'],
        'IfcValve_CHECK': ['aix:AixLib.Fluid.FixedResistances.CheckValve'],
        'IfcValve_ISOLATING': ['aix:AixLib.Fluid.Actuators.Valves.TwoWayLinear'],
        'IfcValve_MIXING': ['aix:AixLib.Fluid.Actuators.Valves.ThreeWayLinear'],
        'IfcHeatExchanger_PLATE': ['aix:AixLib.Fluid.HeatExchangers.ConstantEffectiveness'],
        'IfcHeatExchanger_USERDEFINED': ['aix:AixLib.Fluid.HeatExchangers.SensibleCooler_T', 'libeas:LibEAS.HeatingCircuits.CharacteristicCurve'],  # eigentlich sind das alles Platzhalter...
        #'IfcHeatExchanger_USERDEFINED': ['aix:AixLib.Fluid.HeatExchangers.SensibleCooler_T', 'aix:AixLib.Fluid.HeatExchangers.ConstantEffectiveness', 'libeas:LibEAS.HeatingCircuits.CharacteristicCurve'],  # eigentlich sind das alles Platzhalter...
        'IfcBoiler_WATER': ['aix:AixLib.Fluid.BoilerCHP.BaseClasses.PartialHeatGenerator', 'libeas:LibEAS.BoilerCHP.BoilerlWithoutInternalControl'],  # PartialHeatGenerator zu allgemein?
        'IfcSensor_FLOWSENSOR': ['aix:AixLib.Fluid.Sensors.EnthalpyFlowRate'],  # eigentlich nicht ganz richtig, ist so nicht vom Durchflussmesser zu unterscheiden - habe ich aber nicht... (oder doch: "PressureDrop_Display)
        'IfcSensor_HEATSENSOR': ['libeas:LibEAS.Sensors.WMZ'],
        #'IfcUnitaryEquipment_SPLITSYSTEM': ['aix:AixLib.Fluid.HeatPumps.HeatPump','aix:AixLib.Fluid.BaseClasses.PartialReversibleVapourCompressionMachine','bipl:BivalentPlant.components.WP'], #TODO letzeres sollte nicht vorkommen, benutzerdefinierte Komponente
        'IfcUnitaryEquipment_SPLITSYSTEM': ['aix:AixLib.Fluid.HeatPumps.HeatPump','aix:AixLib.Fluid.BaseClasses.PartialReversibleVapourCompressionMachine'],
        'IfcSensor_TEMPERATURESENSOR': ['libeas:LibEAS.Sensors.TemperatureTwoPort_Display', 'aix:AixLib.Fluid.Sensors.TemperatureTwoPort'],  # neu
        'IfcPump_CIRCULATOR': ['aix:AixLib.Fluid.Movers.SpeedControlled_y', 'aix:AixLib.Fluid.Movers.FlowControlled_m_flow'],  # abweichend
        'IfcTank_EXPANSION': ['aix:AixLib.Fluid.Sources.Boundary_pT'],  # neu
        'IfcFlowInstrument_PRESSUREGAUGE': ['aix:AixLib.Fluid.Sensors.RelativePressure'],  # neu
        'IfcTank_STORAGE': ['libeas:LibEAS.Storage.StratifiedEnhanced','aix:AixLib.Fluid.Storage.Stratified','mbl:Buildings.Fluid.Storage.Stratified'],  # neu
    }

    set_of_translated_modelica_idents = set()
    set_of_translated_modelica_classes = set()
    logging.info("generating class assignments...")
    ifcowl_graph = Graph()
    #TODO neuen Namen für IFC-Modell vergeben?
    for ifc_class_type in dict_mapping:
        for modelica_class in dict_mapping[ifc_class_type]:
            logging.info("searching for " + str(ifc_class_type) + " and " + str(modelica_class))
            #result_all_comps = query_for_modelica_components_of_certain_class(moont_graph, modelica_class)
            result_all_comps = query_for_modelica_components_of_certain_class_with_extends(moont_graph, modelica_class, model_resource_string=model_resource_string)
            for line in result_all_comps:
                comp = line.get('comp')
                ident = line.get('ident')
                set_of_translated_modelica_idents.add(ident) #TODO check that one modelica component is translated only once! (nicht nochmal wegen Vererbung)
                set_of_translated_modelica_classes.add(modelica_class)
                triples = construct_ifc_instance(moont_graph, comp, ifc_class_type, lineno, ident, prefix=prefix)
                lineno += 2
                ifcowl_graph = ifcowl_graph + triples.graph
    check_for_not_translated_components(moont_graph, set_of_translated_modelica_idents, model_resource_string)
    return ifcowl_graph, lineno

def check_for_not_translated_components(moont_graph, set_of_translated_modelica_idents, model_resource_string="hil:HIL.HIL_flat"):
    query_comps = """
                   SELECT DISTINCT ?comp ?ident ?mclass
                   WHERE {
                   """ +   model_resource_string + """ moont:hasPart ?comp.
                       ?comp rdf:type ?mclass.
                       ?comp moont:identifier ?ident.
                   } ORDER BY ASC(?comp)
                   """
    result_all_comps_total = moont_graph.query(query_comps)
    for line in result_all_comps_total:
        mclass = line.get('mclass')
        if not (line.get('ident') in set_of_translated_modelica_idents):
            if not mclass == "http://w3id.org/moont#MComponent":
                logging.info("Modelica component not translated to IFC: " + str(line.get('ident')) + "\t it is of class: " + str(mclass))

def construct_ifc_instance(moont_graph, comp, ifc_class_type, lineno, ident, prefix='hil'):
    ifc_class = ifc_class_type.split("_")[0]
    ifc_predefined_type = ifc_class_type.split("_")[1]
    guid = ifcopenshell.guid.new()
    iguid = prefix + ':IfcGloballyUniqueId_{num:0{width}}'.format(num=lineno, width=3)  # TODO war vorher inst
    ilabel = prefix + ':IfcLabel_{num:0{width}}'.format(num=lineno + 1, width=3)  # TODO war vorher inst: - zurück ändern?
    query_class = """
        construct {
            <""" + str(comp) + """> rdf:type ifc:""" + ifc_class + """.
            <""" + str(comp) + """> ifc:predefinedType_""" + ifc_class + """ ifc:""" + ifc_predefined_type + """ .
            <""" + str(comp) + """> ifc:globalId_IfcRoot """ + iguid + """.
            """ + iguid + """ express:hasString  \"""" + guid + """\" .
            <""" + str(comp) + """> ifc:name_IfcRoot """ + ilabel + """.
            """ + ilabel + """  rdf:type  ifc:IfcLabel .
            """ + ilabel + """ express:hasString  \"""" + ident + """\" .
            }
        WHERE {}"""
    triples = moont_graph.query(query_class)
    return triples
def query_for_modelica_components_of_certain_class(moont_graph, modelica_class):
    query_comps = """
                   SELECT DISTINCT ?comp ?ident
                   WHERE {
                       hil:HIL.HIL_flat moont:hasPart ?comp.
                       ?comp rdf:type """ + modelica_class + """.
                       ?comp moont:identifier ?ident.
                   } ORDER BY ASC(?comp)
                   """
    # GUIDs vergeben (funktioniert nicht in einer construct query)
    result_all_comps = moont_graph.query(query_comps)
    return result_all_comps

"""
def query_for_modelica_components_of_certain_class_with_extends(moont_graph, modelica_class):
    query_comps = """
 #                  SELECT DISTINCT ?comp ?ident
  #                 WHERE {
   #                    hil:HIL.HIL_flat moont:hasPart ?comp.
    #                   ?comp rdf:type / moont:extends* """ + modelica_class + """.
     #                  ?comp moont:identifier ?ident.
      #             } ORDER BY ASC(?comp)
       #            """
"""    # GUIDs vergeben (funktioniert nicht in einer construct query)
    result_all_comps = moont_graph.query(query_comps)
    return result_all_comps
"""

def query_for_modelica_components_of_certain_class_with_extends(moont_graph, modelica_class, model_resource_string="hil:HIL.HIL_flat"):
    query_comps = ("""
                   SELECT DISTINCT ?comp ?ident
                   WHERE { 
                   """ + model_resource_string + """ moont:hasPart ?comp.
                       ?comp rdf:type / moont:extends* """ + modelica_class + """.
                       ?comp moont:identifier ?ident.
                   } ORDER BY ASC(?comp)
                   """)
    # GUIDs vergeben (funktioniert nicht in einer construct query)
    result_all_comps = moont_graph.query(query_comps)
    return result_all_comps


def add_placement_properties_to_objects(graph_mo, lineno, graph_ifc, model_resource_string="hil:HIL.HIL_flat", prefix='hil'):
    logging.info("adding placement properties to objects...")
    iidentx = prefix + ':IfcIdentifier_{num:0{width}}'.format(num=lineno, width=3) #TODO: war vorher inst: - zurück ändern?
    iidenty = prefix + ':IfcIdentifier_{num:0{width}}'.format(num=lineno+1, width=3) #TODO: war vorher inst: - zurück ändern?
    ilabel = prefix + ':IfcLabel_{num:0{width}}'.format(num=lineno+2, width=3) #TODO: war vorher inst: - zurück ändern?
    query_1 = """
        construct {
            """ + iidentx + """ rdf:type ifc:IfcIdentifier.
            """ + iidentx + """ express:hasString  \"x coordinate\" .
            """ + iidenty + """ rdf:type ifc:IfcIdentifier.
            """ + iidenty + """ express:hasString  \"y coordinate\" .
            """ + ilabel + """ rdf:type  ifc:IfcLabel .
            """ + ilabel + """ express:hasString  \"Layout_EE\" .
            }
        WHERE {}"""
    graph_ifc = graph_ifc + graph_mo.query(query_1).graph

    query_comps = """
                   SELECT DISTINCT ?comp ?ident ?coord
                   WHERE {
                   """ + model_resource_string + """ moont:hasPart ?comp.
                       ?comp moont:identifier ?ident.
                       ?comp moont:placement_origin ?coord
                   } ORDER BY ASC(?comp)
                   """
    result_all_comps = graph_mo.query(query_comps)
    lineno += 3
    for line in result_all_comps:
        comp = line.get('comp')
        ident = line.get('ident')
        x_coord = line.get('coord').split(",")[0][1:]
        y_coord = line.get('coord').split(",")[1][:-1]
        ipset = prefix  + ':IfcPropertySet_{num:0{width}}'.format(num=lineno, width=3)
        iguid = prefix  + ':IfcGloballyUniqueId_{num:0{width}}'.format(num=lineno+1, width=3)
        ipsvx = prefix  + ':IfcPropertySingleValue_{num:0{width}}'.format(num=lineno+2, width=3)
        ipsvy = prefix  + ':IfcPropertySingleValue_{num:0{width}}'.format(num=lineno+3, width=3)
        wertx = prefix  + ':IfcReal_{num:0{width}}'.format(num=lineno+4, width=3)
        werty = prefix  + ':IfcReal_{num:0{width}}'.format(num=lineno+5, width=3)
        irdbp = prefix  + ':IfcRelDefinesByProperties_{num:0{width}}'.format(num=lineno+6, width=3)
        iguid2 = prefix  + ':IfcGloballyUniqueId_{num:0{width}}'.format(num=lineno + 7, width=3)
        lineno += 8
        guid = ifcopenshell.guid.new()
        guid2 = ifcopenshell.guid.new()

        query_2 = """
            construct {
                """ + irdbp + """   rdf:type  ifc:IfcRelDefinesByProperties .
                """ + irdbp + """ ifc:globalId_IfcRoot """ + iguid2 + """.
                """ + iguid2 + """ express:hasString  \"""" + guid2 + """\" .
                """ + irdbp + """ ifc:relatedObjects_IfcRelDefinesByProperties <""" + str(comp) + """>.
                """ + irdbp + """ ifc:relatingPropertyDefinition_IfcRelDefinesByProperties """ + ipset + """.
                """ + ipsvx + """   rdf:type  ifc:IfcPropertySingleValue .
                """ + ipsvy + """   rdf:type  ifc:IfcPropertySingleValue .
                """ + ipsvx + """   ifc:name_IfcProperty """ + iidentx + """ . 
                """ + ipsvy + """   ifc:name_IfcProperty """ + iidenty + """ . 
                """ + ipsvx + """   ifc:nominalValue_IfcPropertySingleValue """ + wertx + """ . 
                """ + wertx + """     rdf:type  ifc:IfcReal . 
                """ + wertx + """   express:hasDouble \"""" + x_coord + """\"^^xsd:double .
                """ + ipsvy + """   ifc:nominalValue_IfcPropertySingleValue """ + werty + """ . 
                """ + werty + """     rdf:type  ifc:IfcReal . 
                """ + werty + """    express:hasDouble \"""" + y_coord + """\"^^xsd:double .
                """ + ipset + """   rdf:type  ifc:IfcPropertySet .
                """ + ipset + """ ifc:globalId_IfcRoot """ + iguid + """.
                """ + iguid + """ express:hasString  \"""" + guid + """\" .
                """ + ipset + """ ifc:name_IfcRoot """ + ilabel + """.
                """ + ipset + """ ifc:hasProperties_IfcPropertySet """ + ipsvx + """.
                """ + ipset + """ ifc:hasProperties_IfcPropertySet """ + ipsvy + """.
                }
            WHERE {}"""
        graph_ifc = graph_ifc + graph_mo.query(query_2).graph
    return graph_ifc, lineno

def generate_property_identifier(lineno, name):
    iident_prop = 'hil:IfcIdentifier_{num:0{width}}'.format(num=lineno, width=3)
    query_ident = """
        construct {
            """ + iident_prop + """ rdf:type ifc:IfcIdentifier.
            """ + iident_prop + """ express:hasString  \"""" + name +"""\" .
            }
        WHERE {}"""
    graph_new = dummy_graph().query(query_ident).graph
    return graph_new, iident_prop, lineno+1

def dummy_graph():
    graph_new = Graph()
    graph_new = bind_ifc_prefixes(graph_new)
    graph_new.bind("hil", Namespace("http://www.eas.iis.fraunhofer.de/hil#"))
    return graph_new

def generate_Pset_identifier(lineno, name):
    ilabel_pset = 'hil:IfcLabel_{num:0{width}}'.format(num=lineno, width=3)
    query_ident = """
        construct {
            """ + ilabel_pset + """ rdf:type  ifc:IfcLabel .
            """ + ilabel_pset + """ express:hasString  \"""" + name + """\" .
            }
        WHERE {}"""
    graph_new = dummy_graph().query(query_ident).graph
    return graph_new, ilabel_pset, lineno+1

def get_all_modelica_components(graph_mo, model_resource_string = 'hil:HIL.HIL_flat'):
    query_comps = """
                   SELECT DISTINCT ?comp ?ident ?mclass
                   WHERE {
                       """ + model_resource_string + """ moont:hasPart ?comp.
                       ?comp moont:identifier ?ident.
                       ?comp rdf:type ?mclass.
                       FILTER(?mclass != <http://w3id.org/moont#MComponent>).
                    } ORDER BY ASC(?comp)
                   """
    result_all_comps = graph_mo.query(query_comps)
    return result_all_comps

def get_all_comps_with_stringComment(graph_mo, model_resource_string = 'hil:HIL.HIL_flat'):
    query_comps = """
                   SELECT DISTINCT ?comp ?stringComment
                   WHERE {
                       """ + model_resource_string + """ moont:hasPart ?comp.
                       ?comp moont:identifier ?ident.
                       ?comp moont:stringComment ?stringComment.
                    } ORDER BY ASC(?comp)
                   """
    result_all_comps = graph_mo.query(query_comps)

    dict_sc = dict()
    for line in result_all_comps   :
        comp = line.get('comp')
        sc = line.get('stringComment')
        dict_sc[comp] = convert_Literal_to_string(sc)
    return dict_sc

def get_all_modelica_components_direct_modifications(graph_mo, model_resource_string="hil:HIL.HIL_flat"):
    query_comps = """
                   SELECT DISTINCT ?comp ?directmod
                   WHERE {
                    """ + model_resource_string + """ moont:hasPart ?comp.
                       ?comp moont:identifier ?ident.
                       ?comp moont:modification ?directmod
                    } ORDER BY ASC(?comp)
                   """
    result_all_comps = graph_mo.query(query_comps)

    dict_sc = dict()
    for line in result_all_comps   :
        comp = line.get('comp')
        sc = line.get('directmod')
        dict_sc[comp] = sc.replace('"', '\\\"')
    return dict_sc

def generate_pset(lineno, comp, ilabel_pset):
    """
    generates 4 STEP instances/lines
    :param lineno:
    :param comp:
    :param ilabel_pset:
    :return:
    """
    ipset = 'hil:IfcPropertySet_{num:0{width}}'.format(num=lineno, width=3)
    iguid = 'hil:IfcGloballyUniqueId_{num:0{width}}'.format(num=lineno + 1, width=3)
    irdbp = 'hil:IfcRelDefinesByProperties_{num:0{width}}'.format(num=lineno + 2, width=3)
    iguid2 = 'hil:IfcGloballyUniqueId_{num:0{width}}'.format(num=lineno + 3, width=3)
    guid = ifcopenshell.guid.new()
    guid2 = ifcopenshell.guid.new()
    query_pset = """
        construct {
            """ + irdbp + """   rdf:type  ifc:IfcRelDefinesByProperties .

            """ + irdbp + """ ifc:globalId_IfcRoot """ + iguid2 + """.
            """ + iguid2 + """ express:hasString  \"""" + guid2 + """\" .

            """ + irdbp + """ ifc:relatedObjects_IfcRelDefinesByProperties <""" + str(comp) + """>.
            """ + irdbp + """ ifc:relatingPropertyDefinition_IfcRelDefinesByProperties """ + ipset + """.

            """ + ipset + """   rdf:type  ifc:IfcPropertySet .
            """ + ipset + """ ifc:globalId_IfcRoot """ + iguid + """.
            """ + iguid + """ express:hasString  \"""" + guid + """\" .
            """ + ipset + """ ifc:name_IfcRoot """ + ilabel_pset + """.
            }
        WHERE {}"""
    graph_new = dummy_graph().query(query_pset).graph
    return graph_new, ipset, lineno + 4

def generate_property_in_pset(lineno, ipset, prop_identifier, prop_value):
    """
    generates 2 lines
    :param lineno:
    :param ipset:
    :param prop_identifier:
    :param prop_value:
    :return:
    """
    ipsvx = 'hil:IfcPropertySingleValue_{num:0{width}}'.format(num=lineno, width=3)
    prop_container = 'hil:IfcText_{num:0{width}}'.format(num=lineno + 1, width=3)
    query_prop = """
        construct {
            """ + ipsvx + """   rdf:type  ifc:IfcPropertySingleValue .
            """ + ipsvx + """   ifc:name_IfcProperty """ + prop_identifier + """ . 
            """ + ipsvx + """   ifc:nominalValue_IfcPropertySingleValue """ + prop_container + """ . 
            """ + prop_container + """     rdf:type  ifc:IfcText . 
            """ + prop_container + """   express:hasString \"""" + prop_value + """\" .

            """ + ipset + """ ifc:hasProperties_IfcPropertySet """ + ipsvx + """.
            }
        WHERE {}"""
    graph_new = dummy_graph().query(query_prop).graph
    return graph_new, lineno+2

def convert_resource_from_uri_to_string(mClass):
    trennstelle = mClass.rfind('#')
    if not trennstelle:
        trennstelle = mClass.rfind('/')
    mClass = mClass[trennstelle + 1:len(mClass)]
    return mClass

def convert_Literal_to_string(literalstring):
    #rdflib.term.Literal('XLW10BTR01', datatype=rdflib.term.URIRef('http://www.w3.org/2001/XMLSchema#string'))
    #Nominal     pressure     drop     of     fully     open     valve, used if CvData_gleich_AixLib.Fluid.Types.CvTypes.OpPoint
    trennstelle1 = literalstring.rfind('Literal(')
    trennstelle2 = literalstring.rfind(', datatype')
    if trennstelle1 != -1:
        literalstring = literalstring[trennstelle1 + 1: trennstelle2 - 9]
    return literalstring

def add_modelica_class_and_stringComment_as_property_to_objects(graph_mo, lineno, model_resource_string='hil:HIL.HIL_flat'):
    logging.info("adding Modelica meta info and parameters as properties to objects...")
    graph_1, ilabel_pset, lineno = generate_Pset_identifier(lineno, "Modelica Infos")
    graph_2, iident_class, lineno = generate_property_identifier(lineno, "Modelica Class")
    graph_3a, iident_sc, lineno = generate_property_identifier(lineno, "stringComment")
    graph_3b, iident_dm, lineno = generate_property_identifier(lineno, "modifiction")

    dict_sc = get_all_comps_with_stringComment(graph_mo, model_resource_string=model_resource_string)
    dict_dm = get_all_modelica_components_direct_modifications(graph_mo, model_resource_string)

    graph_ifc = graph_1 + graph_2 + graph_3a + graph_3b
    result_all_comps = get_all_modelica_components(graph_mo, model_resource_string)
    for line in result_all_comps:
        comp = line.get('comp')
        mClass = convert_resource_from_uri_to_string(line.get('mclass'))
        graph_4, ipset, lineno = generate_pset(lineno, comp, ilabel_pset)
        graph_5, lineno = generate_property_in_pset(lineno, ipset, iident_class, mClass)
        graph_ifc = graph_ifc + graph_4 + graph_5
        if comp in dict_sc:
            graph_6, lineno = generate_property_in_pset(lineno, ipset, iident_sc, dict_sc[comp])
            graph_ifc = graph_ifc + graph_6
        if comp in dict_dm:
            graph_7, lineno = generate_property_in_pset(lineno, ipset, iident_dm, dict_dm[comp])
            graph_ifc = graph_ifc + graph_7
    return graph_ifc, lineno




def add_modelica_info_as_properties_to_objects_2(graph_mo, lineno):
    logging.info("adding Modelica meta info as properties to objects...")
    graph_new, ilabel_pset = generate_Pset_identifier(lineno, graph_mo, "Modelica Infos")
    result_all_comps = get_all_modelica_components(graph_mo)
    for line in result_all_comps:
        comp = line.get('comp')
        graph_new, ipset = generate_pset(graph_mo, lineno, comp, ilabel_pset)
    #class
    add_modelica_class_and_stringComment_as_property_to_objects(graph_mo, lineno, ipset)
    #stringComment




def add_modelica_info_as_properties_to_objects(graph_mo, lineno, graph_ifc):
    logging.info("adding Modelica meta info as properties to objects...")
    iident_class = 'hil:IfcIdentifier_{num:0{width}}'.format(num=lineno, width=3)
    iident_comment = 'hil:IfcIdentifier_{num:0{width}}'.format(num=lineno+1, width=3)
    ilabel = 'hil:IfcLabel_{num:0{width}}'.format(num=lineno+2, width=3)
    query_1 = """
        construct {
            """ + iident_class + """ rdf:type ifc:IfcIdentifier.
            """ + iident_class + """ express:hasString  \"Modelica Class\" .
            """ + iident_comment + """ rdf:type ifc:IfcIdentifier.
            """ + iident_comment + """ express:hasString  \"comment\" .
            """ + ilabel + """ rdf:type  ifc:IfcLabel .
            """ + ilabel + """ express:hasString  \"Modelica Infos\" .
            }
        WHERE {}"""
    graph_ifc = graph_ifc + graph_mo.query(query_1).graph

    #TODO nicht jede Komponente hat einen StringComment
    query_comps = """
                   SELECT DISTINCT ?comp ?ident ?stringComment ?mclass
                   WHERE {
                       hil:HIL.HIL_flat moont:hasPart ?comp.
                       ?comp moont:identifier ?ident.
                       ?comp moont:stringComment ?stringComment.
                       ?comp rdf:type ?mclass.
                       FILTER(?mclass != <http://w3id.org/moont#MComponent>).
                    } ORDER BY ASC(?comp)
                   """

    result_all_comps = graph_mo.query(query_comps)
    lineno += 3
    for line in result_all_comps:
        comp = line.get('comp')
        ident = line.get('ident')
        stringComment = line.get('stringComment')
        mClass = line.get('mclass')
        trennstelle = mClass.rfind('#')
        if not trennstelle:
            trennstelle=mClass.rfind('/')
        mClass = mClass[trennstelle+1:len(mClass)]
    #    if mClass == "MComponent":
    #        continue     #Filter im SPARQL funktioniert nicht, daher Krücke hier
        ipset = 'hil:IfcPropertySet_{num:0{width}}'.format(num=lineno, width=3)
        iguid = 'hil:IfcGloballyUniqueId_{num:0{width}}'.format(num=lineno+1, width=3)
        ipsvx = 'hil:IfcPropertySingleValue_{num:0{width}}'.format(num=lineno+2, width=3)
        ipsvy = 'hil:IfcPropertySingleValue_{num:0{width}}'.format(num=lineno+3, width=3)
        wert_mclass = 'hil:IfcText_{num:0{width}}'.format(num=lineno+4, width=3)
        wert_stringCOmment = 'hil:IfcText_{num:0{width}}'.format(num=lineno+5, width=3)
        irdbp = 'hil:IfcRelDefinesByProperties_{num:0{width}}'.format(num=lineno+6, width=3)
        iguid2 = 'hil:IfcGloballyUniqueId_{num:0{width}}'.format(num=lineno + 7, width=3)
        lineno += 8
        guid = ifcopenshell.guid.new()
        guid2 = ifcopenshell.guid.new()

        query_2 = """
            construct {
                """ + irdbp + """   rdf:type  ifc:IfcRelDefinesByProperties .
                
                """ + irdbp + """ ifc:globalId_IfcRoot """ + iguid2 + """.
                """ + iguid2 + """ express:hasString  \"""" + guid2 + """\" .
                
                """ + irdbp + """ ifc:relatedObjects_IfcRelDefinesByProperties <""" + str(comp) + """>.
                """ + irdbp + """ ifc:relatingPropertyDefinition_IfcRelDefinesByProperties """ + ipset + """.
                
                """ + ipsvx + """   rdf:type  ifc:IfcPropertySingleValue .
                """ + ipsvx + """   ifc:name_IfcProperty """ + iident_class + """ . 
                """ + ipsvx + """   ifc:nominalValue_IfcPropertySingleValue """ + wert_mclass + """ . 
                """ + wert_mclass + """     rdf:type  ifc:IfcText . 
                """ + wert_mclass + """   express:hasString \"""" + mClass + """\" .
                
                """ + ipsvy + """   rdf:type  ifc:IfcPropertySingleValue .
                """ + ipsvy + """   ifc:name_IfcProperty """ + iident_comment + """ . 
                """ + ipsvy + """   ifc:nominalValue_IfcPropertySingleValue """ + wert_stringCOmment + """ . 
                """ + wert_stringCOmment + """     rdf:type  ifc:IfcText . 
                """ + wert_stringCOmment + """    express:hasString \"""" + stringComment + """\" .

                """ + ipset + """   rdf:type  ifc:IfcPropertySet .
                """ + ipset + """ ifc:globalId_IfcRoot """ + iguid + """.
                """ + iguid + """ express:hasString  \"""" + guid + """\" .
                """ + ipset + """ ifc:name_IfcRoot """ + ilabel + """.
                """ + ipset + """ ifc:hasProperties_IfcPropertySet """ + ipsvx + """.
                """ + ipset + """ ifc:hasProperties_IfcPropertySet """ + ipsvy + """.
                }
            WHERE {}"""
        graph_ifc = graph_ifc + graph_mo.query(query_2).graph
    return graph_ifc, lineno

def reduce_ports_and_their_classes(g_libs):
    query_1 = """
    construct {
        ?father_class moont:hasPart ?class_port .
        ?class_port moont:identifier ?comp_port_name.
        ?class_port rdf:type ?port_class.
        ?port_class moont:extends ?port_father_class.
        ?port_father_class rdfs:subClassOf moont:MConnector.
        }
    WHERE {
        ?father_class moont:hasPart ?class_port .
        ?class_port moont:identifier ?comp_port_name.
        ?class_port rdf:type ?port_class.
        ?port_class moont:extends* ?port_father_class.
        ?port_father_class rdfs:subClassOf moont:MConnector.
    } ORDER BY ASC(?comp_port)
    """
    res1 = g_libs.query(query_1)
    return res1.graph

def construct_only_extends(g_libs):
    query_2 = """
    construct {
        ?class moont:extends ?father_class.
        }
    WHERE {
        ?class moont:extends ?father_class.
    } ORDER BY ASC(?comp_port)
    """
    res2 = g_libs.query(query_2)
    return res2.graph


def construct_all_connectors(GD):
    """
    erzeugt für alle Konnektoren einen DistributionPort (nicht nur für die verbundenen), dafür ist aber Schemawissen nötig
    :param GD:
    :return:
    """
    # Ports
    # TODO nests ist noch nicht korrekt
    query_construct_allConnectors = """
    construct {
        ?comp_port rdf:type ifc:IfcDistributionPort.
        ?inst_comp ifc:nests ?comp_port.
        }
    WHERE {
        ?inst_comp moont:hasPart ?comp_port.
        ?comp_port moont:identifier ?comp_port_name.

        ?inst_comp a ?class.
        ?class moont:extends* ?father_class.
        ?father_class moont:hasPart ?class_port .
        ?class_port moont:identifier ?comp_port_name.

        ?port_father_class rdfs:subClassOf moont:MConnector.
        ?port_class moont:extends* ?port_father_class.
        ?class_port rdf:type ?port_class.
    } ORDER BY ASC(?comp_port)
    """

    query_construct_allConnectors = """
    SELECT ?class_port ?inst_comp ?comp_port
    WHERE {
        hil:HIL.HIL_flat moont:hasPart ?inst_comp.
        ?inst_comp moont:hasPart ?comp_port.
        ?comp_port moont:identifier ?comp_port_name.

        ?inst_comp a ?class.
        ?class moont:extends* ?father_class.
        ?father_class moont:hasPart ?class_port .
        ?class_port moont:identifier ?comp_port_name.
    } 
    """
    res4 = GD.query(query_construct_allConnectors)
    # TODO diese bisherige query bringt nur Ports, das dürfte aber ein Zufall sein?!, folgende Zeilen müssen eigentlich ergänzt werden
    # ?port_father_class rdfs: subClassOf moont: MConnector.
    # ?port_class moont: extends * ?port_father_class.
    # ?class_port rdf: type ?port_class.

    query_construct_allConnectors = """
    construct {
        ?comp_port rdf:type ifc:IfcDistributionPort.
        ?inst_comp ifc:nests ?comp_port.
        }
    WHERE {
        hil:HIL.HIL_flat moont:hasPart ?inst_comp.
        ?inst_comp moont:hasPart ?comp_port.
        ?comp_port moont:identifier ?comp_port_name.

        ?inst_comp a ?class.
        ?class moont:extends* ?father_class.
        ?father_class moont:hasPart ?class_port .
        ?class_port moont:identifier ?comp_port_name.
    } 
    """
    res4 = GD.query(query_construct_allConnectors)
    return res4.graph

def serialize_to_ttl(graph_ifcowl, target):
    logging.info("writing file " + target)
    graph_ifcowl.serialize(target, format='turtle')