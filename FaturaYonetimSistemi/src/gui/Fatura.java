/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import net.proteanit.sql.DbUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Fatura extends javax.swing.JFrame {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    double aratoplamPara = 0;
    double kdv = 0;
    double geneltoplam;

    double euroaratoplamPara, eurokdv, eurogeneltop, dolararatoplamPara, dolarkdv, dolargeneltop;
    Timer updateTimer;
    int delay = 100;

    String dovizgunceltarih;
    double dolarkur, eurokur;
    String tarih, saat;
    String faturano;
    
    boolean internetBaglanti;
    
    final ImageIcon soruisareti_icon = new ImageIcon(getClass().getResource("/iconlar/soruisareti_icon.png"));
    final ImageIcon ok_icon = new ImageIcon(getClass().getResource("/iconlar/ok_icon.png"));
    final ImageIcon unlem_icon = new ImageIcon(getClass().getResource("/iconlar/unlem_icon.png"));

    public Fatura() throws IOException, MalformedURLException, ParseException {
        initComponents();
        this.setLocationRelativeTo(null);
        internetBaglanti=internetBaglantiKontrol();
        jtable_panel.setVisible(false);
        tarihdigitalsaat(txt_saat, txt_tarih);
       
        if(internetBaglanti==true){
        dovizkurlari();  
        lbl_dolarkuryaz.setText(String.valueOf(dolarkur));
        lbl_eurokuryaz.setText(String.valueOf(eurokur));
        lbl_kurtarihyaz.setText(dovizgunceltarih);
        }
        
        else{
            lbl_dolarkuryaz.setText("Bağlantı yok!");
            lbl_eurokuryaz.setText("Bağlantı yok!");
            lbl_kurtarihyaz.setText("Bağlantı yok!");
            uyariMesajiPanel(unlem_icon, "Lütfen internet bağlantınızı kontrol edin! \nBağlantı olmadan Dolar ve Euro kurunda işlem yapamazsınız.", "Tamam", "", "Uyarı");
            }
        
        tabloayarlari(urun_tablo);
        tabloayarlari(jtable_ulistele);
        btn_turklira.setSelected(true);
        textAyarlari();

        butonAyari(btn_turklira);
        butonAyari(btn_dolar);
        butonAyari(btn_euro);
        butonAyari(btn_kredikart);

        try {
            conn = db.connect_db();

            Statement st = conn.createStatement();
            rs = st.executeQuery("SELECT MAX(FATURA_NO) FROM FATURALAR");
            if (rs.next()) {
                int faturaNo = rs.getInt(1) + 1;
                txt_faturano.setText(Integer.toString(faturaNo));
            }
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.toString());
        }

    }

    private void textAyarlari() {
        txt_aratc.setText("");
        txt_aratelno.setText("");
        txt_odenenpara.setEnabled(false);
        txt_aratop.setText("-");
        txt_kdv.setText("-");
        txt_geneltop.setText("-");
        txt_paraustu.setText("-");
        txt_aratop.setEnabled(false);
        txt_kdv.setEnabled(false);
        txt_geneltop.setEnabled(false);
        txt_paraustu.setEnabled(false);
        txt_mad.setText("-");
        txt_madres.setText("-");
        txt_memail.setText("-");
        txt_msoyad.setText("-");
        txt_mtcno.setText("-");
        txt_mtelefon.setText("-");
        txt_mad.setEnabled(false);
        txt_madres.setEnabled(false);
        txt_memail.setEnabled(false);
        txt_msoyad.setEnabled(false);
        txt_mtcno.setEnabled(false);
        txt_mtelefon.setEnabled(false);
        txt_uadet.setText("-");
        txt_ukategori.setText("-");
        txt_uid.setText("-");
        txt_uadi.setText("-");
        txt_uaciklama.setText("-");
        txt_ufiyat.setText("-");
        txt_uadet.setEnabled(false);
        txt_ukategori.setEnabled(false);
        txt_uid.setEnabled(false);
        txt_uadi.setEnabled(false);
        txt_ufiyat.setEnabled(false);
        txt_uaciklama.setEnabled(false);
    }

    private void tabloayarlari(JTable table) {
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setBackground(new Color(32, 136, 203));
        table.getTableHeader().setForeground(new Color(255, 255, 255));
        table.setRowHeight(25);
        table.setFillsViewportHeight(true);//Boş tabloyu beyaz olarak açtırır.
    }

    private void tarihdigitalsaat(JLabel saat1, JLabel tarih1) { //Fatura tarih saat methodu
           updateTimer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Date zaman = new Date();
                String saatformat = "HH:mm:ss";
                DateFormat format = new SimpleDateFormat(saatformat);
                saat = format.format(zaman);
                saat1.setText(saat);
            }
        });
        updateTimer.start();
        SimpleDateFormat dFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        tarih = dFormat.format(date);
        tarih1.setText(tarih);
    }

    private void Update_table(String sql, javax.swing.JTable tbl) { //Tabloya veri çekmek için
        try {
            conn = db.connect_db();
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            tbl.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {

            try {
                rs.close();
                pst.close();

            } catch (Exception e) {

            }
        }
    }

    private void setPanelEnabled(JPanel panel, Boolean isEnabled) { //Paneli aktif/deaktif etme
        panel.setEnabled(isEnabled);

        Component[] components = panel.getComponents();

        for (Component component : components) {
            if (component instanceof JPanel) {
                setPanelEnabled((JPanel) component, isEnabled);
            }
            component.setEnabled(isEnabled);
        }
    }

    private void dovizkurlari() throws MalformedURLException, IOException, ParseException { //Anlık döviz kuru çekme
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE); //Siteden gelen değer virgüllü olduğu için double çevirimini NumberFormat classı ile yapıyoruz
        URL url = new URL("https://finans.truncgil.com/today.json");
        JSONTokener tokener = new JSONTokener(url.openStream());
        JSONObject obj = new JSONObject(tokener);
        dovizgunceltarih = (String) obj.get("Update_Date");
        JSONObject usd = obj.getJSONObject("USD");
        Number usd1 = format.parse(usd.getString("Satış"));
        dolarkur = usd1.doubleValue();
        JSONObject euro = obj.getJSONObject("EUR");
        Number euro1 = format.parse(euro.getString("Satış"));
        eurokur = euro1.doubleValue();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fatura_panel = new keeptoo.KGradientPanel();
        lbl_saat = new javax.swing.JLabel();
        lbl_tarih = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        lbl_msoyad = new javax.swing.JLabel();
        lbl_mad = new javax.swing.JLabel();
        lbl_memail = new javax.swing.JLabel();
        lbl_mtelefon = new javax.swing.JLabel();
        lbl_uadet = new javax.swing.JLabel();
        lbl_uad = new javax.swing.JLabel();
        lbl_uaciklama = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txt_arauad = new javax.swing.JTextField();
        lbl_araurunad = new javax.swing.JLabel();
        lbl_ufiyat = new javax.swing.JLabel();
        lbl_aratop = new javax.swing.JLabel();
        lbl_paraustu = new javax.swing.JLabel();
        lbl_odenenmiktar = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lbl_aratc10 = new javax.swing.JLabel();
        txt_aratc = new javax.swing.JTextField();
        lbl_aratelno11 = new javax.swing.JLabel();
        txt_aratelno = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txt_mad = new javax.swing.JTextField();
        txt_msoyad = new javax.swing.JTextField();
        txt_mtelefon = new javax.swing.JTextField();
        txt_memail = new javax.swing.JTextField();
        lbl_mtc = new javax.swing.JLabel();
        txt_mtcno = new javax.swing.JTextField();
        lbl_madres = new javax.swing.JLabel();
        txt_madres = new javax.swing.JTextField();
        lbl_araurunid1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btn_ulistele = new javax.swing.JLabel();
        txt_uadi = new javax.swing.JTextField();
        txt_uadet = new javax.swing.JTextField();
        txt_ufiyat = new javax.swing.JTextField();
        txt_uaciklama = new javax.swing.JTextField();
        lbl_uid = new javax.swing.JLabel();
        txt_uid = new javax.swing.JTextField();
        btn_ekle = new keeptoo.KButton();
        btn_kaydet = new keeptoo.KButton();
        btn_kapat = new keeptoo.KButton();
        btn_temizle = new keeptoo.KButton();
        lbl_odemeyontem = new javax.swing.JLabel();
        txt_tarih = new javax.swing.JLabel();
        txt_saat = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        urun_tablo = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        btn_turklira = new javax.swing.JRadioButton();
        btn_kredikart = new javax.swing.JRadioButton();
        lbl_turklira = new javax.swing.JLabel();
        btn_dolar = new javax.swing.JRadioButton();
        lbl_dolar = new javax.swing.JLabel();
        btn_euro = new javax.swing.JRadioButton();
        lbl_euro = new javax.swing.JLabel();
        lbl_kredikart = new javax.swing.JLabel();
        txt_aratop = new javax.swing.JTextField();
        txt_odenenpara = new javax.swing.JTextField();
        txt_paraustu = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        lbl_kurdolar = new javax.swing.JLabel();
        lbl_dolarkuryaz = new javax.swing.JLabel();
        lbl_kureuro = new javax.swing.JLabel();
        lbl_eurokuryaz = new javax.swing.JLabel();
        lbl_kurtarih = new javax.swing.JLabel();
        lbl_kurtarihyaz = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        lbl_kdv = new javax.swing.JLabel();
        txt_kdv = new javax.swing.JTextField();
        lbl_geneltop = new javax.swing.JLabel();
        txt_geneltop = new javax.swing.JTextField();
        lbl_faturano = new javax.swing.JLabel();
        txt_faturano = new javax.swing.JLabel();
        txt_ukategori = new javax.swing.JTextField();
        lbl_kategori = new javax.swing.JLabel();
        btn_urunsil = new keeptoo.KButton();
        jtable_panel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtable_ulistele = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        btn_sec = new keeptoo.KButton();
        btn_Kapat = new keeptoo.KButton();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        fatura_panel.setkEndColor(new java.awt.Color(153, 255, 204));
        fatura_panel.setkGradientFocus(200);
        fatura_panel.setkStartColor(new java.awt.Color(204, 204, 255));
        fatura_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl_saat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_saat.setText("Saat:");
        fatura_panel.add(lbl_saat, new org.netbeans.lib.awtextra.AbsoluteConstraints(935, 49, -1, -1));

        lbl_tarih.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_tarih.setText("Tarih:");
        fatura_panel.add(lbl_tarih, new org.netbeans.lib.awtextra.AbsoluteConstraints(934, 13, -1, -1));
        fatura_panel.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 117, 1070, 10));

        lbl_msoyad.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_msoyad.setText("Soyad:");
        fatura_panel.add(lbl_msoyad, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 240, -1, -1));

        lbl_mad.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_mad.setText("Ad:");
        fatura_panel.add(lbl_mad, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 243, -1, -1));

        lbl_memail.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_memail.setText("E-Mail:");
        fatura_panel.add(lbl_memail, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 300, -1, -1));

        lbl_mtelefon.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_mtelefon.setText("Telefon:");
        fatura_panel.add(lbl_mtelefon, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 240, -1, -1));

        lbl_uadet.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_uadet.setText("Adet:");
        fatura_panel.add(lbl_uadet, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 520, -1, -1));

        lbl_uad.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_uad.setText("Ürün Adı:");
        fatura_panel.add(lbl_uad, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 530, -1, -1));

        lbl_uaciklama.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_uaciklama.setText("Açıklama:");
        fatura_panel.add(lbl_uaciklama, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 520, -1, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setText("Ürün Seç");
        fatura_panel.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 400, -1, -1));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setText("Müşteri Seç");
        fatura_panel.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 160, -1, -1));

        txt_arauad.setBackground(new Color(0,0,0,0)
        );
        txt_arauad.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_arauad.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txt_arauad.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(96, 83, 150), 2));
        txt_arauad.setOpaque(false);
        txt_arauad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_arauadActionPerformed(evt);
            }
        });
        fatura_panel.add(txt_arauad, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 370, 170, -1));

        lbl_araurunad.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_araurunad.setText("Ürün Adı");
        fatura_panel.add(lbl_araurunad, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 370, -1, -1));

        lbl_ufiyat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_ufiyat.setText("Fiyat(₺):");
        fatura_panel.add(lbl_ufiyat, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 520, -1, -1));

        lbl_aratop.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_aratop.setText("Ara Toplam");
        fatura_panel.add(lbl_aratop, new org.netbeans.lib.awtextra.AbsoluteConstraints(655, 710, -1, -1));

        lbl_paraustu.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_paraustu.setText("Para Üstü");
        fatura_panel.add(lbl_paraustu, new org.netbeans.lib.awtextra.AbsoluteConstraints(663, 921, -1, -1));

        lbl_odenenmiktar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_odenenmiktar.setText("Ödenen Miktar");
        fatura_panel.add(lbl_odenenmiktar, new org.netbeans.lib.awtextra.AbsoluteConstraints(645, 862, -1, -1));
        fatura_panel.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 350, 1080, 10));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_customer_40px.png"))); // NOI18N
        fatura_panel.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 120, -1, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_product_40px.png"))); // NOI18N
        fatura_panel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 360, -1, -1));

        lbl_aratc10.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_aratc10.setText("TC-No");
        fatura_panel.add(lbl_aratc10, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 130, -1, -1));

        txt_aratc.setBackground(new Color(0,0,0,0));
        txt_aratc.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        txt_aratc.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txt_aratc.setText("         ");
        txt_aratc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(96, 83, 150), 2));
        txt_aratc.setOpaque(false);
        txt_aratc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_aratcActionPerformed(evt);
            }
        });
        fatura_panel.add(txt_aratc, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 130, 170, -1));

        lbl_aratelno11.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_aratelno11.setText("Telefon-No");
        fatura_panel.add(lbl_aratelno11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, -1, -1));

        txt_aratelno.setBackground(new Color(0,0,0,0));
        txt_aratelno.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        txt_aratelno.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txt_aratelno.setText("         ");
        txt_aratelno.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(96, 83, 150), 2));
        txt_aratelno.setOpaque(false);
        txt_aratelno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_aratelnoActionPerformed(evt);
            }
        });
        fatura_panel.add(txt_aratelno, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 170, 170, -1));

        jLabel17.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        jLabel17.setText("Lütfen TC veya Telefon numarası ile arama yapınız.");
        jLabel17.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        fatura_panel.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 200, -1, -1));

        txt_mad.setBackground(new Color(0,0,0,0));
        txt_mad.setFont(new java.awt.Font("Segoe UI Semilight", 1, 15)); // NOI18N
        txt_mad.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_mad.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_mad.setOpaque(false);
        fatura_panel.add(txt_mad, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 240, 140, -1));

        txt_msoyad.setBackground(new Color(0,0,0,0));
        txt_msoyad.setFont(new java.awt.Font("Segoe UI Semilight", 1, 15)); // NOI18N
        txt_msoyad.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_msoyad.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_msoyad.setOpaque(false);
        fatura_panel.add(txt_msoyad, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 240, 140, -1));

        txt_mtelefon.setBackground(new Color(0,0,0,0));
        txt_mtelefon.setFont(new java.awt.Font("Segoe UI Semilight", 1, 15)); // NOI18N
        txt_mtelefon.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_mtelefon.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_mtelefon.setOpaque(false);
        fatura_panel.add(txt_mtelefon, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 240, 140, -1));

        txt_memail.setBackground(new Color(0,0,0,0));
        txt_memail.setFont(new java.awt.Font("Segoe UI Semilight", 1, 15)); // NOI18N
        txt_memail.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_memail.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_memail.setOpaque(false);
        fatura_panel.add(txt_memail, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 300, 210, -1));

        lbl_mtc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_mtc.setText("TC-No:");
        fatura_panel.add(lbl_mtc, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 240, -1, -1));

        txt_mtcno.setBackground(new Color(0,0,0,0));
        txt_mtcno.setFont(new java.awt.Font("Segoe UI Semilight", 1, 15)); // NOI18N
        txt_mtcno.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_mtcno.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_mtcno.setOpaque(false);
        fatura_panel.add(txt_mtcno, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 240, 140, -1));

        lbl_madres.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_madres.setText("Adres:");
        fatura_panel.add(lbl_madres, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 300, -1, -1));

        txt_madres.setBackground(new Color(0,0,0,0));
        txt_madres.setFont(new java.awt.Font("Segoe UI Semilight", 1, 15)); // NOI18N
        txt_madres.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_madres.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_madres.setOpaque(false);
        fatura_panel.add(txt_madres, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 300, 140, -1));

        lbl_araurunid1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_araurunid1.setText("Ürün Listesi");
        fatura_panel.add(lbl_araurunid1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 430, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        jLabel3.setText("Lütfen 'Ürün Adı' ile veya 'Ürün Listesi'nden ürün seçimi yapınız.");
        jLabel3.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        fatura_panel.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 440, -1, -1));

        btn_ulistele.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_bulleted_list_35px.png"))); // NOI18N
        btn_ulistele.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_ulistele.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_ulisteleMousePressed(evt);
            }
        });
        fatura_panel.add(btn_ulistele, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 420, -1, -1));

        txt_uadi.setBackground(new Color(0,0,0,0)
        );
        txt_uadi.setFont(new java.awt.Font("Segoe UI Semilight", 1, 15)); // NOI18N
        txt_uadi.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_uadi.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_uadi.setOpaque(false);
        fatura_panel.add(txt_uadi, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 520, 150, -1));

        txt_uadet.setBackground(new Color(0,0,0,0)
        );
        txt_uadet.setFont(new java.awt.Font("Segoe UI Semilight", 1, 15)); // NOI18N
        txt_uadet.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_uadet.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_uadet.setOpaque(false);
        fatura_panel.add(txt_uadet, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 520, 110, -1));

        txt_ufiyat.setBackground(new Color(0,0,0,0)
        );
        txt_ufiyat.setFont(new java.awt.Font("Segoe UI Semilight", 1, 15)); // NOI18N
        txt_ufiyat.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_ufiyat.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_ufiyat.setOpaque(false);
        fatura_panel.add(txt_ufiyat, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 520, 110, -1));

        txt_uaciklama.setBackground(new Color(0,0,0,0)
        );
        txt_uaciklama.setFont(new java.awt.Font("Segoe UI Semilight", 1, 15)); // NOI18N
        txt_uaciklama.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_uaciklama.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_uaciklama.setOpaque(false);
        fatura_panel.add(txt_uaciklama, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 520, 150, -1));

        lbl_uid.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_uid.setText("Ürün ID");
        fatura_panel.add(lbl_uid, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 480, -1, -1));

        txt_uid.setBackground(new Color(0,0,0,0));
        txt_uid.setFont(new java.awt.Font("Segoe UI Semilight", 1, 15)); // NOI18N
        txt_uid.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_uid.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));
        txt_uid.setOpaque(false);
        fatura_panel.add(txt_uid, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 480, 40, -1));

        btn_ekle.setText("Ürünü Ekle");
        btn_ekle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_ekle.setkBorderRadius(15);
        btn_ekle.setkEndColor(new java.awt.Color(153, 153, 255));
        btn_ekle.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_ekle.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_ekle.setkHoverStartColor(new java.awt.Color(153, 153, 255));
        btn_ekle.setkStartColor(new java.awt.Color(0, 204, 204));
        btn_ekle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ekleActionPerformed(evt);
            }
        });
        fatura_panel.add(btn_ekle, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 420, 90, 40));

        btn_kaydet.setText("Fatura Oluştur");
        btn_kaydet.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_kaydet.setkBorderRadius(15);
        btn_kaydet.setkEndColor(new java.awt.Color(153, 153, 255));
        btn_kaydet.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_kaydet.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_kaydet.setkHoverStartColor(new java.awt.Color(0, 255, 51));
        btn_kaydet.setkStartColor(new java.awt.Color(0, 204, 102));
        btn_kaydet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_kaydetActionPerformed(evt);
            }
        });
        fatura_panel.add(btn_kaydet, new org.netbeans.lib.awtextra.AbsoluteConstraints(467, 931, 100, 40));

        btn_kapat.setText("Kapat");
        btn_kapat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_kapat.setkBorderRadius(15);
        btn_kapat.setkEndColor(new java.awt.Color(153, 153, 153));
        btn_kapat.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_kapat.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_kapat.setkHoverStartColor(new java.awt.Color(102, 102, 102));
        btn_kapat.setkStartColor(new java.awt.Color(102, 102, 102));
        btn_kapat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_kapatActionPerformed(evt);
            }
        });
        fatura_panel.add(btn_kapat, new org.netbeans.lib.awtextra.AbsoluteConstraints(61, 931, 100, 40));

        btn_temizle.setText("Temizle");
        btn_temizle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_temizle.setkBorderRadius(15);
        btn_temizle.setkEndColor(new java.awt.Color(255, 102, 102));
        btn_temizle.setkHoverEndColor(new java.awt.Color(255, 102, 102));
        btn_temizle.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_temizle.setkHoverStartColor(new java.awt.Color(255, 0, 0));
        btn_temizle.setkStartColor(new java.awt.Color(255, 102, 102));
        btn_temizle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_temizleActionPerformed(evt);
            }
        });
        fatura_panel.add(btn_temizle, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 930, 90, 40));

        lbl_odemeyontem.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_odemeyontem.setText("Ödeme Yöntemi");
        fatura_panel.add(lbl_odemeyontem, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 620, -1, -1));

        txt_tarih.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        txt_tarih.setText("[tarih]");
        txt_tarih.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        fatura_panel.add(txt_tarih, new org.netbeans.lib.awtextra.AbsoluteConstraints(990, 13, -1, -1));

        txt_saat.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        txt_saat.setText("[saat]");
        txt_saat.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        fatura_panel.add(txt_saat, new org.netbeans.lib.awtextra.AbsoluteConstraints(990, 51, -1, -1));

        jScrollPane1.setBackground(new Color(0,0,0,0));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        urun_tablo.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        urun_tablo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kategori", "Ürün Adı", "Açıklama", "Fiyat", "Adet", "Ara Toplam"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        urun_tablo.setFocusable(false);
        urun_tablo.setIntercellSpacing(new java.awt.Dimension(0, 0));
        urun_tablo.setRowHeight(25);
        urun_tablo.setSelectionBackground(new java.awt.Color(232, 57, 95));
        urun_tablo.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(urun_tablo);

        fatura_panel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 610, 610, 289));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/faturaolustur-removebg-preview (2).png"))); // NOI18N
        fatura_panel.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(338, 0, -1, -1));

        btn_turklira.setBackground(new Color(0,0,0,0)
        );
        btn_turklira.setOpaque(false);
        btn_turklira.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_turkliraActionPerformed(evt);
            }
        });
        fatura_panel.add(btn_turklira, new org.netbeans.lib.awtextra.AbsoluteConstraints(757, 590, -1, -1));

        btn_kredikart.setBackground(new Color(0,0,0,0)
        );
        btn_kredikart.setOpaque(false);
        btn_kredikart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_kredikartActionPerformed(evt);
            }
        });
        fatura_panel.add(btn_kredikart, new org.netbeans.lib.awtextra.AbsoluteConstraints(927, 650, -1, -1));

        lbl_turklira.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lbl_turklira.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_turkish_lira_30px.png"))); // NOI18N
        lbl_turklira.setText(" Türk Lirası");
        fatura_panel.add(lbl_turklira, new org.netbeans.lib.awtextra.AbsoluteConstraints(787, 590, -1, -1));

        btn_dolar.setBackground(new Color(0,0,0,0)
        );
        btn_dolar.setOpaque(false);
        btn_dolar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_dolarActionPerformed(evt);
            }
        });
        fatura_panel.add(btn_dolar, new org.netbeans.lib.awtextra.AbsoluteConstraints(757, 650, -1, -1));

        lbl_dolar.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lbl_dolar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_us_dollar_30px.png"))); // NOI18N
        lbl_dolar.setText("Amerikan Doları");
        fatura_panel.add(lbl_dolar, new org.netbeans.lib.awtextra.AbsoluteConstraints(787, 650, -1, -1));

        btn_euro.setBackground(new Color(0,0,0,0)
        );
        btn_euro.setOpaque(false);
        btn_euro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_euroActionPerformed(evt);
            }
        });
        fatura_panel.add(btn_euro, new org.netbeans.lib.awtextra.AbsoluteConstraints(927, 590, -1, -1));

        lbl_euro.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lbl_euro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_euro_30px.png"))); // NOI18N
        lbl_euro.setText("Euro");
        fatura_panel.add(lbl_euro, new org.netbeans.lib.awtextra.AbsoluteConstraints(956, 590, -1, -1));

        lbl_kredikart.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lbl_kredikart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconlar/icons8_mastercard_credit_card_30px.png"))); // NOI18N
        lbl_kredikart.setText("Kredi Kartı");
        fatura_panel.add(lbl_kredikart, new org.netbeans.lib.awtextra.AbsoluteConstraints(956, 650, -1, -1));

        txt_aratop.setBackground(new Color(0,0,0,0));
        txt_aratop.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txt_aratop.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_aratop.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_aratop.setOpaque(false);
        fatura_panel.add(txt_aratop, new org.netbeans.lib.awtextra.AbsoluteConstraints(802, 710, 150, -1));

        txt_odenenpara.setBackground(new Color(0,0,0,0));
        txt_odenenpara.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txt_odenenpara.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_odenenpara.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_odenenpara.setOpaque(false);
        txt_odenenpara.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                txt_odenenparaMouseExited(evt);
            }
        });
        fatura_panel.add(txt_odenenpara, new org.netbeans.lib.awtextra.AbsoluteConstraints(802, 862, 150, -1));

        txt_paraustu.setBackground(new Color(0,0,0,0));
        txt_paraustu.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txt_paraustu.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_paraustu.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_paraustu.setOpaque(false);
        fatura_panel.add(txt_paraustu, new org.netbeans.lib.awtextra.AbsoluteConstraints(802, 924, 150, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Döviz Kurları");
        fatura_panel.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 0, -1, -1));

        lbl_kurdolar.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lbl_kurdolar.setText("Dolar:");
        fatura_panel.add(lbl_kurdolar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 42, -1, -1));

        lbl_dolarkuryaz.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        lbl_dolarkuryaz.setText("[kur]");
        lbl_dolarkuryaz.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        fatura_panel.add(lbl_dolarkuryaz, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, -1, -1));

        lbl_kureuro.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lbl_kureuro.setText("Euro:");
        fatura_panel.add(lbl_kureuro, new org.netbeans.lib.awtextra.AbsoluteConstraints(156, 44, -1, -1));

        lbl_eurokuryaz.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        lbl_eurokuryaz.setText("[kur]");
        lbl_eurokuryaz.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        fatura_panel.add(lbl_eurokuryaz, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 42, -1, -1));

        lbl_kurtarih.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lbl_kurtarih.setText("Güncellenme Tarihi:");
        fatura_panel.add(lbl_kurtarih, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, -1, -1));

        lbl_kurtarihyaz.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        lbl_kurtarihyaz.setText("[kur]");
        lbl_kurtarihyaz.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        fatura_panel.add(lbl_kurtarihyaz, new org.netbeans.lib.awtextra.AbsoluteConstraints(162, 88, -1, -1));
        fatura_panel.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 580, 1080, 10));

        lbl_kdv.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_kdv.setText("KDV(%18)");
        fatura_panel.add(lbl_kdv, new org.netbeans.lib.awtextra.AbsoluteConstraints(662, 760, -1, -1));

        txt_kdv.setBackground(new Color(0,0,0,0));
        txt_kdv.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txt_kdv.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_kdv.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_kdv.setOpaque(false);
        fatura_panel.add(txt_kdv, new org.netbeans.lib.awtextra.AbsoluteConstraints(802, 763, 150, -1));

        lbl_geneltop.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_geneltop.setText("Genel Toplam");
        fatura_panel.add(lbl_geneltop, new org.netbeans.lib.awtextra.AbsoluteConstraints(648, 812, -1, -1));

        txt_geneltop.setBackground(new Color(0,0,0,0));
        txt_geneltop.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txt_geneltop.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_geneltop.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_geneltop.setOpaque(false);
        fatura_panel.add(txt_geneltop, new org.netbeans.lib.awtextra.AbsoluteConstraints(802, 812, 150, -1));

        lbl_faturano.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lbl_faturano.setText("Fatura Numarası:");
        fatura_panel.add(lbl_faturano, new org.netbeans.lib.awtextra.AbsoluteConstraints(903, 90, -1, -1));

        txt_faturano.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_faturano.setForeground(new java.awt.Color(255, 51, 51));
        txt_faturano.setText("[no]");
        txt_faturano.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        fatura_panel.add(txt_faturano, new org.netbeans.lib.awtextra.AbsoluteConstraints(1021, 88, -1, -1));

        txt_ukategori.setBackground(new Color(0,0,0,0)
        );
        txt_ukategori.setFont(new java.awt.Font("Segoe UI Semilight", 1, 15)); // NOI18N
        txt_ukategori.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_ukategori.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255)));
        txt_ukategori.setOpaque(false);
        fatura_panel.add(txt_ukategori, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 520, 90, -1));

        lbl_kategori.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_kategori.setText("Kategori:");
        fatura_panel.add(lbl_kategori, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 530, -1, -1));

        btn_urunsil.setText("Ürünü Sil");
        btn_urunsil.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_urunsil.setkBorderRadius(15);
        btn_urunsil.setkEndColor(new java.awt.Color(255, 102, 102));
        btn_urunsil.setkHoverEndColor(new java.awt.Color(255, 102, 102));
        btn_urunsil.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_urunsil.setkHoverStartColor(new java.awt.Color(255, 0, 0));
        btn_urunsil.setkStartColor(new java.awt.Color(255, 102, 102));
        btn_urunsil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_urunsilActionPerformed(evt);
            }
        });
        fatura_panel.add(btn_urunsil, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 420, 90, 40));

        jtable_panel.setBackground(new java.awt.Color(238, 238, 255));
        jtable_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jtable_ulistele.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jtable_ulistele.setModel(new javax.swing.table.DefaultTableModel(
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
        jtable_ulistele.setFocusable(false);
        jtable_ulistele.setIntercellSpacing(new java.awt.Dimension(0, 0));
        jtable_ulistele.setRowHeight(25);
        jtable_ulistele.setSelectionBackground(new java.awt.Color(232, 57, 95));
        jScrollPane3.setViewportView(jtable_ulistele);

        jtable_panel.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 636, 205));

        jPanel1.setBackground(new java.awt.Color(238, 238, 255));

        btn_sec.setText("Seç");
        btn_sec.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_sec.setkBorderRadius(15);
        btn_sec.setkEndColor(new java.awt.Color(153, 153, 255));
        btn_sec.setkHoverEndColor(new java.awt.Color(0, 0, 0));
        btn_sec.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btn_sec.setkHoverStartColor(new java.awt.Color(0, 255, 51));
        btn_sec.setkStartColor(new java.awt.Color(0, 204, 102));
        btn_sec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_secActionPerformed(evt);
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

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel4.setText("Seçim yapınız");
        jLabel4.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(232, 57, 95)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel4))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_sec, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Kapat, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel4)
                .addGap(30, 30, 30)
                .addComponent(btn_sec, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(btn_Kapat, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jtable_panel.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(636, 0, 99, 205));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(fatura_panel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(172, Short.MAX_VALUE)
                    .addComponent(jtable_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(173, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(fatura_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(384, Short.MAX_VALUE)
                    .addComponent(jtable_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(403, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //PDF Fatura Tablo ayarlamaları
    public static PdfPCell getIRDCell(String text) {
        PdfPCell cell = new PdfPCell(new Paragraph(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5.0f);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        return cell;
    }

    public static PdfPCell getIRHCell(String text, int alignment) {
        FontSelector fs = new FontSelector();
        com.itextpdf.text.Font font = FontFactory.getFont(FontFactory.HELVETICA, 14);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    private void veritabaniEkle(String sql) {

        try {
            conn = db.connect_db();
            pst = conn.prepareStatement(sql);
            pst.execute();
          
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        } finally {
            try {
                conn.close();
            } catch (Exception ex) {

            }
        }

    }

    private int urunadet(String sql) {
        int urunadet = 0;
        try {
            conn = db.connect_db();
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                urunadet = Integer.parseInt(rs.getString(5));
            }
        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                conn.close();
            } catch (Exception ex) {

            }
        }
        return urunadet;
    }

   private static boolean internetBaglantiKontrol() {
    try {
        final URL url = new URL("http://www.google.com");
        final URLConnection conn = url.openConnection();
        conn.connect();
        conn.getInputStream().close();
        return true;
    } catch (MalformedURLException e) {
        throw new RuntimeException(e);
    } catch (IOException e) {
        return false;
    }
}
   ButtonGroup g = new ButtonGroup();
   private void butonAyari(JRadioButton button){
   g.add(button);
   }
   
    private void uyariMesajiPanel(ImageIcon icon,String mesaj,String secenek,String secenek1,String title){ //Uyarı mesajı kişiselleştirme
         JLabel label = new JLabel(mesaj);
         label.setFont(new Font("Segoe UI", Font.BOLD, 15));
         JFrame frame = new JFrame();
         String[] secenekler={secenek+secenek1};
         JOptionPane.showOptionDialog(frame.getContentPane(),label,title,0,JOptionPane.INFORMATION_MESSAGE,icon,secenekler,null);
         
    }

    private void txt_arauadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_arauadActionPerformed
        txt_uadet.setEnabled(true);
        String id = txt_arauad.getText();
        try {

            conn = db.connect_db();
            String sql = "SELECT * FROM URUNLER WHERE URUN_AD='" + txt_arauad.getText() + "'";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                txt_uid.setText(rs.getString(1));
                txt_ukategori.setText(rs.getString(2));
                txt_uadi.setText(rs.getString(3));
                txt_ufiyat.setText(rs.getString(4));
                txt_uadet.setText("1"); 
                txt_uaciklama.setText(rs.getString(6));
            } else {
                uyariMesajiPanel(unlem_icon, "Ürün kaydı bulunamadı!", "Tamam", "", "Hata");
                

                txt_uadet.setText("1");

            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                conn.close();
            } catch (Exception ex) {

            }
        }
        txt_uid.setEnabled(false);
        txt_uadi.setEnabled(false);
        txt_ufiyat.setEnabled(false);
        txt_ukategori.setEnabled(false);
        txt_uaciklama.setEnabled(false);


    }//GEN-LAST:event_txt_arauadActionPerformed

    private void txt_aratcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_aratcActionPerformed
        if (txt_aratelno.getText().length() == 0) {
            String tc = txt_aratc.getText();
            try {

                conn = db.connect_db();
                String sql = "SELECT * FROM MUSTERILER WHERE TC_NO='" + tc + "'";
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();
                if (rs.next()) {
                    txt_mad.setText(rs.getString(2));
                    txt_mtcno.setText(rs.getString(4));
                    txt_mtelefon.setText(rs.getString(5));
                    txt_memail.setText(rs.getString(6));
                    txt_madres.setText(rs.getString(7));
                    txt_msoyad.setText(rs.getString(3));
                } else {
                    uyariMesajiPanel(unlem_icon, "Müşteri kaydı bulunamadı!", "Tamam", "", "Hata");
                   
                }

            } catch (Exception e) {

                JOptionPane.showMessageDialog(null, e);
            } finally {
                try {
                    conn.close();
                } catch (Exception ex) {

                }
            }
        } else {
            uyariMesajiPanel(unlem_icon, "Lütfen sadece TC veya telefon numarası ile arama yapınız!", "Tamam", "", "Uyarı");
            }
    }//GEN-LAST:event_txt_aratcActionPerformed

    private void btn_ulisteleMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_ulisteleMousePressed
        setPanelEnabled(fatura_panel, false);
        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            Logger.getLogger(Fatura.class.getName()).log(Level.SEVERE, null, ex);
        }
        btn_ekle.setVisible(false);
        btn_urunsil.setVisible(false);
        Update_table("select * from URUNLER", jtable_ulistele);
        jtable_panel.setVisible(true);


    }//GEN-LAST:event_btn_ulisteleMousePressed

    private void btn_KapatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_KapatActionPerformed
        jtable_panel.setVisible(false);
        txt_arauad.setFocusable(true);
        btn_ekle.setVisible(true);
        btn_urunsil.setVisible(true);
        setPanelEnabled(fatura_panel, true);
        txt_aratop.setEnabled(false);
        txt_kdv.setEnabled(false);
        txt_geneltop.setEnabled(false);
        txt_paraustu.setEnabled(false);
        txt_ukategori.setEnabled(false);
        txt_uid.setEnabled(false);
        txt_uadi.setEnabled(false);
        txt_ufiyat.setEnabled(false);
        txt_uaciklama.setEnabled(false);
    }//GEN-LAST:event_btn_KapatActionPerformed

    private void btn_secActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_secActionPerformed
       try{
       TableModel model = jtable_ulistele.getModel();
       int i = jtable_ulistele.getSelectedRow();
           
       if(String.valueOf(model.getValueAt(i, 4)).equals("Stokta yok")) {
           uyariMesajiPanel(unlem_icon, "Ürün stokta yok!", "Tamam", "", "Hata");
                    
       }
       else{
           
            txt_uid.setText(model.getValueAt(i, 0).toString());
            txt_ukategori.setText(model.getValueAt(i, 1).toString());
            txt_uadi.setText(model.getValueAt(i, 2).toString());
            txt_ufiyat.setText(model.getValueAt(i, 3).toString());
            txt_uaciklama.setText(model.getValueAt(i, 5).toString());
            jtable_panel.setVisible(false);
            setPanelEnabled(fatura_panel, true);
            txt_aratop.setEnabled(false);
            txt_kdv.setEnabled(false);
            txt_geneltop.setEnabled(false);
            txt_paraustu.setEnabled(false);
            txt_arauad.setEnabled(true);
            txt_arauad.setText("");
            btn_ekle.setVisible(true);
            btn_urunsil.setVisible(true);
            txt_uadet.setEnabled(true);
            txt_uadet.setText("1");
            txt_mad.setEnabled(false);
            txt_ukategori.setEnabled(false);
            txt_madres.setEnabled(false);
            txt_memail.setEnabled(false);
            txt_msoyad.setEnabled(false);
            txt_mtcno.setEnabled(false);
            txt_mtelefon.setEnabled(false);
            txt_uid.setEnabled(false);
            txt_uadi.setEnabled(false);
            txt_ufiyat.setEnabled(false);
            txt_uaciklama.setEnabled(false);
            txt_odenenpara.setEnabled(false);
        } 
       }       
        catch (Exception ex) {
            uyariMesajiPanel(unlem_icon, "Lütfen seçim yapınız!", "Tamam", "", "Uyarı");
            }
        
    }//GEN-LAST:event_btn_secActionPerformed
    

    private void btn_ekleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ekleActionPerformed
        try{
         if (txt_uadi.getText().length() >= 2 && txt_uadet.getText().length() > 0 && Integer.parseInt(txt_uadet.getText())>0) {
            int urunadet = 0;
            btn_turklira.setSelected(true);
            txt_odenenpara.setEnabled(true);
            btn_euro.setSelected(false);
            btn_dolar.setSelected(false);
            btn_kredikart.setSelected(false);
            double fiyat = Double.parseDouble(txt_ufiyat.getText());
            int adet = Integer.parseInt(txt_uadet.getText());
            double total = adet * fiyat;

            String sql = "SELECT * FROM URUNLER WHERE ID='" + txt_uid.getText() + "'";
            
            urunadet = urunadet(sql);                      
             int[]stokkontrol=new int[urun_tablo.getRowCount()];       
            if (Integer.parseInt(txt_uadet.getText()) <= urunadet && Integer.parseInt(txt_uadet.getText()) != 0) {
                DefaultTableModel model = (DefaultTableModel) urun_tablo.getModel();             
                                     
                for(int i=0;i<urun_tablo.getRowCount();i++){ 
                   if(txt_uadi.getText().equals(urun_tablo.getValueAt(i, 1).toString())){
                    stokkontrol[i]=stokkontrol[i]+Integer.parseInt(urun_tablo.getValueAt(i, 4).toString());
                     urunadet=urunadet-stokkontrol[i];
                     if(urunadet-Integer.parseInt(txt_uadet.getText())<0){
                         urunadet=urunadet-Integer.parseInt(txt_uadet.getText());
                     }
                   }               
                 }
                if(urunadet==0 || urunadet<0){
                        uyariMesajiPanel(unlem_icon, "Ürün stok adedinden fazla ürün eklenemez!", "Tamam", "", "Uyarı");
                       }
                else{
                model.addRow(new Object[]{txt_ukategori.getText(), txt_uadi.getText(), txt_uaciklama.getText(), fiyat + " TL", adet, total +" TL"});
                aratoplamPara = aratoplamPara + total;
                kdv = aratoplamPara * 0.18;
                geneltoplam = aratoplamPara + kdv;
                txt_aratop.setText(String.format("%.2f TL", aratoplamPara));
                txt_kdv.setText(String.format("%.2f TL", kdv));
                txt_geneltop.setText(String.format("%.2f TL", geneltoplam)); 
                txt_arauad.setEnabled(true);
                }                 
                
                
            } else {
             uyariMesajiPanel(unlem_icon, "Ürün stok adedinden fazla miktar girilemez! Ürün stok miktarı: " + urunadet, "Tamam", "", "Uyarı");    
                          

            }
        } else {
            if(Integer.parseInt(txt_uadet.getText())<0){
                uyariMesajiPanel(unlem_icon, "Negatif değer girilemez!", "Tamam", "", "Uyarı");
                
            } 
            else{
                uyariMesajiPanel(unlem_icon, "Ürün adedini 0 giremezsiniz!", "Tamam", "", "Uyarı");
              }
            
        }   
        }catch(Exception ex){
            if(txt_uid.getText().equals("-")){
             uyariMesajiPanel(unlem_icon, "Lütfen ürün seçiniz!", "Tamam", "", "Hata");
             
            }
            else{
              uyariMesajiPanel(unlem_icon, "Boş veya ondalıklı değer girilemez!", "Tamam", "", "Uyarı");  
             
            }
            
        }
        

    }//GEN-LAST:event_btn_ekleActionPerformed

    private void btn_kaydetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_kaydetActionPerformed
        if (txt_mad.getText().length() > 1 && txt_uid.getText().length() > 0 && txt_odenenpara.getText().length() > 0) {
            
            String isim = txt_mad.getText();
            String soyad = txt_msoyad.getText();
            String telefon = txt_mtelefon.getText();
            String email = txt_memail.getText();
            String path = "D:\\";
            com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
            

            try {
                Image image = Image.getInstance("src/iconlar/faturalogo.png");//Üst logo
                image.scaleAbsolute(175, 125f);//logoyu boyutlandırma
                PdfWriter.getInstance(doc, new FileOutputStream(path + "" + isim + " " +soyad+" "+txt_faturano.getText()+" "+ txt_tarih.getText() + ".pdf"));
                doc.open();
                LineSeparator ls = new LineSeparator();
                Paragraph bosluk = new Paragraph("\n");
                doc.add(image);
                doc.add(bosluk);
                PdfPTable tarih = new PdfPTable(4);// PDF'te tarih ve saat için 2 sütunluk tablo oluşturma
                tarih.addCell(getIRDCell("Fatura-NO"));
                tarih.addCell(getIRDCell("Tarih"));
                tarih.addCell(getIRDCell("Saat"));
                tarih.addCell(getIRDCell("Faturayi Düzenleyen"));
                tarih.addCell(getIRDCell(txt_faturano.getText()));
                tarih.addCell(getIRDCell(this.tarih));
                tarih.addCell(getIRDCell(saat));
                tarih.addCell(getIRDCell(Kullanicisim.kullanicisim));
                
                PdfPTable faturabilgi = new PdfPTable(3);
                faturabilgi.setWidthPercentage(170);
                faturabilgi.setHorizontalAlignment(Element.ALIGN_RIGHT);
                faturabilgi.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
                faturabilgi.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
                faturabilgi.addCell(getIRHCell("Fatura Bilgileri", PdfPCell.ALIGN_RIGHT));
                faturabilgi.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
                faturabilgi.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
                PdfPCell tablo = new PdfPCell(tarih);
                tablo.setBorder(0);
                faturabilgi.addCell(tablo);
                doc.add(faturabilgi);
                Paragraph bosluk1 = new Paragraph("\n \n");
            
                doc.add(bosluk1);
                
                Paragraph paragraph5=new Paragraph("Müsteri Ad Soyad: "+txt_mad.getText()+" "+txt_msoyad.getText()+"\nMüsteri TC-NO: "+txt_mtcno.getText()+" "+"\nMüsteri Telefon-NO: "+txt_mtelefon.getText()+"\nMüsteri E-Mail: "+txt_memail.getText()+"\nMüsteri Adres: "+txt_madres.getText());
                doc.add(paragraph5);
                
                PdfPTable tbl = new PdfPTable(6);
                tbl.setWidthPercentage(100);
                tbl.addCell("Kategori");
                tbl.addCell("Ürün Adı");
                tbl.addCell("Açıklama");
                tbl.addCell("Fiyat");
                tbl.addCell("Adet");
                tbl.addCell("Ara Toplam");
                for (int i = 0; i < urun_tablo.getRowCount(); i++) {
                    String a = urun_tablo.getValueAt(i, 0).toString();
                    String b = urun_tablo.getValueAt(i, 1).toString();
                    String c = urun_tablo.getValueAt(i, 2).toString();
                    String d = urun_tablo.getValueAt(i, 3).toString();
                    String e = urun_tablo.getValueAt(i, 4).toString();
                    String f = urun_tablo.getValueAt(i, 5).toString();
                    tbl.addCell(a);
                    tbl.addCell(b);
                    tbl.addCell(c);
                    tbl.addCell(d);
                    tbl.addCell(e);
                    tbl.addCell(f);
                }
                String odemeturu=null;
                if(btn_turklira.isSelected()==true){
                    odemeturu="Türk Lirasi";
                }
                else if(btn_dolar.isSelected()==true){
                    odemeturu="Dolar";
                }
                else if(btn_euro.isSelected()==true){
                    odemeturu="Euro";
                }
                else{
                    odemeturu="Kredi Karti(POS)";
                }
                doc.add(new Chunk(ls));
                doc.add(tbl);
                doc.add(new Chunk(ls));
                Paragraph paragraph3 = new Paragraph("Ödeme Türü: "+odemeturu+"\nAra Toplam: " + txt_aratop.getText() + "\nKDV: " + txt_kdv.getText() + "\nGenel Toplam: " + txt_geneltop.getText() + "\nÖdenen Ücret: "+txt_odenenpara.getText()+"\nPara Üstü: "+txt_paraustu.getText());
                doc.add(paragraph3);
                Paragraph paragraph4=new Paragraph("\n Ziyaretiniz için tesekkür ederiz ! Yine bekleriz.");
                paragraph4.setAlignment(Element.ALIGN_CENTER);
                doc.add(new Chunk(ls));
                doc.add(paragraph4);
                String odemeyontem;
                if (btn_kredikart.isSelected() == true) {
                    odemeyontem = "Kredi Kartı";
                } else {
                    odemeyontem = "Nakit";
                }

                String sql = "INSERT INTO FATURALAR (FATURA_NO,MUSTERI_TC,MUSTERI_ADI,ODEME_YONTEMI,ARATOPLAM,KDV,GENELTOPLAM)"
                        + " VALUES('" + txt_faturano.getText() + "','" + txt_mtcno.getText() + "','" + txt_mad.getText() + "','" + odemeyontem + "','"
                        + txt_aratop.getText() + "','" + txt_kdv.getText() + "','" + txt_geneltop.getText() + "')";
                veritabaniEkle(sql);

                for (int i = 0; i < urun_tablo.getRowCount(); i++) {
                    String urunkategori = urun_tablo.getValueAt(i, 0).toString();
                    String urunad = urun_tablo.getValueAt(i, 1).toString();
                    String urunaciklama = urun_tablo.getValueAt(i, 2).toString();
                    String urunfiyat = urun_tablo.getValueAt(i, 3).toString();
                    String urunadet = urun_tablo.getValueAt(i, 4).toString();
                    String urunaratop = urun_tablo.getValueAt(i, 5).toString();
                    String sql1 = "INSERT INTO FATURA_URUNLER (FATURA_NO,URUN_ADI,KATEGORI,ACIKLAMA,ADET,FIYAT,TOPLAM)"
                            + " VALUES('" + txt_faturano.getText() + "','" + urunad + "','" + urunkategori + "','" + urunaciklama + "','"
                            + urunadet + "','" + urunfiyat + "','" + urunaratop + "')";
                    veritabaniEkle(sql1);
                    
                    int urunstok = 0;
                    int stokkalan = 0;
                    String urunstoksql = "SELECT * FROM URUNLER WHERE URUN_AD='" + urunad + "'";
                    urunstok = urunadet(urunstoksql);
                    
                    String stokyok="Stokta yok";
                  
                    stokkalan=urunstok - Integer.parseInt(urunadet);
                    
                    if(stokkalan==0){
                     String sqlguncellesifir = "UPDATE URUNLER SET ADET='" + stokyok + "' WHERE URUN_AD='" + urunad + "'";
                     veritabaniEkle(sqlguncellesifir);
                    }
                    else{
                    String sqlguncelle = "UPDATE URUNLER SET ADET='" + String.valueOf(stokkalan) + "' WHERE URUN_AD='" + urunad + "'";
                    
                    veritabaniEkle(sqlguncelle);
                    }
                    
                }
                uyariMesajiPanel(ok_icon, "Fatura başarıyla oluşturulup kaydedildi!", "Tamam", "", "Fatura Oluşturuldu!");  
                
                setVisible(false);
                new Fatura().setVisible(true);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
            doc.close();

        } else {
            uyariMesajiPanel(unlem_icon, "Bilgileri eksiksiz girdiğinizden emin olun!", "Tamam", "", "Uyarı");  
           }


    }//GEN-LAST:event_btn_kaydetActionPerformed

    private void btn_kapatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_kapatActionPerformed
      setVisible(false);
      new AnaEkran().setVisible(true);

    }//GEN-LAST:event_btn_kapatActionPerformed

    private void btn_temizleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_temizleActionPerformed
               
        setVisible(false);
        try {
            new Fatura().setVisible(true);
        } catch (IOException ex) {
            Logger.getLogger(Fatura.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Fatura.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btn_temizleActionPerformed

    private void btn_turkliraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_turkliraActionPerformed
        btn_dolar.setSelected(false);
        btn_euro.setSelected(false);
        txt_odenenpara.setEnabled(true);
        btn_kredikart.setSelected(false);
        txt_paraustu.setText("");
        txt_odenenpara.setText("");
        txt_aratop.setText(String.format("%.2f TL", aratoplamPara));
        txt_kdv.setText(String.format("%.2f TL", kdv));
        txt_geneltop.setText(String.format("%.2f TL", geneltoplam));
    }//GEN-LAST:event_btn_turkliraActionPerformed

    private void btn_dolarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_dolarActionPerformed
        if(internetBaglanti==true){
        btn_turklira.setSelected(false);
        txt_odenenpara.setEnabled(true);
        btn_euro.setSelected(false);
        btn_kredikart.setSelected(false);
        dolararatoplamPara = aratoplamPara / dolarkur;
        dolarkdv = kdv / dolarkur;
        dolargeneltop = geneltoplam / dolarkur;
        txt_paraustu.setText("");
        txt_odenenpara.setText("");
        txt_aratop.setText(String.format("%.2f $", dolararatoplamPara));
        txt_kdv.setText(String.format("%.2f $", dolarkdv));
        txt_geneltop.setText(String.format("%.2f $", dolargeneltop));    
        }
        else{
            btn_turklira.setSelected(true);
        }
    }//GEN-LAST:event_btn_dolarActionPerformed

    private void btn_euroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_euroActionPerformed
        if(internetBaglanti==true){
        txt_odenenpara.setEnabled(true);
        btn_turklira.setSelected(false);
        btn_dolar.setSelected(false);
        btn_kredikart.setSelected(false);
        euroaratoplamPara = aratoplamPara / eurokur;
        eurokdv = kdv / eurokur;
        eurogeneltop = geneltoplam / eurokur;
        txt_paraustu.setText("");
        txt_odenenpara.setText("");
        txt_aratop.setText(String.format("%.2f €", euroaratoplamPara));
        txt_kdv.setText(String.format("%.2f €", eurokdv));
        txt_geneltop.setText(String.format("%.2f €", eurogeneltop));   
        }
        else{
            btn_turklira.setSelected(true);
        }
    }//GEN-LAST:event_btn_euroActionPerformed

    private void btn_kredikartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_kredikartActionPerformed
        btn_turklira.setSelected(false);
        btn_euro.setSelected(false);
        btn_dolar.setSelected(false);
        txt_paraustu.setText("Kredi Kartı");
        txt_odenenpara.setText("Kredi Kartı");
        txt_aratop.setText(String.format("%.2f TL", aratoplamPara));
        txt_kdv.setText(String.format("%.2f TL", kdv));
        txt_geneltop.setText(String.format("%.2f TL", geneltoplam));
        txt_odenenpara.setEnabled(false);
    }//GEN-LAST:event_btn_kredikartActionPerformed

    private void txt_aratelnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_aratelnoActionPerformed
        if (txt_aratc.getText().length() == 0) {
            String tel = txt_aratelno.getText();
            try {

                conn = db.connect_db();
                String sql = "SELECT * FROM MUSTERILER WHERE TELEFON_NO like '" + tel + "%'";
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();
                if (rs.next()) {
                    txt_mad.setText(rs.getString(2));
                    txt_mtelefon.setText(rs.getString(5));
                    txt_mtcno.setText(rs.getString(4));
                    txt_memail.setText(rs.getString(6));
                    txt_msoyad.setText(rs.getString(3));
                    txt_madres.setText(rs.getString(7));
                } else {
                    uyariMesajiPanel(unlem_icon, "Müşteri kaydı bulunamadı!", "Tamam", "", "Hata");  
                    }

            } catch (Exception e) {

                JOptionPane.showMessageDialog(null, e);
            } finally {
                try {
                    conn.close();
                } catch (Exception ex) {

                }
            }
        } else {
            uyariMesajiPanel(unlem_icon, "Lütfen sadece TC veya telefon numarası ile arama yapınız!", "Tamam", "", "Uyarı");
        }
    }//GEN-LAST:event_txt_aratelnoActionPerformed

    private void txt_odenenparaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txt_odenenparaMouseExited
        if (txt_odenenpara.getText().length() > 0) {
            if (btn_dolar.isSelected() == true) {
                Double odenenmiktar = Double.parseDouble(txt_odenenpara.getText());
                if (odenenmiktar >= dolargeneltop) {
                    odenenmiktar = (odenenmiktar - dolargeneltop) * dolarkur;
                    txt_paraustu.setText(String.format("%.2f TL", odenenmiktar));
                    txt_paraustu.setEditable(false);
                } else {
                    uyariMesajiPanel(unlem_icon, "Genel toplamdan az değer girilemez!", "Tamam", "", "Uyarı");  
                    txt_paraustu.setText("");
                    txt_odenenpara.setText("");
                }
            } else if (btn_euro.isSelected() == true) {
                Double odenenmiktar = Double.parseDouble(txt_odenenpara.getText());
                if (odenenmiktar >= eurogeneltop) {
                    odenenmiktar = (odenenmiktar - eurogeneltop) * eurokur;
                    txt_paraustu.setText(String.format("%.2f TL", odenenmiktar));
                    txt_paraustu.setEditable(false);
                } else {
                    uyariMesajiPanel(unlem_icon, "Genel toplamdan az değer girilemez!", "Tamam", "", "Uyarı"); 
                    txt_paraustu.setText("");
                    txt_odenenpara.setText("");
                }
            } else if (btn_turklira.isSelected() == true) {
                Double odenenmiktar = Double.parseDouble(txt_odenenpara.getText());
                if (odenenmiktar >= geneltoplam) {
                    odenenmiktar = odenenmiktar - geneltoplam;
                    txt_paraustu.setText(String.format("%.2f TL", odenenmiktar));
                    txt_paraustu.setEditable(false);
                } else {
                    uyariMesajiPanel(unlem_icon, "Genel toplamdan az değer girilemez!", "Tamam", "", "Uyarı"); 
                    txt_paraustu.setText("");
                    txt_odenenpara.setText("");
                }
            }

        }

    }//GEN-LAST:event_txt_odenenparaMouseExited

    private void btn_urunsilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_urunsilActionPerformed
       DefaultTableModel model = (DefaultTableModel) urun_tablo.getModel();       
       int secilen = urun_tablo.getSelectedRow();
       if(secilen>=0){
       btn_turklira.setSelected(true);
       txt_odenenpara.setText("");
       txt_paraustu.setText("");
       model.removeRow(secilen);
       double kalanaratoplam = 0;
       for (int i = 0; i < urun_tablo.getRowCount(); i++){
        String a=String.valueOf(urun_tablo.getValueAt(i, 5));
        String[] b=a.split(" ");
        Double aratop = Double.parseDouble(b[0]);
        kalanaratoplam += aratop;
    }
       if(btn_turklira.isSelected()==true){
       aratoplamPara = kalanaratoplam;
       kdv = aratoplamPara * 0.18;
       geneltoplam = aratoplamPara + kdv;
       txt_aratop.setText(String.format("%.2f TL", kalanaratoplam));
       txt_kdv.setText(String.format("%.2f TL", kalanaratoplam*0.18));
       txt_geneltop.setText(String.format("%.2f TL", (kalanaratoplam*0.18)+kalanaratoplam));
       }
       else if(btn_dolar.isSelected()==true){
        kalanaratoplam=kalanaratoplam/dolarkur;
        dolararatoplamPara = kalanaratoplam;
        dolarkdv = kalanaratoplam *0.18;
        dolargeneltop = dolarkdv+dolararatoplamPara;
        txt_aratop.setText(String.format("%.2f $", dolararatoplamPara));
        txt_kdv.setText(String.format("%.2f $", dolarkdv));
        txt_geneltop.setText(String.format("%.2f $", dolargeneltop));   
       }
       else if(btn_euro.isSelected()==true){
        kalanaratoplam=kalanaratoplam/eurokur;
        euroaratoplamPara = kalanaratoplam;
        eurokdv = kalanaratoplam *0.18;
        eurogeneltop = eurokdv+euroaratoplamPara;
        txt_aratop.setText(String.format("%.2f €", euroaratoplamPara));
        txt_kdv.setText(String.format("%.2f €", eurokdv));
        txt_geneltop.setText(String.format("%.2f €", eurogeneltop));   
       }    
       }else{
           uyariMesajiPanel(unlem_icon, "Lütfen silmek istediğiniz ürünü seçiniz!", "Tamam", "", "Uyarı"); 
          }
       
    }//GEN-LAST:event_btn_urunsilActionPerformed

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
            java.util.logging.Logger.getLogger(Fatura.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Fatura.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Fatura.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Fatura.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Fatura().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(Fatura.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(Fatura.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private keeptoo.KButton btn_Kapat;
    private javax.swing.JRadioButton btn_dolar;
    private keeptoo.KButton btn_ekle;
    private javax.swing.JRadioButton btn_euro;
    private keeptoo.KButton btn_kapat;
    private keeptoo.KButton btn_kaydet;
    private javax.swing.JRadioButton btn_kredikart;
    private keeptoo.KButton btn_sec;
    private keeptoo.KButton btn_temizle;
    private javax.swing.JRadioButton btn_turklira;
    private javax.swing.JLabel btn_ulistele;
    private keeptoo.KButton btn_urunsil;
    private keeptoo.KGradientPanel fatura_panel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JPanel jtable_panel;
    private javax.swing.JTable jtable_ulistele;
    private javax.swing.JLabel lbl_aratc10;
    private javax.swing.JLabel lbl_aratelno11;
    private javax.swing.JLabel lbl_aratop;
    private javax.swing.JLabel lbl_araurunad;
    private javax.swing.JLabel lbl_araurunid1;
    private javax.swing.JLabel lbl_dolar;
    private javax.swing.JLabel lbl_dolarkuryaz;
    private javax.swing.JLabel lbl_euro;
    private javax.swing.JLabel lbl_eurokuryaz;
    private javax.swing.JLabel lbl_faturano;
    private javax.swing.JLabel lbl_geneltop;
    private javax.swing.JLabel lbl_kategori;
    private javax.swing.JLabel lbl_kdv;
    private javax.swing.JLabel lbl_kredikart;
    private javax.swing.JLabel lbl_kurdolar;
    private javax.swing.JLabel lbl_kureuro;
    private javax.swing.JLabel lbl_kurtarih;
    private javax.swing.JLabel lbl_kurtarihyaz;
    private javax.swing.JLabel lbl_mad;
    private javax.swing.JLabel lbl_madres;
    private javax.swing.JLabel lbl_memail;
    private javax.swing.JLabel lbl_msoyad;
    private javax.swing.JLabel lbl_mtc;
    private javax.swing.JLabel lbl_mtelefon;
    private javax.swing.JLabel lbl_odemeyontem;
    private javax.swing.JLabel lbl_odenenmiktar;
    private javax.swing.JLabel lbl_paraustu;
    private javax.swing.JLabel lbl_saat;
    private javax.swing.JLabel lbl_tarih;
    private javax.swing.JLabel lbl_turklira;
    private javax.swing.JLabel lbl_uaciklama;
    private javax.swing.JLabel lbl_uad;
    private javax.swing.JLabel lbl_uadet;
    private javax.swing.JLabel lbl_ufiyat;
    private javax.swing.JLabel lbl_uid;
    private javax.swing.JTextField txt_aratc;
    private javax.swing.JTextField txt_aratelno;
    private javax.swing.JTextField txt_aratop;
    private javax.swing.JTextField txt_arauad;
    private javax.swing.JLabel txt_faturano;
    private javax.swing.JTextField txt_geneltop;
    private javax.swing.JTextField txt_kdv;
    private javax.swing.JTextField txt_mad;
    private javax.swing.JTextField txt_madres;
    private javax.swing.JTextField txt_memail;
    private javax.swing.JTextField txt_msoyad;
    private javax.swing.JTextField txt_mtcno;
    private javax.swing.JTextField txt_mtelefon;
    private javax.swing.JTextField txt_odenenpara;
    private javax.swing.JTextField txt_paraustu;
    private javax.swing.JLabel txt_saat;
    private javax.swing.JLabel txt_tarih;
    private javax.swing.JTextField txt_uaciklama;
    private javax.swing.JTextField txt_uadet;
    private javax.swing.JTextField txt_uadi;
    private javax.swing.JTextField txt_ufiyat;
    private javax.swing.JTextField txt_uid;
    private javax.swing.JTextField txt_ukategori;
    private javax.swing.JTable urun_tablo;
    // End of variables declaration//GEN-END:variables
}
