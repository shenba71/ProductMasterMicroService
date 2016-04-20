package com.schawk.productmaster.feed.dao;

import java.util.List;

import org.json.JSONArray;

public interface ProductMasterFeedDao {

    public String saveInputFeed(JSONArray jsonArray) throws Exception;

    public String searchFeed(String style, String color, String size) throws Exception;

    public List<String> searchFeedByStyles(String[] styleNumbers) throws Exception;

    public String saveProductMetaDataStyle(String productMetaData) throws Exception;

    public String saveColorMetaData(String colorMetaData, String styleNumber) throws Exception;

    public String saveSizeMetaData(String sizeMetaData, String styleNumber, String colorNumber)
            throws Exception;

    public String updateColorMetaData(String colorMetaData, String styleNumber, String colorNumber)
            throws Exception;

    public String updateSizeMetaData(String sizeMetaData, String styleNumber, String colorNumber,
            String sizeCode) throws Exception;

    public int getIndexForSize(String styleNumber, String colorCode, String sizeCode)
            throws Exception;

    public String updateProductMetaDataStyle(String productMetaData) throws Exception;

}
