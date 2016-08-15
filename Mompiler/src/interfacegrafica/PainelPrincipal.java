
package interfacegrafica;

import classessintaticas.AnalysisError;
import classessintaticas.LexicalError;
import classessintaticas.Lexico;
import classessintaticas.SemanticError;
import classessintaticas.Semantico;
import classessintaticas.Sintatico;
import classessintaticas.SyntaticError;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import tabela.Simbolos;
import tabela.TabelaDeSimbolos;

public class PainelPrincipal extends javax.swing.JFrame {

    public static TabelaDeSimbolos tabelaPrincipal;
    public List<Simbolos> listaDeSimbolos;
    private DefaultTableModel modeloTabela;
 
    
  
    public PainelPrincipal() {
        initComponents();
        
        PainelTab.setEnabledAt(1, false);
        
        tabelaPrincipal = new TabelaDeSimbolos();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
   
        
        
        
        codigo.setText("int main(){\n\n}");
       
        
        novoPrograma.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                if(codigo.getText() != null || codigo.getText() != ""){
                    if (JOptionPane.showConfirmDialog(null, "Deseja mesmo criar um novo arquivo?") == 1){
                        codigo.setText("");
                    }else{
                        //
                    };
                }
                
                codigo.setText("");
            }
        });
        
        botaoAnalisar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pegarCodigo();
                PainelTab.setEnabledAt(1, true);
                
                Lexico lexico = new Lexico();
                Sintatico sintatico = new Sintatico();
                Semantico semantico = new Semantico();
                tabelaPrincipal = new TabelaDeSimbolos();
                

                lexico.setInput(codigo.getText());
                areaDeErros.setText("Código compilado com sucesso.");
                
                
                try {
                    sintatico.parse(lexico, semantico);
                    String erros = tabelaPrincipal.buscarNaoUsadas();
//                    System.out.println("Olhe o nome da variável!" + listaDeSimbolos.get(0).nomeDaVariavel + "\n ");
//                    System.out.println("Olhe o tipo da variável!" + listaDeSimbolos.get(0).tipoDaVariavel + " \n");
//                    System.out.println("Olhe o escopo da variável!" + listaDeSimbolos.get(0).escopo + "\n ");
                    if (!"".equals(erros)){
                        areaDeErros.setText(erros);      
                    }
                    areaDeErros.setText(areaDeErros.getText() + "\nCompilado com Sucesso!");
                    listaDeSimbolos = tabelaPrincipal.getListaDeSimbolos();
                    criaTabelaNova();
                    
                } catch(LexicalError erro){
                    areaDeErros.setText("Erro léxico na posição " + erro.getPosition());
                } catch(SyntaticError erro){
                    areaDeErros.setText("Erro sintático na posição " + erro.getPosition());
                }catch(SemanticError erro){
                    areaDeErros.setText("Erro semântico na posição " + erro.getPosition() + ". " + erro.getMessage());
                }
                

            }
        });
        
        abrirPrograma.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filtro = new FileNameExtensionFilter("Arquivos textuais", "txt","text");
                chooser.setFileFilter(filtro);
                chooser.showOpenDialog(null);
                File f = chooser.getSelectedFile();
                String filename = f.getAbsolutePath();
                
                try {
                    FileReader reader = new FileReader(filename);
                    BufferedReader br = new BufferedReader(reader);
                    codigo.read(br, null);
                    br.close();
                    codigo.requestFocus();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Erro! Arquivo Inválido!");
                }
                        
            }
        });
        
        salvarPrograma.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                String text = codigo.getText();
                JFileChooser chooser = new JFileChooser();
                chooser.showSaveDialog(null);
                try {
                    FileWriter escritor = new FileWriter(chooser.getSelectedFile() + ".txt");
                    escritor.write(text.toString());
                    escritor.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    
                }
                JOptionPane.showMessageDialog(null, "Arquivo Salvo com Sucesso!");
            }
        });
    
        itemMenuRealizar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pegarCodigo();
                Lexico lexico = new Lexico();
                Sintatico sintatico = new Sintatico();
                Semantico semantico = new Semantico();

                

                lexico.setInput(codigo.getText());

                try {
                    sintatico.parse(lexico, semantico);
                    System.out.println("FUNCIONO CARAIO");
                } catch (AnalysisError erroLexico) {
                    StringWriter escritorErros = new StringWriter();
                    erroLexico.printStackTrace(new PrintWriter(escritorErros));
                    areaDeErros.setText(escritorErros.toString());
                    System.out.println("NÃO FUNCIONO CARAIO");
                }
            }
        });
        
        exemplosMenu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Exemplos ex = new Exemplos();
                ex.setVisible(true);
            }
        });
        
        
        
    }

    public String pegarCodigo(){
        String codigoTexto;
        codigoTexto = codigo.getText();
        
        System.out.println(codigoTexto);
        return codigoTexto;
        
        
    }
    
    public void criarTabela(){ 
        String[] colunas = {"Nome", "Tipo", "Escopo", "Iniciado", "Usado", "Parâmetros", "Vetor", "Função","Posição"};
        Object[][] dados = new Object[listaDeSimbolos.size()][];
        
        for (int i = 0; i < listaDeSimbolos.size(); i++) {
            for (int j = 0; j < listaDeSimbolos.size(); j++) {
                dados[i][j] = new Object[]{listaDeSimbolos.get(i).nomeDaVariavel, listaDeSimbolos.get(i).tipoDaVariavel, listaDeSimbolos.get(i).escopo,
                                            listaDeSimbolos.get(i).inicializada, listaDeSimbolos.get(i).usada, listaDeSimbolos.get(i).parametro,
                                            listaDeSimbolos.get(i).vetor, listaDeSimbolos.get(i).funcao, listaDeSimbolos.get(i).posicao };
            }
        }
        
 
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PainelTab = new javax.swing.JTabbedPane();
        painelEditor = new javax.swing.JPanel();
        painelRolagem = new javax.swing.JScrollPane();
        codigo = new javax.swing.JTextPane();
        botaoAnalisar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        areaDeErros = new javax.swing.JTextArea();
        painelTabela = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabelaGrafica = new javax.swing.JTable();
        barraDeMenu = new javax.swing.JMenuBar();
        menuArquivo = new javax.swing.JMenu();
        novoPrograma = new javax.swing.JMenuItem();
        abrirPrograma = new javax.swing.JMenuItem();
        salvarPrograma = new javax.swing.JMenuItem();
        salvarComo = new javax.swing.JMenuItem();
        realizarAnaliseMenu = new javax.swing.JMenu();
        itemMenuRealizar = new javax.swing.JMenuItem();
        exemplosMenu = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        PainelTab.setBorder(new javax.swing.border.MatteBorder(null));

        painelRolagem.setViewportView(codigo);

        botaoAnalisar.setText("Análise");

        jScrollPane1.setEnabled(false);

        areaDeErros.setColumns(20);
        areaDeErros.setRows(5);
        jScrollPane1.setViewportView(areaDeErros);

        javax.swing.GroupLayout painelEditorLayout = new javax.swing.GroupLayout(painelEditor);
        painelEditor.setLayout(painelEditorLayout);
        painelEditorLayout.setHorizontalGroup(
            painelEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelEditorLayout.createSequentialGroup()
                .addGroup(painelEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(painelRolagem)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, painelEditorLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(botaoAnalisar))
                    .addGroup(painelEditorLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 649, Short.MAX_VALUE)))
                .addContainerGap())
        );
        painelEditorLayout.setVerticalGroup(
            painelEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelEditorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(painelRolagem, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botaoAnalisar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PainelTab.addTab("Editor", painelEditor);

        tabelaGrafica.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tabelaGrafica);

        javax.swing.GroupLayout painelTabelaLayout = new javax.swing.GroupLayout(painelTabela);
        painelTabela.setLayout(painelTabelaLayout);
        painelTabelaLayout.setHorizontalGroup(
            painelTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 669, Short.MAX_VALUE)
        );
        painelTabelaLayout.setVerticalGroup(
            painelTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelTabelaLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        PainelTab.addTab("Tabela", painelTabela);

        menuArquivo.setText("Arquivo");

        novoPrograma.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        novoPrograma.setText("Novo");
        menuArquivo.add(novoPrograma);

        abrirPrograma.setText("Abrir ");
        menuArquivo.add(abrirPrograma);

        salvarPrograma.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        salvarPrograma.setText("Salvar");
        menuArquivo.add(salvarPrograma);

        salvarComo.setText("Salvar como");
        menuArquivo.add(salvarComo);

        barraDeMenu.add(menuArquivo);

        realizarAnaliseMenu.setText("Análise Sintética");

        itemMenuRealizar.setText("Realizar análise sintética");
        realizarAnaliseMenu.add(itemMenuRealizar);

        exemplosMenu.setText("Exemplos de Linguagem");
        realizarAnaliseMenu.add(exemplosMenu);

        barraDeMenu.add(realizarAnaliseMenu);

        setJMenuBar(barraDeMenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PainelTab)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PainelTab)
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
            java.util.logging.Logger.getLogger(PainelPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PainelPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PainelPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PainelPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PainelPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane PainelTab;
    private javax.swing.JMenuItem abrirPrograma;
    private javax.swing.JTextArea areaDeErros;
    private javax.swing.JMenuBar barraDeMenu;
    private javax.swing.JButton botaoAnalisar;
    private javax.swing.JTextPane codigo;
    private javax.swing.JMenuItem exemplosMenu;
    private javax.swing.JMenuItem itemMenuRealizar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JMenu menuArquivo;
    private javax.swing.JMenuItem novoPrograma;
    private javax.swing.JPanel painelEditor;
    private javax.swing.JScrollPane painelRolagem;
    private javax.swing.JPanel painelTabela;
    private javax.swing.JMenu realizarAnaliseMenu;
    private javax.swing.JMenuItem salvarComo;
    private javax.swing.JMenuItem salvarPrograma;
    private javax.swing.JTable tabelaGrafica;
    // End of variables declaration//GEN-END:variables

    private void criaTabelaNova() {
        Object[] colunas = {"Nome", "Tipo", "Escopo", "Iniciada", "Usada", "Parâmetros", "Vetor", "Função", "Posição"};
        
        DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0);
        
        tabelaGrafica.setModel(modeloTabela);
        tabelaGrafica.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        for(Simbolos simbolo: listaDeSimbolos){
            modeloTabela.addRow(new Object[]{simbolo.nomeDaVariavel, simbolo.tipoDaVariavel, simbolo.escopo, simbolo.inicializada, simbolo.usada, simbolo.parametro, simbolo.vetor, simbolo.funcao, simbolo.posicao});
        }
    }
}
