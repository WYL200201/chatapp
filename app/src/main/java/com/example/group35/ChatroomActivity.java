package com.example.group35;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ChatroomActivity extends AppCompatActivity {
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("IEMS5722");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chatroom, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < 500) {
            return true;
        }
        lastClickTime = currentTime;

        ChatroomActivityFragment fragment = (ChatroomActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
        if (fragment == null) {
            Log.e("ChatroomActivity", "Fragment not found");
            return super.onOptionsItemSelected(item);
        }

        switch (item.getItemId()) {
            case R.id.action_logout:
                handleLogout();
                break;
            case R.id.action_add_friend:
                Log.d("ChatroomActivity", getString(R.string.add_friend_clicked));
                fragment.createNewDialog();
                break;
            case R.id.Gen_QRcode:
                Log.d("ChatroomActivity", "Generate QR Code");
                fragment.generateQrCode();
                break;
            case R.id.scan_QR_Code:
                Log.d("ChatroomActivity", "Scan QR Code");
                fragment.scanCode();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void handleLogout() {
        ChatroomActivityFragment.setLoginInformation(false, "", 0);
        Toast.makeText(this, "You have logged out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void finish() {
        moveTaskToBack(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                String qrContent = result.getContents();
                if (qrContent != null) {
                    ChatroomActivityFragment fragment = (ChatroomActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                    if (fragment != null) {
                        fragment.createNewDialog();
                        fragment.searchFriend(qrContent.trim());
                    }
                } else {
                    Toast.makeText(this, getString(R.string.cant_read_qr_code), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
