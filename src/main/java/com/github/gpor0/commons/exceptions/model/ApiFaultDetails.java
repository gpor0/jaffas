package com.github.gpor0.commons.exceptions.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2020-07-30T13:21:25.217127+02:00[Europe/Ljubljana]")
public class ApiFaultDetails {

    private String faultCode;
    private Map<String, String> fields = new HashMap<>();

    /**
     *
     **/
    public ApiFaultDetails faultCode(String faultCode) {
        this.faultCode = faultCode;
        return this;
    }

    public String getFaultCode() {
        return faultCode;
    }

    public void setFaultCode(String faultCode) {
        this.faultCode = faultCode;
    }

    /**
     *
     **/
    public ApiFaultDetails fields(Map<String, String> fields) {
        this.fields = fields;
        return this;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiFaultDetails apiFaultDetails = (ApiFaultDetails) o;
        return Objects.equals(this.faultCode, apiFaultDetails.faultCode) &&
                Objects.equals(this.fields, apiFaultDetails.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(faultCode, fields);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ApiFaultDetails {\n");

        sb.append("    faultCode: ").append(toIndentedString(faultCode)).append("\n");
        sb.append("    fields: ").append(toIndentedString(fields)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }


}

