package com.ig2i.symphonia;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateOnlyFormat {

    private static SimpleDateFormat fmt = new SimpleDateFormat();

    //Format de date utilisé dans l'historique
    public static String format(Date date) {
        fmt.applyPattern("dd/MM/yyyy");
        return fmt.format(date);
    }

}
