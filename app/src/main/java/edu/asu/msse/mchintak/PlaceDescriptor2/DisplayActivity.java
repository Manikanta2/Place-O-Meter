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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayActivity extends AppCompatActivity implements  DialogInterface.OnClickListener{

    private ListView placeLV;
    public PlaceLibrary places;
    private String[] placeNames;
    private String[] colLabels;
    private int[] colIds;
    private List<HashMap<String,String>> fillMaps;

    private PlaceLibrary placeLibrary;

    private TextView distanceTV;
    private TextView bearingTV;

    private EditText nameET;
    private EditText descriptionET;
    private EditText addressET;
    private EditText categoryET;
    private EditText elevationET;
    private EditText latitudeET;
    private EditText longitudeET;
    private EditText nameBox;
    private  String selectedPlace;

    private ArrayList<String> placesList;
    private String destinationPlace;
    private String currentPlace;

    private EditText descriptionBox;
    private EditText addressBox;
    private EditText categoryBox;
    private EditText elevationBox;
    private EditText latitudeBox;
    private EditText longitudeBox;

    private Button button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_display);

        nameET = (EditText) findViewById(R.id.nameET);
        addressET = (EditText) findViewById(R.id.addressET);
        descriptionET = (EditText) findViewById(R.id.descriptionET);
        categoryET = (EditText) findViewById(R.id.categoryET);
        elevationET = (EditText) findViewById(R.id.elevationET);
        latitudeET = (EditText) findViewById(R.id.latitudeET);
        longitudeET = (EditText) findViewById(R.id.longitudeET);


        distanceTV = (TextView)findViewById(R.id.distanceTV);
        bearingTV = (TextView)findViewById(R.id.bearingTV);

        Intent intent = getIntent();
        places = intent.getSerializableExtra("places") != null ? (PlaceLibrary) intent.getSerializableExtra("places") :
                new PlaceLibrary(this);
        selectedPlace = intent.getStringExtra("selected") != null ? intent.getStringExtra("selected") : "unknown";
        final PlaceDescription aPlace = places.get(selectedPlace);
        nameET.setText(aPlace.name);
        addressET.setText(aPlace.address);
        descriptionET.setText(aPlace.description);
        categoryET.setText(aPlace.category);
        elevationET.setText(Double.toString(aPlace.elevation));
        latitudeET.setText(Double.toString(aPlace.latitude));
        longitudeET.setText(Double.toString(aPlace.longitude));

        placesList = new ArrayList<>(Arrays.asList(places.getNames()));



        Button button = (Button) findViewById(R.id.distance_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent distView = new Intent(DisplayActivity.this, DistAndBearing.class);
                distView.putExtra("name", aPlace.name);
                distView.putExtra("elevation", aPlace.elevation);
                distView.putExtra("longitude", aPlace.longitude);
                distView.putExtra("latitude", aPlace.latitude);
                distView.putExtra("places", places);
                startActivity(distView);
            }
        });





