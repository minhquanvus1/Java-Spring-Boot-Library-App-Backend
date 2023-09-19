package com.luv2code.springbootlibrary.dao;

import com.luv2code.springbootlibrary.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

@RepositoryRestResource
public interface HistoryRepository extends JpaRepository<History, Long> {

    // available at: http://localhost:8080/api/histories/search/findBooksByUserEmail?email={userEmail}
    // return list of History objects, organizing in Pages (support Pagination)
    Page<History> findBooksByUserEmail(@RequestParam("email") String userEmail, Pageable pageable);
}
