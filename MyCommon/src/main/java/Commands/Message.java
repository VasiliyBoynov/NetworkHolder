package Commands;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "id"
)
@JsonSubTypes(
        value = {
                @JsonSubTypes.Type(value = NewUser.class, name = "NewUser"),
                @JsonSubTypes.Type(value = User.class, name = "User"),
                @JsonSubTypes.Type(value = GetFile.class, name = "GetFile"),
                @JsonSubTypes.Type(value = SetFile.class, name = "SetFile"),
                @JsonSubTypes.Type(value = FileInfo.class, name = "FileInfo"),
                @JsonSubTypes.Type(value = DeleteFile.class, name = "DeleteFile"),
                @JsonSubTypes.Type(value = Answer.class, name = "Answer")
        }
)
public abstract class Message {
}
