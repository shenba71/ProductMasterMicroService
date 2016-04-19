package com.schawk.productmaster.feed.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    
    private String convertUpdatedString(Map valuMap){
    	Map<String,String> updatedMap = new HashMap<String, String>();
    	Set<String> keySet = valuMap.keySet();
    	for (String object : keySet) {
    		updatedMap.put("colors.$.color."+object, (String) valuMap.get(object));
		}
    	return convertMapToJson(updatedMap);
    	
    }

	@Override
	public String updateColorDatasToProductMetadata(Map valueMap,
			String styleNumber, String colorNumber) throws Exception {
		String updatedColorData = convertUpdatedString(valueMap);
		return productMasterFeedDao.updateColorMetaData(updatedColorData, styleNumber, colorNumber);
	}

	@Override
	public String updateSizeDatasToProductMetadata(Map valueMap,
			String styleNumber, String colorNumber, String sizeCode)
			throws Exception {
		String updatedSizeDatas = convertUpdatedString(valueMap);
		return productMasterFeedDao.updateSizeMetaData(updatedSizeDatas, styleNumber, colorNumber);
	}
    

}
