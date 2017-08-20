package sunok.jang.camera_crop_example;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;

/**
 * Created by jang on 2017. 8. 6..
 */

public class CameraFragment extends Fragment implements CameraContract.view, View.OnClickListener {
    public static final int CAMERA_PERMISSION_REQUEST = 123;
    public static final int REQUEST_CROP = 1;
    public static final int REQUEST_IAMGE_BY_GALLERY = 2;


    private AlertDialog mAlertDialog;
    CameraPresenter presenter;
    FrameLayout frameLayout;
    CameraTouchEventView cameraTouchEventView;

    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new CameraPresenter(getActivity(), this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_camera, container, false);
        frameLayout = view.findViewById(R.id.camera_preview);
        view.findViewById(R.id.button_capture).setOnClickListener(this);
        view.findViewById(R.id.button_finish).setOnClickListener(this);
        view.findViewById(R.id.button_album).setOnClickListener(this);
        cameraTouchEventView = view.findViewById(R.id.camera_touchview);
        presenter.onCreateView();
        return view;
    }


    @Override
    public void showAlertDialog(@Nullable String title, @Nullable String message,
                                @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                @NonNull String positiveText,
                                @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                @NonNull String negativeText) {
        AlertDialog.Builder builder
                = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, onPositiveButtonClickListener)
                .setNegativeButton(negativeText, onNegativeButtonClickListener);
        mAlertDialog = builder.show();
    }


    @Override
    public void finishActivity(@Nullable String message) {
        if (message != null) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
        getActivity().finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    @Override
    public void startCropFragment(File pictureFile) {

        if (pictureFile != null) {
            ((MainActivity) getActivity()).moveToCropFragment(Uri.fromFile(pictureFile));
        }
    }

    @Override
    public CameraTouchEventView getCameraTouchEventView() {
        return cameraTouchEventView;
    }


    @Override
    public void addPreview(CameraPreview preview) {
        frameLayout.addView(preview);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.startCamera();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_capture:
                presenter.capture();
                break;
            case R.id.button_finish:
                finishActivity("finish");
                break;
            case R.id.button_album:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IAMGE_BY_GALLERY);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CameraFragment.REQUEST_IAMGE_BY_GALLERY:
                if (resultCode == Activity.RESULT_OK) {
                    try {
//                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData() );
                        Uri picture = data.getData();
                        if (picture != null) {
                            ((MainActivity) getActivity()).moveToCropFragment(picture);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }
}
