package org.apache.cordova.replayer;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;

public class Oauth extends CordovaPlugin {
    @Override
    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
        if(action.equals("open")) {
            callbackContext.success("hello world");
        }

        return true;
    }
}