package iss.workshop.ca_team5;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class FirstGridViewAdapter extends BaseAdapter {
    private  Context context;
    Bitmap [] result;
    int [] imageId;
    private static LayoutInflater inflater=null;
    public FirstGridViewAdapter(MainActivity mainActivity, Bitmap[] downloadedImagesArr, int[] prgmImages) {
        // TODO Auto-generated constructor stub

        context=mainActivity;
        result=downloadedImagesArr;
        imageId=prgmImages;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imageId.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.first_grid, null);
        holder.img=(ImageView) rowView.findViewById(R.id.image_view);

     if(holder.img!=null) {

         if(result.length>0){
             holder.img.setImageBitmap(result[position]);
         }
         else {
             holder.img.setImageResource(imageId[position]);
         }
     }


     rowView.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Toast.makeText(context, "You Clicked " + result[position], Toast.LENGTH_LONG).show();
        }
    });


        return rowView;
    }
}

