package com.example.group35;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatroomAdapter extends ArrayAdapter<Chatroom> {
    private final int resourceId;

    public ChatroomAdapter(Context context, int textViewResourceId, ArrayList<Chatroom> objects) {
        super(context, textViewResourceId, objects);
        this.resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        // Reuse convertView if possible, otherwise inflate a new view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

            // Initialize ViewHolder and bind views
            holder = new ViewHolder();
            holder.chatroom = convertView.findViewById(R.id.chatroom_name);

            // Set the ViewHolder as a tag for future reuse
            convertView.setTag(holder);
        } else {
            // Retrieve the ViewHolder from the tag
            holder = (ViewHolder) convertView.getTag();
        }

        // Get the Chatroom object for the current position
        Chatroom chatroom = getItem(position);
        if (chatroom != null) {
            // Bind the chatroom name to the TextView
            holder.chatroom.setVisibility(View.VISIBLE);
            holder.chatroom.setText(chatroom.name);
        }

        return convertView;
    }

    // Static ViewHolder class to hold view references
    static class ViewHolder {
        TextView chatroom;
    }
}
