/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EstabelecimentoUI;

import controladoras.CategoriaJpaController;
import controladoras.EstabelecimentoJpaController;
import controladoras.ProductoVendaJpaController;
import controladoras.ProductosJpaController;
import controladoras.UtilizadoresJpaController;
import controladoras.VendaJpaController;
import controladoras.exceptions.NonexistentEntityException;
import entidades.Categoria;
import entidades.Estabelecimento;
import entidades.ProductoVenda;
import entidades.Productos;
import entidades.Utilizadores;
import entidades.Venda;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 *
 * @author _aphie_d
 */
public class Admin extends javax.swing.JFrame {

    int cont = 0;
    Venda venda;
    int actual = 0; //preco total dos productos no cesto de venda
    private Estabelecimento estActual;
    private Utilizadores utlActual; //utilizador actual (Agente de comercio a usar o sistema)
    DefaultTableModel model = new DefaultTableModel();
    DefaultTableModel model2 = new DefaultTableModel();
    DefaultTableModel model3 = new DefaultTableModel();
    List categorias = new ArrayList();
    HashMap<String, String> todascategorias = new HashMap<>();

    /**
     * Creates new form Admin
     *
     * @param user
     * @param est
     */
    public Admin(Utilizadores user, Estabelecimento est) {
        estActual = est;
        utlActual = user;
        initComponents();
        AdminUserCode.setText(utlActual.getCodigoUtilizador());
        AdminUserName.setText(utlActual.getNomeUtlizador());
        TipoEstbSelecionadojLabel.setText(estActual.getTipoEstabelecimento());
        AdminUserName.setVisible(true);
        AdminSearchTxtField.setEnabled(false);
        AdminAddBtn.setEnabled(false);
        AdminCnclButton.setEnabled(false);
        AdminConfVendaButton.setEnabled(false);
        AdminRemvBtn.setEnabled(false);
        model.setColumnIdentifiers(new String[]{"#", "Cod. Barras", "Nome Producto", "Quantidade", "Preco Total"});
        model2.setColumnIdentifiers(new String[]{"#", "Cod. Barras", "Nome Producto", "Categoria", "Quant. Stock", "Preco/Un"});
        model3.setColumnIdentifiers(new String[]{"#", "Nome Completo", "Iniciais", "Funcao"});
        mostrarProductosNoStock();
        acncorarCatComboBox(estActual);
        ancorarComboUser();
        mostrarUsers();
        estabelecimentoActual();
        ancorarComboTipoEst();
        acncorarEstCatComboBox(estActual);
        ancorarCatAddComboBox();
//        fillCategorias();
    }

//    public Admin() {
////        initComponents();
////        AdminUserName.setText(utlActual.getNome());
//    }
//    public void fillCategorias() {
//        
//    }
    public void mostrarProductosDaVendaNoCesto() {
        model.setRowCount(0);
        AdminPrecoTotalTxtField.setText("0.00");
    }

    private void estabelecimentoActual() {
        if (estActual != null) {
            AdminNomeEstbjTextField.setText(estActual.getNomeEstabelecimento());
            AdminCodEstbjTextField.setText(estActual.getCodigoEstabelecimento());
            AdminEmailEstbjTextField.setText(estActual.getEmailEstabelecimento());
            AdminTelEstbjTextField.setText(estActual.getTelefoneEstabelecimento());
            AdminCisadeEstbjTextField.setText(estActual.getCidadeEstabelecimento());
            AdminCodPostalEstbjTextField.setText(estActual.getCodigoPostal());
            AdminIvaEstbjTextField.setText(estActual.getIvaEstabelecimento().toString());
            AdminEstadoEstbjTextField.setText(estActual.getActivo().toString().toUpperCase());
            AdminAddEstjButton.setEnabled(true);
        } else {
            AdminAddEstjButton.setEnabled(true);
        }
    }

