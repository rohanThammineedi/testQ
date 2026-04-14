// package qfTest.QF.Test.service;

// import java.util.ArrayList;
// import java.util.Base64;
// import java.util.List;
// import java.util.Map;
// import java.util.UUID;

// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;

// import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import net.datafaker.Faker;
// import qfTest.QF.Test.constants.InstanceConstants;
// import qfTest.QF.Test.dto.Fields;
// import qfTest.QF.Test.dto.IssueResponse;
// import qfTest.QF.Test.dto.IssueType;
// import qfTest.QF.Test.dto.Project;
// import qfTest.QF.Test.model.mark.Em;
// import qfTest.QF.Test.model.mark.Strike;
// import qfTest.QF.Test.model.mark.Strong;
// import qfTest.QF.Test.model.mark.Underline;
// import qfTest.QF.Test.model.node.AdfNode;
// import qfTest.QF.Test.model.node.block.BlockQuote;
// import qfTest.QF.Test.model.node.block.BulletList;
// import qfTest.QF.Test.model.node.block.CodeBlock;
// import qfTest.QF.Test.model.node.block.Doc;
// import qfTest.QF.Test.model.node.block.Heading;
// import qfTest.QF.Test.model.node.block.ListItem;
// import qfTest.QF.Test.model.node.block.OrderedList;
// import qfTest.QF.Test.model.node.block.Panel;
// import qfTest.QF.Test.model.node.block.Paragraph;
// import qfTest.QF.Test.model.node.block.Rule;
// import qfTest.QF.Test.model.node.block.Table;
// import qfTest.QF.Test.model.node.block.TableCell;
// import qfTest.QF.Test.model.node.block.TableHeader;
// import qfTest.QF.Test.model.node.block.TableRow;
// import qfTest.QF.Test.model.node.inline.Text;

// @Service
// @AllArgsConstructor
// @Slf4j
// public class JiraIssueService {

//     private static final long SYNC_WAIT_MS = 2 * 60 * 1000L;

//     private final Faker faker;
//     private final RestTemplate restTemplate;

//     private String createRandomString() {
//         return faker.lorem().sentence(10) + " " + UUID.randomUUID();
//     }

//     private HttpHeaders authHeaders() {
//         String credentials = InstanceConstants.email + ":" + InstanceConstants.token;
//         String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
//         HttpHeaders headers = new HttpHeaders();
//         headers.set("Authorization", "Basic " + encoded);
//         headers.setContentType(MediaType.APPLICATION_JSON);
//         return headers;
//     }

//     // ===================== ISSUE CRUD =====================

//     private String createIssue() {
//         Fields fields = new Fields();
//         fields.setSummary(createRandomString());
//         fields.setProject(new Project(InstanceConstants.sourceProkectKey));
//         fields.setIssueType(new IssueType("Task"));

//         Map<String, Object> body = Map.of("fields", fields);
//         HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, authHeaders());

//         String url = InstanceConstants.sourceInstance + "/rest/api/3/issue";
//         ResponseEntity<IssueResponse> response = restTemplate.exchange(
//                 url, HttpMethod.POST, request, IssueResponse.class);

//         String issueKey = response.getBody().getKey();
//         log.info("Created source issue: {}", issueKey);
//         return issueKey;
//     }

//     private IssueResponse getSourceIssue(String issueKey) {
//         String url = InstanceConstants.sourceInstance + "/rest/api/3/issue/" + issueKey;
//         HttpEntity<Void> request = new HttpEntity<>(authHeaders());
//         ResponseEntity<IssueResponse> response = restTemplate.exchange(
//                 url, HttpMethod.GET, request, IssueResponse.class);
//         log.info("Fetched source issue: {}", issueKey);
//         return response.getBody();
//     }

//     private IssueResponse getTargetIssue(String replicaKey) {
//         String url = InstanceConstants.destInstance + "/rest/api/3/issue/" + replicaKey;
//         HttpEntity<Void> request = new HttpEntity<>(authHeaders());
//         ResponseEntity<IssueResponse> response = restTemplate.exchange(
//                 url, HttpMethod.GET, request, IssueResponse.class);
//         log.info("Fetched target issue: {}", replicaKey);
//         return response.getBody();
//     }

