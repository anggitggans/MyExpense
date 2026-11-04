package myexpense;

import javax.swing.*;
import java.awt.*;

public class Login extends JFrame {
    private final AuthService auth = new AuthService(new JdbcUserRepository());
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JLabel lblPesan;

    public Login() {
        setTitle("Login - MyExpense");
        setSize(400, 470);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = Ui.kosong();
        panel.add(Ui.judul("MyExpense"));
        panel.add(Ui.tengah(new JLabel("Login ke akun Anda")));
        panel.add(Ui.jarak(14));

        lblPesan = Ui.pesanError();
        panel.add(lblPesan);

        panel.add(Ui.label("Email"));
        panel.add(Ui.jarak(4));
        txtEmail = Ui.field(new JTextField());
        panel.add(txtEmail);
        panel.add(Ui.jarak(10));

        panel.add(Ui.label("Password"));
        panel.add(Ui.jarak(4));
        txtPassword = Ui.field(new JPasswordField());
        panel.add(txtPassword);
        panel.add(Ui.jarak(6));

        JLabel lupa = Ui.tautan("Lupa password?", this::bukaLupaPassword);
        JPanel kanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        kanan.setOpaque(false);
        kanan.setAlignmentX(Component.LEFT_ALIGNMENT);
        kanan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        kanan.add(lupa);
        panel.add(kanan);
        panel.add(Ui.jarak(14));

        JButton btnLogin = Ui.tombol("LOGIN");
        btnLogin.addActionListener(e -> prosesLogin());
        panel.add(btnLogin);
        panel.add(Ui.jarak(14));

        panel.add(Ui.baris(new JLabel("Belum punya akun?"), Ui.tautan("Daftar di sini", this::bukaDaftar)));

        getRootPane().setDefaultButton(btnLogin);
        add(panel);
    }

    private void bukaDaftar() {
        Register dlg = new Register(this, auth);
        dlg.setVisible(true);
        if (dlg.getEmailTerdaftar() != null) {
            txtEmail.setText(dlg.getEmailTerdaftar());
            txtPassword.setText("");
            tampilkan("Pendaftaran berhasil! Silakan login.", new Color(30, 120, 60));
            txtPassword.requestFocus();
        }
    }

    private void bukaLupaPassword() {
        ForgotPassword dlg = new ForgotPassword(this, auth);
        dlg.setVisible(true);
        if (dlg.isBerhasil()) {
            txtPassword.setText("");
            tampilkan("Password berhasil diubah. Silakan login.", new Color(30, 120, 60));
        }
    }

    private void tampilkan(String teks, Color warna) {
        lblPesan.setForeground(warna);
        lblPesan.setText(teks);
    }

    private void prosesLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            tampilkan("Email dan password wajib diisi.", Ui.MERAH);
            return;
        }
        try {
            User u = auth.login(email, password);
            if (u != null) {
                new Dashboard(u.nama).setVisible(true);
                dispose();
                return;
            }
            tampilkan("Email atau password salah!", Ui.MERAH);
            txtPassword.setText("");
            txtPassword.requestFocus();
        } catch (UserRepository.DbException ex) {
            // Database belum siap: akun demo tetap bisa masuk agar dashboard bisa dicoba
            if (email.equalsIgnoreCase("admin@myexpense.com") && password.equals("admin123")) {
                JOptionPane.showMessageDialog(this,
                        "MySQL belum terhubung, masuk dengan MODE DEMO.\n\n" + ex.getMessage(),
                        "Mode Demo", JOptionPane.INFORMATION_MESSAGE);
                new Dashboard("Admin (demo)").setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
