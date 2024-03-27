"""
functions create (non geometric) instances in IFC-files
- function to create a minimal IFC file from scratch
- ...
"""
import tempfile
import time
import ifcopenshell
import ifcopenshell.api
import ifcopenshell.guid
import ifcopenshell.util.placement
import logging
import networkx as nx
import numpy as np
from PythonLib.IFC.HelpersGeometry import rotate_local_coordinates_in_global_coordinates
from PythonLib.IFC.IfcNetwork import get_or_generate_layout


def create_IfcFile_with_minimal_header(filename, timestring, creator, organization, application, application_version, project_globalid, project_name):
    """
    :param filename:
    :param timestring:
    :param creator:
    :param organization:
    :param application:
    :param application_version:
    :param project_globalid:
    :param project_name:
    :return:
    """
    header_short = """ISO-10303-21;
    HEADER;
    FILE_DESCRIPTION(('ViewDefinition [CoordinationView]'),'2;1');
    FILE_NAME('%(filename)s','%(timestring)s',('%(creator)s'),('%(organization)s'),'%(application)s','%(application)s','');
    FILE_SCHEMA(('IFC4'));
    ENDSEC;
    DATA;
    #12=IFCDIMENSIONALEXPONENTS(0,0,0,0,0,0,0);
    #13=IFCSIUNIT(*,.LENGTHUNIT.,$,.METRE.);
    #14=IFCSIUNIT(*,.AREAUNIT.,$,.SQUARE_METRE.);
    #15=IFCSIUNIT(*,.VOLUMEUNIT.,$,.CUBIC_METRE.);
    #16=IFCSIUNIT(*,.PLANEANGLEUNIT.,$,.RADIAN.);
    #17=IFCMEASUREWITHUNIT(IFCPLANEANGLEMEASURE(0.017453292519943295),#16);
    #18=IFCCONVERSIONBASEDUNIT(#12,.PLANEANGLEUNIT.,'DEGREE',#17);
    #19=IFCUNITASSIGNMENT((#13,#14,#15,#18));
    ENDSEC;
    END-ISO-10303-21;
    """ % locals()
    # Write the template to a temporary file
    temp_handle, temp_filename = tempfile.mkstemp(suffix=".ifc")
    with open(temp_filename, "wb") as f:
        # f.write(template)
        bytes_obj = bytes(header_short, "UTF-8")  # zunächst umwandeln in byted-object
        f.write(bytes_obj)
    return ifcopenshell.open(temp_filename)


def create_IfcOwnerHistory(ifcfile, creatorS = "Elisabeth Eckstaedt", organizationS = "Fraunhofer IIS/EAS"):
    """
    :param ifcfile:
    :return: ifcfile with owner_history and dependent entities added
    """
    applicationS = "IfcOpenShell"
    application_version = "0.6.0b0" #TODO aus Python-Umgebung rausziehen
    person = ifcfile.createIfcPerson(None, None, creatorS, None, None, None, None, None)
    organization = ifcfile.createIfcOrganization(None, organizationS, None, None, None)
    personAndOrganization = ifcfile.createIfcPersonAndOrganization(person, organization, None)
    application = ifcfile.createIfcApplication(organization, application_version, applicationS)
    owner_history = ifcfile.createIfcOwnerHistory(personAndOrganization, application, None, "ADDED", None, personAndOrganization, application, int(time.time()))  # timestamp in full seconds
    return ifcfile, owner_history, personAndOrganization, application

