package billsPPaymentSystem_05;

import javax.persistence.*;

@Entity
@Table(name = "credit_cards")
public class CreditCard extends BillingDetail {
    private CardType cardType;
    private String expirationMonth;
    private String expirationYear;

    public CreditCard(){}

    @Enumerated(EnumType.STRING)
    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    @Column(name = "expiration_month", nullable = false)
    public String getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(String expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    @Column(name = "expiration_year", nullable = false)
    public String getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(String expirationYear) {
        this.expirationYear = expirationYear;
    }
}
