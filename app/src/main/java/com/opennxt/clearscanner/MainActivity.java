package com.opennxt.clearscanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    static final int REQUEST_TAKE_PHOTO = 1;

    String currentPhotoPath;

    private static final String TAG = "MainActivity" ;
    FloatingActionButton FabMainAdd, FabCamera, FabImportImage;
    Float translationY = 100f;

    OvershootInterpolator interpolator = new OvershootInterpolator();

    Boolean isMenuOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        iniFabMenu();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void iniFabMenu() {

        FabMainAdd = findViewById(R.id.main_add_fab);
        FabCamera = findViewById(R.id.camera_fab);
        FabImportImage = findViewById(R.id.import_image_fab);

        FabCamera.setAlpha(0f);
        FabImportImage.setAlpha(0f);

        FabCamera.setTranslationY(translationY);
        FabImportImage.setTranslationY(translationY);

        FabMainAdd.setOnClickListener(this);
        FabCamera.setOnClickListener(this);
        FabImportImage.setOnClickListener(this);

    }

    private void openMenu() {
        isMenuOpen = !isMenuOpen;

        FabMainAdd.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();
        FabCamera.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        FabImportImage.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();

    }

    private void closeMenu() {
        isMenuOpen = !isMenuOpen;

        FabMainAdd.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();
        FabCamera.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        FabImportImage.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();

    }

    private void openCamera() throws IOException {

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "Img_" + timeStamp;

        // Create an image file name
        String imageFileName = fileName + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

         currentPhotoPath = image.getAbsolutePath();

        Uri imageUri =  FileProvider.getUriForFile(this,
                "com.opennxt.ClearScanner.fileprovider",
                                                       image);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);

    }

    public void openMediaContent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_GET_CONTENT);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    private void  handleCameraFab() {
        Log.i(TAG, "handleCameraFab: ");
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.main_add_fab:
                Log.i(TAG, "Select one of the menu");
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                    Toast.makeText(getApplicationContext(),"Select one of the menu",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.camera_fab:
                checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
                try {
                    openCamera();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Opening Camera.");
                handleCameraFab();
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                }
                Toast.makeText(getApplicationContext(),"Opening Camera.",Toast.LENGTH_SHORT).show();
                break;
            case R.id.import_image_fab:
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
                openMediaContent();
                Log.i(TAG, "Opening Gallery..");
                handleCameraFab();
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                }
                Toast.makeText(getApplicationContext(),"Opening Gallery.",Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private void checkPermission(String permission, int requestCode) {

        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { permission },
                    requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,
                        "Camera Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(MainActivity.this,
                        "Camera Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }
}