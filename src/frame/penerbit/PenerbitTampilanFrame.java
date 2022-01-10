package frame.penerbit;

import db.koneksi;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.management.Query;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import model.Penerbit;

public class PenerbitTampilanFrame extends JFrame{
    
    JLabel jLabel = new JLabel("Cari");
    JTextField eCari = new JTextField();
    JButton bCari = new JButton("Cari");
    
    String header [] = {"id","Penerbit"};
    TableModel tableModel = new DefaultTableModel(header, 0);
    JTable tPenerbit = new JTable(tableModel);
    JScrollPane jScrollPane = new JScrollPane(tPenerbit);
    
    JButton bTambah = new JButton("Tambah");
    JButton bUbah = new JButton("Ubah");
    JButton bHapus = new JButton("Hapus");
    JButton bBatal = new JButton("Batal");
    JButton bTutup = new JButton("Tutup");
    
    Penerbit penerbit;
    

public void setKomponen(){
    getContentPane().setLayout(null);
    getContentPane().add(jLabel);
    getContentPane().add(eCari);
    getContentPane().add(jScrollPane);
    getContentPane().add(bCari);
    getContentPane().add(bTambah);
    getContentPane().add(bUbah);
    getContentPane().add(bHapus);
    getContentPane().add(bBatal);
    getContentPane().add(bTutup);
    
    jLabel.setBounds(10,10,50,25);
    eCari.setBounds(60,10,330,25);
    bCari.setBounds(400,10,70,25);
    bTutup.setBounds(400,220,70,25);
    bTambah.setBounds(10,220,80,25);
    bUbah.setBounds(95,220,70,25);
    bHapus.setBounds(170,220,70,25);
    bBatal.setBounds(245,220,70,25);
    jScrollPane.setBounds(10,45,460,160);
    
    resetTable("");
    setListener();
    setVisible(true);
       
}

public PenerbitTampilanFrame(){
    setSize(500,300);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setKomponen();
}

    public static void main(String[] args) {
        PenerbitTampilanFrame penerbitTamilanFrame = new PenerbitTampilanFrame();
    }
    
public ArrayList<Penerbit> getPenerbitList(String keyword){
    ArrayList<Penerbit> penerbitList = new ArrayList<Penerbit>();
    koneksi Koneksi = new koneksi();
    Connection connetion = Koneksi.getConnection();
    
    String query = "SELECT * FROM penerbit "+keyword;
    Statement statement;
    ResultSet resultSet;
    
    try {
        statement = connetion.createStatement();
        resultSet = statement.executeQuery(query);
        while(resultSet.next()){
            penerbit = new Penerbit(resultSet.getInt("id"),
                    resultSet.getString("penerbit"));
            penerbitList.add(penerbit);
        }
    } catch (SQLException | NullPointerException ex){
        System.err.println("Koneksi Null Gagal");
    }
    return penerbitList;
} 

public final void selectPenerbit(String keyword){
    ArrayList<Penerbit> list = getPenerbitList(keyword);
    DefaultTableModel model = (DefaultTableModel)tPenerbit.getModel();
    Object[] row = new Object[2];
    
    for (int i = 0; i < list.size(); i++){
        row[0] = list.get(i).getId();
        row[1] = list.get(i).getPenerbit();
        
        model.addRow(row);
    }
}

public final void resetTable(String keyword){
    DefaultTableModel model = (DefaultTableModel)tPenerbit.getModel();
    model.setRowCount(0);
    selectPenerbit(keyword);
}

public void setListener(){
    bTutup.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();        }
    });
    
    bCari.addActionListener(new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            resetTable(" WHERE penerbit like '%"+eCari.getText()+"%'");
        }   
    });

    bBatal.addActionListener(new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            resetTable("");
        }
    });
    
    bHapus.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
        int i = tPenerbit.getSelectedRow();
        int pilihan = JOptionPane.showConfirmDialog(null,
                       "yakin mau hapus ?",
                       "konfirmasi hapus",
                       JOptionPane.YES_NO_OPTION);
        if(pilihan==0){
            if(i>=0){
            try {
                TableModel model = tPenerbit.getModel();
                koneksi Koneksi = new koneksi();
                Connection con = Koneksi.getConnection();
                String executeQuery = "delete from penerbit where id =?";
                PreparedStatement ps = con.prepareStatement(executeQuery);
                ps.setString(1, model.getValueAt(i,0).toString());
                ps.executeUpdate();
            } catch (SQLException ex){
                System.err.println(ex);
            }
            }else{
                JOptionPane.showMessageDialog(null, "Pilih data yang ingin dihapus");      
            }
        }
        resetTable("");
        }
    });
    
    bUbah.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int i = tPenerbit.getSelectedRow();
            if (i>=0){
                TableModel model = tPenerbit.getModel();
                penerbit = new Penerbit();
                penerbit.setId(Integer.parseInt(model.getValueAt(i, 0).toString()));
                penerbit.setPenerbit(model.getValueAt(i, 1).toString());
                PenerbitTambahFrame penerbitTambahFrame = new PenerbitTambahFrame(penerbit);
            }else{
                JOptionPane.showMessageDialog(null, "Pilih data yang ingin diubah");
            }
        }
    });
    
    bTambah.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            PenerbitTambahFrame penerbitTambahFrame= new PenerbitTambahFrame();
        }
    });
    
    addWindowListener(new java.awt.event.WindowAdapter(){
        public void windowActivated(java.awt.event.WindowEvent evt){
            resetTable("");
        }
    });
}

}