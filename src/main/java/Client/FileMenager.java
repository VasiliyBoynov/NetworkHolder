package Client;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

public class FileMenager {
    private Path startPath = Paths.get("");
    private Path tmpPath = startPath.toAbsolutePath();

    public Path getTmpPath() {
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
        tmpPath = tmpPath.getParent();
    }
    public void cd(Path path){
        Path pathIn = (path.isAbsolute()) ? path:Paths.get(tmpPath.toString()+"\\"+path.toString());
        if (pathIn.toFile().isDirectory()) {
            tmpPath = pathIn;
        } else {
            System.out.println("ucorrected path");}
    }




}
