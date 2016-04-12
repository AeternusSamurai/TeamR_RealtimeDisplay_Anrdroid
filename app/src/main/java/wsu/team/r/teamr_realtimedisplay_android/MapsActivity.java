package wsu.team.r.teamr_realtimedisplay_android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

//List View imports

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ExpandableListView.OnChildClickListener {

    private GoogleMap mMap;
    private DatabaseConnectionService dbcService;

    //List View variables
    private ExpandableListView expandableListView;
    private List<String> parentHeaderInformation;
    private HashMap<String, List<String>> childItems;

    private ExpandableListViewAdapter expandableListViewAdapter;

    private int parent = 0;
    private int child = 0;

    private RefreshReceiver refreshReceiver;
    private UpdateReceiver updateReceiver;

    private boolean keepService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent database = new Intent(this, DatabaseConnectionService.class);
        startService(database);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dbcService = DatabaseConnectionService.getInstance();
        refreshReceiver = new RefreshReceiver();
        updateReceiver = new UpdateReceiver();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Expandable List View setup
        parentHeaderInformation = new ArrayList<>();
        childItems = new HashMap<>();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.open_drawer, R.string.close_drawer
        );
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        dbcService = DatabaseConnectionService.getInstance();
        parentHeaderInformation.add("All");
        parentHeaderInformation.add("Police");
        parentHeaderInformation.add("Fire Department");
        parentHeaderInformation.add("Medical");
        parentHeaderInformation.add("FBI");
        parentHeaderInformation.add("Other");
        childItems = returnGroupedChildItems();
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListViewAdapter = new ExpandableListViewAdapter(getApplicationContext(), parentHeaderInformation, childItems);
        expandableListView.setAdapter(expandableListViewAdapter);
        expandableListView.setOnChildClickListener(this);
    }

    @Override
    protected void onResume() {
        registerReceiver(refreshReceiver, new IntentFilter("REFRESH_MARKERS"));
        registerReceiver(updateReceiver, new IntentFilter("UPDATING_ASSETS"));
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(refreshReceiver);
        unregisterReceiver(updateReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to stop the asset retrieval service?")
                    .setTitle("Close Asset Retrieval Service")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            stopService(new Intent(getApplicationContext(), DatabaseConnectionService.class));
                            finish();
                        }
                    })
                    .setNeutralButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //List view methods
    private HashMap<String, List<String>> returnGroupedChildItems() {
        //ID, Location(Lat|Long), Name, Department
        HashMap<String, List<String>> childContent = new HashMap<String, List<String>>();
        dbcService = DatabaseConnectionService.getInstance();
        //List<Asset> assets = new ArrayList<>();
        if (dbcService != null) {
            List<Asset> assets = dbcService.getInfoAssets();

            //Child item label names
            List<String> police = new ArrayList<>();
            Set<String> policeSet = new HashSet<>();

            List<String> firedep = new ArrayList<>();
            Set<String> firedepSet = new HashSet<>();

            List<String> medical = new ArrayList<>();
            Set<String> medicalSet = new HashSet<>();

            List<String> fbi = new ArrayList<>();
            List<String> other = new ArrayList<>();
            Set<String> otherSet = new HashSet<>();
            fbi.add("All");
            other.add("All");
            medical.add("All");
            police.add("All");
            firedep.add("All");

            for (Asset asset : assets) {
                if (asset.retrieveStringData("Department").contains("Fire")) {
                    firedepSet.add(asset.retrieveStringData("Department"));
                } else if (asset.retrieveStringData("Department").contains("Police")) {
                    policeSet.add(asset.retrieveStringData("Department"));
                } else if (asset.retrieveStringData("Department").contains("EMS") || asset.retrieveStringData("Department").contains("Emergency Medical Services")) {
                    medicalSet.add(asset.retrieveStringData("Department"));
                } else {
                    if (!asset.retrieveStringData("Department").equals("FBI")) {
                        otherSet.add(asset.retrieveStringData("Department"));
                    }
                }
            }

            police.addAll(policeSet);
            firedep.addAll(firedepSet);
            medical.addAll(medicalSet);
            other.addAll(otherSet);


            for (int i = 0; i < parentHeaderInformation.size(); i++) {
                switch (i) {
                    case 0:
                        // All filter: Display all of the assets
                        ArrayList<String> allArr = new ArrayList<>();
                        allArr.add("All");
                        childContent.put(parentHeaderInformation.get(i), allArr);
                        break;
                    case 1:
                        // Police filter: display by police departments
                        childContent.put(parentHeaderInformation.get(i), police);
                        break;
                    case 2:
                        // Fire Filter: display by fire departments
                        childContent.put(parentHeaderInformation.get(i), firedep);
                        break;
                    case 3:
                        // Medical Filter: display by EMS
                        childContent.put(parentHeaderInformation.get(i), medical);
                        break;
                    case 4:
                        // FBI Filter: display by FBI
                        childContent.put(parentHeaderInformation.get(i), fbi);
                        break;
                    case 5:
                        // Other filter
                        childContent.put(parentHeaderInformation.get(i), other);
                        break;
                    default:
                        // do nothing
                        break;
                }
            }
        } else {
            ArrayList<String> allArr = new ArrayList<>();
            allArr.add("All");
            for (int i = 0; i < parentHeaderInformation.size(); i++) {
                switch (i) {
                    case 0:
                        // All filter: Display all of the assets
                        childContent.put(parentHeaderInformation.get(i), allArr);
                        break;
                    case 1:
                        // Police filter: display by police departments
                        childContent.put(parentHeaderInformation.get(i), allArr);
                        break;
                    case 2:
                        // Fire Filter: display by fire departments
                        childContent.put(parentHeaderInformation.get(i), allArr);
                        break;
                    case 3:
                        // Medical Filter: display by EMS
                        childContent.put(parentHeaderInformation.get(i), allArr);
                        break;
                    case 4:
                        // FBI Filter: display by FBI
                        childContent.put(parentHeaderInformation.get(i), allArr);
                        break;
                    case 5:
                        // Other filter
                        childContent.put(parentHeaderInformation.get(i), allArr);
                        break;
                    default:
                        // do nothing
                        break;
                }
            }
        }
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
        CameraPosition cameraPosition = new CameraPosition.Builder().target(dayton).zoom(15).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        displayMarkers(groupPosition, childPosition);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displayMarkers(Integer group, Integer child) {
        mMap.clear();
        dbcService = DatabaseConnectionService.getInstance();
        // group is parent info
        // child is specific action
        String parent = parentHeaderInformation.get(group);
        String chld = childItems.get(parent).get(child);
        this.parent = group;
        this.child = child;

        if (dbcService != null) {
            if (parent.equals("All")) {
                // display all of the assets
                for (Asset asset : dbcService.getInfoAssets()) {
                    double lat = asset.retrieveDoubleData("Latitude");
                    double lng = asset.retrieveDoubleData("Longitude");
                    BitmapDescriptor color = getDepartmentColor(asset.retrieveStringData("Department"));
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title(asset.getDisplayInfo())
                            .snippet(asset.getExtraInfo())
                            .icon(color));
                }
            } else if (parent.equals("Other")) {
                for(Asset asset : dbcService.getInfoAssets()){
                    if(childItems.get(parent).contains(asset.retrieveStringData("Department"))){
                        double lat = asset.retrieveDoubleData("Latitude");
                        double lng = asset.retrieveDoubleData("Longitude");
                        BitmapDescriptor color = getDepartmentColor(asset.retrieveStringData("Department"));
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .title(asset.getDisplayInfo())
                                .snippet(asset.getExtraInfo())
                                .icon(color));
                    }
                }
            } else {
                if (chld.equals("All")) {
                    // display all of the assets from a group
                    for (Asset asset : dbcService.getInfoAssets()) {
                        if (asset.retrieveStringData("Department").contains(parent)) {
                            double lat = asset.retrieveDoubleData("Latitude");
                            double lng = asset.retrieveDoubleData("Longitude");
                            BitmapDescriptor color = getDepartmentColor(asset.retrieveStringData("Department"));
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat, lng))
                                    .title(asset.getDisplayInfo())
                                    .snippet(asset.getExtraInfo())
                                    .icon(color));
                        }
                    }
                } else {
                    for (Asset asset : dbcService.getInfoAssets()) {
                        if (asset.retrieveStringData("Department").equals(chld)) {
                            double lat = asset.retrieveDoubleData("Latitude");
                            double lng = asset.retrieveDoubleData("Longitude");
                            BitmapDescriptor color = getDepartmentColor(asset.retrieveStringData("Department"));
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat, lng))
                                    .title(asset.getDisplayInfo())
                                    .snippet(asset.getExtraInfo())
                                    .icon(color));
                        }
                    }
                }
            }
            if(!dbcService.getInfoAssets().isEmpty() && dbcService != null) {
                Random rnd = new Random();
                int randLocation = rnd.nextInt(dbcService.getInfoAssets().size());
                LatLng zoomLoc = new LatLng(dbcService.getInfoAssets().get(randLocation).retrieveDoubleData("Latitude"), dbcService.getInfoAssets().get(randLocation).retrieveDoubleData("Longitude"));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(zoomLoc).zoom(15).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }


    public class RefreshReceiver extends BroadcastReceiver {

        public RefreshReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("REFRESH_MARKERS")) {
                ConnectionStatus cs = (ConnectionStatus) intent.getSerializableExtra("CONNECTION_STATUS");
                if (cs == ConnectionStatus.GOOD) {
                    Toast.makeText(context, "Asset information updated", Toast.LENGTH_LONG).show();
                } else if (cs == ConnectionStatus.NO_CONNECTION) {
                    Toast.makeText(context, "Unable to retrieve updated assets: No connection", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "No connection to the server: Connection timeout", Toast.LENGTH_LONG).show();
                }
                childItems = returnGroupedChildItems();
                expandableListViewAdapter.setChildDataSource(childItems);
                expandableListViewAdapter.notifyDataSetChanged();
                displayMarkers(parent, child);
            }
        }
    }

    public class UpdateReceiver extends BroadcastReceiver {

        public UpdateReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("UPDATING_ASSETS")) {
                Toast.makeText(context, "Updating asset information...", Toast.LENGTH_LONG).show();
            }
        }
    }

    private BitmapDescriptor getDepartmentColor(String department) {
        BitmapDescriptor color = null;
        if (department.contains("Fire Department") || department.contains("Fire")) {
            color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        } else if (department.contains("Police Department") || department.contains("Police")) {
            color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
        } else if (department.contains("EMS") || department.contains("Emergency Medial Services")) {
            color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        } else if (department.contains("FBI")) {
            color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
        } else {
            color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
        }
        return color;
    }
}
