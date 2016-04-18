package com.schawk.productmaster.feed.rest.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
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
    public String saveProductFeed(@RequestParam(value = "style_number",required = true) String styleNumber,
            @RequestParam(value="product_name",required = false)String productName,
            @RequestParam(value="product_type",required = false)String productType,
            @RequestParam(value="category",required = false)String catagory,
            @RequestParam(value="gender",required = false)String gender,
            @RequestParam(value="product_description",required = false)String productDescription,
            @RequestParam(value="division",required = false)String division,
            @RequestParam(value="vendor",required = false)String vendor) throws Exception{
                Map<String,String> productMetaData = new HashMap<String,String>();
                productMetaData.put("styleNumber",styleNumber);
                if(productName!=null && !productName.isEmpty()){
                	productMetaData.put("productName",productName);
                }if(productType!=null &&!productType.isEmpty()){
                	 productMetaData.put("productType",productType);
                }if(catagory!=null &&!catagory.isEmpty()){
                	productMetaData.put("catagory",catagory);
                }if(gender!=null &&!gender.isEmpty()){
                	productMetaData.put("gender",gender);
                }if(productDescription!=null &&!productDescription.isEmpty()){
                	productMetaData.put("productDescription",productDescription);
                }if(division!=null &&!division.isEmpty()){
                	productMetaData.put("division", division);
                }if(vendor!=null &&!vendor.isEmpty()){
                	productMetaData.put("vendor",vendor);
                }    
                
                return productMasterStagingService.saveProductMetaData(productMetaData);
                
        
    }

    @RequestMapping(value = "/styles/{styleNumber}/colors",method = RequestMethod.POST)
    public String saveProductMetaDataColor(@PathVariable("styleNumber") String styleNumber,
    		@RequestParam(value="color_code",required=true) String colorCode,
    		@RequestParam(value="color_description",required=false) String colorDescription) throws Exception{
    		Map<String,String> colorMetaData = new HashMap<String,String>();
    		colorMetaData.put("colorCode", colorCode);
    		colorMetaData.put("colorDescription", colorDescription);
    				return productMasterStagingService.saveColorDatasToProductMetadata(colorMetaData,styleNumber);
    	
    	
    }
    
    @RequestMapping(value = "/styles/{styleNumber}/colors/{colorNumber}/sizes")
    public String saveProductMetaDataSize(@PathVariable("styleNumber") String styleNumber,
    		@PathVariable("colorNumber") String colorNumber,
    		@RequestParam(value="size_code",required=true) String sizeCode,
    		@RequestParam(value="upc",required = false) String upc,
    		@RequestParam(value="sku_id",required=false) String skuId) throws Exception{
    	 Map<String,String> sizeMetaData = new HashMap<String,String>();
    	 sizeMetaData.put("sizeCode", sizeCode);
    	 sizeMetaData.put("upc", upc);
    	 sizeMetaData.put("skuId",skuId);
    	return productMasterStagingService.saveSizeDatasToProductMetadata(sizeMetaData, styleNumber, colorNumber);
    }
    
    @RequestMapping(value = "/style/{styleNumber}", method = RequestMethod.GET)
    public String getInputJsonFeed(@PathVariable("styleNumber") String styleNumber,
            @RequestParam(value = "color", required = false) String color,
            @RequestParam(value = "size", required = false) String size) {

        LOG.debug("Controller for search");
        String searchResult = productMasterSearchservice.searchProductDetails(styleNumber, color,
                size);
        LOG.debug("Response :" + searchResult);
        return searchResult;
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

}
