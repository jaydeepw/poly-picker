package nl.changer.polypicker;

import nl.changer.polypicker.R;
import nl.changer.polypicker.model.Image;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;


/**
 * Created by Gil on 04/03/2014.
 */
public class GalleryFragment extends Fragment {

    private static final String TAG = GalleryFragment.class.getSimpleName();

    GridView mGalleryGridView;
    ImageGalleryAdapter mGalleryAdapter;
    ImagePickerActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);

        Log.d(TAG, "onCreateView");
        mGalleryAdapter = new ImageGalleryAdapter(getActivity());
        mGalleryGridView = (GridView) rootView.findViewById(R.id.gallery_grid);
        mActivity = ((ImagePickerActivity) getActivity());


        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.ORIENTATION};
        final String orderBy = MediaStore.Images.Media._ID;
        Cursor imageCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
        while (imageCursor.moveToNext()) {
            Uri uri = Uri.parse(imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            int orientation = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
            mGalleryAdapter.add(new Image(uri, orientation));
        }
        imageCursor.close();

        mGalleryGridView.setAdapter(mGalleryAdapter);
        mGalleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Image image = mGalleryAdapter.getItem(i);
                if (!mActivity.containsImage(image)) {
                	mActivity.addImage(image);
                } else {
                	mActivity.removeImage(image);
                }

                // refresh the view to
               mGalleryAdapter.getView(i, view, adapterView);
            }
        });

        return rootView;
    }

    class ViewHolder {
        ImageView mThumbnail;
        Image mImage;
    }

    public class ImageGalleryAdapter extends ArrayAdapter<Image> {

        public ImageGalleryAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_gallery_thumbnail, null);
                holder = new ViewHolder();
                holder.mThumbnail = (ImageView) convertView.findViewById(R.id.thumbnail_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Image image = getItem(position);
            boolean isSelected = mActivity.containsImage(image);

            ((FrameLayout) convertView).setForeground(isSelected ? getResources().getDrawable(R.drawable.gallery_photo_selected) : null);

            if (holder.mImage == null || !holder.mImage.equals(image)) {
                mActivity.mImageFetcher.loadImage(image.mUri, holder.mThumbnail);
                holder.mImage = image;
            }
            return convertView;
        }
    }

}