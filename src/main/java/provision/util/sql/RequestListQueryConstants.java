package provision.util.sql;

/**
 * Created by IntelliJ IDEA.
 * User: dvitorino
 * Date: May 13, 2005
 * Time: 1:11:43 PM
 * To change this template use File | Settings | File Templates.
 */
public interface RequestListQueryConstants {

    public static final String REQUEST_QUERIES_FILE="request_queries.properties";
    public static final String REQUEST_FIELDS_FILE="request_fields.properties";

    public static final String OP_LIKE_END="LIKE_END";
    public static final String OP_LIKE_START="LIKE_START";

    public static final String VAR_PRE="var.";
    public static final String PR_PRE="requestor.";
    public static final String RS_PRE="reseller.";

    public static final String COMPLETED="completed.";
    public static final String ERRORS="errors.";
    public static final String PENDING="pending.";
/*  All keys need prefixes above */
    public static final String HIST_SORT_DATE="protrx.sort.date_updated";
    public static final String HIST_SORT_TRXHDR="protrx.sort.trx_hdr";
    public static final String HIST_SORT_XMLID="protrx.sort.request_id";
    public static final String HIST_SORT_DEVSIMID="protrx.sort.device_sim_id";
    public static final String HIST_SORT_DEVESN="protrx.sort.dev_esn";

    public static final String STATUSES_SORT_DEVSIMID="statuses.sort.device_sim_id";
    public static final String STATUSES_SORT_TRXHDR="statuses.sort.trx_hdr";
    public static final String STATUSES_SORT_CONTR_NO="statuses.sort.sap_contr_no";
    public static final String STATUSES_SORT_IMSI="statuses.sort.imsi";
    public static final String STATUSES_SORT_ICCID="statuses.sort.iccid";

    public static final String STATUSES_SORT_ESNIMEI="statuses.sort.dev_esn";

    public static final String HIST_BY_DEVSIMID="protrx.by_device_sim_id";
    public static final String HIST_BY_BILLCOM="protrx.by_billing_com";
    public static final String HIST_BY_DEVID="protrx.by_device_id";
    public static final String HIST_BY_ESNIMEI_DEVID="protrx.by_esn_imei_and_device_id";
    public static final String HIST_BY_ESNIMEI="protrx.by_esn_imei";
    public static final String HIST_BY_IMSI="protrx.by_imsi";
    public static final String HIST_BY_MSISDN="protrx.by_msisdn";
    public static final String HIST_BY_ICCID="protrx.by_iccid";
    public static final String HIST_DEVID_ASSIGNS="protrx.by_device_id.assigns";
    public static final String HIST_ESNIMEI_ASSIGNS="protrx.by_esn_imei.assigns";

    public static final String STATUSES_BY_DEVSIMID="statuses.by_device_sim_id";
    public static final String STATUSES_BY_BILLCOM="statuses.by_billing_com";
    public static final String STATUSES_BY_DEVID="statuses.by_device_id";
    public static final String STATUSES_BY_ESNIMEI="statuses.by_esn_imei";
    public static final String STATUSES_BY_IMSI="statuses.by_imsi";
    public static final String STATUSES_BY_ICCID="statuses.by_iccid";
    public static final String STATUSES_BY_MSISDN="statuses.by_msisdn";

    public static final String ARCHIVE_TABLES="archive.tables";
    public static final String ARCHIVE_TABLE_SET_SEPARATOR = ";";
    public static final String ARCHIVE_TABLE_SEPARATOR = "::";


    public static final String DATE_UPDATED="date_updated";
    public static final String TRX_HDR="trx_hdr";
    public static final String REQUEST_ID="request_id";
    public static final String LOGIN_ID="login_id";
    public static final String SAP_SOLD_TO="sap_sold_to";
    public static final String DEVICE_ID="device_id";
    public static final String DEV_ESN="dev_esn";
    public static final String DEV_IMEI="dev_imei";
    public static final String DEVICE_SIM_ID="device_sim_id";
    public static final String BILLING_ID="billing_id";
    public static final String BILLING_COM="billing_com";
    public static final String MSISDN="msisdn";
    public static final String IMSI="imsi";
    public static final String ICCID="iccid";
    public static final String MINIMUM="minimum";
    public static final String MAXIMUM="maximum";
    public static final String NO_OF_DAYS="no_of_days";
    public static final String DATE_LIMIT="date_limit";
    public static final String VAR_ID="var_id";
    public static final String SIM_ID="sim_id";
}
