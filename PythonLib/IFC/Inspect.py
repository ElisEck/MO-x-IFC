"""
functions to inspect existing ifc-files
Naming conventions for suffixes of function names
- "get" returns "something"
- "count_by" returns a dict
- "dataFrame" returns a dataFrame
- "print" plots to the console
- "generate" generates an extra (stat) file
"""
import ifcopenshell
import pandas as pd
import ifcopenshell.util.element
import logging
from PythonLib.IFC.HelpersGeometry import get_coordinate, calculate_area_of_IfcConnectionSurfaceGeometry

def get_dict_mapping_name_guid(ifcfile):
    mapping_reverse = dict()
    for element in ifcfile.by_type("IfcElement"):
        mapping_reverse[element.Name] = element.GlobalId
    return mapping_reverse


def get_attribute_dict_with_EndElementInformation(element, end_element_guid_set, ifcfile_system):

    if len(element.ContainedInStructure) > 0:
        container_string_1 = element.ContainedInStructure[0].RelatingStructure.Name  # TODO handle several containments
    else:
        container_string_1 = "no container"

    element_in_system_ifcfile = ifcfile_system.by_guid(element.GlobalId)
    if element_in_system_ifcfile.HasAssignments:
        system_string = element_in_system_ifcfile.HasAssignments[0].RelatingGroup.Name  # TODO handle several groups
    else:
        system_string = "no system"

    if element.GlobalId in end_element_guid_set:
        elementstatus_1 = "Endelement"
    else:
        elementstatus_1 = "Mittelelement"

    if len(list(element.IsTypedBy)) == 0:
        logging.info("Element without type: " + str(element))
        typestring = ""
    else:
        if len(list(element.IsTypedBy)) > 1:
            logging.info("Element with several types: " + str(element))
        try:
            typestring = element.IsTypedBy[0].RelatingType.is_a() + "_" + element.IsTypedBy[0].RelatingType.PredefinedType
        except:
            typestring = element.IsTypedBy[0].RelatingType.is_a() + "_"

    attribute_dict = {
        "ifc_name": element.Name,
        "ifc_class": element.is_a(),
        "ifc_predefined_type": typestring,
        "ifc_system": system_string,
        "elementstatus": elementstatus_1,
        "container": container_string_1
    }
    return attribute_dict


def get_attribute_dict(element, ifcfile_system):
    """
    :param element:
    :param ifcfile_system:
    :return: dict with 5 attributed for each node: "ifc_name", "ifc_class", "ifc_predefined_type", "ifc_system", "container"
    """

    #if len(element.ContainedInStructure) > 0:
    try:
        container_string_1 = element.ContainedInStructure[0].RelatingStructure.Name  # TODO handle several containments
    except:
        container_string_1 = "no container"

    element_in_system_ifcfile = ifcfile_system.by_guid(element.GlobalId)
    if element_in_system_ifcfile.HasAssignments:
        system_string = element_in_system_ifcfile.HasAssignments[0].RelatingGroup.Name  # TODO handle several groups
        if system_string[0] == "_": #leading underscore has to be removed, otherwise this string will not be shown in the matplotlib legend
            system_string = system_string[1:]
    else:
        system_string = "no system"

    if len(list(element.IsTypedBy)) == 0:
        logging.info("Element without type: " + str(element))
        typestring = "no type"
    else:
        if len(list(element.IsTypedBy)) > 1:
            logging.info("Element with several types: " + str(element))
        try:
            typestring = element.IsTypedBy[0].RelatingType.is_a() + "_" + element.IsTypedBy[0].RelatingType.PredefinedType
        except:
            typestring = element.IsTypedBy[0].RelatingType.is_a() + "_"

    attribute_dict = {
        "ifc_name": element.Name,
        "ifc_class": element.is_a(),
        "ifc_predefined_type": typestring,
        "ifc_system": system_string,
        "container": container_string_1
    }
    return attribute_dict


def get_list_of_IfcWindow_containers(ifcfile):
    ls_containers = list()
    for window in ifcfile.by_type('IfcWindow'):
        if len(window.Decomposes) > 0:
            ls_containers.append(window.Decomposes[0].RelatingObject)
        else:
            ls_containers.append("")
    return ls_containers


def get_type_string(element):
    if len(element.IsTypedBy) > 0:
        ts = element.IsTypedBy[0].RelatingType.is_a() + "_" + element.IsTypedBy[0].RelatingType.PredefinedType  # TODO handle several IsTypedBy
    else:
        ts = ""
    return ts


def get_IfcElements_without_ports(ifcfile):
    #TODO test implementation (implementation was lost, I reimplemented it, but did not use yet)
    elements_all = ifcfile.by_type('IfcElement')
    elements_with_ports = set()
    for element in elements_all:
        for irn in element.IsNestedBy:
            for ro in irn.RelatedObjects:
                if ro.is_a('IfcDistributionPort'):
                    elements_with_ports.add(element)
    elements_without_ports = set(elements_all).difference(set(elements_with_ports))
    return elements_all, elements_with_ports, elements_without_ports

def dict_ports_by_IfcElement(ifcfile):
    elements_all = ifcfile.by_type('IfcElement')
    dict_element_ports = {}
    for element in elements_all:
        for irn in element.IsNestedBy:
            for ro in irn.RelatedObjects:
                if ro.is_a('IfcDistributionPort'):
                    #if ro.name
                    if element in dict_element_ports.keys():
                        dict_element_ports[element].append(ro)
                    else:
                        dict_element_ports[element] = [ro]
    return dict_element_ports

def dict_guid_by_Name_for_IfcElements(ifcfile):
    dict_element_ports = {}
    for element in ifcfile.by_type('IfcElement'):
        if element.Name in dict_element_ports.keys():
            logging.info("warning - duplicate Name: " + element.Name)
        else:
            dict_element_ports[element.Name] = element.GlobalId
    return dict_element_ports

def set_of_hubs(ifcfile):
    dict_element_ports = dict_ports_by_IfcElement(ifcfile)
    set_of_hubs = set()
    for element in dict_element_ports:
        if len(dict_element_ports[element]) > 2:
            set_of_hubs.add(element)
    return set_of_hubs


