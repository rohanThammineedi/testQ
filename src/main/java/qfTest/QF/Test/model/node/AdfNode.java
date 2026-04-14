package qfTest.QF.Test.model.node;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import qfTest.QF.Test.model.node.block.BlockQuote;
import qfTest.QF.Test.model.node.block.BulletList;
import qfTest.QF.Test.model.node.block.CodeBlock;
import qfTest.QF.Test.model.node.block.Doc;
import qfTest.QF.Test.model.node.block.Expand;
import qfTest.QF.Test.model.node.block.ExtensionFrame;
import qfTest.QF.Test.model.node.block.Heading;
import qfTest.QF.Test.model.node.block.ListItem;
import qfTest.QF.Test.model.node.block.Media;
import qfTest.QF.Test.model.node.block.MediaGroup;
import qfTest.QF.Test.model.node.block.MediaSingle;
import qfTest.QF.Test.model.node.block.MultiBodiedExtension;
import qfTest.QF.Test.model.node.block.NestedExpand;
import qfTest.QF.Test.model.node.block.OrderedList;
import qfTest.QF.Test.model.node.block.Panel;
import qfTest.QF.Test.model.node.block.Paragraph;
import qfTest.QF.Test.model.node.block.Rule;
import qfTest.QF.Test.model.node.block.Table;
import qfTest.QF.Test.model.node.block.TableCell;
import qfTest.QF.Test.model.node.block.TableHeader;
import qfTest.QF.Test.model.node.block.TableRow;
import qfTest.QF.Test.model.node.inline.Date;
import qfTest.QF.Test.model.node.inline.Emoji;
import qfTest.QF.Test.model.node.inline.HardBreak;
import qfTest.QF.Test.model.node.inline.InlineCard;
import qfTest.QF.Test.model.node.inline.MediaInline;
import qfTest.QF.Test.model.node.inline.Mention;
import qfTest.QF.Test.model.node.inline.StatusNode;
import qfTest.QF.Test.model.node.inline.Text;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    // Block nodes
    @JsonSubTypes.Type(value = Doc.class, name = "doc"),
    @JsonSubTypes.Type(value = Paragraph.class, name = "paragraph"),
    @JsonSubTypes.Type(value = Heading.class, name = "heading"),
    @JsonSubTypes.Type(value = CodeBlock.class, name = "codeBlock"),
    @JsonSubTypes.Type(value = BlockQuote.class, name = "blockquote"),
    @JsonSubTypes.Type(value = BulletList.class, name = "bulletList"),
    @JsonSubTypes.Type(value = OrderedList.class, name = "orderedList"),
    @JsonSubTypes.Type(value = ListItem.class, name = "listItem"),
    @JsonSubTypes.Type(value = Panel.class, name = "panel"),
    @JsonSubTypes.Type(value = Rule.class, name = "rule"),
    @JsonSubTypes.Type(value = Table.class, name = "table"),
    @JsonSubTypes.Type(value = TableRow.class, name = "tableRow"),
    @JsonSubTypes.Type(value = TableCell.class, name = "tableCell"),
    @JsonSubTypes.Type(value = TableHeader.class, name = "tableHeader"),
    @JsonSubTypes.Type(value = Expand.class, name = "expand"),
    @JsonSubTypes.Type(value = NestedExpand.class, name = "nestedExpand"),
    @JsonSubTypes.Type(value = MediaSingle.class, name = "mediaSingle"),
    @JsonSubTypes.Type(value = MediaGroup.class, name = "mediaGroup"),
    @JsonSubTypes.Type(value = Media.class, name = "media"),
    @JsonSubTypes.Type(value = MultiBodiedExtension.class, name = "multiBodiedExtension"),
    @JsonSubTypes.Type(value = ExtensionFrame.class, name = "extensionFrame"),
    // Inline nodes
    @JsonSubTypes.Type(value = Text.class, name = "text"),
    @JsonSubTypes.Type(value = HardBreak.class, name = "hardBreak"),
    @JsonSubTypes.Type(value = Emoji.class, name = "emoji"),
    @JsonSubTypes.Type(value = Mention.class, name = "mention"),
    @JsonSubTypes.Type(value = InlineCard.class, name = "inlineCard"),
    @JsonSubTypes.Type(value = Date.class, name = "date"),
    @JsonSubTypes.Type(value = StatusNode.class, name = "status"),
    @JsonSubTypes.Type(value = MediaInline.class, name = "mediaInline")
})
public class AdfNode {
    private String type;
    private List<AdfNode> content;
}