"""
functions to help with handling of nx-Graphs
"""
import logging
import networkx as nx
import pandas as pd
import ifcopenshell
from PythonLib.IFC.Inspect import dataFrame_given_IfcElement


def reduce_graph_to_skeleton(graph):
    Ao_backup = graph.copy()
    removed_nodes = set()
    full_nodelist = list(graph.nodes())
    for node_id in full_nodelist:
        if node_is_deletion_candidate(graph.nodes[node_id]):
            ns = nx.all_neighbors(graph, node_id)  # also predecessors (in a directed graph)
            neighbors = list(ns)
            if len(neighbors) == 2:  # hier werden nun auch die ehemaligen Verbindungsknoten zwischen Clustern entfernt #TODO prüfen, ob das eine gute Idee ist
                neighbor_1_id = neighbors[0]
                neighbor_2_id = neighbors[1]
                graph.remove_node(node_id)
                graph.add_edge(neighbor_1_id, neighbor_2_id)
                removed_nodes.add(node_id)
    nx.set_edge_attributes(graph, dict(len=1))  # add length 1 to all edges compiled from IFC-file (important for "weight" of edges)
    reduced_graph=graph
    graph = Ao_backup.copy()
    return reduced_graph


def reduce_graph_to_skeleton_simple(graph):
    """
    delete all uninteresting nodes without replacement
    :param graph:
    :return: original graph is manipulated therefore no return value
    """
    removed_nodes = set()
    full_nodelist = list(graph.nodes())
    for node_id in full_nodelist:
        if node_is_deletion_candidate(graph.nodes[node_id]):
            ns = nx.all_neighbors(graph, node_id)  # also predecessors (in a directed graph)
            neighbors = list(ns)
            if len(neighbors) == 2:  # hier werden nun auch die ehemaligen Verbindungsknoten zwischen Clustern entfernt #TODO prüfen, ob das eine gute Idee ist
                neighbor_1_id = neighbors[0]
                neighbor_2_id = neighbors[1]
                graph.remove_node(node_id)
                graph.add_edge(neighbor_1_id, neighbor_2_id)
                removed_nodes.add(node_id)
    nx.set_edge_attributes(graph, dict(len=1))  # add length 1 to all edges compiled from IFC-file (important for "weight" of edges)

def annotate_graph(graph):
    """
    original graph is manipulated, therefore no return value necessary
    :param graph:
    :return:
    """
    full_nodelist = list(graph.nodes())
    for node_id in full_nodelist:
        if node_is_deletion_candidate(graph.nodes[node_id]):
            graph.nodes[node_id]['status'] = 'white'
        else:
            graph.nodes[node_id]['status'] = 'keeper'
    #wenn es nicht genau zwei Nachbarn gibt, ist der Knoten interessant und bleibt, egal von welcher Art er ist
    for node_id in full_nodelist:
        ns = nx.all_neighbors(graph, node_id)  # also predecessors (in a directed graph)
        neighbors = list(ns)
        if len(neighbors) != 2:
            graph.nodes[node_id]['status'] = 'keeper'
    #wenn beide Nachbarn bleiben, dann bleibt auch der Knoten selbst - da es nichts sparen würde
    for node_id in full_nodelist:
        ns = nx.all_neighbors(graph, node_id)
        neighbors = list(ns)
        if len(neighbors) == 2:
            if graph.nodes[neighbors[0]]['status'] == 'keeper':
                if graph.nodes[neighbors[1]]['status'] == 'keeper':
                    graph.nodes[node_id]['status'] = 'keeper'
                    #2lqrrghQz0700100000sH8
    #TODO remove this temporary workaround :triangles should not be present!
    #set_of_trinangles = check_for_triangles(graph)  # vertices of trinangles are annotated in original graph
    return graph


