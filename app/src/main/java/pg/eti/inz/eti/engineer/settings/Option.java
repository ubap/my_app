package pg.eti.inz.eti.engineer.settings;

/**
 * Represents one option in settings.
 */
public class Option {

    private String name;
    private String value;
    private String type;

    public Option(String name, String value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}