def get_unused_IfcShapeRepresentation(ifcfile):
    isrs = ifcfile.by_type('IfcShapeRepresentation')
    set_of_unused_sr = set()
    for sr in isrs:
        if len(sr.OfProductRepresentation) == 0:
            set_of_unused_sr.add(sr)
        else:
            for pr in sr.OfProductRepresentation:
                if len(pr.ShapeOfProduct) == 0:
                    set_of_unused_sr.add(sr)
    logging.info("found " + str(len(set_of_unused_sr)) + " unused IfcShapeRepresentation")
    return set_of_unused_sr

def get_instances_of_unconnected_IfcDistributionPort(ifcfile):
    unconnected_ports = set()
    ports = ifcfile.by_type('IfcDistributionPort')
    for port in ports:
        #port.Nests[0].RelatingObject #find object that nests the port
        try:
            for pc in port.ConnectedFrom:
                other_port = pc.RelatingPort
        except:
            try:
                for pc in port.ConnectedTo:
                    other_port = pc.RelatedPort
            except:
                unconnected_ports.add(port)
    return unconnected_ports()


def get_instance_list_old(ifcfile, maxid):
    instancelist = []
    for instanceid in range(1, maxid):  # TODO more elegant loop?
        try:
            instance = ifcfile.by_id(instanceid)
            instancelist.append(instance)
        except:
            test = 1
    return instancelist

def get_instance_list(ifcfile):
    instancelist = []
    for instance in ifcfile:
        instancelist.append(instance)
    return instancelist

def set_of_instance_ids(ifcfile):
    """
    to quickly check which instance numbers/id are contained in an ifcfile
    :param ifcfile:
    :return: set of instance ids
    """
    instance_id_set = set()
    for instance in ifcfile:
        instance_id_set.add(instance.id())
    return instance_id_set

def set_of_instances(ifcfile):
    """
    :param ifcfile:
    :return: set of instances
    """
    logging.info("generating set of all instances of the ifcfile")
    instance_set = set()
    for instance in ifcfile:
        instance_set.add(instance)
    logging.info(str(len(instance_set)) + " instances contained")
    return instance_set

def get_IfcGeometricRepresentationContext_by_identifier(ifcfile, identifier):
    """
    :param ifcfile:
    :param identifier: String
    :return: first instance of IfcGeometricRepresentationContext with the given name
    """
    contexts = ifcfile.by_type("IfcGeometricRepresentationContext")
    for c in contexts:
        if c.ContextIdentifier == identifier:
            return c
    return None #if context with that name is not contained in ifcfile

def count_by_class(elements):
    """
    :param elements: (list or set or...)
    :return: dict_count_of_classes
    """
    dict_count_of_classes = {}
    for element in elements:
        ifc_class = element.is_a()
        if ifc_class in dict_count_of_classes.keys():
            count = dict_count_of_classes[ifc_class]
            dict_count_of_classes[ifc_class] = count + 1
        else:
            dict_count_of_classes[ifc_class] = 1
    return dict_count_of_classes

def count_ports_by_flowdirection(ports):
    """
    :param elements: (list or set or...)
    :return: dict_count_of_classes
    """
    dict_count_of_flowdirection = {}
    for port in ports:
        fd = port.FlowDirection
        if fd in dict_count_of_flowdirection.keys():
            count = dict_count_of_flowdirection[fd]
            dict_count_of_flowdirection[fd] = count + 1
        else:
            dict_count_of_flowdirection[fd] = 1
    return dict_count_of_flowdirection

def count_elements_by_no_of_ports(ifcfile):
    dict_element_ports = dict_ports_by_IfcElement(ifcfile)
    dict_element_by_no_of_ports = {}
    for element in dict_element_ports:
        no_of_ports = len(dict_element_ports[element])
        if no_of_ports in dict_element_by_no_of_ports.keys():
            dict_element_by_no_of_ports[no_of_ports].append(element) #muss nicht zugewiesen werden...
        else:
            dict_element_by_no_of_ports[no_of_ports] = [element]
    return dict_element_by_no_of_ports

def dict_ports_by_flowdirection(ports):
    """
    :param elements: (list or set or...)
    :return: dict_count_of_classes
    """
    dict_by_flowdirection = {}
    for port in ports:
        fd = port.FlowDirection
        if fd in dict_by_flowdirection.keys():
            dict_by_flowdirection[fd].append(port) #muss nicht zugewiesen werden...
        else:
            dict_by_flowdirection[fd] = [port]
    return dict_by_flowdirection

def count_by_predefined_type(elements):
    """
    :param elements: (list or set or...)
    :return: dict_count_by_predefined_type
    """
    dict_count_by = {}
    for element in elements:
        if len(element.IsTypedBy) > 1:
            print("attention: more than one IsTypedBy for element present")
        if len(element.IsTypedBy) == 0:
            ifc_predefined_type = "NoTypeGiven"
        else:
            ifc_predefined_type = element.IsTypedBy[0].RelatingType.is_a() + "_" + element.IsTypedBy[0].RelatingType.PredefinedType  # TODO handle several IsTypedBy
        if ifc_predefined_type in dict_count_by.keys():
            count = dict_count_by[ifc_predefined_type]
            dict_count_by[ifc_predefined_type] = count + 1
        else:
            dict_count_by[ifc_predefined_type] = 1
    return dict_count_by

def count_connected_instances(ifcfile):
    logging.info("counting connected instances of all IfcElements in file")
    dict_count_by = {}
    for element in ifcfile.by_type('IfcElement'):
        list_directly_connected = ifcfile.traverse(element)
        dict_count_by[element] = len(list_directly_connected)
    return dict_count_by

def count_referenced_instances_for_IfcElement_Representation(ifcfile):
    logging.info("counting connected instances for geometry of all IfcElements in file")
    dict_count_by = {}
    for element in ifcfile.by_type('IfcElement'):
        if element.Representation:
            list_directly_connected = ifcfile.traverse(element.Representation)
            dict_count_by[element] = len(list_directly_connected)
        else :
            dict_count_by[element] = 0
    return dict_count_by

