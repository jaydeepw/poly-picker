poly-picker
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
* There are no libraries that help me multi-choose images from both camera and gallery with beautiful UX.

Features
==========
* Allows taking pictures from camera as well.
* Multi-selection of images from gallery.
* Ability to select/capture images upto a specified limit.
* Preview thumbnails of selected images.
* No dependency.

Download
--------

```groovy

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.android.support:support-v4:21.0.0'
    // and your other dependencies
}

// add external respository url in addition to having
// your preferred repository.
repositories {
    maven {
        url "https://repo.commonsware.com.s3.amazonaws.com"
    }
}

dependencies {
	compile 'net.the4thdimension:poly-picker:1.0.9'
}

```
Requires Android 4.0+.

Getting started
==========

Add camera permissions and required features to your AndroidManifest.xml

```xml

<uses-feature android:name="android.hardware.camera" />
<uses-feature android:name="android.hardware.camera.autofocus" />

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

Start the PolyPicker activity and get the result back.

```java

private void getImages() {
	Intent intent = new Intent(mContext, ImagePickerActivity.class);
	intent.putExtra(ImagePickerActivity.EXTRA_SELECTION_LIMIT, 3);	// allow only upto 3 images to be selected.
	startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
}

@Override
protected void onActivityResult(int requestCode, int resuleCode, Intent intent) {
	super.onActivityResult(requestCode, resuleCode, intent);

	if (resuleCode == Activity.RESULT_OK) {
		if (requestCode == INTENT_REQUEST_GET_IMAGES) {
			Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            
            if(parcelableUris == null) {
            	return;
            }

            // show images using uris returned.
		}
	}
}

```

Contributing
==============

Please fork this repository and contribute back using
[pull requests](https://github.com/jaydeepw/poly-picker/pulls).

Please follow Android code [style guide](https://source.android.com/source/code-style.html)

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


Alternative projects
==========
* [android-multiple-image-picker](https://github.com/giljulio/android-multiple-image-picker)
* [MultipleImagePick](https://github.com/luminousman/MultipleImagePick)


Release Notes
============

## v1.0.9

* Replace camera view with CommonsGuy camera view which is tested well and handles camera functionality better on variety of hardware.

[1]: https://github.com/jaydeepw/poly-picker/tree/eclipse-develop

