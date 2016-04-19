package com.schawk.productmaster.feed.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkUpdateRequestBuilder;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteRequestBuilder;
import com.mongodb.BulkWriteResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;
import com.schawk.productmaster.config.service.SpringMongoConfigService;
import com.schawk.productmaster.feed.dao.ProductMasterFeedDao;

@Repository
public class ProductMasterFeedDaoImpl implements ProductMasterFeedDao {

    @Autowired
    private SpringMongoConfigService springMongoConfigService;

    @Autowired
    private MongoProperties mongoProperties;

    private static final Logger LOG = LoggerFactory.getLogger(ProductMasterFeedDaoImpl.class);

    private static final String COLLECTION_NAME = "product_master";
    private static final String PRODUCT_STYLE = "styleNumber";
    private static final String PRODUCT_COLOR = "colors.color.colorCode";
    private static final String PRODUCT_SIZE = "Product size";

    private MongoTemplate mongoTemplate;

    @Override
    public String saveInputFeed(JSONArray inputFeed) {
        String response = null;
        try {
            mongoTemplate = springMongoConfigService.getMongoTemplate();
            DBCollection collection = mongoTemplate.getDb().getCollection(COLLECTION_NAME);

            // Get BulkWriteOperation by accessing the mongodb

            BulkWriteOperation bulkWriteOperation = collection.initializeUnorderedBulkOperation();

            // perform the upsert operation in the loop to add objects for bulk
            // execution
            for (int i = 0; i < inputFeed.length(); i++) {

                String product = inputFeed.getJSONObject(i).getString("Product style");
                String color = inputFeed.getJSONObject(i).getString("Product color");
                DBObject obj = (DBObject) JSON.parse(inputFeed.getJSONObject(i).toString());
                BasicDBObject object = new BasicDBObject();
                object.append("$set", obj);

                // get a bulkWriteRequestBuilder by issuing find on the
                // ProductStyle and Product color

                BulkWriteRequestBuilder bulkWriteRequestBuilder = bulkWriteOperation
                        .find(new BasicDBObject("Product style", product).append("Product color",
                                color));

                // get hold of upsert operation from bulkWriteRequestBuilder

                BulkUpdateRequestBuilder updateReq = bulkWriteRequestBuilder.upsert();
                updateReq.update(object);
            }
            // execute bulk operation on mycol collection
            LOG.debug("Started inserting the json Inputs");
            BulkWriteResult result = bulkWriteOperation.execute();
            LOG.debug("Number of inserted documents=====>" + result.getInsertedCount());
            LOG.debug("Number of updated documents=====>" + result.getModifiedCount());
            LOG.debug("Number of Matched documents=====>" + result.getMatchedCount());
            LOG.debug("Number of Upserted documents=====>"
                    + (result.getUpserts() == null ? 0 : result.getUpserts().size()));
            LOG.debug("Completed insertion of inputs..");
            response = "No Of Inserted Documents"
                    + (result.getUpserts() == null ? 0 : result.getUpserts().size()) + "   "
                    + "No Of Updated Documents" + result.getModifiedCount();
        } catch (Exception e) {
            e.printStackTrace();
            response = "Insertion Failed" + e.getMessage();
        }

        return response;
    }

    @Override
    public String searchFeed(String style, String color, String size) throws Exception {
        Query query = new Query();
        String results = null;
        query.addCriteria(Criteria.where(PRODUCT_STYLE).is(style));
        if (color != null && !color.isEmpty()) {
            query.addCriteria(Criteria.where(PRODUCT_COLOR).is(color));
        }
        if (size != null && !size.isEmpty()) {
            query.addCriteria(Criteria.where(PRODUCT_SIZE).is(size));
        }
        LOG.debug("Search Query" + query);
        mongoTemplate = springMongoConfigService.getMongoTemplate();
        List<String> searchResults = mongoTemplate.find(query, String.class, COLLECTION_NAME);
        if (CollectionUtils.isEmpty(searchResults) == false) {
            results = searchResults.toString();
        } else {
            results = "No Records Found";
        }
        return results;
    }

    @Override
    public List<String> searchFeedByStyles(String[] styleNumbers) throws Exception {
        LOG.debug("Search for multiple style numbers");
        Query query = new Query();
        query.addCriteria(Criteria.where(PRODUCT_STYLE).in(styleNumbers));
        System.out.println(query);
        mongoTemplate = springMongoConfigService.getMongoTemplate();
        List<String> searchResult = mongoTemplate.find(query, String.class, COLLECTION_NAME);
        return searchResult;
    }
    
    @Override
    public String saveProductMetaData(String productMetaData) throws Exception{
    		JSONObject productJson = new JSONObject(productMetaData);
    		System.out.println(productJson);
    		String styleNumber = productJson.getString("styleNumber");
    		System.out.println(styleNumber);
    	 mongoTemplate = springMongoConfigService.getMongoTemplate();
         DBCollection collection = mongoTemplate.getDb().getCollection(COLLECTION_NAME);
         DBObject obj = (DBObject) JSON.parse(productMetaData);
         BasicDBObject styleObject = new BasicDBObject("$set",obj);
         WriteResult res = collection.update(new BasicDBObject("styleNumber", styleNumber), styleObject, true, false);
         return res.toString();
         
    }
    
