"""
functions to help with plotting graphs
"""
import datetime
import logging
import os
import socket

import matplotlib
from networkx import compose
from networkx import draw_networkx_labels
import networkx as nx
import matplotlib.pyplot as plt
from PythonLib.IFC.HelpersGraph import get_leafs_and_loners, graph_characteristics_as_string, graph_by_system


def logging_config(output_logs_path, scriptname):
    timestamp = datetime.datetime.now().strftime('%Y-%m-%d %H%M%S')
    logging.basicConfig(filename=output_logs_path + timestamp + '_' + scriptname + '.log', filemode='w', encoding='utf-8', level=logging.INFO, format='%(asctime)s %(message)s', datefmt='%Y-%m-%d %H:%M:%S')
    streamhandler = logging.StreamHandler()
    streamhandler.setFormatter(logging.Formatter('%(asctime)s %(message)s', "%Y-%m-%d %H:%M:%S"))
    logging.getLogger().addHandler(streamhandler)
    logging.info("executed script " + scriptname)
    logging.info("executed on machine " + socket.gethostname())
    logging.info("working directory: " + os.getcwd())

def get_fig_axes_for_A4q(id):
    fig, axes = plt.subplots(nrows=1, ncols=1, figsize=(11, 5), dpi=300)  # A4quer 28cm breit
    fig.subplots_adjust(left=0.01, right=0.99, bottom=0.01, top=0.87, wspace=0.02)
    fig.suptitle(id, fontsize=10)
    return fig, axes

cmap = matplotlib.cm.get_cmap('tab20')
cmap2 = matplotlib.cm.get_cmap('tab20c')
colordict_system = {
    'KK_Rueckkuehlung':     cmap(0),
    'KK_Rueckkuehlung_VL':  cmap(0),
    'KK_Rueckkuehlung_RL':  cmap(0),
    'KK_Erzeugung':         cmap(2),
    'KK_Erzeugung_VL':      cmap(2),
    'KK_Erzeugung_RL':      cmap(2),
    'KK4_RLT': cmap(4),
    'KK4_RLT_VL': cmap(4),
    'KK4_RLT_RL': cmap(4),
    'KK2_ULK': cmap(6),
    'KK2_ULK_VL': cmap(6),
    'KK2_ULK_RL': cmap(6),
    'KK3_Server': cmap(8),
    'KK3_Server_VL': cmap(8),
    'KK3_Server_RL': cmap(8),
    'KK1_BKA': cmap(10),
    'KK1_BKA_VL': cmap(10),
    'KK1_BKA_RL': cmap(10),
    'Split': cmap(12),
    'Split_VL': cmap(12),
    'Split_RL': cmap(12),
    'Zuluft': cmap(14), #leading underscore had to be removed to be shown in the matplotlib legend
    'Kondensat': cmap(16), #leading underscore had to be removed to be shown in the matplotlib legend
}
colordict_type = {
    'IfcPipeSegmentType_RIGIDSEGMENT': cmap(6),
    'IfcPipeSegmentType_USERDEFINED': cmap(7),
    'IfcPipeFittingType_BEND':     cmap2(0),
    'IfcPipeFittingType_CONNECTOR':     cmap2(1),
    'IfcPipeFittingType_TRANSITION':     cmap2(2),
    'IfcPipeFittingType_JUNCTION':     cmap2(3),
    'IfcFlowInstrumentType_PRESSUREGAUGE':     cmap(8),
    'IfcFlowInstrumentType_THERMOMETER':     cmap(9),
    'IfcSensorType_PRESSURESENSOR':     cmap(10),
    'IfcValveType_PRESSUREREDUCING':     cmap2(4),
    'IfcValveType_ISOLATING':     cmap2(5),
    'IfcValveType_CHECK':     cmap2(4),
    'IfcValveType_MIXING':     cmap2(5),
    'IfcValveType_REGULATING':     cmap2(7),
    'IfcFilterType_WATERFILTER':     cmap(10),
    'IfcPumpType_CIRCULATOR':     cmap(12),
    'IfcChillerType_WATERCOOLED':     cmap(13),
    'IfcTankType_PRESSUREVESSEL':     cmap(14),
    'IfcTankType_EXPANSION':     cmap(15),
    'IfcDistributionElementType_':     cmap(16),
    'IfcCoilType_DXCOOLINGCOIL':     cmap(17),
    'IfcUnitaryEquipmentType_AIRHANDLER':     cmap(18),
    'IfcUnitaryEquipmentType_SPLITSYSTEM':     cmap(19),
    'IfcUnitaryEquipmentType_USERDEFINED':     cmap(8),
    'IfcHeatExchangerType_PLATE':     cmap2(8),
    'IfcUnitaryEquipmentType_AIRCONDITIONINGUNIT':     cmap2(9),
    'IfcCoilType_HYDRONICCOIL':     cmap2(10),
}

