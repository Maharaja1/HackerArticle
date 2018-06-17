package apps.in.hackerarticle;

import android.app.Application;
import android.content.Context;

import apps.in.hackerarticle.di.AppComponent;
import apps.in.hackerarticle.di.DaggerAppComponent;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {
    private AppComponent component;

    public static AppComponent getAppComponent(Context context) {
        return ((App) context.getApplicationContext()).component;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config =
                new RealmConfiguration.Builder().name("hackerarticle.realm").build();
        Realm.setDefaultConfiguration(config);
        component = DaggerAppComponent.builder().build();
    }
}
