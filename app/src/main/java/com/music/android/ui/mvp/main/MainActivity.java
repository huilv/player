package com.music.android.ui.mvp.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.gms.charger.ChargerSdk;
import com.google.android.gms.charger.provider.GlobalProvider;
import com.google.android.gms.cover.CoverSdk;
import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.base.BaseActivity;
import com.music.android.bean.CommonBean;
import com.music.android.bean.MusicInfoBean;
import com.music.android.bean.PlaylistNameBean;
import com.music.android.config.AppConfigManager;
import com.music.android.managers.AnalyticsManager;
import com.music.android.managers.Constant;
import com.music.android.managers.LocalMusicImpl;
import com.music.android.managers.permission.PermissionCallback;
import com.music.android.managers.permission.PermissionManager;
import com.music.android.ui.dialog.EvaluateDialog;
import com.music.android.ui.mvp.main.mysong.MySongFragment;
import com.music.android.ui.mvp.main.mysong.MySongsListFragment;
import com.music.android.ui.mvp.main.search.SearchFragment;
import com.music.android.ui.mvp.main.stream.HotFragment;
import com.music.android.ui.mvp.main.stream.RankFragment;
import com.music.android.ui.mvp.main.stream.StyleFragment;
import com.music.android.ui.widgets.PermissionPopupWindow;
import com.music.android.ui.widgets.playinglayout.CalculatViewPosition;
import com.music.android.utils.AnalyticsConstants;
import com.music.android.utils.AnalyticsUtils;
import com.music.android.utils.Constants;
import com.music.android.utils.L;
import com.music.android.utils.LocalMusicUtils;
import com.music.android.utils.PermissionUtils;
import com.music.android.utils.PlayHelper;
import com.music.android.utils.PlayUtils;
import com.music.android.utils.PrefUtils;
import com.music.android.utils.ScreenUtils;
import com.music.android.utils.SharedPreferencesHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements OnAddFragmentListener, View.OnClickListener, SlidingUpPanelLayout.PanelSlideListener, DrawerLayout.DrawerListener {

    private DrawerLayout mDrawerLayout;

    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    private ControlsFragment controlsFragment;

    private MusicInfoBean localMusicInfoBean;

    boolean isCalculate;

    private static final long DAY_TIME = 24 * 60 * 60 * 1000;

    private static final String TAG = "MainActivity";

    private boolean isGoToMySongs = false;

    private static final String APP_START_TIME = "action_app_start_times";
    private static final String ISSHOWLOVEAPP = "is_show_love_app";
    private boolean isNeedShowMarkGuide = false;
    private static final int GUIDE_SHOW_TIMES = 3;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_main);
        initAppConfig();
        initFragment();
        getMusic();
        showPermissionDialog();
        showOrHideLocker(false);
        isCalculate = PrefUtils.getBoolean(MusicApp.context, CalculatViewPosition.IS_CALULATE, false);
        checkPermissionAndThenLoad();
