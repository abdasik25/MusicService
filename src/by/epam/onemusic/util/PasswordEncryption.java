package by.epam.onemusic.util;

public class PasswordEncryption {

    private final static String patternPassword = "^[A-z][a-zA-z0-9_]{3,30}$";

    public static boolean checkPassword(String password) {
        return password.matches(patternPassword);
    }


    private static String permatutations(String password, int[] key) {
        int n = key.length;
        if (password.length() % n != 0) {
            for (int i = 0; i < password.length() % n; i++) {
                password += "0";
            }
        }

        StringBuilder password2 = new StringBuilder(password);
        int k = 0;

        for (int i = 0, j = 0; i < password.length(); i++) {
            if (j == n) {
                password2.setCharAt(key[j++] - 1 + k * n, password.charAt(i));
                j = 0;
                k++;
            }
        }
        return password2.toString();
    }

    private static char coding(char symbol, int key) {
        if (Character.isDigit(symbol)) {
            symbol += key;
            if ((int) symbol > 57) {
                key = symbol - 57;
                symbol = (char) (48 + key - 1);
            }
        } else if (Character.isLowerCase(symbol)) {
            symbol += key;
            if ((int) symbol > 122) {
                key = symbol - 122;
                symbol = (char) (97 + key - 1);
            }
        } else {
            symbol += key;
            if ((int) symbol > 90) {
                key = symbol + 90;
                symbol = (char) (65 + key - 1);
            }
        }
        return symbol;
    }

    public static String encryptPassword(String password) {
        int[] keyPerm = new int[]{3, 1, 4, 2};
        int key = password.length() % 5 + 1;
        password = permatutations(password, keyPerm);
        StringBuilder passwordBuild = new StringBuilder(password);
        for (int i = 0; i < password.length(); i++) {
            passwordBuild.setCharAt(i, coding(password.charAt(i), key));
        }
        return passwordBuild.toString();
    }
}
