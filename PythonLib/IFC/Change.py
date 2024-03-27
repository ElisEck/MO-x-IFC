"""
functions to alter existing IFC files
"""

import ifcopenshell
import ifcopenshell.api
import ifcopenshell.util.placement
import logging
import pandas as pd
import re

from PythonLib.IFC.GeometryCreator import create_BoundingBox
from PythonLib.IFC.HelpersGeometry import get_2D_coordinates_of_element
from PythonLib.IFC.Inspect import get_replacements_old

def attach_propertyvalue_tolistofguids(element_set, property_name, pv, propertyset_name, owner_history, ifcfile):
    pv = ifcfile.createIfcPropertySingleValue(property_name, None, ifcfile.create_entity("IfcText", pv), None)
    ps = ifcfile.createIfcPropertySet(ifcopenshell.guid.new(), owner_history, propertyset_name, None, [pv])
    ifcfile.createIfcRelDefinesByProperties(ifcopenshell.guid.new(), owner_history, None, None, list(element_set), ps)
    return ifcfile

def attach_properties_from_excel_to_ifcfile_new(ifcfile, filename_excelimport):
    """
    Anforderungen an die Exceltabelle
        - Name des Tabellenblatts: "Tabelle1"
        - Spalte D = GlobalID
        - Spalten D:G werden eingelesen
        - Spaltenbezeichnungen müssen lauten: PSet_Name, Property_Name, Property_Wert
        - Header in Zeile 2
        - Werte ab Zeile 3
        (kann aber leicht umkonfiguriert werden s.u.)
    :param ifcfile:
    :param filename_excelimport:
    :return:
    """
    # zu setzende Parameter aus Excel lesen
    # TODO Umlaute in Property-Namen (und wahrscheinlich auch in PSet-Namen) behandeln
    parameterliste = pd.read_excel(filename_excelimport,
                                   sheet_name='Tabelle1',
                                   usecols='D:G',
                                   index_col=0,  # Spaltennummer in der die GUID steht (gezählt, aber der erster einzulesenden Spalte mit 0)
                                   # skiprows=2,#komplett leere Zeilen werden sowieso übersprungen. Falls beschriftete übersprungen werden sollen - hier angeben - header geht dann aber kaputt
                                   header=1,  # in welcher Zeile (Zählung beginnt bei 0) steht der Column-Name (Achtung: darf kein Leerzeichen enthalten), alles was davor steht, wird nicht eingelesen
                                   na_values=None, keep_default_na=True, na_filter=True, verbose=False, mangle_dupe_cols=True)

    ifcfile, owner_history, personAndOrganization, application = create_IfcOwnerHistory(ifcfile)

    # iteriere über alle Objekte die Properties bekommen sollen
    for row in parameterliste.iterrows():
        property_values = []
        if type(row[1]['Property_Wert']) == str:
            property_value = ifcfile.createIfcPropertySingleValue(row[1]['Property_Name'], None, ifcfile.create_entity("IfcText", row[1]['Property_Wert']), None)
        elif (type(row[1]['Property_Wert']) == int) or (type(row[1]['Property_Wert']) == float):
            property_value = ifcfile.createIfcPropertySingleValue(row[1]['Property_Name'], None, ifcfile.create_entity("IfcReal", row[1]['Property_Wert']), None)
        else:
            print("row2: " + str(row))
        property_values.append(property_value)
        property_set = ifcfile.createIfcPropertySet(ifcopenshell.guid.new(), owner_history, row[1]['PSet_Name'], None, property_values)
        ifcreldefbyp = ifcfile.createIfcRelDefinesByProperties(ifcopenshell.guid.new(), owner_history, None, None, [ifcfile.by_guid(row[0])], property_set)

