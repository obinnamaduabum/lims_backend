package com.hertfordshire.dto.places;

public class StructuredFormattingDto {

    private String main_text_matched_substrings;
    private String secondary_text;
    private String main_text;

    public String getMain_text_matched_substrings() {
        return main_text_matched_substrings;
    }

    public void setMain_text_matched_substrings(String main_text_matched_substrings) {
        this.main_text_matched_substrings = main_text_matched_substrings;
    }

    public String getSecondary_text() {
        return secondary_text;
    }

    public void setSecondary_text(String secondary_text) {
        this.secondary_text = secondary_text;
    }

    public String getMain_text() {
        return main_text;
    }

    public void setMain_text(String main_text) {
        this.main_text = main_text;
    }
}