def add_plot_label_to_graph(graph_large, graph_reduced):
    """
    generate plot label in large graph (ifc_name) for all nodes present also in smaller graph
    :param graph_large: larger graph
    :param graph_reduced: smaller graph
    :return: nothing, graph_large is instantly changed
    """
    for node in graph_large:
        if node in graph_reduced:
            graph_large.nodes[node]['label_plot'] = graph_large.nodes[node]['ifc_name']
        else:
            graph_large.nodes[node]['label_plot'] = ""
def get_range_xy(layout):
    min_x = 0
    max_x = 0
    min_y = 0
    max_y = 0
    for node_id in layout:
        x = layout[node_id][0]
        y = layout[node_id][1]
        if x < min_x: min_x = x
        if x > max_x: max_x = x
        if y < min_y: min_y = y
        if y > max_y: max_y = y
    delta_x = max_x - min_x
    delta_y = max_y - min_y
    return delta_x, delta_y, min_x, min_y


def get_range_z(dict_coordinates_3D):
    min_z = 0
    max_z = 0
    for node_id in dict_coordinates_3D:
        x = dict_coordinates_3D[node_id][2]
        if x < min_z: min_z = x
        if x > max_z: max_z = x
    delta_z = max_z - min_z
    return delta_z, min_z


def set_to_colordict(some_set):
    """
    returns a dictionary that assigns an integer (0...19) to every item in the set, if item is already an integer it remains unchanged if it is smaller than 20
    :param some_set:
    :return:
    """
    colordict = {}
    i=0
    for type in some_set:
        if isinstance(type, int):
            colordict[type] = type % 20
        else:
            colordict[type] = i % 20
            i = i+1 #TODO +1 wäre ausreichend und sinnvoller um den Wertebereich voll auszuschöpfen
    return colordict

def set_to_colordict_rgb(some_set):

    return colordict

def arbitrary_colordict(graph, color_attribute = "ifc_predefined_type"):
    """
    returns a dictionary that assigns an integer (0...19) to every item in the set, if item is already an integer it remains unchanged if it is smaller than 20
    :param some_set:
    :return:
    """
    color_attribute_set = set()
    for node_id in graph:
        node = graph.nodes[node_id]
        try:
            node[color_attribute]
        except:
            node[color_attribute] = 'attribute_unset'
        color_attribute_set.add(node[color_attribute])

    cmap = matplotlib.cm.get_cmap('tab20')
    colordict = {}
    i = 0
    for type in color_attribute_set:
        if isinstance(type, int):
            colordict[type] = cmap(type % 20)
        else:
            colordict[type] = cmap(i % 20)
            i = i + 1  # +1 ist ausreichend und sinnvoll um den Wertebereich voll auszuschöpfen
    return colordict

