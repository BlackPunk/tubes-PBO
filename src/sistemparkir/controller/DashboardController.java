package sistemparkir.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import sistemparkir.database.DatabaseMySQL;
import sistemparkir.view.DashboardView;

/**
 *
 * @author Ammar Amri
 */
public class DashboardController extends MouseAdapter{
    private DashboardView dashboard = new DashboardView();
    private JPanel parkir = dashboard.getParkir();
    private JPanel pendapatan = dashboard.getPendapatan();
    private JPanel pengaturan = dashboard.getPengaturan();
    private JPanel keluar = dashboard.getKeluar();
    private JPanel topPanel = dashboard.getTopPanel();
    private int xx,xy;

    public DashboardController() {
        dashboard.setLocationRelativeTo(null);
        dashboard.setListener(this);
        dashboard.setVisible(true);
    }
    

    @Override
    public void mouseEntered(MouseEvent e) {
        Object source = e.getSource();
        if (source.equals(parkir)) {
            dashboard.setColor(parkir);
        }else if (source.equals(pendapatan)) {
            dashboard.setColor(pendapatan);
        }else if (source.equals(pengaturan)) {
            dashboard.setColor(pengaturan);
        }else if (source.equals(keluar)){
            dashboard.setColor(keluar);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        Object source = e.getSource();
        if (source.equals(parkir)) {
            dashboard.resetColor(parkir);
        }else if (source.equals(pendapatan)) {
            dashboard.resetColor(pendapatan);
        }else if (source.equals(pengaturan)) {
            dashboard.resetColor(pengaturan);
        }else{
            dashboard.resetColor(keluar);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Object source = e.getSource();
        if (source.equals(keluar)) {
            int dialogBtn = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(dashboard, "Anda yakin akan keluar?", "PERINGATAN", dialogBtn);
            if (dialogResult == 0) {
                System.exit(0);
            }
        }else if (source.equals(topPanel)) {
            xx = e.getX();
            xy = e.getY();
        }else if (source.equals(pengaturan)) {
            if (!DatabaseMySQL.isConnect()) {
                JOptionPane.showMessageDialog(dashboard, "Gagal Terkoneksi dengan database","DATABASE FAILED",JOptionPane.ERROR_MESSAGE);
            }else{
                new PengaturanController();
            }
        }else if (source.equals(parkir)) {
            new ParkirController();
        }else if (source.equals(pendapatan)) {
            if (!DatabaseMySQL.isConnect()) {
                JOptionPane.showMessageDialog(dashboard, "Gagal Terkoneksi dengan database","DATABASE FAILED",JOptionPane.ERROR_MESSAGE);
            }else{
                new PendapatanController();
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int x = e.getXOnScreen();
        int y = e.getYOnScreen();
        dashboard.setLocation(x-xx,y-xy);
    }   
}
