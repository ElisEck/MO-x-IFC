within HIL;
model HIL_flat "kompletter HIL Versuchsstand (ohne Regelung) - ohne Submodelle"
  extends Modelica.Icons.Example;
  AixLib.Fluid.Movers.SpeedControlled_y pum_VS_H(redeclare package Medium =
        Medium, redeclare
      AixLib.Fluid.Movers.Data.Pumps.Wilo.Stratos25slash1to8 per)
    annotation (Placement(transformation(extent={{-60,520},{-40,540}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VS_1(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom) "XLM10BTR01"
    annotation (Placement(transformation(extent={{-140,520},{-160,540}})));
  AixLib.Fluid.HeatExchangers.ConstantEffectiveness hex_EH(
    redeclare package Medium1 = Medium,
    redeclare package Medium2 = Medium,
    m1_flow_nominal=mp_nom,
    m2_flow_nominal=mp_nom,
    dp1_nominal=dp_nom,
    dp2_nominal=dp_nom) annotation (Placement(transformation(
        extent={{10,-10},{-10,10}},
        rotation=90,
        origin={-290,450})));
  AixLib.Fluid.FixedResistances.Junction jun_VS_1(
    redeclare package Medium = Medium,
    m_flow_nominal={mp_nom,mp_nom,mp_nom},
    dp_nominal={dp_nom,dp_nom,dp_nom}) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=180,
        origin={10,530})));
  parameter Modelica.Units.SI.MassFlowRate mp_nom=1
    "Nominal mass flow rate, used for regularization near zero flow";
  parameter Modelica.Units.SI.PressureDifference dp_nom=1000
    "Nominal pressure drop of fully open valve, used if CvData=AixLib.Fluid.Types.CvTypes.OpPoint";
  replaceable package Medium = AixLib.Media.Water constrainedby
    Modelica.Media.Interfaces.PartialMedium annotation (choicesAllMatching=true);
  Modelica.Blocks.Sources.RealExpression re_pum_EH(y=1)
    annotation (Placement(transformation(extent={{-80,560},{-60,580}})));
  LibEAS.Sensors.PressureDrop_Display dp_VS_2(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{40,520},{60,540}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VS_2(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{100,520},{80,540}})));
  LibEAS.Storage.StratifiedEnhanced tan_WV(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    VTan=0.871,
    hTan=2,
    dIns=0.1) "Wärmeverteiler"
    annotation (Placement(transformation(extent={{120,460},{140,520}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VS_7(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{80,440},{100,460}})));
  LibEAS.Sensors.PressureDrop_Display dp_VS_1(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{-220,520},{-200,540}})));
  AixLib.Fluid.FixedResistances.Junction jun_VS_4_H(
    redeclare package Medium = Medium,
    m_flow_nominal={mp_nom,mp_nom,mp_nom},
    dp_nominal={dp_nom,dp_nom,dp_nom}) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=0,
        origin={250,610})));
  AixLib.Fluid.FixedResistances.Junction jun_VS_5_H(
    redeclare package Medium = Medium,
    m_flow_nominal={mp_nom,mp_nom,mp_nom},
    dp_nominal={dp_nom,dp_nom,dp_nom}) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=0,
        origin={330,610})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VS_3(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{100,600},{80,620}})));
  LibEAS.Sensors.PressureDrop_Display dp_VS_3(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{40,600},{60,620}})));
  LibEAS.Storage.StratifiedEnhanced tan_S(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    VTan=0.884,
    hTan=2,
    dIns=0.1) "Sammler"
    annotation (Placement(transformation(extent={{-160,100},{-140,160}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VS_11(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{-178,400},{-198,420}})));
  LibEAS.Sensors.PressureDrop_Display dp_VS_11(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom) annotation (Placement(transformation(
        extent={{10,-10},{-10,10}},
        rotation=0,
        origin={-270,410})));
  AixLib.Fluid.HeatExchangers.ConstantEffectiveness hex_EK(
    redeclare package Medium1 = Medium,
    redeclare package Medium2 = Medium,
    m1_flow_nominal=mp_nom,
    m2_flow_nominal=mp_nom,
    dp1_nominal=dp_nom,
    dp2_nominal=dp_nom) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=90,
        origin={-290,-230})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VS_17(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{-180,-40},{-200,-20}})));
  LibEAS.Sensors.PressureDrop_Display dp_VS_17(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom) annotation (Placement(transformation(
        extent={{10,-10},{-10,10}},
        rotation=0,
        origin={-230,-30})));
  AixLib.Fluid.Sensors.Pressure senp_VS_11(redeclare package Medium = Medium)
    annotation (Placement(transformation(extent={{-240,420},{-220,440}})));
  AixLib.Fluid.Sensors.Pressure senp_VS_17(redeclare package Medium = Medium)
    annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=90,
        origin={-290,-50})));
  AixLib.Fluid.Movers.SpeedControlled_y pum_VS_K(redeclare package Medium =
        Medium, redeclare
      AixLib.Fluid.Movers.Data.Pumps.Wilo.Stratos25slash1to8 per)
    annotation (Placement(transformation(extent={{-40,-220},{-20,-200}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VS_16(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{-160,-280},{-180,-260}})));
  Modelica.Blocks.Sources.RealExpression re_pum_EH1(y=1)
    annotation (Placement(transformation(extent={{-60,-180},{-40,-160}})));
  LibEAS.Sensors.PressureDrop_Display dp_VS_16(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{-220,-280},{-200,-260}})));
  AixLib.Fluid.FixedResistances.Junction jun_VS_5_H1(
    redeclare package Medium = Medium,
    m_flow_nominal={mp_nom,mp_nom,mp_nom},
    dp_nominal={dp_nom,dp_nom,dp_nom}) annotation (Placement(transformation(
        extent={{-10,10},{10,-10}},
        rotation=0,
        origin={-90,-270})));
  AixLib.Fluid.Sensors.Pressure senp_VS_16(redeclare package Medium = Medium)
    annotation (Placement(transformation(extent={{-140,-262},{-120,-242}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VS_15(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{-60,-220},{-80,-200}})));
  LibEAS.Storage.StratifiedEnhanced tan_KV(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    VTan=0.871,
    hTan=2,
    dIns=0.1) "Kälteverteiler"
    annotation (Placement(transformation(extent={{120,-200},{140,-140}})));
  LibEAS.Sensors.PressureDrop_Display dp_VS_15(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{40,-220},{60,-200}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VS_21(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{222,-220},{202,-200}})));
  AixLib.Fluid.FixedResistances.Junction jun_VS_18(
    redeclare package Medium = Medium,
    m_flow_nominal={mp_nom,mp_nom,mp_nom},
    dp_nominal={dp_nom,dp_nom,dp_nom}) annotation (Placement(transformation(
        extent={{10,-10},{-10,10}},
        rotation=180,
        origin={250,-210})));
  LibEAS.Sensors.PressureDrop_Display dp_VS_21(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{160,-220},{180,-200}})));
  AixLib.Fluid.FixedResistances.Junction jun_VS_19(
    redeclare package Medium = Medium,
    m_flow_nominal={mp_nom,mp_nom,mp_nom},
    dp_nominal={dp_nom,dp_nom,dp_nom}) annotation (Placement(transformation(
        extent={{10,-10},{-10,10}},
        rotation=180,
        origin={330,-210})));
  LibEAS.Sensors.PressureDrop_Display dp_VS_8(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom) annotation (Placement(transformation(
        extent={{10,-10},{-10,10}},
        rotation=0,
        origin={50,210})));
  LibEAS.Sensors.PressureDrop_Display dp_VS_12(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom) annotation (Placement(transformation(
        extent={{10,-10},{-10,10}},
        rotation=0,
        origin={50,50})));
  LibEAS.Sensors.PressureDrop_Display dp_VS_9(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom) annotation (Placement(transformation(
        extent={{10,-10},{-10,10}},
        rotation=0,
        origin={50,130})));
  LibEAS.Sensors.PressureDrop_Display dp_VS_7(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom) annotation (Placement(transformation(
        extent={{10,-10},{-10,10}},
        rotation=0,
        origin={50,450})));
  LibEAS.Sensors.PressureDrop_Display dp_VS_10(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom) annotation (Placement(transformation(
        extent={{10,-10},{-10,10}},
        rotation=0,
        origin={-70,170})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VS_10(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{-100,160},{-120,180}})));
  LibEAS.Sensors.PressureDrop_Display dp_VS_14(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom) annotation (Placement(transformation(
        extent={{10,-10},{-10,10}},
        rotation=0,
        origin={-30,30})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VS_14(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{-100,20},{-120,40}})));
  AixLib.Fluid.Sensors.Pressure senp_VS_14(redeclare package Medium = Medium)
    annotation (Placement(transformation(extent={{-80,40},{-60,60}})));
  LibEAS.Sensors.PressureDrop_Display dp_VS_13(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom) annotation (Placement(transformation(
        extent={{10,-10},{-10,10}},
        rotation=0,
        origin={50,-130})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VS_13(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{80,-140},{100,-120}})));
  AixLib.Fluid.FixedResistances.Junction jun_VS_14(
    redeclare package Medium = Medium,
    m_flow_nominal={mp_nom,mp_nom,mp_nom},
    dp_nominal={dp_nom,dp_nom,dp_nom}) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=-90,
        origin={10,30})));
  AixLib.Fluid.FixedResistances.Junction jun_VS_10(
    redeclare package Medium = Medium,
    m_flow_nominal={mp_nom,mp_nom,mp_nom},
    dp_nominal={dp_nom,dp_nom,dp_nom}) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=-90,
        origin={-30,170})));
  AixLib.Fluid.FixedResistances.Junction jun_VS_78(
    redeclare package Medium = Medium,
    m_flow_nominal={mp_nom,mp_nom,mp_nom},
    dp_nominal={dp_nom,dp_nom,dp_nom}) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=-90,
        origin={-8,230})));
  AixLib.Fluid.Sources.Boundary_pT mag_10(
    redeclare package Medium = Medium,
    p=150*dp_nom,
    nPorts=1) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=90,
        origin={-90,130})));
  AixLib.Fluid.FixedResistances.Junction jun_VS_17(
    redeclare package Medium = Medium,
    m_flow_nominal={mp_nom,mp_nom,mp_nom},
    dp_nominal={dp_nom,dp_nom,dp_nom}) annotation (Placement(transformation(
        extent={{-10,10},{10,-10}},
        rotation=-90,
        origin={-270,-90})));
  LibEAS.Sensors.PressureDrop_Display dp_VS_22(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom) annotation (Placement(transformation(
        extent={{10,10},{-10,-10}},
        rotation=90,
        origin={-270,-170})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VS_22(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom) annotation (
      Placement(transformation(
        extent={{10,-10},{-10,10}},
        rotation=-90,
        origin={-270,-130})));
  Modelica.Blocks.Sources.TimeTable tt_VT(table=[0.0,0.0; 2,0; 2,1; 8,1; 8,0],
      timeScale(displayUnit="h") = 3600) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=90,
        origin={510,370})));
  Modelica.Blocks.Sources.TimeTable tt_VH(table=[0.0,0.0; 9,0; 9,1; 12,1; 12,0],
      timeScale(displayUnit="h")) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=90,
        origin={590,110})));
  Modelica.Blocks.Sources.TimeTable tt_VK(table=[0.0,0.0; 13,0.0; 13,1; 20,1;
        20,0.0], timeScale(displayUnit="h") = 3600) annotation (Placement(
        transformation(
        extent={{-10,-10},{10,10}},
        rotation=90,
        origin={670,-150})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VT_7(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{80,200},{100,220}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VH_7(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{80,120},{100,140}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VK_7(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{80,40},{100,60}})));
  AixLib.Fluid.Actuators.Valves.ThreeWayLinear twv_VT(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom_KK,
    dpValve_nominal=10*dp_nom)
    annotation (Placement(transformation(extent={{420,-420},{440,-440}})));
  AixLib.Fluid.Movers.SpeedControlled_y pum_VT(redeclare package Medium =
        Medium, redeclare
      AixLib.Fluid.Movers.Data.Pumps.Wilo.Stratos25slash1to6 per)
    annotation (Placement(transformation(extent={{540,-280},{560,-260}})));
  Modelica.Blocks.Sources.RealExpression re_cv_VT(y=0.45)
    annotation (Placement(transformation(extent={{400,-480},{420,-460}})));
  LibEAS.Sensors.PressureDrop_Display dp_KK_1(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom_KK,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=0,
        origin={350,-270})));
  LibEAS.Sensors.PressureDrop_Display dp_KK_2(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom_KK,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{460,-280},{480,-260}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_KK_1(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom_KK)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=0,
        origin={390,-270})));
  AixLib.Fluid.HeatExchangers.HeaterCooler_u load_KK(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom_KK,
    dp_nominal=dp_nom,
    Q_flow_nominal=20000) "Klimakammer"
                          annotation (Placement(transformation(
        extent={{10,10},{-10,-10}},
        rotation=90,
        origin={752,-310})));
  LibEAS.Sensors.WMZ senq_KK(redeclare package Medium = Medium, m_flow_nominal=
        mp_nom_KK)
    annotation (Placement(transformation(extent={{720,-420},{700,-440}})));
  AixLib.Fluid.Sensors.EnthalpyFlowRate senh_KK(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom_KK)
    annotation (Placement(transformation(extent={{700,-260},{720,-280}})));
  AixLib.Fluid.Actuators.Valves.TwoWayLinear val_KK_3(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom_KK,
    dpValve_nominal=10*dp_nom) annotation (Placement(transformation(
        extent={{-10,10},{10,-10}},
        rotation=90,
        origin={590,-310})));
  Modelica.Blocks.Sources.RealExpression re_pum_VT1(y=0)
    annotation (Placement(transformation(extent={{640,-320},{620,-300}})));
  AixLib.Fluid.FixedResistances.Junction jun_KK_1(
    redeclare package Medium = Medium,
    m_flow_nominal={mp_nom_KK,mp_nom_KK,mp_nom_KK},
    dp_nominal={dp_nom,dp_nom,dp_nom}) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=0,
        origin={430,-270})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_KK_2(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom_KK)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=0,
        origin={510,-270})));
  AixLib.Fluid.FixedResistances.Junction jun_KK_2(
    redeclare package Medium = Medium,
    m_flow_nominal={mp_nom_KK,mp_nom_KK,mp_nom_KK},
    dp_nominal={dp_nom,dp_nom,dp_nom}) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=0,
        origin={590,-270})));
  LibEAS.Sensors.PressureDrop_Display dp_KK_4(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom_KK,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{620,-280},{640,-260}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_KK_4(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom_KK)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=0,
        origin={670,-270})));
  LibEAS.Sensors.PressureDrop_Display dp_KK_5(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom_KK,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=90,
        origin={590,-390})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_KK_5(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom_KK)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=90,
        origin={590,-350})));
  LibEAS.Sensors.PressureDrop_Display dp_KK_6(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom_KK,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{640,-440},{620,-420}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_KK_6(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom_KK)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=0,
        origin={670,-430})));
  AixLib.Fluid.FixedResistances.Junction jun_KK_6(
    redeclare package Medium = Medium,
    m_flow_nominal={mp_nom_KK,mp_nom_KK,mp_nom_KK},
    dp_nominal={dp_nom,dp_nom,dp_nom}) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=180,
        origin={590,-430})));
  LibEAS.Sensors.PressureDrop_Display dp_KK_7(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom_KK,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{480,-440},{460,-420}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_KK_7(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom_KK)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=0,
        origin={510,-430})));
  LibEAS.Sensors.PressureDrop_Display dp_KK_3(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom_KK,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=90,
        origin={430,-390})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_KK_3(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom_KK)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=90,
        origin={430,-350})));
  LibEAS.Sensors.PressureDrop_Display dp_KK_8(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom_KK,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{10,-10},{-10,10}},
        rotation=0,
        origin={350,-432})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_KK_8(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom_KK)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=0,
        origin={390,-432})));
  Modelica.Blocks.Sources.TimeTable tt_KK(table=[0.0,0.0; 21,0.0; 21,1; 50,1;
        50,0], timeScale(displayUnit="h") = 3600) annotation (Placement(
        transformation(
        extent={{-10,-10},{10,10}},
        rotation=0,
        origin={510,-230})));
  AixLib.Fluid.Sources.Boundary_pT      bou_EH_1(
    redeclare package Medium = Medium,
    p=dp_nom*200,
    T=338.15,
    nPorts=1)
    annotation (Placement(transformation(extent={{-580,560},{-560,580}})));
  AixLib.Fluid.Actuators.Valves.ThreeWayLinear twv_EH(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dpValve_nominal=10*dp_nom)
    annotation (Placement(transformation(extent={{-460,560},{-440,580}})));
  AixLib.Fluid.Movers.SpeedControlled_y pum_EH(redeclare package Medium =
        Medium, redeclare
      AixLib.Fluid.Movers.Data.Pumps.Wilo.Stratos25slash1to8 per)
    annotation (Placement(transformation(extent={{-380,560},{-360,580}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_EH_1(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom) "XLW10BTR01"
    annotation (Placement(transformation(extent={{-340,560},{-320,580}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_EH_3(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom) "XLW10BTR11"
    annotation (Placement(transformation(extent={{-320,400},{-340,420}})));
  AixLib.Fluid.FixedResistances.Junction jun_EH(
    redeclare package Medium = Medium,
    m_flow_nominal={mp_nom,mp_nom,mp_nom},
    dp_nominal={dp_nom,dp_nom,dp_nom}) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=180,
        origin={-450,410})));
  AixLib.Fluid.Sources.Boundary_pT bou_EH_5(
    redeclare package Medium = Medium,
    p=150*dp_nom,
    nPorts=1)
    annotation (Placement(transformation(extent={{-580,400},{-560,420}})));
  Modelica.Blocks.Sources.RealExpression re_twv_EH(y=0.9)
    annotation (Placement(transformation(extent={{-480,600},{-460,620}})));
  AixLib.Fluid.Sensors.Pressure senp_EH_5(redeclare package Medium = Medium)
    "XLW10BPR11"
    annotation (Placement(transformation(extent={{-560,420},{-540,440}})));
  Modelica.Blocks.Sources.RealExpression re_pum_EH2(y=1)
    annotation (Placement(transformation(extent={{-400,600},{-380,620}})));
  LibEAS.Sensors.PressureDrop_Display dp_EH_1(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{-540,560},{-520,580}})));
  LibEAS.Sensors.PressureDrop_Display dp_EH_5(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{-520,400},{-540,420}})));
  LibEAS.Sensors.PressureDrop_Display dp_EH_2(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{-360,400},{-380,420}})));
  LibEAS.Sensors.PressureDrop_Display dp_EH_3(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{-420,560},{-400,580}})));
  LibEAS.Sensors.PressureDrop_Display dp_EH_6(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom) annotation (Placement(transformation(
        extent={{10,10},{-10,-10}},
        rotation=-90,
        origin={-450,530})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_EH_5(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{-500,560},{-480,580}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_EH_6(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{-480,400},{-500,420}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_EH_7(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom) annotation (Placement(transformation(
        extent={{10,10},{-10,-10}},
        rotation=-90,
        origin={-450,490})));
  AixLib.Fluid.FixedResistances.CheckValve cv_EH(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dpValve_nominal=dp_nom) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=90,
        origin={-450,450})));
  AixLib.Fluid.Sources.Boundary_pT      bou_EK_1(
    redeclare package Medium = Medium,
    p=dp_nom*200,
    T=285.15,
    nPorts=1)
    annotation (Placement(transformation(extent={{-580,-220},{-560,-200}})));
  AixLib.Fluid.Actuators.Valves.ThreeWayLinear twv_EK(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dpValve_nominal=10*dp_nom)
    annotation (Placement(transformation(extent={{-460,-220},{-440,-200}})));
  AixLib.Fluid.Movers.SpeedControlled_y pum_EK(redeclare package Medium =
        Medium, redeclare
      AixLib.Fluid.Movers.Data.Pumps.Wilo.Stratos25slash1to8 per)
    annotation (Placement(transformation(extent={{-380,-220},{-360,-200}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_EK_1(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{-340,-220},{-320,-200}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_EK_3(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{-320,-380},{-340,-360}})));
  AixLib.Fluid.FixedResistances.Junction jun_EK(
    redeclare package Medium = Medium,
    m_flow_nominal={mp_nom,mp_nom,mp_nom},
    dp_nominal={dp_nom,dp_nom,dp_nom}) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=180,
        origin={-450,-370})));
  AixLib.Fluid.Sources.Boundary_pT bou_EK_5(
    redeclare package Medium = Medium,
    p=150*dp_nom,
    nPorts=1)
    annotation (Placement(transformation(extent={{-580,-380},{-560,-360}})));
  Modelica.Blocks.Sources.RealExpression re_twv_EK(y=0.9)
    annotation (Placement(transformation(extent={{-480,-180},{-460,-160}})));
  AixLib.Fluid.Sensors.Pressure senp_EK_5(redeclare package Medium = Medium)
    annotation (Placement(transformation(extent={{-560,-360},{-540,-340}})));
  Modelica.Blocks.Sources.RealExpression re_pum_EK(y=1)
    annotation (Placement(transformation(extent={{-400,-180},{-380,-160}})));
  LibEAS.Sensors.PressureDrop_Display dp_EK_1(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{-540,-220},{-520,-200}})));
  LibEAS.Sensors.PressureDrop_Display dp_EK_5(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{-520,-380},{-540,-360}})));
  LibEAS.Sensors.PressureDrop_Display dp_EK_2(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{-360,-380},{-380,-360}})));
  LibEAS.Sensors.PressureDrop_Display dp_EK_3(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{-420,-220},{-400,-200}})));
  LibEAS.Sensors.PressureDrop_Display dp_EK_6(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom) annotation (Placement(transformation(
        extent={{10,10},{-10,-10}},
        rotation=-90,
        origin={-450,-250})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_EK_5(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{-500,-220},{-480,-200}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_EK_6(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{-480,-380},{-500,-360}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_EK_7(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom) annotation (Placement(transformation(
        extent={{10,10},{-10,-10}},
        rotation=-90,
        origin={-450,-290})));
  AixLib.Fluid.FixedResistances.CheckValve cv_EK(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dpValve_nominal=dp_nom) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=90,
        origin={-450,-330})));
  parameter Modelica.Units.SI.MassFlowRate mp_nom_KK=0.7
    "Nominal mass flow rate";
  AixLib.Fluid.Actuators.Valves.ThreeWayLinear twv_VT1(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dpValve_nominal=10*dp_nom)
    annotation (Placement(transformation(extent={{260,440},{280,420}})));
  AixLib.Fluid.Movers.SpeedControlled_y pum_VT1(redeclare package Medium =
        Medium, redeclare
      AixLib.Fluid.Movers.Data.Pumps.Wilo.Stratos25slash1to4 per)
    annotation (Placement(transformation(extent={{340,440},{360,420}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VT_2(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{380,420},{400,440}})));
  Modelica.Blocks.Sources.RealExpression re_cv_VT1(y=0.95)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=90,
        origin={290,390})));
  LibEAS.Sensors.PressureDrop_Display dp_VT_1(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=90,
        origin={250,370})));
  LibEAS.Sensors.PressureDrop_Display dp_VT_2(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{300,420},{320,440}})));
  LibEAS.Sensors.PressureDrop_Display dp_VT_3(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom) annotation (Placement(transformation(
        extent={{10,10},{-10,-10}},
        rotation=90,
        origin={250,510})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VT_1(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=90,
        origin={250,410})));
  AixLib.Fluid.FixedResistances.CheckValve cv_VT_1(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dpValve_nominal=dp_nom) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=90,
        origin={250,330})));
  AixLib.Fluid.Sensors.Pressure senp_VT_2(redeclare package Medium = Medium)
    annotation (Placement(transformation(extent={{420,440},{440,460}})));
  LibEAS.Sensors.PressureDrop_Display dp_VT_4(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{500,420},{520,440}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VT_4(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{540,420},{560,440}})));
  AixLib.Fluid.HeatExchangers.HeaterCooler_u load_VT(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom,
    Q_flow_nominal=20000) annotation (Placement(transformation(
        extent={{-10,10},{10,-10}},
        rotation=90,
        origin={630,470})));
  LibEAS.Sensors.WMZ senq_VT(redeclare package Medium = Medium, m_flow_nominal=
        mp_nom)
    annotation (Placement(transformation(extent={{600,560},{580,580}})));
  AixLib.Fluid.Sensors.EnthalpyFlowRate senh_VT(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{580,420},{600,440}})));
  LibEAS.Sensors.PressureDrop_Display dp_VT_6(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{520,560},{500,580}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VT_6(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{560,560},{540,580}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VT_3(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{400,560},{380,580}})));
  AixLib.Fluid.FixedResistances.CheckValve cv_VT_3(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dpValve_nominal=dp_nom) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=-90,
        origin={250,550})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VT_5(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=-90,
        origin={250,470})));
  LibEAS.Sensors.PressureDrop_Display dp_VT_7(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{440,560},{420,580}})));
  LibEAS.Sensors.PressureDrop_Display dp_VT_5(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=0.01*dp_nom)
    annotation (Placement(transformation(extent={{10,-10},{-10,10}},
        rotation=-90,
        origin={470,450})));
  AixLib.Fluid.Actuators.Valves.TwoWayLinear val_VT_5(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dpValve_nominal=10*dp_nom) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=90,
        origin={470,490})));
  Modelica.Blocks.Sources.RealExpression re_pum_VT2(y=0)
    annotation (Placement(transformation(extent={{420,480},{440,500}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VT_8(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{10,10},{-10,-10}},
        rotation=-90,
        origin={470,530})));
  AixLib.Fluid.Actuators.Valves.ThreeWayLinear twv_VH(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dpValve_nominal=10*dp_nom)
    annotation (Placement(transformation(extent={{340,180},{360,160}})));
  AixLib.Fluid.Movers.SpeedControlled_y pum_VH(redeclare package Medium =
        Medium, redeclare
      AixLib.Fluid.Movers.Data.Pumps.Wilo.Stratos25slash1to4 per)
    annotation (Placement(transformation(extent={{420,180},{440,160}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VH_2(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{460,160},{480,180}})));
  Modelica.Blocks.Sources.RealExpression re_cv_VH(y=0.7)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=90,
        origin={370,130})));
  LibEAS.Sensors.PressureDrop_Display dp_VH_1(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=90,
        origin={330,110})));
  LibEAS.Sensors.PressureDrop_Display dp_VH_2(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{380,160},{400,180}})));
  LibEAS.Sensors.PressureDrop_Display dp_VH_3(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom) annotation (Placement(transformation(
        extent={{10,10},{-10,-10}},
        rotation=90,
        origin={330,250})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VH_1(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=90,
        origin={330,150})));
  AixLib.Fluid.FixedResistances.CheckValve cv_VH_1(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dpValve_nominal=dp_nom) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=90,
        origin={330,70})));
  AixLib.Fluid.Sensors.Pressure senp_VH_2(redeclare package Medium = Medium)
    annotation (Placement(transformation(extent={{500,180},{520,200}})));
  LibEAS.Sensors.PressureDrop_Display dp_VH_4(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{580,160},{600,180}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VH_4(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{620,160},{640,180}})));
  AixLib.Fluid.HeatExchangers.HeaterCooler_u load_VH(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom,
    Q_flow_nominal=20000) annotation (Placement(transformation(
        extent={{-10,10},{10,-10}},
        rotation=90,
        origin={710,210})));
  LibEAS.Sensors.WMZ senq_VH(redeclare package Medium = Medium, m_flow_nominal=
        mp_nom)
    annotation (Placement(transformation(extent={{680,300},{660,320}})));
  AixLib.Fluid.Sensors.EnthalpyFlowRate senh_VH(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{660,160},{680,180}})));
  LibEAS.Sensors.PressureDrop_Display dp_VH_6(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{600,300},{580,320}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VH_6(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{640,300},{620,320}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VH_3(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{480,300},{460,320}})));
  AixLib.Fluid.FixedResistances.CheckValve cv_VH_3(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dpValve_nominal=dp_nom) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=-90,
        origin={330,290})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VH_5(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=-90,
        origin={330,210})));
  LibEAS.Sensors.PressureDrop_Display dp_VH_7(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{520,300},{500,320}})));
  LibEAS.Sensors.PressureDrop_Display dp_VH_5(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=0.01*dp_nom)
    annotation (Placement(transformation(extent={{10,-10},{-10,10}},
        rotation=-90,
        origin={550,190})));
  AixLib.Fluid.Actuators.Valves.TwoWayLinear val_VH_5(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dpValve_nominal=10*dp_nom) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=90,
        origin={550,230})));
  Modelica.Blocks.Sources.RealExpression re_pum_VH1(y=0)
    annotation (Placement(transformation(extent={{500,220},{520,240}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VH_8(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{10,10},{-10,-10}},
        rotation=-90,
        origin={550,270})));
  AixLib.Fluid.Actuators.Valves.ThreeWayLinear twv_VK(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dpValve_nominal=10*dp_nom)
    annotation (Placement(transformation(extent={{420,-80},{440,-100}})));
  AixLib.Fluid.Movers.SpeedControlled_y pum_VK(redeclare package Medium =
        Medium, redeclare
      AixLib.Fluid.Movers.Data.Pumps.Wilo.Stratos25slash1to4 per)
    annotation (Placement(transformation(extent={{500,-80},{520,-100}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VK_2(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{540,-100},{560,-80}})));
  Modelica.Blocks.Sources.RealExpression re_cv_VK(y=0.85)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=90,
        origin={450,-130})));
  LibEAS.Sensors.PressureDrop_Display dp_VK_1(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=90,
        origin={410,-150})));
  LibEAS.Sensors.PressureDrop_Display dp_VK_2(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{460,-100},{480,-80}})));
  LibEAS.Sensors.PressureDrop_Display dp_VK_3(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom) annotation (Placement(transformation(
        extent={{10,10},{-10,-10}},
        rotation=90,
        origin={410,-10})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VK_1(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=90,
        origin={410,-110})));
  AixLib.Fluid.FixedResistances.CheckValve cv_VK_1(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dpValve_nominal=dp_nom) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=90,
        origin={410,-190})));
  AixLib.Fluid.Sensors.Pressure senp_VK_2(redeclare package Medium = Medium)
    annotation (Placement(transformation(extent={{580,-80},{600,-60}})));
  LibEAS.Sensors.PressureDrop_Display dp_VK_4(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{660,-100},{680,-80}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VK_4(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{700,-100},{720,-80}})));
  AixLib.Fluid.HeatExchangers.HeaterCooler_u load_VK(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom,
    Q_flow_nominal=-15000)
                          annotation (Placement(transformation(
        extent={{-10,10},{10,-10}},
        rotation=90,
        origin={790,-50})));
  LibEAS.Sensors.WMZ senq_VK(redeclare package Medium = Medium, m_flow_nominal=
        mp_nom)
    annotation (Placement(transformation(extent={{760,40},{740,60}})));
  AixLib.Fluid.Sensors.EnthalpyFlowRate senh_VK(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{740,-100},{760,-80}})));
  LibEAS.Sensors.PressureDrop_Display dp_VK_6(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{680,40},{660,60}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VK_6(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{720,40},{700,60}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VK_3(
      redeclare package Medium = Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{560,40},{540,60}})));
  AixLib.Fluid.FixedResistances.CheckValve cv_VK_3(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dpValve_nominal=dp_nom) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=-90,
        origin={410,30})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VK_5(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{-10,-10},{10,10}},
        rotation=-90,
        origin={410,-50})));
  LibEAS.Sensors.PressureDrop_Display dp_VK_7(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=dp_nom)
    annotation (Placement(transformation(extent={{600,40},{580,60}})));
  LibEAS.Sensors.PressureDrop_Display dp_VK_5(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dp_nominal=0.01*dp_nom)
    annotation (Placement(transformation(extent={{10,-10},{-10,10}},
        rotation=-90,
        origin={630,-70})));
  AixLib.Fluid.Actuators.Valves.TwoWayLinear val_VK_5(
    redeclare package Medium = Medium,
    m_flow_nominal=mp_nom,
    dpValve_nominal=10*dp_nom) annotation (Placement(transformation(
        extent={{-10,-10},{10,10}},
        rotation=90,
        origin={630,-30})));
  Modelica.Blocks.Sources.RealExpression re_pum_VK1(y=0)
    annotation (Placement(transformation(extent={{580,-40},{600,-20}})));
  LibEAS.Sensors.TemperatureTwoPort_Display senT_VK_8(redeclare package Medium =
        Medium, m_flow_nominal=mp_nom)
    annotation (Placement(transformation(extent={{10,10},{-10,-10}},
        rotation=-90,
        origin={630,10})));
