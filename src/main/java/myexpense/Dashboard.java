package myexpense;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/** Dashboard CatatUang berbasis Java Swing; seluruh data transaksi berasal dari Hibernate. */
public class Dashboard extends JFrame {
    private static final Color GREEN = new Color(34, 116, 78), BG = new Color(244, 247, 245), RED = new Color(190, 65, 65), BLUE = new Color(48, 105, 160);
    private final User user;
    private final AuthService auth;
    private final TransaksiRepository repo;
    private final NumberFormat rupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Tanggal", "Jenis", "Kategori", "Keterangan", "Nominal"}, 0) { public boolean isCellEditable(int r,int c){return false;} };
    private final JTable table = new JTable(model);
    private final JTextField cari = new JTextField(15);
    private final JComboBox<String> filterJenis = new JComboBox<>(new String[]{"Semua", "Pemasukan", "Pengeluaran"});
    private final JComboBox<String> filterKategori = new JComboBox<>(new String[]{"Semua", "Makanan", "Transportasi", "Tagihan", "Hiburan", "Pendidikan", "Kesehatan", "Belanja", "Gaji", "Lainnya"});
    private final JTextField dari = new JTextField(10), sampai = new JTextField(10);
    private final JLabel totalMasuk = new JLabel(), totalKeluar = new JLabel(), saldo = new JLabel();
    private final JLabel status = new JLabel(" ");

