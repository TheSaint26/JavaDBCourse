package billsPPaymentSystem_05;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "bank_accounts")
public class BankAccount extends BillingDetail {
    private String bankName;
    private String SWIFT;

    public BankAccount(){}

    @Column(name = "bank_name", nullable = false)
    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Column(name = "swift", nullable = false, unique = true)
    public String getSWIFT() {
        return SWIFT;
    }

    public void setSWIFT(String SWIFT) {
        this.SWIFT = SWIFT;
    }
}
