package qfTest.QF.Test.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("key")
    private String key;           // e.g. "QF-123"

    @JsonProperty("fields")
    private Fields fields;
}