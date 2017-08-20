package sunok.jang.camera_crop_example;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import sunok.jang.camera_crop_example.paint.FreeDrawView;
import sunok.jang.camera_crop_example.paint.PathRedoUndoCountChangeListener;

/**
 * Created by jang on 2017. 8. 20..
 */

public class PaintFragment extends Fragment implements View.OnClickListener {


    Bitmap picture;
    ImageView imageView;
    LinearLayout containerPen;
    LinearLayout containerHighLighter;
    FreeDrawView freeDrawView;

    Button penThin, penMid, penThick, penBlack, penYellow, penBlue, penPurple;
    Button hlThin, hlMid, hlThick, hlGreen, hlYellow, hlBlue, hlPurple;

    Button selectedColor, selectedThick;
    Integer color;
    FreeDrawView.PenThick thick;

    public static PaintFragment newInstance(Bitmap bitmap) {
        PaintFragment fragment = new PaintFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("picture", bitmap);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_paint, container, false);

        imageView = view.findViewById(R.id.container_image);
        freeDrawView = view.findViewById(R.id.draw_view);

        view.findViewById(R.id.button_pen).setOnClickListener(this);
        view.findViewById(R.id.button_highliter).setOnClickListener(this);
        view.findViewById(R.id.button_complete).setOnClickListener(this);

        containerPen = view.findViewById(R.id.container_pen);
        penThin = view.findViewById(R.id.pen_thin);
        penMid = view.findViewById(R.id.pen_mid);
        penThick = view.findViewById(R.id.pen_thick);
        penBlack = view.findViewById(R.id.pen_black);
        penYellow = view.findViewById(R.id.pen_yellow);
        penBlue = view.findViewById(R.id.pen_blue);
        penPurple = view.findViewById(R.id.pen_purple);

        containerHighLighter = view.findViewById(R.id.container_highliter);
        hlThin = view.findViewById(R.id.highliter_thin);
        hlMid = view.findViewById(R.id.highliter_mid);
        hlThick = view.findViewById(R.id.highliter_thick);
        hlGreen = view.findViewById(R.id.highliter_green);
        hlYellow = view.findViewById(R.id.highliter_yellow);
        hlBlue = view.findViewById(R.id.highliter_blue);
        hlPurple = view.findViewById(R.id.highliter_purple);


