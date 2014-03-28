package sk.tuke.ursus.redirecto.model;

import com.google.gson.annotations.SerializedName;

import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;
import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

public class Room implements Parcelable {

	public int id;
	public String name;
	@SerializedName("floor") public String description;
	@SerializedName("phone_number") public String phoneNumber;
	@SerializedName("created_at") public String createdAt;
	@SerializedName("changed_at") public String changedAt;
	private boolean isAdded = false;

	public void setAdded(boolean isAdded) {
		this.isAdded = isAdded;
	}

	public boolean isAdded() {
		return isAdded;
	}

	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(Rooms.COLUMN_ID, id);
		values.put(Rooms.COLUMN_NAME, name);
		values.put(Rooms.COLUMN_DESC, description);
		values.put(Rooms.COLUMN_PHONE_NUMBER, phoneNumber);
		values.put(Rooms.COLUMN_CREATED_AT, createdAt);
		values.put(Rooms.COLUMN_CHANGED_AT, changedAt);
		return values;
	}

	@Override
	public String toString() {
		return name + "(" + description + " · " + phoneNumber + ")";
	}

	/**
	 * Parcelable boiler plate
	 */
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
		description = source.readString();
		phoneNumber = source.readString();
		createdAt = source.readString();
		changedAt = source.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeString(phoneNumber);
		dest.writeString(createdAt);
		dest.writeString(changedAt);
	}

	@Override
	public int describeContents() {
		return 0;
	}

}
