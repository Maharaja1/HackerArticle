/*
 * *
 *  * Copyright (C) 2017 Ryan Kay Open Source Project
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package apps.in.hackerarticle.di;

import android.content.Context;

import javax.inject.Named;

import apps.in.hackerarticle.data.ArticleRepository;
import apps.in.hackerarticle.data.remote.ApiService;
import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class HAModule {

    private final Context context;

    public HAModule(Context context) {
        this.context = context;
    }

    @HAScope
    @Provides
    public ArticleRepository getUserRepo(ApiService service) {
        return new ArticleRepository(service);
    }

    /*@HAScope
      @Provides
      public ProductDao getProductDao(AppDatabase appDatabase) {
        return appDatabase.productDao();
      }
    */
    @HAScope
    @Provides
    @Named("activity")
    public CompositeDisposable getCompositeDisposable() {
        return new CompositeDisposable();
    }

    @HAScope
    @Provides
    @Named("vm")
    public CompositeDisposable getVMCompositeDisposable() {
        return new CompositeDisposable();
    }
}
