package com.schawk.productmaster.feed.service;

/**
 * 
 * @author sharanya.ramamoorthy
 *
 */
public interface ProductMasterSearchService {

    public String searchProductUsingStyleAndColor(String styleNumber, String colorNumber);

    public String searchProductUsingStyle(String styleNumber, String[] field);

    public String searchProducts(String columnName, String[] columnValues,
            String[] columnsToInclude);

    public String globalSearch(String searchField) throws Exception;

    public String searchProductSizeByStyleColor(String styleNumber, String colorCode, String size);

    public String searchProductSizesByStyleColor(String styleNumber, String colorCode);
}
