package DB;

public enum SQLCommand {
    SQLInsert("insert into 'users'('nickName','Password') values (?,?)"),
    SQLSelectNickId(" select * from 'users' where nickName = ?  "),
    //SQLSelectFileName("select * from 'Files' where user_id = ?"),
    SQLSelectFileName("select * from 'Files' where user_id = ? and path_client = ?"),
    SQLInsertFile("insert into 'Files'('user_id','path_client','name_file_server','lastTimeModif') values (?,?,?,?)"),
    SQLUpdateStatus("UPDATE 'Files' SET 'Status' = ? WHERE name_file_server = ?");
    //SQLUpdateStatus("select * from 'Files' where name_file_server = ?");

    private String command;

    SQLCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
