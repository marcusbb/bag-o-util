/**
 * (C) 2001 Research In Motion Ltd. All Rights Reserved. RIM, Research In Motion -- Reg. U.S. Patent and Trademark
 * Office The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited All materials confidential
 * information of Research In Motion, Limited
 * <p/>
 **/

package provision.services.logging;

public interface LogEvent {
    /**
     * *********************************************************************
     * Registration Interface Events
     * **********************************************************************
     */
    String REG_M_PACKET_RECEIVED = "REG_M_Packet_Received";     //info
    String REG_G_PACKET_RECEIVED = "REG_G_Packet_Received";     //info
    String REG_C_PACKET_RECEIVED = "REG_C_Packet_Received";     //info
    String REG_I_PACKET_RECEIVED = "REG_I_Packet_Received";     //info
    String REG_WIFI_PACKET_RECEIVED = "REG_WIFI_Packet_Received"; // info
    String REG_PACKET_UNKOWN = "REG_Unrecognized_Type";         //error
    String REG_EXCEPTION = "REG_Exception";                     //error

    /**
     * *********************************************************************
     * Registration Processing Events
     * **********************************************************************
     */
    String REG_POLICY_BLOCK_PIN = "REG_Policy_Block_PIN";       //info
    String REG_PACKET_DISCARDED = "REG_Packet_Discarded";       //warn

    String REG_HANDLE_REG_EXCEPTION = "REG_handleProRegisterEv_Exception";   //error
    String REG_RELAY_ACTION_EXCEPTION = "REG_performRelayAction_Exception";  //error
    String REG_EMAIL_RIM_ADMIN_EXCEPTION = "REG_Email_Rim_Admin_Exception";  //error
    String REG_PROREQUESTOR_IS_NULL = "REG_ProRequestorFW_Is_Null";          //error
    // The ESN or IMEI in the registration belongs to another PIN
    String REG_ESN_IMEI_BELONGS_TO_ANOTHER_PIN = "REG_Esn_Imei_Belongs_To_Another_PIN"; // error
    // The ESN or IMEI of the PIN in registration has changed to a different value
    String REG_ESN_IMEI_HAS_CHANGED = "REG_Esn_Imei_Changed_Between_Registrations"; // error

    String REG_RELAYID_NOT_LOCAL = "REG_RelayId_Not_Local";  // warn
    String REG_EFGID_UPDATE = "REG_EFGID_Update"; //info
    
    /**
     * *********************************************************************
     * SOAP Services Deployment Events
     * **********************************************************************
     */
    String SOAP_SRV_DEPLOYMENT_FAILED = "SOAP_Services_Deployment_Failed";  //error
    String SOAP_SRV_DEPLOYMENT_OK = "SOAP_Services_Deployed_Successfully";  //info

    /**
     * *********************************************************************
     * Web Services Events
     * **********************************************************************
     */
    String WS_ARI_EXCEPTION = "WS_ARI_Exception";                           //warn
    String WS_AXIS_EXCEPTION = "Failed_To_Create_ProvisioningRequestBean";  //error

    String WS_DOC_LITERAL_EXCEPTION = "WS_DOC_LITERAL_Exception";           //error
    String WS_DOC_LITERAL_INFO = "WS_DOC_LITERAL_INFO";           //info
    
    String WS_BIS_EXCEPTION = "WS_BIS_Exception";


    /**
     * *********************************************************************
     * ERP Events                                         *
     * **********************************************************************
     */
    String SAP_CONFIG_INFO = "SAP_Config_Info";                                                                     //info
    String SAP_INIT_INFO = "SAP_Init_Info";                                                                               //info
    String SAP_CONNECTION_INFO = "SAP_Connection_Info";                                                      //info
    String SAP_BAPI_RESPONDED = "SAP_BAPI_Responded";                                                       //info from native code
    String SAP_CREATE_CUSTOMER = "SAP_Create_Customer";                                                   //info
    String SAP_CREATE_CUSTOMER_RESULT = "SAP_Create_Customer_Result";                           //info
    String SAP_GET_CUSTOMER = "SAP_Get_Customer";                                                             //info
    String SAP_GET_CUSTOMER_RESULT = "SAP_Get_Customer_Result";                                     //info
    String SAP_CREATE_CONTRACT = "SAP_Create_Contract";                                                      //info
    String SAP_CREATE_CONTRACT_RESULT = "SAP_Create_Contract_Result";                              //info
    String SAP_GET_DEV_INFO = "SAP_Get_Device_Info";                                                           //info
    String SAP_GET_DEV_INFO_RESULT = "SAP_Get_Device_Info_Result";                                   //info
    String SAP_GET_DEV_DETAIL = "SAP_Get_Device_Detail";                                                      //info
    String SAP_GET_DEV_DETAIL_RESULT = "SAP_Get_Device_Detail_Result";                              //info
    String SAP_GET_SALESAREAS = "SAP_Get_Salesareas";                                                         //info
    String SAP_GET_SALESAREAS_RESULT = "SAP_Get_Salesareas_Result";                                 //info
    String SAP_VALIDATE_DEVICE = "SAP_Validate_Device";                                                        //info
    String SAP_VALIDATE_DEVICE_RESULT = "SAP_Validate_Device_Result";                                //info
    String SAP_GET_BILLTO = "SAP_Get_Billto";                                                                          //info
    String SAP_GET_BILLTO_RESULT = "SAP_Get_Billto_Result";                                                  //info
    String SAP_GET_HWPRD = "SAP_Get_HW_PRD";                                                                    //info
    String SAP_GET_HWPRD_RESULT = "SAP_Get_HW_PRD_Result";                                            //info
    String SAP_CREATE_SRV_CONTRACT = "SAP_Create_Service_Contract";                                 //info
    String SAP_CREATE_SRV_CONTRACT_RESULT = "SAP_Create_Service_Contract_Result";          //info
    String SAP_DEACTIVATE_SERVICE = "SAP_Deactivate_Service";                                              //info
    String SAP_DEACTIVATE_SERVICE_RESULT = "SAP_Deactivate_Service_Result";                      //info
    String SAP_SUSPEND_SERVICE = "SAP_Suspend_Service";                                                      //info
    String SAP_SUSPEND_SERVICE_RESULT = "SAP_Suspend_Service_Result";                              //info
    String SAP_RESUME_SERVICE = "SAP_Resume_Service";                                                        //info
    String SAP_RESUME_SERVICE_RESULT = "SAP_Resume_Service_Result";                                //info
    String SAP_MODIFY_BILLING_COMMENT = "SAP_Modify_Billing_Comment";                             //info
    String SAP_MODIFY_BILLING_COMMENT_RESULT = "SAP_Modify_Billing_Comment_Result";     //info

    String SAP_NATIVE_EXCEPTION = "SAP_Native_Exception";   //warn
    String SAP_INVALID_PARAM = "SAP_Invalid_Parameter";     //error
    String SAP_EXCEPTION = "SAP_Exception";                 //error
    String SAP_START_UP_FAILED = "SAP_Start_Up_Failed";     //fatal

    /**
     * *********************************************************************
     * FTP Events
     * **********************************************************************
     */
//    String FTP_LOGIN_OK = "FTP_Login_Passed";                           //info
//    String FTP_DISCONNECT = "FTP_Disconnected";                         //info
//    String FTP_EVENT_MAP = "FTP_Event_Map";                             //info
//    String FTP_FILE_SIZE = "FTP_Get_File_Size";                         //info
//    String FTP_FILE_LIST = "FTP_Process_File_List";                     //info
//    String FTP_DISPATCH_EVENT = "FTP_Dispatch_Event";                   //info
//
//    String FTP_LOGIN_FAILED = "FTP_Login_Failed";                       //warn
//    String FTP_DISCONNECTING_SERVER = "FTP_Disconnecting_Server";       //warn
//    String FTP_FILE_SIZE_0 = "FTP_File_Size_Is_0";                      //warn
//    String FTP_EXCEPTION = "FTP_Exception";                             //error
//    String FTP_SETUP_DATA_PORT = "FTP_Setting_Up_Data_Port";            //error
//    String FTP_NON_POSITIVE_RESPONSE = "FTP_Non_Positive_Response";     //info
//    String FTP_INVALID_SO_TIMEOUT = "FTP_Invalid_SO_Timeout_System_Parm"; //error

    /**
     * *********************************************************************
     * Credit Card Validation Events
     * **********************************************************************
     */
    String CC_GET_AVS_CODES = "CC_Get_AVS_Codes";                       //warn
    String CC_GET_STATUS_CODES = "CC_Get_Status_Codes";                 //warn

    String CC_DISCONNECTING = "CC_Disconecting";                        //info
    String CC_RESPONSE = "CC_Response_From_PaymentPlus";                //info

    String CC_DISCONNECT_FAILED = "CC_Failed_To_Disconnect";            //warn
    String CC_EXCEPTION = "CC_Exception";                               //error

    /**
     * *********************************************************************
     * Mail Receiver Events                               *
     * **********************************************************************
     */
//    // ReceiverDriver
//    String MRCVR_GET_POLLED_ACCTS = "Mrcvr_Getting_Polled_Accounts";
//    String MRCVR_CREATE_MAIL_GATEWAY = "Mrcvr_Create_Mail_Gateway";
//    String MRCVR_EXECUTION_FAILED = "Mrcvr_MailReceiver_Execution_Failed";
//    String MRCVR_GET_POLLED_ACCOUNTS = "Mrcvr_Get_Polled_Accounts";
//    String MRCVR_GET_POLLEDACCOUNT_BEAN = "Mrcvr_Get_PolledAccount_Bean";
//    String MRCVR_GET_SERVER_CONTEXT = "Mrcvr_Get_WLServer_Context";
//    String MRCVR_GET_WLSERVER = "Mrcvr_Get_WLServer_Connection";
//
//    // GroupController
//    String MRCVR_GET_CONTROLSERVICE_PROPERTIES = "Mrcvr_Get_Controlservice_Props";
//    String MRCVR_GET_MULTICAST_PARMS = "Mrcvr_Get_Multicast_Parameters";
//
//    // ControlAgent
//    String MRCVR_SET_LISTENER_SOCKET = "Mrcvr_Set_Listener_Socket";
//    String MRCVR_RECREATE_RESOURCE = "Mrcvr_Recreate_Resource";
//    String MRCVR_RECEIVE_RESOURCE = "Mrcvr_Receive_Resource";
//
//    // PeerMonitor
//    String MRCVR_PEERMONITOR_THREAD_START = "Mrcvr_Start_PeerMonitor";
//    String MRCVR_ROLE_ESTABLISHED = "Mrcvr_Established_Role";
//    String MRCVR_ROLE_ARBITRATION = "Mrcvr_Establishing_Role";
//    String MRCVR_ROLE_SETUP = "Mrcvr_Get_Role";
//    String MRCVR_REGET_ROLE = "Mrcvr_Assert_Role";
//    String MRCVR_SHUTDOWN_REQUEST = "Mrcvr_Shutdown_Request";
//    String MRCVR_PARSE_SHUTDOWN_TARGET = "Mrcvr_Parse_Shutdown_Target";
//    String MRCVR_HANDLE_MSG = "Mrcvr_Handle_Message";
//
//    // LeastBusy
//    String MRCVR_TRACK_FAR_END = "Mrcvr_Track_Far_End";
//    String MRCVR_RELEASE_PROCESSOR = "Rrcvr_Release_Processor";
//
//    // Role
//    String MRCVR_ROLE_END = "Mrcvr_End_Role";
//
//    // Master
//    String MRCVR_FAR_END_LOST = "Mrcvr_Far_End_Lost";
//    String MRCVR_BAD_MSG = "Mrcvr_Bad_Message";
//    String MRCVR_JOB_COMPLETION_FAIL = "Mrcvr_Job_Completion_Failed";
//    String MRCVR_JOB_STARTED = "Mrcvr_Job_Started";
//    String MRCVR_ISSUE_JOB = "Mrcvr_Job_Issued";
//    String MRCVR_ISSUE_JOB_FAILED = "Mrcvr_Issue_Job_Failed"; // Also Slave
//    String MRCVR_ALL_PORTS_BUSY = "Mrcvr_All_Ports_Busy";
//    String MRCVR_MASTER_NOTIFIED_BAD_JOB = "Mrcvr_Master_Job_Failed.";
//    String MRCVR_MASTER_FAIL_RESPONSE = "Mrcvr_Master_Fail_To_Respond";
//
//    // Slave
//    String MRCVR_SET_HEARTBEAT_PERIOD = "Mrcvr_Set_Heartbeat";
//    String MRCVR_NO_HEARTBEAT_RESPONSE = "Mrcvr_No_Heartbeat_Response";
//    String MRCVR_COMMUNICATION_FAILURE = "Mrcvr_Communication_Failure";
//    String MRCVR_SLAVE_HANDLE_MSG = "Mrcvr_Handle_Slave_Message";
//    String MRCVR_WORK_RESPONSE_FAIL = "Mrcvr_Fail_Respond_To_Work";
//    String MRCVR_SEND_HEARTBEAT = "Mrcvr_Fail_Send_Heartbeat";
//
//    // MailTransportEJB
    String MTPT_SEND_MAIL = "Mtpt_Send_Mail";
//
//    // MailGateway
//    String MRCVR_INITIALIZE_PROVIDER = "Mrcvr_Init_Provider";
//    String MRCVR_AUTHENTICATION = "Mrcvr_Authenticate";
//    String MRCVR_PROCESS_SUCCESS = "Mrcvr_Process_OK";
//    String MRCVR_PROCESS = "Mrcvr_Process";
//    String MRCVR_DISPATCH_REQUEST = "Mrcvr_Dispatch_Request";
//    String MRCVR_OPEN_STORE = "Mrcvr_Open_Inbox";
//
//    // ReceivedMessage
//    String MRCVR_MSG_BAD_CONTENT = "Mrcvr_Bad_Msg";
//    String MRCVR_UNSUPPORTED_CONTENT_TYPE = "Mrcvr_Unsupported_Content_Type";
//    String MRCVR_PARSE_MIME_MESSAGE = "Mrcvr_Parse_Mime_Message";
//    String MRCVR_PARSE_CONTENT_TYPE = "Mrcvr_Parse_Content_Type";
//
//    // MailEventMapDAO
//    String MRCVR_GET_EVENT_BY_DETAIL = "Mrcvr_Get_Event_By_Detail";
//    String MRCVR_GET_EVENT_SQL = "Mrcvr_Get_Event_SQL";
//
//    // ContainerDispatch
//    String MRCVR_SEND_TO_CONTAINER = "Mrcvr_Send_To_Container";
//    String MRCVR_DISPATCHER_HOME = "Mrcvr_Get_Dispatcher";
//    String MRCVR_GET_WEB_SERVER = "Mrcvr_Get_Server";
//    String MRCVR_DISPATCH_MAIL = "Mrcvr_Dispatch_Mail";
//    String MRCVR_FORM_EVENT = "Mrcvr_Form_Event";
//    String MRCVR_MISSING_FIELDS = "Mrcvr_Missing_Fields";
//    String MRCVR_DISPATCH_EVENT = "Mrcvr_Dispatch_Event";
//    String POP3ARCHIVE_MISSING_SOME_CRITICAL_FIELD = "pop3Archive_Missing_some_Critical_Field";        //warn


