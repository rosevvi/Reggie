package com.rosevii.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import sun.rmi.runtime.Log;

import java.time.LocalDateTime;

/**
 * @author: rosevvi
 * @date: 2023/3/7 10:44
 * @version: 1.0
 * @description:
 * 解决公共字段自动填充问题
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
      log.info("公共字段自动填充[insert]...");
      metaObject.setValue("createTime", LocalDateTime.now());
      metaObject.setValue("updateTime", LocalDateTime.now());

      metaObject.setValue("createUser", BaseContext.getCurrent());
      metaObject.setValue("updateUser", BaseContext.getCurrent());

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]...");
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrent());
    }
}
