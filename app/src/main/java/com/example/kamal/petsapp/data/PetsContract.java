package com.example.kamal.petsapp.data;

import android.provider.BaseColumns;

public final class PetsContract {
    //class for creatin basic schema for our Pets Database


    public PetsContract(){
        //empty constructor
    }

    public static final class PetsEntry implements BaseColumns {

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


    }
}
