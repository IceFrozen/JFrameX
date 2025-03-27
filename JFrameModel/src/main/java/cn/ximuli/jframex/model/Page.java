package cn.ximuli.jframex.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Page<T> {
    private int page;
    private int pageSize;
    private int total;
    private List<T> data;

    public boolean isLastPage() {
        return page >= total;
    }

    public boolean isFirstPage() {
        return page == 1;
    }


    public int getTotalPage() {
        return total;
    }

    public void next() {
        if (!isLastPage()) {
            page +=1;
        }
    }
    public void pre() {
        if (!isFirstPage()) {
            page -=1;
        }
    }
}
