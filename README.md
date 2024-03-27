# MO-x-IFC

This repository contains tools for a bidirectional Modelica-IFC-Translation.

Concept presented at EG-ICE 2021: ["Bidirectional coupling of Building Information Modeling and Building Simulation using ontologies"](https://publica.fraunhofer.de/handle/publica/412499) 

to clone this repository (including submodules) use
```
git clone --recurse-submodules https://github.com/ElisEck/MO-x-IFC`
```
# Converters
## Schematizer (converter 02)
## IFC2RDF (converter 03)
this is a copy of the used binary from https://github.com/pipauwel/IFCtoRDF
## SemTran (converter 04)
## TTL2MO (converter 05)
## MoTTL-Transcriptor v.0.9 (converter 07)

This component transcribes Modelica packages to knowledge graphs. It was developed as a Java tool and is delivered as a *.jar.

### How to use it
* download the executable *.jar from XXX
* it contains all dependencies and can be run out of the box
* it can be executed on a JVM version 11 (or higher)
* run the following command to generate a turtle file representing some Modelica package (adapt the paths to your files)
```
java -Dpolyglot.engine.WarnInterpreterOnly=false -jar c:\MoTTL-Transcriptor-0.9-SNAPSHOT-jar-with-dependencies.jar -p "ex" -n "LBDCG_example" -t "knowledge graph representing the Modelica LBDCG_example package" -v "v1.0.1-1.0.0" -o "C:\output\path" --inputPath "c:\LBDCG_example"
```
you need to provided six parameters:
* inputPath i: path to directory containing the input package (package.mo, package.order) e.g. c:\\LibEAS (without trailing slash), use quotes if whitespaces contained 
* outputPath o: path to directory where the turtle file should be placed e.g. c:\\TMP (without trailing slash), use quotes if whitespaces contained
* prefix p: ontology prefix e.g. \"mbl\" (without colon and quotes)
* ontologyLongName n: e.g. \"Buildings\" (no quotes necessary)
* ontologyTitle t: e.g. Modelica Buildings Library (v8.0.0) ontology
* ontologyVersion v: combined version number of Modelica package and the ontology v8.0.0-1.0.0

### Examples
* example outputs (knowledge graphs in ttl serialization) for common libraries (AixLib, BuildingSystems, Modelica Buildings, Modelica Standard Library) are contained in the folder: ./src/main/resources/ontologies/8_ModelicaLibraries
* their native Modelica files are available on their respective github repositories
* an example input Modelica package is contained in the folder: ./src/test/resources/C_HeatPumpPlant
* the corresponding output is contained in the file: ./src/test/java/output/ex_20221215_1154_fullclean.ttl

### Dependencies
Through maven this code depends primarily on:
* ANTLR 4.8

### sources belonging to the EC3 paper
presented at EC3 2023 ["Representing Modelica Models as Knowledge Graphs Using The MoOnt Ontology"](https://doi.org/10.35490/EC3.2023.173) 
* MoOnt definition is given in this file: ./src/main/resources/ontologies/7_MoOnt/MoOnt.ttl
* the heat pump plant example (native Modelica Code) is given here: ./src/test/resources/C_HeatPumpPlant
* the corresponding output (knowledge graph in ttl serialization) is contained in the file: ./src/test/java/output/ex_20221215_1154_fullclean.ttl
* knowledge graphs in ttl serialization for common Modelica libraries (AixLib, BuildingSystems, Modelica Buildings, Modelica Standard Library) are contained in the folder: ./src/main/resources/ontologies/8_ModelicaLibraries
* python code to execute the queries described in section "Discussion and Result Analysis – Answering Competency Questions" are given here: ./test/EC3example_SPARQLqueries.py
* as discussed in the paper there are some implementation issues with CQ2, these are shown here: ./test/CQ2
* the outputs of these scripts are shown here (only excerpts are shown in the paper wrt page limitations): ./test/EC3example_SPARQLqueries_outputs.txt

## ProTran (converter 08)

## RDF2IFC v.0.9 (converter 09)
see https://github.com/ElisEck/RDF2IFC

## Voluminizer (converter 10)

# Contact
* Elisabeth Eckstädt
* Fraunhofer Institute for Integrated Curcuits IIS
* Division Engineering of Adaptive Systems EAS
* elisabeth [dot] eckstaedt ( at ) eas [dot] iis [dot] f r a u n h o f e r [dot] de

# License and Copyright
* Software Copyright License for Academic Use of the Fraunhofer Software, Version 1.0, see details at [LICENSE](MoTTL-Transcriptor/LICENSE)
* © 2021 Gesellschaft zur Förderung der angewandten Forschung e.V. acting on behalf of its Fraunhofer Institut für Integrierte Schaltungen, Institutsteil Entwicklung Adaptiver Systeme. All rights reserved.