    /**
     * *********************************************************************
     * TrxDispatcher, AsyncProRequest Events          *
     * **********************************************************************
     */
    // onMessage
    String ONMESSAGE_JMS = "onMessage_JMS";        //info
    String ONMESSAGE_JMSEXCEPTION = "onMessage_JMSException";
    String ONMESSAGE_CONFIGURATIONMANAGER_REMOTEEXCEPTION = "onMessage_ConfigurationManager_RemoteException";
    String ONMESSAGE_CONFIGURATIONMANAGER_CREATEEXCEPTION = "onMessage_ConfigurationManager_CreateException";
    String ONMESSAGE_CAST_OBJECTMESSAGE_JMSEXCEPTION = "onMessage_cast_ObjectMessage_JMSException";
    String ONMESSAGE_WRONG_JMSTYPE = "onMessage_Wrong_JMSType";
    String ONMESSAGE_HANDLECRECONTRACTEV_PSEXCEPTION = "onMessage_handleCreContractEv_PSException";
    String ONMESSAGE_HANDLEPROREQUESTEV_EXCEPTION = "onMessage_handleProRequestEv_Exception";
    String ONMESSAGE_SETTRXTIMEOUT_SYSTEMEXCEPTION = "onMessage_setTrxTimeout_SystemException";
    String ONMESSAGE_EXCEPTION = "onMessage_Exception";

    String ONMESSAGE_HANDLEBATCHREQUESTEV_EXCEPTION = "onMessage_handleBatchRequestEv_Exception";

    //pokeNetops
    String POKENETOPS_CREATEEXCEPTION = "pokeNetOps_CreateException";
    String POKENETOPS_REMOTEEXCEPTION = "pokeNetOps_RemoteException";

    //cancelDependencies
    String REVERTDEPENDENCIES = "revertDependencies";        //info
    String REVERTDEPENDENCIES_PROTRX_FINDEREXCEPTION = "revertDependencies_ProTrx_FinderException";        //warn
    //handleActivationAckEv
    String HANDLEACTIVATIONACKEV_FINDBYNETOPBATCH_REMOTEEXCEPTION = "handleActivationAckEv_findByNetOpBatch_RemoteException";
    String HANDLEACTIVATIONACKEV_FINDBYNETOPBATCH_FINDEREXCEPTION = "handleActivationAckEv_findByNetOpBatch_FinderException";        //warn
    String HANDLEACTIVATIONACKEV_START = "handleActivationAckEv_Start";        //info
    String HANDLEACTIVATIONACKEV_INVALID_SERVPRD_REGION = "handleActivationAckEv_INVALID_SERVPRD_REGION";        //warn
    String HANDLEACTIVATIONACKEV_PROCESS_DETAILS = "handleActivationAckEv_Process_Details";        //info
    String HANDLEACTIVATIONACKEV_BELLMOBILITY_DETAILS_BAD_INFO = "handleActivationAckEv_BELLMOBILITY_Details_BAD_Info";        //warn
    String HANDLEACTIVATIONACKEV_TRXHDR_ZERO = "handleActivationAckEv_TrxHdr_ZERO";        //warn
    String HANDLEACTIVATIONACKEV_PROMOTE_PROTRX_FINDEREXCEPTION = "handleActivationAckEv_Promote_ProTrx_FinderException";        //warn
    String HANDLEACTIVATIONACKEV_CONCLUDE_OFF_PROTRX_FINDEREXCEPTION = "handleActivationAckEv_Conclude_Off_ProTrx_FinderException";        //warn
    String HANDLEACTIVATIONACKEV_MISMATCH_WITH_ACTIVE_DEV_ACK = "handleActivationAckEv_Mismatch_with_ACTIVE_DEV_Ack";        //warn
    String HANDLEACTIVATIONACKEV_MISMATCH_WITH_DEACTIVATED_DEV_ACK = "handleActivationAckEv_Mismatch_with_DEACTIVATED_DEV_Ack";        //warn
    String HANDLEACTIVATIONACKEV_REVERSE_TO_DEACTIVATED_DEV = "handleActivationAckEv_Reverse_to_DEACTIVATED_DEV";        //warn
    String HANDLEACTIVATIONACKEV_REVERSE_TO_ACTIVE_DEV = "handleActivationAckEv_Reverse_to_ACTIVE_DEV";        //warn
    String HANDLEACTIVATIONACKEV_MISMATCH_ERR_IGNORED = "handleActivationAckEv_Mismatch_Err_Ignored";        //warn
    String HANDLEACTIVATIONACKEV_DEVSERV_UPDATE = "handleActivationAckEv_DevServ_Update";        //info
    String HANDLEACTIVATIONACKEV_PROTRXHDR_FINDEREXCEPTION = "handleActivationAckEv_ProTrxHdr_FinderException";        //warn
    String HANDLEACTIVATIONACKEV_PROTRX_FINDEREXCEPTION = "handleActivationAckEv_ProTrx_FinderException";        //warn
    String HANDLEACTIVATIONACKEV_REMOTEEXCEPTION = "handleActivationAckEv_RemoteException";
    String HANDLEACTIVATIONACKEV_EJBEXCEPTION = "handleActivationAckEv_EJBException";
    String HANDLEACTIVATIONACKEV_EXCEPTION = "handleActivationAckEv_Exception";
    String HANDLEACTIVATIONACKEV_NOTSUPPORTEDEXCEPTION = "handleActivationAckEv_NotSupportedException";
    String HANDLEACTIVATIONACKEV_SYSTEMEXCEPTION = "handleActivationAckEv_SystemException";
    String HANDLEACTIVATIONACKEV_ROLLBACKEXCEPTION = "handleActivationAckEv_RollbackException";
    String HANDLEACTIVATIONACKEV_HEURISTICMIXEDEXCEPTION = "handleActivationAckEv_HeuristicMixedException";
    String HANDLEACTIVATIONACKEV_HEURISTICROLLBAckEXCEPTION = "handleActivationAckEv_HeuristicRollbackException";
    String HANDLEACTIVATIONACKEV_GENERALFAILUREEXCEPTION = "handleActivationAckEv_GeneralFailureException";
    String HANDLEACTIVATIONACKEV_ROLLBACK_EXCEPTION = "handleActivationAckEv_Rollback_Exception";
    String HANDLEACTIVATIONACKEV_RELAY_UPDATE_ERR = "handleActivationAckEv_Relay_Update_Err";
    //handleTimeoutEv
    String HANDLETIMEOUTEV_SETTRXTIMEOUT_SYSTEMEXCEPTION = "handleTimeoutEv_setTrxTimeout_SystemException";
    String HANDLETIMEOUTEV_START = "handleTimeoutEv_Start";        //info
    String HANDLETIMEOUTEV_INVALID_SERVPRD_REGION = "handleTimeoutEv_INVALID_SERVPRD_REGION";        //warn
    String HANDLETIMEOUTEV_ACTIVE_AFTER_1DAY = "handleTimeoutEv_Active_After_1Day";        //info
    String HANDLETIMEOUTEV_TRX_MISSING_DEVSERV = "handleTimeoutEv_Trx_Missing_DevServ";        //warn
    String HANDLETIMEOUTEV_TRX_MULTIPLE_DEVSERV = "handleTimeoutEv_Trx_Multiple_DevServ";        //warn
    String HANDLETIMEOUTEV_FINDPENDINGBYDEVICEID_FINDEREXCEPTION = "handleTimeoutEv_findPendingByDeviceId_FinderException";        //warn
    String HANDLETIMEOUTEV_CREATEEXCEPTION = "handleTimeoutEv_CreateException";
    String HANDLETIMEOUTEV_PSEXCEPTION = "handleTimeoutEv_PSException";
    String HANDLETIMEOUTEV_REMOTEEXCEPTION = "handleTimeoutEv_RemoteException";
    String HANDLETIMEOUTEV_NOTSUPPORTEDEXCEPTION = "handleTimeoutEv_NotSupportedException";
    String HANDLETIMEOUTEV_SYSTEMEXCEPTION = "handleTimeoutEv_SystemException";
    String HANDLETIMEOUTEV_ROLLBACKEXCEPTION = "handleTimeoutEv_RollbackException";
    String HANDLETIMEOUTEV_HEURISTICMIXEDEXCEPTION = "handleTimeoutEv_HeuristicMixedException";
    String HANDLETIMEOUTEV_HEURISTICROLLBACKEXCEPTION = "handleTimeoutEv_HeuristicRollbackException";
    String HANDLETIMEOUTEV_EXCEPTION = "handleTimeoutEv_Exception";
    //completeTransactions
    String COMPLETETRANSACTIONS_START = "completeTransactions_Start";        //info
    String COMPLETETRANSACTIONS_SETTRXTIMEOUT_SYSTEMEXCEPTION = "completeTransactions_setTrxTimeout_SystemException";
    String COMPLETETRANSACTIONS_SAPCONTRACT_ALREADY_SET = "completeTransactions_SAPContract_Already_Set";        //warn
    String COMPLETETRANSACTIONS_SAPCONTRACTEXEC_EXCEPTION = "completeTransactions_SAPContractExec_Exception";
    String COMPLETETRANSACTIONS_CONTRACT_ALREADY_ACTIVE = "completeTransactions_Contract_Already_Active";        //info
    String COMPLETETRANSACTIONS_FIND_HWPRD_GETREGION_FINDEREXCEPTION = "completeTransactions_Find_HWPrd_getRegion_FinderException";        //warn
    String COMPLETETRANSACTIONS_PSEXCEPTION = "completeTransactions_PSException";
    String COMPLETETRANSACTIONS_NOTIFY = "completeTransactions_Notify";        //info
    String COMPLETETRANSACTIONS_NOTIFY_PROBLEM = "completeTransactions_Notify_Problem";        //info
    String COMPLETETRANSACTIONS_MAILDELIVERYEXCEPTION = "completeTransactions_MailDeliveryException";
    String COMPLETETRANSACTIONS_MAILTRANSPORT_CREATEEXCEPTION = "completeTransactions_MailTransport_CreateException";
    String COMPLETETRANSACTIONS_NOTIFY_REMOTEEXCEPTION = "completeTransactions_Notify_RemoteException";
    String COMPLETETRANSACTIONS_FINDEREXCEPTION = "completeTransactions_FinderException";        //warn
    String COMPLETETRANSACTIONS_NOTSUPPORTEDEXCEPTION = "completeTransactions_NotSupportedException";
    String COMPLETETRANSACTIONS_SYSTEMEXCEPTION = "completeTransactions_SystemException";
    String COMPLETETRANSACTIONS_ROLLBACKEXCEPTION = "completeTransactions_RollbackException";
    String COMPLETETRANSACTIONS_HEURISTICMIXEDEXCEPTION = "completeTransactions_HeuristicMixedException";
    String COMPLETETRANSACTIONS_HEURISTICROLLBACKEXCEPTION = "completeTransactions_HeuristicRollbackException";
    String COMPLETETRANSACTIONS_CREATEEXCEPTION = "completeTransactions_CreateException";
    String COMPLETETRANSACTIONS_REMOTEEXCEPTION = "completeTransactions_RemoteException";
    String COMPLETETRANSACTIONS_PROTRXDAOEXCEPTION = "completeTransactions_ProTrxDAOException";
    String COMPLETETRANSACTIONS_EXCEPTION = "completeTransactions_Exception";
    String COMPLETETRANSACTIONS_FINISH = "completeTransactions_Finish";        //info
    //updateProTrxDevServsContr
    String UPDATEPROTRXDEVSERVSCONTR_SKIP_OLD_CONTR = "updateProTrxDevServsContr_Skip_Old_Contr";    //warn
    String UPDATEPROTRXDEVSERVSCONTR = "updateProTrxDevServsContr";        //info
    String UPDATEPROTRXDEVSERVSCONTR_PSEXCEPTION = "updateProTrxDevServsContr_PSException";
    String UPDATEPROTRXDEVSERVSCONTR_FINDEREXCEPTION = "updateProTrxDevServsContr_FinderException";        //warn
    //handleComplTranEv
    String HANDLECOMPLTRANEV_FINDEREXCEPTION = "handleComplTranEv_FinderException";        //warn
    String HANDLECOMPLTRANEV_REMOTEEXCEPTION = "handleComplTranEv_RemoteException";
    String HANDLECOMPLTRANEV_EXCEPTION = "handleComplTranEv_Exception";
    //updateAckPageMart
    String UPDATEACKPAGEMART = "updateAckPageMart";        //info
    String UPDATEACKPAGEMART_REMOTEEXCEPTION = "updateAckPageMart_RemoteException";
    String UPDATEACKPAGEMART_FINDEREXCEPTION = "updateAckPageMart_FinderException";        //warn


