package com.example.service;

import com.example.model.Transaction;
import com.example.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    private final String CURRENCY_API = "https://api.fxratesapi.com/latest";

    public Transaction addTransaction(Transaction transaction) {
        // Perform currency conversion if currency is not INR
        if (!transaction.getCurrency().equals("INR")) {
            RestTemplate restTemplate = new RestTemplate();
            String url = CURRENCY_API + "?base=" + transaction.getCurrency() + "&symbols=INR";
            
            // Fetch conversion rates from the API
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.get("rates") != null) {
                Map<String, Double> rates = (Map<String, Double>) response.get("rates");
                double conversionRate = rates.get("INR");
                
                // Convert amount to INR
                double convertedAmount = transaction.getAmount() * conversionRate;
                transaction.setAmount(convertedAmount);
                transaction.setCurrency("INR");
            }
        }
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}