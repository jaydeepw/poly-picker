
package changer.nl.polypicker;

import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = "CameraPreview";

	private Context mContext;
	private SurfaceHolder mHolder;
	private Camera mCamera;
	// private List<Camera.Size> mSupportedPreviewSizes;
	private Size mPreviewSize;

	public CameraPreview(Context context, Camera camera) {
		super(context);
		mContext = context;
		mCamera = camera;

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// empty. surfaceChanged will take care of stuff
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// Log.e(TAG, "surfaceChanged => w=" + w + ", h=" + h);
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.
		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			Log.w(TAG, " The holder surface does not exist.");
			return;
		}

		// stop preview before making changes
		try {
			if (mCamera != null) {
				mCamera.stopPreview();
			}
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
			e.printStackTrace();
		}

		// set preview size and make any resize, rotate or reformatting changes
		// here
		// start preview with new settings
		try {
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
			if (mCamera != null) {
				mCamera.setParameters(parameters);
				mCamera.setDisplayOrientation(90);
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();
			}
		} catch (Exception e) {
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

		// supported preview sizes
		List<Size> supportedPreviewSizes = null;

		if (mCamera != null) {
			supportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();

			for (Size str : supportedPreviewSizes) {
				Log.i(TAG, str.width + "/" + str.height);
			}
		}

		if (supportedPreviewSizes != null) {
			// TODO: fix this.
			// thisis a hack involved here for now.
			// Was not able to figure out why resolveSize above is return height
			// = 0 on my Nexus 4
			mPreviewSize = getOptimalPreviewSize(supportedPreviewSizes, width, getResources().getDisplayMetrics().heightPixels);
		}

		float ratio = 0;

		if (mPreviewSize != null) {
			if (mPreviewSize.height >= mPreviewSize.width) {
				ratio = (float) mPreviewSize.height / (float) mPreviewSize.width;
			} else {
				ratio = (float) mPreviewSize.width / (float) mPreviewSize.height;
			}
		}

		Log.i(TAG, " width: " + mPreviewSize.width + " height: " + mPreviewSize.height);
		// Toast.makeText(getContext(), " width: " + mPreviewSize.width + " height: " +
		// mPreviewSize.height, Toast.LENGTH_SHORT).show();

		if (ratio != 0) {
			// One of these methods should be used,
			// second method squishes preview slightly.
			setMeasuredDimension(width, (int) (width * ratio));
			// setMeasuredDimension((int) (width * ratio), height);
		} else {
			setMeasuredDimension(width, height);
		}
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		Log.d(TAG, "values: " + w + "/" + h);
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) h / w;

		if (sizes == null) {
			return null;
		}

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		for (Size size : sizes) {
			double ratio = (double) size.height / size.width;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
				continue;
			}

			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}

		// TODO: This is very hacky way to do device specific customization.
		// Ideally CameraFragment from the library specified here should be used in future.
		// https://github.com/commonsguy/cwac-camera
		if (Build.MODEL != null && Build.MODEL.equalsIgnoreCase("GT-I9500")) {
			optimalSize.width = 1008;
			optimalSize.height = 566;
		}

		return optimalSize;
	}
}