package net.cleonet.cleo.photofeed_galileo.domain;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by Pepe on 9/7/17.
 */

public class Util {
    public Geocoder geocoder;
    public final static String GRAVATAR_URL="http://www.gravatar.com/avatar";

    public Util(Geocoder geocoder) {
        this.geocoder = geocoder;
    }

    public String getAvatarUrl(String email) {
        //Log.d("AvatarHelper", GRAVATAR_URL + md5(email) + "?s=64");
        return GRAVATAR_URL + md5(email) + "?s=64";
    }

    public String getFromLocation(double lat, double lng) {
        String result = "";
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat,lng, 1);
            Address address = addresses.get(0);
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                result += address.getAddressLine(i) + ", ";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String md5(final String s) {
        final String md5 = "MD5";
        try {
            //Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(md5);
            digest.update(s.getBytes());
            byte MessageDigest[] = digest.digest();

            //Create HEX string
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : MessageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
