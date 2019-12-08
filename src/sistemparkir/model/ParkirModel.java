/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemparkir.model;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Position;
import sistemparkir.database.DatabaseMySQL;
/**
 *
 * @author 62822
 */
public class ParkirModel {
    private Connection konek;
    private Statement st;
    private ResultSet rs;
    private MotorModel motor = new MotorModel();
    private MobilModel mobil = new MobilModel();
    
    public boolean parkirMasuk(String nopol, String jenis){      
        var current_time = System.currentTimeMillis();
        System.out.println(new Date(current_time));
        String sql = "INSERT INTO parkir_masuk(no_polisi,jenis_kendaraan,waktu_masuk)"
                    + "VALUES('"+nopol+"','"+jenis+"',"+current_time+")";
        try {
            konek = DatabaseMySQL.getConnection();
            st = konek.createStatement();
            st.execute(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public DefaultTableModel setTabelMasuk(DefaultTableModel masuk){
        String query = "SELECT * FROM parkir_masuk ORDER BY no_tiket ASC";        
        try {
            konek = DatabaseMySQL.getConnection();
            st = konek.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {                
                String no_tiket = rs.getString("no_tiket");
                String no_pol = rs.getString("no_polisi");
                String jenis = rs.getString("jenis_kendaraan");
                String jam_masuk = new SimpleDateFormat("HH:mm:ss").format(new Date(rs.getLong("waktu_masuk")));
                String tgl_masuk = new SimpleDateFormat("dd-MM-yyyy").format(new Date(rs.getLong("waktu_masuk")));
                
                String [] data = {no_tiket, no_pol, jenis, tgl_masuk, jam_masuk};
                masuk.addRow(data);
            }
            return masuk;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
   public String[] ValidasiCari (String Cari) {
       DecimalFormat rupiah = (DecimalFormat) DecimalFormat.getCurrencyInstance();
       DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
       formatRp.setCurrencySymbol("Rp ");
       formatRp.setMonetaryDecimalSeparator(',');
       formatRp.setGroupingSeparator('.');
       rupiah.setDecimalFormatSymbols(formatRp);
       String query = "SELECT * FROM parkir_masuk WHERE no_polisi='"+Cari+"' OR no_tiket='"+Cari+"'";
//       
//       "SELECT *, DATEDIFF(CURDATE(), tgl_masuk) durasi_hari, (TIME_FORMAT(CURTIME(),'%H')-TIME_FORMAT(jam_masuk, '%H')) durasi_jam "
//                    + "FROM tb_parkir WHERE no_pol='"+txtKNoPol.getText()+"' AND biaya=0";
       
       try {
            konek = DatabaseMySQL.getConnection();
            st = konek.createStatement();
            rs = st.executeQuery(query);
            if (rs.next()) {
                String no_tiket = rs.getString("no_tiket");
                String no_pol = rs.getString("no_polisi");
                String jenis = rs.getString("jenis_kendaraan");
                Long waktu_masuk = rs.getLong("waktu_masuk");
                Long current_waktu = System.currentTimeMillis();
                Long diff = current_waktu - waktu_masuk;
                
		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);
                
                String jam_masuk = new SimpleDateFormat("HH:mm:ss").format(new Date(waktu_masuk));
                String tgl_masuk = new SimpleDateFormat("dd-MM-yyyy").format(new Date(waktu_masuk));
                
                int mobil_menginap = 50000;
                int motor_menginap = 20000;
                
                String biaya;
                int biaya_motor = motor.getTarifAwal().intValue();
                int biaya_mobil = mobil.getTarifAwal().intValue();
                if (jenis.equalsIgnoreCase("motor")) {
                    if (diffDays >= 1) {
                        if (diffHours >= 1) {
                            biaya_motor += (diffDays*motor_menginap) + ((diffHours -1)* motor.getTarifPerJam());
                        } else {
                            biaya_motor += (diffDays*motor_menginap);
                        }
                    } else {
                        if (diffHours >= 1) {
                            biaya_motor += ((diffHours -1)* motor.getTarifPerJam());
                        }
                    } biaya = rupiah.format(biaya_motor);
                } else {
                    if (diffDays >= 1) {
                        if (diffHours >= 1) {
                            biaya_mobil += (diffDays*mobil_menginap) + ((diffHours -1)* mobil.getTarifPerJam());
                        } else {
                            biaya_mobil += (diffDays*mobil_menginap);
                        }
                    } else {
                        if (diffHours >= 1) {
                            biaya_mobil += ((diffHours -1)* mobil.getTarifPerJam());
                        }
                    }
                    biaya = rupiah.format(biaya_mobil);
                }
                String [] data = {no_tiket,no_pol,jenis,jam_masuk,tgl_masuk,diffDays+" Hari, "+diffHours+" Jam, "+diffMinutes+" Menit. ",biaya};
                return data;
            } else {
                return null;
            }
       } catch (Exception e) {
           e.printStackTrace();
       }
        return null;
   }
   
   private int toMins(String s) {
        String[] hourMin = s.split(":");
        int hour = Integer.parseInt(hourMin[0]);
        int mins = Integer.parseInt(hourMin[1]);
        int hoursInMins = hour * 60;
        return hoursInMins + mins;
   }
}
