"""
translates the MO-KG (moont-graph) to IFC-KG (ifc-owl graph) using SPARQL CONSTRUCT queries
input files: _mo.ttl
output file(s):  _ifc.ttl
copied from: SemanticScripting\MO-x-IFC\08_ProceduralTranslator.py
"""
import logging
import os
from rdflib import Namespace, Graph
from PythonLib.HelperProceduralTranslation import generate_IfcRelConnectsPorts, generate_IfcRelNests_for_ports, generate_instance_objects, add_placement_properties_to_objects, bind_ifc_prefixes, add_metadata_to_graph, add_modelica_class_and_stringComment_as_property_to_objects
from PythonLib.IFC.HelpersPlot import logging_config

path = "../examples/SIM2BIM/D_HIL/"
logging_config(path + "debug/", scriptname = os.path.basename(__file__)[:-3])

shortname = 'hil'
mrs= "hil:HIL.HIL_flat"

logging.info("parsing (enhanced) instance model graph...")
graph_mo = Graph()
graph_mo.parse(path + shortname + '_mo.ttl', format='turtle') #0.2MB
graph_mo = bind_ifc_prefixes(graph_mo) #restliche Bindings für hil, moont, Modelica-Libs und Standardontologien (rdf, owl, ...) bereits im Inputfile vorhanden

graph_ifcowl, lineno = generate_instance_objects(graph_mo, lineno=1, model_resource_string=mrs, prefix=shortname)
#graph_ifcowl.add((URIRef("http://www.eas.iis.fraunhofer.de/bipl#"), OWL.imports, URIRef("https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL#")))
graph_ifcowl = add_metadata_to_graph(graph_ifcowl, path + shortname + '_mo.ttl')

graph_ifcowl, lineno = add_placement_properties_to_objects(graph_mo, lineno + 1, graph_ifcowl, model_resource_string=mrs, prefix=shortname)

graph_ifcowl3, lineno = generate_IfcRelConnectsPorts(graph_mo, lineno, prefix=shortname)
graph_ifcowl = graph_ifcowl + graph_ifcowl3

graph_ifcowl4, lineno = generate_IfcRelNests_for_ports(graph_mo, lineno, prefix=shortname)
graph_ifcowl = graph_ifcowl + graph_ifcowl4

graph_ifcowl5, lineno = add_modelica_class_and_stringComment_as_property_to_objects(graph_mo, lineno + 1, model_resource_string=mrs)
graph_ifcowl = graph_ifcowl + graph_ifcowl5

#Binding of prefixes for better readability of ttl file
graph_ifcowl = bind_ifc_prefixes(graph_ifcowl)
#graph_ifcowl.bind("hil", Namespace("http://www.eas.iis.fraunhofer.de/hil#"))
graph_ifcowl.bind(shortname, Namespace("http://www.eas.iis.fraunhofer.de/" + shortname + "#"))

target = path + shortname + "_ifc.ttl"
logging.info("writing file " + target)
graph_ifcowl.serialize(target, format='turtle')

logging.info("finished succesfully")