package pg.eti.inz.eti.engineer.settings;

/**
 * Represents one option in settings.
 */
public class Option {

    private String name;
    private String value;

    public Option(String name, String value) {
        this.name = name;
        this.value = value;
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
}
