package com.teste.dao;

import com.teste.model.Funcionario;
import com.teste.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para operações CRUD de Funcionário no SQLite.
 * Requisito 4 - Persistência com SQLite.
 */
public class FuncionarioDAO {

    private static final DateTimeFormatter DB_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Insere um novo funcionário no banco de dados.
     */
    public void inserir(Funcionario funcionario) {
        String sql = "INSERT INTO funcionarios (nome, data_nascimento, salario, funcao) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, funcionario.getNome());
            pstmt.setString(2, funcionario.getDataNascimento().format(DB_FORMATTER));
            pstmt.setDouble(3, funcionario.getSalario().doubleValue());
            pstmt.setString(4, funcionario.getFuncao());

            pstmt.executeUpdate();

            // Recupera o ID gerado
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    funcionario.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inserir funcionário: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Atualiza um funcionário existente no banco de dados.
     */
    public void atualizar(Funcionario funcionario) {
        String sql = "UPDATE funcionarios SET nome = ?, data_nascimento = ?, salario = ?, funcao = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, funcionario.getNome());
            pstmt.setString(2, funcionario.getDataNascimento().format(DB_FORMATTER));
            pstmt.setDouble(3, funcionario.getSalario().doubleValue());
            pstmt.setString(4, funcionario.getFuncao());
            pstmt.setInt(5, funcionario.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar funcionário: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Remove um funcionário do banco de dados pelo ID.
     */
    public void remover(int id) {
        String sql = "DELETE FROM funcionarios WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao remover funcionário: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Remove um funcionário do banco de dados pelo nome.
     */
    public void removerPorNome(String nome) {
        String sql = "DELETE FROM funcionarios WHERE nome = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao remover funcionário: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lista todos os funcionários do banco de dados.
     */
    public List<Funcionario> listarTodos() {
        List<Funcionario> funcionarios = new ArrayList<>();
        String sql = "SELECT * FROM funcionarios ORDER BY id";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                funcionarios.add(mapearFuncionario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar funcionários: " + e.getMessage());
            e.printStackTrace();
        }

        return funcionarios;
    }

    /**
     * Busca um funcionário pelo ID.
     */
    public Funcionario buscarPorId(int id) {
        String sql = "SELECT * FROM funcionarios WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearFuncionario(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar funcionário: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Verifica se existem funcionários cadastrados no banco.
     */
    public boolean existemRegistros() {
        String sql = "SELECT COUNT(*) FROM funcionarios";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar registros: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Remove todos os funcionários do banco de dados.
     */
    public void removerTodos() {
        String sql = "DELETE FROM funcionarios";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            System.err.println("Erro ao remover todos os funcionários: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Atualiza o salário de todos os funcionários com um percentual de aumento.
     */
    public void aplicarAumento(BigDecimal percentual) {
        String sql = "UPDATE funcionarios SET salario = salario * (1 + ? / 100)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, percentual.doubleValue());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao aplicar aumento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Mapeia um ResultSet para um objeto Funcionario.
     */
    private Funcionario mapearFuncionario(ResultSet rs) throws SQLException {
        return new Funcionario(
                rs.getInt("id"),
                rs.getString("nome"),
                LocalDate.parse(rs.getString("data_nascimento"), DB_FORMATTER),
                BigDecimal.valueOf(rs.getDouble("salario")),
                rs.getString("funcao")
        );
    }
}
