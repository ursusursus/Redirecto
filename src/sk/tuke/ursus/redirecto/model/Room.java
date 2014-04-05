package sk.tuke.ursus.redirecto.model;

import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;
import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Miestnosù
 */
public class Room implements Parcelable {

	/**
	 * ID miestnosti
	 */
	public int id;

	/**
	 * Meno miestnosti
	 */
	public String name;

	/**
	 * Popis miestnosti
	 */
	@SerializedName("floor") public String description;

	/**
	 * TelefÛnne ËÌslo miestnosti
	 */
	@SerializedName("phone_number") public String phoneNumber;

	/**
	 * Timestamp poslednej zmeny
	 */
	@SerializedName("created_at") public String createdAt;

	/**
	 * Timestamp vytvorenia
	 */
	@SerializedName("changed_at") public String changedAt;

	/**
	 * Pridan˝
	 */
	private boolean isAdded = false;

	/**
	 * NastavÌ priadanie
	 */
	public void setAdded(boolean isAdded) {
		this.isAdded = isAdded;
	}

	/**
	 * Vr·ti Ëi je miestnosù pridan·
	 */
	public boolean isAdded() {
		return isAdded;
	}

	/**
	 * NaplnÌ ContentValues Ëlensk˝mi premenn˝mi miestnosti
	 */
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

	/**
	 * Miestnosù vo forme stringu
	 */
	@Override
	public String toString() {
		return name + "(" + description + " ∑ " + phoneNumber + ")";
	}

	/**
	 * Parcelable boilerplate, nevöÌmaù si
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
