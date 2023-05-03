package net.lazybd.vpn.util.api;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import net.lazybd.vpn.util.api.models.SignInResponse;
import net.lazybd.vpn.util.api.models.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticationManager {

    User user;
    SignInResponse signInResponse;
    SharedPreferences authSharedPreferences, userSharedPreferences;


    String accessToken;
    Activity activity;


    public void initialize(Activity activity) {
        this.activity = activity;
        authSharedPreferences = activity.getSharedPreferences("Auth", Context.MODE_PRIVATE);
        userSharedPreferences = activity.getSharedPreferences("User", Context.MODE_PRIVATE);
    }


    public boolean signIn(String email, String password) {
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("email", email);
        credentials.put("password", password);
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Response<SignInResponse> response = null;
        apiService.signIn(credentials).enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                Log.d("result", Integer.toString(response.code()));
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {

            }
        });

        return false;
    }

    public User getUser() {
        return user;
    }

    public User getActiveUser() {
        user = new User();
        Log.d("99", userSharedPreferences.getString("userName", userSharedPreferences.getString("id", null)));
        user.setId(userSharedPreferences.getString("id", null));
        user.setUserName(userSharedPreferences.getString("userName", null));
        user.setEmail(userSharedPreferences.getString("email", null));
        user.setPackageName(userSharedPreferences.getString("packageName", null));
        String expireAt = userSharedPreferences.getString("expireAt", null);
        if (expireAt != null) {
            DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            try {
                Date date = dateFormat.parse(expireAt);
                user.setExpireAt(date);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        return user;
    }


    public void setUser(User user) {

        SharedPreferences.Editor userEditor = userSharedPreferences.edit();
        userEditor.clear();
        userEditor.putString("id", user.getId());
        userEditor.putString("name", user.getName());
        userEditor.putString("userName", user.getUserName());
        userEditor.putString("packageName", user.getPackageName());
        userEditor.putString("email", user.getEmail());
        userEditor.putString("expireAt", user.getExpireAt().toString());
        userEditor.commit();
        Log.d("99", userSharedPreferences.getString("userName", null));
        this.user = user;

    }

    public String getAccessToken() {
        accessToken = authSharedPreferences.getString("accessToken", null);
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        SharedPreferences.Editor authEditor = authSharedPreferences.edit();
        authEditor.putString("accessToken", accessToken);
        authEditor.commit();
        this.accessToken = accessToken;
    }

    public void clearPreferences() {
        SharedPreferences.Editor userEditor = userSharedPreferences.edit();
        userEditor.clear();
        SharedPreferences.Editor authEditor = authSharedPreferences.edit();
        authEditor.clear();
    }

    public boolean isLoggedIn() {
        if (getAccessToken() == null) return false;
        getActiveUser();
        if (user == null) return false;
        return true;
    }
}
