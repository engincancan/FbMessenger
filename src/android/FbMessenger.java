package org.engin.test.FbMessenger;


import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.provider.Settings;

public class FbMessenger extends CordovaPlugin {
    public static final String TAG = "FbMessenger";

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("shareMethod")) {

        }
        else {
            return false;
        }
        return true;
    }



}
