package com.example.user.university_project;
/*
* Created By Michael Beaseley
* Brunel Dissertation Project
* 2016/2017
*
* Disclaimer: Followed tutorial from Indooraltas (www.indooratlas.com)
*/
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import android.os.Looper;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;
import com.indooratlas.android.sdk.resources.IAFloorPlan;
import com.indooratlas.android.sdk.resources.IALatLng;
import com.indooratlas.android.sdk.resources.IAResourceManager;
import com.indooratlas.android.sdk.resources.IAResult;
import com.indooratlas.android.sdk.resources.IAResultCallback;
import com.indooratlas.android.sdk.resources.IATask;
import com.indooratlas.android.sdk.resources.IALocationListenerSupport;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

public class mapview extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IALocationListener, BeaconConsumer, MonitorNotifier {

    private GoogleMap mMap;
    private SupportMapFragment mMapFrag;
    private Marker mMarker;
    private LatLng latLng;
    private IALocation location;
    private IARegion mOveralFloorPlan = null;
    private GroundOverlay mGroundOverlay= null;
    private IALocationManager mIALocationManager;
    private IAResourceManager mResourceManager;
    private IATask<IAFloorPlan> mFetchFloorPlanTask;
    private Target mLoadTarget;
    private boolean mCameraPositionNeedsUpdating = true;
    private static final String TAG = "mapview";
    private BeaconManager mBeaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mIALocationManager = IALocationManager.create(this);
        mResourceManager = IAResourceManager.create(this);
        /*
        final String floorPlanId = getString(R.string.indooratlas_floor_plan_id);
        if (!TextUtils.isEmpty(floorPlanId)) {
            final IALocation FLOOR_PLAN_ID = IALocation.from(IARegion.floorPlan(floorPlanId));
            mIALocationManager.setLocation(FLOOR_PLAN_ID);
        }
        */
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mapview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent in = new Intent(mapview.this, settingsActivity.class);
            startActivity(in);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_NFC) {
            Intent in = new Intent(mapview.this, nfcActivity.class);
            startActivity(in);
        } else if (id == R.id.nav_userguide) {
            Intent in = new Intent(mapview.this, userguideActivity.class);
            startActivity(in);
        } else if (id == R.id.nav_settings) {
            Intent in = new Intent(mapview.this, settingsActivity.class);
            startActivity(in);
        } else if (id == R.id.nav_aboutus) {
            Intent in = new Intent(mapview.this, aboutusActivity.class);
            startActivity(in);
        } else if (id == R.id.nav_logoff) {
            moveTaskToBack(true);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public IALocationListener mIALocationListener = new IALocationListener() {
        @Override
        public void onLocationChanged(IALocation iaLocation) {
            Log.d(TAG, "Latitude: " + iaLocation.getLatitude());
            Log.d(TAG, "Longitude: " + iaLocation.getLongitude());
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            //....... N/A.....
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        if(mMap == null) {
            mMapFrag = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map));
            mMap = mMapFrag.getMap();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        }
        mIALocationManager.requestLocationUpdates(IALocationRequest.create(), mIALocationListener);
        mIALocationManager.registerRegionListener(mRegionListener);

        mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the main Eddystone_UID frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        mBeaconManager.bind(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mIALocationManager.removeLocationUpdates(this);
        mIALocationManager.registerRegionListener(mRegionListener);
        mBeaconManager.unbind(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIALocationManager.destroy();
    }
    @Override
    public void onLocationChanged(IALocation iaLocation) {
        latLng = new LatLng(iaLocation.getLatitude(), iaLocation.getLongitude());
        if(mMarker == null) {
            if(mMap != null) {
                mMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0f));
            }
        } else {
            mMarker.setPosition(latLng);
        }
    }
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        //...... N/A......
    }

    private IALocationListener mListener = new IALocationListenerSupport() {
        @Override
        public void onLocationChanged(IALocation location) {
            Log.d(TAG, "new location received with coordinates: " + location.getLatitude() + "," + location.getLongitude());

            if (mMap == null) {
                //location received before map is initialized, ignored update here
                return;
            }

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (mMarker == null) {
                // first location, add marker
                mMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            } else {
                // move existing markers position to received location
                mMarker.setPosition(latLng);
            }

            // our camera position needs updating if location has significantly changed
            if (mCameraPositionNeedsUpdating) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.5f));
                mCameraPositionNeedsUpdating = false;
            }
        }
    };

    private IARegion.Listener mRegionListener = new IARegion.Listener() {
        @Override
        public void onEnterRegion(IARegion Region) {
            if(Region.getType() == IARegion.TYPE_FLOOR_PLAN) {
                final String newID = Region.getId();
                //are we entering a new floor plan or coming back to floor plan
                if(mGroundOverlay == null || !Region.equals(mOveralFloorPlan)) {
                    mCameraPositionNeedsUpdating = true; // entering new fp, need to move camera
                    if (mGroundOverlay != null) {
                        mGroundOverlay.remove();
                        mGroundOverlay = null;
                    }
                    mOveralFloorPlan = Region;
                    fetchFloorPlan(newID);
                } else {
                    mGroundOverlay.setTransparency(0.0f);
                }
            }
        }

        @Override
        public void onExitRegion(IARegion iaRegion) {
            if(mGroundOverlay != null) {
                // Indicate we left this floor plan but leave it there for reference
                // If we enter another floor plan, this one will be removed and another one loaded
                mGroundOverlay.setTransparency(0.5f);
            }
        }
    };

    private void setupGroundOverlay(IAFloorPlan floorPlan, Bitmap bitmap) {
        if (mGroundOverlay != null) {
            mGroundOverlay.remove();
        }

        if(mMap != null) {
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
            IALatLng iaLatLng = floorPlan.getCenter();
            LatLng center = new LatLng(iaLatLng.latitude, iaLatLng.longitude);
            GroundOverlayOptions fpOverlay = new GroundOverlayOptions()
                    .image(bitmapDescriptor)
                    .position(center, floorPlan.getWidthMeters(), floorPlan.getHeightMeters())
                    .bearing(floorPlan.getBearing());

            mGroundOverlay = mMap.addGroundOverlay(fpOverlay);
        }
    }

    private void fetchFloorPlanBitmap(final IAFloorPlan floorPlan) {
        final String url = floorPlan.getUrl();

        if(mLoadTarget == null) {
            mLoadTarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Log.d(TAG, "onBitmap loaded with dimensions: " + bitmap.getWidth() + "x"
                            + bitmap.getHeight());
                    setupGroundOverlay(floorPlan, bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    showInfo("Failed to load bitmap");
                    mOveralFloorPlan = null;
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    //N/A
                }
            };
        }

        RequestCreator request = Picasso.with(this).load(url);

        request.into(mLoadTarget);
    }

    private void fetchFloorPlan(String id) {
        cancelPendingNetworkCalls();

        final IATask<IAFloorPlan> task = mResourceManager.fetchFloorPlanWithId(id);

        task.setCallback(new IAResultCallback<IAFloorPlan>() {
                @Override
                public void onResult(IAResult<IAFloorPlan> result) {
                    if(result.isSuccess() && result.getResult() != null) {
                        //retrieve bitmap for this floor plan metadata
                        fetchFloorPlanBitmap(result.getResult());
                    } else {
                        //ignore errors if this task was already canceled
                        if (!task.isCancelled()) {
                            showInfo("Loading floor plan failed : " + result.getError());
                            mOveralFloorPlan = null;
                        }
                    }
                }
        }, Looper.getMainLooper());

        //keep reference to task so that it can be canceled if needed
        mFetchFloorPlanTask = task;
    }
    private void cancelPendingNetworkCalls() {
        if (mFetchFloorPlanTask != null && !mFetchFloorPlanTask.isCancelled()) {
            mFetchFloorPlanTask.cancel();
        }
    }

    private void showInfo(String text) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), text,
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.button_close, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    public void onBeaconServiceConnect() {
        Identifier myBeaconNameID = Identifier.parse("");
        Identifier myBeaconInstID = Identifier.parse("");
        Region region = new Region("my-beacon-region", myBeaconNameID, myBeaconInstID, null);
        mBeaconManager.addMonitorNotifier(this);
        try {
            mBeaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void didEnterRegion(Region region) {
        Log.d(TAG, "I detected a beacon in the region with namespace id " + region.getId1() +
        " and instance id: " + region.getId2());

        Toast.makeText(this,"Beacon Detected", Toast.LENGTH_SHORT).show();

    }
    public void didExitRegion(Region region) {
        //.......N/A......
    }
    public void didDetermineStateForRegion(int state, Region region) {
        //.......N/A......
    }
}
