within LBDCG_example;
model HeatPumpPlant_V2 "enhanced for optimized results on CQ4"
  extends Modelica.Icons.Example;
  LibEAS.HeatingCircuits.CharacteristicCurve consumer(
    redeclare package Medium = Medium,
    Qp_1=Qp_1,
    Qp_2=Qp_2,
    mp_nom=mp_nom,
    dp_nom=dp_nom) "ideal consumer (modeled as a heating curve)" annotation (
      Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=-90,
        origin={130,-30})));
  LibEAS.BoundaryConditions.AnnualFluctuation annualTemperatureCurve
    annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=-90,
        origin={150,50})));
  parameter Modelica.SIunits.PressureDifference dp_nom=1000
    "nominal pressure difference for all components"
    annotation (Dialog(group="Hydraulik"));
      parameter Modelica.SIunits.MassFlowRate mp_nom=0.34
    "nominal mass flow rate for all components"
    annotation (Dialog(group="Hydraulik"));
  parameter Modelica.SIunits.HeatFlowRate Qp_1=10000 "heating load at -15°C"
    annotation (Dialog(group="Verbraucher"));
  parameter Modelica.SIunits.HeatFlowRate Qp_2=1000 "heating load at 5°C"
    annotation (Dialog(group="Verbraucher"));
  Buildings.Fluid.HeatPumps.Carnot_y            heaPum(
    redeclare package Medium1 = Medium,
    redeclare package Medium2 = Medium_sou,
    use_eta_Carnot_nominal=false,
    COP_nominal=4,
    dp1_nominal=dp_nom,
    dp2_nominal=dp_nom_sou,
    energyDynamics=Modelica.Fluid.Types.Dynamics.FixedInitial,
    P_nominal=1000*(20/4))
                     annotation (Placement(transformation(
        extent={{-10,10},{10,-10}},
        rotation=90,
        origin={-110,-30})));
  package Medium = AixLib.Media.Water annotation (
      __Dymola_choicesAllMatching=true);
  package Medium_sou = AixLib.Media.Air annotation (
      __Dymola_choicesAllMatching=true);
  Buildings.Fluid.Sources.Boundary_pT
                                   bou(redeclare package Medium =
        Medium,
    p=160000,
    nPorts=1)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=90,
        origin={-90,-70})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_pri_VL(redeclare package
      Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{-80,-20},{-60,0}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_pri_RL(redeclare package
      Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{-60,-60},{-80,-40}})));
  LibEAS.Storage.StratifiedEnhanced SP(
    redeclare package Medium = Medium,
    hTan=h_SP,
    dIns=0.1,
    nSeg=nSeg,
    m_flow_nominal=mp_nom,
    VTan=V_SP,
    T_start=293.15) "Speicher 3100l, h=2.0m (d~1.4m) - 40Schichten"
    annotation (Placement(transformation(extent={{-2,-60},{38,20}})));
  Buildings.Fluid.Movers.FlowControlled_m_flow
                                            pum_pri(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{-40,-20},{-20,0}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_sec_VL(redeclare package
      Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{60,-20},{80,0}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_sec_RL(redeclare package
      Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{80,-60},{60,-40}})));
  Modelica.Blocks.Sources.RealExpression measuredStorageTemperature(y=SP.heaPorVol[
        1].T)
    annotation (Placement(transformation(extent={{-200,34},{-160,52}})));
  parameter Integer nSeg=20 "Number of volume segments of the storage tank"
    annotation (Dialog(group="Speicher"));
  LibEAS.Sensors.PressureDrop_Display res_pri(
    redeclare package Medium = Medium,        m_flow_nominal=mp_nom, dp_nominal=
       dp_nom)
    annotation (Placement(transformation(extent={{-20,-60},{-40,-40}})));
  LibEAS.Sensors.PressureDrop_Display res_sec(
    redeclare package Medium = Medium,        m_flow_nominal=mp_nom, dp_nominal=
       dp_nom)
    annotation (Placement(transformation(extent={{120,-60},{100,-40}})));
  parameter Modelica.SIunits.Power Qp_heater_nom=10000 "Nennleistung Erzeuger"
    annotation (Dialog(group="Erzeuger"));
  parameter Modelica.SIunits.Volume V_SP=0.8
    "Speichervolumen" annotation (Dialog(group="Speicher"));
  parameter Modelica.SIunits.Length h_SP=2.0 "Höhe Speicher (ohne Dämmung)"
    annotation (Dialog(group="Speicher"));
  parameter Real yMin=0.4 "minimale Teillast des Erzeugers"
    annotation (Dialog(group="Erzeuger"));
  Buildings.Fluid.Sources.Boundary_pT sou_sou(
    use_T_in=true,
    nPorts=1,
    redeclare package Medium = Medium_sou,
    p=100000,
    T=283.15) "Fluid source on source side"
    annotation (Placement(transformation(extent={{-260,-60},{-240,-40}})));
  Buildings.Fluid.Sources.Boundary_pT sin_sou(
    nPorts=1,
    redeclare package Medium = Medium_sou,
    p=100000,
    T=281.15) "Fluid sink on source side"
    annotation (Placement(transformation(extent={{-260,-20},{-240,0}})));
  Buildings.Fluid.Movers.SpeedControlled_y pum_sou(
    redeclare package Medium = Medium_sou,
    per(
      pressure(V_flow={8.79043600562e-06*100,0.00277777777778*100,
            0.00556874120956*100,0.00776635021097*100,0.00978815049226*100,
            0.0113484528833*100,0.0127329465541*100,0.013985583685*100,
            0.0154360056259*100}, dp={60,50,49,48,47,46,45,44,43}),
      use_powerCharacteristic=true,
      power(V_flow={8.79043600562e-06*100,0.00277777777778*100,0.00556874120956
            *100,0.00776635021097*100,0.00978815049226*100,0.0113484528833*100,
            0.0127329465541*100,0.013985583685*100,0.0154360056259*100}, P={50,
            60,59,58,57,56,55,54,53})),
    final inputType=AixLib.Fluid.Types.InputType.Continuous)
    "Fan or pump at source side of HP" annotation (Placement(transformation(
        extent={{-10,10},{10,-10}},
        rotation=0,
        origin={-210,-50})));

  LibEAS.Sensors.TemperatureTwoPort_Display senT_sou_RL(redeclare package
      Medium = Medium_sou, m_flow_nominal=mp_nom_sou)
    annotation (Placement(transformation(extent={{-160,-20},{-180,0}})));
  parameter Modelica.SIunits.MassFlowRate mp_nom_sou=1
    "Nennmassestrom Quellkreis Wärmepumpe";
  LibEAS.Sensors.TemperatureTwoPort_Display senT_sou_VL(redeclare package
      Medium = Medium_sou, m_flow_nominal=mp_nom_sou)
    annotation (Placement(transformation(extent={{-180,-60},{-160,-40}})));
  LibEAS.Sensors.PressureDrop_Display res_sou(
    redeclare package Medium = Medium_sou,
    m_flow_nominal=mp_nom_sou,
    dp_nominal=dp_nom_sou)
    annotation (Placement(transformation(extent={{-200,-20},{-220,0}})));
  parameter Modelica.SIunits.Pressure dp_nom_sou=10
    "Pressure difference Quellkreis";
  Modelica.Blocks.Sources.RealExpression setPoint_ControlValue_pum_sou(y=1)
    annotation (Placement(transformation(extent={{-260,-90},{-240,-70}})));
  Modelica.Blocks.Continuous.Integrator W_el annotation (Placement(
        transformation(
        extent={{-10,-10},{10,10}},
        rotation=90,
        origin={-110,10})));
  LibEAS.Controls.SetPoint_Temperature setPointStorageTemperature(
      targetTemperature=40) annotation (Placement(transformation(rotation=0,
          extent={{-200,60},{-180,80}})));
  LibEAS.Controls.ControlGeneratorWithPumpMassFlow
    controlGeneratorWithPumpMassFlow(mp_nom_pum=mp_nom/2, yMin=yMin)
    annotation (Placement(transformation(rotation=0, extent={{-120,40},{-100,60}})));
