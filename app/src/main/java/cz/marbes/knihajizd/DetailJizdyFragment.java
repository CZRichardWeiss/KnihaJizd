package cz.marbes.knihajizd;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TableRow;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailJizdyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailJizdyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailJizdyFragment extends DialogFragment
{
    private OnFragmentInteractionListener mListener;

    Helper h;

    EditText et_od_misto;
    EditText et_do_misto;
    Button tl_od_cas;
    Button tl_do_cas;
    CheckBox cb_soukroma_jizda;
    CheckBox cb_tankovano;

    CheckBox cb_plna_nadrz;
    EditText et_litru;

    TableRow radek_plna_nadrz;
    TableRow radek_litru;

    Button tl_ulozit;
    Button tl_zrusit;
    Button tl_smazat;

    int od_cas;
    int do_cas;

    private int id_jizdy;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id_jizdy Parameter 1.
     * @return A new instance of fragment DetailJizdyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailJizdyFragment newInstance(int id_jizdy) {
        DetailJizdyFragment fragment = new DetailJizdyFragment();
        Bundle args = new Bundle();
        args.putInt("id_jizdy", id_jizdy);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailJizdyFragment() {

    }

    public void aktualizovatPopisek(Button tl, int cas)
    {
        tl.setText(new SimpleDateFormat("dd. MM. yyyy hh:mm:aa").format(new Date(cas)));
    }

    public void prepnoutTankovani(boolean b)
    {
        radek_plna_nadrz.setVisibility(b ? View.VISIBLE : View.GONE);
        radek_litru.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    public void nacistHodnoty(int id)
    {
        SQLiteDatabase rdb = h.getReadableDatabase();

        Cursor c = rdb.query("jizdy", new String[]{"od_misto", "do_misto", "do_cas", "od_cas", "soukroma", "tankovano", "plna_nadrz", "litru"}, "_id = ?", new String[]{String.valueOf(id)}, null, null, null);
        c.moveToPosition(0);
        et_od_misto.setText(c.getString(c.getColumnIndex("od_misto")));
        et_do_misto.setText(c.getString(c.getColumnIndex("do_misto")));
        od_cas = c.getInt(c.getColumnIndex("od_misto"));
        do_cas = c.getInt(c.getColumnIndex("do_misto"));
        cb_soukroma_jizda.setChecked(c.getInt(c.getColumnIndex("soukroma")) == 1);
        cb_tankovano.setChecked(c.getInt(c.getColumnIndex("soukroma")) == 1);
        if (cb_tankovano.isChecked())
        {
            cb_plna_nadrz.setChecked(c.getInt(c.getColumnIndex("plna_nadrz")) == 1);
            et_litru.setText(c.getInt(c.getColumnIndex("litru")));
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        Log.d("id_jizdy", String.valueOf(id_jizdy));
        nacistHodnoty(id_jizdy);

        et_od_misto = (EditText) getView().findViewById(R.id.et_od_misto);
        et_do_misto = (EditText) getView().findViewById(R.id.et_do_misto);
        tl_od_cas = (Button) getView().findViewById(R.id.tl_od_cas);
        tl_do_cas = (Button) getView().findViewById(R.id.tl_do_cas);
        cb_soukroma_jizda = (CheckBox) getView().findViewById(R.id.cb_soukroma_jizda);
        cb_tankovano = (CheckBox) getView().findViewById(R.id.cb_tankovano);

        cb_plna_nadrz = (CheckBox) getView().findViewById(R.id.cb_plna_nadrz);
        et_litru = (EditText) getView().findViewById(R.id.et_litru);

        radek_plna_nadrz = (TableRow) getView().findViewById(R.id.radek_plna_nadrz);
        radek_litru = (TableRow) getView().findViewById(R.id.radek_litru);

        tl_ulozit = (Button) getView().findViewById(R.id.tl_ulozit);
        tl_zrusit = (Button) getView().findViewById(R.id.tl_zrusit);
        tl_smazat = (Button) getView().findViewById(R.id.tl_smazat);

        aktualizovatPopisek(tl_od_cas, od_cas);
        aktualizovatPopisek(tl_do_cas, do_cas);

        tl_do_cas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tl_od_cas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cb_tankovano.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prepnoutTankovani(isChecked);
            }
        });

        tl_ulozit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase wdb = h.getWritableDatabase();

                ContentValues cv = new ContentValues();
                cv.put("od_misto", et_od_misto.getText().toString());
                cv.put("do_misto", et_do_misto.getText().toString());
                cv.put("od_cas", od_cas);
                cv.put("do_cas", do_cas);
                cv.put("soukroma", cb_soukroma_jizda.isChecked()?0:1);
                cv.put("tankovano", cb_soukroma_jizda.isChecked()?0:1);
                if (cb_soukroma_jizda.isChecked())
                {
                    cv.put("plna_nadrz", cb_plna_nadrz.isChecked());
                    cv.put("litru", et_litru.getText().toString());
                }

                dismiss();
            }
        });

        tl_zrusit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tl_smazat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper h = new Helper(getContext());
                SQLiteDatabase wdb = h.getWritableDatabase();

                wdb.delete("jizdy", "_id = ?", new String[]{String.valueOf(id_jizdy)});

                dismiss();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id_jizdy = getArguments().getInt("id_jizdy");
            h = new Helper(getActivity());

            Log.d("jméno", h.getDatabaseName());

            SQLiteDatabase rdb = h.getReadableDatabase();

            Cursor c = rdb.query("jizdy", new String[]{"od_cas", "do_cas"}, "_id = "+id_jizdy, new String[]{}, null, null, null, null);
            c.moveToPosition(0);
            od_cas = c.getInt(c.getColumnIndex("od_cas"));
            do_cas = c.getInt(c.getColumnIndex("do_cas"));

            rdb.close();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_jizdy, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
