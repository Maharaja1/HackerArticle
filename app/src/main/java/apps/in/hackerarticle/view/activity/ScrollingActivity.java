package apps.in.hackerarticle.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import apps.in.hackerarticle.R;
import apps.in.hackerarticle.data.AppUtils;
import apps.in.hackerarticle.data.entity.Story;
import apps.in.hackerarticle.view.fragment.TabFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ScrollingActivity extends AppCompatActivity {
    public Unbinder unbinder;

    @BindView(R.id.tv_time_by)
    TextView tv_time_by;

    @BindView(R.id.tv_site)
    TextView tv_site;

    @BindView(R.id.tv_title)
    TextView tv_title;

    private FragmentManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_scrolling);
            unbinder = ButterKnife.bind(this);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            mManager = getSupportFragmentManager();
            getBundleData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getBundleData() {
        try {

            Bundle bundle = getIntent().getExtras();
            if (bundle.get(AppUtils.ACTION) != null) {
                if (bundle.getInt(AppUtils.ACTION) == (AppUtils.ACTION_VIEW)) {
                    Story story = bundle.getParcelable("data");

                    String time = AppUtils.timeAgo(story.getTime() + "", this);
                    tv_time_by.setText(time + " - " + story.getBy());
                    tv_site.setText(story.getUrl());
                    tv_title.setText(story.getTitle());

                    mManager
                            .beginTransaction()
                            .add(R.id.fragment_container, TabFragment.newInstance(story))
                            .commit();
                }
            }
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unbinder.unbind();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
