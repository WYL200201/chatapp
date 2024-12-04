package com.example.group35;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<Message> {
    private final int resourceId;
    public MessageAdapter(Context context, int textViewResourceId, ArrayList<Message> objects) {
        super(context, textViewResourceId, objects);
        this.resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Message message = getItem(position);
        updateView(holder, message);
        return convertView;
    }

    private void updateView(ViewHolder holder, Message message) {
        if (message == null) return;

        // 重置所有布局的可见性
        holder.reset();

        if (message.type == Message.TYPE_RECEIVE) {
            displayReceiveMessage(holder, message);
        } else if (message.type == Message.TYPE_SEND) {
            displaySendMessage(holder, message);
        }
    }

    private void displayReceiveMessage(ViewHolder holder, Message message) {
        holder.leftLayout.setVisibility(View.VISIBLE);
        holder.extraInfo.setVisibility(View.VISIBLE);
        holder.receiveTextView.setText("User: " + message.user_name + "\n" + message.content);
        holder.extraInfo.setText(message.time);
        setViewGravity(holder.extraInfo, Gravity.LEFT);
    }

    private void displaySendMessage(ViewHolder holder, Message message) {
        holder.rightLayout.setVisibility(View.VISIBLE);
        holder.extraInfo.setVisibility(View.VISIBLE);
        holder.sendTextView.setText("User: " + message.user_name + "\n" + message.content);
        holder.extraInfo.setText(message.time);
        setViewGravity(holder.extraInfo, Gravity.RIGHT);
    }

    private void setViewGravity(View view, int gravity) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.gravity = gravity;
        view.setLayoutParams(params);
    }

    static class ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        LinearLayout redEnvelopeLayout;
        TextView receiveTextView;
        TextView sendTextView;
        TextView extraInfo;

        ViewHolder(View view) {
            leftLayout = view.findViewById(R.id.left_layout);
            rightLayout = view.findViewById(R.id.right_layout);
            receiveTextView = view.findViewById(R.id.tv_receive);
            sendTextView = view.findViewById(R.id.tv_send);
            extraInfo = view.findViewById(R.id.extra_info);
        }

        void reset() {
            leftLayout.setVisibility(View.GONE);
            rightLayout.setVisibility(View.GONE);
            extraInfo.setVisibility(View.GONE);
        }
    }
}
