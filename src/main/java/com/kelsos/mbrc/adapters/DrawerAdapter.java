package com.kelsos.mbrc.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.model.NavigationEntry;

import java.util.ArrayList;

public class DrawerAdapter extends ArrayAdapter<NavigationEntry> {
    private Context mContext;
    private int mResource;
    private ArrayList<NavigationEntry> mData;
    private Typeface robotoLight;

    public DrawerAdapter(Context context, int resource, ArrayList<NavigationEntry> objects) {
        super(context, resource, objects);
        this.mResource = resource;
        this.mContext = context;
        this.mData = objects;
        robotoLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_light.ttf");
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder;

        NavigationEntry current = mData.get(position);
        if (row == null) {
            LayoutInflater layoutInflater = ((Activity) mContext).getLayoutInflater();
            row = layoutInflater.inflate(mResource, parent, false);

            holder = new Holder();
            holder.itemName = (CheckedTextView) row.findViewById(R.id.dr_option_text);
            holder.itemName.setTypeface(robotoLight);
            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        holder.itemName.setText(current.getLabel());
        return row;
    }

    static class Holder {
        CheckedTextView itemName;
    }
}