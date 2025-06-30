package model;

import java.sql.*;

/**
 * Classe singleton para gerenciar a conexão com o banco de dados SQLite
 * Responsável por criar as tabelas na primeira execução
 */
public class Database {
    private static final String URL = "jdbc:sqlite:restaurante.db";
    private static Database instance;
    private Connection connection;

    // Construtor privado para garantir singleton
    private Database() {
        try {
            // Registrar driver do SQLite
            Class.forName("org.sqlite.JDBC");
            // Estabelecer conexão
            connection = DriverManager.getConnection(URL);
            // Criar tabelas se não existirem
            criarTabelas();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Erro ao conectar ao banco de dados: " + e.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para obter a instância única do banco de dados
     */
    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     * Retorna a conexão ativa com o banco de dados
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Cria todas as tabelas necessárias no banco de dados
     */
    private void criarTabelas() throws SQLException {
        // SQL para criação das tabelas
        String[] tabelas = {
            // Tabela de clientes
            "CREATE TABLE IF NOT EXISTS clientes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL," +
                "telefone TEXT NOT NULL," +
                "desconto REAL DEFAULT 0)",
                
            // Tabela de mesas
            "CREATE TABLE IF NOT EXISTS mesas (" +
                "numero INTEGER PRIMARY KEY," +
                "capacidade INTEGER NOT NULL," +
                "ocupada BOOLEAN NOT NULL," +
                "vip BOOLEAN NOT NULL," +
                "vista_exclusiva BOOLEAN)",
                
            // Tabela de reservas
            "CREATE TABLE IF NOT EXISTS reservas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "cliente_id INTEGER NOT NULL," +
                "mesa_numero INTEGER NOT NULL," +
                "horario TEXT NOT NULL," +
                "FOREIGN KEY(cliente_id) REFERENCES clientes(id)," +
                "FOREIGN KEY(mesa_numero) REFERENCES mesas(numero))",
                
            // Tabela de produtos (cardápio)
            "CREATE TABLE IF NOT EXISTS produtos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL," +
                "preco REAL NOT NULL," +
                "descricao TEXT)",
                
            // Tabela de pedidos
            "CREATE TABLE IF NOT EXISTS pedidos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "mesa_numero INTEGER NOT NULL," +
                "data TEXT NOT NULL," +
                "status TEXT NOT NULL," +
                "FOREIGN KEY(mesa_numero) REFERENCES mesas(numero))",
                
            // Tabela de itens dos pedidos
            "CREATE TABLE IF NOT EXISTS itens_pedido (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "pedido_id INTEGER NOT NULL," +
                "produto_id INTEGER NOT NULL," +
                "quantidade INTEGER NOT NULL," +
                "FOREIGN KEY(pedido_id) REFERENCES pedidos(id)," +
                "FOREIGN KEY(produto_id) REFERENCES produtos(id))"
        };

        // Executar cada comando SQL
        try (Statement stmt = connection.createStatement()) {
            for (String sql : tabelas) {
                stmt.execute(sql);
            }
        }
    }
    
    /**
     * Fecha a conexão com o banco de dados
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
