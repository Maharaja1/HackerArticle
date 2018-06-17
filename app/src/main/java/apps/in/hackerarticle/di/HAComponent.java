package apps.in.hackerarticle.di;

import apps.in.hackerarticle.view.activity.ArticleActivity;
import apps.in.hackerarticle.view.fragment.ListFragment;
import dagger.Subcomponent;

@HAScope
@Subcomponent(modules = HAModule.class)
public interface HAComponent {
    void inject(ArticleActivity articleActivity);

    void inject(ListFragment listFragment);
}
