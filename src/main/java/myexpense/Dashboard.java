package myexpense;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Dashboard MyExpense: kartu ringkasan, grafik batang pemasukan vs pengeluaran
 * per bulan, dan grafik lingkaran pengeluaran per kategori.
 * Data di bawah masih contoh; ganti dengan hasil query database bila sudah ada.
 */
public class Dashboard extends JFrame {

    private static final Color HIJAU = new Color(40, 120, 75);
    private static final Color MERAH = new Color(217, 83, 79);
    private static final Color BIRU = new Color(47, 111, 179);
    private static final Color[] WARNA = {
        HIJAU, BIRU, new Color(224, 160, 48), MERAH, new Color(138, 99, 184), new Color(42, 163, 163)
    };

    private static final String[] BULAN = {"Jan","Feb","Mar","Apr","Mei","Jun","Jul","Agu","Sep","Okt","Nov","Des"};
    private static final double[] MASUK  = {3.5e6,3.5e6,4.2e6,3.5e6,3.5e6,4.0e6,3.5e6,3.5e6,4.3e6,4.25e6,0,0};
    private static final double[] KELUAR = {2.1e6,1.9e6,2.4e6,2.2e6,2.6e6,2.0e6,2.3e6,2.5e6,2.2e6,1.21e6,0,0};
    private static final String[] KATEGORI = {"Makanan","Transportasi","Tagihan","Hiburan","Pendidikan","Kesehatan"};
    private static final double[] NILAI = {7.2e6, 3.1e6, 4.0e6, 1.8e6, 2.2e6, 0.9e6};

    private final NumberFormat rp = NumberFormat.getInstance(Locale.forLanguageTag("id-ID"));

    public Dashboard() {
        this(null);
    }

