package cn.ximuli.jframex.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class TbRukuMain  implements java.io.Serializable {
    private String rkId;
    private String pzs;
    private String je;
    private String ysjl;
    private String gysname;
    private String rkdate;
    private String czy;
    private String jsr;
    private String jsfs;
    private Set<TbRukuDetail> tabRukuDetails;
}
