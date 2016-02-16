package wsu.team.r.teamr_realtimedisplay_android;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by Chase on 2/15/2016.
 */
public class DatabaseConnectionService extends Service {

    private ArrayList<Asset> assets;

    public class LocalBinder extends Binder {
        DatabaseConnectionService getService(){
            return DatabaseConnectionService.this;
        }
    }

    @Override
    public void onCreate() {
        //Initial initalize list
        //get data from database
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // Not used
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private final IBinder binder = new LocalBinder();
}