    /**
     * *********************************************************************
     * TrxManager Events	                                *
     * **********************************************************************
     */
    String LAYEXECMAJORERR_FINDEREXCEPTION = "layExecMajorErr_FinderException";
    String LAYEXECMAJORERR_REMOTEEXCEPTION = "layExecMajorErr_RemoteException";
    // updateExistingProTrx
    String UPDATE_EXISTING_PROTRX_UPDATED = "updateExistingProTrx_Updated";
    String UPDATE_EXISTING_PROTRX_FINDEREXCEPTION = "updateExistingProTrx_FinderException";
    String UPDATE_EXISTING_PROTRX_REMOTEEXCEPTION = "updateExistingProTrx_RemoteException";
    String UPDATE_EXISTING_PROTRX_CREATEEXCEPTION = "updateExistingProTrx_CreateException";
    String UPDATE_EXISTING_PROTRX_SQLEXCEPTION = "updateExistingProTrx_SQLException";
    //creProTrx
    String CREPROTRX_PROTRXHEADER_CREATEEXCEPTION = "creProTrx_ProTrxHeader_CreateException";
    String CREPROTRX_PROTRXHEADER_REMOTEEXCEPTION = "creProTrx_ProTrxHeader_RemoteException";
    String CREPROTRX_PROTRXHEADER_SQLEXCEPTION = "creProTrx_ProTrxHeader_SQLException";
    String CREPROTRX_UPDATE_EXISTING_PROTRX = "creProTrx_Update_Existing_ProTrx";        //info
    String CREPROTRX_CREATEEXCEPTION = "creProTrx_CreateException";
    String CREPROTRX_REMOTEEXCEPTION = "creProTrx_RemoteException";
    String CREPROTRX_SQLEXCEPTION = "creProTrx_SQLException";

    String CREPROTRX2_EXCEPTION = "CreProTrx2_Exception";
    String CREPROTRX2_UPDATE_EXISTING_PROTRX = "creProTrx2_Update_Existing_ProTrx";        //info
    String CREPROTRX2_NO_NETOPID = "creProTrx2_No_NetOpId";        //error
    //creDevice
    String CREDEVICE_NULL_HWPRD_SKIPPED = "creDevice_NULL_HwPrd_Skipped";        //warn
    String CREDEVICE = "creDevice";        //info
    String CREDEVICE_EXCEPTION = "creDevice_Exception";
    //attainPendingProTrx
    String ATTAINPENDINGPROTRX_FOUND_MULTIPLE = "attainPendingProTrx_Found_Multiple";        //warn
    String ATTAINPENDINGPROTRX_REMOTEEXCEPTION = "attainPendingProTrx_RemoteException";
    //HandleCheckOut
    String HANDLECHECKOUT_CONFIGURATIONMANAGER_REMOTEEXCEPTION = "HandleCheckOut_ConfigurationManager_RemoteException";
    String HANDLECHECKOUT_CONFIGURATIONMANAGER_CREATEEXCEPTION = "HandleCheckOut_ConfigurationManager_CreateException";
    String HANDLECHECKOUT_SETTRXTIMEOUT_SYSTEMEXCEPTION = "HandleCheckOut_setTrxTimeout_SystemException";
    String HANDLECHECKOUT_INSUFF_PERM = "HandleCheckOut_INSUFF_PERM";        //warn
    String HANDLECHECKOUT_CREDITSTATUS_PROBLEM = "HandleCheckOut_CreditStatus_Problem";        //warn
    String HANDLECHECKOUT_VALIDATECREDIT_REMOTEEXCEPTION = "HandleCheckOut_validateCredit_RemoteException";
    String HANDLECHECKOUT_ACCOUNTMANAGER_CREATEEXCEPTION = "HandleCheckOut_AccountManager_CreateException";
    String HANDLECHECKOUT_DEVICEMANAGER_CREATEEXCEPTION = "HandleCheckOut_DeviceManager_CreateException";
    String HANDLECHECKOUT_VALIDATEDEVICES_GENERALFAILUREEXCEPTION = "HandleCheckOut_ValidateDevices_GeneralFailureException";
    String HANDLECHECKOUT_VALIDATEDEVICES_PSEXCEPTION = "HandleCheckOut_ValidateDevices_PSException";
    String HANDLECHECKOUT_VALIDATEDEVICES_REMOTEEXCEPTION = "HandleCheckOut_ValidateDevices_RemoteException";
    String HANDLECHECKOUT_START_TRXREQUESTLINEITEMS = "HandleCheckOut_Start_TrxRequestLineItems";        //info
    String HANDLECHECKOUT_PROBLEM_TRXREQUESTLINEITEM_SKIPPED = "HandleCheckOut_Problem_TrxRequestLineItem_Skipped";        //warn
    String HANDLECHECKOUT_BLACKLISTED_DEV_SKIPPED = "HandleCheckOut_BLACKLISTED_DEV_Skipped";        //warn
    String HANDLECHECKOUT_DEVHOME_FINDBYPRIMARYKEY_REMOTEEXCEPTION = "HandleCheckOut_devhome_findByPrimaryKey_RemoteException";
    String HANDLECHECKOUT_NULL_REGION_IN_TRXREQLINEIT_SKIPPED = "HandleCheckOut_NULL_Region_in_trxreqlineit_Skipped";        //warn
    String HANDLECHECKOUT_PROBLEM_TRXREQDEVSERV_SKIPPED = "HandleCheckOut_Problem_trxreqDevServ_Skipped";        //warn
    String HANDLECHECKOUT_NO_ACTIV_PERM_IN_REQ = "HandleCheckOut_NO_ACTIV_PERM_IN_REQ";        //warn
    String HANDLECHECKOUT_NO_DEACTIV_PERM_IN_REQ = "HandleCheckOut_NO_DEACTIV_PERM_IN_REQ";        //warn
    String HANDLECHECKOUT_CHECKSERVPRD_INVALID_SERVPRD_REGION = "HandleCheckOut_checkServPrd_INVALID_SERVPRD_REGION";        //warn
    String HANDLECHECKOUT_CHECKSERVPRD_REMOTEEXCEPTION = "HandleCheckOut_checkServPrd_RemoteException";
    String HANDLECHECKOUT_DEVSERVHASETYPE_PSEXCEPTION = "HandleCheckOut_devServHasEType_PSException";
    String HANDLECHECKOUT_DEVSERV_FOUND = "HandleCheckOut_DevServ_Found";        //info
    String HANDLECHECKOUT_PROBLEM_FOUND_PENDING_PROTRX = "HandleCheckOut_Problem_Found_Pending_ProTrx";        //warn
    String HANDLECHECKOUT_REVERSE_PENDING_DEACTIV_DEV_TO_ACTIVE_DEV = "HandleCheckOut_Reverse_PENDING_DEACTIV_DEV_to_ACTIVE_DEV";    //info
    String HANDLECHECKOUT_ALREADY_ACTIVE_DEV = "HandleCheckOut_ALREADY_ACTIVE_DEV";        //info
    String HANDLECHECKOUT_ALREADY_PENDING_ACTIV_DEV = "HandleCheckOut_ALREADY_PENDING_ACTIV_DEV";        //info
    String HANDLECHECKOUT_ALREADY_PENDING_DEACTIV_DEV = "HandleCheckOut_ALREADY_PENDING_DEACTIV_DEV";        //info
    String HANDLECHECKOUT_PROBLEM_NO_PENDING_PROTRX = "HandleCheckOut_Problem_No_PENDING_ProTrx";        //warn
    String HANDLECHECKOUT_TRYED_TO_MODIFY_ALREADY_PENDING_ACTIV2_DEV = "HandleCheckOut_Tryed_to_MODIFY_ALREADY_PENDING_ACTIV2_DEV";    //warn
    String HANDLECHECKOUT_CHANGED_TO_PENDING_MODIFICATION_OFF = "HandleCheckOut_Changed_to_PENDING_MODIFICATION_OFF";        //info
    String HANDLECHECKOUT_FINDBYPRIMARYKEY_DEVSERV_FINDEREXCEPTION = "HandleCheckOut_findByPrimaryKey_DevServ_FinderException";
    String HANDLECHECKOUT_NO_EMAIL_EXISTS_FOR_DEVICE = "HandleCheckOut_No_Email_Exists_for_Device";        //warn
    String HANDLECHECKOUT_ALREADY_DEACTIV_DEV = "HandleCheckOut_ALREADY_DEACTIV_DEV";        //info
    String HANDLECHECKOUT_DEACTIVATED_DEPENDENT = "HandleCheckOut_DEACTIVATED_DEPENDENT";        //info
    String HANDLECHECKOUT_IS_PENDING_DEACTIV_DEPENDENT = "HandleCheckOut_is_PENDING_DEACTIV_DEPENDENT";        //info
    String HANDLECHECKOUT_ALREADY_PENDING_ACTIV3_DEV = "HandleCheckOut_ALREADY_PENDING_ACTIV3_DEV";        //info
    String HANDLECHECKOUT_PROTRXDAOEXCEPTION_CANT_DEACTIVATE_DEV = "HandleCheckOut_ProTrxDAOException_CANT_DEACTIVATE_DEV";
    String HANDLECHECKOUT_REVERSE_PENDING_ACTIV_DEV_TO_DEACTIVATED_DEV = "HandleCheckOut_Reverse_PENDING_ACTIV_DEV_to_DEACTIVATED_DEV";    //info
    String HANDLECHECKOUT_REVERSE_PENDING_ACTIV_DEV_TO_DEACTIVATED_DEPENDENT = "HandleCheckOut_Reverse_PENDING_ACTIV_DEV_to_DEACTIVATED_DEPENDENT";    //info
    String HANDLECHECKOUT_ALREADY_PENDING_ACTIV2_DEV = "HandleCheckOut_ALREADY_PENDING_ACTIV2_DEV";        //info
    String HANDLECHECKOUT_DEVSERVHOME_FINDBYSECKEY_FINDEREXCEPTION = "HandleCheckOut_devServhome_findBySecKey_FinderException";    //warn
    String HANDLECHECKOUT_REMOTEEXCEPTION = "HandleCheckOut_RemoteException";
    String HANDLECHECKOUT_NO_EMAIL_EXISTS_OR_REQUESTED_FOR_DEVICE = "HandleCheckOut_No_Email_Exists_or_Requested_for_Device";    //warn
    String HANDLECHECKOUT_FINISH = "HandleCheckOut_Finish";        //info
    String HANDLECHECKOUT_NOTSUPPORTEDEXCEPTION = "HandleCheckOut_NotSupportedException";
    String HANDLECHECKOUT_SYSTEMEXCEPTION = "HandleCheckOut_SystemException";
    String HANDLECHECKOUT_ROLLBACKEXCEPTION = "HandleCheckOut_RollbackException";
    String HANDLECHECKOUT_HEURISTICMIXEDEXCEPTION = "HandleCheckOut_HeuristicMixedException";
    String HANDLECHECKOUT_HEURISTICROLLBACKEXCEPTION = "HandleCheckOut_HeuristicRollbackException";
    String HANDLECHECKOUT_EXCEPTION = "HandleCheckOut_Exception";

    //JMStoTrxDispatcher
    String JMSTOTRXDISPATCHER_INICTX_NAMINGEXCEPTION = "JMStoTrxDispatcher_iniCtx_NamingException";
    String JMSTOTRXDISPATCHER_QCONFACTORY_NAMINGEXCEPTION = "JMStoTrxDispatcher_qconFactory_NamingException";
    String JMSTOTRXDISPATCHER_QUEUE_NAMINGEXCEPTION = "JMStoTrxDispatcher_Queue_NamingException";        //warn
    String JMSTOTRXDISPATCHER_QUEUE2_NAMINGEXCEPTION = "JMStoTrxDispatcher_Queue2_NamingException";
    String JMSTOTRXDISPATCHER_SEND = "JMStoTrxDispatcher_Send";        //info
    String JMSTOTRXDISPATCHER_JMSEXCEPTION = "JMStoTrxDispatcher_JMSException";

    String PROTRXDAOEXCEPTION_ = "ProTrxDAOException_";
    String CONFIGURATIONMANAGER_EXCEPTION = "ConfigurationManager_Exception";

    //processProRequest
    String PROCESSPROREQUEST_EXCEPTION = "processProRequest_Exception";
    String PROCESSPROREQUEST_FAILED_REQUEST = "processProRequest_Failed_Request";        //warn
    String PROCESSPROREQUEST_NO_PERMISSION = "processProRequest_No_Permission";        //warn
    String PROCESSPROREQUEST_FINISH = "processProRequest_Finish";        //info

    //ResponseRetry
    String CHECKRETRY = "CheckRetry";  //info
    String CHECKRETRY_EXCEPTION = "CheckRetry_Exception";
    String CHECKRETRY_SUCEEDED = "CheckRetry_Succeeded";    //info
    String NOTIFYMAXRETRIESREACHED = "NotifyMaxRetriesReached";
    String NOTIFYMAXRETRIESREACHED_EXCEPTION = "NotifyMaxRetriesReached_Exception";
    String CRERETRY_NEW_TRXHDR = "CreRetry_New_TrxHdr";  //info
    String CRERETRY_NEW_RESPONSE_RETRIES = "CreRetry_New_Response_Retries";  //info
    String CRERETRY_NO_LINEITEMS = "CreRetry_No_LineItems";  //warn
    String CRERETRY_ALREADY_EXISTS = "CreRetry_Already_Exists";  //warn
    String CRERETRY_EXCEPTION = "CreRetry_Exception";

