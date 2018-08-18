package kulkarni.aditya.materialnews.model;

/**
 * Created by maverick on 3/2/18.
 */

public class SourceInfo {

    private String id;

    private String name;

    public SourceInfo() {
    }

    public SourceInfo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