equation
  connect(bou.ports[1], senT_pri_RL.port_b) annotation (Line(points={{-90,-60},
          {-90,-50},{-80,-50}}, color={0,127,255}));
  connect(senT_pri_VL.port_b, pum_pri.port_a)
    annotation (Line(points={{-60,-10},{-40,-10}}, color={0,127,255}));
  connect(senT_sec_VL.port_b, consumer.port_a) annotation (Line(points={{80,-10},
          {130,-10},{130,-20}}, color={0,127,255}));
  connect(measuredStorageTemperature.y, controlGeneratorWithPumpMassFlow.T_mea)
    annotation (Line(points={{-158,43},{-148,43},{-148,44},{-121,44}}, color={0,0,127},

      thickness=1));
  connect(controlGeneratorWithPumpMassFlow.pumpSignal, pum_pri.m_flow_in)
    annotation (Line(points={{-99,56},{-30,56},{-30,2}}, color={0,0,127},
      thickness=1));
  connect(setPointStorageTemperature.y, controlGeneratorWithPumpMassFlow.T_set)
    annotation (Line(points={{-179,70},{-150,70},{-150,56},{-121,56}}, color={0,0,127},

      thickness=1));
  connect(pum_pri.port_b, SP.fluPorVol[1]) annotation (Line(points={{-20,-10},{
          12,-10},{12,-42.2},{12.4,-42.2}}, color={0,127,255}));
  connect(SP.fluPorVolSec[1], senT_sec_VL.port_a) annotation (Line(points={{23.6,
          -42.2},{26.8,-42.2},{26.8,-10},{60,-10}},      color={0,127,255}));
  connect(SP.fluPorVolSec[nSeg], senT_sec_RL.port_b) annotation (Line(points={{23.6,
          -38.4},{25.8,-38.4},{25.8,-50},{60,-50}},      color={0,127,255}));
  connect(res_pri.port_b, senT_pri_RL.port_a)
    annotation (Line(points={{-40,-50},{-60,-50}}, color={0,127,255}));
  connect(SP.fluPorVol[nSeg], res_pri.port_a) annotation (Line(points={{12.4,
          -38.4},{12.2,-38.4},{12.2,-50},{-20,-50}}, color={0,127,255}));
  connect(consumer.port_b, res_sec.port_a) annotation (Line(points={{130,-40},{
          130,-50},{120,-50}}, color={0,127,255}));
  connect(res_sec.port_b, senT_sec_RL.port_a)
    annotation (Line(points={{100,-50},{80,-50}}, color={0,127,255}));
  connect(senT_pri_RL.port_b, heaPum.port_a1) annotation (Line(points={{-80,-50},
          {-104,-50},{-104,-40}}, color={0,127,255}));
  connect(heaPum.port_b1, senT_pri_VL.port_a) annotation (Line(points={{-104,-20},
          {-104,-10},{-80,-10}}, color={0,127,255}));
  connect(sou_sou.ports[1], pum_sou.port_a)
    annotation (Line(points={{-240,-50},{-220,-50}}, color={0,127,255}));
  connect(pum_sou.port_b, senT_sou_VL.port_a)
    annotation (Line(points={{-200,-50},{-180,-50}}, color={0,127,255}));
  connect(senT_sou_VL.port_b, heaPum.port_b2) annotation (Line(points={{-160,
          -50},{-116,-50},{-116,-40}}, color={0,127,255}));
  connect(heaPum.port_a2, senT_sou_RL.port_a) annotation (Line(points={{-116,
          -20},{-116,-10},{-160,-10}}, color={0,127,255}));
  connect(senT_sou_RL.port_b, res_sou.port_a)
    annotation (Line(points={{-180,-10},{-200,-10}}, color={0,127,255}));
  connect(res_sou.port_b, sin_sou.ports[1])
    annotation (Line(points={{-220,-10},{-240,-10}}, color={0,127,255}));
  connect(setPoint_ControlValue_pum_sou.y, pum_sou.y) annotation (Line(points={
          {-239,-80},{-209.5,-80},{-209.5,-62},{-210,-62}}, color={0,0,127},
      thickness=1));
  connect(heaPum.P, W_el.u)
    annotation (Line(points={{-110,-19},{-110,-2}}, color={0,0,127}));
  connect(controlGeneratorWithPumpMassFlow.generatorSignal, heaPum.y)
    annotation (Line(points={{-99,44},{-94,44},{-94,-42},{-101,-42}}, color={0,0,127},

      thickness=1));
  connect(annualTemperatureCurve.T_oda, consumer.T_oda) annotation (Line(points=
         {{150,39},{150,-6},{138,-6},{138,-20.2}}, color={0,0,127},
      thickness=1));
  connect(annualTemperatureCurve.T_oda, sou_sou.T_in) annotation (Line(points={
          {150,39},{150,-90},{-270,-90},{-270,-46},{-262,-46}}, color={0,0,127},

      thickness=1));
  annotation (Icon(coordinateSystem(preserveAspectRatio=false)), Diagram(
        coordinateSystem(preserveAspectRatio=false, extent={{-280,-100},{180,
            100}}), graphics={
        Rectangle(
          extent={{10,20},{-140,-80}},
          fillColor={255,255,170},
          fillPattern=FillPattern.Solid,
          pattern=LinePattern.None),
        Rectangle(
          extent={{140,20},{30,-80}},
          fillColor={213,255,170},
          fillPattern=FillPattern.Solid,
          pattern=LinePattern.None,
          lineColor={0,0,0}),
        Text(
          extent={{-18,-72},{10,-80}},
          pattern=LinePattern.None,
          fillColor={255,255,170},
          fillPattern=FillPattern.Solid,
          lineColor={255,128,0},
          fontSize=8,
          textString="primary curcuit 
index: pri
"),     Text(
          extent={{102,-70},{138,-80}},
          pattern=LinePattern.None,
          fillColor={255,255,170},
          fillPattern=FillPattern.Solid,
          lineColor={0,140,72},
          fontSize=8,
          textString="secondary curcuit
index: sec"),
        Rectangle(
          extent={{-132,-14},{-92,-48}},
          lineColor={217,67,180},
          lineThickness=1),
        Rectangle(
          extent={{-144,20},{-266,-86}},
          fillColor={255,170,213},
          fillPattern=FillPattern.Solid,
          pattern=LinePattern.None,
          lineColor={0,0,0}),
        Text(
          extent={{-174,-74},{-146,-84}},
          pattern=LinePattern.None,
          fillColor={255,255,170},
          fillPattern=FillPattern.Solid,
          lineColor={217,67,180},
          fontSize=8,
          textString="heat source (air)
index sou"),
        Rectangle(
          extent={{-88,90},{-216,30}},
          fillColor={205,213,255},
          fillPattern=FillPattern.Solid,
          pattern=LinePattern.None,
          lineColor={0,0,0}),
        Text(
          extent={{-172,88},{-88,76}},
          pattern=LinePattern.None,
          fillColor={255,255,170},
          fillPattern=FillPattern.Solid,
          lineColor={28,108,200},
          fontSize=8,
          textString="heat generator control"),
        Rectangle(
          extent={{172,94},{128,28}},
          fillColor={170,170,255},
          fillPattern=FillPattern.Solid,
          pattern=LinePattern.None,
          lineColor={0,0,0}),
        Text(
          extent={{128,94},{172,82}},
          pattern=LinePattern.None,
          fillColor={255,255,170},
          fillPattern=FillPattern.Solid,
          lineColor={102,44,145},
          fontSize=8,
          textString="ambient")}),
    experiment(
      StopTime=31536000,
      Interval=600.0012,
      __Dymola_Algorithm="Dassl"),
    Documentation(revisions="<html>
<ul>
<li>2022-12-09 EE copied to mo-x-ifc repository</li>
<li>2022-12-05 EE unified all IBPSA references to MBL, AixLib removed from example</li>
<li>2022-11-14 EE copy of FMI4BIM_Komponententests.Waermepumpen.modulierend.Test_Carnot_y for presentation at LBDCG</li>
<li>HeatPump within FMI4BIM.Anlagenkatalog.Testmodell_Erzeuger</li>
</ul>
</html>", info="<html>
<p>model information</p>
<ul>
<li>source circuit (index &quot;sou): ideal air source for heat pump (annual and daily temperature fluctuation modeled with a sine), no icing</li>
<li>primary circuit (index &quot;pri&quot;): hot water charging a storage tank</li>
<li>secondary circuit (index &quot;sec&quot;): hot water serving the building</li>
<li>building/consumer model: power given by a &quot;heating curve&quot;: maximum heating power at -15&deg;C (and below): 10kW, minimal heating power at 5&deg;C or more: 1kW</li>
<li>heat pump: thermal power 10kW, minimal part load operation at 40&percnt; (4kW_th)</li>
<li>storage 800l</li>
</ul>
<p>model metadata:</p>
<ul>
<li>Duration of Annual Simulation @notebook (dassl, precision 10E-4, grid=600s): 61s</li>
<li>reference result (annual energy consumption): 4,908 E10 Ws ~ 13.633 kWh</li>
</ul>
<p>Disclaimer: since this model depends on the LibEAS library it will not be running outside the institute, but it is nevertheless useful as a showcase for the MoTTLtranscriptor</p>
</html>"),
    __Dymola_Commands(
      file="Arbeitsordner/Elisabeth/TestHeatPump/Testmodell mit Speicher 220518 1622.mos"
        "220518 1622",
      file="Arbeitsordner/Elisabeth/TestHeatPump/Speicher 220518 1644.mos"
        "220518 1644",
      file="Arbeitsordner/Elisabeth/TestHeatPump/Speicher 220518 1735.mos"
        "220518 1735",
      file="Arbeitsordner/Elisabeth/TestHeatPump/220531 1630.mos" "220531 1630",
      file= "220614 1217 HeatPump.mos" "220614 1217",
      file= "Speicherauswertung.mos" "220614 Speicher"),
    __Dymola_experimentSetupOutput,
    __Dymola_experimentFlags(
      Advanced(GenerateVariableDependencies=false, OutputModelicaCode=false),
      Evaluate=false,
      OutputCPUtime=true,
      OutputFlatModelica=false));
end HeatPumpPlant_V2;
