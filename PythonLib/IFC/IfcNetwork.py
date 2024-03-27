"""
copied from ifcscripting\IFC\IfcNetwork.py
#TODO überflüssige Funktionen entfernen (ist eine Kopie von IfcGraph - dort ist auch vieles noch da)
"""
import ifcopenshell.api
from rdflib import RDFS, RDF, Graph, URIRef
from rdflib.namespace import XSD
import rdflib
from PythonLib.IFC.Change import add_element_to_system_assignment
from PythonLib.IFC.HelpersGraph import *
from PythonLib.IFC.HelpersGeometry import get_coordinates, get_coordinate, get_2D_coordinates_of_element
from PythonLib.IFC.HelpersGraph import node_is_deletion_candidate, reduce_graph_to_skeleton_simple, generate_cluster_no_as_attribute, copy_cluster_no_as_attribute, generate_layout_and_colour, generate_layout
from PythonLib.IFC.HelpersPlot import *
from PythonLib.IFC.Create import *
from PythonLib.IFC.Inspect import get_attribute_dict_with_EndElementInformation, get_attribute_dict
from PythonLib.IFC.Remove import remove_orphans_of_IfcElements

def get_or_generate_layout(graph, ifcfile):
    #check if 2D coordinates are already present
    layout = dict()
    layout_existing = False
    set_of_elements_with_missing_coordinates = set()
    for node in graph.nodes:
        element = ifcfile.by_guid(node)
        xcoord, ycoord = get_2D_coordinates_of_element(element)
        layout[node] = (xcoord, ycoord) #tuple
        if xcoord != None and ycoord != None: #sobald es ein Element gibt, welches schon Koordinaten hat, werden diese verwendet
            layout_existing = True
        if xcoord == None:
            set_of_elements_with_missing_coordinates.add(node)
        if ycoord == None:
            set_of_elements_with_missing_coordinates.add(node)
    #TODO handle existing layout where some coordinates are missing
    #if not already present: generate them
    if not layout_existing:
        layout, x_min, x_max, y_min, y_max = generate_layout(graph)
    return layout, layout_existing


def attach_property_with_graphstatus_to_ifcfile(nodelist, nodes_to_remove, owner_history, ifcfile):
    """
    all nodes get an IFC property which says, whether they are "only contained in full graph" or "also present in reduced graph"
    :param nodelist: list - all nodes in graph
    :param nodes_to_remove: set - nodes that will be removed, get property 'nur_im_vollst_graph_enthalten'
    :param owner_history:
    :param ifcfile:
    :return: ifcfile
    """
    ifc_objects_in_Graph = set()
    removed_ifc_objects_in_Graph = set()
    persistant_ifc_objects_in_Graph = set()
    for node in nodelist:
        ifc_objects_in_Graph.add(ifcfile.by_guid(node))
        if node in nodes_to_remove:
            removed_ifc_objects_in_Graph.add(ifcfile.by_guid(node))
        else:
            persistant_ifc_objects_in_Graph.add(ifcfile.by_guid(node))
    property_value1 = ifcfile.createIfcPropertySingleValue('reduction_status', None, ifcfile.create_entity("IfcText",'nur_im_vollst_graph_enthalten'), None)
    property_set1 = ifcfile.createIfcPropertySet(ifcopenshell.guid.new(), owner_history, "Pset_Schematizer", None, [property_value1]) #TODO check if it possible to use the same pset, change name: prefix Pset is reserved
    property_value2 = ifcfile.createIfcPropertySingleValue('reduction_status', None, ifcfile.create_entity("IfcText",'auch_im_red_graph_enthalten'), None)
    property_set2 = ifcfile.createIfcPropertySet(ifcopenshell.guid.new(), owner_history, "Pset_Schematizer", None, [property_value2]) #TODO check if it possible to use the same pset, change name: prefix Pset is reserved
    ifcreldefbyp1 = ifcfile.createIfcRelDefinesByProperties(ifcopenshell.guid.new(), owner_history, None, None, list(removed_ifc_objects_in_Graph), property_set1)
    ifcreldefbyp2 = ifcfile.createIfcRelDefinesByProperties(ifcopenshell.guid.new(), owner_history, None, None, list(persistant_ifc_objects_in_Graph), property_set2)
    return ifcfile


def attach_property_with_leafstatus_to_ifcfile(list_leaf_guids, owner_history, ifcfile):
    """
    :param list_leaf_guids: set
    :param owner_history:
    :param ifcfile:
    :return: ifcfile
    """
    leaf_ifc_objects = set()
    for node_id in list_leaf_guids:
        leaf_ifc_objects.add(ifcfile.by_guid(node_id))
    property_value3 = ifcfile.createIfcPropertySingleValue('node_degree', None, ifcfile.create_entity("IfcText",'LEAF'), None)
    property_set3 = ifcfile.createIfcPropertySet(ifcopenshell.guid.new(), owner_history, "Pset_Schematizer", None, [property_value3])
    ifcreldefbyp3 = ifcfile.createIfcRelDefinesByProperties(ifcopenshell.guid.new(), owner_history, None, None, list(leaf_ifc_objects), property_set3)
    return ifcfile


def attach_property_with_nodestatus_to_ifcfile(graph, change_graph, property_name, propertyset_name, owner_history, ifcfile):
    """
    directly changes ifcfile
    :param owner_history:
    :param ifcfile:
    """
    loner_set, leaf_set, leaf_set_connected, pipe_set, pipe_set_connected, hub_set, hub_set_connected = generate_Sets_for_graph(ifcfile, graph, change_graph)
    if len(list(loner_set))>0:
        property_value_loner = ifcfile.createIfcPropertySingleValue(property_name, None, ifcfile.create_entity("IfcText",'loner'), None)
        property_set_loner = ifcfile.createIfcPropertySet(ifcopenshell.guid.new(), owner_history, propertyset_name, None, [property_value_loner])
        ifcreldefbyprpoerties_loner = ifcfile.createIfcRelDefinesByProperties(ifcopenshell.guid.new(), owner_history, None, None, list(loner_set), property_set_loner)
    if len(list(leaf_set))>0:
        property_value_leaf = ifcfile.createIfcPropertySingleValue(property_name, None, ifcfile.create_entity("IfcText",'leaf'), None)
        property_set_leaf = ifcfile.createIfcPropertySet(ifcopenshell.guid.new(), owner_history, propertyset_name, None, [property_value_leaf])
        ifcreldefbyprpoerties_leaf = ifcfile.createIfcRelDefinesByProperties(ifcopenshell.guid.new(), owner_history, None, None, list(leaf_set), property_set_leaf)
    if len(list(pipe_set))>0:
        property_value_pipe = ifcfile.createIfcPropertySingleValue(property_name, None, ifcfile.create_entity("IfcText",'pipe'), None)
        property_set_pipe = ifcfile.createIfcPropertySet(ifcopenshell.guid.new(), owner_history, propertyset_name, None, [property_value_pipe])
        ifcreldefbyprpoerties_pipe = ifcfile.createIfcRelDefinesByProperties(ifcopenshell.guid.new(), owner_history, None, None, list(pipe_set), property_set_pipe)
    if len(list(hub_set))>0:
        property_value_hub = ifcfile.createIfcPropertySingleValue(property_name, None, ifcfile.create_entity("IfcText",'hub'), None)
        property_set_hub = ifcfile.createIfcPropertySet(ifcopenshell.guid.new(), owner_history, propertyset_name, None, [property_value_hub])
        ifcreldefbyprpoerties_hub = ifcfile.createIfcRelDefinesByProperties(ifcopenshell.guid.new(), owner_history, None, None, list(hub_set), property_set_hub)
    if len(list(leaf_set_connected))>0:
        property_value_leaf_connected = ifcfile.createIfcPropertySingleValue(property_name, None, ifcfile.create_entity("IfcText",'leaf (connected in this step)'), None)
        property_set_leaf_connected = ifcfile.createIfcPropertySet(ifcopenshell.guid.new(), owner_history, propertyset_name, None, [property_value_leaf_connected])
        ifcreldefbyprpoerties_leaf_connected = ifcfile.createIfcRelDefinesByProperties(ifcopenshell.guid.new(), owner_history, None, None, list(leaf_set_connected), property_set_leaf_connected)
    if len(list(pipe_set_connected))>0:
        property_value_pipe_connected = ifcfile.createIfcPropertySingleValue(property_name, None, ifcfile.create_entity("IfcText",'pipe (connected in this step)'), None)
        property_set_pipe_connected = ifcfile.createIfcPropertySet(ifcopenshell.guid.new(), owner_history, propertyset_name, None, [property_value_pipe_connected])
        ifcreldefbyprpoerties_pipe_connected = ifcfile.createIfcRelDefinesByProperties(ifcopenshell.guid.new(), owner_history, None, None, list(pipe_set_connected), property_set_pipe_connected)
    if len(list(hub_set_connected))>0:
        property_value_hub_connected = ifcfile.createIfcPropertySingleValue(property_name, None, ifcfile.create_entity("IfcText",'hub (connected in this step)'), None)
        property_set_hub_connected = ifcfile.createIfcPropertySet(ifcopenshell.guid.new(), owner_history, propertyset_name, None, [property_value_hub_connected])
        ifcreldefbyprpoerties_hub_connected = ifcfile.createIfcRelDefinesByProperties(ifcopenshell.guid.new(), owner_history, None, None, list(hub_set_connected), property_set_hub_connected)
    return loner_set,leaf_set, leaf_set_connected, pipe_set, pipe_set_connected, hub_set, hub_set_connected


