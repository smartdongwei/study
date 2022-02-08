package com.wdw.study.service;



import com.wdw.study.pojo.ActivitiVo;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;

public interface ActivitiService {
    void getFlowImgByInstanceId(String processInstanceId, OutputStream outputStream);

    void askForLeave(ActivitiVo activitiVo);

    ArrayList<Map> queryUserTaskByUserName(ActivitiVo activitiVo);

    ArrayList<Map> getHistoricProcessInstance(ActivitiVo activitiVo);

    void completeUserTaskById(ActivitiVo activitiVo);
}
