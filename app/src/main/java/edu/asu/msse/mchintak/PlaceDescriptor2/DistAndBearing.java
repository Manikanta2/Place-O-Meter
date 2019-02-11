package edu.asu.msse.mchintak.PlaceDescriptor2;

/*
 * Copyright 2019 Manikanta Chintakunta,
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * Right to use :  I give every person the right to  build and evaluate
 * the software package for the purpose of determining me grade and program assessment.
 *
 * Purpose: For displaying the description of a place which includes address, category, elevation, latitude, longitude.
 * It also calculates the Great Circle Distance and Initial bearing between any two places.
 *
 * @author Manikanta Chintakunta
 * mailto:mchintak@asu.edu

 * @version February 04, 2019
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class DistAndBearing extends AppCompatActivity implements  AdapterView.OnItemSelectedListener {
    Double elevation;
    Double longitude;
    Double latitude;
    TextView name;

    Spinner dropdown;
    PlaceLibrary placeLib;
    TextView dist;
    TextView bearing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_and_bearing);
        dist = findViewById(R.id.distanceID);
        bearing = findViewById(R.id.bearingID);
        elevation = (Double)getIntent().getSerializableExtra("elevation");
        longitude = (Double)getIntent().getSerializableExtra("longitude");
        latitude = (Double)getIntent().getSerializableExtra("latitude");
        name = findViewById(R.id.distFromID);
        dropdown = findViewById(R.id.spinner);
        name.setText((String)getIntent().getSerializableExtra("name"));
        placeLib = (PlaceLibrary)getIntent().getSerializableExtra("places");
        String[] otherPlaces = placeLib.getNames();
        ArrayAdapter<String> placesDropdown = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, otherPlaces);
        dropdown.setAdapter(placesDropdown);
        dropdown.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedPlace = dropdown.getSelectedItem().toString();
        PlaceDescription place2 = placeLib.get(selectedPlace);
        dist.setText("Great Circle Distance: " + calcDistance(longitude, latitude, place2.latitude, place2.longitude));
        bearing.setText("Great Circle Bearing: " + calcBearing(longitude,latitude, place2.latitude, place2.longitude));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    public double calcBearing(double lon1, double lat1, double lat2, double lon2) {
        Double lon1Rads = Math.toRadians(lat1);
        Double lon2Rads = Math.toRadians(lat2);
        Double distLon = Math.toRadians(lon2-lon1);
        double y = Math.sin(distLon) * Math.cos(lon2Rads);
        double x = Math.cos(lon1Rads) * Math.sin(lon2Rads) - Math.sin(lon1Rads)*Math.cos(lon2Rads) * Math.cos(distLon);
        return Math.toDegrees((Math.atan2(y, x)));
    }

    public double calcDistance(double lon1, double lat1, double lat2, double lon2) {
        Double earthRadius = 6371.0;
        Double lon1Rads = Math.toRadians(lat1);
        Double lon2Rads = Math.toRadians(lat2);
        Double distLat = Math.toRadians(lat2-lat1);
        Double distLon = Math.toRadians(lon2-lon1);
        double calcedDist = Math.sin(distLat/2) * Math.sin(distLat/2) + Math.cos(lon1Rads) * Math.cos(lon2Rads)
                * Math.sin(distLon/2) * Math.sin(distLon/2);
        double calcDist = 2 * Math.atan2(Math.sqrt(calcedDist), Math.sqrt(1-calcedDist));
        System.out.println("selected: " + lon1 + " " + lon2 + " " + lat1 + " " + lat2);
        return earthRadius * calcDist;
    }
}