//     // ===================== VERIFY SYNC =====================

//     public boolean verifySync() throws InterruptedException {

//         // Step 1: Create issue on source
//         String sourceIssueKey = createIssue();
//         log.debug("Source Issue Created {}", sourceIssueKey);

//         // Step 2: Wait for sync engine
//         log.info("Waiting for sync...");
//         Thread.sleep(SYNC_WAIT_MS);

//         // Step 3: Fetch source issue and get replica key
//         IssueResponse sourceIssue = getSourceIssue(sourceIssueKey);
//         sourceIssue.getFields().configureReplicaKey("customfield_10419");
//         String replicaKey = sourceIssue.getFields().getSyncedIssueKey();
//         log.info("Replica key from source issue: {}", replicaKey);

//         if (replicaKey == null || replicaKey.isBlank()) {
//             log.error("Sync failed — replica field not populated on: {}", sourceIssueKey);
//             return false;
//         }

//         // Step 4: Fetch target issue using replica key
//         IssueResponse targetIssue;
//         try {
//             targetIssue = getTargetIssue(replicaKey);
//         } catch (Exception e) {
//             log.error("Failed to fetch target issue: {} — {}", replicaKey, e.getMessage());
//             return false;
//         }

//         // Step 5: Compare summary
//         String sourceSummary = sourceIssue.getFields().getSummary();
//         String targetSummary = targetIssue.getFields().getSummary();
//         log.info("Source summary : {}", sourceSummary);
//         log.info("Target summary : {}", targetSummary);
//         boolean summaryMatches = sourceSummary != null && sourceSummary.equals(targetSummary);
//         log.info("Summary match  : {}", summaryMatches);

//         // Step 6: Update summary check
//         boolean updateSummaryCheck = updateSummaryCheck(sourceIssueKey, targetIssue.getKey());

//         // Step 7: Description sync check
//         boolean descriptionSynced = verifyDescriptionSync(sourceIssueKey, targetIssue.getKey());

//         return summaryMatches && updateSummaryCheck && descriptionSynced;
//     }

//     // ===================== SUMMARY UPDATE CHECK =====================

//     private boolean updateSummaryCheck(String sourceIssueKey, String targetIssueKey)
//             throws InterruptedException {
//         String newSummary = createRandomString();

//         Map<String, Object> body = Map.of("fields", Map.of("summary", newSummary));
//         HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, authHeaders());

//         String url = InstanceConstants.sourceInstance + "/rest/api/3/issue/" + sourceIssueKey;
//         restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);
//         log.info("Updated source issue: {} with new summary: {}", sourceIssueKey, newSummary);

//         log.info("Waiting for sync...");
//         Thread.sleep(SYNC_WAIT_MS);

//         IssueResponse targetIssue = getTargetIssue(targetIssueKey);
//         String targetSummary = targetIssue.getFields().getSummary();
//         log.info("New summary      : {}", newSummary);
//         log.info("Target summary   : {}", targetSummary);

//         boolean summaryMatches = newSummary.equals(targetSummary);
//         log.info("Update sync match: {}", summaryMatches);

//         return summaryMatches;
//     }

//     // ===================== DESCRIPTION SYNC CHECK =====================

//     private boolean verifyDescriptionSync(String sourceIssueKey, String targetIssueKey)
//             throws InterruptedException {

//         AdfNode description = buildDescription();

//         Map<String, Object> fields = Map.of("description", description);
//         Map<String, Object> body = Map.of("fields", fields);

//         HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, authHeaders());

//         String url = InstanceConstants.sourceInstance + "/rest/api/3/issue/" + sourceIssueKey;
//         restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);
//         log.info("Updated description on source issue: {}", sourceIssueKey);

//         log.info("Waiting for sync...");
//         Thread.sleep(SYNC_WAIT_MS);

//         IssueResponse sourceIssue = getSourceIssue(sourceIssueKey);
//         IssueResponse targetIssue = getTargetIssue(targetIssueKey);

//         AdfNode sourceDescription = sourceIssue.getFields().getDescription();
//         AdfNode targetDescription = targetIssue.getFields().getDescription();

