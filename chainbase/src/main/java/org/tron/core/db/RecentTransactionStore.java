package org.tron.core.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tron.core.capsule.BytesCapsule;
import org.tron.core.exception.ItemNotFoundException;

@Component
public class RecentTransactionStore extends TronStoreWithRevoking<BytesCapsule> {

  @Autowired
  private RecentTransactionStore(@Value("recent-transaction") String dbName) {
    super(dbName, BytesCapsule.class);
  }

  @Override
  public BytesCapsule get(byte[] key) throws ItemNotFoundException {
    return revokingDB.get(key);
  }
}
