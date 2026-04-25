package com.negocionaarea.mobile_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PhotonResponse {
    private List<Feature> features;

    @Getter
    @Setter
    public static class Feature {
        private Geometry geometry;
        private Properties properties;
    }

    @Getter
    @Setter
    public static class Geometry {
        private double[] coordinates;
    }

    @Getter
    @Setter
    public static class Properties {
        private String name;
        private String city;
        private String state;
        private String country;
    }


}
