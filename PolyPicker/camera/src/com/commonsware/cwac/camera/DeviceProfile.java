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

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import java.util.Locale;
import com.commonsware.cwac.camera.CameraHost.RecordingHint;

abstract public class DeviceProfile {
  abstract public boolean useTextureView();

  abstract public boolean portraitFFCFlipped();

  abstract public int getMinPictureHeight();

  abstract public int getMaxPictureHeight();

  abstract public boolean doesZoomActuallyWork(boolean isFFC);

  abstract public int getDefaultOrientation();

  abstract public boolean useDeviceOrientation();

  abstract public int getPictureDelay();
  
  abstract public RecordingHint getDefaultRecordingHint();

  private static volatile DeviceProfile SINGLETON=null;

  synchronized public static DeviceProfile getInstance(Context ctxt) {
    if (SINGLETON == null) {
//       android.util.Log.wtf("DeviceProfile",
//       String.format("\"%s\" \"%s\"", Build.MANUFACTURER,
//       Build.PRODUCT));

      if ("motorola".equalsIgnoreCase(Build.MANUFACTURER)
          && "XT890_rtgb".equals(Build.PRODUCT)) {
        SINGLETON=new SimpleDeviceProfile.MotorolaRazrI();
      }
      else {
        int resource=findResource(ctxt);

        if (resource != 0) {
          SINGLETON=
              new SimpleDeviceProfile().load(ctxt.getResources()
                                                 .getXml(resource));
        }
        else {
          SINGLETON=new SimpleDeviceProfile();
        }
      }
    }

    return(SINGLETON);
  }

  private static int findResource(Context ctxt) {
    Resources res=ctxt.getResources();
    StringBuilder buf=new StringBuilder("cwac_camera_profile_");

    buf.append(clean(Build.MANUFACTURER));

    int mfrResult=
        res.getIdentifier(buf.toString(), "xml", ctxt.getPackageName());

    buf.append("_");
    buf.append(clean(Build.PRODUCT));

    int result=
        res.getIdentifier(buf.toString(), "xml", ctxt.getPackageName());

    return(result == 0 ? mfrResult : result);
  }

  private static String clean(String input) {
    return(input.replaceAll("[\\W]", "_").toLowerCase(Locale.US));
  }

  private boolean isCyanogenMod() {
    return(System.getProperty("os.version").contains("cyanogenmod") || Build.HOST.contains("cyanogenmod"));
  }
}
