package com.fizz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class NearbyBars extends AsyncTask<LatLng, Void, PlacePOI[]> {
	private URLConnection urlConnection;

	@Override
	protected PlacePOI[] doInBackground(LatLng... pos) {
		makeRequest(pos[0]);
		String result = aggregateResult();
		return processOutPut(result);
	}

	@Override
	protected void onPostExecute(PlacePOI[] results) {
		SplashActivity.addBars(results);
	}

	// Makes the request to the server
//	@SuppressWarnings("deprecation")
	private void makeRequest(LatLng pos) {
		String query = "key=REDACTED"
						+ "&location=" + pos.latitude+","+pos.longitude + "&radius=" + 5000
						+ "&sensor=true" + "&types=bar";
		URL url = null;
		try {
			url = new URL(
					"https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
							+ query);
			Log.e("Fizz",url.toString()) ;
		} catch (MalformedURLException e) {		e.printStackTrace();
		} // Will never happen.

		try {
			urlConnection = url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} // MIGHT happen
	}

	private String aggregateResult() {
		// Gather up the entire request into one string for
		// processing
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			while ((line = in.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.e("Fizz", sb.toString()) ;
		return sb.toString();
	}

	private PlacePOI[] processOutPut(String result) {
		PlacePOI[] placefinalresults = null;
		try {
			JSONObject json = new JSONObject(result);
			JSONArray results = json.getJSONArray("results");
			int numResults = results.length();
			placefinalresults = new PlacePOI[numResults];

			for (int i = 0; i < numResults; i++) {
				JSONObject r = results.getJSONObject(i);
				JSONObject latlng = r.getJSONObject("geometry").getJSONObject(
						"location");
				placefinalresults[i] = new PlacePOI(r.getString("name"),
						latlng.getDouble("lat"), latlng.getDouble("lng"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return placefinalresults;
	}

}
