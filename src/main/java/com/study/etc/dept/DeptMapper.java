package com.study.etc.dept;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DeptMapper {

    @Select("SELECT DNAME FROM DEPT WHERE DEPTNO = #{deptNo}")
    String findDnameByDeptNo(int deptNo);
}
