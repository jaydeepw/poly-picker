poly-picker
===========

Android library project for selecting/capturing multiple images from the device.


Why?
==========

Android does not provide multi-selection of images out of the box until [API 18](http://developer.android.com/reference/android/content/Intent.html#EXTRA_ALLOW_MULTIPLE).

Features
==========
* Allow taking pictures from camera as well.
* Multi-selection of images from gallery.
* Preview thumbnail of selected images.
* No dependecy. 

Getting started
==========

Add camera permissions and required features to you AndroidManifest.xml

```xml>

<uses-feature android:name="android.hardware.camera" />
<uses-feature android:name="android.hardware.camera.autofocus" />

<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ" />
```

Declare the PolyPicker activity in your AndroidManifest.xml

```xml

<activity
            android:name="nl.changer.polypicker.ui.ImagePickerActivity"
            android:configChanges="mcc|mnc|touchscreen|orientation|uiMode|screenSize|keyboardHidden" />
```

Start the PolyPicker activity and get the result back.

```java

private void getImages() {
	Intent intent = new Intent(mContext, ImagePickerActivity.class);
	startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
}

@Override
protected void onActivityResult(int requestCode, int resuleCode, Intent intent) {
	super.onActivityResult(requestCode, resuleCode, intent);

	if (resuleCode == Activity.RESULT_OK) {
		if (requestCode == INTENT_REQUEST_GET_IMAGES) {
			Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.TAG_IMAGE_URI);
            
            if(parcelableUris == null) {
            	return;
            }

            // show images using uris returned.
		}
	}
}

```

Credits
==========
This project is inspired by and modified from an existing project mentioned below.

[android-multiple-image-picker](https://github.com/giljulio/android-multiple-image-picker)


Alternative
==========
[android-multiple-image-picker](https://github.com/giljulio/android-multiple-image-picker)
[MultipleImagePick](https://github.com/luminousman/MultipleImagePick)