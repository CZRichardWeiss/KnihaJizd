package cz.marbes.knihajizd;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableRow;

public class SpravaAutaActivity extends FragmentActivity implements
        NovaJizdaFragment.OnFragmentInteractionListener,
        DetailJizdyFragment.OnFragmentInteractionListener {

    private ViewPager vp;
    private boolean probihaJizda;
    private TabLayout tabLayout;
    private int id_auta;
    private int od_cas;
    private int id_jizdy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprava_auta);

        vp = (ViewPager) findViewById(R.id.vpPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        setProbihaJizda(false);
        id_auta = getIntent().getIntExtra("id_auta", 0);
    }

    public void prepniNaVyberAuta(View v)
    {
        Intent i = new Intent(this, VyberAutaActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
    }

    public void setProbihaJizda(boolean b)
    {
        probihaJizda = b;
        //Změň taby
        MyPagerAdapter mpa = new MyPagerAdapter(getSupportFragmentManager(), b);
        vp.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), b));
        tabLayout.setupWithViewPager(vp);
        mpa.getItem(0);
        mpa.notifyDataSetChanged();
    }

    public void novaJizda(View v)
    {
        setProbihaJizda(true);
        od_cas = (int)(System.currentTimeMillis() / 1000L);
    }

    public void ukoncitJizdu(View v)
    {
        setProbihaJizda(false);
        //Vyber druhý tab (historie jízd)
        tabLayout.getTabAt(1).select();

        //Příprava databáze
        Helper h = new Helper(this);
        SQLiteDatabase rdb = h.getReadableDatabase();
        SQLiteDatabase wdb = h.getWritableDatabase();

        //Vložit vložit nový záznam s novou jízdou
        ContentValues cv = new ContentValues();
        cv.put("id_auta", id_auta);
        cv.put("od_misto", "");
        cv.put("do_misto", "");
        cv.put("od_cas", od_cas);
        cv.put("do_cas", (int)(System.currentTimeMillis() / 1000L));
        cv.put("tankovano", 0);
        cv.put("plna_nadrz", 0);
        cv.put("litru", 0);
        cv.put("soukroma", 0);
        try {
            id_jizdy = (int) wdb.insertOrThrow("jizdy", null, cv);
        }
        catch (SQLException e)
        {
            Log.d("Výjimka", e.getMessage());
        }

        Log.d("idecko", String.valueOf(id_jizdy));

        wdb.close();
        rdb.close();

        //Otevři příslušný detail jízdy
        DetailJizdyFragment df = new DetailJizdyFragment();
        Bundle args = new Bundle();
        args.putInt("id_jizdy", id_jizdy);
        df.setArguments(args);
        df.show(getSupportFragmentManager(), "detail_jizdy");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //Spravuje fragmenty
    public static class MyPagerAdapter extends FragmentStatePagerAdapter {
        private boolean probihaJizda;

        public MyPagerAdapter(FragmentManager fragmentManager, boolean _probihaJizda) {
            super(fragmentManager);
            this.probihaJizda = _probihaJizda;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return 3;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if(probihaJizda)
                        return ProbihajiciJizdaFragment.newInstance();
                    else
                        return NovaJizdaFragment.newInstance();
                case 1:
                    //TODO přístup do databáze
                    return HistorieJizdFragment.newInstance(0);
                case 2:
                    return PomocneFotkyFragment.newInstance();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    notifyDataSetChanged();
                    return probihaJizda?"Probíhající jízda":"Nová jízda";
                case 1:
                    return "Historie jízd";
                case 2:
                    return "Pomocné fotky";
                default:
                    return null;
            }
        }

        @Override
        public int getItemPosition(Object object)
        {
            if (object instanceof ProbihajiciJizdaFragment && !probihaJizda || object instanceof NovaJizdaFragment && probihaJizda)
                return POSITION_NONE;
            else
                return POSITION_UNCHANGED;
        }
    }
}
