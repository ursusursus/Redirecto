package sk.tuke.ursus.redirecto.provider;

import sk.tuke.ursus.redirecto.provider.RedirectoContract.AccessPoints;
import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQL datab�zov� helper
 * 
 * @author Vlastimil Bre�ka
 * 
 */
public class RedirectoDatabase extends SQLiteOpenHelper {

	private static final String DB_NAME = "redirecto.db";
	private static final int DB_VERSION = 1;

	public RedirectoDatabase(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Rooms.TABLE + "("
				+ Rooms.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ Rooms.COLUMN_CURRENT + " INTEGER DEFAULT 0,"
				+ Rooms.COLUMN_NAME + " TEXT NOT NULL,"
				+ Rooms.COLUMN_DESC + " TEXT NOT NULL,"
				+ Rooms.COLUMN_PHONE_NUMBER + " TEXT NOT NULL,"
				+ Rooms.COLUMN_CREATED_AT + " TEXT NOT NULL,"
				+ Rooms.COLUMN_CHANGED_AT + " TEXT NOT NULL)");

		db.execSQL("CREATE TABLE " + AccessPoints.TABLE + "("
				+ AccessPoints.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ AccessPoints.COLUMN_SSID + " TEXT NOT NULL,"
				+ AccessPoints.COLUMN_BSSID + " TEXT NOT NULL UNIQUE)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//
	}
}
