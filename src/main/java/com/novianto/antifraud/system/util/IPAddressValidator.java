package com.novianto.antifraud.system.util;

import java.util.regex.Pattern;

public class IPAddressValidator {

    /**
    * Check apakah alamat IP yang diberikan tidak valid menggunakan regular expression.
    *
    * @param ip IP address akan di check
    * @return True jika alamat IP tidak valid, false jika tidak
    */

    public static boolean isNonValidIp(String ip){
        Pattern pattern = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$");
        return !pattern.matcher(ip).matches();
    }
}
