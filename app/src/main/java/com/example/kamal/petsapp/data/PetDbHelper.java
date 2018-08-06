package com.example.kamal.petsapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.kamal.petsapp.data.PetsContract.PetsEntry;


public class PetDbHelper extends SQLiteOpenHelper {

    //creating constant for database name
    private static final String DATABASE_NAME="petsdata.db";

    //creating constant for database version
    private static final int DATABASE_VERSION=1;


    public PetDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_PETS_TABLE = "CREATE TABLE "+PetsEntry.TABLE_NAME+"("+PetsEntry.PET_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PetsEntry.PET_NAME + " TEXT NOT NULL, "
                + PetsEntry.PET_BREED + " TEXT, "
                + PetsEntry.PET_GENDER + " INTEGER NOT NULL, "
                + PetsEntry.PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_PETS_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