def attach_properties_from_excel_to_ifcfile_old(ifcfile, filename_excelimport, df_allIfcRoot):
    """
    Anforderungen an die Exceltabelle
        - Name des Tabellenblatts: "Tabelle1"
        - Spalte D = GlobalID
        - Spalten D:G werden eingelesen
        - Spaltenbezeichnungen müssen lauten: PSet_Name, Property_Name, Property_Wert
        - Header in Zeile 2
        - Werte ab Zeile 3
        (kann aber leicht umkonfiguriert werden s.u.)
    :param ifcfile:
    :param filename_excelimport:
    :param df_allIfcRoot:
    :return:
    """
    # zu setzende Parameter aus Excel lesen
    # TODO Umlaute in Property-Namen (und wahrscheinlich auch in PSet-Namen) behandeln
    parameterliste = pd.read_excel(filename_excelimport,
                                   sheet_name='Tabelle1',
                                   usecols='D:G',
                                   index_col=2,  # Spaltennummer in der die GUID steht (gezählt, aber der erster einzulesenden Spalte mit 0)
                                   # skiprows=2,#komplett leere Zeilen werden sowieso übersprungen. Falls beschriftete übersprungen werden sollen - hier angeben - header geht dann aber kaputt
                                   header=1,  # in welcher Zeile (Zählung beginnt bei 0) steht der Column-Name (Achtung: darf kein Leerzeichen enthalten), alles was davor steht, wird nicht eingelesen
                                   na_values=None, keep_default_na=True, na_filter=True, verbose=False, mangle_dupe_cols=True)

    # Exceltabelle und IFC-File mergen: übrig bleibt eine dataFrame mit allen Objekten die im IFC und im Excel enthalten sind
    zuErgaenzendeProperties = pd.merge(df_allIfcRoot, parameterliste, left_index=True, right_index=True)  # based on index (=GlobalId bei beiden DataFrames)

    # erzeuge eine Liste aller betroffenen GUIDs (die neue Properties bekommen)
    df_reset = zuErgaenzendeProperties.reset_index()
    guidListe = df_reset['GlobalID'].drop_duplicates(keep='first', inplace=False)  # series

    # erstbeste owner-History nehmen für die neuen Objekte
    # TODO eigene korrekte OwnerHistory anlegen
    owner_history = ifcfile.by_type("IfcOwnerHistory")[0]

    # iteriere über alle Objekte die Properties bekommen sollen
    for objectguid in guidListe:
        print("objectguid: " + objectguid)
        subset1 = zuErgaenzendeProperties.filter(like=objectguid, axis=0)  # subset1: enthält alle Änderungen an einem Objekt
        subset1 = subset1.reset_index()
        psetliste = subset1['PSet_Name'].drop_duplicates(keep='first', inplace=False)
        subset1 = subset1.set_index('PSet_Name')

        # iteriere über alle PropertySets eines Objekts
        for pset in psetliste:
            print("PSet_Name: " + pset)
            subset2 = subset1.filter(like=pset, axis=0)  # subset2: enthält alle Änderungen in einem PropertySet
            property_values = []

            # iteriere über alle Properties in einem PSets
            for index, row in subset2.iterrows():
                # print("row1 " + row)
                if type(row['Property_Wert']) == str:
                    property_value = ifcfile.createIfcPropertySingleValue(row['Property_Name'], None, ifcfile.create_entity("IfcText", row['Property_Wert']), None)
                elif (type(row['Property_Wert']) == int) or (type(row['Property_Wert']) == float):
                    property_value = ifcfile.createIfcPropertySingleValue(row['Property_Name'], None, ifcfile.create_entity("IfcReal", row['Property_Wert']), None)
                else:
                    print("row2: " + str(row))
                property_values.append(property_value)

            property_set = ifcfile.createIfcPropertySet(ifcfile.guid.new(), owner_history, pset, None, property_values)

        ifcreldefbyp = ifcfile.createIfcRelDefinesByProperties(ifcfile.guid.new(), owner_history, None, None, [ifcfile.by_guid(objectguid)], property_set)


