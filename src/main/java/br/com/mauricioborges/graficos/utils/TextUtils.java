package br.com.mauricioborges.graficos.utils;

/**
 * Utilitários para texto
 *
 * @author Mauricio Borges
 * @since 10/2022
 */
public abstract class TextUtils {

    /**
     * Sobrescrever números
     *
     * @param number número
     * @return string contendo o número sobrescrito
     */
    public static String sup(int number) {
        char[] array = String.valueOf(number).toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : array) {
            try {
                sb.append(supAlgarismo(Integer.parseInt(String.valueOf(c))));
            } catch (NumberFormatException e) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String supAlgarismo(int n) {
        switch (n) {
            case 0:
                return "⁰";
            case 1:
                return "¹";
            case 2:
                return "²";
            case 3:
                return "³";
            case 4:
                return "⁴";
            case 5:
                return "⁵";
            case 6:
                return "⁶";
            case 7:
                return "⁷";
            case 8:
                return "⁸";
            case 9:
                return "⁹";
            default:
                return null;
        }
    }

}
