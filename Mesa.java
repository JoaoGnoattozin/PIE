package model;

import java.sql.*;

/**
 * Classe que representa uma mesa do restaurante
 * Contém métodos para persistência no banco de dados
 */
public class Mesa {
    private int numero;
    private int capacidade;
    private boolean ocupada;

    // Construtores
    public Mesa() {}

    public Mesa(int numero, int capacidade) {
        setNumero(numero);
        setCapacidade(capacidade);
        this.ocupada = false;
    }

    // Getters e Setters
    public int getNumero() { return numero; }
    public void setNumero(int numero) {
        if (numero < 1 || numero > 20) {
            throw new IllegalArgumentException("Número da mesa deve ser entre 1 e 20");
        }
        this.numero = numero;
    }
    
    public int getCapacidade() { return capacidade; }
    public void setCapacidade(int capacidade) {
        if (capacidade < 1 || capacidade > 10) {
            throw new IllegalArgumentException("Capacidade deve ser entre 1 e 10");
        }
        this.capacidade = capacidade;
    }
    
    public boolean isOcupada() { return ocupada; }
    public void setOcupada(boolean ocupada) { this.ocupada = ocupada; }

    /**
     * Salva a mesa no banco de dados
     * @throws SQLException em caso de erro no banco de dados
     */
    public void salvar() throws SQLException {
        Connection conn = Database.getInstance().getConnection();
        String sql = "INSERT OR REPLACE INTO mesas (numero, capacidade, ocupada, vip) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numero);
            stmt.setInt(2, capacidade);
            stmt.setBoolean(3, ocupada);
            stmt.setBoolean(4, false); // Mesa regular
            stmt.executeUpdate();
        }
    }
    
    /**
     * Busca uma mesa pelo número
     * @param numero Número da mesa
     * @return Mesa encontrada ou null se não existir
     * @throws SQLException em caso de erro no banco de dados
     */
    public static Mesa buscarPorNumero(int numero) throws SQLException {
        Connection conn = Database.getInstance().getConnection();
        String sql = "SELECT * FROM mesas WHERE numero = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numero);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Mesa m = new Mesa();
                    m.setNumero(rs.getInt("numero"));
                    m.setCapacidade(rs.getInt("capacidade"));
                    m.setOcupada(rs.getBoolean("ocupada"));
                    return m;
                }
            }
        }
        return null;
    }
    
    /**
     * Lista todas as mesas cadastradas
     * @return Lista de mesas
     * @throws SQLException em caso de erro no banco de dados
     */
    public static List<Mesa> listarTodas() throws SQLException {
        List<Mesa> mesas = new ArrayList<>();
        Connection conn = Database.getInstance().getConnection();
        String sql = "SELECT * FROM mesas ORDER BY numero";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Mesa m = new Mesa();
                m.setNumero(rs.getInt("numero"));
                m.setCapacidade(rs.getInt("capacidade"));
                m.setOcupada(rs.getBoolean("ocupada"));
                mesas.add(m);
            }
        }
        return mesas;
    }
    
    /**
     * Lista apenas as mesas disponíveis (não ocupadas)
     * @return Lista de mesas disponíveis
     * @throws SQLException em caso de erro no banco de dados
     */
    public static List<Mesa> listarDisponiveis() throws SQLException {
        List<Mesa> mesas = new ArrayList<>();
        Connection conn = Database.getInstance().getConnection();
        String sql = "SELECT * FROM mesas WHERE ocupada = false ORDER BY numero";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Mesa m = new Mesa();
                m.setNumero(rs.getInt("numero"));
                m.setCapacidade(rs.getInt("capacidade"));
                m.setOcupada(false);
                mesas.add(m);
            }
        }
        return mesas;
    }
}
