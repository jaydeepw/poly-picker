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
        Espresso.onView(withId(R.id.get_n_images)).perform(ViewActions.click());
    }
}