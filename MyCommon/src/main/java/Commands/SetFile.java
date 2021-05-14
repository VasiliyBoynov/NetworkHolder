package Commands;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

public class SetFile extends Message{
    private Path path;
    private FileTime lastModified;
    private long sizeFile;
    private long position;
    private byte[] data;

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

    public long getSizeFile() {
        return sizeFile;
    }

    public void setSizeFile(long sizeFile) {
        this.sizeFile = sizeFile;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