def fixed_colordict():
    colordict = {
    'IfcPipeSegmentType_RIGIDSEGMENT': (0.8588235294117647, 0.8588235294117647, 0.5529411764705883, 1.0),
    'IfcPipeFittingType_CONNECTOR': (0.6823529411764706, 0.7803921568627451, 0.9098039215686274, 1.0),
    'IfcPipeFittingType_BEND': (1.0, 0.7333333333333333, 0.47058823529411764, 1.0),
    'IfcPipeFittingType_TRANSITION': (0.596078431372549, 0.8745098039215686, 0.5411764705882353, 1.0),
    'IfcValveType_ISOLATING': (1.0, 0.596078431372549, 0.5882352941176471, 1.0),
    'IfcValveType_PRESSUREREDUCING': (0.7803921568627451, 0.7803921568627451, 0.7803921568627451, 1.0),
    'IfcValveType_MIXING': (0.7803921568627451, 0.7803921568627451, 0.7803921568627451, 1.0), #doppelt
    'IfcFilterType_WATERFILTER': (0.7686274509803922, 0.611764705882353, 0.5803921568627451, 1.0),
    'IfcValveType_REGULATING': (0.12156862745098039, 0.4666666666666667, 0.7058823529411765, 1.0),
    'IfcUnitaryEquipmentType_USERDEFINED': (1.0, 0.4980392156862745, 0.054901960784313725, 1.0),
    'IfcUnitaryEquipmentType_AIRHANDLER': (1.0, 0.4980392156862745, 0.054901960784313725, 1.0), #doppelt
    'IfcUnitaryEquipmentType_AIRCONDITIONINGUNIT': (1.0, 0.4980392156862745, 0.054901960784313725, 1.0), #doppelt
    'IfcFlowInstrumentType_PRESSUREGAUGE': (0.17254901960784313, 0.6274509803921569, 0.17254901960784313, 1.0),
    'IfcDistributionElementType_': (0.17254901960784313, 0.6274509803921569, 0.17254901960784313, 1.0), #doppelt - was ist das?
    'IfcSensorType_PRESSURESENSOR': (0.17254901960784313, 0.6274509803921569, 0.17254901960784313, 1.0), #doppelt - was ist das?
    'IfcHeatExchangerType_PLATE': (0.7725490196078432, 0.6901960784313725, 0.8352941176470589, 1.0),
    'IfcFlowInstrumentType_THERMOMETER': (0.5803921568627451, 0.403921568627451, 0.7411764705882353, 1.0),
    'IfcValveType_CHECK': (0.9686274509803922, 0.7137254901960784, 0.8235294117647058, 1.0),
    'IfcPumpType_CIRCULATOR': (0.5490196078431373, 0.33725490196078434, 0.29411764705882354, 1.0),
    'IfcPipeFittingType_JUNCTION': (0.8901960784313725, 0.4666666666666667, 0.7607843137254902, 1.0),
    'IfcTankType_PRESSUREVESSEL': (0.4980392156862745, 0.4980392156862745, 0.4980392156862745, 1.0),
    'IfcTankType_EXPANSION': (0.7372549019607844, 0.7411764705882353, 0.13333333333333333, 1.0),
    'IfcChillerType_WATERCOOLED': (0.09019607843137255, 0.7450980392156863, 0.8117647058823529, 1.0),
    'IfcCoilType_HYDRONICCOIL': (0.09019607843137255, 0.7450980392156863, 0.8117647058823529, 1.0), #doppelt
    'IfcPipeSegmentType_USERDEFINED': (0.8392156862745098, 0.15294117647058825, 0.1568627450980392, 1.0) #red for replacements
    }
    return colordict

def plot_customized(graph, positions, colordict, axes, color_attribute="ifc_predefined_type", label_attribute="ifc_name", node_size=5, font_size=3):
    """
    plots all nodes coloured by specified attribute and with a given label
    :param graph:
    :param positions:
    :param colordict:
    :param color_attribute:
    :param label_attribute: use an unused identifier to omit labels
    :return:
    """
    """
    color_attribute_set = set()
    for node_id in graph:
        node = graph.nodes[node_id]
        try:
            node[color_attribute]
        except:
            node[color_attribute] = 'attribute_unset'

        color_attribute_set.add(node[color_attribute])
    colordict = set_to_colordict_rgb(color_attribute_set)
    """
    colors = [u[1] for u in graph.nodes(data=color_attribute)]
    colors2 = [colordict[v] for v in colors]
    if label_attribute == "-":
        nx.draw_networkx(graph, pos=positions, node_color=colors2, with_labels=False, node_size=node_size, width=0.1, arrows=False, ax=axes, font_size=font_size)
    else:
        nx.draw_networkx(graph, pos=positions, node_color=colors2, labels=nx.get_node_attributes(graph, label_attribute), node_size=node_size, width=0.1, arrows=False, ax=axes, font_size=font_size)


def add_legend(axes, colordict, fontsize=3, markersize=4, location = 'upper left'):
    from matplotlib.lines import Line2D
    legend_elements = list()
    #create dummy geometry to be able to plot legend
    for item in colordict:
        legend_elements.append(Line2D([0], [0], marker='o', color=colordict[item], label=item, markerfacecolor=colordict[item], markersize=markersize))
    axes.legend(handles=legend_elements, loc=location, fontsize=fontsize) #innen
    #axes.legend(handles=legend_elements, bbox_to_anchor=(-0.1,0.5), fontsize=fontsize)


