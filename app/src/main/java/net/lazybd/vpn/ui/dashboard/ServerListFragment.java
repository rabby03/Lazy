package net.lazybd.vpn.ui.dashboard;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.lazybd.vpn.R;
import net.lazybd.vpn.ui.login.LoginFragment;
import net.lazybd.vpn.util.api.ApiClient;
import net.lazybd.vpn.util.api.ApiService;
import net.lazybd.vpn.util.api.AuthenticationManager;
import net.lazybd.vpn.util.api.models.SockServer;
import net.lazybd.vpn.util.api.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerListFragment extends Fragment {

    private ServerListViewModel mViewModel;
    View view;
    RecyclerView recyclerView;
    TextView userNameView,packageNameView,expireAtView;
    User user;
    public static ServerListFragment newInstance() {
        return new ServerListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_server_list, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ServerListViewModel.class);
        recyclerView = (RecyclerView) view.findViewById(R.id.server_list);
        userNameView=(TextView) view.findViewById(R.id.user_name);
        packageNameView=(TextView) view.findViewById(R.id.package_name);
        expireAtView=(TextView) view.findViewById(R.id.expire_at);




        // TODO: Use the ViewModel

        AuthenticationManager authenticationManager = new AuthenticationManager();
        authenticationManager.initialize(getActivity());
           user=authenticationManager.getActiveUser();
        if(user.getUserName()!=null) userNameView.setText(user.getUserName());
        if(user.getPackageName()!=null) packageNameView.setText(user.getPackageName());
        if(user.getExpireAt()!=null) expireAtView.setText(user.getExpireAt().toString());

        ApiService apiService = ApiClient.getRetrofitInstance(authenticationManager.getAccessToken()).create(ApiService.class);
        apiService.getServers().enqueue(new Callback<List<SockServer>>() {
            @Override
            public void onResponse(Call<List<SockServer>> call, Response<List<SockServer>> response) {
                if (response.isSuccessful()) {

                    List<SockServer> servers = response.body();
                    ServerListViewAdapter serverListViewAdapter = new ServerListViewAdapter(servers, new ServerListViewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(SockServer sockServer) {

                            ServerConnectionFragment fragment=ServerConnectionFragment.newInstance();
                            fragment.setSockServer(sockServer);
                            getFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack("server_list").commit();

                        }
                    });

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(serverListViewAdapter);


                } else {
                    getFragmentManager().beginTransaction().replace(R.id.container, LoginFragment.newInstance()).commit();
                }
            }

            @Override
            public void onFailure(Call<List<SockServer>> call, Throwable t) {
                Toast.makeText(getContext(),"Something went wrong",Toast.LENGTH_LONG).show();
            }
        });

    }


}