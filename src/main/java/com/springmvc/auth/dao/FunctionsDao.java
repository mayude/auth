package com.springmvc.auth.dao;

import com.springmvc.auth.common.BaseDao;
import com.springmvc.auth.entity.Functions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

/**
 * Created by dello on 2016/7/29.
 */
@Repository
public class FunctionsDao extends BaseDao {
    private Logger logger= LoggerFactory.getLogger(FunctionsDao.class);

    /**
     * Functions的RowMapper映射类
     */
    private class FunctionsRowMapper implements RowMapper<Functions>{

        @Override
        public Functions mapRow(ResultSet resultSet, int i) throws SQLException {
            Functions functions=new Functions();
            functions.setId(resultSet.getLong("id"));
            functions.setName(resultSet.getString("name"));
            functions.setAccordion(resultSet.getInt("accordion"));
            functions.setParentId(resultSet.getLong("parent_id"));
            functions.setSerialNum(resultSet.getInt("serial_num"));
            functions.setUrl(resultSet.getString("url"));
            return functions;
        }
    }

    /**
     *  保存功能 目的可以将主键带出来。
     * @param functions functions功能对象
     */
    public void saveFunctions(Functions functions){
        String sql="insert into auth_function(name,parent_id,url,serial_num,accordion) values(?,?,?,?,?)";
        KeyHolder keyHolder=new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps=connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1,functions.getName());
                ps.setLong(2,functions.getParentId());
                ps.setString(3,functions.getUrl());
                ps.setInt(4,functions.getSerialNum());
                ps.setInt(5,functions.getAccordion());
                return ps;
            }
        },keyHolder);

        functions.setId(keyHolder.getKey().longValue());
    }

    /**
     * 根据id更新Url
     * @param id
     * @param url
     */
    public void updateUrl(Long id,String url){
        String sql="update from auth_function set url=? where id=?";
        jdbcTemplate.update(sql,url,id);
    }

    /**
     * 根据parentId去查找功能 分页
     * @param page
     * @param size
     * @param parentId
     * @return
     */
    public List<Functions> findFunctions(int page,int size,Long parentId){
        String sql="select * from auth_function where parent_id =? limit ?,?";
        try {
            return jdbcTemplate.query(sql,new Object[]{parentId,(page-1)*size,size},new FunctionsRowMapper());
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据id删除functions
     * @param id
     */
    public void deleteById(Long id){
        String sql="delete from auth_function where id =?";
        jdbcTemplate.update(sql,id);
    }

    /**
     * 查询全部功能
     * @return 返回功能链表
     */
    public List<Functions> findALlFunctions(){
        String sql="select * from auth_function";
        try {
            return jdbcTemplate.query(sql,new FunctionsRowMapper());
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

}
