package polypickerdemo.changer.nl.polypickerdemo;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import nl.changer.polypickerdemo.MainActivity;
import nl.changer.polypickerdemo.R;

import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by jay on 6/3/15.
 */
@LargeTest
public class HelloWorldEspressoTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public HelloWorldEspressoTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public void testListGoesOverTheFold() {

        for (int i = 0; i < 3; i++) {
            takePictureFromCamera();
        }
    }

    private void takePictureFromCamera() {
        Espresso.onView(withId(R.id.get_n_images)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.take_picture)).perform(ViewActions.click());
        waitForSometime(8000);  // picture processing delay
        Espresso.onView(withId(R.id.action_btn_done)).perform(ViewActions.click());
        waitForSometime(500);
        Espresso.onView(withId(R.id.selected_photos_container)).perform(ViewActions.swipeLeft());
        Espresso.onView(withId(R.id.selected_photos_container)).perform(ViewActions.swipeLeft());
    }

    private void waitForSometime(int timeToWaitFor) {
        try {
            Thread.sleep(timeToWaitFor);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}