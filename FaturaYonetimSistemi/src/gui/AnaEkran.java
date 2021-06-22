/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;


import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.Timer;
import keeptoo.Drag;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author PC
 */
public class AnaEkran extends javax.swing.JFrame {
      
    Connection conn=null;
    PreparedStatement pst=null;
    ResultSet rs=null;
    private String musteritoplamsayi="SELECT COUNT (*) FROM MUSTERILER";
    private String uruntoplamsayi="SELECT COUNT (*) FROM URUNLER";
    private String kullanicilartoplamsayi="SELECT COUNT (*) FROM KULLANICILAR";
    private String faturalartoplamsayi="SELECT COUNT (*) FROM FATURALAR";
    private String adTextYazi="Müşteri adı giriniz";
    private String soyadTextYazi="Müşteri soyadı giriniz";
    private String tcTextYazi="Müşteri TC-No giriniz";
    private String telnoTextYazi="Müşteri telefon numarası giriniz";
    private String mailTextYazi="Müşteri e-mail giriniz";
    private String adresTextYazi="Müşteri adresi giriniz";
    private String musteriguncelleText="TC-No veya ID ile arama yapınız";
    private String urunguncelleText="Ürün ID ile arama yapınız";
    private String urunadTextYazi="Ürün adını giriniz";
    private String urunFiyatTextYazi="Ürün fiyatını giriniz";
    private String urunAciklamaTextYazi="Ürün açıklamasını giriniz";
    private String urunadetTextYazi="Ürün adetini giriniz";
    private String yoneticiText="Lütfen arama yapınız";
    final ImageIcon soruisareti_icon = new ImageIcon(getClass().getResource("/iconlar/soruisareti_icon.png"));
    final ImageIcon ok_icon = new ImageIcon(getClass().getResource("/iconlar/ok_icon.png"));
    final ImageIcon unlem_icon = new ImageIcon(getClass().getResource("/iconlar/unlem_icon.png"));
    
    Timer updateTimer;
    int delay=100;
    
    public AnaEkran() {
        initComponents();
        this.setLocationRelativeTo(null);
        
        if(Kullanicisim.rol.equals("Yönetici")){
            kasiyer_panel.setVisible(false);
            musterislemsecenek_panel.setVisible(false);
            urunislemsecenek_panel.setVisible(false);
            panel_yonetim.setVisible(false);
            anasayfa_panel.setVisible(true);
            yonetim_panel.setVisible(true);
            kullanicisim_label.setText("Yönetici Sayın "+Kullanicisim.kullanicisim);
         }
        else{
          
            yonetim_panel.setVisible(false);
            musterislemsecenek_panel.setVisible(false);
            urunislemsecenek_panel.setVisible(false);
            anasayfa_panel.setVisible(true);
            kasiyer_panel.setVisible(true);
            kullanicisim_label.setText("Kasiyer Sayın "+Kullanicisim.kullanicisim);
        }
              
        tablotoplamsayi(musteritoplamsayi, kayitlimusterisayi_label);
        tablotoplamsayi(uruntoplamsayi, kayitliurunsayi_label);
        tablotoplamsayi(kullanicilartoplamsayi, kayitlikullanicisayi_label);
        tablotoplamsayi(faturalartoplamsayi, kayitlifaturasayi_label);
        tabloayarlari(tbl_musteri);
        tabloayarlari(tbl_urun);
        tabloayarlari(yoneticikasiyer_table);
        genelTextAyarlari();
        }
 
  
   private void genelTextAyarlari(){
        //TextField karakter limitleri belirlenir.
        TextAyarlari.setLimitTextField(txt_Ad, 20);
        TextAyarlari.setLimitTextField(txt_Soyad11, 20);
        TextAyarlari.setLimitTextField(txt_Tel11, 11);
        TextAyarlari.setLimitTextField(txt_tc11, 11);
        TextAyarlari.setLimitTextField(txt_ad10, 20);
        TextAyarlari.setLimitTextField(txt_Soyad10, 20);
        TextAyarlari.setLimitTextField(txt_Tel10, 11);
        TextAyarlari.setLimitTextField(txt_tc10, 11);
        
        TextAyarlari.setLimitTextField(txt_UrunAdı, 20);
        TextAyarlari.setLimitTextField(txt_fiyat, 10);
        TextAyarlari.setLimitTextField(txt_UrunAdet, 10);
        TextAyarlari.setLimitTextField(txt_Aciklama, 50);
        
        TextAyarlari.setLimitTextField(txt_UrunAdi, 20);
        TextAyarlari.setLimitTextField(txt_fiyat1, 10);
        TextAyarlari.setLimitTextField(txt_Adet, 10);
        TextAyarlari.setLimitTextField(txt_Aciklama1, 50);
        
        //TextField sadece rakam/harf ayarlamaları yapılır
        TextAyarlari.setOnlyLetter(txt_Ad);
        TextAyarlari.setOnlyLetter(txt_Soyad11);
        TextAyarlari.setOnlyNumber(txt_tc11);
        TextAyarlari.setOnlyNumber(txt_Tel11);
        TextAyarlari.setOnlyLetter(txt_ad10);
        TextAyarlari.setOnlyLetter(txt_Soyad10);
        TextAyarlari.setOnlyNumber(txt_tc10);
        TextAyarlari.setOnlyNumber(txt_Tel10);
        
        
        
        TextAyarlari.setOnlyNumber(txt_UrunAdet);
        
        TextAyarlari.setOnlyNumber(txt_Adet);
        
   }
    
    private void veritabaniEkle(String sql,javax.swing.JTextField txt,String tablo){
        int id=0;
        try
            {
                conn = db.connect_db();
                pst = conn.prepareStatement(sql);
                pst.execute(); 
                ResultSet rs = pst.getGeneratedKeys(); //Eklenen müşteri/ürünün id'sini alıyor.
                if(rs.next())
                {
                     id = rs.getInt(1);
                }
                uyariMesajiPanel(ok_icon, tablo+" "+txt.getText()+" başarıyla eklenmiştir! \n \n"+tablo+" ID: "+id, "Tamam", "","Başarılı");
                
            }
            catch(Exception ex)
            {
                JOptionPane.showMessageDialog(null, ex);
            }
            finally
            {
                try
                {
                    conn.close();
                }
                catch(Exception ex)
                {
                
                }
            }
        
        
        
    }
    
