package net.lazybd.vpn.ui.dashboard;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.VpnService;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.lazybd.vpn.IVpnService;
import net.lazybd.vpn.R;
import net.lazybd.vpn.SocksVpnService;
import net.lazybd.vpn.util.Utility;
import net.lazybd.vpn.util.api.ApiClient;
import net.lazybd.vpn.util.api.ApiService;
import net.lazybd.vpn.util.api.AuthenticationManager;
import net.lazybd.vpn.util.api.models.SockServer;
import net.lazybd.vpn.util.api.models.User;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerConnectionFragment extends Fragment {

    private ServerConnectionViewModel mViewModel;
    View view;
    private boolean mRunning = false;
    TextView userNameView,packageNameView,expireAtView,serverIpView,disconnectButton;

    User user;

    SockServer sockServer;
    private boolean mStarting = false, mStopping = false;
    ApiService apiService;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName p1, IBinder binder) {
            mBinder = IVpnService.Stub.asInterface(binder);

            try {
                mRunning = mBinder.isRunning();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mRunning) {
                updateState();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName p1) {
            mBinder = null;
        }
    };
    private final Runnable mStateRunnable = new Runnable() {
        @Override
        public void run() {
            updateState();
        }
    };
    private IVpnService mBinder;
    TextView textView;


    public static ServerConnectionFragment newInstance() {
        return new ServerConnectionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_server_connection, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ServerConnectionViewModel.class);
        userNameView=(TextView) view.findViewById(R.id.user_name);
        packageNameView=(TextView) view.findViewById(R.id.package_name);
        expireAtView=(TextView) view.findViewById(R.id.expire_at);
        serverIpView=(TextView) view.findViewById(R.id.server_ip);
        disconnectButton=(TextView) view.findViewById(R.id.disconnect);
        AuthenticationManager authenticationManager = new AuthenticationManager();
        authenticationManager.initialize(getActivity());
         apiService= ApiClient.getRetrofitInstance(authenticationManager.getAccessToken()).create(ApiService.class);
        user=authenticationManager.getActiveUser();
        if(user.getUserName()!=null) userNameView.setText(user.getUserName());
        if(user.getPackageName()!=null) packageNameView.setText(user.getPackageName());
        if(user.getExpireAt()!=null) expireAtView.setText(user.getExpireAt().toString());
        serverIpView.setText(sockServer.getHost());
        disconnectButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               getFragmentManager().popBackStack();
               stopVpn();
           }
       });

        startVpn();
        // TODO: Use the ViewModel
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Utility.startVpn(getActivity(), sockServer);
            checkState();
        }
    }
    private void startVpn() {
        mStarting = true;
        Intent i = VpnService.prepare(getActivity());
        if (i != null) {
            startActivityForResult(i, 0);
        } else {
            onActivityResult(0, Activity.RESULT_OK, null);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("connectedServerId",sockServer.getId() );

        apiService.setActivity(data).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    private void stopVpn() {

        if (mBinder == null)
            return;

        mStopping = true;

        try {
            mBinder.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mBinder = null;

        getActivity().unbindService(mConnection);
        checkState();
        Map<String, Object> data = new HashMap<>();
        data.put("connectedServerId",null );
        apiService.setActivity(data).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

    }

    private void checkState() {
        mRunning = false;


        if (mBinder == null) {
            getActivity().bindService(new Intent(getActivity(), SocksVpnService.class), mConnection, 0);
        }
    }

    private void updateState() {
        if (mBinder == null) {
            mRunning = false;
        } else {
            try {
                mRunning = mBinder.isRunning();
            } catch (Exception e) {
                mRunning = false;
            }
        }



        if ((!mStarting && !mStopping) || (mStarting && mRunning) || (mStopping && !mRunning)) {

        }

        if (mStarting && mRunning) {
            mStarting = false;
        }

        if (mStopping && !mRunning) {
            mStopping = false;
        }

    }
    public SockServer getSockServer() {
        return sockServer;
    }

    public void setSockServer(SockServer sockServer) {
        this.sockServer = sockServer;
    }

    private void gotoDashBoard(){
        getFragmentManager().beginTransaction().replace(R.id.container, ServerListFragment.newInstance()).commitNow();
    }


}