//         log.info("Source description : {}", sourceDescription);
//         log.info("Target description : {}", targetDescription);

//         boolean descriptionMatches = sourceDescription != null
//                 && sourceDescription.equals(targetDescription);
//         log.info("Description match  : {}", descriptionMatches);

//         return descriptionMatches;
//     }

//     // ===================== ADF BUILDERS =====================

//     private AdfNode buildDescription() {
//         Doc doc = new Doc();
//         doc.setType("doc");
//         doc.setContent(new ArrayList<>(List.of(
//                 buildHeading("Automated Test Description", 1),
//                 buildParagraph("This is a plain text paragraph " + UUID.randomUUID()),
//                 buildRichTextParagraph(),
//                 buildBulletList(),
//                 buildOrderedList(),
//                 buildTable(),
//                 buildCodeBlock(),
//                 buildPanel(),
//                 buildBlockQuote(),
//                 buildRule())));
//         return doc;
//     }

//     private Heading buildHeading(String text, int level) {
//         Text textNode = new Text();
//         textNode.setType("text");
//         textNode.setText(text);

//         Heading heading = new Heading();
//         heading.setType("heading");
//         heading.setAttrs(new Heading.HeadingAttrs(level, null));
//         heading.setContent(new ArrayList<>(List.of(textNode)));
//         return heading;
//     }

//     private Paragraph buildParagraph(String text) {
//         Text textNode = new Text();
//         textNode.setType("text");
//         textNode.setText(text);

//         Paragraph paragraph = new Paragraph();
//         paragraph.setType("paragraph");
//         paragraph.setContent(new ArrayList<>(List.of(textNode)));
//         return paragraph;
//     }

//     private Paragraph buildRichTextParagraph() {
//         Text boldText = new Text();
//         boldText.setType("text");
//         boldText.setText("Bold text ");
//         Strong strong = new Strong();
//         strong.setType("strong");
//         boldText.setMarks(new ArrayList<>(List.of(strong)));

//         Text italicText = new Text();
//         italicText.setType("text");
//         italicText.setText("Italic text ");
//         Em em = new Em();
//         em.setType("em");
//         italicText.setMarks(new ArrayList<>(List.of(em)));

//         Text underlineText = new Text();
//         underlineText.setType("text");
//         underlineText.setText("Underline text ");
//         Underline underline = new Underline();
//         underline.setType("underline");
//         underlineText.setMarks(new ArrayList<>(List.of(underline)));

//         Text strikeText = new Text();
//         strikeText.setType("text");
//         strikeText.setText("Strike text");
//         Strike strike = new Strike();
//         strike.setType("strike");
//         strikeText.setMarks(new ArrayList<>(List.of(strike)));

//         Paragraph paragraph = new Paragraph();
//         paragraph.setType("paragraph");
//         paragraph.setContent(new ArrayList<>(List.of(boldText, italicText, underlineText, strikeText)));
//         return paragraph;
//     }

//     private BulletList buildBulletList() {
//         BulletList bulletList = new BulletList();
//         bulletList.setType("bulletList");
//         bulletList.setContent(new ArrayList<>(List.of(
//                 buildListItem("Bullet item 1"),
//                 buildListItem("Bullet item 2"),
//                 buildListItem("Bullet item 3"))));
//         return bulletList;
//     }

//     private OrderedList buildOrderedList() {
//         OrderedList orderedList = new OrderedList();
//         orderedList.setType("orderedList");
//         orderedList.setAttrs(new OrderedList.OrderedListAttrs(1));
//         orderedList.setContent(new ArrayList<>(List.of(
//                 buildListItem("Ordered item 1"),
//                 buildListItem("Ordered item 2"),
//                 buildListItem("Ordered item 3"))));
//         return orderedList;
//     }

//     private ListItem buildListItem(String text) {
//         ListItem listItem = new ListItem();
//         listItem.setType("listItem");
//         listItem.setContent(new ArrayList<>(List.of(buildParagraph(text))));
//         return listItem;
//     }

//     private Table buildTable() {
//         TableRow headerRow = new TableRow();
//         headerRow.setType("tableRow");
//         headerRow.setContent(new ArrayList<>(List.of(
//                 buildTableHeader("Header 1"),
//                 buildTableHeader("Header 2"),
//                 buildTableHeader("Header 3"))));

