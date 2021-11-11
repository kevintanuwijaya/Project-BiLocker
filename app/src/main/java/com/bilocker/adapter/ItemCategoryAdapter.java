package com.bilocker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bilocker.R;
import com.bilocker.model.ItemCategory;

import java.util.Vector;

public class ItemCategoryAdapter extends BaseAdapter {

    private Context context;
    private Vector<ItemCategory> categories;
    private LayoutInflater inflater;

    public ItemCategoryAdapter(Context context, Vector<ItemCategory> categories) {
        this.context = context;
        this.categories = categories;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_type_items,null);
        TextView itemName = convertView.findViewById(R.id.item_category_name);
        ImageView itemImage = convertView.findViewById(R.id.item_category_image);
        itemImage.setImageResource(categories.get(position).getImage());
        itemName.setText(categories.get(position).getName());
        return convertView;
    }
}
