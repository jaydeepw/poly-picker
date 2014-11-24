/***
  Copyright (c) 2013-2014 CommonsWare, LLC
  
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

package com.commonsware.cwac.camera;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.android.mms.exif.ExifInterface;

public class ImageCleanupTask extends Thread {
  private byte[] data;
  private int cameraId;
  private PictureTransaction xact=null;
  private boolean applyMatrix=true;

  ImageCleanupTask(Context ctxt, byte[] data, int cameraId,
                   PictureTransaction xact) {
    this.data=data;
    this.cameraId=cameraId;
    this.xact=xact;

    float heapPct=(float)data.length / calculateHeapSize(ctxt);

    applyMatrix=(heapPct < xact.host.maxPictureCleanupHeapUsage());
  }

  @Override
  public void run() {
    Camera.CameraInfo info=new Camera.CameraInfo();

    Camera.getCameraInfo(cameraId, info);

    Matrix matrix=null;
    Bitmap cleaned=null;
    ExifInterface exif=null;

    if (applyMatrix) {
      if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
        if (xact.host.getDeviceProfile().portraitFFCFlipped()
            && (xact.displayOrientation == 90 || xact.displayOrientation == 270)) {
          matrix=flip(new Matrix());
        }
        else if (xact.mirrorFFC()) {
          matrix=mirror(new Matrix());
        }
      }

      try {
        int imageOrientation=0;

        if (xact.host.getDeviceProfile().useDeviceOrientation()) {
          imageOrientation=xact.displayOrientation;
        }
        else {
          exif=new ExifInterface();
          exif.readExif(data);

          Integer exifOrientation=
              exif.getTagIntValue(ExifInterface.TAG_ORIENTATION);

          if (exifOrientation != null) {
            if (exifOrientation == 6) {
              imageOrientation=90;
            }
            else if (exifOrientation == 8) {
              imageOrientation=270;
            }
            else if (exifOrientation == 3) {
              imageOrientation=180;
            }
            else if (exifOrientation == 1) {
              imageOrientation=0;
            }
            else {
              // imageOrientation=
              // xact.host.getDeviceProfile().getDefaultOrientation();
              //
              // if (imageOrientation == -1) {
              // imageOrientation=xact.displayOrientation;
              // }
            }
          }
        }

        if (imageOrientation != 0) {
          matrix=
              rotate((matrix == null ? new Matrix() : matrix),
                     imageOrientation);
        }
      }
      catch (IOException e) {
        Log.e("CWAC-Camera", "Exception parsing JPEG", e);
        // TODO: ripple to client
      }

      if (matrix != null) {
        Bitmap original=
            BitmapFactory.decodeByteArray(data, 0, data.length);

        cleaned=
            Bitmap.createBitmap(original, 0, 0, original.getWidth(),
                                original.getHeight(), matrix, true);
        original.recycle();
      }
    }

    if (xact.needBitmap) {
      if (cleaned == null) {
        cleaned=BitmapFactory.decodeByteArray(data, 0, data.length);
      }

      xact.host.saveImage(xact, cleaned);
    }

    if (xact.needByteArray) {
      if (matrix != null) {
        ByteArrayOutputStream out=new ByteArrayOutputStream();

        // if (exif == null) {
        cleaned.compress(Bitmap.CompressFormat.JPEG, 100, out);
        // }
        // else {
        // exif.deleteTag(ExifInterface.TAG_ORIENTATION);
        //
        // try {
        // exif.writeExif(cleaned, out);
        // }
        // catch (IOException e) {
        // Log.e("CWAC-Camera", "Exception writing to JPEG",
        // e);
        // // TODO: ripple to client
        // }
        // }

        data=out.toByteArray();

        try {
          out.close();
        }
        catch (IOException e) {
          Log.e(CameraView.TAG, "Exception in closing a BAOS???", e);
        }
      }

      xact.host.saveImage(xact, data);
    }

    System.gc();
  }

  // from http://stackoverflow.com/a/8347956/115145

  private Matrix mirror(Matrix input) {
    float[] mirrorY= { -1, 0, 0, 0, 1, 0, 0, 0, 1 };
    Matrix matrixMirrorY=new Matrix();

    matrixMirrorY.setValues(mirrorY);
    input.postConcat(matrixMirrorY);

    return(input);
  }

  private Matrix flip(Matrix input) {
    float[] mirrorY= { -1, 0, 0, 0, 1, 0, 0, 0, 1 };
    Matrix matrixMirrorY=new Matrix();

    matrixMirrorY.setValues(mirrorY);
    input.preScale(1.0f, -1.0f);
    input.postConcat(matrixMirrorY);

    return(input);
  }

  private Matrix rotate(Matrix input, int degree) {
    input.setRotate(degree);

    return(input);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private static int calculateHeapSize(Context ctxt) {
    ActivityManager am=
        (ActivityManager)ctxt.getSystemService(Context.ACTIVITY_SERVICE);
    int memoryClass=am.getMemoryClass();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      if ((ctxt.getApplicationInfo().flags & ApplicationInfo.FLAG_LARGE_HEAP) != 0) {
        memoryClass=am.getLargeMemoryClass();
      }
    }

    return(memoryClass * 1048576); // MB * bytes in MB
  }
}