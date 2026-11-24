
package com.bankguard.database;

// ============================================================
// IMPORTS
// ============================================================

// I import these classes so I can read my SQL files.
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SqlLoader {

    // ========================================================
    // 1. LOADING MY SQL FILES
    // ========================================================

    public static String loadSql(String filePath)
            throws IOException {

        // ========== DECLARATIONS ==========

        InputStream sqlFile;
        String sqlContent;


        // ========== INITIALISATION ==========

        // I locate my SQL file inside the resources folder.
        sqlFile = SqlLoader.class.getResourceAsStream(filePath);


        // ========== VALIDATION ==========

        // I check whether the file exists before reading it.
        if (sqlFile == null) {

            throw new IOException(
                    "SQL file not found: " + filePath);
        }


        // ========== PROCESSING ==========

        // I read the SQL instructions stored in the file.
        try (InputStream file = sqlFile) {

            sqlContent = new String(
                    file.readAllBytes(),
                    StandardCharsets.UTF_8);
        }


        // ========== OUTPUT ==========

        // I return the SQL instructions for Java to execute.
        return sqlContent;
    }
}
