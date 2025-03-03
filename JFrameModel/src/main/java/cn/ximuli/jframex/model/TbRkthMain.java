package cn.ximuli.jframex.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TbRkthMain implements java.io.Serializable {
    private String rkthId;
    private String pzs;
    private String je;
    private String ysjl;
    private String gysname;
    private String rtdate;
    private String czy;
    private String jsr;
    private String jsfs;
    private Set tbRkthDetails = new HashSet(0);

    public TbRkthMain(String rkthId, String pzs, String je, String ysjl,
                      String gysname, String rtdate, String czy, String jsr, String jsfs) {
        this.rkthId=rkthId;
        this.pzs = pzs;
        this.je = je;
        this.ysjl = ysjl;
        this.gysname = gysname;
        this.rtdate = rtdate;
        this.czy = czy;
        this.jsr = jsr;
        this.jsfs = jsfs;
    }
}