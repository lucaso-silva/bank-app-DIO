package org.lucas.repository;

import org.lucas.exception.AccountWithInvestmentException;
import org.lucas.exception.InvestmentNotFoundException;
import org.lucas.exception.WalletNotFoundException;
import org.lucas.model.AccountWallet;
import org.lucas.model.Investment;
import org.lucas.model.InvestmentWallet;

import java.util.ArrayList;
import java.util.List;

import static org.lucas.repository.CommonsRepository.checkFundsForTransaction;

public class InvestmentRepository {

    private long nextId = 0;
    private final List<Investment> investments = new ArrayList<>();
    private final List<InvestmentWallet> wallets = new ArrayList<>();

    public Investment create(final long tax, final long initialFunds){
        this.nextId++;
        var investment = new Investment(this.nextId, tax, initialFunds);
        investments.add(investment);
        return investment;
    }

    public InvestmentWallet initInvestment(final AccountWallet account, final long id){
        if(!wallets.isEmpty()){
            var accountsInUse = wallets.stream().map(InvestmentWallet::getAccount).toList();
            if(accountsInUse.contains(account)) {
                throw new AccountWithInvestmentException("Account " + account + " has already been invested");
            }
        }
        var investment = findById(id);
        checkFundsForTransaction(account, investment.initialFunds());
        var wallet = new InvestmentWallet(investment, account, investment.initialFunds());
        wallets.add(wallet);
        return wallet;
    }

    public void deposit(final String accId, final long funds) {
        var wallet = findWalletByAccountId(accId);
        wallet.addMoney(wallet.getAccount().reduceMoney(funds), wallet.getService(), "Investment");
    }

    public void withdraw(final String accId, final long funds){
        var wallet = findWalletByAccountId(accId);
        checkFundsForTransaction(wallet, funds);
        wallet.getAccount().addMoney(wallet.reduceMoney(funds), wallet.getService(), "Investment withdrawal");
        if(wallet.getFunds()==0){
            wallets.remove(wallet);
        }
    }

    public void updateAmount(){
        wallets.forEach(w -> w.updateAmount(w.getInvestment().tax()));
    }

    public Investment findById(final long id){
        return investments.stream().filter(a -> a.id() == id)
                .findFirst()
                .orElseThrow(
                        () -> new InvestmentNotFoundException("Investment with id " + id + " not found")
                );
    }

    public InvestmentWallet findWalletByAccountId(final String accId){
        return wallets.stream()
                .filter(w -> w.getAccount().getAccId().contains(accId))
                .findFirst()
                .orElseThrow(
                        () -> new WalletNotFoundException("Wallet not found for account " + accId)
                );
    }

    public List<InvestmentWallet> listWallets(){
        return this.wallets;
    }

    public List<Investment> list() {
        return this.investments;
    }
}
