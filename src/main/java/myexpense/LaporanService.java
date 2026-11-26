
package myexpense;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

public class LaporanService {

    private final TransaksiRepository repo = new TransaksiRepository();

    public void tampilkanLaporan(
            int userId,
            LocalDate dari,
            LocalDate sampai
    ) throws Exception {

        // 1. Ambil transaksi berdasarkan user dan periode
        List<TransaksiEntity> data = repo.cari(
                userId,
                "",
                "Semua",
                "Semua",
                dari,
                sampai
        );

        // 2. Ambil template laporan dari resources
        InputStream file = getClass().getResourceAsStream(
                "/laporan_keuangan.jrxml"
        );

        if (file == null) {
            throw new Exception(
                    "File laporan_keuangan.jrxml tidak ditemukan. "
                    + "Pastikan berada di src/main/resources."
            );
        }

        // 3. Kirim data transaksi ke JasperReports
        try (InputStream template = file) {

            JRBeanCollectionDataSource dataSource =
                    new JRBeanCollectionDataSource(data);

            Map<String, Object> parameter = new HashMap<>();

            // 4. Compile template JRXML
            var jasperReport =
                    JasperCompileManager.compileReport(template);

            // 5. Isi laporan dengan data transaksi
            JasperPrint hasil = JasperFillManager.fillReport(
                    jasperReport,
                    parameter,
                    dataSource
            );

            // 6. Tampilkan laporan
            JasperViewer.viewReport(hasil, false);
        }
    }
}
