package com.example.productsfromusa.utils;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CurrencyUtils {

    public int convertRub(int num, int reduction) {
        int intValue = (int) num;
        if(reduction == 10) {
            intValue = (intValue + 5) / 10 * 10;
        } else if (reduction == 100) {
            intValue = (intValue + 50) / 100 * 100;
        } else if (reduction == 9) {
            String numberAsString = String.valueOf(intValue);
            String modifiedNumberString = "";
            if(numberAsString.length() >= 1) {
                modifiedNumberString = numberAsString.substring(0, numberAsString.length() - 1) + "9";
            } else {
                modifiedNumberString = "9";
            }
            intValue = Integer.parseInt(modifiedNumberString);
        } else if (reduction == 99) {
            String numberAsString = String.valueOf(intValue);
            String modifiedNumberString = "";
            if (numberAsString.length() >= 2) {
                int c = Integer.parseInt(numberAsString.substring(numberAsString.length() - 2, numberAsString.length()));
                if (c >= 50) {
                    modifiedNumberString = numberAsString.substring(0, numberAsString.length() - 2) + "99";
                } else {
                    intValue -= 100;
                    numberAsString = String.valueOf(intValue);
                    modifiedNumberString = numberAsString.substring(0, numberAsString.length() - 2) + "99";
                }
            } else {
                if (intValue < 50) {
                    modifiedNumberString = "49";
                } else {
                    modifiedNumberString = "99";
                }
            }
            intValue = Integer.parseInt(modifiedNumberString);
        }
        return intValue;
    }

    public double convertUSD(double mn, int reduction) {
        if(reduction == 10) {
            mn = Math.round(mn * 10.0) / 10.0;
        } else if(reduction == 100) {
            int intValue = (int) mn;
            mn = (double) intValue;
        } else if(reduction == 9) {
            BigDecimal bd = new BigDecimal(Double.toString(mn));
            bd = bd.setScale(1, BigDecimal.ROUND_DOWN);
            bd = bd.subtract(new BigDecimal("0.01"));
            mn = bd.doubleValue();
        } else if(reduction == 99) {
            BigDecimal bd = new BigDecimal(Double.toString(mn));
            bd = bd.setScale(0, BigDecimal.ROUND_UP);
            bd = bd.subtract(new BigDecimal("0.01"));
            mn = bd.doubleValue();
        }
        return mn;
    }
}
