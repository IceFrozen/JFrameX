package cn.ximuli.jframex.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TbRukuDetail implements java.io.Serializable {
    private String id;
    private String tbSpinfo;
    private String tbRukuMain;
    private Double dj;
    private Integer sl;

    public void setTabSpinfo(String tbSpinfo) {
        this.tbSpinfo = tbSpinfo;
    }

    public void setTabRukuMain(String tbRukuMain) {
        this.tbRukuMain = tbRukuMain;
    }
}
