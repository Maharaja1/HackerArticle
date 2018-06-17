package apps.in.hackerarticle.view.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import apps.in.hackerarticle.R;
import apps.in.hackerarticle.data.AppUtils;
import apps.in.hackerarticle.data.entity.Story;
import apps.in.hackerarticle.view.adapter.ViewPagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.MODE_PRIVATE;

public class TabFragment extends Fragment {

    private static final String TAG = TabFragment.class.getSimpleName();
    // This is our tablayout
    @BindView(R.id.tablayout)
    TabLayout tabLayout;
    // This is our viewPager
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private String[] tabTitle = {"Comment's", "Article"};
    private FragmentManager mManager;
    private Unbinder unbinder;
    private SharedPreferences mPreferences;
    private TextView tv_titleC, tv_titleA, tv_countC, tv_countA;
    private Story story;

    public static TabFragment newInstance(Story story) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putInt(AppUtils.ACTION, AppUtils.ACTION_VIEW);
        args.putParcelable("data", story);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mManager = getActivity().getSupportFragmentManager();

        mPreferences = getActivity().getSharedPreferences(AppUtils.SHARED_PREFS, MODE_PRIVATE);

        if (getArguments() != null) {
            getBundleData();
        }
    }

    private void getBundleData() {
        try {

            Bundle bundle = getArguments();
            if (bundle.get(AppUtils.ACTION) != null) {
                if (bundle.getInt(AppUtils.ACTION) == (AppUtils.ACTION_VIEW)) {
                    story = bundle.getParcelable("data");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView ");
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        try {
            // Initializing view
            unbinder = ButterKnife.bind(this, view);

            if (story != null) initializationView();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void initializationView() {
        try {
            setupViewPager(viewPager);

            // Initializing the tablayout
            tabLayout.setupWithViewPager(viewPager);
            setupTabIcons();

            viewPager.addOnPageChangeListener(
                    new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(
                                int position, float positionOffset, int positionOffsetPixels) {
                        }

                        @Override
                        public void onPageSelected(int position) {
                            viewPager.setCurrentItem(position);
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(tabTitle.length);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        for (int i = 0; i < tabTitle.length; i++) {
            String title = tabTitle[i];
            if (title.equals("Comment's"))
                adapter.addFragment(ListFragment.newInstance(story), title);
            else if (story.getUrl() != null && !story.getUrl().isEmpty())
                adapter.addFragment(WebFragment.newInstance(story), title);
        }
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView ");
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setupTabIcons() {
        for (int i = 0; i < tabTitle.length; i++) {
            String title = tabTitle[i];
            View view = getLayoutInflater().inflate(R.layout.custom_tab, null);
            if (title.equals("Comment's")) {
                tv_titleC = (TextView) view.findViewById(R.id.tv_title);
                tv_countC = (TextView) view.findViewById(R.id.tv_count);
                tv_titleC.setText(title);
                if (story.getDescendants() > 0) {
                    tv_countC.setVisibility(View.VISIBLE);
                    tv_countC.setText("" + story.getDescendants());
                } else tv_countC.setVisibility(View.GONE);

            } else {
                tv_titleA = (TextView) view.findViewById(R.id.tv_title);
                tv_countA = (TextView) view.findViewById(R.id.tv_count);
                tv_titleA.setText(title);
                tv_countA.setVisibility(View.GONE);
            }
            tabLayout.getTabAt(i).setCustomView(view);
        }
    }
}
