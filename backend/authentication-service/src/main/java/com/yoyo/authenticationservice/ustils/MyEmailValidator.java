package com.yoyo.authenticationservice.ustils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyEmailValidator {

   private Pattern pattern;
   private Matcher matcher;

    static{
        Pattern pattern;
       Matcher matcher;
    }

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public MyEmailValidator() {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    public boolean validate(final String hex) {

        matcher = pattern.matcher(hex);
        return matcher.matches();

    }
}


