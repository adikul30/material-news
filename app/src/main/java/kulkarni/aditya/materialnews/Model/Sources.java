package kulkarni.aditya.materialnews.Model;

/**
 * Created by adicool on 15/7/17.
 */

public class Sources {
    private String source;
    private boolean isSelected;

    public Sources(String source, boolean isSelected) {
        this.source = source;
        this.isSelected = isSelected;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Sources() {
    }
}
