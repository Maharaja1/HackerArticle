package apps.in.hackerarticle.di;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import apps.in.hackerarticle.data.remote.ApiService;
import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {

    public static final String BASE_URL = "https://hacker-news.firebaseio.com";

    private static int REQUEST_TIMEOUT = 60;
    private static OkHttpClient okHttpClient;

    private static void initOkHttp() {
        OkHttpClient.Builder httpClient =
                new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                        .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                        .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(interceptor);

        httpClient.addInterceptor(
                new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder =
                                original
                                        .newBuilder()
                                        .addHeader("Accept", "application/json")
                                        .addHeader("Content-Type", "application/json");

                        // Adding Authorization token (API Key)
                        // Requests will be denied without API key
            /*if (!TextUtils.isEmpty(AppUtils.getApiKey(context))) {
              requestBuilder.addHeader("Authorization", AppUtils.getApiKey(context));
            }*/

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                });

        okHttpClient = httpClient.build();
    }

    @Singleton
    @Provides
    public Executor getExecutor() {
        return Executors.newFixedThreadPool(2);
    }

    @Singleton
    @Provides
    public ApiService getApiClient() {
        if (okHttpClient == null) initOkHttp();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }
}
