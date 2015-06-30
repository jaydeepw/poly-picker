/***
  Copyright (c) 2013 CommonsWare, LLC
  
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

import android.hardware.Camera;
import android.hardware.Camera.Size;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraUtils {
  // based on ApiDemos

  private static final double ASPECT_TOLERANCE=0.1;

  public static Camera.Size getOptimalPreviewSize(int displayOrientation,
                                                  int width,
                                                  int height,
                                                  Camera.Parameters parameters) {
    double targetRatio=(double)width / height;
    List<Camera.Size> sizes=parameters.getSupportedPreviewSizes();
    Camera.Size optimalSize=null;
    double minDiff=Double.MAX_VALUE;
    int targetHeight=height;

    if (displayOrientation == 90 || displayOrientation == 270) {
      targetRatio=(double)height / width;
    }

    // Try to find an size match aspect ratio and size

    for (Size size : sizes) {
      double ratio=(double)size.width / size.height;

      if (Math.abs(ratio - targetRatio) <= ASPECT_TOLERANCE) {
        if (Math.abs(size.height - targetHeight) < minDiff) {
          optimalSize=size;
          minDiff=Math.abs(size.height - targetHeight);
        }
      }
    }

    // Cannot find the one match the aspect ratio, ignore
    // the requirement

    if (optimalSize == null) {
      minDiff=Double.MAX_VALUE;

      for (Size size : sizes) {
        if (Math.abs(size.height - targetHeight) < minDiff) {
          optimalSize=size;
          minDiff=Math.abs(size.height - targetHeight);
        }
      }
    }

    return(optimalSize);
  }

  public static Camera.Size getBestAspectPreviewSize(int displayOrientation,
                                                     int width,
                                                     int height,
                                                     Camera.Parameters parameters) {
    return(getBestAspectPreviewSize(displayOrientation, width, height,
                                    parameters, 0.0d));
  }

  public static Camera.Size getBestAspectPreviewSize(int displayOrientation,
                                                     int width,
                                                     int height,
                                                     Camera.Parameters parameters,
                                                     double closeEnough) {
    double targetRatio=(double)width / height;
    Camera.Size optimalSize=null;
    double minDiff=Double.MAX_VALUE;

    if (displayOrientation == 90 || displayOrientation == 270) {
      targetRatio=(double)height / width;
    }

    List<Size> sizes=parameters.getSupportedPreviewSizes();

    Collections.sort(sizes,
                     Collections.reverseOrder(new SizeComparator()));

    for (Size size : sizes) {
      double ratio=(double)size.width / size.height;

      if (Math.abs(ratio - targetRatio) < minDiff) {
        optimalSize=size;
        minDiff=Math.abs(ratio - targetRatio);
      }

      if (minDiff < closeEnough) {
        break;
      }
    }

    return(optimalSize);
  }

  public static Camera.Size getLargestPictureSize(CameraHost host,
                                                  Camera.Parameters parameters) {
    return(getLargestPictureSize(host, parameters, true));
  }

  public static Camera.Size getLargestPictureSize(CameraHost host,
                                                  Camera.Parameters parameters,
                                                  boolean enforceProfile) {
    Camera.Size result=null;

    for (Camera.Size size : parameters.getSupportedPictureSizes()) {

      // android.util.Log.d("CWAC-Camera",
      // String.format("%d x %d", size.width, size.height));

      if (!enforceProfile
          || (size.height <= host.getDeviceProfile()
                                 .getMaxPictureHeight() && size.height >= host.getDeviceProfile()
                                                                              .getMinPictureHeight())) {
        if (result == null) {
          result=size;
        }
        else {
          int resultArea=result.width * result.height;
          int newArea=size.width * size.height;

          if (newArea > resultArea) {
            result=size;
          }
        }
      }
    }

    if (result == null && enforceProfile) {
      result=getLargestPictureSize(host, parameters, false);
    }

    return(result);
  }

  public static Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
    Camera.Size result=null;

    for (Camera.Size size : parameters.getSupportedPictureSizes()) {
      if (result == null) {
        result=size;
      }
      else {
        int resultArea=result.width * result.height;
        int newArea=size.width * size.height;

        if (newArea < resultArea) {
          result=size;
        }
      }
    }

    return(result);
  }

  public static String findBestFlashModeMatch(Camera.Parameters params,
                                              String... modes) {
    String match=null;

    List<String> flashModes=params.getSupportedFlashModes();

    if (flashModes != null) {
      for (String mode : modes) {
        if (flashModes.contains(mode)) {
          match=mode;
          break;
        }
      }
    }

    return(match);
  }

  private static class SizeComparator implements
      Comparator<Camera.Size> {
    @Override
    public int compare(Size lhs, Size rhs) {
      int left=lhs.width * lhs.height;
      int right=rhs.width * rhs.height;

      if (left < right) {
        return(-1);
      }
      else if (left > right) {
        return(1);
      }

      return(0);
    }
  }
}
