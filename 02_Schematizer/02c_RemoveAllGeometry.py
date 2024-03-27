"""
removes 3D and 2D geometry to reduce filesize
input files: _F_3D_2D.ifc
output file(s):  _F.ifc
copied from: ifcscripting\mo-x-ifc\02_Schematizer\02d_RemoveAllGeometry.py
"""
import ifcopenshell
import logging
from PythonLib.IFC.HelpersPlot import logging_config
from PythonLib.IFC.Remove import set_of_unreferenced_instances, delete_all_IfcShapeRepresentations_of_IfcElement
import os

#logging
path = "../examples/BIM2SIM/A_Server/"
logging_config(path + "debug/", scriptname = os.path.basename(__file__)[:-3])

id = "Server"
source = path + "debug/" + id + "_F_3D_2D" + ".ifc"
target = path + id + "_F.ifc"

logging.info("reading file: " + source)
ifcfile = ifcopenshell.open(source)

logging.info("deleting IfcShapeRepresentation and their Items")
for element in ifcfile.by_type('IfcElement'):
    delete_all_IfcShapeRepresentations_of_IfcElement(ifcfile, element)

set_of_unreferenced_instances, set_of_referenced_instances = set_of_unreferenced_instances(ifcfile)
if len(set_of_unreferenced_instances) < len(set_of_referenced_instances):
    logging.info("removing " + str(len(set_of_unreferenced_instances)) + " unreferenced instances")
    i=0
    for unreferenced_id in set_of_unreferenced_instances:
        if i%1000==0:
            logging.info("run " + str(i))
        i=i+1
        ifcfile.remove(unreferenced_id)
    logging.info("writing file: " + target)
    ifcfile.write(target)
else:
    #if more unreferenced instances are present: copying the referenced ones to a new file instead of removing the unreferenced
    logging.info("copying " + str(len(set_of_referenced_instances)) + " referenced instances to a new file")
    g = ifcopenshell.file(schema=ifcfile.schema)
    i=0
    for referenced_instance in set_of_referenced_instances:
        if i%1000==0:
            logging.info("run " + str(i))
        i=i+1
        g.add(referenced_instance)
    logging.info("writing file: " + target)
    g.write(target)

logging.info("finished succesfully")