"""
functions to create geometric instances in an existing ifcfile
- create a visualization of the element coordinate systems
- ...
"""
import ifcopenshell
import ifcopenshell.api
import logging

OO = 0., 0., 0.

X = 1., 0., 0.
Y = 0., 1., 0.
Z = 0., 0., 1.

origin = 0., 0., 0.
dir_x = 1., 0., 0.
dir_z = 0., 0., 1.

#ifc_dir_z = ifcfile.createIfcDirection(dir_z)
#ifc_dir_x = ifcfile.createIfcDirection(dir_x)

def create_dummy_geometry(ifcfile, element, ifc_dir_x, ifc_dir_z, subcontext3D1, dim, xcoord=0.0, ycoord=0.0):
    """
    create a dummy 3D geometry for each element
    every Class has its own size, geometry and colour (types not used to distinguish)
    placement at the placement of the IfcProduct, if not given otherwise
    :param ifcfile:
    :param element:
    :param ifc_dir_x:
    :param ifc_dir_z:
    :param subcontext3D1:
    :param dim:
    :param xcoord: placement relative to local placement of the IfcProduct - usually not necessary
    :param ycoord: placement relative to local placement of the IfcProduct - usually not necessary
    :return:
    """
    colors = {
        'blue': create_colour(ifcfile, 16.0 / 255.0, 112.0 / 255.0, 176.0 / 255.0),
        'orange': create_colour(ifcfile, 240.0 / 255.0, 112.0 / 255.0, 0.0),
        'green': create_colour(ifcfile, 32.0 / 255.0, 160.0 / 255.0, 32.0 / 255.0),
        'red': create_colour(ifcfile, 208.0 / 255.0, 32.0 / 255.0, 32.0 / 255.0),
        'purple': create_colour(ifcfile, 144.0 / 255.0, 96.0 / 255.0, 176.0 / 255.0),
        'pink': create_colour(ifcfile, 224.0 / 255.0, 112.0 / 255.0, 192.0 / 255.0),
        'turq': create_colour(ifcfile, 16.0 / 255.0, 176.0 / 255.0, 192.0 / 255.0),
        'lgreen': create_colour(ifcfile, 176.0 / 255.0, 176.0 / 255.0, 32.0 / 255.0),
        'grey': create_colour(ifcfile, 112.0 / 255.0, 112.0 / 255.0, 112.0 / 255.0)}

    if element.is_a("IfcHeatExchanger"):
        isr = create_cuboid(ifcfile, ifc_dir_x, ifc_dir_z, subcontext3D1, colors['green'], length_x=dim, length_y=dim, length_z=dim, pos_x=xcoord, pos_y=ycoord, pos_z=0.0)
    elif element.is_a("IfcAirToHeatRecovery"):
        isr = create_cuboid(ifcfile, ifc_dir_x, ifc_dir_z, subcontext3D1, colors['red'], length_x=dim, length_y=dim, length_z=dim, pos_x=xcoord, pos_y=ycoord, pos_z=0.0)
    elif element.is_a("IfcCoil"):
        isr = create_cuboid(ifcfile, ifc_dir_x, ifc_dir_z, subcontext3D1, colors['purple'], length_x=dim, length_y=dim, length_z=dim, pos_x=xcoord, pos_y=ycoord, pos_z=0.0)
    elif element.is_a("IfcAirTerminal"):
        isr = create_cuboid(ifcfile, ifc_dir_x, ifc_dir_z, subcontext3D1, colors['red'], length_x=dim*0.5, length_y=dim*0.5, length_z=dim*0.5, pos_x=xcoord, pos_y=ycoord, pos_z=0.0)
    elif element.is_a("IfcAirTerminalBox"):
        isr = create_cuboid(ifcfile, ifc_dir_x, ifc_dir_z, subcontext3D1, colors['pink'], length_x=dim*0.5, length_y=dim*0.5, length_z=dim*0.5, pos_x=xcoord, pos_y=ycoord, pos_z=0.0)
    elif element.is_a("IfcPump"):
        #isr = create_sphere(ifcfile, ifc_dir_x, ifc_dir_z, subcontext3D1, radius=dim, pos_x=xcoord, pos_y=ycoord, pos_z=0.0) #spheres are not supported by KIT viewer, therefore cuboid used
        isr = create_cuboid(ifcfile, ifc_dir_x, ifc_dir_z, subcontext3D1, colors['blue'], length_x=dim*1.5, length_y=dim*1.5, length_z=dim*1.5, pos_x=xcoord, pos_y=ycoord, pos_z=0.0)
    elif element.is_a("IfcFan"):
        isr = create_cuboid(ifcfile, ifc_dir_x, ifc_dir_z, subcontext3D1, colors['turq'], length_x=dim*1.5, length_y=dim*1.5, length_z=dim*1.5, pos_x=xcoord, pos_y=ycoord, pos_z=0.0)
    elif element.is_a("IfcPipeSegment"):
       isr = create_cylinder(ifcfile, ifc_dir_x, ifc_dir_z, subcontext3D1, colors['grey'], radius=dim/5, length_z=dim*5, pos_x=xcoord, pos_y=ycoord, pos_z=0.0)
    elif element.is_a("IfcPipeFitting"):
       isr = create_cylinder(ifcfile, ifc_dir_x, ifc_dir_z, subcontext3D1, colors['grey'], radius=dim/2, length_z=dim*2, pos_x=xcoord, pos_y=ycoord, pos_z=0.0)
    elif element.is_a("IfcSensor"):
        isr = create_cylinder(ifcfile, ifc_dir_x, ifc_dir_z, subcontext3D1, colors['red'], radius=dim/2, length_z=dim*2, pos_x=xcoord, pos_y=ycoord, pos_z=0.0)
    elif element.is_a("IfcValve"):
        isr = create_cylinder(ifcfile, ifc_dir_x, ifc_dir_z, subcontext3D1, colors['purple'], radius=dim/2, length_z=dim*2, pos_x=xcoord, pos_y=ycoord, pos_z=0.0)
    elif element.is_a("IfcFlowInstrument"):
        isr = create_cylinder(ifcfile, ifc_dir_x, ifc_dir_z, subcontext3D1, colors['pink'], radius=dim/2, length_z=dim*2, pos_x=xcoord, pos_y=ycoord, pos_z=0.0)
    elif element.is_a("IfcFilter"):
        isr = create_cylinder(ifcfile, ifc_dir_x, ifc_dir_z, subcontext3D1, colors['blue'], radius=dim/2, length_z=dim*2, pos_x=xcoord, pos_y=ycoord, pos_z=0.0)
    elif element.is_a("IfcDamper"):
        isr = create_cylinder(ifcfile, ifc_dir_x, ifc_dir_z, subcontext3D1, colors['orange'], radius=dim / 2, length_z=dim * 2, pos_x=xcoord, pos_y=ycoord, pos_z=0.0)
    elif element.is_a("IfcDuctSilencer"):
        isr = create_cylinder(ifcfile, ifc_dir_x, ifc_dir_z, subcontext3D1, colors['green'], radius=dim / 2, length_z=dim * 2, pos_x=xcoord, pos_y=ycoord, pos_z=0.0)
    elif element.is_a("IfcTank"):
       isr = create_cylinder_upright(ifcfile, ifc_dir_x, ifc_dir_z, subcontext3D1, None, radius=2*dim, length_z=5*dim, pos_x=xcoord, pos_y=ycoord, pos_z=0.0)
    elif element.is_a("IfcDistributionElement"):
        isr = create_cuboid(ifcfile, ifc_dir_x, ifc_dir_z, subcontext3D1, colors['orange'], length_x=dim, length_y=dim, length_z=dim, pos_x=xcoord, pos_y=ycoord, pos_z=0.0)
    else :
       logging.info("undefined geometry for class: " + str(element.is_a()) + " of object " + str(element))
    product_shape = ifcfile.createIfcProductDefinitionShape('Geometriezuordnung', 'Erluterung zur Geometriezuordnung', [isr])
    ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=element, attributes={"Representation": product_shape}) #hier entsteht eine ownerhistory vom Typ ADDED/eine andere wird modified?!

