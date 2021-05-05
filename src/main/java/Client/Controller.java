package Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import Client.Commands.Authorization;
import Client.Commands.Commands;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Controller implements AutoCloseable{

    public static final Controller instance = new Controller();

    private AtomicBoolean authorization = new AtomicBoolean();
    private AtomicBoolean startAuthorisation = new AtomicBoolean();
    private AtomicBoolean finishAuthorisation = new AtomicBoolean();
    private static Channel ch;

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
        String commandLine;
        while (true) {
            commandLine = reader.readLine();

            if (commandLine.equals("exit")) {
                this.close();
                break;}


            if (commandLine.equals("newUser")){
                commandLine=null;
                autorization(Commands.newUser);
            }

            if (commandLine.equals("user")){
                commandLine=null;
                autorization(Commands.user);
            }

            if (commandLine.equals("send")){
                commandLine=null;
                sendFile();
            }






            //ch.writeAndFlush(commandLine.getBytes(StandardCharsets.UTF_8));

        }
    };

    public void autorization(Commands instr) throws IOException, InterruptedException {

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

        Authorization command = new Authorization(instr);
        command.setNickName(name);
        command.setPassword(password);


        ch.writeAndFlush(command.objToByte()).sync();
        startAuthorisation.set(true);

        System.out.println("Wait answer from Server");

        while (!getFinishAuthorisation().get()){}

        String rezl = (getAuthorization().get())? "Authorization was successful":"Authorization was unsuccessful";
        System.out.println(rezl);

    }

    public void sendFile() throws IOException {
        System.out.println("Для выхода из режима отпрравки файла на сервер наберите \"exit\"");
        String str;
        Path startPath = Paths.get("");
        Path tmpPath = startPath.toAbsolutePath();
        while (true){
            System.out.println("Введите путь к файлу, который хотите синхронизовать с облачным хранилищем");
            str = reader.readLine();
            if (str.equals("exit")) break;
            if (str.startsWith("ls")){
                if (str.equals("ls") | str.split(" ").length==1){
                    ls(tmpPath);
                } else {
                    Path path = Paths.get(str.split(" ")[1]);
                    try {
                        ls(path);
                    } catch (NullPointerException e) {
                        System.out.println("Папка отсутствует :" + str.split(" ")[1]);
                    }

                }


            }
        }
    }

    private void ls(Path path){
        Path pathIn = (path.isAbsolute()) ? path:path.toAbsolutePath();
        System.out.println(pathIn);
        for (File f : Arrays.stream(pathIn.toFile().listFiles())
                .filter(file -> file.isDirectory())
                .sorted()
                .collect(Collectors.toList())) {
            System.out.println(f.getName());
        }
        for (File f : Arrays.stream(pathIn.toFile().listFiles())
                .filter(file -> file.isFile())
                .sorted()
                .collect(Collectors.toList())) {
            System.out.println(f.getName());
        }
    }

    @Override
    public void close() throws Exception {
        if (reader!=null){reader.close();}
        ch.flush();
        ch.close();

    }

}
