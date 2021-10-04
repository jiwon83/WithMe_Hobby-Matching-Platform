package com.cookandroid.withmetabbar.googlemap;

import com.cookandroid.withmetabbar.model.Meet;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public interface LatLngCallback {
//    void latLangCall(ArrayList<LatLng> list, ArrayList<Meet> meets);
    void latLangCall( ArrayList<Meet> meets);
}
