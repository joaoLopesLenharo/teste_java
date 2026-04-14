package com.teste.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Conexão SQLite e criação de schema. A URL JDBC pode ser alterada para testes (ex.: memória).
 */
public final class DatabaseUtil {

    private static volatile String jdbcUrl = "jdbc:sqlite:funcionarios.db";

    private DatabaseUtil() {
    }

    /**
     * Define a URL JDBC (útil para testes com {@code jdbc:sqlite::memory:}).
     */
    public static void configurarUrl(String url) {
        jdbcUrl = url;
    }

    public static String getJdbcUrl() {
        return jdbcUrl;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }

    /**
     * Inicializa o banco criando a tabela se necessário.
     */
    public static void inicializarBanco() {
        String sql = """
                CREATE TABLE IF NOT EXISTS funcionarios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    data_nascimento TEXT NOT NULL,
                    salario REAL NOT NULL,
                    funcao TEXT NOT NULL
                )
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✓ Banco de dados inicializado com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