def generate_Sets_for_graph(ifcfile, graph, change_graph):
    loner_set = set()
    leaf_set = set()
    leaf_set_connected = set()
    pipe_set = set()
    pipe_set_connected = set()
    hub_set = set()
    hub_set_connected = set()
    simple_graph = nx.Graph(graph) #remove double edges
    for node_id in graph:
        node = ifcfile.by_guid(node_id)
        ns = nx.all_neighbors(simple_graph, node_id)  # also predecessors (in a directed graph)
        neighbor_count = len(list(ns))
        if neighbor_count==0:
            loner_set.add(node)
        elif neighbor_count==1:
            if change_graph.has_node(node_id):
                leaf_set_connected.add(node)
            else:
                leaf_set.add(node)
        elif neighbor_count==2:
            if change_graph.has_node(node_id):
                pipe_set_connected.add(node)
            else:
                pipe_set.add(node)
        else:
            if change_graph.has_node(node_id):
                hub_set_connected.add(node)
            else:
                hub_set.add(node)
    return loner_set, leaf_set, leaf_set_connected, pipe_set, pipe_set_connected, hub_set, hub_set_connected


class IfcNetwork:


    def __init__(self, name, ifcfile, ifcfile_system):
        self.name = name
        self.ifcfile = ifcfile
        self.ifcfile_system = ifcfile_system


    def extract_graph_and_leafs_from_ifc_Ao_with_EndElementInformation(self, end_element_guid_set):
        graph_1 = nx.MultiDiGraph()  # parallel edges are allowed, directed edges
        for ircp in self.ifcfile.by_type('IfcRelConnectsPorts'):
            if ircp.RelatingPort != None: #kann vorkommen, da ich manche ports gelöscht habe (deren Containerobjekte gelöscht wurden)
                if len(list(ircp.RelatingPort.Nests))==0:
                    logging.info("Port ohne Vaterelement: " + str(ircp.RelatingPort))
                else:
                    element = ircp.RelatingPort.Nests[0].RelatingObject
                    other_element = ircp.RelatedPort.Nests[0].RelatingObject
                    graph_1.add_nodes_from([(element.GlobalId, get_attribute_dict_with_EndElementInformation(element, end_element_guid_set, self.ifcfile_system))])
                    graph_1.add_nodes_from([(other_element.GlobalId, get_attribute_dict_with_EndElementInformation(other_element, end_element_guid_set, self.ifcfile_system))])
                    graph_1.add_edge(element.GlobalId, other_element.GlobalId)
        nx.set_edge_attributes(graph_1, dict(len=1))#add length 1 to all edges compiled from IFC-file (important for "weight" of edges)
        ll_graph, ll_set, leafs_set, loners_set = get_leafs_and_loners(graph_1)
        self.graph_construction = graph_1 #graph_construction was formerly known as graph_Ao
        self.leafs_and_loners_set_o = ll_set

    def extract_full_graph_and_leafs_from_ifc_to_Ao(self):
        graph_1 = nx.MultiDiGraph()  # parallel edges are allowed, directed edges
        for ircp in self.ifcfile.by_type('IfcRelConnectsPorts'):
            element_exists = False
            other_element_exists = False
            if ircp.RelatingPort != None: #kann vorkommen, da ich manche ports gelöscht habe (deren Containerobjekte gelöscht wurden)
                if len(list(ircp.RelatingPort.Nests))==0 or ircp.RelatingPort.Nests[0].RelatingObject == None:
                    logging.info("Port ohne Vaterelement: " + str(ircp.RelatingPort))
                else:
                    element = ircp.RelatingPort.Nests[0].RelatingObject
                    graph_1.add_nodes_from([(element.GlobalId, get_attribute_dict(element, self.ifcfile_system))])
                    element_exists = True
            if ircp.RelatedPort != None:  # kann vorkommen, da ich manche ports gelöscht habe (deren Containerobjekte gelöscht wurden)
                if len(list(ircp.RelatedPort.Nests)) == 0 or ircp.RelatedPort.Nests[0].RelatingObject == None:
                    logging.info("Port ohne Vaterelement: " + str(ircp.RelatedPort))
                else:
                    other_element = ircp.RelatedPort.Nests[0].RelatingObject
                    graph_1.add_nodes_from([(other_element.GlobalId, get_attribute_dict(other_element, self.ifcfile_system))])
                    other_element_exists = True
            if element_exists and other_element_exists:
                namestring = ircp.Name
                if namestring == None:
                    namestring = "original"
                graph_1.add_edge(element.GlobalId, other_element.GlobalId, name=namestring, guid=ircp.GlobalId)
        nx.set_edge_attributes(graph_1, dict(len=1))#add length 1 to all edges compiled from IFC-file (important for "weight" of edges)
        ll_graph, ll_set, leafs_set, loners_set = get_leafs_and_loners(graph_1)
        self.graph_construction = graph_1
        self.leafs_and_loners_set_o = ll_set


    def extract_graph_construction(self):
        """
        reads all but "new edge from graph algorithm (replacement graph Ro)" edges to Ro
        :return:
        """
        graph_1 = nx.MultiDiGraph()  # parallel edges are allowed, directed edges
        for ircp in self.ifcfile.by_type('IfcRelConnectsPorts'):
            if ircp.Name == "new edge from graph algorithm (replacement graph Ro)" or ircp.Name == "replacement graph Ro":
                continue
            element_exists = False
            other_element_exists = False
            if ircp.RelatingPort != None: #kann vorkommen, da ich manche ports gelöscht habe (deren Containerobjekte gelöscht wurden)
                if len(list(ircp.RelatingPort.Nests))==0 or ircp.RelatingPort.Nests[0].RelatingObject == None:
                    logging.info("Port ohne Vaterelement: " + str(ircp.RelatingPort))
                else:
                    element = ircp.RelatingPort.Nests[0].RelatingObject
                    graph_1.add_nodes_from([(element.GlobalId, get_attribute_dict(element, self.ifcfile_system))])
                    element_exists = True
            if ircp.RelatedPort != None:  # kann vorkommen, da ich manche ports gelöscht habe (deren Containerobjekte gelöscht wurden)
                if len(list(ircp.RelatedPort.Nests)) == 0 or ircp.RelatedPort.Nests[0].RelatingObject == None:
                    logging.info("Port ohne Vaterelement: " + str(ircp.RelatedPort))
                else:
                    other_element = ircp.RelatedPort.Nests[0].RelatingObject
                    graph_1.add_nodes_from([(other_element.GlobalId, get_attribute_dict(other_element, self.ifcfile_system))])
                    other_element_exists = True
            if element_exists and other_element_exists:
                namestring = ircp.Name
                if namestring == None:
                    namestring = "original"
                graph_1.add_edge(element.GlobalId, other_element.GlobalId, name=namestring, guid=ircp.GlobalId)
        nx.set_edge_attributes(graph_1, dict(len=1))#add length 1 to all edges compiled from IFC-file (important for "weight" of edges)
        ll_graph, ll_set, leafs_set, loners_set = get_leafs_and_loners(graph_1)
        self.graph_construction = graph_1
        self.leafs_and_loners_set_o = ll_set

    def extract_graph_schematic(self):
        """
        liest bereits reduzierten Graph aus IFC aus und speichert ihn in ig.Ro, dazu muss die IfcRelConnectsPorts-Relation folgende Namen haben:
            "new edge from graph algorithm (replacement graph Ro)" oder "new edge from graph algorithm (Ro_2 (manuelle Nachpflege))"
        only workinig on IfcRelConnectsPorts with name either "new edge from graph algorithm (replacement graph Ro)" or "new edge from graph algorithm (Ro_2 (manuelle Nachpflege))"
        :return: nothing, generates self.graph_schematic
        """
        graph_1 = nx.MultiDiGraph()  # parallel edges are allowed, directed edges
        for ircp in self.ifcfile.by_type('IfcRelConnectsPorts'):
            if ircp.Name == "new edge from graph algorithm (replacement graph Ro)" or ircp.Name == "new edge from graph algorithm (Ro_2 (manuelle Nachpflege))" or ircp.Name == "replacement graph Ro":
                element_exists = False
                other_element_exists = False
                if ircp.RelatingPort != None: #kann vorkommen, da ich manche ports gelöscht habe (deren Containerobjekte gelöscht wurden)
                    if len(list(ircp.RelatingPort.Nests))==0 or ircp.RelatingPort.Nests[0].RelatingObject == None:
                        logging.info("Port ohne Vaterelement: " + str(ircp.RelatingPort))
                    else:
                        element = ircp.RelatingPort.Nests[0].RelatingObject
                        graph_1.add_nodes_from([(element.GlobalId, get_attribute_dict(element, self.ifcfile_system))])
                        element_exists = True
                if ircp.RelatedPort != None:  # kann vorkommen, da ich manche ports gelöscht habe (deren Containerobjekte gelöscht wurden)
                    if len(list(ircp.RelatedPort.Nests)) == 0 or ircp.RelatedPort.Nests[0].RelatingObject == None:
                        logging.info("Port ohne Vaterelement: " + str(ircp.RelatedPort))
                    else:
                        other_element = ircp.RelatedPort.Nests[0].RelatingObject
                        graph_1.add_nodes_from([(other_element.GlobalId, get_attribute_dict(other_element, self.ifcfile_system))])
                        other_element_exists = True
                if element_exists and other_element_exists:
                    namestring = ircp.Name
                    if namestring == None:
                        namestring = "original"
                    graph_1.add_edge(element.GlobalId, other_element.GlobalId, name=namestring, guid=ircp.GlobalId)
        nx.set_edge_attributes(graph_1, dict(len=1))#add length 1 to all edges compiled from IFC-file (important for "weight" of edges)
        self.graph_schematic = graph_1

    def extract_graph_and_leafs_from_ifc_to_Ro(self):
        """
        working on all IfcRelConnectsPorts no matter which name they have
        (created by copying extract_Ro2_graph_and_leafs_from_ifc_to_Ro)
        :return: nothing, generates self.graph_Ro
        """
        graph_1 = nx.MultiDiGraph()  # parallel edges are allowed, directed edges
        for ircp in self.ifcfile.by_type('IfcRelConnectsPorts'):
            element_exists = False
            other_element_exists = False
            if ircp.RelatingPort != None:  # kann vorkommen, da ich manche ports gelöscht habe (deren Containerobjekte gelöscht wurden)
                if len(list(ircp.RelatingPort.Nests)) == 0 or ircp.RelatingPort.Nests[0].RelatingObject == None:
                    logging.info("Port ohne Vaterelement: " + str(ircp.RelatingPort))
                else:
                    element = ircp.RelatingPort.Nests[0].RelatingObject
                    graph_1.add_nodes_from([(element.GlobalId, get_attribute_dict(element, self.ifcfile_system))])
                    element_exists = True
            if ircp.RelatedPort != None:  # kann vorkommen, da ich manche ports gelöscht habe (deren Containerobjekte gelöscht wurden)
                if len(list(ircp.RelatedPort.Nests)) == 0 or ircp.RelatedPort.Nests[0].RelatingObject == None:
                    logging.info("Port ohne Vaterelement: " + str(ircp.RelatedPort))
                else:
                    other_element = ircp.RelatedPort.Nests[0].RelatingObject
                    graph_1.add_nodes_from([(other_element.GlobalId, get_attribute_dict(other_element, self.ifcfile_system))])
                    other_element_exists = True
            if element_exists and other_element_exists:
                namestring = ircp.Name
                if namestring == None:
                    namestring = "original"
                graph_1.add_edge(element.GlobalId, other_element.GlobalId, name=namestring, guid=ircp.GlobalId)
        nx.set_edge_attributes(graph_1, dict(len=1))  # add length 1 to all edges compiled from IFC-file (important for "weight" of edges)
        self.graph_schematic = graph_1


    def reduce_graph_to_skeleton_So(self):
        graph_to_reduce = self.graph_construction.copy()
        reduce_graph_to_skeleton_simple(graph_to_reduce)
        self.graph_So = graph_to_reduce

    def reduce_graph_to_skeleton_Sm2_6(self):
        graph_to_reduce = self.graph_Am2_6.copy()
        reduce_graph_to_skeleton_simple(graph_to_reduce)
        self.graph_Sm2_6 = graph_to_reduce

    def visualize_graph_reduction(self):
        no_of_subgraphs = nx.number_connected_components(self.graph_construction.to_undirected())
        pos_spring = nx.nx_agraph.graphviz_layout(self.graph_construction)
        pos_spring_reduced = nx.nx_agraph.graphviz_layout(self.graph_schematic)
        # visualize reduction
        figure_graph_reduction(self.name, self.graph_construction, self.graph_schematic, pos_spring, pos_spring_reduced, no_of_subgraphs)
        # visualize open ends
        figure_leafs_types(self.name, self.graph_schematic, pos_spring)
        #figure_leafs_names(self.name, self.graph_Ro, pos_spring)
        plt.close(fig="all")

    def classifiy_IfcElements_without_ports(self):
        # add meaningful elements without ports as loners to graph
        self.elements_with_ports = set()
        elements_all = set()
        for element in self.ifcfile.by_type('IfcElement'):
            elements_all.add(element)
            irns = element.IsNestedBy
            for irn in irns:
                nested_objects = irn.RelatedObjects
                for nested_object in nested_objects:
                    if nested_object.is_a() == "IfcDistributionPort":
                        self.elements_with_ports.add(element)
        self.elements_without_ports=elements_all.difference(self.elements_with_ports)
        self.elements_without_ports_with_coordinates = set()
        self.elements_without_ports_without_coordinates = set()
        element_guids_without_ports_set = set()
        for element in self.elements_without_ports:
            #if len(element.IsTypedBy) > 0 or len(element.IsDefinedBy) > 0 or len(element.IsDeclaredBy) > 0:  # elements without any additional information are not considered #Condition is skipped now, since I assume that these elements have been wiped in a preprocessing
            if get_coordinate(element).sum() != 0:
                element_guids_without_ports_set.add(element.GlobalId)
                self.elements_without_ports_with_coordinates.add(element)
            else:
                self.elements_without_ports_without_coordinates.add(element)
        self.leafs_and_loners_set_m_0 = self.leafs_and_loners_set_o.union(element_guids_without_ports_set)


    def add_replacements_to_ifcfile(self, graph, oh):
        """
        adds an IfcVirtualElement for all replacements
        create a group for it and assign all replacees to this group
        create and assign an element type IfcPipeSegmentType USERDEFINED for these IfcVirtualElements
        * ersetzt - replaced - replacee
        * Ersatz - replacing - replacement
        :param graph:
        :return:
        """

        replacement_set = get_replacement_nodes(graph) #get all nodes named "replacing"
        elementset = set()
        for guid_of_replacement in replacement_set:
            replacee_1 = self.ifcfile.by_guid(graph.nodes[guid_of_replacement]['replacing'][0]) #TODO properties and placement of first replacee are used, check whether this is a good idea
            irag = replacee_1.HasAssignments[0]
            name = graph.nodes[guid_of_replacement]['ifc_name']
            element = self.ifcfile.createIfcVirtualElement(guid_of_replacement, oh, name, None, None, replacee_1.ObjectPlacement, None, None)     #TODO add representation to virtual pressure drop (only important for 2D)
            elementset.add(element)
            add_element_to_system_assignment(self.ifcfile, irag, element)
            group = self.ifcfile.createIfcGroup(ifcopenshell.guid.new(), oh, graph.nodes[guid_of_replacement]['ifc_name'], 'grouping of all elements replaced by replacement ' + str(guid_of_replacement), None)
            object_set = set()
            for replacee_id in graph.nodes[guid_of_replacement]['replacing']:
                object_set.add(self.ifcfile.by_guid(replacee_id))
            descriptionstring = 'assignment for replacement ' + str(guid_of_replacement)
            assignment = self.ifcfile.createIfcRelAssignsToGroup(ifcopenshell.guid.new(), oh, str(graph.nodes[guid_of_replacement]['ifc_name']), descriptionstring, tuple(object_set), None, group)
        elementtype = self.ifcfile.createIfcPipeSegmentType(ifcopenshell.guid.new(), oh, 'pressure drop', 'replacement element for several pressure drops', None, None, None, None, None, 'USERDEFINED')
        self.ifcfile.createIfcRelDefinesByType(ifcopenshell.guid.new(), oh, None, 'assigning all replacement pressure drops to their type', tuple(elementset), elementtype)

    def add_replacements_to_ifcfile_with_properties(self, graph, oh):
        """
        adds an IfcVirtualElement for all replacements
        create a group for it and assign all replacees to this group
        create and assign an element type IfcPipeSegmentType USERDEFINED for these IfcVirtualElements
        assigns property "reduction status" to all elements, stating the replacing element for replaced elements
        * ersetzt - replaced - replacee
        * Ersatz - replacing - replacement
        :param graph:
        :return:
        """
        replacement_set = get_replacement_nodes(graph)  # get all nodes named "replacing"
        elementset = set()
        for guid_of_replacement in replacement_set:
            replacee_1 = self.ifcfile.by_guid(graph.nodes[guid_of_replacement]['replacing'][0])  # TODO properties and placement of first replacee are used, check whether this is a good idea
            irag = replacee_1.HasAssignments[0]
            name = graph.nodes[guid_of_replacement]['ifc_name']
            #element = self.ifcfile.createIfcVirtualElement(guid_of_replacement, oh, name, None, None, replacee_1.ObjectPlacement, replacee_1.Representation, None)  # TODO add proper representation to virtual pressure drop (only important for 2D), now the representation of the 1st replacee is added
            element = self.ifcfile.createIfcPipeSegment(guid_of_replacement, oh, name, None, None, replacee_1.ObjectPlacement, replacee_1.Representation, None)  # TODO change class of replacement back to virtual element (but not shown in simpleBIM!)
            elementset.add(element)
            add_element_to_system_assignment(self.ifcfile, irag, element)
            group = self.ifcfile.createIfcGroup(ifcopenshell.guid.new(), oh, graph.nodes[guid_of_replacement]['ifc_name'], 'grouping of all elements replaced by replacement ' + str(guid_of_replacement), None)
            object_set = set()
            for replacee_id in graph.nodes[guid_of_replacement]['replacing']:
                cur_replacee = self.ifcfile.by_guid(replacee_id)
                object_set.add(cur_replacee)
                ifcopenshell.api.run("attribute.edit_attributes", self.ifcfile, product=cur_replacee, attributes={"Description": "replaced in schematic graph"})
            descriptionstring = 'assignment for replacement ' + str(guid_of_replacement)
            assignment = self.ifcfile.createIfcRelAssignsToGroup(ifcopenshell.guid.new(), oh, str(graph.nodes[guid_of_replacement]['ifc_name']), descriptionstring, tuple(object_set), None, group)
           # TODO check if it is possible to use the same pset
            property_string = 'replaced by ' + str(name)
            property_value1 = self.ifcfile.createIfcPropertySingleValue('reduction_status', None, self.ifcfile.create_entity("IfcText", property_string), None)
            property_set1 = self.ifcfile.createIfcPropertySet(ifcopenshell.guid.new(), oh, "Schematizer", None, [property_value1])
            ifcreldefbyp1 = self.ifcfile.createIfcRelDefinesByProperties(ifcopenshell.guid.new(), oh, None, None, tuple(object_set), property_set1)
            property_value2 = self.ifcfile.createIfcPropertySingleValue('reduction_status', None, self.ifcfile.create_entity("IfcText", "replacing others"), None)
            property_set2 = self.ifcfile.createIfcPropertySet(ifcopenshell.guid.new(), oh, "Schematizer", None, [property_value2])
            ifcreldefbyp2 = self.ifcfile.createIfcRelDefinesByProperties(ifcopenshell.guid.new(), oh, None, None, tuple([self.ifcfile.by_guid(guid_of_replacement), ]), property_set2)
        elementtype = self.ifcfile.createIfcPipeSegmentType(ifcopenshell.guid.new(), oh, 'pressure drop', 'replacement element for several pressure drops', None, None, None, None, None, 'USERDEFINED')
        self.ifcfile.createIfcRelDefinesByType(ifcopenshell.guid.new(), oh, None, 'assigning all replacement pressure drops to their type', tuple(elementset), elementtype)
        persistant_set = get_persistant_nodes(graph)
        object_set_2 = set()
        for persistant_guid in persistant_set:
            object_set_2.add(self.ifcfile.by_guid(persistant_guid))
        property_value3 = self.ifcfile.createIfcPropertySingleValue('reduction_status', None, self.ifcfile.create_entity("IfcText", "persistant node (neither replaced nor replacing)"), None)
        property_set3 = self.ifcfile.createIfcPropertySet(ifcopenshell.guid.new(), oh, "Schematizer", None, [property_value3])
        ifcreldefbyp3 = self.ifcfile.createIfcRelDefinesByProperties(ifcopenshell.guid.new(), oh, None, None, tuple(object_set_2), property_set3)

    def delete_replacees(self):
        """
        deletes alle replacees (IfcElements that are replaced by another (virtual) element) in graph Ro
        :return:
        """
        replacement_set = get_replacement_nodes(self.graph_schematic)  # get all nodes named "replacing"
        deletioncount = 0
        for guid_of_replacement in replacement_set:
            for replacee_id in self.graph_schematic.nodes[guid_of_replacement]['replacing']:
                element = self.ifcfile.by_guid(replacee_id)
                if deletioncount % 100 == 0:
                    logging.info("deleted " + str(deletioncount) + " elements so far")
                deletioncount = deletioncount + 1
                self.ifcfile.remove(element)
        logging.info("deleted " + str(deletioncount) + " elements")
        logging.info("deleting orphans")
        remove_orphans_of_IfcElements(self.ifcfile)

    def generate_graph_schematic(self):
        self.graph_schematic = reduce_graph_to_skeleton_replacing(self.graph_construction)


    def reduction_for_manual_assignment(self):
        Ao_backup=self.graph_Sm2_3.copy()
        graph=self.graph_Sm_3
        removed_nodes = set()
        full_nodelist = list(graph.nodes())
        for node_id in full_nodelist:
            if node_is_deletion_candidate(self.graph_Sm_3.nodes[node_id]):
                ns = nx.all_neighbors(self.graph_Sm_3, node_id) #also predecessors (in a directed graph)
                neighbors = list(ns)
                if len(neighbors) == 2: #hier werden nun auch die ehemaligen Verbindungsknoten zwischen Clustern entfernt #TODO prüfen, ob das eine gute Idee ist
                    neighbor_1_id = neighbors[0]
                    neighbor_2_id = neighbors[1]
                    graph.remove_node(node_id)
                    graph.add_edge(neighbor_1_id, neighbor_2_id)
                    removed_nodes.add(node_id)
        nx.set_edge_attributes(graph, dict(len=1)) #add length 1 to all edges compiled from IFC-file (important for "weight" of edges)
        self.graph_Rwr_3 = graph
        self.graph_Sm2_3 = Ao_backup.copy()


    def attach_position_in_ifc_as_node_attribute(self, graph):
        # get original positions in model, z-position mainly ignored, only as scatter
        dict_coordinates = get_coordinates(self.ifcfile, graph.nodes())
        dict_coordinates_2D = {}
        dict_coordinates_3D = {}
        for node_id in dict_coordinates:
            x = dict_coordinates[node_id][0]
            y = dict_coordinates[node_id][1]
            z = dict_coordinates[node_id][2]
            dict_coordinates_2D[node_id] = [x, y]
            dict_coordinates_3D[node_id] = [x, y, z]
        delta_x_2D, delta_y_2D, min_x_2D, min_y_2D = get_range_xy(dict_coordinates_2D)
        delta_z, min_z = get_range_z(dict_coordinates_3D)
        for node_id in graph:
            graph.nodes[node_id]["lon_pos"] = (dict_coordinates_2D[node_id][0] - min(min_x_2D, 0)) * 10 / delta_x_2D + dict_coordinates_3D[node_id][2] * 1 / delta_z  # no negative values, add scatter_z
            graph.nodes[node_id]["lat_pos"] = (dict_coordinates_2D[node_id][1] - min(min_y_2D, 0)) * 10 / delta_y_2D + dict_coordinates_3D[node_id][2] * 1 / delta_z  # no negative values, add scatter_z


    def attach_Am2_3_layout_position_as_node_attribute(self, graph):
        # original layout with all nodes
        delta_x_orig, delta_y_orig, min_x_orig, min_y_orig = get_range_xy(self.layout_Am2_3)
        for node_id in graph:
            graph.nodes[node_id]["lon_spring_Am2_3"] = self.layout_Am2_3[node_id][0] * 10 / delta_x_orig  # Graphviz Geo-Layout needs coordinates given in degree: -180...0...+180, but I don't want to get into effects of projection, so limit value range between 0...10
            graph.nodes[node_id]["lat_spring_Am2_3"] = self.layout_Am2_3[node_id][1] * 10 / delta_y_orig  # -90...0...+90


    def attach_spring_layout_position_as_node_attribute(self, graph):
        # spring layout only with reduced nodes
        pos_reduced = nx.nx_agraph.graphviz_layout(graph)
        delta_x_red, delta_y_red, min_x_red, min_y_red = get_range_xy(pos_reduced)

        for node_id in graph:
            graph.nodes[node_id]["lon_spring"] = pos_reduced[node_id][0] * 10 / delta_x_red  # Graphviz Geo-Layout needs coordinates given in degree: -180...0...+180, but I don't want to get into effects of projection, so limit value range between 0...10
            graph.nodes[node_id]["lat_spring"] = pos_reduced[node_id][1] * 10 / delta_y_red  # -90...0...+90


    def attach_positions_as_node_attribute(self, graph):
        self.attach_spring_layout_position_as_node_attribute(graph)
        try:
            self.attach_Am2_3_layout_position_as_node_attribute(graph)
        except:
            logging.info("graph Am2_3 not present -- layout parameteres therefore not attached as node attribute")
        self.attach_position_in_ifc_as_node_attribute(graph)


    def add_new_edges_to_ifcfile(self, graph_new, graph_old, name_edge, oh):
        """
        prüft ob die Kante im alten Graph schon (ggf. flipped) vorhanden war - wenn NICHT:
        - erzeugt er zwei Ports "new port for edge with replacement"
        - erzeugt er die entsprechende Verbindung "new edge from graph algorithm (" + name +")"
        :param graph_new:
        :param graph_old:
        :param name_edge: IfcRoot#Name der neuen IfcRelConnectsPorts-Entität
        :param oh:
        :return:
        """
        edgelistold = [e for e in graph_old.edges]
        edge_set_old = set()
        edge_set_old.update(edgelistold)
        #oh = create_IfcOwnerHistory(self.ifcfile)
        counter_edges = 0
        counter_nodes = 0
        for edge in graph_new.edges:
            flipped_edge = (edge[1], edge[0], 0)
            if (not edge in edge_set_old) and (not flipped_edge in edge_set_old): #neue Ports erstellen und verbinden
                #logging.info("adding edge to ifcfile " + str(edge))
                node_1 = edge[0]
                node_2 = edge[1]
                try: # to enable also the processing of new graphs that have nodes, that are not present in this IFC-File: in this case only the edges connecting two existing nodes/IfcElements are added
                    element_1 = self.ifcfile.by_guid(node_1)
                    element_2 = self.ifcfile.by_guid(node_2)
                    counter_edges = counter_edges + 1
                    new_port_1 = self.ifcfile.createIfcDistributionPort(ifcopenshell.guid.new(), oh, None, "new port for edge with replacement", None, element_1.ObjectPlacement, None, "SOURCEANDSINK", None, None)
                    if len(element_1.IsNestedBy) > 0: #wenn es schon eingebettete Elemente gibt
                        old_nested_objects = element_1.IsNestedBy[0].RelatedObjects
                        new_nested_objects = old_nested_objects + (new_port_1, )
                        ifcopenshell.api.run("attribute.edit_attributes", self.ifcfile, product=element_1.IsNestedBy[0], attributes={"RelatedObjects": new_nested_objects})
                    else:
                        counter_nodes = counter_nodes + 1
                        self.ifcfile.createIfcRelNests(ifcopenshell.guid.new(), oh, None, None, element_1, (new_port_1, ))
                    new_port_2 = self.ifcfile.createIfcDistributionPort(ifcopenshell.guid.new(), oh, None, "new port for edge with replacement", None, element_2.ObjectPlacement, None, "SOURCEANDSINK", None, None)
                    if len(element_2.IsNestedBy) > 0:  # wenn es schon eingebettete Elemente gibt
                        old_nested_objects = element_2.IsNestedBy[0].RelatedObjects
                        new_nested_objects = old_nested_objects + (new_port_2,)
                        ifcopenshell.api.run("attribute.edit_attributes", self.ifcfile, product=element_2.IsNestedBy[0], attributes={"RelatedObjects": new_nested_objects})
                    else:
                        counter_nodes = counter_nodes + 1
                        self.ifcfile.createIfcRelNests(ifcopenshell.guid.new(), oh, None, None, element_2, (new_port_2, ))
                    guid_edge = ifcopenshell.guid.new()
                    self.ifcfile.createIfcRelConnectsPorts(guid_edge, oh, name_edge, None, new_port_1, new_port_2, None)
                except:
                    logging.info("edge skipped, because node not present: " + str(edge))
                finally:
                    a=0
        logging.info(name_edge + ": added " + str(counter_edges) + " edges (and two ports for each)")
        logging.info(name_edge + ": added " + str(counter_nodes) + " new IfcRelNests (if no port was present at these elements before)")


    def export_to_gephi_with_guid_labels(self, graph, filename):
        for node in graph.nodes:
            if 'replacing' in graph.nodes[node]:
                graph.nodes[node].pop("replacing")
        graph_simple = nx.Graph(graph)  # to get rid of the double edges
        nx.write_graphml(graph_simple, filename, named_key_ids=True)

    def export_to_gephi_with_guid_labels_with_positions(self, graph, filename):
        self.attach_positions_as_node_attribute(graph)
        graph_simple = nx.Graph(graph) #to get rid of the double edges
        nx.write_graphml(graph_simple, filename, named_key_ids=True)

    def export_to_gephi_with_name_labels(self, graph, filename):
        self.attach_positions_as_node_attribute(graph)
        graph_simple = nx.Graph(graph)
        mapping = dict()
        for node_id in graph_simple:
            mapping[node_id] = graph_simple.nodes[node_id]["ifc_name"]
        graph_simple = nx.relabel_nodes(graph_simple, mapping)
        nx.write_graphml(graph_simple, filename, named_key_ids=True)


    def dict_mapping_name_guid(self):
        graph_simple = nx.Graph(self.graph_Sm2_3.copy())
        mapping_reverse = dict()
        for node_id in graph_simple:
            mapping_reverse[graph_simple.nodes[node_id]["ifc_name"]] = node_id
        return mapping_reverse

    def add_edges_by_nodenames_Am2_5(self, new_edges, dict_mapping_name_guid):
        for new_edge in new_edges:
            name_1 = new_edge[0]
            name_2 = new_edge[1]
            guid_1 = dict_mapping_name_guid[name_1]
            guid_2 = dict_mapping_name_guid[name_2]
            self.graph_Am2_5.add_edge(guid_1, guid_2)

    def add_edges_by_guid(self, new_edges):
        for new_edge in new_edges:
            self.graph_Am2_6.add_edge(new_edge[0], new_edge[1])


    def delete_edges_by_nodenames_Am2_5(self, deleted_edges, dict_mapping_name_guid):
        for del_edge in deleted_edges:
            name_1 = del_edge[0]
            name_2 = del_edge[1]
            guid_1 = dict_mapping_name_guid[name_1]
            guid_2 = dict_mapping_name_guid[name_2]
            try:
                self.graph_Am2_5.remove_edge(guid_1, guid_2)
            except:
                logging.info("deletion of edge not successful: " + str(guid_1) +" - " + str(guid_2))

    def delete_edges_by_guid(self, deleted_edges):
        for del_edge in deleted_edges:
            try:
                self.graph_Am2_6.remove_edge(del_edge[0], del_edge[1])
            except:
                logging.info("deletion of edge not successful: " + str(del_edge[0]) +" - " + str(del_edge[1]))

    def attach_graph_properties_of_nodes_to_ifcfile(self): #TODO maybe approach it the other way round: generate the properties for all elements in the file, instead of elements in the graph
        owner_history = create_IfcOwnerHistory(self.ifcfile)
        attach_property_with_nodestatus_to_ifcfile(self.graph_construction, nx.MultiDiGraph(), property_name="Status Ao", propertyset_name="Graph_EE", owner_history=owner_history, ifcfile=self.ifcfile)
        try:
            attach_property_with_nodestatus_to_ifcfile(self.graph_So, nx.MultiDiGraph(), property_name="Status So", propertyset_name="Graph_EE", owner_history=owner_history, ifcfile=self.ifcfile)
            attach_property_with_nodestatus_to_ifcfile(self.graph_Am_0, nx.MultiDiGraph(), property_name="Status Am_0", propertyset_name="Graph_EE", owner_history=owner_history, ifcfile=self.ifcfile)
            attach_property_with_nodestatus_to_ifcfile(self.graph_Am_1, self.graph_new_edges_1, property_name="Status Am_1", propertyset_name="Graph_EE", owner_history=owner_history, ifcfile=self.ifcfile)
            attach_property_with_nodestatus_to_ifcfile(self.graph_Am_2, self.graph_new_edges_2, property_name="Status Am_2", propertyset_name="Graph_EE", owner_history=owner_history, ifcfile=self.ifcfile)
            attach_property_with_nodestatus_to_ifcfile(self.graph_Am_3, self.graph_new_edges_3, property_name="Status Am_3", propertyset_name="Graph_EE", owner_history=owner_history, ifcfile=self.ifcfile)
            attach_property_with_nodestatus_to_ifcfile(self.graph_Am2_3, nx.MultiDiGraph(), property_name="Status Am2_3", propertyset_name="Graph_EE", owner_history=owner_history, ifcfile=self.ifcfile)
            attach_property_with_nodestatus_to_ifcfile(self.graph_Am2_4, nx.MultiDiGraph(), property_name="Status Am2_4", propertyset_name="Graph_EE", owner_history=owner_history, ifcfile=self.ifcfile)
            attach_property_with_nodestatus_to_ifcfile(self.graph_Am2_5, nx.MultiDiGraph(), property_name="Status Am2_5", propertyset_name="Graph_EE", owner_history=owner_history, ifcfile=self.ifcfile)
        except:
            logging.info("one/several of graphs So, Am_0...3, Am2_3...5 not present - therefore no properties (of it and all successors) were attached to ifcfile")
        try:
            attach_property_with_nodestatus_to_ifcfile(self.graph_Am2_6, nx.MultiDiGraph(), property_name="Status Am2_6", propertyset_name="Graph_EE", owner_history=owner_history, ifcfile=self.ifcfile)
        except:
            logging.info("graph Am2_6 not present - therefore no properties were attached for it to ifcfile")

    def generate_df_of_leafs_and_loners(self):
        #loner_set, leaf_set, leaf_set_connected, pipe_set, pipe_set_connected, hub_set, hub_set_connected = generate_Sets_for_graph(self.ifcfile, self.graph_Am2_3, nx.MultiDiGraph())
        element_id_list = [e.GlobalId for e in self.elements_without_ports]
        interesting_nodes=self.leafs_and_loners_set_o.union(element_id_list)
        ls_guid = []
        ls_name = []
        ls_connections = []
        ls_system = []
        ls_storey = []
        for node_id in interesting_nodes:
            element=self.ifcfile.by_guid(node_id)
            ls_guid.append(element.GlobalId)
            ls_name.append(element.Name)
            ns = nx.all_neighbors(self.graph_Am2_3, node_id)  # also predecessors (in a directed graph)
            neighbor_count = len(list(ns))
            ls_connections.append(neighbor_count)
            if element.HasAssignments:
                ls_system.append(element.HasAssignments[0].RelatingGroup.Name)  # TODO handle several groups
            else:
                ls_system.append("no system")
                #logging.info("element without Group: " + str(element))
            ls_storey.append(self.name)
        df_leafs_loners = pd.DataFrame({
            "Name": ls_name,
            "Anzahl Verbindungen in Am2_3": ls_connections,
            "zugehöriges System": ls_system,
            "File": ls_storey,
        }, index=ls_guid)
        return df_leafs_loners

    def add_edges_from_manual_assignment_list_Am2_4(self, df_con):
        self.graph_Am2_4 = self.graph_Am2_3.copy()
        edge_len = 1
        for index, row in df_con.iterrows():
            guid_1=row['guid']
            guid_2=row['guid partner']
            if (guid_1 in self.graph_Am2_4.nodes and guid_2 in self.graph_Am2_4.nodes): #only add edges if both nodes are present in graph already
                self.graph_Am2_4.add_edge(guid_1, guid_2, len=edge_len)

    def generate_layout_and_colour_Am2_3(self):
        self.layout_Am2_3 = nx.nx_agraph.graphviz_layout(self.graph_Am2_3)
        self.graph_Am2_3 = generate_cluster_no_as_attribute(self.graph_Am2_3.to_undirected()) #cluster number is for nodes only, therefore it is sufficient to generate it for a graph that contains all nodes (But not all edges yet)
        copy_cluster_no_as_attribute(self.graph_Am2_3, self.graph_construction)
        copy_cluster_no_as_attribute(self.graph_Am2_3, self.graph_So)
        copy_cluster_no_as_attribute(self.graph_Am2_3, self.graph_Sm_0)
        copy_cluster_no_as_attribute(self.graph_Am2_3, self.graph_Sm_1)
        copy_cluster_no_as_attribute(self.graph_Am2_3, self.graph_Sm_2)
        copy_cluster_no_as_attribute(self.graph_Am2_3, self.graph_Sm_3)
        copy_cluster_no_as_attribute(self.graph_Am2_3, self.graph_Sm2_3)

    def generate_layout_and_colour_Am2_4(self):
        """
        generates layout for graph Am2_4
        adds cluster no as attribute to Am2_4
        :return:
        """
        self.layout_Am2_4 = nx.nx_agraph.graphviz_layout(self.graph_Am2_4)
        self.graph_Am2_4 = generate_cluster_no_as_attribute(self.graph_Am2_4.to_undirected()) #cluster number is for nodes only, therefore it is sufficient to generate it for a graph that contains all nodes (But not all edges yet)

    def figure_leafs_names(self, graph, layout, graphname, full_graph):
        """
        :param graph: graph to plot
        :param layout: name of the layout to use, if "x" is given --> standard spring layout is used
        :param graphname: used for title and filename
        :param full_graph: graph to use for layout creation, also the ifc_name of the nodes is taken from there
        :return: save figure named "_leafs_names.png" to EAS_KLT/output_logs
        """
        if layout=="x":
            layout = nx.nx_agraph.graphviz_layout(full_graph)
        fig, axes = plt.subplots(nrows=1, ncols=1, figsize=(5.7, 4), dpi=300)
        fig.suptitle("graph representations ("+ graphname + ") of EAS Neubau Kälte " + self.name + "/n(labels showing names of components, color: ifc_predefined_type)", fontsize=10)

        axes.set_title(graph_characteristics_as_string(graph), fontsize=6)
        plot_customized(graph, layout, axes=axes, color_attribute="ifc_predefined_type", label_attribute="x")

        #leafs_graph, leafs_set = get_leafs_and_loners(graph)
        leafs_graph = nx.Graph()
        element_id_list = [e.GlobalId for e in self.elements_without_ports]
        interesting_nodes=self.leafs_and_loners_set_o.union(element_id_list)
        for element_id in interesting_nodes:
            leafs_graph.add_node(element_id, ifc_name=full_graph.nodes[element_id]["ifc_name"])
        draw_networkx_labels(leafs_graph, layout, labels=nx.get_node_attributes(leafs_graph, "ifc_name"), bbox=None, horizontalalignment='center', verticalalignment='center', ax=axes, clip_on=True, font_size=3)

        fig.savefig("C:/_DATEN/WORKSPACES/PyCharm/ifcscripting/_Projekte/EAS_KLT/output_logs/" + self.name + "_" + graphname + "_leafs_names.png")
        plt.close(fig="all")

    def figures_systemwise_leafs_names(self, graph, layout, graphname, full_graph):
        """
        :param graph: graph to plot
        :param layout: name of the layout to use, if "x" is given --> standard spring layout is used
        :param graphname: used for title and filename
        :param full_graph: graph to use for layout creation, also the ifc_name of the nodes is taken from there
        :return: save figure named "_leafs_names.png" to EAS_KLT/output_logs
        """

        dict_systems = {
            'Kondensat': ['_Kondensat'],
            'Split': ['Split_VL', 'Split_RL', 'Split'],
            'Server': ['KK3_Server_VL', 'KK3_Server_RL', 'KK3_Server'],
            'ULK': ['KK2_ULK_VL', 'KK2_ULK_RL', 'KK2_ULK'],
            'BKA': ['KK1_BKA_VL', 'KK1_BKA_RL', 'KK1_BKA'],
            'RLT': ['KK4_RLT_VL', 'KK4_RLT_RL', 'KK4_RLT'],
            'Zuluft': ['_Zuluft'],
            'Erzeugung': ['KK_Erzeugung_VL', 'KK_Erzeugung_RL', 'KK_Erzeugung'],
            'Rueckkuehlung': ['KK_Rueckkuehlung_VL', 'KK_Rueckkuehlung_RL', 'KK_Rueckkuehlung'],
        }

        if layout=="x":
            layout = nx.nx_agraph.graphviz_layout(full_graph)

        for system in dict_systems:
            #create reduced subgraph
            system_sub_graph = graph.copy()
            full_nodelist = list(system_sub_graph.nodes())
            for node_id in full_nodelist:
                subsystemnames = dict_systems[system]
                if not system_sub_graph.nodes[node_id]["ifc_system"] in subsystemnames:
                    system_sub_graph.remove_node(node_id)

            fig, axes = plt.subplots(nrows=1, ncols=1, figsize=(5.7, 4), dpi=300)
            fig.suptitle(graphname + " System: " + system + "/n(labels showing names of components, color: ifc_predefined_type)", fontsize=10)

            axes.set_title(graph_characteristics_as_string(system_sub_graph), fontsize=6)
            plot_customized(system_sub_graph, layout, axes=axes, color_attribute="ifc_predefined_type", label_attribute="x")

            ll_graph, ll_set, leafs_set, loners_set = get_leafs_and_loners(system_sub_graph)
            element_id_list = [e.GlobalId for e in self.elements_without_ports]
            interesting_nodes=ll_set.union(element_id_list)
            for element_id in interesting_nodes:
                ll_graph.add_node(element_id, ifc_name=full_graph.nodes[element_id]["ifc_name"])
            draw_networkx_labels(ll_graph, layout, labels=nx.get_node_attributes(ll_graph, "ifc_name"), bbox=None, horizontalalignment='center', verticalalignment='center', ax=axes, clip_on=True, font_size=3)

            fig.savefig("C:/_DATEN/WORKSPACES/PyCharm/ifcscripting/_Projekte/EAS_KLT/output_logs/" + self.name + "_" + graphname + "_" + system + "_leafs_names.png")
            plt.close(fig="all")

    def figures_systemwise_compare_A_S(self, graph_skeleton, graph_full, graphname, path="c:/_NEXTCLOUD/EAS/Dissertation/Bilder Visualisierungen/058 Schematizer/"):
        dict_systems = {
         #   'Kondensat': ['_Kondensat'],
         #   'Split': ['Split_VL', 'Split_RL', 'Split'],
            'Server': ['KK3_Server_VL', 'KK3_Server_RL', 'KK3_Server'],
       #     'ULK': ['KK2_ULK_VL', 'KK2_ULK_RL', 'KK2_ULK'],
       #     'BKA': ['KK1_BKA_VL', 'KK1_BKA_RL', 'KK1_BKA'],
       #     'RLT': ['KK4_RLT_VL', 'KK4_RLT_RL', 'KK4_RLT'],
       #     'Zuluft': ['_Zuluft'],
       #     'Erzeugung': ['KK_Erzeugung_VL', 'KK_Erzeugung_RL', 'KK_Erzeugung'],
        #    'Rueckkuehlung': ['KK_Rueckkuehlung_VL', 'KK_Rueckkuehlung_RL', 'KK_Rueckkuehlung'],
        }

        for system in dict_systems:
            #create subgraphs by system
            graph_system_original = graph_by_system(graph_full, dict_systems[system])
            graph_system_skeleton = graph_by_system(graph_skeleton, dict_systems[system])
            graph_system_combined = compose(nx.Graph(graph_system_skeleton), nx.Graph(graph_system_original))
            layout1 = nx.nx_agraph.graphviz_layout(graph_system_original)
            layout2 = nx.nx_agraph.graphviz_layout(graph_system_combined)
            layout3 = nx.nx_agraph.graphviz_layout(graph_system_skeleton)

            #label2 enthält die Bezeichnung im Skeleton (entweder wie Original oder keine (falls es ein replacee ist))
            for node in graph_system_original:
                if node in graph_system_skeleton:
                    graph_system_original.nodes[node]['label2'] = graph_system_original.nodes[node]['ifc_name']
                else:
                    graph_system_original.nodes[node]['label2'] = ""

            colordict = fixed_colordict()

            cm = 1 / 2.54
          #  fig, axes = plt.subplots(nrows=3, ncols=2, figsize=(17.4*cm, 22*cm), dpi=300)
            fig, axes = plt.subplots(nrows=3, ncols=2, figsize=(17.4*cm*2, 22*cm*2), dpi=300)
          #  axes.text(size=8, family=['calibri'])
            """
            Standardwerte für subplots_adjust
            left  = 0.125  # the left side of the subplots of the figure
            right = 0.9    # the right side of the subplots of the figure
            bottom = 0.1   # the bottom of the subplots of the figure
            top = 0.9      # the top of the subplots of the figure
            wspace = 0.1   # the amount of width reserved for blank space between subplots
            hspace = 0.1   # the amount of height reserved for white space between subplots
            """
            node_size = 4
            font_size = 8
            fig.subplots_adjust(left=0.01, right=0.99, bottom=0.01, top=0.95, hspace = 0.1, wspace = 0.1)

            fig.suptitle("Vergleich Konstruktions- und Berechnungsgraph für System: " + system, fontsize=font_size+2, family=['calibri'], y=1.0) #title oberhalb der figure

            subfigure_graph_plotlabel_type(axes[0,0], graph_system_original, layout1, "A original nodes and edges\nLayout 1: ", colordict, node_size=node_size, font_size=font_size)
            subfigure_graph_plotlabel_type(axes[0,1], graph_system_original, layout2, "B original nodes and edges\nLayout 2: ", colordict, node_size=node_size, font_size=font_size)
            subfigure_legend(axes[1,0], colordict, font_size=font_size, markersize=node_size)
            subfigure_graph_type(axes[1,1], graph_system_combined, layout2, "C original nodes and edges with replacements\nLayout 2: ", colordict, node_size=node_size, font_size=font_size)
            #subfigure_graph_name_type(axes[2,0], graph_system_skeleton, layout3, "E reduced graph\nLayout 3: ", colordict, node_size=node_size, font_size=font_size)
            subfigure_graph_type(axes[2,0], graph_system_skeleton, layout3, "E reduced graph\nLayout 3: ", colordict, node_size=node_size, font_size=font_size)
            #subfigure_graph_name_type(axes[2,1], graph_system_skeleton, layout2, "D reduced graph\nLayout 2: ", colordict, node_size=node_size, font_size=font_size)
            subfigure_graph_type(axes[2,1], graph_system_skeleton, layout2, "D reduced graph\nLayout 2: ", colordict, node_size=node_size, font_size=font_size)

            fig.savefig(path + self.name + "_" + system + "_ComparisonConstructionAndSchematicGraph.png")
            plt.close(fig="all")



    def figure_node_versions_3(self):
        """
        plots 3 subgraphs with node enhancements: Ao, So, Sm_0
        plot was formerly known as "_node_enhred_230213_3.png"
        :return: save figure named "_nodes_3.png" to EAS_KLT/output_logs
        """
        fig, axes = plt.subplots(nrows=1, ncols=3, figsize=(11.5, 4.8), dpi=300) #figsize in inch
        #fig, axes = plt.subplots(nrows=1, ncols=3, figsize=(6.3, 2.75), dpi=300) #16x7cm - aber dann passen die Texte nicht
        fig.suptitle("graph representations of EAS Neubau Kälte " + self.name + " (label and colour show cluster number) ", fontsize=10)

        subfigure_graph_cluster_no(axes[0], self.graph_construction, self.layout_Am2_3, "Original graph/n")
        subfigure_graph_cluster_no(axes[1], self.graph_So, self.layout_Am2_3, "skeleton/n")
        subfigure_graph_cluster_no(axes[2], self.graph_Sm_0, self.layout_Am2_3, "Enhanced by portless nodes with coordinates/n")

        fig.savefig("C:/_DATEN/WORKSPACES/PyCharm/ifcscripting/_Projekte/EAS_KLT/output_logs/" + self.name + "_nodes_3.png")
        plt.close(fig="all")

    def figure_node_versions_4(self):
        """
        plots 4 subgraphs with node enhancements: Ao, So, Sm_0, Sm2_3
        plot was formerly known as "_node_enhred_230213_4.png"
        :return: save figure named "_nodes_4.png" to EAS_KLT/output_logs
        :return:
        """
        fig, axes = plt.subplots(nrows=1, ncols=4, figsize=(11.5, 4.8), dpi=300) #figsize in inch
        #fig, axes = plt.subplots(nrows=1, ncols=3, figsize=(6.3, 2.75), dpi=300) #16x7cm - aber dann passen die Texte nicht
        fig.suptitle("graph representations of EAS Neubau Kälte " + self.name + " (label and colour show cluster number) ", fontsize=10)

        subfigure_graph_cluster_no(axes[0], self.graph_construction, self.layout_Am2_3, "Original graph/n")
        subfigure_graph_cluster_no(axes[1], self.graph_So, self.layout_Am2_3, "skeleton/n")
        subfigure_graph_cluster_no(axes[2], self.graph_Sm_0, self.layout_Am2_3, "Enhanced by portless nodes with coordinates/n")
        subfigure_graph_cluster_no(axes[3], self.graph_Sm2_3, self.layout_Am2_3, "Enhanced by portless nodes without coordinates/n")

        fig.savefig("C:/_DATEN/WORKSPACES/PyCharm/ifcscripting/_Projekte/EAS_KLT/output_logs/" + self.name + "_nodes_4.png")
        plt.close(fig="all")

    def figure_automated_edge_enhancement(self):
        """
        plot 3 subgraphs for Sm_1, Sm_2, Sm_3 and show automated edge enhancements
        plot was formerly known as "_edge_enh_230213.png"
        :return: save figure named "_automated_edge_enhancement.png" to EAS_KLT/output_logs
        """
        fig, axes = plt.subplots(nrows=1, ncols=3, figsize=(11.5, 4.8), dpi=300)
        fig.suptitle("graph representations of EAS Neubau Kälte " + self.name + " (label and colour show cluster number)",fontsize=10)

        subfigure_edge_change(axes[0], self.graph_Sm_1, self.graph_new_edges_1, self.layout_Am2_3, fontcolor="red", titletext="edges added between nodes with zero distance/n")
        subfigure_edge_change(axes[1], self.graph_Sm_2, self.graph_new_edges_2, self.layout_Am2_3, fontcolor="blue", titletext="edges added between nodes with almost zero distance/n")
        subfigure_edge_change(axes[2], self.graph_Sm_3, self.graph_new_edges_3, self.layout_Am2_3, fontcolor="green", titletext="edges added between nodes with distance below threshold/n")

        fig.savefig("C:/_DATEN/WORKSPACES/PyCharm/ifcscripting/_Projekte/EAS_KLT/output_logs/" + self.name + "_automated_edge_enhancement.png")
        plt.close(fig="all")

    def figure_automated_edge_enhancement_in_single_graph(self):
        """
        plot graph with automatically created edges in different colors
        plot was formerly known as "_edge_check.png"
        :return:  save figure named "_automated_edge_enhancement.png" to EAS_KLT/output_logs
        """
        fig, axes = plt.subplots(nrows=1, ncols=1, figsize=(11.5, 4.8), dpi=300)
        fig.suptitle("graph representations of EAS Neubau Kälte " + self.name + " (label and colour show cluster number)",fontsize=10)
        axes.set_title(graph_characteristics_as_string(self.graph_Sm2_3), fontsize=6)

        plot_customized(self.graph_Sm2_3, self.layout_Am2_3, axes=axes, color_attribute="cluster_no", label_attribute="x")
        nx.draw_networkx_edges(self.graph_new_edges_1, edge_color="red", ax=axes, pos=self.layout_Am2_3, arrows=False)
        nx.draw_networkx_edges(self.graph_new_edges_2, edge_color="blue", ax=axes, pos=self.layout_Am2_3, arrows=False)
        nx.draw_networkx_edges(self.graph_new_edges_3, edge_color="green", ax=axes, pos=self.layout_Am2_3, arrows=False)

        leafs_graph = nx.Graph()
        element_id_list = [e.GlobalId for e in self.elements_without_ports]
        interesting_nodes = self.leafs_and_loners_set_o.union(element_id_list)
        for element_id in interesting_nodes:
            leafs_graph.add_node(element_id, ifc_name=self.graph_Sm2_3.nodes[element_id]["ifc_name"])
        draw_networkx_labels(leafs_graph, self.layout_Am2_3, labels=nx.get_node_attributes(leafs_graph, "ifc_name"), bbox=None, horizontalalignment='center', verticalalignment='center', ax=axes, clip_on=True, font_size=3)

        fig.savefig("C:/_DATEN/WORKSPACES/PyCharm/ifcscripting/_Projekte/EAS_KLT/output_logs/" + self.name + "_automated_edge_enhancement_in_single_graph.png")
        plt.close(fig="all")




    def attach_schematics_to_ifcfile_Am2_4(self):
        """
        generate schematics of Am2_4
        :return:
        """
        #TODO make the exported graph a parameter
        #TODO try different layouts of graph

        # geometric contexts
        Origin_WCS = self.ifcfile.createIfcCartesianPoint((0.0, 0.0, 0.0))
        RefDirectionX_WCS = self.ifcfile.createIfcDirection((1., 0., 0.))
        AxisZ_WCS = self.ifcfile.createIfcDirection((0., 0., 1.))
        A2P_WCS = self.ifcfile.createIfcAxis2Placement3D(Origin_WCS, AxisZ_WCS, RefDirectionX_WCS) #TODO lieber vorhandenen Context übernehmen
        context2D1 = self.ifcfile.create_IfcGeometricRepresentationContext('ContextPlan2D', 'Plan', 2, 0.001, A2P_WCS, None)
        project = self.ifcfile.by_type('IfcProject')[0]
        old_contexts = project.RepresentationContexts
        new_contexts = old_contexts + (context2D1,)
        ifcopenshell.api.run("attribute.edit_attributes", self.ifcfile, product=project, attributes={"RepresentationContexts": new_contexts})
        subcontext2D1 = self.ifcfile.createIfcGeometricRepresentationSubContext('Schematics', 'Plan', None, None, None, None, context2D1, 0.01, "PLAN_VIEW", None); #vorletzter Parameter "TargetView" taucht im FZK nicht auf

        layout, graph_with_cluster_no = generate_layout_and_colour()
        self.generate_layout_and_colour_Am2_4()
        x_min, x_max, y_min, y_max = get_extent_of_layout(self.layout_Am2_4)

        fontSize = self.ifcfile.createIfcPositiveLengthMeasure(20)
        textStyleFontModel = self.ifcfile.createIfcTextStyleFontModel('Ellis Font', ['sans-serif'], None, None, None, fontSize)
        textcolor=self.ifcfile.createIfcColourRGB(None, 1.0, 1.0, 0.0)
        textStyleForDefinedFont = self.ifcfile.createIfcTextStyleForDefinedFont(textcolor, None)
        textStyle = self.ifcfile.createIfcTextStyle(None, textStyleForDefinedFont, None, textStyleFontModel, None)

        textBoxExtent = self.ifcfile.createIfcPlanarExtent(100, 50)  # TODO variable extent?

        for node in self.graph_Am2_3.nodes: #TODO why Am2_3 here?
            element=self.ifcfile.by_guid(node)
            symbol_position_in_wcs=self.layout_Am2_4[node]
            if (element.ObjectPlacement.RelativePlacement.RefDirection == None):
                RefDirectionX_ECS_in_WCS = (1,0,0)
            else:
                RefDirectionX_ECS_in_WCS = element.ObjectPlacement.RelativePlacement.RefDirection.DirectionRatios
            if (element.ObjectPlacement.RelativePlacement.Axis == None):
                AxisZ_ECS_in_WCS = (0,0,1)
            else:
                AxisZ_ECS_in_WCS = element.ObjectPlacement.RelativePlacement.Axis.DirectionRatios

            tuple_Origin_WCS_in_ECS =   rotate_local_coordinates_in_global_coordinates(tuple(np.array(element.ObjectPlacement.RelativePlacement.Location.Coordinates) * -1), RefDirectionX_ECS_in_WCS, AxisZ_ECS_in_WCS)

            Origin_WCS_in_ECS       = self.ifcfile.createIfcCartesianPoint( tuple_Origin_WCS_in_ECS)
            RefDirectionX_WCS_in_ECS= self.ifcfile.createIfcDirection(      rotate_local_coordinates_in_global_coordinates((1.0, 0.0, 0.0), RefDirectionX_ECS_in_WCS, AxisZ_ECS_in_WCS) )#tuple contains float64 and therefore it cannot be used directly
            AxisZ_WCS_in_ECS        = self.ifcfile.createIfcDirection(      rotate_local_coordinates_in_global_coordinates((0.0, 0.0, 1.0), RefDirectionX_ECS_in_WCS, AxisZ_ECS_in_WCS) )
            A2P_WCS_in_ECS = self.ifcfile.createIfcAxis2Placement3D(Origin_WCS_in_ECS, AxisZ_WCS_in_ECS, RefDirectionX_WCS_in_ECS)

            #circle
            x=-element.ObjectPlacement.RelativePlacement.Location.Coordinates[0]+symbol_position_in_wcs[0]
            y=-element.ObjectPlacement.RelativePlacement.Location.Coordinates[1]+symbol_position_in_wcs[1]
            z=-element.ObjectPlacement.RelativePlacement.Location.Coordinates[2]
            tuple_Symbol_in_ECS = rotate_local_coordinates_in_global_coordinates((x,y,z), RefDirectionX_ECS_in_WCS, AxisZ_ECS_in_WCS)
            Symbol_in_ECS = self.ifcfile.createIfcCartesianPoint(tuple_Symbol_in_ECS)
            A2P_Symbol_in_ECS = self.ifcfile.createIfcAxis2Placement3D(Symbol_in_ECS, AxisZ_WCS_in_ECS, RefDirectionX_WCS_in_ECS)
            circle = self.ifcfile.createIfcCircle(A2P_Symbol_in_ECS, 20.0)
            #Text
            textliteral = self.ifcfile.createIfcTextLiteralWithExtent(element.Name, A2P_Symbol_in_ECS, 'LEFT', textBoxExtent, 'bottom-left')
            self.ifcfile.createIfcStyledItem(textliteral, [textStyle])
            #line to neighbors
            ns = nx.all_neighbors(self.graph_Am2_4, node)
            list_of_lines_to_neighbors = []
            for neighbor in ns:
                x = -element.ObjectPlacement.RelativePlacement.Location.Coordinates[0] + self.layout_Am2_4[neighbor][0]
                y = -element.ObjectPlacement.RelativePlacement.Location.Coordinates[1] + self.layout_Am2_4[neighbor][1]
                z = -element.ObjectPlacement.RelativePlacement.Location.Coordinates[2]
                tuple_neighbor_symbol_in_ECS = rotate_local_coordinates_in_global_coordinates((x, y, z), RefDirectionX_ECS_in_WCS, AxisZ_ECS_in_WCS)
                nb= self.ifcfile.createIfcCartesianPoint(tuple_neighbor_symbol_in_ECS)
                polyline = self.ifcfile.createIfcPolyLine([Symbol_in_ECS, nb])
                list_of_lines_to_neighbors.append(polyline)
            list_of_lines_to_neighbors.append(circle)

            symbol_representation = self.ifcfile.createIfcShapeRepresentation(subcontext2D1, "Schematics", "Curve2D", list_of_lines_to_neighbors)
            symbol_label_representation = self.ifcfile.createIfcShapeRepresentation(subcontext2D1, "Schematics Text", "Text", [textliteral])
            pds=element.Representation
            old_representations=pds.Representations
            new_representations=old_representations + (symbol_representation,symbol_label_representation)
            ifcopenshell.api.run("attribute.edit_attributes", self.ifcfile, product=pds, attributes={"Representations": new_representations})

    def export_to_TTL(self, path):
        rdfGraph = Graph() #no namespace collision with networkx? Beispiel: GB=nx.Graph()
        nm = rdfGraph.namespace_manager
        ns_ex = rdflib.Namespace("http://eas.iis.fraunhofer.de/ifc/neubau/"+self.name+"/")
        ns_brick = rdflib.Namespace("https://brickschema.org/schema/1.0.3/Brick/") #TODO check for updated BRICK version
        ns_ifc = rdflib.Namespace("https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL/") #TODO choose correct IFC version according to file header
        nm.bind("eas_"+self.name, ns_ex)
        nm.bind("brick", ns_brick)
        nm.bind("ifc", ns_ifc)
        for node in self.graph_Am2_3.nodes:
            s = URIRef("http://eas.iis.fraunhofer.de/ifc/neubau/" + self.name + "/" + node)
            ifcclass = self.ifcfile.by_guid(node).is_a()
            o1 = URIRef("https://standards.buildingsmart.org/IFC/DEV/IFC4/ADD2_TC1/OWL/" + ifcclass)
            rdfGraph.add((s, RDF.type, o1,))
            o2 = rdflib.Literal(self.ifcfile.by_guid(node).Name, datatype=XSD.String)
            rdfGraph.add((s, RDFS.label, o2))
        for edge in self.graph_Am2_3.edges:
            node_1 = URIRef("http://eas.iis.fraunhofer.de/ifc/neubau/"+self.name+"/"+edge[0])
            node_2 = URIRef("http://eas.iis.fraunhofer.de/ifc/neubau/"+self.name+"/"+edge[1])
            rdfGraph.add((node_1, ns_brick.feeds, node_2,))
        rdfGraph.serialize(destination=path, format="ttl", encoding="utf-8")

    def dataFrame_graphinfo_with_ElementStatusFromIFC(self, graph, end_element_guid_set):
        leafs_and_loners_graph, leafs_and_loners_set, leafs_set, loners_set = get_leafs_and_loners(graph)
        ls_name = []
        ls_guid = []
        ls_class = []
        ls_ifc_predefined_type = []
        ls_system = []
        ls_elementstatus = []
        ls_container = []
        ls_status = []
        for leaf in leafs_and_loners_set:
            element = self.ifcfile.by_guid(leaf)
            dict = get_attribute_dict_with_EndElementInformation(element, end_element_guid_set, self.ifcfile_system)
            ls_name.append(element.Name)
            ls_guid.append(element.GlobalId)
            ls_class.append(dict["ifc_class"])
            ls_ifc_predefined_type.append(dict["ifc_predefined_type"])
            ls_system.append(dict["ifc_system"])
            ls_elementstatus.append(dict["elementstatus"])
            ls_container.append(dict["container"])
            if leaf in loners_set:
                ls_status.append("loner")
            else:
                ls_status.append("leaf")

        df = pd.DataFrame({
            "guid": ls_guid,
            "ifc_name": ls_name,
            "ifc_class": ls_class,
            "ifc_predefined_type": ls_ifc_predefined_type,
            "ifc_system": ls_system,
            "elementstatus": ls_elementstatus,
            "status_Am2_5": ls_status,
            "container": ls_container})
        return df

    def dataFrame_graphinfo_with_ElementStatusFromIfc(self, graph):
        leafs_and_loners_graph, leafs_and_loners_set, leafs_set, loners_set = get_leafs_and_loners(graph)
        ls_name = []
        ls_guid = []
        ls_class = []
        ls_ifc_predefined_type = []
        ls_system = []
        ls_elementstatus = []
        ls_container = []
        ls_status = []
        for leaf in leafs_and_loners_set:
            element = self.ifcfile.by_guid(leaf)
            dict = get_attribute_dict(element, self.ifcfile_system)
            ls_name.append(element.Name)
            ls_guid.append(element.GlobalId)
            ls_class.append(dict["ifc_class"])
            ls_ifc_predefined_type.append(dict["ifc_predefined_type"])
            ls_system.append(dict["ifc_system"])
            ls_elementstatus.append(dict["elementstatus"])
            ls_container.append(dict["container"])
            if leaf in loners_set:
                ls_status.append("loner")
            else:
                ls_status.append("leaf")

        df = pd.DataFrame({
            "guid": ls_guid,
            "ifc_name": ls_name,
            "ifc_class": ls_class,
            "ifc_predefined_type": ls_ifc_predefined_type,
            "ifc_system": ls_system,
            "elementstatus": ls_elementstatus,
            "status_Am2_5": ls_status,
            "container": ls_container})
        return df

    def dataFrame_graphinfo(self, graph):
        leafs_and_loners_graph, leafs_and_loners_set, leafs_set, loners_set = get_leafs_and_loners(graph)
        ls_name = []
        ls_guid = []
        ls_class = []
        ls_ifc_predefined_type = []
        ls_system = []
        ls_container = []
        ls_status = []
        for leaf in leafs_and_loners_set:
            element = self.ifcfile.by_guid(leaf)
            dict = get_attribute_dict(element, self.ifcfile_system)
            ls_name.append(element.Name)
            ls_guid.append(element.GlobalId)
            ls_class.append(dict["ifc_class"])
            ls_ifc_predefined_type.append(dict["ifc_predefined_type"])
            ls_system.append(dict["ifc_system"])
            ls_container.append(dict["container"])
            if leaf in loners_set:
                ls_status.append("loner")
            else:
                ls_status.append("leaf")

        df = pd.DataFrame({
            "guid": ls_guid,
            "ifc_name": ls_name,
            "ifc_class": ls_class,
            "ifc_predefined_type": ls_ifc_predefined_type,
            "ifc_system": ls_system,
            "status_graph": ls_status,
            "container": ls_container})
        return df

def port_names_for_new_ports(ifcfile, graph):
    set_of_elements_with23_ports = set()
    set_of_elements_odd_ports = set()
    for node in graph:
        element = ifcfile.by_guid(node)
        ports_all = element.IsNestedBy[0].RelatedObjects
        ports_for_new_edges = list()
        for port in ports_all:
            if port.Description == 'new port for edge with replacement':
                ports_for_new_edges.append(port)
        #TODO give port name if only one port is present
        if len(ports_for_new_edges) == 2:
            ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=ports_for_new_edges[0], attributes={"Description": "port_a"})
            ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=ports_for_new_edges[1], attributes={"Description": "port_b"})
            set_of_elements_with23_ports.add(element)
        elif len(ports_for_new_edges) == 3:
            ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=ports_for_new_edges[0], attributes={"Description": "port_1"})
            ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=ports_for_new_edges[1], attributes={"Description": "port_2"})
            ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=ports_for_new_edges[2], attributes={"Description": "port_3"})
            set_of_elements_with23_ports.add(element)
        else:
            #TODO prüfen warum es so viele Objekte mit 1 oder mehr als 3 ports gibt
            logging.info(str(element.Name) + " has " + str(len(ports_for_new_edges)) + " ports")
            set_of_elements_odd_ports.add(element)

