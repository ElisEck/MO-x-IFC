# MO-x-IFC

This repository contains tools for a bidirectional Modelica-IFC-Translation.

See conference paper: ["Bidirectional coupling of Building Information Modeling and Building Simulation using ontologies"](https://publica.fraunhofer.de/handle/publica/412499) EG-ICE 2021 

## MoTTL-Transcriptor v.0.9

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
* example outputs for common libraries (AixLib, BuildingSystems, Modelica Buildings, Modelica Standard Library) are contained in the folder: ./src/main/resources/ontologies/8_ModelicaLibraries
* an example input Modelica package is contained in the folder: ./src/test/resources/C_HeatPumpPlant

### Dependencies
Through maven this code depends primarily on:
* ANTLR 4.8

### Contact
* Elisabeth Eckstädt
* Fraunhofer Institute for Integrated Curcuits IIS
* Division Engineering of Adaptive Systems EAS
* elisabeth [dot] eckstaedt ( at ) eas [dot] iis [dot] f r a u n h o f e r [dot] de

### License and Copyright
* Software Copyright License for Academic Use of the Fraunhofer Software, Version 1.0, see details at [LICENSE](LICENSE)
* © 2021 Gesellschaft zur Förderung der angewandten Forschung e.V. acting on behalf of its Fraunhofer Institut für Integrierte Schaltungen, Institutsteil Entwicklung Adaptiver Systeme. All rights reserved.