def correct_storey_height(ifcfile, new_z):
    '''
    only applicable for files containing a single storey
    :param ifcfile:
    :param new_z:
    :return:
    '''
    storey = ifcfile.by_type("IfcBuildingStorey")[0]
    storey_origin = ifcfile.createIfcCartesianPoint((0.0, 0.0, new_z))
    ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=storey.ObjectPlacement.RelativePlacement, attributes={"Location": storey_origin})


def move_elements_from_site_to_storey(ifcfile):
    '''
    only applicable for storey wise ifcfiles
    :param ifcfile:
    :return:
    '''
    site = ifcfile.by_type("IfcSite")[0]
    storey = ifcfile.by_type("IfcBuildingStorey")[0]
    elements_to_move = site.ContainsElements[0].RelatedElements
    rcss_site = site.ContainsElements[0]
    rcss_storey = storey.ContainsElements[0]
    elements_in_storey_old = rcss_storey.RelatedElements
    elements_in_storey_new = elements_to_move + elements_in_storey_old
    elements_in_site_new = tuple()
    ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=rcss_site, attributes={"RelatedElements": elements_in_site_new})
    ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=rcss_storey, attributes={"RelatedElements": elements_in_storey_new})
    for element in elements_to_move:
        ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=element.ObjectPlacement, attributes={"PlacementRelTo": storey.ObjectPlacement})


def add_element_to_IfcRelContainedInSpatialStructure(ifcfile, ircss, new_element):
    old_elements_in_container = ircss.RelatedElements
    new_elements_in_container = old_elements_in_container + (new_element,)
    ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=ircss, attributes={"RelatedElements": new_elements_in_container})


def add_element_to_system_assignment(ifcfile, irag, new_element):
    old_elements_in_container = irag.RelatedObjects
    new_elements_in_container = old_elements_in_container + (new_element,)
    ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=irag, attributes={"RelatedObjects": new_elements_in_container})


def remove_element_from_system_assignment(ifcfile, irag, element_to_remove):
    new_elements = list()
    for oe in irag.RelatedObjects:
        if not oe == element_to_remove:
            new_elements.append(oe)
    ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=irag, attributes={"RelatedObjects": tuple(new_elements)})


def add_element_to_type_assignment(ifcfile, irdbt, new_element):
    old_elements_in_container = irdbt.RelatedObjects
    new_elements_in_container = old_elements_in_container + (new_element,)
    ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=irdbt, attributes={"RelatedObjects": new_elements_in_container})


def remove_element_from_type_assignment(ifcfile, irdbt, element_to_remove):
    new_elements = list()
    for oe in irdbt.RelatedObjects:
        if not oe == element_to_remove:
            new_elements.append(oe)
    ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=irdbt, attributes={"RelatedObjects": tuple(new_elements)})


def replace_IfcPresentationStyleAssignment_by_direct_style_assignment(ifcfile):
    #TODO finish implementation and test
    isis=ifcfile.by_type('IfcStyledItem')
    for isi in isis:
        if isi.Styles[0].is_a('IfcPresentationStyleAssignment'):
            isi_styles_old = isi.Styles #expecting a tuple with 1 entry
            isi_styles_new = isi.Styles[0].Styles[0] #also expecting a tuple with 1 entry
            #TODO overwrite and skip intermediate instance IfcPresentationStyleAssignment

def rename_elements_with_duplicate_name(ifcfile):
    ifcfile.by_type('IfcElement')
    elementnames = dict()
    for element in ifcfile.by_type('IfcElement'):
        if element.Name in elementnames:
            elementnames[element.Name] = elementnames[element.Name] + 1
            namestring = str(element.Name) + "_" +str(elementnames[element.Name])
            ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=element, attributes={"Name": namestring})
        else:
            elementnames[element.Name] = 1


