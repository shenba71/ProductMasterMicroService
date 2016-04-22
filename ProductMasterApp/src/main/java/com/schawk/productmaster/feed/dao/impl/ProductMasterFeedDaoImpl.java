package com.schawk.productmaster.feed.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;
import com.schawk.productmaster.config.service.SpringMongoConfigService;
import com.schawk.productmaster.feed.dao.ProductMasterFeedDao;
import com.schawk.productmaster.web.rest.errors.CustomMongoException;

/**
 * @author shenbagaganesh.param
 *
 */
@Repository
public class ProductMasterFeedDaoImpl implements ProductMasterFeedDao {

    @Autowired
    private SpringMongoConfigService springMongoConfigService;

    @Autowired
    MongoTemplate mongoTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(ProductMasterFeedDaoImpl.class);

    private static final String COLLECTION_NAME = "product_master";
    private static final String PRODUCT_STYLE = "styleNumber";
    private static final String PRODUCT_COLOR = "colors.color.colorCode";
    private static final String PRODUCT_SIZE = "colors.color.sizes.size.sizeCode";

    /*private MongoTemplate mongoTemplate;*/

    @Override
    public String saveInputFeed(JSONArray inputFeed) {
        String response = null;
        try {
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
    public List<String> searchFeedByStyles(String[] styleNumbers) throws Exception {
        LOG.debug("Search for multiple style numbers");
        Query query = new Query();
        query.addCriteria(Criteria.where(PRODUCT_STYLE).in(styleNumbers));
        System.out.println(query);
        List<String> searchResult = mongoTemplate.find(query, String.class, COLLECTION_NAME);
        return searchResult;
    }

    /* (non-Javadoc)
     * @see com.schawk.productmaster.feed.dao.ProductMasterFeedDao#saveProductMetaData(java.lang.String)
     * 
     * recieves the input metadata for style and inserts in to Mongo
     */
    @Override
    public String saveProductMetaDataStyle(String productMetaData)throws Exception{
        String response = null;
        String styleNumber = null;
        try{
            LOG.debug("Inside saveProductMetaDataStyle");
        JSONObject productJson = new JSONObject(productMetaData);
        styleNumber = productJson.getString("styleNumber");
        DBCollection collection = mongoTemplate.getDb().getCollection(COLLECTION_NAME);
        DBObject styleObject = (DBObject) JSON.parse(productMetaData);
        collection.insert(styleObject);
        response = searchStyleRecord(styleNumber);
        }
        catch(MongoException mongoException){
            response = "Exception while Inserting";
            throw new CustomMongoException(styleNumber, mongoException.getCode(),mongoException.getMessage());
        } 
       
        LOG.debug(response);
        return response;

    }
    
    @Override
    public String updateProductMetaDataStyle(String productMetaData) throws Exception {
        LOG.debug("Inside Update Style datas");
        JSONObject productJson = new JSONObject(productMetaData);
        String styleNumber = productJson.getString("styleNumber");
        DBCollection collection = mongoTemplate.getDb().getCollection(COLLECTION_NAME);
        DBObject obj = (DBObject) JSON.parse(productMetaData);
        BasicDBObject styleObject = new BasicDBObject("$set", obj);
        WriteResult res = collection.update(new BasicDBObject("styleNumber", styleNumber),
                styleObject, true, false);
        return searchStyleRecord(styleNumber);

    }

    /* (non-Javadoc)
     * @see com.schawk.productmaster.feed.dao.ProductMasterFeedDao#saveColorMetaData(java.lang.String, java.lang.String)
     * 
     * recieves input colormetadata json and inserts into mongodb
     */
    @Override
    public String saveColorMetaData(String colorMetaData, String styleNumber) throws Exception {
        String response = null;
        LOG.debug("Inside save color Meta data values");
        DBCollection collection = mongoTemplate.getDb().getCollection(COLLECTION_NAME);
        JSONObject productJson = new JSONObject(colorMetaData);
        String colorNumber = productJson.getString("colorCode");
        DBObject obj = (DBObject) JSON.parse(colorMetaData);
        BasicDBObject colorMetaDataObject = new BasicDBObject("color", obj);
        BasicDBObject styleObject = new BasicDBObject("styleNumber", styleNumber);
        BasicDBObject colorObject = new BasicDBObject("colors", colorMetaDataObject);
        BasicDBObject object = new BasicDBObject();
        object.append("$addToSet", colorObject).append("$setOnInsert", styleObject);
        if (searchColorRecord(colorNumber, styleNumber).equalsIgnoreCase("NO COLOR RECORDS")) {
            collection.update(styleObject, object, true, false);
            response = searchStyleRecord(styleNumber);
        } else {
            response = "Record already exists";
        }
        return response;
    }

    /* (non-Javadoc)
     * @see com.schawk.productmaster.feed.dao.ProductMasterFeedDao#saveSizeMetaData(java.lang.String, java.lang.String, java.lang.String)
     * 
     * recieves size metadata as json and inserts into mongodb
     */
    @Override
    public String saveSizeMetaData(String sizeMetaData, String styleNumber, String colorNumber)
            throws Exception {
        String response = null;
        LOG.debug("Save Size metedata values");
        DBCollection collection = mongoTemplate.getDb().getCollection(COLLECTION_NAME);
        JSONObject objects = new JSONObject(sizeMetaData);
        String sizeCode = objects.getString("sizeCode");
        DBObject obj = (DBObject) JSON.parse(sizeMetaData);
        BasicDBObject sizeMetaDataObject = new BasicDBObject("size", obj);
        BasicDBObject queryObject = new BasicDBObject("styleNumber", styleNumber).append(
                "colors.color.colorCode", colorNumber);
        BasicDBObject sizeObject = new BasicDBObject("colors.$.color.sizes", sizeMetaDataObject);
        if (searchStyleRecord(styleNumber).equalsIgnoreCase("NO STYLE RECORDS")
                || searchColorRecord(colorNumber, styleNumber).equalsIgnoreCase("NO COLOR RECORDS")) {
            BasicDBObject colorObject = new BasicDBObject("colorCode", colorNumber);
            saveColorMetaData(colorObject.toString(), styleNumber);
        }
        BasicDBObject object = new BasicDBObject();
        object.append("$addToSet", sizeObject);
        if (searchSizeRecord(styleNumber, colorNumber, sizeCode)
                .equalsIgnoreCase("NO SIZE RECORDS")) {
            collection.update(queryObject, object, true, false);
            response = searchStyleRecord(styleNumber);
            ;
        } else {
            response = "SIZE RECORD ALREADY EXISTS";
        }

        return response;

    }

    @Override
    public String updateColorMetaData(String colorMetaData, String styleNumber, String colorNumber)
            throws Exception {
        DBCollection collection = mongoTemplate.getDb().getCollection(COLLECTION_NAME);
        DBObject obj = (DBObject) JSON.parse(colorMetaData);
        System.out.println(colorNumber + "------------" + styleNumber);
        BasicDBObject queryObject = new BasicDBObject("styleNumber", styleNumber).append(
                "colors.color.colorCode", colorNumber);
        BasicDBObject colorObject = new BasicDBObject("$set", obj);
        WriteResult res = collection.update(queryObject, colorObject, true, false);
        return res.toString();

    }

    private String searchStyleRecord(String styleNumber) throws Exception {
        Query query = new Query();
        String results = null;
        query.addCriteria(Criteria.where(PRODUCT_STYLE).is(styleNumber));
        List<String> searchResults = mongoTemplate.find(query, String.class, COLLECTION_NAME);
        if (CollectionUtils.isEmpty(searchResults) == false) {
            results = searchResults.toString();
        } else {
            results = "NO STYLE RECORDS";
        }

        return results;
    }

    private String searchColorRecord(String colorNumber, String styleNumber) throws Exception {
        Query query = new Query();
        String results = null;
        query.addCriteria(
                Criteria.where(PRODUCT_STYLE).is(styleNumber).and(PRODUCT_COLOR).is(colorNumber))
                .fields().include("colors.$.color.sizes");
        String searchResults = mongoTemplate.findOne(query, String.class, COLLECTION_NAME);
        if (searchResults != null && searchResults.isEmpty() == false) {
            results = searchResults.toString();

        } else {
            results = "NO COLOR RECORDS";
        }

        return results;
    }

    private String searchSizeRecord(String styleNumber, String colorCode, String sizeCode)
            throws Exception {
        Query query = new Query();
        String results = null;
        query.addCriteria(Criteria.where(PRODUCT_STYLE).is(styleNumber).and(PRODUCT_COLOR)
                .is(colorCode));
        query.addCriteria(Criteria.where(PRODUCT_SIZE).is(sizeCode));
        String searchResults = mongoTemplate.findOne(query, String.class, COLLECTION_NAME);
        if (searchResults != null && searchResults.isEmpty() == false) {
            results = searchResults.toString();

        } else {
            results = "NO SIZE RECORDS";
        }

        return results;
    }

    @Override
    public String updateSizeMetaData(String sizeMetaData, String styleNumber, String colorNumber,
            String sizeCode) throws Exception {
        DBCollection collection = mongoTemplate.getDb().getCollection(COLLECTION_NAME);
        DBObject obj = (DBObject) JSON.parse(sizeMetaData);

        BasicDBObject queryObject = new BasicDBObject("styleNumber", styleNumber).append(
                "colors.color.colorCode", colorNumber);
        BasicDBObject colorObject = new BasicDBObject("$set", obj);
        WriteResult res = collection.update(queryObject, colorObject, true, false);
        return res.toString();

    }

	@Override
    public int getIndexForSize(String styleNumber, String colorCode, String sizeCode)
            throws Exception {
        String result = searchColorRecord(colorCode, styleNumber);
        JSONObject productJson = new JSONObject(result);
        System.out.println(productJson.toString());
        JSONArray sizeArray = productJson.getJSONArray("colors").getJSONObject(0)
                .getJSONObject("color").getJSONArray("sizes");
        int position = 0;
        for (int i = 0; i < sizeArray.length(); i++) {
            JSONObject objects = sizeArray.getJSONObject(i);
            if (objects.getJSONObject("size").getString("sizeCode").equalsIgnoreCase(sizeCode)) {
                position = i;
            }

        }
        return position;

    }

    @Override
    public String searchFeed(String style, String color, String size) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * This is a refined search applicable only to specified fields and returns the required columns from json
     * @param columnName
     * @param columnValues
     * @param columnsToInclude
     * @return
     * @throws Exception
     */
     @Override
     public String searchProducts(String columnName, String[] columnValues,
             String[] columnsToInclude) throws Exception {
         LOG.debug("Search for multiple style numbers and display specified columns");
         String results = null;
         Query query = new Query();
         query.addCriteria(Criteria.where(columnName).in(columnValues));

         if (columnsToInclude != null) {
             for (String FieldName : columnsToInclude) {
                 query.fields().include(FieldName);
             }
         }
         LOG.debug("Query : " + query);
         mongoTemplate = springMongoConfigService.getMongoTemplate();
         List<String> searchResults = mongoTemplate.find(query, String.class, COLLECTION_NAME);

         if (CollectionUtils.isEmpty(searchResults) == false) {
             results = searchResults.toString();
         } else {
             results = "NO RECORDS FOUND";
         }
         return results;
     }

     /**
      * Get product metadata based on style and color
      * @param styleNumber
      * @param colorNumber
      * @return
      * @throws Exception
      */
     @Override
     public String searchFeedByStyleAndColor(String styleNumber, String colorNumber)
             throws Exception {
         LOG.debug("Search for style numbers and color");
         String results = null;
         mongoTemplate = springMongoConfigService.getMongoTemplate();
         Query query = new Query();
         query.addCriteria(
                 Criteria.where(PRODUCT_STYLE).is(styleNumber).and(PRODUCT_COLOR).is(colorNumber));

         query.fields().include("catagory").include("styleNumber").include("gender")
                 .include("productName").include("productType").include("colors.$");
         // uncomment to get color without sizes and comment the above line
         // query.fields().exclude("colors.color.sizes");
         LOG.debug("Query : " + query);
         String searchResult = mongoTemplate.findOne(query, String.class, COLLECTION_NAME);
         if (StringUtils.isEmpty(searchResult) == false) {
             results = searchResult.toString();
         } else {
             results = "NO RECORDS FOUND FOR GIVEN STYLE AND COLOR";
         }
         return results;
     }

     /**
      * Retrieve product metadata based on style and include the specified columns
      * @param styleNumber
      * @param field
      * @return
      * @throws Exception
      */
     @Override
     public String searchFeedByStyle(String styleNumber, String[] field) throws Exception {
         LOG.debug("Search for style numbers and color");
         mongoTemplate = springMongoConfigService.getMongoTemplate();
         String results = null;
         Query query = new Query();
         query.addCriteria(Criteria.where(PRODUCT_STYLE).is(styleNumber));

         if (field != null) {
             System.out.println(Arrays.asList(field));
             for (String key : field) {
                 query.fields().include(key);
             }
         }
         LOG.debug("Query : " + query);
         String searchResult = mongoTemplate.findOne(query, String.class, COLLECTION_NAME);
         if (StringUtils.isEmpty(searchResult) == false) {
             results = searchResult.toString();
         } else {
             results = "NO RECORDS FOUND FOR GIVEN STYLE";
         }
         return results;
     }

    /**
     * This is a global search applicable only to specified fields which are given in text indexes
     * @param searchField
     * @throws Exception
     */
    @Override
    public String globalSearch(String searchField) throws Exception {
        LOG.debug("Inside globalSearch method");
        List<String> resultList = new ArrayList<String>();
        mongoTemplate = springMongoConfigService.getMongoTemplate();
        DBCollection collection = mongoTemplate.getDb().getCollection(COLLECTION_NAME);
        DBObject searchQuery = QueryBuilder.start().text(searchField).get();
        DBCursor cursor = collection.find(searchQuery);

        while (cursor.hasNext()) {
            String resultString = cursor.next().toString();
            LOG.debug("Results :" + resultString);
            resultList.add(resultString);
            LOG.debug("Results size" + resultList.size());
        }

        if (!resultList.isEmpty()) {
            return resultList.toString();
        } else {
            LOG.debug("No Records Found");
            return "No Records Found";
        }
    }


}
