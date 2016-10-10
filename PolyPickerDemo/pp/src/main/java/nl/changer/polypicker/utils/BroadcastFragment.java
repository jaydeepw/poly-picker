package nl.changer.polypicker.utils;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by laurentmeyer on 10/10/16.
 */
public abstract class BroadcastFragment extends Fragment{

    public abstract String getName();
    public abstract BroadcastReceiver setupBroadcastReceiver();
    public abstract IntentFilter setupIntentFilter();

    BroadcastReceiver receiver = setupBroadcastReceiver();
    IntentFilter filter = setupIntentFilter();

    @Override
    public void onResume() {
        super.onResume();
        Log.d("BaseFragment", getName() + " Resume has been called; broadcast receiver has been attached");
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("BaseFragment", getName() + " Destroyed has been called the broadcast manager won't answer anymore");
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(receiver);
    }
}