    public Dashboard(User user, AuthService auth, TransaksiRepository repo) {
        this.user=user; this.auth=auth; this.repo=repo;
        setTitle("CatatUang - Pengelolaan Keuangan Pribadi"); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850,600)); setSize(1120,720); setLocationRelativeTo(null);
        getContentPane().setBackground(BG); setLayout(new BorderLayout());
        add(header(), BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Dashboard", dashboardPanel());
        tabs.addTab("Tambah Transaksi", formTransaksi(null));
        tabs.addTab("Riwayat Transaksi", riwayatPanel());
        tabs.addTab("Laporan Pengeluaran", laporanPanel());
        tabs.addTab("Pengaturan", pengaturanPanel());
        add(tabs, BorderLayout.CENTER);
        status.setBorder(BorderFactory.createEmptyBorder(7,14,7,14)); add(status, BorderLayout.SOUTH);
        muatSemua();
    }

    private JPanel header(){
        JPanel p=new JPanel(new BorderLayout()); p.setBackground(GREEN); p.setBorder(BorderFactory.createEmptyBorder(14,20,14,20));
        JLabel title=new JLabel("CatatUang  |  Halo, "+user.nama); title.setForeground(Color.WHITE); title.setFont(new Font("SansSerif",Font.BOLD,20));
        JButton logout=new JButton("Logout"); logout.addActionListener(e->{ if(JOptionPane.showConfirmDialog(this,"Yakin ingin keluar?","Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){new Login(auth).setVisible(true);dispose();}});
        p.add(title,BorderLayout.WEST); p.add(logout,BorderLayout.EAST); return p;
    }

    private JPanel dashboardPanel(){
        JPanel p=new JPanel(new BorderLayout(12,12)); p.setBackground(BG); p.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));
        JPanel cards=new JPanel(new GridLayout(1,3,12,0)); cards.setOpaque(false);
        cards.add(card("Total Pemasukan",totalMasuk,GREEN)); cards.add(card("Total Pengeluaran",totalKeluar,RED)); cards.add(card("Saldo",saldo,BLUE));
        p.add(cards,BorderLayout.NORTH);
        JPanel center=new JPanel(new BorderLayout(8,8)); center.setBackground(Color.WHITE); center.setBorder(BorderFactory.createEmptyBorder(14,14,14,14));
        JLabel label=new JLabel("Transaksi Terbaru"); label.setFont(new Font("SansSerif",Font.BOLD,16)); center.add(label,BorderLayout.NORTH);
        JTable recent=new JTable(model); center.add(new JScrollPane(recent),BorderLayout.CENTER);
        JButton refresh=new JButton("Perbarui Ringkasan"); refresh.addActionListener(e->muatSemua()); center.add(refresh,BorderLayout.SOUTH);
        p.add(center,BorderLayout.CENTER); return p;
    }

    private JPanel card(String title,JLabel value,Color color){
        JPanel p=new JPanel(new GridLayout(2,1,0,8)); p.setBackground(Color.WHITE); p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,5,0,0,color),BorderFactory.createEmptyBorder(15,15,15,15)));
        JLabel l=new JLabel(title); l.setForeground(Color.GRAY); value.setFont(new Font("SansSerif",Font.BOLD,20)); p.add(l); p.add(value); return p;
    }

    private JPanel riwayatPanel(){
        JPanel p=new JPanel(new BorderLayout(8,8)); p.setBackground(BG); p.setBorder(BorderFactory.createEmptyBorder(14,14,14,14));
        JPanel tools=new JPanel(new FlowLayout(FlowLayout.LEFT)); tools.setBackground(BG);
        tools.add(new JLabel("Cari:")); tools.add(cari); tools.add(new JLabel("Jenis:")); tools.add(filterJenis); tools.add(new JLabel("Kategori:")); tools.add(filterKategori);
        tools.add(new JLabel("Dari (yyyy-mm-dd):")); tools.add(dari); tools.add(new JLabel("Sampai:")); tools.add(sampai);
        JButton apply=new JButton("Terapkan Filter"); apply.addActionListener(e->muatTabel());
        JButton reset=new JButton("Reset"); reset.addActionListener(e->{cari.setText("");filterJenis.setSelectedIndex(0);filterKategori.setSelectedIndex(0);dari.setText("");sampai.setText("");muatTabel();});
        tools.add(apply);tools.add(reset); p.add(tools,BorderLayout.NORTH);
        table.setRowHeight(25); p.add(new JScrollPane(table),BorderLayout.CENTER);
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.RIGHT)); actions.setBackground(BG);
        JButton edit=new JButton("Edit"), del=new JButton("Hapus"), add=new JButton("Tambah Transaksi");
        edit.addActionListener(e->editTerpilih()); del.addActionListener(e->hapusTerpilih()); add.addActionListener(e->new TransaksiDialog(this,null).setVisible(true));
        actions.add(add);actions.add(edit);actions.add(del);p.add(actions,BorderLayout.SOUTH); return p;
    }

    private JPanel formTransaksi(Object unused){
        JPanel p=new JPanel(new BorderLayout()); p.setBackground(BG); p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        JButton add=new JButton("Buka Form Tambah Transaksi"); add.addActionListener(e->new TransaksiDialog(this,null).setVisible(true)); p.add(add,BorderLayout.NORTH);
        JLabel info=new JLabel("Catat pemasukan atau pengeluaran dengan mengisi jenis, tanggal, kategori, nominal, dan keterangan.",SwingConstants.CENTER); p.add(info,BorderLayout.CENTER); return p;
    }

    // Panel laporan: grafik pengeluaran dan tombol untuk membuka laporan JasperReports.
    private JPanel laporanPanel(){
        JPanel p=new JPanel(new BorderLayout(10,10)); p.setBackground(BG); p.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));
        JLabel summary=new JLabel("Laporan bulan berjalan akan dihitung dari transaksi tersimpan.");
        JPanel bagianAtas=new JPanel(new BorderLayout(8,8)); bagianAtas.setBackground(BG);
        bagianAtas.add(summary,BorderLayout.NORTH);
        JPanel filter=new JPanel(new FlowLayout(FlowLayout.LEFT)); filter.setBackground(BG);
        JTextField tanggalDari=new JTextField(YearMonth.now().atDay(1).toString(),10);
        JTextField tanggalSampai=new JTextField(LocalDate.now().toString(),10);
        filter.add(new JLabel("Dari (yyyy-mm-dd):")); filter.add(tanggalDari);
        filter.add(new JLabel("Sampai:")); filter.add(tanggalSampai);
        JButton cetak=new JButton("Lihat Laporan Keuangan");
        JButton refresh=new JButton("Perbarui Laporan");
        filter.add(cetak); filter.add(refresh);
        bagianAtas.add(filter,BorderLayout.SOUTH); p.add(bagianAtas,BorderLayout.NORTH);

        JPanel chart=new JPanel(){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                List<TransaksiEntity> data=repo.semuaByUser(user.id);
                Map<String,BigDecimal> sums=new LinkedHashMap<>();
                for(TransaksiEntity t:data) if("Pengeluaran".equalsIgnoreCase(t.getJenis()) && YearMonth.from(t.getTanggal()).equals(YearMonth.now())) sums.merge(t.getKategori(),t.getNominal(),BigDecimal::add);
                int x=35,y=25;
                BigDecimal max=sums.values().stream().max(BigDecimal::compareTo).orElse(BigDecimal.ONE);
                g2.setFont(new Font("SansSerif",Font.PLAIN,12));
                for(var en:sums.entrySet()){
                    int w=max.signum()==0?0:en.getValue().multiply(BigDecimal.valueOf(Math.max(100,getWidth()-260))).divide(max,0,java.math.RoundingMode.HALF_UP).intValue();
                    g2.setColor(new Color(49,139,96)); g2.fillRoundRect(x,y,w,24,8,8);
                    g2.setColor(Color.DARK_GRAY); g2.drawString(en.getKey()+" - "+rupiah.format(en.getValue()),x+w+8,y+17); y+=42;
                }
                if(sums.isEmpty())g2.drawString("Belum ada data pengeluaran.",x,y);
                g2.dispose();
            }
        };
        chart.setBackground(Color.WHITE); chart.setOpaque(true); chart.setPreferredSize(new Dimension(760,420));

        refresh.addActionListener(e->{
            YearMonth bulan=YearMonth.now();
            summary.setText("Total pengeluaran bulan ini: "+rupiah.format(totalPeriode("Pengeluaran",bulan.atDay(1),bulan.atEndOfMonth())));
            chart.repaint();
        });

      
