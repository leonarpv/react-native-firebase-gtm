package com.medium.react_native_firebase_gtm;


import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.Map;

public class ReactNativeFirebaseRemoteConfig extends ReactContextBaseJavaModule {
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    public ReactNativeFirebaseRemoteConfig(ReactApplicationContext reactContext) {
        super(reactContext);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    }

    private final String TAG = "firebase-remote-config";

    /**
     * Fetch remote config value from Firebase
     * Conversion Warning: param cacheTime is passed as int (because react-native doesn't recognize long),
     *                     but mFirebaseRemoteConfig.fetch actually takes long as parameter
     *
     * @param cacheTime cache time in seconds
     */
    @ReactMethod
    public void fetchRemoteConfig(int cacheTime) {
        mFirebaseRemoteConfig.fetch(cacheTime)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                            Log.d(TAG, "Fetched remote config.");
                        } else {
                            Log.e(TAG, "Fetch remote config: failed.");
                        }
                    }
                });
    }

    /**
     * Pass a map-like object as firebase remote config defaults
     * @param parameter javascript object
     */
    @ReactMethod
    public void setDefaults(ReadableMap parameter) {
        Map<String, Object> defaults = ConversionUtils.toMap(parameter);
        mFirebaseRemoteConfig.setDefaults(defaults);
    }

    /**
     * Return the remote config value (as String) for given key
     * @param key The name of the key
     * @param callback callback to be invoked with returned value
     */
    @ReactMethod
    public void getString(String key, Callback callback) {
        String value = mFirebaseRemoteConfig.getString(key);
        callback.invoke(value);
    }

    /**
     * Return the remote config value (as Boolean) for given key
     * @param key The name of the key
     * @param callback callback to be invoked with returned value
     */
    @ReactMethod
    public void getBoolean(String key, Callback callback) {
        boolean value = mFirebaseRemoteConfig.getBoolean(key);
        callback.invoke(value);
    }

    @Override
    public String getName() {
        return "ReactNativeFirebaseRemoteConfig";
    }
}
