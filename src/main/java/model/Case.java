package model;

/**
 * Created by zhaokangpan on 2017/4/8.
 */
public class Case {

    private int id;
    private String case_id = "";
    private String case_name = "";
    private String case_pub_date = "";
    private String case_content = "";
    private String case_type = "";
    private String case_no = "";
    private String case_court_name = "";
    private String case_procedure = "";
    private String case_verdict_date = "";
    private String case_keyword = "";
    private String case_wenshu_type = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCase_id() {
        return case_id;
    }

    public void setCase_id(String case_id) {
        this.case_id = case_id;
    }

    public String getCase_name() {
        return case_name;
    }

    public void setCase_name(String case_name) {
        this.case_name = case_name;
    }

    public String getCase_pub_date() {
        return case_pub_date;
    }

    public void setCase_pub_date(String case_pub_date) {
        this.case_pub_date = case_pub_date;
    }

    public String getCase_content() {
        return case_content;
    }

    public void setCase_content(String case_content) {
        this.case_content = case_content;
    }

    public String getCase_type() {
        return case_type;
    }

    public void setCase_type(String case_type) {
        this.case_type = case_type;
    }

    public String getCase_no() {
        return case_no;
    }

    public void setCase_no(String case_no) {
        this.case_no = case_no;
    }

    public String getCase_court_name() {
        return case_court_name;
    }

    public void setCase_court_name(String case_court_name) {
        this.case_court_name = case_court_name;
    }

    public String getCase_verdict_date() {
        return case_verdict_date;
    }

    public void setCase_verdict_date(String case_verdict_date) {
        this.case_verdict_date = case_verdict_date;
    }

    public String getCase_procedure() {
        return case_procedure;
    }

    public void setCase_procedure(String case_procedure) {
        this.case_procedure = case_procedure;
    }

    public String getCase_keyword() {
        return case_keyword;
    }

    public void setCase_keyword(String case_keyword) {
        this.case_keyword = case_keyword;
    }

    public String getCase_wenshu_type() {
        return case_wenshu_type;
    }

    public void setCase_wenshu_type(String case_wenshu_type) {
        this.case_wenshu_type = case_wenshu_type;
    }
}
