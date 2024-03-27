"""
functions remove things from existing IFC files
"""
import os
import ifcopenshell
import ifcopenshell.util.placement
import  ifcopenshell.api
import logging

from PythonLib.IFC.Inspect import get_instance_list_old, get_instance_list, set_of_instance_ids, get_duplicates_and_their_replacements, set_of_IfcDistributionPorts_in_set_of_ircp, set_of_IfcElements_in_set_of_ircp, set_of_instances


def remove_IfcRelConnectsPortToElement(ifcfile):
    deletionlist = []
    logging.info("removing all IfcRelConnectsPortToElement (already replaced by IfcRelNests)")
    for ircpe in ifcfile.by_type('IFCRELCONNECTSPORTTOELEMENT'):
        deletionlist.append(ircpe)
        ifcfile.remove(ircpe)
    return deletionlist

def remove_IfcRelSpaceBoundary(ifcfile):
    deletionlist = []
    logging.info("removing all IfcRelSpaceBoundary")
    for ircpe in ifcfile.by_type('IfcRelSpaceBoundary'):
        deletionlist.append(ircpe)
        ifcfile.remove(ircpe)
    return deletionlist

def remove_2D_representations(ifcfile):
    logging.info("removing 2D representations")
    itemcount = 0
    representationcount = 0
    for sr in ifcfile.by_type('IfcShapeRepresentation'):
        if sr.RepresentationIdentifier == 'Schematics' or sr.RepresentationIdentifier == 'Schematics Text':  # it is not possible to recognize deletion candidates by assignment to a 2D context since simpleBIM moves everything to the IFCGEOMETRICREPRESENTATIONSUBCONTEXT('Body', 'Model')
            for item in sr.Items:
                # logging.info("removed: " + str(item))
                ifcfile.remove(item)
                itemcount += 1
            # logging.info("removed: " + str(repr))
            ifcfile.remove(sr)
            representationcount += 1
    logging.info("removed " + str(representationcount) + " representations, " + str(itemcount) + " items")

def remove_3D_representations(ifcfile):
    """
    removes only the link to the representation, not the geometric items
    :param ifcfile:
    :return:
    """
    logging.info("removing 3D representations")
    itemcount = 0
    representationcount = 0
    for sr in ifcfile.by_type('IfcShapeRepresentation'):
        if sr.RepresentationIdentifier == 'Body':  # it is not possible to recognize deletion candidates by assignment to a 2D context since simpleBIM moves everything to the IFCGEOMETRICREPRESENTATIONSUBCONTEXT('Body', 'Model')
            for item in sr.Items:
                # logging.info("removed: " + str(item))
                ifcfile.remove(item)
                itemcount += 1
            # logging.info("removed: " + str(repr))
            ifcfile.remove(sr)
            representationcount += 1
    logging.info("removed " + str(representationcount) + " representations, " + str(itemcount) + " items")

def remove_3D_representations_full(ifcfile):
    """
    removes the link to the representation and the geometric items
    :param ifcfile:
    :return:
    """
    logging.info("removing 3D representations (if RepresentationIdentifier equals \"Body\")")
    deletioncount = 0
    itemcount = 0
    representationcount = 0
    for sr in ifcfile.by_type('IfcShapeRepresentation'):
        if sr.RepresentationIdentifier == 'Body':  # it is not possible to recognize deletion candidates by assignment to a 2D context since simpleBIM moves everything to the IFCGEOMETRICREPRESENTATIONSUBCONTEXT('Body', 'Model')
            logging.info("======================== processing " + str(sr) + " - geometry of " + str(sr.OfProductRepresentation[0].ShapeOfProduct[0]))
           # if sr.id() == 379773:
           #     logging.info("skipping " + str(sr))
           #     continue
            for representation_item in sr.Items:
                itemcount += 1
                # logging.info("removed: " + str(item))
                logging.info(str(itemcount) + " traversing " + str(representation_item) )
                connected_to_item = ifcfile.traverse(representation_item)
                logging.info("...found " + str(len(connected_to_item)) + " instances to remove. Removing...")
                for i in connected_to_item:
                    #print(i)
                    ifcfile.remove(i)
                    deletioncount += 1
                if itemcount % 1000 == 0:
                    target = "33 ohne 3D Geometrie/33_aus_23_" + str(itemcount) + ".ifc"
                    logging.info("writing file: " + target)
                    ifcfile.write(target)
                #ifcfile.remove(representation_item)
                #itemcount += 1
            # logging.info("removed: " + str(repr))
            ifcfile.remove(sr)
            representationcount += 1
    logging.info("removed " + str(representationcount) + " IfcShapeRepresentation with " + str(itemcount) + " items. Together with connected instances " + str(deletioncount) + " instances have been removed")

