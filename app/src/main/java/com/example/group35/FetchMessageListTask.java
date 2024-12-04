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

public class FetchMessageListTask {
    private static final String BASE_URL = "http://159.138.21.80:5000/api/a3/";
    private static final String emptyMessageList = "Server returns 0 messages";

    private final ArrayList<Message> messages;
    private final MessageAdapter messageAdapter;
    private final ListView lv;
    private final IdNamePage idNamePage;
    private final Context context;
    private final int originMessagesSize;

    public FetchMessageListTask(ArrayList<Message> messages, MessageAdapter messageAdapter, ListView lv, IdNamePage idNamePage, Context context) {
        this.messages = messages;
        this.messageAdapter = messageAdapter;
        this.lv = lv;
        this.context = context;
        this.idNamePage = idNamePage;
        this.originMessagesSize = this.messages.size();
    }

    public void fetchMessages() {
        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MessageApiService apiService = retrofit.create(MessageApiService.class);

        // Make the network request
        apiService.getMessages(idNamePage.chatroomId, idNamePage.currentPage).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MessageResponse messageResponse = response.body();

                    if ("OK".equals(messageResponse.status)) {
                        idNamePage.currentPage = messageResponse.data.current_page;
                        idNamePage.totalPage = messageResponse.data.total_pages;

                        for (MessageResponse.MessageData messageData : messageResponse.data.messages) {
                            Message message = new Message(
                                    messageData.id,
                                    messageData.message,
                                    messageData.user_id,
                                    messageData.name,
                                    messageData.user_id == idNamePage.user_id ? Message.TYPE_SEND : Message.TYPE_RECEIVE,
                                    messageData.message_time
                            );
                            messages.add(0, message);
                        }

                        messageAdapter.notifyDataSetChanged();
                        lv.setSelection(messages.size() - originMessagesSize);
                    } else {
                        Toast.makeText(context, emptyMessageList, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Failed to fetch messages", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Define API service interface specific to FetchMessageListTask
    private interface MessageApiService {
        @GET("get_messages")
        Call<MessageResponse> getMessages(@Query("chatroom_id") int chatroomId, @Query("page") int page);
    }

    // Response model
    public static class MessageResponse {
        public String status;
        public MessageDataWrapper data;

        public static class MessageDataWrapper {
            public int current_page;
            public int total_pages;
            public ArrayList<MessageData> messages;
        }

        public static class MessageData {
            public int id;
            public String message;
            public int user_id;
            public String name;
            public String message_time;
        }
    }
}
