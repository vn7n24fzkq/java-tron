package org.tron.core.store;

import com.google.protobuf.ByteString;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tron.common.parameter.CommonParameter;
import org.tron.common.utils.ByteArray;
import org.tron.core.capsule.TransactionInfoCapsule;
import org.tron.core.capsule.TransactionRetCapsule;
import org.tron.core.db.TransactionStore;
import org.tron.core.db.TronStoreWithRevoking;
import org.tron.core.exception.BadItemException;
import org.tron.protos.Protocol.TransactionInfo;

@Slf4j(topic = "DB")
@Component
public class TransactionRetStore extends TronStoreWithRevoking<TransactionRetCapsule> {

  @Autowired
  private TransactionStore transactionStore;

  @Autowired
  public TransactionRetStore(@Value("transactionRetStore") String dbName) {
    super(dbName, TransactionRetCapsule.class);
  }

  @Override
  public void put(byte[] key, TransactionRetCapsule item) {
    if (BooleanUtils.toBoolean(CommonParameter.getInstance()
        .getStorage().getTransactionHistorySwitch())) {
      super.put(key, item);
    }
  }

  public TransactionInfoCapsule getTransactionInfo(byte[] key) throws BadItemException {
    long blockNumber = transactionStore.getBlockNumber(key);
    if (blockNumber == -1) {
      return null;
    }
    TransactionRetCapsule result  = revokingDB.getUnchecked(ByteArray.fromLong(blockNumber));
    if (Objects.isNull(result)) {
      return null;
    }
    if (Objects.isNull(result.getInstance())) {
      return null;
    }

    ByteString id = ByteString.copyFrom(key);
    for (TransactionInfo transactionResultInfo : result.getInstance().getTransactioninfoList()) {
      if (transactionResultInfo.getId().equals(id)) {
        return new TransactionInfoCapsule(transactionResultInfo);
      }
    }
    return null;
  }

  public TransactionRetCapsule getTransactionInfoByBlockNum(byte[] key) throws BadItemException {
    return revokingDB.getUnchecked(key);
  }

}
