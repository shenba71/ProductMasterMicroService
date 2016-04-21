package com.schawk.productmaster.feed.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.schawk.productmaster.feed.dao.ProductMasterFeedDao;
import com.schawk.productmaster.feed.dao.impl.ProductMasterFeedDaoImpl;
import com.schawk.productmaster.feed.service.ProductMasterSearchService;

import antlr.StringUtils;

@Service
public class ProductMasterSearchServiceImpl implements ProductMasterSearchService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductMasterFeedDaoImpl.class);
    @Autowired
    private ProductMasterFeedDao productMasterFeedDao;

    @Override
    public String searchProductDetails(String style, String color, String size) {
        String searchResult = null;
        try {
            LOG.debug("Search material with style" + style + "color===" + color + "size===" + size);
            searchResult = productMasterFeedDao.searchFeed(style, color, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    @Override
    public List<String> searchProductDetailsbyStyles(String[] styleNumbers) {
        List<String> searchResults = null;
        try {
            searchResults = productMasterFeedDao.searchFeedByStyles(styleNumbers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchResults;
    }

    /**
     * Search the product with the specified color
     */
    @Override
    public String searchProductUsingStyleAndColor(String styleNumber, String colorCode) {
        String searchResult = null;
        try {
            searchResult = productMasterFeedDao.searchFeedByStyleAndColor(styleNumber, colorCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    /**
     * Search the product details of the given styleNumber and fields that should be included in the query results.
     */
    @Override
    public String searchProductUsingStyle(String styleNumber, String[] field) {
        String searchResult = null;
        try {
            searchResult = productMasterFeedDao.searchFeedByStyle(styleNumber, field);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    /**
     * This is a refined search applicable only to specified fields
     */
    @Override
    public String searchProducts(String columnName, String[] columnValues,
            String[] columnsToInclude) {
        String searchResult = null;
        String colorCodePrefix = "colors.color.";
        try {
            if (columnName.equalsIgnoreCase("colorCode")
                    || columnName.equalsIgnoreCase("colorDescription")) {
                columnName = colorCodePrefix.concat(columnName);
            }
            searchResult = productMasterFeedDao.searchProducts(columnName, columnValues,
                    columnsToInclude);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    /**
     * This is a global search applicable only to specified fields which are given in text indexes
     * @param searchField
     * @throws Exception
     */
    @Override
    public String globalSearch(String searchField) throws Exception {
    	
    	if(searchField != null && searchField != ""){
    		return productMasterFeedDao.globalSearch(searchField);
    	} else {
    		LOG.debug("Search field is empty");
    		return "Please enter a text to search";
    	}       
    }
}
