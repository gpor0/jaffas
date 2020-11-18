package com.github.gpor0.jaffas.exceptions.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2020-07-30T13:21:25.217127+02:00[Europe/Ljubljana]")
public class ApiFault {

    private ApiFaultRequest request;
    private List<ApiFaultDetails> details = new ArrayList<>();
    private String debug;

    /**
     *
     **/
    public ApiFault request(ApiFaultRequest request) {
        this.request = request;
        return this;
    }

    public ApiFaultRequest getRequest() {
        return request;
    }

    public void setRequest(ApiFaultRequest request) {
        this.request = request;
    }

    /**
     *
     **/
    public ApiFault details(List<ApiFaultDetails> details) {
        this.details = details;
        return this;
    }

    public List<ApiFaultDetails> getDetails() {
        return details;
    }

    public void setDetails(List<ApiFaultDetails> details) {
        this.details = details;
    }

    /**
     * For debug purpose only, omitted on PROD env.
     **/
    public ApiFault debug(String debug) {
        this.debug = debug;
        return this;
    }

    public String getDebug() {
        return debug;
    }

    public void setDebug(String debug) {
        this.debug = debug;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiFault apiFault = (ApiFault) o;
        return Objects.equals(this.request, apiFault.request) &&
                Objects.equals(this.details, apiFault.details) &&
                Objects.equals(this.debug, apiFault.debug);
    }

    @Override
    public int hashCode() {
        return Objects.hash(request, details, debug);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ApiFault {\n");

        sb.append("    request: ").append(toIndentedString(request)).append("\n");
        sb.append("    details: ").append(toIndentedString(details)).append("\n");
        sb.append("    debug: ").append(toIndentedString(debug)).append("\n");
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

