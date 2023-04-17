package edu.byu.cs240.familymap.user_interface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.List;

import edu.byu.cs240.familymap.R;
import edu.byu.cs240.familymap.data_storage.DataModel;
import model.Event;
import model.Person;

public class SearchActivity extends AppCompatActivity {

    private static final int EVENT_ITEM_VIEW_TYPE = 0;
    private static final int PERSON_ITEM_VIEW_TYPE = 1;
    private final DataModel dataModel = DataModel.initialize();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        dataModel.eventSearch(null);
        dataModel.personSearch(null);

        SearchAdapter adapter = new SearchAdapter(dataModel.getSearchEvents(),
                dataModel.getSearchPeople());
        recyclerView.setAdapter(adapter);

        SearchView simpleSearchView = findViewById(R.id.simpleSearchView);
        simpleSearchView.setIconifiedByDefault(false);

        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // Not used
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                dataModel.eventSearch(newText.toLowerCase());
                dataModel.personSearch(newText.toLowerCase());

                SearchAdapter adapter = new SearchAdapter(dataModel.getSearchEvents(),
                        dataModel.getSearchPeople());
                recyclerView.setAdapter(adapter);

                return false;
            }
        });
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

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private final List<Event> searchEvents;
        private final List<Person> searchPeople;

        SearchAdapter(List<Event> searchEvents, List<Person> searchPeople) {
            this.searchEvents = searchEvents;
            this.searchPeople = searchPeople;
        }

        @Override
        public int getItemViewType(int position) {
            return position < searchPeople.size() ? PERSON_ITEM_VIEW_TYPE : EVENT_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {

            if(position < searchPeople.size()) {
                holder.bind(searchPeople.get(position));
            } else {
                holder.bind(searchEvents.get(position - searchPeople.size()));
            }
        }

        @Override
        public int getItemCount() {
            return searchEvents.size() + searchPeople.size();
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView icon;
        private final TextView description;

        private final int viewType;
        private Person person;
        private Event event;

        SearchViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            icon = itemView.findViewById(R.id.itemImageView);
            description = itemView.findViewById(R.id.itemTextView);
        }

        private void bind(Person person) {
            this.person = person;

            Drawable genderIcon;
            if (person.getGender().equals("m")) {
                genderIcon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_male)
                        .color(Color.BLUE).sizeDp(40);
            } else {
                genderIcon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_female)
                        .color(Color.MAGENTA).sizeDp(40);
            }
            icon.setImageDrawable(genderIcon);

            String personInfo = person.getFirstName() + " " + person.getLastName();
            description.setText(personInfo);
        }

        private void bind(Event event) {
            this.event = event;

            Drawable markerIcon = new IconDrawable(getApplicationContext(),
                    FontAwesomeIcons.fa_map_marker).color(Color.BLACK).sizeDp(40);;
            icon.setImageDrawable(markerIcon);

            Person associatedPerson = dataModel.getDataUserPerson();
            for (Person person : dataModel.getDataPeople()) {
                if (event.getPersonID().equals(person.getPersonID())) {
                    associatedPerson = person;
                }
            }

            String eventInfo = event.getEventType().toUpperCase() + ": " + event.getCity() +
                    ", " + event.getCountry() + " (" + event.getYear() + ")" + "\n" +
                    associatedPerson.getFirstName() + " " + associatedPerson.getLastName();
            description.setText(eventInfo);
        }

        @Override
        public void onClick(View view) {

            if(viewType == EVENT_ITEM_VIEW_TYPE) {

                dataModel.setTransitionEvent(event);
                Intent i = new Intent(getApplicationContext(), EventActivity.class);
                startActivity(i);
            } else {

                dataModel.setTransitionPerson(person);
                Intent i = new Intent(getApplicationContext(), PersonActivity.class);
                startActivity(i);
            }
        }
    }
}
