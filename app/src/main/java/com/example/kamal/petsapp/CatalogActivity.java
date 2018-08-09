package com.example.kamal.petsapp;

import android.app.LoaderManager;
import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.PeriodicSync;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kamal.petsapp.data.PetDbHelper;
import com.example.kamal.petsapp.data.PetProvider;
import com.example.kamal.petsapp.data.PetsContract;
import com.example.kamal.petsapp.data.PetsContract.PetsEntry;

import java.util.List;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private PetDbHelper mDbHelper;
     SQLiteDatabase db;
    SQLiteDatabase db2write;




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

        mDbHelper=new PetDbHelper(this);
        displayDatabaseInfo();

    }


//
private void displayDatabaseInfo() {
    // Define a projection that specifies which columns from the database
    // you will actually use after this query.

    SQLiteDatabase database=mDbHelper.getReadableDatabase();
    String[] projection = {
            PetsEntry._ID,
            PetsEntry.PET_NAME,
            PetsEntry.PET_BREED,
            PetsEntry.PET_GENDER,
            PetsEntry.PET_WEIGHT };


   Cursor cursor=getContentResolver().query(PetsEntry.CONTENT_URI,projection,null,null,null);


        ListView petItem_view = (ListView)findViewById(R.id.list_petitem);

        View emptyView = (View)findViewById(R.id.empty_view);
        petItem_view.setEmptyView(emptyView);

        PetCursorAdapter petCursorAdapter = new PetCursorAdapter(this,cursor);

        petItem_view.setAdapter(petCursorAdapter);


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
         ContentValues contentValues = new ContentValues();
         contentValues.put(PetsEntry.PET_NAME,"Toto");
         contentValues.put(PetsEntry.PET_BREED,"Terrier");
         contentValues.put(PetsEntry.PET_GENDER,PetsEntry.GENDER_MALE);
         contentValues.put(PetsEntry.PET_WEIGHT,6);

         Uri mNewUri = getContentResolver().insert(PetsEntry.CONTENT_URI,contentValues);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