    //ContractUpdate
    String RETRYCONTRACT = "RetryContract";  //info
    String RETRYCONTRACT_SKIPPED = "RetryContract_Skipped";  //info
    String RETRYCONTRACT_EXCEPTION = "RetryContract_Exception";
    String EXECUTE = "execute";  // info
    String EXECUTE_SKIP_ERROR = "execute_Skip_Error";  // warn
    String EXECUTE_SKIP_NO_TRXHDR = "execute_Skip_No_TrxHdr";        //warn
    String EXECUTE_SKIP_DIFFERENT_TRXHDR = "execute_Skip_Different_TrxHdr";        //warn
    String EXECUTE_SKIP_NO_BILLINGID = "execute_Skip_No_BillingId";        //info
    String EXECUTE_SKIP_NO_CONTRACTNO = "execute_Skip_No_ContractNo";        //info
    String EXECUTE_SKIP_INVALIDPARAM = "execute_Skip_InvalidParam";  // warn
    String EXECUTE_CREATESERVICECONTRACT_REMOTEEXCEPTION = "execute_CreateServiceContract_RemoteException";
    String EXECUTE_CONFIGMGR_EXCEPTION = "execute_ConfigMgr_Exception";
    String EXECUTE_DEACTIVATESERVICE_REMOTEEXCEPTION = "execute_DeactivateService_RemoteException";
    String EXECUTE_SUSPENDSERVICE_REMOTEEXCEPTION = "execute_SuspendService_RemoteException";
    String EXECUTE_RESUMESERVICE_REMOTEEXCEPTION = "execute_ResumeService_RemoteException";
    String EXECUTE_MODIFYBILLINGCOM_REMOTEEXCEPTION = "execute_ModifyBillingCom_RemoteException";
    String UPDATETRXAFTERSAPCONTR_START = "updateTrxAfterSAPContr_Start";        //info
    String UPDATETRXAFTERSAPCONTR_EXCEPTION = "updateTrxAfterSAPContr_Exception";
    String UPDATETRXAFTERSAPCONTR_FINISH = "updateTrxAfterSAPContr_Finish";        //info
    String UPDATETRXCONTR = "updateTrxContr";        //info
    String UPDATETRXCONTR_EXCEPTION = "updateTrxContr_Exception";
    String UPDATETRXCONTR_SAPPROBLEM = "updateTrxContr_SAPproblem";        //warn
    String UPDATETRXAFTERSAPMODBILLCOM = "updateTrxAfterSAPModBillCom";        //info
    String UPDATETRXAFTERSAPMODBILLCOM_EXCEPTION = "updateTrxAfterSAPModBillCom_Exception";
    String UPDATETRXAFTERSAPMODBILLCOM_SAPPROBLEM = "updateTrxAfterSAPModBillCom_SAPproblem";
    //DeviceControl
    String UPDATEDEVICEINFO_DIFFERENT_ESN = "updateDeviceInfo_Different_ESN";        //warn
    String UPDATEDEVICEINFO_DIFFERENT_SOLDTO = "updateDeviceInfo_Different_SoldTo";        //warn
    String UPDATEDEVICEINFO_REMOTEEXCEPTION = "updateDeviceInfo_RemoteException";
    String PERFORMREQUESTFORDEVICE_EXCEPTION = "performRequestForDevice_Exception";
    String VALIDATEDEV_ANOTHER_SOLDTO = "validateDev_Another_SoldTo";    //warn
    String VALIDATEDEV_EXCEPTION = "validateDev_Exception";


    /**
     * *********************************************************************
     * DBCommand, Config, Web Events                     *
     * **********************************************************************
     */
    //SQL Accessor events
    String DBCOMMAND_EXECUTE_SQL_SELECT_WITH_PARAM = "DBCommand_Execute_SQL_Select_With_Parameters";
    String DBCOMMAND_EXECUTE_SQL_UPDATE = "DBCommand_Execute_SQL_Update";
    String DBCOMMAND_EXECUTE_SQL_UPDATE_WITH_PARAM = "DBCommand_Execute_SQL_Update_With_Parameters";
    String DBCOMMAND_SET_SQL_STATEMENT_PARAMETERS = "DBCommand_Set_SQL_Statement_Parameters";
    String DBCOMMAND_EXECUTE_STORED_PROC = "DBCommand_Execute_Stored_Procedure";
    String DBCOMMAND_EXECUTE_STORED_FUNC = "DBCommand_Execute_Stored_Function";
    String DBCOMMAND_RELEASE_DB_RESOURCES = "DBCommand_Release_DB_Resources";
    String DBCONNECTION_CONNECT_TO_DB = "DBConnection_Connect_To_DB";
    String DBCOMMAND_EXECUTE_STORED_FUNC_WITH_RETURN_OBJECT = "DBCommand_Execute_Stored_Function_With_Return_Object";


    //ConfigManager events
    String CONFIG_GET_DEVICE_PROPERTIES = "Config_Get_Device_Properties";
    String CONFIG_GET_DEACT_CODES = "Config_Get_Deact_Codes";
    String CONFIG_GET_REGIONS = "Config_Get_Regions";
    String CONFIG_GET_SERVICES_FIND_NETOPSERVICESHOME = "Config_Get_Services_Find_NetopServicesHome";
    String CONFIG_GET_RELAY_REGIONS = "Config_Get_Relay_Regions"; //cr330
    String CONFIG_GET_ROLES = "Config_Get_Roles";
    String CONFIG_GET_HWPRODUCTS = "Config_Get_HWProducts";
    String CONFIG_GET_HWPRDSERVICES = "Config_Get_HWPRDServices";
    String CONFIG_GET_LANGUAGES = "Config_Get_Languages";
    String CONFIG_GET_STATUSES = "Config_Get_Statuses";
    String CONFIG_GET_SYSTEM_PARM_KEY = "Config_Get_System_Parm_Key";
    String CONFIG_GET_SYSTEM_PARMS = "Config_Get_System_Parms";
    String CONFIG_GET_SYSTEM_PARMS_DETAILS = "Config_Get_System_Parms_Details";
    String CONFIG_GET_DEVICE_SERVICES = "Config_Get_Device_Services";
    String CONFIG_SET_SYSTEM_PARMS = "Config_Set_System_Parms";
    String CONFIG_GET_SERVICES = "Config_Get_Services";
    String CONFIG_GET_ROLES_BY_GROUP = "Config_Get_Roles_By_Group";
    String CONFIG_GET_PERMISSIONS_BY_ROLE = "Config_Get_Permissions_By_Role";
    String CONFIG_GET_NETWORK_TYPES = "Config_Get_Network_Types";
    String CONFIG_GET_RESPONSE_RETRY_COUNT = "Config_Get_Response_Retry_Count";
    String CONFIG_GET_NETWORK_BILLING_IDS = "Config_Get_Network_Billing_Ids";

    //WEB events
    String WEB_LOGIN = "Web_Login";
    String WEB_UPDATE_ACCOUNT = "Web_Update_Account";
    String WEB_CHECKOUT = "Web_Checkout";
    String WEB_RETRY_SAP_CONTRACT = "Web_Retry_SAP_Contract";
    String WEB_USER_LOOKUP = "Web_User_Lookup";
    String WEB_CUSTOMER_LIST = "Web_Customer_List";
    String WEB_CREATE_CUSTOMER = "Web_Create_Customer";
    String WEB_CREATE_USER = "Web_Create_User";
    String WEB_DEVICE_LOOKUP = "Web_Device_Lookup";
    String WEB_DEVICE_LIST = "Web_Device_List";
    String WEB_UPDATE_SYSTEM_PARMS = "Web_Update_System_Parms";

    String WEB_REQUEST_PASSWORD = "Web_Request_Password";
    String WEB_CHANGE_PASSWORD = "Web_Change_Password";
    String WEB_ACCOUNT_LOOKUP = "Web_Account_Lookup";
    String WEB_VALIDATE_DEVICE = "Web_Validate_Device";
    String WEB_BATCH_UPLOAD = "Web_Batch_Upload";
    String WEB_XML_UPLOAD = "Web_XML_Upload";
    String WEB_GET_BILLTOS = "Web_Get_Billtos";
    String WEB_CREATE_PROREQUESTOR = "Web_Create_ProRequestor";
    String WEB_UPDATE_PROREQUESTOR = "Web_Update_ProRequestor";
    String WEB_SERVICE_LOOKUP = "Web_Service_Lookup";
    String WEB_SERVICE_LIST = "Web_Service_List";
    String WEB_PROREQUEST_CHECKOUT = "Web_ProRequest_Checkout";
    String WEB_VERIFY_USER_STATUS = "Web_Verify_User_Status";
    String WEB_RESPONSE_RETRIES_REACHED_LIST = "Web_Response_Retries_Reached_List";
    String WEB_RESPONSE_RETRIES_LIST = "Web_Response_Retries_List";
    String WEB_RESPONSE_RETRY = "Web_Response_Retry";
    String WEB_FIND_SERVICE = "Web_Find_Service";
    String WEB_FIND_ALL_SERVICES = "Web_Find_All_Services";
    String WEB_GET_ALL_NETOP_SERVICES = "Web_Get_All_Netop_Services";

    String WEB_DPIN_BROADCAST_RETRIES_REACHED_LIST = "Web_Dpin_Broadcast_Retries_Reached_List";
    String WEB_DPIN_BROADCAST_RETRIES = "Web_Dpin_Broadcast_Retries";

    String WEB_HWPRD_LIST = "Web_HwPrd_List";
    String WEB_LOOKUP_HWPRD = "Web_Lookup_Hwprd";
    String WEB_ADD_HWPRD = "Web_Add_Hwprd";
    String WEB_UPDATE_HWPRD = "Web_Update_Hwprd";
    String WEB_REMOVE_HWPRD = "Web_Remove_Hwprd";

    String WEB_PARTNER_LIST = "Web_Partner_List";
    String WEB_GET_PARTNER = "Web_Get_Partner";
    String WEB_ADD_PARTNER = "Web_Add_Hwprd";
    String WEB_UPDATE_PARTNER = "Web_Update_Partner";
    String WEB_REMOVE_PARTNER = "Web_Remove_Partner";
    String WEB_PARTNER_GET_ALLOWED_SERVS = "Web_Partner_Get_Allowed_Servs";

    String WEB_SENDBATCH_NOTIFICATION = "WEB_SendBatch_Notification";

    String WEB_SERVICE_PRD_LIST = "Web_ServicePrd_List";
    String WEB_LOOKUP_SERVICE_PRD = "Web_Lookup_ServicePrd";
    String WEB_ADD_SERVICE_PRD = "Web_Add_ServicePrd";
    String WEB_UPDATE_SERVICE_PRD = "Web_Update_ServicePrd";
    String WEB_REMOVE_SERVICE_PRD = "Web_Remove_ServicePrd";

    String WEB_SERVICE_TYPE_LIST = "Web_Service_Type_List";
    String WEB_ADD_SERVICE_TYPE = "Web_Add_Service_Type";
    String WEB_UPDATE_SERVICE_TYPE = "Web_Update_Service_Type";
    String WEB_REMOVE_SERVICE_TYPE = "Web_Remove_Service_Type";

    String WEB_UPDATE_SERV_ACCESS = "Web_Update_Serv_Access";

    String WEB_SERVICE_BOOK_PUSH = "Web_Service_Book_Push";
    String WEB_ONE_TIME_SERVICE_BOOK_PUSH = "Web_One_Time_Service_Book_Push"; 
    String WEB_SEARCH_ASSIGNED_SERVS = "Web_Search_Assigned_Servs";
    String WEB_ASSIGN_SERVICES = "Web_Assign_Services";
    String WEB_BATCH_VIEW_GET_ALLOWED = "Web_Batch_View_Get_Allowed";
    String WEB_ADMIN_DEVICE_LOOKUP = "Web_Admin_Device_Lookup";

    String WEB_ADD_PIN_RANGE = "Web_Add_Pin_Range";
    String WEB_UPDATE_PIN_RANGE = "Web_Update_Pin_Range";
    String WEB_REMOVE_PIN_RANGE = "Web_Remove_Pin_Range";
    String WEB_RECYCLE_PINS = "Web_Recycle_Pins";
    String WEB_RIM_HANDHELD_ADMIN = "Web_Rim_Handheld_Admin";

    String WEB_VAR_LIST = "Web_VAR_List";
    String WEB_VAR_DETAILS = "Web_VAR_Details";
    String WEB_CREATE_VAR = "Web_Create_VAR";
    String WEB_UPDATE_VAR = "Web_Update_VAR";

    String WEB_SERVICE_BOOK_LIST = "Web_Service_Book_List";
    String WEB_LOOKUP_SERVICE_BOOK = "Web_Lookup_Service_Book";
    String WEB_SERVICE_BOOK_XML_UPLOAD = "Web_Service_Book_XML_Upload";
    String WEB_SERVICE_BOOK_XML_RELOAD = "Web_Service_Book_XML_Reload";
    String WEB_CREATE_SERVICE_RECORD = "Web_Create_Service_Record";
    String WEB_EDIT_NEW_SERVICE_RECORD = "Web_Edit_New_Service_Record";
    String WEB_UPDATE_SERVICE_RECORD = "Web_Update_Service_Record";
    String WEB_REMOVE_SERVICE_RECORD = "Web_Remove_Service_Record";

    String WEB_SB_TEMPLATE_LIST = "Web_SB_Template_List";
    String WEB_LOOKUP_SB_TEMPLATE = "Web_Lookup_SB_Template";
    String WEB_CREATE_SB_TEMPLATE = "Web_Create_SB_Template";
    String WEB_UPDATE_SB_TEMPLATE = "Web_Update_SB_Template";
    String WEB_REMOVE_SB_TEMPLATE = "Web_Remove_SB_Template";

    String WEB_ALLOWED_DEVS_LIST = "Web_Allowed_Devs_List";
    String WEB_CREATE_ALLOWED_DEV = "Web_Create_Allowed_Dev";
    String WEB_REMOVE_ALLOWED_DEVS = "Web_Remove_Allowed_Devs";
    /**
     * *********************************************************************
     * Account Events	   		                            *
     * **********************************************************************
     */

