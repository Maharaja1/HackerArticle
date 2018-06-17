package apps.in.hackerarticle.di;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Annotated as a Singelton since we don't want to have multiple instances of a Single Database,
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    HAComponent getAppComponent(HAModule HAModule);
}
