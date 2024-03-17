package androidx.core.location;

import android.content.Context;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.collection.SimpleArrayMap;
import androidx.core.location.GnssStatusCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.core.os.CancellationSignal;
import androidx.core.os.ExecutorCompat;
import androidx.core.util.Consumer;
import androidx.core.util.ObjectsCompat;
import androidx.core.util.Preconditions;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

public final class LocationManagerCompat {
    private static final long GET_CURRENT_LOCATION_TIMEOUT_MS = 30000;
    private static final long MAX_CURRENT_LOCATION_AGE_MS = 10000;
    private static final long PRE_N_LOOPER_TIMEOUT_S = 5;
    private static Field sContextField;
    static final WeakHashMap<LocationListenerKey, WeakReference<LocationListenerTransport>> sLocationListeners = new WeakHashMap<>();

    public static boolean isLocationEnabled(LocationManager locationManager) {
        if (Build.VERSION.SDK_INT >= 28) {
            return Api28Impl.isLocationEnabled(locationManager);
        }
        if (Build.VERSION.SDK_INT <= 19) {
            try {
                if (sContextField == null) {
                    Field declaredField = LocationManager.class.getDeclaredField("mContext");
                    sContextField = declaredField;
                    declaredField.setAccessible(true);
                }
                Context context = (Context) sContextField.get(locationManager);
                if (context != null) {
                    if (Build.VERSION.SDK_INT != 19) {
                        return !TextUtils.isEmpty(Settings.Secure.getString(context.getContentResolver(), "location_providers_allowed"));
                    }
                    if (Settings.Secure.getInt(context.getContentResolver(), "location_mode", 0) != 0) {
                        return true;
                    }
                    return false;
                }
            } catch (ClassCastException | IllegalAccessException | NoSuchFieldException | SecurityException unused) {
            }
        }
        if (locationManager.isProviderEnabled("network") || locationManager.isProviderEnabled("gps")) {
            return true;
        }
        return false;
    }

    public static boolean hasProvider(LocationManager locationManager, String str) {
        if (Build.VERSION.SDK_INT >= 31) {
            return Api31Impl.hasProvider(locationManager, str);
        }
        if (locationManager.getAllProviders().contains(str)) {
            return true;
        }
        try {
            if (locationManager.getProvider(str) != null) {
                return true;
            }
            return false;
        } catch (SecurityException unused) {
            return false;
        }
    }