def remove_given_IfcShapeRepresentation(ifcfile, set_of_unused_sr):
    representationcount = 0
    itemcount = 0
    deletioncount = 0
    for sr in set_of_unused_sr:
        representationcount += 1
        logging.info(str(representationcount))
        for representation_item in sr.Items:
            itemcount += 1
            logging.info("traversing " + str(representation_item) )
            connected_to_item = ifcfile.traverse(representation_item)
            logging.info("...found " + str(len(connected_to_item)) + " instances to remove. Removing...")
            for i in connected_to_item:
                ifcfile.remove(i)
                deletioncount += 1
        ifcfile.remove(sr)
    logging.info("removed " + str(representationcount) + " IfcShapeRepresentation with " + str(itemcount) + " items. Together with connected instances " + str(deletioncount) + " instances have been removed")
def remove_empty_contexts(ifcfile):
    contextcount = 0
    for context in ifcfile.by_type('IfcGeometricRepresentationContext'):  # including subcontexts
        if len(context.RepresentationsInContext) == 0:  # don't remove contexts that have subcontexts
            logging.info(str(context) + " Anzahl enthaltene Representations: " + str(len(context.RepresentationsInContext)) + "---removed")
            ifcfile.remove(context)
            contextcount += 1
        else:
            logging.info(str(context) + " Anzahl enthaltene Representations: " + str(len(context.RepresentationsInContext)))
    logging.info("removed " + str(contextcount) + "(Sub)contexts")
    # return ifcfile #obsolet, is a pointer anyway


def remove_unreferenced_instances(ifcfile, instancelist):
    deletioncount = 0
    instancecount = 0
    deletionlist = []
    for instance in instancelist:
        instancecount += 1
        if instancecount % 100000 == 0:
            logging.info("instance no " +  str(instancecount) + " - " + str(instance))
        if len(ifcfile.get_inverse(instance)) == 0 \
                and not instance.is_a('IfcRelationship') \
                and not instance.is_a('IFCSTYLEDITEM') \
                and not instance.is_a('IfcPresentationLayerAssignment'):
            deletionlist.append(instance)
            deletioncount += 1
            ifcfile.remove(instance)
    logging.info("deleted " + str(deletioncount) + " instances without inverses")
    return deletionlist

def remove_unreferenced_instances_iterative(ifcfile):
    deletionlist = [1,1]
    iterationcount = 0
    while len(deletionlist)>0:
        iterationcount += 1
        logging.info("deletion run #" + str(iterationcount))
        instancelist = get_instance_list_old(ifcfile, 4521563)  # max instanceID
        deletionlist = remove_unreferenced_instances(ifcfile, instancelist)

def remove_unreferenced_instances_iterative_2(ifcfile):
    deletionlist = [1,1]
    iterationcount = 0
    while len(deletionlist)>0:
        iterationcount += 1
        logging.info("deletion run #" + str(iterationcount))
        logging.info("creating instance list")
        instancelist = get_instance_list(ifcfile)
        logging.info("removing")
        deletionlist = remove_unreferenced_instances(ifcfile, instancelist)


def set_of_unreferenced_instances(ifcfile):
    logging.info("checking for unreferenced instances by traversing all IfcRoot (" + str(len(ifcfile.by_type('IfcRoot'))) + ")")
    set_of_unreferenced_instances = set_of_instances(ifcfile)
    set_of_referenced_instances = set()
    i=0
    for ir in ifcfile.by_type('IfcRoot'):
        list_directly_connected = ifcfile.traverse(ir)
        set_of_referenced_instances = set_of_referenced_instances.union(set(list_directly_connected))
        set_of_unreferenced_instances = set_of_unreferenced_instances.difference(set(list_directly_connected))
        if i%100 == 0:
            logging.info("run: " + str(i) + "\t processing " + str(ir) + "\t unreferenced: " + str(len(set_of_unreferenced_instances)) + "\t referenced: " + str(len(set_of_referenced_instances)))
        i = i + 1
    logging.info("final run completed -  unreferenced: " + str(len(set_of_unreferenced_instances)) + "\t referenced: " + str(len(set_of_referenced_instances)))
    return set_of_unreferenced_instances, set_of_referenced_instances