    @Override
    public String saveColorMetaData(String colorMetaData,String styleNumber) throws Exception{
    	String response = null;
    	 mongoTemplate = springMongoConfigService.getMongoTemplate();
         DBCollection collection = mongoTemplate.getDb().getCollection(COLLECTION_NAME);
         JSONObject productJson = new JSONObject(colorMetaData);
 		String colorNumber = productJson.getString("colorCode");
 		
         DBObject obj = (DBObject) JSON.parse(colorMetaData);
         BasicDBObject colorMetaDataObject = new BasicDBObject("color",obj);
         BasicDBObject styleObject = new BasicDBObject("styleNumber", styleNumber);
         BasicDBObject colorObject = new BasicDBObject("colors", colorMetaDataObject);
         BasicDBObject object = new BasicDBObject();
         object.append("$addToSet",colorObject ).append("$setOnInsert",styleObject);
         if(searchColorRecord(colorNumber,styleNumber).equalsIgnoreCase("NO COLOR RECORDS")){
         WriteResult res = collection.update(styleObject, object, true, false);
         response = res.toString();
         }
         else {
        	 response = "Record already exists";
         }
		 return response;    	
    }
    
    @Override
    public String saveSizeMetaData(String sizeMetaData, String styleNumber, String colorNumber)throws Exception{
    	mongoTemplate = springMongoConfigService.getMongoTemplate();
        DBCollection collection = mongoTemplate.getDb().getCollection(COLLECTION_NAME);
        DBObject obj = (DBObject) JSON.parse(sizeMetaData);
        BasicDBObject sizeMetaDataObject = new BasicDBObject("size",obj);
        BasicDBObject queryObject = new BasicDBObject("styleNumber", styleNumber).append("colors.color.colorCode", colorNumber);
        BasicDBObject sizeObject = new BasicDBObject("colors.$.color.sizes", sizeMetaDataObject);
        if(searchStyleRecord(styleNumber).equalsIgnoreCase("NO STYLE RECORDS") || searchColorRecord(colorNumber,styleNumber).equalsIgnoreCase("NO COLOR RECORDS") ){
        	BasicDBObject colorObject = new BasicDBObject("colorCode",colorNumber);
    		saveColorMetaData(colorObject.toString(),styleNumber);
        }
        BasicDBObject object = new BasicDBObject();
        object.append("$addToSet", sizeObject);
        WriteResult res = collection.update(queryObject, object, true, false);
		return res.toString();            
        
        
    }
    
    @Override
    public String updateColorMetaData(String colorMetaData, String styleNumber, String colorNumber)throws Exception{
    	mongoTemplate = springMongoConfigService.getMongoTemplate();
        DBCollection collection = mongoTemplate.getDb().getCollection(COLLECTION_NAME);
        DBObject obj = (DBObject) JSON.parse(colorMetaData);
        System.out.println(colorNumber+"------------"+styleNumber);
        BasicDBObject queryObject = new BasicDBObject("styleNumber", styleNumber).append("colors.color.colorCode", colorNumber);
       // BasicDBObject updateObject = new BasicDBObject("colors.$.color",obj);
        BasicDBObject colorObject = new BasicDBObject("$set",obj);
        WriteResult res = collection.update(queryObject, colorObject, true, false);
		return res.toString();    
    	
    }
    
    /*private BasicDBObject createProductDetailJson(String styleNumber,String colorNumber){
    	BasicDBObject productDetail = new BasicDBObject("styleNumber",styleNumber);
    	BasicDBObject colorMetaDataObject = new BasicDBObject("colorCode",colorNumber);
    	BasicDBObject obj = new BasicDBObject("color",colorMetaDataObject);
    	List colorList = new ArrayList<BasicDBObject>();
    	colorList.add(obj);
    	//BasicDBObject colorObject = new BasicDBObject("colors", colorList);
        productDetail.append("colors", colorList);
		return productDetail;
    }*/
    
    private String searchStyleRecord(String styleNumber) throws Exception{
    	Query query = new Query();
    	String results = null;
    	query.addCriteria(Criteria.where(PRODUCT_STYLE).is(styleNumber));
    	mongoTemplate = springMongoConfigService.getMongoTemplate();
    	List<String> searchResults = mongoTemplate.find(query, String.class, COLLECTION_NAME);
        if (CollectionUtils.isEmpty(searchResults) == false) {
            results = searchResults.toString();
        } else {
            results = "NO STYLE RECORDS";
        }
    	
        return results;
    }
    
    private String searchColorRecord(String colorNumber,String styleNumber) throws Exception{
    	Query query = new Query();
    	String results = null;
    	query.addCriteria(Criteria.where(PRODUCT_STYLE).is(styleNumber).and(PRODUCT_COLOR).is(colorNumber));
    	mongoTemplate = springMongoConfigService.getMongoTemplate();
    	List<String> searchResults = mongoTemplate.find(query, String.class, COLLECTION_NAME);
        if (CollectionUtils.isEmpty(searchResults) == false) {
            results = searchResults.toString();
        } else {
            results = "NO COLOR RECORDS";
        }
    	
        return results;
    }

	@Override
	public String updateSizeMetaData(String colorMetaData, String styleNumber,
			String colorNumber) throws Exception {
		return null;
	}

}
