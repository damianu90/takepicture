package com.duzieblo.example.takepicture;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;

/**
 * @author duzieblo
 * @since 2018-01-22
 */
public class PictureUtils {

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        //Odczytywanie rozmiarów obrazu z pliku na dysku
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // nie zwraca bitmapy tylko opcje
        BitmapFactory.decodeFile(path, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        Log.d(PictureUtils.class.getName(), "Szerokość zdjęcia: " + srcWidth);
        Log.d(PictureUtils.class.getName(), "Wysokość zdjęcia: " + srcHeight);
        Log.d(PictureUtils.class.getName(), "Szerokość parametru: " + destWidth);
        Log.d(PictureUtils.class.getName(), "Wysokość parametru: " + destHeight);

        //Określanie współczynnika przeskalowania
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            float heightScale = srcHeight / destHeight;
            float widthScale = srcWidth / destWidth;
            inSampleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);
            Log.d(PictureUtils.class.getName(), "inSampleSize: " + inSampleSize);
        }

        options = new BitmapFactory.Options();
        //określenie jak duża powinna być próbka pikseli
        // oryginału na każdy piksel przeskalowanej bitmapy
        options.inSampleSize = inSampleSize;

        //Odczytywanie pliku i tworzenie finalnej bitmapy
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path, size.x, size.y);
    }
}
