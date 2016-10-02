package com.james.bagels.activities;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.james.bagels.Bagels;
import com.james.bagels.R;
import com.james.bagels.adapters.BagelAdapter;
import com.james.bagels.data.Bagel;
import com.james.bagels.services.BagelService;
import com.james.bagels.utils.BagelPageTransformer;
import com.james.bagels.utils.ImageUtils;
import com.james.bagels.utils.StaticUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnPageChange;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE = 4537;
    private static final int REQUEST_WALLPAPER = 7246;

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.leftFab)
    FloatingActionButton leftFab;
    @BindView(R.id.rightFab)
    FloatingActionButton rightFab;

    private BagelAdapter adapter;
    private boolean isShown = true;

    private MenuItem applyItem, setBagelItem, deleteItem;
    private SharedPreferences prefs;

    private Drawable left, add;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Bagels bagels = (Bagels) getApplicationContext();

        bagels.getBagels(new Bagels.BagelsListener() {
            @Override
            public void onBagels(List<Bagel> bagels) {
                adapter = new BagelAdapter(getSupportFragmentManager(), bagels);
                if (viewPager != null) {
                    viewPager.setAdapter(adapter);
                    onPageChange(viewPager.getCurrentItem());
                }
            }
        });

        viewPager.setPageTransformer(false, new BagelPageTransformer());

        left = ImageUtils.getVectorDrawable(this, R.drawable.ic_chevron_left);
        add = ImageUtils.getVectorDrawable(this, R.drawable.ic_add);

        rightFab.setImageDrawable(ImageUtils.getVectorDrawable(this, R.drawable.ic_chevron_right));
        leftFab.setImageDrawable(add);

        rightFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() < adapter.getCount()) viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });

        leftFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() < 1) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, REQUEST_IMAGE);
                } else viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
        });
    }

    @OnPageChange(R.id.viewPager)
    public void onPageChange(int position) {
        if (position == adapter.getCount() - 1) rightFab.hide();
        else rightFab.show();

        if ((position == 0 && !isShown) || (position > 0 && isShown)) {
            isShown = !isShown;
            leftFab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onHidden(FloatingActionButton fab) {
                    fab.setImageDrawable(isShown ? add : left);
                    fab.show();
                }
            });
        }

        Bagel bagel = adapter.bagels.get(viewPager.getCurrentItem());
        if (setBagelItem != null) setBagelItem.setVisible(!bagel.location.matches(prefs.getString(BagelService.WALLPAPER_KEY, "")));
        if (deleteItem != null) deleteItem.setVisible(!bagel.isTrueBagel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        applyItem = menu.findItem(R.id.action_apply);
        setBagelItem = menu.findItem(R.id.action_set);
        deleteItem = menu.findItem(R.id.action_delete);

        WallpaperInfo info = WallpaperManager.getInstance(this).getWallpaperInfo();
        applyItem.setVisible(info == null || !info.getPackageName().matches(getPackageName()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_set:
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString(BagelService.WALLPAPER_KEY, adapter.bagels.get(viewPager.getCurrentItem()).location).apply();
                setBagelItem.setVisible(false);

                if (StaticUtils.isServiceRunning(this, BagelService.class)) {
                    Intent intent = new Intent(BagelService.ACTION_UPDATE);
                    intent.setClass(this, BagelService.class);
                    startService(intent);
                }
                break;
            case R.id.action_delete:
                int size = prefs.getInt(Bagels.BAGELS_SIZE_KEY, 0);
                SharedPreferences.Editor editor = prefs.edit();

                for (int i = viewPager.getCurrentItem(); i < size; i++) {
                    editor.putString(Bagels.BAGELS_KEY + i, prefs.getString(Bagels.BAGELS_KEY + (i + 1), null));
                }

                editor.putInt(Bagels.BAGELS_SIZE_KEY, size - 1).apply();

                ((Bagels) getApplicationContext()).getBagels(new Bagels.BagelsListener() {
                    @Override
                    public void onBagels(List<Bagel> bagels) {
                        adapter = new BagelAdapter(getSupportFragmentManager(), bagels);
                        if (viewPager != null) {
                            viewPager.setAdapter(adapter);
                            onPageChange(viewPager.getCurrentItem());
                        }
                    }
                });
                break;
            case R.id.action_apply:
                startActivity(new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER));
                break;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE:
                    int size = prefs.getInt(Bagels.BAGELS_SIZE_KEY, 0);
                    SharedPreferences.Editor editor = prefs.edit();

                    for (int i = 1; i <= size; i++) {
                        editor.putString(Bagels.BAGELS_KEY + i, prefs.getString(Bagels.BAGELS_KEY + (i - 1), null));
                    }

                    editor.putString(Bagels.BAGELS_KEY + 0, data.getDataString()).putInt(Bagels.BAGELS_SIZE_KEY, size + 1).apply();

                    ((Bagels) getApplicationContext()).getBagels(new Bagels.BagelsListener() {
                        @Override
                        public void onBagels(List<Bagel> bagels) {
                            adapter = new BagelAdapter(getSupportFragmentManager(), bagels);
                            if (viewPager != null) {
                                viewPager.setAdapter(adapter);
                                onPageChange(viewPager.getCurrentItem());
                            }
                        }
                    });
                    break;
                case REQUEST_WALLPAPER:
                    WallpaperInfo info = WallpaperManager.getInstance(this).getWallpaperInfo();
                    applyItem.setVisible(info == null || !info.getPackageName().matches(getPackageName()));
                    break;
            }
        }
    }

}
