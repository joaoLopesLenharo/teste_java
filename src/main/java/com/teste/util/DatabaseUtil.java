package com.teste.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe utilitária para gerenciar a conexão com o banco de dados SQLite.
 * Requisito 4 - Uso de SQLite para armazenamento e persistência.
 */
public class DatabaseUtil {

    private static final String URL = "jdbc:sqlite:funcionarios.db";

    /**
     * Obtém uma conexão com o banco de dados SQLite.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Inicializa o banco de dados criando a tabela de funcionários caso não exista.
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
