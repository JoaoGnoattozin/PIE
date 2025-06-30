package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import model.*;
import controller.ReservaController;

/**
 * Painel para listar todas as reservas cadastradas
 */
public class ListarReservasPanel extends JPanel {
    private JTable reservasTable;
    private DefaultTableModel tableModel;
    private ReservaController controller;
    
    /**
     * Construtor que inicializa o painel
     */
    public ListarReservasPanel() {
        this.controller = new ReservaController();
        
        // Configuração do layout
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Inicializa os componentes
        initComponents();
        
        // Carrega as reservas do banco de dados