def replace_IfcShapeRepresentation_by_BoundingBox(ifcfile, element, subcontext):
    logging.info("replacing geometry of " + str(element))
    s = ifcopenshell.geom.settings()
    bb = create_BoundingBox(ifcfile, element)
    if bb:
        sr_old = element.Representation.Representations
        sr_new = ifcfile.createIfcShapeRepresentation(subcontext, 'Box', 'BoundingBox', (bb, ))
        #ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=element.Representation, attributes={"Representations": sr_old +(sr_new,)}) #complement boundingbox
        ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=element.Representation, attributes={"Representations": (sr_new,)}) #replace old represenation by boundingbox
        for sr in sr_old:
            for item in sr.Items:
                ifcfile.remove(item)
            ifcfile.remove(sr)
        """
        for oldr in sr_old:
            list_directly_connected = ifcfile.traverse(oldr)
            logging.info("removing " + str(len(list_directly_connected)) + " elements")
            for el in list_directly_connected:
                ifcfile.remove(el)
                """



def replace_ifcRelConnectsPortToElement_with_ifcRelNests(ifcfile):
    """
    :param ifcfile that will be manipulated: one IfcRelNests for each element containing (one or more) port(s)
    """
    ircpes = ifcfile.by_type('IFCRELCONNECTSPORTTOELEMENT')
    dict_zuordnung = {}
    i=0
    j=0
    for ircpe in ircpes:
        #zuordnung_alt_guid = s.GlobalId
        if ircpe.RelatedElement != None: #solche Konstellationen ergeben sich durch das löschen von Objekten
            element_guid = ircpe.RelatedElement.GlobalId
            if element_guid in dict_zuordnung.keys():
                list_ports = dict_zuordnung[element_guid]
                i=i+1
            else:
                list_ports = []
                dict_zuordnung[element_guid] = list_ports
                j=j+1
            port = ircpe.RelatingPort
            list_ports.append(port)
            #ifcfile.remove(ircpe) #TODO remove unnecessary IFCRELCONNECTSPORTTOELEMENT - yields error somehow... https://github.com/IfcOpenShell/IfcOpenShell/issues/1246
        else:
            logging.info(str(ircpe) + " - was not replaced by IfcRelNests, since no element is present")
    for element_guid in dict_zuordnung:
        element = ifcfile.by_guid(element_guid)
        ports = dict_zuordnung[element_guid]
        ifcfile.createIfcRelNests(ifcopenshell.guid.new(), None, None, None, element, ports)


def round_coordinates_of_IfcCartesianPoints(ifcfile, digits=2):
    """
    round coordinates of all IfcCartesianPoints in file
    :param ifcfile:
    :param digits: standard: 2
    :return: nothing
    """
    for cp in ifcfile.by_type('IfcCartesianPoint'):
        if len(cp.Coordinates) == 3:
            rounded_coordinates = (round(cp.Coordinates[0], digits), round(cp.Coordinates[1], digits), round(cp.Coordinates[2], digits))
        else: #2D point
            rounded_coordinates = (round(cp.Coordinates[0], digits), round(cp.Coordinates[1], digits))
        ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=cp, attributes={"Coordinates": rounded_coordinates})



def replace_IfcOwnerHistory_Duplicates_old_DONT_USE(ifcfile):
    """
    old, deprecated, is not working correctly! (the replacing IfcOwnerHistory ist changed with a .MODIFIED. attribute!)
    DEPRECATED: use remove_duplicates_of_certain_ifcclass(sourcefile, targetfile_stub, ifcclass) instead
    :param ifcfile:
    :return:
    """
    logging.info("Get all IfcOwnerHistory")
    ioh = ifcfile.by_type("IfcOwnerHistory")
    logging.info("Found " + str(len(ioh)) + " IfcOwnerHistory. Find replacements now...")
    ersatzdict, eindeutige = get_replacements_old(ioh)
    ir = ifcfile.by_type('IfcRoot')
    logging.info("Changing Model...")
    for rootelement in ir:
        if rootelement.OwnerHistory == None:
            continue
        oh_old = rootelement.OwnerHistory
        if rootelement.OwnerHistory.id() != ersatzdict[rootelement.OwnerHistory].id():
            ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=rootelement, attributes={"OwnerHistory": ersatzdict[rootelement.OwnerHistory]})
            #ifcfile.remove(oh_old)

