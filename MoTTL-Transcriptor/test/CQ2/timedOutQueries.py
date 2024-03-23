# -*- coding: utf-8 -*-

"""
example queries as for CQ2 in my EC3-2023 paper
used in Python 3.8.13 [MSC v.1916 64 bit (AMD64)] on win32
"""
from rdflib import Graph
#files are located in this repo: https://gitlab.cc-asp.fraunhofer.de/eckstaedt/mo-x-ifc
GA = Graph()
#language level: 346 triple
GA.parse('c:/_DATEN/WORKSPACES/IntelliJ/mo-x-ifc/src/main/resources/ontologies/7_MoOnt/MoOnt.ttl', format='turtle')
#add Modelica Standard library: 278.000 triple
GA.parse('c:/_DATEN/WORKSPACES/IntelliJ/mo-x-ifc/src/main/resources/ontologies/8_ModelicaLibraries/MSL.ttl', format='turtle')
#add IBPSA libary level: 958.000 triple
GA.parse('c:/_DATEN/WORKSPACES/IntelliJ/mo-x-ifc/src/main/resources/ontologies/8_ModelicaLibraries/MBL.ttl', format='turtle')
GA.parse('c:/_DATEN/WORKSPACES/IntelliJ/mo-x-ifc/src/main/resources/ontologies/8_ModelicaLibraries/AixLib.ttl', format='turtle')
#add user defined library level: 1.032.000 triple
GA.parse('c:/_DATEN/WORKSPACES/IntelliJ/mo-x-ifc/src/main/resources/ontologies/8_ModelicaLibraries/LibEAS.ttl', format='turtle')
#add example: 1.033.000 triple
GA.parse('c:/_DATEN/WORKSPACES/IntelliJ/mo-x-ifc/src/test/java/output/ex_20221209_1438_fullclean.ttl', format='turtle')


# find class of all connected connectors in example by their identifier, without duplettes "MComponent" (expected result: 2xFluidPort_a, 2x FluidPort_b, RealiInput, Realoutput)
query_CQ2a = """
select ?class ?partA ?ident  where {
    ex:LBDCG_example.HeatPumpPlant_V2.heaPum rdf:type/moont:extends*/moont:hasPart ?partT .
    ?partT moont:identifier ?ident.
    ?partT a ?class.
    ex:LBDCG_example.HeatPumpPlant_V2.heaPum moont:hasPart ?partA .
    ?partA a moont:MConnectorComponent .
    ?partA moont:identifier ?ident.
    FILTER (?class != moont:MComponent)
    } ORDER BY ASC(?class)
"""

### one condition
#immediate result: 61 models
query_CQ2b1 = """
select DISTINCT ?alt where {
    ?alt moont:extends* mbl:Buildings.Fluid.Interfaces.PartialFourPortInterface.
    } ORDER BY ASC(?alt)
"""
# immediate result: 1528 models
query_CQ2b2 = """
select DISTINCT ?alt where {
    ?alt moont:extends* ?class.
    ?class moont:hasPart ?comp .
    ?comp rdf:type msl:Modelica.Blocks.Interfaces.RealOutput.
    } ORDER BY ASC(?alt)
"""
# immediate result: 1437 models
query_CQ2b3 = """
select DISTINCT ?alt where {
    ?alt moont:extends* ?class.
    ?class moont:hasPart ?comp .
    ?comp rdf:type msl:Modelica.Blocks.Interfaces.RealInput.
    } ORDER BY ASC(?alt)
"""

# same question, same result expected, but timed out after 5:30min - why is the propertyChain that slow? - check in other engines (Jena, ...) as well
query_CQ2b4 = """
select DISTINCT ?alt where {
    ?alt moont:extends*/moont:hasPart/rdf:type msl:Modelica.Blocks.Interfaces.RealOutput.
    } ORDER BY ASC(?alt)
"""

