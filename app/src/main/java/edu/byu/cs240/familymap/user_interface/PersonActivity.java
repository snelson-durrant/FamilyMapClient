package edu.byu.cs240.familymap.user_interface;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import java.util.List;

import edu.byu.cs240.familymap.R;
import edu.byu.cs240.familymap.data_storage.DataModel;
import edu.byu.cs240.familymap.data_storage.FamilyMember;
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
        List<FamilyMember> personFamily = dataModel.getFamilyMembers(activityPerson);

        TextView firstName = findViewById(R.id.firstNameSection);
        firstName.setText(activityPerson.getFirstName());
        TextView lastName = findViewById(R.id.lastNameSection);
        lastName.setText(activityPerson.getLastName());
        TextView gender = findViewById(R.id.genderSection);
        if (activityPerson.getGender().equals("m")) {
            String male = "Male";
            gender.setText(male);
        } else {
            String female = "Female";
            gender.setText(female);
        }

        expandableListView.setAdapter(new ExpandableListAdapter(personEvents, personFamily));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }

        return true;
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int EVENTS_GROUP_POSITION = 0;
        private static final int FAMILY_GROUP_POSITION = 1;

        private final List<Event> personEvents;
        private final List<FamilyMember> personFamily;

        ExpandableListAdapter(List<Event> personEvents, List<FamilyMember> personFamily) {
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
                    throw new IllegalArgumentException("Unrecognized group position: "
                            + groupPosition);
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
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                                 ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group,
                        parent, false);
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
                    throw new IllegalArgumentException("Unrecognized group position: "
                            + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {
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

            eventItemView.setOnClickListener(v -> {

                dataModel.setTransitionEvent(personEvents.get(childPosition));

                Intent i = new Intent(getApplicationContext(), EventActivity.class);
                startActivity(i);
            });
        }

        private void initializeFamilyView(View familyItemView, final int childPosition) {

            Drawable genderIcon;
            if (personFamily.get(childPosition).getPerson().getGender().equals("m")) {
                genderIcon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_male)
                        .color(Color.BLUE).sizeDp(40);
            } else {
                genderIcon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_female)
                        .color(Color.MAGENTA).sizeDp(40);
            }

            ImageView icon = familyItemView.findViewById(R.id.itemImageView);
            icon.setImageDrawable(genderIcon);

            TextView familyInfoView = familyItemView.findViewById(R.id.itemTextView);
            String eventInfo = personFamily.get(childPosition).getPerson().getFirstName() +
                    " " + personFamily.get(childPosition).getPerson().getLastName() +
                    "\n" + personFamily.get(childPosition).getRelationship();
            familyInfoView.setText(eventInfo);

            familyItemView.setOnClickListener(v -> {

                dataModel.setTransitionPerson(personFamily.get(childPosition).getPerson());

                Intent i = new Intent(getApplicationContext(), PersonActivity.class);
                startActivity(i);
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}

