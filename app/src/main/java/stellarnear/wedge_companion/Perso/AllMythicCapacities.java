package stellarnear.wedge_companion.Perso;

import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by jchatron on 26/12/2017.
 */

public class AllMythicCapacities {
    private Context mC;
    private List<MythicCapacity> allMythicCapacities = new ArrayList<>();
    private Map<String, MythicCapacity> mapIdMythiccapacity = new HashMap<>();
    private String pjID = "";

    public AllMythicCapacities(Context mC, String pjID) {
        this.mC = mC;
        this.pjID = pjID;
        buildCapacitiesList();
    }

    private void buildCapacitiesList() {
        allMythicCapacities = new ArrayList<>();
        mapIdMythiccapacity = new HashMap<>();
        try {
            String extendID = pjID.equalsIgnoreCase("") ? "" : "_" + pjID;
            InputStream is = mC.getAssets().open("mythiccapacities" + extendID + ".xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("mythiccapacity");

            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    MythicCapacity mythicCapacity = new MythicCapacity(
                            readValue("name", element2),
                            readValue("type", element2),
                            readValue("descr", element2),
                            readValue("id", element2),
                            mC,
                            pjID);
                    allMythicCapacities.add(mythicCapacity);
                    mapIdMythiccapacity.put(mythicCapacity.getId(), mythicCapacity);
                }
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MythicCapacity> getAllMythicCapacitiesList() {
        return allMythicCapacities;
    }

    public MythicCapacity getMythiccapacity(String mythiccapacitytId) {
        MythicCapacity selectedMythicCapacity;
        try {
            selectedMythicCapacity = mapIdMythiccapacity.get(mythiccapacitytId);
        } catch (Exception e) {
            selectedMythicCapacity = null;
        }
        return selectedMythicCapacity;
    }

    public String readValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        } catch (Exception e) {
            return "";
        }
    }

    public void reset() {
        buildCapacitiesList();
    }

    public boolean mythicCapacityIsActive(String id) {
        boolean val = false;
        try {
            val = getMythiccapacity(id).isActive();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return val;
    }
}
