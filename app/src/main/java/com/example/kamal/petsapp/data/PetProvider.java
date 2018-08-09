package com.example.kamal.petsapp.data;

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.example.kamal.petsapp.data.PetsContract.PetsEntry;


public class PetProvider extends ContentProvider {


    private static final int PETS=100;
    private static final int PETS_ID=101;


    public PetDbHelper petDbHelper;

    private static final UriMatcher sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);

    static {

        //Adding uri to access the whole table pets

        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY,PetsContract.PATH_PETS,PETS);

        //Adding uri to access a particular '#' row in the pets table
        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY,PetsContract.PATH_PETS+"/#",PETS_ID);


    }


    //creating a global variable of PetDpHelper to access the database



    @Override
    public boolean onCreate() {
        petDbHelper = new PetDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,@Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = petDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(PetsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PETS_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = PetsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(PetsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }



    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetsEntry.CONTENT_LIST_TYPE;
            case PETS_ID:
                return PetsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
       int match=sUriMatcher.match(uri);
       switch (match){
           case PETS: return insertPet(uri,values);

           default:throw new IllegalArgumentException("Insertion is not supported for URI:"+uri);
                    
       }
    }

    private Uri insertPet(Uri uri, ContentValues values) {

        String name=values.getAsString(PetsEntry.PET_NAME);
        if(name==null){
            throw new IllegalArgumentException("Need to insert Pet Name");
        }

        Integer gender = values.getAsInteger(PetsEntry.PET_GENDER);
        if(gender==null || !PetsEntry.validGender(gender)){
            throw new IllegalArgumentException("Gender Value not selected");
        }

        Integer weight=values.getAsInteger(PetsEntry.PET_WEIGHT);
        if(weight==null || weight<=0){
            throw new IllegalArgumentException("Enter a Valid Weight");
        }


        //database variable for writing nto the database
        SQLiteDatabase writePets = petDbHelper.getWritableDatabase();

        long id=writePets.insert(PetsEntry.TABLE_NAME,null,values);

        if(id==-1){
            Log.e("key","Failed to insert row for"+uri);
        }

        return ContentUris.withAppendedId(uri,id);

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = petDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                return database.delete(PetsEntry.TABLE_NAME, selection, selectionArgs);
            case PETS_ID:
                // Delete a single row given by the ID in the URI
                selection = PetsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return database.delete(PetsEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int match=sUriMatcher.match(uri);
        switch (match){
            case PETS:
                return updatePets(uri,values,selection,selectionArgs);

            case PETS_ID:
                selection = PetsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                updatePets(uri,values,selection,selectionArgs);

                default:throw new IllegalArgumentException("Cannot updatefor URI:"+uri);

        }
    }

    private int updatePets(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if(values.containsKey(PetsEntry.PET_NAME)){
            String name=values.getAsString(PetsEntry.PET_NAME);
            if(name==null){
                throw new IllegalArgumentException("Pet Requires a Name");
            }
        }

        if(values.containsKey(PetsEntry.PET_WEIGHT)){
            Integer weight=values.getAsInteger(PetsEntry.PET_WEIGHT);
            if(weight==null || weight<0){
                throw new IllegalArgumentException("Pet Weight should be Valid");
            }
        }

        if(values.containsKey(PetsEntry.PET_GENDER)){
            Integer gender=values.getAsInteger(PetsEntry.PET_GENDER);
            if(gender==null || !PetsEntry.validGender(gender)){
                throw new IllegalArgumentException("Gender Not Selected");
            }
        }

        if(values.size()==0){
            return 0;
        }

        SQLiteDatabase updatePetsDatabase = petDbHelper.getWritableDatabase();

        return updatePetsDatabase.update(PetsEntry.TABLE_NAME,values,selection,selectionArgs);
    }
}
