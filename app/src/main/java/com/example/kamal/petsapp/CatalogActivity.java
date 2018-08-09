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
import android.widget.AdapterView;
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
     PetCursorAdapter petCursorAdapter;
     public static final int PET_LOADER=0;
     public static final String intentExtra="PetID";



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



        ListView petItem_view = (ListView) findViewById(R.id.list_petitem);

        View emptyView = (View) findViewById(R.id.empty_view);
        petItem_view.setEmptyView(emptyView);


        petCursorAdapter = new PetCursorAdapter(this,null);
        petItem_view.setAdapter(petCursorAdapter);

        petItem_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //here parent is the adapter view
                //view is the single item which is clicked
                //position is the position of the view in the listview
                //id is the id of our item clicked
                Log.d("key",String.valueOf(id)+String.valueOf(position));

                Uri ClickPetUri = Uri.withAppendedPath(PetsEntry.CONTENT_URI,String.valueOf(id));

                Intent intent = new Intent(CatalogActivity.this,EditorActivity.class);
                intent.putExtra(intentExtra,ClickPetUri.toString());
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(PET_LOADER,null,this);
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
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                 deleteAllPets();

                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void deleteAllPets() {

        int id = getContentResolver().delete(PetsEntry.CONTENT_URI,null,null);
        Log.d("key",String.valueOf(id));
        if(id!=-1){
            Toast.makeText(this, "Shelter Got Empty :(", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Error Deleting", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

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

        //query will be done in the background
        String[] projection = {
                PetsEntry._ID,
                PetsEntry.PET_NAME,
                PetsEntry.PET_BREED};
       return new CursorLoader(this,PetsEntry.CONTENT_URI,projection,null,null,null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //update the view with the new cursor
        petCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        petCursorAdapter.swapCursor(null);

    }
}
