package com.example.warhammer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DataAdapter extends BaseAdapter {
    private Context mContext;
    private final ArrayList<Post> posts;

    public DataAdapter(Context c, ArrayList<Post> posts) {
        mContext = c;
        this.posts = posts;
    }

    public int getCount() {
        return posts.size();
    }

    public Object getItem(int position) {
        return posts.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        View grid;
        final Post post = this.posts.get(position);
        if (convertView == null) {
            grid = new View(mContext);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.cellgrid, parent, false);
        } else {
            grid = (View) convertView;
        }

        ImageView imageView = (ImageView) grid.findViewById(R.id.imagepart);
        TextView textView = (TextView) grid.findViewById(R.id.textpart);
        TextView name = (TextView) grid.findViewById(R.id.name);

        imageView.setMaxHeight(10);
        imageView.setMaxWidth(10);

        //добавить название сверху
        name.setText(post.getName());
        textView.setText(post.getPrice(0));
        Picasso.get().load(post.getPhotoThumb()).into(imageView); //загрузить фото из url в нужный эулумент

        return grid;
    }

}