/*
        try {

            JSONObject obj = new JSONObject(place);

            String name = obj.getString("address-title");
            String description = obj.getString("description");
            String address = obj.getString("address-street");
            String category = obj.getString("category");
            String elevation = obj.getString("elevation");
            String latitude = obj.getString("latitude");
            String longitude = obj.getString("longitude");


            nameET.setText(name);
            addressET.setText(address);
            descriptionET.setText(description);
            categoryET.setText(category);
            elevationET.setText(elevation);
            latitudeET.setText(latitude);
            longitudeET.setText(longitude);

            Log.d("My App", obj.toString());



        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + place+ "\"");
        }



    */

        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(Exception ex){
            android.util.Log.d(this.getClass().getSimpleName(),"exception action bar: "+ex.getLocalizedMessage());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        android.util.Log.d(this.getClass().getSimpleName(), "called onCreateOptionsMenu()");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.place_display_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        android.util.Log.d(this.getClass().getSimpleName(), "called onOptionsItemSelected() id: "+item.getItemId()
                +" title "+item.getTitle());
        switch (item.getItemId()) {
            // the user selected the up/home button (left arrow at left of action bar)
            case android.R.id.home:
                android.util.Log.d(this.getClass().getSimpleName(),"onOptionsItemSelected -> home");
                Intent i = new Intent();
                i.putExtra("places", places);
                this.setResult(RESULT_OK,i);
                finish();
                return true;
            // the user selected the action (garbage can) to remove the place
            case R.id.action_remove:
                android.util.Log.d(this.getClass().getSimpleName(),"onOptionsItemSelected -> remove");
                this.removePlaceAlert();
                return true;

            case R.id.action_modify:
                android.util.Log.d(this.getClass().getSimpleName(),"onOptionsItemSelected -> modify");
                this.modifyPlaceAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void modifyPlaceAlert() {
        android.util.Log.d(this.getClass().getSimpleName(),"Inside modify");
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Place Name and details");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

            PlaceDescription aPlace = places.get(selectedPlace);


            this.descriptionBox = new EditText(this);
            descriptionBox.setHint(aPlace.description);
            layout.addView(descriptionBox);

            this.addressBox = new EditText(this);
            addressBox.setHint(aPlace.address);
            layout.addView(addressBox);

            this.categoryBox = new EditText(this);
            categoryBox.setHint(aPlace.category);
            layout.addView(categoryBox);

            this.elevationBox = new EditText(this);
            elevationBox.setHint(String.valueOf(aPlace.elevation));
            elevationBox.setInputType(InputType.TYPE_CLASS_NUMBER);
            layout.addView(elevationBox);

            this.latitudeBox = new EditText(this);
            latitudeBox.setHint(String.valueOf(aPlace.latitude));
            latitudeBox.setInputType(InputType.TYPE_CLASS_NUMBER);
            layout.addView(latitudeBox);

            this.longitudeBox = new EditText(this);
            longitudeBox.setHint(String.valueOf(aPlace.longitude));
            longitudeBox.setInputType(InputType.TYPE_CLASS_NUMBER);
            layout.addView(longitudeBox);

            categoryBox.setText(aPlace.category);
            descriptionBox.setText(aPlace.description);
            addressBox.setText(aPlace.address);
            String finalresult = new Double(aPlace.elevation).toString();
            elevationBox.setText(finalresult);
            String finalresult1 = new Double(aPlace.latitude).toString();
            latitudeBox.setText(finalresult1);
            String finalresult2 = new Double(aPlace.longitude).toString();
            longitudeBox.setText(finalresult2);

            dialog.setView(layout);
            dialog.setNegativeButton("Modify", this);
           // dialog.setPositiveButton("Modify", this);
            dialog.show();

    }

    private void removePlaceAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Place Name selected"+this.selectedPlace+"?");
        //dialog.setNegativeButton("Cancel", this);
        dialog.setPositiveButton("Remove", this);
        dialog.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int whichButton){
            android.util.Log.d(this.getClass().getSimpleName(), "onClick positive button? " +
                    (whichButton == DialogInterface.BUTTON_POSITIVE));
            if (whichButton == DialogInterface.BUTTON_POSITIVE) {
                places.remove(this.selectedPlace);
                Intent i = new Intent();
                i.putExtra("places", places);
                this.setResult(RESULT_OK, i);
                finish();
            }

            if (whichButton == DialogInterface.BUTTON_NEGATIVE) {
                android.util.Log.d(this.getClass().getSimpleName(),"Inside negative button");
                PlaceDescription aPlace = places.get(selectedPlace);

                String name = aPlace.name;
                String category = categoryBox.getText().toString();
                String description = descriptionBox.getText().toString();
                String address = addressBox.getText().toString();
                Double elevation = Double.parseDouble(elevationBox.getText().toString());
                Double latitude = Double.parseDouble(latitudeBox.getText().toString());
                Double longitude = Double.parseDouble(longitudeBox.getText().toString());


                places.modify(new PlaceDescription(name, description, address, category, elevation, latitude, longitude));
                //prepareAdapter();
               // SimpleAdapter sa = new SimpleAdapter(this, fillMaps, R.layout.place_list_item, colLabels, colIds);
               // placeLV.setAdapter(sa);

                Intent i = new Intent();
                i.putExtra("places", places);
                this.setResult(RESULT_OK, i);
                finish();
            }
    }

    private void prepareAdapter() {

        colLabels = this.getResources().getStringArray(R.array.col_header);
        colIds = new int[] {R.id.place_list_item_TV};
        this.placeNames = places.getNames();
        Arrays.sort(this.placeNames);
        fillMaps = new ArrayList<HashMap<String,String>>();
        HashMap<String,String> titles = new HashMap<>();
        // first row contains column headers
        titles.put("Name","Name");
        fillMaps.add(titles);
        // fill in the remaining rows with first last and student id.
        for (int i = 0; i < placeNames.length; i++) {
            String firstNLast = placeNames[i];
            HashMap<String,String> map = new HashMap<>();
            map.put("Name", firstNLast);
            android.util.Log.w(this.getClass().getSimpleName(),"Place has name"+placeNames[i]);
            fillMaps.add(map);
        }
    }



}