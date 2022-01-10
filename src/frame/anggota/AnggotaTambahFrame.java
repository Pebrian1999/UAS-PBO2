
package frame.anggota;

import db.koneksi;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.Anggota;
import model.KeyValue;
import model.Petugas;


public class AnggotaTambahFrame extends javax.swing.JFrame {

    BufferedImage bImage;
    int status;
    Statement st;
    ResultSet rs;
    PreparedStatement ps;
    SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
    String qryPetugas = "SELECT * FROM petugas ORDER BY nama_petugas";
    
    
    private final int SEDANG_TAMBAH = 101;
    private final int SEDANG_UBAH = 102;
    private final int IMG_WIDTH = 183;
    private final int IMG_HEIGHT = 224;
    
    public void rbJenisKelaminSetSelected(String jenisKelamin){
        if(jenisKelamin.equals("Laki-Laki"))
            rbLaki.setSelected(true);
        else
            rbPerempuan.setSelected(true);
    }
    
    public String rbJenisKelaminGetSelected(){
        if(rbLaki.isSelected())
            return "Laki-Laki";
        else if(rbPerempuan.isSelected())
            return "Perempuan";
        else
            return"";
    }
    
    public Vector getCbData(String qry, String key, String value){
        Vector v = new Vector();
        try{
            koneksi koneksi = new koneksi();
            Connection connection = koneksi.getConnection();
            st = connection.createStatement();
            rs = st.executeQuery(qry);
            while(rs.next()){
                v.addElement(new KeyValue(rs.getInt(key),rs.getString(value)));
            }
        } catch (SQLException ex){
            System.err.println("error getData(): "+ex);
        } return v;
    }
    
    public void cbSetModel(String qry, String key, String value, JComboBox<String> jcb){
        Vector v = getCbData(qry, key, value);
        DefaultComboBoxModel model;
        model = new DefaultComboBoxModel(v);
        jcb.setModel(model);
    }
    
    public void cbSetSelected(String data, JComboBox<String> cb){
        KeyValue item = new KeyValue();
        for (int i=0;i < cb.getItemCount(); i++)
        {
            cb.setSelectedIndex(i);
            item.setValue(((KeyValue)cb.getSelectedItem()).getValue());
            if (item.getValue().equalsIgnoreCase(data))
            {
                cb.setSelectedIndex(i);
                break;
            }
        }
    }
    
    public String makeId(){
        String id,idDate,idSem = null;
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
        idDate = df.format(now);
        id= idDate+001;
        try{
            koneksi koneksi = new koneksi();
            Connection connection = koneksi.getConnection();
            String query = "SELECT * FROM anggota where id LIKE ? " + "ORDER BY id DESC";
            ps = connection.prepareStatement(query);
            ps.setString(1, idDate+"%");
            rs=ps.executeQuery();
            while(rs.next()){
                idSem = rs.getString(1);
                break;
                            }
            } catch (SQLException ex){
                    System.err.println("ERror makeId(): "+ex);
            if (idSem!=null){
                int angka = Integer.parseInt(idSem.substring(6, 9));
                angka++;
                id=idDate+String.format("%03d", angka);
            } 
        }return id;
    }
    
    public Date getFormattedDate(String tanggal){
    try {
        Date tanggalLahir = dateFormat.parse(tanggal);
        return tanggalLahir;
    }catch (ParseException ex) {
        System.err.println("Error TAnggal : "+ex);
        return new Date();
    }
}
    
    public BufferedImage getBufferedImage(Blob imageBlob){
        InputStream binaryStream=null;
        BufferedImage b = null;
        try{
            binaryStream = imageBlob.getBinaryStream();
            b = ImageIO.read(binaryStream);
        } catch(SQLException | IOException ex){
            System.err.println("Error getBufferedImage : "+ex);
        } return b;
    }
    
