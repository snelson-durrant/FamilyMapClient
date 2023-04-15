package edu.byu.cs240.familymap.user_interface;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.byu.cs240.familymap.R;
import edu.byu.cs240.familymap.data_storage.DataModel;
import model.Event;
import model.Person;


public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;
    private Event selectedEvent;
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
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_map, container, false);

        setHasOptionsMenu(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        View logoutSection = view.findViewById(R.id.mapTextView);

        logoutSection.setOnClickListener((isClicked) -> {

            if (selectedEvent != null) {

                Person associatedPerson = dataModel.getDataUserPerson();
                for (Person person : dataModel.getDataPeople()) {
                    if (selectedEvent.getPersonID().equals(person.getPersonID())) {
                        associatedPerson = person;
                    }
                }
                dataModel.setTransitionPerson(associatedPerson);

                Intent i = new Intent(getContext(), PersonActivity.class);
                startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {

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
    public void onMapReady(@NonNull GoogleMap googleMap) {

        map = googleMap;
        map.setOnMapLoadedCallback(this);

        updateMarkers();

        map.setOnMarkerClickListener(marker -> {
            selectedEvent = (Event) marker.getTag();
            LatLng location = new LatLng(selectedEvent.getLatitude(),
                    selectedEvent.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLng(location));

            map.clear();
            updateMarkers();
            updateLines();
            updateEventInfo();
            return true;
        });

        Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_android)
                .color(Color.GREEN).sizeDp(40);;
        ImageView icon = requireView().findViewById(R.id.eventImageView);
        icon.setImageDrawable(genderIcon);

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

            if (selectedEvent != null) {

                boolean stillExists = false;
                for (Event filteredEvent : dataModel.getFilteredEvents()) {
                    if (selectedEvent.getEventID().equals(filteredEvent.getEventID())) {

                        updateLines();
                        updateEventInfo();
                        stillExists = true;
                    }
                }

                if (!stillExists) {

                    TextView eventInfo = requireView().findViewById(R.id.eventTextView);
                    String defaultText = "Click on any marker to see event details";
                    eventInfo.setText(defaultText);

                    Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_android)
                            .color(Color.GREEN).sizeDp(40);;
                    ImageView icon = requireView().findViewById(R.id.eventImageView);
                    icon.setImageDrawable(genderIcon);
                }
            }
        }
    }

    private void updateMarkers() {

        List<String> eventTypes = dataModel.getAllEventTypes();

        dataModel.filter();

        for (Event event : dataModel.getFilteredEvents()) {

            LatLng location = new LatLng(event.getLatitude(), event.getLongitude());

            int colorIndex = 0;
            for (int i = 0; i < eventTypes.size(); i++) {
                if (eventTypes.get(i).equalsIgnoreCase(event.getEventType())) {
                    colorIndex = i % 10;
                }
            }

            Marker marker = map.addMarker(new MarkerOptions().position(location)
                    .icon(BitmapDescriptorFactory.defaultMarker(possibleColors.get(colorIndex))));
            marker.setTag(event);
            marker.setTitle(event.getCity() + ", " + event.getCountry());
        }
    }

    private void updateLines() {

        if (selectedEvent != null) {
            Person associatedPerson = dataModel.getDataUserPerson();
            for (Person person : dataModel.getDataPeople()) {
                if (selectedEvent.getPersonID().equals(person.getPersonID())) {
                    associatedPerson = person;
                }
            }

            if (dataModel.isFamilyTreeLines()) {

                lineRecurse(associatedPerson.getFatherID(), selectedEvent, 20);
                lineRecurse(associatedPerson.getMotherID(), selectedEvent, 20);
            }

            if (dataModel.isLifeStoryLines()) {

                ArrayList<Event> orderedEvents = dataModel.orderPersonEvents(associatedPerson);
                if (!orderedEvents.isEmpty()) {

                    Event firstEvent = orderedEvents.get(0);
                    for (Event lifeEvent : orderedEvents) {

                        if (!lifeEvent.equals(firstEvent)) {

                            PolylineOptions line = new PolylineOptions().add(
                                            new LatLng(firstEvent.getLatitude(),
                                                    firstEvent.getLongitude()),
                                            new LatLng(lifeEvent.getLatitude(),
                                                    lifeEvent.getLongitude()))
                                    .width(15).color(Color.YELLOW);
                            map.addPolyline(line);
                        }
                        firstEvent = lifeEvent;
                    }
                }
            }

            if (dataModel.isSpouseLines()) {

                if (associatedPerson.getSpouseID() != null) {
                    for (Person person : dataModel.getDataPeople()) {
                        if (associatedPerson.getSpouseID().equals(person.getPersonID())) {

                            ArrayList<Event> orderedEvents = dataModel.orderPersonEvents(person);
                            if (!orderedEvents.isEmpty()) {

                                Event firstEvent = orderedEvents.get(0);
                                if (firstEvent != null) {

                                    PolylineOptions line = new PolylineOptions().add(
                                                    new LatLng(selectedEvent.getLatitude(),
                                                            selectedEvent.getLongitude()),
                                                    new LatLng(firstEvent.getLatitude(),
                                                            firstEvent.getLongitude()))
                                            .width(15).color(Color.BLUE);
                                    map.addPolyline(line);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private void lineRecurse(String inputID, Event inputEvent, int inputWeight) {

        if (inputID != null && inputEvent != null) {
            for (Person person : dataModel.getDataPeople()) {
                if (inputID.equals(person.getPersonID())) {

                    ArrayList<Event> orderedEvents = dataModel.orderPersonEvents(person);
                    if (!orderedEvents.isEmpty()) {

                        Event firstEvent = orderedEvents.get(0);
                        if (firstEvent != null) {

                            PolylineOptions line = new PolylineOptions().add(
                                            new LatLng(inputEvent.getLatitude(),
                                                    inputEvent.getLongitude()),
                                            new LatLng(firstEvent.getLatitude(),
                                                    firstEvent.getLongitude()))
                                    .width(inputWeight).color(Color.RED);
                            map.addPolyline(line);
                            inputEvent = firstEvent;
                        }
                    }
                }
            }

            for (Person person : dataModel.getDataPeople()) {
                if (inputID.equals(person.getPersonID())) {
                    lineRecurse(person.getFatherID(), inputEvent, inputWeight - 4);
                    lineRecurse(person.getMotherID(), inputEvent, inputWeight - 4);
                }
            }
        }
    }

    private void updateEventInfo() {

        if (selectedEvent != null) {

            Person associatedPerson = dataModel.getDataUserPerson();
            for (Person person : dataModel.getDataPeople()) {
                if (selectedEvent.getPersonID().equals(person.getPersonID())) {
                    associatedPerson = person;
                }
            }

            Drawable genderIcon;
            if (associatedPerson.getGender().equals("m")) {
                genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male)
                        .color(Color.BLUE).sizeDp(40);
            } else {
                genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female)
                        .color(Color.MAGENTA).sizeDp(40);
            }

            ImageView icon = requireView().findViewById(R.id.eventImageView);
            icon.setImageDrawable(genderIcon);

            TextView eventInfo = requireView().findViewById(R.id.eventTextView);
            String textInfo = associatedPerson.getFirstName() + " " +
                    associatedPerson.getLastName() + "\n" +
                    selectedEvent.getEventType().toUpperCase() + ": " +
                    selectedEvent.getCity() + ", " + selectedEvent.getCountry() +
                    " (" + selectedEvent.getYear() + ")";
            eventInfo.setText(textInfo);
        }
    }
}