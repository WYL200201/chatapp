package com.example.group35;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class LoginActivityFragment extends Fragment {
    private EditText etUserId;
    private EditText etUserPassword;
    private static final ArrayList<String> paraNames = new ArrayList<>(Arrays.asList("user_id", "password"));

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // 加载登录界面的布局
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化控件
        etUserId = view.findViewById(R.id.et_user_id);
        etUserPassword = view.findViewById(R.id.et_user_password);

        // 登录按钮点击事件
        view.findViewById(R.id.bt_login).setOnClickListener(v -> handleLogin());

        // 注册按钮点击事件
        view.findViewById(R.id.bt_sign_up).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SignUpActivity.class);
            startActivity(intent);
        });
    }

    /**
     * 处理登录逻辑
     */
    private void handleLogin() {
        // 获取用户输入的ID和密码
        String userIdInput = etUserId.getText().toString().trim();
        String userPassword = etUserPassword.getText().toString().trim();

        // 清空输入框
        etUserId.setText("");
        etUserPassword.setText("");

        // 验证用户ID是否为空
        if (TextUtils.isEmpty(userIdInput)) {
            showToast(getString(R.string.user_id_empty));
            return;
        }

        // 验证用户ID格式是否正确
        int userId;
        try {
            userId = Integer.parseInt(userIdInput);
            if (userId < 0) {
                showToast("User ID must be a positive number");
                return;
            }
        } catch (NumberFormatException e) {
            showToast("Invalid User ID format");
            return;
        }

        // 验证密码是否为空
        if (TextUtils.isEmpty(userPassword)) {
            showToast(getString(R.string.user_password_empty));
            return;
        }

        // 执行登录任务
        executeLoginTask(userId, userPassword);
    }

    /**
     * 执行登录任务
     */
    private void executeLoginTask(int userId, String password) {
        new Thread(() -> {
            ArrayList<String> paraValues = new ArrayList<>();
            paraValues.add(String.valueOf(userId));
            paraValues.add(password);

            String jsonResult = Utils.postHTTPRequest("http://159.138.21.80:5000/api/a3/login", paraNames, paraValues);
            if (jsonResult.equals("")) {
                showToastOnUiThread("Login failed");
                return;
            }

            try {
                JSONObject json = new JSONObject(jsonResult);
                String status = json.getString("status");
                if ("OK".equals(status)) {
                    String userName = json.getString("name");
                    ChatroomActivityFragment.setLoginInformation(true, userName, userId);
                    showToastOnUiThread("You have successfully logged in as " + userName);
                    startActivity(new Intent(getContext(), ChatroomActivity.class));
                } else {
                    String message = json.getString("message");
                    showToastOnUiThread(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToastOnUiThread("An error occurred");
            }
        }).start();
    }

    /**
     * 在主线程显示Toast消息
     */
    private void showToastOnUiThread(String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show());
        }
    }

    /**
     * 显示Toast消息
     */
    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
