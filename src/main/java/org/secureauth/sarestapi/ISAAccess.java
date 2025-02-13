package org.secureauth.sarestapi;

import org.secureauth.sarestapi.data.DFP.DFP;
import org.secureauth.sarestapi.data.IPEval;
import org.secureauth.sarestapi.data.PushAcceptStatus;
import org.secureauth.sarestapi.data.Requests.AccessHistoryRequest;
import org.secureauth.sarestapi.data.Requests.DFPScoreRequest;
import org.secureauth.sarestapi.data.Response.*;
import org.secureauth.sarestapi.data.UserProfile.NewUserProfile;
import org.secureauth.sarestapi.data.UserProfile.UserToGroups;
import org.secureauth.sarestapi.data.UserProfile.UsersToGroup;
import org.secureauth.sarestapi.exception.SARestAPIException;

import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.NewCookie;
import java.util.Hashtable;

public interface ISAAccess {

	void updateConfig(Hashtable<String, Object> config);

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
	IPEval iPEvaluation(String userId, String ipAddress) throws SARestAPIException;

	/**
	 * <p>
	 * Returns the list of Factors available for the specified user
	 * </p>
	 * 
	 * @param userId the userid of the identity you wish to have a list of possible
	 *               second factors
	 * @return {@link FactorsResponse}
	 */
	FactorsResponse factorsByUser(String userId) throws SARestAPIException;

	/**
	 * <p>
	 * Returns the list of Factors available for the specified user
	 * </p>
	 * 
	 * @param userId the userid of the identity you wish to have a list of possible
	 *               second factors. This method supports special characters for
	 *               userId since it uses QP (Query Params) in order to create the
	 *               request.
	 * @return {@link FactorsResponse}
	 */
	FactorsResponse factorsByUserQP(String userId) throws SARestAPIException;

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
	ResponseObject sendPushToAcceptReq(String userId, String factorId, String endUserIP, String clientCompany,
			String clientDescription) throws SARestAPIException;

	/**
	 * Send push to accept request asynchronously retrieving the cookie required to
	 * check the push notification status later.
	 * This method only applies when Identity Platform is on Cloud
	 *
	 * @param userId            the user id of the identity
	 * @param factorId          the P2A Id to be compared against
	 * @param endUserIP         The End Users IP Address
	 * @param clientCompany     The Client Company Name
	 * @param clientDescription The Client Description
	 * @return {@link StatefulResponseObject}
	 */
	StatefulResponseObject sendPushToAcceptReqStateful(String userId, String factorId, String endUserIP,
			String clientCompany, String clientDescription) throws SARestAPIException;

	ResponseObject sendPushToAcceptSymbolReq(String userId, String factorId, String endUserIP, String clientCompany,
			String clientDescription) throws SARestAPIException;

	/**
	 * Send push symbol to accept request asynchronously retrieving the cookie
	 * required to check the push notification status later.
	 * This method only applies when Identity Platform is on Cloud
	 *
	 * @param userId            the user id of the identity
	 * @param factorId          the P2A Id to be compared against
	 * @param endUserIP         The End Users IP Address
	 * @param clientCompany     The Client Company Name
	 * @param clientDescription The Client Description
	 * @return {@link StatefulResponseObject}
	 */
	StatefulResponseObject sendPushToAcceptSymbolReqStateful(String userId, String factorId, String endUserIP,
			String clientCompany, String clientDescription) throws SARestAPIException;

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
	ResponseObject sendPushBiometricReq(String biometricType, String userId, String factorId, String endUserIP,
			String clientCompany, String clientDescription) throws SARestAPIException;

	/**
	 * Send push to accept request asynchronously retrieving the cookie required to
	 * check the push notification status later.
	 * This method only applies when Identity Platform is on Cloud
	 *
	 * @param biometricType     fingerprint, face
	 * @param userId            the user id of the identity
	 * @param factorId          the P2A Id to be compared against
	 * @param endUserIP         The End Users IP Address
	 * @param clientCompany     The Client Company Name
	 * @param clientDescription The Client Description
	 * @return {@link StatefulResponseObject}
	 */
	StatefulResponseObject sendPushBiometricReqStateful(String biometricType, String userId, String factorId,
			String endUserIP, String clientCompany, String clientDescription) throws SARestAPIException;

	/**
	 * <p>
	 * Perform adaptive auth query
	 * </p>
	 * 
	 * @param userId    the user id of the identity
	 * @param endUserIP the IP of requesting client
	 * @return {@link FactorsResponse}
	 */
	AdaptiveAuthResponse adaptiveAuthQuery(String userId, String endUserIP) throws SARestAPIException;