"""
def create_IfcOwnerHistory(ifcfile):
    EAS=ifcfile.create_entity('IfcOrganization', Name='Fraunhofer IIS/EAS')
    EE=ifcfile.create_entity('IfcPerson', GivenName='Elisabeth Eckstädt')
    data = {
        'OwningUser': ifcfile.create_entity('IfcPersonAndOrganization', EE, EAS),
        'OwningApplication': ifcfile.create_entity('IfcApplication', EAS, 'version0', 'EAS Skript via ifcopenshell', 'FMI4BIM_Identifier'),
        'CreationDate': int((datetime.datetime.now() - datetime.datetime(1970, 1, 1)).total_seconds())
        ##TODO restliche Pflichparameter ergänzen
    }
    return ifcfile.create_entity('IfcOwnerHistory', **data)
"""
def create_IfcElement_based_on_class_name(ifcfile, classname, guid, owner_history, name, description, object_type, object_placement, representation, tag):
    """
    directly changes the given ifcfile
    """
    if classname == "IfcUnitaryEquipment":
        new_element = ifcfile.createIfcUnitaryEquipment(guid, owner_history, name, description, object_type, object_placement, representation, tag, None)
    elif classname == "IfcHeatExchanger":
        new_element = ifcfile.createIfcHeatExchanger(guid, owner_history, name, description, object_type, object_placement, representation, tag, None)
    elif classname == "IfcTank":
        new_element = ifcfile.createIfcTank(guid, owner_history, name, description, object_type, object_placement, representation, tag, None)
    elif classname == "IfcSensor":
        new_element = ifcfile.createIfcSensor(guid, owner_history, name, description, object_type, object_placement, representation, tag, None)
    elif classname == "IfcPipeFitting":
        new_element = ifcfile.createIfcPipeFitting(guid, owner_history, name, description, object_type, object_placement, representation, tag, None)
    elif classname == "IfcChiller":
        new_element = ifcfile.createIfcChiller(guid, owner_history, name, description, object_type, object_placement, representation, tag, None)
    elif classname == "IfcCoil":
        new_element = ifcfile.createIfcCoil(guid, owner_history, name, description, object_type, object_placement, representation, tag, None)
    elif classname == "IfcFlowInstrument":
        new_element = ifcfile.createIfcFlowInstrument(guid, owner_history, name, description, object_type, object_placement, representation, tag, None)
    elif classname == "IfcValve":
        new_element = ifcfile.createIfcValve(guid, owner_history, name, description, object_type, object_placement, representation, tag, None)
    elif classname == "IfcPipeFitting":
        new_element = ifcfile.createIfcPipeFitting(guid, owner_history, name, description, object_type, object_placement, representation, tag, None)
    elif classname == "IfcDuctFitting":
        new_element = ifcfile.createIfcDuctFitting(guid, owner_history, name, description, object_type, object_placement, representation, tag, None)
    elif classname == "IfcPipeSegment":
        new_element = ifcfile.createIfcPipeSegment(guid, owner_history, name, description, object_type, object_placement, representation, tag, None)
    elif classname == "IfcDuctSegment":
        new_element = ifcfile.createIfcDuctSegment(guid, owner_history, name, description, object_type, object_placement, representation, tag, None)
    elif classname == "IfcPump":
        new_element = ifcfile.createIfcPump(guid, owner_history, name, description, object_type, object_placement, representation, tag, None)
    elif classname == "IfcFilter":
        new_element = ifcfile.createIfcFilter(guid, owner_history, name, description, object_type, object_placement, representation, tag, None)
    else:
        new_element = ifcfile.createIfcBuildingElementProxy(guid, owner_history, name, description, object_type, object_placement, representation, tag, None)
        logging.info("Klassenname unbekannt, daher BuildingElementProxy erzeugt: " + classname)
    return new_element

def create_project_topology(ifcfile, projectname):
    """
    units, IfcOwnerHistory, 3D IfcGeometricRepresentationContext, IfcProject
    :param ifcfile:
    :param projectname:
    :return:
    """
    unit1 = ifcfile.createIfcSiUnit(None, "LENGTHUNIT", None, "METRE")
    unit2 = ifcfile.createIfcSiUnit(None, "AREAUNIT", None, "SQUARE_METRE")
    unit3 = ifcfile.createIfcSiUnit(None, "VOLUMEUNIT", None, "CUBIC_METRE")
    unitassignment = ifcfile.createIfcUnitAssignment([unit1, unit2, unit3])

    timestamp = time.time()
    timestring = time.strftime("%Y-%m-%dT%H:%M:%S", time.gmtime(timestamp))
    creatorS = "Elisabeth Eckstaedt"
    organizationS = "Fraunhofer IIS/EAS"
    applicationS, application_version = "IfcOpenShell", "0.6.0b0"
    person = ifcfile.createIfcPerson(None, None, creatorS, None, None, None, None, None)
    organization = ifcfile.createIfcOrganization(None, organizationS, None, None, None)
    personAndOrganization = ifcfile.createIfcPersonAndOrganization(person, organization, None)
    application = ifcfile.createIfcApplication(organization, application_version, applicationS, applicationS)
    ts = int(timestamp)
    owner_history = ifcfile.createIfcOwnerHistory(personAndOrganization, application, "READWRITE", "ADDED", ts, personAndOrganization, application, ts)
    ifc_dir_x, ifc_dir_z, context3D1, subcontext3D1 = create_IfcGeometricRepresentationContext(ifcfile)
    project = ifcfile.createIfcProject(ifcopenshell.guid.new(), owner_history, projectname, None, None, None, None, [context3D1], unitassignment)
    return ifc_dir_x, ifc_dir_z, context3D1, subcontext3D1, owner_history, project

