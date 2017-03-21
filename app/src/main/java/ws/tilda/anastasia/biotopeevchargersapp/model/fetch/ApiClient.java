package ws.tilda.anastasia.biotopeevchargersapp.model.fetch;

import android.support.annotation.NonNull;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ws.tilda.anastasia.biotopeevchargersapp.model.GetChargersResponse;

public class ApiClient {
    private static final String APIPATH = "https://otaniemi3d.cs.hut.fi/EVChargers/";

    private static OkHttpClient.Builder okhttpClientBuilder = new OkHttpClient.Builder();

    private static ChargerService chargerService = null;

    public static ChargerService getApi() {

        getLogger();

        if (chargerService == null) {
            Retrofit retrofit = getRetrofit();
            chargerService = retrofit.create(ChargerService.class);
        }
        return chargerService;
    }

    @NonNull
    private static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                        .baseUrl(APIPATH)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okhttpClientBuilder.build())
                        .build();
    }

    private static void getLogger() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttpClientBuilder.addInterceptor(logging);
    }

    public interface ChargerService {
        @POST(".")
        Call<GetChargersResponse> getChargers(@Body Query query);
    }
}
