package net.lazybd.vpn.util.api;

import net.lazybd.vpn.util.api.models.SignInResponse;
import net.lazybd.vpn.util.api.models.SockServer;
import net.lazybd.vpn.util.api.models.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/sign-in")
    Call<SignInResponse> signIn(@Body Map<String,Object> body);

    @GET("get-user")
    Call<User> getUser();

    @POST("set-activity")
    Call<User> setActivity(@Body Map<String,Object> body);

    @GET("get-servers")
    Call<List<SockServer>> getServers();

}
