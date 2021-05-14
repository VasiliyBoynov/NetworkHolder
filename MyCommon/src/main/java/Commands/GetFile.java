package Commands;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

public class GetFile extends  Message{
    private Path path;
    private FileTime lastModified;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public FileTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(FileTime lastModified) {
        this.lastModified = lastModified;
    }
}
