package common;

public class Validation {
    public static boolean regexID(String id) {
        return !id.matches("^(CG-)(\\d{3})$");
    }

    public static boolean regexEmail(String email) {
        return !email.matches("^\\w{3,}@[a-z]{3,7}\\.[a-z]{2,3}$");
    }

    public static boolean regexAge(String age) {
        return !age.matches("\\d+") || Integer.parseInt(age) < 18;
    }
}
