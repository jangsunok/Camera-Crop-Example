package sunok.jang.camera_crop_example;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    FrameLayout frameLayout;
    private int page = -1;
    private final int PAGE_CAMERA = 0, PAGE_CROP = 1, PAGE_PAINT = 2;
    CameraFragment cameraFragment;
    CropFragment cropFragment;
    PaintFragment paintFragment;
    LinearLayout btnOnPaint;
    Button btnUnDo, btnReDo;
    Uri uri;
    Bitmap paint;

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
            initToolbarReDoButton(false);
        }
    }


    public void moveToCropFragment(Uri uri) {
        if (cropFragment == null || this.uri!=uri) {
            this.uri = uri;
            cropFragment = CropFragment.newInstance(uri);
        }
        if (page != PAGE_CROP) {
            page = PAGE_CROP;
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame, cropFragment)
                    .commit();
            initToolbarReDoButton(false);
        }

    }


    public void moveToPaintFragment(Bitmap bitmap) {
        if (paintFragment == null || bitmap!=paint) {
            paint = bitmap;
            paintFragment = PaintFragment.newInstance(bitmap);
        }
        if (page != PAGE_PAINT) {
            page = PAGE_PAINT;
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame, paintFragment)
                    .commit();
            initToolbarReDoButton(true);
        }

    }

    public void initToolbarReDoButton(boolean bool) {
        if (bool) {
            if (btnOnPaint == null) {
                btnOnPaint = (LinearLayout) findViewById(R.id.btn_on_paint);
                btnUnDo = (Button) findViewById(R.id.btn_undo);
                btnReDo = (Button) findViewById(R.id.btn_redo);
            }
            btnOnPaint.setVisibility(View.VISIBLE);
            btnUnDo.setOnClickListener(this);
            btnReDo.setOnClickListener(this);
            setUndoEnable(false);
            setRedoEnable(false);
        } else {
            if (btnOnPaint != null) {
                btnOnPaint.setVisibility(View.GONE);
                btnUnDo.setOnClickListener(null);
                btnReDo.setOnClickListener(null);
            }
        }
    }


    public void setUndoEnable(boolean bool) {
        if (btnUnDo != null) {
            btnUnDo.setEnabled(bool);
        }
    }


    public void setRedoEnable(boolean bool) {
        if (btnReDo != null) {
            btnReDo.setEnabled(bool);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_undo:
                if (page == PAGE_PAINT && paintFragment != null) {
                    paintFragment.unDo();
                }
                break;
            case R.id.btn_redo:
                if (page == PAGE_PAINT && paintFragment != null) {
                    paintFragment.reDo();
                }
                break;
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
        } else if (PAGE_PAINT == PAGE_PAINT) {
            moveToCropFragment(uri);
        } else
            super.onBackPressed();
    }

}