def reduce_graph_to_skeleton_replacing(graph):
    """
    replace all uninteresting nodes with placeholder
    ersetze Reihenschaltungen uninteressanter Bauteile durch Ersatzknoten
    Originalgraph wird kopiert und nur die Kopie wird verändert
    Begriffe:
    * ersetzt - replaced - replacee
    * Ersatz - replacing - replacement
    :param graph:
    :return: veränderter Graph
    """
    full_nodelist = list(graph.nodes())
    new_graph = nx.Graph(graph)  # not directed, no multiple edges
    new_graph = annotate_graph(new_graph) #every node needs to have attribute 'status' - if it is 'keeper' this node is kept, else it is replaced (use function
    replacement_id=1
    set_of_replaced_nodes = set()
    for node_id in full_nodelist:
        if node_id in set_of_replaced_nodes:
            continue
        if new_graph.nodes[node_id]['status'] == 'keeper':
            continue
        replacement_string = "%03d" % replacement_id
        set_of_newly_replaced_nodes = handle_string_of_graph(node_id, new_graph, replacement_string)
        replacement_id += 1
        set_of_replaced_nodes = set_of_replaced_nodes.union(set_of_newly_replaced_nodes)
#    nx.set_edge_attributes(graph, dict(len=1))  # add length 1 to all edges compiled from IFC-file (important for "weight" of edges)
    logging.info("replaced " + str(len(set_of_replaced_nodes)) + " nodes by " + str(replacement_id) + " replacements")
    return new_graph


def handle_string_of_graph(startnode, graph, replacement_id):
    """
    ersetzt Strang durch einen Ersatzknoten
    :param startnode:
    :param graph: Originalgraph - wird manipuliert da nur als Zeiger übergeben
    :param replacement_id:
    :return: set_of_replaced_nodes
    """
    set_of_replaced_nodes = set()
    replacement_node_name = "replacement_" + str(replacement_id)
    replacement_node_id = ifcopenshell.guid.new()
    while '$$' in replacement_node_id:
        replacement_node_id = ifcopenshell.guid.new()
    graph.add_node(replacement_node_id, replacing = [startnode])
    graph.nodes[replacement_node_id]['ifc_name'] = replacement_node_name
    graph.nodes[replacement_node_id]['ifc_system'] = graph.nodes[startnode]['ifc_system']
    graph.nodes[replacement_node_id]['ifc_predefined_type'] = 'IfcPipeSegmentType_USERDEFINED'
    neighbors = list(nx.all_neighbors(graph, startnode))
    neighbor_1_id = neighbors[0]
    neighbor_2_id = neighbors[1]
    graph.remove_node(startnode) #includes both edges?!
    set_of_replaced_nodes.add(startnode)
    graph.add_edge(replacement_node_id, neighbor_1_id, len=4)
    graph.add_edge(replacement_node_id, neighbor_2_id, len=4)
    neighbors_of_replacement = list(nx.all_neighbors(graph, replacement_node_id))

    while graph.nodes[neighbors_of_replacement[0]]['status'] != 'keeper' or  graph.nodes[neighbors_of_replacement[1]]['status'] != 'keeper':
        for nbr in neighbors_of_replacement:
            if graph.nodes[nbr]['status'] == 'keeper':
                continue
            graph.nodes[replacement_node_id]['replacing'].append(nbr)
            nbnb = get_over_next_neighbor(graph, replacement_node_id, nbr)
            graph.add_edge(replacement_node_id, nbnb, len=4)
            graph.remove_node(nbr) #including both edges
            set_of_replaced_nodes.add(nbr)
            neighbors_of_replacement = list(nx.all_neighbors(graph, replacement_node_id))
            if len(neighbors_of_replacement) == 1:
                # this is a cycle
                # TODO remove this temporary workaround, no cycles should be present
                logging.info("handled string contains a cycle, replacing abandoned. Replacement ID: " + str(replacement_id) + " startnode: " + str(startnode))
                return set_of_replaced_nodes
    return set_of_replaced_nodes


def get_over_next_neighbor(graph, node_1, node_2):
    neighbors_of_2 = nx.all_neighbors(graph, node_2)
    for nb in neighbors_of_2:
        if nb == node_1:
            continue
        return nb


def check_for_triangles(graph):
    set_of_triangles = set()
    for edge in graph.edges():
        set_0 = set(nx.all_neighbors(graph, edge[0]))
        set_1 = set(nx.all_neighbors(graph, edge[1]))
        common_nodes = set_0.intersection(set_1)
        if len(common_nodes) > 0:
            for cn in common_nodes:
                ecken = [edge[0], edge[1], cn]
                graph.nodes[edge[0]]['status'] = 'keeper' #TODO: remove temporary marking vertices of triangles as "keepers"
                graph.nodes[edge[1]]['status'] = 'keeper' #TODO dito
                graph.nodes[cn]['status'] = 'keeper' #TODO dito
                ecken.sort()
                triangle = tuple(ecken)
                set_of_triangles.add(triangle)
    logging.info(str(len(set_of_triangles)) + " triangles detected")
    return set_of_triangles

