package stellarnear.wedge_companion.Perso;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class PersoManager {

    private static Map<String,Perso> currentMapPJ = new HashMap<>();
    private static String currentPJ = "";

    public static Perso getCurrentPJ() {
        Perso current=null;
        try {
                current = currentMapPJ.get(currentPJ);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return current;
    }

    public static void initPJs(Context applicationContext) {
        currentMapPJ.put("Wedge",new Perso(applicationContext,""));
        currentMapPJ.put("Halda",new Perso(applicationContext,"halda"));
        currentMapPJ.put("Sylphe",new Perso(applicationContext,"sylphe"));
        currentMapPJ.put("Rana",new Perso(applicationContext,"rana"));
        currentPJ="Wedge";
    }

    public static String getCurrentNamePJ() {
        return currentPJ;
    }

    public static void swap() {
        if(currentPJ.equalsIgnoreCase("Wedge")){
            currentPJ="Halda";
        } else if(currentPJ.equalsIgnoreCase("Halda")) {
            currentPJ="Wedge";
        } else if(currentPJ.equalsIgnoreCase("Sylphe")) {
            currentPJ="Rana";
        } else if(currentPJ.equalsIgnoreCase("Rana")) {
            currentPJ="Sylphe";
        } else {
            currentPJ="Wedge";
        }
    }

    public static void setMainPJ() {
        if(currentPJ.equalsIgnoreCase("Sylphe")){
            currentPJ="Wedge";
        } else if(currentPJ.equalsIgnoreCase("Rana")) {
            currentPJ="Halda";
        }
    }

    public static void setPetPJ() {
        if(currentPJ.equalsIgnoreCase("Wedge")){
            currentPJ="Sylphe";
        } else if(currentPJ.equalsIgnoreCase("Halda")) {
            currentPJ="Rana";
        }
    }

    public static boolean isMainPJ() {
        return currentPJ.equalsIgnoreCase("wedge")||currentPJ.equalsIgnoreCase("halda");
    }
}
