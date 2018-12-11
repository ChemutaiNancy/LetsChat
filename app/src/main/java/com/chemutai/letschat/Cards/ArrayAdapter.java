package com.chemutai.letschat.Cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chemutai.letschat.R;

import java.util.List;

public class ArrayAdapter extends android.widget.ArrayAdapter<Cards> {

    Context mContext;

    public ArrayAdapter(Context mContext, int resourceId, List<Cards> items) {
        super(mContext, resourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Cards cards_item = getItem(position);

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item,parent, false);
        }

            TextView name = convertView.findViewById(R.id.txtName);
            ImageView image = convertView.findViewById(R.id.image);

            name.setText(cards_item.getName());

            switch (cards_item.getProfileImageUrl()){
                case "default":
                    Glide.with(convertView.getContext()).load(R.mipmap.ic_launcher_circle).into(image);
                    break;

                    default:
                        Glide.clear(image);//ensures the image is erased before its placed
                        Glide.with(convertView.getContext()).load(cards_item.getProfileImageUrl()).into(image);
                        break;
            }

            /*image.setImageResource(R.mipmap.ic_launcher);//random image*/

        return convertView;

    }
}
