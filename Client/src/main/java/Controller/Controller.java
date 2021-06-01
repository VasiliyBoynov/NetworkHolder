package Controller;

import java.io.*;


import Commands.*;
import Constant.Constant;
import io.netty.channel.Channel;

import java.net.SocketException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import static java.nio.file.Files.getLastModifiedTime;

public class Controller implements AutoCloseable{

    public static final Controller instance = new Controller();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private AtomicBoolean authorization = new AtomicBoolean();
    private AtomicBoolean startAuthorisation = new AtomicBoolean();
    private AtomicBoolean finishAuthorisation = new AtomicBoolean();
    private AtomicBoolean statusSetFile = new AtomicBoolean();
    private AtomicBoolean startSetFile = new AtomicBoolean();
    private AtomicBoolean finchSetFile = new AtomicBoolean();
    private AtomicBoolean statusInfo = new AtomicBoolean();
    private AtomicBoolean getFile = new AtomicBoolean();
    private Path path;
    private long getSize;

    private Channel ch;
    private String txt;
    private List<Info> list;

    public long getGetSize() {
        return getSize;
    }

    public Path getPath() {
        return path;
    }

    public boolean getGetFile() {
        return getFile.get();
    }

    public void setGetFile(boolean getFile) {
        this.getFile.set(getFile);
    }

    public boolean getStatusInfo() {
        return statusInfo.get();
    }
    public void setStatusInfo(boolean statusInfo) {
        this.statusInfo.set(statusInfo);
    }

    public AtomicBoolean getStartSetFile() {
        return startSetFile;
    }

    public void setStartSetFile(AtomicBoolean startSetFile) {
        this.startSetFile = startSetFile;
    }

    public void setFinchSetFile(AtomicBoolean finchSetFile) {
        this.finchSetFile = finchSetFile;
    }

    public AtomicBoolean getStatusSetFile() {
        return statusSetFile;
    }

    public void setStatusSetFile(AtomicBoolean statusSetFile) {
        this.statusSetFile = statusSetFile;
    }

    public AtomicBoolean getFinchSetFile() {
        return finchSetFile;
    }

    public void setFinchSetFile(boolean finchSetFile) {
        this.finchSetFile.set(finchSetFile);
    }

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

    private FileMenager fm = new FileMenager();


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

    public List<Info> getList() {
        return list;
    }

    public void setList(List<Info> list) {
        this.list = list;
    }

    public FileMenager getFm() {
        return fm;
    }

