package com.duzieblo.example.takepicture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author duzieblo
 * @since 2018-01-22
 */
public class PictureUtils {

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        //Odczytywanie rozmiarów obrazu z pliku na dysku
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //Określanie współczynnika przeskalowania
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            float heightScale = srcHeight / destHeight;
            float widthScale = srcWidth / destWidth;
            inSampleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        //Odczytywanie pliku i tworzenie finalnej bitmapy
        return BitmapFactory.decodeFile(path, options);
    }
}