    //SAPCustomerDAO
    String GETPLATINUMFLAG_SQLEXCEPTION = "getPlatinumFlag_SQLException"; //error
    String CUSTOMEREXISTS_SQLEXCEPTION = "customerExists_SQLException"; //error
    String CUSTOMEREXISTS_CUSTOMER_NUM_IS_EMPTY = "customerExists_customerNumIsEmpty";
    String INSERTSAPCUSTOMER_INVALID_PARAM = "insertSAPCustomer_INVALID_PARAM"; //warn
    String INSERTSAPCUSTOMER_SAPCUSTOMER_EXISTS = "insertSAPCustomer_customerExists"; //error
    String INSERTSAPCUSTOMER_ERROR = "insertSAPCustomer_ERROR"; //error
    String SELECTSAPCUSTOMER_NO_RECORD_FOR_PRIMARY_KEY = "selectSAPCustomer_noRecordForPrimaryKey"; //error
    String SELECTSAPCUSTOMER_CUSTOMER_NUM_IS_EMPTY = "selectSAPCustomer_customerNumIsEmpty";
    String SELECTSAPCUSTOMER_SQLEXCEPTION = "selectSAPCustomer_SQLException"; //error
    String DELETESAPCUSTOMER_ERROR = "deleteSAPCustomer_ERROR"; //error
    String DELETESAPCUSTOMER_CUSTOMER_NUM_IS_EMPTY = "deleteSAPCustomer_customerNumIsEmpty";
    String UPDATESAPCustomer_ERROR = "updateSAPCustomer_ERROR"; //error
    String SAPCUSTOMERLIST_SQLEXCEPTION = "sapCustomerList_SQLException"; //error
    String SAPCUSTOMERFINDBYPRIMARYKEY_NOT_FOUND = "sapCustomerFindByPrimaryKey_notFound"; //error

    // ------CR285 Hide Password Support-------
    String CHANGE_OTHER_STAFF_PASSWORD = "Change_Other_Staff_Password";

    //AccountDAO
    String FINDBYPRIMARYKEY_NOT_FOUND = "findByPrimaryKey_Not_Found";        //info
    String FINDBYSTATUS_SQLEXCEPTION = "findByStatus_SQLException";
    String FINDBYCUSTOMERNUM_SQLEXCEPTION = "findByCustomerNum_SQLException";
    String FINDALL_SQLEXCEPTION = "findAll_SQLException";
    String GETPERMISSIONS_SQLEXCEPTION = "getPermissions_SQLException";
    String GETROLES_SQLEXCEPTION = "getRoles_SQLException";
    String USEREXISTS_SQLEXCEPTION = "userExists_SQLException";
    String INSERTACCOUNT_INVALID_PARAM = "insertAccount_INVALID_PARAM";        //warn
    String INSERTACCOUNT_ACCOUNT_EXISTS = "insertAccount_ACCOUNT_EXISTS";        //warn
    String INSERTACCOUNT_ERROR = "insertAccount_ERROR";
    String SELECTACCOUNT_NO_RECORD_FOR_PRIMARY_KEY = "selectAccount_No_Record_for_Primary_Key";        //warn
    String SELECTACCOUNT_SQLEXCEPTION = "selectAccount_SQLException";
    String DELETEACCOUNT_ERROR = "deleteAccount_ERROR";
    String UPDATEACCOUNT_ILLEGAL_DATA_VALUES = "updateAccount_Illegal_Data_Values";        //warn
    String UPDATEACCOUNT_ERROR = "updateAccount_ERROR";
    String GETHWPRDS_SQLEXCEPTION = "getHWPRDs_SQLException";
    String GETSALESORG_SQLEXCEPTION = "getSalesOrg_SQLException";
    String CLOSERESULTSET_SQLEXCEPTION = "closeResultSet_SQLException";
    String FINDBYROLEID_SQLEXCEPTION = "findByRoleId_SQLException";
    //AccountEJB
    String LOGINFAILED_NAMINGEXCEPTION = "loginFailed_NamingException";
    String LOGINFAILED_TIMETRIGGEREXCEPTION = "loginFailed_TimeTriggerException";
    String LOGINFAILED_PARAMSETEXCEPTION = "loginFailed_ParamSetException";
    String LOGINFAILED_SQLEXCEPTION = "loginFailed_SQLException";
    String LOGINFAILED_REMOTEEXCEPTION = "loginFailed_RemoteException";
    String LOGINFAILED_CREATEEXCEPTION = "loginFailed_CreateException";
    String LOGINFAILED_MAILDELIVERYEXCEPTION = "loginFailed_MailDeliveryException";
    String GETMANAGEDACCOUNTS_INSUFF_PERM = "getManagedAccounts_INSUFF_PERM";        //warn
    String LOGINFAILED_ACCOUNTLOCKED = "loginFailed_Account_Locked"; //info added in REQ413
    String LOGINFAILED_EMAILNOTIFICATIONSENT = "loginFailed_Email_Notification_Sent"; //info added in REQ413
    //AccountManagerBean
    String GETSAPCUSTOMERLIST_SQLEXCEPTION = "getSapCustomerList_SQLException";        //error
    String GETSAPCUSTOMER_EXCEPTION = "Get_Sap_Customer_Exception";        //error
    String NEWCUSTOMER_CUSTRESULT_REMOTEEXCEPTION = "newCustomer_custResult_RemoteException";        //fatal
    String NEWCUSTOMER_CUSTRESULT_IS_NOT_SUCCESSFUL = "newCustomer_custResult_is_Not_Successful";        //warn
    String NEWCUSTOMER_CREATEEXCEPTION_ACCOUNT_EXISTS = "newCustomer_CreateException_ACCOUNT_EXISTS";        //warn
    String NEWCUSTOMER_CREATEEXCEPTION_INVALID_PARAM = "newCustomer_CreateException_INVALID_PARAM";
    String NEWCUSTOMER_CREATEEXCEPTION = "newCustomer_CreateException";
    String NEWCUSTOMER_REMOTEEXCEPTION = "newCustomer_RemoteException";
    String NEWCUSTOMER_SQLEXCEPTION = "newCustomer_SQLException";
    String NEWCUSTOMER_REMOVEEXCEPTION = "newCustomer_RemoveException";
    String GETCUSTOMER_GCRES_IS_NOT_SUCCESSFUL = "getCustomer_gcRes_is_Not_Successful";        //warn
    String GETCUSTOMER_REMOTEEXCEPTION = "getCustomer_RemoteException";        //fatal
    String NEWACCOUNT_MUST_BE_RIM = "newAccount_Must_be_RIM";        //warn
    String NEWACCOUNT_DIFF_ACCOUNT_TYPE_EXISTS = "newAccount_Diff_ACCOUNT_TYPE_EXISTS";        //warn
    String NEWACCOUNT_CREATEEXCEPTION_ACCOUNT_EXISTS = "newAccount_CreateException_ACCOUNT_EXISTS";        //warn
    String NEWACCOUNT_CREATEEXCEPTION_INVALID_PARAM = "newAccount_CreateException_INVALID_PARAM";        //warn
    String NEWACCOUNT_CREATEEXCEPTION_ACCOUNT_TYPE = "newAccount_CreateException_ACCOUNT_TYPE";        //warn
    String NEWACCOUNT_CREATEEXCEPTION = "newAccount_CreateException";
    String NEWACCOUNT_REMOTEEXCEPTION = "newAccount_RemoteException";
    String NEWACCOUNT_SQLEXCEPTION = "newAccount_SQLException";
    String NEWACCOUNT_FINDEREXCEPTION = "newAccount_FinderException";        //warn
    String NEWPROREQUESTOR_CUSTOMER_EXISTS = "newProRequestor_Customer_Exists";
    String NEWPROREQUESTOR_CREATEEXCEPTION = "newProRequestor_CreateException";
    String NEWPROREQUESTOR_CREATEEXCEPTION_PRO_MAP_EXISTS = "newProRequestor_CreateException_Prorequestor_Map_Exists";
    String NEWPROREQUESTOR_REMOTEEXCEPTION = "newProRequestor_RemoteException";
    String NEWPROREQUESTOR_SQLEXCEPTION = "newProRequestor_SQLException";
    String NEWPROREQUESTOR_FINDEREXCEPTION = "newProRequestor_FinderException";        //warn
    String UPDATEPROREQUESTOR_CREATEEXCEPTION = "updateProRequestor_CreateException";
    String UPDATEPROREQUESTOR_CREATEEXCEPTION_PRO_MAP_EXISTS = "updateProRequestor_CreateException_Prorequestor_Map_Exists";
    String UPDATEPROREQUESTOR_PSEXCEPTION = "updateProRequestor_PSException";
    String UPDATEPROREQUESTOR_REMOTEEXCEPTION = "updateProRequestor_RemoteException";
    String UPDATEPROREQUESTOR_SQLEXCEPTION = "updateProRequestor_SQLException";
    String UPDATEPROREQUESTOR_FINDEREXCEPTION = "updateProRequestor_FinderException";        //warn
    String UPDATEPROREQUESTOR_REMOVEEXCEPTION = "updateProRequestor_RemoveException";        //warn
    String LISTMANAGEDACCOUNTS_FINDEREXCEPTION = "listManagedAccounts_FinderException";        //error
    String LISTMANAGEDACCOUNTS_REMOTEEXCEPTION = "listManagedAccounts_RemoteException";
    String LISTACCOUNTS_FINDEREXCEPTION = "listAccounts_FinderException";        //warn
    String LISTACCOUNTS_REMOTEEXCEPTION = "listAccounts_RemoteException";
    String GETHWPRDSBYCUSTNO_REMOTEEXCEPTION = "getHWPRDsByCustNo_RemoteException";
    String GETHWPRDS_FINDEREXCEPTION = "getHWPRDs_FinderException";        //warn
    String GETHWPRDS_REMOTEEXCEPTION = "getHWPRDs_RemoteException";
    String LISTACCOUNTSBYVARID = "listAccountsByVarId";
    String LISTACCOUNTSBY_INVALID_PARAM = "listAccountsBy_INVALID_PARAM";
    String LISTACCOUNTSBY_INSUFF_PERM = "listAccountsBy_INSUFF_PERM";        //warn
    String LISTACCOUNTSBY_FINDEREXCEPTION = "listAccountsBy_FinderException";        //warn
    String LISTACCOUNTSBY_REMOTEEXCEPTION = "listAccountsBy_RemoteException";
    String GETMANAGEDACCOUNT_MANAGER_FINDEREXCEPTION = "getManagedAccount_Manager_FinderException";        //warn
    String GETMANAGEDACCOUNT_MANAGER_REMOTEEXCEPTION = "getManagedAccount_Manager_RemoteException";
    String GETMANAGEDACCOUNT_INSUFF_PERM = "getManagedAccount_INSUFF_PERM";        //warn
    String GETMANAGEDACCOUNT_FINDEREXCEPTION = "getManagedAccount_FinderException";        //warn
    String GETMANAGEDACCOUNT_REMOTEEXCEPTION = "getManagedAccount_RemoteException";
    String HANDLEUNLOCKACCOUNTEVENT_FINDEREXCEPTION = "handleUnlockAccountEvent_FinderException";        //warn
    String HANDLEUNLOCKACCOUNTEVENT_REMOTEEXCEPTION = "handleUnlockAccountEvent_RemoteException";
    String UPDATEACCOUNT_FINDEREXCEPTION = "updateAccount_FinderException";        //warn
    String UPDATEACCOUNT_REMOTEEXCEPTION = "updateAccount_RemoteException";
    String SENDPASSWORD_FINDEREXCEPTION = "sendPassword_FinderException";        //warn
    String SENDPASSWORD_REMOTEEXCEPTION = "sendPassword_RemoteException";
    String SENDPASSWORD_MAILDELIVERYEXCEPTION = "sendPassword_MailDeliveryException";
    String SENDPASSWORD_MAIL_CREATEEXCEPTION = "sendPassword_Mail_CreateException";
    String SENDPASSWORD_MAIL_REMOTEEXCEPTION = "sendPassword_MAil_RemoteException";
    String LOGIN_FINDEREXCEPTION = "login_FinderException";        //info
    String LOGIN_REMOTEEXCEPTION = "login_RemoteException";
    String LOGIN_EXCEPTION = "login_Exception";
    String LOGIN_ACCOUNT_LOCKED = "login_ACCOUNT_LOCKED";        //warn
    String LOGIN_ACCOUNT_INACTIVE = "login_ACCOUNT_INACTIVE";        //warn
    String LOGIN_INVALID_PASSWORD = "login_INVALID_PASSWORD";        //warn
    String LOGIN_SUCCESSFUL = "login_Successful";        //info
    String LOGIN_GETDETAILS_REMOTEEXCEPTION = "login_getDetails_RemoteException";
    String VALIDATECREDIT_CREATEEXCEPTION = "validateCredit_CreateException";
    String VALIDATECREDIT_REMOTEEXCEPTION = "validateCredit_RemoteException";
    String VALIDATECREDIT_CREDITVALIDATIONEXCEPTION = "validateCredit_CreditValidationException";        //warn
    String CHECKCREDITLIMIT_CREATEEXCEPTION = "checkCreditLimit__CreateException";
    String CHECKCREDITLIMIT_REMOTEEXCEPTION = "checkCreditLimit__RemoteException";
    String CHECKCREDITLIMIT_MAILDELIVERYEXCEPTION = "checkCreditLimit__MailDeliveryException";

    String NEWSUBSCRIBER_CANNOT_RETRIEVE_DEVICE_DETAILS = "newSubscriber_Cannot_Retrieve_Device_Details";
    String NEWSUBSCRIBER_DEVICE_ALREADY_OWNED = "newSubscriber_DEVICE_ALREADY_OWNED";
    String NEWSUBSCRIBER_ACCOUNT_EXISTS = "newSubscriber_ACCOUNT_EXISTS";        //info
    String NEWSUBSCRIBER_INVALID_PARAM = "newSubscriber_INVALID_PARAM";
    String NEWSUBSCRIBER_GENERAL_CREATEEXCEPTION = "newSubscriber_GENERAL_CreateException";
    String NEWSUBSCRIBER_REMOTEEXCEPTION = "newSubscriber_RemoteException";
    String NEWSUBSCRIBER_SQLEXCEPTION = "newSubscriber_SQLException";
    String NEWSUBSCRIBER_VALIDATIONEXCEPTION = "newSubscriber_ValidationException";
    String NEWSUBSCRIBER_USEREXISTSEXCEPTION = "newSubscriber_UserExistsException";
    String NEWSUBSCRIBER_ACCESSOREXCEPTION = "newSubscriber_AccessorException";
    String NEWSUBSCRIBER_CONNECTIONEXCEPTION = "newSubscriber_ConnectionException";
    String NEWSUBSCRIBER_LOGINEXCEPTION = "newSubscriber_LoginException";        //info
    String NEWSUBSCRIBER_NETWORKADDRESSINUSEEXCEPTION = "newSubscriber_NetworkAddressInUseException";

