package Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


import Commands.Message;
import Commands.NewUser;
import Commands.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Controller implements AutoCloseable{

    public static final Controller instance = new Controller();

    private AtomicBoolean authorization = new AtomicBoolean();
    private AtomicBoolean startAuthorisation = new AtomicBoolean();
    private AtomicBoolean finishAuthorisation = new AtomicBoolean();
    private Channel ch;
    private String txt;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public final int MAX_PACKAGE_BYTE=60000;

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public AtomicBoolean getAuthorization() {
        return authorization;
    }

    public AtomicBoolean getStartAuthorisation() {
        return startAuthorisation;
    }

    public AtomicBoolean getFinishAuthorisation() {
        return finishAuthorisation;
    }

    private  BufferedReader reader;

    private final static byte lengthPassword = 3;


    public void setCh(Channel ch) {
        this.ch = ch;
    }

    public void setAuthorization(boolean authorization) {
        this.authorization.set(authorization);
    }

    public void setStartAuthorisation(boolean startAuthorisation) {
        this.startAuthorisation.set(startAuthorisation);
    }

    public void setFinishAuthorisation(boolean finishAuthorisation) {
        this.finishAuthorisation.set(finishAuthorisation);
    }


    public void work() throws Exception {

        reader = new BufferedReader(new InputStreamReader(System.in));
        FileMenager fm = new FileMenager();
        String commandLine;
        while (true) {
            System.out.print(fm.getTmpPath()+"> ");
            commandLine = reader.readLine();

            if (commandLine.equals("exit")) {
                this.close();
                break;}
            if (commandLine.equals("newUser")){

                autorization(new NewUser());
            }
            if (commandLine.equals("user")){

                autorization(new User());
            }
            if (commandLine.equals("send") ){

                if (getAuthorization().get()) {
                    sendFile(fm.getTmpPath());
                } else {
                    System.out.println("Вы не прошли процедуру авторизации.\nДля старта процедуры введите командное слово \"user\" или \"newUser\" ");
                }
            }
            if (commandLine.startsWith("ls")){
                if (commandLine.equals("ls") | commandLine.split(" ").length==1){
                    fm.ls();
                } else {
                    Path path = Paths.get(commandLine.split(" ")[1]);
                    try {
                        fm.ls(path);
                    } catch (NullPointerException e) {
                        System.out.println("Папка отсутствует :" + commandLine.split(" ")[1]);
                    }
                }
            }

            if (commandLine.trim().split(" ")[0].equals("cd") & commandLine.trim().split(" ").length>1) {

                fm.cd(Paths.get(commandLine.trim().split(" ")[1]));
            } else {
                if (commandLine.trim().split(" ")[0].equals("cd")) {
                    fm.cd(); }
            }






            //ch.writeAndFlush(commandLine.getBytes(StandardCharsets.UTF_8));

        }
    };

    public void autorization(NewUser msg) throws IOException, InterruptedException {

        setAuthorization(false);
        setStartAuthorisation(false);
        setFinishAuthorisation(false);

        String name = new String();
        while (true){
            System.out.print("Введите \"Имя пользователя\":");
            name = reader.readLine();
            if (!name.equals("") & name.indexOf(" ")==-1) break;
            System.out.printf("%s%n",(name.equals(""))?
                    "Поле \"Имя пользователя\" не может быть пустым!":
                    "Поле \"Имя пользователя\" не должно содержать пробелы!");
            System.out.print("Введите \"Имя пользователя\":");
        }

        String password = new String();

        while (true){
            System.out.print("Пароль:");
            password = reader.readLine();
            boolean hasNumber = false;
            boolean hasLowerCase = false;
            boolean hasUpperCase = false;
            boolean hasNotGap = (password.indexOf(" ") == -1);
            if (!password.equals("")){
                for (int i = 0; i < password.length(); i++) {
                    if (!hasNumber) {
                        hasNumber = Pattern.matches("\\p{N}", String.valueOf(password.charAt(i)));
                    }
                    if (!hasLowerCase){
                        hasLowerCase = Pattern.matches("\\p{Ll}",String.valueOf(password.charAt(i)));
                    }
                    if (!hasUpperCase){
                        hasUpperCase = Pattern.matches("\\p{Lu}",String.valueOf(password.charAt(i)));
                    }
                }

                if (password.length()>lengthPassword-1
                        & hasNumber
                        & hasLowerCase
                        & hasUpperCase
                        & hasNotGap){break;}

                if (password.length()<lengthPassword) System.out.printf("Длительность пароля должна быть больше %d символов%n",lengthPassword);
                if (!hasNumber) System.out.println("В пароле должен быть как минимум 1 цифра");
                if (!hasLowerCase) System.out.println("В пароле должен быть символ в нижнем регистре");
                if (!hasUpperCase) System.out.println("В пароле должен быть символ в верхнем регистре");
                if (!hasNotGap) System.out.println("В пароле не должно быть разделительных символов");
            }

        }

        msg.setNickName(name);
        msg.setPassword(password);
        ch.writeAndFlush(msg).sync();
        startAuthorisation.set(true);

        System.out.println("Wait answer from Server");

        executor.submit(()->{
            String rezl;
            while (!getFinishAuthorisation().get()){}
            rezl = (getAuthorization().get())? "Authorization was successful":"Authorization was unsuccessful\n"+txt;
            System.out.println(rezl);

        });



    }

    public void sendFile(Path tmpPath) throws IOException {

        System.out.println("Для выхода из режима отпрравки файла на сервер наберите \"exit\"");
        String str;

        while (true){
            System.out.println("Введите путь к файлу, который хотите синхронизовать с облачным хранилищем");
            str = reader.readLine();
            if (str.equals("exit")) break;


            Path pathIn = (Paths.get(str).isAbsolute()) ? Paths.get(str):Paths.get(tmpPath.toString()+"\\"+str);
            if (pathIn.toFile().isFile()){
                System.out.println("Start send File");


                //SendFile sendFile = new SendFile(pathIn);
               // ch.writeAndFlush(sendFile.objToByte());
            } else {
                System.out.println("uncorrected path to file");
            }






        }
    }





    @Override
    public void close() throws Exception {
        if (reader!=null){reader.close();}
        ch.flush();
        ch.close();
        executor.shutdownNow();

    }

}