def create_IfcGeometricRepresentationContext(ifcfile):
    ifc_dir_x = ifcfile.createIfcDirection([1.0, 0.0, 0.0])  # TODO: prüfen: vertauscht?!
    ifc_dir_z = ifcfile.createIfcDirection([0.0, 0.0, 1.0])
    origin = ifcfile.createIfcCartesianPoint([0., 0., 0.])
    wcs = ifcfile.createIfcAxis2Placement3D(origin, ifc_dir_z, ifc_dir_x)

    Origin_WCS = ifcfile.createIfcCartesianPoint((0.0, 0.0, 0.0))
    RefDirectionX_WCS = ifcfile.createIfcDirection((1., 0., 0.))
    AxisZ_WCS = ifcfile.createIfcDirection((0., 0., 1.))
    A2P_WCS = ifcfile.createIfcAxis2Placement3D(Origin_WCS, AxisZ_WCS, RefDirectionX_WCS)  # TODO lieber vorhandenen Context übernehmen
    context3D1 = ifcfile.createIfcGeometricRepresentationContext('Schema3DausModelica_iD', 'Schema3DausModelica', 3, 0.001, A2P_WCS, None)
    subcontext3D1 = ifcfile.createIfcGeometricRepresentationSubContext('subcontext3Delli1', 'Model', None, None, None, None, context3D1, 0.01, "MODEL_VIEW", None);
    return ifc_dir_x, ifc_dir_z, context3D1, subcontext3D1


def add_new_contexts(ifcfile):
    # add geometric contexts to IFC
    Origin_WCS = ifcfile.createIfcCartesianPoint((0.0, 0.0, 0.0))
    RefDirectionX_WCS = ifcfile.createIfcDirection((1., 0., 0.))
    AxisZ_WCS = ifcfile.createIfcDirection((0., 0., 1.))
    A2P_WCS = ifcfile.createIfcAxis2Placement3D(Origin_WCS, AxisZ_WCS, RefDirectionX_WCS)  # TODO lieber vorhandenen Context übernehmen
    context2D1 = ifcfile.createIfcGeometricRepresentationContext('ContextPlan2D', 'Plan', 2, 0.001, A2P_WCS, None)
    project = ifcfile.by_type('IfcProject')[0]
    old_contexts = project.RepresentationContexts
    new_contexts = old_contexts + (context2D1,)
    ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=project, attributes={"RepresentationContexts": new_contexts})
    subcontext2D1 = ifcfile.createIfcGeometricRepresentationSubContext('Schematics', 'Plan', None, None, None, None, context2D1, 0.01, "PLAN_VIEW", None);  # vorletzter Parameter "TargetView" taucht im FZK nicht auf
    return subcontext2D1


def generate_text_entities(ifcfile):
    fontSize = ifcfile.createIfcPositiveLengthMeasure(20)
    textStyleFontModel = ifcfile.createIfcTextStyleFontModel('Ellis Font', ['sans-serif'], None, None, None, fontSize)
    textcolor = ifcfile.createIfcColourRGB(None, 0.0, 0.0, 0.0)
    textStyleForDefinedFont = ifcfile.createIfcTextStyleForDefinedFont(textcolor, None)
    textStyle = ifcfile.createIfcTextStyle(None, textStyleForDefinedFont, None, textStyleFontModel, None)
    textBoxExtent = ifcfile.createIfcPlanarExtent(100, 50)  # TODO variable extent?
    return textStyle, textBoxExtent