def create_cuboid(ifcfile, ifc_dir_x, ifc_dir_z, geometric_representation_context, color, length_x=1.0, length_y=1.0, length_z=1.0, pos_x=0.0, pos_y=0.0, pos_z=0.0):
    """
    :param ifcfile: file to add the entities to
    :param ifc_dir_x: handle to dir_x (to avoid duplicate)
    :param ifc_dir_z: handle to dir_z (to avoid duplicate)
    :param geometric_representation_context:
    :param length_x (1.0): give with comma (otherwise interpreted as integer)
    :param length_y (1.0): give with comma (otherwise interpreted as integer)
    :param length_z (1.0): give with comma (otherwise interpreted as integer)
    :param pos_x (0.0):
    :param pos_y (0.0):
    :param pos_z (0.0):
    :return: IfcShapeRepresentation (IfcShapeRepresentation::RepresentationIdentifier = Body, IfcShapeRepresentation::RepresentationType = SweptSolid)
    """
    origin = 0., 0., 0.
    origin_2D = 0., 0.
    new_origin = ifcfile.createIfcCartesianPoint([pos_x, pos_y, pos_z])
    axis2placement = ifcfile.createIfcAxis2Placement3D(new_origin, ifc_dir_z, ifc_dir_x)
    #polyline = create_IfcPolyline(ifcfile, [origin, (length_x, 0.0, 0.0), (length_x, length_y, 0.0), (0.0, length_y, 0.0), origin])
    polyline = create_IfcPolyline(ifcfile, [origin_2D, (length_x, 0.0), (length_x, length_y), (0.0, length_y), origin_2D]) #TODO wieder die alter Version mit 3D-Koordinaten nehmen?
    ifcclosedprofile = ifcfile.createIfcArbitraryClosedProfileDef("AREA", None, polyline)
    solid = ifcfile.createIfcExtrudedAreaSolid(ifcclosedprofile, axis2placement, ifc_dir_z, length_z)
    if color != None:
        isi = ifcfile.createIfcStyledItem(solid, [color], None)
    return ifcfile.createIfcShapeRepresentation(geometric_representation_context, "Body", "SweptSolid", [solid])