	PushAcceptStatus queryPushAcceptStatus(String refId) throws SARestAPIException;

	/**
	 * Perform push notification status query in mode stateful using the session
	 * affinity cookie.
	 * This method only applies when Identity Platform is on Cloud
	 * 
	 * @param refId  the reference id
	 * @param cookie the the session affinity cookie.
	 * @return {@link PushAcceptStatus}
	 */
	PushAcceptStatus queryPushAcceptStatusStateful(String refId, Cookie cookie) throws SARestAPIException;

	/**
	 *
	 * <p>
	 * Checks if the Username exists within the datastore within SecureAuth
	 * </p>
	 * 
	 * @param userId the userid of the identity
	 * @return {@link ResponseObject}
	 */
	BaseResponse validateUser(String userId) throws SARestAPIException;

	/**
	 * the OTP throttling count to 0 after the end-user successfully authenticates;
	 * the attempt count is stored in a directory attribute configured in the Web
	 * Admin
	 * 
	 * @param userId id of user
	 * @return base answer
	 */
	ThrottleResponse resetThrottleReq(String userId) throws SARestAPIException;

	/**
	 * the OTP throttling count to 0 after the end-user successfully authenticates;
	 * the attempt count is stored in a directory attribute configured in the Web
	 * Admin
	 * this method supports special characters
	 * 
	 * @param userId id of user
	 * @return base answer
	 */
	ThrottleResponse resetThrottleReqQP(String userId) throws SARestAPIException;

	/**
	 * GET the end-user's current count of OTP usage attempts
	 * 
	 * @param userId id of user
	 * @return base answer
	 */
	ThrottleResponse getThrottleReq(String userId) throws SARestAPIException;

	/**
	 * GET the end-user's current count of OTP usage attempts
	 * 
	 * @param userId id of user. This method supports special characters for userId
	 *               since it uses QP (Query Params) in order to create the request.
	 * @return base answer
	 */
	ThrottleResponse getThrottleReqQP(String userId) throws SARestAPIException;

	/**
	 * <p>
	 * Checks the users password against SecureAuth Datastore
	 * </p>
	 *
	 * @param userId   the userid of the identity
	 * @param password The password of the user to validate
	 * @return {@link ResponseObject}
	 */
	BaseResponse validateUserPassword(String userId, String password) throws SARestAPIException;

	/**
	 * <p>
	 * Checks the users password against SecureAuth Datastore
	 * </p>
	 *
	 * @param userId    the userid of the identity
	 * @param password  The password of the user to validate
	 * @param enduserIp the enduser's ip address
	 * @return {@link ResponseObject}
	 */
	BaseResponse validateUserPassword(String userId, String password, String enduserIp) throws SARestAPIException;

	/**
	 * <p>
	 * Checks the users pin against SecureAuth Datastore
	 * </p>
	 * 
	 * @param userId the userid of the identity
	 * @param pin    The pin of the user to validate
	 * @return {@link ResponseObject}
	 */
	BaseResponse validateUserPin(String userId, String pin) throws SARestAPIException;

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
	BaseResponse validateKba(String userId, String answer, String factorId) throws SARestAPIException;

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
	BaseResponse validateOath(String userId, String otp, String factorId) throws SARestAPIException;

	/**
	 * <p>
	 * Send One Time Passcode by Phone
	 * </p>
	 * 
	 * @param userId   the userid of the identity
	 * @param factorId Phone Property "Phone1"
	 * @return {@link ResponseObject}
	 */
	ResponseObject deliverOTPByPhone(String userId, String factorId) throws SARestAPIException;

	/**
	 * <p>
	 * Send One Time Passcode by Phone Ad Hoc
	 * </p>
	 * 
	 * @param userId      the userid of the identity
	 * @param phoneNumber Phone Number to call
	 * @return {@link ResponseObject}
	 */
	ResponseObject deliverAdHocOTPByPhone(String userId, String phoneNumber) throws SARestAPIException;

	/**
	 * <p>
	 * Send One Time Passcode by SMS to Registered User
	 * </p>
	 * 
	 * @param userId   the userid of the identity
	 * @param factorId Phone Property "Phone1"
	 * @return {@link ResponseObject}
	 */
	ResponseObject deliverOTPBySMS(String userId, String factorId) throws SARestAPIException;

