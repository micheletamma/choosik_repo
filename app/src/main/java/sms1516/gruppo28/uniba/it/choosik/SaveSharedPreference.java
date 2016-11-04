package sms1516.gruppo28.uniba.it.choosik;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe per controllo e gestione della sequenza di activity nel caso in cui l'utente sia
 * gia loggato o meno.
 */

public class SaveSharedPreference {
    static final String PREF_USER_NAME = "username";
    static final String PREF_EMAIL = "email";
    static final String ISARTIST = "isartist";
    static final String PROVINCIA = "provincia";
    static final String ARTISTI ="artisti";


    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    /**
     * Setta nelle preferenze il nome utente che si e' loggato.
     */
    public static void setUserName(Context ctx, String userName) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    public static void setArtisti(Context ctx, String [] artisti){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        Set<String> mySet = new HashSet<String>(Arrays.asList(artisti));
        editor.putStringSet(ARTISTI,mySet);
        editor.commit();
    }
    public static void setIsArtist(Context ctx, boolean isartist) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(ISARTIST, isartist);
        editor.commit();
    }


    public static void setEmail(Context ctx, String email) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_EMAIL, email);
        editor.commit();
    }
    public static void setProvincia(Context ctx, String provincia) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PROVINCIA, provincia);
        editor.commit();
    }

    public static String [] getArtisti (Context ctx){
        String [] test = new String []{"c","a"};
        Set<String> temp = new HashSet<String>(Arrays.asList(test));
        String [] artisti = new String [getSharedPreferences(ctx).getStringSet(ARTISTI,temp).size()];
        Set<String> mySet = new HashSet<String>(getSharedPreferences(ctx).getStringSet(ARTISTI,temp));
        artisti = mySet.toArray(artisti);
        return artisti;
    }

    public static boolean getIsArtist(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(ISARTIST, false);
    }

    public static String getUserName(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static String getEmail(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_EMAIL, "");
    }
    public static String getProvincia(Context ctx) {
        return getSharedPreferences(ctx).getString(PROVINCIA, "");
    }

    /**
     * Metodo da richiamare nel navbar per eseguire il logut, cancellando l'username registrato
     * come loggato.
     */
    public static void clearUserName(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear(); // cancella tutti i dati salvati nelle preferenze
        editor.commit();
    }
}
