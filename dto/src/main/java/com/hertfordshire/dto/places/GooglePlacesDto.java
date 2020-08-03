package com.hertfordshire.dto.places;

import java.util.List;

public class GooglePlacesDto {

    private String reference;
    private String[] types;
    private List<MatchedSubstringsDto> matched_substrings;
    private List<TermsDto> terms;
    private String description;
    private String id;
    private String place_id;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List<TermsDto> getTerms() {
        return terms;
    }

    public void setTerms(List<TermsDto> terms) {
        this.terms = terms;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public List<MatchedSubstringsDto> getMatched_substrings() {
        return matched_substrings;
    }

    public void setMatched_substrings(List<MatchedSubstringsDto> matched_substrings) {
        this.matched_substrings = matched_substrings;
    }
}
