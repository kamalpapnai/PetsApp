package com.example.kamal.petsapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.kamal.petsapp.data.PetsContract;

import org.w3c.dom.Text;

public class PetCursorAdapter extends CursorAdapter {


        public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

            //returning the new view to bindView
            return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

            //view is view returned from newView so that we can put values in it
        TextView petName=(TextView)view.findViewById(R.id.pet_name);
        TextView breedName=(TextView)view.findViewById(R.id.breed_name);

        String name = cursor.getString(cursor.getColumnIndex(PetsContract.PetsEntry.PET_NAME));
        String breed=cursor.getString(cursor.getColumnIndex(PetsContract.PetsEntry.PET_BREED));

        //putting value in the textviews
        petName.setText(name);
        breedName.setText(breed);

    }
}
