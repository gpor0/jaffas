package com.github.gpor0.jaffas.context;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.HttpHeaders;
import java.util.List;
import java.util.Locale;

@RequestScoped
public class RequestScope {

    protected HttpHeaders headers;

    public String acceptableLanguage() {

        if (headers == null) {
            return null;
        }

        Locale acceptableLocale = acceptableLocale();

        return acceptableLocale == null ? null : acceptableLocale.getISO3Language();
    }

    public Locale acceptableLocale() {

        if (headers == null) {
            return null;
        }

        List<Locale> acceptableLanguages = headers.getAcceptableLanguages();

        return acceptableLanguages.stream().findFirst().orElse(null);
    }

    public void init(HttpHeaders headers) {
        this.headers = headers;
    }
}
