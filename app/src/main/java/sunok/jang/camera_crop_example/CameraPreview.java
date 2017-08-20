package sunok.jang.camera_crop_example;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jang on 2017. 8. 6..
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback{
    String TAG = "CameraPreview";
    Context mContext;
    long lastFocusedAt;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private List<Camera.Size> mSupportedPreviewSizes;
    private Camera.Size mPreviewSize;
    CameraTouchEventView cameraTouchEventView;
    Camera.PictureCallback pictureCallback;



    public CameraPreview(Context context, Camera camera, Camera.PictureCallback pictureCallback,
                         CameraTouchEventView cameraTouchEventView) {
        super(context);
        mCamera = camera;
        mContext = context;
        this.pictureCallback = pictureCallback;
        this.cameraTouchEventView = cameraTouchEventView;
        //set bestResolution Camera
        findBestCameraResolution();

        // supported preview sizes
        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);

        //setTouchToFocusMode(mCamera.getParameters());
    }

    public void surfaceCreated(SurfaceHolder holder) {
        startPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        startPreview();
    }


    public void startPreview() {
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            mCamera.setParameters(parameters);

            mCamera.setPreviewDisplay(mHolder);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting mCamera preview: " + e.getMessage());
        }
    }

    private void focusOnTouch(MotionEvent event) {
        if (mCamera != null) {
            submitFocusAreaRect(event);
        }
    }

    private static final int FOCUS_AREA_SIZE = 150;

    public Rect calculateFocusArea(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int left = (int) x - (FOCUS_AREA_SIZE / 2);
        int top = (int) y - (FOCUS_AREA_SIZE / 2);

        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
    }


    private void submitFocusAreaRect(MotionEvent event) {
        mCamera.cancelAutoFocus();
        Camera.Parameters cameraParameters = mCamera.getParameters();
        setTouchToFocusMode(cameraParameters);
        Rect focusArea = calculateFocusArea(event);
        if(cameraTouchEventView!=null){
            cameraTouchEventView.drawTouchEvent(focusArea);
        }
        ArrayList<Camera.Area> focusAreas = new ArrayList<>();
        focusAreas.add(new Camera.Area(focusArea, 1000));
        cameraParameters.setFocusAreas(focusAreas);

        if (cameraParameters.getMaxNumMeteringAreas() > 0) {
            cameraParameters.setMeteringAreas(focusAreas);
        }
        if (cameraParameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }


        try {
            mCamera.setParameters(cameraParameters);

        } catch (Exception e) {
            e.printStackTrace();
        }

        mCamera.autoFocus(this);
    }


    private void setTouchToFocusMode(Camera.Parameters parameters) {
        String focusMode = null;
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            focusMode = Camera.Parameters.FOCUS_MODE_AUTO;
        }
        if (focusMode != null && focusMode.length() > 0) {
            parameters.setFocusMode(focusMode);
        }
        mCamera.setParameters(parameters);
    }


    private boolean isAutoFocusSupported() {
        List<String> supportedFocusModes = mCamera.getParameters().getSupportedFocusModes();
        return supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && isAutoFocusSupported()) {
            focusOnTouch(event);
        }
        return true;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }

        if (mPreviewSize != null) {
            float ratio;
            if (mPreviewSize.height >= mPreviewSize.width)
                ratio = (float) mPreviewSize.height / (float) mPreviewSize.width;
            else
                ratio = (float) mPreviewSize.width / (float) mPreviewSize.height;

            // One of these methods should be used, second method squishes preview slightly
            setMeasuredDimension(width, (int) (width * ratio));
            //        setMeasuredDimension((int) (width * ratio), height);
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;


        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;

        int targetHeight = h;
        int areaSize = 0;
        for (Camera.Size size : sizes) {
            double ratio = (double) size.height / size.width;
            if (ratio < 1) ratio = (double) size.width / size.height;

            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (areaSize < size.width * size.height) {
                areaSize = size.width * size.height;
                optimalSize = size;
            }
        }

        if (optimalSize == null) {
            double minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (targetHeight - size.height < minDiff && targetHeight - size.height > 0) {
                    optimalSize = size;
                    minDiff = targetHeight - size.height;
                }
            }
        }

        return optimalSize;
    }


    private void findBestCameraResolution() {

        Camera.Parameters params = mCamera.getParameters();

        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        Camera.Size maxSize = null;
        for (Camera.Size size : sizes) {
            if (maxSize == null || size.width > maxSize.width) {
                maxSize = size;
            }
        }
        if (maxSize != null) {
            params.setPictureSize(maxSize.width, maxSize.height);
            mCamera.setParameters(params);
        }
    }


    @Override
    public void onAutoFocus(boolean b, Camera camera) {
        lastFocusedAt = System.currentTimeMillis();

    }



    public void takePhoto() {
        if (!isFocused()){
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    camera.takePicture(null, null, pictureCallback);
                }
            });
        }else{
            mCamera.takePicture(null, null, pictureCallback);
        }
    }

    public boolean isFocused() {
        return (System.currentTimeMillis() - lastFocusedAt) < 3000;
    }
}