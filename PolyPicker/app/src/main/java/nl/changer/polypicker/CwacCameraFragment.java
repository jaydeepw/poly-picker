/***
 7  Copyright (c) 2013 CommonsWare, LLC

 Licensed under the Apache License, Version 2.0 (the "License"); you may
 not use this file except in compliance with the License. You may obtain
 a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package nl.changer.polypicker;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.Parameters;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.commonsware.cwac.camera.CameraFragment;
import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraUtils;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;

import nl.changer.polypicker.model.Image;

public class CwacCameraFragment extends CameraFragment {

    private static final String KEY_USE_FFC = "com.commonsware.cwac.camera.demo.USE_FFC";

    private static final String TAG = CwacCameraFragment.class.getSimpleName();

    private MenuItem autoFocusItem = null;
    private MenuItem recordItem = null;
    String flashMode = null;

    private View mTakePictureBtn;

    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        SimpleCameraHost.Builder builder = new SimpleCameraHost.Builder(new DemoCameraHost(getActivity()));
        setHost(builder.useFullBleedPreview(true).build());

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_title));
        mProgressDialog.setIndeterminate(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View cameraView = super.onCreateView(inflater, container, savedInstanceState);
        View results = inflater.inflate(R.layout.fragment_camera_cwac, container, false);

        ((ViewGroup) results.findViewById(R.id.camera)).addView(cameraView);

        mTakePictureBtn = results.findViewById(R.id.take_picture);
        mTakePictureBtn.setOnClickListener(mOnTakePictureClicked);

        results.setKeepScreenOn(true);
        setRecordingItemVisibility();
        return results;
    }

    private View.OnClickListener mOnTakePictureClicked = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
        if (mTakePictureBtn.isEnabled()) {
            // enable the button after the photo is
            // saved on the device.
            mTakePictureBtn.setEnabled(false);
            mProgressDialog.show();

            PictureTransaction pictureTransaction = new PictureTransaction(getHost());
            pictureTransaction.needBitmap(true);
            pictureTransaction.flashMode(flashMode);
            takePicture(pictureTransaction);
        }
        }
    };

    void setRecordingItemVisibility() {
        if (recordItem != null) {
            if (getDisplayOrientation() != 0
                    && getDisplayOrientation() != 180) {
                recordItem.setVisible(false);
            }
        }
    }

    private class DemoCameraHost extends SimpleCameraHost implements Camera.FaceDetectionListener {
        boolean supportsFaces = false;

        public DemoCameraHost(Context _ctxt) {
            super(_ctxt);
        }

        @Override
        public boolean useFrontFacingCamera() {
            if (getArguments() == null) {
                return (false);
            }

            return getArguments().getBoolean(KEY_USE_FFC);
        }

        @Override
        public boolean useSingleShotMode() {
            return false;
        }

        @Override
        public void saveImage(PictureTransaction xact, Bitmap bitmap) {
            String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, getPhotoFilename(), null);
            Uri contentUri = Uri.parse(path);
            final Image image = getImageFromContentUri(contentUri);

            // run the media scanner service
            MediaScannerConnection.scanFile(getActivity(), new String[]{path}, new String[]{"image/jpeg"}, null);

            // the current method is an async. call.
            // so make changes to the UI on the main thread.
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ImagePickerActivity) getActivity()).addImage(image);
                    mTakePictureBtn.setEnabled(true);
                    mProgressDialog.dismiss();
                }
            });
        }

        public Image getImageFromContentUri(Uri contentUri) {
            String[] cols = {MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.ORIENTATION};

            // can post image
            Cursor cursor = getActivity().getContentResolver().query(contentUri, cols, null, null, null);

            Uri uri = null;
            int orientation = -1;

            try {
                if (cursor.moveToFirst()) {
                    uri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                    orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

            return new Image(uri, orientation);
        }

        @Override
        public void autoFocusAvailable() {
            if (autoFocusItem != null) {
                autoFocusItem.setEnabled(true);

                if (supportsFaces) {
                    startFaceDetection();
                }
            }
        }

        @Override
        public void autoFocusUnavailable() {
            if (autoFocusItem != null) {
                stopFaceDetection();

                if (supportsFaces) {
                    autoFocusItem.setEnabled(false);
                }
            }
        }

        @Override
        public void onCameraFail(FailureReason reason) {
            super.onCameraFail(reason);
            Toast.makeText(getActivity(), "Sorry, but you cannot use the camera now!", Toast.LENGTH_LONG).show();
        }

        @Override
        public Parameters adjustPreviewParameters(Parameters parameters) {
            flashMode = CameraUtils.findBestFlashModeMatch(parameters,
                    Parameters.FLASH_MODE_RED_EYE,
                    Parameters.FLASH_MODE_AUTO,
                    Parameters.FLASH_MODE_ON);

            if (parameters.getMaxNumDetectedFaces() > 0) {
                supportsFaces = true;
            } else {
                Log.w(TAG, "Face detection not available for this camera");
            }

            return super.adjustPreviewParameters(parameters);
        }

        @Override
        public void onFaceDetection(Face[] faces, Camera camera) {
            // ignore face detection.
        }

        @Override
        @TargetApi(16)
        public void onAutoFocus(boolean success, Camera camera) {
            super.onAutoFocus(success, camera);
            // takePictureItem.setEnabled(true);
            // TODO: check if take picture can be called here.
        }
    }
}