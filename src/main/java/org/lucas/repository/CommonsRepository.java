package org.lucas.repository;

import lombok.NoArgsConstructor;
import org.lucas.exception.NoEnoughFundsException;
import org.lucas.model.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;
import static org.lucas.model.BankService.ACCOUNT;

@NoArgsConstructor(access = PRIVATE)
public final class CommonsRepository {

    public static void checkFundsForTransaction(final Wallet source, final long amount){
        if(source.getFunds() < amount){
            throw new NoEnoughFundsException("No enough funds in account wallet");
        }
    }

    public static List<Money> generateMoney(final UUID transactionId, final long funds, final String description){
        var history = new MoneyAudit(transactionId, ACCOUNT, description, OffsetDateTime.now());
        return Stream.generate(()-> new Money(history)).limit(funds).toList();
    }
}
