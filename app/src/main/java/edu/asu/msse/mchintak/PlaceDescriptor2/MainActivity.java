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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener, DialogInterface.OnClickListener {

    private ListView placeLV;
    public PlaceLibrary places;
    private String[] placeNames;
    private String[] colLabels;
    private int[] colIds;
    private List<HashMap<String,String>> fillMaps;
    private EditText nameBox;
    private EditText descriptionBox;
    private EditText addressBox;
    private EditText categoryBox;
    private EditText elevationBox;
    private EditText latitudeBox;
    private EditText longitudeBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        placeLV = (ListView)findViewById(R.id.place_list_view);

        places = new PlaceLibrary(this);
        placeNames = places.getNames();

        this.prepareAdapter();
        SimpleAdapter sa = new SimpleAdapter(this, fillMaps, R.layout.place_list_item, colLabels, colIds);
        placeLV.setAdapter(sa);
        placeLV.setOnItemClickListener(this);

        setTitle("Places");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        android.util.Log.d(this.getClass().getSimpleName(), "called onCreateOptionsMenu()");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        android.util.Log.d(this.getClass().getSimpleName(), "called onOptionsItemSelected()");
        switch (item.getItemId()) {
            case R.id.action_add:
                android.util.Log.d(this.getClass().getSimpleName(),"onOptionsItemSelected -> add");
                this.newPlaceAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            places = data.getSerializableExtra("places") != null ? (PlaceLibrary) data.getSerializableExtra("places") : places;
            this.prepareAdapter();
            SimpleAdapter sa = new SimpleAdapter(this, fillMaps, R.layout.place_list_item, colLabels, colIds);
            placeLV.setAdapter(sa);
            placeLV.setOnItemClickListener(this);
        }
    }


    private void newPlaceAlert() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Place Name and details");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        this.nameBox = new EditText(this);
        nameBox.setHint("Name");
        layout.addView(nameBox);

        this.descriptionBox = new EditText(this);
        descriptionBox.setHint("Description");
        layout.addView(descriptionBox);

        this.addressBox = new EditText(this);
        addressBox.setHint("Address");
        layout.addView(addressBox);

        this.categoryBox = new EditText(this);
        categoryBox.setHint("Category");
        layout.addView(categoryBox);

        this.elevationBox = new EditText(this);
        elevationBox.setHint("Elevation");
        elevationBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(elevationBox);

        this.latitudeBox = new EditText(this);
        latitudeBox.setHint("Latitude");
        latitudeBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(latitudeBox);

        this.longitudeBox = new EditText(this);
        longitudeBox.setHint("Longitude");
        longitudeBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(longitudeBox);

        dialog.setView(layout);
        dialog.setNegativeButton("Cancel", this);
        dialog.setPositiveButton("Add", this);
        dialog.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int whichButton) {
        android.util.Log.d(this.getClass().getSimpleName(),"onClick positive button? "+
                (whichButton==DialogInterface.BUTTON_POSITIVE));
        if(whichButton == DialogInterface.BUTTON_POSITIVE) {
            String name = nameBox.getText().toString();
            String category = categoryBox.getText().toString();
            String address = addressBox.getText().toString();
            String description  = descriptionBox.getText().toString();
            Double elevation = elevationBox.getText().toString().equals("") ? 0 : Double.parseDouble(elevationBox.getText().toString());
            Double latitude = latitudeBox.getText().toString().equals("") ? 0 : Double.parseDouble(latitudeBox.getText().toString());
            Double longitude = longitudeBox.getText().toString().equals("") ? 0 : Double.parseDouble(longitudeBox.getText().toString());

            places.add(new PlaceDescription(name, description, address, category, elevation, latitude, longitude));
            prepareAdapter();
            SimpleAdapter sa = new SimpleAdapter(this, fillMaps, R.layout.place_list_item, colLabels, colIds);
            placeLV.setAdapter(sa);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String[] placeNames = places.getNames();
        Arrays.sort(placeNames);
        if(position > 0 && position <= placeNames.length) {
            android.util.Log.d(this.getClass().getSimpleName(), "in method onItemClick. selected: " + placeNames[position-1]);
            Intent displayPlace = new Intent(this, DisplayActivity.class);
            displayPlace.putExtra("places", places);
            displayPlace.putExtra("selected", placeNames[position-1]);
            this.startActivityForResult(displayPlace, 1);
        }

    }

}
