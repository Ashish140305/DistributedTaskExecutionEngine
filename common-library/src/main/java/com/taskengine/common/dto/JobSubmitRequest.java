package com.taskengine.common.dto;

import java.util.Map;

/**
 * REST API request for submitting a new job.
 */
public class JobSubmitRequest {

    private String name;
    private String type;
    private String inputData;
    private Map<String, String> parameters;

    public JobSubmitRequest() {
    }

    public JobSubmitRequest(String name, String type, String inputData, Map<String, String> parameters) {
        this.name = name;
        this.type = type;
        this.inputData = inputData;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "JobSubmitRequest{name='" + name + "', type='" + type + "'}";
    }
}
