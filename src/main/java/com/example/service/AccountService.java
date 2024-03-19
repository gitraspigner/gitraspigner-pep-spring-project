package com.example.service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    public Optional<Account> accountExistsByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public Optional<Account> accountExistsByUsernameAndPassword(String username, String password) {
        return accountRepository.findByUsernameAndPassword(username, password);
    }

    public Optional<Account> accountExistsById(int id) {
        return accountRepository.findById(id);
    }

    /**
     *  

  
    public @ResponseBody ResponseEntity<String> registerAccountService() {

    }

    public List<Person> getAllPeople() {
        return personRepository.findAll();
    }

    public Person savePerson(Person person) {
        return personRepository.save(person);
    }

    // more methods as needed
    */


}
