package com.aveng.vnapp.web.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aveng.vnapp.service.TransactionService;
import com.aveng.vnapp.service.dto.TransactionDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * @author apaydin
 */
@RestController
@RequestMapping("/transactions")
@Slf4j
public class TransactionController {

    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> retrieveTransactions() {

        List<TransactionDTO> transactions = transactionService.retrieveTransactions();

        //todo pagination

        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> retrieveTransaction(@PathVariable String id) {

        return transactionService.retrieveTransaction(id)
            .map(transactionDTO -> new ResponseEntity<>(transactionDTO, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
