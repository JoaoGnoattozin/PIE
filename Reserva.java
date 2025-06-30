package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe que representa uma reserva no restaurante
 * Relaciona um cliente com uma mesa em um horário específico
 */
public class Reserva {
    private int id;
    private Cliente cliente;
    private Mesa mesa;
    private LocalDateTime horario;
    
    // Formato para exibição de datas
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Construtores
    public Reserva() {}

    public Reserva(Cliente cliente, Mesa mesa, LocalDateTime horario) {
        this.cliente = cliente;
        this.mesa = mesa;
        this.horario = horario;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }
        this.cliente = cliente;
    }
    
    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) {
        if (mesa == null) {
            throw new IllegalArgumentException("Mesa não pode ser nula");
        }
        this.mesa = mesa;
    }
    
    public LocalDateTime getHorario() { return horario; }
    public void setHorario(LocalDateTime horario) {
        if (horario == null || horario.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Horário inválido");
        }
        this.horario = horario;
    }
    
    /**
     * Retorna o horário formatado como string
     * @return String no formato "dd/MM/yyyy HH:mm"
     */
    public String getHorarioFormatado() {
        return horario.format(FORMATTER);
    }
    
    /**
     * Salva a reserva no banco de dados
     * @throws SQLException em caso de erro no banco de dados
     */
    public void salvar() throws SQLException {
        Connection conn = Database.getInstance().getConnection();
        String sql = "INSERT INTO reservas (cliente_id, mesa_numero, horario) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, cliente.getId());
            stmt.setInt(2, mesa.getNumero());
            stmt.setString(3, horario.toString());
            stmt.executeUpdate();
            
            // Obter o ID gerado
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.id = rs.getInt(1);
                }
            }
        }
        
        // Marcar mesa como ocupada
        mesa.setOcupada(true);
        mesa.salvar();
    }
    
    /**
     * Cancela a reserva no banco de dados
     * @throws SQLException em caso de erro no banco de dados
     */
    public void cancelar() throws SQLException {
        Connection conn = Database.getInstance().getConnection();
        String sql = "DELETE FROM reservas WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
        
        // Liberar a mesa
        mesa.setOcupada(false);
        mesa.salvar();
    }
}
