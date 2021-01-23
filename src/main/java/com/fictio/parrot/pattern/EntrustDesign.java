package com.fictio.parrot.pattern;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.fictio.parrot.algorithm.SnowflakeUtil;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

enum OptNode {
    COLLECT,PRODUCTION,STORE,HOSPITAL
}

enum BillStatus {
    CREATED,SUBMITED,CHECKED,FINISED;
}

@ToString
class Bill {
    Long billNo;
    OptNode from;
    OptNode to;
    BillStatus status;
    List<String> details;
}

interface HandoverService {
    String createBill(Bill b);
    String submitBill(Bill b);
    String checktBill(Bill b);
}

class CollectService implements HandoverService {
    @Override
    public String createBill(Bill b) {
        b.billNo = new SnowflakeUtil().nextId();
        b.status = BillStatus.CREATED;
        if(OptNode.PRODUCTION.equals(b.to)) {
            return "创建采血到成分";
        }else if(OptNode.STORE.equals(b.to)) {
            return "创建采血到成品";
        }else {
            return "无法创建";
        }
    }
    @Override
    public String submitBill(Bill b) {
        b.status = BillStatus.SUBMITED;
        if(OptNode.PRODUCTION.equals(b.to)) {
            return "提交采血到成分";
        }else if(OptNode.STORE.equals(b.to)) {
            return "提交采血到成品";
        }else {
            return "无法提交";
        }
    }
    @Override
    public String checktBill(Bill b) {
        return "无法校验";
    }
}

class ProdtionService implements HandoverService {
    @Override
    public String createBill(Bill b) {
        if(OptNode.STORE.equals(b.to)) {
            b.billNo = new SnowflakeUtil().nextId();
            b.status = BillStatus.CREATED;
            return "创建成分到成品";
        }else {
            return "无法创建";
        }
    }
    @Override
    public String submitBill(Bill b) {
        if(OptNode.STORE.equals(b.to)) {
            b.status = BillStatus.SUBMITED;
            return "提交成分到成品";
        }else {
            return "无法创建";
        }
    }
    @Override
    public String checktBill(Bill b) {
        b.status = BillStatus.CHECKED;
        if(OptNode.PRODUCTION.equals(b.to)) {
            return "校验接收单";
        }else {
            return "无法校验";
        }
    }
}

class Delegator implements HandoverService {

    HandoverService handorverService = new CollectService();

    Map<OptNode,HandoverService> services = new HashMap<>();

    public Delegator() {
        services.put(OptNode.COLLECT, new CollectService());
        services.put(OptNode.PRODUCTION, new ProdtionService());
    }

    @Override
    public String createBill(Bill b) {
        return handorverService.createBill(b);
    }

    @Override
    public String submitBill(Bill b) {
        return handorverService.submitBill(b);
    }

    @Override
    public String checktBill(Bill b) {
        return handorverService.checktBill(b);
    }

    public Delegator setOpt(OptNode from) {
        handorverService = services.getOrDefault(from, null);
        if(handorverService == null) throw new RuntimeException("暂不支持环节");
        return this;
    }
}

@Slf4j
public class EntrustDesign {

    private List<String> details = Arrays.asList("A001","A002","A003");

    Delegator delegator = new Delegator();

    @Before public void before() {
    }

    @Test public void test() {
        Bill rowBill = new Bill();
        rowBill.from = OptNode.COLLECT;
        rowBill.to = OptNode.PRODUCTION;
        rowBill.details = details;
        String result = delegator.setOpt(rowBill.from).createBill(rowBill);
        log.debug("1.1 建单 : {} = {}",rowBill, result);
        result = delegator.submitBill(rowBill);
        log.debug("1.2 交单 : {} = {}",rowBill, result);
        result = delegator.setOpt(rowBill.to).checktBill(rowBill);
        log.debug("1.3 验单 : {} = {}",rowBill, result);

        Bill collectBill = new Bill();
        collectBill.billNo = null;
        collectBill.from = OptNode.PRODUCTION;
        collectBill.to = OptNode.STORE;
        collectBill.details = Arrays.asList("A001","A002","A003","B001");

        result = delegator.setOpt(collectBill.from).createBill(collectBill);
        log.debug("2.1 建单 : {} = {}",collectBill, result);
        result = delegator.submitBill(collectBill);
        log.debug("2.2 交单 : {} = {}",collectBill, result);
        result = delegator.setOpt(collectBill.to).checktBill(collectBill);
        log.debug("2.3 验单 : {} = {}",collectBill, result);

    }
}
