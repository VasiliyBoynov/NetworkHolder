package DB;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.concurrent.Semaphore;

public class DB {
    private static Connection connection;
    private static Semaphore smp = new Semaphore(1);
    private static final String SECRET_SALT = "DOP_COD_SALT";

    static {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:Server/src/main/java/DB/myDB.db");
        } catch (SQLException troubles) {
            troubles.printStackTrace();
        }
    }

    public MessageDB checkUser(String nickName, String password){
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQLCommand.SQLSelectNickId.getCommand());
            preparedStatement.setString(1,nickName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                if (resultSet.getString("Password").equals(getPassword(password))) {
                    return new MessageDB(true,"successfully");
                } else {
                    return new MessageDB(false,"don't correct password");
                }
            } else {
                return new MessageDB(false,"User with this nickname does not exist");
            }
        } catch (SQLException throwables) {
            return new MessageDB(false,"Don't connect to database");
        }
    }

    public MessageDB addUser(String nickName, String password)  {
        try {
            smp.acquire();
        } catch (InterruptedException e) {
            smp.release();
            e.printStackTrace();
        }
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQLCommand.SQLInsert.getCommand());

            try {
                preparedStatement.setString(1,nickName);
                preparedStatement.setString(2,getPassword(password));
                preparedStatement.executeUpdate();
            } catch (SQLException throwables) {
                smp.release();
                return new MessageDB(false,String.format("The User with nickName \"%s\" already exists",nickName));
            }
        } catch (SQLException throwables) {
            smp.release();
            return new MessageDB(false,"Don't connect to database");
        }
        smp.release();
        return new MessageDB(true,"successfully");
    }

    private String getPassword(String plainPassword){
        String hashedPassword = null;
        String passwordWithSalt = plainPassword + SECRET_SALT;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            hashedPassword = bytesToHex(md.digest(passwordWithSalt.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedPassword;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }



}
