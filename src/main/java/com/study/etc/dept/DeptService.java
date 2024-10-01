package com.study.etc.dept;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeptService {

    private final DeptMapper deptMapper;

    public String findDnameByDeptNo(int deptNo) {
        return deptMapper.findDnameByDeptNo(deptNo);
    }
}