def dataframe_triangles(set_of_triangles, edges, graph):
    ls_node_11 = []
    ls_node_12 = []
    ls_node_13 = []
    ls_edge_11 = []
    ls_edge_12 = []
    ls_node_21 = []
    ls_node_22 = []
    ls_node_23 = []
    ls_edge_21 = []
    ls_edge_22 = []
    ls_node_31 = []
    ls_node_32 = []
    ls_node_33 = []
    ls_edge_31 = []
    ls_edge_32 = []
    for triangle in set_of_triangles:
        ls_node_11.append(triangle[0])
        ls_node_12.append(graph.nodes[triangle[0]]['ifc_name'])
        ls_node_13.append(graph.nodes[triangle[0]]['ifc_system'])
        ls_node_21.append(triangle[1])
        ls_node_22.append(graph.nodes[triangle[1]]['ifc_name'])
        ls_node_23.append(graph.nodes[triangle[1]]['ifc_system'])
        ls_node_31.append(triangle[2])
        ls_node_32.append(graph.nodes[triangle[2]]['ifc_name'])
        ls_node_33.append(graph.nodes[triangle[2]]['ifc_system'])
        try:
            edge_1 = graph[triangle[0]][triangle[1]]
        except:
            edge_1 = graph[triangle[1]][triangle[0]]
        try:
            edge_2 = graph[triangle[1]][triangle[2]]
        except:
            edge_2 = graph[triangle[2]][triangle[1]]
        try:
            edge_3 = graph[triangle[2]][triangle[0]]
        except:
            edge_3 = graph[triangle[0]][triangle[2]]
        ls_edge_11.append(edge_1[0]['guid'])
        ls_edge_12.append(edge_1[0]['name'])
        ls_edge_21.append(edge_2[0]['guid'])
        ls_edge_22.append(edge_2[0]['name'])
        ls_edge_31.append(edge_3[0]['guid'])
        ls_edge_32.append(edge_3[0]['name'])

    df = pd.DataFrame(      {
         "node_1 guid": ls_node_11,
         "node_2 guid": ls_node_21,
         "node_3 guid": ls_node_31,
         "node_1 name": ls_node_12,
         "node_2 name": ls_node_22,
         "node_3 name": ls_node_32,
         "node_1 system": ls_node_13,
         "node_2 system": ls_node_23,
         "node_3 system": ls_node_33,
         "edge 1 guid": ls_edge_11,
         "edge 2 guid": ls_edge_21,
         "edge 3 guid": ls_edge_31,
         "edge 1 name": ls_edge_12,
         "edge 2 name": ls_edge_22,
         "edge 3 name": ls_edge_32,
         }    )
    return df

def dataframe_manifolds(manifolds, graph):
    ls_node_11 = []
    ls_node_12 = []
    ls_node_13 = []
    ls_node_14 = []
    ls_edge_11 = []
    ls_edge_12 = []
    ls_node_21 = []
    ls_node_22 = []
    ls_node_23 = []
    ls_node_24 = []
    for manifold in manifolds:
        ns = nx.all_neighbors(graph, manifold)
        for nb in ns:
            ls_node_11.append(manifold)
            ls_node_12.append(graph.nodes[manifold]['ifc_name'])
            ls_node_13.append(graph.nodes[manifold]['ifc_system'])
            ls_node_14.append(graph.nodes[manifold]['ifc_predefined_type'])
            ls_node_21.append(nb)
            ls_node_22.append(graph.nodes[nb]['ifc_name'])
            ls_node_23.append(graph.nodes[nb]['ifc_system'])
            ls_node_24.append(graph.nodes[nb]['ifc_predefined_type'])
            try:
                edge_1 = graph[manifold][nb]
            except:
                edge_1 = graph[nb][manifold]
            ls_edge_11.append(edge_1[0]['guid'])
            ls_edge_12.append(edge_1[0]['name'])

    df = pd.DataFrame(      {
         "node_1 guid": ls_node_11,
         "node_2 guid": ls_node_21,
         "node_1 name": ls_node_12,
         "node_2 name": ls_node_22,
         "node_1 system": ls_node_13,
         "node_2 system": ls_node_23,
         "node_1 type": ls_node_14,
         "node_2 type": ls_node_24,
         "edge 1 guid": ls_edge_11,
         "edge 1 name": ls_edge_12,
         }    )
    return df


