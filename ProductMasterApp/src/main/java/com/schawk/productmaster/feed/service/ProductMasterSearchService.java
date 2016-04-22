package com.schawk.productmaster.feed.service;

public interface ProductMasterSearchService {

	public String searchProductUsingStyleAndColor(String styleNumber,
			String colorNumber);

	public String searchProductUsingStyle(String styleNumber, String[] field);

	public String searchProducts(String columnName, String[] columnValues,
			String[] columnsToInclude);

	public String globalSearch(String searchField) throws Exception;
}
