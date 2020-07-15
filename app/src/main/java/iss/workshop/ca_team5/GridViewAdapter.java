package iss.workshop.ca_team5;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class GridViewAdapter extends ArrayAdapter {
    private  Context context;
    private    int[] workingImages = {R.drawable.afraid,R.drawable.hidden,R.drawable.what,R.drawable.full,R.drawable.no_way,
            R.drawable.afraid,R.drawable.full,R.drawable.hug,R.drawable.what,R.drawable.peep,
            R.drawable.afraid,R.drawable.full,R.drawable.no_way,R.drawable.stop,R.drawable.stop,
            R.drawable.afraid,R.drawable.full,R.drawable.tired,R.drawable.snore,R.drawable.what};
    @Override
    public int getCount() {
        return workingImages.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    public GridViewAdapter(Context context, int resId)
    {
        super(context,resId);
        this.context=context;
        //this.workingImages=workingImages;

        for(int i=0;i<workingImages.length;i++)
        {
            add(null);
        }
    }

    public View getView(int i, View view, @NonNull ViewGroup viewGroup) {

        MainActivity.MyJavaScriptInterface  test=new MainActivity.MyJavaScriptInterface();
        String[] tt=test.getList();

        LayoutInflater inflater=(LayoutInflater)context.getSystemService(
                Activity.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.first_grid,null);
        //getting view in row_data

        ImageView image = view1.findViewById(R.id.images);


        image.setImageResource(workingImages[i]);
        //  if(workingImages.length==0) {
        //  Picasso.get().load("https://via.placeholder.com/300.png/09f/fffC/O%20https://placeholder.com/").into(image);
        //   }
        //  else {
        //  image.setImageResource(workingImages[i]);
        //Loading image using Picasso
        //Picasso.get().load(workingImages.get(i)).into(image);
        //  }
        return view1;

    }
}

