package Commands;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

public class GetFile extends  Message{
    private String path;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
