package myexpense;

import javax.swing.SwingUtilities;

/**
 * Kelas utama proyek: membuka halaman Login.
 */
public class Myexpense {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