def print_triangles(set_of_triangles, graph):
    triangle_count = 0
    for triangle in set_of_triangles:
        triangle_count += 1
        try:
            edge_1 = graph[triangle[0]][triangle[1]]
        except:
            edge_1 = graph[triangle[1]][triangle[0]]
        try:
            edge_2 = graph[triangle[1]][triangle[2]]
        except:
            edge_2 = graph[triangle[2]][triangle[1]]
        try:
            edge_3 = graph[triangle[2]][triangle[0]]
        except:
            edge_3 = graph[triangle[0]][triangle[2]]
        edges = [edge_1, edge_2, edge_3]
        for edge in edges:
            if edge[0]['name'] == "original":
                continue
            logging.info("Dreieck " + str(triangle_count) + " hat Kante " + str(edge[0]['guid']) + " aus Quelle " + str(edge[0]['name']))
        return edges



def generate_cluster_no_as_attribute(graph):
    """
    :param graph:
    :return: graph with additional attribute cluster_no
    """
    subgraph_list = [graph.subgraph(c) for c in nx.connected_components(graph)]
    cluster_no = 0
    for subgraph in subgraph_list:
        for node in subgraph:
            graph.nodes[node]["cluster_no"] = cluster_no
        cluster_no += 1
    return graph


def copy_cluster_no_as_attribute(source_graph, target_graph):
    for node in target_graph:
        target_graph.nodes[node]["cluster_no"] = source_graph.nodes[node]["cluster_no"]
    return target_graph


def graph_characteristics_as_string(graph):
    string = (str(graph.number_of_nodes()) + " nodes (" +
              str(count_leafs(graph)) + " leafs, " +
              str(count_loners(graph)) + " loners) " +
              str(graph.number_of_edges()) + " edges, " +
              str(nx.number_connected_components(graph.to_undirected())) + " subgraphs")
    return string

def count_leafs(graph):
    leafs_set = set()
    simple_graph = nx.Graph(graph)
    for node_id in simple_graph:
        ns = nx.all_neighbors(simple_graph, node_id)  # also predecessors (in a directed graph)
        if len(list(ns)) == 1:  # count also nodes without any edge
            leafs_set.add(node_id)
    return len(leafs_set)


def count_loners(graph):
    loners_set = set()
    simple_graph = nx.Graph(graph)
    for node_id in graph:
        ns = nx.all_neighbors(simple_graph, node_id)  # also predecessors (in a directed graph)
        if len(list(ns)) == 0:  # count also nodes without any edge
            loners_set.add(node_id)
    return len(loners_set)


def get_extent_of_layout(layout):
    x_min = 0
    x_max = 0
    y_min = 0
    y_max = 0
    for node in layout:
        x = layout[node][0]
        y = layout[node][1]
        if x < x_min: x_min = x
        if x > x_max: x_max = x
        if y > y_max: y_max = y
        if y < y_min: y_min = y
    return x_min, x_max, y_min, y_max

def node_is_deletion_candidate(node): #node is of type dict
    try:
        if (node["ifc_predefined_type"] == "IfcPipeFittingType_BEND"
                or node["ifc_predefined_type"] == "IfcPipeFittingType_CONNECTOR"
                or node["ifc_predefined_type"] == "IfcPipeFittingType_TRANSITION"
                or node["ifc_predefined_type"] == "IfcPipeSegmentType_RIGIDSEGMENT"
                or node["ifc_predefined_type"] == "IfcFilterType_WATERFILTER"
                or node["ifc_predefined_type"] == "IfcValveType_ISOLATING"
                or node["ifc_predefined_type"] == "IfcValveType_PRESSUREREDUCING"
        ):
            return True
        else:
            return False
    except:
        return False


