package com.example.group35;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FetchChatroomListTask {
    private final ArrayList<Chatroom> chatrooms;
    private final ChatroomAdapter chatroomAdapter;
    private final Context context;

    private final ChatApiService apiService;

    public FetchChatroomListTask(Context context, ChatroomAdapter chatroomAdapter, ArrayList<Chatroom> chatrooms) {
        this.context = context;
        this.chatroomAdapter = chatroomAdapter;
        this.chatrooms = chatrooms;

        // Initialize Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://159.138.21.80:5000/api/a3/") // Replace with the base URL of your backend API
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.apiService = retrofit.create(ChatApiService.class);
    }

    public void fetchChatrooms() {
        Call<ChatroomResponse> call = apiService.getChatrooms();

        call.enqueue(new Callback<ChatroomResponse>() {
            @Override
            public void onResponse(Call<ChatroomResponse> call, Response<ChatroomResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChatroomResponse chatroomResponse = response.body();
                    if ("OK".equals(chatroomResponse.getStatus())) {
                        // Clear old data and add the new chatrooms
                        chatrooms.clear();
                        chatrooms.addAll(chatroomResponse.getData());
                        chatroomAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "Failed to load chatrooms", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Server returned an error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChatroomResponse> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
