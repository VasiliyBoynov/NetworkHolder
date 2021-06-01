package DB;

public enum SQLCommand {
    SQLInsert("insert into 'users'('nickName','Password') values (?,?)"),
    SQLSelectNickId("select * from 'users' where nickName = ?"),
    SQLSelectFileInfo("select * from 'Files' where id_user = ? and Status=1 and del=0"),
    SQLSelectFileName("select * from 'Files' where id_user = ? and path_client = ?"),
    SQLInsertFile("insert into 'Files'('id_user','path_client','name_file_server','lastTimeModif','size') values (?,?,?,?,?)"),
    SQLUpdateStatusDelete("UPDATE 'Files' SET 'del' = ? WHERE id_files = ?"),
    SQLUpdateStatus("UPDATE 'Files' SET 'Status' = ?, 'del' = ?  WHERE name_file_server = ? and id_user = ? "),
    SQLUpdateFile("UPDATE 'Files' SET 'lastTimeModif' = ?, 'Status' = ?, 'del' = ?, 'size' = ? where name_file_server = ?"),
    SQLUpdateSizeArray("Update 'users' set 'SizeArray'=? where nickName = ?"),
    SQLFindSizeArray("select u.id_user id, u.nickName nick, f.size size from 'users' as 'u', 'Files' as 'f' where U.id_user=F.id_user and nickName=? and f.Status=1 and f.del=0");
    //SQLUpdateStatus("select * from 'Files' where name_file_server = ?");

    private String command;

    SQLCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
