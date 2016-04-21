package com.schawk.productmaster.feed.rest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.schawk.productmaster.feed.service.ProductMasterSearchService;
import com.schawk.productmaster.feed.service.ProductMasterStagingService;

@RestController
@RequestMapping("/product")
public class ProductMasterController {

    @Autowired
    private ProductMasterStagingService productMasterStagingService;

    @Autowired
    private ProductMasterSearchService productMasterSearchservice;

    private static final Logger LOG = LoggerFactory.getLogger(ProductMasterController.class);

    @RequestMapping(method = RequestMethod.POST)
    public String saveInputfeed(@RequestBody String inputFeed) {
        LOG.debug("JSONRequest recieved!!!");
        return productMasterStagingService.saveInputFeed(inputFeed);
    }

    @RequestMapping(value = "/styles", method = RequestMethod.POST)
    public String saveProductFeed(@RequestParam Map<String, String> params,
            @RequestParam(value = "style_number", required = true) String styleNumber,
            @RequestParam(value = "product_name", required = false) String productName,
            @RequestParam(value = "product_type", required = false) String productType,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "product_description", required = false) String productDescription,
            @RequestParam(value = "division", required = false) String division,
            @RequestParam(value = "vendor", required = false) String vendor) throws Exception {
        Map<String, String> productMetaData = new HashMap<String, String>();
        productMetaData.put("styleNumber", styleNumber);
        if (productName != null && !productName.isEmpty()) {
            productMetaData.put("productName", productName);
        }
        if (productType != null && !productType.isEmpty()) {
            productMetaData.put("productType", productType);
        }
        if (category != null && !category.isEmpty()) {
            productMetaData.put("catagory", category);
        }
        if (gender != null && !gender.isEmpty()) {
            productMetaData.put("gender", gender);
        }
        if (productDescription != null && !productDescription.isEmpty()) {
            productMetaData.put("productDescription", productDescription);
        }
        if (division != null && !division.isEmpty()) {
            productMetaData.put("division", division);
        }
        if (vendor != null && !vendor.isEmpty()) {
            productMetaData.put("vendor", vendor);
        }

        return productMasterStagingService.saveProductMetaData(productMetaData);

    }

    @RequestMapping(value = "/styles/{styleNumber}/colors", method = RequestMethod.POST)
    public String saveProductMetaDataColor(@PathVariable("styleNumber") String styleNumber,
            @RequestParam(value = "color_code", required = true) String colorCode,
            @RequestParam(value = "color_description", required = false) String colorDescription)
                    throws Exception {
        Map<String, String> colorMetaData = new HashMap<String, String>();
        if (colorCode != null && !colorCode.isEmpty()) {
            colorMetaData.put("colorCode", colorCode);
        }
        if (colorDescription != null && !colorDescription.isEmpty()) {
            colorMetaData.put("colorDescription", colorDescription);
        }
        return productMasterStagingService.saveColorDatasToProductMetadata(colorMetaData,
                styleNumber);

    }

    @RequestMapping(value = "/styles/{styleNumber}/colors/{colorNumber}", method = RequestMethod.PUT)
    public String updateProductMetaDataColor(@PathVariable("styleNumber") String styleNumber,
            @PathVariable("colorNumber") String colorNumber,
            @RequestParam(value = "color_description", required = false) String colorDescription,
            @RequestParam(value = "color_value", required = false) String colorValue)
                    throws Exception {
        Map<String, String> colorMetaData = new HashMap<String, String>();
        if (colorDescription != null && !colorDescription.isEmpty()) {
            colorMetaData.put("colorDescription", colorDescription);
        }
        if (colorValue != null && !colorValue.isEmpty()) {
            colorMetaData.put("colorValue", colorValue);
        }
        return productMasterStagingService.updateColorDatasToProductMetadata(colorMetaData,
                styleNumber, colorNumber);

    }

    /* @RequestMapping(value = "/styles/{styleNumber}/colors/{colorNumber}/sizes/{sizeCode}",method = RequestMethod.PUT)
    public String updateProductMetaDataSize(@PathVariable("styleNumber") String styleNumber,
    		@PathVariable("colorNumber") String colorNumber,
    		@PathVariable("sizeCode") String sizeCode,
    		@RequestParam(value="upc",required=false) String upc,
    		@RequestParam(value="sku_id",required=false) String skuId) throws Exception{
    		Map<String,String> sizeMetaData = new HashMap<String,String>();
    		if(upc!=null && !upc.isEmpty()){
    		sizeMetaData.put("upc", upc);
    		}if(skuId!=null && !skuId.isEmpty()){
    			sizeMetaData.put("skuId", skuId);
        		}
    				return productMasterStagingService.updateColorDatasToProductMetadata(colorMetaData,styleNumber,colorNumber);
    	
    	
    }*/

    @RequestMapping(value = "/styles/{styleNumber}/colors/{colorNumber}/sizes")
    public String saveProductMetaDataSize(@PathVariable("styleNumber") String styleNumber,
            @PathVariable("colorNumber") String colorNumber,
            @RequestParam(value = "size_code", required = true) String sizeCode,
            @RequestParam(value = "upc", required = false) String upc,
            @RequestParam(value = "sku_id", required = false) String skuId) throws Exception {
        Map<String, String> sizeMetaData = new HashMap<String, String>();
        sizeMetaData.put("sizeCode", sizeCode);
        sizeMetaData.put("upc", upc);
        sizeMetaData.put("skuId", skuId);
        return productMasterStagingService.saveSizeDatasToProductMetadata(sizeMetaData, styleNumber,
                colorNumber);
    }

    @RequestMapping(value = "/style/{styleNumber}", method = RequestMethod.GET, produces = "application/json")
    public String getInputJsonFeed(@PathVariable("styleNumber") String styleNumber,
            @RequestParam(value = "color", required = false) String color,
            @RequestParam(value = "size", required = false) String size) {

        LOG.debug("Controller for search");
        String searchResult = productMasterSearchservice.searchProductDetails(styleNumber, color,
                size);
        LOG.debug("Response :" + searchResult);
        return searchResult;
    }

    /**
     * Search the product with the specified color
     * Example Input : /styles/12345/colors/000
     * @param styleNumber
     * @param colorCode
     * @return
     */
    @RequestMapping(value = "/styles/{styleNumber}/colors/{colorCode}", method = RequestMethod.GET)
    public String searchProductUsingStyleAndColor(@PathVariable("styleNumber") String styleNumber,
            @PathVariable("colorCode") String colorCode) {
        LOG.debug("StyleNumber : " + styleNumber + " colorCode : " + colorCode);
        return productMasterSearchservice.searchProductUsingStyleAndColor(styleNumber, colorCode);
    }

    /**
     * Search the product details of the given styleNumber and fields that should be included in the query results.
     * Example Input : /styles/12345?include=styleNumber,productName,colors
     * Example Input : /styles/12345
     * @param styleNumber
     * @param fieldsToDisplay
     * @return
     */
    @RequestMapping(value = "/styles/{styleNumber}", method = RequestMethod.GET)
    public String searchProductUsingStyle(@PathVariable("styleNumber") String styleNumber,
            @RequestParam(value = "include", required = false) String fieldsToDisplay) {
        LOG.debug("StyleNumber : " + styleNumber + " Fields to include : " + fieldsToDisplay);

        String[] fieldsToInclude = null;
        if (StringUtils.isNotEmpty(fieldsToDisplay)) {
            fieldsToInclude = fieldsToDisplay.split(",");
        }
        return productMasterSearchservice.searchProductUsingStyle(styleNumber, fieldsToInclude);
    }

    /**
     * This is a q search which can be used by two ways
     * 		a) Refind search for specified fields
     * 		 	Example Input : /styles?q={styleNumber=12345,12346}&include=styleNumber,colors
     * 		b) Global search for specified fields which are mentioned in text indexes
     * 			Example Input : /styles?q=FOOTWEAR
     * @param globalSearchFields
     * @param fieldsToInclude
     * @return
     * @throws Exception 
     */
    @RequestMapping(value = "/styles", method = RequestMethod.GET)
    public String searchProducts(
            @RequestParam(value = "q", required = false) String globalSearchFields,
            @RequestParam(value = "include", required = false) String fieldsToInclude) throws Exception {
        LOG.debug("Query field : " + globalSearchFields + " Fields to include : " + fieldsToInclude);
        
        if (globalSearchFields.startsWith("{")) {
        	globalSearchFields = globalSearchFields.replaceAll("(\\{|\\})", "");
            String[] searchFields = globalSearchFields.split("=");

            String columnName = searchFields[0];

            String[] columnValues = null;
            if (StringUtils.isNotEmpty(searchFields[1])) {
                columnValues = searchFields[1].split(",");
            }

            String[] columnsToInclude = null;
            if (StringUtils.isNotEmpty(fieldsToInclude)) {
                columnsToInclude = fieldsToInclude.split(",");
            }
            return productMasterSearchservice.searchProducts(columnName, columnValues,
                    columnsToInclude);
        } else {
        	// global search is case insensitive
            return productMasterSearchservice.globalSearch(globalSearchFields);
        }
      
    }

    
    /* @RequestMapping(value = "/styles", method = RequestMethod.POST)
    public String searchProducts(@RequestBody String styleNumbers) {
        LOG.debug("Search Multiple Styles...");
        String response = null;
        List<String> searchResults = productMasterSearchservice
                .searchProductDetailsbyStyles(styleNumbers.split(","));
        if (CollectionUtils.isEmpty(searchResults)) {
            response = "Styles Not Found";
    
        } else {
            response = searchResults.toString();
        }
    
        return response;
    }*/

    /*private Map getRequestParametersMap(Map requestMap){
    	
    	Map<String,String> updatedMap = new HashMap<String, String>();
    	Set<String> keySet = requestMap.keySet();
    	for (String object : keySet) {
    		updatedMap.put(", (String) valuMap.get(object));
    	}*/

}