    public void mostrarUsers() {
        UtilizadoresJpaController uc = new UtilizadoresJpaController();
        List<Utilizadores> U = estActual.getUtilizadoresList();
        U.stream().forEach((u) -> {
            model3.addRow(new String[]{u.getIdUtilizador().toString(), u.getNomeUtlizador(), u.getCodigoUtilizador(), u.getFuncaoUtilizador()});
        });

        AdminTabelaUserjTable1.setModel(model3);
        AdminTabelaUserjTable1.getColumnModel().getColumn(0).setMinWidth(0);
        AdminTabelaUserjTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        AdminTabelaUserjTable1.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    public void mostrarProductosNoStock() {
        ProductosJpaController pc = new ProductosJpaController();
        List<Productos> P = pc.findProductosEntities();

        for (Productos p : P) {
            if (p.getEstabelecimentoId().equals(estActual)) {
                model2.addRow(new String[]{p.getProductoId().toString(), p.getCodigoBarras(), p.getNomeProducto(), p.getCategoriaCodigo(), p.getQuantidadeProducto(), p.getPrecoProducto().toString()});
            }
        }

        AdminMngTable.setModel(model2);
        AdminMngTable.getColumnModel().getColumn(0).setMinWidth(0);
        AdminMngTable.getColumnModel().getColumn(0).setMaxWidth(0);
        AdminMngTable.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    private Venda novaVenda() {

        VendaJpaController vc = new VendaJpaController();
        Venda v = new Venda();
        try {
            vc.create(v);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, "Nao foi possivel criar uma nova venda!");
        }
        return v;
    }

    private boolean productoExiste() {
//        ProductosJpaController pc = new ProductosJpaController();
//        List<Productos> P = pc.findProductosEntities();

        if (!estActual.getProductosList().isEmpty()) {
            for (int i = 0; i < estActual.getProductosList().size(); i++) {
                if (AdminMngCodigoTxtField1.getText().trim().equalsIgnoreCase(estActual.getProductosList().get(i).getCodigoBarras().trim())) {
                    return true;
                    //se existir um codigo de barras devolver true
                }
            }
        } else {
            //se a tabela de productos estiver vazia
            return false;
        }
        return false;
    }

    private void cadastrarProducto() {
        ProductosJpaController pc = new ProductosJpaController();
        CategoriaJpaController cc = new CategoriaJpaController();
        List<Productos> P = pc.findProductosEntities();
        List<Categoria> C = cc.findCategoriaEntities();

        Productos p = new Productos();

        if (AdminMngCodigoTxtField1.getText() == ""
                || AdminMngNomeTxtField1.getText() == ""
                || AdminMngPrecoTxtField1.getText() == ""
                || AdminMngQntTxtField1.getText() == ""
                || AdminMngTextArea.getText() == "") {
            JOptionPane.showMessageDialog(rootPane, "Preencha todos os campos!");
        } else {
            p.setCodigoBarras(AdminMngCodigoTxtField1.getText());
            p.setNomeProducto(AdminMngNomeTxtField1.getText());
            p.setPrecoProducto(BigInteger.valueOf(Integer.valueOf(AdminMngPrecoTxtField1.getText()).longValue()));
            p.setQuantidadeProducto(AdminMngQntTxtField1.getText());
            p.setDescricaoProducto(AdminMngTextArea.getText());
            p.setEstabelecimentoId(estActual);
            p.setEstabelecimentoCodigo(estActual.getCodigoEstabelecimento());
            p.setImagem(imagem);

            for (int i = 0; i < C.size(); i++) {
                if (AdminMngCategoryComboBox.getSelectedItem().equals(C.get(i).getDescricaoCategoria().trim())) {
                    p.setCategoriaCodigo(C.get(i).getCodigoCategoria());
                    p.setCategoriaId(C.get(i));
                }
            }

            if (productoExiste() == false) {
                try {
                    pc.create(p);
                    try {
                        javax.ws.rs.client.Client client;
                        client = ClientBuilder.newClient();
                        String BASE_URI = "http://localhost:8086/Comparador/webresources";
                        WebTarget webTarget = client.target(BASE_URI).path("productos");

                        Response response = webTarget
                                .request()
                                .accept(MediaType.APPLICATION_XML)
                                .post(Entity.entity(p, MediaType.APPLICATION_XML));

                        System.out.println(response);
                        client.close();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(rootPane, "Nao foi possivel adicionar o producto online! | " + e.getStackTrace());
                    }
                    AdminMngCodigoTxtField1.setText("");
                    AdminMngNomeTxtField1.setText("");
                    AdminMngPrecoTxtField1.setText("");
                    AdminMngQntTxtField1.setText("");
                    AdminMngTextArea.setText("");
                    jLabelImage.setVisible(false);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(rootPane, "Nao foi possivel adicionar o producto no estoque!");
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Producto ja existe no estoque!");
                AdminMngCodigoTxtField1.setText("");
                AdminMngNomeTxtField1.setText("");
                AdminMngPrecoTxtField1.setText("");
                AdminMngQntTxtField1.setText("");
                AdminMngTextArea.setText("");
                jLabelImage.setVisible(false);
            }
        }

        model2.addRow(new String[]{p.getProductoId().toString(), p.getCodigoBarras(), p.getNomeProducto(), p.getCategoriaCodigo(), p.getQuantidadeProducto(), p.getPrecoProducto().toString()});
        AdminMngTable.setModel(model2);
        AdminMngTable.getColumnModel().getColumn(0).setMinWidth(0);
        AdminMngTable.getColumnModel().getColumn(0).setMaxWidth(0);
        AdminMngTable.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    public void cadastrarUser() {
        UtilizadoresJpaController uc = new UtilizadoresJpaController();
        Utilizadores u = new Utilizadores();

        try {
            u.setNomeUtlizador(AdminNomeUserTxtField.getText());
            u.setCodigoUtilizador(AdminCodUserTxtField.getText());
            u.setFuncaoUtilizador(AdminFuncaoComboBox.getSelectedItem().toString());
            u.setSenhaUtilizador(AdminSenhaTxtField.getText());
            u.setEstabelecimentoId(estActual);
            u.setCodigoEstabelecimento(estActual.getCodigoEstabelecimento());

            uc.create(u);
            model3.addRow(new String[]{u.getIdUtilizador().toString(), u.getNomeUtlizador(), u.getCodigoUtilizador(), u.getFuncaoUtilizador()});
            AdminTabelaUserjTable1.setModel(model3);
            JOptionPane.showMessageDialog(rootPane, "Utilizador Adicionado com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, "Nao foi possivel adicionar utilizador! Erro: " + e.getMessage());
        }
    }

    public boolean existeCategoria(String cat) {
        CategoriaJpaController cc = new CategoriaJpaController();
        Categoria c = new Categoria();
        for (Categoria categorias : estActual.getCategoriaList()) {
            if (categorias.getCodigoCategoria().equalsIgnoreCase(cat)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void adicionarCategoria() {
        CategoriaJpaController cc = new CategoriaJpaController();
        Categoria c = new Categoria();

        try {
            if (!AdminCatjComboBox.getSelectedItem().equals("")) {
                for (Map.Entry mapElement : todascategorias.entrySet()) {
                    if (mapElement.getValue().equals(AdminCatjComboBox.getSelectedItem().toString())) {
                        if (existeCategoria(mapElement.getKey().toString()) == true) {
                            JOptionPane.showMessageDialog(rootPane, "Nao foi possivel adicionar o Categoria online!");
                        } else if (existeCategoria(mapElement.getKey().toString()) == false) {
                            c.setCodigoCategoria(mapElement.getKey().toString());
                        }
                        break;
                    }
                }

                c.setDescricaoCategoria(AdminCatjComboBox.getSelectedItem().toString());
                c.setEstabelecimentoId(estActual);
                c.setCodigoEstabelecimento(estActual.getCodigoEstabelecimento());
                cc.create(c);

//                actualizar o combobox das categorias
                EstabelecimentoJpaController ec = new EstabelecimentoJpaController();
                Estabelecimento E = ec.findEstabelecimento(estActual.getEstabelecimentoId());
                acncorarEstCatComboBox(E);
                acncorarCatComboBox(E);

                try {
                    javax.ws.rs.client.Client client;
                    client = ClientBuilder.newClient();
                    String BASE_URI = "http://localhost:8086/Comparador/webresources";
                    WebTarget webTarget = client.target(BASE_URI).path("categoria");

                    Response response = webTarget
                            .request()
                            .accept(MediaType.APPLICATION_XML)
                            .post(Entity.entity(c, MediaType.APPLICATION_XML));

                    System.out.println(response);
                    client.close();
                } catch (Exception exp) {
                    JOptionPane.showMessageDialog(rootPane, "Nao foi possivel adicionar o Categoria online! | " + exp.getStackTrace());
                }
//                acncorarEstCatComboBox(estActual);
                JOptionPane.showMessageDialog(rootPane, c.getDescricaoCategoria() + " foi adicionado com sucesso a " + estActual.getNomeEstabelecimento());
            } else {
                JOptionPane.showMessageDialog(rootPane, "Selecione uma categoria!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, "Nao foi possivel adicionar categoria a " + estActual.getNomeEstabelecimento() + ". Possivel duplicacao!");
        }
    }

    private void ancorarComboUser() {
        List<String> funcaoUser = new ArrayList<>();
        funcaoUser.add("Sales");
        funcaoUser.add("Admin");

        Object[] funcaoArray = funcaoUser.toArray();
        DefaultComboBoxModel tipoUserModel = new DefaultComboBoxModel(funcaoArray);
        AdminFuncaoComboBox.setModel(tipoUserModel);
    }

    private void ancorarComboTipoEst() {
        List<String> tipoEst = new ArrayList<>();
        tipoEst.add("Supermercado ou Mercearia");
        tipoEst.add("Papelaria ou Grafica");
        tipoEst.add("Restaurante ou Padaria");
        tipoEst.add("Loja de Diversos");
        tipoEst.add("Livraria");
        tipoEst.add("Ferragem");
        tipoEst.add("Farmacia");
        tipoEst.add("Armazem");
        tipoEst.add("Informal");
        tipoEst.add("Bar");

        Object[] tipoArray = tipoEst.toArray();
        DefaultComboBoxModel estabelecimentoModel = new DefaultComboBoxModel(tipoArray);
        AdminTipoEstjComboBox.setModel(estabelecimentoModel);
    }

    private void acncorarCatComboBox(Estabelecimento e) {
//        CategoriaJpaController cc = new CategoriaJpaController();
        List<Categoria> C = e.getCategoriaList();
        List<String> categorias = new ArrayList<>();

        for (int i = 0; i < C.size(); i++) {
            categorias.add(C.get(i).getDescricaoCategoria());
        }

        Object[] categoriasArray = categorias.toArray();
        DefaultComboBoxModel stockModel = new DefaultComboBoxModel(categoriasArray);
        AdminMngCategoryComboBox.setModel(stockModel);
    }

    private void ancorarCatAddComboBox() {
        todascategorias.put("TELECOM", "Telefonia e Comunicação");
        todascategorias.put("ELECDOM", "Eletrodomésticos");
        todascategorias.put("ELECTRO", "Electrônicos");
        todascategorias.put("AUTO", "Automóveis e Motorizadas");
        todascategorias.put("FERR", "Ferramentas e Peças");
        todascategorias.put("OFFICE", "Computadores e Material de Escritório");
        todascategorias.put("ILUMI", "Luzes e Iluminação");
        todascategorias.put("SECPROT", "Segurança e Proteção");
        todascategorias.put("VEST", "Vestiario");
        todascategorias.put("JOI", "Joias e Acessórios");
        todascategorias.put("BELSAU", "Artigos de Beleza e Saúde");
        todascategorias.put("MOV", "Móveis");
        todascategorias.put("OUTDOOR", "Material de Desporto e Casa");
        todascategorias.put("ALIM", "Productos Alimentares");
        todascategorias.put("BEB", "Bebidas e Refrigerantes");
        todascategorias.put("CARNES", "Carnes");
        todascategorias.put("FRUT", "Frutas e Vegetais");
        List<String> categoriasDesc = new ArrayList(todascategorias.values());

        Object[] categoriasArray = categoriasDesc.toArray();
        DefaultComboBoxModel stockModel = new DefaultComboBoxModel(categoriasArray);
        AdminCatjComboBox.setModel(stockModel);
    }

    private void acncorarEstCatComboBox(Estabelecimento e) {
//        CategoriaJpaController cc = new CategoriaJpaController();
        List<Categoria> C = e.getCategoriaList();
        List<String> categorias = new ArrayList<>();

        for (int i = 0; i < C.size(); i++) {
            categorias.add(C.get(i).getDescricaoCategoria());
        }

        Object[] categoriasArray = categorias.toArray();
        DefaultComboBoxModel cmodel = new DefaultComboBoxModel(categoriasArray);
        AdminEstCatjComboBox.setModel(cmodel);
    }

    private void cadastrarEstabelecimento() {
        NovoEstabelecimento novoEst = new NovoEstabelecimento();
        novoEst.setVisible(true);
//        EstabelecimentoJpaController ec = new EstabelecimentoJpaController();
//        UtilizadoresJpaController uc = new UtilizadoresJpaController();
//        Estabelecimento e = new Estabelecimento();
//        Utilizadores u = new Utilizadores();
//
//        e.setNomeEstabelecimento(AdminNomeEstbjTextField.getText());
//        e.setCodigoEstabelecimento(AdminCodEstbjTextField.getText());
//        e.setEmailEstabelecimento(AdminEmailEstbjTextField.getText());
//        e.setTelefoneEstabelecimento(AdminTelEstbjTextField.getText());
//        e.setCidadeEstabelecimento(AdminCisadeEstbjTextField.getText());
//        e.setCodigoPostal(AdminCodPostalEstbjTextField.getText());
//        e.setIvaEstabelecimento(BigInteger.valueOf(Integer.valueOf(AdminIvaEstbjTextField.getText()).longValue()));
//        e.setTipoEstabelecimento(AdminTipoEstjComboBox.getSelectedItem().toString());
//        e.setActivo(true);
//
//        try {
//            ec.create(e);
//
//            try {
//                javax.ws.rs.client.Client client;
//                client = ClientBuilder.newClient();
//                String BASE_URI = "http://localhost:8086/Comparador/webresources";
//                WebTarget webTarget = client.target(BASE_URI).path("estabelecimento");
//
//                Response response = webTarget
//                        .request()
//                        .accept(MediaType.APPLICATION_XML)
//                        .post(Entity.entity(e, MediaType.APPLICATION_XML));
//
//                System.out.println(response);
//                client.close();
//            } catch (Exception exp) {
//                JOptionPane.showMessageDialog(rootPane, "Nao foi possivel adicionar o Estabelecimento online! | " + exp.getStackTrace());
//            }
//
//            NovoUtilizador nu = new NovoUtilizador(e);
//            nu.setVisible(true);
////            JOptionPane.showMessageDialog(rootPane, "Estabelecimento adicionado com sucesso!");
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(rootPane, "Nao foi possivel adicionar um novo estabelecimento!" + ex.getMessage());
//        }

    }

    private void editarEstabelecimento() {
        EstabelecimentoJpaController ec = new EstabelecimentoJpaController();
        Estabelecimento e = estActual;

        e.setNomeEstabelecimento(AdminNomeEstbjTextField.getText());
        e.setCodigoEstabelecimento(AdminCodEstbjTextField.getText());
        e.setEmailEstabelecimento(AdminEmailEstbjTextField.getText());
        e.setTelefoneEstabelecimento(AdminTelEstbjTextField.getText());
        e.setCidadeEstabelecimento(AdminCisadeEstbjTextField.getText());
        e.setCodigoPostal(AdminCodPostalEstbjTextField.getText());
        e.setIvaEstabelecimento(BigInteger.valueOf(Integer.valueOf(AdminIvaEstbjTextField.getText()).longValue()));
        e.setTipoEstabelecimento(AdminTipoEstjComboBox.getSelectedItem().toString());
        e.setActivo(true);

        try {
            ec.edit(e);
            try {
                javax.ws.rs.client.Client client;
                HttpAuthenticationFeature basic;
                basic = HttpAuthenticationFeature.basic("utilizador", "senhaSuperSecreta");
                
                client = ClientBuilder.newClient();
                client.register(basic);
                
                String BASE_URI = "http://localhost:8086/Comparador/webresources";
                WebTarget webTarget = client.target(BASE_URI).path("estabelecimento");

                Response response = webTarget
                        .request()
                        .accept(MediaType.APPLICATION_XML)
                        .put(Entity.entity(e, MediaType.APPLICATION_XML));
                System.out.println(response);
                client.close();
                
//                Fechar janela e refazer o login
                estActual = null;
                utlActual = null;
                Login login = new Login();
                login.setVisible(true);
                dispose();
            } catch (Exception exp) {
                JOptionPane.showMessageDialog(rootPane, "Nao foi possivel actualizar o Estabelecimento online! | " + exp.getStackTrace());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(rootPane, "Nao foi possivel actualizar este estabelecimento!" + ex.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        VendaPainel = new javax.swing.JPanel();
        ProductoPainel = new javax.swing.JPanel();
        AdminSearchTxtField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        AdminCodigoTxtField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        AdminNomeTxtField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        AdminPrecoTxtField = new javax.swing.JTextField();
        AdminCatgTxtField = new javax.swing.JTextField();
        AdminStockTxtField = new javax.swing.JTextField();
        AdminQntTxtField = new javax.swing.JTextField();
        AdminAddBtn = new javax.swing.JButton();
        AdminClsButton = new javax.swing.JButton();
        CestoPainel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        AdminTabela = new javax.swing.JTable();
        AdminRemvBtn = new javax.swing.JButton();
        CalculadoraPainel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        AdminPrecoTotalTxtField = new javax.swing.JTextField();
        AdminConfVendaButton = new javax.swing.JButton();
        AdminCnclButton = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        AdminPrecoTotalTxtField1 = new javax.swing.JTextField();
        AdminNovaVendaBtn = new javax.swing.JButton();
        AdminUserName = new javax.swing.JLabel();
        AdminUserCode = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        ProductoMngPainel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        AdminMngCodigoTxtField1 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        AdminMngNomeTxtField1 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        AdminMngPrecoTxtField1 = new javax.swing.JTextField();
        AdminMngQntTxtField1 = new javax.swing.JTextField();
        AdminMngAddBtn1 = new javax.swing.JButton();
        AdminMngCategoryComboBox = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        AdminMngTextArea = new javax.swing.JTextArea();
        AdminMngClsButton = new javax.swing.JButton();
        AdminMngEditBtn = new javax.swing.JButton();
        AdminJbutonImage = new javax.swing.JButton();
        jLabelImage = new javax.swing.JLabel();
        CestoPainel1 = new javax.swing.JPanel();
        AdminMngRemvBtn = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        AdminMngSearchTxtField1 = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        AdminMngTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        AdminNomeUserTxtField = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        AdminCodUserTxtField = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        AdminFuncaoComboBox = new javax.swing.JComboBox<>();
        AdminAddUserjButton = new javax.swing.JButton();
        AdminSenhaTxtField = new javax.swing.JPasswordField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        AdminTabelaUserjTable1 = new javax.swing.JTable();
        AdminRemoverUserjButton = new javax.swing.JButton();
        AdminDesctivarUserjButton = new javax.swing.JButton();
        AdminActivarUserjButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        AdminNomeEstbjTextField = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        AdminCodEstbjTextField = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        AdminEmailEstbjTextField = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        AdminTelEstbjTextField = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        AdminCisadeEstbjTextField = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        AdminCodPostalEstbjTextField = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        AdminIvaEstbjTextField = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        AdminEstadoEstbjTextField = new javax.swing.JTextField();
        AdminTipoEstjComboBox = new javax.swing.JComboBox<>();
        jLabel34 = new javax.swing.JLabel();
        AdminEstCatjComboBox = new javax.swing.JComboBox<>();
        AdminAddEstjButton = new javax.swing.JButton();
        AdminEditEstjButton = new javax.swing.JButton();
        TipoEstbSelecionadojLabel = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        AdminAddCatjButton = new javax.swing.JButton();
        AdminCatjComboBox = new javax.swing.JComboBox<>();
        label1 = new java.awt.Label();
        label2 = new java.awt.Label();
        Categoria = new java.awt.Label();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        sair = new javax.swing.JMenuItem();

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        jMenu1.setText("File");
        jMenuBar2.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar2.add(jMenu2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ProductoPainel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cesto de Compras", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N

        AdminSearchTxtField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminSearchTxtField.setText("Nome / Codigo de Barras");
        AdminSearchTxtField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AdminSearchTxtFieldMouseClicked(evt);
            }
        });
        AdminSearchTxtField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminSearchTxtFieldActionPerformed(evt);
            }
        });
        AdminSearchTxtField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                AdminSearchTxtFieldKeyTyped(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Procurar Producto:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Codigo:");

        AdminCodigoTxtField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminCodigoTxtField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminCodigoTxtFieldActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Nome:");

        AdminNomeTxtField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("Preco:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Categoria:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Stock:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Quant.:");

        AdminPrecoTxtField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        AdminCatgTxtField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        AdminStockTxtField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        AdminQntTxtField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        AdminAddBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminAddBtn.setText("Adicionar");
        AdminAddBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminAddBtnActionPerformed(evt);
            }
        });

        AdminClsButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminClsButton.setText("Limpar");
        AdminClsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminClsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ProductoPainelLayout = new javax.swing.GroupLayout(ProductoPainel);
        ProductoPainel.setLayout(ProductoPainelLayout);
        ProductoPainelLayout.setHorizontalGroup(
            ProductoPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProductoPainelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ProductoPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ProductoPainelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(250, 250, 250))
                    .addGroup(ProductoPainelLayout.createSequentialGroup()
                        .addGroup(ProductoPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(AdminSearchTxtField)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ProductoPainelLayout.createSequentialGroup()
                                .addGroup(ProductoPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel7))
                                .addGap(18, 18, 18)
                                .addGroup(ProductoPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(AdminCodigoTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(AdminQntTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(ProductoPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(AdminStockTxtField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                        .addComponent(AdminCatgTxtField, javax.swing.GroupLayout.Alignment.TRAILING))
                                    .addComponent(AdminPrecoTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(AdminNomeTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(ProductoPainelLayout.createSequentialGroup()
                                .addComponent(AdminAddBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(AdminClsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        ProductoPainelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AdminAddBtn, AdminClsButton});

        ProductoPainelLayout.setVerticalGroup(
            ProductoPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProductoPainelLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AdminSearchTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(ProductoPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(ProductoPainelLayout.createSequentialGroup()
                        .addComponent(AdminCodigoTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(AdminNomeTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ProductoPainelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)))
                .addGap(18, 18, 18)
                .addGroup(ProductoPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(AdminPrecoTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(ProductoPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(AdminCatgTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(ProductoPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(AdminStockTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(ProductoPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(AdminQntTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(ProductoPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AdminAddBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AdminClsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        ProductoPainelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {AdminAddBtn, AdminClsButton});

        CestoPainel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cesto de Compras", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N

        AdminTabela.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminTabela.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Cod. Barras", "Producto", "Quant.", "Preco Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(AdminTabela);

        AdminRemvBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminRemvBtn.setText("Remover item");
        AdminRemvBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminRemvBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout CestoPainelLayout = new javax.swing.GroupLayout(CestoPainel);
        CestoPainel.setLayout(CestoPainelLayout);
        CestoPainelLayout.setHorizontalGroup(
            CestoPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CestoPainelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CestoPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 648, Short.MAX_VALUE)
                    .addGroup(CestoPainelLayout.createSequentialGroup()
                        .addComponent(AdminRemvBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        CestoPainelLayout.setVerticalGroup(
            CestoPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CestoPainelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(AdminRemvBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        CalculadoraPainel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Calculadora", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N
        CalculadoraPainel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("Preco Total:");

        AdminPrecoTotalTxtField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        AdminConfVendaButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminConfVendaButton.setText("Confirmar");
        AdminConfVendaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminConfVendaButtonActionPerformed(evt);
            }
        });

        AdminCnclButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminCnclButton.setText("Cancelar");
        AdminCnclButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminCnclButtonActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel20.setText("+ IVA:");

        AdminPrecoTotalTxtField1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout CalculadoraPainelLayout = new javax.swing.GroupLayout(CalculadoraPainel);
        CalculadoraPainel.setLayout(CalculadoraPainelLayout);
        CalculadoraPainelLayout.setHorizontalGroup(
            CalculadoraPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, CalculadoraPainelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(CalculadoraPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel20))
                .addGap(18, 18, 18)
                .addGroup(CalculadoraPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(AdminPrecoTotalTxtField1, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AdminPrecoTotalTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(CalculadoraPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(AdminConfVendaButton, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                    .addComponent(AdminCnclButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        CalculadoraPainelLayout.setVerticalGroup(
            CalculadoraPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CalculadoraPainelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(CalculadoraPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AdminPrecoTotalTxtField1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AdminConfVendaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(CalculadoraPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(CalculadoraPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(AdminPrecoTotalTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AdminCnclButton, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(33, 33, 33))
        );

        AdminNovaVendaBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminNovaVendaBtn.setText("Nova Venda");
        AdminNovaVendaBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminNovaVendaBtnActionPerformed(evt);
            }
        });

        AdminUserName.setText("jLabel16");

        AdminUserCode.setText("jLabel18");

        jLabel18.setText("|");

        javax.swing.GroupLayout VendaPainelLayout = new javax.swing.GroupLayout(VendaPainel);
        VendaPainel.setLayout(VendaPainelLayout);
        VendaPainelLayout.setHorizontalGroup(
            VendaPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VendaPainelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(VendaPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(VendaPainelLayout.createSequentialGroup()
                        .addComponent(CalculadoraPainel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(VendaPainelLayout.createSequentialGroup()
                        .addComponent(AdminNovaVendaBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(AdminUserName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AdminUserCode)
                        .addGap(141, 141, 141))
                    .addGroup(VendaPainelLayout.createSequentialGroup()
                        .addComponent(ProductoPainel, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(CestoPainel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        VendaPainelLayout.setVerticalGroup(
            VendaPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, VendaPainelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(VendaPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AdminNovaVendaBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AdminUserName)
                    .addComponent(AdminUserCode)
                    .addComponent(jLabel18))
                .addGap(18, 18, 18)
                .addGroup(VendaPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ProductoPainel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(CestoPainel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(CalculadoraPainel, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Efectuar Venda", VendaPainel);

        ProductoMngPainel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Producto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N
        ProductoMngPainel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel10.setText("Codigo:");

        AdminMngCodigoTxtField1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminMngCodigoTxtField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminMngCodigoTxtField1ActionPerformed(evt);
            }
        });

        jLabel11.setText("Nome:");

        AdminMngNomeTxtField1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel12.setText("Preco:");

        jLabel13.setText("Categoria:");

        jLabel15.setText("Quant.:");

        AdminMngPrecoTxtField1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        AdminMngQntTxtField1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminMngQntTxtField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminMngQntTxtField1ActionPerformed(evt);
            }
        });

        AdminMngAddBtn1.setText("Adicionar ");
        AdminMngAddBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminMngAddBtn1ActionPerformed(evt);
            }
        });

        AdminMngCategoryComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        AdminMngCategoryComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminMngCategoryComboBoxActionPerformed(evt);
            }
        });

        jLabel14.setText("Descricao:");

        AdminMngTextArea.setColumns(20);
        AdminMngTextArea.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminMngTextArea.setRows(5);
        jScrollPane3.setViewportView(AdminMngTextArea);

        AdminMngClsButton.setText("Limpar");
        AdminMngClsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminMngClsButtonActionPerformed(evt);
            }
        });

        AdminMngEditBtn.setText("Editar");

        AdminJbutonImage.setText("Carregar Imagem");
        AdminJbutonImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AdminJbutonImageMouseClicked(evt);
            }
        });
        AdminJbutonImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminJbutonImageActionPerformed(evt);
            }
        });

        jLabelImage.setMaximumSize(new java.awt.Dimension(200, 200));

        javax.swing.GroupLayout ProductoMngPainelLayout = new javax.swing.GroupLayout(ProductoMngPainel);
        ProductoMngPainel.setLayout(ProductoMngPainelLayout);
        ProductoMngPainelLayout.setHorizontalGroup(
            ProductoMngPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProductoMngPainelLayout.createSequentialGroup()
                .addGroup(ProductoMngPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ProductoMngPainelLayout.createSequentialGroup()
                        .addGroup(ProductoMngPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ProductoMngPainelLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(ProductoMngPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel15)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel11)))
                            .addGroup(ProductoMngPainelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel10)))
                        .addGap(18, 18, 18)
                        .addGroup(ProductoMngPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(AdminMngNomeTxtField1)
                            .addComponent(AdminMngPrecoTxtField1)
                            .addComponent(AdminMngCategoryComboBox, 0, 228, Short.MAX_VALUE)
                            .addComponent(AdminMngQntTxtField1)
                            .addComponent(AdminMngCodigoTxtField1)))
                    .addGroup(ProductoMngPainelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(ProductoMngPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ProductoMngPainelLayout.createSequentialGroup()
                                .addComponent(AdminMngAddBtn1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                                .addComponent(AdminMngEditBtn)
                                .addGap(28, 28, 28)
                                .addComponent(AdminMngClsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane3)
                            .addComponent(jLabelImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(ProductoMngPainelLayout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addGap(0, 246, Short.MAX_VALUE)))))
                .addContainerGap())
            .addGroup(ProductoMngPainelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(AdminJbutonImage)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ProductoMngPainelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AdminMngAddBtn1, AdminMngClsButton, AdminMngEditBtn});

        ProductoMngPainelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel11, jLabel12, jLabel13, jLabel14, jLabel15});

        ProductoMngPainelLayout.setVerticalGroup(
            ProductoMngPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProductoMngPainelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ProductoMngPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AdminMngCodigoTxtField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(18, 18, 18)
                .addGroup(ProductoMngPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(AdminMngNomeTxtField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(ProductoMngPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(AdminMngPrecoTxtField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(ProductoMngPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(AdminMngCategoryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(ProductoMngPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(AdminMngQntTxtField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AdminJbutonImage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelImage, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ProductoMngPainelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AdminMngAddBtn1)
                    .addComponent(AdminMngClsButton)
                    .addComponent(AdminMngEditBtn))
                .addContainerGap())
        );

        ProductoMngPainelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {AdminMngAddBtn1, AdminMngClsButton, AdminMngEditBtn});

        ProductoMngPainelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel11, jLabel12, jLabel13, jLabel14, jLabel15});

        CestoPainel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Producto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N
        CestoPainel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        AdminMngRemvBtn.setText("Remover Item");
        AdminMngRemvBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AdminMngRemvBtnMouseClicked(evt);
            }
        });
        AdminMngRemvBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminMngRemvBtnActionPerformed(evt);
            }
        });

        jLabel9.setText("Procurar Producto:");

        AdminMngSearchTxtField1.setText("Nome / Codigo de Barras");
        AdminMngSearchTxtField1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AdminMngSearchTxtField1MouseClicked(evt);
            }
        });
        AdminMngSearchTxtField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                AdminMngSearchTxtField1KeyTyped(evt);
            }
        });

        AdminMngTable.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminMngTable.setModel(new javax.swing.table.DefaultTableModel(
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
        AdminMngTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AdminMngTableMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(AdminMngTable);

        javax.swing.GroupLayout CestoPainel1Layout = new javax.swing.GroupLayout(CestoPainel1);
        CestoPainel1.setLayout(CestoPainel1Layout);
        CestoPainel1Layout.setHorizontalGroup(
            CestoPainel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CestoPainel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CestoPainel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)
                    .addGroup(CestoPainel1Layout.createSequentialGroup()
                        .addComponent(AdminMngRemvBtn)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(CestoPainel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AdminMngSearchTxtField1)))
                .addContainerGap())
        );
        CestoPainel1Layout.setVerticalGroup(
            CestoPainel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CestoPainel1Layout.createSequentialGroup()
                .addGroup(CestoPainel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AdminMngSearchTxtField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 515, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(AdminMngRemvBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(ProductoMngPainel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(CestoPainel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ProductoMngPainel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(CestoPainel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ProductoMngPainel.getAccessibleContext().setAccessibleName("Cadastrar producto");
        CestoPainel1.getAccessibleContext().setAccessibleName("Todos Productos");

        jTabbedPane1.addTab("Gerir Stock", jPanel2);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Utilizador", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel16.setText("Nome Completo:");

        AdminNomeUserTxtField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminNomeUserTxtField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminNomeUserTxtFieldActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel17.setText("Codigo Utilizador:");

        AdminCodUserTxtField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel19.setText("Senha");

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel22.setText("Funcao: ");

        AdminFuncaoComboBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminFuncaoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        AdminFuncaoComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminFuncaoComboBoxActionPerformed(evt);
            }
        });

        AdminAddUserjButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminAddUserjButton.setText("Adicionar");
        AdminAddUserjButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AdminAddUserjButtonMouseClicked(evt);
            }
        });
        AdminAddUserjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminAddUserjButtonActionPerformed(evt);
            }
        });

