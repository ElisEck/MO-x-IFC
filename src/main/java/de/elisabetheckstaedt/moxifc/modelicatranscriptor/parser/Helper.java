package de.elisabetheckstaedt.moxifc.modelicatranscriptor.parser;

public class Helper {

    public static String maskSpecialCharacter(String oldString) {
        String newString = oldString;
        if (newString.contains("\"")) {
            newString = newString.replace("\"", "\\\"");
        }
        return newString;
    }

    public static String attachPrefixToModelicaName(String modelicaName, String backupPrefix) {
        if ((modelicaName.startsWith("Modelica"))) {
            return "msl:" + modelicaName;
        }
        else  if ((modelicaName.startsWith("Buildings"))) {
            return "mbl:" + modelicaName;
        }
        else  if ((modelicaName.startsWith("AixLib"))) {
            return "aix:" + modelicaName;
        }
        else  if ((modelicaName.startsWith("LibEAS"))) {
            return "libeas:" + modelicaName;
        }
        else  if ((modelicaName.startsWith("Real") || modelicaName.startsWith("Boolean") || modelicaName.startsWith("Integer") || modelicaName.startsWith("String"))) {
            return "xsd:" + modelicaName;
        }
        else  {
            return backupPrefix + ":" + modelicaName;
        }
    }

    public static String cleanName(String nameOld) { //TODO replace cleanName
        String nameNew = nameOld;
        if (nameOld.contains("[")) {
            nameNew = nameNew.replace("[", "_");
            nameNew = nameNew.replace("]", "");
        } if (nameOld.contains("+")) {
            nameNew = nameNew.replace("+", "plus");
        } if (nameOld.contains(",")) {
            nameNew = nameNew.replace(",", "comma");
        } if (nameOld.contains(":")) {
            nameNew = nameNew.replace(":", "TO");
        } if (nameOld.contains("*")) {
            nameNew = nameNew.replace("*", "TIMES");
        } if (nameOld.contains("(")) {
            nameNew = nameNew.replace("(", "_");
            nameNew = nameNew.replace(")", "");
        } if (nameOld.contains(">")) {
            nameNew = nameNew.replace(">", "GREATER");
        } if (nameOld.contains("<")) {
            nameNew = nameNew.replace("<", "SMALLER");
        } if (nameOld.contains("-")) {
            nameNew = nameNew.replace("*", "MINUS");
        }
        return nameNew;
    }

    public static String cleanStringFromLineBreaks(String input) {
        input = input.replace("\r\n", "_");
        input = input.replace("\n", "_");
        input = input.replace("\r", "_");
        return input;

    }
}