def set_of_referenced_instances_of_system(ifcfile, system):
    dict_systems = {
        'Server': ['KK3_Server_VL', 'KK3_Server_RL', 'KK3_Server'],
        'RLT': ['KK4_RLT_VL', 'KK4_RLT_RL', 'KK4_RLT'],
        'ULK': ['KK2_ULK_VL', 'KK2_ULK_RL', 'KK2_ULK'],
        'Rueckkuehlung': ['KK_Rueckkuehlung_VL', 'KK_Rueckkuehlung_RL', 'KK_Rueckkuehlung'],
        'Erzeugung': ['KK_Erzeugung_VL', 'KK_Erzeugung_RL', 'KK_Erzeugung'],
        'BKA': ['KK1_BKA_VL', 'KK1_BKA_RL', 'KK1_BKA'],
    }
    logging.info("iterating over elements")
    set_of_elements_in_system = set()
    for element in ifcfile.by_type('IfcElement'):
        if len(element.HasAssignments) > 0:
            if element.HasAssignments[0].RelatingGroup.Name in dict_systems[system]:
                set_of_elements_in_system.add(element)
    logging.info("checking for referenced instances by traversing all elements in system (" + str(len(set_of_elements_in_system)) + ")")
    set_of_ports_of_elements = set()
    set_of_relations_of_elements = set()
    for element in set_of_elements_in_system:
        for port in element.IsNestedBy[0].RelatedObjects:
            set_of_ports_of_elements.add(port)
            set_of_ports_of_elements.add(element.IsNestedBy[0])
            #add IfcRelConnectsPorts
            try:
                set_of_ports_of_elements.add(port.ConnectedTo[0]) #ircp - Achtung: der zweite Port der Verbindung wird hier bewusst nicht kopiert - könnte zu einem anderen System gehören
            except:
                try:
                    set_of_ports_of_elements.add(port.ConnectedFrom[0]) #ircp - Achtung: der zweite Port der Verbindung wird hier bewusst nicht kopiert - könnte zu einem anderen System gehören
                except:
                    logging.info("port" + str(port) + " is unconnected") #TODO maybe remove this port? why should it be kept if it is unconnected?
        for relation in element.IsDefinedBy:
            set_of_relations_of_elements.add(relation)
        for relation in element.IsTypedBy:
            set_of_relations_of_elements.add(relation)
        for iratg in element.HasAssignments: #IFCRELASSIGNSTOGROUP
            set_of_relations_of_elements.add(iratg)
    #TODO als keep IfcRelContainedInSpatialStructure
    for relation in set_of_relations_of_elements: #remove Elements that are not part of the system
        ro_in_system = set()
        for ro in relation.RelatedObjects:
            try:
                if ro.HasAssignments[0].RelatingGroup.Name in dict_systems[system]:
                    ro_in_system.add(ro)
            except:
                continue
        ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=relation, attributes={"RelatedObjects": tuple(ro_in_system)})
    set_of_keepers = set_of_elements_in_system.union(set_of_ports_of_elements).union(set_of_relations_of_elements)
    logging.info("checking for referenced instances by traversing all elements and ports and relations in system (" + str(len(set_of_keepers)) + ")")
    set_of_referenced_instances = set()
    i=0
    for ir in set_of_keepers:
        set_of_referenced_instances.add(ir)
        list_directly_connected = ifcfile.traverse(ir)
        set_of_referenced_instances = set_of_referenced_instances.union(set(list_directly_connected))
        if i%100 == 0:
            logging.info("run: " + str(i) + "\t processing " + str(ir) + "\t referenced: " + str(len(set_of_referenced_instances)))
        i = i + 1
    logging.info("final run completed -  referenced: " + str(len(set_of_referenced_instances)))
    return set_of_referenced_instances


def traverse_remove_unused_instances(ifcfile, target):
    """
    writes ifcfile without unreferencet instances
    step 1: checking which instances are unused
    step 2: removing the unused ones or copying the used ones to a new file (whatever is faster)
    :param ifcfile: ifcfile to investigate
    :param target: path to targetfile
    :return:
    """
    set_of_unreferenced_instances, set_of_referenced_instances = set_of_unreferenced_instances(ifcfile)
    if len(set_of_unreferenced_instances) < len(set_of_referenced_instances):
        logging.info("removing " + str(len(set_of_unreferenced_instances)) + " unreferenced instances")
        i = 0
        for unreferenced_id in set_of_unreferenced_instances:
            if i % 1000 == 0:
                logging.info("run " + str(i))
            i = i + 1
            ifcfile.remove(unreferenced_id)
        logging.info("writing file: " + target)
        ifcfile.write(target)
    else:
        # if more unreferenced instances are present: copying the referenced ones to a new file instead of removing the unreferenced
        logging.info("copying " + str(len(set_of_referenced_instances)) + " referenced instances to a new file")
        g = ifcopenshell.file(schema=ifcfile.schema)
        i = 0
        for referenced_instance in set_of_referenced_instances:
            if i % 1000 == 0:
                logging.info("run " + str(i))
            i = i + 1
            g.add(referenced_instance)
        logging.info("writing file: " + target)
        g.write(target)