def dataFrame_all_IfcRoot (ifcfile):
    """
    :param ifcfile: filehandle
    :return: dataFrame mit 6 Spalten (instanceId, type, GlobalId, OwnerHistory, Name, Description) und einer Zeile für jedes IfcRoot-Objekt
    """
    list = ifcfile.by_type('IFCROOT')  # alle Objekte in eine Liste
    df = pd.DataFrame(columns=['instanceId', 'type', 'GlobalID', 'OwnerHistory', 'Name', 'Description'])
    for s in list:
        dict=s.get_info() #alle Attribute in ein dict
        row = {
            'instanceId': dict['id'],
            'type': dict['type'],
            'GlobalID': dict['GlobalId'],
            'OwnerHistory': dict['OwnerHistory'],
            'Name': dict['Name'],
            'Description': dict['Description'],
        }
        df = df.append(row, ignore_index=True)
    df = df.set_index('GlobalID')
    return df

def dataFrame_all_IfcRelVoidsElement (ifcfile):
    """
    :param ifcfile: filehandle
    """
    irves = ifcfile.by_type('IfcRelVoidsElement')  # alle Objekte in eine Liste
    list_guid = []
    list_building_element_guid = []
    list_building_element_class = []
    list_opening = []
    list_filling_guid = []
    list_filling_class = []
    for s in irves:
        list_guid.append(s.GlobalId)
        list_building_element_guid.append(s.RelatingBuildingElement.GlobalId)
        list_building_element_class.append(s.RelatingBuildingElement.is_a())
        opening = s.RelatedOpeningElement
        list_opening.append(opening)
        if len(opening.HasFillings)>0:
            list_filling_guid.append(opening.HasFillings[0].RelatedBuildingElement.GlobalId) #TODO handle several filling per Opening
            list_filling_class.append(opening.HasFillings[0].RelatedBuildingElement.is_a()) #TODO handle several filling per Opening
            if len(opening.HasFillings) > 1:
                logging.info("Opening has several fillings, only the first one is handled: " + str(opening))
        else:
            list_filling_guid.append("")
            list_filling_class.append("")
        df = pd.DataFrame(
            {   "guid IfcRelVoidsElement" : list_guid,
                "Element with opening - class" : list_building_element_class,
                "Element with opening - guid" : list_building_element_guid,
                "opening" : list_opening,
                "filling element - class" : list_filling_class,
                "filling element - guid" : list_filling_guid },
            index = list_guid)
    return df

#tested only with FZK Haus so far
def dataFrame_all_IfcProducts_position_and_representation(ifcfile):
    '''
    :param ifcfile:
    :return: dataFrame mit 5 Spalten (instanceId, class, position, list of 2D representations, list of 3D representations) und einer Zeile für jedes IfcProduct-Objekt
    '''
    product_list = ifcfile.by_type("IfcProduct")
    #never grow a Data.Frame (unfortunately!)
    list_ids = []
    list_class = []
    list_position = []
    list_2Dlists = []
    list_3Dlists = []
    for product in product_list:
        instance_id = product.id()
        product_class = product.is_a()
        #print(product)
        placement = product.ObjectPlacement
        if product.Representation:
            representations = product.Representation.Representations
            list_2D_representations = []
            list_3D_representations = []
            for repr in representations:
                context = repr.ContextOfItems
                context_identifier = context.ContextIdentifier
                context_type = context.ContextType #'Plan' or 'Model' are valid options for this (according to IFC Standard)
                context_target_view = context.TargetView
                if context_type == 'Plan': #Verwendung von CoordinateSpaceDimension (wie zunächst angedacht) funktioniert nicht, da im FZK-Haus keine Angabe für 2D-Kontexte
                    list_2D_representations.append(repr)
                if context_type == 'Model':
                    list_3D_representations.append(repr)
                representation_identifier = repr.RepresentationIdentifier
                representation_type = repr.RepresentationType
            list_2Dlists.append(list_2D_representations)
            list_3Dlists.append(list_3D_representations)
        else:
            list_2Dlists.append(None)
            list_3Dlists.append(None)
        list_ids.append(instance_id)
        list_class.append(product_class)
        list_position.append(placement)
    df = pd.DataFrame(
        {   "class" : list_class,
           "position" : list_position,
           "ListOf2D" : list_2Dlists,
            "ListOf3D" : list_3Dlists },
        index = list_ids)
    return df

def dataFrame_all_IfcMaterialLayerSets(ifcfile):
    ls_mls_name = list()
    ls_mls_description = list()
    ls_layer_name = list()
    ls_layer_thickness = list()
    ls_layer_isventilated = list()
    ls_layer_description = list()
    ls_layer_category = list()
    ls_layer_material_name = list()
    ls_layer_material_description = list()
    ls_layer_material_category = list()
    for mls in ifcfile.by_type('IfcMaterialLayerSet'):
        for layer in mls.MaterialLayers:
            ls_mls_name.append(mls.LayerSetName)
            ls_mls_description.append(mls.Description)
            material = layer.Material
            ls_layer_name.append(layer.Name)
            ls_layer_thickness.append(layer.LayerThickness)
            ls_layer_isventilated.append(layer.IsVentilated)
            ls_layer_description.append(layer.Description)
            ls_layer_category.append( layer.Category)
            ls_layer_material_name.append(material.Name)
            ls_layer_material_description.append(material.Description)
            ls_layer_material_category.append(material.Category)
            #TODO any property association to Material?
            if len(material.HasProperties)>0:
                print(material.HasProperties)
    df = pd.DataFrame(
        {
           "MaterialLayerSet Name" : ls_mls_name,
            "MaterialLayerSet Descrption" : ls_mls_description,
            "layer name": ls_layer_name,
           "layer thickness" : ls_layer_thickness,
           "layer isventilated" : ls_layer_isventilated,
           "layer description" : ls_layer_description,
           "layer category" : ls_layer_category,
           "layer material name" : ls_layer_material_name,
           "layer material description" : ls_layer_material_description,
           "layer material category" : ls_layer_material_category
        },      )
    return df

