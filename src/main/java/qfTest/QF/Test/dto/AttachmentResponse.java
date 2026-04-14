package qfTest.QF.Test.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttachmentResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("filename")
    private String filename;

    @JsonProperty("size")
    private Long size;

    @JsonProperty("mimeType")
    private String mimeType;

    @JsonProperty("content")
    private String contentUrl;  // used to resolve mediaId via redirect
}