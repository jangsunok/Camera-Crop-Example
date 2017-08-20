package sunok.jang.camera_crop_example;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jang on 2017. 8. 6..
 */

public class CameraPresenter implements CameraContract.presenter {
    Activity context;
    CameraContract.view view;
    Camera mCamera;
    CameraPreview mPreview;


    public CameraPresenter(Activity context, CameraContract.view view) {
        this.context = context;
        this.view = view;
    }


    @Override
    public void onCreateView() {
        if (checkCameraHardware()) {
            if (checkCameraPermission()) {
                startCamera();
            } else {
                requestCameraPermission();
            }
        } else {
            view.finishActivity("No Camera in this Device");
        }

    }

    @Override
    public void onResume() {
        if (mCamera == null) {
            onCreateView();
        }
    }

    @Override
    public void onDestroy() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }

    }

    @Override
    public void startCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }

        try {
            int cameraId = findFacingCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            mCamera = Camera.open(cameraId);
//           if(!canDisableShutterSound(cameraId)){//TODo 임시 주석
//                showDisableMuteDialog();
//            }else {
//                //TODO setMuteCamera
//                mCamera.enableShutterSound(false);
//            }
            mPreview = new CameraPreview(context, mCamera, getPictrueCallback(),
                    view.getCameraTouchEventView());
            view.addPreview(mPreview);

        } catch (Exception e) {
            e.printStackTrace();
            String errorString = "openCamera() failed : " + e.toString();
            new Throwable(errorString, e);
            view.finishActivity(errorString);
        }

    }


    public Camera.PictureCallback getPictrueCallback(){
        return new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                File pictureFile = getOutputMediaFile();

                if (pictureFile == null) {
//                    isCapturing = false;
                    return;
                }
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(bytes);
                    fos.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

//                isCapturing = false;
                view.startCropFragment(pictureFile);
            }
        };
    }

    // make picture and save to a folder
    private File getOutputMediaFile() {
        File mediaStorageDir = getDCIMDirectory();

        if (mediaStorageDir == null) {
            return null;
        }
        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    private File getDCIMDirectory() {
        return context.getExternalFilesDir(Environment.DIRECTORY_DCIM);
    }


    public void showDisableMuteDialog() {
        view.showAlertDialog("무음카메라", "해당 카메라는 무음카메라가 아닙니다. 사진 소리에 주의하세요!",
                null, context.getString(R.string.label_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        view.finishActivity(null);
                    }
                }, context.getString(R.string.label_cancel));
    }

    public boolean canDisableShutterSound(int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        return info.canDisableShutterSound;

    }

    private int findFacingCamera(int facing) {
        int cameraId = -1;
        // Search for the front facing mCamera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == facing) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }


    @Override
    public boolean checkCameraHardware() {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean checkCameraPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.CAMERA)) {
            view.showAlertDialog(context.getString(R.string.permission_title_rationale), context.getString(R.string.permission_camera_rationale),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(context,
                                    new String[]{Manifest.permission.CAMERA}, CameraFragment.CAMERA_PERMISSION_REQUEST);
                        }
                    }, context.getString(R.string.label_ok), null, context.getString(R.string.label_cancel));

        } else {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.CAMERA}, CameraFragment.CAMERA_PERMISSION_REQUEST);
        }
    }

    @Override
    public void capture() {
        mPreview.takePhoto();
    }
}
