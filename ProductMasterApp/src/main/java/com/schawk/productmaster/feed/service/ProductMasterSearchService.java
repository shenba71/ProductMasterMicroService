package com.schawk.productmaster.feed.service;

import java.util.List;

public interface ProductMasterSearchService {

    public String searchProductDetails(String styleNumber, String color, String size);

    public List<String> searchProductDetailsbyStyles(String[] styleNumbers);

    public String searchProductUsingStyleAndColor(String styleNumber, String colorNumber);

    public String searchProductUsingStyle(String styleNumber, String[] field);

    public String searchProducts(String columnName, String[] columnValues,
            String[] columnsToInclude);
    
    public String globalSearch(String searchField) throws Exception;
}
