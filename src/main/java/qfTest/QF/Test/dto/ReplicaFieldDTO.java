package qfTest.QF.Test.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplicaFieldDTO {
    
    @JsonProperty("customfield_10419")
    private String replicaFieldId;
}
