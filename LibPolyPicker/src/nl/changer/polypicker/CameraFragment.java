package nl.changer.polypicker;

import nl.changer.polypicker.model.Image;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class CameraFragment extends Fragment implements Camera.ShutterCallback, Camera.PictureCallback {

    private static final String TAG = CameraFragment.class.getSimpleName();

    Camera mCamera;
    ImageButton mTakePictureBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout 
    	// for this fragment
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);

        mCamera = Camera.open();
        CameraPreview preview = new CameraPreview(getActivity(), mCamera);
        
        ViewGroup previewHolder = (ViewGroup) rootView.findViewById(R.id.preview_holder);
        previewHolder.addView(preview);
        // take picture even when the preview is clicked.
        previewHolder.setOnClickListener(mOnTakePictureClicked);
        
        mTakePictureBtn = (ImageButton) rootView.findViewById(R.id.take_picture);
        mTakePictureBtn.setOnClickListener(mOnTakePictureClicked);
        return rootView;
    }
    
    View.OnClickListener mOnTakePictureClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mTakePictureBtn.isEnabled()){
                mTakePictureBtn.setEnabled(false);
                mCamera.takePicture(CameraFragment.this, null, CameraFragment.this);
            }
        }
    };
    
    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        mTakePictureBtn.setEnabled(true);
        Bitmap picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        // rotates the image to portrait
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        picture = Bitmap.createBitmap(picture, 0, 0, picture.getWidth(), picture.getHeight(), matrix, true);

        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), picture, "" , "");
        Uri contentUri = Uri.parse(path);
        Image image = getImageFromContentUri(contentUri);
        ((ImagePickerActivity)getActivity()).addImage(image);
        
        mCamera.startPreview();
    }


    public Image getImageFromContentUri(Uri contentUri) {

        String[] cols = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.ImageColumns.ORIENTATION
        };
        
         // can post image
        Cursor cursor = getActivity().getContentResolver().query(contentUri, cols, null, null, null);
        cursor.moveToFirst();
        Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
        int orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
        return new Image(uri, orientation);
    }

    @Override
    public void onShutter() {

    }
}
