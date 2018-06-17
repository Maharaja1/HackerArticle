package apps.in.hackerarticle.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import apps.in.hackerarticle.App;
import apps.in.hackerarticle.R;
import apps.in.hackerarticle.data.AppUtils;
import apps.in.hackerarticle.data.ArticleRepository;
import apps.in.hackerarticle.data.entity.Discussion;
import apps.in.hackerarticle.data.entity.Story;
import apps.in.hackerarticle.di.HAModule;
import apps.in.hackerarticle.view.adapter.DiscussionAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

public class ListFragment extends Fragment {

    private static final String TAG = ListFragment.class.getSimpleName();

    @BindView(R.id.rv_list)
    protected RecyclerView recyclerView;

    @BindView(R.id.progress_list)
    protected ProgressBar progressBar;

    @Inject
    ArticleRepository articleRepository;
    Realm realm;
    private Context context;
    private Unbinder unbinder;
    private Story story;
    private List<Long> longs;
    private int position = 0;
    private DiscussionAdapter adapter;
    private List<Discussion> discussions = new ArrayList<>();

    public static ListFragment newInstance(Story story) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(AppUtils.ACTION, AppUtils.ACTION_VIEW);
        args.putParcelable("data", story);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setHasOptionsMenu(true);
            App.getAppComponent(getContext()).getAppComponent(new HAModule(getContext())).inject(this);
            realm = Realm.getDefaultInstance();

            if (getArguments() != null) {
                getBundleData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getBundleData() {
        try {
            Bundle bundle = getArguments();
            if (bundle.get(AppUtils.ACTION) != null) {
                if (bundle.getInt(AppUtils.ACTION) == (AppUtils.ACTION_VIEW)) {
                    story = bundle.getParcelable("data");
                    longs = story.getKids();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // dispose subscriptions
        try {
            realm.close();
            unbinder.unbind();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comment_list, container, false);
        try {
            // Initializing view
            context = view.getContext();
            unbinder = ButterKnife.bind(this, view);

            adapter = new DiscussionAdapter(discussions, getContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);

            getRealmData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void getRealmData() {
        try {
            RealmResults<Discussion> realmResults =
                    realm.where(Discussion.class).equalTo("parent", story.getId()).findAllAsync();
            discussions.addAll(realmResults);
            adapter.notifyDataSetChanged();

            if (discussions.isEmpty()) {
                if (longs != null && !longs.isEmpty()) {
                    getComment(longs.get(position));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getComment(Long id) {
        try {
            showLoading("Loading comment's...");
            articleRepository
                    .getComment(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Observer<Discussion>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                }

                                @Override
                                public void onNext(Discussion s) {
                                    hideLoading("Success");
                                    if (s != null) {
                                        discussions.add(s);
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
                                        position += 1;
                                        if (longs != null && !longs.isEmpty()) {
                                            if (longs.size() > position) {
                                                getComment(longs.get(position));
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

    private void insertorUpdate(Discussion discussion) {
        try {
            realm.executeTransaction(
                    new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.insertOrUpdate(discussion);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_logout).setVisible(false);
        menu.findItem(R.id.action_refresh).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            // Handle item selection
            switch (item.getItemId()) {
                case R.id.action_refresh:
                    position = 0;
                    discussions.clear();
                    if (adapter != null) adapter.notifyDataSetChanged();
                    if (longs != null && !longs.isEmpty()) {
                        getComment(longs.get(position));
                    }
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
