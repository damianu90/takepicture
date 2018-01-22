package com.duzieblo.example.takepicture;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PHOTO = 2;

    private static final String PHOTO_FILENAME = "photo.jpg";

    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.fabTakePhoto) FloatingActionButton fab;

    private Context mContext;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);


        mContext = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.fabTakePhoto)
    public void takePhoto() {
        Log.d(getClass().getName(), "FAB click");
        File photoFile = getPhotoFile(PHOTO_FILENAME);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (photoFile != null && canTakePhoto(captureImage)) {
            Uri uri = FileProvider.getUriForFile(this,
                    "com.duzieblo.example.takepicture.fileprovider",
                    photoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            //zwraca aktywności które obsługują intencję robienia zdjęć
            List<ResolveInfo> cameraActivities = getPackageManager().
                    queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo activity : cameraActivities) {
                //przydzielenie uprawnienia do zapisu pod wskazanym URI dla aktywnosci,
                //które obsługują robienia zdjęć
                grantUriPermission(activity.activityInfo.packageName,
                        uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            startActivityForResult(captureImage, REQUEST_PHOTO);
        } else {
            Toast.makeText(this, R.string.no_take_photo, Toast.LENGTH_SHORT).show();
        }
    }

    private File getPhotoFile(String name) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, name);
    }

    /**
     * Metoda sprawdza czy istnieje jakiekolwiek aktywność która obsługuję robienie zdjęcia.
     * @param intent obiekt intencji robienia zdjęcia.
     * @return
     */
    private boolean canTakePhoto(Intent intent) {
        return intent.resolveActivity(getPackageManager()) != null;
    }
}
