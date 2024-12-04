package com.example.group35;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class FetchAccount {
    private static final String BASE_URL = "http://159.138.21.80:5000/api/a3/";
    private final int user_id;
    private final Context context;
    private final IdNamePage idNamePage;
    private final ArrayList<String> wallet;
    private final boolean needDisplay;

    public FetchAccount(Context context, int user_id, ArrayList<String> wallet) {
        this.context = context;
        this.user_id = user_id;
        this.wallet = wallet;
        this.needDisplay = true;
        this.idNamePage = null;
    }

    public FetchAccount(Context context, IdNamePage idNamePage) {
        this.context = context;
        this.idNamePage = idNamePage;
        this.user_id = idNamePage.user_id;
        this.wallet = new ArrayList<>();
        this.needDisplay = false;
    }

    public void fetchAccount() {
        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AccountApiService apiService = retrofit.create(AccountApiService.class);

        // Make the network request
        apiService.getAccount(user_id).enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AccountResponse walletResponse = response.body();

                    if ("OK".equals(walletResponse.status)) {
                        if (needDisplay) {
                            wallet.clear();
                        }
                    } else {
                        Toast.makeText(context, "Failed to fetch wallet", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Server error while fetching wallet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Define account API Service
    private interface AccountApiService {
        @GET("get_account")
        Call<AccountResponse> getAccount(@Query("user_id") int userId);
    }

    // Wallet Response Model
    public static class AccountResponse {
        public String status;
        public ArrayList<AccountData> data;

        public static class AccountData {
            public String wallet;
        }
    }
}