    String NEWSUBSCRIBER2_FINDEREXCEPTION = "newSubscriber2_FinderException";    //info
    String NEWSUBSCRIBER2_ACCOUNT_EXISTS = "newSubscriber2_ACCOUNT_EXISTS";        //info
    String NEWSUBSCRIBER2_INVALID_PARAM = "newSubscriber2_INVALID_PARAM";
    String NEWSUBSCRIBER2_GENERAL_CREATEEXCEPTION = "newSubscriber2_GENERAL_CreateException";
    String NEWSUBSCRIBER2_REMOTEEXCEPTION = "newSubscriber2_RemoteException";
    String NEWSUBSCRIBER2_SQLEXCEPTION = "newSubscriber2_SQLException";
    String NEWSUBSCRIBER2_ACCESSOREXCEPTION = "newSubscriber2_AccessorException";
    String NEWSUBSCRIBER2_LOGINEXCEPTION = "newSubscriber2_LoginException";        //info
    String NEWSUBSCRIBER2_CONNECTIONEXCEPTION = "newSubscriber2_ConnectionException";
    String NEWSUBSCRIBER2_NETWORKADDRESSINUSEEXCEPTION = "newSubscriber2_NetworkAddressInUseException";

    String UPDATESUBSCRIBER_FINDEREXCEPTION = "updateSubscriber_FinderException";        //info
    String UPDATESUBSCRIBER_REMOTEEXCEPTION = "updateSubscriber_RemoteException";
    String UPDATESUBSCRIBER_VALIDATIONEXCEPTION = "updateSubscriber_ValidationException";
    String UPDATESUBSCRIBER_ACCESSOREXCEPTION = "updateSubscriber_AccessorException";
    String UPDATESUBSCRIBER_LOGINEXCEPTION = "updateSubscriber_LoginException";        //info
    String UPDATESUBSCRIBER_CONNECTIONEXCEPTION = "updateSubscriber_ConnectionException";
    String UPDATESUBSCRIBER_NETWORKADDRESSINUSEEXCEPTION = "updateSubscriber_NetworkAddressInUseException";

    String SENDMAILFWDREQUEST_CREATEEXCEPTION = "sendMailFwdRequest_CreateException";
    String SENDMAILFWDREQUEST_REMOTEEXCEPTION = "sendMailFwdRequest_RemoteException";
    String REMOVEMAILFORWARDING_CREATEEXCEPTION = "removeMailForwarding_CreateException";
    String REMOVEMAILFORWARDING_REMOTEEXCEPTION = "removeMailForwarding_RemoteException";
    String GETBILLTOS_NEEDTOREFRESH_EXCEPTION = "getBillTos_needToRefresh_Exception";
    String GETBILLTOS_NEEDTOREFRESH_INVALID = "getBillTos_needToRefresh_Invalid";        //warn
    String GETBILLTOS_NEEDTOREFRESH = "getBillTos_needToRefresh";        //warn
    String GETBILLTOS_NEEDTOREFRESH_QUITE_FREQUENT = "getBillTos_needToRefresh_Quite_Frequent";        //warn
    String GETBILLTOS_LASTUPDATED_EXCEPTION = "getBillTos_LastUpdated_Exception";
    String GETBILLTOS_UPDATELOCALINFO = "getBillTos_UpdateLocalInfo";        //info
    String GETBILLTOS_UPDATELOCALINFO_EXCEPTION = "getBillTos_UpdateLocalInfo_Exception";
    String GETBILLTO_INFO_EXCEPTION = "getBillTo_Info_Exception";
    String GETBILLTO = "getBillTo";
    String REFRESH_SAPCUSTOMER = "refresh_SAPCustomer";
    String UPDATELOCALBILLTO_ERROR = "updateLocalBillTo_Error";        //error
    String UPDATELOCALBILLTO_EXCEPTION = "updateLocalBillTo_Exception";
    String REMOVELOCALBILLTO_ERROR = "removeLocalBillTo_Error";
    String REMOVELOCALBILLTO_EXCEPTION = "removeLocalBillTo_Exception";
    String ADDLOCALBILLTO_EXCEPTION = "addLocalBillTo_Exception";
    String ATTAINSALESORGSFORCUST = "attainSalesOrgsForCust";        //warn
    String ATTAINSALESORGSFORCUST_EXCEPTION = "attainSalesOrgsForCust_Exception";
    String CREATESALESORGSFORCUST_EXCEPTION = "createSalesOrgsForCust_CreateException";
    String REMOVESALESORGSFORCUST_ERROR = "removeSalesOrgsForCust_Error";        //warn
    String REMOVESALESORGSFORCUST_EXCEPTION = "removeSalesOrgsForCust_Exception";
    String GETBILLTOS_FROMSAP_EXCEPTION = "getBillTos_FromSAP_Exception";
    String REMOVESUBSCRIBER_FINDEREXCEPTION = "removeSubscriber_FinderException";        //warn
    String REMOVESUBSCRIBER_REMOVEEXCEPTION = "removeSubscriber_RemoveException";
    String REMOVESUBSCRIBER_REMOTEEXCEPTION = "removeSubscriber_RemoteException";
    String REMOVESUBSCRIBER_CREATEEXCEPTION = "removeSubscriber_CreateException";
    String REMOVESUBSCRIBER_EXCEPTION = "removeSubscriber_Exception";
    String RESETDEVICEOWNERSHIP_REMOTEEXCEPTION = "resetDeviceOwnership_RemoteException";
    String RESETDEVICEOWNERSHIP_CREATEEXCEPTION = "resetDeviceOwnership_CreateException";
    String SUBSCRIBER_MGR_GET_SCENARIO_EX = "getScenario_Exception";

    //TimeEventListenerMDB
    String ONMESSAGE_CREATEEXCEPTION = "onMessage_CreateException";
    String ONMESSAGE_REMOTEEXCEPTION = "onMessage_RemoteException";
    String ONMESSAGE_PSEXCEPTION = "onMessage_PSException";
    String ONMESSAGE_WRONG_SWITCH = "onMessage_Wrong_Switch";        //warn

    //PSTimerBean
    String INITPSTIMER_SQLEXCEPTION = "initPSTimer_SQLException";
    String INITPSTIMER_NAMINGEXCEPTION = "initPSTimer_NamingException";        //warn
    String INITPSTIMER_PARAMSETEXCEPTION = "initPSTimer_ParamSetException";        //warn
    String INITPSTIMER_TIMETRIGGEREXCEPTION = "initPSTimer_TimeTriggerException";
    String SCHEDULERECURRINGTRIGGER_ALREADY_EXISTS = "scheduleRecurringTrigger_Already_Exists";        //warn
    String SCHEDULEONETIMETRIGGER_ALREADY_EXISTS = "scheduleOneTimeTrigger_Already_Exists";        //warn

    //EventDispatcherEJB
    String DISPATCHEVENT_PSEXCEPTION = "dispatchEvent_PSException";
    String DISPATCHEVENT_CREATEEXCEPTION = "dispatchEvent_CreateException";
    String DISPATCHEVENT_REMOTEEXCEPTION = "dispatchEvent_RemoteException";
    String DISPATCHCHECKOUTEV_CREATEEXCEPTION = "dispatchCheckoutEv_CreateException";
    String DISPATCHCHECKOUTEV_REMOTEEXCEPTION = "dispatchCheckoutEv_RemoteException";
    String DISPATCHPOLLEVENT_CREATEEXCEPTION = "dispatchPollEvent_CreateException";
    String DISPATCHPOLLEVENT_REMOTEEXCEPTION = "dispatchPollEvent_RemoteException";
    String DISPATCHUNLOCKACCOUNTEVENT_CREATEEXCEPTION = "dispatchUnlockAccountEvent_CreateException";
    String DISPATCHUNLOCKACCOUNTEVENT_REMOTEEXCEPTION = "dispatchUnlockAccountEvent_RemoteException";
    String DISPATCHSTARTTIMEREVENT_CREATEEXCEPTION = "dispatchStartTimerEvent_CreateException";
    String DISPATCHSTARTTIMEREVENT_REMOTEEXCEPTION = "dispatchStartTimerEvent_RemoteException";
    String DISPATCHRAWACTACKEVENT_CREATEEXCEPTION = "dispatchRawActAckEvent_CreateException";
    String DISPATCHRAWACTACKEVENT_REMOTEEXCEPTION = "dispatchRawActAckEvent_RemoteException";
    String DISPATCHRAWACTACKEVENT_INVALID_NETOPID = "dispatchRawActAckEvent_Invalid_NetOpId";        //warn
    String DISPATCHNETOPSENDEVENT_CREATEEXCEPTION = "dispatchNetOpSendEvent_CreateException";
    String DISPATCHNETOPSENDEVENT_REMOTEEXCEPTION = "dispatchNetOpSendEvent_RemoteException";
    String DISPATCHNETOPSENDEVENT_INVALID_NETOPID = "dispatchNetOpSendEvent_Invalid_NetOpId";        //warn
    String DISPATCHISPACKEVENT_CREATEEXCEPTION = "dispatchISPAckEvent_CreateException";
    String DISPATCHISPACKEVENT_REMOTEEXCEPTION = "dispatchISPAckEvent_RemoteException";
    String DISPATCHISPACKEVENT_EXCEPTION = "dispatchISPAckEvent_Exception";
    String DISPATCHPROSERVEV_NAMINGEXCEPTION = "dispatchProServEv_NamingException";
    String DISPATCHPROSERVEV_JMSEXCEPTION = "dispatchProServEv_JMSException";

    String DISPATCHBATCHREQUESTEV_NAMINGEXCEPTION = "dispatchBatchRequestEv_NamingException";
    String DISPATCHBATCHREQUESTEV_JMSEXCEPTION = "dispatchBatchRequestEv_JMSException";
    String DISPATCHDPINBROADCASTEV_NAMINGEXCEPTION = "dispatchDPinBroadcastEv_NamingException";
    String DISPATCHDPINBROADCASTEV_JMSEXCEPTION = "dispatchDPinBroadcastEv_JMSException";

    String INVALID_SOAP_TIMEOUT = "Invalid_SOAP_Timeout_System_Parm"; //error
    String SOAP_EXCEPTION = "SOAP_Exception";                             //error

    //NetworkAddress
    String ISNETWORKADDRESSISUSED_ACCESSOREXCEPTION = "isNetworkAddressIsUsed_AccessorException";
    String ISNETWORKADDRESSISUSED_NAMINGEXCEPTION = "isNetworkAddressIsUsed_NamingException";

    //MessagingProviderBean
    String EJBREMOVE_MESSAGINGPROVIDERDAOEXCEPTION = "ejbRemove_MessagingProviderDAOException";
    String EJBSTORE_MESSAGINGPROVIDERDAOEXCEPTION = "ejbStore_MessagingProviderDAOException";
    String EJBLOAD_MESSAGINGPROVIDERDAOEXCEPTION = "ejbLoad_MessagingProviderDAOException";
    String EJBCREATE_MESSAGINGPROVIDERDAOEXCEPTION = "ejbCreate_MessagingProviderDAOException";
    String EJBCREATE_SQLEXCEPTION = "ejbCreate_SQLException";
    String EJBFINDBYPRIMARYKEY_MESSAGINGPROVIDERDAOEXCEPTION = "ejbFindByPrimaryKey_MessagingProviderDAOException";
    String EJBFINDBYDOMAIN_MESSAGINGPROVIDERDAOEXCEPTION = "ejbFindByDomain_MessagingProviderDAOException";
    String RETRIEVEPOPSERVERS_MESSAGINGPROVIDERDAOEXCEPTION = "retrievePOPServers_MessagingProviderDAOException";
    String ADDPOPSERVER_MESSAGINGPROVIDERDAOEXCEPTION = "addPOPServer_MessagingProviderDAOException";

    //MessagingProviderDAO
    String FINDMSPBYID_SQLEXCEPTION = "findMSPById_SQLException";
    String FINDMSPBYDOMAIN_NOT_FOUND = "findMSPByDomain_Not_Found";        //warn
    String FINDMSPBYDOMAIN_SQLEXCEPTION = "findMSPByDomain_SQLException";
    String CREATE_INVALID_PARAM = "create_Invalid_Param";
    String CREATE_MSP_EXISTS = "create_MSP_Exists";        //warn
    String CREATE_INSERT_ERROR = "create_Insert_Error";
    String STORE_ILLEGAL_DATA_VALUES_FOR_UPDATE = "store_Illegal_Data_Values_for_Update";
    String STORE_UPDATE_ERROR = "store_Update_Error";
    String REMOVE_DELETE_ERROR = "remove_Delete_Error";
    String LOAD_NO_RECORD_FOR_PRIMARY_KEY = "load_No_Record_for_Primary_Key";
    String LOAD_SQLEXCEPTION = "load_SQLException";
    String RETRIEVEPOPSERVERS_SQLEXCEPTION = "retrievePOPServers_SQLException";
    String ADDPOPSERVER_INSERT_ERROR = "addPOPServer_Insert_Error";
    String RETRIEVEMSPIDFORPOPSERVER_SQLEXCEPTION = "retrieveMSPIdForPOPServer_SQLException";
    String RETRIEVEMSPS_SQLEXCEPTION = "retrieveMSPs_SQLException";

