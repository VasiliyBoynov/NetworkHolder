package DB;

public enum SQLCommand {
    SQLInsert("insert into 'users'('nickName','Password') values (?,?)"),
    SQLSelectNickId(" select * from 'users' where nickName = ?  ");
    private String command;

    SQLCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
