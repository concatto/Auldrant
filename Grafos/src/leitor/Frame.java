package leitor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;


/**
 *
 * @author Guilherme Gustavo Gohr Darosci
 */
public class Frame extends javax.swing.JFrame {

  LeitorXml leitor;
  File f;
  int i;
  Grafo g;
  DFS algoritmo;
 
    public Frame() {
        initComponents();       
        
        
        botaoNovoXML.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                leitor = new LeitorXml();  
                //g = new Grafo(Grafo) LeitorXml.grafoFromXML(g);
                
                g = new Grafo();
                g = (Grafo) LeitorXml.grafoFromXML();
                
                try{
                f = LeitorXml.f;
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(null, "Nenhuma XML selecionada!");
                }
                algoritmo = new DFS(g);
                if (algoritmo.isConexo() == true) labelInformado.setText("O grafo informado é conexo");
                else{
                    labelInformado.setText("O grafo informado não é conexo");
                }
                
               
                try {
                    Files.lines(f.toPath()).forEach(i ->areatextoXML.append(i)); //Escreve o XML no campo de texto.
                } catch (IOException ex) {
                    Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "XML Inválido. Cheque o charmap do seu XML.");
                }
               
            }
        });
        
       
               
              
                
       
        
    }

    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ScrollPane = new javax.swing.JScrollPane();
        areatextoXML = new javax.swing.JTextArea();
        botaoNovoXML = new javax.swing.JButton();
        labelInformado = new javax.swing.JLabel();
        Barramenu = new javax.swing.JMenuBar();
        MenuFile = new javax.swing.JMenu();
        novoXMLMenu = new javax.swing.JMenuItem();
        novoAjuda = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        areatextoXML.setColumns(20);
        areatextoXML.setLineWrap(true);
        areatextoXML.setRows(5);
        areatextoXML.setToolTipText("XML escolhido");
        areatextoXML.setEnabled(false);
        ScrollPane.setViewportView(areatextoXML);

        botaoNovoXML.setText("Novo XML");

        labelInformado.setText("O grafo informado é: ");

        MenuFile.setText("Novo");

        novoXMLMenu.setText("Novo XML");
        MenuFile.add(novoXMLMenu);

        novoAjuda.setText("Ajuda");
        MenuFile.add(novoAjuda);

        Barramenu.add(MenuFile);

        setJMenuBar(Barramenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(botaoNovoXML)
                        .addGap(54, 54, 54))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(labelInformado, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(45, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelInformado)
                        .addGap(200, 200, 200)
                        .addComponent(botaoNovoXML))
                    .addComponent(ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Frame().setVisible(true);
            }
        });
    }

    public Grafo getG() {
        return g;
    }
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar Barramenu;
    private javax.swing.JMenu MenuFile;
    private javax.swing.JScrollPane ScrollPane;
    private javax.swing.JTextArea areatextoXML;
    private javax.swing.JButton botaoNovoXML;
    private javax.swing.JLabel labelInformado;
    private javax.swing.JMenuItem novoAjuda;
    private javax.swing.JMenuItem novoXMLMenu;
    // End of variables declaration//GEN-END:variables
}