    public Blob getBlobImage(BufferedImage bi){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Blob blFile = null;
        try{
            ImageIO.write(bi, "png", baos);
            blFile = new javax.sql.rowset.serial.SerialBlob(baos.toByteArray());
        } catch (SQLException | IOException ex) {
           Logger.getLogger(AnggotaTambahFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return blFile;        
    }
    
    private BufferedImage resizeImage(BufferedImage originalImage, int type){
        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH,IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();
        return resizedImage;
    }
    
    public AnggotaTambahFrame() {
        initComponents();
        setLocationRelativeTo(null);
        
        eId.setText(makeId());
        eId.setEnabled(false);
        eNamaAnggota.requestFocus();
        cbSetModel(qryPetugas, "id", "nama_petugas", cbPetugas);
        status=SEDANG_TAMBAH;
    }

    public AnggotaTambahFrame(Anggota anggota){
        initComponents();
        setLocationRelativeTo(null);
        
        eId.setText(anggota.getId());
        eId.setEnabled(false);
        eNamaAnggota.requestFocus();
        eNamaAnggota.setText(anggota.getNamaAnggota());
        rbJenisKelaminSetSelected(anggota.getJenisKelamin());
        jXDatePicker1.setDate(getFormattedDate(anggota.getTanggalLahir()));
        cbAgama.setSelectedItem(anggota.getAgama());
        cbSetModel(qryPetugas, "id", "nama_petugas", cbPetugas);
        cbSetSelected(anggota.getPetugas().getNamaPetugas(), cbPetugas);
        bImage = getBufferedImage(anggota.getFotoAnggota());
        lbGambar.setIcon(new ImageIcon(bImage));
        status = SEDANG_UBAH;
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        fChooser1 = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        eId = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        eNamaAnggota = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cbAgama = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        cbPetugas = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        pGambar = new javax.swing.JPanel();
        lbGambar = new javax.swing.JLabel();
        bPilih = new javax.swing.JButton();
        bSimpan = new javax.swing.JButton();
        bBatal = new javax.swing.JButton();
        rbLaki = new javax.swing.JRadioButton();
        rbPerempuan = new javax.swing.JRadioButton();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Id");

        jLabel2.setText("Nama anggota");

        jLabel3.setText("Jenis Kelamin");

        jLabel4.setText("Tanggal Lahir");

        jLabel5.setText("Agama");

        cbAgama.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "- Pilih Agama -", "Islam", "Kristen Protestan", "Kristen Katolik", "Hindu", "Buddha", "Kong Hu Chu" }));

        jLabel6.setText("Petugas");

        cbPetugas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel7.setText("Foto Anggota");

