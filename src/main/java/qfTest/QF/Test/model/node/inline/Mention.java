package qfTest.QF.Test.model.node.inline;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import qfTest.QF.Test.model.node.AdfNode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Mention extends AdfNode {
    private MentionAttrs attrs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MentionAttrs {
        private String id;
        private String text;
        private String accessLevel;
        private String userType;
    }
}