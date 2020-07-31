package com.github.gpor0.commons.exceptions.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2020-07-30T13:21:25.217127+02:00[Europe/Ljubljana]")
public class ApiFaultRequest {

    private String rUid;
    private String sourceIp;
    private String method;
    private String uri;
    private Map<String, String> headers = new HashMap<>();

    /**
     *
     **/
    public ApiFaultRequest rUid(String rUid) {
        this.rUid = rUid;
        return this;
    }

    public String getrUid() {
        return rUid;
    }

    public void setrUid(String rUid) {
        this.rUid = rUid;
    }

    /**
     *
     **/
    public ApiFaultRequest sourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
        return this;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    /**
     *
     **/
    public ApiFaultRequest method(String method) {
        this.method = method;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    /**
     *
     **/
    public ApiFaultRequest uri(String uri) {
        this.uri = uri;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     *
     **/
    public ApiFaultRequest headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiFaultRequest apiFaultRequest = (ApiFaultRequest) o;
        return Objects.equals(this.rUid, apiFaultRequest.rUid) &&
                Objects.equals(this.sourceIp, apiFaultRequest.sourceIp) &&
                Objects.equals(this.method, apiFaultRequest.method) &&
                Objects.equals(this.uri, apiFaultRequest.uri) &&
                Objects.equals(this.headers, apiFaultRequest.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rUid, sourceIp, method, uri, headers);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ApiFaultRequest {\n");

        sb.append("    rUid: ").append(toIndentedString(rUid)).append("\n");
        sb.append("    sourceIp: ").append(toIndentedString(sourceIp)).append("\n");
        sb.append("    method: ").append(toIndentedString(method)).append("\n");
        sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
        sb.append("    headers: ").append(toIndentedString(headers)).append("\n");
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

