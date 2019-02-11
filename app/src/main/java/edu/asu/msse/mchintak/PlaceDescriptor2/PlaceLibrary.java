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

import android.app.Activity;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PlaceLibrary extends Object implements Serializable {

    public Hashtable<String,PlaceDescription> places;
    private static final boolean debugOn = false;
    static public List<PlaceDescription> placeObjs = new ArrayList<>();

    public PlaceLibrary(Activity parent) {
        debug("creating a new place library");
        places = new Hashtable<String, PlaceDescription>();
        try {
            this.resetFromJsonFile(parent);
        } catch (Exception ex) {
            android.util.Log.d(this.getClass().getSimpleName(), "error resetting");
        }
    }

    private boolean resetFromJsonFile(Activity parent) {
        boolean ret =true;
        try {
            places.clear();
            InputStream is = parent.getApplicationContext().getResources().openRawResource(R.raw.places);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            StringBuffer sb = new StringBuffer();
            while (br.ready()) {
                sb.append(br.readLine());
            }
            String placeJsonStr = sb.toString();
            JSONObject placesJson = new JSONObject(new JSONTokener(placeJsonStr));
            Iterator<String> it = placesJson.keys();
            while (it.hasNext()) {
                String sName = it.next();
                JSONObject aPlace = placesJson.optJSONObject(sName);
                debug("importing place named " + sName + " json is: " + aPlace.toString());
                if (aPlace != null) {
                    PlaceDescription stu = new PlaceDescription(aPlace.toString());
                    places.put(sName, stu);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            ret = false;
        } catch (JSONException e) {
            e.printStackTrace();
            ret = false;
        }

        return ret;
    }

    public Map<String, String> getGreatCircleDist(String currentPlace, String destinationPlace){
        List<String> result = new ArrayList<String>();
        Map<String, String> map = new HashMap<String, String>();
        Double radius = 3961d;

        //android.util.Log.d(this.getClass().getSimpleName(),String.valueOf(places.get(currentPlace).longitude));
        Double lon1 = Double.parseDouble(String.valueOf(places.get(currentPlace).longitude));
        Double lat1 = Double.parseDouble(String.valueOf(places.get(currentPlace).latitude));

        Double lon2 = Double.parseDouble(String.valueOf(places.get(destinationPlace).longitude));
        Double lat2 = Double.parseDouble(String.valueOf(places.get(destinationPlace).latitude));

        Double dlon = lon2 - lon1;
        Double dlat = lat2 - lat1;

        Double a = Math.pow((Math.sin(dlat / 2)), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow((Math.sin(dlon / 2)), 2);
        Double c = 2* Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double distance = radius * c;

        Double degToRad = Math.PI / 180.0;
        Double phil1 = lat1 * degToRad;
        Double phil2 = lat2 * degToRad;
        Double lam1 = lon1 * degToRad;
        Double lam2 = lon2 * degToRad;

        Double initial_bearing = Math.atan2(Math.sin(lam2 - lam1) * Math.cos(phil1), Math.cos(phil1) * Math.sin(phil2) - Math.sin(phil1) * Math.cos(phil2) * Math.cos(lam2 - lam1)) * 180 / Math.PI;

        map.put("distance", distance+"");
        map.put("initial_bearing", initial_bearing+"");

        return map;
    }
    private void debug(String message) {
        if (debugOn)
            android.util.Log.d(this.getClass().getSimpleName(),"debug:");
    }

    public String[] getNames() {
        String[] ret = {};
        debug("getting " + places.size() + " place names.");
        if (places.size() > 0) {
            ret = (places.keySet()).toArray(new String[0]);
        }
        return ret;
    }

    public boolean add(PlaceDescription placeDescription) {

        boolean ret = true;
        debug("adding Place named: " + ((placeDescription == null) ? "unknown" : placeDescription.name));
        try {
            places.put(placeDescription.name, placeDescription);
        } catch (Exception ex) {
            ret = false;
        }
        return ret;
    }

    public boolean modify(PlaceDescription placeDescription) {

        boolean ret = true;
        debug("modifying Place named: " + ((placeDescription == null) ? "unknown" : placeDescription.name));
        try {
            places.put(placeDescription.name, placeDescription);
        } catch (Exception ex) {
            ret = false;
        }
        return ret;
    }

    public PlaceDescription getPlaceFromName(String name) {
        for(PlaceDescription place :placeObjs)
        {
            if(place.name == name)
                return place;
        }
        return null;
    }

    public PlaceDescription get(String aName) {

        PlaceDescription ret = new PlaceDescription("unknown", "unknown", "unknown", "unknown", 0.0, 0.0, 0.0);
        PlaceDescription aPlace = places.get(aName);
        if (aPlace != null) {
            ret = aPlace;
        }
        return ret;
    }

    public boolean remove(String selectedPlace) {

        debug("removing place named: " + selectedPlace);
        android.util.Log.d(this.getClass().getSimpleName(),"removing place named: " + selectedPlace);
        return ((places.remove(selectedPlace) == null) ? false: true);
    }
}
