package com.negocionaarea.mobile_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpenCageResponse {

    private List<Result> results;

    @Getter
    @Setter
    public static class Result {
        private Geometry geometry;
    }

    @Getter
    @Setter
    public static class Geometry {
        private Double lat;
        private Double lng;
    }
}
