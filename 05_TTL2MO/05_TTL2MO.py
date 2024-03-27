"""
created a Modelica file in a Modelica package with
- a Modelica component for every component in the Mo-KG
- connections between Modelica components as given in the Mo-KG
input files: _mo.ttl
output file(s):  generated.mo
copied from: SemanticScripting\MO-x-IFC\05_TTL2MO.py
"""
import os
import shutil
from rdflib import Graph
import logging
from modelica_builder.model import Model
from PythonLib.IFC.HelpersPlot import logging_config
from PythonLib.ModelicaHelper import add_model_to_package, add_connection_2, insert_component_at_location_2

path = "../examples/BIM2SIM/A_Server/"
logging_config(path + "debug/", scriptname = os.path.basename(__file__)[:-3])

filename_original = "Server"

logging.info("read from TTL-File...")
#rdflib
GA = Graph()
GA.parse(path + filename_original + "_mo.ttl", format='turtle')

logging.info("copying Modelica master package")
package_name = "GeneratedModels"
try:
    shutil.copytree(package_name, path + package_name, symlinks=False, ignore=None, copy_function=shutil.copy2, ignore_dangling_symlinks=False, dirs_exist_ok=False)
except:
    logging.info("Modelica package could not be copied (maybe it is already present at target location?)")

logging.info("Creating Modelica Model")
model = Model("GeneratedModels/Master/Master.mo")

model.add_parameter('Modelica.Units.SI.MassFlowRate', 'mp_nom') #MSL4
model.add_parameter('Modelica.Units.SI.PressureDifference', 'dp_nom') #MSL4

q_components = """
PREFIX moont: <https://w3id.org/moont/>
select DISTINCT ?comp ?ident ?guid ?placem ?class where {
    ?comp a moont:MComponent.
    ?comp moont:identifier ?ident.
    ?comp moont:comment ?guid.
    ?comp moont:placement_origin ?placem.
    ?comp a ?class.
    FILTER(STRSTARTS(STR(?class), "https://w3id.org/aix/")) .
    } 
"""
componentlist = list(GA.query(q_components))

set_x = set()
set_y = set()
for component in componentlist:
    lower_left_corner = component[3].value[1:-1].rsplit(', ') #[x, y]
    set_x.add(float(lower_left_corner[0]))
    set_y.add(float(lower_left_corner[1]))
min_x = min(set_x)
min_y = min(set_y)
dict_old_to_new_placement = {}
for component in componentlist:
    old_placement = component[3].value
    lower_left_corner_old = component[3].value[1:-1].rsplit(', ')
    lower_left_corner_new = [int(float(lower_left_corner_old[0])-min_x), int(float(lower_left_corner_old[1])-min_y)] #TODO ins Raster einfügen, #TODO derzeit keine Rundung, sondern abschneiden der Kommastellen
    dict_old_to_new_placement[old_placement] = lower_left_corner_new

for component in componentlist:
    identifier = component[1].value.replace("(", "_").replace(")", "_")
    string_comment = component[0].rsplit('/')[-1] + " - " + component[2].value
    lower_left_corner = dict_old_to_new_placement[component[3].value]
    modelica_class = component[4].rsplit('https://w3id.org/aix/')[-1]
    modifications = {'Medium': 'Medium', 'm_flow_nominal': 'mp_nom', 'dp_nominal': 'dp_nom'}  # TODO nicht für alle die gleichen Modifications
    model = insert_component_at_location_2(model, modelica_class, identifier, string_comment, lower_left_corner, modifications)

query_ports = """
PREFIX moont: <https://w3id.org/moont/>
select DISTINCT ?oid1 ?pid1 ?pl1 ?oid2 ?pid2 ?pl2 where {
    ?port1 moont:connectedTo ?port2.
    ?obj1 moont:hasPart ?port1.
    ?obj2 moont:hasPart ?port2.
    ?port1 moont:identifier ?pid1.
    ?port2 moont:identifier ?pid2.
    ?obj1 moont:identifier ?oid1.
    ?obj2 moont:identifier ?oid2.
    ?obj1 moont:placement_origin ?pl1.
    ?obj2 moont:placement_origin ?pl2.
    } 
"""

connectionlist_rdflib = list(GA.query(query_ports))
for con in connectionlist_rdflib:
    component1 = con[0].value.replace("(", "_").replace(")", "_")
    component1_portname = con[1].value.replace("(", "_").replace(")", "_")
    center_1 = dict_old_to_new_placement[con[2].value] #TODO eigentlich ist das die lower left corner
    component2 = con[3].value.replace("(", "_").replace(")", "_")
    component2_portname = con[4].value.replace("(", "_").replace(")", "_")
    center_2 = dict_old_to_new_placement[con[5].value]
    model = add_connection_2(model, component1, component1_portname, component2, component2_portname, center_1, center_2)

#TODO variable name of the generated model - package.order needs to be adjusted
add_model_to_package(model, "generated", package_name, path + package_name + "/")

logging.info("finished succesfully")
