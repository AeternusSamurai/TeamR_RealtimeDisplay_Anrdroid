package wsu.team.r.teamr_realtimedisplay_android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//List View imports
import android.widget.ExpandableListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import wsu.team.r.teamr_realtimedisplay_android.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseConnectionService dbcService;

    //List View variables
    private ExpandableListView expandableListView;
    private List<String>parentHeaderInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Expandable List View setup
        parentHeaderInformation = new ArrayList<>();

        //Parent label name
        parentHeaderInformation.add("Police");
        parentHeaderInformation.add("Fire Department");
        parentHeaderInformation.add("Medical");
        HashMap<String, List<String>> allChildItems = returnGroupedChildItems();
        expandableListView = (ExpandableListView)findViewById(R.id.expandableListView);
        ExpandableListViewAdapter expandableListViewAdapter = new ExpandableListViewAdapter(getApplicationContext(), parentHeaderInformation, allChildItems);
        expandableListView.setAdapter(expandableListViewAdapter);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            dbcService = ((DatabaseConnectionService.LocalBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            dbcService = null;
        }
    };

    private void bindService(){
        bindService(new Intent(this, DatabaseConnectionService.class), connection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService(){
        unbindService(connection);
    }

    //List view methods
    private HashMap<String, List<String>> returnGroupedChildItems(){
        //ID, Location(Lat|Long), Name, Department
        HashMap<String, List<String>> childContent = new HashMap<String, List<String>>();

        //Child item label names
        List<String> police = new ArrayList<>();
        police.add("Police Name 1");
        police.add("Police Name 2");
        police.add("Police Name 3");
        police.add("Police Name 4");

        List<String> firedep = new ArrayList<>();
        firedep.add("Fire Name 1");
        firedep.add("Fire Name 2");
        firedep.add("Fire Name 3");
        firedep.add("Fire Name 4");

        List<String> medical = new ArrayList<>();
        medical.add("Medical Name 1");
        medical.add("Medical Name 2");
        medical.add("Medical Name 3");
        medical.add("Medical Name 4");

        childContent.put(parentHeaderInformation.get(0), police);
        childContent.put(parentHeaderInformation.get(1), firedep);
        childContent.put(parentHeaderInformation.get(2), medical);

        return childContent;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng dayton = new LatLng(39.779324, -84.063376);
        mMap.addMarker(new MarkerOptions().position(dayton).title("Marker in Dayton"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(dayton));
    }
}