def get_leafs_and_loners(graph):
    ll_graph = nx.MultiDiGraph()
    ll_set = set()
    leafs_set = set()
    loners_set = set()
    for node_id in graph:
        ns = nx.all_neighbors(graph, node_id)  # also predecessors (in a directed graph)
        neighbor_count = len(list(ns))
        #print(neighbor_count)
        if neighbor_count == 0: #count also nodes without any edge
            try:
                ll_graph.add_node(node_id, ifc_predefined_type=graph.nodes[node_id]["ifc_predefined_type"],ifc_name=graph.nodes[node_id]["ifc_name"],ifc_system=graph.nodes[node_id]["ifc_system"], elementstatus=graph.nodes[node_id]["elementstatus"],
                                  ifc_class=graph.nodes[node_id]["ifc_class"],container=graph.nodes[node_id]["container"], status="loner")
            except:
                ll_graph.add_node(node_id, ifc_predefined_type=graph.nodes[node_id]["ifc_predefined_type"],ifc_name=graph.nodes[node_id]["ifc_name"],ifc_system=graph.nodes[node_id]["ifc_system"],
                                  ifc_class=graph.nodes[node_id]["ifc_class"],container=graph.nodes[node_id]["container"], status="loner")
            loners_set.add(node_id)
            ll_set.add(node_id)
        if neighbor_count == 1:
            try:
                ll_graph.add_node(node_id, ifc_predefined_type=graph.nodes[node_id]["ifc_predefined_type"],ifc_name=graph.nodes[node_id]["ifc_name"],ifc_system=graph.nodes[node_id]["ifc_system"], elementstatus=graph.nodes[node_id]["elementstatus"],
                                  ifc_class=graph.nodes[node_id]["ifc_class"],container=graph.nodes[node_id]["container"], status="leaf")
            except:
                ll_graph.add_node(node_id, ifc_predefined_type=graph.nodes[node_id]["ifc_predefined_type"],ifc_name=graph.nodes[node_id]["ifc_name"],ifc_system=graph.nodes[node_id]["ifc_system"],
                                  ifc_class=graph.nodes[node_id]["ifc_class"],container=graph.nodes[node_id]["container"], status="leaf")
            leafs_set.add(node_id)
            ll_set.add(node_id)
    return ll_graph, ll_set, leafs_set, loners_set


def get_replacement_nodes(graph):
    """
    :param graph:
    :return: set of node ids that replace other nodes
    """
    replacement_set = set ()
    for node in graph.nodes:
        if 'replacing' in graph.nodes[node]:
           replacement_set.add(node)
    return replacement_set

def get_persistant_nodes(graph):
    """
    :param graph:
    :return: set of node ids that don't replace other nodes
    """
    persistant_set = set ()
    for node in graph.nodes:
        if not 'replacing' in graph.nodes[node]:
           persistant_set.add(node)
    return persistant_set
def get_manifolds(graph):
    """
    get set of node ids with more than two neighbors
    :param graph:
    :return:
    """
    manifold_set = set()
    for node_id in graph:
        ns = nx.all_neighbors(graph, node_id)
        neighbor_count = len(list(ns))
        if neighbor_count > 2:
            manifold_set.add(node_id)
    return manifold_set

def compare_edges_of_2_graphmls(file_exported, file_imported):
    exported_graph = nx.read_graphml(file_exported)
    edited_graph = nx.read_graphml(file_imported)
    kept_edges = set()
    deleted_edges = set()
    new_edges = set()
    for old_edge in exported_graph.edges():
        if edited_graph.has_edge(old_edge[0], old_edge[1]):
            kept_edges.add(old_edge)
        else:
            deleted_edges.add(old_edge)
    for new_edge in edited_graph.edges():
        if exported_graph.has_edge(new_edge[0], new_edge[1]):
            kept_edges.add(new_edge)
        else:
            new_edges.add(new_edge)
    return kept_edges, deleted_edges, new_edges

def dataframe_edges(graph):
    ls_node_1 = []
    ls_node_2 = []
    ls_name = []
    for edge in graph.edges():
        ls_node_1.append(edge[0])
        ls_node_2.append(edge[1])
        ls_name.append(graph.get_edge_data(edge[0], edge[1])[0]['name']) #TODO handle second edge between two nodes
    df = pd.DataFrame(
        {"node_1": ls_node_1,
         "node_2": ls_node_2,
         "name": ls_name
         }
    )
    return df

