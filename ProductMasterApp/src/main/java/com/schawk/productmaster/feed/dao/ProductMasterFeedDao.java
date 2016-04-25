package com.schawk.productmaster.feed.dao;

import org.json.JSONArray;

public interface ProductMasterFeedDao {

    public String saveInputFeed(JSONArray jsonArray) throws Exception;

    public String searchSizeRecord(String styleNumber, String colorCode, String sizeCode)
            throws Exception;

    public String saveProductMetaDataStyle(String productMetaData) throws Exception;

    public String saveColorMetaData(String colorMetaData, String styleNumber) throws Exception;

    public String saveSizeMetaData(String sizeMetaData, String styleNumber, String colorNumber)
            throws Exception;

    public String updateColorMetaData(String colorMetaData, String styleNumber, String colorNumber)
            throws Exception;

    public String updateSizeMetaData(String sizeMetaData, String styleNumber, String colorNumber,
            String sizeCode) throws Exception;

    /**
     * Get product metadata based on style and color
     * @param styleNumber
     * @param colorNumber
     * @return
     * @throws Exception
     */
    public String findProductByStyleAndColor(String styleNumber, String colorNumber)
            throws Exception;

    public int getIndexForSize(String styleNumber, String colorCode, String sizeCode)
            throws Exception;

    /**
     * Retrieve product metadata based on style and include the specified
     * columns
     * @param styleNumber
     * @param field
     * @return
     * @throws Exception
     */
    public String findProductByStyle(String styleNumber, String[] field) throws Exception;

    public String updateProductMetaDataStyle(String productMetaData) throws Exception;

    /**
     * This is a refined search applicable only to specified fields and returns
     * the required columns from json
     * @param columnName
     * @param columnValues
     * @param columnsToInclude
     * @return
     * @throws Exception
     */
    public String findProductByFields(String columnName, String[] columnValues,
            String[] columnsToInclude) throws Exception;

    public String globalSearch(String searchField) throws Exception;

    /**
     * Retrieve product size based on style, color and size
     * @param styleNumber
     * @param colorCode
     * @param sizeCode
     * @return
     * @throws Exception
     */
    public String findProductByStyleColorAndSize(String styleNumber, String colorCode,
            String sizeCode) throws Exception;

    /**
     * Retrieve product sizes based on style, color and size
     * @param styleNumber
     * @param colorCode
     * @return
     * @throws Exception
     */
    public String findProductSizesByStyleAndColor(String styleNumber, String colorCode)
            throws Exception;

}