def create_cylinder(ifcfile, ifc_dir_x, ifc_dir_z, geometric_representation_context, color, radius=1.0, length_z=1.0, pos_x=0.0, pos_y=0.0, pos_z=0.0):
    """
    liegender Zylinder, extrudiert entlang x-Achse
    :param ifcfile:
    :param ifc_dir_x:
    :param ifc_dir_z:
    :param geometric_representation_context:
    :param color:
    :param radius:
    :param length_z:
    :param pos_x:
    :param pos_y:
    :param pos_z:
    :return:
    """
    new_origin = ifcfile.createIfcCartesianPoint([pos_x, pos_y, pos_z])
    axis2placement = ifcfile.createIfcAxis2Placement3D(new_origin, ifc_dir_x, ifc_dir_z)
    circle = ifcfile.createIfcCircle(new_origin, radius)
    ifcclosedprofile = ifcfile.createIfcArbitraryClosedProfileDef("AREA", None, circle)
    solid = ifcfile.createIfcExtrudedAreaSolid(ifcclosedprofile, axis2placement, ifc_dir_z, length_z) #Extrusionsrichtung wird jetzt im Lokalen Koordinatensystem betrachtet
    if color != None:
        isi = ifcfile.createIfcStyledItem(solid, [color], None)
    return ifcfile.createIfcShapeRepresentation(geometric_representation_context, "Body", "SweptSolid", [solid])

