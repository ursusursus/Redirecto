package sk.tuke.ursus.redirecto.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import sk.tuke.ursus.redirecto.model.Room;
import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;

public class RedirectoProvider extends ContentProvider {

	private static final UriMatcher sUriMatcher = buildUriMatcher();

	private static final int ROOMS = 100;

	private RedirectoDatabase mDbHelper;

	@Override
	public boolean onCreate() {
		mDbHelper = new RedirectoDatabase(getContext());

		return true;
	}

	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = RedirectoContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, Rooms.PATH, ROOMS);

		return matcher;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count;
		final SQLiteDatabase db = mDbHelper.getWritableDatabase();

		final int match = sUriMatcher.match(uri);
		switch (match) {
			case ROOMS:
				count = db.delete(Rooms.TABLE, selection, selectionArgs);
				break;

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);

		}
		return count;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = mDbHelper.getWritableDatabase();

		final int match = sUriMatcher.match(uri);
		switch (match) {
			case ROOMS:
				long insertedId = db.insert(Rooms.TABLE, null, values);
				Uri.withAppendedPath(uri, String.valueOf(insertedId));
				break;

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);

		}
		return uri;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] allValues) {
		return -1;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int count;
		final SQLiteDatabase db = mDbHelper.getWritableDatabase();

		final int match = sUriMatcher.match(uri);
		switch (match) {
			case ROOMS:
				count = db.update(Rooms.TABLE, values, selection, selectionArgs);
				break;

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);

		}
		return count;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		final SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = null;

		final int match = sUriMatcher.match(uri);
		switch (match) {
			case ROOMS:
				cursor = db.query(Rooms.TABLE, projection, selection, selectionArgs, null, null, Rooms.COLUMN_NAME + " ASC");
				cursor.setNotificationUri(getContext().getContentResolver(), uri);
				break;

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);

		}
		return cursor;
	}

}
