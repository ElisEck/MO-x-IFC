"""
functions to help with geometry
- get coordinates from files
- calculate distances
- rotate
- ...
"""

import ifcopenshell
import math
import pandas as pd
import numpy as np
from shapely.geometry import Polygon
import logging

def rotate_local_coordinates_in_global_coordinates(vector_loc, x_loc, z_loc):
    y_loc = tuple(np.cross(x_loc, z_loc)*-1)
    A = (x_loc, y_loc, z_loc) #Abbildungsmatrix
    vector_glob = tuple(np.dot(A, vector_loc))
    return  np.array(vector_glob).tolist()


def rotate_global_coordinates_in_local_coordinates(vector_glob, x_loc, z_loc):
    origin_loc = (0.0, 0.0, 0.0)
    y_loc = tuple(np.cross(x_loc, z_loc)*1)
    A = (x_loc, y_loc, z_loc) #Abbildungsmatrix
    A_inv = np.matrix(A).I #inverse
    versch = np.array(vector_glob)+np.array(origin_loc)
    vector_loc = np.dot(A_inv,versch)
    return np.array(tuple([vector_loc[0, 0], vector_loc[0, 1], vector_loc[0, 2]])).tolist()


def get_coordinates(ifcfile, element_guids):
    """
    get of Placement of IfcElements (x,y,z)
    :param ifcfile:
    :param element_guids:
    :return: dict
    """
    dict_leafs_coordinates = {}
    for element_guid in element_guids:
        element = ifcfile.by_guid(element_guid)
        dict_leafs_coordinates[element_guid] = get_coordinate(element)
    return dict_leafs_coordinates


def get_coordinate(element):
    """
    get x,y,z-Array of Placement of an IfcElement
    :param element:
    :return:
    """
    placement = ifcopenshell.util.placement.get_local_placement(element.ObjectPlacement)
    coord = np.array([placement[0, 3], placement[1, 3], placement[2, 3]])
    return coord

def get_2D_coordinates_of_element(object):
    """
    looks for properties named "x coordinate" and "y coordinate" and returns their value
    :param object: IfcOpenShell-Objekt
    :return: coordinates or None
    """
    xcoord = None
    ycoord = None
    for pset in object.IsDefinedBy:
        psvs = pset.RelatingPropertyDefinition.HasProperties
        for psv in psvs:
            if psv.Name == "x coordinate":
                xcoord = psv.NominalValue.wrappedValue  # IfcReal(-4.5)
            if psv.Name == "y coordinate":
                ycoord = psv.NominalValue.wrappedValue  # IfcReal(-4.5)
    return xcoord, ycoord

def calculate_distances(dict_leafs_coordinates):
    dict_leafs_id = {}
    i=0
    distance_matrix = np.empty((len(dict_leafs_coordinates),len(dict_leafs_coordinates)))
    for leaf_guid_from in dict_leafs_coordinates:
        dict_leafs_id[i] = leaf_guid_from
        j=0
        for leaf_guid_to in dict_leafs_coordinates: #TODO eleganter machen, nur Dreicksmatrix berechnen
            #distance_matrix[i,j] = np.sqrt(np.sum(np.square(dict_leafs_coordinates[leaf_guid_from] - dict_leafs_coordinates[leaf_guid_to])))
            distance_matrix[i,j] = math.dist(dict_leafs_coordinates[leaf_guid_from],dict_leafs_coordinates[leaf_guid_to])
            if i==j:
                    distance_matrix[i,j] = None
            #print(str(dict_leafs_coordinates[leaf_guid_from]) + " zu " + str(dict_leafs_coordinates[leaf_guid_to]) + " Abstand:" + str(distance_matrix[i,j]))
            j=j+1
        i = i+1
    return distance_matrix, dict_leafs_id


def find_nearest_partner(ifcfile, dict_leafs_id, distance_matrix, dict_leafs_coordinates):
    dist_min = list()
    leaf_guid = list()
    leaf_name = list()
    partner_guid = list()
    partner_name = list()
    coord_leaf = list()
    coord_partner = list()
    for leaf_id in dict_leafs_id:
        partner_id = np.nanargmin(distance_matrix[leaf_id,])
        dist_min.insert(leaf_id, distance_matrix[leaf_id,partner_id])
        leaf_guid.insert(leaf_id, dict_leafs_id[leaf_id])
        partner_guid.insert(leaf_id, dict_leafs_id[partner_id])
        leaf_name.insert(leaf_id, ifcfile.by_guid(dict_leafs_id[leaf_id]).Name)
        partner_name.insert(leaf_id, ifcfile.by_guid(dict_leafs_id[partner_id]).Name)
        coord_leaf.insert(leaf_id, dict_leafs_coordinates[dict_leafs_id[leaf_id]])
        coord_partner.insert(leaf_id, dict_leafs_coordinates[dict_leafs_id[partner_id]])
    df = pd.DataFrame(        {
            "partner": partner_guid,
            "leaf_name": leaf_name,
            "partner_name": partner_name,
            "leaf_coord": coord_leaf,
            "partner_coord": coord_partner,
            "dist": dist_min,
         } , index=leaf_guid) #stichprobenhaften Kontrolle: sieht gut aus
    return df

def calculate_area_of_IfcConnectionSurfaceGeometry(icsg):
    area_gross = 0
    area_inner = 0
    area_net = 0
    surface = icsg.SurfaceOnRelatingElement
    if surface.is_a('IfcCurveBoundedPlane'):
        area_gross = calculate_area_of_IfcPolyline(surface.OuterBoundary)
        if surface.InnerBoundaries != None:
            for inner in surface.InnerBoundaries:
                area_inner = area_inner + calculate_area_of_IfcPolyline(inner)
        area_net = area_gross - area_inner
    else:
        logging.info("no area calculation for " + str(surface.is_a()) + " implemented (" + str(surface) + ")")
    return area_gross, area_inner, area_net

def calculate_area_of_IfcPolyline(ipl):
    pointlist = ipl.Points
    x = []
    y = []
    for point in pointlist:
        x.append(point.Coordinates[0])
        y.append(point.Coordinates[1])
    pgon = Polygon(zip(x, y))
    return pgon.area

def find_dimensions_of_model(objects):
    """
    :param objects:
    :return: min_x, max_x, min_y, max_y
    """
    min_x = 0.0
    max_x = 0.0
    min_y = 0.0
    max_y = 0.0
    for object in objects:
        xcoord = 0.0
        ycoord = 0.0
        xcoord, ycoord = get_2D_coordinates_of_element(object)
        try:
            if xcoord < min_x: min_x=xcoord
            if xcoord > max_x: max_x=xcoord
        except:
            logging.info("no x coordinate found for: " + str(object))
        try:
            if ycoord < min_y: min_y=ycoord
            if ycoord > max_y: max_y=ycoord
        except:
            logging.info("no y coordinate found for: " + str(object))
    return min_x, max_x, min_y, max_y



