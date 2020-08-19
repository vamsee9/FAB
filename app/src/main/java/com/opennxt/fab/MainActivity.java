package com.opennxt.fab;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    /**private FAB start here**/
    private FloatingActionButton mMainAddFab, mCameraFab, mImportImageFab;
    private TextView mCameraText, mImportImageText;
    private boolean isOpen = false;
    private Animation mFabOpenAnim, mFabCloseAnim;

    OvershootInterpolator interpolator = new OvershootInterpolator();
    /**private FAB start here**/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add FAB Icons Here
        mMainAddFab = findViewById(R.id.main_add_fab);
        mCameraFab = findViewById(R.id.camera_fab);
        mImportImageFab = findViewById(R.id.import_image_fab);

        // Add FAB Text Here
        mCameraText = findViewById(R.id.camera_text);
        mImportImageText = findViewById(R.id.import_image_text);

        // Add FAB Anim Here
        mFabOpenAnim = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        mFabCloseAnim = AnimationUtils.loadAnimation(this, R.anim.fab_close);


        mMainAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isOpen) {

                    mMainAddFab.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();

                    mCameraFab.startAnimation(mFabCloseAnim);
                    mImportImageFab.startAnimation(mFabCloseAnim);
                    mCameraText.setVisibility(View.INVISIBLE);
                    mImportImageText.setVisibility(View.INVISIBLE);

                    isOpen = false;
                } else {

                    mMainAddFab.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();

                    mCameraFab.startAnimation(mFabOpenAnim);
                    mImportImageFab.startAnimation(mFabOpenAnim);
                    mCameraText.setVisibility(View.VISIBLE);
                    mImportImageText.setVisibility(View.VISIBLE);

                    isOpen = true;
                }

            }
        });
    }
}