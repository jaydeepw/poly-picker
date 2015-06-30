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

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import com.commonsware.cwac.camera.CameraHost.RecordingHint;
import org.xmlpull.v1.XmlPullParser;

public class SimpleDeviceProfile extends DeviceProfile {
  private boolean useTextureView=
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
          && !isCyanogenMod();
  private boolean portraitFFCFlipped=false;
  private int minPictureHeight=0;
  private int maxPictureHeight=Integer.MAX_VALUE;
  private boolean doesZoomActuallyWork=true;
  private int defaultOrientation=-1;
  private boolean useDeviceOrientation=false;
  private int pictureDelay=0;
  private RecordingHint recordingHint=RecordingHint.NONE;

  SimpleDeviceProfile load(XmlPullParser xpp) {
    StringBuilder buf=null;

    try {
      while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
        switch (xpp.getEventType()) {
          case XmlPullParser.START_TAG:
            buf=new StringBuilder();
            break;

          case XmlPullParser.TEXT:
            if (buf != null) {
              buf.append(xpp.getText());
            }
            break;

          case XmlPullParser.END_TAG:
            if (buf != null) {
              set(xpp.getName(), buf.toString().trim());
            }
            break;
        }

        xpp.next();
      }
    }
    catch (Exception e) {
      Log.e("CWAC-Camera",
            String.format("Exception parsing device profile for %s %s",
                          Build.MANUFACTURER, Build.MODEL), e);
    }

    return(this);
  }

  @SuppressLint("DefaultLocale")
  private void set(String name, String value) {
    if ("useTextureView".equals(name)) {
      useTextureView=Boolean.parseBoolean(value);
    }
    else if ("portraitFFCFlipped".equals(name)) {
      portraitFFCFlipped=Boolean.parseBoolean(value);
    }
    else if ("doesZoomActuallyWork".equals(name)) {
      doesZoomActuallyWork=Boolean.parseBoolean(value);
    }
    else if ("useDeviceOrientation".equals(name)) {
      useDeviceOrientation=Boolean.parseBoolean(value);
    }
    else if ("minPictureHeight".equals(name)) {
      minPictureHeight=Integer.parseInt(value);
    }
    else if ("maxPictureHeight".equals(name)) {
      maxPictureHeight=Integer.parseInt(value);
    }
//    else if ("defaultOrientation".equals(name)) {
//      defaultOrientation=Integer.parseInt(value);
//    }
    else if ("pictureDelay".equals(name)) {
      pictureDelay=Integer.parseInt(value);
    }
    else if ("recordingHint".equals(name)) {
      String hint=value.toUpperCase();
      
      if ("ANY".equals(hint)) {
        recordingHint=RecordingHint.ANY;
      }
      else if ("STILL_ONLY".equals(hint)) {
        recordingHint=RecordingHint.STILL_ONLY;
      }
      else if ("VIDEO_ONLY".equals(hint)) {
        recordingHint=RecordingHint.VIDEO_ONLY;
      }
    }
  }

  @Override
  public boolean useTextureView() {
    return(useTextureView);
  }

  @Override
  public boolean portraitFFCFlipped() {
    return(portraitFFCFlipped);
  }

  @Override
  public int getMinPictureHeight() {
    return(minPictureHeight);
  }

  @Override
  public int getMaxPictureHeight() {
    return(maxPictureHeight);
  }

  @Override
  public boolean doesZoomActuallyWork(boolean isFFC) {
    return(doesZoomActuallyWork);
  }

  // only needed if EXIF headers are mis-coded, such
  // as on Nexus 7 (2012)

  @Override
  public int getDefaultOrientation() {
    return(defaultOrientation);
  }

  // for devices like DROID Mini where setRotation()
  // goes BOOM

  @Override
  public boolean useDeviceOrientation() {
    return(useDeviceOrientation);
  }

  // for devices like Galaxy Nexus where delaying
  // taking a picture after modifying Camera.Parameters
  // seems to help

  @Override
  public int getPictureDelay() {
    return(pictureDelay);
  }
  
  @Override
  public RecordingHint getDefaultRecordingHint() {
    return(recordingHint);
  }

  // based on http://stackoverflow.com/a/9801191/115145
  // and
  // https://github.com/commonsguy/cwac-camera/issues/43#issuecomment-23791446

  private boolean isCyanogenMod() {
    return(System.getProperty("os.version").contains("cyanogenmod") || Build.HOST.contains("cyanogenmod"));
  }

  static class MotorolaRazrI extends SimpleDeviceProfile {
    public boolean doesZoomActuallyWork(boolean isFFC) {
      return(!isFFC);
    }
  }
}
