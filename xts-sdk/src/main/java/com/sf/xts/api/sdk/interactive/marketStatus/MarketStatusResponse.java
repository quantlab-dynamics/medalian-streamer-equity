package com.sf.xts.api.sdk.interactive.marketStatus;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import org.apache.commons.lang.builder.ToStringBuilder;

public class MarketStatusResponse {

	@SerializedName("result")
    @Expose
    private Result result;
    
    @SerializedName("code")
    @Expose
    private String code;
    
    @SerializedName("description")
    @Expose
    private String description;
    
    @SerializedName("type")
    @Expose
    private String type;

    /**
     * it return result
     * @return String
     */
    public Result getResult() {
		return result;
	}

	/**
	 * it set result
	 * @param result String
	 */
	public void setResult(Result result) {
		this.result = result;
	}

	/**
	 * it return code
	 * @return String
	 */
	public String getCode() {
        return code;
    }

    /**
     * it set code
     * @param code String
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * it return description
     * @return String
     */
    public String getDescription() {
        return description;
    }

    /**
     * it set description
     * @param description String
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * it return type
     * @return String
     */
    public String getType() {
        return type;
    }

    /**
     * it set type
     * @param type String
     */
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("result", result).append("code", code).append("description", description).append("type", type).toString();
    }

}
