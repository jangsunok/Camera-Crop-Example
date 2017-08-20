package sunok.jang.camera_crop_example;


import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jang on 2017. 8. 20..
 */

public class CropFragment extends Fragment implements View.OnClickListener{
    Uri picture;



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
        if(getArguments()!=null){
            picture = getArguments().getParcelable("picture");
        }

        view.findViewById(R.id.button_rotate_left).setOnClickListener(this);
        view.findViewById(R.id.button_rotate_right).setOnClickListener(this);
        view.findViewById(R.id.button_complete).setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_rotate_left:
                break;
            case R.id.button_rotate_right:
                break;
            case R.id.button_complete:
                break;
        }
    }
}