def remove_connection_to_self(ifcfile):
    ircps = ifcfile.by_type('IfcRelConnectsPorts')
    for ircp in ircps:
        port_1 = ircp.RelatingPort
        port_2 = ircp.RelatedPort
        element_1 = port_1.Nests[0].RelatingObject
        element_2 = port_2.Nests[0].RelatingObject
        if element_1 == element_2:
            logging.info("deleting selfconnection of " + str(element_1))
            ifcfile.remove(ircp)
            ifcfile.remove(port_1)
            ifcfile.remove(port_2)



def remove_all_properties_of_all_spaces(ifcfile):
    for space in ifcfile.by_type('IfcSpace'):
        for definition in space.IsDefinedBy:
            pset = definition.RelatingPropertyDefinition
            for prop in pset.HasProperties:
                ifcfile.remove(prop)
            ifcfile.remove(pset)
            ifcfile.remove(definition)

def remove_certain_properties_of_all_spaces(ifcfile, dict_properties_by_pset):
    property_deletion_counter = 0
    space_counter = 0
    pset_counter = 0
    property_counter = 0
    for space in ifcfile.by_type('IfcSpace'):
        space_counter += 1
        for definition in space.IsDefinedBy:
            pset = definition.RelatingPropertyDefinition
            if pset.is_a('IfcPropertySet'):  # not for quantity sets or predefined property sets
                pset_counter += 1
                for property in pset.HasProperties:
                    property_counter += 1
                    if  pset.Name in dict_properties_by_pset and property.Name in dict_properties_by_pset[pset.Name]:
                        ifcfile.remove(property)
                        property_deletion_counter += 1
    logging.info("deleted " + str(property_deletion_counter) + " IFCPROPERTYSINGLEVALUE")

def remove_orphans_of_IfcElements(ifcfile):
    """
    remove nests, ports, their relation, IfcRelDefinesByProperties, IfcRelDefinesByType (these are IfcRoot elements)
    :param ifcfile:
    :return:
    """
    deletioncount = 0
    logging.info("checking " + str(len(ifcfile.by_type('IfcRelNests'))) + " IfcRelNests")
    for irn in ifcfile.by_type('IfcRelNests'):
        if irn.RelatingObject == None:
            for ro in irn.RelatedObjects:
                ifcfile.remove(ro) #assuming all nested objects are Ports, that are not to be kept, of the element is not present any more
                deletioncount += 1
            ifcfile.remove(irn)
            deletioncount += 1
    logging.info("deleted " + str(deletioncount) + " Ports and IfcRelNests Relationen")

    logging.info("checking " + str(len(ifcfile.by_type('IfcRelConnectsPorts'))) + " IfcRelConnectsPorts")
    for ircp in ifcfile.by_type('IfcRelConnectsPorts'):
        if ircp.RelatingPort == None or ircp.RelatedPort == None:
            ifcfile.remove(ircp)
            deletioncount += 1
    logging.info("deleted " + str(deletioncount) + " Ports and IfcRelNests Relationen, IfcRelConnectsPorts")

    logging.info("checking " + str(len(ifcfile.by_type('IfcRelDefinesByProperties'))) + " IfcRelDefinesByProperties")
    for irdp in ifcfile.by_type('IfcRelDefinesByProperties'):
        if len(irdp.RelatedObjects) == 0:
            ifcfile.remove(irdp)
            deletioncount += 1
    logging.info("deleted " + str(deletioncount) + " Ports and IfcRelNests Relationen, IfcRelConnectsPorts, IfcRelDefinesByProperties")

    logging.info("checking " + str(len(ifcfile.by_type('IfcRelDefinesByType'))) + " IfcRelDefinesByType")
    for irdp in ifcfile.by_type('IfcRelDefinesByType'):
        if len(irdp.RelatedObjects) == 0:
            ifcfile.remove(irdp)
            deletioncount += 1
    logging.info("deleted " + str(deletioncount) + " Ports and IfcRelNests Relationen, IfcRelConnectsPorts, IfcRelDefinesByProperties, IfcRelDefinesByType")

def remove_elements_and_their_ports(ifcfile, elementlist):
    for guid in elementlist:
        element = ifcfile.by_guid(guid)
        for irn in element.IsNestedBy:
            for port in irn.RelatedObjects:
                ifcfile.remove(port)
            ifcfile.remove(irn)
        logging.info("removed " + str(element.Name) + " with its ports and their nest relationship")
        ifcfile.remove(element)

