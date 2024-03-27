within GeneratedModels;
model generated

  parameter Modelica.Units.SI.PressureDifference dp_nom;

  parameter Modelica.Units.SI.MassFlowRate mp_nom;
  package Medium = Buildings.Media.Water "Medium model";
  Modelica.Fluid.Fittings.SharpEdgedOrifice orifice
    annotation (Placement(transformation(extent={{-66,-54},{-46,-34}})));
  Modelica.Fluid.Fittings.SharpEdgedOrifice orifice1
    annotation (Placement(transformation(extent={{36,-58},{56,-38}})));
  AixLib.Fluid.Actuators.Valves.ThreeWayLinear FLOWCONTROLLER_6_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcValve_484 - 2lqrrghQz0700000001nNk" annotation(Placement(transformation(extent={{293,636},{313.0,656.0}})));

  AixLib.Fluid.FixedResistances.CheckValve FLOWCONTROLLER_7_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcValve_411 - 2lqrrghQz0700000001nO0" annotation(Placement(transformation(extent={{606,276},{626.0,296.0}})));

  AixLib.Fluid.Movers.FlowControlled_m_flow FLOWMOVINGDEVICE_1_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPump_409 - 2lqrrghQz0700000001nNz" annotation(Placement(transformation(extent={{663,431},{683.0,451.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop FLOWSEGMENT_163_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_419 - 2lqrrghQz0700000001nO6" annotation(Placement(transformation(extent={{472,157},{492.0,177.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop FLOWSEGMENT_157_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_365 - 2lqrrghQz0700000001nNo" annotation(Placement(transformation(extent={{459,683},{479.0,703.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop FLOWSEGMENT_159_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_360 - 2lqrrghQz0700000001nNu" annotation(Placement(transformation(extent={{613,596},{633.0,616.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop replacement_042(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_353 - 33wW1CdubCe9GRJoJsSXht" annotation(Placement(transformation(extent={{470,767},{490.0,787.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop replacement_049(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_348 - 1t1JiX9Rv6FQ_2$3LUrtsV" annotation(Placement(transformation(extent={{494,80},{514.0,100.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop replacement_041(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_346 - 14Ic2WLhHEIeg2tYMKEw1w" annotation(Placement(transformation(extent={{538,642},{558.0,662.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop replacement_015(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_344 - 3X3TC0JKD7FxtCvI2zJpFY" annotation(Placement(transformation(extent={{184,331},{204.0,351.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop replacement_014(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_342 - 3g4Lr416L9BxT$ZE1aCpGR" annotation(Placement(transformation(extent={{324,232},{344.0,252.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop replacement_045(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_335 - 3VpCULpmH9iQmIwe7J3i6o" annotation(Placement(transformation(extent={{652,347},{672.0,367.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop replacement_057(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_330 - 15ATPRW8r7Hvo5dp7QlI7Q" annotation(Placement(transformation(extent={{169,553},{189.0,573.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop replacement_040(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_325 - 1pnZOee8zEYQrKlbhWHbTg" annotation(Placement(transformation(extent={{260,714},{280.0,734.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop replacement_046(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_320 - 2gTxXzkO16WAmg4R$SHgr1" annotation(Placement(transformation(extent={{539,220},{559.0,240.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop replacement_047(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_318 - 0DOoHMTPTC4ejpZERpwIcb" annotation(Placement(transformation(extent={{547,126},{567.0,146.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop replacement_002(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_313 - 2$8qIWZqT9pOCe1Hdglcre" annotation(Placement(transformation(extent={{313,200},{333.0,220.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop replacement_048(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_308 - 00xikHkH500ek5eXlgovUW" annotation(Placement(transformation(extent={{427,81},{447.0,101.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop replacement_001(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_305 - 0sU$uJt653JgW8QKdh5Cp7" annotation(Placement(transformation(extent={{253,369},{273.0,389.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop replacement_043(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_298 - 3kOa9I3Qn1WBWeFOJrSHPT" annotation(Placement(transformation(extent={{644,514},{664.0,534.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop replacement_059(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_293 - 3vrhARD$bF8vVCooFHr5Tb" annotation(Placement(transformation(extent={{238,565},{258.0,585.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop FLOWSEGMENT_175_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_239 - 2lqrrghQz0700000001vw$" annotation(Placement(transformation(extent={{165,499},{185.0,519.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop replacement_044(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_231 - 3wJ7gIUaPDcA9QbP_Tc7$5" annotation(Placement(transformation(extent={{683,641},{703.0,661.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop FLOWSEGMENT_181_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_213 - 2lqrrghQz0700000001vxO" annotation(Placement(transformation(extent={{117,575},{137.0,595.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop FLOWSEGMENT_182_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_201 - 2lqrrghQz0700000001vxR" annotation(Placement(transformation(extent={{83,515},{103.0,535.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop FLOWSEGMENT_180_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeSegment_186 - 2lqrrghQz0700000001vxL" annotation(Placement(transformation(extent={{93,456},{113.0,476.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop FLOWFITTING_151_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeFitting_407 - 2lqrrghQz0700000001nNn" annotation(Placement(transformation(extent={{375,663},{395.0,683.0}})));

  AixLib.Fluid.FixedResistances.Junction FLOWFITTING_511_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeFitting_270 - 2lqrrghQz0700000003ZOU" annotation(Placement(transformation(extent={{195,417},{215.0,437.0}})));

  AixLib.Fluid.FixedResistances.Junction FLOWFITTING_163_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeFitting_264 - 2lqrrghQz0700000001nXG" annotation(Placement(transformation(extent={{391,190},{411.0,210.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop FLOWFITTING_180_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeFitting_211 - 2lqrrghQz0700000001vxS" annotation(Placement(transformation(extent={{0,535},{20.0,555.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop FLOWFITTING_179_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeFitting_198 - 2lqrrghQz0700000001vxP" annotation(Placement(transformation(extent={{59,635},{79.0,655.0}})));

  AixLib.Fluid.FixedResistances.PressureDrop FLOWFITTING_178_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcPipeFitting_193 - 2lqrrghQz0700000001vxM" annotation(Placement(transformation(extent={{10,432},{30.0,452.0}})));

  AixLib.Fluid.Sensors.Temperature DISTRIBUTIONCONTROLELEMENT_4_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcFlowInstrument_24 - 2lqrrghQz0700000001nOD" annotation(Placement(transformation(extent={{402,0},{422.0,20.0}})));

  AixLib.Fluid.Sensors.Temperature DISTRIBUTIONCONTROLELEMENT_2_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcFlowInstrument_206 - 2lqrrghQz0700000001nNw" annotation(Placement(transformation(extent={{750,681},{770.0,701.0}})));

  AixLib.Fluid.Sensors.Temperature DISTRIBUTIONCONTROLELEMENT_5_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcFlowInstrument_181 - 2lqrrghQz0700000001nOG" annotation(Placement(transformation(extent={{519,0},{539.0,20.0}})));

  AixLib.Fluid.Sensors.Temperature DISTRIBUTIONCONTROLELEMENT_3_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcFlowInstrument_175 - 2lqrrghQz0700000001nOA" annotation(Placement(transformation(extent={{614,77},{634.0,97.0}})));

  AixLib.Fluid.Sensors.Temperature DISTRIBUTIONCONTROLELEMENT_1_(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcFlowInstrument_163 - 2lqrrghQz0700000001nNq" annotation(Placement(transformation(extent={{480,845},{500.0,865.0}})));

  AixLib.Fluid.HeatExchangers.HeaterCooler_u IfcBuildingElementProxy_25(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcCoil_414 - 2NcoxBZbm7n00000000kL0" annotation(Placement(transformation(extent={{232,256},{252.0,276.0}})));

  AixLib.Fluid.HeatExchangers.HeaterCooler_u IfcBuildingElementProxy_28(Medium=Medium, m_flow_nominal=mp_nom, dp_nominal=dp_nom) "IfcCoil_290 - 2NcoxBZbm7n00000001JfI" annotation(Placement(transformation(extent={{285,290},{305.0,310.0}})));
equation
  connect(orifice.port_b, orifice1.port_a) annotation (Line(points={{-46,-44},{28,-44},{28,-48},{36,-48}}, color={0,127,255}));
  connect(FLOWCONTROLLER_6_.port_3, replacement_059.port_b) annotation(Line(points={{303,636},
          {258,575}},                                                                                     color={0,125,255}));

  connect(FLOWCONTROLLER_6_.port_2, replacement_040.port_a) annotation(Line(points={{313,646},
          {260,724}},                                                                                     color={0,125,255}));

  connect(FLOWCONTROLLER_7_.port_b, replacement_046.port_a) annotation(Line(points={{626,286},
          {539,230}},                                                                                     color={0,125,255}));

  connect(FLOWCONTROLLER_7_.port_a, replacement_045.port_a) annotation(Line(points={{606,286},
          {652,357}},                                                                                     color={0,125,255}));

  connect(FLOWMOVINGDEVICE_1_.port_b, replacement_045.port_b) annotation(Line(points={{683,441},
          {672,357}},                                                                                       color={0,125,255}));

  connect(FLOWMOVINGDEVICE_1_.port_a, replacement_043.port_b) annotation(Line(points={{663,441},
          {664,524}},                                                                                       color={0,125,255}));

  ERROR;
  connect(FLOWSEGMENT_157_.port_2, replacement_041.port_a) annotation(Line(points={{459,683},{538,642}}, color={0,125,255}));

  connect(FLOWSEGMENT_157_.port_1, FLOWFITTING_151_.port_a) annotation(Line(points={{459,683},{375,663}}, color={0,125,255}));

  connect(FLOWSEGMENT_157_.port_3, replacement_042.port_a) annotation(Line(points={{459,683},{470,767}}, color={0,125,255}));

  connect(FLOWSEGMENT_159_.port_2, replacement_043.port_a) annotation(Line(points={{613,596},{644,514}}, color={0,125,255}));

  connect(FLOWSEGMENT_159_.port_3, replacement_044.port_a) annotation(Line(points={{613,596},{683,641}}, color={0,125,255}));

  connect(FLOWSEGMENT_159_.port_1, replacement_041.port_b) annotation(Line(points={{613,596},{538,642}}, color={0,125,255}));

  ERROR;
  ERROR;

  ERROR;

  ERROR;

  ERROR;

  ERROR;

  connect(FLOWFITTING_151_.port_b, FLOWCONTROLLER_6_.port_1) annotation(Line(points={{395,673},
          {293,646}},                                                                                      color={0,125,255}));

  connect(FLOWFITTING_511_.port_3, replacement_015.port_a) annotation(Line(points={{205,417},
          {184,341}},                                                                                    color={0,125,255}));

  connect(FLOWFITTING_511_.port_2, replacement_001.port_a) annotation(Line(points={{215,427},
          {253,379}},                                                                                    color={0,125,255}));

  connect(FLOWFITTING_163_.port_3, replacement_014.port_a) annotation(Line(points={{401,190},
          {324,242}},                                                                                    color={0,125,255}));

  connect(FLOWFITTING_163_.port_2, replacement_002.port_a) annotation(Line(points={{411,200},
          {362,200},{362,210},{313,210}},                                                                color={0,125,255}));

  ERROR;
  ERROR;

  ERROR;

  ERROR;

  ERROR;

  connect(IfcBuildingElementProxy_25.port_b, replacement_015.port_b) annotation(Line(points={{252,266},
          {204,341}},                                                                                              color={0,125,255}));

  connect(IfcBuildingElementProxy_25.port_a, replacement_014.port_b) annotation(Line(points={{232,266},
          {344,242}},                                                                                              color={0,125,255}));

  connect(IfcBuildingElementProxy_28.port_b, replacement_002.port_b) annotation(Line(points={{305,300},
          {333,210}},                                                                                              color={0,125,255}));

  connect(IfcBuildingElementProxy_28.port_a, replacement_001.port_b) annotation(Line(points={{285,300},
          {273,379}},                                                                                              color={0,125,255}));
                                                                                        annotation(Line(points={{117,575},{59,635}}, color={0,125,255}),
                                                                                                   Line(points={{83,515},{0,535}}, color={0,125,255}),
                                                                                                   Line(points={{93,456},{10,432}}, color={0,125,255}),
              Diagram(coordinateSystem(extent={{-100,-100},{1380,920}})),  Icon(
        coordinateSystem(extent={{-100,-100},{1380,920}})));
end generated;
