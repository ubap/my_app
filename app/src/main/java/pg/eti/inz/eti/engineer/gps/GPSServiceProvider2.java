package pg.eti.inz.eti.engineer.gps;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.app.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * https://developer.android.com/reference/android/location/LocationManager.html
 */

public class GPSServiceProvider2 {

    private static GPSServiceProvider2 instance;

    private CoreService.GPSBinder mService;
    private Boolean mBound = false;

    public static GPSServiceProvider2 getInstance() {
        if (instance == null) {
            instance = new GPSServiceProvider2();
        }
        return instance;
    }

    public void init(Activity context) {
        Intent intent = new Intent(context, CoreService.class);
        context.bindService(intent, mConnection, 0);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = (CoreService.GPSBinder) service;
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public Location getLocation() {
        if (mBound) {
            return mService.getLocation();
        } else {
            return null;
        }
    }

    public List<Location>getPath() {
        if (mBound) {
            return mService.getPath();
        } else {
            return null;
        }
    }

    public double getPathLength() {
        if (mBound) {
            return mService.getPathLength();
        } else {
            return 0;
        }
    }

}
