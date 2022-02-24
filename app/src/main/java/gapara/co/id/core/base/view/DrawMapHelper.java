package gapara.co.id.core.base.view;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import gapara.co.id.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DrawMapHelper {

    private Activity activity;
    private GoogleMap mMap;
    public LatLng mOrigin;
    public LatLng mDestination;
    private Polyline mPolyline;
    public ArrayList<LatLng> mMarkerPoints = new ArrayList<>();

    public DrawMapHelper(Activity activity, GoogleMap mMap) {
        this.activity = activity;
        this.mMap = mMap;
    }

    public void drawRoute() {

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(mOrigin, mDestination);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Key
        String key = "key=" + activity.getString(R.string.google_maps_key);

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception on download", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to download data from Google Directions URL
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask", "DownloadTask : " + data);

                if (url != null && url.length > 0) {
                    for (int i = 0; i < url.length; i++) {
                        Log.e("DownloadTask", "wwwwwwwww : " + url[i]);
                    }
                }
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Directions in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            Log.e("result", "bro " + result);
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                if (mPolyline != null) {
                    mPolyline.remove();
                }
                mPolyline = mMap.addPolyline(lineOptions);

            } else {
                Toast.makeText(activity, "No route is found", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void customDraw(Location currentLocation) { // sample after purchase directions
        Log.e("customDraw", "lat: " + currentLocation.getLatitude() + " long: " + currentLocation.getLongitude());
        ArrayList<LatLng> points = new ArrayList<>();
        PolylineOptions lineOptions = new PolylineOptions();

        LatLng position1 = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        points.add(position1);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_app));
        markerOptions.position(position1);
        mMap.addMarker(markerOptions);

        LatLng lastPositionLatLng = new LatLng(0.0, 0.0);
        for (int index = 10; index < 35; index++) {
            Location loc = currentLocationPlus(currentLocation, index);
            LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());
            points.add(pos);
            lastPositionLatLng = pos;
        }

        MarkerOptions lastPositionMarkerOptions = new MarkerOptions();
        lastPositionMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_app));
        lastPositionMarkerOptions.position(lastPositionLatLng);
        mMap.addMarker(lastPositionMarkerOptions);

        // Adding all the points in the route to LineOptions
        lineOptions.addAll(points);
        lineOptions.width(8);
        lineOptions.color(Color.RED);
        if (mPolyline != null) {
            mPolyline.remove();
        }
        mPolyline = mMap.addPolyline(lineOptions);
    }

    //http://cariprogram.blogspot.com/2013/01/mencari-jarak-dua-titik-gps-latitude-longitude-android-mysql-xml-json.html
    private void getDistance(Location currentLocation, Location destinationLocation) {

        double calculateCurrentLatitudeWithDestinationLatitude = Math.cos(Math.toRadians(destinationLocation.getLatitude())) *
                Math.cos(Math.toRadians(currentLocation.getLatitude()));

        double calculateCurrentLongitudeWithDestinationLongitude = Math.cos(Math.toRadians(currentLocation.getLongitude()) -
                Math.toRadians(destinationLocation.getLongitude()));

        double distance = 6371 * Math.acos(calculateCurrentLatitudeWithDestinationLatitude) *

                calculateCurrentLongitudeWithDestinationLongitude +

                Math.sin(Math.toRadians(destinationLocation.getLatitude())) *
                        Math.sin(Math.toRadians(currentLocation.getLatitude()));
    }

    private Location currentLocationPlus(Location currentLocation, double meters) {

        // number of km per degree = ~111km (111.32 in google maps, but range varies
        // between 110.567km at the equator and 111.699km at the poles)
        // 1km in degree = 1 / 111.32km = 0.0089
        // 1m in degree = 0.0089 / 1000 = 0.0000089
        double coef = meters * 0.0000089;

        double new_lat = currentLocation.getLatitude() + coef;

        // pi / 180 = 0.018
        double new_long = currentLocation.getLongitude() + coef / Math.cos(currentLocation.getLatitude() * 0.018);
        currentLocation.setLatitude(new_lat);
        currentLocation.setLongitude(new_long);
        return currentLocation;
    }
}