def create_cylinder_upright(ifcfile, ifc_dir_x, ifc_dir_z, geometric_representation_context, color, radius=1.0, length_z=1.0, pos_x=0.0, pos_y=0.0, pos_z=0.0):
    """
    stehender Zylinder, extrudiert entlang z-Achse
    :param ifcfile: file to add the entities to
    :param ifc_dir_x: handle to dir_x (to avoid duplicate)
    :param ifc_dir_z: handle to dir_x (to avoid duplicate)
    :param geometric_representation_context:
    :param length_x (1.0): give with comma (otherwise interpreted as integer)
    :param length_y (1.0): give with comma (otherwise interpreted as integer)
    :param length_z (1.0): give with comma (otherwise interpreted as integer)
    :param pos_x (0.0):
    :param pos_y (0.0):
    :param pos_z (0.0):
    :return: IfcShapeRepresentation (IfcShapeRepresentation::RepresentationIdentifier = Body, IfcShapeRepresentation::RepresentationType = SweptSolid)
    """
    new_origin = ifcfile.createIfcCartesianPoint([pos_x, pos_y, pos_z])
    axis2placement = ifcfile.createIfcAxis2Placement3D(new_origin, ifc_dir_z, ifc_dir_x)
    circle = ifcfile.createIfcCircle(new_origin, radius)
    ifcclosedprofile = ifcfile.createIfcArbitraryClosedProfileDef("AREA", None, circle)
    solid = ifcfile.createIfcExtrudedAreaSolid(ifcclosedprofile, axis2placement, ifc_dir_z, length_z)
    if color != None:
        isi = ifcfile.createIfcStyledItem(solid, [color], None)
    return ifcfile.createIfcShapeRepresentation(geometric_representation_context, "Body", "SweptSolid", [solid])

def create_sphere(ifcfile, ifc_dir_x, ifc_dir_z, geometric_representation_context, radius=1.0, pos_x=0.0, pos_y=0.0, pos_z=0.0):
    """
    not supported by FZK/KIT-Viewer - therefore not used
    :param ifcfile:
    :param ifc_dir_x:
    :param ifc_dir_z:
    :param geometric_representation_context:
    :param radius:
    :param pos_x:
    :param pos_y:
    :param pos_z:
    :return:
    """
    new_origin = ifcfile.createIfcCartesianPoint([pos_x, pos_y, pos_z])
    axis2placement = ifcfile.createIfcAxis2Placement3D(new_origin, ifc_dir_z, ifc_dir_x)
    sphere = ifcfile.createIfcSphere(axis2placement, radius)
    solid = ifcfile.createIfcCsgSolid(sphere)
    return ifcfile.createIfcShapeRepresentation(geometric_representation_context, "Body", "SweptSolid", [solid])

def create_IfcAxis2Placement(ifcfile, point=OO, dir1=Z, dir2=X):
    """
    Creates an IfcAxis2Placement3D from Location, Axis and RefDirection specified as Python tuples
    :param ifcfile:
    :param point:
    :param dir1:
    :param dir2:
    :return: IfcAxis2Placement3D
    """
    point = ifcfile.createIfcCartesianPoint(point)
    dir1 = ifcfile.createIfcDirection(dir1)
    dir2 = ifcfile.createIfcDirection(dir2)
    axis2placement = ifcfile.createIfcAxis2Placement3D(point, dir1, dir2)
    return axis2placement

def create_IfcLocalPlacement(ifcfile, point=OO, dir1=Z, dir2=X, relative_to=None):
    """
    Creates an IfcLocalPlacement from Location, Axis and RefDirection, specified as Python tuples, and relative placement
    wenn restliche Parameter nicht gegeben, dann Standardplatzierung
    :param ifcfile:
    :param point:
    :param dir1:
    :param dir2:
    :param relative_to:
    :return: IfcLocalPlacement
    """
    axis2placement = create_IfcAxis2Placement(ifcfile, point, dir1, dir2)
    ifclocalplacement2 = ifcfile.createIfcLocalPlacement(relative_to, axis2placement)
    return ifclocalplacement2


def create_IfcPolyline(ifcfile, point_list):
    """
    Creates an IfcPolyLine from a list of points, specified as Python tuples
    :param ifcfile:
    :param point_list:
    :return: IfcPolyLine
    """
    ifcpts = []
    for point in point_list:
        point = ifcfile.createIfcCartesianPoint(point)
        ifcpts.append(point)
    polyline = ifcfile.createIfcPolyLine(ifcpts)
    return polyline