    public static void getCurrentLocation(LocationManager locationManager, String str, CancellationSignal cancellationSignal, Executor executor, Consumer<Location> consumer) {
        if (Build.VERSION.SDK_INT >= 30) {
            Api30Impl.getCurrentLocation(locationManager, str, cancellationSignal, executor, consumer);
            return;
        }
        if (cancellationSignal != null) {
            cancellationSignal.throwIfCanceled();
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(str);
        if (lastKnownLocation == null || SystemClock.elapsedRealtime() - LocationCompat.getElapsedRealtimeMillis(lastKnownLocation) >= MAX_CURRENT_LOCATION_AGE_MS) {
            CancellableLocationListener cancellableLocationListener = new CancellableLocationListener(locationManager, executor, consumer);
            locationManager.requestLocationUpdates(str, 0, 0.0f, cancellableLocationListener, Looper.getMainLooper());
            if (cancellationSignal != null) {
                Objects.requireNonNull(cancellableLocationListener);
                cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
                    public final void onCancel() {
                        LocationManagerCompat.CancellableLocationListener.this.cancel();
                    }
                });
            }
            cancellableLocationListener.startTimeout(GET_CURRENT_LOCATION_TIMEOUT_MS);
            return;
        }
        executor.execute(new Runnable(lastKnownLocation) {
            public final /* synthetic */ Location f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                Consumer.this.accept(this.f$1);
            }
        });
    }

    public static void requestLocationUpdates(LocationManager locationManager, String str, LocationRequestCompat locationRequestCompat, Executor executor, LocationListenerCompat locationListenerCompat) {
        if (Build.VERSION.SDK_INT >= 31) {
            Api31Impl.requestLocationUpdates(locationManager, str, locationRequestCompat.toLocationRequest(), executor, locationListenerCompat);
        } else if (Build.VERSION.SDK_INT < 30 || !Api30Impl.tryRequestLocationUpdates(locationManager, str, locationRequestCompat, executor, locationListenerCompat)) {
            LocationListenerTransport locationListenerTransport = new LocationListenerTransport(new LocationListenerKey(str, locationListenerCompat), executor);
            if (Build.VERSION.SDK_INT < 19 || !Api19Impl.tryRequestLocationUpdates(locationManager, str, locationRequestCompat, locationListenerTransport)) {
                synchronized (sLocationListeners) {
                    locationManager.requestLocationUpdates(str, locationRequestCompat.getIntervalMillis(), locationRequestCompat.getMinUpdateDistanceMeters(), locationListenerTransport, Looper.getMainLooper());
                    registerLocationListenerTransport(locationManager, locationListenerTransport);
                }
            }
        }
    }

    static void registerLocationListenerTransport(LocationManager locationManager, LocationListenerTransport locationListenerTransport) {
        WeakReference put = sLocationListeners.put(locationListenerTransport.getKey(), new WeakReference(locationListenerTransport));
        LocationListenerTransport locationListenerTransport2 = put != null ? (LocationListenerTransport) put.get() : null;
        if (locationListenerTransport2 != null) {
            locationListenerTransport2.unregister();
            locationManager.removeUpdates(locationListenerTransport2);
        }
    }

    public static void requestLocationUpdates(LocationManager locationManager, String str, LocationRequestCompat locationRequestCompat, LocationListenerCompat locationListenerCompat, Looper looper) {
        if (Build.VERSION.SDK_INT >= 31) {
            Api31Impl.requestLocationUpdates(locationManager, str, locationRequestCompat.toLocationRequest(), ExecutorCompat.create(new Handler(looper)), locationListenerCompat);
        } else if (Build.VERSION.SDK_INT < 19 || !Api19Impl.tryRequestLocationUpdates(locationManager, str, locationRequestCompat, locationListenerCompat, looper)) {
            locationManager.requestLocationUpdates(str, locationRequestCompat.getIntervalMillis(), locationRequestCompat.getMinUpdateDistanceMeters(), locationListenerCompat, looper);
        }
    }

    public static void removeUpdates(LocationManager locationManager, LocationListenerCompat locationListenerCompat) {
        WeakHashMap<LocationListenerKey, WeakReference<LocationListenerTransport>> weakHashMap = sLocationListeners;
        synchronized (weakHashMap) {
            ArrayList arrayList = null;
            for (WeakReference<LocationListenerTransport> weakReference : weakHashMap.values()) {
                LocationListenerTransport locationListenerTransport = (LocationListenerTransport) weakReference.get();
                if (locationListenerTransport != null) {
                    LocationListenerKey key = locationListenerTransport.getKey();
                    if (key.mListener == locationListenerCompat) {
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                        }
                        arrayList.add(key);
                        locationListenerTransport.unregister();
                        locationManager.removeUpdates(locationListenerTransport);
                    }
                }
            }
            if (arrayList != null) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    sLocationListeners.remove((LocationListenerKey) it.next());
                }
            }
        }
        locationManager.removeUpdates(locationListenerCompat);
    }

    public static String getGnssHardwareModelName(LocationManager locationManager) {
        if (Build.VERSION.SDK_INT >= 28) {
            return Api28Impl.getGnssHardwareModelName(locationManager);
        }
        return null;
    }

    public static int getGnssYearOfHardware(LocationManager locationManager) {
        if (Build.VERSION.SDK_INT >= 28) {
            return Api28Impl.getGnssYearOfHardware(locationManager);
        }
        return 0;
    }

    private static class GnssLazyLoader {
        static final SimpleArrayMap<Object, Object> sGnssStatusListeners = new SimpleArrayMap<>();

        private GnssLazyLoader() {
        }
    }

    public static boolean registerGnssStatusCallback(LocationManager locationManager, GnssStatusCompat.Callback callback, Handler handler) {
        if (Build.VERSION.SDK_INT >= 30) {
            return registerGnssStatusCallback(locationManager, ExecutorCompat.create(handler), callback);
        }
        return registerGnssStatusCallback(locationManager, (Executor) new InlineHandlerExecutor(handler), callback);
    }

    public static boolean registerGnssStatusCallback(LocationManager locationManager, Executor executor, GnssStatusCompat.Callback callback) {
        if (Build.VERSION.SDK_INT >= 30) {
            return registerGnssStatusCallback(locationManager, (Handler) null, executor, callback);
        }
        Looper myLooper = Looper.myLooper();
        if (myLooper == null) {
            myLooper = Looper.getMainLooper();
        }
        return registerGnssStatusCallback(locationManager, new Handler(myLooper), executor, callback);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0082, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x008d, code lost:
        return false;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:46:0x0097 */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00cb A[Catch:{ ExecutionException -> 0x00c1, TimeoutException -> 0x00a6, all -> 0x00a3, all -> 0x00e7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00e0 A[Catch:{ ExecutionException -> 0x00c1, TimeoutException -> 0x00a6, all -> 0x00a3, all -> 0x00e7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00ea A[SYNTHETIC, Splitter:B:72:0x00ea] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean registerGnssStatusCallback(android.location.LocationManager r9, android.os.Handler r10, java.util.concurrent.Executor r11, androidx.core.location.GnssStatusCompat.Callback r12) {
        /*
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 30
            if (r0 < r1) goto L_0x000b
            boolean r9 = androidx.core.location.LocationManagerCompat.Api30Impl.registerGnssStatusCallback(r9, r10, r11, r12)
            return r9
        L_0x000b:
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 < r1) goto L_0x0016
            boolean r9 = androidx.core.location.LocationManagerCompat.Api24Impl.registerGnssStatusCallback(r9, r10, r11, r12)
            return r9
        L_0x0016:
            r0 = 1
            r1 = 0
            if (r10 == 0) goto L_0x001c
            r2 = r0
            goto L_0x001d
        L_0x001c:
            r2 = r1
        L_0x001d:
            androidx.core.util.Preconditions.checkArgument(r2)
            androidx.collection.SimpleArrayMap<java.lang.Object, java.lang.Object> r2 = androidx.core.location.LocationManagerCompat.GnssLazyLoader.sGnssStatusListeners
            monitor-enter(r2)
            androidx.collection.SimpleArrayMap<java.lang.Object, java.lang.Object> r3 = androidx.core.location.LocationManagerCompat.GnssLazyLoader.sGnssStatusListeners     // Catch:{ all -> 0x010b }
            java.lang.Object r3 = r3.get(r12)     // Catch:{ all -> 0x010b }
            androidx.core.location.LocationManagerCompat$GpsStatusTransport r3 = (androidx.core.location.LocationManagerCompat.GpsStatusTransport) r3     // Catch:{ all -> 0x010b }
            if (r3 != 0) goto L_0x0033
            androidx.core.location.LocationManagerCompat$GpsStatusTransport r3 = new androidx.core.location.LocationManagerCompat$GpsStatusTransport     // Catch:{ all -> 0x010b }
            r3.<init>(r9, r12)     // Catch:{ all -> 0x010b }
            goto L_0x0036
        L_0x0033:
            r3.unregister()     // Catch:{ all -> 0x010b }
        L_0x0036:
            r3.register(r11)     // Catch:{ all -> 0x010b }
            java.util.concurrent.FutureTask r11 = new java.util.concurrent.FutureTask     // Catch:{ all -> 0x010b }
            androidx.core.location.-$$Lambda$LocationManagerCompat$JLIcm4BkQpukCiUbhX4BKZUICt4 r4 = new androidx.core.location.-$$Lambda$LocationManagerCompat$JLIcm4BkQpukCiUbhX4BKZUICt4     // Catch:{ all -> 0x010b }
            r4.<init>(r9, r3)     // Catch:{ all -> 0x010b }
            r11.<init>(r4)     // Catch:{ all -> 0x010b }
            android.os.Looper r9 = android.os.Looper.myLooper()     // Catch:{ all -> 0x010b }
            android.os.Looper r4 = r10.getLooper()     // Catch:{ all -> 0x010b }
            if (r9 != r4) goto L_0x0051
            r11.run()     // Catch:{ all -> 0x010b }
            goto L_0x0057
        L_0x0051:
            boolean r9 = r10.post(r11)     // Catch:{ all -> 0x010b }
            if (r9 == 0) goto L_0x00f2
        L_0x0057:
            java.util.concurrent.TimeUnit r9 = java.util.concurrent.TimeUnit.SECONDS     // Catch:{ ExecutionException -> 0x00c1, TimeoutException -> 0x00a6, all -> 0x00a3 }
            r4 = 5
            long r4 = r9.toNanos(r4)     // Catch:{ ExecutionException -> 0x00c1, TimeoutException -> 0x00a6, all -> 0x00a3 }
            long r6 = java.lang.System.nanoTime()     // Catch:{ ExecutionException -> 0x00c1, TimeoutException -> 0x00a6, all -> 0x00a3 }
            long r6 = r6 + r4
            r9 = r1
        L_0x0065:
            java.util.concurrent.TimeUnit r8 = java.util.concurrent.TimeUnit.NANOSECONDS     // Catch:{ InterruptedException -> 0x0097, ExecutionException -> 0x0094, TimeoutException -> 0x0091, all -> 0x008e }
            java.lang.Object r4 = r11.get(r4, r8)     // Catch:{ InterruptedException -> 0x0097, ExecutionException -> 0x0094, TimeoutException -> 0x0091, all -> 0x008e }
            java.lang.Boolean r4 = (java.lang.Boolean) r4     // Catch:{ InterruptedException -> 0x0097, ExecutionException -> 0x0094, TimeoutException -> 0x0091, all -> 0x008e }
            boolean r4 = r4.booleanValue()     // Catch:{ InterruptedException -> 0x0097, ExecutionException -> 0x0094, TimeoutException -> 0x0091, all -> 0x008e }
            if (r4 == 0) goto L_0x0083
            androidx.collection.SimpleArrayMap<java.lang.Object, java.lang.Object> r4 = androidx.core.location.LocationManagerCompat.GnssLazyLoader.sGnssStatusListeners     // Catch:{ InterruptedException -> 0x0097, ExecutionException -> 0x0094, TimeoutException -> 0x0091, all -> 0x008e }
            r4.put(r12, r3)     // Catch:{ InterruptedException -> 0x0097, ExecutionException -> 0x0094, TimeoutException -> 0x0091, all -> 0x008e }
            if (r9 == 0) goto L_0x0081
            java.lang.Thread r9 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x010b }
            r9.interrupt()     // Catch:{ all -> 0x010b }
        L_0x0081:
            monitor-exit(r2)     // Catch:{ all -> 0x010b }
            return r0
        L_0x0083:
            if (r9 == 0) goto L_0x008c
            java.lang.Thread r9 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x010b }
            r9.interrupt()     // Catch:{ all -> 0x010b }
        L_0x008c:
            monitor-exit(r2)     // Catch:{ all -> 0x010b }
            return r1
        L_0x008e:
            r10 = move-exception
            r0 = r9
            goto L_0x00e8
        L_0x0091:
            r11 = move-exception
            r0 = r9
            goto L_0x00a8
        L_0x0094:
            r10 = move-exception
            r0 = r9
            goto L_0x00c3
        L_0x0097:
            long r4 = java.lang.System.nanoTime()     // Catch:{ ExecutionException -> 0x00a1, TimeoutException -> 0x009f }
            long r4 = r6 - r4
            r9 = r0
            goto L_0x0065
        L_0x009f:
            r11 = move-exception
            goto L_0x00a8
        L_0x00a1:
            r10 = move-exception
            goto L_0x00c3
        L_0x00a3:
            r10 = move-exception
            r0 = r1
            goto L_0x00e8
        L_0x00a6:
            r11 = move-exception
            r0 = r1
        L_0x00a8:
            java.lang.IllegalStateException r9 = new java.lang.IllegalStateException     // Catch:{ all -> 0x00e7 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x00e7 }
            r12.<init>()     // Catch:{ all -> 0x00e7 }
            java.lang.StringBuilder r10 = r12.append(r10)     // Catch:{ all -> 0x00e7 }
            java.lang.String r12 = " appears to be blocked, please run registerGnssStatusCallback() directly on a Looper thread or ensure the main Looper is not blocked by this thread"
            java.lang.StringBuilder r10 = r10.append(r12)     // Catch:{ all -> 0x00e7 }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x00e7 }
            r9.<init>(r10, r11)     // Catch:{ all -> 0x00e7 }
            throw r9     // Catch:{ all -> 0x00e7 }
        L_0x00c1:
            r10 = move-exception
            r0 = r1
        L_0x00c3:
            java.lang.Throwable r9 = r10.getCause()     // Catch:{ all -> 0x00e7 }
            boolean r9 = r9 instanceof java.lang.RuntimeException     // Catch:{ all -> 0x00e7 }
            if (r9 != 0) goto L_0x00e0
            java.lang.Throwable r9 = r10.getCause()     // Catch:{ all -> 0x00e7 }
            boolean r9 = r9 instanceof java.lang.Error     // Catch:{ all -> 0x00e7 }
            if (r9 == 0) goto L_0x00da
            java.lang.Throwable r9 = r10.getCause()     // Catch:{ all -> 0x00e7 }
            java.lang.Error r9 = (java.lang.Error) r9     // Catch:{ all -> 0x00e7 }
            throw r9     // Catch:{ all -> 0x00e7 }
        L_0x00da:
            java.lang.IllegalStateException r9 = new java.lang.IllegalStateException     // Catch:{ all -> 0x00e7 }
            r9.<init>(r10)     // Catch:{ all -> 0x00e7 }
            throw r9     // Catch:{ all -> 0x00e7 }
        L_0x00e0:
            java.lang.Throwable r9 = r10.getCause()     // Catch:{ all -> 0x00e7 }
            java.lang.RuntimeException r9 = (java.lang.RuntimeException) r9     // Catch:{ all -> 0x00e7 }
            throw r9     // Catch:{ all -> 0x00e7 }
        L_0x00e7:
            r10 = move-exception
        L_0x00e8:
            if (r0 == 0) goto L_0x00f1
            java.lang.Thread r9 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x010b }
            r9.interrupt()     // Catch:{ all -> 0x010b }
        L_0x00f1:
            throw r10     // Catch:{ all -> 0x010b }
        L_0x00f2:
            java.lang.IllegalStateException r9 = new java.lang.IllegalStateException     // Catch:{ all -> 0x010b }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x010b }
            r11.<init>()     // Catch:{ all -> 0x010b }
            java.lang.StringBuilder r10 = r11.append(r10)     // Catch:{ all -> 0x010b }
            java.lang.String r11 = " is shutting down"
            java.lang.StringBuilder r10 = r10.append(r11)     // Catch:{ all -> 0x010b }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x010b }
            r9.<init>(r10)     // Catch:{ all -> 0x010b }
            throw r9     // Catch:{ all -> 0x010b }
        L_0x010b:
            r9 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x010b }
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.core.location.LocationManagerCompat.registerGnssStatusCallback(android.location.LocationManager, android.os.Handler, java.util.concurrent.Executor, androidx.core.location.GnssStatusCompat$Callback):boolean");
    }

    public static void unregisterGnssStatusCallback(LocationManager locationManager, GnssStatusCompat.Callback callback) {
        if (Build.VERSION.SDK_INT >= 24) {
            synchronized (GnssLazyLoader.sGnssStatusListeners) {
                Object remove = GnssLazyLoader.sGnssStatusListeners.remove(callback);
                if (remove != null) {
                    Api24Impl.unregisterGnssStatusCallback(locationManager, remove);
                }
            }
            return;
        }
        synchronized (GnssLazyLoader.sGnssStatusListeners) {
            GpsStatusTransport gpsStatusTransport = (GpsStatusTransport) GnssLazyLoader.sGnssStatusListeners.remove(callback);
            if (gpsStatusTransport != null) {
                gpsStatusTransport.unregister();
                locationManager.removeGpsStatusListener(gpsStatusTransport);
            }
        }
    }

    private LocationManagerCompat() {
    }

    private static class LocationListenerKey {
        final LocationListenerCompat mListener;
        final String mProvider;

        LocationListenerKey(String str, LocationListenerCompat locationListenerCompat) {
            this.mProvider = (String) ObjectsCompat.requireNonNull(str, "invalid null provider");
            this.mListener = (LocationListenerCompat) ObjectsCompat.requireNonNull(locationListenerCompat, "invalid null listener");
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof LocationListenerKey)) {
                return false;
            }
            LocationListenerKey locationListenerKey = (LocationListenerKey) obj;
            if (!this.mProvider.equals(locationListenerKey.mProvider) || !this.mListener.equals(locationListenerKey.mListener)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return ObjectsCompat.hash(this.mProvider, this.mListener);
        }
    }

    private static class LocationListenerTransport implements LocationListener {
        final Executor mExecutor;
        volatile LocationListenerKey mKey;

        LocationListenerTransport(LocationListenerKey locationListenerKey, Executor executor) {
            this.mKey = locationListenerKey;
            this.mExecutor = executor;
        }

        public LocationListenerKey getKey() {
            return (LocationListenerKey) ObjectsCompat.requireNonNull(this.mKey);
        }

        public void unregister() {
            this.mKey = null;
        }

        public void onLocationChanged(Location location) {
            if (this.mKey != null) {
                this.mExecutor.execute(new Runnable(location) {
                    public final /* synthetic */ Location f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        LocationManagerCompat.LocationListenerTransport.this.lambda$onLocationChanged$0$LocationManagerCompat$LocationListenerTransport(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onLocationChanged$0$LocationManagerCompat$LocationListenerTransport(Location location) {
            LocationListenerKey locationListenerKey = this.mKey;
            if (locationListenerKey != null) {
                locationListenerKey.mListener.onLocationChanged(location);
            }
        }

        public void onLocationChanged(List<Location> list) {
            if (this.mKey != null) {
                this.mExecutor.execute(new Runnable(list) {
                    public final /* synthetic */ List f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        LocationManagerCompat.LocationListenerTransport.this.lambda$onLocationChanged$1$LocationManagerCompat$LocationListenerTransport(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onLocationChanged$1$LocationManagerCompat$LocationListenerTransport(List list) {
            LocationListenerKey locationListenerKey = this.mKey;
            if (locationListenerKey != null) {
                locationListenerKey.mListener.onLocationChanged(list);
            }
        }

        public void onFlushComplete(int i) {
            if (this.mKey != null) {
                this.mExecutor.execute(new Runnable(i) {
                    public final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        LocationManagerCompat.LocationListenerTransport.this.lambda$onFlushComplete$2$LocationManagerCompat$LocationListenerTransport(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onFlushComplete$2$LocationManagerCompat$LocationListenerTransport(int i) {
            LocationListenerKey locationListenerKey = this.mKey;
            if (locationListenerKey != null) {
                locationListenerKey.mListener.onFlushComplete(i);
            }
        }

        public void onStatusChanged(String str, int i, Bundle bundle) {
            if (this.mKey != null) {
                this.mExecutor.execute(new Runnable(str, i, bundle) {
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ int f$2;
                    public final /* synthetic */ Bundle f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        LocationManagerCompat.LocationListenerTransport.this.lambda$onStatusChanged$3$LocationManagerCompat$LocationListenerTransport(this.f$1, this.f$2, this.f$3);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onStatusChanged$3$LocationManagerCompat$LocationListenerTransport(String str, int i, Bundle bundle) {
            LocationListenerKey locationListenerKey = this.mKey;
            if (locationListenerKey != null) {
                locationListenerKey.mListener.onStatusChanged(str, i, bundle);
            }
        }

        public void onProviderEnabled(String str) {
            if (this.mKey != null) {
                this.mExecutor.execute(new Runnable(str) {
                    public final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        LocationManagerCompat.LocationListenerTransport.this.lambda$onProviderEnabled$4$LocationManagerCompat$LocationListenerTransport(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onProviderEnabled$4$LocationManagerCompat$LocationListenerTransport(String str) {
            LocationListenerKey locationListenerKey = this.mKey;
            if (locationListenerKey != null) {
                locationListenerKey.mListener.onProviderEnabled(str);
            }
        }

        public void onProviderDisabled(String str) {
            if (this.mKey != null) {
                this.mExecutor.execute(new Runnable(str) {
                    public final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        LocationManagerCompat.LocationListenerTransport.this.lambda$onProviderDisabled$5$LocationManagerCompat$LocationListenerTransport(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onProviderDisabled$5$LocationManagerCompat$LocationListenerTransport(String str) {
            LocationListenerKey locationListenerKey = this.mKey;
            if (locationListenerKey != null) {
                locationListenerKey.mListener.onProviderDisabled(str);
            }
        }
    }

    private static class GnssStatusTransport extends GnssStatus.Callback {
        final GnssStatusCompat.Callback mCallback;

        GnssStatusTransport(GnssStatusCompat.Callback callback) {
            Preconditions.checkArgument(callback != null, "invalid null callback");
            this.mCallback = callback;
        }

        public void onStarted() {
            this.mCallback.onStarted();
        }

        public void onStopped() {
            this.mCallback.onStopped();
        }

        public void onFirstFix(int i) {
            this.mCallback.onFirstFix(i);
        }

        public void onSatelliteStatusChanged(GnssStatus gnssStatus) {
            this.mCallback.onSatelliteStatusChanged(GnssStatusCompat.wrap(gnssStatus));
        }
    }

    private static class PreRGnssStatusTransport extends GnssStatus.Callback {
        final GnssStatusCompat.Callback mCallback;
        volatile Executor mExecutor;

        PreRGnssStatusTransport(GnssStatusCompat.Callback callback) {
            Preconditions.checkArgument(callback != null, "invalid null callback");
            this.mCallback = callback;
        }

        public void register(Executor executor) {
            boolean z = true;
            Preconditions.checkArgument(executor != null, "invalid null executor");
            if (this.mExecutor != null) {
                z = false;
            }
            Preconditions.checkState(z);
            this.mExecutor = executor;
        }

        public void unregister() {
            this.mExecutor = null;
        }

        public void onStarted() {
            Executor executor = this.mExecutor;
            if (executor != null) {
                executor.execute(new Runnable(executor) {
                    public final /* synthetic */ Executor f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        LocationManagerCompat.PreRGnssStatusTransport.this.lambda$onStarted$0$LocationManagerCompat$PreRGnssStatusTransport(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onStarted$0$LocationManagerCompat$PreRGnssStatusTransport(Executor executor) {
            if (this.mExecutor == executor) {
                this.mCallback.onStarted();
            }
        }

        public void onStopped() {
            Executor executor = this.mExecutor;
            if (executor != null) {
                executor.execute(new Runnable(executor) {
                    public final /* synthetic */ Executor f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        LocationManagerCompat.PreRGnssStatusTransport.this.lambda$onStopped$1$LocationManagerCompat$PreRGnssStatusTransport(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onStopped$1$LocationManagerCompat$PreRGnssStatusTransport(Executor executor) {
            if (this.mExecutor == executor) {
                this.mCallback.onStopped();
            }
        }

        public void onFirstFix(int i) {
            Executor executor = this.mExecutor;
            if (executor != null) {
                executor.execute(new Runnable(executor, i) {
                    public final /* synthetic */ Executor f$1;
                    public final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        LocationManagerCompat.PreRGnssStatusTransport.this.lambda$onFirstFix$2$LocationManagerCompat$PreRGnssStatusTransport(this.f$1, this.f$2);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onFirstFix$2$LocationManagerCompat$PreRGnssStatusTransport(Executor executor, int i) {
            if (this.mExecutor == executor) {
                this.mCallback.onFirstFix(i);
            }
        }

        public void onSatelliteStatusChanged(GnssStatus gnssStatus) {
            Executor executor = this.mExecutor;
            if (executor != null) {
                executor.execute(new Runnable(executor, gnssStatus) {
                    public final /* synthetic */ Executor f$1;
                    public final /* synthetic */ GnssStatus f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        LocationManagerCompat.PreRGnssStatusTransport.this.lambda$onSatelliteStatusChanged$3$LocationManagerCompat$PreRGnssStatusTransport(this.f$1, this.f$2);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onSatelliteStatusChanged$3$LocationManagerCompat$PreRGnssStatusTransport(Executor executor, GnssStatus gnssStatus) {
            if (this.mExecutor == executor) {
                this.mCallback.onSatelliteStatusChanged(GnssStatusCompat.wrap(gnssStatus));
            }
        }
    }

    private static class GpsStatusTransport implements GpsStatus.Listener {
        final GnssStatusCompat.Callback mCallback;
        volatile Executor mExecutor;
        private final LocationManager mLocationManager;

        GpsStatusTransport(LocationManager locationManager, GnssStatusCompat.Callback callback) {
            Preconditions.checkArgument(callback != null, "invalid null callback");
            this.mLocationManager = locationManager;
            this.mCallback = callback;
        }

        public void register(Executor executor) {
            Preconditions.checkState(this.mExecutor == null);
            this.mExecutor = executor;
        }

        public void unregister() {
            this.mExecutor = null;
        }

        public void onGpsStatusChanged(int i) {
            GpsStatus gpsStatus;
            Executor executor = this.mExecutor;
            if (executor != null) {
                if (i == 1) {
                    executor.execute(new Runnable(executor) {
                        public final /* synthetic */ Executor f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            LocationManagerCompat.GpsStatusTransport.this.lambda$onGpsStatusChanged$0$LocationManagerCompat$GpsStatusTransport(this.f$1);
                        }
                    });
                } else if (i == 2) {
                    executor.execute(new Runnable(executor) {
                        public final /* synthetic */ Executor f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            LocationManagerCompat.GpsStatusTransport.this.lambda$onGpsStatusChanged$1$LocationManagerCompat$GpsStatusTransport(this.f$1);
                        }
                    });
                } else if (i == 3) {
                    GpsStatus gpsStatus2 = this.mLocationManager.getGpsStatus((GpsStatus) null);
                    if (gpsStatus2 != null) {
                        executor.execute(new Runnable(executor, gpsStatus2.getTimeToFirstFix()) {
                            public final /* synthetic */ Executor f$1;
                            public final /* synthetic */ int f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run() {
                                LocationManagerCompat.GpsStatusTransport.this.lambda$onGpsStatusChanged$2$LocationManagerCompat$GpsStatusTransport(this.f$1, this.f$2);
                            }
                        });
                    }
                } else if (i == 4 && (gpsStatus = this.mLocationManager.getGpsStatus((GpsStatus) null)) != null) {
                    executor.execute(new Runnable(executor, GnssStatusCompat.wrap(gpsStatus)) {
                        public final /* synthetic */ Executor f$1;
                        public final /* synthetic */ GnssStatusCompat f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            LocationManagerCompat.GpsStatusTransport.this.lambda$onGpsStatusChanged$3$LocationManagerCompat$GpsStatusTransport(this.f$1, this.f$2);
                        }
                    });
                }
            }
        }

        public /* synthetic */ void lambda$onGpsStatusChanged$0$LocationManagerCompat$GpsStatusTransport(Executor executor) {
            if (this.mExecutor == executor) {
                this.mCallback.onStarted();
            }
        }

        public /* synthetic */ void lambda$onGpsStatusChanged$1$LocationManagerCompat$GpsStatusTransport(Executor executor) {
            if (this.mExecutor == executor) {
                this.mCallback.onStopped();
            }
        }

        public /* synthetic */ void lambda$onGpsStatusChanged$2$LocationManagerCompat$GpsStatusTransport(Executor executor, int i) {
            if (this.mExecutor == executor) {
                this.mCallback.onFirstFix(i);
            }
        }

        public /* synthetic */ void lambda$onGpsStatusChanged$3$LocationManagerCompat$GpsStatusTransport(Executor executor, GnssStatusCompat gnssStatusCompat) {
            if (this.mExecutor == executor) {
                this.mCallback.onSatelliteStatusChanged(gnssStatusCompat);
            }
        }
    }

    private static final class CancellableLocationListener implements LocationListener {
        private Consumer<Location> mConsumer;
        private final Executor mExecutor;
        private final LocationManager mLocationManager;
        private final Handler mTimeoutHandler = new Handler(Looper.getMainLooper());
        Runnable mTimeoutRunnable;
        private boolean mTriggered;

        public void onProviderEnabled(String str) {
        }

        public void onStatusChanged(String str, int i, Bundle bundle) {
        }

        CancellableLocationListener(LocationManager locationManager, Executor executor, Consumer<Location> consumer) {
            this.mLocationManager = locationManager;
            this.mExecutor = executor;
            this.mConsumer = consumer;
        }

        public void cancel() {
            synchronized (this) {
                if (!this.mTriggered) {
                    this.mTriggered = true;
                    cleanup();
                }
            }
        }

        public void startTimeout(long j) {
            synchronized (this) {
                if (!this.mTriggered) {
                    $$Lambda$LocationManagerCompat$CancellableLocationListener$t_KfxTHtM_iJ8uJYVqEknYPfeGQ r0 = new Runnable() {
                        public final void run() {
                            LocationManagerCompat.CancellableLocationListener.this.lambda$startTimeout$0$LocationManagerCompat$CancellableLocationListener();
                        }
                    };
                    this.mTimeoutRunnable = r0;
                    this.mTimeoutHandler.postDelayed(r0, j);
                }
            }
        }

        public /* synthetic */ void lambda$startTimeout$0$LocationManagerCompat$CancellableLocationListener() {
            this.mTimeoutRunnable = null;
            onLocationChanged((Location) null);
        }

        public void onProviderDisabled(String str) {
            onLocationChanged((Location) null);
        }

        public void onLocationChanged(Location location) {
            synchronized (this) {
                if (!this.mTriggered) {
                    this.mTriggered = true;
                    this.mExecutor.execute(new Runnable(location) {
                        public final /* synthetic */ Location f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            Consumer.this.accept(this.f$1);
                        }
                    });
                    cleanup();
                }
            }
        }

        private void cleanup() {
            this.mConsumer = null;
            this.mLocationManager.removeUpdates(this);
            Runnable runnable = this.mTimeoutRunnable;
            if (runnable != null) {
                this.mTimeoutHandler.removeCallbacks(runnable);
                this.mTimeoutRunnable = null;
            }
        }
    }

    private static final class InlineHandlerExecutor implements Executor {
        private final Handler mHandler;

        InlineHandlerExecutor(Handler handler) {
            this.mHandler = (Handler) Preconditions.checkNotNull(handler);
        }

        public void execute(Runnable runnable) {
            if (Looper.myLooper() == this.mHandler.getLooper()) {
                runnable.run();
            } else if (!this.mHandler.post((Runnable) Preconditions.checkNotNull(runnable))) {
                throw new RejectedExecutionException(this.mHandler + " is shutting down");
            }
        }
    }

    private static class Api31Impl {
        private Api31Impl() {
        }

        static boolean hasProvider(LocationManager locationManager, String str) {
            return locationManager.hasProvider(str);
        }

        static void requestLocationUpdates(LocationManager locationManager, String str, LocationRequest locationRequest, Executor executor, LocationListener locationListener) {
            locationManager.requestLocationUpdates(str, locationRequest, executor, locationListener);
        }
    }

    private static class Api30Impl {
        private static Class<?> sLocationRequestClass;
        private static Method sRequestLocationUpdatesExecutorMethod;

        private Api30Impl() {
        }

        static void getCurrentLocation(LocationManager locationManager, String str, CancellationSignal cancellationSignal, Executor executor, Consumer<Location> consumer) {
            android.os.CancellationSignal cancellationSignal2 = cancellationSignal != null ? (android.os.CancellationSignal) cancellationSignal.getCancellationSignalObject() : null;
            Objects.requireNonNull(consumer);
            locationManager.getCurrentLocation(str, cancellationSignal2, executor, new java.util.function.Consumer() {
                public final void accept(Object obj) {
                    Consumer.this.accept((Location) obj);
                }
            });
        }

        public static boolean tryRequestLocationUpdates(LocationManager locationManager, String str, LocationRequestCompat locationRequestCompat, Executor executor, LocationListenerCompat locationListenerCompat) {
            if (Build.VERSION.SDK_INT >= 30) {
                try {
                    if (sLocationRequestClass == null) {
                        sLocationRequestClass = Class.forName("android.location.LocationRequest");
                    }
                    if (sRequestLocationUpdatesExecutorMethod == null) {
                        Method declaredMethod = LocationManager.class.getDeclaredMethod("requestLocationUpdates", new Class[]{sLocationRequestClass, Executor.class, LocationListener.class});
                        sRequestLocationUpdatesExecutorMethod = declaredMethod;
                        declaredMethod.setAccessible(true);
                    }
                    LocationRequest locationRequest = locationRequestCompat.toLocationRequest(str);
                    if (locationRequest != null) {
                        sRequestLocationUpdatesExecutorMethod.invoke(locationManager, new Object[]{locationRequest, executor, locationListenerCompat});
                        return true;
                    }
                } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | UnsupportedOperationException | InvocationTargetException unused) {
                }
            }
            return false;
        }

        public static boolean registerGnssStatusCallback(LocationManager locationManager, Handler handler, Executor executor, GnssStatusCompat.Callback callback) {
            synchronized (GnssLazyLoader.sGnssStatusListeners) {
                GnssStatusTransport gnssStatusTransport = (GnssStatusTransport) GnssLazyLoader.sGnssStatusListeners.get(callback);
                if (gnssStatusTransport == null) {
                    gnssStatusTransport = new GnssStatusTransport(callback);
                }
                if (!locationManager.registerGnssStatusCallback(executor, gnssStatusTransport)) {
                    return false;
                }
                GnssLazyLoader.sGnssStatusListeners.put(callback, gnssStatusTransport);
                return true;
            }
        }
    }

    private static class Api28Impl {
        private Api28Impl() {
        }

        static boolean isLocationEnabled(LocationManager locationManager) {
            return locationManager.isLocationEnabled();
        }

        static String getGnssHardwareModelName(LocationManager locationManager) {
            return locationManager.getGnssHardwareModelName();
        }

        static int getGnssYearOfHardware(LocationManager locationManager) {
            return locationManager.getGnssYearOfHardware();
        }
    }

    static class Api19Impl {
        private static Class<?> sLocationRequestClass;
        private static Method sRequestLocationUpdatesLooperMethod;

        private Api19Impl() {
        }

        static boolean tryRequestLocationUpdates(LocationManager locationManager, String str, LocationRequestCompat locationRequestCompat, LocationListenerTransport locationListenerTransport) {
            if (Build.VERSION.SDK_INT >= 19) {
                try {
                    if (sLocationRequestClass == null) {
                        sLocationRequestClass = Class.forName("android.location.LocationRequest");
                    }
                    if (sRequestLocationUpdatesLooperMethod == null) {
                        Method declaredMethod = LocationManager.class.getDeclaredMethod("requestLocationUpdates", new Class[]{sLocationRequestClass, LocationListener.class, Looper.class});
                        sRequestLocationUpdatesLooperMethod = declaredMethod;
                        declaredMethod.setAccessible(true);
                    }
                    LocationRequest locationRequest = locationRequestCompat.toLocationRequest(str);
                    if (locationRequest != null) {
                        synchronized (LocationManagerCompat.sLocationListeners) {
                            sRequestLocationUpdatesLooperMethod.invoke(locationManager, new Object[]{locationRequest, locationListenerTransport, Looper.getMainLooper()});
                            LocationManagerCompat.registerLocationListenerTransport(locationManager, locationListenerTransport);
                        }
                        return true;
                    }
                } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | UnsupportedOperationException | InvocationTargetException unused) {
                }
            }
            return false;
        }

        static boolean tryRequestLocationUpdates(LocationManager locationManager, String str, LocationRequestCompat locationRequestCompat, LocationListenerCompat locationListenerCompat, Looper looper) {
            if (Build.VERSION.SDK_INT >= 19) {
                try {
                    if (sLocationRequestClass == null) {
                        sLocationRequestClass = Class.forName("android.location.LocationRequest");
                    }
                    if (sRequestLocationUpdatesLooperMethod == null) {
                        Method declaredMethod = LocationManager.class.getDeclaredMethod("requestLocationUpdates", new Class[]{sLocationRequestClass, LocationListener.class, Looper.class});
                        sRequestLocationUpdatesLooperMethod = declaredMethod;
                        declaredMethod.setAccessible(true);
                    }
                    LocationRequest locationRequest = locationRequestCompat.toLocationRequest(str);
                    if (locationRequest != null) {
                        sRequestLocationUpdatesLooperMethod.invoke(locationManager, new Object[]{locationRequest, locationListenerCompat, looper});
                        return true;
                    }
                } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | UnsupportedOperationException | InvocationTargetException unused) {
                }
            }
            return false;
        }
    }

    static class Api24Impl {
        private Api24Impl() {
        }

        static boolean registerGnssStatusCallback(LocationManager locationManager, Handler handler, Executor executor, GnssStatusCompat.Callback callback) {
            Preconditions.checkArgument(handler != null);
            synchronized (GnssLazyLoader.sGnssStatusListeners) {
                PreRGnssStatusTransport preRGnssStatusTransport = (PreRGnssStatusTransport) GnssLazyLoader.sGnssStatusListeners.get(callback);
                if (preRGnssStatusTransport == null) {
                    preRGnssStatusTransport = new PreRGnssStatusTransport(callback);
                } else {
                    preRGnssStatusTransport.unregister();
                }
                preRGnssStatusTransport.register(executor);
                if (!locationManager.registerGnssStatusCallback(preRGnssStatusTransport, handler)) {
                    return false;
                }
                GnssLazyLoader.sGnssStatusListeners.put(callback, preRGnssStatusTransport);
                return true;
            }
        }

        static void unregisterGnssStatusCallback(LocationManager locationManager, Object obj) {
            if (obj instanceof PreRGnssStatusTransport) {
                ((PreRGnssStatusTransport) obj).unregister();
            }
            locationManager.unregisterGnssStatusCallback((GnssStatus.Callback) obj);
        }
    }
}
