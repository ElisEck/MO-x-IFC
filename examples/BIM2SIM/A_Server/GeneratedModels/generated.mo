within GeneratedModels;
model generated "do not touch: necessary to as a copy master"

  
  parameter Modelica.Units.SI.PressureDifference dp_nom;
  
  parameter Modelica.Units.SI.MassFlowRate mp_nom;
  package Medium = Buildings.Media.Water "Medium model";
  Modelica.Fluid.Fittings.SharpEdgedOrifice orifice
    annotation (Placement(transformation(extent={{-66,-54},{-46,-34}})));
  Modelica.Fluid.Fittings.SharpEdgedOrifice orifice1
    annotation (Placement(transformation(extent={{36,-58},{56,-38}})));
  AixLib.Fluid.FixedResistances.CheckValve FLOWCONTROLLER_7_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcValve_179 - 2lqrrghQz0700000001nO0" annotation(Placement(transformation(extent={{656,427},{676.0,447.0}})));
  
  AixLib.Fluid.Actuators.Valves.ThreeWayLinear FLOWCONTROLLER_6_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcValve_168 - 2lqrrghQz0700000001nNk" annotation(Placement(transformation(extent={{190,277},{210.0,297.0}})));
  
  AixLib.Fluid.Movers.FlowControlled_m_flow FLOWMOVINGDEVICE_1_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPump_637 - 2lqrrghQz0700000001nNz" annotation(Placement(transformation(extent={{623,265},{643.0,285.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop replacement_011(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_994 - 13BNUFFYH3AvQEWdv1tBpZ" annotation(Placement(transformation(extent={{405,151},{425.0,171.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop replacement_014(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_91 - 2pVe14Lov7OxordoDv4F_3" annotation(Placement(transformation(extent={{656,653},{676.0,673.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop replacement_013(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_854 - 1fNHMJvlfFbRfmg4We6meF" annotation(Placement(transformation(extent={{563,204},{583.0,224.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop replacement_002(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_736 - 243EFl3HLE2QFpctxGq6KY" annotation(Placement(transformation(extent={{430,656},{450.0,676.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop FLOWSEGMENT_163_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_69 - 2lqrrghQz0700000001nO6" annotation(Placement(transformation(extent={{597,598},{617.0,618.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop FLOWSEGMENT_159_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_569 - 2lqrrghQz0700000001nNu" annotation(Placement(transformation(extent={{493,151},{513.0,171.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop FLOWSEGMENT_157_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_567 - 2lqrrghQz0700000001nNo" annotation(Placement(transformation(extent={{316,156},{336.0,176.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop replacement_008(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_527 - 3f2SuUWc92hAMkw79x$rJu" annotation(Placement(transformation(extent={{659,342},{679.0,362.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop replacement_005(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_507 - 01pJ_DY6j9YPiMuxLPH48N" annotation(Placement(transformation(extent={{266,597},{286.0,617.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop replacement_012(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_465 - 3PrFdpRnPD9w12Kj62Sioh" annotation(Placement(transformation(extent={{597,686},{617.0,706.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop replacement_004(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_444 - 0swE0fC2jFs8d2_E14Z13r" annotation(Placement(transformation(extent={{451,568},{471.0,588.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop FLOWSEGMENT_182_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_385 - 2lqrrghQz0700000001vxR" annotation(Placement(transformation(extent={{78,442},{98.0,462.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop FLOWSEGMENT_180_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_380 - 2lqrrghQz0700000001vxL" annotation(Placement(transformation(extent={{98,419},{118.0,439.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop replacement_015(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_339 - 3HwAZX76b64u0WKGDEZ$yO" annotation(Placement(transformation(extent={{530,75},{550.0,95.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop replacement_003(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_287 - 348yK5cLD6Nw9HuouX41uu" annotation(Placement(transformation(extent={{122,223},{142.0,243.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop replacement_009(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_227 - 1mic7Jb_T4NgDQdufa0ZAS" annotation(Placement(transformation(extent={{625,510},{645.0,530.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop replacement_006(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_202 - 2ItiJ9XBz8$h5OqGJxLqjg" annotation(Placement(transformation(extent={{181,368},{201.0,388.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop FLOWSEGMENT_175_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_188 - 2lqrrghQz0700000001vw$" annotation(Placement(transformation(extent={{156,462},{176.0,482.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop FLOWSEGMENT_181_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_161 - 2lqrrghQz0700000001vxO" annotation(Placement(transformation(extent={{87,512},{107.0,532.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop replacement_007(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_154 - 2m4FV69YH3bh3OH$0Smbpe" annotation(Placement(transformation(extent={{288,75},{308.0,95.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop replacement_001(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_113 - 3ekcsQd3DETvlUIZdvZ0Lv" annotation(Placement(transformation(extent={{302,514},{322.0,534.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop replacement_010(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_1041 - 2IE4sOA8D4CxGTlLeYtkcn" annotation(Placement(transformation(extent={{678,586},{698.0,606.0}})));
  
  AixLib.Fluid.FixedResistances.Junction FLOWFITTING_163_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeFitting_371 - 2lqrrghQz0700000001nXG" annotation(Placement(transformation(extent={{509,614},{529.0,634.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop FLOWFITTING_180_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeFitting_358 - 2lqrrghQz0700000001vxS" annotation(Placement(transformation(extent={{0,398},{20.0,418.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop FLOWFITTING_179_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeFitting_353 - 2lqrrghQz0700000001vxP" annotation(Placement(transformation(extent={{10,556},{30.0,576.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop FLOWFITTING_178_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeFitting_348 - 2lqrrghQz0700000001vxM" annotation(Placement(transformation(extent={{0,439},{20.0,459.0}})));
  
  AixLib.Fluid.FixedResistances.Junction FLOWFITTING_511_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeFitting_255 - 2lqrrghQz0700000003ZOU" annotation(Placement(transformation(extent={{227,515},{247.0,535.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop FLOWFITTING_151_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeFitting_186 - 2lqrrghQz0700000001nNn" annotation(Placement(transformation(extent={{252,216},{272.0,236.0}})));
  
  AixLib.Fluid.FixedResistances.PressureDrop FLOWFITTING_172_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeFitting_173 - 2lqrrghQz0700000001vw_" annotation(Placement(transformation(extent={{130,542},{150.0,562.0}})));
  
  AixLib.Fluid.Sensors.Temperature DISTRIBUTIONCONTROLELEMENT_4_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcFlowInstrument_47 - 2lqrrghQz0700000001nOD" annotation(Placement(transformation(extent={{761,594},{781.0,614.0}})));
  
  AixLib.Fluid.Sensors.Temperature DISTRIBUTIONCONTROLELEMENT_5_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcFlowInstrument_399 - 2lqrrghQz0700000001nOG" annotation(Placement(transformation(extent={{719,708},{739.0,728.0}})));
  
  AixLib.Fluid.Sensors.Temperature DISTRIBUTIONCONTROLELEMENT_3_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcFlowInstrument_394 - 2lqrrghQz0700000001nOA" annotation(Placement(transformation(extent={{618,768},{638.0,788.0}})));
  
  AixLib.Fluid.Sensors.Temperature DISTRIBUTIONCONTROLELEMENT_1_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcFlowInstrument_390 - 2lqrrghQz0700000001nNq" annotation(Placement(transformation(extent={{264,0},{284.0,20.0}})));
  
  AixLib.Fluid.Sensors.Temperature DISTRIBUTIONCONTROLELEMENT_2_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcFlowInstrument_26 - 2lqrrghQz0700000001nNw" annotation(Placement(transformation(extent={{569,7},{589.0,27.0}})));
  
  AixLib.Fluid.HeatExchangers.HeaterCooler_u IfcBuildingElementProxy_25(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcCoil_212 - 2NcoxBZbm7n00000000kL0" annotation(Placement(transformation(extent={{376,542},{396.0,562.0}})));
  
  AixLib.Fluid.HeatExchangers.HeaterCooler_u IfcBuildingElementProxy_28(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcCoil_209 - 2NcoxBZbm7n00000001JfI" annotation(Placement(transformation(extent={{341,646},{361.0,666.0}})));
  

equation
  connect(orifice.port_b, orifice1.port_a) annotation (Line(points={{-46,-44},{28,-44},{28,-48},{36,-48}}, color={0,127,255}));
  connect(FLOWCONTROLLER_7_.port_b, replacement_009.port_b) annotation(Line(points={{656,427},{625,510}}, color={0,125,255}));
  
  connect(FLOWCONTROLLER_7_.port_a, replacement_008.port_a) annotation(Line(points={{656,427},{659,342}}, color={0,125,255}));
  
  connect(FLOWCONTROLLER_6_.port_3, replacement_006.port_a) annotation(Line(points={{190,277},{181,368}}, color={0,125,255}));
  
  connect(FLOWCONTROLLER_6_.port_2, replacement_003.port_b) annotation(Line(points={{190,277},{122,223}}, color={0,125,255}));
  
  connect(FLOWCONTROLLER_6_.port_1, FLOWFITTING_151_.port_a) annotation(Line(points={{190,277},{252,216}}, color={0,125,255}));
  
  connect(FLOWMOVINGDEVICE_1_.port_a, replacement_008.port_b) annotation(Line(points={{623,265},{659,342}}, color={0,125,255}));
  
  connect(FLOWMOVINGDEVICE_1_.port_b, replacement_013.port_b) annotation(Line(points={{623,265},{563,204}}, color={0,125,255}));
  
  connect(FLOWSEGMENT_163_.new port for edge with replacement, FLOWFITTING_163_.port_1) annotation(Line(points={{597,598},{509,614}}, color={0,125,255}));
  
  connect(FLOWSEGMENT_159_.port_3, replacement_015.port_a) annotation(Line(points={{493,151},{530,75}}, color={0,125,255}));
  
  connect(FLOWSEGMENT_159_.port_1, replacement_011.port_a) annotation(Line(points={{493,151},{405,151}}, color={0,125,255}));
  
  connect(FLOWSEGMENT_159_.port_2, replacement_013.port_a) annotation(Line(points={{493,151},{563,204}}, color={0,125,255}));
  
  connect(FLOWSEGMENT_157_.port_3, replacement_011.port_b) annotation(Line(points={{316,156},{405,151}}, color={0,125,255}));
  
  connect(FLOWSEGMENT_157_.port_2, replacement_007.port_b) annotation(Line(points={{316,156},{288,75}}, color={0,125,255}));
  
  connect(FLOWSEGMENT_182_.port_3, FLOWFITTING_180_.new port for edge with replacement) annotation(Line(points={{78,442},{0,398}}, color={0,125,255}));
  
  connect(FLOWSEGMENT_180_.port_3, FLOWFITTING_178_.new port for edge with replacement) annotation(Line(points={{98,419},{0,439}}, color={0,125,255}));
  
  connect(FLOWSEGMENT_175_.new port for edge with replacement, FLOWSEGMENT_180_.port_1) annotation(Line(points={{156,462},{98,419}}, color={0,125,255}));
  
  connect(FLOWSEGMENT_175_.new port for edge with replacement, FLOWSEGMENT_182_.port_1) annotation(Line(points={{156,462},{78,442}}, color={0,125,255}));
  
  connect(FLOWSEGMENT_175_.new port for edge with replacement, FLOWSEGMENT_181_.port_1) annotation(Line(points={{156,462},{87,512}}, color={0,125,255}));
  
  connect(FLOWSEGMENT_181_.port_2, FLOWFITTING_179_.new port for edge with replacement) annotation(Line(points={{87,512},{10,556}}, color={0,125,255}));
  
  connect(FLOWFITTING_163_.port_3, replacement_004.port_a) annotation(Line(points={{509,614},{451,568}}, color={0,125,255}));
  
  connect(FLOWFITTING_163_.port_2, replacement_002.port_a) annotation(Line(points={{509,614},{430,656}}, color={0,125,255}));
  
  connect(FLOWFITTING_511_.port_3, replacement_005.port_b) annotation(Line(points={{227,515},{266,597}}, color={0,125,255}));
  
  connect(FLOWFITTING_511_.port_2, replacement_001.port_b) annotation(Line(points={{227,515},{302,514}}, color={0,125,255}));
  
  connect(FLOWFITTING_151_.port_b, FLOWSEGMENT_157_.port_1) annotation(Line(points={{252,216},{316,156}}, color={0,125,255}));
  
  connect(DISTRIBUTIONCONTROLELEMENT_4_.new port for edge with replacement, replacement_010.port_b) annotation(Line(points={{761,594},{678,586}}, color={0,125,255}));
  
  connect(DISTRIBUTIONCONTROLELEMENT_5_.new port for edge with replacement, replacement_014.port_b) annotation(Line(points={{719,708},{656,653}}, color={0,125,255}));
  
  connect(DISTRIBUTIONCONTROLELEMENT_3_.new port for edge with replacement, replacement_012.port_b) annotation(Line(points={{618,768},{597,686}}, color={0,125,255}));
  
  connect(DISTRIBUTIONCONTROLELEMENT_1_.new port for edge with replacement, replacement_007.port_a) annotation(Line(points={{264,0},{288,75}}, color={0,125,255}));
  
  connect(DISTRIBUTIONCONTROLELEMENT_2_.new port for edge with replacement, replacement_015.port_b) annotation(Line(points={{569,7},{530,75}}, color={0,125,255}));
  
  connect(IfcBuildingElementProxy_25.port_b, replacement_004.port_b) annotation(Line(points={{376,542},{451,568}}, color={0,125,255}));
  
  connect(IfcBuildingElementProxy_25.port_a, replacement_001.port_a) annotation(Line(points={{376,542},{302,514}}, color={0,125,255}));
  
  connect(IfcBuildingElementProxy_28.port_b, replacement_005.port_a) annotation(Line(points={{341,646},{266,597}}, color={0,125,255}));
  
  connect(IfcBuildingElementProxy_28.port_a, replacement_002.port_b) annotation(Line(points={{341,646},{430,656}}, color={0,125,255}));
  

  annotation (Diagram(coordinateSystem(extent={{-100,-100},{1380,920}})),  Icon(
        coordinateSystem(extent={{-100,-100},{1380,920}})));
end generated;
