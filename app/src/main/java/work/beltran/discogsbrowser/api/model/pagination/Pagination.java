package work.beltran.discogsbrowser.api.model.pagination;

import com.google.gson.annotations.SerializedName;

/**
 * Created by miquel on 22.04.16.
 */
public class Pagination {
    @SerializedName("page")
    private Integer page;
    @SerializedName("pages")
    private int pages;
    private int items;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getItems() {
        return items;
    }

    public void setItems(int items) {
        this.items = items;
    }
}
