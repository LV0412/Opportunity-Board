import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeedDemoData {
    public static void main(String[] args) throws Exception {
        Path envPath = Path.of(args.length > 0 ? args[0] : ".env");
        Path sqlPath = Path.of(args.length > 1
                ? args[1]
                : "src/main/resources/db/demo/demo_flow_data.sql");

        Map<String, String> env = readEnv(envPath);
        String url = required(env, "DATABASE_URL");
        String username = required(env, "DATABASE_USERNAME");
        String password = required(env, "DATABASE_PASSWORD");
        String sql = Files.readString(sqlPath);

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                for (String command : splitSqlStatements(sql)) {
                    if (!command.isBlank()) {
                        statement.execute(command);
                    }
                }
                connection.commit();
                printCount(statement, "users", "demo users");
                printCount(statement, "opportunities", "demo opportunities");
                printCount(statement, "applications", "demo applications");
                printCount(statement, "reports", "demo reports");
                printCount(statement, "notifications", "demo notifications");
            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    private static Map<String, String> readEnv(Path path) throws Exception {
        Map<String, String> values = new HashMap<>();
        for (String line : Files.readAllLines(path)) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            int separator = line.indexOf('=');
            if (separator > 0) {
                values.put(line.substring(0, separator).trim(), line.substring(separator + 1));
            }
        }
        return values;
    }

    private static String required(Map<String, String> env, String name) {
        String value = env.get(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing " + name + " in environment file");
        }
        return value;
    }

    private static List<String> splitSqlStatements(String sql) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideString = false;

        for (int index = 0; index < sql.length(); index++) {
            char character = sql.charAt(index);
            if (character == '\'' && insideString && index + 1 < sql.length()
                    && sql.charAt(index + 1) == '\'') {
                current.append(character).append(sql.charAt(++index));
                continue;
            }
            if (character == '\'') {
                insideString = !insideString;
            }
            if (character == ';' && !insideString) {
                if (!current.toString().isBlank()) {
                    statements.add(current.toString());
                }
                current.setLength(0);
            } else {
                current.append(character);
            }
        }
        if (!current.toString().isBlank()) {
            statements.add(current.toString());
        }
        return statements;
    }

    private static void printCount(Statement statement, String table, String label) throws Exception {
        try (ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM " + table
                + " WHERE id::text LIKE 'aaaaaaaa-%'"
                .replace("aaaaaaaa", prefixFor(table)))) {
            result.next();
            System.out.printf("%s: %d%n", label, result.getLong(1));
        }
    }

    private static String prefixFor(String table) {
        return switch (table) {
            case "users" -> "aaaaaaaa";
            case "opportunities" -> "cccccccc";
            case "applications" -> "dddddddd";
            case "reports" -> "ffffffff";
            case "notifications" -> "eeeeeeee";
            default -> throw new IllegalArgumentException("Unsupported table: " + table);
        };
    }
}
