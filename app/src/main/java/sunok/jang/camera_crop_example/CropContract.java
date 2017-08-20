package sunok.jang.camera_crop_example;

import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * Created by jang on 2017. 8. 6..
 */

public interface CropContract {
    interface view {

    }

    interface presenter {
        void onCreateView();
        void onResume();
        void onDestroy();

        void crop();
        void rotateRight();
        void rotateLeft();
        void complete();

    }
}