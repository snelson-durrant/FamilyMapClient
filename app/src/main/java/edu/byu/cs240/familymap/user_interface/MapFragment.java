package edu.byu.cs240.familymap.user_interface;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.List;

import edu.byu.cs240.familymap.R;
import edu.byu.cs240.familymap.data_storage.DataModel;
import model.Event;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;
    private final DataModel dataModel = DataModel.initialize();
    private final List<Float> possibleColors = Arrays.asList(

            BitmapDescriptorFactory.HUE_RED,
            BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_YELLOW,
            BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_ROSE,
            BitmapDescriptorFactory.HUE_AZURE,
            BitmapDescriptorFactory.HUE_VIOLET,
            BitmapDescriptorFactory.HUE_CYAN
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_map, container, false);

        setHasOptionsMenu(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.fragment_map_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Class newActivityClass = null;

        switch(item.toString()) {
            case "Settings":
                newActivityClass = SettingsActivity.class;
                break;
        }

        Intent i = new Intent(getActivity(), newActivityClass);
        startActivity(i);

        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.setOnMapLoadedCallback(this);

        updateMarkers();

        map.setOnMarkerClickListener(marker -> {
            Event clickedEvent = (Event) marker.getTag();
            LatLng location = new LatLng(clickedEvent.getLatitude(), clickedEvent.getLongitude());
            marker.showInfoWindow();

            map.animateCamera(CameraUpdateFactory.newLatLng(location));
            return true;
        });
    }

    @Override
    public void onMapLoaded() {

        // Callback not used, but needed
    }

    @Override
    public void onResume() {

        super.onResume();
        if (map != null) {

            map.clear();
            updateMarkers();
        }
    }

    private void updateMarkers() {

        List<String> eventTypes = dataModel.getAllEventTypes();

        dataModel.filter();

        for (Event event : dataModel.getFilteredEvents()) {

            LatLng location = new LatLng(event.getLatitude(), event.getLongitude());

            int colorIndex = 0;
            for (int i = 0; i < eventTypes.size(); i++) {
                if (eventTypes.get(i).equals(event.getEventType())) {
                    colorIndex = i % 10;
                }
            }

            Marker marker = map.addMarker(new MarkerOptions().position(location)
                    .icon(BitmapDescriptorFactory.defaultMarker(possibleColors.get(colorIndex))));
            marker.setTag(event);
            marker.setTitle(event.getCity() + ", " + event.getCountry());
        }
    }
}