package qfTest.QF.Test.model.node.block;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import qfTest.QF.Test.model.node.AdfNode;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Doc extends AdfNode {
    private Integer version = 1;
}