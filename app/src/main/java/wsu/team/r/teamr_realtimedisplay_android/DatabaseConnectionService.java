package wsu.team.r.teamr_realtimedisplay_android;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Chase on 2/15/2016.
 */
public class DatabaseConnectionService extends Service {

    private ArrayList<Asset> assets;
    private JSONParser parser = new JSONParser();

    private JSONArray jAssets = null;

    private Timer timer;

    public class LocalBinder extends Binder {
        DatabaseConnectionService getService(){
            return DatabaseConnectionService.this;
        }
    }

    @Override
    public void onCreate() {
        //Initial initalize list
        assets = new ArrayList<>();
        //get data from database
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new LoadAssets().execute();
            }
        }, 0, 60000);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        timer = null;
        // run a backup of the data in case of network connection lose
        super.onDestroy();
    }

    // Not used
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private final IBinder binder = new LocalBinder();

    class LoadAssets extends AsyncTask<String, String, String>{
        protected String doInBackground(String... args){
            List<Pair<String,String>> params = new ArrayList<>();

            //TODO get the url to connect to the database web sevice.
            JSONObject json = parser.makeHttpRequest("","GET",params);

            try{
                int success = json.getInt("success");
                if(success == 1){
                    jAssets = json.getJSONArray("assets");

                    for(int i = 0; i < jAssets.length(); i++){
                        //Create asset object

                        JSONObject c = jAssets.getJSONObject(i);
                        JSONArray jNames = c.names();
                        for(int j = 0; j < jNames.length(); i++){
                            //Get values from the JSON objects
                            jNames.getString(i);
                        }

                        //Add the new asset to the list
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String s){
            // broadcast that the assets have be retreived
            // reset the timer for the next refresh time
        }
    }
}