        if (getArguments() != null) {
            picture = getArguments().getParcelable("picture");
            imageView.setImageBitmap(picture);
            final float ratio = (float) picture.getHeight() / picture.getWidth();

            //Glide.with(context).load(picture).into(imageView);
            //Resize imageView, FreeDrawView
            imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }

                    ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                    layoutParams.height = (int) (imageView.getHeight() * ratio);
                    imageView.setLayoutParams(layoutParams);
                }
            });

            freeDrawView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                    //FIXME:freeDrawView의 높이가 imageView의 높이보다 커지게됨..ㅠ
                    ViewGroup.LayoutParams layoutParams = freeDrawView.getLayoutParams();
                    layoutParams.height = (int) (freeDrawView.getHeight() * ratio);
                    freeDrawView.setLayoutParams(layoutParams);
                }
            });
            freeDrawView.setPathRedoUndoCountChangeListener(getPathReDoUndoListener());
            freeDrawView.setEnabled(false);
        }

        return view;
    }

    public PathRedoUndoCountChangeListener getPathReDoUndoListener() {
        return new PathRedoUndoCountChangeListener() {
            @Override
            public void onUndoCountChanged(int undoCount) {
                if (undoCount > 0) {
                    ((MainActivity) getActivity()).setUndoEnable(true);
                } else {
                    ((MainActivity) getActivity()).setUndoEnable(false);
                }
            }

            @Override
            public void onRedoCountChanged(int redoCount) {
                if (redoCount > 0) {
                    ((MainActivity) getActivity()).setRedoEnable(true);
                } else {
                    ((MainActivity) getActivity()).setRedoEnable(false);
                }
            }
        };
    }


    public void changeColor(int colorId) {
        color = colorId;
        freeDrawView.setPaintColor(getResources().getColor(colorId));
    }

    public void changeAlpha(FreeDrawView.PenAlpha alpha) {
        freeDrawView.setPaintAlpha(alpha.getAlpha());
    }


    public void changeThick(FreeDrawView.PenThick thick) {
        this.thick = thick;
        freeDrawView.setPaintWidthDp(thick.getDp());

    }


    public void unDo() {
        freeDrawView.undoLast();
    }

    public void reDo() {
        freeDrawView.redoLast();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_pen:
                if (containerPen.getVisibility() == View.VISIBLE) {
                    containerPen.setVisibility(View.GONE);
                } else {
                    if (containerHighLighter.getVisibility() == View.VISIBLE) {
                        containerHighLighter.setVisibility(View.GONE);
                        initHighLighter(false);
                    }
                    containerPen.setVisibility(View.VISIBLE);
                    initPenView(true);
                }
                break;
            case R.id.button_highliter:
                if (containerHighLighter.getVisibility() == View.VISIBLE) {
                    containerHighLighter.setVisibility(View.GONE);
                } else {
                    if (containerPen.getVisibility() == View.VISIBLE) {
                        containerPen.setVisibility(View.GONE);
                        initPenView(false);
                    }
                    containerHighLighter.setVisibility(View.VISIBLE);
                    initHighLighter(true);
                }
                break;
            case R.id.button_complete:
                freeDrawView.getDrawScreenshot(getDrawCreatorListener(), picture);
                break;


        }
    }


    public void initPenView(boolean bool) {
        if (bool) {
            if (selectedColor == null) {
                //init color
                changeColor(R.color.black);
                changeThick(FreeDrawView.PenThick.THIN);
                penBlack.setSelected(true);
                penThin.setSelected(true);
                selectedColor = penBlack;
                selectedThick = penThin;
                changeAlpha(FreeDrawView.PenAlpha.NONE);
                freeDrawView.setEnabled(true);
            }
            penThin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeThick(FreeDrawView.PenThick.THIN);
                    selectedThick.setSelected(false);
                    selectedThick = penThin;
                    penThin.setSelected(true);

                }
            });
            penMid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeThick(FreeDrawView.PenThick.MID);
                    selectedThick.setSelected(false);
                    selectedThick = penMid;
                    penMid.setSelected(true);
                }
            });
            penThick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeThick(FreeDrawView.PenThick.THICK);
                    selectedThick.setSelected(false);
                    selectedThick = penThick;
                    penThick.setSelected(true);
                }
            });
            penBlack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeColor(R.color.black);
                    selectedColor.setSelected(false);
                    selectedColor = penBlack;
                    penBlack.setSelected(true);
                    changeAlpha(FreeDrawView.PenAlpha.NONE);
                }
            });
            penYellow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeColor(R.color.yellow);
                    selectedColor.setSelected(false);
                    selectedColor = penYellow;
                    penYellow.setSelected(true);
                    changeAlpha(FreeDrawView.PenAlpha.NONE);
                }
            });
            penBlue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeColor(R.color.blue);
                    selectedColor.setSelected(false);
                    selectedColor = penBlue;
                    penBlue.setSelected(true);
                    changeAlpha(FreeDrawView.PenAlpha.NONE);
                }
            });

            penPurple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeColor(R.color.purple);
                    selectedColor.setSelected(false);
                    selectedColor = penPurple;
                    penPurple.setSelected(true);
                    changeAlpha(FreeDrawView.PenAlpha.NONE);
                }
            });
        } else {
            penThick.setOnClickListener(null);
            penMid.setOnClickListener(null);
            penThick.setOnClickListener(null);

            penBlack.setOnClickListener(null);
            penYellow.setOnClickListener(null);
            penBlue.setOnClickListener(null);
            penPurple.setOnClickListener(null);

        }
    }

    public void initHighLighter(Boolean bool) {
        if (bool) {
            if (selectedColor == null) {
                //init color
                changeColor(R.color.green_op);
                changeAlpha(FreeDrawView.PenAlpha.ALPHA25);
                changeThick(FreeDrawView.PenThick.THIN);
                hlGreen.setSelected(true);
                hlThin.setSelected(true);
                selectedColor = hlGreen;
                selectedThick = hlThin;
                freeDrawView.setEnabled(true);
            }
            hlThin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeThick(FreeDrawView.PenThick.THIN);
                    selectedThick.setSelected(false);
                    selectedThick = hlThin;
                    hlThin.setSelected(true);
                }
            });
            hlMid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeThick(FreeDrawView.PenThick.MID);
                    selectedThick.setSelected(false);
                    selectedThick = hlMid;
                    hlMid.setSelected(true);
                }
            });
            hlThick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeThick(FreeDrawView.PenThick.THICK);
                    selectedThick.setSelected(false);
                    selectedThick = hlThick;
                    hlThick.setSelected(true);
                }
            });
            hlGreen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeColor(R.color.green_op);
                    changeAlpha(FreeDrawView.PenAlpha.ALPHA25);
                    selectedColor.setSelected(false);
                    selectedColor = hlGreen;
                    hlGreen.setSelected(true);
                }
            });
            hlYellow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeColor(R.color.yellow_op);
                    changeAlpha(FreeDrawView.PenAlpha.ALPHA30);
                    selectedColor.setSelected(false);
                    selectedColor = hlYellow;
                    hlYellow.setSelected(true);

                }
            });
            hlBlue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeColor(R.color.blue_op);
                    changeAlpha(FreeDrawView.PenAlpha.ALPHA30);
                    selectedColor.setSelected(false);
                    selectedColor = hlBlue;
                    hlBlue.setSelected(true);
                }
            });

            hlPurple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeColor(R.color.purple_op);
                    changeAlpha(FreeDrawView.PenAlpha.ALPHA25);
                    selectedColor.setSelected(false);
                    selectedColor = hlPurple;
                    hlPurple.setSelected(true);

                }
            });
        } else {
            hlThick.setOnClickListener(null);
            hlMid.setOnClickListener(null);
            hlThick.setOnClickListener(null);

            hlGreen.setOnClickListener(null);
            hlYellow.setOnClickListener(null);
            hlBlue.setOnClickListener(null);
            hlPurple.setOnClickListener(null);

        }
    }

    public FreeDrawView.DrawCreatorListener getDrawCreatorListener() {
        return new FreeDrawView.DrawCreatorListener() {
            @Override
            public void onDrawCreated(Bitmap draw) {
                startActivity(DoneActivity.getStartIntent(getActivity(), getImageUri(draw)));
                getActivity().finish();
            }

            @Override
            public void onDrawCreationError() {
                Toast.makeText(getActivity(), "Error when chapture the paint", Toast.LENGTH_SHORT).show();
            }
        };
    }

    public Uri getImageUri(Bitmap inImage) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        try {
            File tempFile = File.createTempFile("IMG_" + timeStamp, ".jpg", getActivity().getFilesDir());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] bitmapData = bytes.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
            return Uri.fromFile(tempFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }


}
