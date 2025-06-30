package view;

import javax.swing.*;
import java.awt.*;
import model.*;

/**
 * Classe principal que representa a janela do sistema
 * Contém todas as abas de funcionalidades
 */
public class MainFrame extends JFrame {
    
    /**
     * Construtor que inicializa a janela principal
     */
    public MainFrame() {
        // Configurações básicas da janela
        setTitle("Sistema de Restaurante - Reservas e Pedidos");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela
        
        // Inicializa a interface e carrega dados iniciais
        initUI();
        carregarDadosIniciais();
    }
    
    /**
     * Inicializa os componentes da interface
     */
    private void initUI() {
        // Cria um painel com abas para organizar as funcionalidades
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Adiciona as abas ao painel principal
        tabbedPane.addTab("Reservas", new ReservaPanel());
        tabbedPane.addTab("Cancelar Reservas", new CancelarReservaPanel());
        tabbedPane.addTab("Listar Reservas", new ListarReservasPanel());
        tabbedPane.addTab("Mesas Disponíveis", new MesasDisponiveisPanel());
        tabbedPane.addTab("Cardápio", new CardapioPanel());
        tabbedPane.addTab("Pedidos", new PedidosPanel());
        
        // Adiciona o painel de abas à janela
        add(tabbedPane);
    }
    
    /**
     * Carrega dados iniciais no banco de dados (mesas)
     */
    private void carregarDadosIniciais() {
        try {
            // Verifica se já existem mesas cadastradas
            if (Mesa.buscarPorNumero(1) == null) {
                // Cria mesas regulares (1 a 8)
                for (int i = 1; i <= 8; i++) {
                    Mesa mesa = new Mesa(i, 4); // Mesas para 4 pessoas
                    mesa.salvar();
                }
                
                // Cria mesas VIP (9 e 10)
                MesaVIP mesa9 = new MesaVIP(9, 6, true);  // Mesa grande com vista
                MesaVIP mesa10 = new MesaVIP(10, 4, false); // Mesa padrão sem vista
                mesa9.salvar();
                mesa10.salvar();
                
                JOptionPane.showMessageDialog(this, 
                    "Dados iniciais (mesas) criados com sucesso!", 
                    "Informação", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar dados iniciais: " + e.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Método main para iniciar a aplicação
     */
    public static void main(String[] args) {
        // Garante que a interface seja criada na thread de eventos do Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Define o look and feel do sistema operacional
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                // Cria e exibe a janela principal
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Erro ao iniciar a aplicação: " + e.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
