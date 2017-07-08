PolyPicker
===========

Android library project for selecting/capturing multiple images from the device.

Result
==========
<img src="https://github.com/jaydeepw/poly-picker/blob/develop/pp-animation.gif" />

Caution!
==========
Eclipse library project structure has been dropped. If you wish to use this library in your eclipse IDE, please checkout [eclipse-develop][1].
No further development will be done or merged into [eclipse-develop][1] branch.


Why?
==========
* Most of the apps we develop require fetching images from camera or gallery.
* Android does not provide multi-selection of images out of the box until [API 18](http://developer.android.com/reference/android/content/Intent.html#EXTRA_ALLOW_MULTIPLE).
* Dealing with camera on variety of hardware and fragmentation in underlying software is difficult.
* There are no libraries that help me multi-choose images from both camera and gallery with beautiful UX.

Features
==========
* Allows taking pictures from camera as well.
* Multi-selection of images from gallery.
* Ability to select/capture images upto a specified limit.
* Preview thumbnails of selected images.

Download
==========

```groovy

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    // and
    // your
    // other
    // dependencies...
}

// add external respository url in addition to having
// your preferred repository.
repositories {
    // for downloading polypicker dependency cwac-camera
    maven {
        url "https://repo.commonsware.com.s3.amazonaws.com"
    }

    // for downloading poly-picker now we are using jitpack.
    // Goodbye Maven Central
    maven {
        url "https://jitpack.io"
    }
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    // your dependencies
    compile 'com.github.jaydeepw:poly-picker:1.0.23'
}

```
Requires Android 4.0+.

Getting started
==========

Add camera permissions and required features to your AndroidManifest.xml

```xml

<uses-feature android:name="android.hardware.camera" />
<uses-feature android:name="android.hardware.camera.autofocus" android:required="false" />

<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```


Request large heap memory using "largeHeap" attribute for your application. This will avoid application to
crash on low memory devices. The side effect would be that your application may force
other applications to be kicked out of memory. Nothing very severe.

```xml

<application
		android:icon="@drawable/ic_launcher"
		android:label="@string/app_name"
		android:largeHeap="true">
		.
		.
</application>

```

Declare the PolyPicker activity in your AndroidManifest.xml with some theme
that is a descendent of AppCompat.

```xml

<activity
            android:name="nl.changer.polypicker.ImagePickerActivity" />
```

Start the PolyPicker activity and get the result back.

```java

private void getImages() {
	    Intent intent = new Intent(mContext, ImagePickerActivity.class);
        Config config = new Config.Builder()
                .setTabBackgroundColor(R.color.white)    // set tab background color. Default white.
                .setTabSelectionIndicatorColor(R.color.blue)
                .setCameraButtonColor(R.color.green)
                .setSelectionLimit(2)    // set photo selection limit. Default unlimited selection.
                .build();
        ImagePickerActivity.setConfig(config);
        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
	super.onActivityResult(requestCode, resultCode, intent);

	if (resultCode == Activity.RESULT_OK) {
		if (requestCode == INTENT_REQUEST_GET_IMAGES) {
			Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

            if (parcelableUris == null) {
                return;
            }

            // Java doesn't allow array casting, this is a little hack
            Uri[] uris = new Uri[parcelableUris.length];
            System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);

            if (uris != null) {
                for (Uri uri : uris) {
                    Log.i(TAG, " uri: " + uri);
                    mMedia.add(uri);
                }

                showMedia();
            }
		}
	}
}

```

Testing Snapshot build
==========

Snapshot builds are development builds that need refining and bug fixes. Open source community can greatly
help in achieveing this by testing such builds and logging issues and feedback that can make PolyPicker better, together.
Add snapshot dependency to your app module's build.gradle file

```groovy

repositories {
    // for downloading Polypicker dependency cwac-camera
    maven {
        url "https://repo.commonsware.com.s3.amazonaws.com"
    }

    // for downloading polypicker v1.0.13-SNAPSHOT
    maven {
        url "https://oss.sonatype.org/content/repositories/snapshots/"
    }
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.android.support:appcompat-v7:21.0.+'
    // and other dependencies

    // PolyPicker dependency.
    compile 'net.the4thdimension:poly-picker:1.0.13-SNAPSHOT'
}

```

Add camera permissions and required features to your AndroidManifest.xml

```xml

<uses-feature android:name="android.hardware.camera" />
<uses-feature android:name="android.hardware.camera.autofocus" android:required="false" />

<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```


Request large heap memory using "largeHeap" attribute for your application. This will avoid application to
crash on low memory devices. The side effect would be that your application may force
other applications to be kicked out of memory. Nothing very severe.

```xml

<application
		android:icon="@drawable/ic_launcher"
		android:label="@string/app_name"
		android:largeHeap="true">
		.
		.
</application>

```

Declare the PolyPicker activity in your AndroidManifest.xml

```xml

<activity
            android:name="nl.changer.polypicker.ImagePickerActivity" />
```

Start PolyPicker activity to request images.

```java

// start polypicker activity to grab some images.
Intent intent = new Intent(mContext, ImagePickerActivity.class);
        Config config = new Config.Builder()
                .setTabBackgroundColor(R.color.white)    // set tab background color. Default white.
                .setTabSelectionIndicatorColor(R.color.blue)
                .setCameraButtonColor(R.color.green)
                .setSelectionLimit(2)    // set photo selection limit. Default unlimited selection.
                .build();
        ImagePickerActivity.setConfig(config);
        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);


// parse images returned by polypicker
@Override
protected void onActivityResult(int requestCode, int resuleCode, Intent intent) {
	super.onActivityResult(requestCode, resuleCode, intent);

	if (resuleCode == Activity.RESULT_OK) {
		if (requestCode == INTENT_REQUEST_GET_IMAGES) {
			Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

            if (parcelableUris == null) {
                return;
            }

            // Java doesn't allow array casting, this is a little hack
            Uri[] uris = new Uri[parcelableUris.length];
            System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);

            if (uris != null) {
                for (Uri uri : uris) {
                    Log.i(TAG, " uri: " + uri);
                    mMedia.add(uri);
                }

                showMedia();
            }
		}
	}
}

```

Contributing
==============

Please fork this repository and contribute back using
[pull requests](https://github.com/jaydeepw/poly-picker/pulls).

Please follow Android code [style guide](https://source.android.com/source/code-style.html)

## You can contribute to polypicker in following ways
 * Test on multiple devices you have
 * Write unit tests
 * Write UI tests
 * Help with string translations
 * Fix open issues in the library

Developed by
============

 * Jaydeep Wagh - <jaydeep.w@gmail.com>
 * Twitter - [Jaydeep_W](https://twitter.com/Jaydeep_W)

Credits
==========
 * This project is inspired by and modified from an existing project mentioned below.
[android-multiple-image-picker](https://github.com/giljulio/android-multiple-image-picker)

 * Dealing with camera on variety of hardware and fragmentation in underlying software is difficult.
CommonsGuy's library [Cwac Camera](https://github.com/commonsguy/cwac-camera) helped handle it better in this project

Donations!
==========
* **Using Bitcoins**: If this project has helped you understand issues, be productive by using this library in your app or just being nice with me, you can always donate me Bitcoins at this address `3QJEmgqXsT1CFLtURYWxzmww59DdKYVwNk`

* **Using Paypal**: [Pay Jay](https://www.paypal.me/jaydeepw)


Alternative projects
==========
* [android-multiple-image-picker](https://github.com/giljulio/android-multiple-image-picker)
* [MultipleImagePick](https://github.com/luminousman/MultipleImagePick)


Release Notes
============

## 1.0.23

* [Fix image rotation](https://github.com/jaydeepw/poly-picker/pull/108)

## v1.0.22

* Add Danish translations

## v1.0.17

* Add Japanese and Portuguese(Brazil) translations

## v1.0.14

* Add autofocus feature when taking picture using camera.
* Material theme for camera fragment
* Configurable UI controls to match the theme of the host application using the library.

## v1.0.11

* Fix leaking progress dialog window when the device orientation changes

## v1.0.10

* Persist captured images even on device orientation changes

## v1.0.9

* Replace camera view with CommonsGuy camera view which is tested well and handles camera functionality better on variety of hardware.

[1]: https://github.com/jaydeepw/poly-picker/tree/eclipse-develop
