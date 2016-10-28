package sms1516.gruppo28.uniba.it.choosik;

import java.util.HashMap;


/**
 * Created by Giorgio on 28/10/2016.
 */

public class Dizionario {
    static HashMap dizProv;
    static String [] nomi;


    public static String[] getNomi() {
        return nomi;
    }

    public static HashMap dizionarioProvicia() {
        String[] nomiProvince = {
                "Agrigento", "Alessandria", "Ancona", "Aosta", "Aquila", "Arezzo", "Ascoli Piceno",
                "Asti", "Avellino", "Bari", "Belluno", "Benevento", "Bergamo", "Biella", "Bologna",
                "Bolzano", "Brescia", "Brindisi", "Cagliari", "Caltanissetta", "Campobasso", "Caserta",
                "Catania", "Catanzaro", "Chieti", "Como", "Cosenza", "Cremona", "Crotone", "Cuneo",
                "Enna", "Ferrara", "Firenze", "Foggia", "Forlì e Cesena", "Frosinone", "Genova",
                "Gorizia", "Grosseto", "Imperia", "Isernia", "La Spezia", "Latina", "Lecce", "Lecco",
                "Livorno", "Lodi", "Lucca", "Macerata", "Mantova", "Massa-Carrara", "Matera", "Messina",
                "Milano", "Modena", "Napoli", "Novara", "Nuoro", "Oristano", "Padova", "Palermo", "Parma",
                "Pavia", "Perugia", "Pesaro e Urbino", "Pescara", "Piacenza", "Pisa", "Pistoia",
                "Pordenone", "Potenza", "Prato", "Ragusa", "Ravenna", "Reggio Calabria", "Reggio Emilia",
                "Rieti", "Rimini", "Roma", "Rovigo", "Salerno", "Sassari", "Savona", "Siena", "Siracusa",
                "Sondrio", "Taranto", "Teramo", "Terni", "Torino", "Trapani", "Trento", "Treviso",
                "Trieste", "Udine", "Varese", "Venezia", "Verbano-Cusio-Ossola", "Vercelli", "Verona",
                "Vibo Valentia", "Vicenza", "Viterbo"};

        String[] siglaprovincie = {
                "AG", "AL", "AN", "AO", "AR", "AP", "AT", "AV", "BA", "BT", "BL", "BN", "BG",
                "BI", "BO", "BZ", "BS", "BR", "CA", "CL", "CB", "CI", "CE", "CT", "CZ", "CH",
                "CO", "CS", "CR", "CR", "CN", "EN", "FM", "FE", "FI", "FG",
                "FC", "FR", "GE", "GO", "GR", "IM", "IS", "SP", "AQ", "LT", "LE", "LC", "LI",
                "LO", "LU", "MC", "MN", "MS", "MT", "ME", "MI", "MO", "MB", "NA", "NO", "NU", "OT",
                "OR", "PD", "PA", "PR", "PV", "PG", "PU", "PE", "PC", "PI", "PT", "PN", "PZ", "PO",
                "RG", "RA", "RC", "RE", "RI", "RN", "RM", "RO", "SA",
                "VS", "SS", "SV", "SI", "SR", "SO", "TA", "TE", "TR", "TO", "TP", "TN", "TV", "TS",
                "UD", "VA", "VE", "VB", "VC", "VR", "VV", "VI", "VT",};


        dizProv = new HashMap<String, String>();
        for (int i = 0; i < nomiProvince.length; i++) {
            dizProv.put(nomiProvince[i], siglaprovincie[i]);
        }
        nomi=nomiProvince;
        return dizProv;

    }
}