package qfTest.QF.Test.model.node.inline;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import qfTest.QF.Test.model.mark.AdfMark;
import qfTest.QF.Test.model.node.AdfNode;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MediaInline extends AdfNode {
    private MediaInlineAttrs attrs;
    private List<AdfMark> marks;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MediaInlineAttrs {
        private String id;
        private String collection;
        private String type;
        private String alt;
        private Integer width;
        private Integer height;
        private String occurrenceKey;
    }
}