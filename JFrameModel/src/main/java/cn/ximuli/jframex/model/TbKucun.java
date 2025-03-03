package cn.ximuli.jframex.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TbKucun  implements java.io.Serializable {
    private String id;
    private String spname;
    private String jc;
    private String cd;
    private String gg;
    private String bz;
    private String dw;
    private Double dj;
    private Integer kcsl;
    public TbKucun() {
    }
    public TbKucun(String id) {
        this.id = id;
    }
    public TbKucun(String id, String spname, String jc, String cd, String gg, String bz, String dw, Double dj, Integer kcsl) {
        this.id = id;
        this.spname = spname;
        this.jc = jc;
        this.cd = cd;
        this.gg = gg;
        this.bz = bz;
        this.dw = dw;
        this.dj = dj;
        this.kcsl = kcsl;
    }
}