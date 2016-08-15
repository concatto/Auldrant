package br.concatto.cartopographer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnMapReadyCallback {
	private static final String SERVER_URL = "http://grandsheets.herokuapp.com/platz.php";
	private Button searchButton;
	private GoogleMap map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		
		searchButton = (Button) findViewById(R.id.searchButton);
		searchButton.setEnabled(false);
		searchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openUpYourSenses();
			}
		});
		
		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	private void openUpYourSenses() {
		if (map == null) return;
		
		AsyncTask<LatLngBounds, Void, MarkerOptions[]> task = new AsyncTask<LatLngBounds, Void, MarkerOptions[]>() {
			@Override
			protected MarkerOptions[] doInBackground(LatLngBounds... params) {
				LatLngBounds bounds = params[0];
				String query = String.format(Locale.US, "?startLatitude=%.5f&endLatitude=%.5f&startLongitude=%.5f&endLongitude=%.5f",
						bounds.southwest.latitude, bounds.northeast.latitude, bounds.southwest.longitude, bounds.northeast.longitude);
				
				try {
					HttpURLConnection conn = (HttpURLConnection) new URL(SERVER_URL + query).openConnection();
					BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String response = in.readLine();
					JSONArray json = new JSONArray(response);
					
					JSONObject obj;
					MarkerOptions[] markers = new MarkerOptions[json.length()];
					for (int i = 0; i < json.length(); i++) {
						obj = json.getJSONObject(i);
						markers[i] = new MarkerOptions()
								.position(new LatLng(obj.getDouble("latitude"), obj.getDouble("longitude")))
								.title(obj.getString("name"));
					}
					
					in.close();
					return markers;
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(MarkerOptions[] result) {
				for (MarkerOptions markerOptions : result) {
					map.addMarker(markerOptions);
				}
			}
		};
		
		task.execute(map.getProjection().getVisibleRegion().latLngBounds);
	}

	@Override
	public void onMapReady(final GoogleMap map) {
		this.map = map;
		searchButton.setEnabled(true);
		
		map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.0272, -111.0225), 13.2f));
		
		map.setOnMapLongClickListener(new OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng point) {
				insertPlace(point);
			}
		});
	}

	private void insertPlace(final LatLng point) {
		LayoutInflater inflater = getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.insertion_dialog, null);
		
		final EditText nameEdit = (EditText) dialogView.findViewById(R.id.insertionName);
		final EditText categoryEdit = (EditText) dialogView.findViewById(R.id.insertionCategory);
		TextView latitudeView = (TextView) dialogView.findViewById(R.id.insertionLatitude);
		TextView longitudeView = (TextView) dialogView.findViewById(R.id.insertionLongitude);
		
		final String latitude = String.format(Locale.US, "%.5f", point.latitude);
		final String longitude = String.format(Locale.US, "%.5f", point.longitude);
		
		latitudeView.setText(String.valueOf(latitude));
		longitudeView.setText(String.valueOf(longitude));
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setView(dialogView);
		dialog.setNegativeButton("Cancelar", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		dialog.setPositiveButton("Confirmar", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = nameEdit.getText().toString();
				String category = categoryEdit.getText().toString();
				
				AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
					@Override
					protected String doInBackground(String... params) {
						if (params.length != 4) {
							return "execute() com dados inválidos";
						}
						
						String query = String.format(Locale.getDefault(), "name=%s&category=%s&latitude=%s&longitude=%s", params[0], params[1], params[2], params[3]);
						
						try {
							HttpURLConnection conn = (HttpURLConnection) new URL(SERVER_URL).openConnection();
							conn.setDoOutput(true);
							OutputStream out = new BufferedOutputStream(conn.getOutputStream());
							out.write(query.getBytes());
							out.flush();
							
							BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
							String response = in.readLine();
							
							in.close();
							out.close();

							return response != null ? response : "Sound of silence";
						} catch (MalformedURLException e) {
							return "URL errada";
						} catch (IOException e) {
							return "IOException: " + e.getMessage();
						}
					}
					
					@Override
					protected void onPostExecute(String result) {
						Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
					}
				};
				
				task.execute(name, category, latitude, longitude);
			}
		});
		
		dialog.create().show();
	}
}