def plot_cluster_allocation(graph, positions):
    """
    plots all nodes coloured by cluster, and with cluster_no as node label
    :param graph:
    :param positions:
    :return:
    """
    subgraph_list = [graph.subgraph(c) for c in nx.connected_components(graph)]
    cluster_no = 0
    for subgraph in subgraph_list:
        for node in subgraph:
            graph.nodes[node]["cluster"] = cluster_no
            subgraph.nodes[node]["cluster"] = cluster_no
        cluster_no += 1
        color = cluster_no % 20
        colorlist = [color]*nx.number_of_nodes(subgraph)
        nx.draw_networkx(subgraph, pos=positions, node_color=colorlist, vmin=0, vmax=19, cmap='tab20',  labels=nx.get_node_attributes(subgraph, "cluster"), node_size=40)


def figure_graph_reduction(name, graph, graph_reduced, pos_spring, pos_spring_reduced, no_of_subgraphs):
    fig, axes = plt.subplots(nrows=2, ncols=3, figsize =(11.5, 4.8), dpi=300)
    fig.suptitle("graph representations of EAS Neubau Kälte " + name + " (label and colour show cluster number) " + str(no_of_subgraphs) + " clusters/subgraphs/components", fontsize=10)
    axes[0,0].set_title("Original graph: " + str(graph.number_of_nodes()) + " nodes, " + str(graph.number_of_edges()) + " edges", fontsize=6)
    axes[0,1].set_title("Reduced graph: " + str(graph_reduced.number_of_nodes()) + " nodes, " + str(graph_reduced.number_of_edges()) + " edges", fontsize=6)
    axes[0,2].set_title("Reduced graph with new layout", fontsize=6)
    axes[1,0].set_title("coloured by IfcPredefinedType", fontsize=6)
    plot_customized(graph, pos_spring, axes=axes[0,0], color_attribute="cluster_no", label_attribute="cluster_no")
    plot_customized(graph_reduced, pos_spring, axes=axes[0,1], color_attribute="cluster_no", label_attribute="cluster_no")
    plot_customized(graph_reduced, pos_spring_reduced, axes=axes[0,2], color_attribute="cluster_no", label_attribute="cluster_no")
    plot_customized(graph, pos_spring, axes=axes[1,0], color_attribute="ifc_predefined_type", label_attribute="x")
    plot_customized(graph_reduced, pos_spring, axes=axes[1,1], color_attribute="ifc_predefined_type", label_attribute="x")
    plot_customized(graph_reduced, pos_spring_reduced, axes=axes[1,2], color_attribute="ifc_predefined_type", label_attribute="")
    #plt.close()
    fig.savefig("Elisabeth/Netzwerk/output_logs/" + name + "_reduction.png")


