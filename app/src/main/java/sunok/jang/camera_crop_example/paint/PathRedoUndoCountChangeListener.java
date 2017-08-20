package sunok.jang.camera_crop_example.paint;

/**
 * Created by jang on 2017. 8. 20..
 */

public interface PathRedoUndoCountChangeListener {
    void onUndoCountChanged(int undoCount);

    void onRedoCountChanged(int redoCount);
}
