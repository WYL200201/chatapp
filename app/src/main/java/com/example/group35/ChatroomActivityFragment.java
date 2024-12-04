package com.example.group35;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatroomActivityFragment extends Fragment {
    private ArrayList<Chatroom> chatrooms;
    private ListView lv;
    private ChatroomAdapter chatroomAdapter;
    private LinearLayout layout;
    private ImageView mHBack, mHHead, mUserLine;
    private TextView mUserName, mID;
    private ItemView mNickName;

    private AlertDialog dialog;
    private EditText id_search_box;
    private Button friend_search, add_friend;
    private LinearLayout friend_display;
    private TextView friend_id_display, friend_name_display;

    private static String user_name = "";
    private static int user_id = 0;
    private static boolean hasLoggedIn = false;
    private ArrayList<String> wallet = new ArrayList<>();
    private String currentContent = "public rooms";

    private static final String BASE_URL = "http://159.138.21.80:5000/api/a3/";
    private static final String GET_USER_URL = BASE_URL + "get_user";
    private static final String CHECK_FRIEND_URL = BASE_URL + "check_friend";
    private static final String BEFRIEND_URL = BASE_URL + "add_friends";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatroom, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://159.138.21.80/api/a3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ChatApiService chatApiService = retrofit.create(ChatApiService.class);

        initViews(view);
        setupListView();
        setupBottomNavigation(view);
        loadChatrooms();
    }

    private void initViews(View view) {
        layout = view.findViewById(R.id.account_layout);
        mHBack = view.findViewById(R.id.h_back);
        mHHead = view.findViewById(R.id.h_head);
        mUserLine = view.findViewById(R.id.user_line);
        mUserName = view.findViewById(R.id.user_name);
        mID = view.findViewById(R.id.user_id);
        mNickName = view.findViewById(R.id.nickName);
        lv = view.findViewById(R.id.listview_main);
        toggleAccountLayoutVisibility(false);
    }

    private void toggleAccountLayoutVisibility(boolean isVisible) {
        int visibility = isVisible ? View.VISIBLE : View.GONE;
        layout.setVisibility(visibility);
        mHBack.setVisibility(visibility);
        mHHead.setVisibility(visibility);
        mUserLine.setVisibility(visibility);
        mUserName.setVisibility(visibility);
        mID.setVisibility(visibility);
        mNickName.setVisibility(visibility);
    }

    private void setupListView() {
        chatrooms = new ArrayList<>();
        chatroomAdapter = new ChatroomAdapter(getActivity(), R.layout.listview_chatroom_item, chatrooms);
        lv.setAdapter(chatroomAdapter);

        lv.setOnItemClickListener((parent, view, position, id) -> {
            if (hasLoggedIn) {
                openChatroom(chatrooms.get(position));
            } else {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
    }

    private void setupBottomNavigation(View view) {
        BottomNavigationView navigation = view.findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.public_chatrooms:
                    loadChatrooms();
                    return true;
                case R.id.friends:
                    loadFriends();
                    return true;
                    //account
                case R.id.wallet:
                    loadAccount();
                    return true;
                default:
                    return false;
            }
        });
    }

    private void loadChatrooms() {
        currentContent = "public rooms";
        chatrooms.clear();
        chatroomAdapter.notifyDataSetChanged();

        // 使用 FetchChatroomListTask 通过 Retrofit 加载聊天室列表
        FetchChatroomListTask task = new FetchChatroomListTask(getContext(), chatroomAdapter, chatrooms);
        task.fetchChatrooms();
    }

    private void loadFriends() {
        currentContent = "friends";
        chatrooms.clear();
        chatroomAdapter.notifyDataSetChanged();
        toggleAccountLayoutVisibility(false);

        FetchFriendsTask task = new FetchFriendsTask(chatrooms, chatroomAdapter, lv, getContext());
        task.fetchFriends(user_id);
    }

    private void loadAccount() {
        currentContent = "account";
        chatrooms.clear();
        chatroomAdapter.notifyDataSetChanged();
        toggleAccountLayoutVisibility(true);

        FetchAccount accountTask = new FetchAccount(getContext(), user_id, wallet);
        accountTask.fetchAccount();

        setAccountData();
    }

    private void setAccountData() {
        mUserName.setText(user_name);
        mID.setText(String.valueOf(user_id));
        mNickName.setRightDesc(user_name);

        Glide.with(getActivity())
                .load(R.drawable.head)
                .apply(new RequestOptions().transform(new BlurTransformation(25)))
                .into(mHBack);

        Glide.with(getActivity())
                .load(R.drawable.head)
                .apply(new RequestOptions().transform(new CropCircleTransformation()))
                .into(mHHead);
    }

    private void openChatroom(Chatroom chatroom) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("chatroom_name", chatroom.name);
        bundle.putString("user_name", user_name);
        bundle.putInt("chatroom_id", chatroom.id);
        bundle.putInt("user_id", user_id);
        intent.putExtra("data", bundle);
        startActivity(intent);
    }

    public static void setLoginInformation(boolean status, String userName, int userId) {
        hasLoggedIn = status;
        user_name = userName;
        user_id = userId;
    }

    public void createNewDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        View friendSearchView = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_friend, null);
        id_search_box = friendSearchView.findViewById(R.id.id_search_box);
        friend_search = friendSearchView.findViewById(R.id.friend_search);
        friend_display = friendSearchView.findViewById(R.id.friend_display);
        friend_name_display = friendSearchView.findViewById(R.id.friend_name_display);
        friend_id_display = friendSearchView.findViewById(R.id.friend_id_display);
        add_friend = friendSearchView.findViewById(R.id.add_friend);

        dialogBuilder.setView(friendSearchView);
        dialog = dialogBuilder.create();
        dialog.show();

        friend_search.setOnClickListener(v -> searchFriend(id_search_box.getText().toString().trim()));
        add_friend.setOnClickListener(v -> {
            if (!add_friend.getText().toString().equalsIgnoreCase("Friended")) {
                addFriend(String.valueOf(user_id), friend_id_display.getText().toString(), user_name, friend_name_display.getText().toString());
            }
        });
    }

    void searchFriend(String friend_id) {
        if (friend_id.isEmpty()) {
            Toast.makeText(getActivity(), "Please Enter the User ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("user_id", friend_id);
        Utils.sendOkHttpGetRequest(GET_USER_URL, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    if (json.getString("status").equalsIgnoreCase("OK")) {
                        JSONArray data = json.getJSONArray("data");
                        getActivity().runOnUiThread(() -> updateFriendDisplay(data));
                    } else {
                        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Friend not found", Toast.LENGTH_SHORT).show());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateFriendDisplay(JSONArray data) {
        try {
            JSONObject friend = data.getJSONObject(0);
            friend_name_display.setText(friend.getString("name"));
            friend_id_display.setText(friend.getString("id"));
            checkFriend(String.valueOf(user_id), friend.getString("id"));
            friend_display.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkFriend(String user_id, String friend_id) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("friend_id", friend_id);

        Utils.sendOkHttpGetRequest(CHECK_FRIEND_URL, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    getActivity().runOnUiThread(() -> {
                        try {
                            if (json.getString("status").equalsIgnoreCase("OK")) {
                                add_friend.setText("Friended");
                                add_friend.setEnabled(false);
                            } else {
                                add_friend.setText("Add as Friend");
                                add_friend.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addFriend(String user_id, String friend_id, String name, String friend_name) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("user_id", user_id)
                        .add("friend_id", friend_id)
                        .build();

                Request request = new Request.Builder()
                        .url(BEFRIEND_URL)
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                JSONObject json = new JSONObject(response.body().string());
                if (json.getString("status").equalsIgnoreCase("OK")) {
                    getActivity().runOnUiThread(() -> {
                        add_friend.setText("Friended");
                        add_friend.setEnabled(false);
                        if (currentContent.equals("friends")) {
                            loadFriends();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void generateQrCode() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        View qrCodeView = getLayoutInflater().inflate(R.layout.fragment_chatroom_genqrcode, null);
        ImageView qrCodeImage = qrCodeView.findViewById(R.id.qrcode_image);

        String qrCodeUrl = "http://api.qrserver.com/v1/create-qr-code/?data=" + user_id;
        Picasso.get().load(qrCodeUrl).resize(600, 600).into(qrCodeImage);

        dialogBuilder.setView(qrCodeView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    public void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setCaptureActivity(CaptureActivity.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }
}
