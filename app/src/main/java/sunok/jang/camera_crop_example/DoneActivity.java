package sunok.jang.camera_crop_example;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class DoneActivity extends AppCompatActivity{
   ImageView image;

   public static Intent getStartIntent(Context context, Uri uri){
       Intent intent = new Intent(context, DoneActivity.class);
       intent.putExtra("picture", uri);
       return intent;
   }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);
        image = (ImageView)findViewById(R.id.image);

        if(getIntent()!=null){
            Uri uri = getIntent().getParcelableExtra("picture");
            if(uri!=null){
                image.setImageURI(uri);
            }
        }


    }




}
