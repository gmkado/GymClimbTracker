package com.grant.gymclimbtracker.climber_client;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;


import com.parse.Parse;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;


public class ClimberMainActivity extends FragmentActivity implements ClimberListFragment.OnClimberListFragmentInteractionListener
{

    MyPageAdapter pageAdapter;
    private ClimberLocalDbSource mDbSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_climber);

        List<Fragment> fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(),fragments);

        ViewPager pager = (ViewPager)findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);

        // initialize Parse
        Parse.initialize(this, "V5kBuzDdaozQHFn16IoFqPZ60xYeG6MNx4RIiG58", "2z71inisqSUuVSZqqGFs5iCEXbFrSHctlWlfSQzM");

        // TODO: remove this
        // test parse
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();

        // create dbsource for local database
        mDbSource = new ClimberLocalDbSource(this);
        mDbSource.open();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbSource.close();
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();

        fList.add(ClimberListFragment.newInstance());

        return fList;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGymListFragmentInteraction(Uri uri) {

    }

    @Override
    public ClimberLocalDbSource getDbSource() {
        // check if project is in project list
        return mDbSource;
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;


        public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }


        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return this.fragments.get(position).toString();
        }
    }


}