def replace_IfcOwnerHistory_Duplicates_DONT_USE(ifcfile, inputfilepath, outputfilepath):
    """
    works with text replacement, therefore the original ifcfilehandle is not changed
    DEPRECATED: use remove_duplicates_of_certain_ifcclass(sourcefile, targetfile_stub, ifcclass) instead
    :param ifcfile:
    :param inputfilepath:
    :param outputfilepath:
    :return: the changed ifcfile
    """
    logging.info("Get all IfcOwnerHistory")
    ioh = ifcfile.by_type("IfcOwnerHistory")
    logging.info("Found " + str(len(ioh)) + " IfcOwnerHistory. Find replacements now...")
    ersatzdict, set_unique_oh = get_replacements_old(ioh)
    replacements = {}
    for oh in ersatzdict:
        old = ",#" + str(oh.id()) + ","
        new = ",#" + str(ersatzdict[oh].id()) + ","
        if old==new:
            continue
        replacements[old] = new
    logging.info("Writing to file: " + outputfilepath)
    with open(inputfilepath) as infile, open(outputfilepath, 'w') as outfile:
        for line in infile:
            for src, target in replacements.items():
                line = line.replace(src, target)
            outfile.write(line)
    logging.info("Reimporting file: " + outputfilepath)
    ifcfile2 = ifcopenshell.open(outputfilepath)
    set_all_oh = set(ioh)
    set_oh_to_delete = set_all_oh.difference(set_unique_oh)
    logging.info("deleting in file: " + outputfilepath)
    for deletee in set_oh_to_delete:
        ifcfile2.remove(ifcfile2.by_id(deletee.id()))
    logging.info("overwrite file: " + outputfilepath)
    ifcfile2.write(outputfilepath)
    return ifcfile2

def replace_IfcPolyLoop_Duplicates_DONT_USE(ifcfile):
    """
    DEPRECATED: use remove_duplicates_of_certain_ifcclass(sourcefile, targetfile_stub, ifcclass) instead
    :param ifcfile:
    :return:
    """
    logging.info("Get all IfcPolyLoop")
    ioh = ifcfile.by_type("IfcPolyLoop")
    logging.info("Found " + str(len(ioh)) + " IfcPolyLoop. Find replacements now...")
    ersatzdict, eindeutige = get_replacements_old(ioh)
    ir = ifcfile.by_type('IfcRoot')
    logging.info("Changing Model...")
    for rootelement in ir:
        if rootelement.OwnerHistory == None:
            continue
        oh_old = rootelement.OwnerHistory
        if rootelement.OwnerHistory.id() != ersatzdict[rootelement.OwnerHistory].id():
            ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=rootelement, attributes={"OwnerHistory": ersatzdict[rootelement.OwnerHistory]})

def replace_instance_ids_text_based(ersatzdict, inputfilepath, outputfilepath):
    """
    replace all instance references in the file read from inputfilepath, remove the instance definitions of the then orphaned instances
    does not make use of any ifcopenshell functions! only String operations applied
    doesn't return anything
    :param ersatzdict: should contain the instances as objects (not yet the instance ids), these are generated within this function
    :param inputfilepath:
    :param outputfilepath:
    """
    replacements = {}
    for oh in ersatzdict:
        old = "#" + str(oh.id()) + "" #ohne Komma
        new = "#" + str(ersatzdict[oh].id()) + "" #ohne Komma
        if old==new:
            continue
        replacements[old] = new
    logging.info("Writing to file: " + outputfilepath)
    with open(inputfilepath) as infile, open(outputfilepath, 'w') as outfile:
        for line in infile:
            if line[0] != '#': #ignore empty and header lines
                outfile.write(line)
                continue
            references_in_line = re.compile('#\d*').findall(line)
            instance_id = references_in_line.pop(0)#ersten Treffer ignorieren: das ist die Instanz-ID
            if instance_id in replacements:
                continue #line not written to outfile
            for ref in references_in_line: #without the first element
                if ref in replacements: #check for each element whether it should be replaced
                    line = line.replace(ref, replacements[ref])
            outfile.write(line)