    //BIS_DAO
    String GETALLBISSITES_SQLEXCEPTION = "getAllBISSites_SQLException";
    String GETALLBISSITES_GET_NETTYPE_SUPPORTED_EXCEPTION = "getAllBISSites_Get_NetType_Supported_Exception";
    String GETBISSITE_SQLEXCEPTION = "getBISSite_SQLException";
    String GETBISSITES_GET_NETTYPE_SUPPORTED_EXCEPTION = "getBISSites_Get_NetType_Supported_Exception";


    //SubscriberDAO
    String FINDBYLOGINID_SUBSCRIBER_NOT_FOUND = "findByLoginID_Subscriber_Not_Found";        //warn
    String FINDBYPRIMARYKEY_SUBSCRIBER_NOT_FOUND = "findByPrimaryKey_Subscriber_Not_Found";        //info
    String INSERTSUBSCRIBER_ERROR = "insertSubscriber_Error";
    String SELECTSUBSCRIBER_NO_RECORD = "selectSubscriber_No_Record";        //warn
    String SELECTSUBSCRIBER_SQLEXCEPTION = "selectSubscriber_SQLException";
    String UPDATESUBSCRIBER_ERROR = "updateSubscriber_Error";
    String DELETESUBSCRIBER_ERROR_IN_MSP_INTEGRATION = "deleteSubscriber_Error_in_MSP_INTEGRATION";
    String DELETESUBSCRIBER_ERROR = "deleteSubscriber_Error";
    String SUBSCRIBEREXISTS_SQLEXCEPTION = "subscriberExists_SQLException";
    String GETMAILINTEGRATIONRULES_SQLEXCEPTION = "getMailIntegrationRules_SQLException";
    String INSERTMSPINTEGRATION_GETSEQUENCENUMBER_SQLEXCEPTION = "insertMSPIntegration_getSequenceNumber_SQLException";
    String INSERTMSPINTEGRATION_ERROR = "insertMSPIntegration_Error";
    String REMOVEMSPINTEGRATION_ERROR = "removeMSPIntegration_Error";
    String UPDATEMSPINTEGRATIONRULE_ERROR = "updateMSPIntegrationRule_Error";
    String RESETREPLYTOFLAG_ERROR = "resetReplytoFlag_Error";
    String FINDPLANNAME_SQLEXCEPTION = "findPlanName_Error";

    //SubscriberBean
    String EJBREMOVE_SUBSCRIBERDAOEXCEPTION = "ejbRemove_SubscriberDAOException";
    String EJBSTORE_SUBSCRIBERDAOEXCEPTION = "ejbStore_SubscriberDAOException";
    String EJBLOAD_SUBSCRIBERDAOEXCEPTION = "ejbLoad_SubscriberDAOException";
    String EJBCREATE_SUBSCRIBERDAOEXCEPTION = "ejbCreate_SubscriberDAOException";
    String EJBFINDBYPRIMARYKEY_SUBSCRIBERDAOEXCEPTION = "ejbFindByPrimaryKey_SubscriberDAOException";        //info
    String EJBFINDBYLOGINID_SUBSCRIBERDAOEXCEPTION = "ejbFindByLoginId_SubscriberDAOException";        //warn
    String GETMAILINTEGRATIONRULES_SUBSCRIBERDAOEXCEPTION = "getMailIntegrationRules_SubscriberDAOException";
    String ADDMAILINTEGRATIONRULE_SUBSCRIBERDAOEXCEPTION = "addMailIntegrationRule_SubscriberDAOException";
    String EDITMAILINTEGRATIONRULE_SUBSCRIBERDAOEXCEPTION = "editMailIntegrationRule_SubscriberDAOException";
    String REMOVEDMAILINTEGRATIONRULE_SUBSCRIBERDAOEXCEPTION = "removedMailIntegrationRule_SubscriberDAOException";
    String RESETREPLYTOFLAG_SUBSCRIBERDAOEXCEPTION = "resetReplytoFlag_SubscriberDAOException";
    String GETBISSUBSCRIBER_ACCESSOREXCEPTION = "getBISSubscriber_AccessorException";
    String GETBISSUBSCRIBER_LOGINEXCEPTION = "getBISSubscriber_LoginException";

    //ResourceManager
    String GETSTRING_MISSINGRESOURCE = "getString_MississingResourceException";

    //Sim Lock
    String OBTAINSIMLOCK_NO_SIM_FOUND = "obtainSIMLock_SIMNotFound";
    String OBTAINSIMLOCK_SQLEXCEPTION = "obtainSIMLock_SQLException";
    String OBTAINSIMLOCK_SIMDAOEXCEPTION = "obtainSIMLock_SIMDAOException";
    String OBTAINSIMLOCK_REMOTEEXCEPTION = "obtainSIMLock_RemoteException";
    String OBTAINSIMLOCK_CREATEEXCEPTION = "obtainSIMLock_CreateException";
    String OBTAINSIMLOCK_CONCURRENT_UPDATE = "obtainSIMLock_ConcurrentUpdateException";
    String OBTAINSIMLOCK_INTERUPTEXCEPTION = "obtainSIMLock_InteruptException";
    String OBTAINSIMLOCK_NO_BILLING_ID = "obtainSIMLock_BillingIdNotAvailable";
    String OBTAINSIMLOCK_PARAMETER_NOT_SET = "obtainSIMLock_TimeoutParameterNotSet";
    String RELEASESIMLOCK_REMOTEEXCEPTION = "releaseSIMLock_RemoteException";
    String RELEASESIMLOCK_CREATEEXCEPTION = "releaseSIMLock_CreateException";
    String OBTAINSIMLOCK_SIMID_NULL = "obtainSIMLock_simId_null";
    String OBTAINSIMLOCK_BILLINGID_NULL = "obtainSIMLock_BillingId_null";
    String OBTAINSIMLOCK_SAPSOLDTO_NULL = "obtainSIMLock_SapSoldTo_null";


    /**
     * *********************************************************************
     * ARI                                                *
     * **********************************************************************
     */
    // CarrierServlet
    String CARRSERV_IO_ERROR = "Carrier_Servlet_IO_Error";
    String CARRSERV_INTERNAL_ERROR = "Carrier_Servlet_Internal_Error";
    String CARRSERV_WRITE_TO_CLIENT = "Carrier_Servlet_Write_Response";
    String CARRSERV_RETURN_ERROR = "Carrier_Servlet_Return_Error";

    // RootParser
    String ROOT_PARSER_SAX_PARSE = "Root_Parser_SAX_Parse";
    String ROOT_PARSER_VALIDATE_ERROR = "Root_Parser_Validate_Error";
    String ROOT_PARSER_VALIDATE_WARNING = "Root_Parser_Validate_Warning";

    // ARIController
    String CONTROLLER_GET_BUILDER = "Controller_Get_Builder";
    String CONTROLLER_AUTHENTICATION_FAIL = "Controller_Authentication_Fail";
    String CONTROLLER_DOCUMENT_GET_VERSION = "Controller_Document_Get_Version";
    String CONTROLLER_AUTHENTICATION_OK = "Controller_Authentication_Ok";
    String CONTROLLER_PROCESSING_ERROR = "Controller_Processing_Error";
    String CONTROLLER_NO_SYNC_RESPONSE = "Controller_No_Sync_response";
    String CONTROLLER_CARRIER_LOGIN = "Controller_Carrier_Login";
    String CONTROLLER_REQUESTOR_NOT_FOUND = "Controller_Requestor_Not_Found";

    // CarrierResponseChannel
    String CARRIERRESP_REQUESTOR_NOT_FOUND = "CarrierResp_Requestor_Not_Found";
    String CARRIERRESP_NO_REQUESTOR_INFO = "CarrierResp_No_Requestor_Info";
    String CARRIERRESP_INVALID_PROTOCOL = "CarrierResp_Invalid_Protocol";
    String CARRIERRESP_PROCESS_POST = "CarrierResp_Process_Post";
    String CARRIERRESP_PROCESS_SOAP = "CarrierResp_Process_Soap";
    String CARRIERRESP_BACKUP_FAIL = "CarrierResp_Backup";
    String CARRIERRESP_EXCEPTION = "CarrierResp_Exception";

    // ResponseBuilder
    String RESPBLDR_GET_ERR_DESC = "RespBuilder_Get_Err_Desc";
    String BUILDER_GET_DTD_URL = "RespBuilder_Get_DTD_URL";
    String RESPBLDR_NO_LOGIN = "RespBuiler_No_Login";

    // EventBuilder
    String EVENTBLDR_GET_MAX_LINEITEMS = "EventBuilder_Get_Max_LineItems";
    String AWS_BILLINGID_NOT_IMSI = "AWS_BillingId_Not_IMSI";
    String AWS_XML_TRANSFORMATION_FAILED = "AWS_XML_Transformation_Failed";

    // T-Mobile ARI
    String TMO_RECEIVED_REQUEST = "TMO_ARI_Received_Request";
    String TMO_NORMAL_RESPONSE = "TMO_ARI_Sending_Normal_Response";
    String TMO_BUILDING_NORMAL_RESPONSE = "TMO_ARI_Building_Normal_Response";
    String TMO_BUILDING_GLOBAL_ERROR_RESPONSE = "TMO_ARI_Building_Error_Response";
    String TMO_UNENCRYPTED_RESPONSE = "TMO_ARI_Sending_Unencrypted_Response";
    String TMO_HTTP_500_RESPONSE = "TMO_ARI_Sending_HTTP_500_Response";
    String TMO_BUILDING_RUNTIME_ERROR_RESPONSE = "TMO_ARI_Building_Runtime_Error_Response";
    String TMO_DECRYPTION_ERROR_NO_KEY = "TMO_ARI_Decryption_Error_No_Key";
    String TMO_DECRYPTION_ERROR_KEY_DATA_MISMATCH = "TMO_ARI_Decryption_Error_Key_Data_Mismatch";

    // ISODateFormat
    String DT_BAD_TIMEZONE = "DateTime_Bad_Timezone";
    String DT_BAD_FORMAT = "DateTime_Bad_Format";
    String DT_MAPJAVA_FORMAT = "DateTime_Map_To_Java_Format";

    //NetopDAO
    String GETALLNETOPS_SQLEXCEPTION = "getAllNetworkOperators_SQLException";
    String GETNETOP_SQLEXCEPTION = "getNetworkOperator_SQLException";
    /**
     * *********************************************************************
     * RELAY CONNECTOR                                          *
     * **********************************************************************
     */
    // DAO
    String RELAY_DAOEXCEPTION = "Relay_Connector_Data_Exception";

    // RelayConnector

    //IDecryptor
    String IDDECRYPTOR_GETKEY_EXCEPTION = "getkey_exception";
    String IDDECRYPTOR_DECODE_EXCEPTION = "decode_exception";

    //Subscriber/PRV soap interface
    String SUBSCRIBER_MAP_REQUEST = "Subscriber_Map_Request";
    String SUBSCRIBER_BUILD_REQUEST = "Subscriber_Build_Request";
    String SUBSCRIBER_BUILD_RESPONSE = "Subscriber_Build_Response";
    String SUBSCRIBER_REQUEST_LISTENER = "Subscriber_Request_Listener";

    //HWProduct events
    String HWPRODUCT_RETRIEVE_DETAILS = "HwProduct_Retrieve_Details";
    String HWPRODUCT_CREATE = "HwProduct_Create";
    String HWPRODUCT_UPDATE = "HwProduct_Update";
    String HWPRODUCT_REMOVE = "HwProduct_Remove";

    //Handheld eventd
    String HANDHELD_INVALID_PARAM = "HANDHELD_validation_Invalid_Parameter";     //error
    String HANDHELD_VALIDATEHANDHELINSAP_CREATEEXCEPTION = "HANDHELD_validatehandheldinSAP_CreateException";     //error
    String HANDHELD_VALIDATEHANDHELINSAP_REMOTEEEXCEPTION = "HANDHELD_validatehandheldinSAP_RemoteException";     //error
    String HANDHELD_VALIDATEHANDHELINSAP_PSEXCEPTION = "HANDHELD_validatehandheldinSAP_PSException";     //error

    String HANDHELD_CHECKINPROVISIONING_PSEXCEPTION = "HANDHELD_checkInProvisioning_PSException";
    String HANDHELD_CHECKINPROVISIONING_FINDEREXCEPTION = "HANDHELD_checkInProvisioning_FinderException";
    String HANDHELD_CHECKINPROVISIONING_CREATEEXCEPTION = "HANDHELD_checkInProvisioning_CreateException";     //error
    String HANDHELD_CHECKINPROVISIONING_REMOTEEXCEPTION = "HANDHELD_checkInProvisioning_RemoteException";     //error
    String HANDHELD_CHECKINPROVISIONING_ACCESSOREXCEPTION = "HANDHELD_checkInProvisioning_AccessorException";     //error
    String HANDHELD_CHECKINPROVISIONING_CONNECTIONEXCEPTION = "HANDHELD_checkInProvisioning_ConnectionException";     //error

    //SUBSCRIBER MANAGER BEAN
    String GETSUBSCRIBERBYPIN_EXCEPTION = "getSubscriberByPin_Exception";     //error

    String MDS_MISSING_CONFIG = "Missing_MDS_URL_Systemparms";
    String MDS_PUSH_ERROR = "MDS_Push_Error";

    String SAPCACHEDAO_SQLEXCEPTION = "SapCacheDAO_SQLException";
    String SAPCACHECARRIERDAO_SQLEXCEPTION = "SapCacheCarrierDAO_SQLException";

    //SAP allowed services
//    String WEB_SAP_ALLOWED_SERVICES_LIST = "Web_Sap_allowed_Service_List";
//    String WEB_ADD_SAP_ALLOWED_SERVICE = "Web_Add_Sap_Allowed_Service";
//    String WEB_UPDATE_SAP_ALLOWED_SERVICE = "Web_Update_Sap_Allowed_Service";
//    String WEB_DELETE_SAP_ALLOWED_SERVICE = "Web_Delete_Sap_Allowed_Service";
//
//    String WEB_SAP_NOT_ALLOWED_HWPRD_LIST = "Web_Sap_Not_Allowed_Hwprd_List";
//    String WEB_ADD_SAP_NOT_ALLOWED_HWPRD = "Web_Add_Sap_Not_Allowed_Hwprd";
//    String WEB_DELETE_SAP_NOT_ALLOWED_HWPRD = "Web_Delete_Sap_Not_Allowed_Hwprd";

