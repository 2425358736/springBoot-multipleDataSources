package com.multiple.data.sources.service.impl;
import com.multiple.data.sources.domain.base.Item;
import com.multiple.data.sources.domain.sys.SysUser;
import com.multiple.data.sources.mapper.base.ItemMapper;
import com.multiple.data.sources.mapper.sys.SysUserMapper;
import com.multiple.data.sources.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author admin
 */
@Service
@Slf4j
public class TestServiceImpl implements TestService {
    private final SysUserMapper sysUserMapper;

    private final ItemMapper itemMapper;

    public TestServiceImpl(SysUserMapper sysUserMapper, ItemMapper itemMapper) {
        this.sysUserMapper = sysUserMapper;
        this.itemMapper = itemMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String multiSourceOperation() {
        SysUser sysUser = new SysUser();
        sysUser.setUserName("刘志强");
        sysUser.setPassword("123456");
        sysUserMapper.insertSelective(sysUser);

        Item item = new Item();
        item.setItemKey("key");
        item.setItemValue("value");
        itemMapper.insertSelective(item);


        int[] i = {1};
        log.info(String.valueOf(i[2]));
        return "测试失败，观察数据库结果";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String multiSourceOperation2() {
        SysUser sysUser = new SysUser();
        sysUser.setUserName("刘志强");
        sysUser.setPassword("123456");
        sysUserMapper.insertSelective(sysUser);

        Item item = new Item();
        item.setItemKey("key");
        item.setItemValue("value");
        itemMapper.insertSelective(item);

        return "测试成功，观察数据库结果";
    }
}
