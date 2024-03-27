"""
trying to solve CQ2 (timed out queries) with other triplestore --> not working
"""
#pip install oxrdflib
from rdflib import Graph
from oxrdflib import *
GB = Graph(store="Oxigraph")
#language level
GB.parse('c:/_DATEN/WORKSPACES/IntelliJ/mo-x-ifc/src/main/resources/ontologies/7_MoOnt/MoOnt.ttl', format='turtle')
#add Modelica Standard library
GB.parse('c:/_DATEN/WORKSPACES/IntelliJ/mo-x-ifc/src/main/resources/ontologies/8_ModelicaLibraries/MSL.ttl', format='turtle')
#add IBPSA libary level
GB.parse('c:/_DATEN/WORKSPACES/IntelliJ/mo-x-ifc/src/main/resources/ontologies/8_ModelicaLibraries/MBL.ttl', format='turtle')
GB.parse('c:/_DATEN/WORKSPACES/IntelliJ/mo-x-ifc/src/main/resources/ontologies/8_ModelicaLibraries/AixLib.ttl', format='turtle')
#add user defined library level
#GA.parse('c:/_DATEN/WORKSPACES/IntelliJ/mo-x-ifc/src/main/resources/ontologies/8_ModelicaLibraries/LibEAS.ttl', format='turtle')
GB.parse('c:/_DATEN/WORKSPACES/IntelliJ/mo-x-ifc/src/test/java/output/libeas_20221209_1438_fullclean.ttl', format='turtle')
#add example
GB.parse('c:/_DATEN/WORKSPACES/IntelliJ/mo-x-ifc/src/test/java/output/ex_20221209_1438_fullclean.ttl', format='turtle')
#parsing all graphs takes ~2min on a standard desktop machine

