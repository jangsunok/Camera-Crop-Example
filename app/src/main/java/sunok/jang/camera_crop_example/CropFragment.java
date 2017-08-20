package sunok.jang.camera_crop_example;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by jang on 2017. 8. 20..
 */

public class CropFragment extends Fragment implements View.OnClickListener, CropContract.view {
    Uri picture;
    CropImageView cropImageView;


    public static CropFragment newInstance(Uri uri) {
        CropFragment fragment = new CropFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("picture", uri);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_crop, container, false);

        cropImageView = view.findViewById(R.id.container_crop);
        view.findViewById(R.id.button_rotate_left).setOnClickListener(this);
        view.findViewById(R.id.button_rotate_right).setOnClickListener(this);
        view.findViewById(R.id.button_complete).setOnClickListener(this);
        if (getArguments() != null) {
            picture = getArguments().getParcelable("picture");
            cropImageView.setImageURI(picture);
            //Glide.with(context).load(picture).into(cropImageView);
            cropImageView.setCropMode(CropImageView.CropMode.FREE);
            cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
        }

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_rotate_left:
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
                break;
            case R.id.button_rotate_right:
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                break;
            case R.id.button_complete:
                //FIXME:일단 crop기능으로
                cropImageView.cropAsync(picture, new CropImageView.Callback() {
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "Error during on Crop", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccessCrop(Bitmap cropped) {
                        cropImageView.setImageBitmap(cropped);
                    }
                });
                break;
        }
    }

}
