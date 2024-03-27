"""
combines the knowledge graph (KG) of the instance model with the KGs of IFC data schema and IFC-AixLib-Alignment --> serialize "_combined.ttl"
performs reasoning, new triples are saved to KG "moinst_python" --> serialize _classes_mo.owl/ttl
amend KG with triple for data type properties that cannot be reasoned: position of components, connection of components --> serialize as ttl and owl
input files: _ifc.ttl
output file(s):  _mo.ttl
copied from: SemanticScripting\MO-x-IFC\04_SemanticTranslator.py
"""
from owlready2 import *
import logging
from PythonLib.IFC.HelpersPlot import logging_config
from PythonLib.SemanticHelper import generate_combined_graph_file, save_ontology_to_ttl
from PythonLib.TranslationHelper import insert_triples_port_description, insert_triples_port_connections, insert_triples_components, insert_triples_components_placement, insert_triples_header, insert_triples_components_class_assignment, get_sparql_header

#logging
path = "../examples/BIM2SIM/A_Server/"
logging_config(path + "debug/", scriptname = os.path.basename(__file__)[:-3])
id = "Server"

#TODO Vorbereitung automatisieren: der Header vom IFC-KG muss vorher angepasst werden (sowohl die prefixes, als auch die allerersten Triple)
filepaths_models_ttl = [
    path + id + "_ifc.ttl",
    "IFC4_ADD2_TC1_C20.ttl",
    "A26.ttl", #TODO prüfen Temperatursensor versehentlich auf PRESSUREGAUGE gemappt?
    ]
logging.info("read from TTL-Files, generate joint OWL-Files...")
filename_combined_graph = "debug/" + id + "_combined"
generate_combined_graph_file(filepaths_models_ttl, path, filename_combined_graph )

logging.info("loading joint owl file: " + path + filename_combined_graph + ".owl")
onto = get_ontology(path + filename_combined_graph + ".owl")
onto.load()
moinst_python = get_ontology("http://eas.iis.fraunhofer.de/testmodelicapython/")

sparql_header = get_sparql_header()

logging.info("synchronizing reasoner")
with moinst_python:    sync_reasoner_pellet(infer_property_values = True) #Pellet - finishes within 1min
logging.info("finished synchronizing reasoner")

logging.info("saving to 2 files file: " + path + id + "_classes_mo.owl/ttl")
moinst_python.save(path + "debug/" +id + "_classes_mo.owl")
save_ontology_to_ttl(moinst_python, path + "debug/" + id + "_classes_mo.ttl")

logging.info("reducing graph to interesting triples and enhancing DataTypeProperties")
with moinst_python: #Kommentare geben an: Anzahl Triple in moinst_python und default_world danach
    insert_triples_header(default_world, sparql_header)
    insert_triples_components(default_world, sparql_header)
    insert_triples_components_placement(default_world, sparql_header)
    insert_triples_components_class_assignment(default_world, sparql_header)
    insert_triples_port_description(default_world, sparql_header)               #1926   45429
    insert_triples_port_connections(default_world, sparql_header)               #2040   45543 #+114

logging.info("saving to 2 files: " + path + id + "_mo.owl/ttl")
moinst_python.save(path + "debug/" +id + "_mo.owl")
save_ontology_to_ttl(moinst_python, path + id + "_mo.ttl")

logging.info("finished succesfully")
