"""
trying to solve CQ2 (timed out queries) with "construct"ing a new graph --> is working! (and performing much better)
constructed tripled are wrong - but sufficient to answer the query!
"""
query_CQ2b1 = """
construct {?alt rdf:type msl:Modelica.Blocks.Interfaces.RealOutput} where {
    ?alt moont:extends* ?class.
    ?class moont:hasPart ?comp .
    ?comp rdf:type msl:Modelica.Blocks.Interfaces.RealOutput.
    } ORDER BY ASC(?alt)
"""
query_CQ2b2 = """
construct {?alt rdf:type msl:Modelica.Blocks.Interfaces.RealInput} where {
    ?alt moont:extends* ?class.
    ?class moont:hasPart ?comp .
    ?comp rdf:type msl:Modelica.Blocks.Interfaces.RealInput.
    } ORDER BY ASC(?alt)
"""
query_CQ2b3 = """
construct {?alt moont:extends mbl:Buildings.Fluid.Interfaces.PartialFourPortInterface} where {
    ?alt moont:extends* mbl:Buildings.Fluid.Interfaces.PartialFourPortInterface.
    } ORDER BY ASC(?alt)
"""
query_CQ2b4 = """
construct {?alt moont:extends mbl:Buildings.Fluid.Interfaces.PartialFourPortInterface} where {
    ?alt moont:extends* aix:AixLib.Fluid.Interfaces.PartialFourPortInterface.
    } ORDER BY ASC(?alt)
"""

resc = GA.query(query_CQ2b1)
newGraph = resc.graph
resc = GA.query(query_CQ2b2)
newGraph2 = newGraph + resc.graph
resc = GA.query(query_CQ2b3)
newGraph3 = newGraph2 + resc.graph
resc = GA.query(query_CQ2b4)
newGraph4 = newGraph3 + resc.graph

newGraph3.parse('c:/_DATEN/WORKSPACES/IntelliJ/mo-x-ifc/src/test/java/output/ex_20221209_1438_fullclean.ttl', format='turtle')#to include prefixes
v = newGraph3.serialize(format="ttl") #to check

query_CQ2b = """
select ?alt where {
    ?alt moont:extends mbl:Buildings.Fluid.Interfaces.PartialFourPortInterface.
    ?alt rdf:type msl:Modelica.Blocks.Interfaces.RealInput.
    ?alt rdf:type msl:Modelica.Blocks.Interfaces.RealOutput.
    } ORDER BY ASC(?alt)
"""
res = newGraph3.query(query_CQ2b)
for row in res:
   print(row[0] )