	/**
	 * <p>
	 * Send yubikey validation
	 * </p>
	 * 
	 * @param userId       the userid of the identity
	 * @param yubikeyToken the generated token by the yubikey
	 * @return {@link BaseResponse}
	 */
	BaseResponse validateYubicoToken(String userId, String yubikeyToken);

	/**
	 * <p>
	 * Validate One Time Passcode sent by SMS
	 * </p>
	 * 
	 * @param userId the userid of the identity
	 * @param otp    OTP Value to compare against what was sent
	 * @return {@link ValidateOTPResponse}
	 */
	ValidateOTPResponse validateOTP(String userId, String otp) throws SARestAPIException;

	/**
	 * <p>
	 * Send One Time Passcode by SMS Ad Hoc
	 * </p>
	 * 
	 * @param userId      the userid of the identity
	 * @param phoneNumber Phone Number to send SMS to
	 * @return {@link ResponseObject}
	 */
	ResponseObject deliverAdHocOTPBySMS(String userId, String phoneNumber) throws SARestAPIException;

	/**
	 * <p>
	 * Send One Time Passcode by Email to Help Desk
	 * </p>
	 * 
	 * @param userId   the userid of the identity
	 * @param factorId Help Desk Property "HelpDesk1"
	 * @return {@link ResponseObject}
	 */
	ResponseObject deliverHelpDeskOTPByEmail(String userId, String factorId) throws SARestAPIException;

	/**
	 * <p>
	 * Send One Time Passcode by Email
	 * </p>
	 * 
	 * @param userId   the userid of the identity
	 * @param factorId Email Property "Email1"
	 * @return {@link ResponseObject}
	 */
	ResponseObject deliverOTPByEmail(String userId, String factorId) throws SARestAPIException;

	/**
	 * <p>
	 * Send One Time Passcode by Email Ad Hoc
	 * </p>
	 * 
	 * @param userId       the userid of the identity
	 * @param emailAddress Email Address
	 * @return {@link ResponseObject}
	 */
	ResponseObject deliverAdHocOTPByEmail(String userId, String emailAddress) throws SARestAPIException;

	/**
	 * <p>
	 * Send One Time Passcode by Push
	 * </p>
	 * 
	 * @param userId   the userid of the identity
	 * @param factorId Device Property "z0y9x87wv6u5t43srq2p1on"
	 * @return {@link ResponseObject}
	 */
	ResponseObject deliverOTPByPush(String userId, String factorId) throws SARestAPIException;

	/**
	 * <p>
	 * Send One Time Passcode by Helpdesk
	 * </p>
	 * 
	 * @param userId   the userid of the identity
	 * @param factorId Help Desk Property "HelpDesk1"
	 * @return {@link ResponseObject}
	 */
	ResponseObject deliverOTPByHelpDesk(String userId, String factorId) throws SARestAPIException;

	/**
	 * <p>
	 * Send Link to accept by email
	 * </p>
	 * 
	 * @param userId   the userid of the identity
	 * @param factorId Email Property "Email1"
	 * @return {@link StatefulResponseObject}
	 */
	StatefulResponseObject emailLink(String userId, String factorId) throws SARestAPIException;

	/**
	 * <p>
	 * Send Link to accept by email
	 * </p>
	 * 
	 * @param userId   the userid of the identity
	 * @param factorId Phone Property "Phone1"
	 * @return {@link StatefulResponseObject}
	 */
	StatefulResponseObject smsLink(String userId, String factorId) throws SARestAPIException;

	/**
	 * <p>
	 * Verify Link to accept using code
	 * </p>
	 * 
	 * @param linkId the id provided when making a link to accept request
	 * @param cookie the session cookie
	 * @return {@link PushAcceptStatus}
	 */
	PushAcceptStatus verifyLinkToAcceptStatus(String linkId, Cookie cookie);

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
	ResponseObject accessHistory(String userId, String ipAddress) throws SARestAPIException;

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
	DFPConfirmResponse DFPConfirm(String userId, String fingerprintId) throws SARestAPIException;

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
	@Deprecated
	DFPValidateResponse DFPValidateNewFingerprint(String userId, String hostAddress, String jsonString)
			throws SARestAPIException;

	DFPValidateResponse DFPValidateNewFingerprint(DFP fingerprint) throws SARestAPIException;

	/**
	 * <p>
	 * Returns the url for the JavaScript Source for DFP
	 * </p>
	 * 
	 * @return {@link JSObjectResponse}
	 */
	JSObjectResponse javaScriptSrc();

