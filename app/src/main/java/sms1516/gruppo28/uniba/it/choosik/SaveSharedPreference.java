package sms1516.gruppo28.uniba.it.choosik;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Classe per controllo e gestione della sequenza di activity nel caso in cui l'utente sia
 * gia loggato o meno.
 */

public class SaveSharedPreference {
    static final String PREF_USER_NAME= "username";
    static final String PREF_EMAIL= "email";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    /**
     * Setta nelle preferenze il nome utente che si e' loggato.
     */
    public static void setUserName(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    public static void setEmail(Context ctx, String email)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_EMAIL, email);
        editor.commit();
    }

    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static String getEmail(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_EMAIL, "");
    }

    /**
     * Metodo da richiamare nel navbar per eseguire il logut, cancellando l'username registrato
     * come loggato.
     */
    public static void clearUserName(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear(); // cancella tutti i dati salvati nelle preferenze
        editor.commit();
    }
}