def figures_systemwise_compare_A_S(graph_4, graph_2, id, colordict):
    dict_systems = {
        'Server': ['KK3_Server_VL', 'KK3_Server_RL', 'KK3_Server'],
        'ULK': ['KK2_ULK_VL', 'KK2_ULK_RL', 'KK2_ULK'],
        'BKA': ['KK1_BKA_VL', 'KK1_BKA_RL', 'KK1_BKA'],
        'RLT': ['KK4_RLT_VL', 'KK4_RLT_RL', 'KK4_RLT'],
        'Erzeugung': ['KK_Erzeugung_VL', 'KK_Erzeugung_RL', 'KK_Erzeugung'],
        'Rueckkuehlung': ['KK_Rueckkuehlung_VL', 'KK_Rueckkuehlung_RL', 'KK_Rueckkuehlung'],
    }

    for system in dict_systems:
        #create subgraphs by system
        graph_2_sys = graph_by_system(graph_2, dict_systems[system])
        graph_4_sys = graph_by_system(graph_4, dict_systems[system])
        graph_3_sys = compose(nx.Graph(graph_4_sys), nx.Graph(graph_2_sys))
        layout_sys_3 = nx.nx_agraph.graphviz_layout(graph_3_sys)
        layout_sys_4 = nx.nx_agraph.graphviz_layout(graph_4_sys)

        for node in graph_2_sys:
            if node in graph_4_sys:
                graph_2_sys.nodes[node]['label_plot'] = graph_2_sys.nodes[node]['ifc_name']
            else:
                graph_2_sys.nodes[node]['label_plot'] = ""


        fig, axes = plt.subplots(nrows=1, ncols=4, figsize=(11, 5), dpi=300)
        fig.subplots_adjust(left=0.01, right=0.99, bottom=0.01, top=0.87, wspace = 0.02)
        fig.suptitle("System: " + system, fontsize=10)

        subfigure_graph_plotlabel_type(axes[0], graph_2_sys, layout_sys_3, "original nodes and edges\n", colordict)
        subfigure_graph_plotlabel_type(axes[1], graph_3_sys, layout_sys_3, "original nodes and edges with replacements\n", colordict)
        subfigure_graph_plotlabel_type(axes[2], graph_4_sys, layout_sys_3, "reduced graph\n", colordict)
        subfigure_graph_plotlabel_type(axes[3], graph_4_sys, layout_sys_4, "other layout (self-optimized)\n", colordict)
        fig.savefig("C:/_DATEN/WORKSPACES/PyCharm/ifcscripting/_Projekte/EAS_KLT/output_logs/" + id + "_" + system + "_compare.png")

        fig, axes = plt.subplots(nrows=1, ncols=1, figsize=(11, 5), dpi=300)
        fig.subplots_adjust(left=0.01, right=0.99, bottom=0.01, top=0.87, wspace = 0.02)
        fig.suptitle("System: " + system, fontsize=10)
        subfigure_graph_plotlabel_type(axes, graph_4_sys, layout_sys_4, "other layout (self-optimized)\n", colordict)
        fig.savefig("C:/_DATEN/WORKSPACES/PyCharm/ifcscripting/_Projekte/EAS_KLT/output_logs/" + id + "_" + system + "_final.png")

        plt.close(fig="all")


def figures_systemwise(graph, id, colordict, filename_stub="C:/_DATEN/WORKSPACES/PyCharm/ifcscripting/_Projekte/EAS_KLT/output/" + str(id) + "_"):
    dict_systems = {
        'Server': ['KK3_Server_VL', 'KK3_Server_RL', 'KK3_Server'],
        'ULK': ['KK2_ULK_VL', 'KK2_ULK_RL', 'KK2_ULK'],
        'BKA': ['KK1_BKA_VL', 'KK1_BKA_RL', 'KK1_BKA'],
        'RLT': ['KK4_RLT_VL', 'KK4_RLT_RL', 'KK4_RLT'],
        'Erzeugung': ['KK_Erzeugung_VL', 'KK_Erzeugung_RL', 'KK_Erzeugung'],
        'Rueckkuehlung': ['KK_Rueckkuehlung_VL', 'KK_Rueckkuehlung_RL', 'KK_Rueckkuehlung'],
    }

    for system in dict_systems:
        graph_4_sys = graph_by_system(graph, dict_systems[system])
        layout_sys_4 = nx.nx_agraph.graphviz_layout(graph_4_sys)
        fig, axes = plt.subplots(nrows=1, ncols=1, figsize=(11, 5), dpi=600)
        fig.legend(loc='lower left')
        fig.subplots_adjust(left=0.01, right=0.99, bottom=0.01, top=0.87, wspace=0.02)
        fig.suptitle("System: " + system, fontsize=10)
        subfigure_graph_ifcname_type(axes, graph_4_sys, layout_sys_4, "other layout (self-optimized)\n", colordict)
        fig.savefig(filename_stub + system + "_ConstructionGraph.png")


def figure_leafs_types(name, graph_reduced, pos_spring):# TODO this is almost a copy of figure_leafs_names - make it more elegant by giving the option as parameter
    leafs_graph, leafs_set, aa, bb = get_leafs_and_loners(graph_reduced)
    fig, axes = plt.subplots(nrows=1, ncols=1, figsize=(11.5, 4.8), dpi=300)
    fig.suptitle("graph representations of EAS Neubau Kälte OG2 (labels showing names of components)", fontsize=10)
    plot_customized(graph_reduced, pos_spring, axes=axes, color_attribute="ifc_predefined_type", label_attribute="x")
    draw_networkx_labels(leafs_graph, pos_spring, labels=nx.get_node_attributes(leafs_graph, "ifc_predefined_type"), bbox=None, horizontalalignment='center', verticalalignment='center', ax=axes, clip_on=True, font_size=3)
    #plt.close()
    fig.savefig("Elisabeth/Netzwerk/output_logs/"+name+"_leafs_types.png")