def remove_duplicates_of_certain_ifcclass(sourcefile, targetfile_stub, ifcClass_to_investigate):
    """
    :param sourcefile: path to original file
    :param targetfile_stub:
    :param ifcClass_to_investigate: Name of IfcClass z.B. IfcOwnerHistory
    :return: path of target file
    """
    logging.info("reading file: " + sourcefile)
    ifcfile = ifcopenshell.open(sourcefile)
    logging.info("file contains " + str(len(set_of_instance_ids(ifcfile))) + " instances")
    dict_replacements, set_duplicates = get_duplicates_and_their_replacements(ifcfile, ifcClass_to_investigate)
    targetfile_path = os.path.dirname(os.path.abspath(sourcefile)) + "\\" + targetfile_stub + ifcClass_to_investigate + ".ifc"
    #TODO skip if no duplicates are present
    replace_instance_ids_text_based(dict_replacements, sourcefile, targetfile_path)

def remove_edge_and_ports(ifcfile, ircp):
    try:
        ifcfile.remove(ircp.RelatingPort)
    except:
        logging.info("no relating port in " + str(ircp))
    try:
        ifcfile.remove(ircp.RelatedPort)
    except:
        logging.info("no related port in " + str(ircp))
    ifcfile.remove(ircp)

def delete_all_IfcShapeRepresentations_of_IfcElement(ifcfile, element):
    """
    manipulates ifcfile directly therefore no return value
    :param ifcfile:
    :param element:
    """
    try:
        pds = element.Representation
        srs_old = pds.Representations
        ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=element.Representation, attributes={"Representations": ()}) #replace old represenation by boundingbox
        #alternative Implementierung - funktioniert nicht #TODO pds auch noch entfernen - spart noch etwas mehr Platz
        #ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=element, representation=None)
        #ifcfile.remove(pds)

        for sr in srs_old:
            for item in sr.Items:
                ifcfile.remove(item)
            ifcfile.remove(sr)
    except:
        logging.info("deleting geometry of " + str(element) + " failed")

def delete_replacees(ifcfile):
    """
    deletes alle replacees (IfcElements that are replaced by another (virtual) element) in graph Ro
    :return:
    """
    deletioncount = 0
    for element in ifcfile.by_type('IfcElement'):
        if element.Description == "replaced in Ro":
            if deletioncount % 100 == 0:
                logging.info("deleted " + str(deletioncount) + " elements so far")
            deletioncount = deletioncount + 1
            ifcfile.remove(element)
    logging.info("deleted " + str(deletioncount) + " elements")
    logging.info("deleting orphans")
    remove_orphans_of_IfcElements(ifcfile)


def remove_ports_not_in_Ro(ifcfile, Ro_set_of_ircp):
    Ro_set_of_ports = set_of_IfcDistributionPorts_in_set_of_ircp(Ro_set_of_ircp)
    ports_all = set(ifcfile.by_type('IfcDistributionPort'))
    ports_to_delete = ports_all.difference(Ro_set_of_ports)
    logging.info("deleting " + str(len(ports_to_delete)) + " ports that are not part of Ro or Ro_2")
    i = 0
    for el in ports_to_delete:
        if i % 1000 == 0:
            logging.info("deletion run " + str(i))
        i = i + 1
        ifcfile.remove(el)


def remove_elements_not_in_Ro(ifcfile, Ro_set_of_ircp):
    Ro_set_of_elements = set_of_IfcElements_in_set_of_ircp(Ro_set_of_ircp)
    elements_all = set(ifcfile.by_type('IfcElement'))
    elements_to_delete = elements_all.difference(Ro_set_of_elements)
    logging.info("deleting " + str(len(elements_to_delete)) + " elements that are not part of Ro or Ro_2")
    i = 0
    for el in elements_to_delete:
        if i % 1000 == 0:
            logging.info("deletion run " + str(i))
        i = i + 1
        ifcfile.remove(el)
    logging.info("deleting orphans")
    remove_orphans_of_IfcElements(ifcfile)

def remove_ircp_not_in_Ro(ifcfile, Ro_set_of_ircp):
    elements_all = set(ifcfile.by_type('IfcRelConnectsPorts'))
    elements_to_delete = elements_all.difference(Ro_set_of_ircp)
    logging.info("deleting " + str(len(elements_to_delete)) + " IfcRelConnectsPorts that are not part of Ro or Ro_2")
    i = 0
    for el in elements_to_delete:
        if i % 1000 == 0:
            logging.info("deletion run " + str(i))
        i = i + 1
        ifcfile.remove(el)



