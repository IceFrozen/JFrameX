package cn.ximuli.jframex.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TbRkthDetail implements java.io.Serializable {
    private Integer id;
    private String tbRkthMain;
    private String spid;
    private Double dj;
    private Integer sl;

    public TbRkthDetail(String tbRkthMain, String spid, Double dj, Integer sl) {
        this.tbRkthMain = tbRkthMain;
        this.spid = spid;
        this.dj = dj;
        this.sl = sl;
    }
}