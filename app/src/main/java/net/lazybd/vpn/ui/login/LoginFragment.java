package net.lazybd.vpn.ui.login;

import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import net.lazybd.vpn.R;
import net.lazybd.vpn.ui.dashboard.ServerListFragment;
import net.lazybd.vpn.util.api.ApiClient;
import net.lazybd.vpn.util.api.ApiService;
import net.lazybd.vpn.util.api.AuthenticationManager;
import net.lazybd.vpn.util.api.models.SignInResponse;
import net.lazybd.vpn.util.api.models.User;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private LoginFramgentViewModel mViewModel;
    ProgressDialog progressDialog;
    EditText userNameEditText, passwordEditText;
    Button loginButton;
    View view;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_login, container, false);

        userNameEditText = (EditText) view.findViewById(R.id.user_name);
        passwordEditText = (EditText) view.findViewById(R.id.password);
        loginButton = (Button) view.findViewById(R.id.login_btn);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginFramgentViewModel.class);
        AuthenticationManager authenticationManager=new AuthenticationManager();
        authenticationManager.initialize(getActivity());
        authenticationManager.clearPreferences();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Logging In");
                progressDialog.show();
                Map<String, Object> credentials = new HashMap<>();
                credentials.put("email", userNameEditText.getText().toString());
                credentials.put("password",passwordEditText.getText().toString());
                ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
                apiService.signIn(credentials).enqueue(new Callback<SignInResponse>() {
                    @Override
                    public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                       if(response.isSuccessful()){
                           SignInResponse signInResponse=response.body();
                           progressDialog.dismiss();
                           authenticationManager.setAccessToken(signInResponse.getAccessToken());
                           authenticationManager.setUser(signInResponse.getUser());
                           getFragmentManager().beginTransaction().replace(R.id.container, ServerListFragment.newInstance()).commitNow();
                       }else {
                           progressDialog.setMessage("Login Failed");
                       }
                    }

                    @Override
                    public void onFailure(Call<SignInResponse> call, Throwable t) {
                        progressDialog.setMessage("Login Failed");

                    }
                });


//
//                boolean isLoginSuccess= authenticationManager.signIn(userNameEditText.getText().toString(),passwordEditText.getText().toString());
//                if(isLoginSuccess){
//                    progressDialog.dismiss();
//
//                }else {
//                    progressDialog.setMessage("Failed");
//                }

            }
        });

        // TODO: Use the ViewModel
    }



}