cetak.addActionListener(e -> {
    try {
        LocalDate mulai = parseTanggal(tanggalDari.getText());
        LocalDate akhir = parseTanggal(tanggalSampai.getText());

        if (mulai == null || akhir == null) {
            throw new IllegalArgumentException(
                "Tanggal awal dan akhir wajib diisi."
            );
        }

        if (mulai.isAfter(akhir)) {
            throw new IllegalArgumentException(
                "Tanggal awal tidak boleh melebihi tanggal akhir."
            );
        }

        new LaporanService().tampilkanLaporan(
            user.id, mulai, akhir
        );

    } catch (Exception ex) {
        ex.printStackTrace();

        JOptionPane.showMessageDialog(
            this,
            "Gagal menampilkan laporan: " + ex.getMessage()
                + "\nPeriksa Output NetBeans untuk detail error.",
            "Kesalahan Laporan",
            JOptionPane.ERROR_MESSAGE
        );
    }
});

        p.add(new JScrollPane(chart),BorderLayout.CENTER); return p;
    }

    private JPanel pengaturanPanel(){
        JPanel p=new JPanel(new GridBagLayout()); p.setBackground(BG); GridBagConstraints c=new GridBagConstraints();c.insets=new Insets(7,7,7,7);c.fill=GridBagConstraints.HORIZONTAL;
        JTextField nama=new JTextField(user.nama,22),email=new JTextField(user.email,22),hp=new JTextField(); try{hp.setText(Myexpense.getUserRepository().noHpById(user.id));}catch(Exception ex){}
        JButton save=new JButton("Simpan Profil"); JButton password=new JButton("Ganti Password");
        addRow(p,c,0,"Nama",nama);addRow(p,c,1,"Email",email);addRow(p,c,2,"Nomor HP",hp);c.gridx=1;c.gridy=3;p.add(save,c);c.gridy=4;p.add(password,c);
        save.addActionListener(e->{try{auth.ubahProfil(user,nama.getText(),email.getText(),hp.getText());JOptionPane.showMessageDialog(this,"Profil berhasil diperbarui. Silakan login ulang agar data akun diperbarui.");}catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Gagal",JOptionPane.ERROR_MESSAGE);}});
        password.addActionListener(e->{JPasswordField a=new JPasswordField(),b=new JPasswordField();Object[] fields={"Password baru",a,"Konfirmasi",b};if(JOptionPane.showConfirmDialog(this,fields,"Ganti Password",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){try{String msg=auth.resetPassword(user,new String(a.getPassword()),new String(b.getPassword()));if(msg.isEmpty())JOptionPane.showMessageDialog(this,"Password berhasil diubah.");else JOptionPane.showMessageDialog(this,msg);}catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage());}}});
        return p;
    }

    private void addRow(JPanel p,GridBagConstraints c,int row,String label,JComponent field){c.gridx=0;c.gridy=row;p.add(new JLabel(label),c);c.gridx=1;p.add(field,c);}

    private void muatSemua(){
        muatTabel();
        BigDecimal masuk=totalPeriode("Pemasukan",null,null);
        BigDecimal keluar=totalPeriode("Pengeluaran",null,null);
        totalMasuk.setText(rupiah.format(masuk)); totalKeluar.setText(rupiah.format(keluar)); saldo.setText(rupiah.format(masuk.subtract(keluar)));
    }

    private BigDecimal totalPeriode(String jenis,LocalDate start,LocalDate end){
        try{return repo.total(user.id,jenis,start,end);}
        catch(Exception ex){pesan(ex.getMessage()==null?"Gagal menghitung total transaksi.":ex.getMessage());return BigDecimal.ZERO;}
    }

    private void muatTabel(){
        try{
            LocalDate start=parseTanggal(dari.getText()); LocalDate end=parseTanggal(sampai.getText());
            List<TransaksiEntity> rows=repo.cari(user.id,cari.getText(),(String)filterJenis.getSelectedItem(),(String)filterKategori.getSelectedItem(),start,end);
            model.setRowCount(0);
            for(TransaksiEntity t:rows)model.addRow(new Object[]{t.getId(),t.getTanggal(),t.getJenis(),t.getKategori(),t.getKeterangan(),rupiah.format(t.getNominal())});
        }catch(Exception ex){pesan(ex.getMessage()==null?"Gagal memuat riwayat transaksi.":ex.getMessage());}
    }

    private LocalDate parseTanggal(String s){return s==null||s.isBlank()?null:LocalDate.parse(s.trim(),DateTimeFormatter.ISO_LOCAL_DATE);}

    private void editTerpilih(){
        int row=table.getSelectedRow(); if(row<0){pesan("Pilih transaksi yang ingin diedit.");return;}
        int id=(int)model.getValueAt(table.convertRowIndexToModel(row),0);
        TransaksiEntity t=repo.cariById(id,user.id); if(t!=null)new TransaksiDialog(this,t).setVisible(true);
    }

    private void hapusTerpilih(){
        int row=table.getSelectedRow(); if(row<0){pesan("Pilih transaksi yang ingin dihapus.");return;}
        int id=(int)model.getValueAt(table.convertRowIndexToModel(row),0);
        if(JOptionPane.showConfirmDialog(this,"Hapus transaksi terpilih?","Konfirmasi Hapus",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){repo.hapus(id,user.id);muatSemua();}
    }

    private void pesan(String s){status.setText(s);}

    private class TransaksiDialog extends JDialog{
        private final TransaksiEntity edit;
        private final JTextField tanggal=new JTextField(LocalDate.now().toString()),kategori=new JTextField(),nominal=new JTextField(),keterangan=new JTextField();
        private final JComboBox<String> jenis=new JComboBox<>(new String[]{"Pengeluaran","Pemasukan"});

        TransaksiDialog(Frame owner,TransaksiEntity data){
            super(owner,data==null?"Tambah Transaksi":"Edit Transaksi",true); edit=data;
            setSize(420,390);setLocationRelativeTo(owner);setLayout(new GridLayout(0,2,8,8));
            ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
            if(data!=null){tanggal.setText(data.getTanggal().toString());jenis.setSelectedItem(data.getJenis());kategori.setText(data.getKategori());nominal.setText(data.getNominal().toPlainString());keterangan.setText(data.getKeterangan());}
            add(new JLabel("Jenis"));add(jenis);add(new JLabel("Tanggal (yyyy-mm-dd)"));add(tanggal);
            add(new JLabel("Kategori"));add(kategori);add(new JLabel("Nominal"));add(nominal);
            add(new JLabel("Keterangan"));add(keterangan);
            JButton save=new JButton("Simpan"),cancel=new JButton("Batal");add(save);add(cancel);
            cancel.addActionListener(e->dispose());save.addActionListener(e->simpan());
        }

        void simpan(){
            try{
                LocalDate d=LocalDate.parse(tanggal.getText().trim());String kat=kategori.getText().trim();BigDecimal n=new BigDecimal(nominal.getText().trim());
                if(kat.isEmpty()||n.signum()<=0)throw new IllegalArgumentException("Kategori wajib diisi dan nominal harus lebih dari 0.");
                if(edit==null)repo.tambah(user.id,d,(String)jenis.getSelectedItem(),kat,keterangan.getText().trim(),n);
                else repo.ubah(edit.getId(),user.id,d,(String)jenis.getSelectedItem(),kat,keterangan.getText().trim(),n);
                dispose();muatSemua();
            }catch(Exception ex){JOptionPane.showMessageDialog(this,"Data tidak valid: "+ex.getMessage(),"Validasi",JOptionPane.ERROR_MESSAGE);}
        }
    }
}
