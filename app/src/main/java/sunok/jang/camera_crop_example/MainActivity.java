package sunok.jang.camera_crop_example;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    FrameLayout frameLayout;
    private int page = -1;
    private final int PAGE_CAMERA = 0, PAGE_CROP = 1;
    CameraFragment cameraFragment;
    CropFragment cropFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameLayout = (FrameLayout) findViewById(R.id.frame);
        setupToolbar((Toolbar) findViewById(R.id.toolbar));
        moveToCameraFragment();
    }


    public void moveToCameraFragment() {
        if (cameraFragment == null) {
            cameraFragment = CameraFragment.newInstance();
        }
        if (page != PAGE_CAMERA) {
            page = PAGE_CAMERA;
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame, cameraFragment)
                    .commit();
        }
    }


    public void moveToCropFragment(Uri uri) {
        if (cropFragment == null) {
            cropFragment = CropFragment.newInstance(uri);
        }
        if (page != PAGE_CROP) {
            page = PAGE_CROP;
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame, cropFragment)
                    .commit();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CameraFragment.CAMERA_PERMISSION_REQUEST:
                if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (page == PAGE_CROP) {
            moveToCameraFragment();
        } else {
            super.onBackPressed();
        }
    }
}
