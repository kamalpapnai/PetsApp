package com.example.kamal.petsapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.kamal.petsapp.data.PetDbHelper;
import com.example.kamal.petsapp.data.PetsContract;
import com.example.kamal.petsapp.data.PetsContract.PetsEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    private Uri getPetUri;

    private boolean mPetHasChanged;


    private int mGender =PetsEntry.GENDER_UNKNOWN;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        String getUri = getIntent().getStringExtra(CatalogActivity.intentExtra);

        if(getUri!=null){
            getPetUri=Uri.parse(getUri);
            setTitle("Edit Pet");
            Log.d("key","Add Pet Uri:"+String.valueOf(getPetUri));
            getLoaderManager().initLoader(CatalogActivity.PET_LOADER,null,this);

        }else{
            setTitle("Add a Pet");
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        //setting the default value of weight as 0
        mWeightEditText.setText("0");
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();

        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);


    }


    private View.OnTouchListener mTouchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPetHasChanged=true;
            return false;
        }
    };

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
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if ( getPetUri== null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                savePet();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity) if no view has been touched
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.

                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener()
                {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                                Log.d("key","InHomeArea");
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.

        Log.d("key","InShowUnsavedArea");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //for showing up the message of the alert dialog
        builder.setMessage(R.string.unsaved_changes_dialog_msg);

        //setPositive means on the right side ...here we are setting up the discard button whose click is defined in onBackPressed method
        builder.setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("key","InfinishenterArea");
                finish();
            }
        });

        //making keep editing as left button and make a new dailoginterface upon which if we click then the dailog gets dismiss
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.

                Log.d("key","InShowenterArea");
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        Log.d("key","InCreateArea");
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mPetHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.


        //this is just an interface for the discarded button which is set up in showUnsavedChangesDailog method as setPositiveButton
//        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        // User clicked "Discard" button, close the current activity.
//                        Log.d("key","OnBackPressedArea");
//                        finish();
//                    }
//                };

        // Show dialog that there are unsaved changes
        Log.d("key","OnBackPressed2Area");

        //interface for discarded button i being passed to the called method
        showUnsavedChangesDialog();
    }

    private void savePet() {

        //get values from the fields irrespective of inserting or editing
        String name = mNameEditText.getText().toString().trim();
        String breed = mBreedEditText.getText().toString().trim();
        String weight = mWeightEditText.getText().toString().trim();

        if(TextUtils.isEmpty(name) || mGenderSpinner.getSelectedItemPosition()==0){
            return;
        }

        //converting string weight to int
        int weightPet = Integer.parseInt(weight);

        ContentValues userValues = new ContentValues();
        userValues.put(PetsEntry.PET_NAME, name);
        userValues.put(PetsEntry.PET_BREED, breed);
        userValues.put(PetsEntry.PET_GENDER, mGender);
        userValues.put(PetsEntry.PET_WEIGHT, weightPet);



        if(getPetUri!=null){
            //we are in edit mode
            getContentResolver().update(getPetUri,userValues,null,null);
        }

        else {
             //we are in insert activity
            Uri newRowUri = getContentResolver().insert(PetsEntry.CONTENT_URI, userValues);
            Log.d("key", String.valueOf(newRowUri));

            if (newRowUri == null) {
                Toast.makeText(this, "Pet Data not inserted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Pet Data Inserted", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            return new CursorLoader(this,getPetUri,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
       if(data==null || data.getCount()<1){
           return;
       }
       Log.d("key","Row in cusor"+String.valueOf(data.getCount()));
        Log.d("key","Row in cusor is first"+String.valueOf(data.moveToFirst()));

        if(data.moveToFirst()){

            int name_ColumnIndex=data.getColumnIndex(PetsEntry.PET_NAME);
            int breed_ColumnIndex=data.getColumnIndex(PetsEntry.PET_BREED);
            int gender_ColumnIndex=data.getColumnIndex(PetsEntry.PET_GENDER);
            int weight_columnIndex=data.getColumnIndex(PetsEntry.PET_WEIGHT);


            String pet_name = data.getString(name_ColumnIndex);
            String breed_name=data.getString(breed_ColumnIndex);
            int gender = data.getInt(gender_ColumnIndex);
            int weight=data.getInt(weight_columnIndex);



            mNameEditText.setText(pet_name);
            mBreedEditText.setText(breed_name);
            mGenderSpinner.setSelection(gender);
            mWeightEditText.setText(String.valueOf(weight));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.getText().clear();
        mBreedEditText.getText().clear();
        mGenderSpinner.setSelection(0);
        mWeightEditText.getText().clear();

    }
}
