package billsPPaymentSystem_05;

public enum CardType {
    CLASSIC("CLASSIC"),
    SILVER("SILVER"),
    GOLD("GOLD"),
    PLATINUM("PLATINUM");
    private final String value;

    CardType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