def subfigure_edge_change(subplot_axes, graph, change_graph, layout, fontcolor, titletext):
    subplot_axes.set_title(titletext + graph_characteristics_as_string(graph), fontsize=6)
    plot_customized(graph, layout, axes=subplot_axes, color_attribute="cluster_no", label_attribute="x")
    nx.draw_networkx_edges(change_graph, edge_color=fontcolor, ax=subplot_axes, pos=layout, arrows=False)
    lbls1 = {}
    for node_id in change_graph.nodes(): lbls1[node_id] = change_graph.nodes()[node_id]['ifc_name']
    nx.draw_networkx_labels(change_graph, pos=layout, labels=lbls1, font_color=fontcolor, ax=subplot_axes, font_size=3)


def subfigure_graph_cluster_no(subplot_axes, graph, layout, titletext):
    """
    node label and colour by cluster no
    """
    subplot_axes.set_title(titletext + graph_characteristics_as_string(graph), fontsize=6)
    plot_customized(graph, layout, axes=subplot_axes, color_attribute="cluster_no", label_attribute="cluster_no")

def subfigure_graph_name_type(subplot_axes, graph, layout, titletext, colordict, node_size=5, font_size=3):
    """
    node label by name, colour by ifc_predefined_type,
    """
    subplot_axes.set_title(titletext + graph_characteristics_as_string(graph), fontsize=font_size+1) #, y=0.8
    plot_customized(graph, layout, colordict, axes=subplot_axes, color_attribute="ifc_predefined_type", label_attribute="ifc_name", node_size=node_size, font_size=font_size)
    #add_legend(subplot_axes, colordict)


def subfigure_graph_type(subplot_axes, graph, layout, titletext, colordict, node_size=5, font_size=3):
    """
    node colour by ifc_predefined_type, no label
    """
    subplot_axes.set_title(titletext + graph_characteristics_as_string(graph), fontsize=font_size+1)
    plot_customized(graph, layout, colordict, axes=subplot_axes, color_attribute="ifc_predefined_type", label_attribute="-", node_size=node_size, font_size=font_size)
   # add_legend(subplot_axes, colordict)


def subfigure_graph_plotlabel_type(subplot_axes, graph, layout, titletext, colordict, node_size=5, font_size=3):
    """
    node label by special "label_plot" attribute, colour by ifc_predefined_type,
    """
    subplot_axes.set_title(titletext + graph_characteristics_as_string(graph), fontsize=font_size+1)
    plot_customized(graph, layout, colordict, axes=subplot_axes, color_attribute="ifc_predefined_type", label_attribute="label_plot", node_size=node_size, font_size=font_size)
  #  add_legend(subplot_axes, colordict)

def subfigure_legend(subplot_axes, colordict, font_size=3, markersize=3):
    """
    node label by special "label_plot" attribute, colour by ifc_predefined_type,
    """
    subplot_axes.set_title("Legende", fontsize=font_size+1)
    subplot_axes.axis('off')
    add_legend(subplot_axes, colordict, fontsize=font_size, markersize=markersize, location = 'center')

def subfigure_graph_ifcname_type(subplot_axes, graph, layout, titletext, colordict):
    """
    node label is ifc_name, colour by ifc_predefined_type,
    """
    subplot_axes.set_title(titletext + graph_characteristics_as_string(graph), fontsize=6)
    plot_customized(graph, layout, colordict, axes=subplot_axes, color_attribute="ifc_predefined_type", label_attribute="ifc_name")
    add_legend(subplot_axes, colordict)

def subfigure_graph_plotlabel_system(subplot_axes, graph, layout, titletext, colordict):
    """
    node label by special "label_plot" attribute, colour by ifc_system,
    """
    subplot_axes.set_title(titletext + graph_characteristics_as_string(graph), fontsize=6)
    plot_customized(graph, layout, colordict, axes=subplot_axes, color_attribute="ifc_system", label_attribute="label_plot")
    add_legend(subplot_axes, colordict)