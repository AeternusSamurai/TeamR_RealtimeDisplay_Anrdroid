package wsu.team.r.teamr_realtimedisplay_android;

import android.util.Log;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.net.URLEncoder;

/**
 * Created by Chase on 2/22/2016.
 */
public class JSONParser {
    private static InputStream is = null;
    private static JSONObject jObj = null;
    private static String json = "";

    public JSONParser(){}

    public JSONObject makeHttpRequest(String url, String method, List<Pair<String,String>> params){
        URL urlCon;
        HttpURLConnection http = null;
        try{
            if(method.equals("POST")){
                // does nothing for now
            }else if(method.equals("GET")){
                String paramString = URLEncoder.encode(params.toString(), "utf-8");
                url += "?" + paramString;
                urlCon = new URL(url);
                http = (HttpURLConnection) urlCon.openConnection();
                http.setRequestMethod(method);
                http.setDoInput(true);
                http.connect();
                is = http.getInputStream();
            }
        }catch (UnsupportedEncodingException e){
            // Uh... bad encoding maybe
            e.printStackTrace();
        }catch (MalformedURLException e) {
            // That's a badly formed url
            e.printStackTrace();
        }catch (IOException e){
            // Something happened
            e.printStackTrace();
        }finally {
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null){
                    sb.append(line + "\n");
                }
                is.close();
                json = sb.toString();
            }catch(Exception e){
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }

            try{
                jObj = new JSONObject(json);
            }catch(JSONException e){
                Log.e("JSON Parser", "Error Parsing data " + e.toString());
            }
            if(http != null){
                http.disconnect();
            }
        }

        return jObj;
    }
}
