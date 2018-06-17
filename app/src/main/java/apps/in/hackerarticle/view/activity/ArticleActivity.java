package apps.in.hackerarticle.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import apps.in.hackerarticle.App;
import apps.in.hackerarticle.R;
import apps.in.hackerarticle.data.AppUtils;
import apps.in.hackerarticle.data.ArticleRepository;
import apps.in.hackerarticle.data.entity.Story;
import apps.in.hackerarticle.di.HAModule;
import apps.in.hackerarticle.view.adapter.ArticleAdapter;
import apps.in.hackerarticle.view.listener.Itemlistener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

public class ArticleActivity extends AppCompatActivity implements Itemlistener {
    public Unbinder unbinder;
    // [END declare_auth]
    @BindView(R.id.rv_list)
    RecyclerView recyclerView;

    @BindView(R.id.article_progress)
    ProgressBar progressBar;

    @Inject
    ArticleRepository articleRepository;
    Realm realm;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    private Context context;
    private ArticleAdapter adapter;
    private List<Story> stories = new ArrayList<>();
    private int position = 0;
    private List<Long> longs = new ArrayList<>();
    private android.support.v7.app.ActionBar actionBar;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        try {
            unbinder = ButterKnife.bind(this);
            context = this;
            mPreferences = this.getSharedPreferences(AppUtils.SHARED_PREFS, MODE_PRIVATE);
            realm = Realm.getDefaultInstance();

            actionBar = getSupportActionBar();

            App.getAppComponent(getApplicationContext())
                    .getAppComponent(new HAModule(getApplicationContext()))
                    .inject(this);

            adapter = new ArticleAdapter(stories, this, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

            String sync = mPreferences.getString(AppUtils.SHARED_SYNC, "");
            if (sync.isEmpty()) {
                getStories();
            } else {
                setSubTitle();
                getRealmData();
            }
            // [START initialize_auth]
            mAuth = FirebaseAuth.getInstance();
            // [END initialize_auth]
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getRealmData() {
        try {
            RealmResults<Story> realmResults = realm.where(Story.class).findAllAsync();
            stories.addAll(realmResults);
            adapter.notifyDataSetChanged();

            if (stories.isEmpty()) {
                getStories();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getStories() {
        try {
            showLoading("Loading stories...");
            articleRepository
                    .getStories("topstories")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Observer<List<Long>>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                }

                                @Override
                                public void onNext(List<Long> s) {
                                    hideLoading("Success");
                                    if (!s.isEmpty()) longs = s;
                                }

                                @Override
                                public void onError(Throwable e) {
                                    hideLoading("Loading Error");
                                }

                                @Override
                                public void onComplete() {
                                    hideLoading("Complete");
                                    if (longs != null && !longs.isEmpty()) {
                                        getStory(longs.get(position) + "");
                                    }
                                }
                            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getStory(String id) {
        try {
            showLoading("Loading stories...");
            articleRepository
                    .getStory(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Observer<Story>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                }

                                @Override
                                public void onNext(Story s) {
                                    hideLoading("Success");
                                    if (s != null) {
                                        stories.add(s);
                                        adapter.notifyDataSetChanged();
                                        insertorUpdate(s);
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    hideLoading("Loading Error");
                                }

                                @Override
                                public void onComplete() {
                                    try {
                                        mPreferences
                                                .edit()
                                                .putString(AppUtils.SHARED_SYNC, AppUtils.getUniqueId())
                                                .commit();
                                        setSubTitle();
                                        position += 1;
                                        if (longs != null && !longs.isEmpty()) {
                                            if (longs.size() > position) {
                                                getStory(longs.get(position) + "");
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertorUpdate(Story story) {
        try {
            realm.executeTransaction(
                    new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.insertOrUpdate(story);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSubTitle() {
        try {
            String sync = mPreferences.getString(AppUtils.SHARED_SYNC, "");
            if (sync.isEmpty()) return;
            String time = AppUtils.timeAgo(sync, context);
            actionBar.setSubtitle(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        try {
            int id = item.getItemId();
            //noinspection SimplifiableIfStatement
            if (id == R.id.action_refresh) {
                longs.clear();
                position = 0;
                stories.clear();
                if (adapter != null) adapter.notifyDataSetChanged();
                getStories();
                return true;
            } else if (id == R.id.action_logout) {
                // Firebase sign out
                mAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onOptionsItemSelected(item);
    }

    public void showLoading(String message) {
        try {
            progressBar.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideLoading(String message) {
        try {
            progressBar.setVisibility(View.GONE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            realm.close();
            unbinder.unbind();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ItemClick(Story story) {
        try {
            Intent intentView = new Intent(this, ScrollingActivity.class);
            intentView.putExtra(AppUtils.ACTION, AppUtils.ACTION_VIEW);
            intentView.putExtra("data", story);
            startActivity(intentView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