equation
  connect(re_pum_EH.y, pum_VS_H.y)
    annotation (Line(points={{-59,570},{-59,542},{-50,542}}, color={0,0,127}));
  connect(hex_EH.port_b2, dp_VS_1.port_a) annotation (Line(points={{-284,460},{
          -284,530},{-220,530}}, color={0,127,255}));
  connect(dp_VS_1.port_b, senT_VS_1.port_b)
    annotation (Line(points={{-200,530},{-160,530}}, color={0,127,255}));
  connect(senT_VS_1.port_a, pum_VS_H.port_a)
    annotation (Line(points={{-140,530},{-60,530}}, color={0,127,255}));
  connect(pum_VS_H.port_b, jun_VS_1.port_2) annotation (Line(points={{-40,530},
          {-1.77636e-15,530}},                     color={0,127,255}));
  connect(jun_VS_1.port_1, dp_VS_2.port_a)
    annotation (Line(points={{20,530},{40,530}}, color={0,127,255}));
  connect(dp_VS_2.port_b, senT_VS_2.port_b)
    annotation (Line(points={{60,530},{80,530}}, color={0,127,255}));
  connect(senT_VS_3.port_a, jun_VS_4_H.port_1)
    annotation (Line(points={{100,610},{240,610}}, color={0,127,255}));
  connect(jun_VS_4_H.port_2, jun_VS_5_H.port_1)
    annotation (Line(points={{260,610},{320,610}}, color={0,127,255}));
  connect(jun_VS_1.port_3, dp_VS_3.port_a)
    annotation (Line(points={{10,540},{10,610},{40,610}},  color={0,127,255}));
  connect(dp_VS_3.port_b, senT_VS_3.port_b)
    annotation (Line(points={{60,610},{80,610}},   color={0,127,255}));
  connect(senT_VS_2.port_a, tan_WV.fluPorVol[1]) annotation (Line(points={{100,530},
          {100,504},{127.2,504},{127.2,476.2}},    color={0,127,255}));
  connect(tan_WV.fluPorVol[10], senT_VS_7.port_b) annotation (Line(points={{127.2,
          476.2},{114,476.2},{114,450},{100,450}},   color={0,127,255}));
  connect(hex_EH.port_a2, dp_VS_11.port_b) annotation (Line(points={{-284,440},
          {-284,425},{-280,425},{-280,410}},
                               color={0,127,255}));
  connect(dp_VS_11.port_a, senT_VS_11.port_b)
    annotation (Line(points={{-260,410},{-198,410}},
                                                   color={0,127,255}));
  connect(tan_S.fluPorVol[1], senT_VS_11.port_a) annotation (Line(points={{-152.8,
          116.2},{-170,116.2},{-170,410},{-178,410}},      color={0,127,255}));
  connect(dp_VS_17.port_a, senT_VS_17.port_b)
    annotation (Line(points={{-220,-30},{-200,-30}}, color={0,127,255}));
  connect(tan_S.fluPorVol[10], senT_VS_17.port_a) annotation (Line(points={{-152.8,
          116.2},{-162,116.2},{-162,116},{-170,116},{-170,-30},{-180,-30}},
                                                  color={0,127,255}));
  connect(senp_VS_11.port, senT_VS_11.port_b) annotation (Line(points={{-230,
          420},{-224,420},{-224,410},{-198,410}},
                                          color={0,127,255}));
  connect(re_pum_EH1.y, pum_VS_K.y) annotation (Line(points={{-39,-170},{-39,
          -198},{-30,-198}}, color={0,0,127}));
  connect(dp_VS_16.port_b, senT_VS_16.port_b)
    annotation (Line(points={{-200,-270},{-180,-270}}, color={0,127,255}));
  connect(senp_VS_16.port, senT_VS_16.port_a) annotation (Line(points={{-130,
          -262},{-130,-270},{-160,-270}}, color={0,127,255}));
  connect(senp_VS_16.port, jun_VS_5_H1.port_1) annotation (Line(points={{-130,
          -262},{-130,-270},{-100,-270}}, color={0,127,255}));
  connect(hex_EK.port_b2, dp_VS_16.port_a) annotation (Line(points={{-284,-240},
          {-284,-270},{-220,-270}}, color={0,127,255}));
  connect(jun_VS_5_H1.port_3, senT_VS_15.port_b) annotation (Line(points={{-90,
          -260},{-92,-260},{-92,-210},{-80,-210}}, color={0,127,255}));
  connect(senT_VS_15.port_a, pum_VS_K.port_a)
    annotation (Line(points={{-60,-210},{-40,-210}}, color={0,127,255}));
  connect(pum_VS_K.port_b, dp_VS_15.port_a)
    annotation (Line(points={{-20,-210},{40,-210}}, color={0,127,255}));
  connect(dp_VS_15.port_b, tan_KV.fluPorVol[10]) annotation (Line(points={{60,
          -210},{60,-212},{108,-212},{108,-183.8},{127.2,-183.8}}, color={0,127,
          255}));
  connect(jun_VS_18.port_2, jun_VS_19.port_1)
    annotation (Line(points={{260,-210},{320,-210}}, color={0,127,255}));
  connect(tan_KV.fluPorVolSec[10], dp_VS_21.port_a) annotation (Line(points={{
          132.8,-183.8},{152,-183.8},{152,-210},{160,-210}}, color={0,127,255}));
  connect(dp_VS_21.port_b, senT_VS_21.port_b)
    annotation (Line(points={{180,-210},{202,-210}}, color={0,127,255}));
  connect(senT_VS_21.port_a, jun_VS_18.port_1)
    annotation (Line(points={{222,-210},{240,-210}}, color={0,127,255}));
  connect(senT_VS_7.port_a, dp_VS_7.port_a)
    annotation (Line(points={{80,450},{60,450}},
                                               color={0,127,255}));
  connect(dp_VS_10.port_b, senT_VS_10.port_a)
    annotation (Line(points={{-80,170},{-100,170}},
                                                  color={0,127,255}));
  connect(senT_VS_10.port_b, tan_S.fluPorVolSec[1]) annotation (Line(points={{-120,
          170},{-147.2,170},{-147.2,116.2}},               color={0,127,255}));
  connect(dp_VS_14.port_b, senT_VS_14.port_a)
    annotation (Line(points={{-40,30},{-100,30}},     color={0,127,255}));
  connect(tan_S.fluPorVolSec[10], senT_VS_14.port_b) annotation (Line(points={{-147.2,
          116.2},{-128,116.2},{-128,30},{-120,30}},                       color
        ={0,127,255}));
  connect(senp_VS_14.port, dp_VS_14.port_b) annotation (Line(points={{-70,40},{
          -70,30},{-40,30}},      color={0,127,255}));
  connect(tan_KV.fluPorVol[1], senT_VS_13.port_b) annotation (Line(points={{
          127.2,-183.8},{108,-183.8},{108,-130},{100,-130}}, color={0,127,255}));
  connect(senT_VS_13.port_a, dp_VS_13.port_a)
    annotation (Line(points={{80,-130},{60,-130}}, color={0,127,255}));
  connect(dp_VS_12.port_b, jun_VS_14.port_1)
    annotation (Line(points={{40,50},{10,50},{10,40}},     color={0,127,255}));
  connect(dp_VS_13.port_b, jun_VS_14.port_2) annotation (Line(points={{40,-130},
          {10,-130},{10,20}},   color={0,127,255}));
  connect(jun_VS_14.port_3, dp_VS_14.port_a) annotation (Line(points={{
          1.77636e-15,30},{-20,30}},     color={0,127,255}));
  connect(dp_VS_10.port_a, jun_VS_10.port_3)
    annotation (Line(points={{-60,170},{-40,170}},
                                                 color={0,127,255}));
  connect(jun_VS_78.port_3, jun_VS_10.port_1)
    annotation (Line(points={{-18,230},{-30,230},{-30,180}},
                                                          color={0,127,255}));
  connect(dp_VS_9.port_b, jun_VS_10.port_2) annotation (Line(points={{40,130},{
          -30,130},{-30,160}},         color={0,127,255}));
  connect(dp_VS_7.port_b, jun_VS_78.port_1)
    annotation (Line(points={{40,450},{-8,450},{-8,240}},
                                                       color={0,127,255}));
  connect(dp_VS_8.port_b, jun_VS_78.port_2)
    annotation (Line(points={{40,210},{-8,210},{-8,220}},
                                                       color={0,127,255}));
  connect(mag_10.ports[1], dp_VS_10.port_b)
    annotation (Line(points={{-90,140},{-90,170},{-80,170}},
                                                         color={0,127,255}));
  connect(jun_VS_17.port_1, dp_VS_17.port_b) annotation (Line(points={{-270,-80},
          {-272,-80},{-272,-30},{-240,-30}}, color={0,127,255}));
  connect(senp_VS_17.port, dp_VS_17.port_b) annotation (Line(points={{-280,-50},
          {-280,-52},{-272,-52},{-272,-30},{-240,-30}}, color={0,127,255}));
  connect(jun_VS_17.port_2, senT_VS_22.port_b)
    annotation (Line(points={{-270,-100},{-270,-120}}, color={0,127,255}));
  connect(senT_VS_22.port_a, dp_VS_22.port_a)
    annotation (Line(points={{-270,-140},{-270,-160}}, color={0,127,255}));
  connect(dp_VS_22.port_b, hex_EK.port_a2) annotation (Line(points={{-270,-180},
          {-276,-180},{-276,-220},{-284,-220}}, color={0,127,255}));
  connect(senT_VT_7.port_a, dp_VS_8.port_a)
    annotation (Line(points={{80,210},{60,210}},
                                               color={0,127,255}));
  connect(senT_VH_7.port_a, dp_VS_9.port_a)
    annotation (Line(points={{80,130},{60,130}}, color={0,127,255}));
  connect(senT_VK_7.port_a, dp_VS_12.port_a)
    annotation (Line(points={{80,50},{60,50}},   color={0,127,255}));
  connect(re_cv_VT.y,twv_VT. y)
    annotation (Line(points={{421,-470},{421,-442},{430,-442}},
                                                            color={0,0,127}));
  connect(senh_KK.port_b,load_KK. port_a)
    annotation (Line(points={{720,-270},{720,-272},{752,-272},{752,-300}},
                                                           color={0,127,255}));
  connect(load_KK.port_b,senq_KK. port_a) annotation (Line(points={{752,-320},{
          752,-429.825},{720.175,-429.825}},
                                           color={0,127,255}));
  connect(senh_KK.H_flow,senq_KK. u2) annotation (Line(points={{710,-281},{710,
          -284},{709.925,-284},{709.925,-420.1}},    color={0,0,127}));
  connect(dp_KK_1.port_b,senT_KK_1. port_a)
    annotation (Line(points={{360,-270},{380,-270}},
                                                   color={0,127,255}));
  connect(senT_KK_1.port_b, jun_KK_1.port_1)
    annotation (Line(points={{400,-270},{420,-270}}, color={0,127,255}));
  connect(jun_KK_1.port_2, dp_KK_2.port_a)
    annotation (Line(points={{440,-270},{460,-270}}, color={0,127,255}));
  connect(dp_KK_2.port_b, senT_KK_2.port_a)
    annotation (Line(points={{480,-270},{500,-270}}, color={0,127,255}));
  connect(re_pum_VT1.y, val_KK_3.y)
    annotation (Line(points={{619,-310},{602,-310}}, color={0,0,127}));
  connect(val_KK_3.port_b, jun_KK_2.port_3)
    annotation (Line(points={{590,-300},{590,-280}}, color={0,127,255}));
  connect(dp_KK_4.port_b, senT_KK_4.port_a)
    annotation (Line(points={{640,-270},{660,-270}}, color={0,127,255}));
  connect(jun_KK_2.port_2, dp_KK_4.port_a)
    annotation (Line(points={{600,-270},{620,-270}}, color={0,127,255}));
  connect(pum_VT.port_b, jun_KK_2.port_1)
    annotation (Line(points={{560,-270},{580,-270}}, color={0,127,255}));
  connect(senT_KK_2.port_b, pum_VT.port_a)
    annotation (Line(points={{520,-270},{540,-270}}, color={0,127,255}));
  connect(senT_KK_5.port_b, val_KK_3.port_a)
    annotation (Line(points={{590,-340},{590,-320}}, color={0,127,255}));
  connect(dp_KK_5.port_b, senT_KK_5.port_a)
    annotation (Line(points={{590,-380},{590,-360}}, color={0,127,255}));
  connect(senq_KK.port_b, senT_KK_6.port_b) annotation (Line(points={{699.775,
          -429.875},{689.888,-429.875},{689.888,-430},{680,-430}}, color={0,127,
          255}));
  connect(senT_KK_4.port_b, senh_KK.port_a)
    annotation (Line(points={{680,-270},{700,-270}}, color={0,127,255}));
  connect(senT_KK_6.port_a, dp_KK_6.port_a)
    annotation (Line(points={{660,-430},{640,-430}}, color={0,127,255}));
  connect(dp_KK_6.port_b, jun_KK_6.port_1)
    annotation (Line(points={{620,-430},{600,-430}}, color={0,127,255}));
  connect(jun_KK_6.port_3, dp_KK_5.port_a)
    annotation (Line(points={{590,-420},{590,-400}}, color={0,127,255}));
  connect(senT_KK_7.port_a, dp_KK_7.port_a)
    annotation (Line(points={{500,-430},{480,-430}}, color={0,127,255}));
  connect(jun_KK_6.port_2, senT_KK_7.port_b)
    annotation (Line(points={{580,-430},{520,-430}}, color={0,127,255}));
  connect(dp_KK_7.port_b, twv_VT.port_2)
    annotation (Line(points={{460,-430},{440,-430}}, color={0,127,255}));
  connect(dp_KK_3.port_b, senT_KK_3.port_a)
    annotation (Line(points={{430,-380},{430,-360}}, color={0,127,255}));
  connect(twv_VT.port_3, dp_KK_3.port_a)
    annotation (Line(points={{430,-420},{430,-400}}, color={0,127,255}));
  connect(senT_KK_3.port_b, jun_KK_1.port_3)
    annotation (Line(points={{430,-340},{430,-280}}, color={0,127,255}));
  connect(twv_VT.port_1, senT_KK_8.port_b) annotation (Line(points={{420,-430},
          {420,-432},{400,-432}}, color={0,127,255}));
  connect(senT_KK_8.port_a, dp_KK_8.port_a)
    annotation (Line(points={{380,-432},{360,-432}}, color={0,127,255}));
  connect(jun_VS_5_H1.port_2, dp_KK_1.port_a)
    annotation (Line(points={{-80,-270},{340,-270}}, color={0,127,255}));
  connect(dp_KK_8.port_b, jun_VS_17.port_3) annotation (Line(points={{340,-432},
          {-244,-432},{-244,-90},{-260,-90}}, color={0,127,255}));
  connect(tt_KK.y, pum_VT.y) annotation (Line(points={{521,-230},{521,-232},{
          550,-232},{550,-258}}, color={0,0,127}));
  connect(tt_KK.y, load_KK.u) annotation (Line(points={{521,-230},{521,-232},{
          756,-232},{756,-266},{758,-266},{758,-298}}, color={0,0,127}));
  connect(pum_EH.port_b,senT_EH_1. port_a)
    annotation (Line(points={{-360,570},{-340,570}},
                                              color={0,127,255}));
  connect(re_twv_EH.y, twv_EH.y) annotation (Line(points={{-459,610},{-459,582},
          {-450,582}}, color={0,0,127}));
  connect(bou_EH_1.ports[1],dp_EH_1. port_a)
    annotation (Line(points={{-560,570},{-540,570}},
                                                   color={0,127,255}));
  connect(dp_EH_5.port_b,bou_EH_5. ports[1])
    annotation (Line(points={{-540,410},{-560,410}}, color={0,127,255}));
  connect(senp_EH_5.port,dp_EH_5. port_b) annotation (Line(points={{-550,420},{
          -550,410},{-540,410}},            color={0,127,255}));
  connect(senT_EH_3.port_b,dp_EH_2. port_a)
    annotation (Line(points={{-340,410},{-360,410}},
                                                color={0,127,255}));
  connect(dp_EH_2.port_b,jun_EH. port_1)
    annotation (Line(points={{-380,410},{-440,410}},
                                                   color={0,127,255}));
  connect(twv_EH.port_2,dp_EH_3. port_a)
    annotation (Line(points={{-440,570},{-420,570}},
                                                 color={0,127,255}));
  connect(dp_EH_3.port_b,pum_EH. port_a)
    annotation (Line(points={{-400,570},{-380,570}},
                                                 color={0,127,255}));
  connect(dp_EH_1.port_b,senT_EH_5. port_a)
    annotation (Line(points={{-520,570},{-500,570}},
                                                   color={0,127,255}));
  connect(senT_EH_5.port_b,twv_EH. port_1)
    annotation (Line(points={{-480,570},{-460,570}},
                                                   color={0,127,255}));
  connect(jun_EH.port_2,senT_EH_6. port_a)
    annotation (Line(points={{-460,410},{-480,410}}, color={0,127,255}));
  connect(senT_EH_6.port_b,dp_EH_5. port_a)
    annotation (Line(points={{-500,410},{-520,410}}, color={0,127,255}));
  connect(senT_EH_7.port_b,dp_EH_6. port_a)
    annotation (Line(points={{-450,500},{-450,520}},      color={0,127,255}));
  connect(dp_EH_6.port_b,twv_EH. port_3)
    annotation (Line(points={{-450,540},{-450,560}},
                                                 color={0,127,255}));
  connect(senT_EH_7.port_a,cv_EH. port_b)
    annotation (Line(points={{-450,480},{-450,460}},
                                                   color={0,127,255}));
  connect(cv_EH.port_a,jun_EH. port_3)
    annotation (Line(points={{-450,440},{-450,420}},
                                                   color={0,127,255}));
  connect(re_pum_EH2.y, pum_EH.y) annotation (Line(points={{-379,610},{-379,582},
          {-370,582}}, color={0,0,127}));
  connect(senT_EH_1.port_b, hex_EH.port_a1) annotation (Line(points={{-320,570},
          {-296,570},{-296,460}},color={0,127,255}));
  connect(senT_EH_3.port_a, hex_EH.port_b1) annotation (Line(points={{-320,410},
          {-312,410},{-312,440},{-296,440}},
                                color={0,127,255}));
  connect(pum_EK.port_b,senT_EK_1. port_a)
    annotation (Line(points={{-360,-210},{-340,-210}},
                                              color={0,127,255}));
  connect(re_twv_EK.y, twv_EK.y) annotation (Line(points={{-459,-170},{-459,-198},
          {-450,-198}}, color={0,0,127}));
  connect(bou_EK_1.ports[1],dp_EK_1. port_a)
    annotation (Line(points={{-560,-210},{-540,-210}},
                                                   color={0,127,255}));
  connect(dp_EK_5.port_b,bou_EK_5. ports[1])
    annotation (Line(points={{-540,-370},{-560,-370}},
                                                     color={0,127,255}));
  connect(senp_EK_5.port,dp_EK_5. port_b) annotation (Line(points={{-550,-360},
          {-550,-370},{-540,-370}},         color={0,127,255}));
  connect(senT_EK_3.port_b,dp_EK_2. port_a)
    annotation (Line(points={{-340,-370},{-360,-370}},
                                                color={0,127,255}));
  connect(dp_EK_2.port_b,jun_EK. port_1)
    annotation (Line(points={{-380,-370},{-440,-370}},
                                                   color={0,127,255}));
  connect(twv_EK.port_2,dp_EK_3. port_a)
    annotation (Line(points={{-440,-210},{-420,-210}},
                                                 color={0,127,255}));
  connect(dp_EK_3.port_b,pum_EK. port_a)
    annotation (Line(points={{-400,-210},{-380,-210}},
                                                 color={0,127,255}));
  connect(dp_EK_1.port_b,senT_EK_5. port_a)
    annotation (Line(points={{-520,-210},{-500,-210}},
                                                   color={0,127,255}));
  connect(senT_EK_5.port_b,twv_EK. port_1)
    annotation (Line(points={{-480,-210},{-460,-210}},
                                                   color={0,127,255}));
  connect(jun_EK.port_2,senT_EK_6. port_a)
    annotation (Line(points={{-460,-370},{-480,-370}},
                                                     color={0,127,255}));
  connect(senT_EK_6.port_b,dp_EK_5. port_a)
    annotation (Line(points={{-500,-370},{-520,-370}},
                                                     color={0,127,255}));
  connect(senT_EK_7.port_b,dp_EK_6. port_a)
    annotation (Line(points={{-450,-280},{-450,-260}},    color={0,127,255}));
  connect(dp_EK_6.port_b,twv_EK. port_3)
    annotation (Line(points={{-450,-240},{-450,-220}},
                                                 color={0,127,255}));
  connect(senT_EK_7.port_a,cv_EK. port_b)
    annotation (Line(points={{-450,-300},{-450,-320}},
                                                   color={0,127,255}));
  connect(cv_EK.port_a,jun_EK. port_3)
    annotation (Line(points={{-450,-340},{-450,-360}},
                                                   color={0,127,255}));
  connect(re_pum_EK.y,pum_EK. y)
    annotation (Line(points={{-379,-170},{-379,-198},{-370,-198}},
                                                            color={0,0,127}));
  connect(senT_EK_1.port_b, hex_EK.port_b1) annotation (Line(points={{-320,-210},
          {-296,-210},{-296,-220}}, color={0,127,255}));
  connect(senT_EK_3.port_a, hex_EK.port_a1) annotation (Line(points={{-320,-370},
          {-320,-372},{-296,-372},{-296,-240}}, color={0,127,255}));
  connect(pum_VT1.port_b, senT_VT_2.port_a)
    annotation (Line(points={{360,430},{380,430}}, color={0,127,255}));
  connect(re_cv_VT1.y, twv_VT1.y)
    annotation (Line(points={{290,401},{270,401},{270,418}}, color={0,0,127}));
  connect(twv_VT1.port_2, dp_VT_2.port_a)
    annotation (Line(points={{280,430},{300,430}}, color={0,127,255}));
  connect(dp_VT_2.port_b, pum_VT1.port_a)
    annotation (Line(points={{320,430},{340,430}}, color={0,127,255}));
  connect(senT_VT_1.port_b, twv_VT1.port_1) annotation (Line(points={{250,420},
          {250,430},{260,430}}, color={0,127,255}));
  connect(senp_VT_2.port,senT_VT_2. port_b)
    annotation (Line(points={{430,440},{430,430},{400,430}},
                                                       color={0,127,255}));
  connect(dp_VT_4.port_b,senT_VT_4. port_a)
    annotation (Line(points={{520,430},{540,430}},
                                                 color={0,127,255}));
  connect(senT_VT_4.port_b,senh_VT. port_a)
    annotation (Line(points={{560,430},{580,430}},
                                                 color={0,127,255}));
  connect(senh_VT.port_b,load_VT. port_a)
    annotation (Line(points={{600,430},{630,430},{630,460}},
                                                           color={0,127,255}));
  connect(load_VT.port_b,senq_VT. port_a) annotation (Line(points={{630,480},{
          630,569.825},{600.175,569.825}}, color={0,127,255}));
  connect(senq_VT.port_b,senT_VT_6. port_a) annotation (Line(points={{579.775,
          569.875},{569.888,569.875},{569.888,570},{560,570}}, color={0,127,255}));
  connect(senT_VT_6.port_b,dp_VT_6. port_a)
    annotation (Line(points={{540,570},{520,570}}, color={0,127,255}));
  connect(cv_VT_3.port_b,dp_VT_3. port_a)
    annotation (Line(points={{250,540},{250,520}}, color={0,127,255}));
  connect(senh_VT.H_flow,senq_VT. u2) annotation (Line(points={{590,441},{590,
          500.55},{589.925,500.55},{589.925,560.1}}, color={0,0,127}));
  connect(dp_VT_3.port_b,senT_VT_5. port_a)
    annotation (Line(points={{250,500},{250,480}}, color={0,127,255}));
  connect(senT_VT_5.port_b, twv_VT1.port_3) annotation (Line(points={{250,460},
          {250,440},{270,440}}, color={0,127,255}));
  connect(senT_VT_3.port_a,dp_VT_7. port_b)
    annotation (Line(points={{400,570},{420,570}},
                                                 color={0,127,255}));
  connect(cv_VT_1.port_b,dp_VT_1. port_a)
    annotation (Line(points={{250,340},{250,360}}, color={0,127,255}));
  connect(dp_VT_1.port_b,senT_VT_1. port_a)
    annotation (Line(points={{250,380},{250,400}}, color={0,127,255}));
  connect(senT_VT_2.port_b,dp_VT_5. port_a)
    annotation (Line(points={{400,430},{470,430},{470,440}},
                                                         color={0,127,255}));
  connect(dp_VT_5.port_b,val_VT_5. port_a)
    annotation (Line(points={{470,460},{470,480}}, color={0,127,255}));
  connect(dp_VT_4.port_a,dp_VT_5. port_a)
    annotation (Line(points={{500,430},{470,430},{470,440}},
                                                          color={0,127,255}));
  connect(re_pum_VT2.y,val_VT_5. y)
    annotation (Line(points={{441,490},{458,490}},
                                                 color={0,0,127}));
  connect(val_VT_5.port_b,senT_VT_8. port_a)
    annotation (Line(points={{470,500},{470,520}}, color={0,127,255}));
  connect(senT_VT_8.port_b,dp_VT_6. port_b) annotation (Line(points={{470,540},
          {470,570},{500,570}}, color={0,127,255}));
  connect(senT_VT_8.port_b,dp_VT_7. port_a) annotation (Line(points={{470,540},
          {470,570},{440,570}},color={0,127,255}));
  connect(jun_VS_4_H.port_3, cv_VT_3.port_a)
    annotation (Line(points={{250,600},{250,560}}, color={0,127,255}));
  connect(jun_VS_18.port_3, cv_VT_1.port_a)
    annotation (Line(points={{250,-200},{250,320}}, color={0,127,255}));
  connect(tt_VT.y, pum_VT1.y) annotation (Line(points={{510,381},{510,408},{350,
          408},{350,418}}, color={0,0,127}));
  connect(tt_VT.y, load_VT.u) annotation (Line(points={{510,381},{510,408},{636,
          408},{636,458}}, color={0,0,127}));
  connect(senT_VT_3.port_b, senT_VT_7.port_b) annotation (Line(points={{380,570},
          {180,570},{180,210},{100,210}}, color={0,127,255}));
  connect(pum_VH.port_b,senT_VH_2. port_a)
    annotation (Line(points={{440,170},{460,170}},
                                              color={0,127,255}));
  connect(re_cv_VH.y,twv_VH. y)
    annotation (Line(points={{370,141},{350,141},{350,158}},color={0,0,127}));
  connect(twv_VH.port_2,dp_VH_2. port_a)
    annotation (Line(points={{360,170},{380,170}},
                                                 color={0,127,255}));
  connect(dp_VH_2.port_b,pum_VH. port_a)
    annotation (Line(points={{400,170},{420,170}},
                                                 color={0,127,255}));
  connect(senT_VH_1.port_b,twv_VH. port_1)
    annotation (Line(points={{330,160},{330,170},{340,170}},
                                                   color={0,127,255}));
  connect(senp_VH_2.port,senT_VH_2. port_b)
    annotation (Line(points={{510,180},{496,180},{496,170},{480,170}},
                                                       color={0,127,255}));
  connect(dp_VH_4.port_b,senT_VH_4. port_a)
    annotation (Line(points={{600,170},{620,170}},
                                                 color={0,127,255}));
  connect(senT_VH_4.port_b,senh_VH. port_a)
    annotation (Line(points={{640,170},{660,170}},
                                                 color={0,127,255}));
  connect(senh_VH.port_b,load_VH. port_a)
    annotation (Line(points={{680,170},{710,170},{710,200}},
                                                           color={0,127,255}));
  connect(load_VH.port_b,senq_VH. port_a) annotation (Line(points={{710,220},{
          710,309.825},{680.175,309.825}}, color={0,127,255}));
  connect(senq_VH.port_b,senT_VH_6. port_a) annotation (Line(points={{659.775,
          309.875},{649.888,309.875},{649.888,310},{640,310}}, color={0,127,255}));
  connect(senT_VH_6.port_b,dp_VH_6. port_a)
    annotation (Line(points={{620,310},{600,310}}, color={0,127,255}));
  connect(cv_VH_3.port_b,dp_VH_3. port_a)
    annotation (Line(points={{330,280},{330,260}}, color={0,127,255}));
  connect(senh_VH.H_flow,senq_VH. u2) annotation (Line(points={{670,181},{670,
          240.55},{669.925,240.55},{669.925,300.1}}, color={0,0,127}));
  connect(dp_VH_3.port_b,senT_VH_5. port_a)
    annotation (Line(points={{330,240},{330,220}}, color={0,127,255}));
  connect(senT_VH_5.port_b,twv_VH. port_3)
    annotation (Line(points={{330,200},{336,200},{336,180},{350,180}},
                                                  color={0,127,255}));
  connect(senT_VH_3.port_a,dp_VH_7. port_b)
    annotation (Line(points={{480,310},{500,310}},
                                                 color={0,127,255}));
  connect(cv_VH_1.port_b,dp_VH_1. port_a)
    annotation (Line(points={{330,80},{330,100}},  color={0,127,255}));
  connect(dp_VH_1.port_b,senT_VH_1. port_a)
    annotation (Line(points={{330,120},{330,140}}, color={0,127,255}));
  connect(senT_VH_2.port_b,dp_VH_5. port_a)
    annotation (Line(points={{480,170},{550,170},{550,180}},
                                                         color={0,127,255}));
  connect(dp_VH_5.port_b,val_VH_5. port_a)
    annotation (Line(points={{550,200},{550,220}}, color={0,127,255}));
  connect(dp_VH_4.port_a,dp_VH_5. port_a)
    annotation (Line(points={{580,170},{550,170},{550,180}},
                                                          color={0,127,255}));
  connect(re_pum_VH1.y,val_VH_5. y)
    annotation (Line(points={{521,230},{538,230}},
                                                 color={0,0,127}));
  connect(val_VH_5.port_b,senT_VH_8. port_a)
    annotation (Line(points={{550,240},{550,260}}, color={0,127,255}));
  connect(senT_VH_8.port_b,dp_VH_6. port_b) annotation (Line(points={{550,280},
          {550,310},{580,310}}, color={0,127,255}));
  connect(senT_VH_8.port_b,dp_VH_7. port_a) annotation (Line(points={{550,280},
          {550,310},{520,310}},color={0,127,255}));
  connect(senT_VH_3.port_b, senT_VH_7.port_b) annotation (Line(points={{460,310},
          {220,310},{220,130},{100,130}}, color={0,127,255}));
  connect(cv_VH_3.port_a, jun_VS_5_H.port_3)
    annotation (Line(points={{330,300},{330,600}}, color={0,127,255}));
  connect(jun_VS_19.port_3, cv_VH_1.port_a)
    annotation (Line(points={{330,-200},{330,60}}, color={0,127,255}));
  connect(tt_VH.y, pum_VH.y) annotation (Line(points={{590,121},{590,140},{430,
          140},{430,158}}, color={0,0,127}));
  connect(tt_VH.y, load_VH.u) annotation (Line(points={{590,121},{590,140},{716,
          140},{716,198}}, color={0,0,127}));
  connect(pum_VK.port_b,senT_VK_2. port_a)
    annotation (Line(points={{520,-90},{540,-90}},
                                              color={0,127,255}));
  connect(re_cv_VK.y,twv_VK. y)
    annotation (Line(points={{450,-119},{430,-119},{430,-102}},
                                                            color={0,0,127}));
  connect(twv_VK.port_2,dp_VK_2. port_a)
    annotation (Line(points={{440,-90},{460,-90}},
                                                 color={0,127,255}));
  connect(dp_VK_2.port_b,pum_VK. port_a)
    annotation (Line(points={{480,-90},{500,-90}},
                                                 color={0,127,255}));
  connect(senT_VK_1.port_b,twv_VK. port_1)
    annotation (Line(points={{410,-100},{410,-90},{420,-90}},
                                                   color={0,127,255}));
  connect(senp_VK_2.port,senT_VK_2. port_b)
    annotation (Line(points={{590,-80},{590,-90},{560,-90}},
                                                       color={0,127,255}));
  connect(dp_VK_4.port_b,senT_VK_4. port_a)
    annotation (Line(points={{680,-90},{700,-90}},
                                                 color={0,127,255}));
  connect(senT_VK_4.port_b,senh_VK. port_a)
    annotation (Line(points={{720,-90},{740,-90}},
                                                 color={0,127,255}));
  connect(senh_VK.port_b,load_VK. port_a)
    annotation (Line(points={{760,-90},{790,-90},{790,-60}},
                                                           color={0,127,255}));
  connect(load_VK.port_b,senq_VK. port_a) annotation (Line(points={{790,-40},{
          790,49.825},{760.175,49.825}},   color={0,127,255}));
  connect(senq_VK.port_b,senT_VK_6. port_a) annotation (Line(points={{739.775,
          49.875},{729.888,49.875},{729.888,50},{720,50}},     color={0,127,255}));
  connect(senT_VK_6.port_b,dp_VK_6. port_a)
    annotation (Line(points={{700,50},{680,50}},   color={0,127,255}));
  connect(cv_VK_3.port_b,dp_VK_3. port_a)
    annotation (Line(points={{410,20},{410,0}},    color={0,127,255}));
  connect(senh_VK.H_flow,senq_VK. u2) annotation (Line(points={{750,-79},{750,
          -19.45},{749.925,-19.45},{749.925,40.1}},  color={0,0,127}));
  connect(dp_VK_3.port_b,senT_VK_5. port_a)
    annotation (Line(points={{410,-20},{410,-40}}, color={0,127,255}));
  connect(senT_VK_5.port_b,twv_VK. port_3)
    annotation (Line(points={{410,-60},{410,-80},{430,-80}},
                                                  color={0,127,255}));
  connect(senT_VK_3.port_a,dp_VK_7. port_b)
    annotation (Line(points={{560,50},{580,50}}, color={0,127,255}));
  connect(cv_VK_1.port_b,dp_VK_1. port_a)
    annotation (Line(points={{410,-180},{410,-160}},
                                                   color={0,127,255}));
  connect(dp_VK_1.port_b,senT_VK_1. port_a)
    annotation (Line(points={{410,-140},{410,-120}},
                                                   color={0,127,255}));
  connect(senT_VK_2.port_b,dp_VK_5. port_a)
    annotation (Line(points={{560,-90},{560,-80},{630,-80}},
                                                         color={0,127,255}));
  connect(dp_VK_5.port_b,val_VK_5. port_a)
    annotation (Line(points={{630,-60},{630,-40}}, color={0,127,255}));
  connect(dp_VK_4.port_a,dp_VK_5. port_a)
    annotation (Line(points={{660,-90},{660,-80},{630,-80}},
                                                          color={0,127,255}));
  connect(re_pum_VK1.y,val_VK_5. y)
    annotation (Line(points={{601,-30},{618,-30}},
                                                 color={0,0,127}));
  connect(val_VK_5.port_b,senT_VK_8. port_a)
    annotation (Line(points={{630,-20},{630,0}},   color={0,127,255}));
  connect(senT_VK_8.port_b,dp_VK_6. port_b) annotation (Line(points={{630,20},{
          630,50},{660,50}},    color={0,127,255}));
  connect(senT_VK_8.port_b,dp_VK_7. port_a) annotation (Line(points={{630,20},{
          630,50},{600,50}},   color={0,127,255}));
  connect(cv_VK_3.port_a, jun_VS_5_H.port_2) annotation (Line(points={{410,40},
          {410,610},{340,610}}, color={0,127,255}));
  connect(cv_VK_1.port_a, jun_VS_19.port_2) annotation (Line(points={{410,-200},
          {410,-210},{340,-210}}, color={0,127,255}));
  connect(senT_VK_3.port_b, senT_VK_7.port_b)
    annotation (Line(points={{540,50},{100,50}}, color={0,127,255}));
  connect(tt_VK.y, load_VK.u) annotation (Line(points={{670,-139},{670,-120},{
          796,-120},{796,-62}}, color={0,0,127}));
  connect(tt_VK.y, pum_VK.y) annotation (Line(points={{670,-139},{670,-120},{
          510,-120},{510,-102}}, color={0,0,127}));
  annotation (Documentation(revisions="<html>
<p>TODO &Uuml;berpr&uuml;fen: werden die anderen Experimentalstr&auml;nge durchstr&ouml;mt zu Zeiten wo sie eigentlich au&szlig;er Betrieb sind?</p>
<p>TODO Regelung erstellen</p>
<ul>
<li>5 Pumpen im root</li>
<li>3 Pumpen in den Experimentalkreisen</li>
<li>4 Ventile im Root (2x 3WV + 1 2WV)</li>
<li>3 3WV in den Experimentalkreisen</li>
</ul>
<p>TODO Regelbedarf herbeif&uuml;hren</p>
<ul>
<li>schwankender Druck und Temperatur von Geb&auml;udeseite</li>
<li>wechselnde Temperaturanforderungen an den Experimentalstutzen</li>
<li>ideales Versuchsobjekt mit nichtidealem Ersetzen</li>
<li>gleichzeitigen Betrieb der Experimentalstr&auml;nge und/oder Klimakammer</li>
</ul>
<p>BISHER</p>
<ul>
<li>2023-08-29 EE Experimentalkreise direkt eingebaut (nicht mehr als Submodell)</li>
<li>2023-08-28 EE testweise hinzuf&uuml;gen von Datenpunktnamen, &Uuml;berarbeitung Icon</li>
<li>2023-08-24 EE erstmalige Erstellung</li>
</ul>
</html>", info="<html>
<p>jeder Experimentalkreis wird von einem Zeitplan an- und abgeschaltet, die Versrogungspumpen Heizungs- und k&auml;lteseitig laufen hingegen st&auml;ndig</p>
<p>jetzt ist es erstmal so eingestellt, dass alle Kreise nacheinander in Betrieb gehen und so lange an bleiben, bis sich ein station&auml;rer Zustand eingestellt hat, d.h. die Speicher durchgeladen sind</p>
<p>Versuchsablauf in h (als Gesamtsimulationsdauer sind 5 Tage / 120 h eingestellt) - Simulationszeit daf&uuml;r: 2s (zwei Sekunden)</p>
<ul>
<li>0....2: nur Einspeisung mit Verteiler/Sammler</li>
<li>2...8h: VS mit Experimentalkreis Trinkwasser</li>
<li>8...9h: nur VS</li>
<li>9...12h: (~2:45h): VS mit Experimentalkreis Heizung</li>
<li>12...13h: nur VS</li>
<li>13...20h: VS mit Experimentalkreis K&uuml;hlen</li>
<li>20...21h: nur VS</li>
<li>21...50h: VS mit Klimaklammer</li>
<li>ab 50h: nur VS</li>
</ul>
</html>"), experiment(StopTime=432000, __Dymola_Algorithm="Dassl"),
    Diagram(coordinateSystem(extent={{-600,-480},{820,640}})),
    __Dymola_Commands(file="2023-08-24 1822 VS mit Experimentalkreisen.mos"
        "Temperaturen Masseströme Speicher mit Experimentalkreisen", file=
          "2023-08-24 2124 VS mit Experimentalkreisen und Klimakammer und Einspeisung.mos"
        "Einspeisung Experimentalkreise und Klimakammer"));
end HIL_flat;
