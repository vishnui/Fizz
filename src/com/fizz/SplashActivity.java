package com.fizz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SplashActivity extends Activity {

	private static MapFragment mapView;
	private static GoogleMap map;
	private static BitmapDescriptor bd;
	private WifiManager wm ;
	
	public static Typeface tp ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		initMaps();
		getMapReference();
		applyRobotoFont();
		getBarIcon();
//		getWifiInfo() ;
	}

	private void initMaps() {
		// Initalize Google maps
		try {
			MapsInitializer.initialize(getApplicationContext());
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
			Toast.makeText(this, "No Play Services Available",
					Toast.LENGTH_LONG).show();
			finish();
		}
	}

	private void getBarIcon() {
		bd = BitmapDescriptorFactory.fromResource(R.drawable.bar);
	}

	private void applyRobotoFont() {
		TextView gotobar = ((TextView) findViewById(R.id.gotobar));
		tp = Typeface.createFromAsset(getAssets(),
				"Roboto-Light.ttf") ;
		gotobar.setTypeface(tp);
	}
	
	public void go(View view){
		Intent intent = new Intent(this, MenuActivity.class) ;
		startActivity(intent) ;
		finish() ;
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	}

	private void getMapReference() {
		mapView = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.map));
		map = mapView.getMap();

		map.setBuildingsEnabled(true);
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
			@Override
			public void onMyLocationChange(Location location) {
				LatLng pos = new LatLng(location.getLatitude(), location.getLongitude()) ;
				CameraPosition cp = new CameraPosition(pos, 12, 90, 0) ;
				CameraUpdate update = CameraUpdateFactory.newCameraPosition(cp) ;
				map.animateCamera(update) ;
//				new NearbyBars().execute(pos) ;
			}
		}) ;
	}
	
	private void getWifiInfo() {
		wm = (WifiManager) getSystemService(WIFI_SERVICE);
		if(wm == null) finish() ;
		if(wm.isWifiEnabled()) {
			Log.e("WIFI SSID",wm.getConnectionInfo().getSSID()) ;
			if(wm.getConnectionInfo().getSSID().equals("\"UIUCnet\"")){
				Log.e("WIFI SSID",wm.getConnectionInfo().getSSID()) ;
				Intent intent = new Intent(this, MenuActivity.class) ;
				startActivity(intent) ;
				finish() ;
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			}
		}
		else {
			Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK) ;
			startActivityForResult(intent, 122245) ;
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 122245)	getWifiInfo() ;
	}

	public static void addBars(PlacePOI[] results) {
		if(results == null) return ;
		map.clear() ;
		for (int i = 0; i < results.length; i++) {
			map.addMarker(new MarkerOptions()
					.icon(bd)
					.anchor(0.5f, 0.5f)
					.flat(false)
					.title(results[i].name)
					.draggable(false)
					.position(
							new LatLng(results[i].latitude,
									results[i].longitude)));
		}
	}
}
