package com.example.kamal.petsapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public final class PetsContract {


    public PetsContract(){
        //empty constructor
    }

    //class for creatin basic schema for our Pets Database

    //constant for content_authority to used in the URI
    public static final String CONTENT_AUTHORITY = "com.example.kamal.petsapp";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_PETS = "pets";




    public static final class PetsEntry implements BaseColumns {

         //uri to be referenced when need to communicate with content provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;


        //defining string constant for table name
        public static final String TABLE_NAME="pets";

        //defining constant for id
        public static final String PET_ID=BaseColumns._ID;

         //defining constant for 2nd column name
         public static final String PET_NAME="name";

        //defining constant for 3rd column gender
        public static final String PET_GENDER="gender";

        //defining constant for 4th column breed
        public static final String PET_BREED="breed";

        //defining constant for 5th column weight
        public static final String PET_WEIGHT="weight";



        //possible 3 values of pet gender
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;


        public static boolean validGender(int gender) {
            return (gender == GENDER_MALE || gender == GENDER_FEMALE || gender == GENDER_UNKNOWN);
        }
    }
}
