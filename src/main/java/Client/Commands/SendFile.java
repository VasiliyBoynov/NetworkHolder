package Client.Commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

public class SendFile extends Instruction{
    private final int maxSizeByte = 65535;
    private String name;
    private Path path;
    private FileTime timeModify;
    private long size;
    private int partCount;

    public SendFile(Path path) throws IOException {
        super(Commands.setFile);
        this.path = path;
        this.name = path.getFileName().toString();
        this.timeModify = Files.getLastModifiedTime(path,LinkOption.NOFOLLOW_LINKS);
        this.size = path.toFile().length();
        this.partCount = (int) (this.size/maxSizeByte)+1;
    }

    @Override
    public List<String> toJSON() {
        List<String> list = new ArrayList<String>();
        list.add("{\n");
        list.add(String.format("\"%s\": \"%s\",%n","Instruction",getInstruction()));
        list.add(String.format("\"%s\": \"%s\",%n","path",this.path.toString()));
        list.add(String.format("\"%s\": \"%s\"%n","timeModify",this.timeModify.toString()));
        list.add(String.format("\"%s\": \"%s\"%n","size",this.size));
        list.add(String.format("\"%s\": \"%s\"%n","partCount",this.partCount));
        list.add("}\n");
        return list;

    }

}
