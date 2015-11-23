package cz.marbes.knihajizd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by weiss on 12.10.2015.
 */
public class Helper extends SQLiteOpenHelper {

    public Helper(Context context) {
        super(context, "kniha_jizd", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //TODO smazat dropy
        db.execSQL("drop table if exists auta");
        db.execSQL("drop table if exists jizdy");
        db.execSQL("drop table if exists tankovani");
        db.execSQL("create table if not exists auta (_id int, jmeno text)");
        db.execSQL("create table if not exists jizdy (_id int, id_auta int, od_misto text, do_misto text, od_cas int, do_cas int, tankovano int1, id_tankovani int, soukroma int1)");
        db.execSQL("create table if not exists tankovani (_id int, plna_nadrz int1, objem int)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
