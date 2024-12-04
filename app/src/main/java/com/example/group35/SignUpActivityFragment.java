package com.example.group35;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SignUpActivityFragment extends Fragment {
    private EditText et_user_id;
    private EditText et_user_password;
    private EditText et_user_name;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        et_user_id = view.findViewById(R.id.et_user_id);
        et_user_password = view.findViewById(R.id.et_user_password);
        et_user_name = view.findViewById(R.id.et_user_name);

        view.findViewById(R.id.bt_confirm).setOnClickListener(v -> {
            String userId = et_user_id.getText().toString();
            String userPassword = et_user_password.getText().toString();
            String userName = et_user_name.getText().toString();

            et_user_id.setText("");
            et_user_password.setText("");
            et_user_name.setText("");

            if (!validateInput(userId, userPassword, userName)) {
                return;
            }

            int intUserId = Integer.parseInt(userId);
            signUpUser(intUserId, userPassword, userName);
        });
    }

    private boolean validateInput(String userId, String userPassword, String userName) {
        if (userId.isEmpty()) {
            showToast(getString(R.string.user_id_empty));
            return false;
        }

        if (userId.length() < 4 || userId.length() > 9) {
            showToast(getString(R.string.user_id_length_hint));
            return false;
        }

        try {
            int userIdInt = Integer.parseInt(userId);
            if (userIdInt < 0) {
                showToast("User ID format error");
                return false;
            }
        } catch (NumberFormatException e) {
            showToast("User ID format error");
            return false;
        }

        if (userPassword.isEmpty()) {
            showToast(getString(R.string.user_password_empty));
            return false;
        }

        if (userPassword.length() < 4 || userPassword.length() > 20) {
            showToast(getString(R.string.user_password_length_hint));
            return false;
        }

        if (userName.isEmpty()) {
            showToast(getString(R.string.user_name_empty));
            return false;
        }

        if (userName.length() < 4 || userName.length() > 20) {
            showToast(getString(R.string.user_name_length_hint));
            return false;
        }

        return true;
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void signUpUser(int userId, String password, String name) {
        OkHttpClient client = new OkHttpClient();

        FormBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(userId))
                .add("password", password)
                .add("name", name)
                .build();

        Request request = new Request.Builder()
                .url("http://159.138.21.80/api/a3/sign_up")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() -> showToast("Sign up failed: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        String status = json.getString("status");
                        if ("OK".equals(status)) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Sign up successful, please log in");
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);
                            });
                        } else if ("ERROR".equals(status)) {
                            String message = json.getString("message");
                            getActivity().runOnUiThread(() -> showToast(message));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(() -> showToast("Invalid server response"));
                    }
                } else {
                    getActivity().runOnUiThread(() -> showToast("Sign up failed: Server error"));
                }
            }
        });
    }
}