//         TableRow dataRow = new TableRow();
//         dataRow.setType("tableRow");
//         dataRow.setContent(new ArrayList<>(List.of(
//                 buildTableCell("Cell 1"),
//                 buildTableCell("Cell 2"),
//                 buildTableCell("Cell 3"))));

//         Table table = new Table();
//         table.setType("table");
//         table.setAttrs(new Table.TableAttrs(false, "default", null, null));
//         table.setContent(new ArrayList<>(List.of(headerRow, dataRow)));
//         return table;
//     }

//     private TableHeader buildTableHeader(String text) {
//         TableHeader header = new TableHeader();
//         header.setType("tableHeader");
//         header.setAttrs(new TableHeader.TableHeaderAttrs(1, 1, null, null));
//         header.setContent(new ArrayList<>(List.of(buildParagraph(text))));
//         return header;
//     }

//     private TableCell buildTableCell(String text) {
//         TableCell cell = new TableCell();
//         cell.setType("tableCell");
//         cell.setAttrs(new TableCell.TableCellAttrs(1, 1, null, null));
//         cell.setContent(new ArrayList<>(List.of(buildParagraph(text))));
//         return cell;
//     }

//     private CodeBlock buildCodeBlock() {
//         Text codeText = new Text();
//         codeText.setType("text");
//         codeText.setText("System.out.println(\"Hello World\");");

//         CodeBlock codeBlock = new CodeBlock();
//         codeBlock.setType("codeBlock");
//         codeBlock.setAttrs(new CodeBlock.CodeBlockAttrs("java"));
//         codeBlock.setContent(new ArrayList<>(List.of(codeText)));
//         return codeBlock;
//     }

//     private Panel buildPanel() {
//         Panel panel = new Panel();
//         panel.setType("panel");
//         panel.setAttrs(new Panel.PanelAttrs("info"));
//         panel.setContent(new ArrayList<>(List.of(buildParagraph("This is an info panel"))));
//         return panel;
//     }

//     private BlockQuote buildBlockQuote() {
//         BlockQuote blockQuote = new BlockQuote();
//         blockQuote.setType("blockquote");
//         blockQuote.setContent(new ArrayList<>(List.of(buildParagraph("This is a blockquote"))));
//         return blockQuote;
//     }

//     private Rule buildRule() {
//         Rule rule = new Rule();
//         rule.setType("rule");
//         return rule;
//     }
// }


package qfTest.QF.Test.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import qfTest.QF.Test.constants.InstanceConstants;
import qfTest.QF.Test.dto.Fields;
import qfTest.QF.Test.dto.IssueResponse;
import qfTest.QF.Test.dto.IssueType;
import qfTest.QF.Test.dto.Project;
import qfTest.QF.Test.model.mark.Em;
import qfTest.QF.Test.model.mark.Strike;
import qfTest.QF.Test.model.mark.Strong;
import qfTest.QF.Test.model.mark.Underline;
import qfTest.QF.Test.model.node.AdfNode;
import qfTest.QF.Test.model.node.block.BlockQuote;
import qfTest.QF.Test.model.node.block.BulletList;
import qfTest.QF.Test.model.node.block.CodeBlock;
import qfTest.QF.Test.model.node.block.Doc;
import qfTest.QF.Test.model.node.block.Heading;
import qfTest.QF.Test.model.node.block.ListItem;
import qfTest.QF.Test.model.node.block.OrderedList;
import qfTest.QF.Test.model.node.block.Panel;
import qfTest.QF.Test.model.node.block.Paragraph;
import qfTest.QF.Test.model.node.block.Rule;
import qfTest.QF.Test.model.node.block.Table;
import qfTest.QF.Test.model.node.block.TableCell;
import qfTest.QF.Test.model.node.block.TableHeader;
import qfTest.QF.Test.model.node.block.TableRow;
import qfTest.QF.Test.model.node.inline.Text;

@Service
@AllArgsConstructor
@Slf4j
public class JiraIssueService {

    private static final long SYNC_WAIT_MS = 2 * 60 * 1000L;

