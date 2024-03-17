package com.android.launcher3.util;

import android.os.FileUtils;
import com.android.launcher3.Utilities;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
    private static final int BUF_SIZE = 4096;
    private static final String TAG = "IOUtils";

    public static byte[] toByteArray(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            byte[] byteArray = toByteArray((InputStream) fileInputStream);
            fileInputStream.close();
            return byteArray;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        copy(inputStream, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static long copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        if (Utilities.ATLEAST_Q) {
            return FileUtils.copy(inputStream, outputStream);
        }
        byte[] bArr = new byte[4096];
        long j = 0;
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                return j;
            }
            outputStream.write(bArr, 0, read);
            j += (long) read;
        }
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException unused) {
            }
        }
    }
}
