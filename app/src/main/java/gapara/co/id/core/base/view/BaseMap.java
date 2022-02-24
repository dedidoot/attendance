package gapara.co.id.core.base.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

public class BaseMap extends SupportMapFragment {

    public View mOriginalContentView;
    public TouchableWrapper mTouchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        try {
            mOriginalContentView = super.onCreateView(inflater, parent, savedInstanceState);
            mTouchView = new TouchableWrapper(getActivity());
            mTouchView.addView(mOriginalContentView);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return mTouchView;
    }

    @Override
    public View getView() {
        return mOriginalContentView;
    }
}