    private void veritabaniGuncelle(String sql){
        try
        {
            conn=db.connect_db();
            pst=conn.prepareStatement(sql);
            pst.execute();
            uyariMesajiPanel(ok_icon, "Güncelleme işlemi başarıyla gerçekleşmiştir!","Tamam", "","Başarılı");
            
        }
        catch(Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex);
        }
        finally
        {
            try
            {
                
            }
            catch(Exception ex)
            {
                
            }
        }
        
    }
    
    private void veritabaniSil(String sql){
         try
            {
                conn = db.connect_db();
                pst = conn.prepareStatement(sql);
                pst.execute();
                
                uyariMesajiPanel(ok_icon, "Silme işlemi başarıyla gerçekleşmiştir!","Tamam", "","Başarılı");
            }
            catch(Exception ex)
            {
                JOptionPane.showMessageDialog(null, ex);
            }
            finally
            {
                try
                {
                    conn.close();
                }
                catch(Exception ex)
                {
                    
                }
            }
        }
    
    private void tablotoplamsayi(String sql,JLabel jLabel){
         try
        {
            conn=db.connect_db();
            pst=conn.prepareStatement(sql);
            rs=pst.executeQuery();
            jLabel.setText(rs.getString(1));
            
        }
        catch(Exception ex)
        {
            
        }
        finally
        {
            try
            {
                conn.close();
            }
            catch(Exception ex)
            {
                
            }
        }
        
    }
    
    private void uyariMesajiPanel(ImageIcon icon,String mesaj,String secenek,String secenek1,String title){
         JLabel label = new JLabel(mesaj);
         label.setFont(new Font("Segoe UI", Font.BOLD, 15));
         JFrame frame = new JFrame();
         String[] secenekler={secenek+secenek1};
         JOptionPane.showOptionDialog(frame.getContentPane(),label,title,0,JOptionPane.INFORMATION_MESSAGE,icon,secenekler,null);
         
    }
    
     private void Update_table(String sql,javax.swing.JTable tbl) { //Tabloya veri çekmek için
    try{
        conn=db.connect_db();
        pst=conn.prepareStatement(sql);
        rs=pst.executeQuery();
        tbl.setModel(DbUtils.resultSetToTableModel(rs));
    }
    catch(Exception e){
    JOptionPane.showMessageDialog(null, e);
    }
    finally {
            
            try{
                rs.close();
                pst.close();
                
            }
            catch(Exception e){
                
            }
        }
    }
    
     private void cm_doldur(javax.swing.JComboBox<String> cm_box,String sql)
    {
        conn=db.connect_db();
        try
        {
            pst=conn.prepareStatement(sql);
            rs=pst.executeQuery(); 
            
            while(rs.next())
            {
                cm_box.addItem(rs.getString(2));
            }
        }
        catch(Exception ex)
        {
            
        }
        finally
        {
            try
            {
                conn.close();
            }
            catch(Exception ex)
            {
                
            }
        }
        
    }
     private void butonHoverRenklendir(JPanel panel){ //Anasayfa butonlarının üzerine gelince renklendir
         panel.setBackground(new Color(235,235,255));
     }
     
     private void butonOnLeaveHover(JPanel panel){
        panel.setBackground(Color.white);
     }
     
    private void musteriguncellepanelayar(){
        txt_ad10.setText(musteriguncelleText);
        txt_Soyad10.setText(musteriguncelleText);
        txt_tc10.setText(musteriguncelleText);
        txt_Tel10.setText(musteriguncelleText);
        txt_Mail10.setText(musteriguncelleText);
        txt_adres10.setText(musteriguncelleText);
        txt_aratc.setText("");
        txt_araid.setText("");
        cbx_Cinsiyet10.setSelectedIndex(0);
        
        txt_ad10.setEnabled(false);
        txt_Soyad10.setEnabled(false);
        txt_tc10.setEnabled(false);
        txt_Tel10.setEnabled(false);
        txt_Mail10.setEnabled(false);
        txt_adres10.setEnabled(false);
        cbx_Cinsiyet10.setEnabled(false);
    } 
    private void yoneticikasiyerguncelleayar(){
        txt_Ad2.setText(yoneticiText);
        txt_Soyad14.setText(yoneticiText);
        txt_kullaniciadi1.setText(yoneticiText);
        txt_sifre1.setText(yoneticiText);
        txt_araid1.setText("");
        txt_Mail14.setText(yoneticiText);
        cmb_rol1.setSelectedIndex(0);
        
        txt_Ad2.setEnabled(false);
        txt_Soyad14.setEnabled(false);
        txt_kullaniciadi1.setEnabled(false);
        txt_sifre1.setEnabled(false);
        txt_Mail14.setEnabled(false);
        cmb_rol1.setEnabled(false);
    }
    
    private void urunguncellepanelayar(){
        txt_UrunAdi.setText(urunguncelleText);
        txt_fiyat1.setText(urunguncelleText);
        txt_Adet.setText(urunguncelleText);
        txt_Aciklama1.setText(urunguncelleText);
        txt_id1.setText("");
        cmb_urunkategori1.setSelectedIndex(0);
        
        txt_UrunAdi.setEnabled(false);
        txt_fiyat1.setEnabled(false);
        txt_Adet.setEnabled(false);
        txt_Aciklama1.setEnabled(false);
        cmb_urunkategori1.setEnabled(false);
    }
    private void uruneklepanelayar(){
        cmb_urunkategori.setSelectedIndex(0);
        txt_UrunAdı.setText(urunadTextYazi);
        txt_fiyat.setText(urunFiyatTextYazi);
        txt_UrunAdet.setText(urunadetTextYazi);
        txt_Aciklama.setText(urunAciklamaTextYazi);
        
        txt_UrunAdı.setForeground(new java.awt.Color(162, 162, 162));
        txt_fiyat.setForeground(new java.awt.Color(162, 162, 162));
        txt_fiyat.setForeground(new java.awt.Color(162, 162, 162));
        txt_UrunAdet.setForeground(new java.awt.Color(162, 162, 162));
        txt_Aciklama.setForeground(new java.awt.Color(162, 162, 162));
    }
    
    private void musterieklepanelayar(){
        txt_Ad.setText(adTextYazi);
        txt_Soyad11.setText(soyadTextYazi);
        txt_tc11.setText(tcTextYazi);
        txt_Tel11.setText(telnoTextYazi);
        txt_Mail11.setText(mailTextYazi);
        txt_Adres.setText(adresTextYazi);
        cmb_Cinsiyet.setSelectedIndex(0); 
        
        txt_Ad.setForeground(new java.awt.Color(162, 162, 162));
        txt_Soyad11.setForeground(new java.awt.Color(162, 162, 162));
        txt_Tel11.setForeground(new java.awt.Color(162, 162, 162));
        txt_tc11.setForeground(new java.awt.Color(162, 162, 162));
        txt_Mail11.setForeground(new java.awt.Color(162, 162, 162));
        txt_Adres.setForeground(new java.awt.Color(162, 162, 162));
    }
    private void tabloayarlari(JTable table) {
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setBackground(new Color(32, 136, 203));
        table.getTableHeader().setForeground(new Color(255, 255, 255));
        table.setRowHeight(25);
        table.setFillsViewportHeight(true);//Boş tabloyu beyaz olarak açtırır.
    }
       void yonetimeklepanelayar(){
        txt_Ad1.setText("");
        txt_Soyad13.setText("");
        txt_kullaniciadi.setText("");
        txt_sifre.setText("");
        txt_Mail13.setText("");
        cmb_rol.setSelectedIndex(0);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        secenekler_panel = new javax.swing.JPanel();
        yonetim_panel = new javax.swing.JPanel();
        secenekler_panel5 = new javax.swing.JPanel();
        Buton_Anasayfa5 = new javax.swing.JPanel();
        isaret6 = new javax.swing.JPanel();
        anasayfa_label2 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        Buton_musterislem1 = new javax.swing.JPanel();
        isaret9 = new javax.swing.JPanel();
        anasayfa_label4 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        buton_urunislem1 = new javax.swing.JPanel();
        isaret10 = new javax.swing.JPanel();
        anasayfa_label7 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        buton_faturaislem1 = new javax.swing.JPanel();
        isaret11 = new javax.swing.JPanel();
        anasayfa_label5 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        exit_btn1 = new javax.swing.JLabel();
        logo_yonetimpanel = new javax.swing.JLabel();
        buton_yonetimpaneli = new javax.swing.JPanel();
        isaret12 = new javax.swing.JPanel();
        anasayfa_label8 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        kasiyer_panel = new javax.swing.JPanel();
        secenekler_panel6 = new javax.swing.JPanel();
        Buton_Anasayfa6 = new javax.swing.JPanel();
        isaret13 = new javax.swing.JPanel();
        anasayfa_label = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        Buton_musterislem = new javax.swing.JPanel();
        isaret14 = new javax.swing.JPanel();
        anasayfa_label1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        buton_urunislem = new javax.swing.JPanel();
        isaret15 = new javax.swing.JPanel();
        anasayfa_label6 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        buton_faturaislem = new javax.swing.JPanel();
        isaret16 = new javax.swing.JPanel();
        anasayfa_label3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        exit_btn = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        islemler_panel = new javax.swing.JPanel();
        musterislemsecenek_panel = new javax.swing.JPanel();
        musteribilgi_panel = new javax.swing.JPanel();
        musterislemleri_label = new javax.swing.JLabel();
        musteriekle_btn = new javax.swing.JLabel();
        musterilistele_btn = new javax.swing.JLabel();
        musteriguncelle_btn = new javax.swing.JLabel();
        musterislem_panel = new javax.swing.JPanel();
        musteriguncelle_panel = new keeptoo.KGradientPanel();
        lbl_Soyad10 = new javax.swing.JLabel();
        lbl_ad10 = new javax.swing.JLabel();
        lbl_Telefon10 = new javax.swing.JLabel();
        lbl_Adres10 = new javax.swing.JLabel();
        lbl_Mail10 = new javax.swing.JLabel();
        lbl_Cinsiyet10 = new javax.swing.JLabel();
        txt_ad10 = new javax.swing.JTextField();
        txt_Tel10 = new javax.swing.JTextField();
        txt_Mail10 = new javax.swing.JTextField();
        txt_adres10 = new javax.swing.JTextField();
        txt_Soyad10 = new javax.swing.JTextField();
        cbx_Cinsiyet10 = new javax.swing.JComboBox<>();
        lbl_aratc10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txt_aratc = new javax.swing.JTextField();
        btn_Kaydet10 = new keeptoo.KButton();
        btn_Arama10 = new keeptoo.KButton();
        btn_Sil10 = new keeptoo.KButton();
        btn_Kapat10 = new keeptoo.KButton();
        lbl_tc10 = new javax.swing.JLabel();
        txt_tc10 = new javax.swing.JTextField();
        jSeparator4 = new javax.swing.JSeparator();
        lbl_araid = new javax.swing.JLabel();
        txt_araid = new javax.swing.JTextField();
        musteriekle_panel = new javax.swing.JPanel();
        kGradientPanel12 = new keeptoo.KGradientPanel();
        lbl_Soyad11 = new javax.swing.JLabel();
        lbl_Adi = new javax.swing.JLabel();
        lbl_Tel = new javax.swing.JLabel();
        lbl_adres = new javax.swing.JLabel();
        lbl_Mail11 = new javax.swing.JLabel();
        lbl_Cinsiyet11 = new javax.swing.JLabel();
        txt_Ad = new javax.swing.JTextField();
        txt_Tel11 = new javax.swing.JTextField();
        txt_Mail11 = new javax.swing.JTextField();
        txt_Adres = new javax.swing.JTextField();
        txt_Soyad11 = new javax.swing.JTextField();
        cmb_Cinsiyet = new javax.swing.JComboBox<>();
        ekle_label = new javax.swing.JLabel();
        btn_Kaydet11 = new keeptoo.KButton();
        btn_Kapat11 = new keeptoo.KButton();
        lbl_tc11 = new javax.swing.JLabel();
        txt_tc11 = new javax.swing.JTextField();
        musterilistele_panel = new javax.swing.JPanel();
        kGradientPanel7 = new keeptoo.KGradientPanel();
        musteriliste_scrollpane = new javax.swing.JScrollPane();
        tbl_musteri = new javax.swing.JTable();
        btn_Kapat12 = new keeptoo.KButton();
        musteriekle_label = new javax.swing.JLabel();
        musteriguncelle_label = new javax.swing.JLabel();
        musterilistele_label = new javax.swing.JLabel();
        musterislemyapiniz_logo = new javax.swing.JLabel();
        musterislem_separator = new javax.swing.JSeparator();
        urunislemsecenek_panel = new javax.swing.JPanel();
        bilgi_panel = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        urunislem_panel = new javax.swing.JPanel();
        urunekle_panel = new javax.swing.JPanel();
        kGradientPanel8 = new keeptoo.KGradientPanel();
        lbl_adet = new javax.swing.JLabel();
        lbl_UrunAdi = new javax.swing.JLabel();
        lbl_Aciklama = new javax.swing.JLabel();
        txt_UrunAdı = new javax.swing.JTextField();
        txt_Aciklama = new javax.swing.JTextField();
        txt_UrunAdet = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        lbl_fiyat = new javax.swing.JLabel();
        txt_fiyat = new javax.swing.JTextField();
        btn_Kaydet = new keeptoo.KButton();
        btn_Kapat = new keeptoo.KButton();
        lbl_urunkategori = new javax.swing.JLabel();
        cmb_urunkategori = new javax.swing.JComboBox<>();
        urunguncelle_panel = new javax.swing.JPanel();
        kGradientPanel9 = new keeptoo.KGradientPanel();
        lbl_adet1 = new javax.swing.JLabel();
        lbl_UrunID1 = new javax.swing.JLabel();
        lbl_UrunAdi1 = new javax.swing.JLabel();
        lbl_Aciklama1 = new javax.swing.JLabel();
        txt_UrunAdi = new javax.swing.JTextField();
        txt_Aciklama1 = new javax.swing.JTextField();
        txt_Adet = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        lbl_fiyat1 = new javax.swing.JLabel();
        txt_fiyat1 = new javax.swing.JTextField();
        lbl_urunkategori1 = new javax.swing.JLabel();
        cmb_urunkategori1 = new javax.swing.JComboBox<>();
        btn_Arama = new keeptoo.KButton();
        txt_id1 = new javax.swing.JTextField();
        btn_Kaydet1 = new keeptoo.KButton();
        btn_Sil = new keeptoo.KButton();
        btn_Kapat1 = new keeptoo.KButton();
        urunlistele_panel = new javax.swing.JPanel();
        kGradientPanel10 = new keeptoo.KGradientPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbl_urun = new javax.swing.JTable();
        btn_Kapat2 = new keeptoo.KButton();
        urunekle_btn = new javax.swing.JLabel();
        urunguncelle_btn = new javax.swing.JLabel();
        urunlistele_btn = new javax.swing.JLabel();
        lbl_uekle = new javax.swing.JLabel();
        lbl_uguncelle = new javax.swing.JLabel();
        lbl_ulistele = new javax.swing.JLabel();
        logo_urunpanel = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        anasayfa_panel = new javax.swing.JPanel();
        sistembilgi_label = new javax.swing.JLabel();
        kayitlimusteri_panel = new javax.swing.JPanel();
        isaret4 = new javax.swing.JPanel();
        kayitlimusterisayi_label = new javax.swing.JLabel();
        kayitlimusteri_icon = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        hosgeldiniz_label = new javax.swing.JLabel();
        kayitlimusteri_label = new javax.swing.JLabel();
        kayitliurun_label = new javax.swing.JLabel();
        kayitliurun_panel = new javax.swing.JPanel();
        isaret5 = new javax.swing.JPanel();
        kayitliurunsayi_label = new javax.swing.JLabel();
        kayitliurun_icon = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        kayitlifatura_label = new javax.swing.JLabel();
        kayitlifatura_panel = new javax.swing.JPanel();
        isaret7 = new javax.swing.JPanel();
        kayitlifatura_icon = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        kayitlifaturasayi_label = new javax.swing.JLabel();
        kayitliyonetici_label = new javax.swing.JLabel();
        kayitliyonetici_panel = new javax.swing.JPanel();
        isaret8 = new javax.swing.JPanel();
        kayitlikullanıcı_icon = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        kayitlikullanicisayi_label = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        info_icon = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        kullanicisim_label = new javax.swing.JLabel();
        panel_yonetim = new javax.swing.JPanel();
        yonetimbilgi_panel = new javax.swing.JPanel();
        yoneticislemleri_label = new javax.swing.JLabel();
        yoneticikasiyerekle_btn = new javax.swing.JLabel();
        yoneticikasiyerguncelle_btn = new javax.swing.JLabel();
        yonetimislem_panel = new javax.swing.JPanel();
        yoneticikasiyerguncelle_panel = new keeptoo.KGradientPanel();
        jLabel14 = new javax.swing.JLabel();
        btn_Guncelle12 = new keeptoo.KButton();
        btn_Arama11 = new keeptoo.KButton();
        btn_Sil11 = new keeptoo.KButton();
        btn_Kapat13 = new keeptoo.KButton();
        jSeparator8 = new javax.swing.JSeparator();
        lbl_araid1 = new javax.swing.JLabel();
        txt_araid1 = new javax.swing.JTextField();
        lbl_Adi2 = new javax.swing.JLabel();
        txt_Ad2 = new javax.swing.JTextField();
        lbl_Soyad14 = new javax.swing.JLabel();
        txt_Soyad14 = new javax.swing.JTextField();
        lbl_kullaniciadi1 = new javax.swing.JLabel();
        txt_kullaniciadi1 = new javax.swing.JTextField();
        lbl_sifre1 = new javax.swing.JLabel();
        txt_sifre1 = new javax.swing.JTextField();
        lbl_Mail14 = new javax.swing.JLabel();
        txt_Mail14 = new javax.swing.JTextField();
        lbl_rol1 = new javax.swing.JLabel();
        cmb_rol1 = new javax.swing.JComboBox<>();
        yoneticikasiyerekle_panel = new javax.swing.JPanel();
        kGradientPanel13 = new keeptoo.KGradientPanel();
        lbl_Soyad13 = new javax.swing.JLabel();
        lbl_Adi1 = new javax.swing.JLabel();
        lbl_sifre = new javax.swing.JLabel();
        lbl_Mail13 = new javax.swing.JLabel();
        lbl_rol = new javax.swing.JLabel();
        txt_Ad1 = new javax.swing.JTextField();
        txt_sifre = new javax.swing.JTextField();
        txt_Mail13 = new javax.swing.JTextField();
        txt_Soyad13 = new javax.swing.JTextField();
        cmb_rol = new javax.swing.JComboBox<>();
        ekle_label1 = new javax.swing.JLabel();
        btn_Kaydet13 = new keeptoo.KButton();
        btn_Kapat14 = new keeptoo.KButton();
        lbl_kullaniciadi = new javax.swing.JLabel();
        txt_kullaniciadi = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        yoneticikasiyer_table = new javax.swing.JTable();
        ekle_label2 = new javax.swing.JLabel();
        yonetici_label = new javax.swing.JLabel();
        yoneticiguncelle_label = new javax.swing.JLabel();
        yonetimislem_separator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        secenekler_panel.setBackground(new java.awt.Color(255, 255, 255));
        secenekler_panel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                secenekler_panelMouseDragged(evt);
            }
        });
        secenekler_panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                secenekler_panelMousePressed(evt);
            }
        });
        secenekler_panel.setLayout(new java.awt.CardLayout());

        secenekler_panel5.setBackground(new java.awt.Color(255, 255, 255));
        secenekler_panel5.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                secenekler_panel5MouseDragged(evt);
            }
        });
        secenekler_panel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                secenekler_panel5MousePressed(evt);
            }
        });

        Buton_Anasayfa5.setBackground(new java.awt.Color(255, 255, 255));
        Buton_Anasayfa5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 255)));
        Buton_Anasayfa5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Buton_Anasayfa5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Buton_Anasayfa5MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                Buton_Anasayfa5MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Buton_Anasayfa5MousePressed(evt);
            }
        });

        isaret6.setBackground(new java.awt.Color(153, 153, 255));

        javax.swing.GroupLayout isaret6Layout = new javax.swing.GroupLayout(isaret6);
        isaret6.setLayout(isaret6Layout);
        isaret6Layout.setHorizontalGroup(
            isaret6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        isaret6Layout.setVerticalGroup(
            isaret6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        anasayfa_label2.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        anasayfa_label2.setForeground(new java.awt.Color(153, 153, 255));
        anasayfa_label2.setText("Anasayfa");

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/anasayfaicon.png"))); // NOI18N

        javax.swing.GroupLayout Buton_Anasayfa5Layout = new javax.swing.GroupLayout(Buton_Anasayfa5);
        Buton_Anasayfa5.setLayout(Buton_Anasayfa5Layout);
        Buton_Anasayfa5Layout.setHorizontalGroup(
            Buton_Anasayfa5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Buton_Anasayfa5Layout.createSequentialGroup()
                .addComponent(isaret6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(anasayfa_label2, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 219, Short.MAX_VALUE))
        );
        Buton_Anasayfa5Layout.setVerticalGroup(
            Buton_Anasayfa5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(isaret6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Buton_Anasayfa5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Buton_Anasayfa5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(anasayfa_label2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Buton_Anasayfa5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15)
                        .addGap(14, 14, 14)))
                .addContainerGap())
        );

        Buton_musterislem1.setBackground(new java.awt.Color(255, 255, 255));
        Buton_musterislem1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 255)));
        Buton_musterislem1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Buton_musterislem1.setPreferredSize(new java.awt.Dimension(372, 77));
        Buton_musterislem1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Buton_musterislem1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                Buton_musterislem1MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Buton_musterislem1MousePressed(evt);
            }
        });

        isaret9.setBackground(new java.awt.Color(153, 153, 255));

        javax.swing.GroupLayout isaret9Layout = new javax.swing.GroupLayout(isaret9);
        isaret9.setLayout(isaret9Layout);
        isaret9Layout.setHorizontalGroup(
            isaret9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        isaret9Layout.setVerticalGroup(
            isaret9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        anasayfa_label4.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        anasayfa_label4.setForeground(new java.awt.Color(153, 153, 255));
        anasayfa_label4.setText("Müşteri İşlemleri");

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/musterislemicon.png"))); // NOI18N

        javax.swing.GroupLayout Buton_musterislem1Layout = new javax.swing.GroupLayout(Buton_musterislem1);
        Buton_musterislem1.setLayout(Buton_musterislem1Layout);
        Buton_musterislem1Layout.setHorizontalGroup(
            Buton_musterislem1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Buton_musterislem1Layout.createSequentialGroup()
                .addComponent(isaret9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(anasayfa_label4)
                .addGap(0, 181, Short.MAX_VALUE))
        );
        Buton_musterislem1Layout.setVerticalGroup(
            Buton_musterislem1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(isaret9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Buton_musterislem1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(anasayfa_label4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Buton_musterislem1Layout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addComponent(jLabel20)
                .addGap(23, 23, 23))
        );

        buton_urunislem1.setBackground(new java.awt.Color(255, 255, 255));
        buton_urunislem1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 255)));
        buton_urunislem1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buton_urunislem1.setPreferredSize(new java.awt.Dimension(372, 77));
        buton_urunislem1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buton_urunislem1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                buton_urunislem1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                buton_urunislem1MouseExited(evt);
            }
        });

        isaret10.setBackground(new java.awt.Color(153, 153, 255));

        javax.swing.GroupLayout isaret10Layout = new javax.swing.GroupLayout(isaret10);
        isaret10.setLayout(isaret10Layout);
        isaret10Layout.setHorizontalGroup(
            isaret10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        isaret10Layout.setVerticalGroup(
            isaret10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        anasayfa_label7.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        anasayfa_label7.setForeground(new java.awt.Color(153, 153, 255));
        anasayfa_label7.setText("Ürün İşlemleri");

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/urunislemicon.png"))); // NOI18N

        javax.swing.GroupLayout buton_urunislem1Layout = new javax.swing.GroupLayout(buton_urunislem1);
        buton_urunislem1.setLayout(buton_urunislem1Layout);
        buton_urunislem1Layout.setHorizontalGroup(
            buton_urunislem1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buton_urunislem1Layout.createSequentialGroup()
                .addComponent(isaret10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(anasayfa_label7, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 176, Short.MAX_VALUE))
        );
        buton_urunislem1Layout.setVerticalGroup(
            buton_urunislem1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(isaret10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(buton_urunislem1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(anasayfa_label7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(buton_urunislem1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel21)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        buton_faturaislem1.setBackground(new java.awt.Color(255, 255, 255));
        buton_faturaislem1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 255)));
        buton_faturaislem1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buton_faturaislem1.setPreferredSize(new java.awt.Dimension(372, 77));
        buton_faturaislem1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                buton_faturaislem1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                buton_faturaislem1MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buton_faturaislem1MousePressed(evt);
            }
        });

        isaret11.setBackground(new java.awt.Color(153, 153, 255));

        javax.swing.GroupLayout isaret11Layout = new javax.swing.GroupLayout(isaret11);
        isaret11.setLayout(isaret11Layout);
        isaret11Layout.setHorizontalGroup(
            isaret11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        isaret11Layout.setVerticalGroup(
            isaret11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        anasayfa_label5.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        anasayfa_label5.setForeground(new java.awt.Color(153, 153, 255));
        anasayfa_label5.setText("Fatura Oluştur");

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/faturaislemicon.png"))); // NOI18N

        javax.swing.GroupLayout buton_faturaislem1Layout = new javax.swing.GroupLayout(buton_faturaislem1);
        buton_faturaislem1.setLayout(buton_faturaislem1Layout);
        buton_faturaislem1Layout.setHorizontalGroup(
            buton_faturaislem1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buton_faturaislem1Layout.createSequentialGroup()
                .addComponent(isaret11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(anasayfa_label5, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 171, Short.MAX_VALUE))
        );
        buton_faturaislem1Layout.setVerticalGroup(
            buton_faturaislem1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(isaret11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(buton_faturaislem1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(anasayfa_label5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(buton_faturaislem1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel22)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(96, 83, 150));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Yönetici Kontrol Paneli");

        exit_btn1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/cikis_icon.png"))); // NOI18N
        exit_btn1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        exit_btn1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exit_btn1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addComponent(jLabel23)
                .addGap(70, 70, 70)
                .addComponent(exit_btn1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel23))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(exit_btn1))
        );

        logo_yonetimpanel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/anaekranlogo.png"))); // NOI18N
        logo_yonetimpanel.setToolTipText("");
        logo_yonetimpanel.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 0, 2, 0, new java.awt.Color(96, 83, 150)));

        buton_yonetimpaneli.setBackground(new java.awt.Color(255, 255, 255));
        buton_yonetimpaneli.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 255)));
        buton_yonetimpaneli.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buton_yonetimpaneli.setPreferredSize(new java.awt.Dimension(372, 77));
        buton_yonetimpaneli.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                buton_yonetimpaneliMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                buton_yonetimpaneliMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buton_yonetimpaneliMousePressed(evt);
            }
        });

        isaret12.setBackground(new java.awt.Color(153, 153, 255));

        javax.swing.GroupLayout isaret12Layout = new javax.swing.GroupLayout(isaret12);
        isaret12.setLayout(isaret12Layout);
        isaret12Layout.setHorizontalGroup(
            isaret12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        isaret12Layout.setVerticalGroup(
            isaret12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        anasayfa_label8.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        anasayfa_label8.setForeground(new java.awt.Color(153, 153, 255));
        anasayfa_label8.setText("Yönetim Paneli");

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_administrative_tools_25px.png"))); // NOI18N

        javax.swing.GroupLayout buton_yonetimpaneliLayout = new javax.swing.GroupLayout(buton_yonetimpaneli);
        buton_yonetimpaneli.setLayout(buton_yonetimpaneliLayout);
        buton_yonetimpaneliLayout.setHorizontalGroup(
            buton_yonetimpaneliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buton_yonetimpaneliLayout.createSequentialGroup()
                .addComponent(isaret12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(anasayfa_label8, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 171, Short.MAX_VALUE))
        );
        buton_yonetimpaneliLayout.setVerticalGroup(
            buton_yonetimpaneliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(isaret12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(buton_yonetimpaneliLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(anasayfa_label8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(buton_yonetimpaneliLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel25)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout secenekler_panel5Layout = new javax.swing.GroupLayout(secenekler_panel5);
        secenekler_panel5.setLayout(secenekler_panel5Layout);
        secenekler_panel5Layout.setHorizontalGroup(
            secenekler_panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(secenekler_panel5Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(logo_yonetimpanel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(secenekler_panel5Layout.createSequentialGroup()
                .addGroup(secenekler_panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Buton_Anasayfa5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Buton_musterislem1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buton_urunislem1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buton_faturaislem1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buton_yonetimpaneli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        secenekler_panel5Layout.setVerticalGroup(
            secenekler_panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(secenekler_panel5Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(logo_yonetimpanel)
                .addGap(17, 17, 17)
                .addComponent(Buton_Anasayfa5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(Buton_musterislem1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(buton_urunislem1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(buton_faturaislem1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(buton_yonetimpaneli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout yonetim_panelLayout = new javax.swing.GroupLayout(yonetim_panel);
        yonetim_panel.setLayout(yonetim_panelLayout);
        yonetim_panelLayout.setHorizontalGroup(
            yonetim_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(yonetim_panelLayout.createSequentialGroup()
                .addComponent(secenekler_panel5, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        yonetim_panelLayout.setVerticalGroup(
            yonetim_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(secenekler_panel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        secenekler_panel.add(yonetim_panel, "card8");

        secenekler_panel6.setBackground(new java.awt.Color(255, 255, 255));
        secenekler_panel6.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                secenekler_panel6MouseDragged(evt);
            }
        });
        secenekler_panel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                secenekler_panel6MousePressed(evt);
            }
        });

        Buton_Anasayfa6.setBackground(new java.awt.Color(255, 255, 255));
        Buton_Anasayfa6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 255)));
        Buton_Anasayfa6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Buton_Anasayfa6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Buton_Anasayfa6MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                Buton_Anasayfa6MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Buton_Anasayfa6MousePressed(evt);
            }
        });

        isaret13.setBackground(new java.awt.Color(153, 153, 255));

        javax.swing.GroupLayout isaret13Layout = new javax.swing.GroupLayout(isaret13);
        isaret13.setLayout(isaret13Layout);
        isaret13Layout.setHorizontalGroup(
            isaret13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        isaret13Layout.setVerticalGroup(
            isaret13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        anasayfa_label.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        anasayfa_label.setForeground(new java.awt.Color(153, 153, 255));
        anasayfa_label.setText("Anasayfa");

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/anasayfaicon.png"))); // NOI18N

        javax.swing.GroupLayout Buton_Anasayfa6Layout = new javax.swing.GroupLayout(Buton_Anasayfa6);
        Buton_Anasayfa6.setLayout(Buton_Anasayfa6Layout);
        Buton_Anasayfa6Layout.setHorizontalGroup(
            Buton_Anasayfa6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Buton_Anasayfa6Layout.createSequentialGroup()
                .addComponent(isaret13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(anasayfa_label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 219, Short.MAX_VALUE))
        );
        Buton_Anasayfa6Layout.setVerticalGroup(
            Buton_Anasayfa6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(isaret13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Buton_Anasayfa6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(Buton_Anasayfa6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4)
                    .addComponent(anasayfa_label, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );

        Buton_musterislem.setBackground(new java.awt.Color(255, 255, 255));
        Buton_musterislem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 255)));
        Buton_musterislem.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Buton_musterislem.setPreferredSize(new java.awt.Dimension(372, 77));
        Buton_musterislem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Buton_musterislemMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                Buton_musterislemMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Buton_musterislemMousePressed(evt);
            }
        });

        isaret14.setBackground(new java.awt.Color(153, 153, 255));

        javax.swing.GroupLayout isaret14Layout = new javax.swing.GroupLayout(isaret14);
        isaret14.setLayout(isaret14Layout);
        isaret14Layout.setHorizontalGroup(
            isaret14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        isaret14Layout.setVerticalGroup(
            isaret14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        anasayfa_label1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        anasayfa_label1.setForeground(new java.awt.Color(153, 153, 255));
        anasayfa_label1.setText("Müşteri İşlemleri");

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/musterislemicon.png"))); // NOI18N

        javax.swing.GroupLayout Buton_musterislemLayout = new javax.swing.GroupLayout(Buton_musterislem);
        Buton_musterislem.setLayout(Buton_musterislemLayout);
        Buton_musterislemLayout.setHorizontalGroup(
            Buton_musterislemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Buton_musterislemLayout.createSequentialGroup()
                .addComponent(isaret14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(anasayfa_label1)
                .addGap(0, 181, Short.MAX_VALUE))
        );
        Buton_musterislemLayout.setVerticalGroup(
            Buton_musterislemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(isaret14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Buton_musterislemLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(anasayfa_label1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Buton_musterislemLayout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(23, 23, 23))
        );

        buton_urunislem.setBackground(new java.awt.Color(255, 255, 255));
        buton_urunislem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 255)));
        buton_urunislem.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buton_urunislem.setPreferredSize(new java.awt.Dimension(372, 77));
        buton_urunislem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buton_urunislemMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                buton_urunislemMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                buton_urunislemMouseExited(evt);
            }
        });

        isaret15.setBackground(new java.awt.Color(153, 153, 255));

        javax.swing.GroupLayout isaret15Layout = new javax.swing.GroupLayout(isaret15);
        isaret15.setLayout(isaret15Layout);
        isaret15Layout.setHorizontalGroup(
            isaret15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        isaret15Layout.setVerticalGroup(
            isaret15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        anasayfa_label6.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        anasayfa_label6.setForeground(new java.awt.Color(153, 153, 255));
        anasayfa_label6.setText("Ürün İşlemleri");

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/urunislemicon.png"))); // NOI18N

        javax.swing.GroupLayout buton_urunislemLayout = new javax.swing.GroupLayout(buton_urunislem);
        buton_urunislem.setLayout(buton_urunislemLayout);
        buton_urunislemLayout.setHorizontalGroup(
            buton_urunislemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buton_urunislemLayout.createSequentialGroup()
                .addComponent(isaret15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(anasayfa_label6, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 176, Short.MAX_VALUE))
        );
        buton_urunislemLayout.setVerticalGroup(
            buton_urunislemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(isaret15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(buton_urunislemLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(anasayfa_label6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(buton_urunislemLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel6)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        buton_faturaislem.setBackground(new java.awt.Color(255, 255, 255));
        buton_faturaislem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 255)));
        buton_faturaislem.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buton_faturaislem.setPreferredSize(new java.awt.Dimension(372, 77));
        buton_faturaislem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buton_faturaislemMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                buton_faturaislemMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                buton_faturaislemMouseExited(evt);
            }
        });

        isaret16.setBackground(new java.awt.Color(153, 153, 255));

        javax.swing.GroupLayout isaret16Layout = new javax.swing.GroupLayout(isaret16);
        isaret16.setLayout(isaret16Layout);
        isaret16Layout.setHorizontalGroup(
            isaret16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        isaret16Layout.setVerticalGroup(
            isaret16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        anasayfa_label3.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        anasayfa_label3.setForeground(new java.awt.Color(153, 153, 255));
        anasayfa_label3.setText("Fatura Oluştur");

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/faturaislemicon.png"))); // NOI18N

        javax.swing.GroupLayout buton_faturaislemLayout = new javax.swing.GroupLayout(buton_faturaislem);
        buton_faturaislem.setLayout(buton_faturaislemLayout);
        buton_faturaislemLayout.setHorizontalGroup(
            buton_faturaislemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buton_faturaislemLayout.createSequentialGroup()
                .addComponent(isaret16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(anasayfa_label3, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 171, Short.MAX_VALUE))
        );
        buton_faturaislemLayout.setVerticalGroup(
            buton_faturaislemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(isaret16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(buton_faturaislemLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(anasayfa_label3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(buton_faturaislemLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel7)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(96, 83, 150));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Kasiyer Kontrol Paneli");

        exit_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/cikis_icon.png"))); // NOI18N
        exit_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        exit_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exit_btnMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addComponent(jLabel19)
                .addGap(70, 70, 70)
                .addComponent(exit_btn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel19))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(exit_btn))
        );

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/anaekranlogo.png"))); // NOI18N
        jLabel3.setToolTipText("");
        jLabel3.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 0, 2, 0, new java.awt.Color(96, 83, 150)));

        javax.swing.GroupLayout secenekler_panel6Layout = new javax.swing.GroupLayout(secenekler_panel6);
        secenekler_panel6.setLayout(secenekler_panel6Layout);
        secenekler_panel6Layout.setHorizontalGroup(
            secenekler_panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(secenekler_panel6Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(jLabel3))
            .addComponent(Buton_Anasayfa6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(Buton_musterislem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(buton_urunislem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(buton_faturaislem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        secenekler_panel6Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {Buton_Anasayfa6, Buton_musterislem, buton_faturaislem, buton_urunislem});

        secenekler_panel6Layout.setVerticalGroup(
            secenekler_panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(secenekler_panel6Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jLabel3)
                .addGap(17, 17, 17)
                .addComponent(Buton_Anasayfa6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(Buton_musterislem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(buton_urunislem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(buton_faturaislem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        secenekler_panel6Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {Buton_Anasayfa6, Buton_musterislem, buton_faturaislem, buton_urunislem});

        javax.swing.GroupLayout kasiyer_panelLayout = new javax.swing.GroupLayout(kasiyer_panel);
        kasiyer_panel.setLayout(kasiyer_panelLayout);
        kasiyer_panelLayout.setHorizontalGroup(
            kasiyer_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kasiyer_panelLayout.createSequentialGroup()
                .addComponent(secenekler_panel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        kasiyer_panelLayout.setVerticalGroup(
            kasiyer_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(secenekler_panel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        secenekler_panel.add(kasiyer_panel, "card3");

        islemler_panel.setBackground(new java.awt.Color(240, 240, 241));
        islemler_panel.setLayout(new java.awt.CardLayout());

        musterislemsecenek_panel.setBackground(new java.awt.Color(235, 255, 255));
        musterislemsecenek_panel.setPreferredSize(new java.awt.Dimension(850, 723));
        musterislemsecenek_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        musteribilgi_panel.setBackground(new java.awt.Color(96, 83, 150));
        musteribilgi_panel.setPreferredSize(new java.awt.Dimension(850, 39));

        musterislemleri_label.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        musterislemleri_label.setForeground(new java.awt.Color(255, 255, 255));
        musterislemleri_label.setText("Müşteri İşlemleri");

        javax.swing.GroupLayout musteribilgi_panelLayout = new javax.swing.GroupLayout(musteribilgi_panel);
        musteribilgi_panel.setLayout(musteribilgi_panelLayout);
        musteribilgi_panelLayout.setHorizontalGroup(
            musteribilgi_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, musteribilgi_panelLayout.createSequentialGroup()
                .addContainerGap(395, Short.MAX_VALUE)
                .addComponent(musterislemleri_label)
                .addGap(347, 347, 347))
        );
        musteribilgi_panelLayout.setVerticalGroup(
            musteribilgi_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, musteribilgi_panelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(musterislemleri_label))
        );

        musterislemsecenek_panel.add(musteribilgi_panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 860, 42));

        musteriekle_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_add_user_group_man_woman_80px.png"))); // NOI18N
        musteriekle_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        musteriekle_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                musteriekle_btnMouseClicked(evt);
            }
        });
        musterislemsecenek_panel.add(musteriekle_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(102, 57, -1, -1));

        musterilistele_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_business_building_80px.png"))); // NOI18N
        musterilistele_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        musterilistele_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                musterilistele_btnMouseClicked(evt);
            }
        });
        musterislemsecenek_panel.add(musterilistele_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 57, -1, -1));

        musteriguncelle_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_change_employee_male_80px.png"))); // NOI18N
        musteriguncelle_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        musteriguncelle_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                musteriguncelle_btnMouseClicked(evt);
            }
        });
        musterislemsecenek_panel.add(musteriguncelle_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 57, -1, -1));

        musterislem_panel.setLayout(new java.awt.CardLayout());

        musteriguncelle_panel.setkEndColor(new java.awt.Color(153, 255, 204));
        musteriguncelle_panel.setkGradientFocus(200);
        musteriguncelle_panel.setkStartColor(new java.awt.Color(204, 204, 255));

        lbl_Soyad10.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_Soyad10.setText("Soyad");

        lbl_ad10.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_ad10.setText("Ad");

        lbl_Telefon10.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_Telefon10.setText("Telefon No");

        lbl_Adres10.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_Adres10.setText("Adres (İl-İlçe)");

        lbl_Mail10.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_Mail10.setText("E-Mail");

        lbl_Cinsiyet10.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_Cinsiyet10.setText("Cinsiyet");

        txt_ad10.setBackground(new Color(0,0,0,0)
        );
        txt_ad10.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_ad10.setForeground(new java.awt.Color(162, 162, 162));
        txt_ad10.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_ad10.setMinimumSize(new java.awt.Dimension(0, 30));
        txt_ad10.setOpaque(false);
        txt_ad10.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_ad10FocusGained(evt);
            }
        });

        txt_Tel10.setBackground(new Color(0,0,0,0)
        );
        txt_Tel10.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_Tel10.setForeground(new java.awt.Color(162, 162, 162));
        txt_Tel10.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_Tel10.setMinimumSize(new java.awt.Dimension(0, 30));
        txt_Tel10.setOpaque(false);
        txt_Tel10.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_Tel10FocusGained(evt);
            }
        });

        txt_Mail10.setBackground(new Color(0,0,0,0)
        );
        txt_Mail10.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_Mail10.setForeground(new java.awt.Color(162, 162, 162));
        txt_Mail10.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_Mail10.setMinimumSize(new java.awt.Dimension(0, 30));
        txt_Mail10.setOpaque(false);
        txt_Mail10.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_Mail10FocusGained(evt);
            }
        });

        txt_adres10.setBackground(new Color(0,0,0,0)
        );
        txt_adres10.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_adres10.setForeground(new java.awt.Color(162, 162, 162));
        txt_adres10.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_adres10.setMinimumSize(new java.awt.Dimension(0, 30));
        txt_adres10.setOpaque(false);
        txt_adres10.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_adres10FocusGained(evt);
            }
        });

        txt_Soyad10.setBackground(new Color(0,0,0,0)
        );
        txt_Soyad10.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_Soyad10.setForeground(new java.awt.Color(162, 162, 162));
        txt_Soyad10.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_Soyad10.setMinimumSize(new java.awt.Dimension(0, 30));
        txt_Soyad10.setOpaque(false);
        txt_Soyad10.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_Soyad10FocusGained(evt);
            }
        });

        cbx_Cinsiyet10.setBackground(new Color(0,0,0,0));
        cbx_Cinsiyet10.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        cbx_Cinsiyet10.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seçiniz", "Erkek", "Kız" }));
        cbx_Cinsiyet10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));

        lbl_aratc10.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_aratc10.setText("TC-No");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel9.setText("Müşteri Güncelle");

        txt_aratc.setBackground(new Color(0,0,0,0));
        txt_aratc.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        txt_aratc.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txt_aratc.setText("         ");
        txt_aratc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(96, 83, 150), 2));
        txt_aratc.setOpaque(false);

        btn_Kaydet10.setText("Güncelle");
        btn_Kaydet10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_Kaydet10.setkBorderRadius(15);
        btn_Kaydet10.setkEndColor(new java.awt.Color(153, 153, 255));
        btn_Kaydet10.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_Kaydet10.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_Kaydet10.setkHoverStartColor(new java.awt.Color(0, 255, 51));
        btn_Kaydet10.setkStartColor(new java.awt.Color(0, 204, 102));
        btn_Kaydet10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Kaydet10ActionPerformed(evt);
            }
        });

        btn_Arama10.setText("Arama");
        btn_Arama10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_Arama10.setkBorderRadius(15);
        btn_Arama10.setkEndColor(new java.awt.Color(153, 153, 255));
        btn_Arama10.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_Arama10.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_Arama10.setkHoverStartColor(new java.awt.Color(153, 153, 255));
        btn_Arama10.setkStartColor(new java.awt.Color(0, 204, 204));
        btn_Arama10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Arama10ActionPerformed(evt);
            }
        });

        btn_Sil10.setText("Müşteri Sil");
        btn_Sil10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_Sil10.setkBorderRadius(15);
        btn_Sil10.setkEndColor(new java.awt.Color(255, 102, 102));
        btn_Sil10.setkHoverEndColor(new java.awt.Color(255, 102, 102));
        btn_Sil10.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_Sil10.setkHoverStartColor(new java.awt.Color(255, 0, 0));
        btn_Sil10.setkStartColor(new java.awt.Color(255, 102, 102));
        btn_Sil10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Sil10ActionPerformed(evt);
            }
        });

        btn_Kapat10.setText("Kapat");
        btn_Kapat10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_Kapat10.setkBorderRadius(15);
        btn_Kapat10.setkEndColor(new java.awt.Color(153, 153, 153));
        btn_Kapat10.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_Kapat10.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_Kapat10.setkHoverStartColor(new java.awt.Color(102, 102, 102));
        btn_Kapat10.setkStartColor(new java.awt.Color(102, 102, 102));
        btn_Kapat10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Kapat10ActionPerformed(evt);
            }
        });

        lbl_tc10.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_tc10.setText("TC-No");

        txt_tc10.setBackground(new Color(0,0,0,0)
        );
        txt_tc10.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_tc10.setForeground(new java.awt.Color(162, 162, 162));
        txt_tc10.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_tc10.setMinimumSize(new java.awt.Dimension(0, 30));
        txt_tc10.setOpaque(false);
        txt_tc10.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_tc10FocusGained(evt);
            }
        });

        lbl_araid.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_araid.setText("ID");

        txt_araid.setBackground(new Color(0,0,0,0));
        txt_araid.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        txt_araid.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_araid.setText("         ");
        txt_araid.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(96, 83, 150), 2));
        txt_araid.setOpaque(false);

        javax.swing.GroupLayout musteriguncelle_panelLayout = new javax.swing.GroupLayout(musteriguncelle_panel);
        musteriguncelle_panel.setLayout(musteriguncelle_panelLayout);
        musteriguncelle_panelLayout.setHorizontalGroup(
            musteriguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(musteriguncelle_panelLayout.createSequentialGroup()
                .addGroup(musteriguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(musteriguncelle_panelLayout.createSequentialGroup()
                        .addGap(405, 405, 405)
                        .addComponent(jLabel9))
                    .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 860, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, musteriguncelle_panelLayout.createSequentialGroup()
                .addGap(200, 200, 200)
                .addComponent(btn_Kaydet10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(105, 105, 105)
                .addComponent(btn_Sil10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(105, 105, 105)
                .addComponent(btn_Kapat10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(166, 166, 166))
            .addGroup(musteriguncelle_panelLayout.createSequentialGroup()
                .addGroup(musteriguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(musteriguncelle_panelLayout.createSequentialGroup()
                        .addGap(85, 85, 85)
                        .addComponent(lbl_aratc10)
                        .addGap(31, 31, 31)
                        .addComponent(txt_aratc, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(108, 108, 108)
                        .addComponent(btn_Arama10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(116, 116, 116)
                        .addComponent(lbl_araid)
                        .addGap(32, 32, 32)
                        .addComponent(txt_araid, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(musteriguncelle_panelLayout.createSequentialGroup()
                        .addGap(210, 210, 210)
                        .addGroup(musteriguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(musteriguncelle_panelLayout.createSequentialGroup()
                                .addComponent(lbl_ad10)
                                .addGap(170, 170, 170)
                                .addComponent(txt_ad10, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(musteriguncelle_panelLayout.createSequentialGroup()
                                .addComponent(lbl_Soyad10)
                                .addGap(144, 144, 144)
                                .addComponent(txt_Soyad10, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(musteriguncelle_panelLayout.createSequentialGroup()
                                .addComponent(lbl_tc10)
                                .addGap(144, 144, 144)
                                .addComponent(txt_tc10, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(musteriguncelle_panelLayout.createSequentialGroup()
                                .addComponent(lbl_Telefon10)
                                .addGap(106, 106, 106)
                                .addComponent(txt_Tel10, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(musteriguncelle_panelLayout.createSequentialGroup()
                                .addComponent(lbl_Mail10)
                                .addGap(142, 142, 142)
                                .addComponent(txt_Mail10, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(musteriguncelle_panelLayout.createSequentialGroup()
                                .addComponent(lbl_Adres10)
                                .addGap(89, 89, 89)
                                .addComponent(txt_adres10, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(musteriguncelle_panelLayout.createSequentialGroup()
                                .addComponent(lbl_Cinsiyet10)
                                .addGap(130, 130, 130)
                                .addComponent(cbx_Cinsiyet10, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        musteriguncelle_panelLayout.setVerticalGroup(
            musteriguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(musteriguncelle_panelLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jLabel9)
                .addGap(14, 14, 14)
                .addGroup(musteriguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(musteriguncelle_panelLayout.createSequentialGroup()
                        .addGroup(musteriguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_Arama10, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(musteriguncelle_panelLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(lbl_araid))
                            .addComponent(txt_araid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(13, 13, 13)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(musteriguncelle_panelLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(lbl_aratc10))
                    .addComponent(txt_aratc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(musteriguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_ad10)
                    .addGroup(musteriguncelle_panelLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(txt_ad10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28)
                .addGroup(musteriguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_Soyad10)
                    .addGroup(musteriguncelle_panelLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(txt_Soyad10, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28)
                .addGroup(musteriguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_tc10)
                    .addGroup(musteriguncelle_panelLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(txt_tc10, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28)
                .addGroup(musteriguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_Telefon10)
                    .addComponent(txt_Tel10, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(musteriguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_Mail10)
                    .addComponent(txt_Mail10, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(musteriguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_Adres10)
                    .addComponent(txt_adres10, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(musteriguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(musteriguncelle_panelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lbl_Cinsiyet10))
                    .addComponent(cbx_Cinsiyet10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(musteriguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Kapat10, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Sil10, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Kaydet10, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        musterislem_panel.add(musteriguncelle_panel, "card2");

        kGradientPanel12.setkEndColor(new java.awt.Color(153, 255, 204));
        kGradientPanel12.setkGradientFocus(200);
        kGradientPanel12.setkStartColor(new java.awt.Color(204, 204, 255));

        lbl_Soyad11.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_Soyad11.setText("Soyad");

        lbl_Adi.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_Adi.setText("Ad");

        lbl_Tel.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_Tel.setText("Telefon No");

        lbl_adres.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_adres.setText("Adres (İl-İlçe)");

        lbl_Mail11.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_Mail11.setText("E-Mail");

        lbl_Cinsiyet11.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_Cinsiyet11.setText("Cinsiyet");

        txt_Ad.setBackground(new Color(0,0,0,0));
        txt_Ad.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_Ad.setForeground(new java.awt.Color(162, 162, 162));
        txt_Ad.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_Ad.setOpaque(false);
        txt_Ad.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_AdFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_AdFocusLost(evt);
            }
        });

        txt_Tel11.setBackground(new Color(0,0,0,0));
        txt_Tel11.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_Tel11.setForeground(new java.awt.Color(162, 162, 162));
        txt_Tel11.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_Tel11.setOpaque(false);
        txt_Tel11.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_Tel11FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_Tel11FocusLost(evt);
            }
        });

        txt_Mail11.setBackground(new Color(0,0,0,0));
        txt_Mail11.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_Mail11.setForeground(new java.awt.Color(162, 162, 162));
        txt_Mail11.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_Mail11.setOpaque(false);
        txt_Mail11.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_Mail11FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_Mail11FocusLost(evt);
            }
        });

        txt_Adres.setBackground(new Color(0,0,0,0));
        txt_Adres.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_Adres.setForeground(new java.awt.Color(162, 162, 162));
        txt_Adres.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_Adres.setOpaque(false);
        txt_Adres.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_AdresFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_AdresFocusLost(evt);
            }
        });

        txt_Soyad11.setBackground(new Color(0,0,0,0));
        txt_Soyad11.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_Soyad11.setForeground(new java.awt.Color(162, 162, 162));
        txt_Soyad11.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_Soyad11.setOpaque(false);
        txt_Soyad11.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_Soyad11FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_Soyad11FocusLost(evt);
            }
        });

        cmb_Cinsiyet.setBackground(new Color(0,0,0,0));
        cmb_Cinsiyet.setFont(new java.awt.Font("Segoe UI Semibold", 0, 15)); // NOI18N
        cmb_Cinsiyet.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seçiniz", "Erkek", "Kız" }));
        cmb_Cinsiyet.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));
        cmb_Cinsiyet.setOpaque(false);

        ekle_label.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        ekle_label.setText("Müşteri Ekle");

        btn_Kaydet11.setText("Kaydet");
        btn_Kaydet11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_Kaydet11.setkBorderRadius(15);
        btn_Kaydet11.setkEndColor(new java.awt.Color(153, 153, 255));
        btn_Kaydet11.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_Kaydet11.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_Kaydet11.setkHoverStartColor(new java.awt.Color(0, 255, 51));
        btn_Kaydet11.setkStartColor(new java.awt.Color(0, 204, 102));
        btn_Kaydet11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Kaydet11ActionPerformed(evt);
            }
        });

        btn_Kapat11.setText("Kapat");
        btn_Kapat11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_Kapat11.setkBorderRadius(15);
        btn_Kapat11.setkEndColor(new java.awt.Color(153, 153, 153));
        btn_Kapat11.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_Kapat11.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_Kapat11.setkHoverStartColor(new java.awt.Color(102, 102, 102));
        btn_Kapat11.setkStartColor(new java.awt.Color(102, 102, 102));
        btn_Kapat11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Kapat11ActionPerformed(evt);
            }
        });

        lbl_tc11.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_tc11.setText("T.C. No");

        txt_tc11.setBackground(new Color(0,0,0,0));
        txt_tc11.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_tc11.setForeground(new java.awt.Color(162, 162, 162));
        txt_tc11.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_tc11.setOpaque(false);
        txt_tc11.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_tc11FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_tc11FocusLost(evt);
            }
        });

        javax.swing.GroupLayout kGradientPanel12Layout = new javax.swing.GroupLayout(kGradientPanel12);
        kGradientPanel12.setLayout(kGradientPanel12Layout);
        kGradientPanel12Layout.setHorizontalGroup(
            kGradientPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel12Layout.createSequentialGroup()
                .addGap(224, 224, 224)
                .addGroup(kGradientPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(kGradientPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(kGradientPanel12Layout.createSequentialGroup()
                            .addComponent(lbl_Mail11)
                            .addGap(141, 141, 141)
                            .addComponent(txt_Mail11, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(kGradientPanel12Layout.createSequentialGroup()
                            .addComponent(lbl_adres)
                            .addGap(88, 88, 88)
                            .addComponent(txt_Adres, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(kGradientPanel12Layout.createSequentialGroup()
                            .addComponent(lbl_Cinsiyet11)
                            .addGap(129, 129, 129)
                            .addComponent(cmb_Cinsiyet, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(kGradientPanel12Layout.createSequentialGroup()
                            .addComponent(btn_Kaydet11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(214, 214, 214)
                            .addComponent(btn_Kapat11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(kGradientPanel12Layout.createSequentialGroup()
                            .addComponent(lbl_Soyad11)
                            .addGap(143, 143, 143)
                            .addComponent(txt_Soyad11, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(lbl_tc11)
                        .addComponent(lbl_Tel)
                        .addGroup(kGradientPanel12Layout.createSequentialGroup()
                            .addComponent(lbl_Adi)
                            .addGap(169, 169, 169)
                            .addGroup(kGradientPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txt_Ad, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(kGradientPanel12Layout.createSequentialGroup()
                                    .addGap(18, 18, 18)
                                    .addComponent(ekle_label)))))
                    .addGroup(kGradientPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txt_Tel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_tc11, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(224, 224, 224))
        );
        kGradientPanel12Layout.setVerticalGroup(
            kGradientPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel12Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(ekle_label)
                .addGap(38, 38, 38)
                .addGroup(kGradientPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_Adi)
                    .addComponent(txt_Ad, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(kGradientPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Soyad11, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Soyad11))
                .addGap(28, 28, 28)
                .addGroup(kGradientPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_tc11, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_tc11))
                .addGap(28, 28, 28)
                .addGroup(kGradientPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Tel11, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Tel))
                .addGap(28, 28, 28)
                .addGroup(kGradientPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Mail11, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Mail11))
                .addGap(26, 26, 26)
                .addGroup(kGradientPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_adres)
                    .addComponent(txt_Adres, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addGroup(kGradientPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_Cinsiyet11)
                    .addComponent(cmb_Cinsiyet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addGroup(kGradientPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_Kaydet11, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Kapat11, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout musteriekle_panelLayout = new javax.swing.GroupLayout(musteriekle_panel);
        musteriekle_panel.setLayout(musteriekle_panelLayout);
        musteriekle_panelLayout.setHorizontalGroup(
            musteriekle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        musteriekle_panelLayout.setVerticalGroup(
            musteriekle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        musterislem_panel.add(musteriekle_panel, "card3");

        kGradientPanel7.setkEndColor(new java.awt.Color(153, 255, 204));
        kGradientPanel7.setkGradientFocus(200);
        kGradientPanel7.setkStartColor(new java.awt.Color(204, 204, 255));

        musteriliste_scrollpane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tbl_musteri.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        tbl_musteri.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_musteri.setSelectionBackground(new java.awt.Color(232, 57, 95));
        musteriliste_scrollpane.setViewportView(tbl_musteri);

        btn_Kapat12.setText("Kapat");
        btn_Kapat12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_Kapat12.setkBorderRadius(15);
        btn_Kapat12.setkEndColor(new java.awt.Color(153, 153, 153));
        btn_Kapat12.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_Kapat12.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_Kapat12.setkHoverStartColor(new java.awt.Color(102, 102, 102));
        btn_Kapat12.setkStartColor(new java.awt.Color(102, 102, 102));
        btn_Kapat12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Kapat12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout kGradientPanel7Layout = new javax.swing.GroupLayout(kGradientPanel7);
        kGradientPanel7.setLayout(kGradientPanel7Layout);
        kGradientPanel7Layout.setHorizontalGroup(
            kGradientPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel7Layout.createSequentialGroup()
                .addContainerGap(69, Short.MAX_VALUE)
                .addGroup(kGradientPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(musteriliste_scrollpane, javax.swing.GroupLayout.PREFERRED_SIZE, 737, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Kapat12, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 351, Short.MAX_VALUE))
        );
        kGradientPanel7Layout.setVerticalGroup(
            kGradientPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel7Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(musteriliste_scrollpane, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(btn_Kapat12, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(66, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout musterilistele_panelLayout = new javax.swing.GroupLayout(musterilistele_panel);
        musterilistele_panel.setLayout(musterilistele_panelLayout);
        musterilistele_panelLayout.setHorizontalGroup(
            musterilistele_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, musterilistele_panelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(kGradientPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        musterilistele_panelLayout.setVerticalGroup(
            musterilistele_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(musterilistele_panelLayout.createSequentialGroup()
                .addComponent(kGradientPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        musterislem_panel.add(musterilistele_panel, "card4");

        musterislemsecenek_panel.add(musterislem_panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 193, 860, 530));

        musteriekle_label.setBackground(new java.awt.Color(96, 83, 150));
        musteriekle_label.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        musteriekle_label.setForeground(new java.awt.Color(96, 83, 150));
        musteriekle_label.setText("Müşteri Ekle");
        musterislemsecenek_panel.add(musteriekle_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(101, 150, -1, -1));

        musteriguncelle_label.setBackground(new java.awt.Color(96, 83, 150));
        musteriguncelle_label.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        musteriguncelle_label.setForeground(new java.awt.Color(96, 83, 150));
        musteriguncelle_label.setText("Müşteri Güncelle");
        musterislemsecenek_panel.add(musteriguncelle_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(405, 150, -1, -1));

        musterilistele_label.setBackground(new java.awt.Color(96, 83, 150));
        musterilistele_label.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        musterilistele_label.setForeground(new java.awt.Color(96, 83, 150));
        musterilistele_label.setText("Müşteri Listele");
        musterislemsecenek_panel.add(musterilistele_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 150, -1, -1));

        musterislemyapiniz_logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/islemsecmusterilogo.png"))); // NOI18N
        musterislemsecenek_panel.add(musterislemyapiniz_logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 233, -1, -1));

        musterislem_separator.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        musterislem_separator.setMinimumSize(new java.awt.Dimension(850, 10));
        musterislem_separator.setPreferredSize(new java.awt.Dimension(0, 3));
        musterislemsecenek_panel.add(musterislem_separator, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 190, 860, -1));

        islemler_panel.add(musterislemsecenek_panel, "card3");

        urunislemsecenek_panel.setBackground(new java.awt.Color(235, 255, 255));
        urunislemsecenek_panel.setLayout(null);

        bilgi_panel.setBackground(new java.awt.Color(96, 83, 150));
        bilgi_panel.setPreferredSize(new java.awt.Dimension(853, 42));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Ürün İşlemleri");

        javax.swing.GroupLayout bilgi_panelLayout = new javax.swing.GroupLayout(bilgi_panel);
        bilgi_panel.setLayout(bilgi_panelLayout);
        bilgi_panelLayout.setHorizontalGroup(
            bilgi_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bilgi_panelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel12)
                .addGap(354, 354, 354))
        );
        bilgi_panelLayout.setVerticalGroup(
            bilgi_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bilgi_panelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel12))
        );

        urunislemsecenek_panel.add(bilgi_panel);
        bilgi_panel.setBounds(0, 0, 860, 42);

        urunislem_panel.setPreferredSize(new java.awt.Dimension(853, 518));
        urunislem_panel.setLayout(new java.awt.CardLayout());

        kGradientPanel8.setkEndColor(new java.awt.Color(153, 255, 204));
        kGradientPanel8.setkGradientFocus(200);
        kGradientPanel8.setkStartColor(new java.awt.Color(204, 204, 255));

        lbl_adet.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_adet.setText("Adet");

        lbl_UrunAdi.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_UrunAdi.setText("Ürün Adı ");

        lbl_Aciklama.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_Aciklama.setText("Açıklama");

        txt_UrunAdı.setBackground(new Color(0,0,0,0));
        txt_UrunAdı.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_UrunAdı.setForeground(new java.awt.Color(162, 162, 162));
        txt_UrunAdı.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_UrunAdı.setOpaque(false);
        txt_UrunAdı.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_UrunAdıFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_UrunAdıFocusLost(evt);
            }
        });

        txt_Aciklama.setBackground(new Color(0,0,0,0));
        txt_Aciklama.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_Aciklama.setForeground(new java.awt.Color(162, 162, 162));
        txt_Aciklama.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_Aciklama.setOpaque(false);
        txt_Aciklama.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_AciklamaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_AciklamaFocusLost(evt);
            }
        });

        txt_UrunAdet.setBackground(new Color(0,0,0,0));
        txt_UrunAdet.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_UrunAdet.setForeground(new java.awt.Color(162, 162, 162));
        txt_UrunAdet.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_UrunAdet.setOpaque(false);
        txt_UrunAdet.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_UrunAdetFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_UrunAdetFocusLost(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel10.setText("Ürün Ekle");

        lbl_fiyat.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_fiyat.setText("Fiyat (₺)");

        txt_fiyat.setBackground(new Color(0,0,0,0));
        txt_fiyat.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_fiyat.setForeground(new java.awt.Color(162, 162, 162));
        txt_fiyat.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_fiyat.setOpaque(false);
        txt_fiyat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_fiyatFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_fiyatFocusLost(evt);
            }
        });

        btn_Kaydet.setText("Kaydet");
        btn_Kaydet.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_Kaydet.setkBorderRadius(15);
        btn_Kaydet.setkEndColor(new java.awt.Color(153, 153, 255));
        btn_Kaydet.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_Kaydet.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_Kaydet.setkHoverStartColor(new java.awt.Color(0, 255, 51));
        btn_Kaydet.setkStartColor(new java.awt.Color(0, 204, 102));
        btn_Kaydet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_KaydetActionPerformed(evt);
            }
        });

        btn_Kapat.setText("Kapat");
        btn_Kapat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_Kapat.setkBorderRadius(15);
        btn_Kapat.setkEndColor(new java.awt.Color(153, 153, 153));
        btn_Kapat.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_Kapat.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_Kapat.setkHoverStartColor(new java.awt.Color(102, 102, 102));
        btn_Kapat.setkStartColor(new java.awt.Color(102, 102, 102));
        btn_Kapat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_KapatActionPerformed(evt);
            }
        });

        lbl_urunkategori.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_urunkategori.setText("Kategori");

        cmb_urunkategori.setBackground(new Color(0,0,0,0));
        cmb_urunkategori.setFont(new java.awt.Font("Segoe UI Semibold", 0, 15)); // NOI18N
        cmb_urunkategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seçiniz" }));
        cmb_urunkategori.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));
        cmb_urunkategori.setOpaque(false);

        javax.swing.GroupLayout kGradientPanel8Layout = new javax.swing.GroupLayout(kGradientPanel8);
        kGradientPanel8.setLayout(kGradientPanel8Layout);
        kGradientPanel8Layout.setHorizontalGroup(
            kGradientPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel8Layout.createSequentialGroup()
                .addGroup(kGradientPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kGradientPanel8Layout.createSequentialGroup()
                        .addGap(423, 423, 423)
                        .addComponent(jLabel10))
                    .addGroup(kGradientPanel8Layout.createSequentialGroup()
                        .addGap(210, 210, 210)
                        .addGroup(kGradientPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(kGradientPanel8Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(kGradientPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(lbl_UrunAdi)
                                    .addComponent(lbl_fiyat)
                                    .addComponent(lbl_adet)
                                    .addComponent(lbl_Aciklama)
                                    .addComponent(lbl_urunkategori))
                                .addGap(128, 128, 128)
                                .addGroup(kGradientPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmb_urunkategori, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_fiyat, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_UrunAdı, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_UrunAdet, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_Aciklama, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(kGradientPanel8Layout.createSequentialGroup()
                                .addComponent(btn_Kaydet, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(260, 260, 260)
                                .addComponent(btn_Kapat, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(200, 200, 200))
        );
        kGradientPanel8Layout.setVerticalGroup(
            kGradientPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel8Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel10)
                .addGap(65, 65, 65)
                .addGroup(kGradientPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_urunkategori)
                    .addComponent(cmb_urunkategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(kGradientPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_UrunAdi)
                    .addComponent(txt_UrunAdı, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(kGradientPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_fiyat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_fiyat))
                .addGap(35, 35, 35)
                .addGroup(kGradientPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_adet)
                    .addComponent(txt_UrunAdet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(kGradientPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Aciklama)
                    .addComponent(txt_Aciklama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                .addGroup(kGradientPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_Kaydet, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Kapat, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48))
        );

        javax.swing.GroupLayout urunekle_panelLayout = new javax.swing.GroupLayout(urunekle_panel);
        urunekle_panel.setLayout(urunekle_panelLayout);
        urunekle_panelLayout.setHorizontalGroup(
            urunekle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        urunekle_panelLayout.setVerticalGroup(
            urunekle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(urunekle_panelLayout.createSequentialGroup()
                .addComponent(kGradientPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        urunislem_panel.add(urunekle_panel, "card2");

        kGradientPanel9.setkEndColor(new java.awt.Color(153, 255, 204));
        kGradientPanel9.setkGradientFocus(200);
        kGradientPanel9.setkStartColor(new java.awt.Color(204, 204, 255));

        lbl_adet1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_adet1.setText("Adet");

        lbl_UrunID1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_UrunID1.setText("Ürün ID ");

        lbl_UrunAdi1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_UrunAdi1.setText("Ürün Adı ");

        lbl_Aciklama1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_Aciklama1.setText("Açıklama");

        txt_UrunAdi.setBackground(new Color(0,0,0,0));
        txt_UrunAdi.setFont(new java.awt.Font("Segoe UI Semibold", 0, 15)); // NOI18N
        txt_UrunAdi.setForeground(new java.awt.Color(162, 162, 162));
        txt_UrunAdi.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_UrunAdi.setOpaque(false);

        txt_Aciklama1.setBackground(new Color(0,0,0,0));
        txt_Aciklama1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 15)); // NOI18N
        txt_Aciklama1.setForeground(new java.awt.Color(162, 162, 162));
        txt_Aciklama1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_Aciklama1.setOpaque(false);

        txt_Adet.setBackground(new Color(0,0,0,0));
        txt_Adet.setFont(new java.awt.Font("Segoe UI Semibold", 0, 15)); // NOI18N
        txt_Adet.setForeground(new java.awt.Color(162, 162, 162));
        txt_Adet.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_Adet.setOpaque(false);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel11.setText("Ürün Güncelle");

        lbl_fiyat1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_fiyat1.setText("Fiyat(₺)");

        txt_fiyat1.setBackground(new Color(0,0,0,0));
        txt_fiyat1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 15)); // NOI18N
        txt_fiyat1.setForeground(new java.awt.Color(162, 162, 162));
        txt_fiyat1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_fiyat1.setOpaque(false);

        lbl_urunkategori1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_urunkategori1.setText("Kategori");

        cmb_urunkategori1.setBackground(new Color(0,0,0,0));
        cmb_urunkategori1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 15)); // NOI18N
        cmb_urunkategori1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seçiniz" }));
        cmb_urunkategori1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));
        cmb_urunkategori1.setOpaque(false);

        btn_Arama.setText("Arama");
        btn_Arama.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_Arama.setkBorderRadius(15);
        btn_Arama.setkEndColor(new java.awt.Color(153, 153, 255));
        btn_Arama.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_Arama.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_Arama.setkHoverStartColor(new java.awt.Color(153, 153, 255));
        btn_Arama.setkStartColor(new java.awt.Color(0, 204, 204));
        btn_Arama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AramaActionPerformed(evt);
            }
        });

        txt_id1.setBackground(new Color(0,0,0,0));
        txt_id1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        txt_id1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_id1.setText("         ");
        txt_id1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(96, 83, 150), 2));
        txt_id1.setOpaque(false);

        btn_Kaydet1.setText("Güncelle");
        btn_Kaydet1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_Kaydet1.setkBorderRadius(15);
        btn_Kaydet1.setkEndColor(new java.awt.Color(153, 153, 255));
        btn_Kaydet1.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_Kaydet1.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_Kaydet1.setkHoverStartColor(new java.awt.Color(0, 255, 51));
        btn_Kaydet1.setkStartColor(new java.awt.Color(0, 204, 102));
        btn_Kaydet1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Kaydet1ActionPerformed(evt);
            }
        });

        btn_Sil.setText("Ürünü Sil");
        btn_Sil.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_Sil.setkBorderRadius(15);
        btn_Sil.setkEndColor(new java.awt.Color(255, 102, 102));
        btn_Sil.setkHoverEndColor(new java.awt.Color(255, 102, 102));
        btn_Sil.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_Sil.setkHoverStartColor(new java.awt.Color(255, 0, 0));
        btn_Sil.setkStartColor(new java.awt.Color(255, 102, 102));
        btn_Sil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SilActionPerformed(evt);
            }
        });

        btn_Kapat1.setText("Kapat");
        btn_Kapat1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_Kapat1.setkBorderRadius(15);
        btn_Kapat1.setkEndColor(new java.awt.Color(153, 153, 153));
        btn_Kapat1.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_Kapat1.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_Kapat1.setkHoverStartColor(new java.awt.Color(102, 102, 102));
        btn_Kapat1.setkStartColor(new java.awt.Color(102, 102, 102));
        btn_Kapat1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Kapat1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout kGradientPanel9Layout = new javax.swing.GroupLayout(kGradientPanel9);
        kGradientPanel9.setLayout(kGradientPanel9Layout);
        kGradientPanel9Layout.setHorizontalGroup(
            kGradientPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel9Layout.createSequentialGroup()
                .addGroup(kGradientPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kGradientPanel9Layout.createSequentialGroup()
                        .addGap(427, 427, 427)
                        .addComponent(jLabel11))
                    .addGroup(kGradientPanel9Layout.createSequentialGroup()
                        .addGap(330, 330, 330)
                        .addComponent(lbl_UrunID1)
                        .addGap(37, 37, 37)
                        .addComponent(txt_id1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(btn_Arama, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kGradientPanel9Layout.createSequentialGroup()
                        .addGap(210, 210, 210)
                        .addComponent(btn_Kaydet1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(100, 100, 100)
                        .addComponent(btn_Sil, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(100, 100, 100)
                        .addComponent(btn_Kapat1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kGradientPanel9Layout.createSequentialGroup()
                        .addGap(230, 230, 230)
                        .addGroup(kGradientPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(kGradientPanel9Layout.createSequentialGroup()
                                .addComponent(lbl_Aciklama1)
                                .addGap(129, 129, 129)
                                .addComponent(txt_Aciklama1, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(kGradientPanel9Layout.createSequentialGroup()
                                .addGroup(kGradientPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbl_adet1)
                                    .addComponent(lbl_fiyat1))
                                .addGap(142, 142, 142)
                                .addGroup(kGradientPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_Adet, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_fiyat1, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(kGradientPanel9Layout.createSequentialGroup()
                                .addComponent(lbl_UrunAdi1)
                                .addGap(128, 128, 128)
                                .addComponent(txt_UrunAdi, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(kGradientPanel9Layout.createSequentialGroup()
                                .addComponent(lbl_urunkategori1)
                                .addGap(125, 125, 125)
                                .addComponent(cmb_urunkategori1, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(140, 140, 140))
        );
        kGradientPanel9Layout.setVerticalGroup(
            kGradientPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel9Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jLabel11)
                .addGap(36, 36, 36)
                .addGroup(kGradientPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_UrunID1)
                    .addComponent(txt_id1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Arama, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(kGradientPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cmb_urunkategori1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_urunkategori1))
                .addGap(30, 30, 30)
                .addGroup(kGradientPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_UrunAdi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_UrunAdi1))
                .addGap(30, 30, 30)
                .addGroup(kGradientPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_fiyat1)
                    .addComponent(txt_fiyat1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(kGradientPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_adet1)
                    .addComponent(txt_Adet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(kGradientPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Aciklama1)
                    .addComponent(txt_Aciklama1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(60, 60, 60)
                .addGroup(kGradientPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_Kaydet1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Sil, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Kapat1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout urunguncelle_panelLayout = new javax.swing.GroupLayout(urunguncelle_panel);
        urunguncelle_panel.setLayout(urunguncelle_panelLayout);
        urunguncelle_panelLayout.setHorizontalGroup(
            urunguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        urunguncelle_panelLayout.setVerticalGroup(
            urunguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        urunislem_panel.add(urunguncelle_panel, "card3");

        kGradientPanel10.setkEndColor(new java.awt.Color(153, 255, 204));
        kGradientPanel10.setkGradientFocus(200);
        kGradientPanel10.setkStartColor(new java.awt.Color(204, 204, 255));

        jScrollPane4.setBorder(null);

        tbl_urun.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        tbl_urun.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_urun.setSelectionBackground(new java.awt.Color(232, 57, 95));
        jScrollPane4.setViewportView(tbl_urun);

        btn_Kapat2.setText("Kapat");
        btn_Kapat2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_Kapat2.setkBorderRadius(15);
        btn_Kapat2.setkEndColor(new java.awt.Color(153, 153, 153));
        btn_Kapat2.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_Kapat2.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_Kapat2.setkHoverStartColor(new java.awt.Color(102, 102, 102));
        btn_Kapat2.setkStartColor(new java.awt.Color(102, 102, 102));
        btn_Kapat2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Kapat2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout kGradientPanel10Layout = new javax.swing.GroupLayout(kGradientPanel10);
        kGradientPanel10.setLayout(kGradientPanel10Layout);
        kGradientPanel10Layout.setHorizontalGroup(
            kGradientPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel10Layout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addGroup(kGradientPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 737, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Kapat2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        kGradientPanel10Layout.setVerticalGroup(
            kGradientPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel10Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(btn_Kapat2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout urunlistele_panelLayout = new javax.swing.GroupLayout(urunlistele_panel);
        urunlistele_panel.setLayout(urunlistele_panelLayout);
        urunlistele_panelLayout.setHorizontalGroup(
            urunlistele_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        urunlistele_panelLayout.setVerticalGroup(
            urunlistele_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        urunislem_panel.add(urunlistele_panel, "card4");

        urunislemsecenek_panel.add(urunislem_panel);
        urunislem_panel.setBounds(-7, 193, 870, 540);

        urunekle_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_product_80px.png"))); // NOI18N
        urunekle_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        urunekle_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                urunekle_btnMouseClicked(evt);
            }
        });
        urunislemsecenek_panel.add(urunekle_btn);
        urunekle_btn.setBounds(102, 57, 80, 80);

        urunguncelle_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_update_80px.png"))); // NOI18N
        urunguncelle_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        urunguncelle_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                urunguncelle_btnMouseClicked(evt);
            }
        });
        urunislemsecenek_panel.add(urunguncelle_btn);
        urunguncelle_btn.setBounds(409, 57, 80, 80);

        urunlistele_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_details_80px.png"))); // NOI18N
        urunlistele_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        urunlistele_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                urunlistele_btnMouseClicked(evt);
            }
        });
        urunislemsecenek_panel.add(urunlistele_btn);
        urunlistele_btn.setBounds(685, 57, 80, 80);

        lbl_uekle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_uekle.setForeground(new java.awt.Color(96, 83, 150));
        lbl_uekle.setText("Ürün Ekle");
        urunislemsecenek_panel.add(lbl_uekle);
        lbl_uekle.setBounds(114, 150, 63, 20);

        lbl_uguncelle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_uguncelle.setForeground(new java.awt.Color(96, 83, 150));
        lbl_uguncelle.setText("Ürün Güncelle");
        urunislemsecenek_panel.add(lbl_uguncelle);
        lbl_uguncelle.setBounds(410, 150, 93, 20);

        lbl_ulistele.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_ulistele.setForeground(new java.awt.Color(96, 83, 150));
        lbl_ulistele.setText("Ürün Listele");
        urunislemsecenek_panel.add(lbl_ulistele);
        lbl_ulistele.setBounds(690, 150, 78, 20);

        logo_urunpanel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/islemsecurunlogo.png"))); // NOI18N
        urunislemsecenek_panel.add(logo_urunpanel);
        logo_urunpanel.setBounds(150, 240, 600, 425);

        jSeparator2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jSeparator2.setPreferredSize(new java.awt.Dimension(0, 3));
        urunislemsecenek_panel.add(jSeparator2);
        jSeparator2.setBounds(0, 190, 860, 3);

        islemler_panel.add(urunislemsecenek_panel, "card4");

        anasayfa_panel.setBackground(new java.awt.Color(235, 255, 255));
        anasayfa_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        sistembilgi_label.setBackground(new java.awt.Color(204, 255, 255));
        sistembilgi_label.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        sistembilgi_label.setForeground(new java.awt.Color(96, 83, 150));
        sistembilgi_label.setText("Sistem Bilgileri");
        anasayfa_panel.add(sistembilgi_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(389, 199, -1, 54));

        kayitlimusteri_panel.setBackground(new java.awt.Color(255, 255, 255));
        kayitlimusteri_panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        kayitlimusteri_panel.setPreferredSize(new java.awt.Dimension(168, 119));

        isaret4.setBackground(new java.awt.Color(153, 153, 255));

        javax.swing.GroupLayout isaret4Layout = new javax.swing.GroupLayout(isaret4);
        isaret4.setLayout(isaret4Layout);
        isaret4Layout.setHorizontalGroup(
            isaret4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        isaret4Layout.setVerticalGroup(
            isaret4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        kayitlimusterisayi_label.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        kayitlimusterisayi_label.setText("10");

        kayitlimusteri_icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_customer_40px.png"))); // NOI18N

        javax.swing.GroupLayout kayitlimusteri_panelLayout = new javax.swing.GroupLayout(kayitlimusteri_panel);
        kayitlimusteri_panel.setLayout(kayitlimusteri_panelLayout);
        kayitlimusteri_panelLayout.setHorizontalGroup(
            kayitlimusteri_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kayitlimusteri_panelLayout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addGroup(kayitlimusteri_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(kayitlimusteri_icon)
                    .addComponent(kayitlimusterisayi_label))
                .addContainerGap(61, Short.MAX_VALUE))
            .addComponent(isaret4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator3)
        );
        kayitlimusteri_panelLayout.setVerticalGroup(
            kayitlimusteri_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kayitlimusteri_panelLayout.createSequentialGroup()
                .addComponent(isaret4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(kayitlimusteri_icon)
                .addGap(1, 1, 1)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(kayitlimusterisayi_label))
        );

        anasayfa_panel.add(kayitlimusteri_panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(37, 359, -1, -1));

        hosgeldiniz_label.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        hosgeldiniz_label.setForeground(new java.awt.Color(96, 83, 150));
        hosgeldiniz_label.setText("MAKU Fatura Yönetim Paneline Hoşgeldiniz");
        anasayfa_panel.add(hosgeldiniz_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 90, 382, 54));

        kayitlimusteri_label.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        kayitlimusteri_label.setForeground(new java.awt.Color(96, 83, 150));
        kayitlimusteri_label.setText("Kayıtlı Müşteri");
        anasayfa_panel.add(kayitlimusteri_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(68, 295, -1, 57));

        kayitliurun_label.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        kayitliurun_label.setForeground(new java.awt.Color(96, 83, 150));
        kayitliurun_label.setText("Kayıtlı Ürün");
        anasayfa_panel.add(kayitliurun_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(374, 295, -1, 57));

        kayitliurun_panel.setBackground(new java.awt.Color(255, 255, 255));
        kayitliurun_panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        kayitliurun_panel.setPreferredSize(new java.awt.Dimension(168, 119));

        isaret5.setBackground(new java.awt.Color(153, 153, 255));

        javax.swing.GroupLayout isaret5Layout = new javax.swing.GroupLayout(isaret5);
        isaret5.setLayout(isaret5Layout);
        isaret5Layout.setHorizontalGroup(
            isaret5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        isaret5Layout.setVerticalGroup(
            isaret5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 11, Short.MAX_VALUE)
        );

        kayitliurunsayi_label.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        kayitliurunsayi_label.setText("10");

        kayitliurun_icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_product_40px.png"))); // NOI18N

        javax.swing.GroupLayout kayitliurun_panelLayout = new javax.swing.GroupLayout(kayitliurun_panel);
        kayitliurun_panel.setLayout(kayitliurun_panelLayout);
        kayitliurun_panelLayout.setHorizontalGroup(
            kayitliurun_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kayitliurun_panelLayout.createSequentialGroup()
                .addGroup(kayitliurun_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kayitliurun_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jSeparator7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addComponent(isaret5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(kayitliurun_panelLayout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addGroup(kayitliurun_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(kayitliurunsayi_label)
                            .addComponent(kayitliurun_icon))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        kayitliurun_panelLayout.setVerticalGroup(
            kayitliurun_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kayitliurun_panelLayout.createSequentialGroup()
                .addComponent(isaret5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addGroup(kayitliurun_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(kayitliurun_icon))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(kayitliurunsayi_label)
                .addContainerGap())
        );

        anasayfa_panel.add(kayitliurun_panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(329, 359, 168, -1));

        kayitlifatura_label.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        kayitlifatura_label.setForeground(new java.awt.Color(96, 83, 150));
        kayitlifatura_label.setText("Kayıtlı Fatura");
        anasayfa_panel.add(kayitlifatura_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(661, 295, -1, 57));

        kayitlifatura_panel.setBackground(new java.awt.Color(255, 255, 255));
        kayitlifatura_panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        kayitlifatura_panel.setPreferredSize(new java.awt.Dimension(168, 119));

        isaret7.setBackground(new java.awt.Color(153, 153, 255));

        javax.swing.GroupLayout isaret7Layout = new javax.swing.GroupLayout(isaret7);
        isaret7.setLayout(isaret7Layout);
        isaret7Layout.setHorizontalGroup(
            isaret7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        isaret7Layout.setVerticalGroup(
            isaret7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        kayitlifatura_icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_bill_40px.png"))); // NOI18N

        kayitlifaturasayi_label.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        kayitlifaturasayi_label.setText("10");

        javax.swing.GroupLayout kayitlifatura_panelLayout = new javax.swing.GroupLayout(kayitlifatura_panel);
        kayitlifatura_panel.setLayout(kayitlifatura_panelLayout);
        kayitlifatura_panelLayout.setHorizontalGroup(
            kayitlifatura_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(isaret7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(kayitlifatura_panelLayout.createSequentialGroup()
                .addGroup(kayitlifatura_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kayitlifatura_panelLayout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addGroup(kayitlifatura_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(kayitlifatura_icon)
                            .addComponent(kayitlifaturasayi_label)))
                    .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        kayitlifatura_panelLayout.setVerticalGroup(
            kayitlifatura_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kayitlifatura_panelLayout.createSequentialGroup()
                .addComponent(isaret7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(kayitlifatura_icon)
                .addGap(1, 1, 1)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(kayitlifaturasayi_label)
                .addContainerGap())
        );

        anasayfa_panel.add(kayitlifatura_panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(629, 359, -1, -1));

        kayitliyonetici_label.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        kayitliyonetici_label.setForeground(new java.awt.Color(96, 83, 150));
        kayitliyonetici_label.setText("Kayıtlı Yönetici/Kasiyer");
        anasayfa_panel.add(kayitliyonetici_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 510, -1, 57));

        kayitliyonetici_panel.setBackground(new java.awt.Color(255, 255, 255));
        kayitliyonetici_panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        kayitliyonetici_panel.setPreferredSize(new java.awt.Dimension(168, 119));
        kayitliyonetici_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        isaret8.setBackground(new java.awt.Color(153, 153, 255));

        javax.swing.GroupLayout isaret8Layout = new javax.swing.GroupLayout(isaret8);
        isaret8.setLayout(isaret8Layout);
        isaret8Layout.setHorizontalGroup(
            isaret8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 172, Short.MAX_VALUE)
        );
        isaret8Layout.setVerticalGroup(
            isaret8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        kayitliyonetici_panel.add(isaret8, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, -1, -1));

        kayitlikullanıcı_icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_user_40px.png"))); // NOI18N
        kayitliyonetici_panel.add(kayitlikullanıcı_icon, new org.netbeans.lib.awtextra.AbsoluteConstraints(68, 19, -1, -1));
        kayitliyonetici_panel.add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 180, -1));

        kayitlikullanicisayi_label.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        kayitlikullanicisayi_label.setText("3");
        kayitliyonetici_panel.add(kayitlikullanicisayi_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 70, 20, -1));

        anasayfa_panel.add(kayitliyonetici_panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(329, 572, 176, -1));

        jPanel1.setBackground(new java.awt.Color(96, 83, 150));
        jPanel1.setPreferredSize(new java.awt.Dimension(466, 39));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Anasayfa");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(406, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(389, 389, 389))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1))
        );

        anasayfa_panel.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 860, 42));

        info_icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_info_50px_1.png"))); // NOI18N
        anasayfa_panel.add(info_icon, new org.netbeans.lib.awtextra.AbsoluteConstraints(329, 199, -1, -1));
        anasayfa_panel.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 860, 10));

        kullanicisim_label.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        kullanicisim_label.setForeground(new java.awt.Color(0, 153, 153));
        anasayfa_panel.add(kullanicisim_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 60, -1, -1));

        islemler_panel.add(anasayfa_panel, "card2");

        panel_yonetim.setBackground(new java.awt.Color(235, 255, 255));
        panel_yonetim.setPreferredSize(new java.awt.Dimension(850, 723));

        yonetimbilgi_panel.setBackground(new java.awt.Color(96, 83, 150));
        yonetimbilgi_panel.setPreferredSize(new java.awt.Dimension(850, 39));

        yoneticislemleri_label.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        yoneticislemleri_label.setForeground(new java.awt.Color(255, 255, 255));
        yoneticislemleri_label.setText("Yönetim Paneli");

        javax.swing.GroupLayout yonetimbilgi_panelLayout = new javax.swing.GroupLayout(yonetimbilgi_panel);
        yonetimbilgi_panel.setLayout(yonetimbilgi_panelLayout);
        yonetimbilgi_panelLayout.setHorizontalGroup(
            yonetimbilgi_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, yonetimbilgi_panelLayout.createSequentialGroup()
                .addContainerGap(400, Short.MAX_VALUE)
                .addComponent(yoneticislemleri_label)
                .addGap(355, 355, 355))
        );
        yonetimbilgi_panelLayout.setVerticalGroup(
            yonetimbilgi_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, yonetimbilgi_panelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(yoneticislemleri_label))
        );

        yoneticikasiyerekle_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_add_user_group_man_woman_80px.png"))); // NOI18N
        yoneticikasiyerekle_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        yoneticikasiyerekle_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                yoneticikasiyerekle_btnMouseClicked(evt);
            }
        });

        yoneticikasiyerguncelle_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_change_employee_male_80px.png"))); // NOI18N
        yoneticikasiyerguncelle_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        yoneticikasiyerguncelle_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                yoneticikasiyerguncelle_btnMouseClicked(evt);
            }
        });

        yonetimislem_panel.setLayout(new java.awt.CardLayout());

        yoneticikasiyerguncelle_panel.setkEndColor(new java.awt.Color(153, 255, 204));
        yoneticikasiyerguncelle_panel.setkGradientFocus(200);
        yoneticikasiyerguncelle_panel.setkStartColor(new java.awt.Color(204, 204, 255));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel14.setText("Yönetici/Kasiyer Güncelle");

        btn_Guncelle12.setText("Güncelle");
        btn_Guncelle12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_Guncelle12.setkBorderRadius(15);
        btn_Guncelle12.setkEndColor(new java.awt.Color(153, 153, 255));
        btn_Guncelle12.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_Guncelle12.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_Guncelle12.setkHoverStartColor(new java.awt.Color(0, 255, 51));
        btn_Guncelle12.setkStartColor(new java.awt.Color(0, 204, 102));
        btn_Guncelle12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Guncelle12ActionPerformed(evt);
            }
        });

        btn_Arama11.setText("Arama");
        btn_Arama11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_Arama11.setkBorderRadius(15);
        btn_Arama11.setkEndColor(new java.awt.Color(153, 153, 255));
        btn_Arama11.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_Arama11.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_Arama11.setkHoverStartColor(new java.awt.Color(153, 153, 255));
        btn_Arama11.setkStartColor(new java.awt.Color(0, 204, 204));
        btn_Arama11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Arama11ActionPerformed(evt);
            }
        });

        btn_Sil11.setText("Yönetici/Kasiyer Sil");
        btn_Sil11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_Sil11.setkBorderRadius(15);
        btn_Sil11.setkEndColor(new java.awt.Color(255, 102, 102));
        btn_Sil11.setkHoverEndColor(new java.awt.Color(255, 102, 102));
        btn_Sil11.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_Sil11.setkHoverStartColor(new java.awt.Color(255, 0, 0));
        btn_Sil11.setkStartColor(new java.awt.Color(255, 102, 102));
        btn_Sil11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Sil11ActionPerformed(evt);
            }
        });

        btn_Kapat13.setText("Kapat");
        btn_Kapat13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_Kapat13.setkBorderRadius(15);
        btn_Kapat13.setkEndColor(new java.awt.Color(153, 153, 153));
        btn_Kapat13.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_Kapat13.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_Kapat13.setkHoverStartColor(new java.awt.Color(102, 102, 102));
        btn_Kapat13.setkStartColor(new java.awt.Color(102, 102, 102));
        btn_Kapat13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Kapat13ActionPerformed(evt);
            }
        });

        lbl_araid1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_araid1.setText("ID");

        txt_araid1.setBackground(new Color(0,0,0,0));
        txt_araid1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        txt_araid1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_araid1.setText("         ");
        txt_araid1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(96, 83, 150), 2));
        txt_araid1.setOpaque(false);

        lbl_Adi2.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_Adi2.setText("Ad");

        txt_Ad2.setBackground(new Color(0,0,0,0));
        txt_Ad2.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_Ad2.setForeground(new java.awt.Color(162, 162, 162));
        txt_Ad2.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_Ad2.setOpaque(false);

        lbl_Soyad14.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_Soyad14.setText("Soyad");

        txt_Soyad14.setBackground(new Color(0,0,0,0));
        txt_Soyad14.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_Soyad14.setForeground(new java.awt.Color(162, 162, 162));
        txt_Soyad14.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_Soyad14.setOpaque(false);

        lbl_kullaniciadi1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_kullaniciadi1.setText("Kullanıcı Adı");

        txt_kullaniciadi1.setBackground(new Color(0,0,0,0));
        txt_kullaniciadi1.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_kullaniciadi1.setForeground(new java.awt.Color(162, 162, 162));
        txt_kullaniciadi1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_kullaniciadi1.setOpaque(false);

        lbl_sifre1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_sifre1.setText("Şifre");

        txt_sifre1.setBackground(new Color(0,0,0,0));
        txt_sifre1.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_sifre1.setForeground(new java.awt.Color(162, 162, 162));
        txt_sifre1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_sifre1.setOpaque(false);

        lbl_Mail14.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_Mail14.setText("E-Mail");

        txt_Mail14.setBackground(new Color(0,0,0,0));
        txt_Mail14.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_Mail14.setForeground(new java.awt.Color(162, 162, 162));
        txt_Mail14.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_Mail14.setOpaque(false);

        lbl_rol1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_rol1.setText("Rolü");

        cmb_rol1.setBackground(new Color(0,0,0,0));
        cmb_rol1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 15)); // NOI18N
        cmb_rol1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seçiniz", "Yönetici", "Kasiyer" }));
        cmb_rol1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));
        cmb_rol1.setOpaque(false);

        javax.swing.GroupLayout yoneticikasiyerguncelle_panelLayout = new javax.swing.GroupLayout(yoneticikasiyerguncelle_panel);
        yoneticikasiyerguncelle_panel.setLayout(yoneticikasiyerguncelle_panelLayout);
        yoneticikasiyerguncelle_panelLayout.setHorizontalGroup(
            yoneticikasiyerguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(yoneticikasiyerguncelle_panelLayout.createSequentialGroup()
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 860, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(yoneticikasiyerguncelle_panelLayout.createSequentialGroup()
                .addGroup(yoneticikasiyerguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(yoneticikasiyerguncelle_panelLayout.createSequentialGroup()
                        .addGap(290, 290, 290)
                        .addComponent(lbl_araid1)
                        .addGap(31, 31, 31)
                        .addComponent(txt_araid1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(114, 114, 114)
                        .addComponent(btn_Arama11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(yoneticikasiyerguncelle_panelLayout.createSequentialGroup()
                        .addGap(280, 280, 280)
                        .addGroup(yoneticikasiyerguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(yoneticikasiyerguncelle_panelLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(lbl_Adi2)
                                .addGap(89, 89, 89)
                                .addComponent(txt_Ad2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(yoneticikasiyerguncelle_panelLayout.createSequentialGroup()
                                .addComponent(lbl_Soyad14)
                                .addGap(73, 73, 73)
                                .addComponent(txt_Soyad14, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(yoneticikasiyerguncelle_panelLayout.createSequentialGroup()
                                .addComponent(lbl_kullaniciadi1)
                                .addGap(23, 23, 23)
                                .addComponent(txt_kullaniciadi1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(yoneticikasiyerguncelle_panelLayout.createSequentialGroup()
                                .addComponent(lbl_sifre1)
                                .addGap(85, 85, 85)
                                .addComponent(txt_sifre1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(yoneticikasiyerguncelle_panelLayout.createSequentialGroup()
                                .addComponent(lbl_Mail14)
                                .addGap(71, 71, 71)
                                .addComponent(txt_Mail14, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(yoneticikasiyerguncelle_panelLayout.createSequentialGroup()
                                .addComponent(lbl_rol1)
                                .addGap(85, 85, 85)
                                .addComponent(cmb_rol1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, yoneticikasiyerguncelle_panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btn_Guncelle12, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(297, 297, 297)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, yoneticikasiyerguncelle_panelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(yoneticikasiyerguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, yoneticikasiyerguncelle_panelLayout.createSequentialGroup()
                        .addComponent(btn_Sil11, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(67, 67, 67)
                        .addComponent(btn_Kapat13, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(166, 166, 166))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, yoneticikasiyerguncelle_panelLayout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(310, 310, 310))))
        );
        yoneticikasiyerguncelle_panelLayout.setVerticalGroup(
            yoneticikasiyerguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(yoneticikasiyerguncelle_panelLayout.createSequentialGroup()
                .addComponent(jLabel14)
                .addGap(29, 29, 29)
                .addGroup(yoneticikasiyerguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_araid1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Arama11, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_araid1))
                .addGap(11, 11, 11)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(yoneticikasiyerguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_Adi2)
                    .addComponent(txt_Ad2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addGroup(yoneticikasiyerguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_Soyad14)
                    .addComponent(txt_Soyad14, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addGroup(yoneticikasiyerguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_kullaniciadi1)
                    .addComponent(txt_kullaniciadi1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(yoneticikasiyerguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_sifre1)
                    .addGroup(yoneticikasiyerguncelle_panelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(txt_sifre1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28)
                .addGroup(yoneticikasiyerguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_Mail14)
                    .addComponent(txt_Mail14, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addGroup(yoneticikasiyerguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(yoneticikasiyerguncelle_panelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lbl_rol1))
                    .addComponent(cmb_rol1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addGroup(yoneticikasiyerguncelle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Kapat13, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Sil11, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Guncelle12, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        yonetimislem_panel.add(yoneticikasiyerguncelle_panel, "card2");

        kGradientPanel13.setkEndColor(new java.awt.Color(153, 255, 204));
        kGradientPanel13.setkGradientFocus(200);
        kGradientPanel13.setkStartColor(new java.awt.Color(204, 204, 255));
        kGradientPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl_Soyad13.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_Soyad13.setText("Soyad");
        kGradientPanel13.add(lbl_Soyad13, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 140, -1, -1));

        lbl_Adi1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_Adi1.setText("Ad");
        kGradientPanel13.add(lbl_Adi1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, -1, -1));

        lbl_sifre.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_sifre.setText("Şifre");
        kGradientPanel13.add(lbl_sifre, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 250, -1, -1));

        lbl_Mail13.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_Mail13.setText("E-Mail");
        kGradientPanel13.add(lbl_Mail13, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 310, -1, -1));

        lbl_rol.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_rol.setText("Rolü");
        kGradientPanel13.add(lbl_rol, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 380, -1, -1));

        txt_Ad1.setBackground(new Color(0,0,0,0));
        txt_Ad1.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_Ad1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_Ad1.setOpaque(false);
        txt_Ad1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_Ad1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_Ad1FocusLost(evt);
            }
        });
        kGradientPanel13.add(txt_Ad1, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 80, 180, 22));

        txt_sifre.setBackground(new Color(0,0,0,0));
        txt_sifre.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_sifre.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_sifre.setOpaque(false);
        txt_sifre.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_sifreFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_sifreFocusLost(evt);
            }
        });
        kGradientPanel13.add(txt_sifre, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 260, 180, 22));

        txt_Mail13.setBackground(new Color(0,0,0,0));
        txt_Mail13.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_Mail13.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_Mail13.setOpaque(false);
        txt_Mail13.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_Mail13FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_Mail13FocusLost(evt);
            }
        });
        kGradientPanel13.add(txt_Mail13, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 310, 180, 22));

        txt_Soyad13.setBackground(new Color(0,0,0,0));
        txt_Soyad13.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_Soyad13.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_Soyad13.setOpaque(false);
        txt_Soyad13.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_Soyad13FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_Soyad13FocusLost(evt);
            }
        });
        kGradientPanel13.add(txt_Soyad13, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 140, 180, 22));

        cmb_rol.setBackground(new Color(0,0,0,0));
        cmb_rol.setFont(new java.awt.Font("Segoe UI Semibold", 0, 15)); // NOI18N
        cmb_rol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seçiniz", "Yönetici", "Kasiyer" }));
        cmb_rol.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));
        cmb_rol.setOpaque(false);
        kGradientPanel13.add(cmb_rol, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 370, 180, -1));

        ekle_label1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        ekle_label1.setText("Yönetici/Kasiyer Ekle");
        kGradientPanel13.add(ekle_label1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 10, -1, -1));

        btn_Kaydet13.setText("Kaydet");
        btn_Kaydet13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_Kaydet13.setkBorderRadius(15);
        btn_Kaydet13.setkEndColor(new java.awt.Color(153, 153, 255));
        btn_Kaydet13.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_Kaydet13.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_Kaydet13.setkHoverStartColor(new java.awt.Color(0, 255, 51));
        btn_Kaydet13.setkStartColor(new java.awt.Color(0, 204, 102));
        btn_Kaydet13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Kaydet13ActionPerformed(evt);
            }
        });
        kGradientPanel13.add(btn_Kaydet13, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 450, 100, 35));

        btn_Kapat14.setText("Kapat");
        btn_Kapat14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_Kapat14.setkBorderRadius(15);
        btn_Kapat14.setkEndColor(new java.awt.Color(153, 153, 153));
        btn_Kapat14.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_Kapat14.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_Kapat14.setkHoverStartColor(new java.awt.Color(102, 102, 102));
        btn_Kapat14.setkStartColor(new java.awt.Color(102, 102, 102));
        btn_Kapat14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Kapat14ActionPerformed(evt);
            }
        });
        kGradientPanel13.add(btn_Kapat14, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 450, 100, 35));

        lbl_kullaniciadi.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_kullaniciadi.setText("Kullanıcı Adı");
        kGradientPanel13.add(lbl_kullaniciadi, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 200, -1, -1));

        txt_kullaniciadi.setBackground(new Color(0,0,0,0));
        txt_kullaniciadi.setFont(new java.awt.Font("Segoe UI Semibold", 1, 15)); // NOI18N
        txt_kullaniciadi.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_kullaniciadi.setOpaque(false);
        txt_kullaniciadi.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_kullaniciadiFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_kullaniciadiFocusLost(evt);
            }
        });
        kGradientPanel13.add(txt_kullaniciadi, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 200, 180, 22));

        jPanel2.setBackground(new java.awt.Color(0, 223, 223));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 530, Short.MAX_VALUE)
        );

        kGradientPanel13.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 0, 10, 530));

        yoneticikasiyer_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(yoneticikasiyer_table);

        kGradientPanel13.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 80, 450, 290));

        ekle_label2.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        ekle_label2.setText("Yönetici/Kasiyer Görüntüle");
        kGradientPanel13.add(ekle_label2, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 10, -1, -1));

        javax.swing.GroupLayout yoneticikasiyerekle_panelLayout = new javax.swing.GroupLayout(yoneticikasiyerekle_panel);
        yoneticikasiyerekle_panel.setLayout(yoneticikasiyerekle_panelLayout);
        yoneticikasiyerekle_panelLayout.setHorizontalGroup(
            yoneticikasiyerekle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        yoneticikasiyerekle_panelLayout.setVerticalGroup(
            yoneticikasiyerekle_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        yonetimislem_panel.add(yoneticikasiyerekle_panel, "card3");

        yonetici_label.setBackground(new java.awt.Color(96, 83, 150));
        yonetici_label.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        yonetici_label.setForeground(new java.awt.Color(96, 83, 150));
        yonetici_label.setText("Yönetici/Kasiyer Ekle");

        yoneticiguncelle_label.setBackground(new java.awt.Color(96, 83, 150));
        yoneticiguncelle_label.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        yoneticiguncelle_label.setForeground(new java.awt.Color(96, 83, 150));
        yoneticiguncelle_label.setText("Yönetici/Kasiyer Güncelle");

        yonetimislem_separator1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        yonetimislem_separator1.setMinimumSize(new java.awt.Dimension(850, 10));
        yonetimislem_separator1.setPreferredSize(new java.awt.Dimension(0, 3));

        javax.swing.GroupLayout panel_yonetimLayout = new javax.swing.GroupLayout(panel_yonetim);
        panel_yonetim.setLayout(panel_yonetimLayout);
        panel_yonetimLayout.setHorizontalGroup(
            panel_yonetimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_yonetimLayout.createSequentialGroup()
                .addGap(132, 132, 132)
                .addGroup(panel_yonetimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_yonetimLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(yoneticikasiyerekle_btn))
                    .addComponent(yonetici_label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel_yonetimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_yonetimLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(yoneticikasiyerguncelle_btn))
                    .addComponent(yoneticiguncelle_label))
                .addGap(154, 154, 154))
            .addGroup(panel_yonetimLayout.createSequentialGroup()
                .addGroup(panel_yonetimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(yonetimbilgi_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 860, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yonetimislem_separator1, javax.swing.GroupLayout.PREFERRED_SIZE, 860, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yonetimislem_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panel_yonetimLayout.setVerticalGroup(
            panel_yonetimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_yonetimLayout.createSequentialGroup()
                .addComponent(yonetimbilgi_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_yonetimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panel_yonetimLayout.createSequentialGroup()
                        .addComponent(yoneticikasiyerguncelle_btn)
                        .addGap(20, 20, 20)
                        .addComponent(yoneticiguncelle_label))
                    .addGroup(panel_yonetimLayout.createSequentialGroup()
                        .addComponent(yoneticikasiyerekle_btn)
                        .addGap(20, 20, 20)
                        .addComponent(yonetici_label)))
                .addGap(21, 21, 21)
                .addComponent(yonetimislem_separator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(yonetimislem_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        islemler_panel.add(panel_yonetim, "card3");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(secenekler_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(islemler_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 856, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(secenekler_panel, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
            .addComponent(islemler_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void secenekler_panelMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_secenekler_panelMouseDragged
        new Drag(secenekler_panel).moveWindow(evt);
    }//GEN-LAST:event_secenekler_panelMouseDragged

    private void secenekler_panelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_secenekler_panelMousePressed
        new Drag(secenekler_panel).onPress(evt);
    }//GEN-LAST:event_secenekler_panelMousePressed

    private void musteriekle_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_musteriekle_btnMouseClicked
        musterilistele_panel.setVisible(false);
        musteriguncelle_panel.setVisible(false);
        urunislemsecenek_panel.setVisible(false);
        musterislem_panel.setVisible(true);
        musteriekle_panel.setVisible(true);
        musterieklepanelayar();
              
    }//GEN-LAST:event_musteriekle_btnMouseClicked

    private void musteriguncelle_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_musteriguncelle_btnMouseClicked
           
        musterilistele_panel.setVisible(false);
        musteriekle_panel.setVisible(false);
        urunislemsecenek_panel.setVisible(false);
        musterislem_panel.setVisible(true);
        musteriguncelle_panel.setVisible(true);
        musteriguncellepanelayar();
    
          
           
    }//GEN-LAST:event_musteriguncelle_btnMouseClicked

    private void musterilistele_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_musterilistele_btnMouseClicked
       musteriekle_panel.setVisible(false);
       musteriguncelle_panel.setVisible(false);
       musterislem_panel.setVisible(true);
       musterilistele_panel.setVisible(true);
       Update_table("select * from MUSTERILER",tbl_musteri);
       tbl_musteri.setEnabled(false);
    }//GEN-LAST:event_musterilistele_btnMouseClicked

    private void urunekle_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_urunekle_btnMouseClicked
        urunguncelle_panel.setVisible(false);
        urunlistele_panel.setVisible(false);
        urunislem_panel.setVisible(true);
        urunekle_panel.setVisible(true);
        cm_doldur(cmb_urunkategori,"SELECT * FROM URUN_KATEGORI");
        uruneklepanelayar();
    }//GEN-LAST:event_urunekle_btnMouseClicked

    private void urunguncelle_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_urunguncelle_btnMouseClicked
        urunekle_panel.setVisible(false);
        urunlistele_panel.setVisible(false);
        txt_UrunAdi.setEnabled(false);
        txt_fiyat1.setEnabled(false);
        txt_Adet.setEnabled(false);
        txt_Aciklama1.setEnabled(false);
        cm_doldur(cmb_urunkategori1,"SELECT * FROM URUN_KATEGORI");
        urunguncellepanelayar();
        urunislem_panel.setVisible(true);
        urunguncelle_panel.setVisible(true);
    }//GEN-LAST:event_urunguncelle_btnMouseClicked

    private void urunlistele_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_urunlistele_btnMouseClicked
        urunguncelle_panel.setVisible(false);
        urunekle_panel.setVisible(false);
        musterislem_panel.setVisible(false);
        urunislem_panel.setVisible(true);
        urunlistele_panel.setVisible(true);
        Update_table("select * from URUNLER",tbl_urun);
        tbl_urun.setEnabled(false);
    }//GEN-LAST:event_urunlistele_btnMouseClicked

    private void Buton_Anasayfa5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Buton_Anasayfa5MousePressed
        urunislemsecenek_panel.setVisible(false);
        panel_yonetim.setVisible(false);
        musterislemsecenek_panel.setVisible(false);
        tablotoplamsayi(musteritoplamsayi, kayitlimusterisayi_label);
        tablotoplamsayi(uruntoplamsayi, kayitliurunsayi_label);
        tablotoplamsayi(kullanicilartoplamsayi, kayitlikullanicisayi_label);
        anasayfa_panel.setVisible(true);
    }//GEN-LAST:event_Buton_Anasayfa5MousePressed

    private void Buton_musterislem1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Buton_musterislem1MousePressed
        anasayfa_panel.setVisible(false);
        panel_yonetim.setVisible(false);
        urunislemsecenek_panel.setVisible(false);
        musterislem_panel.setVisible(false);
        musterislemsecenek_panel.setVisible(true);
       
    }//GEN-LAST:event_Buton_musterislem1MousePressed

    private void buton_urunislem1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buton_urunislem1MouseClicked
        anasayfa_panel.setVisible(false);
        panel_yonetim.setVisible(false);
        musterislemsecenek_panel.setVisible(false);
        urunislem_panel.setVisible(false);
        urunislemsecenek_panel.setVisible(true);
    }//GEN-LAST:event_buton_urunislem1MouseClicked

    private void exit_btn1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exit_btn1MouseClicked
        JLabel label = new JLabel("Sayın "+Kullanicisim.kullanicisim+" \n Çıkış Yapmak İstediğinize Emin misiniz?");
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JFrame frame = new JFrame();
        String[] secenekler = new String[2];
        secenekler[0] = new String("Evet");
        secenekler[1] = new String("Hayır");
        int secenek=JOptionPane.showOptionDialog(frame.getContentPane(),label,"Çıkış Yap",0,JOptionPane.INFORMATION_MESSAGE,soruisareti_icon,secenekler,null);
        if(secenek==0){
            System.exit(0);
        }
        else{
            frame.dispose();
        }
    }//GEN-LAST:event_exit_btn1MouseClicked

    private void secenekler_panel5MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_secenekler_panel5MouseDragged
        new Drag(secenekler_panel).moveWindow(evt);
    }//GEN-LAST:event_secenekler_panel5MouseDragged

    private void secenekler_panel5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_secenekler_panel5MousePressed
        new Drag(secenekler_panel).onPress(evt);
    }//GEN-LAST:event_secenekler_panel5MousePressed

    private void Buton_Anasayfa6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Buton_Anasayfa6MousePressed
        urunislemsecenek_panel.setVisible(false);
        musterislemsecenek_panel.setVisible(false);
        tablotoplamsayi(musteritoplamsayi, kayitlimusterisayi_label);
        tablotoplamsayi(uruntoplamsayi, kayitliurunsayi_label);
        tablotoplamsayi(kullanicilartoplamsayi, kayitlikullanicisayi_label);
        anasayfa_panel.setVisible(true);
        
    }//GEN-LAST:event_Buton_Anasayfa6MousePressed

    private void Buton_musterislemMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Buton_musterislemMousePressed
        anasayfa_panel.setVisible(false);
        urunislemsecenek_panel.setVisible(false);
        musterislem_panel.setVisible(false);
        musterislemsecenek_panel.setVisible(true);
    }//GEN-LAST:event_Buton_musterislemMousePressed

    private void buton_urunislemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buton_urunislemMouseClicked
        anasayfa_panel.setVisible(false);
        musterislemsecenek_panel.setVisible(false);
        urunislem_panel.setVisible(false);
        urunislemsecenek_panel.setVisible(true);
    }//GEN-LAST:event_buton_urunislemMouseClicked

    private void exit_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exit_btnMouseClicked
        JLabel label = new JLabel("Sayın "+Kullanicisim.kullanicisim+" \n Çıkış Yapmak İstediğinize Emin misiniz?");
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JFrame frame = new JFrame();
        String[] secenekler = new String[2];
        secenekler[0] = new String("Evet");
        secenekler[1] = new String("Hayır");
        int secenek=JOptionPane.showOptionDialog(frame.getContentPane(),label,"Çıkış Yap",0,JOptionPane.INFORMATION_MESSAGE,soruisareti_icon,secenekler,null);
        if(secenek==0){
            System.exit(0);
        }
        else{
            frame.dispose();
        }
    }//GEN-LAST:event_exit_btnMouseClicked

    private void secenekler_panel6MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_secenekler_panel6MouseDragged
        new Drag(secenekler_panel).moveWindow(evt);
    }//GEN-LAST:event_secenekler_panel6MouseDragged

    private void secenekler_panel6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_secenekler_panel6MousePressed
        new Drag(secenekler_panel).onPress(evt);
    }//GEN-LAST:event_secenekler_panel6MousePressed

    private void txt_AdFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_AdFocusGained
        TextAyarlari.TextFieldFocusGained(txt_Ad, adTextYazi);
    }//GEN-LAST:event_txt_AdFocusGained

    private void txt_AdFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_AdFocusLost
        TextAyarlari.TextFieldFocusLost(txt_Ad);
    }//GEN-LAST:event_txt_AdFocusLost

    private void txt_Soyad11FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Soyad11FocusGained
        TextAyarlari.TextFieldFocusGained(txt_Soyad11, soyadTextYazi);
    }//GEN-LAST:event_txt_Soyad11FocusGained

    private void txt_Soyad11FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Soyad11FocusLost
       TextAyarlari.TextFieldFocusLost(txt_Soyad11);
    }//GEN-LAST:event_txt_Soyad11FocusLost

    private void txt_Tel11FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Tel11FocusGained
        TextAyarlari.TextFieldFocusGained(txt_Tel11, telnoTextYazi);
        txt_Tel11.setText("0");
    }//GEN-LAST:event_txt_Tel11FocusGained

    private void txt_Tel11FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Tel11FocusLost
        if(txt_Tel11.getText().equals("0")){
            txt_Tel11.setText("");
        }
        TextAyarlari.TextFieldFocusLost(txt_Tel11);
    }//GEN-LAST:event_txt_Tel11FocusLost

    private void txt_Mail11FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Mail11FocusGained
        TextAyarlari.TextFieldFocusGained(txt_Mail11, mailTextYazi);
    }//GEN-LAST:event_txt_Mail11FocusGained

    private void txt_Mail11FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Mail11FocusLost
        TextAyarlari.TextFieldFocusLost(txt_Mail11);
    }//GEN-LAST:event_txt_Mail11FocusLost

    private void txt_AdresFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_AdresFocusGained
        TextAyarlari.TextFieldFocusGained(txt_Adres, adresTextYazi);
    }//GEN-LAST:event_txt_AdresFocusGained

    private void txt_AdresFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_AdresFocusLost
        TextAyarlari.TextFieldFocusLost(txt_Adres);
    }//GEN-LAST:event_txt_AdresFocusLost

    private void txt_ad10FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_ad10FocusGained
        TextAyarlari.TextFieldFocusGained(txt_ad10, musteriguncelleText);
    }//GEN-LAST:event_txt_ad10FocusGained

    private void txt_Soyad10FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Soyad10FocusGained
        TextAyarlari.TextFieldFocusGained(txt_Soyad10, musteriguncelleText);
    }//GEN-LAST:event_txt_Soyad10FocusGained

    private void txt_Tel10FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Tel10FocusGained
        TextAyarlari.TextFieldFocusGained(txt_Tel10, musteriguncelleText);
    }//GEN-LAST:event_txt_Tel10FocusGained

    private void txt_Mail10FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Mail10FocusGained
        TextAyarlari.TextFieldFocusGained(txt_Mail10, musteriguncelleText);
    }//GEN-LAST:event_txt_Mail10FocusGained

    private void txt_adres10FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_adres10FocusGained
        TextAyarlari.TextFieldFocusGained(txt_adres10, musteriguncelleText);
    }//GEN-LAST:event_txt_adres10FocusGained

    private void btn_Kaydet10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Kaydet10ActionPerformed

        try{
            if((txt_ad10.getText().length()>0 && txt_Soyad10.getText().length()>0 && txt_tc10.getText().length()>0 && txt_Tel10.getText().length()>0 && txt_Mail10.getText().length()>0 && txt_adres10.getText().length()>0 && cbx_Cinsiyet10.getSelectedIndex()>0) && txt_araid.getText().length()>0 || txt_aratc.getText().length()>0){
                String sql="UPDATE MUSTERILER SET AD='"+txt_ad10.getText()+
                    "', SOYAD='"+txt_Soyad10.getText()+"', TC_NO='"+txt_tc10.getText()+"', TELEFON_NO='"+txt_Tel10.getText()+
                    "', EMAIL='"+txt_Mail10.getText()+"', ADRES='"+txt_adres10.getText()+
                    "', CINSIYET='"+cbx_Cinsiyet10.getSelectedItem()+"' WHERE TC_NO='"+txt_aratc.getText()+"'"+" OR ID='"+txt_araid.getText()+"'";
      
                veritabaniGuncelle(sql);
                
                musteriguncellepanelayar();
            }
            else{
                 
                 uyariMesajiPanel(unlem_icon, "Lütfen doğru ve eksiksiz bilgi girdiğinizden emin olun!", "Tamam","", "Hata");
            }
             
        }
       catch(Exception ex){
           JOptionPane.showMessageDialog(null, ex);
       }
    }//GEN-LAST:event_btn_Kaydet10ActionPerformed

    private void btn_Arama10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Arama10ActionPerformed
         if(txt_araid.getText().length()>0 && txt_aratc.getText().length()>0){
         uyariMesajiPanel(unlem_icon, "Lütfen sadece TC veya telefon numarası ile arama yapınız!", "Tamam", "", "Hata");
         txt_araid.setText("");txt_aratc.setText("");
         }
         else{
          try
        {
            conn=db.connect_db();
            String sql="SELECT * FROM MUSTERILER WHERE TC_NO='"+txt_aratc.getText()+"'"+" OR ID='"+txt_araid.getText()+"'";
            pst=conn.prepareStatement(sql);
            rs=pst.executeQuery();
            txt_ad10.setText(rs.getString(2));
            txt_Soyad10.setText(rs.getString(3));
            txt_tc10.setText(rs.getString(4));
            txt_Tel10.setText(rs.getString(5));
            txt_Mail10.setText(rs.getString(6));
            txt_adres10.setText(rs.getString(7));
            if(rs.getString(8).equals("Erkek")){
                cbx_Cinsiyet10.setSelectedIndex(1);
            }
            else{
                cbx_Cinsiyet10.setSelectedIndex(2);
            }
         txt_ad10.setEnabled(true);
         txt_Soyad10.setEnabled(true);
         txt_tc10.setEnabled(true);
         txt_Tel10.setEnabled(true);
         txt_Mail10.setEnabled(true);
         txt_adres10.setEnabled(true);
         cbx_Cinsiyet10.setEnabled(true); 
         
         txt_ad10.setForeground(Color.BLACK);
         txt_Soyad10.setForeground(Color.BLACK);
         txt_tc10.setForeground(Color.BLACK);
         txt_Tel10.setForeground(Color.BLACK);
         txt_Mail10.setForeground(Color.BLACK);
         txt_adres10.setForeground(Color.BLACK);
         cbx_Cinsiyet10.setForeground(Color.BLACK); 
            
        }
        catch(Exception ex)
        {
            if(txt_araid.getText().length()==0 && txt_aratc.getText().length()==0){
                JOptionPane.showMessageDialog(null, "Lütfen geçerli bir değer giriniz");
            }
            else{
                JOptionPane.showMessageDialog(null, "Girdiğiniz bilgilere ait sonuç bulunamadı");
            }
            
        }
        finally
        {
            try
            {
                conn.close();
            }
            catch(Exception ex)
            {
                
            }
        }    
           
        }
    }//GEN-LAST:event_btn_Arama10ActionPerformed

    private void btn_Kaydet11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Kaydet11ActionPerformed
         
        try{
        if(txt_Ad.getText().length()>0 && !txt_Ad.getText().equals(adTextYazi) && txt_Soyad11.getText().length()>0 && txt_tc11.getText().length()>0 && txt_Tel11.getText().length()>0 && txt_Mail11.getText().length()>0 && txt_Adres.getText().length()>0 && cmb_Cinsiyet.getSelectedIndex()>0 ){
        String sql="INSERT INTO MUSTERILER (AD,SOYAD,TC_NO,TELEFON_NO,EMAIL,ADRES,CINSIYET)"+
                             " VALUES('"+txt_Ad.getText()+"','"+txt_Soyad11.getText()+"','"+txt_tc11.getText()+"','"+txt_Tel11.getText()+"','"+txt_Mail11.getText()+"','"+ 
                              txt_Adres.getText()+"','"+cmb_Cinsiyet.getSelectedItem().toString()+"')";
        
        veritabaniEkle(sql, txt_Ad, "Müşteri");      
        musterieklepanelayar();
          
        }    
        else{
            
            uyariMesajiPanel(unlem_icon, "Lütfen doğru ve eksiksiz bilgi girdiğinizden emin olun!", "Tamam", "","Hata");
        }
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
        }
    }//GEN-LAST:event_btn_Kaydet11ActionPerformed

    private void btn_KaydetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_KaydetActionPerformed
           try{
           if(txt_UrunAdı.getText().length()>0 && !txt_UrunAdı.getText().equals(urunadTextYazi) && txt_fiyat.getText().length()>0 && txt_UrunAdet.getText().length()>0 && txt_Aciklama.getText().length()>0 ) {
               String sql="INSERT INTO URUNLER (KATEGORI,URUN_AD,FIYAT,ADET,ACIKLAMA)"+
                             " VALUES('"+cmb_urunkategori.getSelectedItem().toString()+"','"+txt_UrunAdı.getText()+"','"+Double.parseDouble(txt_fiyat.getText())+"','"+txt_UrunAdet.getText()+"','"+txt_Aciklama.getText()+"')";
        
           veritabaniEkle(sql, txt_UrunAdı, "Ürün");  
           uruneklepanelayar();
           }
           else{
                uyariMesajiPanel(unlem_icon, "Lütfen doğru ve eksiksiz bilgi girdiğinizden emin olun!", "Tamam", "","Hata");
           }
        }
        catch(Exception ex){
            uyariMesajiPanel(unlem_icon, "Lütfen virgül yerine nokta kullanınız!", "Tamam", "","Hata");
           }
    }//GEN-LAST:event_btn_KaydetActionPerformed

    private void txt_tc11FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_tc11FocusGained
       TextAyarlari.TextFieldFocusGained(txt_tc11, tcTextYazi);
    }//GEN-LAST:event_txt_tc11FocusGained

    private void txt_tc11FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_tc11FocusLost
        TextAyarlari.TextFieldFocusLost(txt_tc11);
    }//GEN-LAST:event_txt_tc11FocusLost

    private void txt_tc10FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_tc10FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_tc10FocusGained

    private void btn_Kapat10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Kapat10ActionPerformed
        musterislem_panel.setVisible(false);        
    }//GEN-LAST:event_btn_Kapat10ActionPerformed

    private void btn_Kapat11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Kapat11ActionPerformed
        musterislem_panel.setVisible(false);
    }//GEN-LAST:event_btn_Kapat11ActionPerformed

    private void btn_Sil10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Sil10ActionPerformed
        if(!txt_tc10.getText().equals(musteriguncelleText)){
        String sql="DELETE FROM MUSTERILER WHERE TC_NO='"+txt_tc10.getText()+"'";
        veritabaniSil(sql);
        musteriguncellepanelayar();
        }        
        else{
      
        uyariMesajiPanel(unlem_icon, "Lütfen müşteri seçimi yapınız!", "Tamam", "","Hata");
        }
       
    }//GEN-LAST:event_btn_Sil10ActionPerformed

    private void btn_AramaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AramaActionPerformed
         try
        {
            conn=db.connect_db();
            String sql="SELECT * FROM URUNLER WHERE ID='"+txt_id1.getText()+"'";
            pst=conn.prepareStatement(sql);
            rs=pst.executeQuery();
            cmb_urunkategori1.setSelectedItem(rs.getString(2));
            txt_UrunAdi.setText(rs.getString(3));
            txt_fiyat1.setText(rs.getString(4));
            txt_Adet.setText(rs.getString(5));
            txt_Aciklama1.setText(rs.getString(6));

            txt_UrunAdi.setEnabled(true);
            txt_fiyat1.setEnabled(true);
            txt_Adet.setEnabled(true);
            txt_Aciklama1.setEnabled(true);
            cmb_urunkategori1.setEnabled(true);
            
             txt_UrunAdi.setForeground(Color.BLACK);
             txt_fiyat1.setForeground(Color.BLACK);
             txt_Adet.setForeground(Color.BLACK);
             txt_Aciklama1.setForeground(Color.BLACK);
             cmb_urunkategori1.setForeground(Color.BLACK);

        }
        catch(Exception ex)
        {

        }
        finally
        {
            try
            {
                conn.close();
            }
            catch(Exception ex)
            {

            }
        }
       
    
    }//GEN-LAST:event_btn_AramaActionPerformed

    private void buton_faturaislem1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buton_faturaislem1MousePressed
        try {
            new Fatura().setVisible(true);
        } catch (IOException ex) {
            Logger.getLogger(AnaEkran.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(AnaEkran.class.getName()).log(Level.SEVERE, null, ex);
        }
        setVisible(false);
    }//GEN-LAST:event_buton_faturaislem1MousePressed

    private void buton_faturaislemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buton_faturaislemMouseClicked
         try {
            new Fatura().setVisible(true);
        } catch (IOException ex) {
            Logger.getLogger(AnaEkran.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(AnaEkran.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setVisible(false);
    }//GEN-LAST:event_buton_faturaislemMouseClicked

    private void btn_Kaydet1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Kaydet1ActionPerformed
        try{
        if(txt_UrunAdi.getText().length()>0 && !txt_UrunAdi.getText().equals(urunguncelleText) && txt_fiyat1.getText().length()>0 && txt_Adet.getText().length()>0 && txt_Aciklama1.getText().length()>0 && cmb_urunkategori1.getSelectedIndex()>0){
            
        String sql="UPDATE URUNLER SET KATEGORI='"+cmb_urunkategori1.getSelectedItem()+
                    "', URUN_AD='"+txt_UrunAdi.getText()+"', FIYAT='"+Double.parseDouble(txt_fiyat1.getText())+"', ADET='"+txt_Adet.getText()+
                    "', ACIKLAMA='"+txt_Aciklama1.getText()+
                   "' WHERE ID='"+txt_id1.getText()+"'";
         
        veritabaniGuncelle(sql);
        
        urunguncellepanelayar();
               
        }
        else{
            uyariMesajiPanel(unlem_icon, "Lütfen doğru ve eksiksiz bilgi girdiğinizden emin olun!", "Tamam", "","Hata");
        }        
        }catch(Exception ex){
             uyariMesajiPanel(unlem_icon, "Lütfen virgül yerine nokta kullanınız!", "Tamam", "","Hata");
        }
       
           
       
        

    }//GEN-LAST:event_btn_Kaydet1ActionPerformed

    private void btn_SilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SilActionPerformed
       if(!txt_UrunAdi.getText().equals(urunguncelleText)){
       String sql="DELETE FROM URUNLER WHERE ID='"+txt_id1.getText()+"'";
       veritabaniSil(sql);
       urunguncellepanelayar();
       }
       else{
            uyariMesajiPanel(unlem_icon, "Lütfen doğru ve eksiksiz bilgi girdiğinizden emin olun!", "Tamam", "","Hata");
       }
    }//GEN-LAST:event_btn_SilActionPerformed

    private void btn_Kapat1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Kapat1ActionPerformed
        urunislem_panel.setVisible(false);
    }//GEN-LAST:event_btn_Kapat1ActionPerformed

    private void btn_KapatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_KapatActionPerformed
        urunislem_panel.setVisible(false);
    }//GEN-LAST:event_btn_KapatActionPerformed

    private void txt_UrunAdıFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_UrunAdıFocusGained
        TextAyarlari.TextFieldFocusGained(txt_UrunAdı, urunadTextYazi);
    }//GEN-LAST:event_txt_UrunAdıFocusGained

    private void txt_UrunAdıFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_UrunAdıFocusLost
        TextAyarlari.TextFieldFocusLost(txt_UrunAdı);
    }//GEN-LAST:event_txt_UrunAdıFocusLost

    private void txt_fiyatFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_fiyatFocusGained
        TextAyarlari.TextFieldFocusGained(txt_fiyat, urunFiyatTextYazi);
    }//GEN-LAST:event_txt_fiyatFocusGained

    private void txt_fiyatFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_fiyatFocusLost
        TextAyarlari.TextFieldFocusLost(txt_fiyat);
    }//GEN-LAST:event_txt_fiyatFocusLost

    private void txt_UrunAdetFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_UrunAdetFocusGained
       TextAyarlari.TextFieldFocusGained(txt_UrunAdet, urunadetTextYazi);
    }//GEN-LAST:event_txt_UrunAdetFocusGained

    private void txt_UrunAdetFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_UrunAdetFocusLost
        TextAyarlari.TextFieldFocusLost(txt_UrunAdet);
    }//GEN-LAST:event_txt_UrunAdetFocusLost

    private void txt_AciklamaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_AciklamaFocusGained
        TextAyarlari.TextFieldFocusGained(txt_Aciklama, urunAciklamaTextYazi);
    }//GEN-LAST:event_txt_AciklamaFocusGained

    private void txt_AciklamaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_AciklamaFocusLost
        TextAyarlari.TextFieldFocusLost(txt_Aciklama);
    }//GEN-LAST:event_txt_AciklamaFocusLost

    private void btn_Kapat2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Kapat2ActionPerformed
        urunislem_panel.setVisible(false);
    }//GEN-LAST:event_btn_Kapat2ActionPerformed

    private void btn_Kapat12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Kapat12ActionPerformed
        musterislem_panel.setVisible(false);
    }//GEN-LAST:event_btn_Kapat12ActionPerformed

    private void Buton_Anasayfa5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Buton_Anasayfa5MouseEntered
        butonHoverRenklendir(Buton_Anasayfa5);
        butonOnLeaveHover(Buton_musterislem1);
        butonOnLeaveHover(buton_urunislem1);
        butonOnLeaveHover(buton_faturaislem1);
        butonOnLeaveHover(buton_yonetimpaneli);
    }//GEN-LAST:event_Buton_Anasayfa5MouseEntered

    private void Buton_musterislem1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Buton_musterislem1MouseEntered
        butonHoverRenklendir(Buton_musterislem1);
        butonOnLeaveHover(Buton_Anasayfa5);
        butonOnLeaveHover(buton_urunislem1);
        butonOnLeaveHover(buton_faturaislem1);
        butonOnLeaveHover(buton_yonetimpaneli);
    }//GEN-LAST:event_Buton_musterislem1MouseEntered

    private void buton_urunislem1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buton_urunislem1MouseEntered
        butonHoverRenklendir(buton_urunislem1);
        butonOnLeaveHover(Buton_musterislem1);
        butonOnLeaveHover(Buton_Anasayfa5);
        butonOnLeaveHover(buton_faturaislem1);
        butonOnLeaveHover(buton_yonetimpaneli);
    }//GEN-LAST:event_buton_urunislem1MouseEntered

    private void buton_faturaislem1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buton_faturaislem1MouseEntered
        butonHoverRenklendir(buton_faturaislem1);
        butonOnLeaveHover(Buton_musterislem1);
        butonOnLeaveHover(buton_urunislem1);
        butonOnLeaveHover(Buton_Anasayfa5);
        butonOnLeaveHover(buton_yonetimpaneli);
    }//GEN-LAST:event_buton_faturaislem1MouseEntered

    private void buton_yonetimpaneliMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buton_yonetimpaneliMouseEntered
        butonHoverRenklendir(buton_yonetimpaneli);
        butonOnLeaveHover(Buton_musterislem1);
        butonOnLeaveHover(buton_urunislem1);
        butonOnLeaveHover(buton_faturaislem1);
        butonOnLeaveHover(Buton_Anasayfa5);
    }//GEN-LAST:event_buton_yonetimpaneliMouseEntered

    private void Buton_Anasayfa5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Buton_Anasayfa5MouseExited
        butonOnLeaveHover(Buton_Anasayfa5);
    }//GEN-LAST:event_Buton_Anasayfa5MouseExited

    private void Buton_musterislem1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Buton_musterislem1MouseExited
        butonOnLeaveHover(Buton_musterislem1);
    }//GEN-LAST:event_Buton_musterislem1MouseExited

    private void buton_urunislem1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buton_urunislem1MouseExited
        butonOnLeaveHover(buton_urunislem1);
    }//GEN-LAST:event_buton_urunislem1MouseExited

    private void buton_faturaislem1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buton_faturaislem1MouseExited
       butonOnLeaveHover(buton_faturaislem1);
    }//GEN-LAST:event_buton_faturaislem1MouseExited

    private void buton_yonetimpaneliMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buton_yonetimpaneliMouseExited
        butonOnLeaveHover(buton_yonetimpaneli);
    }//GEN-LAST:event_buton_yonetimpaneliMouseExited

    private void Buton_Anasayfa6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Buton_Anasayfa6MouseEntered
        butonHoverRenklendir(Buton_Anasayfa6);
        butonOnLeaveHover(Buton_musterislem);
        butonOnLeaveHover(buton_urunislem);
        butonOnLeaveHover(buton_faturaislem);
       
    }//GEN-LAST:event_Buton_Anasayfa6MouseEntered

    private void Buton_Anasayfa6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Buton_Anasayfa6MouseExited
        butonOnLeaveHover(Buton_Anasayfa6);
    }//GEN-LAST:event_Buton_Anasayfa6MouseExited

    private void Buton_musterislemMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Buton_musterislemMouseEntered
        butonHoverRenklendir(Buton_musterislem);
        butonOnLeaveHover(Buton_Anasayfa6);
        butonOnLeaveHover(buton_urunislem);
        butonOnLeaveHover(buton_faturaislem);
    }//GEN-LAST:event_Buton_musterislemMouseEntered

    private void Buton_musterislemMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Buton_musterislemMouseExited
         butonOnLeaveHover(Buton_musterislem);
    }//GEN-LAST:event_Buton_musterislemMouseExited

    private void buton_urunislemMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buton_urunislemMouseEntered
        butonHoverRenklendir(buton_urunislem);
        butonOnLeaveHover(Buton_musterislem);
        butonOnLeaveHover(Buton_Anasayfa6);
        butonOnLeaveHover(buton_faturaislem);
    }//GEN-LAST:event_buton_urunislemMouseEntered

    private void buton_urunislemMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buton_urunislemMouseExited
         butonOnLeaveHover(buton_urunislem);
    }//GEN-LAST:event_buton_urunislemMouseExited

    private void buton_faturaislemMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buton_faturaislemMouseEntered
        butonHoverRenklendir(buton_faturaislem);
        butonOnLeaveHover(Buton_musterislem);
        butonOnLeaveHover(buton_urunislem);
        butonOnLeaveHover(Buton_Anasayfa6);
    }//GEN-LAST:event_buton_faturaislemMouseEntered

    private void buton_faturaislemMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buton_faturaislemMouseExited
       butonOnLeaveHover(buton_faturaislem);
    }//GEN-LAST:event_buton_faturaislemMouseExited

    private void yoneticikasiyerekle_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_yoneticikasiyerekle_btnMouseClicked
        yoneticikasiyerguncelle_panel.setVisible(false);
        Update_table("select * from KULLANICILAR",yoneticikasiyer_table);
        yonetimeklepanelayar();
        yoneticikasiyerekle_panel.setVisible(true);
    }//GEN-LAST:event_yoneticikasiyerekle_btnMouseClicked

    private void yoneticikasiyerguncelle_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_yoneticikasiyerguncelle_btnMouseClicked
         yoneticikasiyerekle_panel.setVisible(false);
         urunislemsecenek_panel.setVisible(false);
         musterislemsecenek_panel.setVisible(false);
         yoneticikasiyerguncelleayar();
         yoneticikasiyerguncelle_panel.setVisible(true);
    }//GEN-LAST:event_yoneticikasiyerguncelle_btnMouseClicked

    private void btn_Guncelle12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Guncelle12ActionPerformed
          try{
            if(txt_Ad2.getText().length()>0 && txt_Soyad14.getText().length()>0 && txt_kullaniciadi1.getText().length()>0 && txt_sifre1.getText().length()>0 && txt_Mail14.getText().length()>0  && cmb_rol1.getSelectedIndex()>0 && txt_araid1.getText().length()>0){
                String sql="UPDATE KULLANICILAR SET AD='"+txt_Ad2.getText()+
                    "', SOYAD='"+txt_Soyad14.getText()+"', KULLANICI_ADI='"+txt_kullaniciadi1.getText()+"', SIFRE='"+txt_sifre1.getText()+
                    "', ROL='"+cmb_rol1.getSelectedItem().toString()+"', EMAIL='"+txt_Mail14.getText()+
                   "' WHERE ID='"+txt_araid1.getText()+"'";
      
                veritabaniGuncelle(sql);
                
                yoneticikasiyerguncelleayar();
            }
            else{
                 
                 uyariMesajiPanel(unlem_icon, "Lütfen doğru ve eksiksiz bilgi girdiğinizden emin olun!", "Tamam","", "Hata");
            }
             
        }
       catch(Exception ex){
           JOptionPane.showMessageDialog(null, ex);
       }
    }//GEN-LAST:event_btn_Guncelle12ActionPerformed

    private void btn_Arama11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Arama11ActionPerformed
        try
        {
            conn=db.connect_db();
            String sql="SELECT * FROM KULLANICILAR WHERE ID='"+txt_araid1.getText()+"'";
            pst=conn.prepareStatement(sql);
            rs=pst.executeQuery();
            txt_Ad2.setText(rs.getString(2));
            txt_Soyad14.setText(rs.getString(3));
            txt_kullaniciadi1.setText(rs.getString(4));
            txt_sifre1.setText(rs.getString(5));
            txt_Mail14.setText(rs.getString(7));
            if(rs.getString(6).equals("Yönetici")){
                cmb_rol1.setSelectedIndex(1);
            }
            else{
                cmb_rol1.setSelectedIndex(2);
            }
         txt_Ad2.setEnabled(true);
         txt_Soyad14.setEnabled(true);
         txt_kullaniciadi1.setEnabled(true);
         txt_sifre1.setEnabled(true);
         txt_Mail14.setEnabled(true);
         cmb_rol1.setEnabled(true); 
         
         txt_Ad2.setForeground(Color.BLACK);
         txt_Soyad14.setForeground(Color.BLACK);
         txt_kullaniciadi1.setForeground(Color.BLACK);
         txt_sifre1.setForeground(Color.BLACK);
         txt_Mail14.setForeground(Color.BLACK);
         cbx_Cinsiyet10.setForeground(Color.BLACK); 
            
        }
        catch(Exception ex)
        {
            if(txt_araid1.getText().length()==0){
                JOptionPane.showMessageDialog(null, "Lütfen geçerli bir değer giriniz");
            }
            else{
                JOptionPane.showMessageDialog(null, "Girdiğiniz bilgilere ait sonuç bulunamadı");
            }
            
        }
        finally
        {
            try
            {
                conn.close();
            }
            catch(Exception ex)
            {
                
            }
        }
    }//GEN-LAST:event_btn_Arama11ActionPerformed

    private void btn_Sil11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Sil11ActionPerformed
       if(!txt_kullaniciadi1.getText().equals(yoneticiText)){
        String sql="DELETE FROM KULLANICILAR WHERE ID='"+txt_araid1.getText()+"'";
        veritabaniSil(sql);
        yoneticikasiyerguncelleayar();
        }        
        else{
      
        uyariMesajiPanel(unlem_icon, "Lütfen yönetici/kasiyer seçimi yapınız!", "Tamam", "","Hata");
        }
    }//GEN-LAST:event_btn_Sil11ActionPerformed

    private void btn_Kapat13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Kapat13ActionPerformed
        yoneticikasiyerguncelle_panel.setVisible(false);
        yoneticikasiyerekle_panel.setVisible(true);
    }//GEN-LAST:event_btn_Kapat13ActionPerformed

    private void txt_Ad1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Ad1FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_Ad1FocusGained

    private void txt_Ad1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Ad1FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_Ad1FocusLost

    private void txt_sifreFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_sifreFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_sifreFocusGained

    private void txt_sifreFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_sifreFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_sifreFocusLost

    private void txt_Mail13FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Mail13FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_Mail13FocusGained

    private void txt_Mail13FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Mail13FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_Mail13FocusLost

    private void txt_Soyad13FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Soyad13FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_Soyad13FocusGained

    private void txt_Soyad13FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Soyad13FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_Soyad13FocusLost

    private void btn_Kaydet13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Kaydet13ActionPerformed
        String rol = null;
        try{
        if(txt_Ad1.getText().length()>0 && txt_Soyad13.getText().length()>0 && txt_kullaniciadi.getText().length()>0 && txt_sifre.getText().length()>0 && txt_Mail13.getText().length()>0 && cmb_rol.getSelectedIndex()>0 ){
        String sql="INSERT INTO KULLANICILAR (AD,SOYAD,KULLANICI_ADI,SIFRE,ROL,EMAIL)"+
                             " VALUES('"+txt_Ad1.getText()+"','"+txt_Soyad13.getText()+"','"+txt_kullaniciadi.getText()+"','"+txt_sifre.getText()+"','"+cmb_rol.getSelectedItem().toString()+"','"+ 
                              txt_Mail13.getText()+"')";
        if(cmb_rol.getSelectedIndex()==1){
            rol="Yönetici";
        }
        else if(cmb_rol.getSelectedIndex()==2){
            rol="Kasiyer";
        }
        veritabaniEkle(sql, txt_Ad1, rol);      
        Update_table("select * from KULLANICILAR",yoneticikasiyer_table);
        yonetimeklepanelayar();
        }    
        else{
            
            uyariMesajiPanel(unlem_icon, "Lütfen doğru ve eksiksiz bilgi girdiğinizden emin olun!", "Tamam", "","Hata");
        }
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
        }
    }//GEN-LAST:event_btn_Kaydet13ActionPerformed

    private void btn_Kapat14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Kapat14ActionPerformed
        yoneticikasiyerekle_panel.setVisible(false);
        yoneticikasiyerguncelle_panel.setVisible(false);
        panel_yonetim.setVisible(false);
        anasayfa_panel.setVisible(true);
    }//GEN-LAST:event_btn_Kapat14ActionPerformed

    private void txt_kullaniciadiFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_kullaniciadiFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_kullaniciadiFocusGained

    private void txt_kullaniciadiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_kullaniciadiFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_kullaniciadiFocusLost

    private void buton_yonetimpaneliMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buton_yonetimpaneliMousePressed
        anasayfa_panel.setVisible(false);
        musterislemsecenek_panel.setVisible(false);
        urunislemsecenek_panel.setVisible(false);
        yoneticikasiyerguncelle_panel.setVisible(false);
        Update_table("select * from KULLANICILAR",yoneticikasiyer_table);
        panel_yonetim.setVisible(true);
        yoneticikasiyerekle_panel.setVisible(true);
    }//GEN-LAST:event_buton_yonetimpaneliMousePressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AnaEkran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AnaEkran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AnaEkran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AnaEkran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
             new AnaEkran().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Buton_Anasayfa5;
    private javax.swing.JPanel Buton_Anasayfa6;
    private javax.swing.JPanel Buton_musterislem;
    private javax.swing.JPanel Buton_musterislem1;
    private javax.swing.JLabel anasayfa_label;
    private javax.swing.JLabel anasayfa_label1;
    private javax.swing.JLabel anasayfa_label2;
    private javax.swing.JLabel anasayfa_label3;
    private javax.swing.JLabel anasayfa_label4;
    private javax.swing.JLabel anasayfa_label5;
    private javax.swing.JLabel anasayfa_label6;
    private javax.swing.JLabel anasayfa_label7;
    private javax.swing.JLabel anasayfa_label8;
    private javax.swing.JPanel anasayfa_panel;
    private javax.swing.JPanel bilgi_panel;
    private keeptoo.KButton btn_Arama;
    private keeptoo.KButton btn_Arama10;
    private keeptoo.KButton btn_Arama11;
    private keeptoo.KButton btn_Guncelle12;
    private keeptoo.KButton btn_Kapat;
    private keeptoo.KButton btn_Kapat1;
    private keeptoo.KButton btn_Kapat10;
    private keeptoo.KButton btn_Kapat11;
    private keeptoo.KButton btn_Kapat12;
    private keeptoo.KButton btn_Kapat13;
    private keeptoo.KButton btn_Kapat14;
    private keeptoo.KButton btn_Kapat2;
    private keeptoo.KButton btn_Kaydet;
    private keeptoo.KButton btn_Kaydet1;
    private keeptoo.KButton btn_Kaydet10;
    private keeptoo.KButton btn_Kaydet11;
    private keeptoo.KButton btn_Kaydet13;
    private keeptoo.KButton btn_Sil;
    private keeptoo.KButton btn_Sil10;
    private keeptoo.KButton btn_Sil11;
    private javax.swing.JPanel buton_faturaislem;
    private javax.swing.JPanel buton_faturaislem1;
    private javax.swing.JPanel buton_urunislem;
    private javax.swing.JPanel buton_urunislem1;
    private javax.swing.JPanel buton_yonetimpaneli;
    private javax.swing.JComboBox<String> cbx_Cinsiyet10;
    private javax.swing.JComboBox<String> cmb_Cinsiyet;
    private javax.swing.JComboBox<String> cmb_rol;
    private javax.swing.JComboBox<String> cmb_rol1;
    private javax.swing.JComboBox<String> cmb_urunkategori;
    private javax.swing.JComboBox<String> cmb_urunkategori1;
    private javax.swing.JLabel ekle_label;
    private javax.swing.JLabel ekle_label1;
    private javax.swing.JLabel ekle_label2;
    private javax.swing.JLabel exit_btn;
    private javax.swing.JLabel exit_btn1;
    private javax.swing.JLabel hosgeldiniz_label;
    private javax.swing.JLabel info_icon;
    private javax.swing.JPanel isaret10;
    private javax.swing.JPanel isaret11;
    private javax.swing.JPanel isaret12;
    private javax.swing.JPanel isaret13;
    private javax.swing.JPanel isaret14;
    private javax.swing.JPanel isaret15;
    private javax.swing.JPanel isaret16;
    private javax.swing.JPanel isaret4;
    private javax.swing.JPanel isaret5;
    private javax.swing.JPanel isaret6;
    private javax.swing.JPanel isaret7;
    private javax.swing.JPanel isaret8;
    private javax.swing.JPanel isaret9;
    private javax.swing.JPanel islemler_panel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private keeptoo.KGradientPanel kGradientPanel10;
    private keeptoo.KGradientPanel kGradientPanel12;
    private keeptoo.KGradientPanel kGradientPanel13;
    private keeptoo.KGradientPanel kGradientPanel7;
    private keeptoo.KGradientPanel kGradientPanel8;
    private keeptoo.KGradientPanel kGradientPanel9;
    private javax.swing.JPanel kasiyer_panel;
    private javax.swing.JLabel kayitlifatura_icon;
    private javax.swing.JLabel kayitlifatura_label;
    private javax.swing.JPanel kayitlifatura_panel;
    private javax.swing.JLabel kayitlifaturasayi_label;
    private javax.swing.JLabel kayitlikullanicisayi_label;
    private javax.swing.JLabel kayitlikullanıcı_icon;
    private javax.swing.JLabel kayitlimusteri_icon;
    private javax.swing.JLabel kayitlimusteri_label;
    private javax.swing.JPanel kayitlimusteri_panel;
    private javax.swing.JLabel kayitlimusterisayi_label;
    private javax.swing.JLabel kayitliurun_icon;
    private javax.swing.JLabel kayitliurun_label;
    private javax.swing.JPanel kayitliurun_panel;
    private javax.swing.JLabel kayitliurunsayi_label;
    private javax.swing.JLabel kayitliyonetici_label;
    private javax.swing.JPanel kayitliyonetici_panel;
    private javax.swing.JLabel kullanicisim_label;
    private javax.swing.JLabel lbl_Aciklama;
    private javax.swing.JLabel lbl_Aciklama1;
    private javax.swing.JLabel lbl_Adi;
    private javax.swing.JLabel lbl_Adi1;
    private javax.swing.JLabel lbl_Adi2;
    private javax.swing.JLabel lbl_Adres10;
    private javax.swing.JLabel lbl_Cinsiyet10;
    private javax.swing.JLabel lbl_Cinsiyet11;
    private javax.swing.JLabel lbl_Mail10;
    private javax.swing.JLabel lbl_Mail11;
    private javax.swing.JLabel lbl_Mail13;
    private javax.swing.JLabel lbl_Mail14;
    private javax.swing.JLabel lbl_Soyad10;
    private javax.swing.JLabel lbl_Soyad11;
    private javax.swing.JLabel lbl_Soyad13;
    private javax.swing.JLabel lbl_Soyad14;
    private javax.swing.JLabel lbl_Tel;
    private javax.swing.JLabel lbl_Telefon10;
    private javax.swing.JLabel lbl_UrunAdi;
    private javax.swing.JLabel lbl_UrunAdi1;
    private javax.swing.JLabel lbl_UrunID1;
    private javax.swing.JLabel lbl_ad10;
    private javax.swing.JLabel lbl_adet;
    private javax.swing.JLabel lbl_adet1;
    private javax.swing.JLabel lbl_adres;
    private javax.swing.JLabel lbl_araid;
    private javax.swing.JLabel lbl_araid1;
    private javax.swing.JLabel lbl_aratc10;
    private javax.swing.JLabel lbl_fiyat;
    private javax.swing.JLabel lbl_fiyat1;
    private javax.swing.JLabel lbl_kullaniciadi;
    private javax.swing.JLabel lbl_kullaniciadi1;
    private javax.swing.JLabel lbl_rol;
    private javax.swing.JLabel lbl_rol1;
    private javax.swing.JLabel lbl_sifre;
    private javax.swing.JLabel lbl_sifre1;
    private javax.swing.JLabel lbl_tc10;
    private javax.swing.JLabel lbl_tc11;
    private javax.swing.JLabel lbl_uekle;
    private javax.swing.JLabel lbl_uguncelle;
    private javax.swing.JLabel lbl_ulistele;
    private javax.swing.JLabel lbl_urunkategori;
    private javax.swing.JLabel lbl_urunkategori1;
    private javax.swing.JLabel logo_urunpanel;
    private javax.swing.JLabel logo_yonetimpanel;
    private javax.swing.JPanel musteribilgi_panel;
    private javax.swing.JLabel musteriekle_btn;
    private javax.swing.JLabel musteriekle_label;
    private javax.swing.JPanel musteriekle_panel;
    private javax.swing.JLabel musteriguncelle_btn;
    private javax.swing.JLabel musteriguncelle_label;
    private keeptoo.KGradientPanel musteriguncelle_panel;
    private javax.swing.JScrollPane musteriliste_scrollpane;
    private javax.swing.JLabel musterilistele_btn;
    private javax.swing.JLabel musterilistele_label;
    private javax.swing.JPanel musterilistele_panel;
    private javax.swing.JPanel musterislem_panel;
    private javax.swing.JSeparator musterislem_separator;
    private javax.swing.JLabel musterislemleri_label;
    private javax.swing.JPanel musterislemsecenek_panel;
    private javax.swing.JLabel musterislemyapiniz_logo;
    private javax.swing.JPanel panel_yonetim;
    private javax.swing.JPanel secenekler_panel;
    private javax.swing.JPanel secenekler_panel5;
    private javax.swing.JPanel secenekler_panel6;
    private javax.swing.JLabel sistembilgi_label;
    private javax.swing.JTable tbl_musteri;
    private javax.swing.JTable tbl_urun;
    private javax.swing.JTextField txt_Aciklama;
    private javax.swing.JTextField txt_Aciklama1;
    private javax.swing.JTextField txt_Ad;
    private javax.swing.JTextField txt_Ad1;
    private javax.swing.JTextField txt_Ad2;
    private javax.swing.JTextField txt_Adet;
    private javax.swing.JTextField txt_Adres;
    private javax.swing.JTextField txt_Mail10;
    private javax.swing.JTextField txt_Mail11;
    private javax.swing.JTextField txt_Mail13;
    private javax.swing.JTextField txt_Mail14;
    private javax.swing.JTextField txt_Soyad10;
    private javax.swing.JTextField txt_Soyad11;
    private javax.swing.JTextField txt_Soyad13;
    private javax.swing.JTextField txt_Soyad14;
    private javax.swing.JTextField txt_Tel10;
    private javax.swing.JTextField txt_Tel11;
    private javax.swing.JTextField txt_UrunAdet;
    private javax.swing.JTextField txt_UrunAdi;
    private javax.swing.JTextField txt_UrunAdı;
    private javax.swing.JTextField txt_ad10;
    private javax.swing.JTextField txt_adres10;
    private javax.swing.JTextField txt_araid;
    private javax.swing.JTextField txt_araid1;
    private javax.swing.JTextField txt_aratc;
    private javax.swing.JTextField txt_fiyat;
    private javax.swing.JTextField txt_fiyat1;
    private javax.swing.JTextField txt_id1;
    private javax.swing.JTextField txt_kullaniciadi;
    private javax.swing.JTextField txt_kullaniciadi1;
    private javax.swing.JTextField txt_sifre;
    private javax.swing.JTextField txt_sifre1;
    private javax.swing.JTextField txt_tc10;
    private javax.swing.JTextField txt_tc11;
    private javax.swing.JLabel urunekle_btn;
    private javax.swing.JPanel urunekle_panel;
    private javax.swing.JLabel urunguncelle_btn;
    private javax.swing.JPanel urunguncelle_panel;
    private javax.swing.JPanel urunislem_panel;
    private javax.swing.JPanel urunislemsecenek_panel;
    private javax.swing.JLabel urunlistele_btn;
    private javax.swing.JPanel urunlistele_panel;
    private javax.swing.JLabel yonetici_label;
    private javax.swing.JLabel yoneticiguncelle_label;
    private javax.swing.JTable yoneticikasiyer_table;
    private javax.swing.JLabel yoneticikasiyerekle_btn;
    private javax.swing.JPanel yoneticikasiyerekle_panel;
    private javax.swing.JLabel yoneticikasiyerguncelle_btn;
    private keeptoo.KGradientPanel yoneticikasiyerguncelle_panel;
    private javax.swing.JLabel yoneticislemleri_label;
    private javax.swing.JPanel yonetim_panel;
    private javax.swing.JPanel yonetimbilgi_panel;
    private javax.swing.JPanel yonetimislem_panel;
    private javax.swing.JSeparator yonetimislem_separator1;
    // End of variables declaration//GEN-END:variables
}