### two conditions
# (expected result: 44 lines, query takes 15s)
query_CQ2b5 = """
select ?alt ?partT1 where {
    ?alt moont:extends* mbl:Buildings.Fluid.Interfaces.PartialFourPortInterface.
    ?alt moont:extends*/moont:hasPart ?partT1 .
    ?partT1 a msl:Modelica.Blocks.Interfaces.RealInput .
    } ORDER BY ASC(?alt)
"""
# 4789 result after ~10min (but contains duplicates)
query_CQ2b6 = """
select ?alt ?partT1 ?partT2 where {
    ?alt moont:extends*/moont:hasPart ?partT2.
    ?partT2 a msl:Modelica.Blocks.Interfaces.RealOutput.
    ?alt moont:extends*/moont:hasPart ?partT1 .
    ?partT1 a msl:Modelica.Blocks.Interfaces.RealInput .
    } ORDER BY ASC(?alt)
"""

### three conditions
# timed out after 12 min
# new try: without property chains
query_CQ2b7 = """
select ?alt ?partT1 ?partT2 ?partT3 ?partT5 where {
    ?alt moont:extends* ?class.
    ?class moont:hasPart ?partT1 .
    ?partT1 a msl:Modelica.Blocks.Interfaces.RealInput .

    ?alt moont:extends* ?class2.
    ?class2 moont:hasPart ?partT2.
    ?partT2 a msl:Modelica.Blocks.Interfaces.RealOutput.

    ?alt moont:extends* mbl:Buildings.Fluid.Interfaces.PartialFourPortInterface.
    } ORDER BY ASC(?alt)
"""
### four conditions
# doesn't deliver within 12min
query_CQ2b8 = """
select ?alt ?partT1 ?partT2 ?partT3 ?partT5 where {
    ?alt moont:extends*/moont:hasPart ?partT1 .
    ?partT1 a msl:Modelica.Blocks.Interfaces.RealInput .

    ?alt moont:extends*/moont:hasPart ?partT2.
    ?partT2 a msl:Modelica.Blocks.Interfaces.RealOutput.

    ?alt moont:extends*/moont:hasPart ?partT3.
    ?partT3 a msl:Modelica.Fluid.Interfaces.FluidPort_a.

    ?alt moont:extends*/moont:hasPart ?partT5.
    ?partT5 a msl:Modelica.Fluid.Interfaces.FluidPort_b.
    } ORDER BY ASC(?alt)
"""

### all (six) conditions
# timed out after 10min
query_CQ2b9 = """
select ?alt ?partT1 ?partT2 ?partT3 ?partT5  where {
    ?alt moont:extends*/moont:hasPart ?partT1 .
    ?partT1 a msl:Modelica.Blocks.Interfaces.RealInput .

    ?alt moont: extends * / moont:hasPart ?partT2.
    ?partT2 a msl:Modelica.Blocks.Interfaces.RealOutput.

    ?alt moont: extends * / moont:hasPart ?partT3.
    ?partT3 a msl:Modelica.Fluid.Interfaces.FluidPort_a.

    ?alt moont: extends * / moont:hasPart ?partT4.
    ?partT4 a msl:Modelica.Fluid.Interfaces.FluidPort_a.

    ?alt moont: extends * / moont:hasPart ?partT5.
    ?partT5 a msl:Modelica.Fluid.Interfaces.FluidPort_b.

    ?alt moont: extends * / moont:hasPart ?partT6.
    ?partT6 a msl:Modelica.Fluid.Interfaces.FluidPort_b.

    FILTER (?partT3 != ?partT4).
    FILTER (?partT5 != ?partT6).
    } ORDER BY ASC(?alt)
"""


res = GA.query(query_CQ2b7)
for row in res:
    print(row[0] )
    print(row[0] + "\t" + row[1])
    print(row[0] + "\t" + row[1] + "\t" + row[2])
    print(row[0] + "\t" + row[1] + "\t" + row[2]+ "\t" + row[3]+ "\t" + row[4])



