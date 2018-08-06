package com.example.kamal.petsapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.PeriodicSync;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kamal.petsapp.data.PetDbHelper;
import com.example.kamal.petsapp.data.PetsContract.PetsEntry;

public class CatalogActivity extends AppCompatActivity {

    private PetDbHelper mDbHelper;
    private SQLiteDatabase db;
    private SQLiteDatabase db2write;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);


        //setting floating action button which adds a new pet
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        displayDatabaseInfo();
    }


        private void displayDatabaseInfo(){
            // To access our database, we instantiate our subclass of SQLiteOpenHelper
            // and pass the context, which is the current activity.
             mDbHelper = new PetDbHelper(this);

            // Create and/or open a database to read from it
            db = mDbHelper.getReadableDatabase();

            // Perform this raw SQL query "SELECT * FROM pets"
            // to get a Cursor that contains all rows from the pets table.


            //columns i want to get displayed
            String[] projection = {PetsEntry.PET_ID,
                                   PetsEntry.PET_NAME,
                                   PetsEntry.PET_BREED,
                                   PetsEntry.PET_GENDER,
                                   PetsEntry.PET_WEIGHT};

            Cursor cursor = db.query(PetsEntry.TABLE_NAME,projection,null,null,null,null,null);
            try {
                // Display the number of rows in the Cursor (which reflects the number of rows in the
                // pets table in the database).
                TextView displayView = (TextView) findViewById(R.id.text_view_pet);
                displayView.setText("Number of rows in pets database table: " + cursor.getCount());
                displayView.append("\n"+PetsEntry.PET_ID+"   "+PetsEntry.PET_NAME+"     "+PetsEntry.PET_BREED+"     "+
                        PetsEntry.PET_GENDER+"     "+PetsEntry.PET_WEIGHT);


                int idColumnIndex  = cursor.getColumnIndex(PetsEntry.PET_ID);
                int nameColumnIndex=cursor.getColumnIndex(PetsEntry.PET_NAME);
                int breedColumnIndex=cursor.getColumnIndex(PetsEntry.PET_BREED);
                int genderColumnIndex=cursor.getColumnIndex(PetsEntry.PET_GENDER);
                int weightColumnIndex=cursor.getColumnIndex(PetsEntry.PET_WEIGHT);

                while (cursor.moveToNext()){

                    int id = cursor.getInt(idColumnIndex);
                    String name=cursor.getString(nameColumnIndex);
                    String breed = cursor.getString(breedColumnIndex);
                    int gender=cursor.getInt(genderColumnIndex);
                    int weight=cursor.getInt(weightColumnIndex);

                    displayView.append("\n"+id+"    "+name+"    "+breed+"    "+gender+"     "+weight);
                }
            } finally {
                // Always close the cursor when you're done reading from it. This releases all its
                // resources and makes it invalid.
                cursor.close();
            }
        }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyPet();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void insertDummyPet() {

        //content values to keep data in form of key and value pair
        ContentValues dummyDataValues = new ContentValues();
        dummyDataValues.put(PetsEntry.PET_NAME,"Toto");
        dummyDataValues.put(PetsEntry.PET_BREED,"Terrier");
        dummyDataValues.put(PetsEntry.PET_GENDER,PetsEntry.GENDER_MALE);
        dummyDataValues.put(PetsEntry.PET_WEIGHT,"7");

        //db2write database object to access write functionality in out PetsData database
        db2write=mDbHelper.getWritableDatabase();

        //newPetId is the value retturned by insert which indicates id of the new rw inserted
        long newPetId=db2write.insert(PetsEntry.TABLE_NAME,null,dummyDataValues);

        //f unable to insert the returned id is -1
        if(newPetId==-1){
            Toast.makeText(this,"Dummy Data Not Inserted",Toast.LENGTH_SHORT).show();
        }

    }
}
