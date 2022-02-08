package com.wdw.study.common.util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wang
 */
public class ValidatorUtil {
    /**
     * 直接使用即可
     * 检查字段的权限信息
     * @param object
     */
    public static boolean checkObjectValidator(Object object) throws Exception{
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Object>> violations =  validator.validate(object);
        if(violations.isEmpty()){
            return true;
        }else{
            Iterator<ConstraintViolation<Object>> iterator =  violations.iterator();
            while(iterator.hasNext()){
                ConstraintViolation<Object> next = iterator.next();
                throw new Exception(next.getMessage());
            }
        }
        return false;
    }
}