    public void work() throws Exception {

        reader = new BufferedReader(new InputStreamReader(System.in));

        String commandLine;
        help();
        while (true) {
            System.out.print(fm.getTmpPath()+"> ");
            commandLine = reader.readLine();

            if (commandLine.equals("accept")){
                accept();
            }
            if (commandLine.equals("info")){
                info();
            }
            if (commandLine.equals("delete")){
                delete();
            }
            if (commandLine.equals("help")){
            help();
            }
            if (commandLine.equals("exit")) {
                this.close();
                break;}
            if (commandLine.equals("newUser")){

                autorization(new NewUser());
            }
            if (commandLine.equals("user")){

                autorization(new User());
            }

            if (commandLine.trim().split(" ")[0].equals("send")){
                if (getAuthorization().get()) {
                    String str1;
                    String arg;
                    if (commandLine.trim().split(" ").length>1){
                        str1 = commandLine.trim().split(" ")[1];
                    } else str1=null;
                    if (commandLine.trim().split(" ").length>2){
                        arg = commandLine.trim().split(" ")[2];
                    } else arg=null;
                    if (str1==null) {
                        System.out.println("Never format commands \"send path_to_file\"");
                    } else if (arg==null & str1!=null){
                        sendFile(fm.getTmpPath(),str1);
                    } else if (arg.equals("-r")) {
                        Path pathIn = (Paths.get(str1).isAbsolute()) ? Paths.get(str1):Paths.get(fm.getTmpPath().toString()+"\\"+str1);
                        if (pathIn.toFile().exists()){
                            for (Path l : fm.getLS(pathIn)){
                                sendFile(fm.getTmpPath(),l.toString());
                            }
                        } else {
                            System.out.println("Directory does not exist"+pathIn);
                        }


                    }

                } else {
                    dontAutorization();
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
        }
    };
    public void help(){
        System.out.println("Для выхода наберите \"exit\"");
        System.out.println("Для создания нового клиента введите \"newUser\"");
        System.out.println("Для авторизации пользователя введите \"user\"");
        System.out.println("Для загрузки файла на сервер введите \"send\"");
        System.out.println("Для загрузки файла c сервера введите \"accept\"");
        System.out.println("Для просмотра списка доступных файлов на сервере для загрузки введите \"info\"");
        System.out.println("Для повторения информации введите \"help\"");
        System.out.println("Для удаления данных из облачного хранилища введите \"delete\"");

    }

    public void dontAutorization(){
        System.out.println("Вы не прошли процедуру авторизации.\nДля старта процедуры введите командное слово \"user\" или \"newUser\" ");
    }

    public void delete() throws IOException {
        System.out.println("Для выхода из режима удаления файлов с сервера наберите \"exit\"");
        if (getAuthorization().get()){
            String str;
            while (true){
                info();
                System.out.println("Введите id номер файла");
                str = reader.readLine();
                if (str.equals("exit")) break;
                try {
                    int num = Integer.parseInt(str);
                    if (num<=list.size() & num>0 ){
                        System.out.printf("Вы уверены, что хотите удалить файл :%s%n Y/N > ",list.get(num-1).getPath());
                        while(true){
                            str = reader.readLine();
                            if (str.toLowerCase().equals("y") | str.toLowerCase().equals("n")){
                                break;
                            } else {
                                System.out.println("введите \"Y\" или \"N\"");
                            }
                        }
                        if (str.toLowerCase().equals("y")){
                            DeleteFile deleteFile = new DeleteFile();
                            deleteFile.setPath(list.get(num-1).getPath());
                            ch.writeAndFlush(deleteFile);
                        }
                    }
                } catch (NumberFormatException e){
                    System.out.println("введите число!");
                }
            }

        } else {
            dontAutorization();
        }
    }

    public void info(){
        if (getAuthorization().get()){
            ch.writeAndFlush(new FileInfo());
            setStatusInfo(false);
            while (!getStatusInfo()){}
        } else {
            dontAutorization();
        }

    }

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
            int count;
            count=0;
            while (!getFinishAuthorisation().get()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;
                if (count>=Constant.TIME_OUT_S.getConstant()){
                    startAuthorisation.set(false);
                    finishAuthorisation.set(true);
                    authorization.set(false);
                    break;
                }
            }
            rezl = (getAuthorization().get())? "Authorization was successful":"Authorization was unsuccessful\n"+txt;
            System.out.println(rezl);
            System.out.print(fm.getTmpPath()+"> ");
        });
    }

    public void sendFile(Path tmpPath, String str) throws IOException, InterruptedException {
        Path pathIn = (Paths.get(str).isAbsolute()) ? Paths.get(str):Paths.get(tmpPath.toString()+"\\"+str);
        if (pathIn.toFile().isFile()){
            executor.submit(()->{
                startSetFile.set(false);
                finchSetFile.set(false);

                System.out.println("Start send File"+pathIn);
                try(RandomAccessFile accessFile = new RandomAccessFile(pathIn.toFile(), "r")){
                    long length = pathIn.toFile().length();
                    long position = accessFile.getFilePointer();
                    long lastModified = getLastModifiedTime(pathIn).toMillis();
                    long available = length - position;

                    SetFile setFileMataData = new SetFile();
                    setFileMataData.setPath(pathIn.toString());
                    setFileMataData.setLastModified(lastModified);
                    setFileMataData.setSizeFile(length);
                    ch.writeAndFlush(setFileMataData).sync();
                    int count=0;
                    while (!startSetFile.get() & !finchSetFile.get()){
                        Thread.sleep(1000);
                        count++;
                        if (count>=Constant.TIME_OUT_S.getConstant()){
                            startSetFile.set(false);
                            finchSetFile.set(true);
                            statusSetFile.set(false);
                            System.out.printf("Finish send File %s unsuccessful%n",pathIn);
                            break;
                        }

                    }
                    while (available > 0 & startSetFile.get()) {
                        SetFile setFile = new SetFile();
                        //setFile.setPath(pathIn.toString());
                        //setFile.setLastModified(lastModified);
                        //setFile.setSizeFile(length);

                        byte[] buffer;
                        if (available > Constant.MAX_PACKAGE_BYTE.getConstant()) {
                            buffer = new byte[Constant.MAX_PACKAGE_BYTE.getConstant()];
                        } else {
                            buffer = new byte[(int) available];
                        }
                        accessFile.read(buffer);
                        setFile.setData(buffer);
                        setFile.setPosition(position);
                        /*try {
                            ch.writeAndFlush(setFile).sync();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                        ch.writeAndFlush(setFile).sync();
                        position =  accessFile.getFilePointer();
                        available = length - position;
                    }
                    count=0;
                    while (!finchSetFile.get()){
                        Thread.sleep(1000);
                        count++;
                        if (count>=Constant.TIME_OUT_S.getConstant()){
                            startSetFile.set(false);
                            finchSetFile.set(true);
                            statusSetFile.set(false);
                            System.out.printf("Finish send File %s unsuccessful%n",pathIn);
                            break;
                        }

                    }
                    if(finchSetFile.get() & statusSetFile.get()){
                        System.out.printf("Finish send File %s successful%n",pathIn);
                    } else{
                        System.out.printf("Finish send File %s unsuccessful%n%s%n",pathIn,txt);
                    }
                    startSetFile.set(false);
                    finchSetFile.set(false);
                    statusSetFile.set(false);


                    System.out.print(fm.getTmpPath()+"> ");
                } catch (SocketException e) {
                    try {
                        System.out.println("disconnection, server is not available");
                        close();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            //SendFile sendFile = new SendFile(pathIn);
            // ch.writeAndFlush(sendFile.objToByte());
        } else {
            System.out.println("uncorrected path to file " + pathIn);
        }
    }

    public void accept() throws IOException {
        System.out.println("Для выхода из режима приема файлов от сервера наберите \"exit\"");
        if (getAuthorization().get()){

            String str;
            while (true){
                info();
                if (list.size()==0) break;
                System.out.println("Введите id номер файла");
                str = reader.readLine();
                if (str.equals("exit")) break;
                try {
                    int num = Integer.parseInt(str);
                    if (num<=list.size() & num>0 ){
                        System.out.printf("Вы уверены, что хотите загрузить файл :%s%nВ директорию :%s%n Y/N > ",list.get(num-1).getPath(),fm.getTmpPath());
                        while(true){
                            str = reader.readLine();
                            if (str.toLowerCase().equals("y") | str.toLowerCase().equals("n")){
                                break;
                            } else {
                                System.out.println("введите \"Y\" или \"N\"");
                            }
                        }
                        if (str.toLowerCase().equals("y")){
                            if (fm.getTmpPath().toFile().getUsableSpace()>=list.get(num-1).getSize()){
                                getFile.set(true);
                                GetFile getFile = new GetFile();
                                getFile.setPath(list.get(num-1).getPath());
                                path = Paths.get(list.get(num-1).getPath());
                                getSize = list.get(num - 1).getSize();
                                Path path1 = Paths.get(getFm().getTmpPath().toString() + "//" + getPath().toFile().getName());
                                if (path1.toFile().exists()){
                                    path1.toFile().delete();
                                }
                                ch.writeAndFlush(getFile);
                            System.out.printf("Старт загрузки файла с сервера %s в папку %s%n", path.toFile().getName(),fm.getTmpPath());
                            } else {
                                System.out.println("не достаточно свободного места "+fm.getTmpPath());
                            }
                        }
                    }
                } catch (NumberFormatException e){
                    System.out.println("введите число!");
                }
            }
        } else {
            dontAutorization();
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