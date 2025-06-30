package controller;

import model.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe controladora para gerenciar as operações de reserva
 * Faz a mediação entre a view e o model
 */
public class ReservaController {
    
    /**
     * Realiza uma nova reserva
     * @param cliente Cliente que está fazendo a reserva
     * @param numeroMesa Número da mesa desejada
     * @param horario Horário da reserva
     * @throws SQLException em caso de erro no banco de dados
     * @throws IllegalStateException se a mesa já estiver reservada
     */
    public void fazerReserva(Cliente cliente, int numeroMesa, LocalDateTime horario) 
            throws SQLException, IllegalStateException {
        
        // Validar parâmetros
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }
        
        if (numeroMesa < 1 || numeroMesa > 20) {
            throw new IllegalArgumentException("Número da mesa inválido");
        }
        
        if (horario == null || horario.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Horário inválido");
        }
        
        // Buscar mesa no banco de dados
        Mesa mesa = Mesa.buscarPorNumero(numeroMesa);
        if (mesa == null) {
            throw new IllegalArgumentException("Mesa não encontrada");
        }
        
        // Verificar se a mesa já está ocupada
        if (mesa.isOcupada()) {
            throw new IllegalStateException("Mesa já está ocupada");
        }
        
        // Verificar conflito de horário
        if (verificarConflitoHorario(numeroMesa, horario)) {
            throw new IllegalStateException("Já existe reserva para esta mesa no horário selecionado");
        }
        
        // Criar e salvar a reserva
        Reserva reserva = new Reserva(cliente, mesa, horario);
        reserva.salvar();
    }
    
    /**
     * Verifica se já existe reserva para a mesa no horário especificado
     * @param numeroMesa Número da mesa
     * @param horario Horário a verificar
     * @return true se já existir reserva, false caso contrário
     * @throws SQLException em caso de erro no banco de dados
     */
    private boolean verificarConflitoHorario(int numeroMesa, LocalDateTime horario) throws SQLException {
        Connection conn = Database.getInstance().getConnection();
        String sql = "SELECT COUNT(*) FROM reservas WHERE mesa_numero = ? AND horario = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numeroMesa);
            stmt.setString(2, horario.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.getInt(1) > 0;
            }
        }
    }
    
    /**
     * Cancela uma reserva existente
     * @param idReserva ID da reserva a cancelar
     * @throws SQLException em caso de erro no banco de dados
     * @throws IllegalArgumentException se a reserva não for encontrada
     */
    public void cancelarReserva(int idReserva) throws SQLException, IllegalArgumentException {
        // Buscar reserva no banco de dados
        Connection conn = Database.getInstance().getConnection();
        String sql = "SELECT r.*, m.* FROM reservas r JOIN mesas m ON r.mesa_numero = m.numero WHERE r.id = ?";
        
        Reserva reserva = null;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idReserva);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    reserva = new Reserva();
                    reserva.setId(rs.getInt("id"));
                    
                    Cliente cliente = Cliente.buscarPorId(rs.getInt("cliente_id"));
                    reserva.setCliente(cliente);
                    
                    Mesa mesa = new Mesa();
                    mesa.setNumero(rs.getInt("numero"));
                    mesa.setCapacidade(rs.getInt("capacidade"));
                    mesa.setOcupada(rs.getBoolean("ocupada"));
                    reserva.setMesa(mesa);
                    
                    reserva.setHorario(LocalDateTime.parse(rs.getString("horario")));
                }
            }
        }
        
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva não encontrada");
        }
        
        // Cancelar a reserva
        reserva.cancelar();
    }
    
    /**
     * Lista todas as reservas cadastradas
     * @return Lista de reservas
     * @throws SQLException em caso de erro no banco de dados
     */
    public List<Reserva> listarReservas() throws SQLException {
        List<Reserva> reservas = new ArrayList<>();
        Connection conn = Database.getInstance().getConnection();
        
        // Query para buscar reservas com informações de cliente e mesa
        String sql = "SELECT r.id, r.horario, " +
                     "c.id as cliente_id, c.nome as cliente_nome, c.telefone as cliente_telefone, " +
                     "m.numero as mesa_numero, m.capacidade as mesa_capacidade, m.ocupada as mesa_ocupada " +
                     "FROM reservas r " +
                     "JOIN clientes c ON r.cliente_id = c.id " +
                     "JOIN mesas m ON r.mesa_numero = m.numero " +
                     "ORDER BY r.horario";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Reserva reserva = new Reserva();
                reserva.setId(rs.getInt("id"));
                reserva.setHorario(LocalDateTime.parse(rs.getString("horario")));
                
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("cliente_id"));
                cliente.setNome(rs.getString("cliente_nome"));
                cliente.setTelefone(rs.getString("cliente_telefone"));
                reserva.setCliente(cliente);
                
                Mesa mesa = new Mesa();
                mesa.setNumero(rs.getInt("mesa_numero"));
                mesa.setCapacidade(rs.getInt("mesa_capacidade"));
                mesa.setOcupada(rs.getBoolean("mesa_ocupada"));
                reserva.setMesa(mesa);
                
                reservas.add(reserva);
            }
        }
        return reservas;
    }
    
    /**
     * Busca reservas por nome do cliente
     * @param nome Nome ou parte do nome do cliente
     * @return Lista de reservas encontradas
     * @throws SQLException em caso de erro no banco de dados
     */
    public List<Reserva> buscarPorNomeCliente(String nome) throws SQLException {
        List<Reserva> reservas = new ArrayList<>();
        Connection conn = Database.getInstance().getConnection();
        
        String sql = "SELECT r.id, r.horario, " +
                     "c.id as cliente_id, c.nome as cliente_nome, c.telefone as cliente_telefone, " +
                     "m.numero as mesa_numero, m.capacidade as mesa_capacidade, m.ocupada as mesa_ocupada " +
                     "FROM reservas r " +
                     "JOIN clientes c ON r.cliente_id = c.id " +
                     "JOIN mesas m ON r.mesa_numero = m.numero " +
                     "WHERE c.nome LIKE ? " +
                     "ORDER BY r.horario";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reserva reserva = new Reserva();
                    reserva.setId(rs.getInt("id"));
                    reserva.setHorario(LocalDateTime.parse(rs.getString("horario")));
                    
                    Cliente cliente = new Cliente();
                    cliente.setId(rs.getInt("cliente_id"));
                    cliente.setNome(rs.getString("cliente_nome"));
                    cliente.setTelefone(rs.getString("cliente_telefone"));
                    reserva.setCliente(cliente);
                    
                    Mesa mesa = new Mesa();
                    mesa.setNumero(rs.getInt("mesa_numero"));
                    mesa.setCapacidade(rs.getInt("mesa_capacidade"));
                    mesa.setOcupada(rs.getBoolean("mesa_ocupada"));
                    reserva.setMesa(mesa);
                    
                    reservas.add(reserva);
                }
            }
        }
        return reservas;
    }
}
