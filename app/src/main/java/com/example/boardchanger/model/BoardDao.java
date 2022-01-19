package com.example.boardchanger.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BoardDao {

    @Query("select * from Board")
    List<Board> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Board... boards);

    @Delete
    void delete(Board board);

}
