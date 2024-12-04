package com.example.group35;

import android.content.Context;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class FetchFriendsTask {
    private static final String BASE_URL = "http://159.138.21.80:5000/api/a3/";

    private ArrayList<Chatroom> chatrooms;
    private ChatroomAdapter chatroomAdapter;
    private ListView lv;
    private Context context;

    public FetchFriendsTask(ArrayList<Chatroom> chatrooms, ChatroomAdapter chatroomAdapter, ListView lv, Context context) {
        this.chatrooms = chatrooms;
        this.chatroomAdapter = chatroomAdapter;
        this.lv = lv;
        this.context = context;
    }

    public void fetchFriends(int userId) {
        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FriendsApiService friendsApiService = retrofit.create(FriendsApiService.class);

        // Make the network request
        friendsApiService.getFriends(userId).enqueue(new Callback<FriendResponse>() {
            @Override
            public void onResponse(Call<FriendResponse> call, Response<FriendResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    FriendResponse friendResponse = response.body();

                    if ("OK".equals(friendResponse.status)) {
                        chatrooms.clear();
                        for (int i = 0; i < friendResponse.data.friends_name.size(); i++) {
                            String friendName = friendResponse.data.friends_name.get(i);
                            int chatroomId = friendResponse.data.chatrooms.get(i);
                            chatrooms.add(new Chatroom(friendName, chatroomId));
                        }
                        chatroomAdapter.notifyDataSetChanged();
                        lv.setSelection(chatrooms.size() - 1);
                    } else {
                        showToast("No friends found");
                    }
                } else {
                    showToast("Failed to fetch friends");
                }
            }

            @Override
            public void onFailure(Call<FriendResponse> call, Throwable t) {
                showToast("Error: " + t.getMessage());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // Define API service interface specific to FetchFriendsTask
    private interface FriendsApiService {
        @GET("get_friends")
        Call<FriendResponse> getFriends(@Query("user_id") int userId);
    }

    // Response model
    public static class FriendResponse {
        public String status;
        public Data data;

        public static class Data {
            public ArrayList<String> friends_name;
            public ArrayList<Integer> chatrooms;
        }
    }
}
