package edu.byu.cs240.familymap.user_interface;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs240.familymap.R;
import edu.byu.cs240.familymap.data_storage.DataModel;
import model.Event;
import model.Person;

public class PersonActivity extends AppCompatActivity {

    private final DataModel dataModel = DataModel.initialize();
    private Person activityPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        activityPerson = dataModel.getTransitionPerson();
        List<Event> personEvents = dataModel.orderPersonEvents(activityPerson);
        List<Person> personFamily = new ArrayList<>();
        personFamily.add(activityPerson); // TODO ADD HERE

        expandableListView.setAdapter(new ExpandableListAdapter(personEvents, personFamily));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int EVENTS_GROUP_POSITION = 0;
        private static final int FAMILY_GROUP_POSITION = 1;

        private final List<Event> personEvents;
        private final List<Person> personFamily;

        ExpandableListAdapter(List<Event> personEvents, List<Person> personFamily) {
            this.personEvents = personEvents;
            this.personFamily = personFamily;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case EVENTS_GROUP_POSITION:
                    return personEvents.size();
                case FAMILY_GROUP_POSITION:
                    return personFamily.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            // Not used
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // Not used
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case EVENTS_GROUP_POSITION:
                    titleView.setText(R.string.life_events);
                    break;
                case FAMILY_GROUP_POSITION:
                    titleView.setText(R.string.family);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch(groupPosition) {
                case EVENTS_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                    initializeEventsView(itemView, childPosition);
                    break;
                case FAMILY_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                    initializeFamilyView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeEventsView(View eventItemView, final int childPosition) {

            Drawable markerIcon = new IconDrawable(getApplicationContext(),
                    FontAwesomeIcons.fa_map_marker).color(Color.BLACK).sizeDp(40);;
            ImageView icon = eventItemView.findViewById(R.id.itemImageView);
            icon.setImageDrawable(markerIcon);

            TextView eventInfoView = eventItemView.findViewById(R.id.itemTextView);
            String eventInfo = personEvents.get(childPosition).getEventType().toUpperCase() +
                    ": " + personEvents.get(childPosition).getCity() + ", " +
                    personEvents.get(childPosition).getCountry() + " (" +
                    personEvents.get(childPosition).getYear() + ")" + "\n" +
                    activityPerson.getFirstName() + " " + activityPerson.getLastName();
            eventInfoView.setText(eventInfo);

            eventItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // TODO ADD HERE
                }
            });
        }

        private void initializeFamilyView(View familyItemView, final int childPosition) {

            // TODO CHANGE IMAGE

            // TextView trailLocationView = familyItemView.findViewById(R.id.hikingTrailLocation);
            // trailLocationView.setText(hikingTrails.get(childPosition).getLocation());

            familyItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // TODO ADD HERE
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}

