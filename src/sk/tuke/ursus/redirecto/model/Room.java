package sk.tuke.ursus.redirecto.model;

import com.google.gson.annotations.SerializedName;

import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;
import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

public class Room implements Parcelable {

	public int id;
	public String name;
	public String floor;
	@SerializedName("created_at")
	public String createdAt;
	@SerializedName("changed_at")
	public String changedAt;

	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(Rooms.COLUMN_ID, id);
		values.put(Rooms.COLUMN_NAME, name);
		values.put(Rooms.COLUMN_FLOOR, floor);
		values.put(Rooms.COLUMN_CREATED_AT, createdAt);
		values.put(Rooms.COLUMN_CHANGED_AT, changedAt);
		return values;
	}

	public static final Parcelable.Creator<Room> CREATOR = new Parcelable.Creator<Room>() {

		@Override
		public Room createFromParcel(Parcel source) {
			return new Room(source);
		}

		@Override
		public Room[] newArray(int size) {
			return new Room[size];
		}
	};

	public Room(Parcel source) {
		id = source.readInt();
		name = source.readString();
		floor = source.readString();
		createdAt = source.readString();
		changedAt = source.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(floor);
		dest.writeString(createdAt);
		dest.writeString(changedAt);
	}

	@Override
	public int describeContents() {
		return 0;
	}


}
