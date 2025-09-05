package org.lucas.model;

import lombok.Getter;

import java.util.List;

import static org.lucas.model.BankService.ACCOUNT;

@Getter
public class AccountWallet extends Wallet {

    private final List<String> accId;

    public AccountWallet(final List<String> accId) {
        super(ACCOUNT);
        this.accId = accId;
    }

    public AccountWallet(final long amount, final List<String> accId) {
        super(ACCOUNT);
        this.accId = accId;
        addMoney(amount, "amount for account creation");
    }

    public void addMoney(final long amount, final String description){
        var money = generateMoney(amount, description);
        this.money.addAll(money);
    }
}
