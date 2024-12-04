package com.example.group35;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {
    private IdNamePage idNamePage = new IdNamePage();
    private Retrofit retrofit;
    private ChatApiService chatApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getBundleExtra("data").getString("chatroom_name"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Initialize Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("http://159.138.21.80:5000/api/a3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        chatApiService = retrofit.create(ChatApiService.class);

        // Initialize chatroom data
        setIdNamePage();

        // Load ChatActivityFragment into FrameLayout
        loadChatFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            refreshChatFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadChatFragment() {
        ChatActivityFragment fragment = new ChatActivityFragment();
        Bundle args = new Bundle();
        args.putString("chatroom_name", idNamePage.chatroom_name);
        args.putInt("chatroom_id", idNamePage.chatroomId);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_chat, fragment);
        transaction.commit();
    }

    private void refreshChatFragment() {
        ChatActivityFragment fragment = (ChatActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_chat);
        if (fragment != null) {
            fragment.refreshFirstPage();
        } else {
            loadChatFragment(); // Reload fragment if null
        }
    }


    public void setIdNamePage() {
        idNamePage.chatroom_name = getIntent().getBundleExtra("data").getString("chatroom_name");
        idNamePage.user_name = getIntent().getBundleExtra("data").getString("user_name");
        idNamePage.chatroomId = getIntent().getBundleExtra("data").getInt("chatroom_id");
        idNamePage.user_id = getIntent().getBundleExtra("data").getInt("user_id");
    }

    public IdNamePage getIdNamePage() {
        return idNamePage;
    }

}
