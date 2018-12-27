package id.net.gmedia.selby.Util;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONBuilder {
    private JSONObject object = new JSONObject();

    public void add(String key, String value){
        try{
            object.put(key, value);
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.e("JSONBuilder", e.getMessage());
        }
    }

    public void add(String key, float value){
        try{
            object.put(key, value);
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.e("JSONBuilder", e.getMessage());
        }
    }


    public void add(String key, int value){
        try{
            object.put(key, value);
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.e("JSONBuilder", e.getMessage());
        }
    }

    public void add(String key, double value){
        try{
            object.put(key, value);
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.e("JSONBuilder", e.getMessage());
        }
    }

    public void add(String key, Uri value){
        try{
            object.put(key, value);
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.e("JSONBuilder", e.getMessage());
        }
    }

    public void add(String key, JSONArray value){
        try{
            object.put(key, value);
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.e("JSONBuilder", e.getMessage());
        }
    }


    public void add(String key, boolean value){
        try{
            object.put(key, value ? "1" : "0");
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.e("JSONBuilder", e.getMessage());
        }
    }

    public JSONObject create(){
        return object;
    }
}
