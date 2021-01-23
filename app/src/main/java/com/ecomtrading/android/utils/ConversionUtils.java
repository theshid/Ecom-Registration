package com.ecomtrading.android.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ConversionUtils {

    /**
     * Convert Bitmap image to byte array
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap byteArrayToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static String bitmapToBase64(Bitmap bitmap){
        return byteArrayToBase64(bitmapToByteArray(bitmap));
    }

    public static String byteArrayToBase64(byte[] barray) {
        return Base64.encodeToString(barray, Base64.DEFAULT);
    }

    public static Bitmap base64ToBitmap(String photo) {
        return byteArrayToBitmap(base64ToByteArray(photo));
    }

    public static byte[] base64ToByteArray(String base64string) {
        if (base64string != null){
            if (!base64string.isEmpty())
                return Base64.decode(base64string.getBytes(), Base64.DEFAULT);
        }
        return null;
    }
}
