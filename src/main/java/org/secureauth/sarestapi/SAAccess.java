package org.secureauth.sarestapi;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.ws.rs.core.Cookie;

import org.secureauth.sarestapi.data.IPEval;
import org.secureauth.sarestapi.data.PushAcceptStatus;
import org.secureauth.sarestapi.data.SAAuth;
import org.secureauth.sarestapi.data.SABaseURL;
import org.secureauth.sarestapi.data.SAConfig;
import org.secureauth.sarestapi.data.BehavioralBio.BehaveBioRequest;
import org.secureauth.sarestapi.data.DFP.DFP;
import org.secureauth.sarestapi.data.NumberProfile.CarrierInfo;
import org.secureauth.sarestapi.data.Requests.AccessHistoryRequest;
import org.secureauth.sarestapi.data.Requests.AdaptiveAuthRequest;
import org.secureauth.sarestapi.data.Requests.AuthRequest;
import org.secureauth.sarestapi.data.Requests.BehaveBioResetRequest;
import org.secureauth.sarestapi.data.Requests.DFPConfirmRequest;
import org.secureauth.sarestapi.data.Requests.DFPScoreRequest;
import org.secureauth.sarestapi.data.Requests.DFPValidateRequest;
import org.secureauth.sarestapi.data.Requests.DeleteUserRequest;
import org.secureauth.sarestapi.data.Requests.IPEvalRequest;
import org.secureauth.sarestapi.data.Requests.LinkToAcceptFactory;
import org.secureauth.sarestapi.data.Requests.NumberProfileRequest;
import org.secureauth.sarestapi.data.Requests.NumberProfileUpdateRequest;
import org.secureauth.sarestapi.data.Requests.PushToAcceptBiometricsRequest;
import org.secureauth.sarestapi.data.Requests.PushToAcceptRequest;
import org.secureauth.sarestapi.data.Requests.PushToAcceptRequestsFactory;
import org.secureauth.sarestapi.data.Requests.StatusRequest;
import org.secureauth.sarestapi.data.Requests.ThrottleRequest;
import org.secureauth.sarestapi.data.Requests.UserPasswordRequest;
import org.secureauth.sarestapi.data.Requests.ValidateOTPRequest;
import org.secureauth.sarestapi.data.Response.AdaptiveAuthResponse;
import org.secureauth.sarestapi.data.Response.BaseResponse;
import org.secureauth.sarestapi.data.Response.BehaveBioResponse;
import org.secureauth.sarestapi.data.Response.DFPConfirmResponse;
import org.secureauth.sarestapi.data.Response.DFPValidateResponse;
import org.secureauth.sarestapi.data.Response.FactorsResponse;
import org.secureauth.sarestapi.data.Response.GroupAssociationResponse;
import org.secureauth.sarestapi.data.Response.JSObjectResponse;
import org.secureauth.sarestapi.data.Response.NumberProfileResponse;
import org.secureauth.sarestapi.data.Response.ResponseObject;
import org.secureauth.sarestapi.data.Response.StatefulResponseObject;
import org.secureauth.sarestapi.data.Response.ThrottleResponse;
import org.secureauth.sarestapi.data.Response.UserProfileResponse;
import org.secureauth.sarestapi.data.Response.ValidateOTPResponse;
import org.secureauth.sarestapi.data.UserProfile.NewUserProfile;
import org.secureauth.sarestapi.data.UserProfile.UserProfileKB;
import org.secureauth.sarestapi.data.UserProfile.UserToGroups;
import org.secureauth.sarestapi.data.UserProfile.UsersToGroup;
import org.secureauth.sarestapi.exception.SARestAPIException;
import org.secureauth.sarestapi.guid.GUIDStrategy;
import org.secureauth.sarestapi.queries.AccessHistoryQuery;
import org.secureauth.sarestapi.queries.AuthQuery;
import org.secureauth.sarestapi.queries.BehaveBioQuery;
import org.secureauth.sarestapi.queries.DFPQuery;
import org.secureauth.sarestapi.queries.FactorsQuery;
import org.secureauth.sarestapi.queries.IDMQueries;
import org.secureauth.sarestapi.queries.IPEvalQuery;
import org.secureauth.sarestapi.queries.NumberProfileQuery;
import org.secureauth.sarestapi.queries.StatusQuery;
import org.secureauth.sarestapi.queries.ThrottleQuery;
import org.secureauth.sarestapi.queries.ValidateOTPQuery;
import org.secureauth.sarestapi.resources.Resource;
import org.secureauth.sarestapi.resources.SAExecuter;
import org.secureauth.sarestapi.util.JSONUtil;
import org.secureauth.sarestapi.util.RestApiHeader;
import org.secureauth.sarestapi.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * @author rrowcliffe@secureauth.com
 *         <p>
 *         SAAccess is a class that allows access to the SecureAuth REST API.
 *         The intention is to provide an easy method to access
 *         the Secureauth Authentication Rest Services.
 *         </p>
 */

public class SAAccess implements ISAAccess {
    private static final String PUSH_TO_ACCEPT = "push_accept";
    private static final String SYMBOL_TO_ACCEPT = "push_accept_symbol";
    private static final Logger logger = LoggerFactory.getLogger(SAAccess.class);
    protected SABaseURL saBaseURL;
    protected SAAuth saAuth;
    protected SAExecuter saExecuter;
    protected SAConfig saConfig;

    /**
     * <p>
     * Returns a SAAccess Object that can be used to query the SecureAuth Rest API
     * This should be the default object used when setting up connectivity to the
     * SecureAuth Appliance
     * </p>
     *
     * @param host           FQDN of the SecureAuth Appliance
     * @param port           The port used to access the web application on the
     *                       Appliance.
     * @param ssl            Use SSL
     * @param realm          the Configured Realm that enables the RESTApi
     * @param applicationID  The Application ID from the Configured Realm
     * @param applicationKey The Application Key from the Configured Realm
     *
     * @deprecated from 1.0.6.0, replace by
     *             {@link org.secureauth.sarestapi.util.SAFactory}
     */
    @Deprecated
    public SAAccess(String host, String port, boolean ssl, String realm, String applicationID, String applicationKey) {
        saBaseURL = new SABaseURL(host, port, ssl);
        saAuth = new SAAuth(applicationID, applicationKey, realm);
        saExecuter = new SAExecuter(saBaseURL);
        saConfig = SAConfig.getInstance();
    }

    /**
     * <p>
     * Returns a SAAccess Object that can be used to query the SecureAuth Rest API
     * This should be the default object used when setting up connectivity to the
     * SecureAuth Appliance
     * This Object will allow users to support selfSigned Certificates
     * </p>
     *
     * @param host           FQDN of the SecureAuth Appliance
     * @param port           The port used to access the web application on the
     *                       Appliance.
     * @param ssl            Use SSL
     * @param selfSigned     Support for SeflSigned Certificates. Setting to enable
     *                       disable self signed cert support
     * @param realm          the Configured Realm that enables the RESTApi
     * @param applicationID  The Application ID from the Configured Realm
     * @param applicationKey The Application Key from the Configured Realm
     *
     * @deprecated from 1.0.6.0, replace by
     *             {@link org.secureauth.sarestapi.util.SAFactory}
     */
    @Deprecated
    public SAAccess(String host, String port, boolean ssl, boolean selfSigned, String realm, String applicationID,
            String applicationKey) {
        saBaseURL = new SABaseURL(host, port, ssl, selfSigned);
        saAuth = new SAAuth(applicationID, applicationKey, realm);
        saExecuter = new SAExecuter(saBaseURL);
        saConfig = SAConfig.getInstance();
    }

    /**
     * <p>
     * Returns a SAAccess Object that can be used to query the SecureAuth Rest API
     * This should be the default object used when setting up connectivity to the
     * SecureAuth Appliance
     * This Object will allow users to support selfSigned Certificates
     * </p>
     *
     * @param host           FQDN of the SecureAuth Appliance
     * @param port           The port used to access the web application on the
     *                       Appliance.
     * @param ssl            Use SSL
     * @param selfSigned     Support for SeflSigned Certificates. Setting to enable
     *                       disable self signed cert support
     * @param realm          the Configured Realm that enables the RESTApi
     * @param applicationID  The Application ID from the Configured Realm
     * @param applicationKey The Application Key from the Configured Realm
     * @param guidStrategy   The strategy used for the UUID
     *
     *
     * @deprecated from 1.0.6.0, replace by
     *             {@link org.secureauth.sarestapi.util.SAFactory}
     */
    @Deprecated
    public SAAccess(String host, String port, boolean ssl, boolean selfSigned, String realm, String applicationID,
            String applicationKey, GUIDStrategy guidStrategy) {
        saBaseURL = new SABaseURL(host, port, ssl, selfSigned);
        saAuth = new SAAuth(applicationID, applicationKey, realm);
        saExecuter = new SAExecuter(saBaseURL, guidStrategy);
        saConfig = SAConfig.getInstance();
    }

    /**
     * <p>
     * Returns a SAAccess Object that can be used to query the SecureAuth Rest API
     * This should be the default object used when setting up connectivity to the
     * SecureAuth Appliance
     * This Object will allow users to support selfSigned Certificates
     * </p>
     *
     * @param saBaseURL  {@link org.secureauth.sarestapi.data.SABaseURL}
     * @param saAuth     {@link org.secureauth.sarestapi.data.SAAuth}
     * @param saExecuter {@link org.secureauth.sarestapi.resources.SAExecuter}
     *
     */
    public SAAccess(SABaseURL saBaseURL, SAAuth saAuth, SAExecuter saExecuter) {
        this.saBaseURL = saBaseURL;
        this.saAuth = saAuth;
        this.saExecuter = saExecuter;
        saConfig = SAConfig.getInstance();
    }

