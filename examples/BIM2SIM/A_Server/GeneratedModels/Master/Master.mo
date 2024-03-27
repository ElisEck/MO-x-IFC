within GeneratedModels.Master;
model Master "do not touch: necessary to as a copy master"

  package Medium = Buildings.Media.Water "Medium model";
  Modelica.Fluid.Fittings.SharpEdgedOrifice orifice
    annotation (Placement(transformation(extent={{-66,-54},{-46,-34}})));
  Modelica.Fluid.Fittings.SharpEdgedOrifice orifice1
    annotation (Placement(transformation(extent={{36,-58},{56,-38}})));

equation
  connect(orifice.port_b, orifice1.port_a) annotation (Line(points={{-46,-44},{28,-44},{28,-48},{36,-48}}, color={0,127,255}));

  annotation (Diagram(coordinateSystem(extent={{-100,-100},{1380,920}})),  Icon(
        coordinateSystem(extent={{-100,-100},{1380,920}})));
end Master;
