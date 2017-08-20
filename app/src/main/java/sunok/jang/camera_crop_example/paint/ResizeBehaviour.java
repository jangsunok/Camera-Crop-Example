package sunok.jang.camera_crop_example.paint;

/**
 * Created by jang on 2017. 8. 20..
 */

/**
 * When RMFreeDrawView dimensions are changed, you can apply one of the following behaviours
 * {@link #CLEAR} - It just clear the View from every previous paint
 * {@link #FIT_XY} - It stretch the content to fit the new dimensions
 * {@link #CROP} - Keep the exact position of the previous point, if the dimensions changes, there
 * may be points outside the view and not visible
 */
public enum ResizeBehaviour {
    CLEAR,
    FIT_XY,
    CROP
}
