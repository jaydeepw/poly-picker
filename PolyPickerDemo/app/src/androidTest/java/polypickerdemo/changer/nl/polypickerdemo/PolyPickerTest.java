package polypickerdemo.changer.nl.polypickerdemo;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import java.util.concurrent.TimeUnit;

import nl.changer.polypickerdemo.MainActivity;
import nl.changer.polypickerdemo.R;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Created by jay on 6/3/15.
 */
@LargeTest
public class PolyPickerTest extends ActivityInstrumentationTestCase2<MainActivity> {

    /**
     * A minimum delay required on Nexus5 when autofocus is enabled before taking photograph is 4.5 sec
     * and Sony test device is 10sec
     */
    private static final long PHOTO_PROCESSING_DELAY = TimeUnit.SECONDS.toMillis(10);

    public PolyPickerTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public void testListGoesOverTheFold() {

        for (int i = 0; i < 2; i++) {
            takePictureFromCamera();
        }

        takePicturesFromCameraAndGallery();

        waitForSometime(500);
        onView(withId(R.id.hori_scroll_view)).perform(swipeLeft());
        onView(withId(R.id.hori_scroll_view)).perform(swipeLeft());
        onView(withId(R.id.hori_scroll_view)).perform(swipeLeft());

        // ending delay to visually verify the tests
        waitForSometime(8000);
    }

    private void takePicturesFromCameraAndGallery() {
        onView(withId(R.id.get_images)).perform(click());
        onView(withId(R.id.take_picture)).perform(click());
        waitForSometime(PHOTO_PROCESSING_DELAY);  // picture processing delay

        waitForSometime(500);
        onView(withText(R.string.gallery)).perform(click());

        for (int i = 0; i < 3; i++) {
            onData(anything())
                    .inAdapterView(withId(R.id.gallery_grid))
                    .atPosition(i).perform(click());
        }

        onView(withId(R.id.action_btn_done)).perform(click());
    }

    private void takePictureFromCamera() {
        onView(withId(R.id.get_n_images)).perform(click());
        onView(withId(R.id.take_picture)).perform(click());
        waitForSometime(PHOTO_PROCESSING_DELAY);  // picture processing delay
        onView(withId(R.id.action_btn_done)).perform(click());
    }

    private void waitForSometime(long timeToWaitFor) {
        try {
            Thread.sleep(timeToWaitFor);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}