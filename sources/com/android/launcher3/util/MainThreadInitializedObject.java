package com.android.launcher3.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Looper;
import android.util.Log;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.ResourceBasedOverride;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class MainThreadInitializedObject<T> {
    private final ObjectProvider<T> mProvider;
    private T mValue;

    public interface ObjectProvider<T> {
        T get(Context context);
    }

    public MainThreadInitializedObject(ObjectProvider<T> objectProvider) {
        this.mProvider = objectProvider;
    }

    /* renamed from: get */
    public T lambda$get$1$MainThreadInitializedObject(Context context) {
        if (context instanceof SandboxContext) {
            return ((SandboxContext) context).lambda$getObject$0$MainThreadInitializedObject$SandboxContext(this, this.mProvider);
        }
        if (this.mValue == null) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                this.mValue = TraceHelper.allowIpcs("main.thread.object", new Supplier(context) {
                    public final /* synthetic */ Context f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final Object get() {
                        return MainThreadInitializedObject.this.lambda$get$0$MainThreadInitializedObject(this.f$1);
                    }
                });
            } else {
                try {
                    return Executors.MAIN_EXECUTOR.submit(new Callable(context) {
                        public final /* synthetic */ Context f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final Object call() {
                            return MainThreadInitializedObject.this.lambda$get$1$MainThreadInitializedObject(this.f$1);
                        }
                    }).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return this.mValue;
    }

    public /* synthetic */ Object lambda$get$0$MainThreadInitializedObject(Context context) {
        return this.mProvider.get(context.getApplicationContext());
    }

    public T getNoCreate() {
        return this.mValue;
    }

    public void initializeForTesting(T t) {
        this.mValue = t;
    }

    public static <T extends ResourceBasedOverride> MainThreadInitializedObject<T> forOverride(Class<T> cls, int i) {
        return new MainThreadInitializedObject<>(new ObjectProvider(cls, i) {
            public final /* synthetic */ Class f$0;
            public final /* synthetic */ int f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final Object get(Context context) {
                return ResourceBasedOverride.Overrides.getObject(this.f$0, context, this.f$1);
            }
        });
    }

    public static abstract class SandboxContext extends ContextWrapper {
        private static final String TAG = "SandboxContext";
        protected final Set<MainThreadInitializedObject> mAllowedObjects;
        private final Object mDestroyLock = new Object();
        private boolean mDestroyed = false;
        protected final Map<MainThreadInitializedObject, Object> mObjectMap = new HashMap();
        protected final ArrayList<Object> mOrderedObjects = new ArrayList<>();

        public Context getApplicationContext() {
            return this;
        }

        public SandboxContext(Context context, MainThreadInitializedObject... mainThreadInitializedObjectArr) {
            super(context);
            this.mAllowedObjects = new HashSet(Arrays.asList(mainThreadInitializedObjectArr));
        }

        public void onDestroy() {
            synchronized (this.mDestroyLock) {
                for (int size = this.mOrderedObjects.size() - 1; size >= 0; size--) {
                    Object obj = this.mOrderedObjects.get(size);
                    if (obj instanceof SafeCloseable) {
                        ((SafeCloseable) obj).close();
                    }
                }
                this.mDestroyed = true;
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: getObject */
        public <T> T lambda$getObject$0$MainThreadInitializedObject$SandboxContext(MainThreadInitializedObject<T> mainThreadInitializedObject, ObjectProvider<T> objectProvider) {
            synchronized (this.mDestroyLock) {
                if (this.mDestroyed) {
                    Log.e(TAG, "Static object access with a destroyed context");
                }
                if (this.mAllowedObjects.contains(mainThreadInitializedObject)) {
                    T t = this.mObjectMap.get(mainThreadInitializedObject);
                    if (t != null) {
                        return t;
                    }
                    if (Looper.myLooper() == Looper.getMainLooper()) {
                        T createObject = createObject(objectProvider);
                        this.mObjectMap.put(mainThreadInitializedObject, createObject);
                        this.mOrderedObjects.add(createObject);
                        return createObject;
                    }
                    try {
                        return Executors.MAIN_EXECUTOR.submit(new Callable(mainThreadInitializedObject, objectProvider) {
                            public final /* synthetic */ MainThreadInitializedObject f$1;
                            public final /* synthetic */ MainThreadInitializedObject.ObjectProvider f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final Object call() {
                                return MainThreadInitializedObject.SandboxContext.this.lambda$getObject$0$MainThreadInitializedObject$SandboxContext(this.f$1, this.f$2);
                            }
                        }).get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new IllegalStateException("Leaking unknown objects " + mainThreadInitializedObject + "  " + objectProvider);
                }
            }
        }

        /* access modifiers changed from: protected */
        public <T> T createObject(ObjectProvider<T> objectProvider) {
            return objectProvider.get(this);
        }
    }
}
