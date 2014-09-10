package org.oliot.epcis.service.query;

import java.io.StringWriter;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.json.JSONArray;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.EPCISQueryBodyType;
import org.oliot.model.epcis.EPCISQueryDocumentType;
import org.oliot.model.epcis.EventListType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.QueryParams;
import org.oliot.model.epcis.QueryResults;
import org.oliot.model.epcis.QueryResultsBody;
import org.oliot.model.epcis.SubscriptionControls;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.TransformationEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Copyright (C) 2014 KAIST RESL
 *
 * This file is part of Oliot (oliot.org).
 *
 * @author Jack Jaewook Byun, Ph.D student Korea Advanced Institute of Science
 *         and Technology Real-time Embedded System Laboratory(RESL)
 *         bjw0829@kaist.ac.kr
 */
@Controller
@RequestMapping("/query")
public class QueryService implements CoreQueryService, ServletContextAware {

	// Due to Created JAXB is not Throwable and need to send Exception to the
	// Result
	@SuppressWarnings("unused")
	private boolean isQueryParameterException;
	// Not EPC Spec
	@SuppressWarnings("unused")
	private boolean isNumberFormatException;
	@Autowired
	ServletContext servletContext;

	/**
	 * 
	 */
	@Autowired
	private HttpServletRequest request;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;

	}

	/**
	 * Registers a subscriber for a previously defined query having the
	 * specified name. The params argument provides the values to be used for
	 * any named parameters defined by the query. The dest parameter specifies a
	 * destination where results from the query are to be delivered, via the
	 * Query Callback Interface. The dest parameter is a URI that both
	 * identifies a specific binding of the Query Callback Interface to use and
	 * specifies addressing information. The controls parameter controls how the
	 * subscription is to be processed; in particular, it specifies the
	 * conditions under which the query is to be invoked (e.g., specifying a
	 * periodic schedule). The subscriptionID is an arbitrary string that is
	 * copied into every response delivered to the specified destination, and
	 * otherwise not interpreted by the EPCIS service. The client may use the
	 * subscriptionID to identify from which subscription a given result was
	 * generated, especially when several subscriptions are made to the same
	 * destination. The dest argument MAY be null or empty, in which case
	 * results are delivered to a pre-arranged destination based on the
	 * authenticated identity of the caller. If the EPCIS implementation does
	 * not have a destination pre-arranged for the caller, or does not permit
	 * this usage, it SHALL raise an InvalidURIException.
	 */
	@Override
	public void subscribe(String queryName, QueryParams params, URI dest,
			SubscriptionControls controls, String subscriptionID) {
		
		
	}

	/**
	 * Removes a previously registered subscription having the specified
	 * subscriptionID.
	 */
	@Override
	public void unsubscribe(String subscriptionID) {
		// TODO Auto-generated method stub

	}

	/**
	 * Returns a list of all subscriptionIDs currently subscribed to the
	 * specified named query.
	 */
	@Override
	public List<String> getSubscriptionIDs(String queryName) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings({ "resource", "rawtypes", "unchecked" })
	@RequestMapping(value = "/Poll/{queryName}", method = RequestMethod.GET)
	@ResponseBody
	public String poll(@PathVariable String queryName,
			@RequestParam(required = false) String eventType,
			@RequestParam(required = false) String GE_eventTime,
			@RequestParam(required = false) String LT_eventTime,
			@RequestParam(required = false) String GE_recordTime,
			@RequestParam(required = false) String LT_recordTime,
			@RequestParam(required = false) String EQ_action,
			@RequestParam(required = false) String EQ_bizStep,
			@RequestParam(required = false) String EQ_disposition,
			@RequestParam(required = false) String EQ_readPoint,
			@RequestParam(required = false) String WD_readPoint,
			@RequestParam(required = false) String EQ_bizLocation,
			@RequestParam(required = false) String WD_bizLocation,
			@RequestParam(required = false) String EQ_bizTransaction_type,
			@RequestParam(required = false) String EQ_source_type,
			@RequestParam(required = false) String EQ_destination_type,
			@RequestParam(required = false) String EQ_transformationID,
			@RequestParam(required = false) String MATCH_epc,
			@RequestParam(required = false) String MATCH_parentID,
			@RequestParam(required = false) String MATCH_inputEPC,
			@RequestParam(required = false) String MATCH_outputEPC,
			@RequestParam(required = false) String MATCH_anyEPC,
			@RequestParam(required = false) String MATCH_epcClass,
			@RequestParam(required = false) String MATCH_inputEPCClass,
			@RequestParam(required = false) String MATCH_outputEPCClass,
			@RequestParam(required = false) String MATCH_anyEPCClass,
			@RequestParam(required = false) String EQ_quantity,
			@RequestParam(required = false) String GT_quantity,
			@RequestParam(required = false) String GE_quantity,
			@RequestParam(required = false) String LT_quantity,
			@RequestParam(required = false) String LE_quantity,
			@RequestParam(required = false) String EQ_fieldname,
			@RequestParam(required = false) String GT_fieldname,
			@RequestParam(required = false) String GE_fieldname,
			@RequestParam(required = false) String LT_fieldname,
			@RequestParam(required = false) String LE_fieldname,
			@RequestParam(required = false) String EQ_ILMD_fieldname,
			@RequestParam(required = false) String GT_ILMD_fieldname,
			@RequestParam(required = false) String GE_ILMD_fieldname,
			@RequestParam(required = false) String LT_ILMD_fieldname,
			@RequestParam(required = false) String LE_ILMD_fieldname,
			@RequestParam(required = false) String EXIST_fieldname,
			@RequestParam(required = false) String EXIST_ILMD_fieldname,
			@RequestParam(required = false) String HASATTR_fieldname,
			@RequestParam(required = false) String EQATTR_fieldname_attrname,
			@RequestParam(required = false) String orderBy,
			@RequestParam(required = false) String orderDirection,
			@RequestParam(required = false) String eventCountLimit,
			@RequestParam(required = false) String maxEventCount) {
		if (!queryName.equals("SimpleEventQuery"))
			return "Unavailable Query Name";

		// Make Base Result Document
		EPCISQueryDocumentType epcisQueryDocumentType = makeBaseResultDocument(queryName);

		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");

		// eventObjects : Container which all the query results (events) will be
		// contained
		List<Object> eventObjects = epcisQueryDocumentType.getEPCISBody()
				.getQueryResults().getResultsBody().getEventList()
				.getObjectEventOrAggregationEventOrQuantityEvent();

		// To be filtered by eventType
		boolean toGetAggregationEvent = true;
		boolean toGetObjectEvent = true;
		boolean toGetQuantityEvent = true;
		boolean toGetTransactionEvent = true;
		boolean toGetTransformationEvent = true;

		/**
		 * EventType : If specified, the result will only include events whose
		 * type matches one of the types specified in the parameter value. Each
		 * element of the parameter value may be one of the following strings:
		 * ObjectEvent, AggregationEvent, QuantityEvent, TransactionEvent, or
		 * TransformationEvent. An element of the parameter value may also be
		 * the name of an extension event type. If omitted, all event types will
		 * be considered for inclusion in the result.
		 */
		if (eventType != null) {
			toGetAggregationEvent = false;
			toGetObjectEvent = false;
			toGetQuantityEvent = false;
			toGetTransactionEvent = false;
			toGetTransformationEvent = false;

			String[] eventTypeArray = eventType.split(",");

			for (int i = 0; i < eventTypeArray.length; i++) {
				String eventTypeString = eventTypeArray[i];

				if (eventTypeString != null)
					eventTypeString = eventTypeString.trim();

				if (eventTypeString.equals("AggregationEvent"))
					toGetAggregationEvent = true;
				else if (eventTypeString.equals("ObjectEvent"))
					toGetObjectEvent = true;
				else if (eventTypeString.equals("QuantityEvent"))
					toGetQuantityEvent = true;
				else if (eventTypeString.equals("TransactionEvent"))
					toGetTransactionEvent = true;
				else if (eventTypeString.equals("TransformationEvent"))
					toGetTransformationEvent = true;
			}
		}

		if (toGetAggregationEvent == true) {
			// Criteria
			List<Criteria> criteriaList = makeCriteria(GE_eventTime,
					LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
					EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint,
					EQ_bizLocation, WD_bizLocation, EQ_bizTransaction_type,
					EQ_source_type, EQ_destination_type, EQ_transformationID,
					MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass,
					MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity,
					GT_quantity, GE_quantity, LT_quantity, LE_quantity,
					EQ_fieldname, GT_fieldname, GE_fieldname, LT_fieldname,
					LE_fieldname, EQ_ILMD_fieldname, GT_ILMD_fieldname,
					GE_ILMD_fieldname, LT_ILMD_fieldname, LE_ILMD_fieldname,
					EXIST_fieldname, EXIST_ILMD_fieldname, HASATTR_fieldname,
					EQATTR_fieldname_attrname, orderBy, orderDirection,
					eventCountLimit, maxEventCount);

			// Make Query
			Query searchQuery = new Query();
			for (int i = 0; i < criteriaList.size(); i++) {
				searchQuery.addCriteria(criteriaList.get(i));
			}
			
			// Sort and Limit Query
			searchQuery = makeSortAndLimitQuery(searchQuery, orderBy,
					orderDirection, eventCountLimit, maxEventCount);

			// Query
			List<AggregationEventType> aggregationEvents = mongoOperation.find(
					searchQuery, AggregationEventType.class);

			// Adding Query Result after converting DBObject to JAXB
			for (int j = 0; j < aggregationEvents.size(); j++) {
				AggregationEventType aggregationEvent = aggregationEvents
						.get(j);
				JAXBElement element = new JAXBElement(new QName(
						"AggregationEvent"), AggregationEventType.class,
						aggregationEvent);
				eventObjects.add(element);
			}
		}

		// For Each Event Type!
		if (toGetObjectEvent == true) {
			// Criteria
			List<Criteria> criteriaList = makeCriteria(GE_eventTime,
					LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
					EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint,
					EQ_bizLocation, WD_bizLocation, EQ_bizTransaction_type,
					EQ_source_type, EQ_destination_type, EQ_transformationID,
					MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass,
					MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity,
					GT_quantity, GE_quantity, LT_quantity, LE_quantity,
					EQ_fieldname, GT_fieldname, GE_fieldname, LT_fieldname,
					LE_fieldname, EQ_ILMD_fieldname, GT_ILMD_fieldname,
					GE_ILMD_fieldname, LT_ILMD_fieldname, LE_ILMD_fieldname,
					EXIST_fieldname, EXIST_ILMD_fieldname, HASATTR_fieldname,
					EQATTR_fieldname_attrname, orderBy, orderDirection,
					eventCountLimit, maxEventCount);

			// Make Query
			Query searchQuery = new Query();
			for (int i = 0; i < criteriaList.size(); i++) {
				searchQuery.addCriteria(criteriaList.get(i));
			}

			// Sort and Limit Query
			searchQuery = makeSortAndLimitQuery(searchQuery, orderBy,
					orderDirection, eventCountLimit, maxEventCount);

			// Invoke Query
			List<ObjectEventType> objectEvents = mongoOperation.find(
					searchQuery, ObjectEventType.class);

			// Adding Query Result after converting DBObject to JAXB
			for (int j = 0; j < objectEvents.size(); j++) {
				ObjectEventType objectEvent = objectEvents.get(j);
				JAXBElement element = new JAXBElement(new QName("ObjectEvent"),
						ObjectEventType.class, objectEvent);
				eventObjects.add(element);
			}
		}
		if (toGetQuantityEvent == true) {
			// Criteria
			List<Criteria> criteriaList = makeCriteria(GE_eventTime,
					LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
					EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint,
					EQ_bizLocation, WD_bizLocation, EQ_bizTransaction_type,
					EQ_source_type, EQ_destination_type, EQ_transformationID,
					MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass,
					MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity,
					GT_quantity, GE_quantity, LT_quantity, LE_quantity,
					EQ_fieldname, GT_fieldname, GE_fieldname, LT_fieldname,
					LE_fieldname, EQ_ILMD_fieldname, GT_ILMD_fieldname,
					GE_ILMD_fieldname, LT_ILMD_fieldname, LE_ILMD_fieldname,
					EXIST_fieldname, EXIST_ILMD_fieldname, HASATTR_fieldname,
					EQATTR_fieldname_attrname, orderBy, orderDirection,
					eventCountLimit, maxEventCount);

			// Make Query
			Query searchQuery = new Query();
			for (int i = 0; i < criteriaList.size(); i++) {
				searchQuery.addCriteria(criteriaList.get(i));
			}

			// Sort and Limit Query
			searchQuery = makeSortAndLimitQuery(searchQuery, orderBy,
					orderDirection, eventCountLimit, maxEventCount);

			// Query
			List<QuantityEventType> quantityEvents = mongoOperation.find(
					searchQuery, QuantityEventType.class);

			// Adding Query Result after converting DBObject to JAXB
			for (int j = 0; j < quantityEvents.size(); j++) {
				QuantityEventType quantityEvent = quantityEvents.get(j);
				JAXBElement element = new JAXBElement(
						new QName("QuantityEvent"), QuantityEventType.class,
						quantityEvent);
				eventObjects.add(element);
			}
		}
		if (toGetTransactionEvent == true) {
			// Criteria
			List<Criteria> criteriaList = makeCriteria(GE_eventTime,
					LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
					EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint,
					EQ_bizLocation, WD_bizLocation, EQ_bizTransaction_type,
					EQ_source_type, EQ_destination_type, EQ_transformationID,
					MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass,
					MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity,
					GT_quantity, GE_quantity, LT_quantity, LE_quantity,
					EQ_fieldname, GT_fieldname, GE_fieldname, LT_fieldname,
					LE_fieldname, EQ_ILMD_fieldname, GT_ILMD_fieldname,
					GE_ILMD_fieldname, LT_ILMD_fieldname, LE_ILMD_fieldname,
					EXIST_fieldname, EXIST_ILMD_fieldname, HASATTR_fieldname,
					EQATTR_fieldname_attrname, orderBy, orderDirection,
					eventCountLimit, maxEventCount);

			// Make Query
			Query searchQuery = new Query();
			for (int i = 0; i < criteriaList.size(); i++) {
				searchQuery.addCriteria(criteriaList.get(i));
			}

			// Sort and Limit Query
			searchQuery = makeSortAndLimitQuery(searchQuery, orderBy,
					orderDirection, eventCountLimit, maxEventCount);

			// Query
			List<TransactionEventType> transactionEvents = mongoOperation.find(
					searchQuery, TransactionEventType.class);

			// Adding Query Result after converting DBObject to JAXB
			for (int j = 0; j < transactionEvents.size(); j++) {
				TransactionEventType transactionEvent = transactionEvents
						.get(j);
				JAXBElement element = new JAXBElement(new QName(
						"TransactionEvent"), TransactionEventType.class,
						transactionEvent);
				eventObjects.add(element);
			}
		}
		if (toGetTransformationEvent == true) {
			// Criteria
			List<Criteria> criteriaList = makeCriteria(GE_eventTime,
					LT_eventTime, GE_recordTime, LT_recordTime, EQ_action,
					EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint,
					EQ_bizLocation, WD_bizLocation, EQ_bizTransaction_type,
					EQ_source_type, EQ_destination_type, EQ_transformationID,
					MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass,
					MATCH_outputEPCClass, MATCH_anyEPCClass, EQ_quantity,
					GT_quantity, GE_quantity, LT_quantity, LE_quantity,
					EQ_fieldname, GT_fieldname, GE_fieldname, LT_fieldname,
					LE_fieldname, EQ_ILMD_fieldname, GT_ILMD_fieldname,
					GE_ILMD_fieldname, LT_ILMD_fieldname, LE_ILMD_fieldname,
					EXIST_fieldname, EXIST_ILMD_fieldname, HASATTR_fieldname,
					EQATTR_fieldname_attrname, orderBy, orderDirection,
					eventCountLimit, maxEventCount);

			// Make Query
			Query searchQuery = new Query();
			for (int i = 0; i < criteriaList.size(); i++) {
				searchQuery.addCriteria(criteriaList.get(i));
			}

			// Sort and Limit Query
			searchQuery = makeSortAndLimitQuery(searchQuery, orderBy,
					orderDirection, eventCountLimit, maxEventCount);

			// Query
			List<TransformationEventType> transformationEvents = mongoOperation
					.find(searchQuery, TransformationEventType.class);

			// Adding Query Result after converting DBObject to JAXB
			for (int j = 0; j < transformationEvents.size(); j++) {
				TransformationEventType transformationEvent = transformationEvents
						.get(j);
				JAXBElement element = new JAXBElement(new QName(
						"TransformationEvent"), TransformationEventType.class,
						transformationEvent);
				eventObjects.add(element);
			}
		}

		StringWriter sw = new StringWriter();
		JAXB.marshal(epcisQueryDocumentType, sw);
		return sw.toString();
	}

	/**
	 * Invokes a previously defined query having the specified name, returning
	 * the results. The params argument provides the values to be used for any
	 * named parameters defined by the query.
	 * 
	 * @author jack Since var 'Query Param' is just key-value pairs this method
	 *         is better to be parameter of http servlet request
	 * @deprecated
	 */
	@Override
	public QueryResults poll(String queryName, QueryParams params) {
		return null;
	}

	/**
	 * [REST Version of getQueryNames] Returns a list of all query names
	 * available for use with the subscribe and poll methods. This includes all
	 * pre- defined queries provided by the implementation, including those
	 * specified in Section 8.2.7.
	 * 
	 * @return JSONArray of query names ( String )
	 */
	@RequestMapping(value = "/QueryNames", method = RequestMethod.GET)
	@ResponseBody
	public String getQueryNamesREST() {
		JSONArray jsonArray = new JSONArray();
		List<String> queryNames = getQueryNames();
		for (int i = 0; i < queryNames.size(); i++) {
			jsonArray.put(queryNames.get(i));
		}
		return jsonArray.toString(1);
	}

	/**
	 * Returns a list of all query names available for use with the subscribe
	 * and poll methods. This includes all pre- defined queries provided by the
	 * implementation, including those specified in Section 8.2.7.
	 */
	@Override
	public List<String> getQueryNames() {
		List<String> queryNames = new ArrayList<String>();
		queryNames.add("SimpleEventQuery");
		return queryNames;
	}

	/**
	 * Returns a string that identifies what version of the specification this
	 * implementation complies with. The possible values for this string are
	 * defined by GS1. An implementation SHALL return a string corresponding to
	 * a version of this specification to which the implementation fully
	 * complies, and SHOULD return the string corresponding to the latest
	 * version to which it complies. To indicate compliance with this Version
	 * 1.1 of the EPCIS specification, the implementation SHALL return the
	 * string 1.1.
	 */
	@Override
	@RequestMapping(value = "/StandardVersion", method = RequestMethod.GET)
	@ResponseBody
	public String getStandardVersion() {
		return "1.1";
	}

	/**
	 * Returns a string that identifies what vendor extensions this
	 * implementation provides. The possible values of this string and their
	 * meanings are vendor-defined, except that the empty string SHALL indicate
	 * that the implementation implements only standard functionality with no
	 * vendor extensions. When an implementation chooses to return a non-empty
	 * string, the value returned SHALL be a URI where the vendor is the owning
	 * authority. For example, this may be an HTTP URL whose authority portion
	 * is a domain name owned by the vendor, a URN having a URN namespace
	 * identifier issued to the vendor by IANA, an OID URN whose initial path is
	 * a Private Enterprise Number assigned to the vendor, etc.
	 */
	@Override
	@RequestMapping(value = "/VendorVersion", method = RequestMethod.GET)
	@ResponseBody
	public String getVendorVersion() {
		// It is not a version of Vendor
		return null;
	}

	private EPCISQueryDocumentType makeBaseResultDocument(String queryName) {
		// Make Base Result Document
		EPCISQueryDocumentType epcisQueryDocumentType = new EPCISQueryDocumentType();
		EPCISQueryBodyType epcisBody = new EPCISQueryBodyType();
		epcisQueryDocumentType.setEPCISBody(epcisBody);
		QueryResults queryResults = new QueryResults();
		queryResults.setQueryName(queryName);
		epcisBody.setQueryResults(queryResults);
		QueryResultsBody queryResultsBody = new QueryResultsBody();
		queryResults.setResultsBody(queryResultsBody);
		EventListType eventListType = new EventListType();
		queryResultsBody.setEventList(eventListType);
		// Object instanceof JAXBElement
		List<Object> eventObjects = new ArrayList<Object>();
		eventListType
				.setObjectEventOrAggregationEventOrQuantityEvent(eventObjects);
		return epcisQueryDocumentType;
	}

	public Query makeSortAndLimitQuery(Query query, String orderBy,
			String orderDirection, String eventCountLimit, String maxEventCount) {
		/**
		 * orderBy : If specified, names a single field that will be used to
		 * order the results. The orderDirection field specifies whether the
		 * ordering is in ascending sequence or descending sequence. Events
		 * included in the result that lack the specified field altogether may
		 * occur in any position within the result event list. The value of this
		 * parameter SHALL be one of: eventTime, recordTime, or the fully
		 * qualified name of an extension field whose type is Int, Float, Time,
		 * or String. A fully qualified fieldname is constructed as for the
		 * EQ_fieldname parameter. In the case of a field of type String, the
		 * ordering SHOULD be in lexicographic order based on the Unicode
		 * encoding of the strings, or in some other collating sequence
		 * appropriate to the locale. If omitted, no order is specified. The
		 * implementation MAY order the results in any order it chooses, and
		 * that order MAY differ even when the same query is executed twice on
		 * the same data. (In EPCIS 1.0, the value quantity was also permitted,
		 * but its use is deprecated in EPCIS 1.1.)
		 * 
		 * orderDirection : If specified and orderBy is also specified,
		 * specifies whether the results are ordered in ascending or descending
		 * sequence according to the key specified by orderBy. The value of this
		 * parameter must be one of ASC (for ascending order) or DESC (for
		 * descending order); if not, the implementation SHALL raise a
		 * QueryParameterException. If omitted, defaults to DESC.
		 */

		// Update Query with ORDER and LIMIT
		if (orderBy != null) {
			// Currently only eventTime, recordTime can be used
			if (orderBy.trim().equals("eventTime")) {
				if (orderDirection != null) {
					if (orderDirection.trim().equals("ASC")) {
						query.with(new Sort(Sort.Direction.ASC, "eventTime"));
					} else if (orderDirection.trim().equals("DESC")) {
						query.with(new Sort(Sort.Direction.DESC, "eventTime"));
					}
				}
			} else if (orderBy.trim().equals("recordTime")) {
				if (orderDirection != null) {
					if (orderDirection.trim().equals("ASC")) {
						query.with(new Sort(Sort.Direction.ASC, "recordTime"));
					} else if (orderDirection.trim().equals("DESC")) {
						query.with(new Sort(Sort.Direction.DESC, "recordTime"));
					}
				}
			}
		}

		/**
		 * eventCountLimit: If specified, the results will only include the
		 * first N events that match the other criteria, where N is the value of
		 * this parameter. The ordering specified by the orderBy and
		 * orderDirection parameters determine the meaning of “first” for this
		 * purpose. If omitted, all events matching the specified criteria will
		 * be included in the results. This parameter and maxEventCount are
		 * mutually exclusive; if both are specified, a QueryParameterException
		 * SHALL be raised. This parameter may only be used when orderBy is
		 * specified; if orderBy is omitted and eventCountLimit is specified, a
		 * QueryParameterException SHALL be raised. This parameter differs from
		 * maxEventCount in that this parameter limits the amount of data
		 * returned, whereas maxEventCount causes an exception to be thrown if
		 * the limit is exceeded.
		 */
		if (eventCountLimit != null) {
			try {
				int eventCount = Integer.parseInt(eventCountLimit);
				query.limit(eventCount);
			} catch (NumberFormatException nfe) {
				isNumberFormatException = true;
			}
		}

		return query;
	}

	public List<Criteria> makeCriteria(String GE_eventTime,
			String LT_eventTime, String GE_recordTime, String LT_recordTime,
			String EQ_action, String EQ_bizStep, String EQ_disposition,
			String EQ_readPoint, String WD_readPoint, String EQ_bizLocation,
			String WD_bizLocation, String EQ_bizTransaction_type,
			String EQ_source_type, String EQ_destination_type,
			String EQ_transformationID, String MATCH_epc,
			String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
			String MATCH_inputEPCClass, String MATCH_outputEPCClass,
			String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
			String GE_quantity, String LT_quantity, String LE_quantity,
			String EQ_fieldname, String GT_fieldname, String GE_fieldname,
			String LT_fieldname, String LE_fieldname, String EQ_ILMD_fieldname,
			String GT_ILMD_fieldname, String GE_ILMD_fieldname,
			String LT_ILMD_fieldname, String LE_ILMD_fieldname,
			String EXIST_fieldname, String EXIST_ILMD_fieldname,
			String HASATTR_fieldname, String EQATTR_fieldname_attrname,
			String orderBy, String orderDirection, String eventCountLimit,
			String maxEventCount) {

		List<Criteria> criteriaList = new ArrayList<Criteria>();
		try {
			/**
			 * GE_eventTime: If specified, only events with eventTime greater
			 * than or equal to the specified value will be included in the
			 * result. If omitted, events are included regardless of their
			 * eventTime (unless constrained by the LT_eventTime parameter).
			 * Example: 2014-08-11T19:57:59.717+09:00 SimpleDateFormat sdf = new
			 * SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			 * eventTime.setTime(sdf.parse(timeString)); e.g.
			 * 1988-07-04T12:08:56.235-07:00
			 * 
			 * Verified
			 */
			if (GE_eventTime != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				GregorianCalendar geEventTimeCalendar = new GregorianCalendar();

				geEventTimeCalendar.setTime(sdf.parse(GE_eventTime));
				long geEventTimeMillis = geEventTimeCalendar.getTimeInMillis();
				criteriaList.add(Criteria.where("eventTime").gt(
						geEventTimeMillis));
			}
			/**
			 * LT_eventTime: If specified, only events with eventTime less than
			 * the specified value will be included in the result. If omitted,
			 * events are included regardless of their eventTime (unless
			 * constrained by the GE_eventTime parameter).
			 * 
			 * Verified
			 */
			if (LT_eventTime != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				GregorianCalendar ltEventTimeCalendar = new GregorianCalendar();
				Date date = sdf.parse(LT_eventTime);
				ltEventTimeCalendar.setTime(date);
				long ltEventTimeMillis = ltEventTimeCalendar.getTimeInMillis();
				criteriaList.add(Criteria.where("eventTime").lt(
						ltEventTimeMillis));
			}
			/**
			 * GE_recordTime: If provided, only events with recordTime greater
			 * than or equal to the specified value will be returned. The
			 * automatic limitation based on event record time (Section 8.2.5.2)
			 * may implicitly provide a constraint similar to this parameter. If
			 * omitted, events are included regardless of their recordTime,
			 * other than automatic limitation based on event record time
			 * (Section 8.2.5.2).
			 * 
			 * Verified
			 */
			if (GE_recordTime != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				GregorianCalendar geRecordTimeCalendar = new GregorianCalendar();

				geRecordTimeCalendar.setTime(sdf.parse(GE_recordTime));
				long geRecordTimeMillis = geRecordTimeCalendar
						.getTimeInMillis();
				criteriaList.add(Criteria.where("recordTime").gt(
						geRecordTimeMillis));
			}
			/**
			 * LE_recordTime: If provided, only events with recordTime less than
			 * the specified value will be returned. If omitted, events are
			 * included regardless of their recordTime (unless constrained by
			 * the GE_recordTime parameter or the automatic limitation based on
			 * event record time).
			 * 
			 * Verified
			 */
			if (LT_recordTime != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				GregorianCalendar ltRecordTimeCalendar = new GregorianCalendar();

				ltRecordTimeCalendar.setTime(sdf.parse(LT_recordTime));
				long ltRecordTimeMillis = ltRecordTimeCalendar
						.getTimeInMillis();
				criteriaList.add(Criteria.where("recordTime").lt(
						ltRecordTimeMillis));
			}

			/**
			 * EQ_action: If specified, the result will only include events that
			 * (a) have an action field; and where (b) the value of the action
			 * field matches one of the specified values. The elements of the
			 * value of this parameter each must be one of the strings ADD,
			 * OBSERVE, or DELETE; if not, the implementation SHALL raise a
			 * QueryParameterException. If omitted, events are included
			 * regardless of their action field.
			 * 
			 * Verified
			 */
			if (EQ_action != null) {
				String[] eqActionArray = EQ_action.split(",");
				List<String> subStringList = new ArrayList<String>();
				for (int i = 0; i < eqActionArray.length; i++) {
					String eqActionString = eqActionArray[i].trim();
					// According to SPEC, this condition thwos
					// QueryParameterException
					if (!eqActionString.equals("ADD")
							&& !eqActionString.equals("OBSERVE")
							&& !eqActionString.equals("DELETE")) {
						isQueryParameterException = true;
					}
					if (eqActionString.equals("ADD")
							|| eqActionString.equals("OBSERVE")
							|| eqActionString.equals("DELETE"))
						subStringList.add(eqActionString);
				}

				if (subStringList != null)
					criteriaList
							.add(Criteria.where("action").in(subStringList));
			}
			/**
			 * EQ_bizStep: If specified, the result will only include events
			 * that (a) have a non-null bizStep field; and where (b) the value
			 * of the bizStep field matches one of the specified values. If this
			 * parameter is omitted, events are returned regardless of the value
			 * of the bizStep field or whether the bizStep field exists at all.
			 * 
			 * Verified
			 */
			if (EQ_bizStep != null) {
				String[] eqBizStepArray = EQ_bizStep.split(",");
				List<String> subStringList = new ArrayList<String>();
				for (int i = 0; i < eqBizStepArray.length; i++) {
					String eqBizStepString = eqBizStepArray[i].trim();
					subStringList.add(eqBizStepString);
				}
				if (subStringList != null)
					criteriaList.add(Criteria.where("bizStep")
							.in(subStringList));
			}
			/**
			 * EQ_disposition: Like the EQ_bizStep parameter, but for the
			 * disposition field.
			 * 
			 * Verified
			 */
			if (EQ_disposition != null) {
				String[] eqDispositionArray = EQ_disposition.split(",");
				List<String> subStringList = new ArrayList<String>();
				for (int i = 0; i < eqDispositionArray.length; i++) {
					String eqDispositionString = eqDispositionArray[i].trim();
					subStringList.add(eqDispositionString);
				}
				if (subStringList != null)
					criteriaList.add(Criteria.where("disposition").in(
							subStringList));
			}
			/**
			 * EQ_readPoint: If specified, the result will only include events
			 * that (a) have a non-null readPoint field; and where (b) the value
			 * of the readPoint field matches one of the specified values. If
			 * this parameter and WD_readPoint are both omitted, events are
			 * returned regardless of the value of the readPoint field or
			 * whether the readPoint field exists at all.
			 *
			 * //TODO: This query style is successful in mongo console. However,
			 * in the spring/mongo case is not available. Need to check later
			 * 
			 */
			if (EQ_readPoint != null) {
				String[] eqReadPointArray = EQ_readPoint.split(",");
				List<String> subStringList = new ArrayList<String>();
				for (int i = 0; i < eqReadPointArray.length; i++) {
					String eqReadPointString = eqReadPointArray[i].trim();
					subStringList.add(eqReadPointString);
				}
				Criteria criteria = Criteria.where("readPoint.id").in(
						subStringList);
				criteriaList.add(criteria);
			}

			/**
			 * WD_readPoint: If specified, the result will only include events
			 * that (a) have a non-null readPoint field; and where (b) the value
			 * of the readPoint field matches one of the specified values, or is
			 * a direct or indirect descendant of one of the specified values.
			 * The meaning of “direct or indirect descendant” is specified by
			 * master data, as described in Section 6.5. (WD is an abbreviation
			 * for “with descendants.”) If this parameter and EQ_readPoint are
			 * both omitted, events are returned regardless of the value of the
			 * readPoint field or whether the readPoint field exists at all.
			 * 
			 * //TODO: This query style is successful in mongo console. However,
			 * in the spring/mongo case is not available. Need to check later
			 */
			if (WD_readPoint != null) {
				// TODO: Need to check nested query readPoint.id
				// TODO: Need to check regex works or not
				String[] wdReadPointArray = WD_readPoint.split(",");
				List<Pattern> patternArray = new ArrayList<Pattern>();
				for (int i = 0; i < wdReadPointArray.length; i++) {
					String wdReadPointString = wdReadPointArray[i].trim();
					patternArray.add(Pattern.compile("/^" + wdReadPointString
							+ ".*/"));
				}
				Criteria criteria = Criteria.where("readPoint.id").in(
						patternArray);
				criteriaList.add(criteria);
			}
			/**
			 * EQ_bizLocation: Like the EQ_readPoint parameter, but for the
			 * bizLocation field.
			 */
			if (EQ_bizLocation != null) {
				String[] eqBizLocationArray = EQ_bizLocation.split(",");
				List<String> subStringList = new ArrayList<String>();
				for (int i = 0; i < eqBizLocationArray.length; i++) {
					String eqBizLocationString = eqBizLocationArray[i].trim();
					subStringList.add(eqBizLocationString);
				}
				Criteria criteria = Criteria.where("bizLocation.id").in(
						subStringList);
				criteriaList.add(criteria);
			}
			/**
			 * WD_bizLocation: Like the WD_readPoint parameter, but for the
			 * bizLocation field.
			 */
			if (WD_bizLocation != null) {
				// TODO: Need to check nested query readPoint.id
				// TODO: Need to check regex works or not
				String[] wdBizLocationArray = WD_bizLocation.split(",");
				List<Pattern> patternArray = new ArrayList<Pattern>();
				for (int i = 0; i < wdBizLocationArray.length; i++) {
					String wdBizLocationString = wdBizLocationArray[i].trim();
					patternArray.add(Pattern.compile("/^" + wdBizLocationString
							+ ".*/"));
				}
				Criteria criteria = Criteria.where("bizLocation.id").in(
						patternArray);
				criteriaList.add(criteria);
			}
			/**
			 * EQ_bizTransaction_type: EQ_source_type: EQ_destination_type: is
			 * currently not processed, since its description seems ambiguous
			 */

			/**
			 * EQ_transformationID: If this parameter is specified, the result
			 * will only include events that (a) have a transformationID field
			 * (that is, TransformationEvents or extension event type that
			 * extend TransformationEvent); and where (b) the transformationID
			 * field is equal to one of the values specified in this parameter.
			 * TODO: TransformationID 가 있어야만 한다고 함. 이런 필터링을 고려해서 처리해야 하며. 다른
			 * 쿼리조건도 봐보
			 */
			if (EQ_transformationID != null) {
				String[] eqTransformationIDArray = EQ_transformationID
						.split(",");
				List<String> subStringList = new ArrayList<String>();
				for (int i = 0; i < eqTransformationIDArray.length; i++) {
					String eqTransformationIDString = eqTransformationIDArray[i]
							.trim();
					subStringList.add(eqTransformationIDString);
				}
				Criteria criteria = Criteria.where("bizLocation.id").in(
						subStringList);
				criteriaList.add(criteria);
			}

			/**
			 * MATCH_epc: If this parameter is specified, the result will only
			 * include events that (a) have an epcList or a childEPCs field
			 * (that is, ObjectEvent, AggregationEvent, TransactionEvent or
			 * extension event types that extend one of those three); and where
			 * (b) one of the EPCs listed in the epcList or childEPCs field
			 * (depending on event type) matches one of the EPC patterns or URIs
			 * specified in this parameter, where the meaning of “matches” is as
			 * specified in Section 8.2.7.1.1. If this parameter is omitted,
			 * events are included regardless of their epcList or childEPCs
			 * field or whether the epcList or childEPCs field exists.
			 * 
			 * Somewhat verified
			 */
			if (MATCH_epc != null) {
				String[] match_EPCArray = MATCH_epc.split(",");
				List<DBObject> subDBObjectList = new ArrayList<DBObject>();
				for (int i = 0; i < match_EPCArray.length; i++) {
					String match_EPCString = match_EPCArray[i].trim();
					DBObject queryObj = new BasicDBObject();
					queryObj.put("epc", match_EPCString);
					subDBObjectList.add(queryObj);
				}
				Criteria criteria = new Criteria();
				criteria.orOperator(
						Criteria.where("epcList").in(subDBObjectList), Criteria
								.where("childEPCs").in(subDBObjectList));

				criteriaList.add(criteria);
			}

			/**
			 * MATCH_parentID: Like MATCH_epc, but matches the parentID field of
			 * AggregationEvent, the parentID field of TransactionEvent, and
			 * extension event types that extend either AggregationEvent or
			 * TransactionEvent. The meaning of “matches” is as specified in
			 * Section 8.2.7.1.1.
			 */
			if (MATCH_parentID != null) {
				String[] match_parentEPCArray = MATCH_parentID.split(",");

				List<DBObject> subDBObjectList = new ArrayList<DBObject>();
				for (int i = 0; i < match_parentEPCArray.length; i++) {
					String match_parentEPCString = match_parentEPCArray[i]
							.trim();
					DBObject queryObj = new BasicDBObject();
					queryObj.put("epc", match_parentEPCString);
					subDBObjectList.add(queryObj);
				}
				Criteria criteria = Criteria.where("parentID").in(
						subDBObjectList);
				criteriaList.add(criteria);
			}

			/**
			 * MATCH_inputEPC: If this parameter is specified, the result will
			 * only include events that (a) have an inputEPCList (that is,
			 * TransformationEvent or an extension event type that extends
			 * TransformationEvent); and where (b) one of the EPCs listed in the
			 * inputEPCList field matches one of the EPC patterns or URIs
			 * specified in this parameter. The meaning of “matches” is as
			 * specified in Section 8.2.7.1.1. If this parameter is omitted,
			 * events are included regardless of their inputEPCList field or
			 * whether the inputEPCList field exists.
			 */
			if (MATCH_inputEPC != null) {
				String[] match_inputEPCArray = MATCH_inputEPC.split(",");

				List<DBObject> subDBObjectList = new ArrayList<DBObject>();

				for (int i = 0; i < match_inputEPCArray.length; i++) {
					String match_inputEPCString = match_inputEPCArray[i].trim();
					DBObject queryObj = new BasicDBObject();
					queryObj.put("epc", match_inputEPCString);
					subDBObjectList.add(queryObj);
				}
				Criteria criteria = Criteria.where("inputEPCList").in(
						subDBObjectList);
				criteriaList.add(criteria);
			}

			/**
			 * MATCH_outputEPC: If this parameter is specified, the result will
			 * only include events that (a) have an inputEPCList (that is,
			 * TransformationEvent or an extension event type that extends
			 * TransformationEvent); and where (b) one of the EPCs listed in the
			 * inputEPCList field matches one of the EPC patterns or URIs
			 * specified in this parameter. The meaning of “matches” is as
			 * specified in Section 8.2.7.1.1. If this parameter is omitted,
			 * events are included regardless of their inputEPCList field or
			 * whether the inputEPCList field exists.
			 */
			if (MATCH_outputEPC != null) {
				String[] match_outputEPCArray = MATCH_outputEPC.split(",");

				List<DBObject> subDBObjectList = new ArrayList<DBObject>();

				for (int i = 0; i < match_outputEPCArray.length; i++) {
					String match_outputEPCString = match_outputEPCArray[i]
							.trim();
					DBObject queryObj = new BasicDBObject();
					queryObj.put("epc", match_outputEPCString);
					subDBObjectList.add(queryObj);
				}
				Criteria criteria = Criteria.where("outputEPCList").in(
						subDBObjectList);
				criteriaList.add(criteria);
			}

			/**
			 * MATCH_anyEPC: If this parameter is specified, the result will
			 * only include events that (a) have an epcList field, a childEPCs
			 * field, a parentID field, an inputEPCList field, or an
			 * outputEPCList field (that is, ObjectEvent, AggregationEvent,
			 * TransactionEvent, TransformationEvent, or extension event types
			 * that extend one of those four); and where (b) the parentID field
			 * or one of the EPCs listed in the epcList, childEPCs,
			 * inputEPCList, or outputEPCList field (depending on event type)
			 * matches one of the EPC patterns or URIs specified in this
			 * parameter. The meaning of “matches” is as specified in Section
			 * 8.2.7.1.1.
			 */

			if (MATCH_anyEPC != null) {
				String[] match_anyEPCArray = MATCH_anyEPC.split(",");
				List<DBObject> subDBObjectList = new ArrayList<DBObject>();

				for (int i = 0; i < match_anyEPCArray.length; i++) {
					String match_anyEPCString = match_anyEPCArray[i].trim();
					DBObject queryObj = new BasicDBObject();
					queryObj.put("epc", match_anyEPCString);
					subDBObjectList.add(queryObj);
				}
				Criteria criteria = new Criteria();
				criteria.orOperator(
						Criteria.where("epcList").in(subDBObjectList), Criteria
								.where("childEPCs").in(subDBObjectList),
						Criteria.where("inputEPCList").in(subDBObjectList),
						Criteria.where("outputEPCList").in(subDBObjectList));
				criteriaList.add(criteria);
			}

			/**
			 * MATCH_epcClass: If this parameter is specified, the result will
			 * only include events that (a) have a quantityList or a
			 * childQuantityList field (that is, ObjectEvent, AggregationEvent,
			 * TransactionEvent or extension event types that extend one of
			 * those three); and where (b) one of the EPC classes listed in the
			 * quantityList or childQuantityList field (depending on event type)
			 * matches one of the EPC patterns or URIs specified in this
			 * parameter. The result will also include QuantityEvents whose
			 * epcClass field matches one of the EPC patterns or URIs specified
			 * in this parameter. The meaning of “matches” is as specified in
			 * Section 8.2.7.1.1.
			 */
			if (MATCH_epcClass != null) {
				String[] match_epcClassArray = MATCH_epcClass.split(",");

				List<String> subStringList = new ArrayList<String>();
				Criteria criteria = new Criteria();

				for (int i = 0; i < match_epcClassArray.length; i++) {
					String match_epcClassString = match_epcClassArray[i].trim();
					subStringList.add(match_epcClassString);
				}
				criteria.orOperator(
						Criteria.where("extension.quantityList.epcClass").in(
								subStringList),
						Criteria.where("extension.childQuantityList.epcClass")
								.in(subStringList));
				criteriaList.add(criteria);
			}

			/**
			 * MATCH_inputEPCClass: If this parameter is specified, the result
			 * will only include events that (a) have an inputQuantityList field
			 * (that is, TransformationEvent or extension event types that
			 * extend it); and where (b) one of the EPC classes listed in the
			 * inputQuantityList field (depending on event type) matches one of
			 * the EPC patterns or URIs specified in this parameter. The meaning
			 * of “matches” is as specified in Section 8.2.7.1.1.
			 */
			if (MATCH_inputEPCClass != null) {
				String[] match_inputEPCClassArray = MATCH_inputEPCClass
						.split(",");

				List<String> subStringList = new ArrayList<String>();

				for (int i = 0; i < match_inputEPCClassArray.length; i++) {
					String match_inputEPCClassString = match_inputEPCClassArray[i]
							.trim();
					subStringList.add(match_inputEPCClassString);
				}
				Criteria criteria = Criteria
						.where("inputQuantityList.epcClass").in(subStringList);
				criteriaList.add(criteria);
			}

			/**
			 * MATCH_outputEPCClass: If this parameter is specified, the result
			 * will only include events that (a) have an outputQuantityList
			 * field (that is, TransformationEvent or extension event types that
			 * extend it); and where (b) one of the EPC classes listed in the
			 * outputQuantityList field (depending on event type) matches one of
			 * the EPC patterns or URIs specified in this parameter. The meaning
			 * of “matches” is as specified in Section 8.2.7.1.1.
			 */

			if (MATCH_outputEPCClass != null) {
				String[] match_outputEPCClassArray = MATCH_outputEPCClass
						.split(",");
				List<String> subStringList = new ArrayList<String>();

				for (int i = 0; i < match_outputEPCClassArray.length; i++) {
					String match_outputEPCClassString = match_outputEPCClassArray[i]
							.trim();
					subStringList.add(match_outputEPCClassString);
				}
				Criteria criteria = Criteria.where(
						"outputQuantityList.epcClass").in(subStringList);
				criteriaList.add(criteria);
			}

			/**
			 * MATCH_anyEPCClass: If this parameter is specified, the result
			 * will only include events that (a) have a quantityList,
			 * childQuantityList, inputQuantityList, or outputQuantityList field
			 * (that is, ObjectEvent, AggregationEvent, TransactionEvent,
			 * TransformationEvent, or extension event types that extend one of
			 * those four); and where (b) one of the EPC classes listed in any
			 * of those fields matches one of the EPC patterns or URIs specified
			 * in this parameter. The result will also include QuantityEvents
			 * whose epcClass field matches one of the EPC patterns or URIs
			 * specified in this parameter. The meaning of “matches” is as
			 * specified in Section 8.2.7.1.1.
			 */
			if (MATCH_anyEPCClass != null) {
				String[] match_anyEPCClassArray = MATCH_anyEPCClass.split(",");

				List<String> subStringList = new ArrayList<String>();

				for (int i = 0; i < match_anyEPCClassArray.length; i++) {
					String match_anyEPCClassString = match_anyEPCClassArray[i]
							.trim();
					subStringList.add(match_anyEPCClassString);

				}
				Criteria criteria = new Criteria();
				criteria.orOperator(
						Criteria.where("extension.quantityList.epcClass").in(
								subStringList),
						Criteria.where("extension.childQuantityList.epcClass")
								.in(subStringList),
						Criteria.where("inputQuantityList.epcClass").in(
								subStringList),
						Criteria.where("outputQuantityList.epcClass").in(
								subStringList));
				criteriaList.add(criteria);
			}

			/**
			 * (DEPCRECATED in EPCIS 1.1) EQ_quantity; GT_quantity; GE_quantity;
			 * LT_quantity; LE_quantity
			 **/

			/**
			 * Implementation Pending
			 * 
			 * *_fieldname
			 * 
			 **/

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return criteriaList;
	}
}