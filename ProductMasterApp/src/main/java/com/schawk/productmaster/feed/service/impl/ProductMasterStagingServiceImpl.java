package com.schawk.productmaster.feed.service.impl;

import java.util.Map;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schawk.productmaster.feed.dao.ProductMasterFeedDao;
import com.schawk.productmaster.feed.service.ProductMasterStagingService;

@Service
public class ProductMasterStagingServiceImpl implements ProductMasterStagingService {

    @Autowired
    private ProductMasterFeedDao productMasterFeedDao;

    @Override
    public String saveInputFeed(String inputFeed) {
        String response = null;
        try {
            // Converting input json to jsonArray
            JSONArray jsonArray = new JSONArray(inputFeed);
            response = productMasterFeedDao.saveInputFeed(jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    
    @Override
    public String saveProductMetaData(Map productMap) throws Exception{
    	String productInputJson = convertMapToJson(productMap);
    	System.out.println(productInputJson);
    	return productMasterFeedDao.saveProductMetaData(productInputJson);
    }
    
    @Override
    public String saveColorDatasToProductMetadata(Map valueMap,String styleNumber) throws Exception{
    	String colorMetadataJson = convertMapToJson(valueMap);
		return productMasterFeedDao.saveColorMetaData(colorMetadataJson, styleNumber);
   	
    }
    
    @Override
    public String saveSizeDatasToProductMetadata(Map valueMap,String styleNumber, String colorNumber) throws Exception{
    	String sizeMetadataJson = convertMapToJson(valueMap);
		return productMasterFeedDao.saveSizeMetaData(sizeMetadataJson, styleNumber, colorNumber);
   	
    }
    
    private String convertMapToJson(Map productMap){
    	String productMetaDataJson = null;
    	try{
    		 	ObjectMapper mapper = new ObjectMapper();
    	productMetaDataJson = mapper.writeValueAsString(productMap);
    	System.out.println(productMetaDataJson);
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	return productMetaDataJson;
    }
    

}
