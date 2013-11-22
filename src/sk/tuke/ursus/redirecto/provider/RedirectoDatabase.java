package sk.tuke.ursus.redirecto.provider;

import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
				+ Rooms.COLUMN_CREATED_AT + " TEXT NOT NULL,"
				+ Rooms.COLUMN_CHANGED_AT + " TEXT NOT NULL,"
				+ Rooms.COLUMN_FLOOR + " TEXT NOT NULL)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//
	}
}

