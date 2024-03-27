from owlready2 import *
from rdflib import Graph
import logging

def read_ontology_from_ttl(filepath_model_ttl):
    """
    reads ttl-File with rdflib and returns it as owlready2 ontology
    :param filepath_model_ttl:
    :return: onto as owlready2 ontology
    """
    filepath_model_owl = filepath_model_ttl.split(".")[0] + ".owl"
    GA = Graph()
    logging.info("parsing file with rdflib: " + filepath_model_ttl)
    GA.parse(filepath_model_ttl, format='turtle')
    logging.info("...finished")
    v = GA.serialize(format="xml")
    text_file = open(filepath_model_owl, "w")
    logging.info("writing graph to owl format (temporarily necessary): " + filepath_model_owl)
    text_file.write(v)
    text_file.close()
    logging.info("...finished")
    logging.info("parsing file with owlready2: " + filepath_model_owl)
    onto = get_ontology(filepath_model_owl)
    onto.load()
    return onto

def read_ontologies_from_ttl(filepaths_models_ttl, name):
    filepath_model_owl = name + ".owl"
    GA = Graph()
    for filepath_model_ttl in filepaths_models_ttl:
        logging.info("parsing file with rdflib: " + filepath_model_ttl)
        GA.parse(filepath_model_ttl, format='turtle')
        #logging.info("...finished")
    v = GA.serialize(format="xml")
    text_file = open(filepath_model_owl, "w")
    logging.info("writing graph to owl format (temporarily necessary): " + filepath_model_owl)
    text_file.write(v)
    text_file.close()
    logging.info("...finished")
    logging.info("parsing file with owlready2: " + filepath_model_owl)
    onto = get_ontology(filepath_model_owl)
    onto.load()
    logging.info("...finished")
    return onto

def generate_combined_graph_file(filepaths_models_ttl, path, name):
    filepath_model_owl = path + name + ".owl"
    GA = Graph()
    for filepath_model_ttl in filepaths_models_ttl:
        logging.info("parsing file with rdflib: " + filepath_model_ttl)
        GA.parse(filepath_model_ttl, format='turtle')
        #logging.info("...finished")
    v = GA.serialize(format="xml")
    text_file = open(filepath_model_owl, "w")
    logging.info("writing graph to owl format: " + filepath_model_owl)
    text_file.write(v)
    text_file.close()

def save_ontology_to_ttl(onto, filepath_model_ttl):
    filepath_model_owl = filepath_model_ttl.split(".")[0] + ".owl"
    logging.info("saving owl file first: " + filepath_model_owl)
    onto.save(filepath_model_owl)
    logging.info("parsing file with rdflib")
    GA = Graph()
    GA.parse(filepath_model_owl, format='xml')
    #optional Namespaces, Präfixe festlegen, werden bei Serialisierung berücksichtigt
    #EX = Namespace("http://linkedbuildingdata.net/ifc/resources20230605_111920#")
    #GA.bind("ex", EX)
    #logging.info("serializing graph to ttl format")
    v = GA.serialize(format="ttl")
    text_file = open(filepath_model_ttl, "w")
    logging.info("writing graph to ttl format: " + filepath_model_ttl)
    text_file.write(v)
    text_file.close()

def copy_graph(original):
    logging.info("copying graph")
    GD = Graph()
    for t in original.triples((None, None, None)):
            GD.add(t)
    logging.info("finished copying")
    return GD

def print_all_triples(graph):
    for t in graph.triples((None, None, None)):
        print(t)