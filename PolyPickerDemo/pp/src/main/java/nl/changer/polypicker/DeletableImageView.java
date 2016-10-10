package nl.changer.polypicker;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import nl.changer.polypicker.model.Image;

/**
 * Created by laurentmeyer on 10/10/16.
 */
public class DeletableImageView extends RelativeLayout {

    private DeleteListener mDeleteListener;
    private Image mPreviewImage;
    private ImageView mDeleteButton;
    private ImageView mPreviewImageView;

    public DeletableImageView(Context context, Image i, DeleteListener dl) {
        super(context);
        mDeleteListener = dl;
        mPreviewImage = i;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.pp__list_item_selected_thumbnail, this);
        mDeleteButton = (ImageView) findViewById(R.id.pp__delete_image);
        setDeleteListener(this.mDeleteListener);
        mPreviewImageView = (ImageView) findViewById(R.id.pp__selected_photo);
//        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
//        mPreviewImageView.setLayoutParams(new RelativeLayout.LayoutParams(px, px));
        this.setTag(mPreviewImage.mUri);
    }


    public void setDeleteListener(DeleteListener dl){
        mDeleteListener = dl;
        mDeleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeleteListener.onDelete(mPreviewImage);
            }
        });
    }

    // Because of the Fetcher in the activity, we prefer to pass the view not to have OOM errors.
    public ImageView getImagePreviewView(){
        return mPreviewImageView;
    }

    public static abstract class DeleteListener{
         public abstract void onDelete(Image i);
    }
}
