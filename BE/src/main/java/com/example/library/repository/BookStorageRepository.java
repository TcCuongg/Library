package com.example.library.repository;

import com.example.library.entity.Book;
import com.example.library.entity.BookStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookStorageRepository extends JpaRepository<BookStorage, Long> {
    public BookStorage findFirstById(Long id);
}