def dataframe_all_IfcRelAssociatesMaterial(ifcfile):
    objectlist_name = list()
    objectlist_guid = list()
    objectlist_class = list()
    materiallist_id = list()
    materiallist_class = list()
    for iram in ifcfile.by_type('IfcRelAssociatesMaterial'):
        for obj in iram.RelatedObjects:
            objectlist_name.append(obj.Name)
            objectlist_guid.append(obj.GlobalId)
            objectlist_class.append(obj.is_a())
            materiallist_id.append(iram.RelatingMaterial.id())
            materiallist_class.append(iram.RelatingMaterial.is_a())

    df = pd.DataFrame(
        {"objectlist_name": objectlist_name,
         "objectlist_guid": objectlist_guid,
         "objectlist_class": objectlist_class,
         "material id": materiallist_id,
         "material class": materiallist_class}
    )
    return df


def dataframe_all_IfcObject_material_associations(ifcfile):
    ls_object = list()
    ls_object_class = list()
    ls_material_from_instance = list()
    ls_material_from_instance_class = list()
    ls_material_from_type = list()
    ls_material_from_type_class = list()

    for object in ifcfile.by_type('IfcObject'):  # might be sufficient to do it for IfcElement

        ls_object.append(object)
        ls_object_class.append(object.is_a())

        objectmaterial=""
        objectmaterial_class=""
        for association in object.HasAssociations:
            if association.is_a('IfcRelAssociatesMaterial'): #TODO handle several IfcRelAssociatesMaterial
                objectmaterial = association.RelatingMaterial
                objectmaterial_class = association.RelatingMaterial.is_a()
        ls_material_from_instance.append(objectmaterial)
        ls_material_from_instance_class.append(objectmaterial_class)

        typematerial = ""
        typematerial_class = ""
        if len(object.IsTypedBy) > 0:
            type = object.IsTypedBy[0].RelatingType
            for association in type.HasAssociations:
                if association.is_a('IfcRelAssociatesMaterial'):
                    typematerial = association.RelatingMaterial
                    typematerial_class = association.RelatingMaterial.is_a()
            if len(object.IsTypedBy) > 1:
                logging.info("several types assigned to object - only first one is used " + str(object))
        ls_material_from_type.append(typematerial)
        ls_material_from_type_class.append(typematerial_class)

    dfmaterial = pd.DataFrame(
                {   "object" : ls_object,
                    "object class" : ls_object_class,
                    "material from object" : ls_material_from_instance,
                    "materiallist from object class" : ls_material_from_instance_class,
                    "material from type": ls_material_from_type,
                    "material from type class" : ls_material_from_type_class
                        })
    return dfmaterial

def dataFrame_all_IfcProduct_assignments(ifcfile):
    '''
    :param ifcfile:
    :return: dataFrame mit 13 Spalten: GlobalID, Name, Klasse, predefined Type, Container GUID, container Name, portzahl, placement, system, Type GUID, Type Class, TYpe Name, Type predefined Type
    '''
    products = ifcfile.by_type("IfcProduct")
    ls_guid = []
    ls_name = []
    ls_klasse = []
    ls_System = []
    ls_layer_assigment = []
    ls_colour_id_assignment = []
    ls_colour_code_assignment = []
    for product in products:
        ls_guid.append(product.GlobalId)
        ls_name.append(product.Name)
        ls_klasse.append(product.is_a())

        if len(product.HasAssignments) > 0:
            ls_System.append(product.HasAssignments[0].RelatingGroup.Name)
        else:
            ls_System.append("-")

        layer_name = "-" # will be overwritten if any representation item has a layer assigned
        colour_id = "-"
        colour_code = "-"
        if product.Representation != None:
            ers = product.Representation.Representations
            for er in ers:
                if len(er.LayerAssignments) > 0: #this handles layer assignment of an IfcRepresentation
                    if len(er.LayerAssignments) > 1:
                        logging.info("Element hat mehrere Layer zugewiesen (IfcRepresentation), nur der letzte kommt in die Liste: " + str(product))
                    for la in er.LayerAssignments:
                        layer_name = la.Name
                for item in er.Items:
                    if len(item.LayerAssignment)>0: #this handles layer assignment of an IfcRepresentationItem - not present in EAS KLT
                        if len(item.LayerAssignment) > 1:
                            logging.info("Element hat mehrere Layer zugewiesen (IfcRepresentationItem), nur der letzte kommt in die Liste: " + str(product))
                        for la in item.LayerAssignment:
                            layer_name = la.Name
                    if len(item.StyledByItem)>0:
                        if len(item.StyledByItem) > 1:
                            logging.info("Element hat mehrere Zuweisungen von \"StyledByItem\" - nur die letzte Farbe kommt in die Liste: " + str(product))
                        for si in item.StyledByItem:
                            if si.Styles[0].is_a() != 'IfcTextStyle':
                                colour_id = si.Styles[0].Styles[0].Styles[0].SurfaceColour.id()
                                colour_code = str(si.Styles[0].Styles[0].Styles[0].SurfaceColour.Red) + " - " + \
                                              str(si.Styles[0].Styles[0].Styles[0].SurfaceColour.Green) + " - " + \
                                              str(si.Styles[0].Styles[0].Styles[0].SurfaceColour.Blue)

        ls_layer_assigment.append(layer_name)
        ls_colour_id_assignment.append(colour_id)
        ls_colour_code_assignment.append(colour_code)

    df = pd.DataFrame({
        "Name": ls_name,
        "Klasse": ls_klasse,
        "zugeordneter Layer": ls_layer_assigment,
        "zugeordnete Farbe ID": ls_colour_id_assignment,
        "zugeordnete Farbe RGB": ls_colour_code_assignment,
        "System": ls_System,
    }, index=ls_guid)
    return df

def dataFrame_all_IfcElement(ifcfile):
    '''
    :param ifcfile:
    :return: dataFrame mit 13 Spalten: GlobalID, Name, Klasse, predefined Type, Container GUID, container Name, portzahl, placement, system, Type GUID, Type Class, TYpe Name, Type predefined Type
    '''
    elements = ifcfile.by_type("IfcElement")
    df = dataFrame_given_IfcElement(elements)
    return df

