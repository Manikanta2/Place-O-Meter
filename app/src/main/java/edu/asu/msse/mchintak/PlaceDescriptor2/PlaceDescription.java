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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


public class PlaceDescription implements Serializable {

    private static final boolean debugOn = false;

    public String name;
    public String description;
    public String address;
    public String category;
    public Double elevation;
    public Double latitude;
    public Double longitude;

    public PlaceDescription(String name, String description, String address, String category, Double elevation, Double latitude, Double longitude){
        this.name = name;
        this.description = description;
        this.category = category;
        this.address = address;
        this.elevation = elevation;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public PlaceDescription(String jsonStr) {
        JSONObject jo = null;
        try {
            jo = new JSONObject(jsonStr);
            name = jo.getString("name");
            description = jo.getString("description");
            address = jo.getString("address-street");
            category = jo.getString("category");
            elevation = jo.getDouble("elevation");
            latitude = jo.getDouble("latitude");
            longitude = jo.getDouble("longitude");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public PlaceDescription(JSONObject jsonObj){
        try{
            debug("constructor from json received: " + jsonObj.toString());
            name = jsonObj.optString("name","unknown");
            description = jsonObj.optString("description","unknown");
            address = jsonObj.optString("address","unknown");
            category = jsonObj.optString("category","unknown");
            elevation = jsonObj.optDouble("elevation",0.0);
            latitude = jsonObj.optDouble("latitude",0.0);
            longitude = jsonObj.optDouble("longitude", 0.0);
        }catch(Exception ex){
            android.util.Log.d(this.getClass().getSimpleName(), "error getting Student from json string");
        }
    }

    public JSONObject toJson(){
        JSONObject jo = new JSONObject();
        try{
            jo.put("name",name);
            jo.put("description",description);
            jo.put("address",address);
            jo.put("category",category);
            jo.put("elevation",elevation);
            jo.put("latitude",latitude);
            jo.put("longitude",longitude);
        }catch (Exception ex){
            android.util.Log.d(this.getClass().getSimpleName(), "error getting Place from json string");
        }
        return jo;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Place ").append(name).append(" has description ");
        sb.append(description).append(" has address ");
        sb.append(address).append("has category").append(category);
        sb.append("has elevation").append(elevation).append("has lat");
        sb.append(latitude).append("has longitude").append(longitude);
        return sb.toString();
    }
    private void debug(String message) {
        if (debugOn)
            android.util.Log.d(this.getClass().getSimpleName(), message);
    }

    public String toJsonString() {
        String ret = "";
        try {
            JSONObject jo = new JSONObject();
            jo.put("name", name);
            jo.put("description", description);
            jo.put("address", address);
            jo.put("category", category);
            jo.put("elevation", elevation);
            jo.put("latitude",latitude);
            jo.put("longitude",longitude);

            ret = jo.toString();
        } catch (Exception ex) {
            android.util.Log.w(this.getClass().getSimpleName(),"error converting to/from json");
        }
        return ret;
    }
}

