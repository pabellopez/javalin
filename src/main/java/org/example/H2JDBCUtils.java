package org.example;

import java.sql.*;

public class H2JDBCUtils {
    private static String jdbcURL = "jdbc:h2:mem:test";
//    private static String jdbcURL = "jdbc:h2:~/test";
    private static String jdbcUsername = "sa";
    private static String jdbcPassword = "";

    static Connection connection = null;

    static void getConnection() {
        try {
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static void init() {
        getConnection();
        try {
            String sql = "";
            Statement statement = connection.createStatement();
            sql = "create table usuario(" +
                    "  id  int primary key auto_increment," +
                    "  username varchar(20)," +
                    "  nombre varchar(20)," +
                    "  password varchar(20)," +
                    "  administrator boolean," +
                    "  autor boolean" +
                    "  );";

            sql+= "create table articulo(" +
                    "  id  int primary key auto_increment," +
                    "  titulo varchar(50)," +
                    "  cuerpo varchar(5000)," +
                    "  autor int," +
                    "  fecha date" +
                    "  );";

            sql+= "create table etiqueta(" +
                    "  id  int primary key auto_increment," +
                    "  etiqueta varchar(50)" +
                    "  );";



            sql += "create table comentario(" +
                    "  id  int primary key auto_increment," +
                    "  username varchar(5000)," +
                    "  autor int," +
                    "  articulo int" +
                    "  );";
            statement.execute(sql);
            insertRecord();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static void insertRecord() throws SQLException {

        String sql = "INSERT INTO usuario" +
                "  (username, nombre, password, administrator,autor) VALUES " +
                " (?, ?, ?, ?, ?);";
        try {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "admin");
            preparedStatement.setString(2, "ADMINISTRADOR");
            preparedStatement.setString(3, "admin");
            preparedStatement.setBoolean(4, true);
            preparedStatement.setBoolean(5, false);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public static ResultSet getQuery(String sql) {
        ResultSet rs = null;
        try {
            // Step 2:Create a statement using connection object
            Statement statement = connection.createStatement();
            rs = statement.executeQuery(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

    public static void execute(String sql) {
        try {
            // Step 2:Create a statement using connection object
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