def dataFrame_all_IfcTypeObject(ifcfile):
    spdf = pd.DataFrame(columns=['step_id','guid','name', 'class','predefined type', 'number of typed objects', 'number of predefined psets', 'number of psets', 'number of properties in normal psets'])
    for ito in ifcfile.by_type('IfcTypeObject'):
        psets = ito.HasPropertySets
        try:
            no_psets = len(psets)
        except:
            no_psets=0
        no_of_normal_psets = 0
        no_props = 0
        try:
            for pset in psets:
                no_props = no_props + len(pset.HasProperties)
                no_of_normal_psets = no_of_normal_psets + 1
                #if pset.is_a('IfcPreDefinedPropertySet')
        except:
            no_props = no_props
        try:
            pdt = ito.PredefinedType,
        except:
            pdt = ""
        row = pd.Series([
            ito.id(),
            ito.GlobalId,
            ito.Name,
            ito.is_a(),
            pdt,
            len(ito.ObjectTypeOf[0].RelatedObjects),
            no_psets-no_of_normal_psets,
            no_psets,
            no_props,
        ], index=spdf.columns)
        spdf = spdf.append(row, ignore_index=True)
    return spdf

def dataFrame_given_IfcElement(elements):
    '''

    :param elements: list of IfcElements (handle in IFC file, not a list of guids!)
    :return: dataFrame mit 14 Spalten: GlobalID, Name, Description, Klasse, predefined Type, Container GUID, container Name, portzahl, placement, system, Type GUID, Type Class, TYpe Name, Type predefined Type
    '''
    #beps = ifcfile.by_type("IfcBuildingElementProxy")
    ls_guid = []
    ls_name = []
    ls_description = []
    ls_container_name = []
    ls_container_guid = []
    ls_klasse = []
    ls_predefined_type = []
    ls_Anzahl_Ports = []
    ls_placement = []
    ls_System = []
    ls_type_guid = []
    ls_type_class = []
    ls_type_name = []
    ls_type_predefined_type = []
    for element in elements:
        ls_guid.append(element.GlobalId)
        ls_name.append(element.Name)
        ls_description.append(element.Description)
        if len(element.ContainedInStructure) > 0:
            ls_container_name.append(element.ContainedInStructure[0].RelatingStructure.Name)
            ls_container_guid.append(element.ContainedInStructure[0].RelatingStructure.GlobalId)
            if len(list(element.ContainedInStructure))>1:
                logging.info("Element in mehrere Structuren enthalten (ContainedInStructure): " + str(element))
        else:
            ls_container_name.append("no container")
            ls_container_guid.append("no container")
        ls_klasse.append(element.is_a())
        try:
            ls_predefined_type.append(element.PredefinedType)
        except:
            ls_predefined_type.append("")
        if len(element.IsNestedBy)>0:
            try:
                if element.IsNestedBy[0].RelatedObjects[0].is_a() == "IfcDistributionPort":
                    ls_Anzahl_Ports.append(len(element.IsNestedBy[0].RelatedObjects))
                else:
                    ls_Anzahl_Ports.append(0)
            except:
                ls_Anzahl_Ports.append(0)
        else:
            ls_Anzahl_Ports.append(0)
        ls_placement.append(get_coordinate(element))
        if len(element.HasAssignments)>0:
            ls_System.append(element.HasAssignments[0].RelatingGroup.Name)
        else:
            ls_System.append("-")
        if len(element.IsTypedBy) > 0:
            ls_type_guid.append(element.IsTypedBy[0].RelatingType.GlobalId)
            ls_type_class.append(element.IsTypedBy[0].RelatingType.is_a())
            ls_type_name.append(element.IsTypedBy[0].RelatingType.Name)
            try:
                ls_type_predefined_type.append(element.IsTypedBy[0].RelatingType.PredefinedType)
            except:
                ls_type_predefined_type.append("no predefined type")
            if len(element.IsTypedBy) > 1:
                logging.info("Element mit mehreren Typen: " + str(element))
        else:
            ls_type_guid.append("")
            ls_type_class.append("")
            ls_type_name.append("")
            ls_type_predefined_type.append("")
        df = pd.DataFrame(        {
            "Name": ls_name,
            "Description": ls_description,
            "Klasse": ls_klasse,
            "predefined Type": ls_predefined_type,
            "Container GUID": ls_container_guid,
            "Container Name": ls_container_name,
            "Portzahl": ls_Anzahl_Ports,
            "Placement": ls_placement,
            "System": ls_System,
            "Type GUID": ls_type_guid,
            "Type Class": ls_type_class,
            "Type Name": ls_type_name,
            "Type predefined Type": ls_type_predefined_type,
        } , index=ls_guid)
    return df

def dataFrame_all_IfcElement_SystemAssignment(ifcfile, name):
    ls_guid = []
    ls_name = []
    ls_system = []
    ls_storey = []
    for element in ifcfile.by_type('IfcElement'):
        ls_guid.append(element.GlobalId)
        ls_name.append(element.Name)
        if element.HasAssignments:
            ls_system.append(element.HasAssignments[0].RelatingGroup.Name)  # TODO handle several groups
        else:
            ls_system.append("no system")
        #   logging.info("element without Group: " + str(element))
        ls_storey.append(name)
    df_all_elements = pd.DataFrame({
        "Name": ls_name,
        "zugehöriges System": ls_system,
        "File": ls_storey,
    }, index=ls_guid)
    return df_all_elements

def dataFrame_all_IfcSpace(ifcfile):
    spdf = pd.DataFrame(columns=['space_id','space_guid','Name', 'Longname'])
    for space in ifcfile.by_type('IfcSpace'):
        row = pd.Series([
            space.id(),
            space.GlobalId,
            space.Name,
            space.LongName,
        ], index=spdf.columns)
        spdf = spdf.append(row, ignore_index=True)
    return spdf


def dataFrame_all_IfcSpace_with_info_1(ifcfile):
    """
    'space_id', 'space_guid', 'Name', 'Longname', 'Geschoss', 'Gebäudeteil', 'Nutzung'
    :param ifcfile:
    :return:
    """
    spdf = pd.DataFrame(columns=['space_id', 'space_guid', 'Name', 'Longname', 'Geschoss', 'Gebäudeteil', 'Nutzung'])
    for space in ifcfile.by_type('IfcSpace'):
        nutzung = ""
        gebteil = ""
        for iratg in space.HasAssignments:
            if iratg.RelatingGroup.HasAssignments[0].RelatingGroup.Name == "Nutzungsarten":
                nutzung = iratg.RelatingGroup.Name
            if iratg.RelatingGroup.HasAssignments[0].RelatingGroup.Name == "Gebäudeteil":
                gebteil = iratg.RelatingGroup.Name
            iratg.RelatingGroup.Name
            row = pd.Series([
                space.id(),
                space.GlobalId,
                space.Name,
                space.LongName,
                space.Decomposes[0].RelatingObject.Name,
                gebteil,
                nutzung,
            ], index=spdf.columns)
        spdf = spdf.append(row, ignore_index=True)
    return spdf

