import javax.swing.*;
import java.awt.Component;

public class DialogHelper {

    public static String askUUID(Component parent, String message) {
        return JOptionPane.showInputDialog(parent, message);
    }

    public static double askAmount(Component parent) {
        String amountStr = JOptionPane.showInputDialog(parent, "Введите сумму:");
        if (amountStr == null) return -1;
        try {
            return Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            showError(parent, "Некорректная сумма!");
            return -1;
        }
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Информация", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
}