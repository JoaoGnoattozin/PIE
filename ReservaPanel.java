package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.*;
import model.*;
import controller.ReservaController;

/**
 * Painel para realizar novas reservas
 */
public class ReservaPanel extends JPanel {
    private JTextField nomeField, telefoneField, descontoField, mesaField;
    private JFormattedTextField horarioField;
    private JComboBox<String> tipoClienteCombo;
    private ReservaController controller;
    
    /**
     * Construtor que inicializa o painel
     */
    public ReservaPanel() {
        this.controller = new ReservaController();
        
        // Configuração do layout e bordas
        setLayout(new GridLayout(0, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Inicializa os componentes
        initComponents();
    }
    
    /**
     * Inicializa todos os componentes do painel
     */
    private void initComponents() {
        // Campo de nome
        add(new JLabel("Nome:"));
        nomeField = new JTextField();
        add(nomeField);
        
        // Campo de telefone
        add(new JLabel("Telefone (11 dígitos):"));
        telefoneField = new JTextField();
        add(telefoneField);
        
        // Combo box para tipo de cliente
        add(new JLabel("Tipo de Cliente:"));
        tipoClienteCombo = new JComboBox<>(new String[]{"Regular", "VIP"});
        add(tipoClienteCombo);
        
        // Campo de desconto (apenas para VIP)
        add(new JLabel("Desconto VIP (%):"));
        descontoField = new JTextField();
        descontoField.setEnabled(false);
        add(descontoField);
        
        // Listener para habilitar/desabilitar campo de desconto
        tipoClienteCombo.addActionListener(e -> {
            descontoField.setEnabled(tipoClienteCombo.getSelectedIndex() == 1);
        });
        
        // Campo de número da mesa
        add(new JLabel("Número da Mesa (1-10):"));
        mesaField = new JTextField();
        add(mesaField);
        
        // Campo de horário com máscara
        add(new JLabel("Horário (dd/MM/yyyy HH:mm):"));
        horarioField = new JFormattedTextField(
            new SimpleDateFormat("dd/MM/yyyy HH:mm"));
        horarioField.setValue(LocalDateTime.now().plusHours(1)); // Valor padrão: daqui 1 hora
        add(horarioField);
        
        // Botão para fazer reserva
        add(new JLabel()); // Espaço vazio para alinhamento
        JButton reservarButton = new JButton("Fazer Reserva");
        reservarButton.addActionListener(this::fazerReserva);
        add(reservarButton);
    }
    
    /**
     * Método chamado ao clicar no botão de fazer reserva
     */
    private void fazerReserva(ActionEvent e) {
        try {
            // Validação dos campos
            if (nomeField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Nome é obrigatório");
            }
            
            if (telefoneField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Telefone é obrigatório");
            }
            
            if (mesaField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Número da mesa é obrigatório");
            }
            
            if (horarioField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Horário é obrigatório");
            }
            
            // Criar o cliente (regular ou VIP)
            Cliente cliente;
            if (tipoClienteCombo.getSelectedIndex() == 1) { // VIP
                if (descontoField.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("Desconto é obrigatório para clientes VIP");
                }
                
                double desconto = Double.parseDouble(descontoField.getText());
                cliente = new ClienteVIP(
                    nomeField.getText(), 
                    telefoneField.getText(), 
                    desconto
                );
            } else { // Regular
                cliente = new Cliente(
                    nomeField.getText(), 
                    telefoneField.getText()
                );
            }
            
            // Salvar cliente no banco de dados
            cliente.salvar();
            
            // Obter número da mesa e horário
            int numeroMesa = Integer.parseInt(mesaField.getText());
            LocalDateTime horario = LocalDateTime.parse(
                horarioField.getText(), 
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            );
            
            // Fazer a reserva através do controller
            controller.fazerReserva(cliente, numeroMesa, horario);
            
            // Mensagem de sucesso e limpeza dos campos
            JOptionPane.showMessageDialog(this, 
                "Reserva realizada com sucesso!", 
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparCampos();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Número da mesa ou desconto inválido", 
                "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, 
                "Formato de horário inválido. Use dd/MM/yyyy HH:mm", 
                "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, 
                ex.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro no banco de dados: " + ex.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Limpa todos os campos do formulário
     */
    private void limparCampos() {
        nomeField.setText("");
        telefoneField.setText("");
        tipoClienteCombo.setSelectedIndex(0);
        descontoField.setText("");
        mesaField.setText("");
        horarioField.setValue(LocalDateTime.now().plusHours(1));
    }
}
