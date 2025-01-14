/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.entity.Account;
import java.util.Optional;

/**
 *
 * @author pc
 */
public interface AccountService {
    boolean existByUsername(String username);
    Optional<Account> getByUsername();
}
