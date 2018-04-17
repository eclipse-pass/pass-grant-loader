package org.dataconservancy.pass.grant.data;

/**
 * constants class containing the column names for fields we need for our COEUS data pulls
 * the names reflect the mapping to our model.
 *
 * the values come from four views in the COEUS database; and the aliases are used in the query string in
 * {@code CoeusConnector} and to refer to the columns in the ResultSet in {@code GrantUpdater}
 *

 * these are consumed in the {@code CoeusConnector} class for the pull from COEUS, and in the {@code GrantUpdater} class
 * for the push into Fedora
 *
 * @author jrm@jhu.edu
 */
public class CoeusFieldNames {

    public static final String C_GRANT_AWARD_NUMBER = "AWARD_ID";
    public static final String C_GRANT_AWARD_STATUS ="AWARD_STATUS";
    public static final String C_GRANT_LOCAL_AWARD_ID ="GRANT_NUMBER";
    public static final String C_GRANT_PROJECT_NAME ="TITLE";
    public static final String C_GRANT_AWARD_DATE = "AWARD_DATE";
    public static final String C_GRANT_START_DATE = "AWARD_START";
    public static final String C_GRANT_END_DATE = "AWARD_END";

    public static final String C_DIRECT_FUNDER_LOCAL_ID = "SPOSNOR_CODE";// misspelling in COEUS view - if this gets corrected
    //it will collide with C_PRIMARY_SPONSOR_CODE below - this field will then have to be aliased in order to
    //access it in the ResultSet
    public static final String C_DIRECT_FUNDER_NAME = "SPONSOR";
    public static final String C_PRIMARY_FUNDER_LOCAL_ID = "SPONSOR_CODE";
    public static final String C_PRIMARY_FUNDER_NAME = "SPONSOR_NAME";


    public static final String C_PERSON_FIRST_NAME = "FIRST_NAME";
    public static final String C_PERSON_MIDDLE_NAME = "MIDDLE_NAME";
    public static final String C_PERSON_LAST_NAME = "LAST_NAME";
    public static final String C_PERSON_EMAIL = "EMAIL_ADDRESS";
    public static final String C_PERSON_INSTITUTIONAL_ID = "JHED_ID";
    public static final String C_PERSON_EMPLOYEE_ID = "EMPLOYEE_ID";
    //public static final String C_PERSON_AFFILIATION = "";
    //public static final String C_PERSON_ORCID_ID = "";

    //these fields are accessed for processing, but are not mapped to PASS objects
    public static final String C_UPDATE_TIMESTAMP = "UPDATE_TIMESTAMP";
    public static final String C_ABBREVIATED_ROLE = "ABBREVIATED_ROLE";



}
