package com.example.library.service;

import com.example.library.RedisObject.AccountRedis;
import com.example.library.RedisObject.BookMoreRedis;
import com.example.library.entity.*;
import com.example.library.more.BookManage;
import com.example.library.more.BookMore;
import com.example.library.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private AuthorBookRepository authorBookRepository;
    @Autowired
    private RedisTemplate redisBookMoreTemplate;


    public List<BookMore> getAllBook(int count, int size){
        Pageable pageable = PageRequest.of(count, size);
        List<BookMore> booklist = new ArrayList<>();
        List<BookMoreRedis> bookMoreRedisList = new ArrayList<>();

        String redisKey = "getAllBook(" + count + ", " + size + ")";

        boolean hasKey = redisBookMoreTemplate.hasKey(redisKey);

        if(hasKey){
            bookMoreRedisList = redisBookMoreTemplate.opsForList().range(redisKey, 0, -1);
            for(int i = 0; i < bookMoreRedisList.size(); i++){
                booklist.add(new BookMore(bookMoreRedisList.get(i)));
            }
        }
        else {
            booklist = bookRepository.getAllBook(pageable);
            Collections.reverse(booklist);
            if(!booklist.isEmpty()){
                for (int i = 0; i < booklist.size(); i++){
                    bookMoreRedisList.add(new BookMoreRedis(booklist.get(i)));
                }
                redisBookMoreTemplate.opsForList().rightPushAll(redisKey, bookMoreRedisList);
            }
        }
        return booklist;
    }
    public BookMore getTopBook(){
//        String redisKey = "getTopBook";
//
//        boolean hasKey = redisBookMoreTemplate.hasKey(redisKey);
//
//        if(hasKey){
//            BookMoreRedis bookMoreRedis = (BookMoreRedis) redisBookMoreTemplate.opsForValue().get(redisKey);
//            return new BookMore(bookMoreRedis);
//        }
//        else {
//            BookMore bookMore = bookRepository.getTopBook();
//            if(bookMore != null){
//                BookMoreRedis bookMoreRedis = new BookMoreRedis(bookMore);
//                redisBookMoreTemplate.opsForValue().set(redisKey, bookMoreRedis);
//            }
//            return bookMore;
//        }
        return bookRepository.getTopBook();
    }
    public List<BookMore> getBookFollowDesc(int count, int size){
        Pageable pageable = PageRequest.of(count, size, Sort.by(Sort.Direction.DESC, "follow"));
        List<BookMore> booklist = new ArrayList<>();
        List<BookMoreRedis> bookMoreRedisList = new ArrayList<>();

        String redisKey = "getBookFollowDesc(" + count + ", " + size + ")";

        boolean hasKey = redisBookMoreTemplate.hasKey(redisKey);

        if(hasKey){
            bookMoreRedisList = redisBookMoreTemplate.opsForList().range(redisKey, 0, -1);
            for(int i = 0; i < bookMoreRedisList.size(); i++){
                booklist.add(new BookMore(bookMoreRedisList.get(i)));
            }
        }
        else {
            booklist = bookRepository.getBookFollowDesc(pageable);
            Collections.reverse(booklist);
            if(!booklist.isEmpty()){
                for (int i = 0; i < booklist.size(); i++){
                    bookMoreRedisList.add(new BookMoreRedis(booklist.get(i)));
                }
                redisBookMoreTemplate.opsForList().rightPushAll(redisKey, bookMoreRedisList);
            }
        }
        return booklist;
//        return bookRepository.getBookFollowDesc(pageable);
    }
    public List<BookMore> getBookByTitle(String bookTitle, int count, int size){
        Pageable pageable = PageRequest.of(count, size);
        List<BookMore> booklist = new ArrayList<>();
        List<BookMoreRedis> bookMoreRedisList = new ArrayList<>();

        String redisKey = "getBookByTitle:" + bookTitle + "(" + count + ", " + size + ")";

        boolean hasKey = redisBookMoreTemplate.hasKey(redisKey);

        if(hasKey){
            bookMoreRedisList = redisBookMoreTemplate.opsForList().range(redisKey, 0, -1);
            for(int i = 0; i < bookMoreRedisList.size(); i++){
                booklist.add(new BookMore(bookMoreRedisList.get(i)));
            }
        }
        else {
            booklist = bookRepository.getBookByTitle(bookTitle, pageable);
            Collections.reverse(booklist);
            if(!booklist.isEmpty()){
                for (int i = 0; i < booklist.size(); i++){
                    bookMoreRedisList.add(new BookMoreRedis(booklist.get(i)));
                }
                redisBookMoreTemplate.opsForList().rightPushAll(redisKey, bookMoreRedisList);
            }
        }
        return booklist;
//        return bookRepository.getBookByTitle(bookTitle, pageable);
    }
    public BookMore getBookByBookStorageId(Long bookStorageId){
        String redisKey = "getBookByBookStorageId:" + bookStorageId;

        boolean hasKey = redisBookMoreTemplate.hasKey(redisKey);

        if(hasKey){
            BookMoreRedis bookMoreRedis = (BookMoreRedis) redisBookMoreTemplate.opsForValue().get(redisKey);
            return new BookMore(bookMoreRedis);
        }
        else {
            BookMore bookMore = bookRepository.getBookByBookStorageId(bookStorageId);
            if(bookMore != null){
                BookMoreRedis bookMoreRedis = new BookMoreRedis(bookMore);
                redisBookMoreTemplate.opsForList().rightPushAll(redisKey, bookMoreRedis);
            }
            return bookMore;
        }
//        return bookRepository.getBookByBookStorageId(bookStorageId);
    }
    public List<BookMore> getBookByCategory(String category, int count, int size){
        Pageable pageable = PageRequest.of(count, size);
        return bookRepository.getBookByCategory("Đang bán", category, pageable);
    }
    public List<BookMore> getBookByStorage(Long storageId, int count, int size){
        Pageable pageable = PageRequest.of(count, size);
        return bookRepository.getBookByStorageId(storageId, pageable);
    }
    public List<BookMore> findAllByRequest(String request, int count, int size){
        Pageable pageable = PageRequest.of(count, size);
        if(!bookRepository.getBookByCategory("Đang bán", request, pageable).isEmpty()){
            return bookRepository.getBookByCategory("Đang bán", request, pageable);
        }
        if(!bookRepository.getBookByAuthor(request, pageable).isEmpty()){
            return bookRepository.getBookByAuthor(request, pageable);
        }
        if(!bookRepository.getBookByContent(request, pageable).isEmpty()){
            return bookRepository.getBookByContent(request, pageable);
        }
        if(!bookRepository.getBookByStatus(request, pageable).isEmpty()){
            return bookRepository.getBookByStatus(request, pageable);
        }
        if(!bookRepository.getBookByTitle(request, pageable).isEmpty()){
            return bookRepository.getBookByTitle(request, pageable);
        }


        if(!bookRepository.getBookBySale(Integer.parseInt(request), pageable).isEmpty()){
            return bookRepository.getBookBySale(Integer.parseInt(request), pageable);
        }
        else {
            return bookRepository.getBookByCost(Long.parseLong(request), pageable);
        }
    }
    public List<BookMore> findAllByAccountCart(Long accountId, int count, int size){
        Pageable pageable = PageRequest.of(count, size);
        return bookRepository.getAllBookByAccountCart(accountId, pageable);
    }
    public List<BookMore> findAllByAccountBuy(Long accountId, int count, int size){
        Pageable pageable = PageRequest.of(count, size);
        return bookRepository.getAllBookByAccountBuy(accountId, pageable);
    }
    public List<BookManage> findAllBookManage(int count, int size){
        Pageable pageable = PageRequest.of(count, size);
        return bookRepository.getAllBookManage(pageable);
    }
    public List<BookManage> findAllBookManageRequest(String request, int count, int size){
        Pageable pageable = PageRequest.of(count, size);
        if(!bookRepository.getBookManageByCategory(request, pageable).isEmpty()){
            return bookRepository.getBookManageByCategory(request, pageable);
        }
        if(!bookRepository.getBookManageByAuthor(request, pageable).isEmpty()){
            return bookRepository.getBookManageByAuthor(request, pageable);
        }
        if(!bookRepository.getBookManageByStatus(request, pageable).isEmpty()){
            return bookRepository.getBookManageByStatus(request, pageable);
        }
        if(!bookRepository.getBookManageByTitle(request, pageable).isEmpty()){
            return bookRepository.getBookManageByTitle(request, pageable);
        }


        if(!bookRepository.getBookManageBySale(Integer.parseInt(request), pageable).isEmpty()){
            return bookRepository.getBookManageBySale(Integer.parseInt(request), pageable);
        }
        else {
            return bookRepository.getBookManageByCost(Long.parseLong(request), pageable);
        }
    }
    public List<BookManage> updateBook(Long bookId, Long authorId, String title, String categoryName, String authorName, String content, Long cost,
                                       int sale, String status, int count, int size){
        List<BookManage> bookManageList = new ArrayList<>();
        if(categoryRepository.findFirstByName(categoryName) != null && authorRepository.findFirstByName(authorName) != null){
            AuthorBook authorBook = authorBookRepository.findAuthorBookByAuthorIdAndBookId(authorId, bookId);
            authorBook.setAuthorToAuthorBook(authorRepository.findFirstByName(authorName));
            Book book = bookRepository.findFirstById(bookId);
            book.setCategoryToBook(categoryRepository.findFirstByName(categoryName));
            book.setTitle(title);
            book.setContent(content);
            book.setCost(cost);
            book.setSale(sale);
            book.setStatus(status);
            bookRepository.save(book);
            bookManageList = findAllBookManage(count, size);
            return bookManageList;
        }
        else return bookManageList;
    }
    public List<BookManage> selectBook(String category, String author, String costStart, String costEnd, String status, int count, int size){
        Pageable pageable = PageRequest.of(count, size);
        Long start, end;
        if(costStart.isEmpty()){
           start = 0L;
        }
        else start = Long.parseLong(costStart);
        if(costEnd.isEmpty()){
            end = 999999999999999999L;
        }else end = Long.parseLong(costEnd);
        if(category == ""){
            category = null;
        }
        if(author == ""){
            author = null;
        }
        if(status == ""){
            status = null;
        }
        return bookRepository.selectBook(category, author, start, end, status, pageable);
    }
    public List<String> findAllStatus(){
        return bookRepository.findAllStatus();
    }

    public BookManage addNewBook(String title, String category, String author, String content, String cost, String sale, String status){
        Book book = new Book();
        AuthorBook authorBook = new AuthorBook();
        if(categoryRepository.findFirstByName(category) != null && authorRepository.findFirstByName(author) != null){
            Category category1 = categoryRepository.findFirstByName(category);
            Author author1 = authorRepository.findFirstByName(author);

            book.setTitle(title);
            book.setFollow(0L);
            book.setCategoryToBook(category1);
            book.setCost(Long.parseLong(cost));
            book.setContent(content);
            book.setSale(Integer.parseInt(sale));
            book.setStatus(status);

            bookRepository.save(book);
            book = bookRepository.findFirstByOrderByIdDesc();

            authorBook.setBookToAuthorBook(book);
            authorBook.setAuthorToAuthorBook(author1);
            authorBookRepository.save(authorBook);

            author1.getAuthorBooksFromAuthor().add(authorBook);
            author1.setAuthorBooksFromAuthor(author1.getAuthorBooksFromAuthor());
            authorRepository.save(author1);
        }
        return bookRepository.getBookManageByBookId(book.getId());
    }
}