def create_IfcCircle(ifcfile, radius, pos_x, pos_y):
    ifc_origin = ifcfile.createIfcCartesianPoint((pos_x, pos_y, 0.0))
    ifc_dir_x = ifcfile.createIfcDirection((1., 0., 0.))
    position = ifcfile.createIfcAxis2Placement2D(ifc_origin, ifc_dir_x)
    circle = ifcfile.createIfcCircle(position, radius)
    return circle

def create_IfcText(ifcfile, text, position, extend_x=5000, extend_y=4000):
    """
    text box with a standard extend of 5000x4000
    :param ifcfile:
    :param text:
    :param position:
    :param extend_x:
    :param extend_y:
    :return:
    """
    extent = ifcfile.createIfcPlanarExtent(extend_x, extend_y) #TODO variable extend
    textliteral = ifcfile.createIfcTextLiteralWithExtent(text, position, 'LEFT', extent, 'bottom-left')
    return textliteral

def create_BoundingBox(ifcfile, element):
    """
    :param ifcfile:
    :param element:
    :return: IfcBoundingBox
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
        return bb
    except:
        logging.info("generating IfcBoundingBox for " + str(element) + " failed")


def create_IfcExtrudedAreaSolid(ifcfile, point_list, ifcaxis2placement, extrude_dir, extrusion):
    """
    Creates an IfcExtrudedAreaSolid from a list of points, specified as Python tuples
    :param ifcfile:
    :param point_list:
    :param ifcaxis2placement:
    :param extrude_dir:
    :param extrusion:
    :return: IfcExtrudedAreaSolid
    """
    polyline = create_IfcPolyline(ifcfile, point_list)
    ifcclosedprofile = ifcfile.createIfcArbitraryClosedProfileDef("AREA", None, polyline)
    ifcdir = ifcfile.createIfcDirection(extrude_dir)
    ifcextrudedareasolid = ifcfile.createIfcExtrudedAreaSolid(ifcclosedprofile, ifcaxis2placement, ifcdir, extrusion)
    return ifcextrudedareasolid


def add_visualization_of_element_coordinate_systems(ifcfile, element, subcontext):
    """
    add Polylines of different length (x=100, y=200, z=300) for each direction and a circle at the origin of the coordinate system
    :param ifcfile:
    :param element:
    :param subcontext:
    :return: nothing
    """
    list_of_lines_to_neighbors = []
    ifc_origin = ifcfile.createIfcCartesianPoint((0.0, 0.0, 0.0))
    ifc_dir_x = ifcfile.createIfcDirection((1., 0., 0.))
    position = ifcfile.createIfcAxis2Placement2D(ifc_origin, ifc_dir_x)
    circle = ifcfile.createIfcCircle(position, 50.0)
    list_of_lines_to_neighbors.append(circle)
    list_of_lines_to_neighbors.append(create_IfcPolyline(ifcfile, [(0.0, 0.0, 0.0), (100.0, 0.0, 0.0)]))
    list_of_lines_to_neighbors.append(create_IfcPolyline(ifcfile, [(0.0, 0.0, 0.0), (0.0, 200.0, 0.0)]))
    list_of_lines_to_neighbors.append(create_IfcPolyline(ifcfile, [(0.0, 0.0, 0.0), (0.0, 0.0, 300.0)]))
    representation_cs = ifcfile.createIfcShapeRepresentation(subcontext, "Axis", "Curve2D", list_of_lines_to_neighbors)
    pds = element.Representation
    old_representations = pds.Representations
    new_representations = old_representations + (representation_cs)
    ifcopenshell.api.run("attribute.edit_attributes", ifcfile, product=pds, attributes={"Representations": new_representations})

def create_colour(ifcfile, red, green, blue):
    """
    add IfcColourRGB, IfcSurfaceStyleShading, IfcSurfaceStyle to IFC file
    :param ifcfile:
    :param red: 0...1
    :param green: 0...1
    :param blue: 0...1
    :return: IfcSurfaceStyle
    """
    icr = ifcfile.createIfcColourRGB(None, red, green, blue)
    isss = ifcfile.createIfcSurfaceStyleShading(icr, None)
    iss = ifcfile.createIfcSurfaceStyle(None, 'POSITIVE', [isss])
    return iss