package edu.byu.cs240.familymap.user_interface;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import edu.byu.cs240.familymap.R;
import edu.byu.cs240.familymap.data_storage.DataModel;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        DataModel dataModel = DataModel.initialize();

        SwitchCompat lifeStoryLines = findViewById(R.id.lifeStoryLinesSwitch);
        lifeStoryLines.setChecked(dataModel.isLifeStoryLines());
        SwitchCompat familyTreeLines = findViewById(R.id.familyTreeLinesSwitch);
        familyTreeLines.setChecked(dataModel.isFamilyTreeLines());
        SwitchCompat spouseLines = findViewById(R.id.spouseLinesSwitch);
        spouseLines.setChecked(dataModel.isSpouseLines());
        SwitchCompat fatherFilter = findViewById(R.id.fathersSideSwitch);
        fatherFilter.setChecked(dataModel.isFatherFilter());
        SwitchCompat motherFilter = findViewById(R.id.mothersSideSwitch);
        motherFilter.setChecked(dataModel.isMotherFilter());
        SwitchCompat maleFilter = findViewById(R.id.maleEventsSwitch);
        maleFilter.setChecked(dataModel.isMaleFilter());
        SwitchCompat femaleFilter = findViewById(R.id.femaleEventsSwitch);
        femaleFilter.setChecked(dataModel.isFemaleFilter());
        View logoutSection = findViewById(R.id.logoutSection);

        logoutSection.setOnClickListener((isClicked) -> {
            dataModel.setAuthtoken(null);

            Intent i = new Intent(SettingsActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });

        lifeStoryLines.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dataModel.setLifeStoryLines(isChecked);
        });

        familyTreeLines.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dataModel.setFamilyTreeLines(isChecked);
        });

        spouseLines.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dataModel.setSpouseLines(isChecked);
        });

        fatherFilter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dataModel.setFatherFilter(isChecked);
            dataModel.filter();
        });

        motherFilter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dataModel.setMotherFilter(isChecked);
            dataModel.filter();
        });

        maleFilter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dataModel.setMaleFilter(isChecked);
            dataModel.filter();
        });

        femaleFilter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dataModel.setFemaleFilter(isChecked);
            dataModel.filter();
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
}