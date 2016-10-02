package com.james.bagels.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.webkit.URLUtil;

public class Bagel implements Parcelable {

    public String location;
    public boolean isTrueBagel;

    public Bagel(String location) {
        this.location = location;
        isTrueBagel = URLUtil.isNetworkUrl(location);
    }

    protected Bagel(Parcel in) {
        location = in.readString();
        isTrueBagel = in.readByte() != 0;
    }

    public static final Creator<Bagel> CREATOR = new Creator<Bagel>() {
        @Override
        public Bagel createFromParcel(Parcel in) {
            return new Bagel(in);
        }

        @Override
        public Bagel[] newArray(int size) {
            return new Bagel[size];
        }
    };

    public String getName() {
        String[] names = location.split("/");
        return names[names.length - 1];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(location);
        dest.writeByte((byte) (isTrueBagel ? 1 : 0));
    }
}
