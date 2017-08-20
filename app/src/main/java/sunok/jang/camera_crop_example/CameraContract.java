package sunok.jang.camera_crop_example;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * Created by jang on 2017. 8. 6..
 */

public interface CameraContract {
    interface view{
        void showAlertDialog(@Nullable String title, @Nullable String message,
                             @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                             @NonNull String positiveText,
                             @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                             @NonNull String negativeText);

        void finishActivity(@Nullable String message);
        void addPreview(CameraPreview preview);
        void startCropFragment(File pictureFile);

        CameraTouchEventView getCameraTouchEventView();
    }

    interface presenter{
        void onCreateView();
        void onResume();
        void onDestroy();

        boolean checkCameraHardware();
        boolean checkCameraPermission();
        void requestCameraPermission();

        void startCamera();
        void capture();

    }
}