package com.example.group35;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ChatApiService {
    // 发送消息 API
    @POST("send_message")
    Call<Void> sendMessage(@Body Message message);

    // 获取聊天室列表 API
    @GET("get_chatrooms")
    Call<ChatroomResponse> getChatrooms();

    // 获取好友列表 API
    @GET("get_friends")
    Call<FriendResponse> getFriends(@Query("user_id") int userId);

    // 获取消息列表 API
    @GET("get_messages")
    Call<FetchMessageListTask.MessageResponse> getMessages(@Query("chatroom_id") int chatroomId, @Query("page") int page);

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // LoginRequest 内部类
    class LoginRequest {
        private final int user_id;
        private final String password;

        public LoginRequest(int user_id, String password) {
            this.user_id = user_id;
            this.password = password;
        }

        public int getUserId() {
            return user_id;
        }

        public String getPassword() {
            return password;
        }
    }

    // LoginResponse 内部类
    class LoginResponse {
        private String status;
        private String message;
        private String name;

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public String getName() {
            return name;
        }
    }

    // 注册 API
    @POST("sign_up")
    Call<SignUpResponse> signUp(@Body SignUpRequest signUpRequest);

    // SignUpRequest 类
    class SignUpRequest {
        private final int user_id;
        private final String password;
        private final String name;

        public SignUpRequest(int user_id, String password, String name) {
            this.user_id = user_id;
            this.password = password;
            this.name = name;
        }

        public int getUserId() {
            return user_id;
        }

        public String getPassword() {
            return password;
        }

        public String getName() {
            return name;
        }
    }

    // SignUpResponse 类
    class SignUpResponse {
        private String status;
        private String message;

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}

