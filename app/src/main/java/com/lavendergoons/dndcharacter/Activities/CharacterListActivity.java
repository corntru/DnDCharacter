package com.lavendergoons.dndcharacter.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.google.firebase.crash.FirebaseCrash;
import com.lavendergoons.dndcharacter.Database.DBAdapter;
import com.lavendergoons.dndcharacter.Fragments.AboutFragment;
import com.lavendergoons.dndcharacter.Fragments.CharacterListFragment;
import com.lavendergoons.dndcharacter.Objects.SimpleCharacter;
import com.lavendergoons.dndcharacter.R;
import com.lavendergoons.dndcharacter.Utils.CharacterManager;
import com.lavendergoons.dndcharacter.Utils.Constants;


/**
  * Initial Activity to hold list of SimpleCharacters
  * Add and delete SimpleCharacters.
  * Selecting SimpleCharacter will launch CharacterNavDrawerActivity,
  * to show all SimpleCharacter info.
 */

public class CharacterListActivity extends AppCompatActivity implements
        CharacterListFragment.OnCharacterClickListener{

    public static final String TAG = "CHARACTER_LIST";
    private static final String FIRST_OPEN = "FIRST_OPEN";
    public static final String VERSION_CODE = "VERSION_CODE";

    Toolbar mToolbar;
    private DBAdapter dbAdapter;
    private CharacterManager characterManager;

    boolean isFirstOpen = true;
    private int storedVersion = -1;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_list);

        createView();

        sharedPreferences = this.getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        sharedEditor = sharedPreferences.edit();
        isFirstOpen = sharedPreferences.getBoolean(FIRST_OPEN, true);
        storedVersion = sharedPreferences.getInt(VERSION_CODE, 0);

        int versionCode = -1;
        String versionName = "";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
        } catch (Exception ex) {
            ex.printStackTrace();
            FirebaseCrash.log(ex.toString());
        }

        Log.d(TAG, "VersionCode: "+versionCode+" StoredVersion: "+storedVersion);
        if ((versionCode != -1 && versionCode != storedVersion) || isFirstOpen) {
            new ChangeLogDialog().showDialog(false);
        }

        sharedEditor.putInt(VERSION_CODE, versionCode);
        sharedEditor.putBoolean(FIRST_OPEN, false);
        sharedEditor.apply();
    }

    private void createView() {
        mToolbar = (Toolbar) findViewById(R.id.character_list_toolbar);
        mToolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(mToolbar);

        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.content_character_list, CharacterListFragment.newInstance(), CharacterListFragment.TAG).commit();
    }

    @Override
    protected void onStart() {
        dbAdapter = new DBAdapter(this);
        dbAdapter.open();

        //Load database in CharacterManager
        characterManager = CharacterManager.getInstance();
        characterManager.loadDatabase(dbAdapter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        dbAdapter.close();
        super.onStop();
    }

    @Override
    public void onFragmentCharacterClick(SimpleCharacter simpleCharacter, long id) {
        Intent intent = new Intent(this, CharacterNavDrawerActivity.class);
        intent.putExtra(Constants.CHARACTER_KEY, simpleCharacter);
        intent.putExtra(Constants.CHARACTER_ID, id);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (isCurrentFragment(AboutFragment.TAG)) {
            mToolbar.setTitle(getString(R.string.app_name));
        }
        super.onBackPressed();
    }

    private boolean isCurrentFragment(String tag) {
        Fragment frag = getSupportFragmentManager().findFragmentByTag(tag);
        return frag != null && frag.isVisible();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionAbout:
                mToolbar.setTitle(getString(R.string.title_fragment_about));
                FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.content_character_list, AboutFragment.newInstance(), AboutFragment.TAG).addToBackStack(AboutFragment.TAG).commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public class ChangeLogDialog {
        public void showDialog(boolean isAnnouncment) {
            Context context = CharacterListActivity.this;

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            LinearLayout dialogLayout = new LinearLayout(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            final WebView webView = new WebView(context);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setBackgroundColor(Color.TRANSPARENT);
            if (isAnnouncment) {
                webView.loadUrl("file:///android_asset/firstOpen.html");
            } else {
                webView.loadUrl("file:///android_asset/info.html");
            }


            dialogLayout.setLayoutParams(params);
            dialogLayout.addView(webView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            //TODO Change
            if (isAnnouncment) {
                builder.setTitle(getString(R.string.announcement_title));
            } else {
                builder.setTitle(getString(R.string.announcement_changelog));
            }
            builder.setView(dialogLayout)
                    .setPositiveButton(R.string.ok, null)
                    .create().show();
        }
    }
}
