package Controller;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FileMenager {
    private Path startPath = Paths.get("");
    private Path tmpPath = startPath.toAbsolutePath();

    public Path getTmpPath() {
        if (tmpPath==null){
            return Paths.get("");
        }
            else
        return tmpPath;
    }

    public void ls(Path path){
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
    public static List<Path> getLS(Path pathIn){
        //Path pathIn = (path.isAbsolute()) ? path:path.toAbsolutePath();
        ArrayList<Path> listFile = new ArrayList<>();
        ArrayList<Path> listDirectory = new ArrayList<>();
        if (pathIn.toFile().isFile()) {
            listFile.add(pathIn);
        } else {
            listDirectory.add(pathIn);
        }
        while(listDirectory.size()>0){
            Path tmp = listDirectory.get(listDirectory.size()-1);
            listDirectory.remove(listDirectory.size()-1);
            for (File f : tmp.toFile().listFiles()) {
                if (f.isFile()){
                    listFile.add(f.toPath());
                } else {
                    listDirectory.add(f.toPath());
                }
            }
        }
        return listFile;
    }

    public void ls(){

        Path pathIn = (tmpPath.isAbsolute()) ? tmpPath:tmpPath.toAbsolutePath();
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

    public void cd(){
        tmpPath = (tmpPath.getParent()!=null)? tmpPath.getParent():tmpPath;
    }
    public void cd(Path path){
        Path pathIn = (path.isAbsolute()) ? path:Paths.get(tmpPath.toString()+"\\"+path.toString());
        if (pathIn.toFile().isDirectory()) {
            tmpPath = pathIn;
        } else {
            System.out.println("ucorrected path");}
    }




}