def dataFrame_all_IfcSpace_with_info_2(ifcfile):
    """
    'space_id', 'space_guid', 'Name', 'Longname', 'Geschoss', 'Gebäudeteil', 'Nutzung', 'Elemente im Raum'
    :param ifcfile:
    :return:
    """
    spdf = pd.DataFrame(columns=['space_id', 'space_guid', 'Name', 'Longname', 'Geschoss', 'Gebäudeteil', 'Nutzung', 'Elements in room'])
    for space in ifcfile.by_type('IfcSpace'):
        nutzung = ""
        gebteil = ""
        list_of_elements = list()
        for ircss in space.ContainsElements:
            for el in ircss.RelatedElements:
                list_of_elements.append(el.HasAssignments[0].RelatingGroup.Name + "_" + el.Name)
        for iratg in space.HasAssignments:
            if iratg.RelatingGroup.HasAssignments[0].RelatingGroup.Name == "Nutzungsarten":
                nutzung = iratg.RelatingGroup.Name
            if iratg.RelatingGroup.HasAssignments[0].RelatingGroup.Name == "Gebäudeteil":
                gebteil = iratg.RelatingGroup.Name
            iratg.RelatingGroup.Name
            row = pd.Series([
                space.id(),
                space.GlobalId,
                space.Name,
                space.LongName,
                space.Decomposes[0].RelatingObject.Name,
                gebteil,
                nutzung,
                list_of_elements,
            ], index=spdf.columns)
        spdf = spdf.append(row, ignore_index=True)
    return spdf

def dataFrame_all_IfcSpace_all_properties(ifcfile):
    spdf = pd.DataFrame(columns=['space_id','space_guid','Name','LongName','pset', 'pname','pnominalvalue'])
    for space in ifcfile.by_type('IfcSpace'):
        for definition in space.IsDefinedBy:
            pset = definition.RelatingPropertyDefinition
            if pset.is_a('IfcPropertySet'): # not for quantity sets or predefined property sets
                for property in pset.HasProperties:
                    row = pd.Series([
                        space.id(),
                        space.GlobalId,
                        space.Name,
                        space.LongName,
                        pset.Name,
                        property.Name,
                        property.NominalValue
                    ], index=spdf.columns)
                    spdf = spdf.append(row, ignore_index=True)
    return spdf

def dataFrame_all_attributes_all_properties(ifcfile, ifcclass='IfcSpace'):
    """
    creates dataframe containing all attributed and all properties (directly or indirectly (via type) attached to object)
    :param ifcfile:
    :param ifcclass: e.g. IfcSpace
    :return: dataframe
    """
    spdf = pd.DataFrame(columns=['instance_id','guid','Name','Description','pset', 'pname','pnominalvalue'])
    for element in ifcfile.by_type(ifcclass):
        logging.info("checking properties of " + str(element))
        psets = set()
        for definition in element.IsDefinedBy:
            if definition.is_a('IfcRelDefinesByType'):
                type = definition.RelatingType
                try:
                    psets.update(type.HasPropertySets)
                except:
                    continue #if no Pset attached to type
            else:
                psets.add(definition.RelatingPropertyDefinition)

        for pset in psets: #direct and indirect (via type) psets of object
            if pset.is_a('IfcPropertySet'): # not for quantity sets or predefined property sets
                for property in pset.HasProperties:
                    row = pd.Series([
                        element.id(),
                        element.GlobalId,
                        element.Name,
                        element.Description,
                        pset.Name,
                        property.Name,
                        property.NominalValue
                    ], index=spdf.columns)
                    spdf = spdf.append(row, ignore_index=True)
    return spdf
def dataFrame_all_IfcSpace_all_quantities(ifcfile):
    spdf = pd.DataFrame(columns=['space_id','space_guid','qset','quantity', 'quantityname','quantitystring'])
    for space in ifcfile.by_type('IfcSpace'):
        for definition in space.IsDefinedBy:
            pdef = definition.RelatingPropertyDefinition
            if pdef.is_a('IfcQuantitySet'):
                for quantity in pdef.Quantities:
                    row = pd.Series([
                        space.id(),
                        space.GlobalId,
                        pdef.Name,
                        quantity.is_a(),
                        quantity.Name,
                        str(quantity),
                    ], index=spdf.columns)
                    spdf = spdf.append(row, ignore_index=True)
    return spdf

def get_BoundingBox_coordinates(ifcfile, element):
    """
    min and max coordinates (wrt to local position of element)
    :param ifcfile:
    :param element:
    :return: [min_x, min_y, min_z, max_x, max_y, max_z]
    """
    s = ifcopenshell.geom.settings()
    try:
        shape = ifcopenshell.geom.create_shape(s, element)
        verts = shape.geometry.verts
        list_x = []
        list_y = []
        list_z = []
        for i in range(0, int(len(verts) / 3), 1):
            list_x.append(verts[i * 3])
            list_y.append(verts[i * 3 + 1])
            list_z.append(verts[i * 3 + 2])
        min_x = min(list_x)
        min_y = min(list_y)
        min_z = min(list_z)
        max_x = max(list_x)
        max_y = max(list_y)
        max_z = max(list_z)
        origin = [min_x, min_y, min_z]
        corner = ifcfile.createIfcCartesianPoint((origin[0], origin[1], origin[2]))
        bb = ifcfile.createIfcBoundingBox(corner, max_x - min_x, max_y - min_y, max_z - min_z)
        return [min_x, min_y, min_z, max_x, max_y, max_z]
    except:
        logging.info("generating IfcBoundingBox for " + str(element) + " failed")

