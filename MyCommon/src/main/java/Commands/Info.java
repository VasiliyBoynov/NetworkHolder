package Commands;

public class Info {
    private String path;
    private String lastModifyFile;
    private long size;

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLastModifyFile() {
        return lastModifyFile;
    }

    public void setLastModifyFile(String lastModifyFile) {
        this.lastModifyFile = lastModifyFile;
    }
}
