import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Main {
    private static Connection connection;
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/";
    private  static final String DATABASE_NAME = "minions_db";

    public static void main(String[] args) throws IOException, SQLException {
        connection = getConnection();

        while (true) {
            System.out.println("Please, enter exercise number (2-9):");

            int number = Integer.parseInt(reader.readLine());

            switch (number) {
                case 2 -> exerciseTwo();
                case 3 -> exerciseThree();
                case 4 -> exerciseFour();
                case 5 -> exerciseFive();
                case 6 -> exerciseSix();
                case 7 -> exerciseSeven();
                case 8 -> exerciseEight();
                case 9 -> exerciseNine();
                default -> System.out.println("Invalid exercise number! You should try again!");
            }
            System.out.println();
            char ch = ' ';
            while (ch != 'Y') {
                System.out.println("Would you like to continue (Y/N)?");
                ch = reader.readLine().toUpperCase().charAt(0);
                if (ch == 'N') {
                    System.out.println("GOODBYE! =^.^=");
                    break;
                }
            }
            if (ch == 'N') {
                break;
            }
        }
    }

    private static void exerciseNine() throws IOException, SQLException {
        System.out.println("Enter minion id:");
        int id = Integer.parseInt(reader.readLine());
        CallableStatement statement = connection.prepareCall("CALL usp_get_older(?);");
        statement.setInt(1, id);
        statement.executeUpdate();
        printMinionNameAndAge(id);
    }

    private static void printMinionNameAndAge(int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT name, age FROM minions " +
                "WHERE id = ?;");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        System.out.printf("%s %d\n", resultSet.getString("name"), resultSet.getInt("age"));
    }

    private static void exerciseEight() throws IOException, SQLException {
        System.out.println("Input minions' ids:");
        int[] idsArr = Arrays.stream(reader.readLine().split("\\s+"))
                .mapToInt(Integer::parseInt)
                .toArray();
        Arrays.stream(idsArr).forEach(id -> {
            try {
                updateMinionNameAndAge(id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });;

        PreparedStatement statement = connection.prepareStatement("SELECT id, name, age FROM minions;");
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            System.out.printf("%d %s %d\n", resultSet.getInt("id"), resultSet.getString("name"), resultSet.getInt("age"));
        }
    }

    private static void updateMinionNameAndAge(int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE minions\n" +
                "SET age = age + 1, name = lower(name)\n" +
                "WHERE id = ?;");
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    private static void exerciseSeven() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM minions;");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<String> resultList = new ArrayList<>();
        while (resultSet.next()) {
            resultList.add(resultSet.getString("name"));
        }
        printFromStartAndEnd(resultList, 0, resultList.size() - 1, resultList.size() / 2);
    }

    private static void printFromStartAndEnd(List<String> resultList, int start, int end, int mid) {
        if (start == mid || start == end) {
            if (resultList.size() % 2 != 0) {
                System.out.println(resultList.get(mid));
            }
            return;
        }
        System.out.println(resultList.get(start));
        System.out.println(resultList.get(end));
        printFromStartAndEnd(resultList, start + 1, end - 1, mid);
    }

    private static void exerciseSix() throws IOException, SQLException {
        System.out.println("Enter villain's id:");
        int id = Integer.parseInt(reader.readLine());
        String villainName = getEntityById(id, "villains");
        if (villainName == null) {
            System.out.println("No such villain was found");
            return;
        }
        int minionsReleased = removeMinionsByVillainId(id);
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM villains WHERE id = ?;");
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.printf("%s was deleted\n", villainName);
        System.out.printf("%d minions released\n", minionsReleased);
    }

    private static int removeMinionsByVillainId(int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM minions_villains " +
                "WHERE villain_id = ?;");
        preparedStatement.setInt(1, id);

        return preparedStatement.executeUpdate();
    }

    private static void exerciseFive() throws IOException, SQLException {
        System.out.println("Input country name:");
        String countryName = reader.readLine();
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE towns \n" +
                "SET name = upper(name)\n" +
                "WHERE country = ?;");
        preparedStatement.setString(1, countryName);

        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected == 0) {
            System.out.println("No town names were affected.");
            return;
        }
        String[] townsArr = new String[rowsAffected];
        preparedStatement = connection.prepareStatement("SELECT name FROM towns WHERE country = ?;");
        preparedStatement.setString(1, countryName);

        ResultSet rs = preparedStatement.executeQuery();
        int index = -1;
        while (rs.next()) {
            townsArr[++index] = rs.getString("name");
        }
        System.out.printf("%d towns were affected.\n", rowsAffected);
        System.out.println(Arrays.toString(townsArr));
    }

    private static void exerciseFour() throws IOException, SQLException {
        System.out.println("Please, enter data for villain and minion:");

        String[] minionTokens = reader.readLine().split("\\s+");
        String[] villainTokens = reader.readLine().split("\\s+");

        String minionName = minionTokens[1];
        int minionAge = Integer.parseInt(minionTokens[2]);
        String minionTown = minionTokens[3];

        String villainName = villainTokens[1];

        addToDatabase("villains", new String[]{"name", "evilness_factor"}, new String[]{villainName, "evil"});
        addToDatabase("towns", new String[]{"name"}, new String[]{minionTown});

        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO minions(name, age, town_id)\n" +
                "SELECT ?, ?, (SELECT id from towns WHERE name = ?);");
        preparedStatement.setString(1, minionName);
        preparedStatement.setInt(2, minionAge);
        preparedStatement.setString(3, minionTown);
        preparedStatement.executeUpdate();
        int minionId = getIntByName(minionName, "minions");
        int villainId = getIntByName(villainName, "villains");
        PreparedStatement statement = connection.prepareStatement("INSERT INTO minions_villains(minion_id, villain_id)\n" +
                "VALUES (?, ?);");
        statement.setInt(1, minionId);
        statement.setInt(2, villainId);
        statement.executeUpdate();
        System.out.printf("Successfully added %s to be minion of %s\n", minionName, villainName);
    }

    private static void addToDatabase(String tableName, String[] columns, String[] values) throws SQLException {
        String sql = String.format("SELECT id FROM %s WHERE name = ?;", tableName);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, values[0]);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()) {
            StringBuilder valBld = new StringBuilder();
                int index = -1;
                while (index < values.length - 1) {
                    valBld.append("'").append(values[++index]).append("'");
                    if (index < values.length - 1) {
                        valBld.append(", ");
                    }
                }

            String newSql = String.format("INSERT INTO %s(%s) VALUES (%s);", tableName,
                    String.join(", ", columns),
                    valBld.toString());
            preparedStatement = connection.prepareStatement(newSql);
            preparedStatement.executeUpdate();
            String name = tableName.substring(0, 1).toUpperCase() + tableName.substring(1, tableName.length() - 1);
            System.out.printf("%s %s was added to the database.\n", name, values[0]);
        }
    }

    private static void exerciseThree() throws IOException, SQLException {
        System.out.println("Please, enter villain's id:");
        int id = Integer.parseInt(reader.readLine());
        String villainName = getEntityById(id, "villains");
        if (villainName == null) {
            System.out.printf("No villain with ID %d exists in the database.\n", id);
            return;
        }
        System.out.printf("Villain: %s\n", villainName);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT m.name, m.age\n" +
                "FROM minions AS m\n" +
                "JOIN minions_villains mv on m.id = mv.minion_id\n" +
                "WHERE mv.villain_id = ?;");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        int minionNumber = 0;
        while (resultSet.next()) {
            System.out.printf("%d. %s %d\n", ++minionNumber, resultSet.getString("name"), resultSet.getInt("age"));
        }
    }

    private static String getEntityById(int id, String tableName) throws SQLException {
        String sqlQuery = String.format("SELECT name FROM %s WHERE id = %d;", tableName, id);
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("name");
        }
        return null;
    }

    private static int getIntByName(String name, String tableName) throws SQLException {
        String sql = String.format("SELECT id FROM %s WHERE name = ?;", tableName);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        ResultSet rs = preparedStatement.executeQuery();
        rs.next();
        return rs.getInt("id");
    }

    private static void exerciseTwo() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT v.name, COUNT(DISTINCT mv.minion_id) AS 'minions_count'\n" +
                "FROM villains AS v\n" +
                "JOIN minions_villains mv on v.id = mv.villain_id\n" +
                "GROUP BY villain_id\n" +
                "HAVING `minions_count` > ?;");
        preparedStatement.setInt(1, 15);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            System.out.printf("%s %d\n", resultSet.getString("name"), resultSet.getInt("minions_count"));
        }

    }


    private static Connection getConnection() throws IOException, SQLException {
        System.out.println("Enter user:");
        String user = reader.readLine();
        System.out.println("Enter password:");
        String password = reader.readLine();
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);

        return DriverManager.getConnection(CONNECTION_STRING + DATABASE_NAME, properties);
    }
}
