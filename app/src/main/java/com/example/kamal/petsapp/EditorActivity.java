package com.example.kamal.petsapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kamal.petsapp.data.PetDbHelper;
import com.example.kamal.petsapp.data.PetsContract.PetsEntry;

public class EditorActivity extends AppCompatActivity {

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;


    private int mGender =PetsEntry.GENDER_UNKNOWN;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();
    }

    private void setupSpinner() {

        ArrayAdapter genderPetAdapter = ArrayAdapter.createFromResource(this,R.array.array_gender_options,android.R.layout.simple_spinner_item);
        genderPetAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mGenderSpinner.setAdapter(genderPetAdapter);

      mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              String selection = (String) parent.getItemAtPosition(position);
              if (!TextUtils.isEmpty(selection)) {
                  if (selection.equals(getString(R.string.gender_male))) {
                      mGender = PetsEntry.GENDER_MALE;
                  } else if (selection.equals(getString(R.string.gender_female))) {
                      mGender = PetsEntry.GENDER_FEMALE;
                  } else {
                      mGender = PetsEntry.GENDER_UNKNOWN;
                  }
              } }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {
              mGender = PetsEntry.GENDER_UNKNOWN;

          }
      });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                insertPet();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertPet() {

        String name = mNameEditText.getText().toString().trim();
        String breed=mBreedEditText.getText().toString().trim();
        String weight=mWeightEditText.getText().toString().trim();

        //converting string weight to int
        int weightPet = Integer.parseInt(weight);

        ContentValues userValues = new ContentValues();
        userValues.put(PetsEntry.PET_NAME,name);
        userValues.put(PetsEntry.PET_BREED,breed);
        userValues.put(PetsEntry.PET_GENDER,mGender);
        userValues.put(PetsEntry.PET_WEIGHT,weightPet);

        PetDbHelper petDbHelper = new PetDbHelper(this);
        SQLiteDatabase db = petDbHelper.getWritableDatabase();

        long row_id=db.insert(PetsEntry.TABLE_NAME,null,userValues);
        if(row_id==-1){
            Toast.makeText(this, "Pet Not Inserted", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"Pet Saved with id:"+row_id,Toast.LENGTH_SHORT).show();
        }

    }
}
