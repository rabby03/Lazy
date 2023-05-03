package net.lazybd.vpn.ui.splash;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.lazybd.vpn.R;
import net.lazybd.vpn.ui.dashboard.ServerConnectionFragment;
import net.lazybd.vpn.ui.dashboard.ServerListFragment;
import net.lazybd.vpn.ui.dashboard.ServerListViewAdapter;
import net.lazybd.vpn.ui.login.LoginFragment;
import net.lazybd.vpn.ui.main.MainFragment;
import net.lazybd.vpn.util.api.ApiClient;
import net.lazybd.vpn.util.api.ApiService;
import net.lazybd.vpn.util.api.AuthenticationManager;
import net.lazybd.vpn.util.api.models.SockServer;
import net.lazybd.vpn.util.api.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashFragment extends Fragment {

    private SplashViewModel mViewModel;
    private View view;

    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_splash, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
        AuthenticationManager authenticationManager=new AuthenticationManager();
        authenticationManager.initialize(getActivity());
        new android.os.Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                if(authenticationManager.getAccessToken()==null){

                   gotoLogin();

                }else {
                    ApiService apiService = ApiClient.getRetrofitInstance(authenticationManager.getAccessToken()).create(ApiService.class);
                    apiService.getUser().enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful()) {
                                User user=response.body();
                                authenticationManager.setUser(user);
                                gotoDashBoard();
                            } else {
                                gotoLogin();
                            }

                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });

                }
            }
        },1000);


    }

    private void gotoDashBoard(){
        getFragmentManager().beginTransaction().replace(R.id.container, ServerListFragment.newInstance()).commit();
    }
    private void gotoLogin(){
        getFragmentManager().beginTransaction().replace(R.id.container, LoginFragment.newInstance()).commitNow();
    }

}