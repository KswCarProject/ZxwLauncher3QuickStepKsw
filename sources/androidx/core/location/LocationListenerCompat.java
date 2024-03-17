package androidx.core.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import java.util.List;

public interface LocationListenerCompat extends LocationListener {
    void onFlushComplete(int i) {
    }

    void onProviderDisabled(String str) {
    }

    void onProviderEnabled(String str) {
    }

    void onStatusChanged(String str, int i, Bundle bundle) {
    }

    void onLocationChanged(List<Location> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            onLocationChanged(list.get(i));
        }
    }
}
