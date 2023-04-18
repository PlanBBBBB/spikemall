package com.itheima.mapper;

import com.itheima.entity.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86139
* @description 针对表【users】的数据库操作Mapper
* @createDate 2023-04-15 15:55:14
* @Entity com.itheima.entity.Users
*/
@Mapper
public interface UsersMapper extends BaseMapper<Users> {

}