        pGambar.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout pGambarLayout = new javax.swing.GroupLayout(pGambar);
        pGambar.setLayout(pGambarLayout);
        pGambarLayout.setHorizontalGroup(
            pGambarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pGambarLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lbGambar, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pGambarLayout.setVerticalGroup(
            pGambarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pGambarLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lbGambar, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        bPilih.setText("Pilih Gambar");
        bPilih.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPilihActionPerformed(evt);
            }
        });

        bSimpan.setText("Simpan");
        bSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSimpanActionPerformed(evt);
            }
        });

        bBatal.setText("Batal");
        bBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBatalActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbLaki);
        rbLaki.setText("Laki-Laki");

        buttonGroup1.add(rbPerempuan);
        rbPerempuan.setText("Perempuan");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(bPilih, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 119, Short.MAX_VALUE)
                        .addComponent(bSimpan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cbPetugas, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pGambar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(eId, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(rbLaki)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(rbPerempuan))
                                .addComponent(eNamaAnggota)
                                .addComponent(cbAgama, 0, 446, Short.MAX_VALUE)
                                .addComponent(jXDatePicker1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(eId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(eNamaAnggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(rbLaki)
                    .addComponent(rbPerempuan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jXDatePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cbAgama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbPetugas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(pGambar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bPilih)
                    .addComponent(bSimpan)
                    .addComponent(bBatal))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(556, 473));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void bBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBatalActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_bBatalActionPerformed

    private void bPilihActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPilihActionPerformed
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files","jpg","png","gif","jpeg");
        
        fChooser1.setFileFilter(filter);
        BufferedImage img = null;
        try {
                int result = fChooser1.showOpenDialog(null);
                if(result == JFileChooser.APPROVE_OPTION){
                    File file = fChooser1.getSelectedFile();
                    img = ImageIO.read(file);
                    int type = img.getType()== 0? BufferedImage.TYPE_INT_ARGB : img.getType();
                    bImage = resizeImage(img, type);
                    lbGambar.setIcon(new ImageIcon(bImage));
            }
        } catch (IOException e) {
            System.err.println("Error bPilih : "+e);
        }
    }//GEN-LAST:event_bPilihActionPerformed

    private void bSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSimpanActionPerformed
    
        Anggota anggota = new Anggota();
        anggota.setId(eId.getText());
        anggota.setNamaAnggota(eNamaAnggota.getText());
        anggota.setJenisKelamin(rbJenisKelaminGetSelected());
        anggota.setTanggalLahir(dateFormat.format(jXDatePicker1.getDate()));
        anggota.setAgama(cbAgama.getSelectedItem().toString());
        anggota.setFotoAnggota(getBlobImage(bImage));
        
        Petugas petugas = new Petugas();
        petugas.setId(((KeyValue)cbPetugas.getSelectedItem()).getKey());
        anggota.setPetugas(petugas);
        if(anggota.getNamaAnggota().equalsIgnoreCase("") ||
           anggota.getJenisKelamin().equalsIgnoreCase("") ||
           anggota.getTanggalLahir().equalsIgnoreCase("") ||
           anggota.getAgama().equalsIgnoreCase("- Pilih Agama -") ||
           anggota.getFotoAnggota()==null){
        JOptionPane.showMessageDialog(null, "Lengkapi Data");
    }else{
            koneksi koneksi = new koneksi ();
            Connection con = koneksi.getConnection();
            PreparedStatement ps;
            try {
                if(status==SEDANG_TAMBAH){
                    String qry = "insert into anggota values (?,?,?,?,?,?,?)";
                    ps = con.prepareStatement(qry);
                    ps.setString(1, anggota.getId());
                    ps.setString(2, anggota.getNamaAnggota());
                    ps.setString(3, anggota.getJenisKelamin());
                    ps.setString(4, anggota.getTanggalLahir());
                    ps.setString(5, anggota.getAgama());
                    ps.setInt(6, anggota.getPetugas().getId());
                    ps.setBlob(7, anggota.getFotoAnggota());
                    ps.executeUpdate();
                }else{
                    String qry = "update anggota set nama_anggota = ?," 
                            + "jenis_kelamin = ?, tanggal_lahir = ?," 
                            + "agama = ?, id_petugas = ?,"
                            + "foto_anggota = ? WHERE id = ?";
                    ps = con.prepareStatement(qry);
                    ps.setString(1, anggota.getNamaAnggota());
                    ps.setString(2, anggota.getJenisKelamin());
                    ps.setString(3, anggota.getTanggalLahir());
                    ps.setString(4, anggota.getAgama());
                    ps.setInt(5, anggota.getPetugas().getId());
                    ps.setBlob(6, anggota.getFotoAnggota());
                    ps.setString(7, anggota.getId());
                    ps.executeUpdate();   
                }
            } catch (SQLException ex) {
                System.err.println("Erro inset/update : "+ex);
            }dispose();
        }
    }//GEN-LAST:event_bSimpanActionPerformed

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
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AnggotaTambahFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AnggotaTambahFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AnggotaTambahFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AnggotaTambahFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AnggotaTambahFrame().setVisible(true);
            }
        });
    }
    
    
    


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bBatal;
    private javax.swing.JButton bPilih;
    private javax.swing.JButton bSimpan;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbAgama;
    private javax.swing.JComboBox<String> cbPetugas;
    private javax.swing.JTextField eId;
    private javax.swing.JTextField eNamaAnggota;
    private javax.swing.JFileChooser fChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private javax.swing.JLabel lbGambar;
    private javax.swing.JPanel pGambar;
    private javax.swing.JRadioButton rbLaki;
    private javax.swing.JRadioButton rbPerempuan;
    // End of variables declaration//GEN-END:variables
}
