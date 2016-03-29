package wsu.team.r.teamr_realtimedisplay_android;

import com.google.android.gms.maps.model.LatLng;

import java.security.Key;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by Chase on 2/8/2016.
 */
public class Asset {
    private HashMap<String, Object> data;

    public Asset(){
        data = new HashMap<>();
    }

    public void addData(String key, Object value){
        data.put(key,value);
    }

    public Object retrieveData(String key){
        return data.get(key);
    }

    public Double retrieveDoubleData(String key){
        return Double.parseDouble((String) data.get(key));
    }

    public String retrieveStringData(String key){
        return (String) data.get(key);
    }

    public Integer retrieveIntegerData(String key){
        return Integer.parseInt((String) data.get(key));
    }

    public Long retrieveLongData(String key){
        return Long.parseLong((String) data.get(key));
    }

    public List<String> getKeys(){
        return new ArrayList<>(data.keySet());
    }

    public String getDisplayInfo(){
        String temp = (String) data.get("Name");
        return temp;
    }

    public String getExtraInfo(){
        String temp = "";
        temp += (String) data.get("Department") + "\n";
        temp += "Lat: " + data.get("Latitude") + "Lng: " + data.get("Longitude");
        return temp;
    }
}