    public void updateConfig(Hashtable<String, Object> config) {
        saConfig.updateConfig(config);
    }

    /**
     * <p>
     * Returns IP Risk Evaluation from the Rest API
     * </p>
     *
     * @param userId    The User ID that you want to validate from
     * @param ipAddress The IP Address of the user making the request for access
     * @return {@link org.secureauth.sarestapi.data.IPEval}
     *
     */
    public IPEval iPEvaluation(String userId, String ipAddress) {
        String ts = getServerTime();
        IPEvalRequest ipEvalRequest = new IPEvalRequest();
        ipEvalRequest.setIp_address(ipAddress);
        ipEvalRequest.setUser_id(userId);
        ipEvalRequest.setType(Resource.RISK);

        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST", IPEvalQuery.queryIPEval(saAuth.getRealm()),
                ipEvalRequest, ts);

        try {

            return saExecuter.executeIPEval(header,
                    saBaseURL.getApplianceURL() + IPEvalQuery.queryIPEval(saAuth.getRealm()), ipEvalRequest, ts);

        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }

        return null;
    }

    /**
     * <p>
     * Returns the list of Factors available for the specified user
     * Used for both /v1/users/factors and /v2/users/factors
     * </p>
     *
     * @param userId the userid of the identity you wish to have a list of possible
     *               second factors
     * @return {@link FactorsResponse}
     */
    public FactorsResponse factorsByUser(String userId) {
        String ts = getServerTime();
        String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_GET,
                FactorsQuery.queryFactors(saAuth.getRealm(), userId), ts);

        try {
            return saExecuter.executeGetRequest(header,
                    saBaseURL.getApplianceURL() + FactorsQuery.queryFactors(saAuth.getRealm(), userId), ts,
                    FactorsResponse.class);
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Returns the list of Factors available for the specified user.
     * Used for /v3/users/factors
     * Includes Preferred MFA configured/set by the admin/user.
     * </p>
     *
     * @param userId the userid of the identity you wish to have a list of possible
     *               second factors
     * @return {@link FactorsResponse}
     */
    public FactorsResponse factorsByUserV3(String userId) {
        String ts = getServerTime();
        String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_GET,
                FactorsQuery.queryFactorsV3(saAuth.getRealm(), userId), ts);

        try {
            return saExecuter.executeGetRequest(header,
                    saBaseURL.getApplianceURL() + FactorsQuery.queryFactorsV3(saAuth.getRealm(), userId), ts,
                    FactorsResponse.class);
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Returns the list of Factors available for the specified user supporting
     * special characters
     * Used for both /v1/users/factors and /v2/users/factors
     * </p>
     *
     * @param userId the userid of the identity you wish to have a list of possible
     *               second factors. This method supports special characters for
     *               userId since it uses QP (Query Params) in order to create the
     *               request.
     * @return {@link FactorsResponse}
     */
    public FactorsResponse factorsByUserQP(String userId) {
        String ts = getServerTime();
        String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_GET,
                FactorsQuery.queryFactorsQP(saAuth.getRealm()), ts);

        try {
            return saExecuter.executeGetRequest(header,
                    saBaseURL.getApplianceURL() + FactorsQuery.queryFactorsQP(saAuth.getRealm()), userId, ts,
                    FactorsResponse.class);

        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Returns the list of Factors available for the specified user supporting
     * special characters
     * Used for /v3/users/factors
     * Includes Preferred MFA configured/set by the admin/user.
     * </p>
     *
     * @param userId the userid of the identity you wish to have a list of possible
     *               second factors. This method supports special characters for
     *               userId since it uses QP (Query Params) in order to create the
     *               request.
     * @return {@link FactorsResponse}
     */
    public FactorsResponse factorsByUserQPV3(String userId) {
        String ts = getServerTime();
        String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_GET,
                FactorsQuery.queryFactorsQPV3(saAuth.getRealm()), ts);

        try {
            return saExecuter.executeGetRequest(header,
                    saBaseURL.getApplianceURL() + FactorsQuery.queryFactorsQPV3(saAuth.getRealm()), userId, ts,
                    FactorsResponse.class);
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Send push to accept request asynchronously
     * </p>
     *
     * @param userId            the user id of the identity
     * @param factorId          the P2A Id to be compared against
     * @param endUserIP         The End Users IP Address
     * @param clientCompany     The Client Company Name
     * @param clientDescription The Client Description
     * @return {@link FactorsResponse}
     */
    public ResponseObject sendPushToAcceptReq(String userId, String factorId, String endUserIP, String clientCompany,
            String clientDescription) {
        return sendPushReq(userId, factorId, endUserIP, clientCompany, clientDescription, PUSH_TO_ACCEPT);
    }

    @Override
    public StatefulResponseObject sendPushToAcceptReqStateful(String userId, String factorId, String endUserIP,
            String clientCompany, String clientDescription) {
        return sendPushToAcceptReqStatefulForType(userId, factorId, endUserIP, clientCompany, clientDescription,
                PUSH_TO_ACCEPT);
    }

    public ResponseObject sendPushToAcceptSymbolReq(String userId, String factorId, String endUserIP,
            String clientCompany, String clientDescription) {
        return sendPushReq(userId, factorId, endUserIP, clientCompany, clientDescription, SYMBOL_TO_ACCEPT);
    }

    @Override
    public StatefulResponseObject sendPushToAcceptSymbolReqStateful(String userId, String factorId, String endUserIP,
            String clientCompany, String clientDescription) {
        return sendPushToAcceptReqStatefulForType(userId, factorId, endUserIP, clientCompany, clientDescription,
                SYMBOL_TO_ACCEPT);
    }

    private StatefulResponseObject sendPushToAcceptReqStatefulForType(String userId, String factorId, String endUserIP,
            String clientCompany, String clientDescription, String type) {
        String ts = getServerTime();
        PushToAcceptRequest req = PushToAcceptRequestsFactory.createPushToAcceptRequest(userId, factorId, endUserIP,
                clientCompany, clientDescription, type);
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST", AuthQuery.queryAuth(saAuth.getRealm()),
                req, ts);
        try {
            return saExecuter.executePostRequestStateful(header,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAuth(saAuth.getRealm()), req, ts,
                    StatefulResponseObject.class);
        } catch (Exception e) {
            throw new SARestAPIException(e);
        }
    }

    private ResponseObject sendPushReq(String userid, String factor_id, String endUserIP, String clientCompany,
            String clientDescription, String type) {
        String ts = getServerTime();
        PushToAcceptRequest req = PushToAcceptRequestsFactory.createPushToAcceptRequest(userid, factor_id, endUserIP,
                clientCompany, clientDescription, type);
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST", AuthQuery.queryAuth(saAuth.getRealm()),
                req, ts);
        try {
            return saExecuter.executePostRequest(header,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAuth(saAuth.getRealm()), req, ts,
                    ResponseObject.class);
        } catch (Exception e) {
            throw new SARestAPIException(e);
        }
    }

    /**
     * <p>
     * Send push to accept biometric request asynchronously
     * </p>
     *
     * @param biometricType     fingerprint, face
     * @param userId            the user id of the identity
     * @param factorId          the P2A Id to be compared against
     * @param endUserIP         The End Users IP Address
     * @param clientCompany     The Client Company Name
     * @param clientDescription The Client Description
     * @return {@link FactorsResponse}
     */
    public ResponseObject sendPushBiometricReq(String biometricType, String userId, String factorId, String endUserIP,
            String clientCompany, String clientDescription) {
        String ts = getServerTime();
        PushToAcceptBiometricsRequest req = PushToAcceptRequestsFactory.createPushToAcceptBiometricRequest(
                biometricType, userId, factorId, endUserIP, clientCompany, clientDescription);
        String header = RestApiHeader.getAuthorizationHeader(this.saAuth, "POST",
                AuthQuery.queryAuth(this.saAuth.getRealm()), req, ts);
        try {
            return this.saExecuter.executePostRequest(header,
                    this.saBaseURL.getApplianceURL() + AuthQuery.queryAuth(this.saAuth.getRealm()), req, ts,
                    ResponseObject.class);
        } catch (Exception e) {
            throw new SARestAPIException(e);
        }
    }

    @Override
    public StatefulResponseObject sendPushBiometricReqStateful(String biometricType, String userId, String factorId,
            String endUserIP, String clientCompany, String clientDescription) {
        String ts = getServerTime();
        PushToAcceptBiometricsRequest req = PushToAcceptRequestsFactory.createPushToAcceptBiometricRequest(
                biometricType, userId, factorId, endUserIP, clientCompany, clientDescription);
        String header = RestApiHeader.getAuthorizationHeader(this.saAuth, "POST",
                AuthQuery.queryAuth(this.saAuth.getRealm()), req, ts);
        try {
            return saExecuter.executePostRequestStateful(header,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAuth(saAuth.getRealm()), req, ts,
                    StatefulResponseObject.class);
        } catch (Exception e) {
            throw new SARestAPIException(e);
        }
    }

    /**
     * <p>
     * Perform adaptive auth query
     * </p>
     *
     * @param userId    the user id of the identity
     * @param endUserIP the IP of requesting client
     * @return {@link FactorsResponse}
     */
    public AdaptiveAuthResponse adaptiveAuthQuery(String userId, String endUserIP) {
        String ts = getServerTime();
        AdaptiveAuthRequest req = new AdaptiveAuthRequest(userId, endUserIP);
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST", AuthQuery.queryAAuth(saAuth.getRealm()),
                req, ts);

        try {
            return saExecuter.executePostRequest(header,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAAuth(saAuth.getRealm()), req, ts,
                    AdaptiveAuthResponse.class);
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    public PushAcceptStatus queryPushAcceptStatus(String refId) {
        String ts = getServerTime();
        String getUri = makePushNotificationQueryURI(refId);
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "GET", getUri, ts);
        try {
            return saExecuter.executeGetRequest(header, saBaseURL.getApplianceURL() + getUri, ts,
                    PushAcceptStatus.class);
        } catch (Exception e) {
            throw new SARestAPIException(e);
        }
    }

    @Override
    public PushAcceptStatus queryPushAcceptStatusStateful(String refId, Cookie sessionAffinityCookie) {
        String ts = getServerTime();
        String getUri = makePushNotificationQueryURI(refId);
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "GET", getUri, ts);
        try {
            return saExecuter.executeGetRequestStateful(header, sessionAffinityCookie,
                    saBaseURL.getApplianceURL() + getUri, ts, PushAcceptStatus.class);
        } catch (Exception e) {
            throw new SARestAPIException(e);
        }
    }

    private String makePushNotificationQueryURI(String refId) {
        return AuthQuery.queryAuth(saAuth.getRealm()) + "/" + refId;
    }

    /**
     *
     * <p>
     * Checks if the Username exists within the datastore within SecureAuth
     * </p>
     *
     * @param userId the userid of the identity
     * @return {@link ResponseObject}
     */
    public BaseResponse validateUser(String userId) {
        String ts = getServerTime();
        AuthRequest authRequest = new AuthRequest();

        authRequest.setUser_id(userId);
        authRequest.setType(Resource.USER_ID);

        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST", AuthQuery.queryAuth(saAuth.getRealm()),
                authRequest, ts);

        try {
            return saExecuter.executeValidateUser(header,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAuth(saAuth.getRealm()), authRequest, ts);
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * the OTP throttling count to 0 after the end-user successfully authenticates;
     * the attempt count is stored in a directory attribute configured in the Web
     * Admin
     *
     * @param userId id of user
     * @return base answer
     */
    public ThrottleResponse resetThrottleReq(String userId) {
        try {
            String ts = getServerTime();
            ThrottleRequest throttleRequest = new ThrottleRequest(0);

            String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_PUT,
                    ThrottleQuery.queryThrottles(saAuth.getRealm(), userId), throttleRequest, ts);

            return saExecuter.executePutRequest(header,
                    saBaseURL.getApplianceURL() + ThrottleQuery.queryThrottles(saAuth.getRealm(), userId),
                    throttleRequest, ThrottleResponse.class, ts);
        } catch (Exception e) {
            throw new SARestAPIException("Exception occurred executing REST query:\n" + e.getMessage());
        }
    }

    /**
     * the OTP throttling count to 0 after the end-user successfully authenticates;
     * the attempt count is stored in a directory attribute configured in the Web
     * Admin
     *
     * @param userId id of user. This method supports special characters for userId
     *               since it uses QP (Query Params) in order to create the request.
     * @return base answer
     */
    public ThrottleResponse resetThrottleReqQP(String userId) {
        try {
            String ts = getServerTime();
            ThrottleRequest throttleRequest = new ThrottleRequest(0);

            String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_PUT,
                    ThrottleQuery.queryThrottlesQP(saAuth.getRealm()), throttleRequest, ts);

            return saExecuter.executePutRequest(header,
                    saBaseURL.getApplianceURL() + ThrottleQuery.queryThrottlesQP(saAuth.getRealm()), userId,
                    throttleRequest, ThrottleResponse.class, ts);
        } catch (Exception e) {
            throw new SARestAPIException("Exception occurred executing REST query:\n" + e.getMessage(), e);
        }
    }

    /**
     * GET the end-user's current count of OTP usage attempts
     *
     * @param userId id of user
     * @return base answer
     */
    public ThrottleResponse getThrottleReq(String userId) {
        try {
            String ts = getServerTime();
            String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_GET,
                    ThrottleQuery.queryThrottles(saAuth.getRealm(), userId), ts);

            return saExecuter.executeGetRequest(header,
                    saBaseURL.getApplianceURL() + ThrottleQuery.queryThrottles(saAuth.getRealm(), userId), ts,
                    ThrottleResponse.class);
        } catch (Exception e) {
            throw new SARestAPIException("Exception occurred executing REST query:\n" + e.getMessage());
        }
    }

    /**
     * GET the end-user's current count of OTP usage attempts
     *
     * @param userId id of user. This method supports special characters for userId
     *               since it uses QP (Query Params) in order to create the request.
     * @return base answer
     */
    public ThrottleResponse getThrottleReqQP(String userId) {
        try {
            String ts = getServerTime();
            String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_GET,
                    ThrottleQuery.queryThrottlesQP(saAuth.getRealm()), ts);

            return saExecuter.executeGetRequest(header,
                    saBaseURL.getApplianceURL() + ThrottleQuery.queryThrottlesQP(saAuth.getRealm()), userId, ts,
                    ThrottleResponse.class);
        } catch (Exception e) {
            throw new SARestAPIException("Exception occurred executing REST query:\n" + e.getMessage());
        }
    }

    /**
     * <p>
     * Checks the users password against SecureAuth Datastore
     * </p>
     *
     * @param userId   the userid of the identity
     * @param password The password of the user to validate
     * @return {@link ResponseObject}
     */
    public BaseResponse validateUserPassword(String userId, String password) {
        return validateUserPassword(userId, password, "");
    }

    /**
     * <p>
     * Checks the users password against SecureAuth Datastore
     * </p>
     *
     * @param userId    the userid of the identity
     * @param password  The password of the user to validate
     * @param enduserIp the enduser's IP Address
     * @return {@link ResponseObject}
     */
    public BaseResponse validateUserPassword(String userId, String password, String enduserIp) {
        String ts = getServerTime();
        AuthRequest authRequest = new AuthRequest();

        authRequest.setUser_id(userId);
        authRequest.setType(Resource.PASSWORD);
        authRequest.setToken(password);
        authRequest.setEnduser_ip(enduserIp);

        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST", AuthQuery.queryAuth(saAuth.getRealm()),
                authRequest, ts);

        try {
            return saExecuter.executeValidateUserPassword(header,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAuth(saAuth.getRealm()), authRequest, ts);
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Checks the users pin against SecureAuth Datastore
     * </p>
     *
     * @param userId the userid of the identity
     * @param pin    The pin of the user to validate
     * @return {@link ResponseObject}
     */
    public BaseResponse validateUserPin(String userId, String pin) {
        String ts = getServerTime();
        AuthRequest authRequest = new AuthRequest();

        authRequest.setUser_id(userId);
        authRequest.setType(Resource.PIN);
        authRequest.setToken(pin);

        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST", AuthQuery.queryAuth(saAuth.getRealm()),
                authRequest, ts);

        try {
            return saExecuter.executeValidateUserPin(header,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAuth(saAuth.getRealm()), authRequest, ts);
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Validate the users Answer to a KB Question
     * </p>
     *
     * @param userId   the userid of the identity
     * @param answer   The answer to the KBA
     * @param factorId the KB Id to be compared against
     * @return {@link ResponseObject}
     */
    public BaseResponse validateKba(String userId, String answer, String factorId) {
        String ts = getServerTime();
        AuthRequest authRequest = new AuthRequest();

        authRequest.setUser_id(userId);
        authRequest.setType(Resource.KBA);
        authRequest.setToken(answer);
        authRequest.setFactor_id(factorId);

        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST", AuthQuery.queryAuth(saAuth.getRealm()),
                authRequest, ts);

        try {
            return saExecuter.executeValidateKba(header,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAuth(saAuth.getRealm()), authRequest, ts);
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Validate the Oath Token
     * </p>
     *
     * @param userId   the userid of the identity
     * @param otp      The One Time Passcode to validate
     * @param factorId The Device Identifier
     * @return {@link ResponseObject}
     */
    public BaseResponse validateOath(String userId, String otp, String factorId) {
        String ts = getServerTime();
        AuthRequest authRequest = new AuthRequest();

        authRequest.setUser_id(userId);
        authRequest.setType(Resource.OATH);
        authRequest.setToken(otp);
        authRequest.setFactor_id(factorId);

        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST", AuthQuery.queryAuth(saAuth.getRealm()),
                authRequest, ts);

        try {
            return saExecuter.executeValidateOath(header,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAuth(saAuth.getRealm()), authRequest, ts);
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Send One Time Passcode by Phone
     * </p>
     *
     * @param userId   the userid of the identity
     * @param factorId Phone Property "Phone1"
     * @return {@link ResponseObject}
     */
    public ResponseObject deliverOTPByPhone(String userId, String factorId) {
        String ts = getServerTime();
        AuthRequest authRequest = new AuthRequest();

        authRequest.setUser_id(userId);
        authRequest.setType(Resource.CALL);
        authRequest.setFactor_id(factorId);

        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST", AuthQuery.queryAuth(saAuth.getRealm()),
                authRequest, ts);

        try {
            return saExecuter.executeOTPByPhone(header,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAuth(saAuth.getRealm()), authRequest, ts);
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Send One Time Passcode by Phone Ad Hoc
     * </p>
     *
     * @param userId      the userid of the identity
     * @param phoneNumber Phone Number to call
     * @return {@link ResponseObject}
     */
    public ResponseObject deliverAdHocOTPByPhone(String userId, String phoneNumber) {
        String ts = getServerTime();
        AuthRequest authRequest = new AuthRequest();

        authRequest.setUser_id(userId);
        authRequest.setType(Resource.CALL);
        authRequest.setToken(phoneNumber);

        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST", AuthQuery.queryAuth(saAuth.getRealm()),
                authRequest, ts);

        try {
            return saExecuter.executeOTPByPhone(header,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAuth(saAuth.getRealm()), authRequest, ts);
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Send One Time Passcode by SMS to Registered User
     * </p>
     *
     * @param userId   the userid of the identity
     * @param factorId Phone Property "Phone1"
     * @return {@link ResponseObject}
     */
    public ResponseObject deliverOTPBySMS(String userId, String factorId) {
        String ts = getServerTime();
        AuthRequest authRequest = new AuthRequest();

        authRequest.setUser_id(userId);
        authRequest.setType(Resource.SMS);
        authRequest.setFactor_id(factorId);
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST", AuthQuery.queryAuth(saAuth.getRealm()),
                authRequest, ts);

        try {
            return saExecuter.executeOTPBySMS(header,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAuth(saAuth.getRealm()), authRequest, ts);
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Validate One Time Passcode sent by SMS
     * </p>
     *
     * @param userId the userid of the identity
     * @param otp    OTP Value to compare against what was sent
     * @return {@link ValidateOTPResponse}
     */
    public ValidateOTPResponse validateOTP(String userId, String otp) {
        String ts = getServerTime();
        ValidateOTPRequest validateOTPRequest = new ValidateOTPRequest();

        validateOTPRequest.setUser_id(userId);
        validateOTPRequest.setOtp(otp);

        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST",
                ValidateOTPQuery.queryValidateOTP(saAuth.getRealm()), validateOTPRequest, ts);

        try {
            return saExecuter.executeValidateOTP(header,
                    saBaseURL.getApplianceURL() + ValidateOTPQuery.queryValidateOTP(saAuth.getRealm()),
                    validateOTPRequest, ts);
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Send One Time Passcode by SMS Ad Hoc
     * </p>
     *
     * @param userId      the userid of the identity
     * @param phoneNumber Phone Number to send SMS to
     * @return {@link ResponseObject}
     */
    public ResponseObject deliverAdHocOTPBySMS(String userId, String phoneNumber) {
        String ts = getServerTime();
        AuthRequest authRequest = new AuthRequest();

        authRequest.setUser_id(userId);
        authRequest.setType(Resource.SMS);
        authRequest.setToken(phoneNumber);
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST", AuthQuery.queryAuth(saAuth.getRealm()),
                authRequest, ts);

        try {
            return saExecuter.executeOTPBySMS(header,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAuth(saAuth.getRealm()), authRequest, ts);
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Send yubikey validation
     * </p>
     *
     * @param userId       the userid of the identity
     * @param yubikeyToken the generated token by the yubikey
     * @return {@link BaseResponse}
     */
    public BaseResponse validateYubicoToken(String userId, String yubikeyToken) {
        String ts = getServerTime();
        AuthRequest authRequest = new AuthRequest();

        authRequest.setUser_id(userId);
        authRequest.setType(Resource.YUBIKEY);
        authRequest.setToken(yubikeyToken);

        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST", AuthQuery.queryAuth(saAuth.getRealm()),
                authRequest, ts);

        try {
            return saExecuter.executeValidateUserPassword(header,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAuth(saAuth.getRealm()), authRequest, ts);
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Send One Time Passcode by Email to Help Desk
     * </p>
     *
     * @param userId   the userid of the identity
     * @param factorId Help Desk Property "HelpDesk1"
     * @return {@link ResponseObject}
     */
    public ResponseObject deliverHelpDeskOTPByEmail(String userId, String factorId) {
        String ts = getServerTime();
        AuthRequest authRequest = new AuthRequest();

        authRequest.setUser_id(userId);
        authRequest.setType(Resource.HELP_DESK);
        authRequest.setFactor_id(factorId);
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST", AuthQuery.queryAuth(saAuth.getRealm()),
                authRequest, ts);

        try {
            return saExecuter.executeOTPByEmail(header,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAuth(saAuth.getRealm()), authRequest, ts);
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Send One Time Passcode by Email
     * </p>
     *
     * @param userId   the userid of the identity
     * @param factorId Email Property "Email1"
     * @return {@link ResponseObject}
     */
    public ResponseObject deliverOTPByEmail(String userId, String factorId) {
        String ts = getServerTime();
        AuthRequest authRequest = new AuthRequest();

        authRequest.setUser_id(userId);
        authRequest.setType(Resource.EMAIL);
        authRequest.setFactor_id(factorId);
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST", AuthQuery.queryAuth(saAuth.getRealm()),
                authRequest, ts);

        try {
            return saExecuter.executeOTPByEmail(header,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAuth(saAuth.getRealm()), authRequest, ts);
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Send One Time Passcode by Email Ad Hoc
     * </p>
     *
     * @param userId       the userid of the identity
     * @param emailAddress Email Address
     * @return {@link ResponseObject}
     */
    public ResponseObject deliverAdHocOTPByEmail(String userId, String emailAddress) {
        String ts = getServerTime();
        AuthRequest authRequest = new AuthRequest();

        authRequest.setUser_id(userId);
        authRequest.setType(Resource.EMAIL);
        authRequest.setToken(emailAddress);
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST", AuthQuery.queryAuth(saAuth.getRealm()),
                authRequest, ts);

        try {
            return saExecuter.executeOTPByEmail(header,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAuth(saAuth.getRealm()), authRequest, ts);
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Send One Time Passcode by Push
     * </p>
     *
     * @param userId   the userid of the identity
     * @param factorId Device Property "z0y9x87wv6u5t43srq2p1on"
     * @return {@link ResponseObject}
     */
    public ResponseObject deliverOTPByPush(String userId, String factorId) {
        String ts = getServerTime();
        AuthRequest authRequest = new AuthRequest();

        authRequest.setUser_id(userId);
        authRequest.setType("push");
        authRequest.setFactor_id(factorId);
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST", AuthQuery.queryAuth(saAuth.getRealm()),
                authRequest, ts);

        try {
            return saExecuter.executePostRequest(header,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAuth(saAuth.getRealm()), authRequest, ts,
                    ResponseObject.class);
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Send One Time Passcode by Helpdesk
     * </p>
     *
     * @param userId   the userid of the identity
     * @param factorId Help Desk Property "HelpDesk1"
     * @return {@link ResponseObject}
     */
    public ResponseObject deliverOTPByHelpDesk(String userId, String factorId) {
        String ts = getServerTime();
        AuthRequest authRequest = new AuthRequest();

        authRequest.setUser_id(userId);
        authRequest.setType("help_desk");
        authRequest.setFactor_id(factorId);
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST", AuthQuery.queryAuth(saAuth.getRealm()),
                authRequest, ts);

        try {
            return saExecuter.executeOTPByHelpDesk(header,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAuth(saAuth.getRealm()), authRequest, ts);
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Send Link to accept by email
     * The response is as follows
     * {
     * "reference_id": "xxxxxxxxxxxxxxxxx",
     * "status": "valid",
     * "message": "",
     * "user_id": "xxxxxxxxxxx"
     * }
     * </p>
     *
     * @param userId   the userid of the identity
     * @param factorId Email Property "Email1"
     * @return {@link StatefulResponseObject}
     */
    public StatefulResponseObject emailLink(String userId, String factorId) {
        String ts = getServerTime();
        AuthRequest authRequest = LinkToAcceptFactory.createLinkToAcceptAuthRequest(userId, factorId,
                Resource.EMAIL_LINK);
        return getStatefulResponseObject(ts, authRequest);
    }

    /**
     * <p>
     * Send Link to accept by email
     * The response is as follows
     * {
     * "reference_id": "xxxxxxxxxxxxxxxxx",
     * "status": "valid",
     * "message": "",
     * "user_id": "xxxxxxxxxxx"
     * }
     * </p>
     *
     * @param userId   the userid of the identity
     * @param factorId Phone Property "Phone1"
     * @return {@link StatefulResponseObject}
     */
    public StatefulResponseObject smsLink(String userId, String factorId) {
        String ts = getServerTime();
        AuthRequest authRequest = LinkToAcceptFactory.createLinkToAcceptAuthRequest(userId, factorId,
                Resource.SMS_LINK);
        return getStatefulResponseObject(ts, authRequest);
    }

    private StatefulResponseObject getStatefulResponseObject(String ts, AuthRequest authRequest) {
        String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_POST,
                AuthQuery.queryAuth(saAuth.getRealm()), authRequest, ts);

        try {
            return saExecuter.executePostRequestStateful(header,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAuth(saAuth.getRealm()), authRequest, ts,
                    StatefulResponseObject.class);
        } catch (Exception e) {
            throw new SARestAPIException(e);
        }
    }

    /**
     * <p>
     * Verify Link to accept using code
     * </p>
     *
     * @param linkId the id provided when making a link to accept request
     * @return {@link PushAcceptStatus}
     */
    public PushAcceptStatus verifyLinkToAcceptStatus(String linkId, Cookie cookie) {
        String ts = getServerTime();
        String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_GET,
                AuthQuery.queryAuthLink(saAuth.getRealm(), linkId), ts);

        try {
            return saExecuter.executeGetRequestStateful(header, cookie,
                    saBaseURL.getApplianceURL() + AuthQuery.queryAuthLink(saAuth.getRealm(), linkId), ts,
                    PushAcceptStatus.class);
        } catch (Exception e) {
            throw new SARestAPIException(e);
        }
    }

    /**
     * <p>
     * Returns response to Access History Post Rest API
     * </p>
     *
     * @param userId    The User ID that you want to validate from
     * @param ipAddress The IP Address of the user to be stored in the Datastore for
     *                  use when evaluating Geo-Velocity
     * @return {@link AccessHistoryRequest}
     *
     */
    public ResponseObject accessHistory(String userId, String ipAddress) {
        String ts = getServerTime();
        AccessHistoryRequest accessHistoryRequest = new AccessHistoryRequest();
        accessHistoryRequest.setIp_address(ipAddress);
        accessHistoryRequest.setUser_id(userId);

        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST",
                AccessHistoryQuery.queryAccessHistory(saAuth.getRealm()), accessHistoryRequest, ts);

        try {

            return saExecuter.executeAccessHistory(header,
                    saBaseURL.getApplianceURL() + AccessHistoryQuery.queryAccessHistory(saAuth.getRealm()),
                    accessHistoryRequest, ts);

        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }

        return null;
    }

    /**
     * <p>
     * Update user access history
     * </p>
     * 
     * @param userid    the user id of the identity
     * @param endUserIP the IP of requesting client
     * @return base response object
     */
    public ResponseObject updateAccessHistory(String userid, String endUserIP) {
        String ts = getServerTime();
        UpdateUserHistoryRequest req = new UpdateUserHistoryRequest(userid, endUserIP);
        String uri = saAuth.getRealm() + "/api/" + Resource.API_VERSION + "/accesshistory";
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST", uri, req, ts);

        try {
            return saExecuter.executePostRequest(header, saBaseURL.getApplianceURL() + uri, req, ts,
                    ResponseObject.class);
        } catch (Exception e) {
            logger.error("Update user access history error", e);
        }
        return null;
    }

    /**
     * <p>
     * Confirm the DFP data from Client using the Rest API
     * </p>
     *
     * @param userId        The User ID that you want to validate from
     * @param fingerprintId The ID of the finger print to check against the data
     *                      store
     * @return {@link DFPConfirmResponse}
     *
     */
    public DFPConfirmResponse DFPConfirm(String userId, String fingerprintId) {
        String ts = getServerTime();
        DFPConfirmRequest dfpConfirmRequest = new DFPConfirmRequest();
        dfpConfirmRequest.setUser_id(userId);
        dfpConfirmRequest.setFingerprint_id(fingerprintId);

        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST",
                DFPQuery.queryDFPConfirm(saAuth.getRealm()), dfpConfirmRequest, ts);

        try {

            return saExecuter.executeDFPConfirm(header,
                    saBaseURL.getApplianceURL() + DFPQuery.queryDFPConfirm(saAuth.getRealm()), dfpConfirmRequest, ts);

        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }

        return null;
    }

    /**
     * <p>
     * Validate the DFP data from Client using the Rest API
     * </p>
     *
     * @param userId      The User ID that you want to validate from
     * @param hostAddress The ID of the finger print to check against the data store
     * @param jsonString  The JSON String provided by the Java Script
     * @return {@link DFPValidateResponse}
     *
     */
    public DFPValidateResponse DFPValidateNewFingerprint(String userId, String hostAddress, String jsonString) {
        String ts = getServerTime();
        DFPValidateRequest dfpValidateRequest = new DFPValidateRequest();
        DFP dfp = JSONUtil.getDFPFromJSONString(jsonString);
        dfpValidateRequest.setFingerprint(dfp);
        dfpValidateRequest.setUser_id(userId);
        dfpValidateRequest.setHost_address(hostAddress);

        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST",
                DFPQuery.queryDFPValidate(saAuth.getRealm()), dfpValidateRequest, ts);

        try {

            return saExecuter.executeDFPValidate(header,
                    saBaseURL.getApplianceURL() + DFPQuery.queryDFPValidate(saAuth.getRealm()), dfpValidateRequest, ts);

        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }

        return null;
    }

    @Override
    public DFPValidateResponse DFPValidateNewFingerprint(DFP fingerprint) {
        String ts = getServerTime();

        String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_POST,
                DFPQuery.queryDFPValidate(saAuth.getRealm()), fingerprint, ts);

        try {
            return saExecuter.executePostRawRequest(header,
                    saBaseURL.getApplianceURL() + DFPQuery.queryDFPValidate(saAuth.getRealm()), fingerprint,
                    DFPValidateResponse.class, ts);
        } catch (Exception e) {
            throw new SARestAPIException("Exception occurred executing score fingerprint", e);
        }
    }

    @Override
    public DFPValidateResponse DFPScoreFingerprint(String userId, String hostAddress, String fingerprintId,
            String fingerPrintJSON) {
        try {
            String ts = getServerTime();
            DFPConfirmRequest dfpConfirmRequest = new DFPConfirmRequest(userId, fingerprintId);
            DFP dfp = JSONUtil.getDFPFromJSONString(fingerPrintJSON);
            DFPValidateRequest dfpValidateRequest = new DFPValidateRequest(userId, hostAddress, dfp);

            DFPScoreRequest dfpScoreRequest = new DFPScoreRequest(dfpConfirmRequest, dfpValidateRequest);

            String query = DFPQuery.queryDFPScore(saAuth.getRealm());
            String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_POST, query, dfpScoreRequest,
                    ts);
            return saExecuter.executePostRawRequest(header, saBaseURL.getApplianceURL() + query, dfpScoreRequest,
                    DFPValidateResponse.class, ts);

        } catch (Exception e) {
            throw new SARestAPIException("Exception occurred executing score fingerprint", e);
        }
    }

    @Override
    public DFPValidateResponse DFPScoreFingerprint(DFP fingerprint) {
        String ts = getServerTime();

        String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_POST,
                DFPQuery.queryDFPScore(saAuth.getRealm()), fingerprint, ts);

        try {
            return saExecuter.executePostRawRequest(header,
                    saBaseURL.getApplianceURL() + DFPQuery.queryDFPScore(saAuth.getRealm()), fingerprint,
                    DFPValidateResponse.class, ts);
        } catch (Exception e) {
            throw new SARestAPIException("Exception occurred executing score fingerprint", e);
        }
    }

    @Override
    public DFPValidateResponse DFPSaveFingerprint(String userId, String hostAddress, String fingerprintId,
            String fingerPrintJSON) {
        try {
            String ts = getServerTime();
            DFPConfirmRequest dfpConfirmRequest = new DFPConfirmRequest(userId, fingerprintId);
            DFP dfp = JSONUtil.getDFPFromJSONString(fingerPrintJSON);
            DFPValidateRequest dfpValidateRequest = new DFPValidateRequest(userId, hostAddress, dfp);

            DFPScoreRequest dfpScoreRequest = new DFPScoreRequest(dfpConfirmRequest, dfpValidateRequest);

            String query = DFPQuery.queryDFPSave(saAuth.getRealm());
            String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_POST, query, dfpScoreRequest,
                    ts);
            return saExecuter.executePostRawRequest(header, saBaseURL.getApplianceURL() + query, dfpScoreRequest,
                    DFPValidateResponse.class, ts);

        } catch (Exception e) {
            throw new SARestAPIException("Exception occurred executing save fingerprint", e);
        }
    }

    /**
     * <p>
     * Returns the url for the JavaScript Source for DFP
     * </p>
     *
     * @return {@link JSObjectResponse}
     */
    public JSObjectResponse javaScriptSrc() {
        String ts = getServerTime();
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "GET", DFPQuery.queryDFPjs(saAuth.getRealm()), ts);

        try {
            return saExecuter.executeGetJSObject(header,
                    saBaseURL.getApplianceURL() + DFPQuery.queryDFPjs(saAuth.getRealm()), ts, JSObjectResponse.class);

        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * Start of Behavior Bio Metrics Methods
     *
     */

    /**
     * <p>
     * Returns the url for the JavaScript Source for BehaveBioMetrics
     * </p>
     *
     * @return {@link JSObjectResponse}
     */
    public JSObjectResponse BehaveBioJSSrc() {
        String ts = getServerTime();
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "GET",
                BehaveBioQuery.queryBehaveBiojs(saAuth.getRealm()), ts);

        try {
            return saExecuter.executeGetJSObject(header,
                    saBaseURL.getApplianceURL() + BehaveBioQuery.queryBehaveBiojs(saAuth.getRealm()), ts,
                    JSObjectResponse.class);

        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Submit Behave Bio Profile using the Rest API
     * </p>
     *
     * @param userId          The User ID that you want to validate from
     * @param behaviorProfile The Behavioral Profile of the user
     * @param hostAddress     The IP Address of the user
     * @param userAgent       The Browser User Agent of the user
     *
     * @return {@link BehaveBioResponse}
     *
     */
    public BehaveBioResponse BehaveBioProfileSubmit(String userId, String behaviorProfile, String hostAddress,
            String userAgent) {
        String ts = getServerTime();
        BehaveBioRequest behaveBioRequest = new BehaveBioRequest();
        behaveBioRequest.setUserId(userId);
        behaveBioRequest.setBehaviorProfile(behaviorProfile);
        behaveBioRequest.setHostAddress(hostAddress);
        behaveBioRequest.setUserAgent(userAgent);

        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST",
                BehaveBioQuery.queryBehaveBio(saAuth.getRealm()), behaveBioRequest, ts);

        try {

            return saExecuter.executeBehaveBioPost(header,
                    saBaseURL.getApplianceURL() + BehaveBioQuery.queryBehaveBio(saAuth.getRealm()), behaveBioRequest,
                    ts);

        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }

        return null;
    }

    /**
     * <p>
     * Submit Reset Request to Behave Bio Profile using the Rest API
     * </p>
     *
     * @param userId     The User ID that you want to validate from
     * @param fieldName  The Behavioral FieldName to Reset
     * @param fieldType  The Behavioral FieldType to Reset
     * @param deviceType The Behavioral DeviceType to Reset
     *
     * @return {@link ResponseObject}
     *
     */
    public ResponseObject BehaveBioProfileReset(String userId, String fieldName, String fieldType, String deviceType) {
        String ts = getServerTime();
        BehaveBioResetRequest behaveBioResetRequest = new BehaveBioResetRequest();
        behaveBioResetRequest.setUserId(userId);
        behaveBioResetRequest.setFieldName(fieldName);
        behaveBioResetRequest.setFieldType(fieldType);
        behaveBioResetRequest.setDeviceType(deviceType);

        String header = RestApiHeader.getAuthorizationHeader(saAuth, "PUT",
                BehaveBioQuery.queryBehaveBio(saAuth.getRealm()), behaveBioResetRequest, ts);

        try {

            return saExecuter.executeBehaveBioReset(header,
                    saBaseURL.getApplianceURL() + BehaveBioQuery.queryBehaveBio(saAuth.getRealm()),
                    behaveBioResetRequest, ts);

        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }

        return null;
    }

    /**
     * END of Behavior Bio Metrics Methods
     *
     */

    /**
     * Start of IDM Methods
     */

    /**
     * <p>
     * Creates User / Profile
     * </p>
     *
     * @param newUserProfile The newUserProfile Object
     * @return {@link ResponseObject}
     */
    public ResponseObject createUser(NewUserProfile newUserProfile) {
        try {
            validateUser(newUserProfile);
            String ts = getServerTime();
            sortKBQKBAbyKey(newUserProfile);
            String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_POST,
                    IDMQueries.queryUsers(saAuth.getRealm()), newUserProfile, ts);

            return saExecuter.executeUserProfileCreateRequest(header,
                    saBaseURL.getApplianceURL() + IDMQueries.queryUsers(saAuth.getRealm()), newUserProfile, ts,
                    ResponseObject.class);

        } catch (Exception e) {
            logger.error("Exception occurred executing REST query:\n" + e.getMessage() + "\n", e);
        }
        return null;
    }

    /**
     * Check mandatory fields for creating a user.
     *
     * @param newUserProfile
     * @return
     */
    private void validateUser(NewUserProfile newUserProfile) {
        if (newUserProfile.getUserId() == null || newUserProfile.getUserId().isEmpty() ||
                newUserProfile.getPassword() == null || newUserProfile.getPassword().isEmpty()) {
            throw new IllegalArgumentException("User and password are required to create a new user");
        }
        return;
    }

    /**
     * <p>
     * Update User / Profile
     * </p>
     *
     * @param userId      the UserID tied to the Profile Object
     * @param userProfile The User'sProfile Object to be updated
     * @return {@link ResponseObject}
     */
    public ResponseObject updateUser(String userId, NewUserProfile userProfile) {
        try {
            String ts = getServerTime();
            sortKBQKBAbyKey(userProfile);
            String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_PUT,
                    IDMQueries.queryUserProfile(saAuth.getRealm(), userId), userProfile, ts);

            return saExecuter.executeUserProfileUpdateRequest(header,
                    saBaseURL.getApplianceURL() + IDMQueries.queryUserProfile(saAuth.getRealm(), userId),
                    userProfile,
                    ts,
                    ResponseObject.class);

        } catch (Exception e) {
            logger.error("Exception occurred executing REST query:\n" + e.getMessage() + "\n", e);
        }
        return null;
    }

    private void sortKBQKBAbyKey(NewUserProfile userProfile) {
        List<Map.Entry<String, UserProfileKB>> userProfileList = userProfile.getKnowledgeBase().entrySet().stream()
                .sorted(Map.Entry.comparingByKey(String::compareToIgnoreCase))
                .collect(Collectors.toList());
        userProfile.setKnowledgeBase(userProfileList.stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (v1, v2) -> v1,
                        LinkedHashMap::new)));
    }

    /**
     * <p>
     * Update User / Profile
     * This method supports special characters for userId since it uses QP (Query
     * Params) in order to create the request.
     * </p>
     *
     * @param userId      the UserID tied to the Profile Object
     * @param userProfile The User'sProfile Object to be updated
     * @return {@link ResponseObject}
     */
    public ResponseObject updateUserQP(String userId, NewUserProfile userProfile) {
        try {
            String ts = getServerTime();
            sortKBQKBAbyKey(userProfile);
            String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_PUT,
                    IDMQueries.queryUserProfileQP(saAuth.getRealm()), userProfile, ts);

            return saExecuter.executeUserProfileUpdateRequest(header,
                    saBaseURL.getApplianceURL() + IDMQueries.queryUserProfileQP(saAuth.getRealm()), userId,
                    userProfile,
                    ts,
                    ResponseObject.class);

        } catch (Exception e) {
            throw new SARestAPIException("Exception occurred executing REST query:\n" + e.getMessage() + "\n", e);
        }
    }

    @Override
    public BaseResponse deleteUser(String userId, String domain, boolean deleteRelatedData) {
        try {
            String ts = getServerTime();
            DeleteUserRequest deleteUserRequest = new DeleteUserRequest(userId, deleteRelatedData, domain);
            String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_DELETE,
                    IDMQueries.queryUsers(saAuth.getRealm()), deleteUserRequest, ts);
            return saExecuter.executeDeleteRawRequest(header,
                    saBaseURL.getApplianceURL() + IDMQueries.queryUsers(saAuth.getRealm()),
                    ts, deleteUserRequest, BaseResponse.class);
        } catch (Exception e) {
            throw new SARestAPIException("Exception occurred executing REST query:\n" + e.getMessage() + "\n", e);
        }
    }

    /**
     * <p>
     * Associate User to Group
     * </p>
     *
     * @param userId    the user id of the identity
     * @param groupName The Name of the group to associate the user to
     * @return {@link GroupAssociationResponse}
     */
    public ResponseObject addUserToGroup(String userId, String groupName) {
        try {
            String ts = getServerTime();
            String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST",
                    IDMQueries.queryUserToGroup(saAuth.getRealm(), userId, groupName), ts);
            return saExecuter.executeSingleUserToSingleGroup(header,
                    saBaseURL.getApplianceURL() + IDMQueries.queryUserToGroup(saAuth.getRealm(), userId, groupName), ts,
                    ResponseObject.class);
        } catch (Exception e) {
            throw new SARestAPIException("Exception occurred executing REST query:\n" + e.getMessage() + "\n", e);
        }
    }

    /**
     * <p>
     * Associate User to Group
     * This method supports special characters for userId since it uses QP (Query
     * Params) in order to create the request.
     * </p>
     *
     * @param userId    the user id of the identity
     * @param groupName The Name of the group to associate the user to
     * @return {@link GroupAssociationResponse}
     */
    public ResponseObject addUserToGroupQP(String userId, String groupName) {
        try {
            String ts = getServerTime();
            String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST",
                    IDMQueries.queryUserToGroupQP(saAuth.getRealm()), ts);

            return saExecuter.executeSingleUserToSingleGroup(header,
                    saBaseURL.getApplianceURL() + IDMQueries.queryUserToGroupQP(saAuth.getRealm()), userId, groupName,
                    ts, ResponseObject.class);
        } catch (Exception e) {
            throw new SARestAPIException("Exception occurred executing REST query:\n" + e.getMessage() + "\n", e);
        }
    }

    /**
     * <p>
     * Associate Group to Users
     * </p>
     *
     * @param usersToGroup The Users to Group object holding the userIds
     * @param groupName    The Name of the group to associate the user to
     * @return {@link GroupAssociationResponse}
     */
    public GroupAssociationResponse addUsersToGroup(UsersToGroup usersToGroup, String groupName) {
        try {
            String ts = getServerTime();
            String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_POST,
                    IDMQueries.queryGroupToUsers(saAuth.getRealm(), groupName), usersToGroup, ts);

            return saExecuter.executeGroupToUsersRequest(header,
                    saBaseURL.getApplianceURL() + IDMQueries.queryGroupToUsers(saAuth.getRealm(), groupName),
                    usersToGroup, ts, GroupAssociationResponse.class);
        } catch (Exception e) {
            throw new SARestAPIException("Exception occurred executing REST query::\n" + e.getMessage() + "\n", e);
        }
    }

    /**
     * <p>
     * Associate Group to User
     * </p>
     *
     * @param groupName the Group Name
     * @param userId    The userId to associate to the group
     * @return {@link GroupAssociationResponse}
     */
    public GroupAssociationResponse addGroupToUser(String groupName, String userId) {
        try {
            String ts = getServerTime();

            String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST",
                    IDMQueries.queryGroupToUser(saAuth.getRealm(), userId, groupName), ts);
            return saExecuter.executeSingleGroupToSingleUser(header,
                    saBaseURL.getApplianceURL() + IDMQueries.queryGroupToUser(saAuth.getRealm(), userId, groupName), ts,
                    GroupAssociationResponse.class);
        } catch (Exception e) {
            throw new SARestAPIException("Exception occurred executing REST query:\n" + e.getMessage() + "\n", e);
        }
    }

    /**
     * <p>
     * Associate Group to User
     * </p>
     *
     * @param groupName the Group Name
     * @param userId    The userId to associate to the group
     * @return {@link GroupAssociationResponse}
     */
    public GroupAssociationResponse addGroupToUserQP(String groupName, String userId) {
        try {
            String ts = getServerTime();

            String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST",
                    IDMQueries.queryGroupToUserQP(saAuth.getRealm()), userId, ts);

            return saExecuter.executeSingleGroupToSingleUser(header,
                    saBaseURL.getApplianceURL() + IDMQueries.queryGroupToUserQP(saAuth.getRealm()), userId, groupName,
                    ts, GroupAssociationResponse.class);
        } catch (Exception e) {
            throw new SARestAPIException("Exception occurred executing REST query:\n" + e.getMessage() + "\n", e);
        }
    }

    /**
     * <p>
     * Associate User To Groups
     * </p>
     *
     * @param userId       The UserId we are going to assign Groups to
     * @param userToGroups The UserToGroups Object holding the list of groups to
     *                     associate to the user
     * @return {@link GroupAssociationResponse}
     */
    public GroupAssociationResponse addUserToGroups(String userId, UserToGroups userToGroups) {
        try {
            String ts = getServerTime();
            String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_POST,
                    IDMQueries.queryUserToGroups(saAuth.getRealm(), userId), userToGroups, ts);

            return saExecuter.executeUserToGroupsRequest(header,
                    saBaseURL.getApplianceURL() + IDMQueries.queryUserToGroups(saAuth.getRealm(), userId), userToGroups,
                    ts, GroupAssociationResponse.class);
        } catch (Exception e) {
            throw new SARestAPIException("Exception occurred executing REST query:\n" + e.getMessage() + "\n", e);
        }
    }

    /**
     * <p>
     * Returns the UserProfile for the specified user
     * </p>
     *
     * @param userId the userid of the identity you wish to have a list of possible
     *               second factors
     * @return {@link UserProfileResponse}
     */
    public UserProfileResponse getUserProfile(String userId) {
        String ts = getServerTime();
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "GET",
                IDMQueries.queryUserProfile(saAuth.getRealm(), userId), ts);

        try {
            return saExecuter.executeGetRequest(header,
                    saBaseURL.getApplianceURL() + IDMQueries.queryUserProfile(saAuth.getRealm(), userId), ts,
                    UserProfileResponse.class);

        } catch (Exception e) {
            logger.error("Exception occurred executing REST query:\n" + e.getMessage() + "\n");
        }
        return null;
    }

    /**
     * <p>
     * Returns the UserProfile for the specified user supporting special characters
     * </p>
     *
     * @param userId the userid of the identity you wish to have a list of possible
     *               second factors. This method supports special characters for
     *               userId since it uses QP (Query Params) in order to create the
     *               request.
     * @return {@link UserProfileResponse}
     */
    public UserProfileResponse getUserProfileQP(String userId) {
        String ts = getServerTime();
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "GET",
                IDMQueries.queryUserProfileQP(saAuth.getRealm()), ts);

        try {
            return saExecuter.executeGetRequest(header,
                    saBaseURL.getApplianceURL() + IDMQueries.queryUserProfileQP(saAuth.getRealm()), userId, ts,
                    UserProfileResponse.class);

        } catch (Exception e) {
            logger.error("Exception occurred executing REST query:\n" + e.getMessage() + "\n");
        }
        return null;
    }

    /**
     * <p>
     * Administrative Password Reset for the specified user
     * </p>
     *
     * @param userId   the userid of the identity you wish to have a list of
     *                 possible second factors
     * @param password the users new password
     * @return {@link ResponseObject}
     */

    public ResponseObject passwordReset(String userId, String password) {
        String ts = getServerTime();
        UserPasswordRequest userPasswordRequest = new UserPasswordRequest();
        userPasswordRequest.setPassword(password);
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST",
                IDMQueries.queryUserResetPwd(saAuth.getRealm(), userId), userPasswordRequest, ts);

        try {
            return saExecuter.executeUserPasswordReset(header,
                    saBaseURL.getApplianceURL() + IDMQueries.queryUserResetPwd(saAuth.getRealm(), userId),
                    userPasswordRequest, ts);

        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Administrative Password Reset for the specified user
     * </p>
     *
     * @param userId   the userid of the identity you wish to have a list of
     *                 possible second factors. This method supports special
     *                 characters for userId since it uses QP (Query Params) in
     *                 order to create the request.
     * @param password the users new password
     * @return {@link ResponseObject}
     */

    public ResponseObject passwordResetQP(String userId, String password) {
        String ts = getServerTime();
        UserPasswordRequest userPasswordRequest = new UserPasswordRequest();
        userPasswordRequest.setPassword(password);
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST",
                IDMQueries.queryUserResetPwdQP(saAuth.getRealm()), userPasswordRequest, ts);

        try {
            return saExecuter.executeUserPasswordReset(header,
                    saBaseURL.getApplianceURL() + IDMQueries.queryUserResetPwdQP(saAuth.getRealm()), userId,
                    userPasswordRequest, ts);

        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Self Service Password Reset for the specified user
     * </p>
     *
     * @param userId          the userid of the identity you wish to have a list of
     *                        possible second factors
     * @param currentPassword the users Current password
     * @param newPassword     the users new Password
     * @return {@link ResponseObject}
     */
    public ResponseObject passwordChange(String userId, String currentPassword, String newPassword) {
        String ts = getServerTime();
        UserPasswordRequest userPasswordRequest = new UserPasswordRequest();
        userPasswordRequest.setCurrentPassword(currentPassword);
        userPasswordRequest.setNewPassword(newPassword);
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST",
                IDMQueries.queryUserChangePwd(saAuth.getRealm(), userId), userPasswordRequest, ts);

        try {
            return saExecuter.executeUserPasswordChange(header,
                    saBaseURL.getApplianceURL() + IDMQueries.queryUserChangePwd(saAuth.getRealm(), userId),
                    userPasswordRequest, ts);

        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * <p>
     * Self Service Password Reset for the specified user
     * </p>
     *
     * @param userId          the userid of the identity you wish to have a list of
     *                        possible second factors. This method supports special
     *                        characters for userId since it uses QP (Query Params)
     *                        in order to create the request.
     * @param currentPassword the users Current password
     * @param newPassword     the users new Password
     * @return {@link ResponseObject}
     */
    public ResponseObject passwordChangeQP(String userId, String currentPassword, String newPassword) {
        String ts = getServerTime();
        UserPasswordRequest userPasswordRequest = new UserPasswordRequest();
        userPasswordRequest.setCurrentPassword(currentPassword);
        userPasswordRequest.setNewPassword(newPassword);
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST",
                IDMQueries.queryUserChangePwdQP(saAuth.getRealm()), userPasswordRequest, ts);

        try {
            return saExecuter.executeUserPasswordChange(header,
                    saBaseURL.getApplianceURL() + IDMQueries.queryUserChangePwdQP(saAuth.getRealm()), userId,
                    userPasswordRequest, ts);

        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }
        return null;
    }

    /**
     * End of IDM Methods
     */

    @Override
    public BaseResponse notifyAuthenticated(String userId, String result, String mfa) {
        String url = saAuth.getRealm() + Resource.APPLIANCE_AUTHENTICATED;
        String serverTime = this.getServerTime();
        Map<String, String> body = Maps.newHashMap();
        body.put(Resource.USER_ID, userId);
        body.put("authenticated", result);
        body.put("authregmethod", mfa);
        String authorization = RestApiHeader.getAuthorizationHeader(
                this.saAuth,
                "POST",
                url,
                body,
                serverTime);
        BaseResponse response;
        try {
            response = saExecuter.executePostRawRequest(
                    authorization,
                    saBaseURL.getApplianceURL() + url,
                    body,
                    BaseResponse.class,
                    serverTime);
            logger.debug("Authenticated notification for user with id [" + userId + "], " +
                    "result [" + result + "] and mfa [" + mfa + "] " +
                    "has been sent to IdP : " + response.getMessage());
        } catch (Exception e) {
            response = new BaseResponse();
            response.setMessage(e.getMessage());
            response.setStatus("invalid");
            logger.error("Unable to send the authenticated notification for user with id [" + userId + "] " +
                    ", result [" + result + "] and mfa [" + mfa + "] to IdP : " + e.getMessage(), e);
        }
        return response;
    }

    /**
     * Start of Phone Number Profile Methods
     */

    /**
     * <p>
     * Submit User Name and Phone Number to the Phone Number Profiling service using
     * the Rest API
     * </p>
     *
     * @param userId      The User ID that you want to validate from
     * @param phoneNumber The Phone number to get a profile on
     *
     * @return {@link NumberProfileResponse}
     *
     */
    public NumberProfileResponse PhoneNumberProfileSubmit(String userId, String phoneNumber) {
        String ts = getServerTime();
        NumberProfileRequest numberProfileRequest = new NumberProfileRequest();
        numberProfileRequest.setUser_id(userId);
        numberProfileRequest.setPhone_number(phoneNumber);

        String header = RestApiHeader.getAuthorizationHeader(saAuth, "POST",
                NumberProfileQuery.queryNumberProfile(saAuth.getRealm()), numberProfileRequest, ts);

        try {

            return saExecuter.executeNumberProfilePost(header,
                    saBaseURL.getApplianceURL() + NumberProfileQuery.queryNumberProfile(saAuth.getRealm()),
                    numberProfileRequest, ts);

        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }

        return null;
    }

    /**
     * <p>
     * Submit Update to Phone Number Profiling Service using the Rest API
     * </p>
     *
     * @param userId       The User ID that you want to validate from
     * @param phoneNumber  user phone number provided
     * @param portedStatus user phone status for the option to block phone numbers
     *                     that recently changed carriers (not_ported, ported)
     * @param carrierCode  6-digit number or a concatenation of the country code and
     *                     phone type
     * @param carrier      name of the carrier or a concatenation of the country
     *                     code and phone type
     * @param countryCode  2-character country code
     * @param networkType  phone connection source (landline, tollfree, mobile,
     *                     virtual, unknown, landline_tollfree)
     *
     * @return {@link BaseResponse}
     *
     */
    public BaseResponse UpdatePhoneNumberProfile(String userId, String phoneNumber, String portedStatus,
            String carrierCode, String carrier, String countryCode, String networkType) {
        String ts = getServerTime();
        NumberProfileUpdateRequest numberProfileUpdateRequest = new NumberProfileUpdateRequest();
        numberProfileUpdateRequest.setUser_id(userId);
        numberProfileUpdateRequest.setPhone_number(phoneNumber);
        numberProfileUpdateRequest.setPortedStatus(portedStatus);
        CarrierInfo carrierInfo = new CarrierInfo();
        carrierInfo.setCarrierCode(carrierCode);
        carrierInfo.setCarrier(carrier);
        carrierInfo.setCountryCode(countryCode);
        carrierInfo.setNetworkType(networkType);
        numberProfileUpdateRequest.setCarrierInfo(carrierInfo);

        String header = RestApiHeader.getAuthorizationHeader(saAuth, "PUT",
                NumberProfileQuery.queryNumberProfile(saAuth.getRealm()), numberProfileUpdateRequest, ts);

        try {

            return saExecuter.executeNumberProfileUpdate(header,
                    saBaseURL.getApplianceURL() + NumberProfileQuery.queryNumberProfile(saAuth.getRealm()),
                    numberProfileUpdateRequest, ts);

        } catch (Exception e) {
            logger.error(new StringBuilder().append("Exception occurred executing REST query::\n")
                    .append(e.getMessage()).append("\n").toString(), e);
        }

        return null;
    }

    /**
     * Validate the yubico OTP token from userId.
     * 
     * @param userId
     * @param token
     * @return ResponseObject with status and message. Where "status" is "valid" or
     *         "invalid" according the case.
     */
    public ResponseObject validateUserYubicoOTPToken(String userId, String token) {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUser_id(userId);
        authRequest.setType(Resource.YUBIKEY);
        authRequest.setToken(token);

        String uri = this.getAuthUri();
        String serverTime = this.getServerTime();
        ResponseObject response;
        try {
            String authHeader = RestApiHeader.getAuthorizationHeader(this.saAuth, "POST", uri, authRequest, serverTime);
            response = this.saExecuter.executePostRequest(authHeader, this.saBaseURL.getApplianceURL() + uri,
                    authRequest, serverTime, ResponseObject.class);
            if (response == null) {
                response = this.buildInvalidResponseObjectWithMessage("null response.");
            }
        } catch (Throwable th) {
            logger.error("Error validating Yubikey token : ", th);
            // invalid response object.
            response = this.buildInvalidResponseObjectWithMessage(th.getMessage());
        }
        return response;
    }

    /**
     * Retrieves the user's status from the username in the endpoint URL and returns
     * a response.
     *
     * @param userId The User ID that you want to validate
     * @return {@link BaseResponse}
     */
    public BaseResponse getUserStatus(String userId) {
        try {
            String ts = getServerTime();

            String query = StatusQuery.queryStatus(saAuth.getRealm(), userId);

            String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_GET, query, ts);

            return saExecuter.executeGetRequest(header, saBaseURL.getApplianceURL() + query, ts, BaseResponse.class);
        } catch (Exception e) {
            throw new SARestAPIException("Exception occurred executing get user status query", e);
        }

    }

    /**
     * Retrieves the user's status from the username in the endpoint URL and returns
     * a response.
     *
     * @param userId The User ID that you want to validate. This method supports
     *               special characters for userId since it uses QP (Query Params)
     *               in order to create the request.
     * @return {@link BaseResponse}
     */
    public BaseResponse getUserStatusQP(String userId) {
        try {
            String ts = getServerTime();

            String query = StatusQuery.queryStatusQP(saAuth.getRealm());

            String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_GET, query, ts);

            return saExecuter.executeGetRequest(header, saBaseURL.getApplianceURL() + query, userId, ts,
                    BaseResponse.class);
        } catch (Exception e) {
            throw new SARestAPIException("Exception occurred executing get user status query", e);
        }

    }

    /**
     * Method invokes a status to the user Id.
     *
     * @param userId The User ID that you want to change status
     * @param status The new status [lock, unlock, enable, disable]
     * @return {@link BaseResponse}
     */
    public BaseResponse setUserStatus(String userId, String status) {
        try {
            String ts = getServerTime();

            String query = StatusQuery.queryStatus(saAuth.getRealm(), userId);

            // payload
            StatusRequest statusRequestPayload = new StatusRequest(status);

            String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_POST, query,
                    statusRequestPayload, ts);

            return saExecuter.executePostRawRequest(header, saBaseURL.getApplianceURL() + query, statusRequestPayload,
                    BaseResponse.class, ts);

        } catch (Exception e) {
            throw new SARestAPIException("Exception occurred executing set user status query", e);
        }

    }

    /**
     * Method invokes a status to the user Id.
     *
     * @param userId The User ID that you want to change status. This method
     *               supports special characters for userId since it uses QP (Query
     *               Params) in order to create the request.
     * @param status The new status [lock, unlock, enable, disable]
     * @return {@link BaseResponse}
     */
    public BaseResponse setUserStatusQP(String userId, String status) {
        try {
            String ts = getServerTime();

            String query = StatusQuery.queryStatusQP(saAuth.getRealm());

            // payload
            StatusRequest statusRequestPayload = new StatusRequest(status);

            String header = RestApiHeader.getAuthorizationHeader(saAuth, Resource.METHOD_POST, query,
                    statusRequestPayload, ts);

            return saExecuter.executePostRawRequest(header, saBaseURL.getApplianceURL() + query, userId, "",
                    statusRequestPayload, BaseResponse.class, ts);

        } catch (Exception e) {
            throw new SARestAPIException("Exception occurred executing set user status query", e);
        }

    }

    /**
     * End of Number Profile Methods
     */

    /**
     * End of All SA Access methods
     */

    /**
     * Start Helper Methods
     * to fetch raw json
     *
     * @param query url
     * @return raw response
     */
    public String executeGetRequest(String query) {
        String ts = getServerTime();
        query = saAuth.getRealm() + query;
        String header = RestApiHeader.getAuthorizationHeader(saAuth, "GET", query, ts);
        try {
            return saExecuter.executeRawGetRequest(header, saBaseURL.getApplianceURL() + query, ts);
        } catch (Exception e) {
            logger.error("Exception occurred executing REST query::\n" + e.getMessage() + "\n", e);
        }
        return null;
    }

    // This is for a quick fix, we need to pass this boolean through configuration,
    // which requires a refactor of this class.
    String getServerTime() {
        return getServerTime(saConfig.getOldIdPSupport());
    }

    String getServerTime(Boolean oldIdpSupport) {
        return TimeUtils.getServerTime(oldIdpSupport);
    }

    private String getAuthUri() {
        return saAuth.getRealm() + "/api/" + Resource.API_VERSION + "/auth";
    }

    private ResponseObject buildInvalidResponseObjectWithMessage(String message) {
        ResponseObject response = new ResponseObject();
        response.setStatus("invalid");
        response.setMessage(message);
        return response;
    }

    public static class UpdateUserHistoryRequest extends AuthRequest {
        private String ip_address;

        public UpdateUserHistoryRequest(String userId, String ip) {
            this.setUser_id(userId);
            ip_address = ip;
        }

        public String getIp_address() {
            return ip_address;
        }

        public void setIp_address(String ip_address) {
            this.ip_address = ip_address;
        }
    }

    /**
     *
     * End Helper Methods
     */
}
