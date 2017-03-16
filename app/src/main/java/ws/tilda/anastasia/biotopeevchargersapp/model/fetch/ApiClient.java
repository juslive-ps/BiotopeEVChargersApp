package ws.tilda.anastasia.biotopeevchargersapp.model.fetch;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ws.tilda.anastasia.biotopeevchargersapp.model.GetChargersResponse;

public class ApiClient {
    public static final String APIPATH = "https://otaniemi3d.cs.hut.fi/EVChargers/";

    private static ChargerService chargerService = null;

    public static ChargerService getApi() {
        OkHttpClient.Builder okhttpClientBuilder = new OkHttpClient.Builder();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttpClientBuilder.addInterceptor(logging);

        if (chargerService == null) {
            // initialize ChargerService

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(APIPATH)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okhttpClientBuilder.build())
                    .build();

            chargerService = retrofit.create(ChargerService.class);
        }
        return chargerService;
    }

    public interface ChargerService {
        @POST(".")
        Call<GetChargersResponse> getChargers(@Body Query query);
    }
}
