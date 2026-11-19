package myexpense;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Komponen tampilan yang dipakai bersama oleh form login, daftar, dan lupa password. */
final class Ui {
    static final Color HIJAU = new Color(40, 120, 75);
    static final Color MERAH = new Color(200, 50, 50);

    private Ui() {}

    static JLabel judul(String teks) {
        JLabel l = new JLabel(teks, SwingConstants.CENTER);
        l.setFont(new Font("Arial", Font.BOLD, 24));
        return tengah(l);
    }

    /** Label selebar panel dengan teks di tengah (semua komponen memakai perataan kiri agar sejajar). */
    static JLabel tengah(JLabel l) {
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, l.getPreferredSize().height + 4));
        return l;
    }

    static JLabel label(String teks) {
        JLabel l = new JLabel(teks);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    static JLabel pesanError() {
        JLabel l = new JLabel(" ");
        l.setForeground(MERAH);
        l.setFont(new Font("Arial", Font.PLAIN, 11));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    static <T extends JComponent> T field(T f) {
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        f.setPreferredSize(new Dimension(300, 32));
        return f;
    }

    static JButton tombol(String teks) {
        JButton b = new JButton(teks);
        b.setBackground(HIJAU);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        return b;
    }

    /** Teks seperti tautan: berwarna hijau, kursor tangan. */
    static JLabel tautan(String teks, Runnable aksi) {
        JLabel l = new JLabel(teks);
        l.setForeground(HIJAU);
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { aksi.run(); }
        });
        return l;
    }

    static JPanel baris(Component... isi) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        for (Component c : isi) p.add(c);
        return p;
    }

    static JPanel kosong() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(24, 40, 24, 40));
        return p;
    }

    static Component jarak(int h) { return Box.createRigidArea(new Dimension(0, h)); }

    static void rapikan(JComponent f, boolean error) {
        f.setBorder(error ? BorderFactory.createLineBorder(MERAH)
                          : UIManager.getBorder(f instanceof JPasswordField ? "PasswordField.border" : "TextField.border"));
    }
}
