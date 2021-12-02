package com.bilocker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bilocker.R;
import com.bilocker.model.ItemCategory;

import java.util.Vector;

public class ItemCategoryAdapter extends ArrayAdapter<ItemCategory> {

    private int resource;
    private LayoutInflater inflater;

    public ItemCategoryAdapter( Context context, int resource, Vector<ItemCategory> objects) {
        super(context, resource, objects);
        this.resource = resource;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(resource,null);
        TextView itemName = convertView.findViewById(R.id.item_category_name);
        ImageView itemImage = convertView.findViewById(R.id.item_category_image);
        itemImage.setImageResource(getItem(position).getImage());
        itemName.setText(getItem(position).getName());
        return convertView;
    }
}