	/**
	 * <p>
	 * Returns the url for the JavaScript Source for BehaveBioMetrics
	 * </p>
	 * 
	 * @return {@link JSObjectResponse}
	 */
	JSObjectResponse BehaveBioJSSrc() throws SARestAPIException;

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
	BehaveBioResponse BehaveBioProfileSubmit(String userId, String behaviorProfile, String hostAddress,
			String userAgent) throws SARestAPIException;

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
	ResponseObject BehaveBioProfileReset(String userId, String fieldName, String fieldType, String deviceType)
			throws SARestAPIException;

	/**
	 * <p>
	 * Creates User / Profile
	 * </p>
	 * 
	 * @param newUserProfile The newUserProfile Object
	 * @return {@link ResponseObject}
	 */
	ResponseObject createUser(NewUserProfile newUserProfile) throws SARestAPIException;

	/**
	 * <p>
	 * Update User / Profile
	 * </p>
	 * 
	 * @param userId      the UserID tied to the Profile Object
	 * @param userProfile The User'sProfile Object to be updated
	 * @return {@link ResponseObject}
	 */
	ResponseObject updateUser(String userId, NewUserProfile userProfile) throws SARestAPIException;

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
	ResponseObject updateUserQP(String userId, NewUserProfile userProfile) throws SARestAPIException;

	/**
	 * <p>
	 * Delete User
	 * </p>
	 * 
	 * @param userId            the UserID to delete
	 * @param domain            the datastore name
	 * @param deleteRelatedData TRUE for complete delete, FALSE for only logical.
	 * @return {@link ResponseObject}
	 */
	BaseResponse deleteUser(String userId, String domain, boolean deleteRelatedData) throws SARestAPIException;

	/**
	 * <p>
	 * Associate User to Group
	 * </p>
	 * 
	 * @param userId    the user id of the identity
	 * @param groupName The Name of the group to associate the user to
	 * @return {@link GroupAssociationResponse}
	 */
	ResponseObject addUserToGroup(String userId, String groupName) throws SARestAPIException;

	/**
	 * <p>
	 * Associate User to Group
	 * </p>
	 * 
	 * @param userId    the user id of the identity. This method supports special
	 *                  characters for userId since it uses QP (Query Params) in
	 *                  order to create the request.
	 * @param groupName The Name of the group to associate the user to
	 * @return {@link GroupAssociationResponse}
	 */
	ResponseObject addUserToGroupQP(String userId, String groupName) throws SARestAPIException;

	/**
	 * <p>
	 * Associate Group to Users
	 * </p>
	 * 
	 * @param usersToGroup The Users to Group object holding the userIds
	 * @param groupName    The Name of the group to associate the user to
	 * @return {@link GroupAssociationResponse}
	 */
	GroupAssociationResponse addUsersToGroup(UsersToGroup usersToGroup, String groupName) throws SARestAPIException;

	/**
	 * <p>
	 * Associate Group to User
	 * </p>
	 * 
	 * @param groupName the Group Name
	 * @param userId    The userId to associate to the group
	 * @return {@link GroupAssociationResponse}
	 */
	GroupAssociationResponse addGroupToUser(String groupName, String userId) throws SARestAPIException;

	/**
	 * <p>
	 * Associate Group to User
	 * </p>
	 * 
	 * @param groupName the Group Name
	 * @param userId    The userId to associate to the group. This method supports
	 *                  special characters for userId since it uses QP (Query
	 *                  Params) in order to create the request.
	 * @return {@link GroupAssociationResponse}
	 */
	GroupAssociationResponse addGroupToUserQP(String groupName, String userId) throws SARestAPIException;

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
	GroupAssociationResponse addUserToGroups(String userId, UserToGroups userToGroups) throws SARestAPIException;

	/**
	 * <p>
	 * Returns the UserProfile for the specified user
	 * </p>
	 * 
	 * @param userId the userid of the identity you wish to have a list of possible
	 *               second factors
	 * @return {@link UserProfileResponse}
	 */
	UserProfileResponse getUserProfile(String userId) throws SARestAPIException;

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
	UserProfileResponse getUserProfileQP(String userId) throws SARestAPIException;

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
	ResponseObject passwordReset(String userId, String password) throws SARestAPIException;

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
	ResponseObject passwordResetQP(String userId, String password) throws SARestAPIException;

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
	ResponseObject passwordChange(String userId, String currentPassword, String newPassword) throws SARestAPIException;

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
	ResponseObject passwordChangeQP(String userId, String currentPassword, String newPassword)
			throws SARestAPIException;

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
	NumberProfileResponse PhoneNumberProfileSubmit(String userId, String phoneNumber) throws SARestAPIException;

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
	BaseResponse UpdatePhoneNumberProfile(String userId, String phoneNumber, String portedStatus, String carrierCode,
			String carrier, String countryCode, String networkType) throws SARestAPIException;

