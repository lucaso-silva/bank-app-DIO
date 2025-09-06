package org.lucas.repository;

import org.lucas.exception.AccountIdInUseException;
import org.lucas.exception.AccountNotFoundException;
import org.lucas.model.AccountWallet;
import org.lucas.model.MoneyAudit;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.lucas.repository.CommonsRepository.checkFundsForTransaction;

public class AccountRepository {

    private List<AccountWallet> accounts;

    public AccountWallet create(final List<String> accId, final long initialFunds){
        var accountIdInUse = accounts.stream().flatMap(a -> a.getAccId().stream()).toList();
        for(var acc : accId) {
            if(accountIdInUse.contains(acc)) {
                throw new AccountIdInUseException("Account with id " + accId + " already exists");
            }
        }
        var newAccount = new AccountWallet(initialFunds, accId);
        accounts.add(newAccount);
        return newAccount;
    }

    public void deposit(final String accId, final long amount){
        var target = findByAccId(accId);
        target.addMoney(amount, "Deposit");
    }

    public void withdraw(final String accId, final long amount){
        var source = findByAccId(accId);
        checkFundsForTransaction(source, amount);
        source.reduceMoney(amount);
//        return amount;
    }

    public void transferMoney(final String sourceAccId, final String targetAccId, final long amount){
        var source = findByAccId(sourceAccId);
        checkFundsForTransaction(source, amount);
        var target = findByAccId(targetAccId);
        var message = "Transfer from " + sourceAccId + " to " + targetAccId;
        target.addMoney(source.reduceMoney(amount), source.getService(), message);
    }

    public AccountWallet findByAccId(final String accId){
        return accounts.stream()
                .filter(a -> a.getAccId().contains(accId))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException("Account accId number " + accId + " not found."));
    }

    public List<AccountWallet> list(){
        return this.accounts;
    }

    public Map<OffsetDateTime, List<MoneyAudit>> getHistory(final String accId){
        var wallet = findByAccId(accId);
        var audit = wallet.getFinancialTransactions();
        return audit.stream()
                .collect(Collectors.groupingBy(t -> t.createdAt().truncatedTo(ChronoUnit.SECONDS)));
    }
}
