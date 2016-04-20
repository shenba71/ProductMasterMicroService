package com.schawk.productmaster.feed.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schawk.productmaster.feed.dao.ProductMasterFeedDao;
import com.schawk.productmaster.feed.service.ProductMasterStagingService;
import com.schawk.productmaster.web.rest.errors.CustomMongoException;

/**
 * @author shenbagaganesh.param
 *
 */
@Service
public class ProductMasterStagingServiceImpl implements ProductMasterStagingService {

    @Autowired
    private ProductMasterFeedDao productMasterFeedDao;

    private static final Logger LOG = LoggerFactory
            .getLogger(ProductMasterStagingServiceImpl.class);

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
    public String saveProductMetaData(Map<String, String> productMap)throws Exception{
        String productInputJson = convertMapToJson(productMap);
        LOG.debug("Request in JSON format.." + productInputJson);
        return productMasterFeedDao.saveProductMetaDataStyle(productInputJson);
    }
    
    @Override
    public String updateProductMetaData(Map<String, String> productMap) throws Exception {
        String productInputJson = convertMapToJson(productMap);
        LOG.debug("Request in JSON format.." + productInputJson);
        return productMasterFeedDao.updateProductMetaDataStyle(productInputJson);
    }

    @Override
    public String saveProductMetaData(String productJson) throws Exception {
        return productMasterFeedDao.saveProductMetaDataStyle(productJson);
    }

    @Override
    public String saveColorDatasToProductMetadata(Map<String, String> valueMap, String styleNumber)
            throws Exception {
        String colorMetadataJson = convertMapToJson(valueMap);
        LOG.debug("Request in JSON format.." + colorMetadataJson);
        return productMasterFeedDao.saveColorMetaData(colorMetadataJson, styleNumber);

    }

    @Override
    public String saveColorDatasToProductMetadata(String colorData, String styleNumber)
            throws Exception {
        return productMasterFeedDao.saveColorMetaData(colorData, styleNumber);

    }

    @Override
    public String saveSizeDatasToProductMetadata(Map<String, String> valueMap, String styleNumber,
            String colorNumber) throws Exception {
        String sizeMetadataJson = convertMapToJson(valueMap);
        LOG.debug("Request in JSON format.." + sizeMetadataJson);
        return productMasterFeedDao.saveSizeMetaData(sizeMetadataJson, styleNumber, colorNumber);

    }

    @Override
    public String saveSizeDatasToProductMetadata(String sizeDataJson, String styleNumber,
            String colorNumber) throws Exception {
        return productMasterFeedDao.saveSizeMetaData(sizeDataJson, styleNumber, colorNumber);

    }

    /**
     * @param productMap
     * @return the input map recieved as JSON
     */
    private String convertMapToJson(Map<String, String> productMap) {
        String productMetaDataJson = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            productMetaDataJson = mapper.writeValueAsString(productMap);
            System.out.println(productMetaDataJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productMetaDataJson;
    }

    private String convertMapToJsonForUpdate(Map<String, String> valuMap, String type, int pos) {
        Map<String, String> updatedMap = new HashMap<String, String>();
        Set<String> keySet = valuMap.keySet();
        for (String object : keySet) {
            if (type.equalsIgnoreCase("color")) {
                updatedMap.put("colors.$.color." + object, (String) valuMap.get(object));
            } else {
                updatedMap.put("colors.$.color.sizes." + pos + ".size." + object,
                        (String) valuMap.get(object));
            }
        }
        return convertMapToJson(updatedMap);

    }

    @Override
    public String updateColorDatasToProductMetadata(Map<String, String> valueMap,
            String styleNumber, String colorNumber) throws Exception {
        String type = "color";
        int pos = 0;
        String updatedColorData = convertMapToJsonForUpdate(valueMap, type, pos);
        LOG.debug("Update Request in JSON format.." + updatedColorData);
        return productMasterFeedDao.updateColorMetaData(updatedColorData, styleNumber, colorNumber);
    }

    @Override
    public String updateSizeDatasToProductMetadata(Map<String, String> valueMap,
            String styleNumber, String colorNumber, String sizeCode) throws Exception {
        String type = "size";
        int pos = productMasterFeedDao.getIndexForSize(styleNumber, colorNumber, sizeCode);
        String updatedSizeDatas = convertMapToJsonForUpdate(valueMap, type, pos);
        LOG.debug("Update Request in JSON format.." + updatedSizeDatas);
        return productMasterFeedDao.updateSizeMetaData(updatedSizeDatas, styleNumber, colorNumber,
                sizeCode);
    }

}
