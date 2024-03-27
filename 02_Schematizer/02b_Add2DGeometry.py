"""
add a 2D geometric representation for the schematic graph
input files: _F_3D.ifc
output file(s):  _F_3D_2D.ifc
copied from: ifcscripting\mo-x-ifc\02_Schematizer\02c_Add2DGeometry.py
"""
import os
import ifcopenshell
import logging
from PythonLib.IFC.Create import attach_2Dgeometry_to_ifcfile
from PythonLib.IFC.HelpersPlot import logging_config
from PythonLib.IFC.IfcNetwork import IfcNetwork

path = "C:/_DATEN/WORKSPACES/IntelliJ/MO-x-IFC/examples/BIM2SIM/A_Server/debug/"
logging_config(path, scriptname = os.path.basename(__file__)[:-3])

dict_systems = {
    'Server': ['KK3_Server_VL','KK3_Server_RL','KK3_Server'],
 #   'RLT': ['KK4_RLT_VL','KK4_RLT_RL','KK4_RLT'],
 #   'ULK': ['KK2_ULK_VL','KK2_ULK_RL','KK2_ULK'],
 #   'Rueckkuehlung': ['KK_Rueckkuehlung_VL','KK_Rueckkuehlung_RL','KK_Rueckkuehlung'],
 #   'Erzeugung': ['KK_Erzeugung_VL','KK_Erzeugung_RL','KK_Erzeugung'],
 #   'BKA': ['KK1_BKA_VL','KK1_BKA_RL','KK1_BKA'],
}

for system in dict_systems:
    logging.info("processing system: " + system)
    logging.info("reading file: " + path + system + "_F_3D.ifc")
    ifcfile = ifcopenshell.open(path + system + "_F_3D.ifc")

    ig = IfcNetwork(id, ifcfile, ifcfile)
    logging.info("extracting graph (schematic) from IFC")
    ig.extract_graph_schematic()

    attach_2Dgeometry_to_ifcfile(ig.graph_schematic, ifcfile)

    logging.info("writing file: " + path + "debug/" + system + "_F_3D_2D.ifc")
    ifcfile.write(path + "debug/" + system + "_F_3D_2D.ifc")

logging.info("finished succesfully")

