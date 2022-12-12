within LBDCG_example;
model HeatPumpPlant
  "heat pump plant with 3 circuits (source: air, primary circuit charging a storage tank, secondary circuit serving a building heating system)"
  extends Modelica.Icons.Example;
  LibEAS.HeatingCircuits.CharacteristicCurve characteristicCurve_2_1(
    redeclare package Medium = Medium,
    Qp_1=Qp_1,
    Qp_2=Qp_2,
    mp_nom=mp_nom,
    dp_nom=dp_nom) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=-90,
        origin={130,-30})));
  LibEAS.BoundaryConditions.AnnualFluctuation jahresgang annotation (Placement(
        transformation(
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
    annotation (Placement(transformation(extent={{0,-60},{40,20}})));
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
  LibEAS.Controls.ControlGeneratorWithPump
    wPRegler_konstantVp_ohneUntereBegr_3_1(yMin=yMin, Ti=300)
    annotation (Placement(transformation(extent={{-140,40},{-120,60}})));
  Modelica.Blocks.Sources.RealExpression realExpression2(y=SP.heaPorVol[1].T)
    annotation (Placement(transformation(extent={{-200,34},{-160,52}})));
  Modelica.Blocks.Math.Gain gain1(k=mp_nom/2)
    annotation (Placement(transformation(extent={{-80,40},{-60,60}})));
  Modelica.Blocks.Sources.RealExpression realExpression1(y=40)
    annotation (Placement(transformation(extent={{-220,60},{-200,80}})));
  Modelica.Blocks.Math.UnitConversions.From_degC from_degC2
                                                           annotation (
      Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=0,
        origin={-170,70})));
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
  Modelica.Blocks.Sources.RealExpression realExpression(y=1)
    annotation (Placement(transformation(extent={{-260,-90},{-240,-70}})));
  Modelica.Blocks.Continuous.Integrator W_el annotation (Placement(
        transformation(
        extent={{-10,-10},{10,10}},
        rotation=90,
        origin={-110,10})));
equation
  connect(bou.ports[1], senT_pri_RL.port_b) annotation (Line(points={{-90,-60},
          {-90,-50},{-80,-50}}, color={0,127,255}));
  connect(senT_pri_VL.port_b, pum_pri.port_a)
    annotation (Line(points={{-60,-10},{-40,-10}}, color={0,127,255}));
  connect(senT_sec_VL.port_b, characteristicCurve_2_1.port_a) annotation (Line(
        points={{80,-10},{130,-10},{130,-20}}, color={0,127,255}));
  connect(realExpression2.y,wPRegler_konstantVp_ohneUntereBegr_3_1.T_mea)
    annotation (Line(points={{-158,43},{-148,43},{-148,44},{-140,44}}, color={0,
          0,127}));
  connect(gain1.y, pum_pri.m_flow_in)
    annotation (Line(points={{-59,50},{-30,50},{-30,2}}, color={0,0,127}));
  connect(realExpression1.y, from_degC2.u)
    annotation (Line(points={{-199,70},{-182,70}}, color={0,0,127}));
  connect(from_degC2.y, wPRegler_konstantVp_ohneUntereBegr_3_1.T_set)
    annotation (Line(points={{-159,70},{-150,70},{-150,55.8},{-140,55.8}},
        color={0,0,127}));
  connect(wPRegler_konstantVp_ohneUntereBegr_3_1.generatorSignal, heaPum.y)
    annotation (Line(points={{-119.95,46.05},{-120,46.05},{-120,46},{-94,46},{-94,
          -44},{-101,-44},{-101,-42}}, color={0,0,127}));
  connect(pum_pri.port_b, SP.fluPorVol[1]) annotation (Line(points={{-20,-10},{
          12,-10},{12,-42.2},{14.4,-42.2}}, color={0,127,255}));
  connect(SP.fluPorVolSec[1], senT_sec_VL.port_a) annotation (Line(points={{
          25.6,-42.2},{26.8,-42.2},{26.8,-10},{60,-10}}, color={0,127,255}));
  connect(SP.fluPorVolSec[nSeg], senT_sec_RL.port_b) annotation (Line(points={{
          25.6,-38.4},{25.8,-38.4},{25.8,-50},{60,-50}}, color={0,127,255}));
  connect(res_pri.port_b, senT_pri_RL.port_a)
    annotation (Line(points={{-40,-50},{-60,-50}}, color={0,127,255}));
  connect(SP.fluPorVol[nSeg], res_pri.port_a) annotation (Line(points={{14.4,
          -38.4},{12.2,-38.4},{12.2,-50},{-20,-50}}, color={0,127,255}));
  connect(characteristicCurve_2_1.port_b, res_sec.port_a) annotation (Line(
        points={{130,-40},{130,-50},{120,-50}}, color={0,127,255}));
  connect(res_sec.port_b, senT_sec_RL.port_a)
    annotation (Line(points={{100,-50},{80,-50}}, color={0,127,255}));
  connect(senT_pri_RL.port_b, heaPum.port_a1) annotation (Line(points={{-80,-50},
          {-104,-50},{-104,-40}}, color={0,127,255}));
  connect(heaPum.port_b1, senT_pri_VL.port_a) annotation (Line(points={{-104,-20},
          {-104,-10},{-80,-10}}, color={0,127,255}));
  connect(sou_sou.ports[1], pum_sou.port_a)
    annotation (Line(points={{-240,-50},{-220,-50}}, color={0,127,255}));
  connect(wPRegler_konstantVp_ohneUntereBegr_3_1.pumpSignal, gain1.u)
    annotation (Line(points={{-120.05,53.95},{-100.025,53.95},{-100.025,50},{-82,
          50}}, color={0,0,127}));
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
  connect(realExpression.y, pum_sou.y) annotation (Line(points={{-239,-80},{
          -209.5,-80},{-209.5,-62},{-210,-62}}, color={0,0,127}));
  connect(heaPum.P, W_el.u)
    annotation (Line(points={{-110,-19},{-110,-2}}, color={0,0,127}));
  connect(jahresgang.T_oda, characteristicCurve_2_1.T_oda) annotation (Line(
        points={{150,39},{150,-10},{138,-10},{138,-20.2}}, color={0,0,127}));
  connect(jahresgang.T_oda, sou_sou.T_in) annotation (Line(points={{150,39},{
          150,-90},{-272,-90},{-272,-46},{-262,-46}}, color={0,0,127}));
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
          extent={{-76,-70},{10,-80}},
          pattern=LinePattern.None,
          fillColor={255,255,170},
          fillPattern=FillPattern.Solid,
          lineColor={255,128,0},
          textString="Primär-/Ladekreis (Index: pri)"),
        Text(
          extent={{68,-72},{138,-80}},
          pattern=LinePattern.None,
          fillColor={255,255,170},
          fillPattern=FillPattern.Solid,
          lineColor={0,140,72},
          textString="Sekundärkreis (Index: sec)"),
        Rectangle(
          extent={{-132,-14},{-92,-48}},
          lineColor={217,67,180},
          lineThickness=1)}),
    experiment(
      StopTime=31536000,
      Interval=600,
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
end HeatPumpPlant;