//        CheckPlayListService.startUploadImg(this);
    }



    private void checkNeedGoToGp() {

        boolean isShowed = PrefUtils.getBoolean(this.getApplicationContext(), ISSHOWLOVEAPP, false);

        if (!isShowed) {
            int times = PrefUtils.getInt(this.getApplicationContext(), APP_START_TIME, 0);

            times++;
            if (times > GUIDE_SHOW_TIMES) {
                isNeedShowMarkGuide = true;
            }
            PrefUtils.putInt(getApplicationContext(), APP_START_TIME, times);
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();

        String come = intent.getStringExtra(Constants.BroadcastConstants.NOTIFICATION_COME_KEY);

        if (!TextUtils.isEmpty(come)) {
            if (come.equals(Constants.BroadcastConstants.NOTIFICATION_COME_VALUE)) {
                if (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    controlsFragment.setExpanded(true);
                    closeDrawer();
                }
            }
        }

    }

    private void initAppConfig() {
        AppConfigManager.getConfig();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void getMusic() {
        if (!PlayHelper.getInstance().isServiceExist()) {
            mSlidingUpPanelLayout.setTouchEnabled(false);
            localMusicInfoBean = SharedPreferencesHelper.getMusicInfoBean();
            if (localMusicInfoBean != null && localMusicInfoBean.path != null) {
                buildData();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void buildData() {
        if (PermissionUtils.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            localMusic.getLocalSongs();
        }
    }

    LocalMusicImpl<MusicInfoBean> localMusic = new LocalMusicImpl<MusicInfoBean>() {
        @Override
        protected void buildMapData(Cursor cursor, Map<String, MusicInfoBean> commonBeanMap) {

        }

        @Override
        protected MusicInfoBean buildData(Cursor cursor) {
            return LocalMusicUtils.getAllSong(cursor);
        }

        @Override
        protected void getResult(List<MusicInfoBean> list) {
            L.d(TAG, "list.size()=" + list.size() + "----" + SharedPreferencesHelper.getLocalMusicSize());
            SharedPreferencesHelper.setLocalMusicSize(list.size());
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) != null && list.get(i).path != null) {
                    if (list.get(i).path.equals(localMusicInfoBean.path)) {
                        PlayUtils.startPlay(list, i);
                        return;
                    }
                }
            }
            list.add(0, localMusicInfoBean);
            PlayUtils.startPlay(list, 0);
        }
    };


    private void initFragment() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerLayout.addDrawerListener(this);
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.menu_shadow, Gravity.START);
        MainFragment fragment = new MainFragment();
        fragment.setOnAddFragmentListener(this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_frame, fragment, "MainFragment").commitAllowingStateLoss();

        controlsFragment = new ControlsFragment();
        controlsFragment.setSlidingUpPanelLayoutWeakReference(mSlidingUpPanelLayout);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.controller_view, controlsFragment, "controlsFragment").commitAllowingStateLoss();

        mSlidingUpPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            findViewById(R.id.content_frame).setPadding(0, ScreenUtils.getStatusBarHeight(MusicApp.context), 0, 0);
        }
        mSlidingUpPanelLayout.addPanelSlideListener(controlsFragment);
        mSlidingUpPanelLayout.addPanelSlideListener(this);
    }

    private boolean listenerSeted = false;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!isCalculate) {
            if (!listenerSeted && mSlidingUpPanelLayout.findViewById(R.id.topContainer) != null) {
                listenerSeted = true;
                new CalculatViewPosition((ViewGroup) findViewById(R.id.parent));
            }
        }
    }


    @Override
    public void onAddFragment(String flag, Object object) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.push_left_in,
                R.anim.push_right_out,
                R.anim.push_left_in,
                R.anim.push_right_out);
        HotFragment hotFragment;
        MySongsListFragment mySongsListFragment;
        MusicInfoBean musicInfoBean;


        switch (flag) {
            case "searchFragment":
                SearchFragment searchFragment = new SearchFragment();
                addFragment(fragmentTransaction, searchFragment, "searchFragment");
                break;
            case "mySongFragment":
                isGoToMySongs = true;
                checkPermissionAndThenLoad();
                break;
            case "favoritesFragment":
                mySongsListFragment = new MySongsListFragment();
                mySongsListFragment.setValue("Favorites", "Favorites");
                mySongsListFragment.setOnAddFragmentListener(this);
                addFragment(fragmentTransaction, mySongsListFragment, "mySongsListFragment");
                break;
            case "recentlyPlayFragment":
                mySongsListFragment = new MySongsListFragment();
                mySongsListFragment.setValue("RecentlyPlay", "Recently Play");
                mySongsListFragment.setOnAddFragmentListener(this);
                addFragment(fragmentTransaction, mySongsListFragment, "mySongsListFragment");
                break;
            case "mySongsListFragment":
                CommonBean commonBean = (CommonBean) object;
                final String keyWord;
                final String mFlag;
                if (commonBean != null) {
                    if (commonBean.singer != null) {
                        keyWord = commonBean.singer;
                        mFlag = "singer";
                    } else {
                        keyWord = commonBean.folder;
                        mFlag = "folder";
                    }
                } else {
                    keyWord = null;
                    mFlag = null;
                }
                mySongsListFragment = new MySongsListFragment();
                mySongsListFragment.setValue(mFlag, keyWord);
                mySongsListFragment.setOnAddFragmentListener(this);
                addFragment(fragmentTransaction, mySongsListFragment, "mySongsListFragment");
                break;
            case "hotFragment":
                int position = (int) object;
                StyleFragment styleFragment;
                Bundle bundle;
                switch (position) {
                    case 0:
                        hotFragment = HotFragment.getInstants(0, "New & Hot", "");
                        hotFragment.setOnAddFragmentListener(this);
                        addFragment(fragmentTransaction, hotFragment, "hotFragment");
                        break;
                    case 1:
                        RankFragment rankFragment = new RankFragment();
                        rankFragment.setOnAddFragmentListener(this);
                        addFragment(fragmentTransaction, rankFragment, "rankFragment");
                        break;
                    case 2:

                        bundle = new Bundle();
                        bundle.putInt(Constant.PropertyConstant.BUNDLE_STYLE, Constant.PropertyConstant.TAG_STYLE);

                        styleFragment = new StyleFragment();
                        styleFragment.setArguments(bundle);
                        styleFragment.setOnAddFragmentListener(this);
                        addFragment(fragmentTransaction, styleFragment, "styleFragment");
                        break;
                    case 3:

                        bundle = new Bundle();
                        bundle.putInt(Constant.PropertyConstant.BUNDLE_STYLE, Constant.PropertyConstant.TAG_AUDIO);

                        styleFragment = new StyleFragment();
                        styleFragment.setArguments(bundle);
                        styleFragment.setOnAddFragmentListener(this);
                        addFragment(fragmentTransaction, styleFragment, "styleFragment");
                        break;
                }
                break;
            case "rankFragment":
                musicInfoBean = (MusicInfoBean) object;
                L.d("MainActivity", " rankFragment musicInfoBean.skey : " + musicInfoBean.skey);
                AnalyticsManager.getInstance().stream(1, musicInfoBean.skey);
                hotFragment = HotFragment.getInstants(1, musicInfoBean.title, String.valueOf(musicInfoBean.id));
                hotFragment.setOnAddFragmentListener(this);
                addFragment(fragmentTransaction, hotFragment, "hotFragment");
                break;
            case "styleFragment":
                musicInfoBean = (MusicInfoBean) object;
                L.d("MainActivity", " styleFragment musicInfoBean.skey : " + musicInfoBean.skey);
                AnalyticsManager.getInstance().stream(2, musicInfoBean.skey);
                hotFragment = HotFragment.getInstants(2, musicInfoBean.title, String.valueOf(musicInfoBean.id));
                hotFragment.setOnAddFragmentListener(this);
                addFragment(fragmentTransaction, hotFragment, "hotFragment");
                break;
            case "audioFragment":
                musicInfoBean = (MusicInfoBean) object;
                L.d("MainActivity", " audioFragment musicInfoBean.skey : " + musicInfoBean.skey);
                AnalyticsManager.getInstance().stream(3, musicInfoBean.skey);
                hotFragment = HotFragment.getInstants(3, musicInfoBean.title, String.valueOf(musicInfoBean.id));
                hotFragment.setOnAddFragmentListener(this);
                addFragment(fragmentTransaction, hotFragment, "hotFragment");
                break;
            case "playListFragment":
                PlaylistNameBean playlistNameBean = (PlaylistNameBean) object;
                mySongsListFragment = new MySongsListFragment();
                mySongsListFragment.setValue("playList", playlistNameBean.name);
                mySongsListFragment.setPlaylistNameBean(playlistNameBean);
                mySongsListFragment.setOnAddFragmentListener(this);
                addFragment(fragmentTransaction, mySongsListFragment, "mySongsListFragment");
                break;

        }
        setDrawerSate(false);
    }

    private void addFragment(FragmentTransaction fragmentTransaction, Fragment fragment, String tag) {
        fragmentTransaction.
                add(R.id.content_frame, fragment, tag).
                addToBackStack(null).
                commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        try {
            if (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                return;
            }
        } catch (IllegalStateException e) {
            return;
        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                setDrawerSate(true);
            }
            getSupportFragmentManager().popBackStack();
        } else {
            if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
                closeDrawer();
            } else {
                checkNeedGoToGp();
                if (isNeedShowMarkGuide) {
                    EvaluateDialog evaluateDialog = new EvaluateDialog();
                    evaluateDialog.show(getSupportFragmentManager(), EvaluateDialog.class.getSimpleName());
                    PrefUtils.putBoolean(getApplicationContext(), ISSHOWLOVEAPP, true);
                    isNeedShowMarkGuide = false;
                } else {
//                    moveTaskToBack(true);
                    super.onBackPressed();
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showPermissionDialog() {

        long currentTime = System.currentTimeMillis();

        if (CoverSdk.checkExistUsageStatsSettingActivity(this)) {
            if (currentTime - SharedPreferencesHelper.getPermissionShowTime() > DAY_TIME && !CoverSdk.checkHasUsageStatsPermission(MusicApp.context)) {
                mDrawerLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!MainActivity.this.isFinishing()) {
                                PermissionPopupWindow permissionPopupWindow = new PermissionPopupWindow(MainActivity.this);
                                permissionPopupWindow.showAtLocation(mDrawerLayout, Gravity.CENTER, 0, 0);
                            }
                        } catch (Exception e) {

                        }
                    }
                });
                SharedPreferencesHelper.setPermissionShowTime(currentTime);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionUtils.REQUEST_CODE) {
            try {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void setDrawerSate(boolean sate) {
        if (sate) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
            return;
        }
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(Gravity.START);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(Gravity.START);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.controller_view:
                break;
        }
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else if (newState == SlidingUpPanelLayout.PanelState.DRAGGING) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        L.d(TAG, "slideOffset=" + slideOffset);
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        L.d(TAG, "onDrawerOpened");
        AnalyticsUtils.vMusicClick(AnalyticsConstants.MY_LIBRARY, AnalyticsConstants.Action.CLICK_MENU, "");

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    /**
     * 返回true是出locker   返回false 是不出locker
     *
     * @param isShow
     */

    private void showOrHideLocker(final boolean isShow) {
        ChargerSdk.setLockerShowListener(new GlobalProvider.LockerShowListener() {
            @Override
            public boolean checkLockerShow() {
                return isShow;
            }
        });
    }

    private void gotoMySongFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.push_left_in,
                R.anim.push_right_out,
                R.anim.push_left_in,
                R.anim.push_right_out);
        MySongFragment mySongFragment = new MySongFragment();
        mySongFragment.setOnAddFragmentListener(this);
        addFragment(fragmentTransaction, mySongFragment, "mySongFragment");
    }

    private void checkPermissionAndThenLoad() {

        //check for permission
        if (PermissionManager.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (isGoToMySongs) {
                gotoMySongFragment();
            }
        } else {
            if (PermissionManager.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(findViewById(R.id.drawerLayout), "V Music will need to read external storage to display songs on your device.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    PermissionManager.askForPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, permissionReadStorageCallback);
                                }
                            }
                        }).show();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    PermissionManager.askForPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, permissionReadStorageCallback);
                }
            }
        }
    }

    private final PermissionCallback permissionReadStorageCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            if (isGoToMySongs) {
                gotoMySongFragment();
            }
        }

        @Override
        public void permissionRefused() {
        }
    };

}
