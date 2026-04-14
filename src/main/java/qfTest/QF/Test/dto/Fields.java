package qfTest.QF.Test.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import qfTest.QF.Test.model.node.AdfNode;

@Data
@NoArgsConstructor
public class Fields {

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("project")
    private Project project;

    @JsonProperty("issuetype")
    private IssueType issueType;

    @JsonProperty("description")
    private AdfNode description;

    @JsonProperty("attachment")
    private List<Attachment> attachments;

    @JsonIgnore
    private final Map<String, Object> dynamicFields = new HashMap<>();

    @JsonIgnore
    private final List<String> replicaFieldKeys = new ArrayList<>();

    @JsonAnySetter
    public void captureField(String key, Object value) {
        this.dynamicFields.put(key, value);
    }

    public void configureReplicaKey(String... keys) {
        this.replicaFieldKeys.addAll(Arrays.asList(keys));
    }

    @JsonIgnore
    public String getSyncedIssueKey() {
        return replicaFieldKeys.stream()
                .filter(dynamicFields::containsKey)
                .map(dynamicFields::get)
                .filter(value -> value != null)
                .map(String::valueOf)
                .findFirst()
                .orElse(null);
    }
}