#DataFrame aller SpaceBoundaries
def dataFrame_all_IfcRelSpaceBoundary(ifcfile):
    sbdf = pd.DataFrame(columns=['Boundary Instanz ID','Boundary guid', 'Name', 'Description', 'Space Instanz ID','Space Name','Space guid', 'BuildingElement','VirtPhys', 'IntExt', 'Geometry', 'Area gross', 'Area inner', 'Area net'])
    for sb in ifcfile.by_type('IfcRelSpaceBoundary'):
        area_gross, area_inner, area_net = calculate_area_of_IfcConnectionSurfaceGeometry(sb.ConnectionGeometry)
        if sb.PhysicalOrVirtualBoundary=='VIRTUAL':
            row = pd.Series([
                sb.id(),
                sb.GlobalId,
                sb.Name,
                sb.Description,
                sb.RelatingSpace.id(),
                sb.RelatingSpace.Name,
                sb.RelatingSpace.GlobalId,
                '',
                sb.PhysicalOrVirtualBoundary,
                sb.InternalOrExternalBoundary,
                sb.ConnectionGeometry.is_a() + ' ' + str(sb.ConnectionGeometry.id()),
                area_gross,
                area_inner,
                area_net
            ], index=sbdf.columns)
        else:
            row = pd.Series([
                sb.id(),
                sb.GlobalId,
                sb.Name,
                sb.Description,
                sb.RelatingSpace.id(),
                sb.RelatingSpace.Name,
                sb.RelatingSpace.GlobalId,
                sb.RelatedBuildingElement.is_a() + ' '+ str(sb.RelatedBuildingElement.id()),
                sb.PhysicalOrVirtualBoundary,
                sb.InternalOrExternalBoundary,
                sb.ConnectionGeometry.is_a() + ' '+ str(sb.ConnectionGeometry.id()),
                area_gross,
                area_inner,
                area_net
            ], index=sbdf.columns)
        sbdf = sbdf.append(row, ignore_index=True)
    sbdf.set_index('Boundary Instanz ID')
    sbdf.sort_values(by=['Space Instanz ID', 'BuildingElement'])
    return sbdf



def print_aggregations(ifcfile):
    for agg in ifcfile.by_type("IfcRelAggregates"):
        print(agg.RelatingObject)
        for related in agg.RelatedObjects:
            print("\t" + str(related))

def print_containment_in_IfcSpatialElements(ifcfile):
    for se in ifcfile.by_type("IfcSpatialElement"):
        for IfcRelContainedInSpatialStructure in se.ContainsElements:
            print(str(se) + ": " + str(len(IfcRelContainedInSpatialStructure.RelatedElements)))

def print_representations_in_context(ifcfile):
    contexts = ifcfile.by_type("IfcGeometricRepresentationContext")
    for context in contexts:
        ls_representation = context.RepresentationsInContext
        print(context)
        print("context " + str(context.id()) + " contains " + str (len(ls_representation)) + " representations")

def print_placements_of_IfcProducts(ifcfile):
    site = ifcfile.by_type("IfcSite")[0]
    building = ifcfile.by_type("IfcBuilding")[0]
    storey = ifcfile.by_type("IfcBuildingStorey")[0]
    print("placement site: " + str(site.ObjectPlacement) + "\t" + str(site.ObjectPlacement.RelativePlacement) + "\t" + str(site.ObjectPlacement.RelativePlacement.Location))
    print("placement building: " + str(building.ObjectPlacement) + "\t" + str(building.ObjectPlacement.RelativePlacement) + "\t" + str(building.ObjectPlacement.RelativePlacement.Location))
    print("placement storey: " + str(storey.ObjectPlacement) + "\t" + str(storey.ObjectPlacement.RelativePlacement) + "\t" + str(storey.ObjectPlacement.RelativePlacement.Location))
    dict_bezugssysteme = dict()
    no_of_products_without_placement = 0
    for product in ifcfile.by_type("IfcProduct"):
        if product.ObjectPlacement != None:
            if product.ObjectPlacement.PlacementRelTo != None:
                bezugssystem = product.ObjectPlacement.PlacementRelTo.id()
            else:
                bezugssystem = "WCS (ohne Bezugssystem)"
            if bezugssystem in dict_bezugssysteme:
                dict_bezugssysteme[bezugssystem] = dict_bezugssysteme[bezugssystem] + 1
            else:
                dict_bezugssysteme[bezugssystem] = 1
        else:
            no_of_products_without_placement = no_of_products_without_placement + 1
    print("Anzahl Products: " + str(len(ifcfile.by_type("IfcProduct"))) + " davon ohne Placement: " + str(no_of_products_without_placement))
    for bs in dict_bezugssysteme:
        print("Products in Bezugssystem \"" + str(bs) + "\": " + str(dict_bezugssysteme[bs]))

def print_count_IfcRoot_by_class(ifc):
    haufigkeit=count_by_class(ifc.by_type('IfcRoot'))
    for k, v in haufigkeit.items():
         print(k + "\t" + str(v))

def print_count_elements_by_no_of_ports(ifcfile):
    haufigkeit = count_elements_by_no_of_ports(ifcfile)
    elements_all, elements_with_ports, elements_without_ports = get_IfcElements_without_ports(ifcfile)
    more_than_4 = 0
    for k, v in haufigkeit.items():
        if k > 4:
            more_than_4 = more_than_4 + len(v)
    print("Elements without ports: " + str(len(elements_without_ports))
          + "\t IfcElements with 1 port: " + str(len(haufigkeit[1]))
          + "\t IfcElements with 2 ports: " + str(len(haufigkeit[2]))
          + "\t IfcElements with 3 ports: " + str(len(haufigkeit[3]))
          + "\t IfcElements with 4 ports: " + str(len(haufigkeit[4]))
          + "\t IfcElements with 5 or more ports: " + str(more_than_4)
          )


def print_count_by_class(entitylist):
    haufigkeit=count_by_class(entitylist)
    for k, v in haufigkeit.items():
         print(k + "\t" + str(v))

def print_parts_of_IfcCurtainWalls(ifcfile):
    for cw in ifcfile.by_type('IfcCurtainWall'):
        # for object in cw.IsDecomposedBy[0].RelatedObjects:
        print(cw)
        print(count_by_class(cw.IsDecomposedBy[0].RelatedObjects))