    public Dashboard(String namaPengguna) {
        setTitle("Dashboard - MyExpense");
        setSize(960, 640);
        setMinimumSize(new Dimension(760, 520));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HIJAU);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel judul = new JLabel("MyExpense - Dashboard" + (namaPengguna == null ? "" : "   |   Halo, " + namaPengguna));
        judul.setFont(new Font("Arial", Font.BOLD, 20));
        judul.setForeground(Color.WHITE);
        JButton btnKeluar = new JButton("Keluar");
        btnKeluar.setFocusPainted(false);
        btnKeluar.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });
        header.add(judul, BorderLayout.WEST);
        header.add(btnKeluar, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Isi
        double totalMasuk = jumlah(MASUK), totalKeluar = jumlah(KELUAR);
        JPanel isi = new JPanel(new BorderLayout(0, 14));
        isi.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        isi.setBackground(new Color(243, 246, 244));

        JPanel kartu = new JPanel(new GridLayout(1, 3, 14, 0));
        kartu.setOpaque(false);
        kartu.add(buatKartu("Total Pemasukan", "Rp " + rp.format(totalMasuk), HIJAU));
        kartu.add(buatKartu("Total Pengeluaran", "Rp " + rp.format(totalKeluar), MERAH));
        kartu.add(buatKartu("Saldo", "Rp " + rp.format(totalMasuk - totalKeluar), BIRU));
        isi.add(kartu, BorderLayout.NORTH);

        JPanel grafik = new JPanel(new GridLayout(1, 2, 14, 0));
        grafik.setOpaque(false);
        grafik.add(bungkus("Pemasukan vs Pengeluaran per Bulan", new BarChart()));
        grafik.add(bungkus("Pengeluaran per Kategori", new PieChart()));
        isi.add(grafik, BorderLayout.CENTER);

        add(isi, BorderLayout.CENTER);
    }

    private static double jumlah(double[] a) {
        double s = 0;
        for (double v : a) s += v;
        return s;
    }

    private JPanel buatKartu(String label, String nilai, Color aksen) {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 5, 0, 0, aksen),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        JLabel l = new JLabel(label);
        l.setForeground(Color.GRAY);
        JLabel v = new JLabel(nilai);
        v.setFont(new Font("Arial", Font.BOLD, 20));
        p.add(l);
        p.add(v);
        return p;
    }

    private JPanel bungkus(String judul, JComponent isi) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JLabel l = new JLabel(judul);
        l.setFont(new Font("Arial", Font.BOLD, 14));
        p.add(l, BorderLayout.NORTH);
        p.add(isi, BorderLayout.CENTER);
        return p;
    }

    /** Grafik batang berpasangan (pemasukan hijau, pengeluaran merah). */
    private static class BarChart extends JComponent {
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), kiri = 40, bawah = 40, atas = 10;
            double max = 1;
            for (int i = 0; i < 12; i++) max = Math.max(max, Math.max(MASUK[i], KELUAR[i]));
            max = Math.ceil(max / 1e6) * 1e6;

            g.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int i = 0; i <= 4; i++) {
                int y = atas + (h - atas - bawah) * i / 4;
                g.setColor(new Color(226, 232, 228));
                g.drawLine(kiri, y, w, y);
                g.setColor(Color.GRAY);
                g.drawString((int) (max * (4 - i) / 4 / 1e6) + " jt", 2, y + 4);
            }
            double slot = (w - kiri) / 12.0, bar = Math.max(3, slot * 0.32);
            for (int i = 0; i < 12; i++) {
                double x = kiri + slot * i + slot / 2;
                int tinggiArea = h - atas - bawah;
                int hm = (int) (MASUK[i] / max * tinggiArea), hk = (int) (KELUAR[i] / max * tinggiArea);
                g.setColor(HIJAU);
                g.fillRect((int) (x - bar), atas + tinggiArea - hm, (int) bar, hm);
                g.setColor(MERAH);
                g.fillRect((int) x, atas + tinggiArea - hk, (int) bar, hk);
                g.setColor(Color.DARK_GRAY);
                g.drawString(BULAN[i], (int) (x - 9), h - bawah + 16);
            }
            // Legenda
            g.setColor(HIJAU); g.fillRect(kiri, h - 14, 10, 10);
            g.setColor(Color.DARK_GRAY); g.drawString("Pemasukan", kiri + 14, h - 5);
            g.setColor(MERAH); g.fillRect(kiri + 90, h - 14, 10, 10);
            g.setColor(Color.DARK_GRAY); g.drawString("Pengeluaran", kiri + 104, h - 5);
            g.dispose();
        }
    }

    /** Grafik lingkaran dengan legenda persentase. */
    private static class PieChart extends JComponent {
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            double total = jumlah(NILAI);
            int d = Math.max(60, Math.min(w, h - 80) - 10);
            int x0 = (w - d) / 2, y0 = 8;
            double sudut = 90;
            for (int i = 0; i < NILAI.length; i++) {
                double span = -NILAI[i] / total * 360;
                g.setColor(WARNA[i % WARNA.length]);
                g.fill(new Arc2D.Double(x0, y0, d, d, sudut, span, Arc2D.PIE));
                sudut += span;
            }
            g.setColor(Color.WHITE);
            int lubang = d / 2;
            g.fillOval(x0 + (d - lubang) / 2, y0 + (d - lubang) / 2, lubang, lubang);

            g.setFont(new Font("Arial", Font.PLAIN, 11));
            int ly = y0 + d + 14, kolom = w / 2;
            for (int i = 0; i < NILAI.length; i++) {
                int lx = 6 + (i % 2) * kolom, yy = ly + (i / 2) * 16;
                g.setColor(WARNA[i % WARNA.length]);
                g.fillRect(lx, yy - 9, 10, 10);
                g.setColor(Color.DARK_GRAY);
                g.drawString(KATEGORI[i] + " " + Math.round(NILAI[i] / total * 100) + "%", lx + 14, yy);
            }
            g.dispose();
        }
    }
}
