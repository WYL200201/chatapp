package com.example.group35;

import java.util.ArrayList;

public class FriendResponse {
    public String status;
    public Data data;

    public static class Data {
        public ArrayList<String> friends_name;
        public ArrayList<Integer> chatrooms;
    }
}