def generate_circle_symbol(ifcfile, element, symbol_position_in_wcs, RefDirectionX_ECS_in_WCS, AxisZ_ECS_in_WCS, AxisZ_WCS_in_ECS, RefDirectionX_WCS_in_ECS):
    # circle
    x = -element.ObjectPlacement.RelativePlacement.Location.Coordinates[0] + symbol_position_in_wcs[0]
    y = -element.ObjectPlacement.RelativePlacement.Location.Coordinates[1] + symbol_position_in_wcs[1]
    z = -element.ObjectPlacement.RelativePlacement.Location.Coordinates[2]
    tuple_Symbol_in_ECS = rotate_local_coordinates_in_global_coordinates((x, y, z), RefDirectionX_ECS_in_WCS, AxisZ_ECS_in_WCS)
    Symbol_in_ECS = ifcfile.createIfcCartesianPoint(tuple_Symbol_in_ECS)
    A2P_Symbol_in_ECS = ifcfile.createIfcAxis2Placement3D(Symbol_in_ECS, AxisZ_WCS_in_ECS, RefDirectionX_WCS_in_ECS)
    circle = ifcfile.createIfcCircle(A2P_Symbol_in_ECS, 20.0)
    return circle, A2P_Symbol_in_ECS, Symbol_in_ECS


def generate_line_to_neighbors(ifcfile, element, node, graph, layout, RefDirectionX_ECS_in_WCS, AxisZ_ECS_in_WCS, Symbol_in_ECS):
    ns = nx.all_neighbors(graph, node)
    list_of_lines_to_neighbors = []
    for neighbor in ns:
        x = -element.ObjectPlacement.RelativePlacement.Location.Coordinates[0] + layout[neighbor][0]
        y = -element.ObjectPlacement.RelativePlacement.Location.Coordinates[1] + layout[neighbor][1]
        z = -element.ObjectPlacement.RelativePlacement.Location.Coordinates[2]
        tuple_neighbor_symbol_in_ECS = rotate_local_coordinates_in_global_coordinates((x, y, z), RefDirectionX_ECS_in_WCS, AxisZ_ECS_in_WCS)
        nb = ifcfile.createIfcCartesianPoint(tuple_neighbor_symbol_in_ECS)
        polyline = ifcfile.createIfcPolyLine([Symbol_in_ECS, nb])
        list_of_lines_to_neighbors.append(polyline)
    return list_of_lines_to_neighbors


def attach_propertyset_with_2D_coordinates(owner_history, ifcfile, object, x, y):
    """
    :param owner_history:
    :param ifcfile:
    :param object:
    :return: property_set
    """
    property_value1 = ifcfile.createIfcPropertySingleValue('x coordinate', None, ifcfile.create_entity("IfcReal",x), None)
    property_value2 = ifcfile.createIfcPropertySingleValue('y coordinate', None, ifcfile.create_entity("IfcReal",y), None)
    property_set1 = ifcfile.createIfcPropertySet(ifcopenshell.guid.new(), owner_history, "Layout_EE", None, [property_value1, property_value2])
    ifcreldefbyp1 = ifcfile.createIfcRelDefinesByProperties(ifcopenshell.guid.new(), owner_history, None, None, (object, ), property_set1)
    return property_set1


