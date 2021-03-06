package cn.yuanyu.dtxmsgrocketmq.construction.service.impl;


import cn.yuanyu.dtxmsgrocketmq.construction.model.AccountChangeEvent;
import cn.yuanyu.dtxmsgrocketmq.construction.service.TransferService;
import cn.yuanyu.dtxmsgrocketmq.construction.dao.AccountInfoMapper;
import cn.yuanyu.dtxmsgrocketmq.construction.dao.JudgeMapper;
import cn.yuanyu.dtxmsgrocketmq.construction.entity.AccountInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
public class TransferServiceImpl implements TransferService {

    @Autowired
    AccountInfoMapper accountInfoMapper;


    @Autowired
    JudgeMapper judgeMapper;


    /**
     * 转入
     */
    @Override
    @Transactional
    public void transferIn(AccountChangeEvent accountChangeEvent) {
        log.info("商业银行更新本地账号，用户：{},金额：{}", accountChangeEvent.getAccountToName(), accountChangeEvent.getAmount());

        if (judgeMapper.isExistTx(accountChangeEvent.getTxNo()) > 0) {
            return;
        }
        //增加金额
        accountInfoMapper.updateAccountBalance(accountChangeEvent.getAccountToName(), accountChangeEvent.getAmount());
        //添加事务记录，用于幂等
        judgeMapper.addTx(accountChangeEvent.getTxNo());

        if (accountChangeEvent.getAmount() == 3) {
            throw new RuntimeException("人为制造异常3");
        }

        log.info("用户{},成功转账{}元", accountChangeEvent.getAccountToName(), accountChangeEvent.getAmount());
    }

    /**
     * 查询所有用户
     */
    @Override
    public List<AccountInfo> getAllUserInfo() {
        return accountInfoMapper.getAllUserInfo();
    }
}
