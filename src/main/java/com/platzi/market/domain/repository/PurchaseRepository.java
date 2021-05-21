package com.platzi.market.domain.repository;

import com.platzi.market.domain.Purchase;

import java.util.List;
import java.util.Optional;

public interface PurchaseRepository {
    List<Purchase> getAll();
    // here we use an optional because sometimes
    // we request for a client who doesnt have orders
    Optional<List<Purchase>> getByClient(String clientId);
    Purchase save(Purchase purchase);
}