    private final Faker faker;
    private final RestTemplate restTemplate;

    private String createRandomString() {
        return faker.lorem().sentence(10) + " " + UUID.randomUUID();
    }

    private HttpHeaders authHeaders() {
        String credentials = InstanceConstants.email + ":" + InstanceConstants.token;
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encoded);
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        return headers;
    }

    // ===================== ISSUE CRUD =====================

    private String createIssue() {
        Fields fields = new Fields();
        fields.setSummary(createRandomString());
        fields.setProject(new Project(InstanceConstants.sourceProkectKey));
        fields.setIssueType(new IssueType("Task"));
        fields.setDescription(buildDescription());  // <-- create with description

        Map<String, Object> body = Map.of("fields", fields);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, authHeaders());

        String url = InstanceConstants.sourceInstance + "/rest/api/3/issue";
        ResponseEntity<IssueResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, request, IssueResponse.class);

        String issueKey = response.getBody().getKey();
        log.info("Created source issue: {}", issueKey);
        return issueKey;
    }

    private IssueResponse getSourceIssue(String issueKey) {
        String url = InstanceConstants.sourceInstance + "/rest/api/3/issue/" + issueKey;
        HttpEntity<Void> request = new HttpEntity<>(authHeaders());
        ResponseEntity<IssueResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, request, IssueResponse.class);
        log.info("Fetched source issue: {}", issueKey);
        return response.getBody();
    }

    private IssueResponse getTargetIssue(String replicaKey) {
        String url = InstanceConstants.destInstance + "/rest/api/3/issue/" + replicaKey;
        HttpEntity<Void> request = new HttpEntity<>(authHeaders());
        ResponseEntity<IssueResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, request, IssueResponse.class);
        log.info("Fetched target issue: {}", replicaKey);
        return response.getBody();
    }

    // ===================== VERIFY SYNC =====================

    public boolean verifySync() throws InterruptedException {

        // Step 1: Create issue on source with summary + description
        String sourceIssueKey = createIssue();
        log.debug("Source Issue Created {}", sourceIssueKey);

        // Step 2: Wait for sync engine
        log.info("Waiting for sync...");
        Thread.sleep(SYNC_WAIT_MS);

        // Step 3: Fetch source issue and get replica key
        IssueResponse sourceIssue = getSourceIssue(sourceIssueKey);
        sourceIssue.getFields().configureReplicaKey("customfield_10419");
        String replicaKey = sourceIssue.getFields().getSyncedIssueKey();
        log.info("Replica key from source issue: {}", replicaKey);

        if (replicaKey == null || replicaKey.isBlank()) {
            log.error("Sync failed — replica field not populated on: {}", sourceIssueKey);
            return false;
        }

        // Step 4: Fetch target issue using replica key
        IssueResponse targetIssue;
        try {
            targetIssue = getTargetIssue(replicaKey);
        } catch (Exception e) {
            log.error("Failed to fetch target issue: {} — {}", replicaKey, e.getMessage());
            return false;
        }

        // Step 5: Compare summary after create
        String sourceSummary = sourceIssue.getFields().getSummary();
        String targetSummary = targetIssue.getFields().getSummary();
        log.info("Source summary : {}", sourceSummary);
        log.info("Target summary : {}", targetSummary);
        boolean summaryMatches = sourceSummary != null && sourceSummary.equals(targetSummary);
        log.info("Summary match  : {}", summaryMatches);

        // Step 6: Compare description after create
        AdfNode sourceDescription = sourceIssue.getFields().getDescription();
        AdfNode targetDescription = targetIssue.getFields().getDescription();
        log.info("Source description : {}", sourceDescription);
        log.info("Target description : {}", targetDescription);
        boolean descriptionMatches = sourceDescription != null && sourceDescription.equals(targetDescription);
        log.info("Description match  : {}", descriptionMatches);

        // Step 7: Update summary check
        boolean updateSummaryCheck = updateSummaryCheck(sourceIssueKey, targetIssue.getKey());

        // Step 8: Update description check
        boolean updateDescriptionCheck = verifyDescriptionSync(sourceIssueKey, targetIssue.getKey());

        return summaryMatches && descriptionMatches && updateSummaryCheck && updateDescriptionCheck;
    }

    // ===================== SUMMARY UPDATE CHECK =====================

    private boolean updateSummaryCheck(String sourceIssueKey, String targetIssueKey)
            throws InterruptedException {
        String newSummary = createRandomString();

        Map<String, Object> body = Map.of("fields", Map.of("summary", newSummary));
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, authHeaders());

        String url = InstanceConstants.sourceInstance + "/rest/api/3/issue/" + sourceIssueKey;
        restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);
        log.info("Updated source issue: {} with new summary: {}", sourceIssueKey, newSummary);

        log.info("Waiting for sync...");
        Thread.sleep(SYNC_WAIT_MS);

        IssueResponse targetIssue = getTargetIssue(targetIssueKey);
        String targetSummary = targetIssue.getFields().getSummary();
        log.info("New summary      : {}", newSummary);
        log.info("Target summary   : {}", targetSummary);

        boolean summaryMatches = newSummary.equals(targetSummary);
        log.info("Update sync match: {}", summaryMatches);

        return summaryMatches;
    }

    // ===================== DESCRIPTION UPDATE CHECK =====================

    private boolean verifyDescriptionSync(String sourceIssueKey, String targetIssueKey)
            throws InterruptedException {

        AdfNode newDescription = buildDescription();

        Map<String, Object> fields = Map.of("description", newDescription);
        Map<String, Object> body = Map.of("fields", fields);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, authHeaders());

        String url = InstanceConstants.sourceInstance + "/rest/api/3/issue/" + sourceIssueKey;
        restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);
        log.info("Updated description on source issue: {}", sourceIssueKey);

        log.info("Waiting for sync...");
        Thread.sleep(SYNC_WAIT_MS);

        IssueResponse sourceIssue = getSourceIssue(sourceIssueKey);
        IssueResponse targetIssue = getTargetIssue(targetIssueKey);

        AdfNode sourceDescription = sourceIssue.getFields().getDescription();
        AdfNode targetDescription = targetIssue.getFields().getDescription();

        log.info("Source description : {}", sourceDescription);
        log.info("Target description : {}", targetDescription);

        boolean descriptionMatches = sourceDescription != null
                && sourceDescription.equals(targetDescription);
        log.info("Description update match: {}", descriptionMatches);

        return descriptionMatches;
    }

    // ===================== ADF BUILDERS =====================

    private AdfNode buildDescription() {
        Doc doc = new Doc();
        doc.setType("doc");
        doc.setContent(new ArrayList<>(List.of(
                buildHeading("Automated Test Description", 1),
                buildParagraph("This is a plain text paragraph " + UUID.randomUUID()),
                buildRichTextParagraph(),
                buildBulletList(),
                buildOrderedList(),
                buildTable(),
                buildCodeBlock(),
                buildPanel(),
                buildBlockQuote(),
                buildRule())));
        return doc;
    }

    private Heading buildHeading(String text, int level) {
        Text textNode = new Text();
        textNode.setType("text");
        textNode.setText(text);

        Heading heading = new Heading();
        heading.setType("heading");
        heading.setAttrs(new Heading.HeadingAttrs(level, null));
        heading.setContent(new ArrayList<>(List.of(textNode)));
        return heading;
    }

    private Paragraph buildParagraph(String text) {
        Text textNode = new Text();
        textNode.setType("text");
        textNode.setText(text);

        Paragraph paragraph = new Paragraph();
        paragraph.setType("paragraph");
        paragraph.setContent(new ArrayList<>(List.of(textNode)));
        return paragraph;
    }

    private Paragraph buildRichTextParagraph() {
        Text boldText = new Text();
        boldText.setType("text");
        boldText.setText("Bold text ");
        Strong strong = new Strong();
        strong.setType("strong");
        boldText.setMarks(new ArrayList<>(List.of(strong)));

        Text italicText = new Text();
        italicText.setType("text");
        italicText.setText("Italic text ");
        Em em = new Em();
        em.setType("em");
        italicText.setMarks(new ArrayList<>(List.of(em)));

        Text underlineText = new Text();
        underlineText.setType("text");
        underlineText.setText("Underline text ");
        Underline underline = new Underline();
        underline.setType("underline");
        underlineText.setMarks(new ArrayList<>(List.of(underline)));

        Text strikeText = new Text();
        strikeText.setType("text");
        strikeText.setText("Strike text");
        Strike strike = new Strike();
        strike.setType("strike");
        strikeText.setMarks(new ArrayList<>(List.of(strike)));

        Paragraph paragraph = new Paragraph();
        paragraph.setType("paragraph");
        paragraph.setContent(new ArrayList<>(List.of(boldText, italicText, underlineText, strikeText)));
        return paragraph;
    }

    private BulletList buildBulletList() {
        BulletList bulletList = new BulletList();
        bulletList.setType("bulletList");
        bulletList.setContent(new ArrayList<>(List.of(
                buildListItem("Bullet item 1"),
                buildListItem("Bullet item 2"),
                buildListItem("Bullet item 3"))));
        return bulletList;
    }

    private OrderedList buildOrderedList() {
        OrderedList orderedList = new OrderedList();
        orderedList.setType("orderedList");
        orderedList.setAttrs(new OrderedList.OrderedListAttrs(1));
        orderedList.setContent(new ArrayList<>(List.of(
                buildListItem("Ordered item 1"),
                buildListItem("Ordered item 2"),
                buildListItem("Ordered item 3"))));
        return orderedList;
    }

    private ListItem buildListItem(String text) {
        ListItem listItem = new ListItem();
        listItem.setType("listItem");
        listItem.setContent(new ArrayList<>(List.of(buildParagraph(text))));
        return listItem;
    }

    private Table buildTable() {
        TableRow headerRow = new TableRow();
        headerRow.setType("tableRow");
        headerRow.setContent(new ArrayList<>(List.of(
                buildTableHeader("Header 1"),
                buildTableHeader("Header 2"),
                buildTableHeader("Header 3"))));

        TableRow dataRow = new TableRow();
        dataRow.setType("tableRow");
        dataRow.setContent(new ArrayList<>(List.of(
                buildTableCell("Cell 1"),
                buildTableCell("Cell 2"),
                buildTableCell("Cell 3"))));

        Table table = new Table();
        table.setType("table");
        table.setAttrs(new Table.TableAttrs(false, "default", null, null));
        table.setContent(new ArrayList<>(List.of(headerRow, dataRow)));
        return table;
    }

    private TableHeader buildTableHeader(String text) {
        TableHeader header = new TableHeader();
        header.setType("tableHeader");
        header.setAttrs(new TableHeader.TableHeaderAttrs(1, 1, null, null));
        header.setContent(new ArrayList<>(List.of(buildParagraph(text))));
        return header;
    }

    private TableCell buildTableCell(String text) {
        TableCell cell = new TableCell();
        cell.setType("tableCell");
        cell.setAttrs(new TableCell.TableCellAttrs(1, 1, null, null));
        cell.setContent(new ArrayList<>(List.of(buildParagraph(text))));
        return cell;
    }

    private CodeBlock buildCodeBlock() {
        Text codeText = new Text();
        codeText.setType("text");
        codeText.setText("System.out.println(\"Hello World\");");

        CodeBlock codeBlock = new CodeBlock();
        codeBlock.setType("codeBlock");
        codeBlock.setAttrs(new CodeBlock.CodeBlockAttrs("java"));
        codeBlock.setContent(new ArrayList<>(List.of(codeText)));
        return codeBlock;
    }

    private Panel buildPanel() {
        Panel panel = new Panel();
        panel.setType("panel");
        panel.setAttrs(new Panel.PanelAttrs("info"));
        panel.setContent(new ArrayList<>(List.of(buildParagraph("This is an info panel"))));
        return panel;
    }

    private BlockQuote buildBlockQuote() {
        BlockQuote blockQuote = new BlockQuote();
        blockQuote.setType("blockquote");
        blockQuote.setContent(new ArrayList<>(List.of(buildParagraph("This is a blockquote"))));
        return blockQuote;
    }

    private Rule buildRule() {
        Rule rule = new Rule();
        rule.setType("rule");
        return rule;
    }
}