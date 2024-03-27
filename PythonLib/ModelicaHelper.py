"""
copied from SemanticScripting\ModelicaHelper.py
"""
import logging

def add_model_to_package(model, model_name, package_name, package_path):
    #TODO adjust package.order if it is a new model, not an existing model changed
    model.set_name(model_name)
    model.set_within_statement(package_name)
    model.save_as(package_path + model_name + ".mo")
    logging.info("Modelica files generated: " + package_path + model_name + ".mo")
    return model

def insert_component_at_location(model, type_, identifier, string_comment, lower_left_corner, component_dict, modifications, extent=[20,20]):
    upper_right_corner = [lower_left_corner[0] + extent[0], lower_left_corner[1] + extent[1]]
    placement_string = "Placement(transformation(extent={{" + str(lower_left_corner[0]) + "," + str(lower_left_corner[1]) + "},{" + str(upper_right_corner[0]) + "," + str(upper_right_corner[1]) + "}}))"
    model.insert_component(type_, identifier, modifications, string_comment=string_comment, annotations=[placement_string])
    component_dict[identifier] = [lower_left_corner[0] + extent[0]/2, lower_left_corner[1] + extent[1]/2]
    return model, component_dict

def add_connection(model, component1, component1_portname, component2, component2_portname, component_dict):
    center_1 = component_dict[component1]
    center_2 = component_dict[component2]
    placement_string = "Line(points={{" + str(center_1[0]) + "," + str(center_1[1]) + "},{" +str(center_2[0]) + "," + str(center_2[1]) + "}}"
    model.add_connect(component1 + "." + component1_portname, component2 + "." + component2_portname, annotations=[placement_string + ", color={0,125,255})"])
    return model

def insert_component_at_location_2(model, type_, identifier, string_comment, lower_left_corner, modifications, extent=[20,20]):
    """
    new version @ 15.8.23
    :param model:
    :param type_:
    :param identifier:
    :param string_comment:
    :param lower_left_corner:
    :param modifications:
    :param extent:
    :return:
    """
    upper_right_corner = [float(lower_left_corner[0]) + extent[0], float(lower_left_corner[1]) + extent[1]]
    placement_string = "Placement(transformation(extent={{" + str(lower_left_corner[0]) + "," + str(lower_left_corner[1]) + "},{" + str(upper_right_corner[0]) + "," + str(upper_right_corner[1]) + "}}))"
    model.insert_component(type_, identifier, modifications, string_comment=string_comment, annotations=[placement_string])
    return model

def add_connection_2(model, component1, component1_portname, component2, component2_portname, center_1, center_2):
    """
    new version @ 15.8.23
    :param model:
    :param component1:
    :param component1_portname:
    :param component2:
    :param component2_portname:
    :param center_1:
    :param center_2:
    :return:
    """
    placement_string = "Line(points={{" + str(center_1[0]) + "," + str(center_1[1]) + "},{" +str(center_2[0]) + "," + str(center_2[1]) + "}}"
    model.add_connect(component1 + "." + component1_portname, component2 + "." + component2_portname, annotations=[placement_string + ", color={0,125,255})"])
    return model

def modelica_name_from_ifc_name(ifc_name):
    modelica_name = ifc_name.replace("(", "_")
    modelica_name = modelica_name.replace(")", "_")
    modelica_name = modelica_name.replace("__", "_")
    return modelica_name

def test(model, component_dict):
    insert_component_at_location(model, 'Modelica.Electrical.Analog.Basic.CCC', 'myInst3', 'my comment3', [-20, -50], component_dict)
    insert_component_at_location(model, 'Modelica.Electrical.Analog.Basic.CCC', 'myInst4', 'my comment3', [-40, -50], component_dict)
    insert_component_at_location(model, 'Modelica.Electrical.Analog.Basic.CCC', 'myInst5', 'my comment3', [0, -50], component_dict)
    insert_component_at_location(model, 'Modelica.Electrical.Analog.Basic.CCC', 'myInst6', 'my comment3', [60, -50], component_dict)
    add_connection(model, "myInst5", "p2", "myInst6", "n1", component_dict)
    model.add_connect("myInst3.p2", "myInst4.n1", annotations=["Line(points={{-20,50},{-40,-50}}, color={0,125,255})"])  # connect(resistor.n, myInst3.p1) annotation (Line(points={{-20,50},{-10,50},{-10,20},{0,20}}, color={0,0,255}));
    return model