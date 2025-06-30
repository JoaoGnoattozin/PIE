package model;

import java.sql.*;

/**
 * Classe que representa um cliente do restaurante
 * Contém métodos para persistência no banco de dados
 */
public class Cliente {
    private int id;
    private String nome;
    private String telefone;

    // Construtores
    public Cliente() {}

    public Cliente(String nome, String telefone) {
        this.nome = nome;
        this.telefone = telefone;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { 
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        this.nome = nome; 
    }
    
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { 
        if (telefone == null || !telefone.matches("\\d{11}")) {
            throw new IllegalArgumentException("Telefone deve ter 11 dígitos");
        }
        this.telefone = telefone; 
    }

    /**
     * Salva o cliente no banco de dados
     * @throws SQLException em caso de erro no banco de dados
     */
    public void salvar() throws SQLException {
        Connection conn = Database.getInstance().getConnection();
        String sql = "INSERT INTO clientes (nome, telefone) VALUES (?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nome);
            stmt.setString(2, telefone);
            stmt.executeUpdate();
            
            // Obter o ID gerado
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.id = rs.getInt(1);
                }
            }
        }
    }
    
    /**
     * Busca um cliente pelo ID
     * @param id ID do cliente a ser buscado
     * @return Cliente encontrado ou null se não existir
     * @throws SQLException em caso de erro no banco de dados
     */
    public static Cliente buscarPorId(int id) throws SQLException {
        Connection conn = Database.getInstance().getConnection();
        String sql = "SELECT * FROM clientes WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente c = new Cliente();
                    c.setId(rs.getInt("id"));
                    c.setNome(rs.getString("nome"));
                    c.setTelefone(rs.getString("telefone"));
                    return c;
                }
            }
        }
        return null;
    }
    
    /**
     * Lista todos os clientes cadastrados
     * @return Lista de clientes
     * @throws SQLException em caso de erro no banco de dados
     */
    public static List<Cliente> listarTodos() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        Connection conn = Database.getInstance().getConnection();
        String sql = "SELECT * FROM clientes ORDER BY nome";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id"));
                c.setNome(rs.getString("nome"));
                c.setTelefone(rs.getString("telefone"));
                clientes.add(c);
            }
        }
        return clientes;
    }
}
