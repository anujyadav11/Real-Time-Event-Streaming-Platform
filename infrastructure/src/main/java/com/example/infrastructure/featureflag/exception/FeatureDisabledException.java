package com.example.infrastructure.featureflag.exception;

public class FeatureDisabledException
        extends RuntimeException {

    public FeatureDisabledException(String feature) {
        super("Feature '" + feature + "' is disabled.");
    }
}