def generate_statFile(ifcfile, filename_including_path):
    filename = filename_including_path.split(".ifc")[0]
    haufigkeit = count_by_class(ifcfile.by_type('IfcRoot'))
    statfile = open(filename + "_ROOT.txt", "w+")
    #statfile.write('Filesize\t' + str(dateigrosse) + "\n")
    for k, v in haufigkeit.items():
        statfile.write(k + "\t" + str(v) + "\n")
    statfile.close()

def get_duplicates_and_their_replacements(ifcfile, ifc_class_to_investigate):
    """
    check which elements of a certain ifcclass are duplicates and which is their original
    :param ifcfile:
    :param ifc_class_to_investigate:
    :return: dict_replacements (maps instance to its replacement), set of duplicates (contains instances)
    """
    logging.info("get all " + ifc_class_to_investigate)
    list_all = ifcfile.by_type(ifc_class_to_investigate)
    class_set = set()
    for i in list_all:
        class_set.add(i.is_a())
    logging.info("found " + str(len(list_all)) + " entities of classes " + str(class_set))
    set_original_hashes = set()
    set_duplicates = set()
    dict_originals_hash_to_instance ={}
    dict_replacements = {}
    logging.info("find replacements...")
    for instance in list_all:
        instance_hash = calculate_hash(instance)
        if instance_hash in set_original_hashes:
            set_duplicates.add(instance)
            dict_replacements[instance] = dict_originals_hash_to_instance[instance_hash]
        else:
            set_original_hashes.add(instance_hash)
            dict_originals_hash_to_instance[instance_hash] = instance
    logging.info("found " + str(len(set_original_hashes)) + " originals and " + str(len(set_duplicates)) + " duplicates")
    return dict_replacements, set_duplicates

def calculate_hash(step_instance):
    """
    calculate a hash of an instance in an IFC step file
    all attributes are considered, but not the instance id nor the instances class
    if an attribute is an ifcopenshell entity its id is hashed
    if the attribute is a primitive the python hash function is used
    :param step_instance:
    :return:
    """
    attribute_dict = step_instance.get_info()
    instance_hash = 0
    for attribute_name in attribute_dict:  # check for all attributes
        if attribute_name == 'id' or attribute_name == 'type':  # but not for the id and type
            continue
        if attribute_dict[attribute_name].__class__ == tuple:
            attribute_hash = 0
            for item in attribute_dict[attribute_name]:
                if isinstance(item, ifcopenshell.entity_instance): #if it is an instance
                    item.is_a()
                    attribute_hash = attribute_hash * 31 + item.id()
                else: #if it is a primitive
                    attribute_hash = attribute_hash * 31 + hash(item)
        else:
            if isinstance(attribute_dict[attribute_name], ifcopenshell.entity_instance): #if it is an instance
                attribute_dict[attribute_name].is_a()
                attribute_hash = attribute_dict[attribute_name].id()
            else:  # if it is a primitive
                attribute_hash = hash(attribute_dict[attribute_name])

        instance_hash = 31 * instance_hash + attribute_hash
    return instance_hash

def compare_instances(instance1, instance2):
    """
    compares two instances in an IFC file
    "gleich" if all attributes are the same (type and instance id are not considered)
    they should be of the same type (compare instances of different classes was not tested)
    :param instance1:
    :param instance2:
    :return: "gleich" or "ungleich"
    """
    dict1 = instance1.get_info()
    dict2 = instance2.get_info()
    for key in dict1:
        if key == 'id':
            continue
        if dict1[key] == dict2[key]:
            continue
        else:
            return "ungleich"
    return "gleich"

def equals(instance1, instance2):
    """
    compares two instances in an IFC file
    "True" if all attributes are the same (type and instance id are not considered)
    they should be of the same type (compare instances of different classes was not tested)
    :param instance1:
    :param instance2:
    :return: True or False
    """
    dict1 = instance1.get_info()
    dict2 = instance2.get_info()
    for key in dict1: #check for all attributes
        if key == 'id':  #but not for the id
            continue
        if dict1[key] == dict2[key]: #TODO maybe it is faster to compare the id of the attribute is not a primitive, but an object
            continue
        else:
            return False
    return True

def get_replacements_old(ioh):
    """
    checks for duplicates in a list of objects
    runs 25s for 1264 IfcOwnerHistory
    :param ioh: list of objects
    :return: list of the replacement (first equal entity is given), set of unique items
    """
    eindeutige = list()
    ersatz = list()
    i=0
    for oh in ioh:
        i=i+1
        partner_gefunden = False
        if len(eindeutige) == 0:
            eindeutige.append(oh)
            ersatz.append(oh)
            continue
        for ein in eindeutige:
            if compare_instances(ein, oh) == "gleich":
                ersatz.append(ein)
                partner_gefunden = True
                break
        if not partner_gefunden:
            eindeutige.append(oh)
            ersatz.append(oh)
    i=0
    dict = {}
    for oh in ioh:
        dict[oh] = ersatz[i]
        i = i+1
    return dict, set(eindeutige)


def set_of_IfcElements_in_set_of_ircp(set_of_ircp):
    Ro_set_of_elements = set()
    for ircp in set_of_ircp:
        Ro_set_of_elements.add(ircp.RelatingPort.Nests[0].RelatingObject)
        Ro_set_of_elements.add(ircp.RelatedPort.Nests[0].RelatingObject)
    return Ro_set_of_elements

def set_of_IfcDistributionPorts_in_set_of_ircp(set_of_ircp):
    Ro_set_of_ports = set()
    for ircp in set_of_ircp:
        Ro_set_of_ports.add(ircp.RelatingPort)
        Ro_set_of_ports.add(ircp.RelatedPort)
    return Ro_set_of_ports

def set_of_IfcRelConnectsPorts_in_Ro(ifcfile):
    Ro_set_of_ircp = set()
    for ircp in ifcfile.by_type('IfcRelConnectsPorts'):
        if ircp.Name == "new edge from graph algorithm (replacement graph Ro)" or ircp.Name == "new edge from graph algorithm (Ro_2 (manuelle Nachpflege))":
            Ro_set_of_ircp.add(ircp)
    return Ro_set_of_ircp