package org.lucas;

import org.lucas.exception.AccountNotFoundException;
import org.lucas.exception.NoEnoughFundsException;
import org.lucas.exception.WalletNotFoundException;
import org.lucas.model.AccountWallet;
import org.lucas.repository.AccountRepository;
import org.lucas.repository.InvestmentRepository;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.*;
import static java.time.temporal.ChronoUnit.*;

public class Main {

    private final static AccountRepository accRepository = new AccountRepository();
    private final static InvestmentRepository invRepository = new InvestmentRepository();

    static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("Hi, welcome to the DIO Bank!");

        while(true){
            System.out.println("Select an option:");
            System.out.println("""
                    \t1. Create a new account
                    \t2. Create a new investment
                    \t3. Create an investment wallet
                    \t4. Deposit into account
                    \t5. Withdraw from account
                    \t6. Transfer between accounts
                    \t7. Invest money
                    \t8. Withdraw from investment
                    \t9. List accounts
                    \t10. List investments
                    \t11. List investment wallets
                    \t12. Update investments
                    \t13. Account history
                    \t14. Exit""");
            var option = in.nextInt();

            switch(option){
                case 1 -> createAccount();
                case 2 -> createInvestment();
                case 3 -> createInvestmentWallet();
                case 4 -> deposit();
                case 5 -> withdraw();
                case 6 -> transferBetweenAccounts();
                case 7 -> incInvestment();
                case 8 -> rescueInvestment();
                case 9 -> accRepository.list().forEach(System.out::println);
                case 10 -> invRepository.list().forEach(System.out::println);
                case 11 -> invRepository.listWallets().forEach(System.out::println);
                case 12 -> {
                    invRepository.updateAmount();
                    System.out.println("Investments updated!");
                }
                case 13 -> checkHistory();
                case 14 -> {
                    System.out.println("Finishing...");
                    System.exit(0);
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    private static void createAccount() {
        System.out.println("Enter the account identifiers separated by semi-colon ';' ");
        var accId = Arrays.stream(in.next().split(";")).toList();
        System.out.println("Inform the initial amount");
        var amount = in.nextLong();
        var wallet = accRepository.create(accId, amount);
        System.out.println("Account created!\n" + wallet);
    }

    private static void createInvestment() {
        System.out.println("Inform the tax of investment");
        var tax = in.nextLong();
        System.out.println("Inform the initial amount");
        var initialFunds = in.nextLong();
        var investment = invRepository.create(tax, initialFunds);
        System.out.println("Investment created!\n" + investment);
    }

    private static void createInvestmentWallet(){
        System.out.println("Please enter the account identifier for the investment");
        var accId = in.next();
        var acc = accRepository.findByAccId(accId);
        System.out.println("Inform the investment Id");
        var invId = in.nextLong();
        var investmentWallet = invRepository.initInvestment(acc, invId);
        System.out.println("Investment account created " + investmentWallet);
    }

    private static void deposit(){
        System.out.println("Please enter the account identifier for the deposit");
        var accId = in.next();

        System.out.println("Enter the amount to deposit");
        var amount = in.nextLong();

        try {
            accRepository.deposit(accId, amount);
        }catch (AccountNotFoundException e){
            System.out.println(e.getMessage());
        }
    }

    private static void withdraw(){
        System.out.println("Please enter the account identifier to withdraw funds");
        var accId = in.next();

        System.out.println("Enter the amount to withdraw");
        var amount = in.nextLong();

        try {
            accRepository.withdraw(accId, amount);
        } catch (NoEnoughFundsException | AccountNotFoundException e){
            System.out.println(e.getMessage());
        }
    }

    private static void transferBetweenAccounts() {
        System.out.println("Inform the account identifier from the source account");
        var sourceAccId = in.next();
        System.out.println("Inform the account identifier to the destination account");
        var targetAccId = in.next();
        System.out.println("Inform the amount to transfer from " + sourceAccId + " to " + targetAccId);
        var amount = in.nextLong();
        try{
            accRepository.transferMoney(sourceAccId, targetAccId, amount);
        }catch (AccountNotFoundException e){
            System.out.println(e.getMessage());
        }
    }

    private static void incInvestment() {
        System.out.println("Inform the account identifier for the investment");
        var accId = in.next();

        System.out.println("Inform the amount of money for the investment");
        var amount = in.nextLong();

        try{
            invRepository.deposit(accId, amount);
        }catch (WalletNotFoundException | AccountNotFoundException e){
            System.out.println(e.getMessage());
        }
    }

    private static void rescueInvestment(){
        System.out.println("Please enter the account identifier to rescue from Investment");
        var accId = in.next();

        System.out.println("Enter the amount to withdraw");
        var amount = in.nextLong();

        try {
            invRepository.withdraw(accId, amount);
        } catch (NoEnoughFundsException | AccountNotFoundException e){
            System.out.println(e.getMessage());
        }
    }

    private static void checkHistory(){
        System.out.println("Inform the account identifier to check history");
        var accId = in.next();
        AccountWallet wallet;

        try{
            var sortedHistory = accRepository.getHistory(accId);
            sortedHistory.forEach((k,v)-> {
                System.out.println(k.format(ISO_DATE_TIME));
                System.out.println(v.getFirst().transactionId());
                System.out.println(v.getFirst().description());
                System.out.println("$" + (v.size()/100) + "," + (v.size()%100));
            });

        }catch (AccountNotFoundException e){
            System.out.println(e.getMessage());
        }
    }
}