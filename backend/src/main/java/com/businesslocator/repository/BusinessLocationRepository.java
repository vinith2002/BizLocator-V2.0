package com.businesslocator.repository;

import com.businesslocator.model.BusinessLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessLocationRepository extends JpaRepository<BusinessLocation, Long> {

    List<BusinessLocation> findByBusinessNameIgnoreCaseOrderByCreatedAtDesc(String businessName);

    @Query("SELECT b FROM BusinessLocation b WHERE LOWER(b.businessName) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY b.createdAt DESC")
    List<BusinessLocation> searchByBusinessName(@Param("query") String query);

    boolean existsByOsmIdAndBusinessName(String osmId, String businessName);

    @Query("SELECT DISTINCT b.businessName FROM BusinessLocation b ORDER BY b.businessName ASC")
    List<String> findAllDistinctBusinessNames();

    List<BusinessLocation> findAllByOrderByCreatedAtDesc();
}
