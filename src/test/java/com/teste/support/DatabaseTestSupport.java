package com.teste.support;

import com.teste.util.DatabaseUtil;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configura SQLite isolado por teste (arquivo temporário).
 */
public final class DatabaseTestSupport {

    private DatabaseTestSupport() {
    }

    public static void resetWithTempFile() throws Exception {
        Path p = Files.createTempFile("rh-test-", ".db");
        p.toFile().deleteOnExit();
        DatabaseUtil.configurarUrl("jdbc:sqlite:" + p.toAbsolutePath().toString().replace('\\', '/'));
        DatabaseUtil.inicializarBanco();
    }
}
