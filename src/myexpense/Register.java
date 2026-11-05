package myexpense;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/** Form pendaftaran akun: nama, email, nomor handphone, password (semua wajib). */
public class Register extends JDialog {
    private final AuthService auth;
    private final JTextField txtNama = Ui.field(new JTextField());
    private final JTextField txtEmail = Ui.field(new JTextField());
    private final JTextField txtHp = Ui.field(new JTextField());
    private final JPasswordField txtPassword = Ui.field(new JPasswordField());
    private final JLabel errUmum = Ui.pesanError(), errNama = Ui.pesanError(), errEmail = Ui.pesanError(),
            errHp = Ui.pesanError(), errPassword = Ui.pesanError();
    private String emailTerdaftar;

    public Register(Frame induk, AuthService auth) {
        super(induk, "Daftar Akun - MyExpense", true);
        this.auth = auth;
        setSize(400, 560);
        setLocationRelativeTo(induk);
        setResizable(false);

        JPanel p = Ui.kosong();
        p.add(Ui.judul("Daftar Akun"));
        p.add(Ui.jarak(10));
        p.add(errUmum);
        tambah(p, "Nama", txtNama, errNama);
        tambah(p, "Email", txtEmail, errEmail);
        tambah(p, "Nomor Handphone", txtHp, errHp);
        tambah(p, "Password (minimal 6 karakter)", txtPassword, errPassword);
        p.add(Ui.jarak(4));

        JButton btn = Ui.tombol("DAFTAR");
        btn.addActionListener(e -> proses());
        p.add(btn);
        p.add(Ui.jarak(12));
        p.add(Ui.baris(new JLabel("Sudah punya akun?"), Ui.tautan("Login di sini", this::dispose)));

        getRootPane().setDefaultButton(btn);
        add(p);
    }

    private void tambah(JPanel p, String label, JComponent f, JLabel err) {
        p.add(Ui.label(label));
        p.add(Ui.jarak(4));
        p.add(f);
        p.add(err);
        p.add(Ui.jarak(4));
    }

    private void proses() {
        for (JLabel l : new JLabel[]{errUmum, errNama, errEmail, errHp, errPassword}) l.setText(" ");
        Ui.rapikan(txtNama, false); Ui.rapikan(txtEmail, false); Ui.rapikan(txtHp, false); Ui.rapikan(txtPassword, false);
        try {
            AuthService.HasilDaftar h = auth.daftar(txtNama.getText(), txtEmail.getText(), txtHp.getText(),
                    new String(txtPassword.getPassword()));
            if (h.sukses()) {
                emailTerdaftar = txtEmail.getText().trim().toLowerCase();
                dispose();
                return;
            }
            if (!h.umum.isEmpty()) errUmum.setText("<html>" + h.umum + "</html>");
            tampilError(h.errors, "nama", errNama, txtNama);
            tampilError(h.errors, "email", errEmail, txtEmail);
            tampilError(h.errors, "noHp", errHp, txtHp);
            tampilError(h.errors, "password", errPassword, txtPassword);
        } catch (UserRepository.DbException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tampilError(Map<String, String> e, String kunci, JLabel lbl, JComponent f) {
        if (e.containsKey(kunci)) { lbl.setText(e.get(kunci)); Ui.rapikan(f, true); }
    }

    /** Email akun yang baru dibuat, atau null bila pendaftaran dibatalkan. */
    public String getEmailTerdaftar() { return emailTerdaftar; }
}