    String CLOSECONNECTION_SQLEXCEPTION = "closeConnection_SQLException";
    String CLOSEPREPAREDSTATEMENT_SQLEXCEPTION = "closePreparedStatement_SQLException";

    //log event for exception list
    String WEB_LIST_ExceptionList = "Web_List_ExceptionList";
    String WEB_LOOKUP_ExceptionList = "Web_Lookup_ExceptionList";
    String WEB_REMOVE_ExceptionList = "Web_Remove_ExceptionList";
    String WEB_ADD_ExceptionList = "Web_Add_ExceptionList";
    String WEB_UPDATE_ExceptionList = "Web_Update_ExceptionList";

    //log event for ProRequestorMap list
    String WEB_LIST_ProRequestorMap = "Web_List_ProRequestorMap";
    String WEB_LOOKUP_ProRequestorMap = "Web_Lookup_ProRequestorMap";
    String WEB_REMOVE_ProRequestorMap = "Web_Remove_ProRequestorMap";
    String WEB_ADD_ProRequestorMap = "Web_Add_ProRequestorMap";
    String WEB_UPDATE_ProRequestorMap = "Web_Update_ProRequestorMap";

    String GETCUSTOMERNAME_NO_NAME_FOR_PRIMARY_KEY = "getCustomerName_noCustomerNameForPrimaryKey";

    String REQUEST_LIST_QUERIES = "Request_List_Queries";
    String REQUEST_LIST_QUERIES_IOEXCEPTION = "Request_List_Queries_IOException";

    String CREATE_FAULTY_REG_LOG = "Create_Faulty_Reg_Log";
    String GATHER_SIM_INFO_FOR_FAULTY_REG_LIMIT_EXCEEDED = "Gather_Sim_Info_For_Faulty_Reg_Limit_Exceeded";
    String FAULTY_REG_MGR_FIND_EXISTING_INFO_MISSING = "Faulty_Reg_Log_Find_Existing_Info_Missing";

    String DETERMINE_IF_RELAYID_BROADCAST_REQUIRED_DUE_TO_DEV_SWAP = "Determine_If_RelayId_Broadcast_Required_Due_To_Dev_Swap";
    String LOOKUP_SIMID_PROSAPTRX = "Lookup_SIMID_ProSapTrx";

    //  Inter Prv Events
    String INTERPRV_ERROR_SENDING_REQUEST = "InterPrv_Error_Sending_Request_to_Regional_PRV";
    String INTERPRV_ERROR_HANDLING_REMOTE_REQUEST = "InterPrv_Error_Handling_Remote_Request";
    String INTERPRV_RETRY_FINDER_EXCEPTION = "InterPrv_Retry_Finder_Exception";
    String INTERPRV_RETRY_REMOTE_EXCEPTION = "InterPrv_Retry_Remote_Exception";
    String INTERPRV_RETRY_CREATE_EXCEPTION = "InterPrv_Retry_Create_Exception";
    String INTERPRV_RETRY_PS_EXCEPTION = "InterPrv_Retry_PS_Exception";
    String INTERPRV_EXCEPTION_RETRY_NOT_CREATED = "InterPrv_Exception. Error sending message to Regional Prv. Retry Entry may not have been created.";
    String INTERPRV_CREATING_RETRY_RECORD = "InterPrv_Creating_Retry_Record";
    String INTERPRV_EXCEPTION_RETRY_DATA_NOT_UPDATED = "InterPrv_Exception. Error Updating retry Data.";
    String INTERPRV_DELETING_RETRY_RECORD = "InterPrv_Deleting_Retry_Record";
    String INTERPRV_REQUEST_RECEIVED_AT_REGIONAL_PRV = "InterPrv_Request_Received_At_Regional_Prv";
    String INTERPRV_REQUEST_IS_BEING_HANDLED_BY_MESSAGE_HANDLER = "InterPrv_Request_Being_Handled_By_MessageHandler";
    String INTERPRV_REQUEST_PARAMETERS_ERROR = "InterPrv_Request_Parameters_Error";
    String INTERPRV_REMOTE_REQUEST_SUCCESSFUL = "InterPrv_Remote_Request_Handled_Successfully";
    String INTERPRV_REMOTE_REQUEST_FAILURE = "InterPrv_Error_Handling_Remote_Request";
    String INTERPRV_REQUEST_BEING_SENT_TO_INTERPRV_QUEUE = "InterPrv_Request_Being_Sent_To_InterPrv_Queue";
    String INTERPRV_REQUEST_BEING_SENT_TO_INTERPRV_INBOUND_QUEUE = "InterPrv_Request_Being_Sent_To_InterPrv_Inbound_Queue";
    String INTERPRV_QUEUE_JMS_EXCEPTION = "InterPrv_Queue_JMS_Exception";
    String INTERPRV_QUEUE_NAMING_EXCEPITON = "InterPrv_Queue_Naming_Exception";
    String INTERPRV_QUEUE_EXCEPITON = "InterPrv_Queue_Exception";
    String INTERPRV_QUEUE_INBOUND_JMS_EXCEPTION = "InterPrv_Queue_Inbound_JMS_Exception";
    String INTERPRV_QUEUE_INBOUND_NAMING_EXCEPITON = "InterPrv_Queue_Inbound_Naming_Exception";
    String INTERPRV_QUEUE_INBOUND_EXCEPITON = "InterPrv_Queue_Inbound_Exception";
    String INTERPRV_RETRIES_SEARCH_BEGIN = "InterPrv_Retries_Search_Begin";
    String INTERPRV_RETRIES_SEARCH_END = "InterPrv_Retries_Search_End";
    String INTERPRV_RETRIES_SEARCH_EXCEPTION = "InterPrv_Retries_Search_Exception";
    String INTERPRV_RETRIES_ERROR_PROCESSING_RETRIES_EXCEPTION = "InterPrv_Retries_Error_Processing_Retries_Exception";
    String INTERPRV_REMOTE_REQUEST_EXCEPTION = "InterPrv_Remote_Request_Exception";
    String INTERPRV_MESSAGE_TYPE_NOT_FOUND = "InterPrv_Message_Type_Not_Found";
    String INTERPRV_MESSAGE_HANDLER_NOT_FOUND = "InterPrv_Message_Handler_Not_Found";
    String INTERPRV_SUCCESS_RESPONSE_FROM_REGIONAL_PRV = "InterPrv_Success_Response_From_Regional_Prv";
    String INTERPRV_FAILURE_RESPONSE_FROM_REGIONAL_PRV = "InterPrv_Failure_Response_From_Regional_Prv";
    String INTERPRV_ERROR_REFRESHING_MESSAGE_DATA = "InterPrv_Error_Refereshing_Message_Data";
    String INTERPRV_UPDATING_LOCAL_RELAYS = "InterPrv_Updating_Local_Relay(s)";
    String INTERPRV_ERROR_UPDATING_LOCAL_RELAYS = "InterPrv_Error_Updating_Local_Relay(s)";
    
    String DUAL_IMSI_DETECTED = "Dual imsi case detected";


    String CACHE_APPLICATION_STARTUP = "Cache_Application_Startup";
    String CACHE_INITIALIZING = "Cache_Initializing";
    String CACHE_TRIGGER_REFRESH_CONFIG_CACHE_NOT_FOUND = "Cache_Trigger_Refresh_Config_Cache_Not_Found";
    String CACHE_GET_ALL_OBJECTS_CONFIG_CACHE_NOT_FOUND = "Cache_Get_All_Objects_Config_Cache_Not_Found";
    String CACHE_GET_EXCEPTION = "Cache_Get_Exception";
    String CACHE_PRELOADING_DATA = "Cache_Preloading_Data";
    String CACHE_LAZY_LOAD_SWITCH = "Cache_Lazy_Load_Switch";
    String GET_CACHE_CONFIG_SQLEXCEPTION = "Get_Cache_Config_SQLException";
    String CACHE_NOT_FOUND_EXCEPTION = "Cache_Not_Found_Exception";
    String GET_ALL_CACHE_CONFIG_SQLEXCEPTION = "Get_All_Cache_Config_SQLException";
    String UPDATE_CACHE_CONFIG_SQLEXCEPTION = "Update_Cache_Config_SQLException";
    String UPDATE_CACHE_CONFIG_INVALID_EVICT_POLICY = "Update_Cache_Config_Invalid_Evict_Policy";
    String UPDATE_PRELOAD_CACHE_SIZE = "Update_Preload_Cache_Size";
    String CACHE_ACCESSOR_INSTANTIATE_CACHE_IMPL = "Cache_Accessor_Instantiate_Cache_Impl";
    String CACHE_PRELOADING_DATA_ELEMENT_LIST_SIZE = "Cache_Preloading_Data_Element_List_Size";
    String CACHE_PUT_DATA_ELEMENT_LIST_SIZE = "Cache_Put_Data_Element_List_Size";
    String CACHE_REGISTER_TX_SYNC_FAILED = "Cache_Register_Tx_Sync_Failed";
    String PERSISTENCE_IMPL_GET_ATTRIBUTE_ERROR = "Persistence_Impl_Get_Attribute_Error";
    String PERSISTENCE_IMPL_INSTANTIATE_ERROR = "Persistence_Impl_Instantiate_Error";
    String PERSISTENCE_IMPL_PRELOAD_EXCEPTION = "Cached_Impl_PreLoad_Exception";
    String PERSISTENCE_IMPL_CREATE_ENTRY_EXCEPTION = "Cached_Impl_Create_Entry_Exception";
    String CACHE_RETRIEVAL_CACHE_NOT_FOUND_EXCEPTION = "Cached_Retrieval_Cache_Not_Found_Exception";
    String CACHE_RETRIEVAL_EXCEPTION = "Cached_Retrieval_Exception";
    String JMX_REGISTER_CACHEMANAGER_Failed = "JMX_Register_CacheManager_Failed";

    String RETRIEVE_VALIDATION_RULES_EXCEPTION = "Retrieve_Validation_Rules_Exception";
    String EXCEPTION_WHILE_UPDATING_VALIDATION_RULE = "Exception_while_updating_validation_rule";
    String EXCEPTION_WHILE_RETRIEVING_VALIDATION_RULES = "Exception_while_retrieving_validation_rules";
    String EXCEPTION_WHILE_PROCESSING_FAULTY_REGISTRATION_LOG = "Exception_while_processing_faulty_registration_log";
    String RULE_PARAMETER_NOT_FOUND = "The parameter(s) for the validation rule could not have been retrieved";
    String RULE_PARAMETER_HAS_WRONG_FORMAT = "The parameter(s) for the validation rule have a wrong format";
    String CREATE_DEVICE_EXCEPTION = "create_device_exception";
    String PERFORM_DEVICE_OPERATION_EXCEPTION = "perform_device_operation_exception";
    String VALIDATION_FAILED = "validation_failed";
    String EXC_CHECKING_HARDWARE_ID_FOR_DIFFERENT_ASSOCIATION = "exception_checking_hardwareid_for_association_with_another_device";
    String VALIDATION_STATUS = "validation_status";
    String EXCEPTION_PERFORMING_VALIDATION_RULE_OPERATION = "exception_performing_validation_rule_operation";
    String MID_VALIDATION_EVENT = "mid_validation_event"; 
    String ISALIASUNIQUE_SQLEXCEPTION="An SQLException error occured during while trying to check if given alias is unique to given prorequestor.";
    
    String EXCEPTION_BLOCKING_UNRECOGNIZED_SUBSCRIBER= "exception_blocking_unrecognized_subscriber";
    
    String EXCEPTION_CREATING_JAXB_CONTEXT= "exception_creating_jaxb_context";
    
    // REQ 495 
    String SOLUTIONTYPEMGR_NAMINGEXCEPTION = "solutiontypemgr_namingexception";
    String SOLUTIONTYPEMGE_INTRPRV_DUPS_FOUND_ON_ADD = "SOLUTIONTYPEMGE_INTRPRV_DUPS_FOUND_ON_ADD";
    String SOLUTIONTYPEMGE_INTRPRV_SOLUTION_NOT_FOUND_ON_UPDATE = "SOLUTIONTYPEMGE_INTRPRV_SOLUTION_NOT_FOUND_ON_UPDATE";
    
    // REQ519
    String ERROR_PROC_BBIDSIMS_REL_REMOTE_EXCEPTION = "Error processing BBID<->Sim relationship. RemoteExceptionException";
    String ERROR_PROC_BBIDSIMS_REL_FINDER_EXCEPTION = "Error processing BBID<->Sim relationship. FinderException";
    String ERROR_PROC_BBIDSIMS_REL_SQLEXCEPTION = "Error processing BBID<->Sim relationship. SQLException";
    String ERROR_PROC_BBIDSIMS_REL_EXCEPTION = "Error processing BBID<->Sim relationship. General Exception";
    
    String ERROR_PROC_BBIDSIMS_VALIDATE_SITE_REMOTE_EXCEPTION = "Error validating site for BBID<->Sim relationship. RemoteException";
    String ERROR_PROC_BBIDSIMS_VALIDATE_SITE_FINDER_EXCEPTION = "Error validating site for BBID<->Sim relationship. FinderException";
    String ERROR_PROC_BBIDSIMS_VALIDATE_SITE_SQL_EXCEPTION = "Error validating site for BBID<->Sim relationship. SQLException";

    String SEND_TO_DEVICE_CONFIG_DISPATCH_QUEUE = "Device config event being sent to the Device Config Dispatch Queue";
    String DEVICE_CONFIG_DISPATCH_QUEUE_JMS_EXCEPTION = "Device config dispatch queue JMS Exception";
	Object DEVICE_CONFIG_DISPATCH_QUEUE_NAMING_EXCEPITON = "Device config dispatch queue Naming Exception";
}
    
    

