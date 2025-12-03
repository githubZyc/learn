package com.example.springaialibaba.repository;

import com.example.springaialibaba.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    /**
     * Find documents by filename
     * @param fileName the filename to search for
     * @return list of documents with matching filename
     */
    List<Document> findByFileName(String fileName);
    
    /**
     * Search for documents containing specific keywords in their content
     * @param keyword the keyword to search for
     * @return list of documents containing the keyword
     */
    @Query("SELECT d FROM Document d WHERE LOWER(d.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Document> findByContentContaining(@Param("keyword") String keyword);
    
    /**
     * Find the most recently created documents
     * @param limit the maximum number of documents to return
     * @return list of recent documents
     */
    @Query("SELECT d FROM Document d ORDER BY d.createdAt DESC")
    List<Document> findRecentDocuments(int limit);
}