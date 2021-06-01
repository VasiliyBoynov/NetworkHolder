package DB;

import Commands.Info;
import Commands.SetFile;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
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

    public MessageDB updateStatusDel(int idUser,String path_client){
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQLCommand.SQLSelectFileName.getCommand());
            preparedStatement.setInt(1,idUser);
            preparedStatement.setString(2,path_client);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                String str = resultSet.getString("name_file_server");
                int idFile = resultSet.getInt("id_files");
                preparedStatement = connection.prepareStatement(SQLCommand.SQLUpdateStatusDelete.getCommand());
                preparedStatement.setBoolean(1,true);
                preparedStatement.setInt(2,idFile);
                preparedStatement.executeUpdate();
                return new MessageDB(true,str);
            }
            else return new MessageDB(false,"File don't find ");
        } catch (SQLException throwables){
            return new MessageDB(false,"DB isn't available");
        }
    }

    public void updateSizeArray(String nickName){
        PreparedStatement preparedStatement = null;

        int id_user=0;
        long size=0;
        try {

            preparedStatement = connection.prepareStatement(SQLCommand.SQLFindSizeArray.getCommand());
            preparedStatement.setString(1,nickName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                size+=resultSet.getInt("size");
            }
            preparedStatement = connection.prepareStatement(SQLCommand.SQLUpdateSizeArray.getCommand());
            preparedStatement.setLong(1,size);
            preparedStatement.setString(2,nickName);
            preparedStatement.executeUpdate();




        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    };

    public void updateStatus(int idUser,String name_file_server, boolean status,boolean del){
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQLCommand.SQLUpdateStatus.getCommand());

            preparedStatement.setBoolean(1,status);
            preparedStatement.setBoolean(2,del);
            preparedStatement.setString(3,name_file_server);
            preparedStatement.setInt(4,idUser);
            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

    public MessageDB getFile(int idUser, String path_client ){
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQLCommand.SQLSelectFileName.getCommand());
            preparedStatement.setInt(1,idUser);
            preparedStatement.setString(2,path_client);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return new MessageDB(true,resultSet.getString("name_file_server"));
            } else {
                return new MessageDB(false,"Dont find file");
            }


        } catch (SQLException throwables) {
            return new MessageDB(false,"DB isn't available");
        }
    }

    public MessageDB setFile(String nickName, SetFile setFile){
        PreparedStatement preparedStatement = null;
        int id_user=0;
        int maxSizeGB=0;
        long size=0;
        long maxSizeB = 0;
        try {
            preparedStatement = connection.prepareStatement(SQLCommand.SQLSelectNickId.getCommand());
            preparedStatement.setString(1,nickName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                id_user=resultSet.getInt("id_user") ;
                maxSizeGB=resultSet.getInt("MaxSizeArrayGB");
                maxSizeB = (long) maxSizeGB*1024*1024*1024;
                size=resultSet.getLong("SizeArray");
            } else {
                return new MessageDB(false,"User with this nickname does not exist");
            }
        } catch (SQLException throwables) {
            return new MessageDB(false,"throwables with DB");
        }



        try{
            preparedStatement = connection.prepareStatement(SQLCommand.SQLSelectFileName.getCommand());
            preparedStatement.setInt(1,id_user);
            preparedStatement.setString(2,setFile.getPath());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                //System.out.println(resultSet.getString("name_file_server"));
                //resultSet.getString("name_file_server");
                if (resultSet.getBoolean("del")){
                    if (maxSizeB<(setFile.getSizeFile()+size))
                        return new MessageDB(false,"Data storage limit has been exceeded, please contact technical customer service to increase the data storage limit.");

                } else {
                    if (maxSizeB<(setFile.getSizeFile()+size-resultSet.getLong("size")))
                        return new MessageDB(false,"Data storage limit has been exceeded, please contact technical customer service to increase the data storage limit.");

                }
                preparedStatement = connection.prepareStatement(SQLCommand.SQLUpdateFile.getCommand());
                preparedStatement.setString(1,String.valueOf(setFile.getLastModified()));
                preparedStatement.setBoolean(2,false);
                preparedStatement.setBoolean(3,false);
                preparedStatement.setLong(4,setFile.getSizeFile());
                preparedStatement.executeUpdate();
                return new MessageDB(true, resultSet.getString("name_file_server"));
            }
            else {
                long sizeNewFile = setFile.getSizeFile();

                if (maxSizeB<(setFile.getSizeFile()+size))
                    return new MessageDB(false,"Data storage limit has been exceeded, please contact technical customer service to increase the data storage limit.");

                preparedStatement = connection.prepareStatement(SQLCommand.SQLInsertFile.getCommand());
                preparedStatement.setInt(1,id_user);
                preparedStatement.setString(2,setFile.getPath());
                preparedStatement.setString(3,getFileNameHash(nickName,setFile.getPath()));
                preparedStatement.setString(4,String.valueOf(setFile.getLastModified()));
                preparedStatement.setLong(5,setFile.getSizeFile());
                preparedStatement.executeUpdate();
                return new MessageDB(true,getFileNameHash(nickName,setFile.getPath()));
            }

        } catch (SQLException throwables) {
        }

        return new MessageDB(false,"throwables with DB");}

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

    public MessageDB infoUser(String nickName){
        PreparedStatement preparedStatement = null;
        int idUser=0;
        try {
            preparedStatement = connection.prepareStatement(SQLCommand.SQLSelectNickId.getCommand());
            preparedStatement.setString(1,nickName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                idUser=resultSet.getInt("id_user") ;
                return new MessageDB(true,String.valueOf(idUser));
            } else {
                return new MessageDB(false,"User with this nickname does not exist");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return new MessageDB(false,"Trebles with DB ");
        }

    }

    public MessageDB listFile(int idUser){
        PreparedStatement preparedStatement = null;
        ArrayList<Info> list = new ArrayList<Info>();
        try {
            preparedStatement = connection.prepareStatement(SQLCommand.SQLSelectFileInfo.getCommand());
            preparedStatement.setInt(1,idUser);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Info info = new Info();
                info.setPath(resultSet.getString("path_client"));
                info.setLastModifyFile(resultSet.getString("lastTimeModif"));
                info.setSize(resultSet.getLong("size"));
                list.add(info);
            }
            MessageDB messageDB = new MessageDB(true,"");
            messageDB.setList(list);
            return messageDB;

        } catch (SQLException throwables) {
            list = null;
            return new MessageDB(false,"Trebles with DB");
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

    private String getFileNameHash(String nickName, String fileName){
        String str = nickName + fileName;
        String fileNameHash = null;
        try {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        fileNameHash = bytesToHex(md.digest(str.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return fileNameHash;
    }



}
