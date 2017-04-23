package model;

/**
 * Created by zhaokangpan on 2017/4/16.
 */
public class Keyword {
    private int id;
    private String k_name;
    private String k_value;
    private String k_type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getK_value() {
        return k_value;
    }

    public void setK_value(String k_value) {
        this.k_value = k_value;
    }

    public String getK_name() {
        return k_name;
    }

    public void setK_name(String k_name) {
        this.k_name = k_name;
    }

    public String getK_type() {
        return k_type;
    }

    public void setK_type(String k_type) {
        this.k_type = k_type;
    }
}