def dataframe_nodes(graph, ifcfile):
    elements = []
    for node in graph.nodes():
        elements.append(ifcfile.by_guid(node))
    df = dataFrame_given_IfcElement(elements)
    return df

def generate_test_graph():
    G = nx.Graph()
    G.add_edge(1, 2)
    G.add_edge(2, 3)
    G.add_edge(3, 4)
    G.add_edge(4, 5)
    G.add_edge(5, 6)
    G.add_edge(6, 7)
    G.add_edge(7, 8)
    G.add_edge(8, 9)
    G.add_edge(8, 10)
    G.add_edge(10, 11)
    G.add_edge(5, 13)
    G.add_edge(13, 14)
    G.add_edge(14, 15)
    G.add_edge(15, 16)
    G.add_edge(15, 17)
    G.add_edge(17, 18)
    G.add_edge(18, 19)
    G.add_edge(19, 20)
    G.add_edge(20, 21)
    G.add_edge(21, 8)
    G.nodes[1]['status'] = 'keeper'
    G.nodes[5]['status'] = 'keeper'
    G.nodes[6]['status'] = 'keeper'
    G.nodes[7]['status'] = 'keeper'
    G.nodes[8]['status'] = 'keeper'
    G.nodes[9]['status'] = 'keeper'
    G.nodes[10]['status'] = 'keeper'
    G.nodes[11]['status'] = 'keeper'
    G.nodes[15]['status'] = 'keeper'
    G.nodes[16]['status'] = 'keeper'
    G.nodes[2]['status'] = 'white'
    G.nodes[3]['status'] = 'white'
    G.nodes[4]['status'] = 'white'
    G.nodes[13]['status'] = 'white'
    G.nodes[14]['status'] = 'white'
    G.nodes[17]['status'] = 'white'
    G.nodes[18]['status'] = 'white'
    G.nodes[19]['status'] = 'white'
    G.nodes[20]['status'] = 'white'
    G.nodes[21]['status'] = 'white'
    return G


def graph_by_system(original_graph, systemnames):
    subgraph_system = original_graph.copy()
    full_nodelist = list(subgraph_system.nodes())
    for node_id in full_nodelist:
        if not subgraph_system.nodes[node_id]["ifc_system"] in systemnames:
            subgraph_system.remove_node(node_id)
    return subgraph_system


def add_edges_from_manual_assignment_to_new_graph(graph_original, df_con, dict_guid_by_name):
    """
    copy original graph to a new graph, this one will be enhanced and returned
    :param graph_original:
    :param df_con: data frame containing new edges
    :param dict_guid_by_name: dict_guid_by_Name_for_IfcElements
    :return: graph with more edges
    """
    graph_new = graph_original.copy()
    edge_len = 1
    for index, row in df_con.iterrows():
        name_1 = row['Name']
        name_2 = row['Name Partner']
        guid_1 = dict_guid_by_name[name_1]
        guid_2 = dict_guid_by_name[name_2]
        if (guid_1 in graph_new.nodes and guid_2 in graph_new.nodes):  # only add edges if both nodes are present in graph already
            graph_new.add_edge(guid_1, guid_2, len=edge_len)  # TODO add name for this edge
    return graph_new


def generate_layout_and_colour(graph):
    """
    generates layout for graph
    adds cluster no as attribute to graph
    :return: layout, graph_with_cluster_no
    """
    layout = nx.nx_agraph.graphviz_layout(graph)
    graph_with_cluster_no = generate_cluster_no_as_attribute(graph.to_undirected())  # cluster number is for nodes only, therefore it is sufficient to generate it for a graph that contains all nodes (But not all edges yet)
    return layout, graph_with_cluster_no


def generate_layout(graph):
    # TODO try different layouts of graph
    layout = nx.nx_agraph.graphviz_layout(graph)
    # get geometrical spread of the layout
    x_min = 0
    x_max = 0
    y_min = 0
    y_max = 0
    for node in layout:
        x = layout[node][0]
        y = layout[node][1]
        if x < x_min: x_min = x
        if x > x_max: x_max = x
        if y > y_max: y_max = y
        if y < y_min: y_min = y
    return layout, x_min, x_max, y_min, y_max
