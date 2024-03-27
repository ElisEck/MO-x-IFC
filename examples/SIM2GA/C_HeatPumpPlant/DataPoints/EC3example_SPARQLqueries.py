# -*- coding: utf-8 -*-

"""
example queries as given in my EC3-2023 paper
used in Python 3.8.13 [MSC v.1916 64 bit (AMD64)] on win32
"""

from rdflib import Graph

__author__ = 'Elisabeth Eckstädt'
__copyright__ = 'Copyright 2022, Fraunhofer IIS/EAS'
__credits__ = '{FMI4BIM FKZ:03ET1603A, iECO FKZ: 68GX21011D}'
__license__ = '{license}'
__version__ = '1.0.0'
__email__ = '{elisabeth.eckstaedt ( a t ) eas.iis.fraunhofer.de}'

GA = Graph()
#language level
GA.parse('c:/_DATEN/WORKSPACES/IntelliJ/mo-x-ifc/src/main/resources/ontologies/7_MoOnt/MoOnt.ttl', format='turtle')
#add Modelica Standard library
GA.parse('c:/_DATEN/WORKSPACES/IntelliJ/mo-x-ifc/src/main/resources/ontologies/8_ModelicaLibraries/MSL.ttl', format='turtle')
#add IBPSA libary level
GA.parse('c:/_DATEN/WORKSPACES/IntelliJ/mo-x-ifc/src/main/resources/ontologies/8_ModelicaLibraries/MBL.ttl', format='turtle')
GA.parse('c:/_DATEN/WORKSPACES/IntelliJ/mo-x-ifc/src/main/resources/ontologies/8_ModelicaLibraries/AixLib.ttl', format='turtle')
#add user defined library level
GA.parse('c:/_DATEN/WORKSPACES/IntelliJ/mo-x-ifc/src/main/resources/ontologies/8_ModelicaLibraries/LibEAS.ttl', format='turtle')
#add example
GA.parse('c:/_DATEN/WORKSPACES/IntelliJ/mo-x-ifc/src/test/java/output/ex_20221209_1438_fullclean.ttl', format='turtle')
#parsing all graphs takes ~2min on a standard desktop machine

# all models that contain the Carnot_y-HeatPump (expected result: 3 models, result within seconds)
query_CQ1a = """
select ?model where {
    ?model moont:hasPart ?comp .
    ?comp a mbl:Buildings.Fluid.HeatPumps.Carnot_y.
    } ORDER BY ASC(?subj)

"""

# all executable models containing an instance of a "PartialFourPortInterface" (expected result: 98  models)
query_CQ1b = """
select DISTINCT ?class ?model where {
    ?model moont:hasPart ?comp .
    ?model moont:extends msl:Modelica.Icons.Example .
    ?comp a ?class .
    ?class moont:extends* mbl:Buildings.Fluid.Interfaces.PartialFourPortInterface .
    } ORDER BY ASC(?class)
"""

# all packages that contain executable models containing PartialFourPortInterface (expected result: 29 packages, query takes ~30s)
query_CQ1c = """
select DISTINCT ?package where {
    ?model moont:hasPart ?comp .
    ?model moont:extends msl:Modelica.Icons.Example .
    ?comp a ?class .
    ?class moont:extends* mbl:Buildings.Fluid.Interfaces.PartialFourPortInterface .
    ?model moont:containedIn ?package.
    } ORDER BY ASC(?package)
"""


# find parameters of components and the executable model itself
query_CQ3 = """
select ?X ?mod ?comment where {
    {
        ex:LBDCG_example.HeatPumpPlant_V2 moont:hasPart ?comp .
        ?comp rdf:type/moont:extends*/moont:hasPart ?partT .
        ?partT moont:identifier ?ident.
        ?partT a moont:MParameterComponent.
        {?partT moont:type msl:Modelica.SIunits.Power.} UNION {?partT moont:type msl:Modelica.SIunits.HeatFlowRate.}
        ?comp moont:hasPart ?X .
        ?X moont:identifier ?ident.
        ?X moont:modification ?mod.
        OPTIONAL {?partT moont:stringComment ?comment.}.
    } UNION {
        ex:LBDCG_example.HeatPumpPlant_V2 moont:hasPart ?X .
        {?X moont:type msl:Modelica.SIunits.Power.} UNION {?X moont:type msl:Modelica.SIunits.HeatFlowRate.}
        ?X moont:identifier ?ident.
        ?X moont:modification ?mod.
        OPTIONAL {?X moont:stringComment ?comment.}.
    }
     FILTER ( datatype(?mod) = xsd:Real) 
    } 
"""

#find only connected Real In/Outputs (expected result:
query_CQ4 = """
select ?comp ?ident ?class where {
    ex:LBDCG_example.HeatPumpPlant_V2 moont:hasPart ?comp .
    ?comp rdf:type/moont:extends*/moont:hasPart ?partT .
    ?partT moont:identifier ?ident.
    ?partT a ?class.
    ?comp moont:hasPart ?partA .
    ?partA a moont:MConnectorComponent .
    ?partA moont:identifier ?ident.
    FILTER (?class = msl:Modelica.Blocks.Interfaces.RealOutput || ?class = msl:Modelica.Blocks.Interfaces.RealInput)
    } ORDER BY ASC(?comp)
"""

### execute queries and print result
res = GA.query(query_CQ1a) #insert respective query
#adjust to no of variables in query
for row in res:
    print(row[0] )
    print(row[0] + "\t" + row[1])
    print(row[0] + "\t" + row[1] + "\t" + row[2])
    print(row[0] + "\t" + row[1] + "\t" + row[2]+ "\t" + row[3]+ "\t" + row[4])
