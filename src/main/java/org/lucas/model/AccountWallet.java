package org.lucas.model;

import java.util.List;

import static org.lucas.model.BankService.ACCOUNT;

public class AccountWallet extends Wallet {
    private final List<String> pix;

    public AccountWallet(final List<String> pix) {
        super(ACCOUNT);
        this.pix = pix;
    }

    public AccountWallet(final long amount, final BankService serviceType, final List<String> pix) {
        super(serviceType);
        this.pix = pix;
        addMoney(amount, "amount for account creation");
    }

    public void addMoney(final long amount, final String description){
        var money = generateMoney(amount, description);
        this.money.addAll(money);
    }
}