        AdminSenhaTxtField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(AdminAddUserjButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22)
                            .addComponent(jLabel19)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(AdminNomeUserTxtField)
                            .addComponent(AdminCodUserTxtField)
                            .addComponent(AdminFuncaoComboBox, 0, 194, Short.MAX_VALUE)
                            .addComponent(AdminSenhaTxtField))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(AdminNomeUserTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(AdminCodUserTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(AdminSenhaTxtField))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(AdminFuncaoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(AdminAddUserjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {AdminCodUserTxtField, AdminFuncaoComboBox, AdminNomeUserTxtField, jLabel16, jLabel17, jLabel19, jLabel22});

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Utilizador", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N

        AdminTabelaUserjTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Nome Completo", "Iniciais", "Funcao"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(AdminTabelaUserjTable1);

        AdminRemoverUserjButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminRemoverUserjButton.setText("Remover");
        AdminRemoverUserjButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AdminRemoverUserjButtonMouseClicked(evt);
            }
        });

        AdminDesctivarUserjButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminDesctivarUserjButton.setText("Desactivar");

        AdminActivarUserjButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminActivarUserjButton.setText("Activar");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(AdminRemoverUserjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(AdminDesctivarUserjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(AdminActivarUserjButton)
                        .addGap(0, 181, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AdminActivarUserjButton, AdminDesctivarUserjButton, AdminRemoverUserjButton});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(AdminRemoverUserjButton)
                        .addComponent(AdminDesctivarUserjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(AdminActivarUserjButton))
                .addContainerGap())
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {AdminActivarUserjButton, AdminDesctivarUserjButton, AdminRemoverUserjButton});

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 359, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Gerir Utilizadores", jPanel3);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Detalhes do Estabelecimento", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel21.setText("Nome do Estabelecimento:");

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel23.setText("Codigo do Estabelecimento:");

        AdminCodEstbjTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminCodEstbjTextFieldActionPerformed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel24.setText("Email:");

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel25.setText("Telefone:");

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel26.setText("Cidade:");

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel27.setText("Codigo Postal: ");

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel28.setText("IVA:");

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel29.setText("Tipo de estabelecimento:");

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel30.setText("Estado:");

        AdminTipoEstjComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel34.setText("Categorias:");

        AdminEstCatjComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        AdminEstCatjComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminEstCatjComboBoxActionPerformed(evt);
            }
        });

        AdminAddEstjButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminAddEstjButton.setText("Novo");
        AdminAddEstjButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AdminAddEstjButtonMouseClicked(evt);
            }
        });
        AdminAddEstjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminAddEstjButtonActionPerformed(evt);
            }
        });

        AdminEditEstjButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminEditEstjButton.setText("Editar");
        AdminEditEstjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminEditEstjButtonActionPerformed(evt);
            }
        });

        TipoEstbSelecionadojLabel.setText("jLabel33");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(AdminAddEstjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(AdminEditEstjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(324, 324, 324))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addGap(18, 18, 18)
                        .addComponent(AdminNomeEstbjTextField)
                        .addContainerGap())))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel26)
                    .addComponent(jLabel24)
                    .addComponent(jLabel28)
                    .addComponent(jLabel34))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(AdminCodEstbjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(AdminEstCatjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(AdminCisadeEstbjTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                            .addComponent(AdminEmailEstbjTextField)
                            .addComponent(AdminIvaEstbjTextField))
                        .addGap(76, 76, 76)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel27)
                                    .addComponent(jLabel30))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(AdminCodPostalEstbjTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                                    .addComponent(AdminEstadoEstbjTextField)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel25)
                                    .addComponent(jLabel29))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(TipoEstbSelecionadojLabel)
                                    .addComponent(AdminTipoEstjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(AdminTelEstbjTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(AdminNomeEstbjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(AdminCodEstbjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29)
                    .addComponent(AdminTipoEstjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TipoEstbSelecionadojLabel)
                        .addGap(23, 23, 23)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25)
                            .addComponent(AdminTelEstbjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel27)
                            .addComponent(AdminCodPostalEstbjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel30)
                            .addComponent(AdminEstadoEstbjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(AdminEmailEstbjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(AdminCisadeEstbjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(AdminIvaEstbjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28))))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AdminEstCatjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 102, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AdminAddEstjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AdminEditEstjButton))
                .addContainerGap())
        );

        jPanel6Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {AdminCisadeEstbjTextField, AdminCodEstbjTextField, AdminCodPostalEstbjTextField, AdminEmailEstbjTextField, AdminEstCatjComboBox, AdminEstadoEstbjTextField, AdminIvaEstbjTextField, AdminNomeEstbjTextField, AdminTelEstbjTextField, AdminTipoEstjComboBox});

        jPanel6Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {AdminAddEstjButton, AdminEditEstjButton});

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Adicionar Categoria", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel31.setText("Categoria:");

        AdminAddCatjButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AdminAddCatjButton.setText("Adicionar");
        AdminAddCatjButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AdminAddCatjButtonMouseClicked(evt);
            }
        });
        AdminAddCatjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdminAddCatjButtonActionPerformed(evt);
            }
        });

        AdminCatjComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        label1.setText("label1");

        label2.setText("label2");

        Categoria.setText("label3");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(AdminAddCatjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(AdminCatjComboBox, 0, 233, Short.MAX_VALUE)))
                .addGap(26, 26, 26))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel31)
                    .addComponent(AdminCatjComboBox))
                .addGap(18, 18, 18)
                .addComponent(AdminAddCatjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        jTabbedPane1.addTab("Definicoes do Estabelecimento", jPanel5);

        jMenu3.setText("File");

        sair.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        sair.setText("Sair");
        sair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sairActionPerformed(evt);
            }
        });
        sair.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                sairKeyPressed(evt);
            }
        });
        jMenu3.add(sair);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 676, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 11, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void AdminFuncaoComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminFuncaoComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AdminFuncaoComboBoxActionPerformed

    private void AdminNomeUserTxtFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminNomeUserTxtFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AdminNomeUserTxtFieldActionPerformed

    private void AdminNovaVendaBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminNovaVendaBtnActionPerformed
        // TODO add your handling code here:
        venda = novaVenda();
        AdminSearchTxtField.setEnabled(true);
        AdminAddBtn.setEnabled(true);
    }//GEN-LAST:event_AdminNovaVendaBtnActionPerformed

    private void AdminCnclButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminCnclButtonActionPerformed
        // TODO add your handling code here:
        VendaJpaController vc = new VendaJpaController();
        ProductoVendaJpaController pvc = new ProductoVendaJpaController();
        List<ProductoVenda> PV = pvc.findProductoVendaEntities();

        int id = venda.getVendaId();

        try {
            for (int i = 0; i < PV.size(); i++) {
                if (PV.get(i).getVendaId().getVendaId().equals(id)) {
                    pvc.destroy(PV.get(i).getProductoVendaId());
                }
            }

            mostrarProductosDaVendaNoCesto();

            vc.destroy(venda.getVendaId());
            JOptionPane.showMessageDialog(rootPane, "Cancelado com sucesso!");
            AdminSearchTxtField.setEnabled(false);
            AdminSearchTxtField.setText("Nome / Codigo de Barras");
            AdminAddBtn.setEnabled(false);
            AdminCnclButton.setEnabled(false);
            AdminConfVendaButton.setEnabled(false);
        } catch (NonexistentEntityException ex) {
            JOptionPane.showMessageDialog(rootPane, "Nao foi possivel cancelar venda!");

        }
    }//GEN-LAST:event_AdminCnclButtonActionPerformed

    private void AdminConfVendaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminConfVendaButtonActionPerformed
        if (model.getRowCount() != 0) {
            int dlgRes = JOptionPane.showConfirmDialog(rootPane, "Tem Certeza que quer efectivar esta venda?");

            if (dlgRes == JOptionPane.YES_OPTION) {
                try {

                    AdminSearchTxtField.setEnabled(false);
                    AdminSearchTxtField.setText("Nome / Codigo de Barras");
                    AdminConfVendaButton.setEnabled(false);
                    AdminCnclButton.setEnabled(false);
                    AdminRemvBtn.setEnabled(false);
                    AdminAddBtn.setEnabled(false);
                    subtratirQntStock();
                    model.setRowCount(0);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(rootPane, ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Adicione productos na tabela!");
        }
    }//GEN-LAST:event_AdminConfVendaButtonActionPerformed

    private void AdminRemvBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminRemvBtnActionPerformed
        // TODO add your handling code here:

        if (AdminTabela.getSelectedRow() != -1) {
            int nrLinha = AdminTabela.getSelectedRow();
            int AdminTblSelProdct = Integer.parseInt(AdminTabela.getValueAt(nrLinha, 0).toString());

            ProductoVendaJpaController pv = new ProductoVendaJpaController();
            ProductoVenda P = pv.findProductoVenda(AdminTblSelProdct);

            int dlgRes = JOptionPane.showConfirmDialog(rootPane, "Tem Certeza que quer remover " + P.getProductoId().getNomeProducto() + " desta venda?");

            if (dlgRes == JOptionPane.YES_OPTION) {
                try {
                    pv.destroy(P.getProductoVendaId());
                    model.removeRow(nrLinha);
                    JOptionPane.showMessageDialog(rootPane, "Apagado com sucesso!");
                } catch (NonexistentEntityException ex) {
                    JOptionPane.showMessageDialog(rootPane, "Nao foi possivel apagar o producto! | " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Selecione pelo menos um producto na tabela!");
        }
    }//GEN-LAST:event_AdminRemvBtnActionPerformed

    private void AdminClsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminClsButtonActionPerformed
        // TODO add your handling code here:
        AdminSearchTxtField.setText("Nome / Codigo de Barras");
        AdminCodigoTxtField.setText("");
        AdminNomeTxtField.setText("");
        AdminCatgTxtField.setText("");
        AdminPrecoTxtField.setText("");
        AdminStockTxtField.setText("");
        AdminQntTxtField.setText("");
    }//GEN-LAST:event_AdminClsButtonActionPerformed

    private void AdminAddBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminAddBtnActionPerformed
        // TODO add your handling code here:
        if (AdminCodigoTxtField.getText().equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Busque o producto!");
        } else if (AdminQntTxtField.getText().equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Escreva a quantidade deste producto a comprar!");
        } else {
            String codigoBarras = AdminCodigoTxtField.getText();
            String qntAComprar = AdminQntTxtField.getText();
            try {
                adicionarProductoVenda(codigoBarras, qntAComprar);
                AdminSearchTxtField.setText("");
                AdminCodigoTxtField.setText("");
                AdminNomeTxtField.setText("");
                AdminCatgTxtField.setText("");
                AdminPrecoTxtField.setText("");
                AdminStockTxtField.setText("");
                AdminQntTxtField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(rootPane, "Nao foi possivel adicionar o producto!");
            }
        }
        AdminCnclButton.setEnabled(true);
        AdminConfVendaButton.setEnabled(true);
        AdminRemvBtn.setEnabled(true);
    }//GEN-LAST:event_AdminAddBtnActionPerformed

    private void AdminCodigoTxtFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminCodigoTxtFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AdminCodigoTxtFieldActionPerformed

    private void AdminSearchTxtFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AdminSearchTxtFieldKeyTyped
        // TODO add your handling code here:
        ProductosJpaController pc = new ProductosJpaController();
        List<Productos> P = pc.findProductosEntities();

        for (int i = 0; i < P.size(); i++) {
            if (AdminSearchTxtField.getText().equals("")) {
                AdminCodigoTxtField.setText("");
                AdminNomeTxtField.setText("");
                AdminCatgTxtField.setText("");
                AdminPrecoTxtField.setText("");
                AdminStockTxtField.setText("");
            } else if (P.get(i).getNomeProducto().toUpperCase().trim().contains(AdminSearchTxtField.getText().toUpperCase().trim()) || P.get(i).getCodigoBarras().contains(AdminSearchTxtField.getText())) {
                AdminCodigoTxtField.setText(P.get(i).getCodigoBarras());
                AdminNomeTxtField.setText(P.get(i).getNomeProducto());
                AdminCatgTxtField.setText(P.get(i).getCategoriaId().getDescricaoCategoria());
                String Prc = P.get(i).getPrecoProducto().toString();
                AdminPrecoTxtField.setText(Prc);
                AdminStockTxtField.setText(P.get(i).getQuantidadeProducto());
            }
        }
    }//GEN-LAST:event_AdminSearchTxtFieldKeyTyped

    private void AdminSearchTxtFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminSearchTxtFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AdminSearchTxtFieldActionPerformed

    private void AdminSearchTxtFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AdminSearchTxtFieldMouseClicked
        // TODO add your handling code here:
        AdminSearchTxtField.setText("");
    }//GEN-LAST:event_AdminSearchTxtFieldMouseClicked

    private void AdminMngSearchTxtField1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AdminMngSearchTxtField1KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_AdminMngSearchTxtField1KeyTyped

    private void AdminMngSearchTxtField1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AdminMngSearchTxtField1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_AdminMngSearchTxtField1MouseClicked

    private void AdminMngCategoryComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminMngCategoryComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AdminMngCategoryComboBoxActionPerformed

    private void AdminMngAddBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminMngAddBtn1ActionPerformed
        // TODO add your handling code here:
        try {
            cadastrarProducto();
            JOptionPane.showMessageDialog(rootPane, "Adicionado com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e.getCause());
        }
    }//GEN-LAST:event_AdminMngAddBtn1ActionPerformed

    private void AdminMngQntTxtField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminMngQntTxtField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AdminMngQntTxtField1ActionPerformed

    private void AdminMngCodigoTxtField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminMngCodigoTxtField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AdminMngCodigoTxtField1ActionPerformed

    private void AdminCodEstbjTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminCodEstbjTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AdminCodEstbjTextFieldActionPerformed

    private void AdminAddUserjButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AdminAddUserjButtonMouseClicked
        // TODO add your handling code here:
        String nome = AdminNomeUserTxtField.getText();
        String cod = AdminCodUserTxtField.getText();
        String funcao = AdminFuncaoComboBox.getSelectedItem().toString();
        String senha = AdminSenhaTxtField.getText();

        if (nome.equals("") || cod.equals("") || funcao.equals("") || senha.equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Preencha todos os campos!");
        } else {
            cadastrarUser();
        }
    }//GEN-LAST:event_AdminAddUserjButtonMouseClicked

    private void AdminAddUserjButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminAddUserjButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AdminAddUserjButtonActionPerformed

    private void AdminMngRemvBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AdminMngRemvBtnMouseClicked
        // TODO add your handling code here:
        if (AdminMngTable.getSelectedRow() != -1) {
            int nrLinha = AdminMngTable.getSelectedRow();
            int AdminTblSelProdct = Integer.parseInt(AdminMngTable.getValueAt(nrLinha, 0).toString());
            ProductosJpaController pc = new ProductosJpaController();
            Productos P = pc.findProductos(AdminTblSelProdct);
            int dlgRes = JOptionPane.showConfirmDialog(rootPane, "Tem Certeza que quer remover " + P.getNomeProducto() + " do stock?");
//
            if (dlgRes == JOptionPane.YES_OPTION) {
                try {
                    try {
                        javax.ws.rs.client.Client client;
                        client = ClientBuilder.newClient();
                        String BASE_URI = "http://localhost:8086/Comparador/webresources";
                        WebTarget webTarget = client.target(BASE_URI).path("productos").path(P.getProductoId().toString());

                        Response response = webTarget.request().delete();

                        System.out.println(response);
                        System.out.println(response.getStatus());
                        System.out.println(response.readEntity(String.class));
                        client.close();
                    } catch (Exception exp) {
                        JOptionPane.showMessageDialog(rootPane, "Nao foi possivel remover o producto online! | " + exp.getMessage());
                    }

                    pc.destroy(P.getProductoId());
                    model2.removeRow(nrLinha);
                    JOptionPane.showMessageDialog(rootPane, "Apagado com sucesso!");
                } catch (NonexistentEntityException ex) {
                    JOptionPane.showMessageDialog(rootPane, "Nao foi possivel apagar o producto! | " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Selecione pelo menos um producto na tabela!");
        }
    }//GEN-LAST:event_AdminMngRemvBtnMouseClicked

    private void AdminMngTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AdminMngTableMouseClicked
    }//GEN-LAST:event_AdminMngTableMouseClicked

    private void AdminRemoverUserjButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AdminRemoverUserjButtonMouseClicked
        // TODO add your handling code here:
//        AdminTabelaUserjTable1
        if (AdminTabelaUserjTable1.getSelectedRow() != -1) {
            int nrLinha = AdminTabelaUserjTable1.getSelectedRow();
            int AdminTblSelProdct = Integer.parseInt(AdminTabelaUserjTable1.getValueAt(nrLinha, 0).toString());
            UtilizadoresJpaController uc = new UtilizadoresJpaController();
            Utilizadores U = uc.findUtilizadores(AdminTblSelProdct);
            int dlgRes = JOptionPane.showConfirmDialog(rootPane, "Tem Certeza que quer remover " + U.getNomeUtlizador() + " do stock?");
//
            if (dlgRes == JOptionPane.YES_OPTION) {
                try {
                    uc.destroy(U.getIdUtilizador());
                    model3.removeRow(nrLinha);
                    JOptionPane.showMessageDialog(rootPane, "Apagado com sucesso!");
                } catch (NonexistentEntityException ex) {
                    JOptionPane.showMessageDialog(rootPane, "Nao foi possivel apagar o producto! | " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Selecione pelo menos um producto na tabela!");
        }

    }//GEN-LAST:event_AdminRemoverUserjButtonMouseClicked

    private void AdminAddEstjButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AdminAddEstjButtonMouseClicked
        // TODO add your handling code here:
        cadastrarEstabelecimento();
    }//GEN-LAST:event_AdminAddEstjButtonMouseClicked

    private void AdminAddEstjButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminAddEstjButtonActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_AdminAddEstjButtonActionPerformed

    private void sairKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sairKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_sairKeyPressed

    private void sairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sairActionPerformed
        // TODO add your handling code here:
        estActual = null;
        utlActual = null;
        Login login = new Login();
        login.setVisible(true);
        dispose();
    }//GEN-LAST:event_sairActionPerformed

    private void AdminJbutonImageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AdminJbutonImageMouseClicked
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(null);
        File f = chooser.getSelectedFile();
        nomeImagem = f.getAbsolutePath();
        jLabelImage.setIcon(new ImageIcon(f.toString()));

        try {
            File ficheiro = new File(nomeImagem);
            FileImageInputStream fis = new FileImageInputStream(ficheiro);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[10240];
            for (int length = 0; (length = fis.read(buffer)) > 0;) {
                baos.write(buffer, 0, length);
            }
            jLabelImage.setVisible(true);
            imagem = baos.toByteArray();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, "Nao foi possivel carregar imagem");
        }
    }//GEN-LAST:event_AdminJbutonImageMouseClicked

    private void AdminJbutonImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminJbutonImageActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AdminJbutonImageActionPerformed

    private void AdminEditEstjButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminEditEstjButtonActionPerformed
        editarEstabelecimento();
    }//GEN-LAST:event_AdminEditEstjButtonActionPerformed

    private void AdminAddCatjButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminAddCatjButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AdminAddCatjButtonActionPerformed

    private void AdminAddCatjButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AdminAddCatjButtonMouseClicked
        // TODO add your handling code here:
        adicionarCategoria();
    }//GEN-LAST:event_AdminAddCatjButtonMouseClicked

    private void AdminEstCatjComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminEstCatjComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AdminEstCatjComboBoxActionPerformed

    private void AdminMngClsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminMngClsButtonActionPerformed
        AdminMngCodigoTxtField1.setText("");
        AdminMngNomeTxtField1.setText("");
        AdminMngPrecoTxtField1.setText("");
        AdminMngQntTxtField1.setText("");
        AdminMngTextArea.setText("");
        jLabelImage.setVisible(false);
    }//GEN-LAST:event_AdminMngClsButtonActionPerformed

    private void AdminMngRemvBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdminMngRemvBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AdminMngRemvBtnActionPerformed

    public void adicionarProductoVenda(String codigo, String qnt) throws Exception {
        ProductosJpaController pc = new ProductosJpaController();
        ProductoVendaJpaController pvc = new ProductoVendaJpaController();

        List<Productos> P = pc.findProductosEntities();
        ProductoVenda pv = new ProductoVenda();

        BigInteger q = new BigInteger(qnt);
        for (int i = 0; i < P.size(); i++) {
            if (P.get(i).getCodigoBarras().equals(codigo)) {
                pv.setProductoId(P.get(i));
                pv.setPreco(P.get(i).getPrecoProducto());
                pv.setQuantidade(qnt);
                pv.setPrecoTotal(P.get(i).getPrecoProducto().multiply(q));
                pv.setVendaId(venda);
                pvc.create(pv);
                mostrarProductosNoCesto(pv.getProductoVendaId());
                break;
            }
        }
    }

    public void mostrarProductosNoCesto(int productovendaId) {
        ProductoVendaJpaController pvc = new ProductoVendaJpaController();
        ProductoVenda PV = pvc.findProductoVenda(productovendaId);

        model.addRow(new String[]{PV.getProductoVendaId().toString(), PV.getProductoId().getCodigoBarras(), PV.getProductoId().getNomeProducto(), PV.getQuantidade(), PV.getPrecoTotal().toString()});
        actual = actual + PV.getPrecoTotal().intValue();
        AdminPrecoTotalTxtField.setText(String.valueOf(actual));
        AdminTabela.setModel(model);
        AdminTabela.getColumnModel().getColumn(0).setMinWidth(0);
        AdminTabela.getColumnModel().getColumn(0).setMaxWidth(0);
        AdminTabela.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    public void subtratirQntStock() throws Exception {
//        JOptionPane.showMessageDialog(rootPane, "Entrou");
        ProductosJpaController pc = new ProductosJpaController();
        VendaJpaController vc = new VendaJpaController();
        List<Productos> P = pc.findProductosEntities();

        Venda v = vc.findVenda(venda.getVendaId());

        List<ProductoVenda> PV = v.getProductoVendaList();
        for (int i = 0; i < P.size(); i++) {
            for (int j = 0; j < PV.size(); j++) {
                if (P.get(i).getProductoId() == PV.get(j).getProductoId().getProductoId()) {
                    int qntPrd = Integer.parseInt(PV.get(j).getQuantidade().trim());
                    int qntPrdStk = Integer.parseInt(P.get(i).getQuantidadeProducto());
                    int subtrct = qntPrdStk - qntPrd;
                    P.get(i).setQuantidadeProducto(Integer.toString(subtrct));
                    pc.edit(P.get(i));

                    try {
                        javax.ws.rs.client.Client client;
                        client = ClientBuilder.newClient();
                        String BASE_URI = "http://localhost:8086/Comparador/webresources";
                        WebTarget webTarget = client.target(BASE_URI).path("productos");

                        Response response = webTarget
                                .request()
                                .accept(MediaType.APPLICATION_XML)
                                .put(Entity.entity(P.get(i), MediaType.APPLICATION_XML));

                        System.out.println(response);
                        client.close();
                    } catch (Exception exp) {
                        JOptionPane.showMessageDialog(rootPane, "Nao foi possivel actualizar producto online! | " + exp.getStackTrace());
                    }
                }
            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AdminActivarUserjButton;
    private javax.swing.JButton AdminAddBtn;
    private javax.swing.JButton AdminAddCatjButton;
    private javax.swing.JButton AdminAddEstjButton;
    private javax.swing.JButton AdminAddUserjButton;
    private javax.swing.JTextField AdminCatgTxtField;
    private javax.swing.JComboBox<String> AdminCatjComboBox;
    private javax.swing.JTextField AdminCisadeEstbjTextField;
    private javax.swing.JButton AdminClsButton;
    private javax.swing.JButton AdminCnclButton;
    private javax.swing.JTextField AdminCodEstbjTextField;
    private javax.swing.JTextField AdminCodPostalEstbjTextField;
    private javax.swing.JTextField AdminCodUserTxtField;
    private javax.swing.JTextField AdminCodigoTxtField;
    private javax.swing.JButton AdminConfVendaButton;
    private javax.swing.JButton AdminDesctivarUserjButton;
    private javax.swing.JButton AdminEditEstjButton;
    private javax.swing.JTextField AdminEmailEstbjTextField;
    private javax.swing.JComboBox<String> AdminEstCatjComboBox;
    private javax.swing.JTextField AdminEstadoEstbjTextField;
    private javax.swing.JComboBox<String> AdminFuncaoComboBox;
    private javax.swing.JTextField AdminIvaEstbjTextField;
    private javax.swing.JButton AdminJbutonImage;
    private javax.swing.JButton AdminMngAddBtn1;
    private javax.swing.JComboBox<String> AdminMngCategoryComboBox;
    private javax.swing.JButton AdminMngClsButton;
    private javax.swing.JTextField AdminMngCodigoTxtField1;
    private javax.swing.JButton AdminMngEditBtn;
    private javax.swing.JTextField AdminMngNomeTxtField1;
    private javax.swing.JTextField AdminMngPrecoTxtField1;
    private javax.swing.JTextField AdminMngQntTxtField1;
    private javax.swing.JButton AdminMngRemvBtn;
    private javax.swing.JTextField AdminMngSearchTxtField1;
    private javax.swing.JTable AdminMngTable;
    private javax.swing.JTextArea AdminMngTextArea;
    private javax.swing.JTextField AdminNomeEstbjTextField;
    private javax.swing.JTextField AdminNomeTxtField;
    private javax.swing.JTextField AdminNomeUserTxtField;
    private javax.swing.JButton AdminNovaVendaBtn;
    private javax.swing.JTextField AdminPrecoTotalTxtField;
    private javax.swing.JTextField AdminPrecoTotalTxtField1;
    private javax.swing.JTextField AdminPrecoTxtField;
    private javax.swing.JTextField AdminQntTxtField;
    private javax.swing.JButton AdminRemoverUserjButton;
    private javax.swing.JButton AdminRemvBtn;
    private javax.swing.JTextField AdminSearchTxtField;
    private javax.swing.JPasswordField AdminSenhaTxtField;
    private javax.swing.JTextField AdminStockTxtField;
    private javax.swing.JTable AdminTabela;
    private javax.swing.JTable AdminTabelaUserjTable1;
    private javax.swing.JTextField AdminTelEstbjTextField;
    private javax.swing.JComboBox<String> AdminTipoEstjComboBox;
    private javax.swing.JLabel AdminUserCode;
    private javax.swing.JLabel AdminUserName;
    private javax.swing.JPanel CalculadoraPainel;
    private java.awt.Label Categoria;
    private javax.swing.JPanel CestoPainel;
    private javax.swing.JPanel CestoPainel1;
    private javax.swing.JPanel ProductoMngPainel;
    private javax.swing.JPanel ProductoPainel;
    private javax.swing.JLabel TipoEstbSelecionadojLabel;
    private javax.swing.JPanel VendaPainel;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelImage;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private java.awt.Label label1;
    private java.awt.Label label2;
    private javax.swing.JMenuItem sair;
    // End of variables declaration//GEN-END:variables
byte[] imagem = null;
    String nomeImagem = null;
}
