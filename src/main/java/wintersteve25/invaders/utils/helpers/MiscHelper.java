package wintersteve25.invaders.utils.helpers;

import java.util.*;

public class MiscHelper {

    public static String langToReg(String lang) {
        return lang.toLowerCase().replace(' ', '_').replace('-', '_');
    }

    public static float randomInRange(float min, float max) {
        return (float) ((Math.random() * (max - min)) + min);
    }

    public static boolean chanceHandling(int chance) {
        Random rand = new Random();
        double randN = rand.nextDouble();

        return randN < (double) chance / 100;
    }

    public static boolean chanceHandling(float chance) {
        Random rand = new Random();
        double randN = rand.nextDouble();

        return randN < (double) chance;
    }
}
