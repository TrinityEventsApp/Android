package com.tea.cmcdona2.casper.Ents;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tea.cmcdona2.casper.R;

import com.example.cmcdona2.tea.R;

import java.util.ArrayList;
import java.util.List;

public class EntsAdapter extends RecyclerView.Adapter<EntsAdapter.DataHandler> {
    List list = new ArrayList();

    /*
    public EntsAdapter(Context context, int resource) {
        super(context, resource);

    }
    */


    @Override
    public DataHandler onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(DataHandler holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class DataHandler extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView Poster;
        TextView title;
        TextView timing;

        DataHandler(View itemView) {
            super(itemView);
            //cardView = (CardView)itemView.findViewbyId(R.id.cardView);
            Poster = (ImageView)itemView.findViewById(R.id.event_poster);
            title = (TextView)itemView.findViewById(R.id.event_title);
            timing = (TextView)itemView.findViewById(R.id.event_timming);
        }
    }

    /*
    @Override
    public void add(Object object) {
        list.add();
        list.add(object);
    }
    */

    //@Override
    /*public int getCount() {
        return this.list.size();
    }
    */

    //@Override
    public Object getItem(int position) {
        return this.list.get(position);
    }


    /* HOW NECESSARY REALLY?
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        row = convertView;
        DataHandler handler;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.ent_item, parent, false);

            handler.Poster = (ImageView) row.findViewById(R.id.event_poster);
            handler.title = (TextView) row.findViewById(R.id.event_title);
            handler.timing = (TextView) row.findViewById(R.id.event_timming);
            row.setTag(handler);
        } else {
            handler = (DataHandler) row.getTag();
        }
        */
/*
        EntItem dataProvider;
        dataProvider = (EntItem) this.getItem(position);
        handler.Poster.setImageBitmap(dataProvider.getEvent_poster_resource());
        handler.title.setText(dataProvider.getEvents_title());
        handler.timing.setText(dataProvider.getEvents_timing());


        return row;

    }
    */
}
