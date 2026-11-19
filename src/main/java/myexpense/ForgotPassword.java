package myexpense;

import javax.swing.*;
import java.awt.*;

/** Lupa password: 1) isi email  2) jika email ada, isi password baru + konfirmasi. */
public class ForgotPassword extends JDialog {
    private final AuthService auth;
    private final CardLayout kartu = new CardLayout();
    private final JPanel isi = new JPanel(kartu);

    private final JTextField txtEmail = Ui.field(new JTextField());
    private final JLabel errEmail = Ui.pesanError();

    private final JLabel info = new JLabel("<html><center>.<br>.</center></html>");
    private final JPasswordField txtBaru = Ui.field(new JPasswordField());
    private final JPasswordField txtKonfirmasi = Ui.field(new JPasswordField());
    private final JLabel errPassword = Ui.pesanError();

    private User user;
    private boolean berhasil;

    public ForgotPassword(Frame induk, AuthService auth) {
        super(induk, "Lupa Password - MyExpense", true);
        this.auth = auth;
        setSize(400, 400);
        setLocationRelativeTo(induk);
        setResizable(false);

        isi.add(langkahEmail(), "email");
        isi.add(langkahPassword(), "password");
        add(isi);
    }

    private JPanel langkahEmail() {
        JPanel p = Ui.kosong();
        p.add(Ui.judul("Lupa Password"));
        p.add(Ui.jarak(8));
        p.add(Ui.tengah(new JLabel("<html><center>Masukkan email akun Anda untuk<br>mengatur ulang password.</center></html>")));
        p.add(Ui.jarak(18));
        p.add(Ui.label("Email"));
        p.add(Ui.jarak(4));
        p.add(txtEmail);
        p.add(errEmail);
        p.add(Ui.jarak(10));
        JButton btn = Ui.tombol("LANJUT");
        btn.addActionListener(e -> cekEmail());
        getRootPane().setDefaultButton(btn);
        p.add(btn);
        p.add(Ui.jarak(14));
        p.add(Ui.baris(Ui.tautan("Kembali ke login", this::dispose)));
        return p;
    }

    private JPanel langkahPassword() {
        JPanel p = Ui.kosong();
        p.add(Ui.judul("Password Baru"));
        p.add(Ui.jarak(8));
        p.add(Ui.tengah(info));
        p.add(Ui.jarak(14));
        p.add(Ui.label("Password Baru"));
        p.add(Ui.jarak(4));
        p.add(txtBaru);
        p.add(Ui.jarak(8));
        p.add(Ui.label("Konfirmasi Password"));
        p.add(Ui.jarak(4));
        p.add(txtKonfirmasi);
        p.add(errPassword);
        p.add(Ui.jarak(10));
        JButton btn = Ui.tombol("SIMPAN PASSWORD BARU");
        btn.addActionListener(e -> simpan());
        p.add(btn);
        p.add(Ui.jarak(14));
        p.add(Ui.baris(Ui.tautan("Gunakan email lain", () -> {
            user = null; txtBaru.setText(""); txtKonfirmasi.setText("");
            errPassword.setText(" ");
            kartu.show(isi, "email");
            getRootPane().setDefaultButton(null);
        })));
        return p;
    }

    private void cekEmail() {
        errEmail.setText(" ");
        Ui.rapikan(txtEmail, false);
        String email = txtEmail.getText().trim();
        if (email.isEmpty()) { errEmail.setText("Email wajib diisi."); Ui.rapikan(txtEmail, true); return; }
        try {
            user = auth.cekEmail(email);
            if (user == null) { errEmail.setText("Email tidak ditemukan."); Ui.rapikan(txtEmail, true); return; }
            info.setText("<html><center>Email <b>" + user.email + "</b> ditemukan.<br>Buat password baru Anda.</center></html>");
            kartu.show(isi, "password");
            txtBaru.requestFocus();
        } catch (UserRepository.DbException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void simpan() {
        try {
            String err = auth.resetPassword(user, new String(txtBaru.getPassword()), new String(txtKonfirmasi.getPassword()));
            if (err.isEmpty()) {
                berhasil = true;
                dispose();
            } else {
                errPassword.setText(err);
            }
        } catch (UserRepository.DbException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isBerhasil() { return berhasil; }
}