	/**
	 * Retrieves the user's status from the username in the endpoint URL and returns
	 * a response.
	 * 
	 * @param userId The User ID that you want to validate
	 * @return {@link BaseResponse}
	 */
	BaseResponse getUserStatus(String userId) throws SARestAPIException;

	/**
	 * Retrieves the user's status from the username in the endpoint URL and returns
	 * a response.
	 * 
	 * @param userId The User ID that you want to validate. This method supports
	 *               special characters for userId since it uses QP (Query Params)
	 *               in order to create the request.
	 * @return {@link BaseResponse}
	 */
	BaseResponse getUserStatusQP(String userId) throws SARestAPIException;

	/**
	 * Method invokes a status to the user Id.
	 * 
	 * @param userId The User ID that you want to change status
	 * @param status The new status [lock, unlock, enable, disable]
	 * @return {@link BaseResponse}
	 */
	BaseResponse setUserStatus(String userId, String status) throws SARestAPIException;

	/**
	 * Method invokes a status to the user Id.
	 * 
	 * @param userId The User ID that you want to change status. This method
	 *               supports special characters for userId since it uses QP (Query
	 *               Params) in order to create the request.
	 * @param status The new status [lock, unlock, enable, disable]
	 * @return {@link BaseResponse}
	 */
	BaseResponse setUserStatusQP(String userId, String status) throws SARestAPIException;

	/**
	 * Retrieves score from fingerprint, user and host.
	 * 
	 * @param userId          User ID provided
	 * @param hostAddress     host
	 * @param fingerprintId   GUID of the profile
	 * @param fingerPrintJSON Descriptive name derived from the user_agent string
	 * @return {@link DFPValidateResponse}
	 */
	@Deprecated
	DFPValidateResponse DFPScoreFingerprint(String userId, String hostAddress, String fingerprintId,
			String fingerPrintJSON) throws SARestAPIException;

	DFPValidateResponse DFPScoreFingerprint(DFP fingerprint) throws SARestAPIException;

	/**
	 * Method to complete the user account profile in the directory
	 * 
	 * @param userId          User ID provided
	 * @param hostAddress     host
	 * @param fingerprintId   GUID of the profile
	 * @param fingerPrintJSON Descriptive name derived from the user_agent string
	 * @return {@link DFPValidateResponse}
	 */
	DFPValidateResponse DFPSaveFingerprint(String userId, String hostAddress, String fingerprintId,
			String fingerPrintJSON) throws SARestAPIException;

	/**
	 * This method will send a authenticated transaction result to Idp for the
	 * specified userId.
	 * 
	 * @param userId The user Id.
	 * @param result Final result of the authenticated flow
	 *
	 *               success: The authentication transaction was successfully
	 *               completed.
	 *
	 *               aborted: The authentication transaction started, but could not
	 *               be successfully completed.
	 *               For example, this status can occur if a user's session times
	 *               out.
	 *
	 *               canceled: The authentication transaction started, but could not
	 *               be successfully completed.
	 *               For example, this status can occur if a user cancels an
	 *               authentication attempt.
	 *
	 *               wrong: The authentication transaction started, but could not be
	 *               successfully completed.
	 *               For example, this status can occur if a user enters bad
	 *               credentials.
	 *
	 * @param mfa    The multi-factor authentication method used. Allowed values are
	 *               :
	 *               NONE
	 *               KBA
	 *               EMAIL
	 *               PHONE
	 *               SMS
	 *               HELP
	 *               PIN
	 *               OATH
	 *               PUSHNOTIFICATION
	 *               VIPCREDENTIAL
	 *               PUSHACCEPT
	 *               YUBIKEY
	 *               EMAILLINK
	 *               SMSLINK
	 *               VALIDATEOTP
	 *               SYMBOL2ACCEPT
	 *               BIOMETRIC2ACCEPT
	 *               FIDO2
	 *               OTHER
	 * @return {@link BaseResponse}
	 */
	BaseResponse notifyAuthenticated(String userId, String result, String mfa) throws SARestAPIException;
}
