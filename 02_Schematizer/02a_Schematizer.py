"""
calculated the schematic graph for the complete (construction) graph in the ifc file
input files: _K.ifc
output file(s):  _K_F_3D.ifc and _F_3D.ifc
copied from: ifcscripting\mo-x-ifc\02_Schematizer\02_Schematizer.py
"""
import ifcopenshell
import logging
import networkx as nx
from PythonLib.IFC.Create import create_IfcOwnerHistory
from PythonLib.IFC.HelpersGraph import graph_characteristics_as_string
from PythonLib.IFC.HelpersPlot import logging_config
from PythonLib.IFC.IfcNetwork import IfcNetwork, port_names_for_new_ports
import os

#logging
path = "../examples/BIM2SIM/A_Server/"
logging_config(path + "debug/", scriptname = os.path.basename(__file__)[:-3])

id = "Server"
source = path + id + "_K.ifc"

logging.info("reading file: " + source)
ifcfile = ifcopenshell.open(source)
ig = IfcNetwork(id, ifcfile, ifcfile)
logging.info("extracting graph from IFC")
ig.extract_graph_construction()
logging.info("_____graph construction: " + graph_characteristics_as_string(ig.graph_construction))

logging.info("reducing graph to schematic")
ig.generate_graph_schematic()

logging.info("adding replacements to IFC (including a group for each replacement)")
ifcfile, oh, personAndOrganization, application = create_IfcOwnerHistory(ifcfile)
ig.add_replacements_to_ifcfile_with_properties(ig.graph_schematic, oh)

logging.info("writing new edges to IFC")
ig.add_new_edges_to_ifcfile(ig.graph_schematic, nx.Graph(), "replacement graph Ro", oh)
logging.info("_____graph schematic: " + graph_characteristics_as_string(ig.graph_schematic))

logging.info("giving port names")
port_names_for_new_ports(ifcfile, ig.graph_schematic)

logging.info("exporting figures comparing construction and schematic graph")
ig.figures_systemwise_compare_A_S(ig.graph_schematic, ig.graph_construction, "", path=path+"debug/")

target_K_F = path + "debug/" + id + "_K_F_3D.ifc"
logging.info("writing file: " + target_K_F)
ifcfile.write(target_K_F)

logging.info("deleting replacees, expect a runtime of 1s per replacee")
ig.delete_replacees()
target_F = path + "debug/" + id + "_F_3D.ifc"
logging.info("writing file: " + target_F)
ifcfile.write(target_F)

logging.info("finished succesfully")