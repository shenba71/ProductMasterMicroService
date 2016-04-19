package com.schawk.productmaster.feed.service;

import java.util.Map;

public interface ProductMasterStagingService {

    public String saveInputFeed(String inputFeed);
    
    public String saveProductMetaData(Map productMap) throws Exception;
    
    public String saveColorDatasToProductMetadata(Map valueMap, String styleNumber) throws Exception;

	String saveSizeDatasToProductMetadata(Map valueMap, String styleNumber, String colorNumber)
			throws Exception;

	public String updateColorDatasToProductMetadata(Map valueMap, String styleNumber,String colorNumber) throws Exception;
	
	public String updateSizeDatasToProductMetadata(Map valueMap, String styleNumber,String colorNumber, String sizeCode) throws Exception;
}
