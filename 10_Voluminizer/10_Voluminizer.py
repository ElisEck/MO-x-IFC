"""
amend 3D and 2D geometric representation to the IFC-File
input files: _F.ifc
output file(s):  _F_3D_2D.ifc
copied from: ifcscripting\mo-x-ifc\10_Voluminizer\10_Voluminizer.py
"""
import os
from PythonLib.IFC.Create import create_project_topology, attach_2Dgeometry_to_ifcfile
from PythonLib.IFC.GeometryCreator import create_dummy_geometry
from PythonLib.IFC.HelpersGeometry import find_dimensions_of_model
from PythonLib.IFC.HelpersPlot import logging_config
from PythonLib.IFC.IfcNetwork import IfcNetwork
from PythonLib.IFC.Change import *

#logging
path = "c:/_DATEN/WORKSPACES/IntelliJ/MO-x-IFC/examples/SIM2BIM/B_HIL/"
logging_config(path + "debug/", scriptname = os.path.basename(__file__)[:-3])

shortname = 'hil'
sourcefile = path + shortname + '_F.ifc'
target = path + shortname + '_F_3D_2D.ifc'

logging.info("reading file: " + sourcefile)
ifcfile = ifcopenshell.open(sourcefile)

ifc_dir_x, ifc_dir_z, context3D1, subcontext3D1, owner_history, project = create_project_topology(ifcfile, "hil")
container_project = ifcfile.createIfcRelAggregates(ifcopenshell.guid.new(), owner_history, "Project Container", None, project, ifcfile.by_type('IfcObject'))

logging.info("adding 3D dummy geometry")
elements = ifcfile.by_type('IfcElement') #don't use IfcProducts to omit IfcDistributionPorts
#define dimensions
min_x, max_x, min_y, max_y = find_dimensions_of_model(elements)
dim_x = (max_x - min_x ) / len(elements)
dim_y = (max_y - min_y ) / len(elements)
dim = float(round(min(dim_x, dim_y)))*2
for element in elements:
    xcoord, ycoord = change_placement_to_coordinates_in_properties(ifcfile, element, ifc_dir_x, ifc_dir_z)
    create_dummy_geometry(ifcfile, element, ifc_dir_x, ifc_dir_z, subcontext3D1, dim)
#TODO Zuordnung zu System

ig = IfcNetwork("merged", ifcfile, ifcfile)
logging.info("extracting graph from IFC")
ig.extract_graph_and_leafs_from_ifc_to_Ro()
logging.info("adding schematics to IFC")
attach_2Dgeometry_to_ifcfile(ig.graph_schematic, ig.ifcfile)
logging.info("writing file: " + target)
ig.ifcfile.write(target)

logging.info("finished succesfully")