def attach_2Dgeometry_to_ifcfile(graph, ifcfile):
    """
    add a circle for each node and a line to all of its neighbors
    with a fixed color
    IfcGeometricRepresentationSubContext('Schematics', 'Plan', None, None, None, None, context2D1, 0.01, "PLAN_VIEW", None)
    :param graph:
    :param ifcfile:
    doesn't return anything since ifcfile is directly manipulated
    """
    subcontext2D1 = add_new_contexts(ifcfile)

    layout, layout_existing = get_or_generate_layout(graph, ifcfile)

    textStyle, textBoxExtent = generate_text_entities(ifcfile)

    ifcfile22, owner_history, personAndOrganization22, application22 = create_IfcOwnerHistory(ifcfile)
    for node in graph.nodes:
        element = ifcfile.by_guid(node)
        symbol_position_in_wcs = layout[node]

        # attach coordinates as property - if not already present
        if not layout_existing:
            pset= attach_propertyset_with_2D_coordinates(owner_history, ifcfile, element, symbol_position_in_wcs[0], symbol_position_in_wcs[1]) #TODO make optional

        if (element.ObjectPlacement.RelativePlacement.RefDirection == None):
            RefDirectionX_ECS_in_WCS = (1, 0, 0)
        else:
            RefDirectionX_ECS_in_WCS = element.ObjectPlacement.RelativePlacement.RefDirection.DirectionRatios

        if (element.ObjectPlacement.RelativePlacement.Axis == None):
            AxisZ_ECS_in_WCS = (0, 0, 1)
        else:
            AxisZ_ECS_in_WCS = element.ObjectPlacement.RelativePlacement.Axis.DirectionRatios

        tuple_Origin_WCS_in_ECS = rotate_local_coordinates_in_global_coordinates(
                                                                    tuple(np.array(element.ObjectPlacement.RelativePlacement.Location.Coordinates) * -1),
                                                                    RefDirectionX_ECS_in_WCS,
                                                                    AxisZ_ECS_in_WCS)

        Origin_WCS_in_ECS = ifcfile.createIfcCartesianPoint(tuple_Origin_WCS_in_ECS)
        RefDirectionX_WCS_in_ECS = ifcfile.createIfcDirection(rotate_local_coordinates_in_global_coordinates((1.0, 0.0, 0.0), RefDirectionX_ECS_in_WCS, AxisZ_ECS_in_WCS))  # tuple contains float64 and therefore it cannot be used directly
        AxisZ_WCS_in_ECS = ifcfile.createIfcDirection(rotate_local_coordinates_in_global_coordinates((0.0, 0.0, 1.0), RefDirectionX_ECS_in_WCS, AxisZ_ECS_in_WCS))
        A2P_WCS_in_ECS = ifcfile.createIfcAxis2Placement3D(Origin_WCS_in_ECS, AxisZ_WCS_in_ECS, RefDirectionX_WCS_in_ECS)

        #circle
        circle, A2P_Symbol_in_ECS, Symbol_in_ECS = generate_circle_symbol(ifcfile, element, symbol_position_in_wcs, RefDirectionX_ECS_in_WCS, AxisZ_ECS_in_WCS, AxisZ_WCS_in_ECS, RefDirectionX_WCS_in_ECS)
        # Text
        textliteral = ifcfile.createIfcTextLiteralWithExtent(element.Name, A2P_Symbol_in_ECS, 'LEFT', textBoxExtent, 'bottom-left')
        ifcfile.createIfcStyledItem(textliteral, [textStyle])
        # lines to neighbors
        list_of_lines_to_neighbors = generate_line_to_neighbors(ifcfile, element, node, graph, layout, RefDirectionX_ECS_in_WCS, AxisZ_ECS_in_WCS, Symbol_in_ECS)

        list_of_lines_to_neighbors.append(circle)

        symbol_representation = ifcfile.createIfcShapeRepresentation(subcontext2D1, "Footprint", "Curve2D", list_of_lines_to_neighbors) #vordefinierte Bezeichnung statt benutzerdefinierte Bezeichnung "Schematics" verwenden
        #symbol_representation = ifcfile.createIfcShapeRepresentation(subcontext2D1, "Schematics", "Curve2D", (circle,)) #testweise mal nur Kreis ohne Linien
        symbol_label_representation = ifcfile.createIfcShapeRepresentation(subcontext2D1, "Annotation", "Text", [textliteral]) #mit RepresentationIdentifier "Schematics Text" zeigt der KIT Viewer den Text nicht an
        pds = element.Representation
        if pds == None: #Virtual Element - has no representation yet
            new_representations = (symbol_representation, symbol_label_representation)
            #new_representations = (symbol_label_representation,) #hier testweise mal ohne Symbol probiert, Text trotzdem nicht zu sehen (Bounding Box hat Abmessung 0)
            #new_representations = (symbol_representation,) #hier testweise mal ohne Text probiert, löste das Problem nicht
            new_pds = ifcfile.createIfcProductDefinitionShape(None, None, new_representations)
            ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=element, attributes={"Representation": new_pds})
        else:
            old_representations = pds.Representations
            new_representations = old_representations + (symbol_representation, symbol_label_representation)
            #new_representations = old_representations + (symbol_label_representation,) #hier testweise mal ohne Symbol probiert, Text trotzdem nicht zu sehen (Bounding Box hat Abmessung 0)
            #new_representations = old_representations + (symbol_representation,) #hier testweise mal ohne Text probiert, löste das Problem nicht
            ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=pds, attributes={"Representations": new_representations})
