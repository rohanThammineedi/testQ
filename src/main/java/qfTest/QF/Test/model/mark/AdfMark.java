package qfTest.QF.Test.model.mark;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Strong.class, name = "strong"),
    @JsonSubTypes.Type(value = Em.class, name = "em"),
    @JsonSubTypes.Type(value = Code.class, name = "code"),
    @JsonSubTypes.Type(value = Strike.class, name = "strike"),
    @JsonSubTypes.Type(value = Underline.class, name = "underline"),
    @JsonSubTypes.Type(value = Link.class, name = "link"),
    @JsonSubTypes.Type(value = TextColor.class, name = "textColor"),
    @JsonSubTypes.Type(value = BackgroundColor.class, name = "backgroundColor"),
    @JsonSubTypes.Type(value = Subsup.class, name = "subsup"),
    @JsonSubTypes.Type(value = Border.class, name = "border"),
    @JsonSubTypes.Type(value = Annotation.class, name = "annotation")
})
public class AdfMark {
    private String type;
}
