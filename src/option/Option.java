package option;

import utils.Utils;
import java.util.Optional;

/**
 *
 * @author diaz
 */
public class Option {

    private final String description;
    private final String camelName;
    private final String longName;
    private final String shortName;
    private final OptionArgumentType argumentType;
    private Optional<Object> optionalValue; // null if not provided by the user
    private final Object defaultValue;
    private final Object argumentValueIfNotProvided; // for the cmd line parsing: null if argument is required, default value else (ex: 1 for -v)
    private final Object associatedObject; // null or a linked object (e.g. a Solver)

    public Option(String description, String longName, String shortName, OptionArgumentType argumentType, Object defaultValue, Object argumentValueIfNotProvided) {
        this(description, longName, shortName, argumentType, defaultValue, argumentValueIfNotProvided, null);
    }

    public Option(String description, String longName, String shortName, OptionArgumentType argumentType, Object defaultValue, Object argumentValueIfNotProvided, Object associatedObject) {
        this.description = description;
        this.camelName = Utils.convertToCamelCase(longName);
        this.longName = longName;
        this.shortName = shortName;
        this.argumentType = argumentType;
        this.optionalValue = Optional.empty();
        this.defaultValue = defaultValue;
        this.argumentValueIfNotProvided = argumentValueIfNotProvided;
        this.associatedObject = associatedObject;
    }

    public String optionNames() {
        return "{" + longName + ", " + camelName + ", " + shortName + "}";
    }

    @Override
    public String toString() {
        return longName + " = " + optionalValue.orElse(defaultValue);
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    public String getArgumentName() {
        switch (argumentType) {
            case INTEGER:
                return "INTEGER";
            case DOUBLE:
                return "FLOAT";
            case BOOLEAN:
                return "on|off";
            case STRING:
                return "STRING";
        }
        return null;
    }

    /**
     * @return the camelName
     */
    public String getCamelName() {
        return camelName;
    }

    /**
     * @return the longName
     */
    public String getLongName() {
        return longName;
    }

    /**
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @return the stored value
     */
    public Object getValue() {
        return optionalValue.orElse(defaultValue);
    }

    /**
     * @return the default value
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * @return the value
     */
    public Optional<Object> getOptional() {
        return optionalValue;
    }

    public boolean isArgumentRequired() {
        return argumentValueIfNotProvided == null;
    }

    public Object getArgumentValueIfNotProvided() {
        return argumentValueIfNotProvided;
    }

    public boolean isSet() {
        return optionalValue.isPresent();
    }

    /**
     * @return the associatedObject
     */
    public Object getAssociatedObject() {
        return associatedObject;
    }

    public void setValue() throws IllegalArgumentException {
        if (argumentValueIfNotProvided == null) { // mandatory argument
            throw new IllegalArgumentException("missing argument");
        }
        optionalValue = Optional.of(argumentValueIfNotProvided);
    }

    public void setValue(Boolean value) throws IllegalArgumentException {
        if (argumentType != OptionArgumentType.BOOLEAN) {
            throw new IllegalArgumentException("unexpected boolean argument");
        }
        this.optionalValue = Optional.of(value);
    }

    public void setValue(Integer value) throws IllegalArgumentException {
        if (argumentType != OptionArgumentType.INTEGER && argumentType != OptionArgumentType.DOUBLE) {
            throw new IllegalArgumentException("unexpected number argument");
        }
        this.optionalValue =  Optional.of(value);
    }

    public void setValue(Double value) throws IllegalArgumentException {
        if (argumentType != OptionArgumentType.DOUBLE) {
            throw new IllegalArgumentException("unexpected float number argument");
        }
        this.optionalValue =  Optional.of(value);
    }

    public void setValue(String value) throws IllegalArgumentException {
        try {
            switch (argumentType) {
                case INTEGER:
                    this.optionalValue =  Optional.of(Integer.parseInt(value));
                    break;
                case DOUBLE:
                    this.optionalValue = Optional.of(Double.parseDouble(value));
                    break;
                case BOOLEAN:
                    this.optionalValue = Optional.of(parseBoolean(value));
                    break;
                case STRING:
                    this.optionalValue = Optional.of(value);
                    break;
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid number argument", e);
        }
    }

    private Boolean parseBoolean(String value) throws IllegalArgumentException {
        switch (value) {
            case "false":
            case "off":
            case "no":
                return Boolean.FALSE;
            case "true":
            case "on":
            case "yes":
                return Boolean.TRUE;
            default:
                throw new IllegalArgumentException("invalid flag");
        }
    }

    public OptionArgumentType getArgumentType() {
        return argumentType;
    }

    public boolean isMatchedBy(String name) {
        return getMatchedName(name) != null;
    }

    public String getMatchedName(String name) {
        if (shortName.startsWith(name)) {
            return shortName;
        }
        if (longName.startsWith(name)) {
            return longName;
        }
        if (camelName.startsWith(name)) {
            return camelName;
        }
        return null;
    }

    public boolean isOnErrorWith(Option option) {
        return (this != option) && (isMatchedBy(option.longName) || isMatchedBy(option.camelName) || isMatchedBy(option.shortName)
                || option.isMatchedBy(longName) || option.isMatchedBy(camelName) || option.isMatchedBy(shortName));
    }
}
