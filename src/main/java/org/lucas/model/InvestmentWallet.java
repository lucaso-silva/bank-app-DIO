package org.lucas.model;

import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.lucas.model.BankService.INVESTMENT;

@ToString
@Getter

public class InvestmentWallet extends Wallet {
    private final Investment investment;
    private final AccountWallet account;

    public InvestmentWallet(final Investment investment, final AccountWallet account, final long amount) {
        super(INVESTMENT);
        this.investment = investment;
        this.account = account;
        addMoney(account.reduceMoney(amount), getService(), "Investment");
    }

    public void updateAmount(final long percentage){
        var amount = getFunds() * percentage/100;
        var history = new MoneyAudit(UUID.randomUUID(), getService(), "Returns", OffsetDateTime.now());
        var money = Stream.generate(() -> new Money(history)).limit(amount).toList();
        this.money.addAll(money);
    }
}