def remove_already_replaced_duplicates_DONT_USE(outputfilepath, set_duplicates):
    """
    makes use of IfcOpenShell Functions, is NOT working correctly
    :param outputfilepath:
    :param set_duplicates:
    :return:
    """
    logging.info("Reimporting file: " + outputfilepath)
    ifcfile2 = ifcopenshell.open(outputfilepath)
    logging.info("deleting in filehandle")
    for deletee in set_duplicates:
        ifcfile2.remove(ifcfile2.by_id(deletee.id())) #TODO Achtung InstanzIDs in den beiden Files stimmen nicht überein
    logging.info("overwrite file: " + outputfilepath)
    ifcfile2.write(outputfilepath)
    return ifcfile2

def apply_Modelica_port_description(ifcfile, set_of_elements):
    set_of_elements_with123_ports = set()
    set_of_elements_odd_ports = set()
    set_of_elements_without_ports = set()
    for element in set_of_elements:
        try:
            ports = element.IsNestedBy[0].RelatedObjects
            if len(ports) == 1:
                ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=ports[0], attributes={"Description": "port"})
                set_of_elements_with123_ports.add(element)
            elif len(ports) == 2:
                ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=ports[0], attributes={"Description": "port_a"})
                ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=ports[1], attributes={"Description": "port_b"})
                set_of_elements_with123_ports.add(element)
            elif len(ports) == 3:
                ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=ports[0], attributes={"Description": "port_1"})
                ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=ports[1], attributes={"Description": "port_2"})
                ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=ports[2], attributes={"Description": "port_3"})
                set_of_elements_with123_ports.add(element)
            else:
                logging.info(str(element.Name) + " has " + str(len(ports)) + " ports")
                set_of_elements_odd_ports.add(element)
        except:
            set_of_elements_without_ports.add(element)
    return set_of_elements_with123_ports, set_of_elements_odd_ports, set_of_elements_without_ports

def apply_port_numbering(ifcfile, set_of_elements):
    i = 0
    for element in set_of_elements:
        try:
            ports = element.IsNestedBy[0].RelatedObjects
            for port in ports:
                i = i + 1
                ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=port, attributes={"Name": "port" + str(i)})
        except:
            a=1 #element without ports

def change_placement_to_coordinates_in_properties(ifcfile, element, ifc_dir_x, ifc_dir_z):
    """
    change placement of element to its 2D coordinates gives as properties
    :param ifcfile:
    :param element:
    :param ifc_dir_x:
    :param ifc_dir_z:
    :return: xcoord, ycoord
    """
    xcoord, ycoord = get_2D_coordinates_of_element(element)
    #handle unset coordinates
    if xcoord == None: xcoord = 0.0
    if ycoord == None: ycoord = 0.0
    xcoord = float(xcoord) #in case it was an integer
    ycoord = float(ycoord)

    base_axis2placement = ifcfile.createIfcAxis2Placement3D(ifcfile.createIfcCartesianPoint([xcoord, ycoord, 0.0]), ifc_dir_z, ifc_dir_x)
    placement = ifcfile.createIfcLocalPlacement(None, base_axis2placement) #TODO placement jetzt doppelt drin?, relative placement
    ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=element, attributes={"ObjectPlacement": placement})
    return xcoord, ycoord