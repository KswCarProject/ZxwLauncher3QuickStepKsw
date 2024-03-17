package androidx.core.hardware.fingerprint;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Handler;
import androidx.core.os.CancellationSignal;
import java.security.Signature;
import javax.crypto.Cipher;
import javax.crypto.Mac;

@Deprecated
public class FingerprintManagerCompat {
    private final Context mContext;

    public static abstract class AuthenticationCallback {
        public void onAuthenticationError(int i, CharSequence charSequence) {
        }

        public void onAuthenticationFailed() {
        }

        public void onAuthenticationHelp(int i, CharSequence charSequence) {
        }

        public void onAuthenticationSucceeded(AuthenticationResult authenticationResult) {
        }
    }

    public static FingerprintManagerCompat from(Context context) {
        return new FingerprintManagerCompat(context);
    }

    private FingerprintManagerCompat(Context context) {
        this.mContext = context;
    }

    public boolean hasEnrolledFingerprints() {
        FingerprintManager fingerprintManagerOrNull;
        if (Build.VERSION.SDK_INT < 23 || (fingerprintManagerOrNull = getFingerprintManagerOrNull(this.mContext)) == null || !Api23Impl.hasEnrolledFingerprints(fingerprintManagerOrNull)) {
            return false;
        }
        return true;
    }

    public boolean isHardwareDetected() {
        FingerprintManager fingerprintManagerOrNull;
        if (Build.VERSION.SDK_INT < 23 || (fingerprintManagerOrNull = getFingerprintManagerOrNull(this.mContext)) == null || !Api23Impl.isHardwareDetected(fingerprintManagerOrNull)) {
            return false;
        }
        return true;
    }

    public void authenticate(CryptoObject cryptoObject, int i, CancellationSignal cancellationSignal, AuthenticationCallback authenticationCallback, Handler handler) {
        FingerprintManager fingerprintManagerOrNull;
        if (Build.VERSION.SDK_INT >= 23 && (fingerprintManagerOrNull = getFingerprintManagerOrNull(this.mContext)) != null) {
            Api23Impl.authenticate(fingerprintManagerOrNull, wrapCryptoObject(cryptoObject), cancellationSignal != null ? (android.os.CancellationSignal) cancellationSignal.getCancellationSignalObject() : null, i, wrapCallback(authenticationCallback), handler);
        }
    }

    private static FingerprintManager getFingerprintManagerOrNull(Context context) {
        return Api23Impl.getFingerprintManagerOrNull(context);
    }

    private static FingerprintManager.CryptoObject wrapCryptoObject(CryptoObject cryptoObject) {
        return Api23Impl.wrapCryptoObject(cryptoObject);
    }

    static CryptoObject unwrapCryptoObject(FingerprintManager.CryptoObject cryptoObject) {
        return Api23Impl.unwrapCryptoObject(cryptoObject);
    }

    private static FingerprintManager.AuthenticationCallback wrapCallback(final AuthenticationCallback authenticationCallback) {
        return new FingerprintManager.AuthenticationCallback() {
            public void onAuthenticationError(int i, CharSequence charSequence) {
                AuthenticationCallback.this.onAuthenticationError(i, charSequence);
            }

            public void onAuthenticationHelp(int i, CharSequence charSequence) {
                AuthenticationCallback.this.onAuthenticationHelp(i, charSequence);
            }

            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult authenticationResult) {
                AuthenticationCallback.this.onAuthenticationSucceeded(new AuthenticationResult(FingerprintManagerCompat.unwrapCryptoObject(Api23Impl.getCryptoObject(authenticationResult))));
            }

            public void onAuthenticationFailed() {
                AuthenticationCallback.this.onAuthenticationFailed();
            }
        };
    }

    public static class CryptoObject {
        private final Cipher mCipher;
        private final Mac mMac;
        private final Signature mSignature;

        public CryptoObject(Signature signature) {
            this.mSignature = signature;
            this.mCipher = null;
            this.mMac = null;
        }

        public CryptoObject(Cipher cipher) {
            this.mCipher = cipher;
            this.mSignature = null;
            this.mMac = null;
        }

        public CryptoObject(Mac mac) {
            this.mMac = mac;
            this.mCipher = null;
            this.mSignature = null;
        }

        public Signature getSignature() {
            return this.mSignature;
        }

        public Cipher getCipher() {
            return this.mCipher;
        }

        public Mac getMac() {
            return this.mMac;
        }
    }

    public static final class AuthenticationResult {
        private final CryptoObject mCryptoObject;

        public AuthenticationResult(CryptoObject cryptoObject) {
            this.mCryptoObject = cryptoObject;
        }

        public CryptoObject getCryptoObject() {
            return this.mCryptoObject;
        }
    }

    static class Api23Impl {
        private Api23Impl() {
        }

        static boolean hasEnrolledFingerprints(Object obj) {
            return ((FingerprintManager) obj).hasEnrolledFingerprints();
        }

        static boolean isHardwareDetected(Object obj) {
            return ((FingerprintManager) obj).isHardwareDetected();
        }

        static void authenticate(Object obj, Object obj2, android.os.CancellationSignal cancellationSignal, int i, Object obj3, Handler handler) {
            ((FingerprintManager) obj).authenticate((FingerprintManager.CryptoObject) obj2, cancellationSignal, i, (FingerprintManager.AuthenticationCallback) obj3, handler);
        }

        static FingerprintManager.CryptoObject getCryptoObject(Object obj) {
            return ((FingerprintManager.AuthenticationResult) obj).getCryptoObject();
        }

        public static FingerprintManager getFingerprintManagerOrNull(Context context) {
            if (Build.VERSION.SDK_INT == 23) {
                return (FingerprintManager) context.getSystemService(FingerprintManager.class);
            }
            if (Build.VERSION.SDK_INT <= 23 || !context.getPackageManager().hasSystemFeature("android.hardware.fingerprint")) {
                return null;
            }
            return (FingerprintManager) context.getSystemService(FingerprintManager.class);
        }

        public static FingerprintManager.CryptoObject wrapCryptoObject(CryptoObject cryptoObject) {
            if (cryptoObject == null) {
                return null;
            }
            if (cryptoObject.getCipher() != null) {
                return new FingerprintManager.CryptoObject(cryptoObject.getCipher());
            }
            if (cryptoObject.getSignature() != null) {
                return new FingerprintManager.CryptoObject(cryptoObject.getSignature());
            }
            if (cryptoObject.getMac() != null) {
                return new FingerprintManager.CryptoObject(cryptoObject.getMac());
            }
            return null;
        }

        public static CryptoObject unwrapCryptoObject(Object obj) {
            FingerprintManager.CryptoObject cryptoObject = (FingerprintManager.CryptoObject) obj;
            if (cryptoObject == null) {
                return null;
            }
            if (cryptoObject.getCipher() != null) {
                return new CryptoObject(cryptoObject.getCipher());
            }
            if (cryptoObject.getSignature() != null) {
                return new CryptoObject(cryptoObject.getSignature());
            }
            if (cryptoObject.getMac() != null) {
                return new CryptoObject(cryptoObject.getMac());
            }
            return null;
        }
    }
}
